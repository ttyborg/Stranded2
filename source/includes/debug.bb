;############################################ Debug Outputs


;### Normal Debug Output
Function debug()
	If set_debug Then
		debugtime=MilliSecs()
		
		;AI
		draw_units_debug()
		
		;FPS
		bmpf_txt_r(set_scrx-3,0,fps+" FPS",5)
		;FPS Factor
		bmpf_txt_r(set_scrx-3,12,Left(f#,5)+" Factor",5)
		
		
		;Pitch	
		;bmpf_txt_r(set_scrx-3,y,Int(EntityPitch(cam)))
		
		;Jump
		;If con_unit(g_player)
		;bmpf_txt_r(set_scrx-3,20, TCunit\phy_jump#+" Jump",5)
		;bmpf_txt_r(set_scrx-3,30, (ms-TCunit\phy_ft)+" Last Col.",5)
		;EndIf
				
		;UDP
		y=450
		For Tudp_con.Tudp_con=Each Tudp_con
			bmpf_txt_r(set_scrx-3,y,DottedIP(Tudp_con\ip)+":"+Tudp_con\port)
			y=y+15
		Next
		
		;Y for Outputs
		y=25
		
		;Debug Times
		If set_debug_rt Then
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_objects$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_units$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_items$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_infos$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_states$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_particles$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_cull$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_poststuff$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_updateworld$,5):y=y+12
			bmpf_txt_r(set_scrx-3,y,set_debug_rt_render+" ms Rendertime",5):y=y+12
			bmpf_txt_r(set_scrx-3,y,(MilliSecs()-debugtime)+" ms Debug",5):y=y+12
		EndIf

		
		;Debug Sequences
		If set_debug_seq Then
			bmpf_txt_r(set_scrx-3,y,"SEQ T: "+(MilliSecs()-seq_start)+" CAM: "+seq_cpoint,5):y=y+12
			i=0
			For Tseq.Tseq=Each Tseq
				bmpf_txt_r(set_scrx-3,y,Tseq\t+" "+Tseq\event$,5)
				y=y+12
				i=i+1
				If i>10 Then Exit
			Next
		EndIf
		
		;Cam Position
		bmpf_txt_r(set_scrx-3,y,"cam: "+Int(EntityX(cam))+"|"+Int(EntityY(cam))+"|"+Int(EntityZ(cam)),5):y=y+12
		;Cam Rotation
		bmpf_txt_r(set_scrx-3,y,"pitch: "+Int(EntityPitch(cam))+" yaw: "+Int(EntityYaw(cam)),5):y=y+12
	
	EndIf
	
	;Testmap
	If set_debug_testmap=1 Then
		If m_section=Csection_game_sp Then
			If set_drawinterface=1 Then
				bmpf_txt_c(set_scrx/2,set_scry-20,se$(204),in_cursor)
			EndIf
			;F12 -> Testmap
			If KeyHit(88) Then
				editor_ini()
				m_section=Csection_editor
				If Instr(set_debug_testmapfile$,":") Then
					load_map(set_debug_testmapfile$+".s2","")
				Else
					load_map("maps\"+set_debug_testmapfile$+".s2","")
				EndIf
				If set_debug_testmapfile$="editor\checkmap" Then
					in_edmap$=""
				Else
					in_edmap$=set_debug_testmapfile$
				EndIf
				set_debug=0
			EndIf
		EndIf
	EndIf
End Function


;### Debug Map
Function debug_map()
	;Scale Factor
	sc#=0.3
	;Back
	;Color 0,0,0
	;Rect 236,63,537,509,1
	;Set Bounds
	Local bx=537/2
	Local bz=509/2
	;Set Center
	Local cx=236+bx
	Local cz=63+bz
	;Calc Center Pos
	Local x,z
	Local camx#=EntityX(cam)
	Local camz#=EntityZ(cam)
	;Units
	For Tunit.Tunit=Each Tunit
		x=(camx#-EntityX(Tunit\h))*sc#
		z=(camz#-EntityZ(Tunit\h))*sc#
		If Abs(x)<bx Then
			If Abs(z)<bz Then
				Color 150,150,150
				Line cx+x,cz+z,cx+((camx#-Tunit\ai_cx#)*sc#),cz+((camz#-Tunit\ai_cz#)*sc#)
				Color 0,0,0
				Rect cx+x-2,cz+z-2,4,4,1
				Color 255,255,0
				Rect cx+x-1,cz+z-1,2,2,1
			EndIf
		EndIf
	Next
	;Center
	Color 255,0,0
	Rect cx-1,cz-1,2,2,1
End Function


;### Debug Log
Function dlog(txt$)
	Local stream
	If FileType(set_rootdir$+"debug.txt")<>1 Then
		stream=WriteFile(set_rootdir$+"debug.txt")
	Else
		stream=OpenFile(set_rootdir$+"debug.txt")
	EndIf
	If stream<>0 Then
		WriteLine stream,txt$
		CloseFile stream
	EndIf
End Function


;### Debug Command
Function dbugcmd(p1$,p2$="")
	con_add()
	con_add("*** Debug: "+p1$+" "+p2$+" ***",Cbmpf_red)
	p1c=parse_getclass(p1$)
	If p1c=-1 Then
		p1$=p_env_class
		p2$=p_env_id
	EndIf
	Select p1$
		Case "map"
			in_conin$="debugmap":con_enter()
		
		Case "vars"
			in_conin$="vars":con_enter()
			
		Case "scripts"
			in_conin$="scripts":con_enter()		
		
		Case "object",Cclass_object
			con_add("object #"+Int(p2$))
			For Tobject.Tobject=Each Tobject
				If Tobject\id=Int(p2$) Then
					con_add("type: "+Tobject\typ+" ("+Dobject_name$(Tobject\typ)+")")
					con_add("health: "+Tobject\health#+"/"+Tobject\health_max#)
					con_add("position: "+Int(EntityX(Tobject\h))+","+Int(EntityY(Tobject\h))+","+Int(EntityZ(Tobject\h)))
					con_add("rotation: "+Int(EntityPitch(Tobject\h))+","+Int(EntityYaw(Tobject\h))+","+Int(EntityRoll(Tobject\h)))
					dbugcmdchilds(p1c,Int(p2$))
					Return
				EndIf
			Next
			con_add("does not exist")
		
		Case "unit",Cclass_unit
			con_add("unit #"+Int(p2$))
			For Tunit.Tunit=Each Tunit
				If Tunit\id=Int(p2$) Then
					con_add("type: "+Tunit\typ+" ("+Dunit_name$(Tunit\typ)+")")
					con_add("health: "+Tunit\health#+"/"+Tunit\health_max#)
					con_add("position: "+Int(EntityX(Tunit\h))+","+Int(EntityY(Tunit\h))+","+Int(EntityZ(Tunit\h)))
					con_add("rotation: "+Int(EntityPitch(Tunit\h))+","+Int(EntityYaw(Tunit\h))+","+Int(EntityRoll(Tunit\h)))
					dbugcmdchilds(p1c,Int(p2$))
					Return
				EndIf
			Next
			con_add("does not exist")
		
		Case "item",Cclass_item
			con_add("unit #"+Int(p2$))
			For Titem.Titem=Each Titem
				If Titem\id=Int(p2$) Then
					con_add("type: "+Titem\typ+" ("+Ditem_name$(Titem\typ)+")")
					con_add("count: "+Titem\count)
					con_add("parent: "+Titem\parent_class+","+Titem\parent_id+" (Mode "+Titem\parent_mode+")")
					con_add("health: "+Titem\health#+"/"+Ditem_health(Titem\typ))
					con_add("position: "+Int(EntityX(Titem\h))+","+Int(EntityY(Titem\h))+","+Int(EntityZ(Titem\h)))
					con_add("rotation: "+Int(EntityPitch(Titem\h))+","+Int(EntityYaw(Titem\h))+","+Int(EntityRoll(Titem\h)))
					dbugcmdchilds(p1c,Int(p2$))
					Return
				EndIf
			Next
			con_add("does not exist")
		
		Case "info",Cclass_info
			con_add("info #"+Int(p2$))
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=Int(p2$) Then
					con_add("type: "+Tinfo\typ+" ("+Dinfo_name$(Tinfo\typ)+")")
					con_add("position: "+Int(EntityX(Tinfo\h))+","+Int(EntityY(Tinfo\h))+","+Int(EntityZ(Tinfo\h)))
					con_add("rotation: "+Int(EntityPitch(Tinfo\h))+","+Int(EntityYaw(Tinfo\h))+","+Int(EntityRoll(Tinfo\h)))
					dbugcmdchilds(p1c,Int(p2$))
					Return
				EndIf
			Next
			con_add("does not exist")
	End Select
End Function


;### Debug Childs
Function dbugcmdchilds(class,id)
	;Vars
	con_add("* locals: ",Cbmpf_red)
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					If Tx\stuff$="" Then
						con_add("$"+Tx\key$+" = "+Tx\value$)
					Else
						con_add("$"+Tx\key$+" = "+Tx\value$+" ("+Tx\stuff$+")")
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;States
	con_add("* states: ",Cbmpf_red)
	For Tstate.Tstate=Each Tstate
		If Tstate\parent_class=class Then
			If Tstate\parent_id=id Then
				con_add(Tstate\typ+" ("+Dstate_name(Tstate\typ)+")")
			EndIf
		EndIf
	Next
	;Units Paths
	If class=Cclass_unit
		con_add("* unitpath: ",Cbmpf_red)
		For Troot.Tup=Each Tup
			If Troot\mode=0 Then
				If Troot\unit=id Then
					con_add("rootnode - current node: "+Troot\info)
					For Tup.Tup=Each Tup
						If Tup\unit=Troot\unit Then
							If Tup\mode>0 Then
								con_add("node "+Tup\mode+" @ info "+Tup\info)
							EndIf
						EndIf
					Next
				EndIf
			EndIf
		Next
	EndIf
	;Items
	If class<>Cclass_item
		con_add("* items: ",Cbmpf_red)
		For Titem.Titem=Each Titem
			If Titem\parent_class=class Then
				If Titem\parent_id=id Then
					con_add(Titem\typ+" ("+Ditem_name$(Titem\typ)+") x"+Titem\count+"")
				EndIf
			EndIf
		Next
	EndIf
	;Scripts
	con_add("* script: ",Cbmpf_red)
	For Tx.Tx=Each Tx
		If Tx\mode=0 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					txt$=Tx\value$
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
			EndIf
		EndIf
	Next
End Function
