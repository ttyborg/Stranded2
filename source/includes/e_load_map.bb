;############################################ Engine Load Map


;### LOAD MAP
Function load_map(path$,pw$)
	map_id$="0"
	If FileType(path$)=1 Then map_id$=FileSize(path$)
	Local stream=ReadFile(path$)
	If stream=0 Then con_add("Unable to load Map "+path$,Cbmpf_red):Return 0
	e_clear()
	tmp_loading=1
	
	;### Headers
	
	;Main Header
	bmpf_loadscreen(s$(21),10)
	in$=ReadLine(stream)
	If Left(in$,15)<>"### Stranded II" Then RuntimeError("Invalid Map! (Invalid Header)")
	ReadLine(stream)							;Version
	map_id$=map_id$+ReadLine(stream)			;Date
	map_id$=map_id$+ReadLine(stream)			;Time
	Local f$=ReadLine(stream)					;Format
	map_mode$=ReadLine(stream)					;Mode
	
	;Type Format
	Local tfobject=0
	Local tfunit=0
	Local tfitem=0
	Local tfstring$=ReadLine(stream)
	If tfstring$<>"" Then
		tfobject=Int(Mid(tfstring$,1,1))
		tfunit=Int(Mid(tfstring$,2,1))
		tfitem=Int(Mid(tfstring$,3,1))
	EndIf
	
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)							;###
	
	;Image
	If map_image<>0 Then FreeImage(map_image)
	map_image=CreateImage(96,72)
	SetBuffer ImageBuffer(map_image)
	LockBuffer ImageBuffer(map_image)
	For x=0 To 96-1
		For y=0 To 72-1
			r=ReadByte(stream)							;R
			g=ReadByte(stream)							;G
			b=ReadByte(stream)							;B
			rgb=255*$1000000 + r*$10000 + g*$100 + b
			WritePixelFast(x,y,rgb)
		Next
	Next
	UnlockBuffer ImageBuffer(map_image)
	SetBuffer BackBuffer()
	;Debug Show Map Image
	If set_debug_load=1 Then
		gui_win_image("",map_image)
	EndIf

	;Password Header
	Local pwkey=ReadByte(stream)				;Code Key
	Local readpw$=ReadLine(stream)				;Password
	;Check Password (Editor only)
	If m_section=Csection_editor Then			
		If code(pw$,pwkey,0)<>readpw$ Then
			;RuntimeError "|"+code(readpw$,pwkey,1)+"|"			
			con_add("ERROR: Invalid Map Password!",Cbmpf_red)
			tmp_loading=0
			Return 0
		EndIf
		map_password$=pw$
	EndIf
	
	;Debug
	If set_debug_load=1 Then
		gui_msg("Map PW Key: "+pwkey+", PW: "+code(readpw$,pwkey,1)+" (len: "+Len(readpw$)+")")
	EndIf
	
	;Map Vars
	map_name$=getfilename$(path$)						;Map Name
	map_path$=path$										;Map Path
	map_day=ReadInt(stream)								;Map Time Day
	map_hour=ReadByte(stream)							;Map Time Hour
	map_minute=ReadByte(stream)							;Map Time Minute
	map_freezetime=ReadByte(stream)						;Map Time frozen?
	map_skybox$=ReadString(stream)						;Map Skybox
	map_multiplayer=ReadByte(stream)					;Map Multiplayer
	map_climate=ReadByte(stream)						;Map Climate
	map_music$=ReadString(stream)						;Map Music
	map_briefing$=ReadString(stream)					;Map Briefing (Global Map Script)
	map_fog(0)=ReadByte(stream)							;Map Fog R
	map_fog(1)=ReadByte(stream)							;Map Fog G
	map_fog(2)=ReadByte(stream)							;Map Fog B
	map_fog(3)=ReadByte(stream)							;Map Fog Mode
	map_lday=map_day									;Map Time Update last Day
	map_lhour=map_hour									;Map Time Update last Hour
	map_lminute=map_minute								;Map Time Update last Minute
	map_timeupdate=1
	ReadByte(stream)									;Extended Stuff
		
	;Interface
	For i=0 To 9
		in_quickslot(i)=ReadString(stream)				;Quickslot
	Next
	
	;Debug
	If set_debug_load=1 Then
		gui_msg("Map Skybox: "+map_skybox$)
		gui_msg("Map Briefing: "+map_briefing$)
		gui_msg("Map Vars Loaded (day "+map_day+", time "+map_hour+":"+map_minute+")")
	EndIf
	
	;Check
	If map_hour<0 Or map_hour>24 Then
		con_add("ERROR: Error in Map Vars (Hour)!",Cbmpf_red)
		tmp_loading=0
		Return 0
	ElseIf map_minute<0 Or map_minute>60 Then
		con_add("ERROR: Error in Map Vars (Minute)!",Cbmpf_red)
		tmp_loading=0
		Return 0
	EndIf
	
	
	;### Maps
	
	;Colormap
	bmpf_loadscreen(s$(22),20)					
	h=ReadInt(stream)									;Colortexture Size
	ter_tex_color=CreateTexture(h,h,256)
	SetBuffer TextureBuffer(ter_tex_color)
	LockBuffer TextureBuffer(ter_tex_color)
	For x=0 To h-1
		For y=0 To h-1
			r=ReadByte(stream)							;R
			g=ReadByte(stream)							;G
			b=ReadByte(stream)							;B
			rgb=255*$1000000 + r*$10000 + g*$100 + b
			WritePixelFast(x,y,rgb)
		Next
	Next
	UnlockBuffer TextureBuffer(ter_tex_color)
	SetBuffer BackBuffer()
	
	;Debug
	If set_debug_load=1 Then gui_msg("Colormap loaded ("+h+"x"+h+")")
	
	;Heightmap
	bmpf_loadscreen(s$(23),30)
	ter_size=ReadInt(stream)							;Terrain Size
	If ter_size<0 Then ter_size=32
	e_terrain(ter_size,"generated")
	For x=0 To ter_size
		For y=0 To ter_size
			ModifyTerrain(ter,x,y,ReadFloat(stream))	;Height
		Next
	Next
	
	;Debug
	If set_debug_load=1 Then gui_msg("Heightmap loaded ("+ter_size+"x"+ter_size+")")

	;Grassmap
	bmpf_loadscreen(s$(24),35)
	grass_map()
	For x=0 To h
		For y=0 To h
			grass_rgb(x,y,3)=ReadByte(stream)			;Gras? 1/0
		Next
	Next
	grass_spread()
	
	;Debug
	If set_debug_load=1 Then gui_msg("Grassmap loaded")
	
	;Ground Y
	ter_groundy=TerrainY(ter,-((ter_size/2)*Cworld_size),0,-((ter_size/2)*Cworld_size))

	
	;on:preload
	map_briefing_key$=preparse_string$(map_briefing$)
	parse_globalevent("preload")
	parse_sel_event("preload")
	
	
	;### Stuff
	
	;Object
	bmpf_loadscreen(s$(25),40)
	c=ReadInt(stream)													;Count
	For i=1 To c
		id=ReadInt(stream)												;ID
		If tfobject Then												;Typ
			typ=ReadShort(stream)
		Else
			typ=ReadByte(stream)
		EndIf
		xp#=ReadFloat(stream)											;X
		zp#=ReadFloat(stream)											;Z
		yaw#=ReadFloat(stream)											;Yaw
		If set_object(id,typ,xp#,zp#,yaw#)<>-1							;SET
			TCobject\health#=ReadFloat(stream)							;Health
			TCobject\health_max#=ReadFloat(stream)						;Health Max
			TCobject\daytimer=ReadInt(stream)							;Daytimer
			HideEntity TCobject\h
			If TCobject\daytimer<0 Then									;Grow
				grow_object(TCobject)
			EndIf
		Else
			RuntimeError("Object of type "+typ+" is undefined! Are you sure that you are using the right mod/version for this map?")
		EndIf
	Next
	serialpeak_object()
	;Debug
	If set_debug_load=1 Then gui_msg("Objects loaded ("+c+")")
	
	;Unit
	bmpf_loadscreen(s$(26),70)
	c=ReadInt(stream)													;Count
	For i=1 To c
		id=ReadInt(stream)												;ID
		If tfunit Then													;Typ
			typ=ReadShort(stream)
		Else
			typ=ReadByte(stream)
		EndIf
		xp#=ReadFloat(stream)											;X
		yp#=ReadFloat(stream)											;Y
		zp#=ReadFloat(stream)											;Z
		If set_unit(id,typ,xp#,zp#)<>-1 Then							;SET
			HideEntity TCunit\h											;POSITION
			PositionEntity TCunit\h,xp#,yp#,zp#								
			ShowEntity TCunit\h
			If TCunit\vh<>0 Then
				HideEntity TCunit\vh
				PositionEntity TCunit\vh,xp#,yp#,zp#
				ShowEntity TCunit\vh
			EndIf
			yaw#=ReadFloat(stream)										;Yaw
			RotateEntity TCunit\h,0,yaw#,0								;ROTATE
			TCunit\health#=ReadFloat(stream)							;Health
			TCunit\health_max#=ReadFloat(stream)						;Health Max
			TCunit\hunger#=ReadFloat(stream)							;Hunger
			TCunit\thirst#=ReadFloat(stream)							;Thirst
			TCunit\exhaustion#=ReadFloat(stream)						;Exhaustion
			TCunit\ai_cx#=ReadFloat(stream)								;AI Center X
			TCunit\ai_cz#=ReadFloat(stream)								;AI Center Z
			If m_section<>Csection_editor Then ai_ini()					;Initialize AI
		Else
			RuntimeError("Unit of type "+typ+" is undefined! Are you sure that you are using the right mod/version for this map?")
		EndIf
	Next
	serialpeak_unit()
	;Debug
	If set_debug_load=1 Then gui_msg("Units loaded ("+c+")")
		
	;Item
	bmpf_loadscreen(s$(27),80)
	c=ReadInt(stream)													;Count
	For i=1 To c
		id=ReadInt(stream)												;ID
		If tfitem Then													;Typ
			typ=ReadShort(stream)
		Else
			typ=ReadByte(stream)
		EndIf
		xp#=ReadFloat(stream)											;X
		yp#=ReadFloat(stream)											;Y
		zp#=ReadFloat(stream)											;Z
		If set_item(id,typ,xp#,yp#,zp#)<>-1 Then						;SET
			yaw#=ReadFloat(stream)										;Yaw
			RotateEntity TCitem\h,0,yaw#,0								;ROTATE
			TCitem\health#=ReadFloat(stream)							;Health
			TCitem\count=ReadInt(stream)								;Count
			TCitem\parent_class=ReadByte(stream)						;Parent Class
			TCitem\parent_mode=ReadByte(stream)							;Parent Mode
			TCitem\parent_id=ReadInt(stream)							;Parent ID
			If TCitem\parent_mode=Cpm_out Then
				If TCitem\parent_class<>0 Then 
					RotateEntity TCitem\h,-90,yaw#,0
				EndIf
			EndIf
		Else
			RuntimeError("Item of type "+typ+" is undefined! Are you sure that you are using the right mod/version for this map?")
		EndIf
	Next
	serialpeak_item()
	;Debug
	If set_debug_load=1 Then gui_msg("Items loaded ("+c+")")
	
	;Info
	bmpf_loadscreen(s$(28),90)
	c=ReadInt(stream)
	For i=1 To c
		id=ReadInt(stream)												;ID
		typ=ReadByte(stream)											;Typ
		xp#=ReadFloat(stream)											;X
		yp#=ReadFloat(stream)											;Y
		zp#=ReadFloat(stream)											;Z
		set_info(id,typ,xp#,yp#,zp#)									;SET
		pitch#=ReadFloat(stream)										;Pitch
		yaw#=ReadFloat(stream)											;Yaw
		RotateEntity TCinfo\h,pitch#,yaw#,0								;ROTATE
		TCinfo\vars$=ReadString(stream)									;Vars
		info_stv()														;String to Vars
		Select typ
			Case 5 info_setupsprite(TCinfo)
			Case 47 info_setuploudspeaker(TCinfo)
		End Select
	Next
	serialpeak_info()
	;Debug
	If set_debug_load=1 Then gui_msg("Infos loaded ("+c+")")
	
	;State
	bmpf_loadscreen(s$(29),92)
	c=ReadInt(stream)													;Count
	For i=1 To c
		typ=ReadByte(stream)											;Typ
		parent_class=ReadByte(stream)									;Parent Class
		parent_id=ReadInt(stream)										;Parent ID
		If set_state(typ,parent_class,parent_id) Then					;SET
			TCstate\x#=ReadFloat(stream)								;X
			TCstate\y#=ReadFloat(stream)								;Y
			TCstate\z#=ReadFloat(stream)								;Z
			TCstate\fx#=ReadFloat(stream)								;FX
			TCstate\fy#=ReadFloat(stream)								;FY
			TCstate\fz#=ReadFloat(stream)								;FZ
			TCstate\value=ReadInt(stream)								;Value
			TCstate\value_f#=ReadFloat(stream)							;Value Float
			TCstate\value_s$=ReadString(stream)							;Value String
			look_state()
		Else
			ReadFloat(stream):ReadFloat(stream):ReadFloat(stream)
			ReadFloat(stream):ReadFloat(stream):ReadFloat(stream)
			ReadInt(stream):ReadFloat(stream):ReadString(stream)
			con_add("Unable to create state "+typ+" ("+Dstate_name$(typ)+") @ "+parent_class+","+parent_id)
		EndIf
	Next
	;Debug
	If set_debug_load=1 Then gui_msg("States loaded ("+c+")")
	
	;Extensions and Vars etc.
	bmpf_loadscreen(s$(30),95)
	c=ReadInt(stream)													;Count
	For i=1 To c
		Tx.Tx=New Tx
		Tx\typ=ReadByte(stream)											;Typ
		Tx\parent_class=ReadByte(stream)								;Parent Class
		Tx\parent_id=ReadInt(stream)									;Parent ID
		Tx\mode=ReadInt(stream)											;Mode
		Tx\key$=ReadString(stream)										;Key
		Tx\value$=ReadString(stream)									;Value
		Tx\stuff$=ReadString(stream)									;Stuff
	Next
	load_map_extensions()
	;Debug
	If set_debug_load=1 Then gui_msg("Extensions loaded ("+c+")")
	
	;Set Definition Vars with default Values on Start (not in editor!)
	If m_section<>Csection_editor Then
		If map_mode$="map" Then
			defvar_ini()
		EndIf
	EndIf
	
	;Load Cached Extensions (loadmap command)
	pv_loadmaploaded=0
	For Txc.Txc=Each Txc
		pv_loadmaploaded=1
		If Txc\mode<7 Then
			;Cleanup
			Select Txc\mode
				;1 - Global Var
				Case 1
					For cx.Tx=Each Tx
						If cx\mode=Txc\mode Then
							If cx\key$=Txc\key$ Then Delete cx
						EndIf
					Next
				;3 - Buildlocks
				Case 3
					For cx.Tx=Each Tx
						If cx\mode=Txc\mode Then
							If cx\key$=Txc\key$ Then Delete cx
						EndIf
					Next					
				;5 - Skills
				Case 5
					For cx.Tx=Each Tx
						If cx\mode=Txc\mode Then
							If cx\key$=Txc\key$ Then Delete cx
						EndIf
					Next
			End Select
			;Create
			Tx.Tx=New Tx
			Tx\typ=Txc\typ
			Tx\parent_class=Txc\parent_class
			Tx\parent_id=Txc\parent_id
			Tx\mode=Txc\mode
			Tx\key$=Txc\key$
			Tx\value$=Txc\value$
			Tx\stuff$=Txc\stuff$
			Delete Txc
		EndIf
	Next
	
	;Load Cam Angels (sav)
	If map_mode$="sav" Then
		Local campitch#=ReadFloat(stream)
		Local camyaw#=ReadFloat(stream)
		RotateEntity cam,campitch#,camyaw#,0
	EndIf
	
	;Pre Parse Scripts
	preparse()
	

	;### End
	ReadLine(stream)
	ReadLine(stream)
	in$=ReadLine(stream)
	If in$<>"www.unrealsoftware.de" Then
		
		;Error
		con_add("ERROR: Unable to load map completely!",3)
		con_add("ERROR: It may be damaged!",3)
		con_add("ERROR: Possible attachments are lost!",3)
		
	Else
	
		;Attachments
		If map_mode$="map" Then
			For Tat.Tat=Each Tat
				Delete Tat
			Next
			While Not Eof(stream)
				atfile$=strinv$(ReadLine(stream))
				atsize=ReadInt(stream)
				Tat.Tat=New Tat
				Tat\path$=atfile$
				If FileType(set_rootdir$+"mods\"+set_moddir$+"\"+atfile$)=1 Then
					For i=1 To atsize
						ReadByte(stream)
					Next
				Else
					atstream=WriteFile(set_rootdir$+"mods\"+set_moddir$+"\"+atfile$)
					If atstream<>0 Then
						For i=1 To atsize
							byte=ReadByte(stream)
							byte=255-byte
							WriteByte(atstream,byte)
						Next
						CloseFile(atstream)
						atstream=0
					EndIf
				EndIf
			Wend
		EndIf
	
	EndIf

	;Music
	If map_music$<>"" Then
		amb_file=LoadSound("sfx\"+map_music$)
		;If amb_file<>0 Then LoopSound amb_file
	EndIf
	
	;Environment
	bmpf_loadscreen(s$(31),97)
	e_environment()
	
	;Spawn Player @ Singleplayer Map
	bmpf_loadscreen(s$(32),100)
	con_add("")
	If m_section=Csection_game_sp Then
		If map_mode$="map" Then
			g_player=1
			game_playerspawn(1)
			game_setplayer(1)
		Else
			game_setplayer(1)
		EndIf
	EndIf
	
	;Load Cached Items and Stuff (loadmap command)
	For Txc.Txc=Each Txc
		;7 Player Item
		If Txc\mode=7 Then
			citem=set_item(-1,Txc\typ,0,0,0,Int(Txc\value$))
			If citem>-1 Then
				store_item(citem,Cclass_unit,g_player)
			EndIf
		;8 Player Quickslot/Weapon/Ammo
		ElseIf Txc\mode=8 Then
			If Txc\typ<10 Then
				in_quickslot(Txc\typ)=Int(Txc\value$)
			ElseIf Txc\typ=10 Then
				g_cplayer\player_weapon=Int(Txc\value$)
			ElseIf Txc\typ=11 Then
				g_cplayer\player_ammo=Int(Txc\value$)
			EndIf
		;9 Player State
		ElseIf Txc\mode=9 Then
			set_state(Txc\typ,Txc\parent_class,Txc\parent_id)
			If get_state(Txc\typ,Txc\parent_class,Txc\parent_id)
				split$(Txc\stuff$,"ż",15)
				TCstate\x#=Float(splits(0))
				TCstate\y#=Float(splits(1))
				TCstate\z#=Float(splits(2))
				TCstate\fx#=Float(splits(3))
				TCstate\fy#=Float(splits(4))
				TCstate\fz#=Float(splits(5))
				TCstate\value=Float(splits(6))
				TCstate\value_f#=Float(splits(7))
				TCstate\value_s$=Float(splits(8))
				TCstate\r=Float(splits(9))
				TCstate\g=Float(splits(10))
				TCstate\b=Float(splits(11))
				look_state()
			EndIf
		EndIf
		Delete Txc
	Next
	
	;Parse Scripts
	tmp_loading=0
	If map_mode$="map" Then
		;on:start
		parse_globalevent("start")
		parse_sel_event("start")
	EndIf
	;on:load
	parse_globalevent("load")
	parse_sel_event("load")
	
	
	;Flush
	FlushKeys()
	FlushMouse()
	;Finish
	CloseFile(stream)
	blackframe=10
	
	;Unit Physics Stuff
	lastms=MilliSecs()
	ms=lastms
	gt=0
	
	;Setcam
	m=m_menu
	m_menu=0
	game_setcam()
	m_menu=m
	
	Return 1
End Function


;### LOAD MAP ID
Function load_map_id$(path$)
	id$="0"
	If FileType(path$)=1 Then
		id$=FileSize(path$)
		Local stream=ReadFile(path$)
		If stream<>0 Then
			ReadLine(stream)						;Header
			ReadLine(stream)						;Version
			id$=id$+ReadLine(stream)				;Date
			id$=id$+ReadLine(stream)				;Time
			CloseFile(stream)
		EndIf
	EndIf	
	Return id$
End Function


;### LOAD MAP HEADER
Function load_map_header(path$,image=1)
	Local stream=ReadFile(path$)
	If stream=0 Then con_add("Unable to load Map Info from "+path$,Cbmpf_red):Return 0
	
	;### Headers
	
	;Main Header
	in$=ReadLine(stream)
	If Left(in$,15)<>"### Stranded II" Then RuntimeError("Invalid Map! (Invalid Header)")
	ReadLine(stream)							;Version
	sg_date$=ReadLine(stream)					;Date
	sg_time$=ReadLine(stream)					;Time
	Local f$=ReadLine(stream)					;Format
	map_mode$=ReadLine(stream)					;Mode
	ReadLine(stream)							;Type Format
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)
	ReadLine(stream)							;###
	
	;Image
	If image=1 Then
		If map_image<>0 Then FreeImage(map_image)
		map_image=CreateImage(96,72)
		SetBuffer ImageBuffer(map_image)
		LockBuffer ImageBuffer(map_image)
		For x=0 To 96-1
			For y=0 To 72-1
				r=ReadByte(stream)							;R
				g=ReadByte(stream)							;G
				b=ReadByte(stream)							;B
				rgb=255*$1000000 + r*$10000 + g*$100 + b
				WritePixelFast(x,y,rgb)
			Next
		Next
		UnlockBuffer ImageBuffer(map_image)
		SetBuffer BackBuffer()
		MaskImage map_image,255,0,255
	Else
		For x=0 To 96-1
			For y=0 To 72-1
				ReadByte(stream)
				ReadByte(stream)
				ReadByte(stream)
			Next
		Next
	EndIf
		
	;Password Header
	ReadByte(stream)									;Code Key
	ReadLine(stream)									;Password

	;Map Vars
	ReadInt(stream)										;Map Time Day
	ReadByte(stream)									;Map Time Hour
	ReadByte(stream)									;Map Time Minute
	ReadByte(stream)									;Map Time frozen?
	ReadString(stream)									;Map Skybox
	sg_multiplayer=ReadByte(stream)						;Map Multiplayer
	ReadByte(stream)									;Map Climate
	ReadString(stream)									;Map Music
	ReadString(stream)									;Map Briefing
	ReadByte(stream)									;Extended Stuff
	
	;Close
	CloseFile(stream)
End Function


;### LOAD SAVEGAMES
Function load_savegames(loadfirst=1)
	;Delete old
	For Tsg.Tsg=Each Tsg
		Delete Tsg
	Next
	;Find
	Local dir=ReadDir("saves")
	Local file$
	If dir<>0 Then
		Repeat
			file$=NextFile(dir)
			If file$="" Then Exit
			If FileType("saves\"+file$)=1 Then
				If Right(file$,4)=".sav" Then
					Tsg.Tsg=New Tsg
					Tsg\name$=Left(file$,Len(file$)-4)
				EndIf
			EndIf
		Forever
		CloseDir dir
	Else
		con_add("ERROR: Unable to read 'saves' dir!",Cbmpf_red)
	EndIf
	;Loadfirst
	If loadfirst Then
		If map_image<>0 Then FreeImage map_image
		For Tsg.Tsg=Each Tsg
			load_map_header("saves\"+Tsg\name$+".sav")
			in_opt(0)=1
			in_input$(0)=""
			Return
		Next
	EndIf
End Function


;### LOAD MAPS
Function load_maps(loadfirst=1,mponly=0)
	;Delete old
	For Tsg.Tsg=Each Tsg
		Delete Tsg
	Next
	;Find
	Local dir=ReadDir("maps")
	Local file$
	If dir<>0 Then
		Repeat
			file$=NextFile(dir)
			If file$="" Then Exit
			;DebugLog file$
			If FileType("maps\"+file$)=1 Then
				If Right(file$,3)=".s2" Then
					Tsg.Tsg=New Tsg
					Tsg\name$=Left(file$,Len(file$)-3)
				EndIf
			EndIf
		Forever
		CloseDir dir
	Else
		con_add("ERROR: Unable to read 'maps' dir!",Cbmpf_red)
	EndIf
	;Delete Maps which are no Multiplayer Maps
	If mponly=1 Then
		For Tsg.Tsg=Each Tsg
			load_map_header("maps\"+Tsg\name$+".s2",0)
			If sg_multiplayer=0 Then
				Delete Tsg
			EndIf
		Next
	ElseIf mponly=-1 Then
		For Tsg.Tsg=Each Tsg
			load_map_header("maps\"+Tsg\name$+".s2",0)
			If sg_multiplayer=1 Then
				Delete Tsg
			EndIf
		Next		
	EndIf
	;Loadfirst
	If loadfirst Then
		If map_image<>0 Then FreeImage map_image
		For Tsg.Tsg=Each Tsg
			load_map_header("maps\"+Tsg\name$+".s2")
			in_opt(0)=1
			in_win_map$=Tsg\name$
			Return
		Next
	EndIf
End Function


;### LOAD MAP EXTENSIONS (PREPARE EXTENSIONS!)
Function load_map_extensions()
	For Tx.Tx=Each Tx
		If Tx\mode=6 Then
			Select Tx\key$
				;Drive/Ride
				Case "g_drive"
					g_drive=Int(Tx\value$)
					If con_unit(g_drive) Then
						;Get Vehicle Stuff
						game_input_vehicledata(TCunit\typ)
						;Set Mode
						TCunit\ai_mode=0
						TCunit\ai_duration=5000
						TCunit\ai_timer=gt
						;Set Ani
						Animate TCunit\mh,0
						TCunit\ani=0
					EndIf
				;Weather
				Case "env_cweather" env_cweather=Int(Tx\value$)
				;Current Weapon/Ammo
				Case "w/a"
					If con_unit(1) Then
						TCunit\player_weapon=Tx\parent_class
						TCunit\player_ammo=Tx\parent_id
						If Tx\value$<>"" Then
							TCunit\player_weapon=Int(Tx\value$)
						EndIf
					EndIf
				;Timer 
				Case "t"
					Ttimer.Ttimer=New Ttimer
					Ttimer\parent_class=Tx\parent_class
					Ttimer\parent_id=Tx\parent_id
					split(Tx\value$,",",3)
					Ttimer\typ=splits$(0)
					Ttimer\duration=splits$(1)
					Ttimer\start=splits$(2)
					Ttimer\mode=splits$(3)
					Ttimer\scr$=Tx\stuff$
				;FRAP (free rotation and position)
				Case "f"
					Tfrap.Tfrap=New Tfrap
					Tfrap\parent_class=Tx\parent_class
					Tfrap\parent_id=Tx\parent_id
					split(Tx\value$,",",2)
					Tfrap\y#=Float(splits$(0))
					Tfrap\pitch#=Float(splits$(1))
					Tfrap\roll#=Float(splits$(2))
					Select Tfrap\parent_class
						;Object
						Case Cclass_object
							If con_object(Tfrap\parent_id) Then
								RotateEntity TCobject\h,Tfrap\pitch#,EntityYaw(TCobject\h),Tfrap\roll#
								PositionEntity TCobject\h,EntityX(TCobject\h),Tfrap\y#,EntityZ(TCobject\h)
								If TCobject\ch<>0 Then
									RotateEntity TCobject\ch,Tfrap\pitch#,EntityYaw(TCobject\h),Tfrap\roll#
									PositionEntity TCobject\ch,EntityX(TCobject\h),Tfrap\y#,EntityZ(TCobject\h)
								EndIf
							EndIf
						;Unit
						Case Cclass_unit
							If con_unit(Tfrap\parent_id) Then
								PositionEntity TCunit\h,EntityX(TCunit\h),Tfrap\y#,EntityZ(TCunit\h)
							EndIf
						;Item
						Case Cclass_item
							If con_item(Tfrap\parent_id) Then
								PositionEntity TCitem\h,EntityX(TCitem\h),Tfrap\y#,EntityZ(TCitem\h)
							EndIf
					End Select
				;Unit Path
				Case "u"
					Tup.Tup=New Tup
					Tup\mode=Tx\parent_class
					Tup\info=Tx\parent_id
					Tup\unit=Int(Tx\value$)
				;Freeze (Ice)
				Case "i"
					If Tx\parent_class=Cclass_unit Then
						For Tunit.Tunit=Each Tunit
							If Tunit\id=Tx\parent_id Then
								Tunit\freeze=1
								Exit
							EndIf
						Next
					EndIf
				;3D Texts
				Case "t3d"	
					t3d.t3d=New t3d
					t3d\class=Tx\parent_class
					t3d\id=Tx\parent_id
					t3d\txt$=Tx\value$
					split(Tx\stuff$,",",2)
					t3d\typ=Int(splits$(0))
					t3d\offset=Int(splits$(1))
					t3d\range=Int(splits$(2))
				;Last Time
				Case "lasttime"
					split(Tx\stuff$,",",2)
					map_lday=Int(splits$(0))
					map_lhour=Int(splits$(1))
					map_lminute=Int(splits$(2))					
					map_timeupdate=1
			End Select
			Delete Tx
		EndIf
	Next
End Function


;Recent Stuff
Function load_recent(add$="")
	;Delete old
	For Trecent.Trecent=Each Trecent
		Delete Trecent
	Next
	Local update=0
	Local limit=0
	Local file=ReadFile("sys/recent.cache")
	If file<>0 Then
		;Add
		If add$<>"" Then
			Trecent.Trecent=New Trecent
			Trecent\file$=add$
		EndIf
		;Read
		While Not Eof(file)
			in$=ReadLine(file)
			If in$<>"" Then
				If in$<>add$ Then
					Trecent.Trecent=New Trecent
					Trecent\file$=in$
					limit=limit+1
					If limit>=15 Then Exit
				EndIf
			EndIf
		Wend
		CloseFile file
		update=1
	Else
		;Add
		If add<>"" Then
			Trecent.Trecent=New Trecent
			Trecent\file$=add$
			update=1
		EndIf
	EndIf
	;Write
	If update=1 Then
		limit=0
		file=WriteFile("sys/recent.cache")
		If file<>0 Then
			For Trecent.Trecent=Each Trecent
				limit=limit+1
				WriteLine file,Trecent\file$
				If limit>=15 Then Exit
			Next
			CloseFile(file)
		EndIf
	EndIf
End Function
