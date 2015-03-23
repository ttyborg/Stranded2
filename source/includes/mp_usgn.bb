;############################################ USGN


;### Load Servers from U.S.G.N.
Function usgn_setup()
	bmpf_loadscreen("Loading U.S.G.N. Serverlist")
	;UDP
	udp_setup()
	If udp=0 Then RuntimeError("Failed to initialize UDP Sockets")
	;TCP
	Local tcp=OpenTCPStream("usgn.unrealsoftware.de",80)
	If tcp<>0 Then
		For Tserver.Tserver=Each Tserver
			Delete Tserver
		Next
		WriteLine(tcp,"GET http://usgn.unrealsoftware.de/game_stranded2/ips.txt")
		in_usgn_version$=Trim(ReadLine(tcp))
		in_usgn_msg$=Trim(ReadLine(tcp))
		in_usgn_msgscr#=500
		While Not Eof(tcp)
			in$=Trim(ReadLine(tcp))
			If in$<>"" Then
				Tserver.Tserver=New Tserver
				sep=Instr(in$,":")
				Tserver\ip=udp_int((Left(in$,sep-1)))
				Tserver\port=Mid(in$,sep+1,-1)
				If set_debug=1 Then
					con_add("Got U.S.G.N. Server "+DottedIP(Tserver\ip)+":"+Tserver\port,Cbmpf_yellow)
				EndIf
			EndIf
		Wend
		CloseTCPStream(tcp)
	EndIf
	
End Function


;### Host Server
Function usgn_hostserver(port)
	bmpf_loadscreen("Adding Server to U.S.G.N. Serverlist")
	;UDP
	udp_setup(port)
	If udp=0 Then RuntimeError("Failed to create server at UDP Port "+port+"!")
	udp_host=1
	set_sv_port=port
	;TCP
	Local tcp=OpenTCPStream("usgn.unrealsoftware.de",80)
	If tcp<>0 Then
		WriteLine(tcp,"GET http://usgn.unrealsoftware.de/game_stranded2/save.php?port="+port)
		If Not Eof(tcp)
			in$=Trim(ReadLine(tcp))
			If in$<>"" Then
				set_sv_ip=udp_int(in$)
			EndIf
		EndIf
		CloseTCPStream(tcp)
	EndIf
	;Map
	m_section=Csection_game_mp
	load_map("maps\"+in_win_map$+".s2","")
	game_playerspawn(1)								;Create Player
	game_setplayer(1)								;Cache own Player Data
	TCunit\player_name$=mp_checkname$(set_name$)	;Set Player Name
	TCunit\player_ip=set_sv_ip						;Set Player IP
	TCunit\player_port=set_sv_port					;Set Player Port
	m_menu=0
End Function


;### Join Server
Function usgn_joinserver(ip,port,password$="")
	;UDP
	udp_setup()
	If udp=0 Then RuntimeError("Failed to initialize UDP Sockets")
	udp_host=0
	;Join
	out=udp_join(ip,port,10000,1000)
	;Joined?
	Select out
		;Ok
		Case 1 joined=1
		;Error
		Default joined=0
	End Select
	;Proceed Joining
	If joined=1 Then
		;Cache Server Data
		set_sv_ip=udp_hostip
		set_sv_port=udp_hostport
		;Send Join Request
		udp_send(set_sv_ip,set_sv_port,1)
		udp_w_tiny(set_name$)
		udp_w_tiny(password$)
		;Get Data
		mp_joinserver=0
		timeout=MilliSecs()
		While mp_joinserver=0
			udp_update()
			If MilliSecs()-timeout>10000 Then
				Return 0
			EndIf
		Wend
		;mp_joinserver
		;1 - Ok
		;2 - Map Get
		;3 - Map Differs
		;RuntimeError("JOINED! Code: "+mp_joinserver)
		Select mp_joinserver
			;1 - Ok
			Case 1
				;Load Map
				m_section=Csection_game_mp
				load_map("maps\"+set_sv_map$+".s2","")
				game_playerspawn(mp_id)							;Create Player
				game_setplayer(mp_id)							;Cache own Player Data
				TCunit\player_name$=mp_name$					;Set Player Name
				TCunit\player_ip=mp_ip							;Set Player IP
				TCunit\player_port=udp_port						;Set Player Port
				m_menu=0
				;Send Join Ok
				udp_send(set_sv_ip,set_sv_port,3)
				udp_w_byte(1)
		End Select
		Return 1
	;Error
	Else
		Return 0
	EndIf
End Function


;### Ping Servers
Function usgn_update()
	;Get
	While RecvUDPMsg(udp)
		If ReadAvail(udp)>0 Then
		
			;From Data
			fip=UDPMsgIP(udp)
			fport=UDPMsgPort(udp)
			
			;Only Read Messages from Servers
			For Tserver.Tserver=Each Tserver
				If Tserver\ip=fip Then
					If Tserver\port=fport Then
						id=ReadByte(udp)
						
						Select id
							
							;Server Data
							Case 101
								Tserver\name$=ReadString(udp)
								Tserver\map$=ReadString(udp)
								Tserver\players=ReadByte(udp)
								Tserver\maxplayers=ReadByte(udp)
								Tserver\password=ReadByte(udp)
							
							;Error
							Default
								If set_debug=1 Then
									con_add("ERROR: Unexpected U.S.G.N. Message: "+id,Cbmpf_red)
								EndIf
								While ReadAvail(udp)>0
									ReadByte(udp)
								Wend
							
						End Select
						
						Exit		
					EndIf
				EndIf
			Next
		EndIf
	Wend
	
	;Send
	For Tserver.Tserver=Each Tserver
		If ms-Tserver\timer>3000 Then
			;Info Request
			If Tserver\ping<>0 Or (ms-Tserver\timer>15000) Then
				Tserver\timer=ms
				Tserver\pingstamp=ms
				WriteByte udp,100
				SendUDPMsg udp,Tserver\ip,Tserver\port
				con_add("send request to "+DottedIP(Tserver\ip)+":"+Tserver\port)
			EndIf
		EndIf
	Next
End Function
