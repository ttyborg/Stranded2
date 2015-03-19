;############################################ BUILD


;### Build
Function game_build()
	;Timer Stuff
	con_unit(g_player)
	If gt-TCunit\player_lastattack<Ditem_rate(TCunit\player_weapon) Then Return 0
	TCunit\player_lastattack=gt
	;Scan for nearby buildplace
	For Tobject.Tobject=Each Tobject
		If EntityDistance(cam,Tobject\h)<75 Then
			If get_state(Cstate_buildplace,Cclass_object,Tobject\id) Then
				;Get State
				If get_state(Cstate_buildplace,Cclass_object,Tobject\id)=0 Then Return 0
				Local typ=TCstate\value
				Local id=0
				;Get Building Type ID
				For Tbui.Tbui=Each Tbui
					If Tbui\mode=2 Then
						If Tbui\typ=typ Then
							id=Tbui\id
							Exit
						EndIf
					EndIf
				Next
				If id=0 Then Return 0
				;Scan Req. Stuff
				Local total=0
				Local complete=0
				Local ok
				For Tbui.Tbui=Each Tbui
					If Tbui\id=id Then
						If Tbui\mode=0 Then
							total=total+1
							ok=0
							;Is Req. complete?
							ok=get_stored_item(Tbui\typ,Cclass_object,Tobject\id)
							If ok=1 Then
								If Tbui\count<=TCitem\count Then
									complete=complete+1		
									ok=1
								Else
									ok=0
								EndIf
							EndIf
							;Not complete, try to complete!
							If ok=0 Then
								If get_stored_item(Tbui\typ,Cclass_unit,g_player) Then
									free_item(TCitem\id,1)
									set_item(-1,Tbui\typ)
									p_add2d(set_scrx/2,set_scry/2,-2,Tbui\typ)
									store_item(TCitem\id,Cclass_object,Tobject\id)
									if_msg(ss$(sm$(172),Ditem_name$(Tbui\typ)),Cbmpf_green)
									sfx_emit sfx_build,cam
									;Build Process
									pc_typ=Cstate_pc_build
									pc_gt=gt
									pc_child=Tobject\id
									in_focus=pc_typ
									;Return
									Return 1
								EndIf
							EndIf
						EndIf
					EndIf
				Next
				;Finished?
				If total=complete Then
					;Finished MSG + Sound
					if_msg(sm$(173),Cbmpf_green)
					sfx_emit sfx_build_finish,cam
					buildclass=0
					buildid=0
					;Object (Positive Type)
					If typ>0 Then
						set_object(-1,typ,EntityX(Tobject\h),EntityZ(Tobject\h),EntityYaw(Tobject\h))
						buildclass=Cclass_object
						buildid=TCobject\id
						;Disable Collision, Add Ghost State
						If Dobject_col(TCobject\typ)>0 Then
							If TCobject\ch<>0 Then
								EntityType TCobject\ch,0
							Else
								EntityType TCobject\h,0
							EndIf
						EndIf
						set_state(Cstate_ghost,buildclass,buildid)
					;Unit (Negative Type)
					Else
						set_unit(-1,Abs(typ),EntityX(Tobject\h),EntityZ(Tobject\h))
						RotateEntity TCunit\h,0,EntityYaw(Tobject\h),0
						buildclass=Cclass_unit
						buildid=TCunit\id
					EndIf
					;Smoke FX
					Local y=e_tery(EntityX(Tobject\h),EntityZ(Tobject\h))
					For i=1 To 15
						p_add(EntityX(Tobject\h)+Rnd(-10,10),y+Rand(2,5),EntityZ(Tobject\h)+Rnd(-10,10),20,Rnd(5,10),Rnd(0.3,1.5))
					Next
					;DebugLog Tobject\id
					;Link
					For Tstate.Tstate=Each Tstate
						If Tstate\typ=Cstate_link Then
							;DebugLog "linkstate found value = "+Tstate\value+" ("+Tobject\id+")"
							If Tstate\value=Tobject\id Then
								;DebugLog "value = buildplace id!"
								If buildclass=Cclass_object Then
									set_state(Cstate_link,Tstate\parent_class,Tstate\parent_id)
									TCstate\value=buildid
									;DebugLog "addstate "+TCstate\parent_id+" -> "+buildid
									Exit
								EndIf
							EndIf
						EndIf
					Next
					;Free Buildplace
					If Tobject<>Null Then
						free_object(Tobject\id)
					EndIf
					;Event: on:build
					For Tbui.Tbui=Each Tbui
						If Tbui\mode=2 Then
							If Tbui\id=id Then
								If Tbui\script$<>"" Then
									parse_task(buildclass,buildid,"build","(buildings.inf-script)",Tbui\script$)
								EndIf
								Exit
							EndIf
						EndIf
					Next
					;Event: on:build_finish
					If typ>0 Then
						If Instr(Dobject_scriptk$(typ),"Śbuild_finish") Then
							parse_task(Cclass_object,buildid,"build_finish","",Dobject_script$(typ))
							parse_sel_event("build_finish")
						EndIf
					Else
						If Instr(Dunit_scriptk$(Abs(typ)),"Śbuild_finish") Then
							parse_task(Cclass_unit,buildid,"build_finish","",Dunit_script$(typ))
							parse_sel_event("build_finish")
						EndIf
					EndIf
					;Free Build Process State
					free_state(Cstate_pc_build,Cclass_unit,g_player)
					;Return
					Return 1
				Else
					if_msg(sm$(174),Cbmpf_red)
					sfx sfx_fail
					;Build Process
					pc_typ=Cstate_pc_build
					pc_gt=gt
					pc_child=Tobject\id
					in_focus=pc_typ
				EndIf
				Return 0
			EndIf
		EndIf
	Next
	;Open Build Menu
	in_buildg$=""
	m_menu=Cmenu_if_build
	in_scr_scr=0
End Function


;### Start Buildsetup
Function game_buildsetup_start(typ,changeh=0)
	If changeh=0 Then
		in_buildcamh=100
	Else
		in_buildcamh=changeh
	EndIf
	;Event: on:build_setup
	p_skipevent=0
	If typ>0 Then
		If Instr(Dobject_scriptk$(typ),"Śbuild_setup") Then
			parse_task(0,0,"build_setup","",Dobject_script$(typ))
			parse_sel_event("build_setup")
		EndIf
	Else
		If Instr(Dunit_scriptk$(Abs(typ)),"Śbuild_setup") Then
			parse_task(0,0,"build_setup","",Dunit_script$(Abs(typ)))
			parse_sel_event("build_setup")
		EndIf
	EndIf

	If p_skipevent=0 Then
		;Dummy Model
		If typ>0 Then
			load_object_model(typ)
			in_buildsetup_dummy=CopyEntity(Dobject_modelh(typ))
			ScaleEntity in_buildsetup_dummy,Dobject_size#(typ,0),Dobject_size#(typ,1),Dobject_size#(typ,2)
		Else
			in_buildsetup_dummy=CopyEntity(Dunit_modelh(Abs(typ)))
			ScaleEntity in_buildsetup_dummy,Dunit_size#(Abs(typ),0),Dunit_size#(Abs(typ),1),Dunit_size#(Abs(typ),2)
		EndIf
		EntityBlend in_buildsetup_dummy,3
		EntityColor in_buildsetup_dummy,0,255,0
		Local x#=EntityX(cam)
		Local z#=EntityZ(cam)
		;Buildsetup at Water Surface?
		in_buildsetup_watersurface=0
		If typ>0 Then
			If Dobject_behaviour$(typ)="aligntowater" Then
				in_buildsetup_watersurface=1
			ElseIf Dobject_behaviour$(typ)="buildingsite_water" Then
				in_buildsetup_watersurface=1
			EndIf
		Else
			If Dunit_align(Abs(typ))=1 Then in_buildsetup_watersurface=1
		EndIf
		;Pos
		PositionEntity in_buildsetup_dummy,x#,e_tery(x#,z#),z#
		;Vars
		in_buildsetup_typ=typ
		m_menu=Cmenu_if_buildsetup
		;Get Buildspace
		in_buildsetup_space=0
		in_buildsetup_id=0
		buiid=0
		For Tbui.Tbui=Each Tbui
			If Tbui\mode=2 Then
				If Tbui\typ=typ Then
					buiid=Tbui\id
					in_buildsetup_id=buiid
					;Find Buildspace Stuff
					For Tbuis.Tbui=Each Tbui
						If Tbuis\mode=1 Then
							If Tbuis\id=buiid Then
								in_buildsetup_space=Tbuis\typ
								Exit
							EndIf
						EndIf
					Next
					Exit
				EndIf
			EndIf
		Next
		;Picking
		If in_buildsetup_space<>6 Then
			game_picking(0)
		EndIf
		If in_buildsetup_watersurface=1 Then
			EntityPickMode env_sea2,2,1
		Else
			EntityPickMode env_sea2,0
		EndIf
	EndIf
End Function


;### Buildsetup Update
Function game_buildsetup_update()
	If in_my>50 Then
		
		;Place is free?
		Local ok=1
		Select in_buildsetup_space
			Case 0,1,2,3,4,5
				EntityColor in_buildsetup_dummy,0,255,0
				For Tobject.Tobject=Each Tobject
					If Dobject_col(Tobject\typ)<>0 Then
						If EntityInView(Tobject\h,cam) Then
							If MeshesIntersect(Tobject\h,in_buildsetup_dummy) Then
								EntityColor in_buildsetup_dummy,255,0,0
								ok=0
								Exit
							EndIf
						EndIf
					EndIf
				Next
			Case 6
				EntityColor in_buildsetup_dummy,255,0,0
				ok=0
		End Select
		
		;Rotate by Mousewheel
		If in_mzs#<>0 Then
			h=in_buildsetup_dummy
			If in_mzs#>0 Then
				RotateEntity h,EntityPitch(h),EntityYaw(h)-(in_mzs#*10.),EntityRoll(h)
			Else
				RotateEntity h,EntityPitch(h),EntityYaw(h)-(in_mzs#*10.),EntityRoll(h)
			EndIf
		EndIf
			
		;1 - Position (on move)
		If Not in_mdown(1) Then
			If in_mrelease(1)=0 Then
				CameraPick(cam,in_mx,in_my)
				Local x2#=PickedX()
				Local z2#=PickedZ()
				PositionEntity in_buildsetup_dummy,x2#,e_tery(x2#,z2#),z2#
				;Assign to Object (Mode 6)
				If in_buildsetup_space=6
					If get_object(PickedEntity())<>-1 Then
						For Tbui.Tbui=Each Tbui
							If Tbui\mode=3 Then
								If Tbui\id=in_buildsetup_id Then
									If Tbui\typ=TCobject\typ Then
										;Position
										PositionEntity in_buildsetup_dummy,EntityX(TCobject\h),EntityY(TCobject\h),EntityZ(TCobject\h)
										;Ok
										EntityColor in_buildsetup_dummy,0,255,0
										ok=1
										Exit
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf
				;Special Positioning
				If in_buildsetup_typ>0 Then
					;Objects
					Select Dobject_align(in_buildsetup_typ)
						Case 0 ;-
						Case 1 PositionEntity in_buildsetup_dummy,x2#,1,z2#
						Case 2 RotateEntity in_buildsetup_dummy,groundpitch#(in_buildsetup_dummy,60),yaw#,0
					End Select
				Else
					;Units
					Select Dunit_align(Abs(in_buildsetup_typ))
						Case 0 ;-
						Case 1 PositionEntity in_buildsetup_dummy,x2#,1,z2#
					End Select
				EndIf
			EndIf
		;2 - Rotate (on down)
		Else
			If in_mrelease(1)=0 Then
				h=in_buildsetup_dummy
				in_cursorf=4
				RotateEntity h,EntityPitch(h),EntityYaw(h)-(in_mxs#*f#*0.5),EntityRoll(h)
				CameraProject cam,EntityX(h),EntityY(h),EntityZ(h)
				in_mx=ProjectedX():in_my=ProjectedY()
				If set_debug=1 Then drawaxes(h,20)
				If in_buildsetup_space=6 Then 
					EntityColor in_buildsetup_dummy,0,255,0
					ok=1
				EndIf
			EndIf
		EndIf
		;3 - Set (on release)
		If in_mrelease(1) Then
			If in_buildsetup_space=6 Then 
				EntityColor in_buildsetup_dummy,0,255,0
				ok=1
			EndIf
			If ok=1 Then
				If game_buildplace(EntityX(in_buildsetup_dummy),EntityZ(in_buildsetup_dummy),EntityYaw(in_buildsetup_dummy),in_buildsetup_typ) Then
					if_msg(sm$(175),Cbmpf_green)
					speech("buildingsite")
				EndIf
			Else
				sfx sfx_fail
				if_msg(sm$(176),Cbmpf_red)
				speech("negative")
			EndIf
			game_buildsetup_end()
		EndIf
		
		;Skip
		If in_mhit(2) Then
			game_buildsetup_end()
		EndIf
		
	EndIf
End Function


;### End Buildsetup
Function game_buildsetup_end()
	;Dummy Model
	FreeEntity in_buildsetup_dummy
	;Vars
	in_buildsetup_typ=0
	m_menu=0
	;Picking
	game_picking(1)
	EntityPickMode env_sea2,0
End Function


;### Create Buildplace
Function game_buildplace(x#,z#,yaw#,typ)
	;Event: on:build_start
	p_skipevent=0
	If typ>0 Then
		If Instr(Dobject_scriptk$(typ),"Śbuild_start") Then
			parse_task(0,0,"build_start","",Dobject_script$(typ))
			parse_sel_event("build_start")
		EndIf
	Else
		If Instr(Dunit_scriptk$(Abs(typ)),"Śbuild_start") Then
			parse_task(0,0,"build_start","",Dunit_script$(Abs(typ)))
			parse_sel_event("build_start")
		EndIf
	EndIf

	If p_skipevent=0 Then
		;Get Buildspace
		space=0
		buiid=0
		For Tbui.Tbui=Each Tbui
			If Tbui\mode=2 Then
				If Tbui\typ=typ Then
					buiid=Tbui\id
					;Find Buildspace Stuff
					For Tbuis.Tbui=Each Tbui
						If Tbuis\mode=1 Then
							If Tbuis\id=buiid Then
								space=Tbuis\typ
								Exit
							EndIf
						EndIf
					Next
					Exit
				EndIf
			EndIf
		Next
		;Check Buildspace
		Local space_objectid=0
		Select space
			;0 - Land
			Case 0
				If e_tery(x#,z#)<0. Then
					sfx sfx_fail
					if_msg(sm$(183),Cbmpf_red)
					speech("negative")
					Return 0
				EndIf
			;1 - Land and Water
			Case 1
				;Check Nothing! Huh!
			;2 - Water
			Case 2
				If e_tery(x#,z#)>0. Then
					sfx sfx_fail
					if_msg(sm$(184),Cbmpf_red)
					speech("negative")
					Return 0
				EndIf
			;3 - Shore
			Case 3
				If e_tery(x#,z#)<-3 And e_tery(x#,z#)>3 Then
					sfx sfx_fail
					if_msg(sm$(185),Cbmpf_red)
					speech("negative")
					Return 0
				EndIf
			;4 - Hill
			Case 4
				If e_tery(x#,z#)<100. Then
					sfx sfx_fail
					if_msg(sm$(186),Cbmpf_red)
					speech("negative")
					Return 0
				EndIf
			;5 - Shallow water
			Case 5
				If e_tery(x#,z#)<-3 And e_tery(x#,z#)>-0.1 Then
					sfx sfx_fail
					if_msg(sm$(187),Cbmpf_red)
					speech("negative")
					Return 0
				EndIf
			;6 - at object
			Case 6
				ok=0
				For Tobject.Tobject=Each Tobject
					If EntityX(Tobject\h)=x# Then
						If EntityZ(Tobject\h)=z# Then
							For Tbui.Tbui=Each Tbui
								If Tbui\mode=3 Then
									If Tbui\id=buiid Then
										;Type ID Okay?
										If Tbui\typ=Tobject\typ Then
											;Grown?
											If Tobject\daytimer>=0 Then
												space_objectid=Tobject\id
												;Okay
												ok=1
												Exit
											EndIf
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					EndIf
				Next
				If ok=0
					sfx sfx_fail
					if_msg(sm$(193),Cbmpf_red)
					speech("negative")
					Return 0	
				EndIf
		End Select
		;Set Buildplace Object
		Local custom=0
		For cbs.Tbui=Each Tbui
			If cbs\id=buiid Then
				If cbs\mode=4 Then
					custom=cbs\typ
				EndIf
			EndIf
		Next
		If custom=0 Then
			;Default Buildingsite
			If e_tery(x#,z#)<0. Then
				set_object(-1,set_buildplacewaterid,x#,z#)
			Else
				set_object(-1,set_buildplaceid,x#,z#)
			EndIf
		Else
			;Custom Buildingsite
			set_object(-1,custom,x#,z#)
		EndIf
		RotateEntity TCobject\h,0,yaw#,0
		pv_lastbuilding=TCobject\id
		Local tmpbuildplaceid=TCobject\id
		;Add Buildplace State in order to Cache Type of Building
		set_state(Cstate_buildplace,Cclass_object,tmpbuildplaceid)
		TCstate\value=typ
		;Add Link State to Building (destroy it if parent is destroyed)
		If space=6 Then
			set_state(Cstate_link,Cclass_object,space_objectid)
			TCstate\value=tmpbuildplaceid
			;DebugLog "linkstate at "+space_objectid+", value="+tmpbuildplaceid
		EndIf
		;Look at Building
		PointEntity cam,TCobject\h
		;Smoke
		Local y=e_tery(x#,z#)
		For i=1 To 10
			p_add(x#+Rnd(-10,10),y+Rand(2,5),z#+Rnd(-10,10),20,Rnd(5,10),Rnd(0.3,1.5))
		Next
		;Okay
		Return 1
	EndIf
End Function


;### Buildplace Focus Stuff
Function game_buildfocus(buildplaceid)
	con_object(buildplaceid)
	If get_state(Cstate_buildplace,Cclass_object,buildplaceid)
		typ=TCstate\value
		;Buildplace Name
		in_fo_x=set_scrx/2:in_fo_y=35
		If typ>0 Then
			bmpf_txt_c(in_fo_x,in_fo_y, ss$(sm$(177),Dobject_name$(typ)))
		Else
			bmpf_txt_c(in_fo_x,in_fo_y, ss$(sm$(177),Dunit_name$(Abs(typ))))
		EndIf
		;Get Building Type ID
		Local id
		For Tbui.Tbui=Each Tbui
			If Tbui\mode=2 Then
				If Tbui\typ=typ Then
					id=Tbui\id
					Exit
				EndIf
			EndIf
		Next
		;Scan Req. Stuff
		Local total=0
		Local complete=0
		Local totalc=0
		Local completec=0
		Local ok
		For Tbui.Tbui=Each Tbui
			If Tbui\id=id Then
				If Tbui\mode=0 Then
					total=total+1
					totalc=totalc+Tbui\count
					ok=0
					;Is Req. complete?
					ok=get_stored_item(Tbui\typ,Cclass_object,buildplaceid)
					If ok=1 Then
						completec=completec+TCitem\count
						If Tbui\count<=TCitem\count Then
							complete=complete+1		
							ok=1
						Else
							ok=0
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		;Draw Req. Stuff
		Local uncomplete=total-complete
		Local x,y,inline
		x=set_scrx/2-51
		y=55
		gui_healthbar#(x,y,completec,totalc)
		Local itemcount
		y=75
		If uncomplete>=8 Then
			inline=8
		Else
			inline=uncomplete
		EndIf
		x=set_scrx/2-((inline*45)/2)
		For Tbui.Tbui=Each Tbui
			If Tbui\id=id Then
				If Tbui\mode=0 Then
					ok=0
					itemcount=0
					;Is Req. complete?
					ok=get_stored_item(Tbui\typ,Cclass_object,buildplaceid)
					If ok=1 Then
						itemcount=TCitem\count
						If Tbui\count<=itemcount Then
							complete=complete+1		
							ok=1
						Else
							ok=0
						EndIf
					EndIf
					;Draw
					If ok=0 Then
						DrawBlock gfx_if_itemback,x,y
						DrawImage Ditem_iconh(Tbui\typ),x,y		
						gui_iconboxc(x,y,Tbui\count-itemcount)
						uncomplete=uncomplete-1
						x=x+45
						If x>=((set_scrx/2)+((8*45)/2))
							If uncomplete>=8 Then
								inline=8
							Else
								inline=uncomplete
							EndIf
							x=set_scrx/2-((inline*45)/2)
							y=y+45
						EndIf		
					EndIf
				EndIf
			EndIf
		Next
	EndIf
End Function
