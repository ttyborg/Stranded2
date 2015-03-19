;############################################ CONSOLE

;### Console
Function console()
	;Control Console
	If KeyHit(41) Then con_toggle()
	;Draw Console
	If in_console=0 Then Return 0
	TileBlock gfx_bg(1)
	Tcon.Tcon=Last Tcon
	If in_conscroll>0 Then
		For i=1 To in_conscroll
			Tcon.Tcon=Before Tcon
			If Handle(Tcon)=0 Then in_conscroll=in_conscroll-1:Exit
		Next
	EndIf
	For y=(set_scry-40) To 0 Step -15
		If Handle(Tcon)=0 Then Exit
		If Left(Tcon\txt$,1)="!" Then
			Bmpf_txt(2,y-2,">",Int(Mid(Tcon\txt$,2,1)))
			Bmpf_txt(14,y,Mid(Tcon\txt$,4,-1),Cbmpf_tiny)
		Else
			Bmpf_txt(2,y,Tcon\txt$,Cbmpf_tiny)
		EndIf
		Tcon.Tcon=Before Tcon
	Next
	;Scroll
	in_conscroll=in_conscroll+(in_mzs*3)
	If KeyHit(201) Then in_conscroll=in_conscroll+25
	If KeyHit(209) Then in_conscroll=in_conscroll-25
	If KeyHit(207) Then in_conscroll=0
	If in_conscroll<0 Then in_conscroll=0
	;Input
	in_inputfocus=-1
	Local key=in_key
	If (key>31 And key<127) Then
		in_conin$=in_conin$+Chr(key)
	ElseIf key=4 Then
		in_conin$=""
	ElseIf key=8 Then
		If Len(in_conin$)>0 Then in_conin$=LSet(in_conin$, Len(in_conin$)-1)
	ElseIf key=13 Then
		con_enter()
	EndIf
	in_key=0
	;Remove ^
	If Left(in_conin$,1)="^" Then in_conin$=Mid(in_conin$,2,-1)
	;Draw Input
	If in_cursor Then
		bmpf_txt(2,set_scry-15,in_conin$+"|",Cbmpf_tiny)
	Else
		bmpf_txt(2,set_scry-15,in_conin$,Cbmpf_tiny)
	EndIf
End Function


;### Console Add Message
Function con_add(txt$="",special=-1)
	Tcon.Tcon=New Tcon
	If special=-1 Then
		Tcon\txt$=txt$
	Else
		Tcon\txt$="!"+special+" "+txt$
	EndIf
End Function


;### Console Clear
Function con_clear()
	For Tcon.Tcon=Each Tcon
		Delete Tcon
	Next
End Function


;### Console Welcome
Function con_welcome()
	con_add()
	con_add("****************************************************************************************",0)
	con_add("Welcome to Stranded II",0)
	con_add("You are playing version "+Cversion,0)
	con_add("Web: www.stranded.unrealsoftware.de",0)
	con_add("Mail: stranded2@unrealsoftware.de",0)
	con_add("****************************************************************************************",0)
	con_add()
End Function


;### Console Memory
Function con_memory()
	con_add()
	Local total=TotalVidMem()
	Local avail=AvailVidMem()
	Local used=total-avail
	con_add("*** Video Memory ***",0)
	con_add("Total: "+(total/1024/1024)+" mb ("+(total/1024)+" kb)",Cbmpf_over)
	con_add("Used: "+(used/1024/1024)+" mb ("+(used/1024)+" kb)",Cbmpf_red)
	con_add("Avail: "+(avail/1024/1024)+" mb ("+(avail/1024)+" kb)",Cbmpf_green)
	con_add()
End Function


;### Console 3D
Function con_3d()
	con_add()
	con_add("*** 3D Stats ***",0)
	con_add("Res: "+GraphicsWidth()+"*"+GraphicsHeight()+"*"+GraphicsDepth(),Cbmpf_over)
	con_add("Tris: "+TrisRendered()+" ("+(TrisRendered()/1000)+" k)",Cbmpf_over)
	con_add("Textures: "+ActiveTextures (),Cbmpf_over)
	con_add()
End Function


;### Console Debugmenu
Function con_debugmenu()
	If m_section=Csection_game_sp Then
		m_menu=Cmenu_if_debugmenu
		con_close()
	Else
		con_add("Debug Menu is only available in-game")
	EndIf
End Function


;### Console Debug
Function con_debug()
	set_debug=1-set_debug
	con_add()
	con_add("*** Debug ***",0)
	If set_debug=1 Then
		con_add("Debug mode is ON",Cbmpf_green)
		;Show Unit Collisionstuff
		For Tunit.Tunit=Each Tunit
			If Tunit\vh<>0 Then EntityAlpha Tunit\vh,0.5
			If Tunit\h_pivot=0 Then EntityAlpha Tunit\h,0.1
		Next
	Else
		con_add("Debug mode is OFF",Cbmpf_red)
		;Hide Unit Collisionstuff
		For Tunit.Tunit=Each Tunit
			If Tunit\vh<>0 Then EntityAlpha Tunit\vh,0.
			If Tunit\h_pivot=0 Then EntityAlpha Tunit\h,0
		Next
	EndIf
	con_add()
End Function


;### Console Debug
Function con_debugtimes()
	set_debug_rt=1-set_debug_rt
	con_add()
	con_add("*** Debug Times ***",0)
	If set_debug_rt=1 Then
		con_add("Debug Times ON",Cbmpf_green)
	Else
		con_add("Debug Times OFF",Cbmpf_red)
	EndIf
	con_add()
End Function


;### Sequences Debug
Function con_debugsequences()
	set_debug_seq=1-set_debug_seq
	con_add()
	con_add("*** Debug Sequences ***",0)
	If set_debug_seq=1 Then
		con_add("Debug Sequences ON",Cbmpf_green)
	Else
		con_add("Debug Sequences OFF",Cbmpf_red)
	EndIf
	con_add()
End Function


;### Console Debug
Function con_debugmap()
	If m_section=Csection_game_sp Or m_section=Csection_game_mp Then
		set_debug_menu=1
		m_menu=Cmenu_if_debug
		con_close()
	Else
		con_add("Debugmap is only available in a single- or multiplayer game",Cbmpf_red)
	EndIf
End Function


;### Script Debug
Function con_debugscript()
	con_add()
	con_add("*** Debug Script ***",0)
	For i=0 To (p_lines-1)
		con_add(zerofill$((i+1),3)+": "+Dpc$(i))
	Next
End Function


;### Console Debug Load
Function con_debugload()
	set_debug_load=1-set_debug_load
	con_add()
	con_add("*** Debug Load ***",0)
	If set_debug_load=1 Then
		con_add("Debug Load ON",Cbmpf_green)
	Else
		con_add("Debug Load OFF",Cbmpf_red)
	EndIf
	con_add()
End Function

;### Console Online
Function con_online()
	Local stream,online,ip$,ping
	stream=OpenTCPStream("www.usgn.unrealsoftware.de",80)
	If stream<>0 Then
		ping=MilliSecs()
		WriteLine stream,"GET http://www.usgn.unrealsoftware.de/getip.php"
		ip$=ReadLine(stream)
		ping=MilliSecs()-ping
		If ip$<>"" Then online=1
		CloseTCPStream(stream)
	EndIf
	con_add()
	con_add("*** Online ***",0)
	If online=1 Then
		con_add("You are ONLINE",Cbmpf_green)
		con_add("IP: "+ip$,Cbmpf_over)
		con_add("Ping: "+ping+" ms (usgn.unrealsoftware.de)",Cbmpf_over)
	Else
		con_add("You are OFFLINE",Cbmpf_red)
	EndIf
	con_add()
End Function


;### Console Save
Function con_save()
	Local name$,stream
	name$="CON_D_"+CurrentDate()+"_T_"+CurrentTime()+".txt"
	name$=Replace(name$," ","_")
	name$=Replace(name$,":","_")
	stream=WriteFile(name$)
	If stream<>0 Then
		For Tcon.Tcon=Each Tcon
			WriteLine(stream,Tcon\txt$)
		Next
		CloseFile(stream)
		con_add("Console output saved to '"+name$+"'",Cbmpf_green)
	Else
		con_add("ERROR: Unable to save console output to '"+name$+"'",Cbmpf_red)
	EndIf
End Function


;### Console Listplayers
Function con_listplayers()
	con_add()
	con_add("*** Player List ***",0)
	For Tunit.Tunit=Each Tunit
		If Tunit\id<100 Then
			If Tunit\id=g_player Then
				con_add("#"+Tunit\id+" "+Tunit\player_name$+" ("+DottedIP(Tunit\player_ip)+":"+Tunit\player_port+")", Cbmpf_green)
			Else
				con_add("#"+Tunit\id+" "+Tunit\player_name$+" ("+DottedIP(Tunit\player_ip)+":"+Tunit\player_port+")", Cbmpf_yellow)
			EndIf
		EndIf
	Next
End Function


;### Console Listvars
Function con_listvars()
	con_add()
	con_add("*** Variables List ***",0)
	For Tscr.Tx=Each Tx
		If Tscr\mode=1 Then
			con_add("GLOBAL $"+Tscr\key$+" = "+Tscr\value$)
		EndIf
	Next
	For Tscr.Tx=Each Tx
		If Tscr\mode=4 Then
			con_add("LOCAL $"+Tscr\key$+" = "+Tscr\value$+" @ "+Tscr\parent_class+","+Tscr\parent_id)
		EndIf
	Next
End Function

;### Console Listconst
Function con_listconst()
	con_add()
	con_add("*** Constants List ***",0)
	For Tconst.Tconst=Each Tconst
		con_add("CONST #"+Tconst\name$+" = "+Tconst\value)
	Next
End Function


;### Console Listitems
Function con_listitems()
	con_add()
	con_add("*** Item List ***",0)
	For Titem.Titem=Each Titem
		If Titem\parent_class=0 Then
			con_add("#"+Titem\id+" "+Ditem_name$(Titem\typ)+" "+Titem\count+"x",Cbmpf_yellow)
		Else
			con_add("#"+Titem\id+" "+Ditem_name$(Titem\typ)+" "+Titem\count+"x (stored "+Titem\parent_class+","+Titem\parent_id+","+Titem\parent_mode+")",Cbmpf_dark)
		EndIf
	Next
End Function


;### Console Listscripts
Function con_listscripts()
	con_add()
	con_add("*** Scripts List ***",0)
	If map_briefing$<>"" Then
		con_add("Script @ 0,0 (Map Script)")
		txt$=map_briefing$
		Repeat
			x=Instr(txt$,"Ś")
			If x<>0 Then
				con_add(Left(txt$,x-1))
				txt$=Mid(txt$,x+1)
			Else
				con_add(txt$)
				Exit
			EndIf
		Forever
	EndIf
	For Tscr.Tx=Each Tx
		If Tscr\mode=0 Then
			If parent_h(Tscr\parent_class,Tscr\parent_id)<>0 Then
				con_add("Script @ "+Tscr\parent_class+","+Tscr\parent_id,Cbmpf_yellow)
				txt$=Tscr\value$
				Repeat
					x=Instr(txt$,"Ś")
					If x<>0 Then
						con_add(Left(txt$,x-1))
						txt$=Mid(txt$,x+1)
					Else
						con_add(txt$)
						Exit
					EndIf
				Forever
			Else
				con_add("Script @ "+Tscr\parent_class+","+Tscr\parent_id+" - PARENT DOES NOT EXIST! -> DELETED",Cbmpf_red)
				txt$=Tscr\value$
				Repeat
					x=Instr(txt$,"Ś")
					If x<>0 Then
						con_add(Left(txt$,x-1))
						txt$=Mid(txt$,x+1)
					Else
						con_add(txt$)
						Exit
					EndIf
				Forever
				Delete Tscr
			EndIf
		EndIf
	Next
End Function


;### Console Listfraps
Function con_listfraps()
	con_add()
	con_add("*** FRAP List ***",0)
	For Tfrap.Tfrap=Each Tfrap
		c=c+1
	Next
	con_add("FRAP Count: "+c)
End Function


;### Console Wireframe
Function con_wireframe()
	set_debug_wireframe=1-set_debug_wireframe
	con_add()
	con_add("*** Wireframe ***",0)
	If set_debug_wireframe=1 Then
		con_add("Wireframe mode is ON",Cbmpf_green)
	Else
		con_add("Wireframe mode is OFF",Cbmpf_red)
	EndIf
	WireFrame set_debug_wireframe
End Function


;### Mod Info
Function con_modinfo()
	con_add()
	con_add("*** Mod Info ***",0)
	con_add("Name/Dir: "+set_moddir$,1)
	con_add("---------------",0)	
	con_add("Objects: "+object_count,1)
	con_add("Units: "+unit_count,1)
	con_add("Items: "+item_count,1)
	con_add("Infos: "+info_count,1)
	con_add("---------------",0)	
	Local c=0
	For Tbui.Tbui=Each Tbui
		If Tbui\mode=2 Then c=c+1
	Next
	con_add("Buildings: "+c,1)
	c=0
	For Tcom.Tcom=Each Tcom
		If Tcom\mode=2 Then c=c+1
	Next
	con_add("Combinations: "+c,1)
	con_add()
End Function


;### Console Enter
Function con_enter()
	Select Trim(in_conin$)
		Case "clear","cls"
			con_clear()
		Case "close"
			in_console=0
		Case "quit","exit"
			m_performquit=1
		Case "welcome","info","version"
			con_welcome()
		Case "memory"
			con_memory()
		Case "3d","tris"
			con_3d()
		Case "debugmenu","cheat","dm"
			con_debugmenu()
		Case "debug"
			con_debug()
		Case "t","debugtimes","times"
			con_debugtimes()
		Case "seq","debugsequences","debugseq","sequences"
			con_debugsequences()
		Case "debugmap"
			con_debugmap()
		Case "debugscript"
			con_debugscript()
		Case "debugload"
			con_debugload()
		Case "online","ip","ping","net","www"
			con_online()
		Case "save"
			con_save()
		Case "listplayers","playerlist"
			con_listplayers()
		Case "vars","listvars","debugvars"
			con_listvars()
		Case "items","listitems"
			con_listitems()
		Case "scripts","listscripts"
			con_listscripts()
		Case "fraps","listfraps"
			con_listfraps()
		Case "wireframe","wf"
			con_wireframe
		Case "centers"
			For Tunit.Tunit=Each Tunit
				con_add(EntityX(Tunit\ai_ch)+"|"+EntityZ(Tunit\ai_ch))
			Next
		Case "gore"
			set_gore=1
		Case "nogore"
			set_gore=0
		Case "interface","if"
			set_drawinterface=1-set_drawinterface
		Case "const"
			con_listconst()
		Case "modinfo"
			con_modinfo()
		Case ""
			con_add("")
		Default
			con_add("ERROR: Unknown Command '"+Trim(in_conin$)+"'",Cbmpf_red)
	End Select
	in_conin$=""
End Function


;### Console Toggle
Function con_toggle(set=-1)
	in_console=1-in_console
	If set<>-1 Then in_console=set
	FlushKeys()
	in_conin$=""
	in_conscroll=0
End Function


;### Console Close
Function con_close()
	con_toggle(0)
End Function


;### Console Open
Function con_open()
	con_toggle(1)
End Function
