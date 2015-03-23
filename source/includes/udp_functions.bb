;################################################# Functions

;Get IP out of String (Advanced with Deriving)
Function udp_ip(ip$)
	;Local
	If Lower(ip$)="local" Then
		ip$="127.0.0.1"
	;URL
	ElseIf Instr(Lower(ip$),"://") Or Instr(Lower(ip$),"www") Then
		;Derive Port from used Protocol
		If Instr(Lower(ip$),"://") Then
			sep=Instr(ip$,":")
			prot$=Left(ip$,sep-1)
			prot$=Lower(prot$)
			Select prot$
				Case "http" port=80
				Case "https" port=443
				Case "ftp" port=21
				Case "ssh" port=22
				Case "irc" port=6667
				Default port=80
			End Select
		;On WWW use HTTP Port 80
		Else
			port=80
		EndIf
		;Try to Connect and to get the IP
		Local tcps=OpenTCPStream(ip$,port)
		If tcps<>0 Then
			ip$=TCPStreamIP(tcps)
			ip$=DottedIP(ip$)
			CloseTCPStream(tcps)
		EndIf
	EndIf
	;Empty
	If ip$="" Then ip$="127.0.0.1"
	;Check IP Format
	a1=Instr(ip$,".")
	If a1=0 Then Return 0
	a2=Instr(ip$,".",a1+1)
	If a2=0 Then Return 0
	a3=Instr(ip$,".",a2+1)
	If a3=0 Then Return 0
	;Get Integer IP out of String
	a1=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a2=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a3=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a4=Int(ip$)
	Return (a1 Shl 24)+(a2 Shl 16)+(a3 Shl 8)+a4
End Function


;IP String to Int
Function udp_int(ip$)
	a1=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a2=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a3=Int(Left(ip$,Instr(ip$,".")-1)):ip$=Right(ip$,Len(ip$)-Instr(ip$,"."))
	a4=Int(ip$)
	Return (a1 Shl 24)+(a2 Shl 16)+(a3 Shl 8)+a4
End Function


;Get own IP from the U.S.G.N. (Unreal Software Gaming Network)
Function udp_getip()
	tcps=OpenTCPStream("usgn.unrealsoftware.de",80)
	If tcps<>0 Then
		WriteLine tcps,"GET http://usgn.unrealsoftware.de/getip.php"
		in$=ReadLine(tcps)
		udp_ip=udp_ip(in$)
		CloseTCPStream(tcps)
		Return 1
	EndIf
	Return 0
End Function


;Clear
Function udp_clear()
	;Connection
	If udp<>0 Then
		CloseUDPStream(udp)
		udp=0
	EndIf
	udp_port=0
	udp_ip=0
	;Connections
	For Tudp_con.Tudp_con=Each Tudp_con
		Delete Tudp_con
	Next
	;Packet Queues
	For Tudp_ipq.Tudp_ipq=Each Tudp_ipq
		FreeBank Tudp_ipq\db
		Delete Tudp_ipq
	Next
	For Tudp_spq.Tudp_spq=Each Tudp_spq
		FreeBank Tudp_spq\db
		Delete Tudp_spq
	Next
	;Messages
	For Tudp_msg.Tudp_msg=Each Tudp_msg
		FreeBank Tudp_msg\db
		Delete Tudp_msg
	Next
	;CESTs
	For Tudp_cest.Tudp_cest=Each Tudp_cest
		Delete Tudp_cest
	Next
	;Resends
	For Tudp_resend.Tudp_resend=Each Tudp_resend
		FreeBank Tudp_resend\db
		Delete Tudp_resend
	Next
End Function


;Setup
Function udp_setup(port=0)
	;Clear
	udp_clear()
	;Connection
	udp=CreateUDPStream(port)
	If udp=0 Then Return 0
	;Data
	udp_port=UDPStreamPort(udp)
End Function


;Join
Function udp_join(ip,port,timeout=10000,rate=500)
	lastsend=0
	start=MilliSecs()
	While MilliSecs()-start<timeout
		
		;Read
		While RecvUDPMsg(udp)
			avail=ReadAvail(udp)
			If avail=1 Then
				;From Data
				fip=UDPMsgIP(udp)
				fport=UDPMsgPort(udp)
				;ID
				id=ReadByte(udp)
				Select id
					;Okay
					Case Cudp_cest_ack,Cudp_cest_exists
						For Tudp_con.Tudp_con=Each Tudp_con
							If Tudp_con\ip=fip Then
								If Tudp_con\port=fport Then
									Delete Tudp_con
								EndIf
							EndIf
						Next
						;Connection Setup
						Tudp_con.Tudp_con=New Tudp_con
						udp_con_setup(Tudp_con,fip,fport)
						;Host Setup
						udp_hostip=fip
						udp_hostport=fport
						;DebugLog "UDP: Joined!"
						Return 1
					;Default (Failure)
					Default Return id
				End Select
			EndIf
		Wend
			
		;Send Request
		If MilliSecs()-lastsend>rate Then
			WriteByte udp,Cudp_cest_req
			SendUDPMsg udp,ip,port
			lastsend=MilliSecs()
			;DebugLog "UDP: Send CEST Join Request "+Cudp_cest_req
		EndIf
	
	Wend
	;Failure
	Return Cudp_cest_timeout
End Function


;Connection Setup
Function udp_con_setup(con.Tudp_con,ip,port)
	;Address
	con\ip=ip
	con\port=port
	;IDs
	con\iid=2
	con\sid=0
	;Timers
	con\ack=MilliSecs()
	con\lastaction=MilliSecs()
	con\resend=0
End Function


;Connection Delete
Function udp_con_delete(con.Tudp_con)
	;Delete
	Delete con
End Function


;Update
Function udp_update()
	;Timer
	udp_ms=MilliSecs()
	
	;Process queued Packets and Update Connections
	udp_process_queued()
	
	;Get new Packets or cest Messages
	udp_get()
	
	;Assemble Messages to Packets
	For Tudp_msg.Tudp_msg=Each Tudp_msg
		packetexists=0
		;Add to existing, unsended Packet
		For Tudp_spq.Tudp_spq=Each Tudp_spq
			If Tudp_spq\sended=0 Then
				If Tudp_spq\ip=Tudp_msg\ip Then
					If Tudp_spq\port=Tudp_msg\port Then
						packetexists=1
						size=BankSize(Tudp_msg\db)
						oldsize=BankSize(Tudp_spq\db)
						;Add System Messages at Beginning
						If Tudp_msg\system=1 Then
							ResizeBank Tudp_spq\db,oldsize+size
							CopyBank Tudp_spq\db,0,Tudp_spq\db,size,oldsize
							CopyBank Tudp_msg\db,0,Tudp_spq\db,0,size
						;Add Normal Messages at End
						Else
							ResizeBank Tudp_spq\db,oldsize+size
							CopyBank Tudp_msg\db,0,Tudp_spq\db,oldsize,size
						EndIf
						If Tudp_msg\reliable=1 Then Tudp_spq\reliable=1
					EndIf
				EndIf
			EndIf
		Next
		;Create new Packet
		If packetexists=0 Then
			Tudp_spq.Tudp_spq=New Tudp_spq
			Tudp_spq\ip=Tudp_msg\ip
			Tudp_spq\port=Tudp_msg\port
			size=BankSize(Tudp_msg\db)
			Tudp_spq\db=CreateBank(size)
			CopyBank Tudp_msg\db,0,Tudp_spq\db,0,size
			Tudp_spq\reliable=Tudp_msg\reliable
			Tudp_spq\time=udp_ms
		EndIf
		;Delete Message
		FreeBank Tudp_msg\db
		Delete Tudp_msg
	Next
	
	;Send Stuff (in an interval of udp_sendrate millisecs)
	If udp_ms-udp_sendtimer>=udp_sendrate Then
		udp_sendtimer=udp_ms
		For Tudp_spq.Tudp_spq=Each Tudp_spq
			If Tudp_spq\sended=0 Then
				;Find Connection
				foundcon=0
				For Tudp_con.Tudp_con=Each Tudp_con
					If Tudp_con\ip=Tudp_spq\ip Then
						If Tudp_con\port=Tudp_spq\port Then
							foundcon=1
							Exit
						EndIf
					EndIf
				Next
				;Create New Connection
				If foundcon=0 Then
					;Connection Setup
					Tudp_con.Tudp_con=New Tudp_con
					udp_con_setup(Tudp_con,Tudp_spq\ip,Tudp_spq\port)
				EndIf
				;Reliable Send ID
				reliableid=0
				If Tudp_spq\reliable=1 Then
					Tudp_con\sid=Tudp_con\sid+2
					If Tudp_con\sid>Cudp_win_limit Then
						Tudp_con\sid=0
					EndIf
					reliableid=Tudp_con\sid
				Else
					reliableid=Tudp_con\sid+1
				EndIf
				;Send
				WriteShort udp,reliableid
				size=BankSize(Tudp_spq\db)
				For i=0 To size-1
					If Cudp_encr=0 Then
						WriteByte udp,PeekByte(Tudp_spq\db,i)
					Else
						byte=PeekByte(Tudp_spq\db,i)
						byte=byte Xor Cudp_encrk
						WriteByte udp,byte
					EndIf
				Next
				SendUDPMsg udp,Tudp_con\ip,Tudp_con\port
				;Sended!
				Tudp_spq\sended=1
				Tudp_spq\id=Tudp_con\sid
				;Delete when unreliable
				If Tudp_spq\reliable=0 Then
					FreeBank Tudp_spq\db
					Delete Tudp_spq
				EndIf
			EndIf
		Next
		
		;Send Connection Establishing Answers
		For Tudp_cest.Tudp_cest=Each Tudp_cest
			WriteByte udp,Tudp_cest\msg
			SendUDPMsg udp,Tudp_cest\ip,Tudp_cest\port
			;DebugLog "UDP: Send CEST Answer "+Tudp_cest\msg+" to "+DottedIP(Tudp_cest\ip)+":"+Tudp_cest\port
			Delete Tudp_cest
		Next
		
		;Send Resend
		For Tudp_resend.Tudp_resend=Each Tudp_resend
			;Send
			WriteShort udp,Tudp_resend\id
			size=BankSize(Tudp_resend\db)
			For i=0 To size-1
				If Cudp_encr=0 Then
					WriteByte udp,PeekByte(Tudp_resend\db,i)
				Else
					byte=PeekByte(Tudp_resend\db,i)
					byte=byte Xor Cudp_encrk
					WriteByte udp,byte
				EndIf
			Next
			SendUDPMsg udp,Tudp_resend\ip,Tudp_resend\port
			;Free
			FreeBank Tudp_resend\db
			Delete Tudp_resend
		Next
	EndIf
	
End Function


;Get
Function udp_get()
	;Read Everything!
	While RecvUDPMsg(udp)
		avail=ReadAvail(udp)
		If avail>0 Then
			
			;From Data
			fip=UDPMsgIP(udp)
			fport=UDPMsgPort(udp)
			
			;###################### Connection Establishing Messages (1 Byte)
			If avail=1 Then
				
				;Read ID
				id=ReadByte(udp)
				
				;DebugLog "UDP: Got CEST Message "+id+" from "+DottedIP(fip)+":"+fport
				
				Select id
					
					;Connection Establishing Request
					Case Cudp_cest_req
						;Is Host
						If udp_host=1
							foundcon=0
							For Tudp_con.Tudp_con=Each Tudp_con
								If Tudp_con\ip=fip Then
									If Tudp_con\port=fport Then
										foundcon=1
										Exit
									EndIf
								EndIf
							Next
							Tudp_cest.Tudp_cest=New Tudp_cest
							Tudp_cest\ip=fip
							Tudp_cest\port=fport
							;Connection already exists
							If foundcon=1 Then
								Tudp_cest\msg=Cudp_cest_exists
							;Acknowledgment
							Else
								Tudp_cest\msg=Cudp_cest_ack
								;Connection Setup
								Tudp_con.Tudp_con=New Tudp_con
								udp_con_setup(Tudp_con,fip,fport)
							EndIf
						;No Host
						Else
							Tudp_cest.Tudp_cest=New Tudp_cest
							Tudp_cest\ip=fip
							Tudp_cest\port=fport
							Tudp_cest\msg=Cudp_cest_nohost
						EndIf
						
					;Server Info Request
					Case 100
						;Host?
						If udp_host=1 Then
							WriteByte udp,101
							WriteString udp,"Bla"
							WriteString udp,"mp_sawmp"
							WriteByte udp,1
							WriteByte udp,4
							WriteByte udp,0
							SendUDPMsg udp,fip,fport
						EndIf
						
					
					;Error
					Default
						;DebugLog "UDP: Unexpected Message from "+DottedIP(fip)+":"+fport
				
				End Select
			
			
			;###################### Packets (at least 3 Bytes)
			ElseIf avail>2 Then
				
				;Valid Connection?
				foundcon=0
				For Tudp_con.Tudp_con=Each Tudp_con
					If Tudp_con\ip=fip Then
						If Tudp_con\port=fport Then
							
							;Update Connection Last Action
							Tudp_con\lastaction=udp_ms
							
							;ID
							id=ReadShort(udp)
							
							;Packet exists? -> Ignore
							foundpacket=0
							For Tudp_ipq.Tudp_ipq=Each Tudp_ipq
								If Tudp_ipq\ip=fip Then
									If Tudp_ipq\port=fport Then
										If Tudp_ipq\id=id Then
											foundpacket=1
											Exit
										EndIf
									EndIf
								EndIf
							Next
							;Packet doesn't exist -> put in Queue
							If foundpacket=0 Then
								Tudp_ipq.Tudp_ipq=New Tudp_ipq
								Tudp_ipq\ip=fip
								Tudp_ipq\port=fport
								Tudp_ipq\id=id
								Tudp_ipq\time=udp_ms
								;Reliable?
								If id Mod 2=0 Then Tudp_ipq\reliable=1								
								;System?
								inbyte=ReadByte(udp)
								If Cudp_encr=1 Then inbyte=inbyte Xor Cudp_encrk
								If inbyte>=Cudp_sys Then Tudp_ipq\system=1
								;Data
								Tudp_ipq\db=CreateBank(avail-2)
								PokeByte(Tudp_ipq\db,0,inbyte)
								For i=1 To (avail-3)
									inbyte=ReadByte(udp)
									If Cudp_encr=0 Then
										PokeByte(Tudp_ipq\db,i,inbyte)
									Else
										inbyte=inbyte Xor Cudp_encrk
										PokeByte(Tudp_ipq\db,i,inbyte)
									EndIf
								Next
								;Process System Messages immediately
								If Tudp_ipq\system=1 Then
									udp_system(Tudp_ipq)
								EndIf	
							EndIf	
							
							;DebugLog "got msg #"+Tudp_ipq\id
						
							foundcon=1
							Exit
						EndIf
					EndIf
				Next
				
				;Invalid Connection
				If foundcon=0 Then
					While ReadAvail(udp)>0
						ReadByte(udp)
					Wend
					;DebugLog "UDP: Unexpected Message from "+DottedIP(fip)+":"+fport
				EndIf
				
				
				;Read ID
				;id=ReadShort(udp)
				
			;###################### Invalid Message
			Else
			
				;Error
				While ReadAvail(udp)>0
					ReadByte(udp)
				Wend
				;DebugLog "UDP: Unexpected Message from "+DottedIP(fip)+":"+fport
			
			EndIf
		
		EndIf
	Wend
End Function


;Process System Messages in Packet (and remove them!)
Function udp_system(in.Tudp_ipq)
	size=BankSize(in\db)
	For i=0 To size-1
		msgid=PeekByte(in\db,i)
		;System ID
		If msgid>=Cudp_sys Then
			Select msgid
				;Ping
				Case Cudp_sys_ping
					
				;Req
				Case Cudp_sys_req
					id=PeekShort(in\db,i+1)
					i=i+2
					
					lowest=10000
					highest=0
					
					;Resend and Dump
					For Tudp_spq.Tudp_spq=Each Tudp_spq
						If Tudp_spq\ip=in\ip Then
							If Tudp_spq\port=in\port Then
								If Tudp_spq\sended=1 Then
									;Dump implicit acknowledged Messages
									If udp_windowing(id-1,Tudp_spq\id)=0 Then
										
										If Tudp_spq\id<lowest Then lowest=Tudp_spq\id
										If Tudp_spq\id>highest Then highest=Tudp_spq\id
										
										DebugLog "discard "+Tudp_spq\id
										
										FreeBank Tudp_spq\db
										Delete Tudp_spq
										
									;Resend requested Message
									ElseIf Tudp_spq\id=id Then
										Tudp_resend.Tudp_resend=New Tudp_resend
										Tudp_resend\ip=Tudp_spq\ip
										Tudp_resend\port=Tudp_spq\port
										Tudp_resend\id=id
										size=BankSize(Tudp_spq\db)
										Tudp_resend\db=CreateBank(size)
										CopyBank Tudp_spq\db,0,Tudp_resend\db,0,size
									EndIf
								EndIf
							EndIf
						EndIf
					Next
					
					lastsys$="!!! REQ "+id+" -> discard "+lowest+"-"+highest
					
				;Ack
				Case Cudp_sys_ack
					id=PeekShort(in\db,i+1)
					i=i+2
					
					lowest=10000
					highest=0
					
					;Dump acknowledged Messages
					For Tudp_spq.Tudp_spq=Each Tudp_spq
						If Tudp_spq\ip=in\ip Then
							If Tudp_spq\port=in\port Then
								If Tudp_spq\sended=1 Then
									If udp_windowing(id,Tudp_spq\id)=0 Then
										
										If Tudp_spq\id<lowest Then lowest=Tudp_spq\id
										If Tudp_spq\id>highest Then highest=Tudp_spq\id
										
										DebugLog "discard "+Tudp_spq\id
										
										FreeBank Tudp_spq\db
										Delete Tudp_spq
										
									EndIf
								EndIf
							EndIf
						EndIf
					Next
					
					lastsys$="ACK "+id+" -> discard "+lowest+"-"+highest
					
				;Nothing
				Default
			End Select
		;Non System ID - Finished!
		Else
			i=i
			Exit
		EndIf
	Next
	;Remove complete Packet
	If i>=size-1 Then
		FreeBank in\db
		Delete in
	;Remove Start with System Messages of Packet
	Else
		CopyBank in\db,i,in\db,0,size-i
		ResizeBank in\db,size-1
		in\system=0
	EndIf
End Function


;Process queued Packets
Function udp_process_queued()
	Local lo.Tudp_ipq
	;Check all Connections
	For Tudp_con.Tudp_con=Each Tudp_con
		ip=Tudp_con\ip
		port=Tudp_con\port
		c=0
		;Count
		For Tudp_ipq.Tudp_ipq=Each Tudp_ipq
			If Tudp_ipq\ip=ip Then
				If Tudp_ipq\port=port Then
					Tudp_ipq\checked=0
					c=c+1
				EndIf
			EndIf
		Next
		;Process in correct Order
		If c>0 Then
			For i=1 To c
				lowest=-1
				;Find Lowest of unchecked
				For Tudp_ipq.Tudp_ipq=Each Tudp_ipq
					If Tudp_ipq\ip=ip Then
						If Tudp_ipq\port=port Then
							If Tudp_ipq\checked=0 Then
								If lowest=-1 Then
									lowest=Tudp_ipq\id
									lo.Tudp_ipq=Tudp_ipq
								Else
									If Tudp_ipq\id<lowest Then
										lowest=Tudp_ipq\id
										lo.Tudp_ipq=Tudp_ipq
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				Next
				
				;Process Packet
				lo\checked=1
				
				;DebugLog "process "+lo\id+" reliable "+lo\reliable+" coniid "+Tudp_con\iid
				
				;Reliable Packet
				If lo\reliable=1 Then
					;Process
					If lo\id=Tudp_con\iid Then
						udp_process(lo)
						Tudp_con\iid=Tudp_con\iid+2
						If Tudp_con\iid>Cudp_win_limit Then
							Tudp_con\iid=2
							For old.Tudp_ipq=Each Tudp_ipq
								If old\ip=ip Then
									If old\port=port Then
										If old\id>Cudp_win_limit/2 Then
											FreeBank old\db
											Delete old
										EndIf
									EndIf
								EndIf
							Next
						EndIf
						FreeBank lo\db
						Delete lo
						Tudp_con\resend=0
					;Discard
					ElseIf lo\id<Tudp_con\iid Then
						FreeBank lo\db
						Delete lo
					;Request Missing Packet(s)
					Else
						If Tudp_con\resend=0 Then
							Tudp_con\resend=udp_ms+udp_request
							DebugLog "request "+Tudp_con\iid
						EndIf
						Exit
					EndIf
				;Unreliable Packet
				Else
					con_add("process unreliable packet")
					udp_process(lo)
					FreeBank lo\db
					Delete lo
					;Process
					;If lo\id=Tudp_con\iid-1 Then
					;	udp_process(lo)
					;Discard
					;ElseIf lo\id<Tudp_con\iid-1 Then
					;	FreeBank lo\db
					;	Delete lo
					;EndIf
					;Request Missing Packet(s)
					;Else
					;	If Tudp_con\resend=0 Then
					;		Tudp_con\resend=udp_ms+udp_request
					;	EndIf
					;	Exit
					;EndIf
				EndIf
					
			Next
		EndIf
		;Connection Timeout
		If udp_ms-Tudp_con\lastaction>=udp_timeout Then
			udp_con_delete(Tudp_con)
		Else
			;Send Acknowledgements
			If Tudp_con\resend=0 Then
				If udp_ms-Tudp_con\ack>=udp_ack Then
					Tudp_con\ack=udp_ms
					udp_send(Tudp_con\ip,Tudp_con\port,Cudp_sys_ack,0)
					udp_w_short(Tudp_con\iid)
				EndIf
			;Request Missed Packet
			Else
				If udp_ms-Tudp_con\resend>=0 Then
					Tudp_con\resend=udp_ms+udp_rerequest
					udp_send(Tudp_con\ip,Tudp_con\port,Cudp_sys_req,0)
					udp_w_short(Tudp_con\iid)
				EndIf
			EndIf
		EndIf
	Next
End Function


;Windowing - delete old Messages
Function udp_windowing(id1,id2)
	If id2=-1 Then Return 1
	;Check at End
	If id1>=Cudp_win_limit-Cudp_win_range Then
		If id2>id1 Then
			Return 1
		Else
			If id2<=Cudp_win_range-(Cudp_win_limit-id1) Then
				Return 1
			Else
				Return 0
			EndIf
		EndIf
	;Check at Start
	ElseIf id1<Cudp_win_range Then
		If id2>id1 Then
			If (id2-id1)<=Cudp_win_range Then
				Return 1
			Else
				Return 0
			EndIf
		Else
			Return 0
		EndIf
	;Check somewhere else (Middle)
	Else
		If id2>id1 Then
			If (id2-id1)<=Cudp_win_range Then
				Return 1
			Else
				Return 0
			EndIf
		Else
			Return 0
		EndIf
	EndIf
End Function

;Repeat
;	z1=Input("1->")
;	z2=Input("2->")
;	Print udp_windowing(z1,z2)
;Forever
