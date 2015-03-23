;############################################ MULTIPLAYER GAME FUNCTIONS


;### Check Name and Return Valid Name
Function mp_checkname$(name$,checkexistence=1)
	Local s,i
	name$=Trim(name$)
	If Len(name$)>15 Then
		name$=Left(name$,15)
	EndIf
	For i=1 To Len(name$)
		s=Asc(Mid(name$,i,1))
		If s<48 Then
			Select s
				Case 32,40,41,45,46
				Default name$=Replace(name$,Chr(s),"_")
			End Select
		ElseIf s>57 And s<65
			name$=Replace(name$,Chr(s),"_")
		ElseIf s>90 And s<97 Then
			Select s
				Case 91,93,95
				Default name$=Replace(name$,Chr(s),"_")
			End Select
		ElseIf s>122 Then
			name$=Replace(name$,Chr(s),"_")
		EndIf
	Next
	name$=Replace(name$,"__","_")
	If name$="" Then name$="Mr. Stranded"
	;Exists?
	If checkexistence=1 Then
		Local free=1
		For Tunit.Tunit=Each Tunit
			If Tunit\id<100 Then
				If Tunit\player_name$=name$ Then
					free=0
					Exit
				EndIf
			EndIf
		Next
		;Change
		If free=0 Then
			i=2
			Local set=0
			While set=0
				free=1
				For Tunit.Tunit=Each Tunit
					If Tunit\id<100 Then
						If Tunit\player_name$=name$+"("+i+")" Then
							free=0
							Exit
						EndIf
					EndIf
				Next
				If free=1 Then
					set=1
					name$=name$+"("+i+")"
				Else
					i=i+1
				EndIf
			Wend
		EndIf
	EndIf
	;Return
	Return name$
End Function


;### Get a unused Player ID (1-100)
Function mp_getplayerid()
	For i=1 To 100
		free=1
		For Tunit.Tunit=Each Tunit
			If Tunit\id=i Then free=0:Exit
		Next
		If free=1 Then Return i
	Next
	Return 0
End Function


;### Get Player
Function mp_getplayer(ip,port)
	For Tunit.Tunit=Each Tunit
		If Tunit\id<100 Then
			If Tunit\player_ip=ip Then
				If Tunit\player_port=port Then
					TCunit.Tunit=Tunit
					Return Tunit\id
				EndIf
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Handle Joinrequest
Function mp_server_joinrequest(ip,port,name$,pw$)
	;Name
	name$=mp_checkname$(name$,1)
	;Check Password
	If pw$=set_sv_pw$ Or set_sv_pw$="" Then
		;Check Playerlimit
		players=0
		For Tunit.Tunit=Each Tunit
			If Tunit\id<100 Then
				players=players+1
			EndIf
		Next
		If players<set_sv_playerlimit Then
			;Accept
			found=0
			freeid=2
			While found=0
				free=1
				For Tunit.Tunit=Each Tunit
					If Tunit\id=freeid Then
						free=0
						Exit
					EndIf
				Next
				If free=1 Then
					found=1
				Else
					freeid=freeid+1
				EndIf
			Wend
			game_playerspawn(freeid,-1)
			con_unit(freeid)
			TCunit\player_name$=name$						;Set Player Name
			TCunit\player_ip=ip								;Set Player IP
			TCunit\player_port=port							;Set Player Port
			;Send Join Accept
			udp_send(ip,port,2)
			udp_w_byte(1)
			udp_w_tiny(name$)								;Player Name
			udp_w_byte(freeid)								;Player ID
			udp_w_int(ip)									;Player IP
			udp_w_tiny(set_sv_name$)						;Server Name
			udp_w_tiny(set_sv_map$)							;Server Map
			udp_w_tiny(map_id$)								;Server Map ID
			udp_w_int(tmp_spawnid)
			;Output
			if_msg(name$+" connected!",Cbmpf_yellow)
		Else
			;Full
			udp_send(ip,port,2)
			udp_w_byte(3)
		EndIf
	Else
		;Wrong Password
		udp_send(ip,port,2)
		udp_w_byte(2)
	EndIf
End Function


;### Client Check Map
Function mp_client_checkmap(map$,mapid$)
	ownid$=load_map_id("maps\"+map$+".s2")
	If ownid$="0" Then
		;Request!
		udp_send(set_sv_ip,set_sv_port,3)
		udp_w_byte(2)
		mp_joinserver=2
	Else
		If ownid$=mapid$ Then
			;Ok!
			mp_joinserver=1
		Else
			;Wrong! Map Differs!
			udp_send(set_sv_ip,set_sv_port,3)
			udp_w_byte(7)
			mp_joinserver=3
		EndIf
	EndIf
End Function


;### Send Player Data
Function mp_server_sendplayerdata(ip,port)
	;Get Player
	id=mp_getplayer(ip,port)
	If id<>0 Then
		;Send Data of other Players to new Player
		For Tunit.Tunit=Each Tunit
			If Tunit\id<100 Then
				If Tunit\id<>id Then
					udp_send(ip,port,3)
					udp_w_byte(100)
					udp_w_tiny(Tunit\player_name$)
					udp_w_byte(Tunit\id)
					udp_w_float(EntityX(Tunit\h))
					udp_w_float(EntityY(Tunit\h))
					udp_w_float(EntityZ(Tunit\h))
					udp_w_float(EntityYaw(Tunit\h))
					udp_w_short(Tunit\player_score)
					udp_w_short(Tunit\player_deads)
					udp_w_byte(1)
					udp_w_short(Tunit\player_ping)
					con_add("send player data "+TCunit\player_name$+" #"+Tunit\id+" To "+DottedIP(ip)+":"+port)
				EndIf
			EndIf
		Next
		;Send Data of New Player to other Players
		For Tunit.Tunit=Each Tunit
			If Tunit\id<100 Then
				If Tunit\id<>1 And Tunit\id<>id Then
					udp_send(Tunit\player_ip,Tunit\player_port,3)
					udp_w_byte(101)
					udp_w_tiny(TCunit\player_name$)
					udp_w_byte(TCunit\id)
					udp_w_float(EntityX(TCunit\h))
					udp_w_float(EntityY(TCunit\h))
					udp_w_float(EntityZ(TCunit\h))
					udp_w_float(EntityYaw(TCunit\h))
					udp_w_short(TCunit\player_score)
					udp_w_short(TCunit\player_deads)
					udp_w_byte(1)
					udp_w_short(Tunit\player_ping)
					con_add("send player data "+TCunit\player_name$+" #"+TCunit\id+" To "+DottedIP(Tunit\player_ip)+":"+Tunit\player_port)
				EndIf
			EndIf
		Next
	EndIf
End Function


;### Get Player Data
Function mp_getplayerdata(mode)
	Local name$=udp_r_tiny()
	Local id=udp_r_byte()
	Local x#=udp_r_float()
	Local y#=udp_r_float()
	Local z#=udp_r_float()
	Local yaw#=udp_r_float()
	Local score=udp_r_short()
	Local deads=udp_r_short()
	Local dead=udp_r_byte()
	Local ping=udp_r_short()
	con_add("get player data "+name$+" #"+id)
	;Create
	If con_unit(id)=0 Then
		set_unit(id,1,x#,z#)
	EndIf
	PositionEntity TCunit\h,x#,y#,z#
	RotateEntity TCunit\h,0,yaw#,0
	TCunit\player_name$=name$
	TCunit\player_score=score
	TCunit\player_deads=deads
	TCunit\player_ping=ping
	;Output
	If mode=101 Then
		if_msg(name$+" joined the game!",Cbmpf_green)
	EndIf
End Function


;### Scoreboard
Function mp_scoreboard()
	;Window
	DrawImage gfx_win,215,0
	DrawImage gfx_icons,236,5,Cicon_globe
	DrawImage gfx_winbar,215,42
	
	;Name
	bmpf_txt(236+37,10,set_sv_name$)
	
	;Adresse
	bmpf_txt_c(236+37+400,7,DottedIP(set_sv_ip)+":"+set_sv_port,Cbmpf_tiny)
	bmpf_txt_c(236+37+400,19,set_sv_map$,Cbmpf_tiny)
	
	;Infos
	bmpf_txt(236,63,"Player",Cbmpf_dark)
	bmpf_txt(236+300,63,"Score",Cbmpf_dark)
	bmpf_txt(236+370,63,"Deads",Cbmpf_dark)
	bmpf_txt(236+470,63,"Net",Cbmpf_dark)
	
	;Players
	y=63+20
	For Tunit.Tunit=Each Tunit
		If Tunit\id<100 Then
			
			;Name
			bmpf_txt(236,y,Tunit\player_name)
			If Tunit\id=1 Then bmpf_txt(236,y+15,"Server",Cbmpf_tiny)
			
			;Ping
			If Tunit\player_ping=0 Then
				bmpf_txt(236+470,y,"...",Cbmpf_dark)
			Else
				col=Cbmpf_red
				If Tunit\player_ping<200 Then col=Cbmpf_yellow
				If Tunit\player_ping<100 Then col=Cbmpf_green
				bmpf_txt(236+470,y,Tunit\player_ping,col)
			EndIf
			
			y=y+40
		
		EndIf
	Next
	
End Function
