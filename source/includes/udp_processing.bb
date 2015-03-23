;Process a Packet and its Messages
Function udp_process(p.Tudp_ipq)
	udp_indb=p\db
	udp_indbp=0
	udp_indbs=BankSize(p\db)
	ip=p\ip
	port=p\port
	While udp_indbp<udp_indbs
		id=udp_r_byte()

		Select id
			
			;1 - Join Request
			Case 1 
				name$=udp_r_tiny()
				pw$=udp_r_tiny()
				mp_server_joinrequest(ip,port,name$,pw$)
				
			;2 - Join Answer
			Case 2
				sub=udp_r_byte()
				Select sub
					;1 - Ok
					Case 1
						mp_name$=udp_r_tiny()
						mp_id=udp_r_byte()
						mp_ip=udp_r_int()
						set_sv_name$=udp_r_tiny()
						set_sv_map$=udp_r_tiny()
						mapid$=udp_r_tiny()
						mp_client_checkmap(set_sv_map$,mapid$)
					;2 - Wrong Password
					Case 2
						RuntimeError("Join: Wrong Password")
					;3 - Full
					Case 3
						RuntimeError("Join: Full")		
					;4 - Banned
					Case 2
						RuntimeError("Join: Banned")
				End Select
						
			;3 - Join Stuff
			Case 3
				sub=udp_r_byte()
				Select sub
					;1 - Ok
					Case 1
						If udp_host=1 Then
							mp_server_sendplayerdata(ip,port)
							if_msg("BLAAAH joined the game!",Cbmpf_green)
						EndIf
					;2 - Map Request
					;3 - Map Send Start
					;4 - Map Send Data
					;5 - Map Send End
					;7 - Map Differs
					;100 - Player Data (existing Player)
					Case 100
						mp_getplayerdata(100)
					;101 - Player Data (new Player)
					Case 101
						mp_getplayerdata(101)

				End Select
				
			;5 - Ping
			Case 5
				sub=udp_r_byte()
				Select sub
					;1 - Request
					Case 1
						pingstamp=udp_r_int()
						udp_send(ip,port,5,1)
						udp_w_byte(2)
						udp_w_int(pingstamp)
					;2 - Answer
					Case 2
						pingstamp=udp_r_int()
						If pingstamp<=MilliSecs() Then
							For Tunit.Tunit=Each Tunit
								If Tunit\id<100 Then
									If Tunit\player_ip=ip Then
										If Tunit\player_port=port Then
											Tunit\player_ping=MilliSecs()-pingstamp
											Exit
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					;3 - Ping Data
					Case 3
						playerid=udp_r_byte()
						ping=udp_r_short()
						For Tunit.Tunit=Each Tunit
							If Tunit\id=playerid Then
								Tunit\player_ping=ping
								Exit
							EndIf
						Next
				End Select
				con_add("GOT PING mode "+sub,Cbmpf_red)
			
			;50 -  Position
			Case 50
				playerid=udp_r_byte()
				x#=udp_r_float()
				y#=udp_r_float()
				z#=udp_r_float()
				yaw#=udp_r_float()
				For Tunit.Tunit=Each Tunit
					If Tunit\id=playerid Then
						PositionEntity Tunit\h,x#,y#,z#
						RotateEntity Tunit\h,0,yaw#,0
						Exit
					EndIf
				Next
				con_add("GOT POS! #"+playerid)
					
			;100 - Message
			Case 100
				sub=udp_r_byte()
				Select sub
					;1 - Chat
					Case 1
						playerid=udp_r_byte()
						msg$=udp_r_txt$()
						For Tunit.Tunit=Each Tunit
							If Tunit\id=playerid Then
								if_msg(Tunit\player_name$+": "+msg$,0,Cchatshow)
								Exit
							EndIf
						Next
						If udp_host=1 Then
							For Tudp_con.Tudp_con=Each Tudp_con
								If Tudp_con\ip<>ip And Tudp_con\port<>port Then
									udp_send(Tudp_con\ip,Tudp_con\port,100,1)
									udp_w_byte(1)
									udp_w_byte(playerid)
									udp_w_txt(msg$)
								EndIf
							Next
						EndIf
					
					;2 - Servermessage
					Case 2
						msg$=udp_r_txt()
						col=udp_r_byte()
						if_msg(msg$,col)
				End Select
			
			
			;Trash
			Default
				con_add("ERROR: Got unexpected Message (#"+id+")")
			
			
		
		End Select
	
	Wend
End Function
