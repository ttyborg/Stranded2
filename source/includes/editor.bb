;############################################ EDITOR

Function editor()
	
	;Unpaused?
	g_unpaused=1
	
	;Input
	getinput()
	in_edoverinfo$=""
	
	;Terrain Ground
	HideEntity env_ground
	e_terrainground(0)
	
	;Render
	If m_menu=0 Then
		;Renderstuff
		ct=MilliSecs()
		UpdateWorld(f#)
		;Debug Times
		If set_debug_rt Then
			set_debug_rt_updateworld$=(MilliSecs()-ct)+" ms Update"
		EndIf
		
		;Update
		cull()
		
		e_environment_update_water()
		set_debug_rt_render=MilliSecs()
		RenderWorld()
		set_debug_rt_render=MilliSecs()-set_debug_rt_render
		mb_update()
		e_environment_water2dfx()
		
		grass_spread()
		e_environment_update()
		update_state()
		draw_info()
	Else
		
		ClsColor 0,0,0
		Cls
	
	EndIf
	
	
	;### Move Cam
	If m_menu=0 Then
		Local camspeed#=(2.+in_accelerate#)*f#
		x=0
		If KeyDown(31) Then MoveEntity cam,0,0,-camspeed#:x=1	;Backwards
		If KeyDown(17) Then MoveEntity cam,0,0,camspeed#:x=1	;Forwards
		If KeyDown(30) Then MoveEntity cam,-camspeed#,0,0:x=1	;Left
		If KeyDown(32) Then MoveEntity cam,camspeed#,0,0:x=1	;Right
		If x Then
			in_accelerate#=in_accelerate#+(f#/0.5)
			If in_accelerate#>30. Then in_accelerate#=30
		Else
			in_accelerate#=0
		EndIf
	EndIf
	
	;### Mouse
	If in_mrelease(1) Then in_turnh=0
	If (in_mx>210 Or in_editor_sidebar=0 ) And m_menu=0 Then
		If in_mx>37 Or in_my<set_scry-37 Then
		
			;Rotate Cam
			If in_mdown(2)=1 Then
				in_cursorf=2
				If in_mxs<>0 Then RotateEntity cam,EntityPitch(cam),EntityYaw(cam)+in_mxs,EntityRoll(cam)
				If in_mys<>0 Then
					If in_mys>0 Then
						If EntityPitch(cam)+in_mys>70
							in_mys=70-EntityPitch(cam)
						EndIf
						RotateEntity cam,EntityPitch(cam)+in_mys,EntityYaw(cam),EntityRoll(cam)
					ElseIf in_mys<0 Then
						If EntityPitch(cam)+in_mys<-70
							in_mys=-70-EntityPitch(cam)
						EndIf
						RotateEntity cam,EntityPitch(cam)+in_mys,EntityYaw(cam),EntityRoll(cam)
					EndIf
				EndIf
			EndIf
			
			;Rotate Turn Handle Entity
			If in_turnh<>0 Then
				in_cursorf=4
				RotateEntity in_turnh,EntityPitch(in_turnh),EntityYaw(in_turnh)-(in_mxs#*f#),EntityRoll(in_turnh)
				CameraProject cam,EntityX(in_turnh),EntityY(in_turnh),EntityZ(in_turnh)
				MoveMouse ProjectedX(),ProjectedY()
				in_mx=MouseX():in_my=MouseY()
				drawaxes(in_turnh,20)
			EndIf
			
			;Pick?
			x=0
			;Pick on -> Click/Release!
			If in_mhit(1)=1 Or in_mrelease(1)=1 Then x=1
			;Pick on:
			If in_opt(1)=0 Then
				;If in_mzs#<>0.0 Then x=1
				;If in_mdown(1) Then x=1
			EndIf
			;Pick on -> Mousemove
			x=1
			;Pick on -> Mousewheel @ in Terrain Menu
			If in_opt(0)=0 Then
				If in_mzs#<>0 Then x=1
			EndIf
			;Perform Pick!
			If x=1 Then
				in_pe=0
				CameraPick(cam,in_mx,in_my)
				in_px#=PickedX()
				in_py#=PickedY()
				in_pz#=PickedZ()
				in_pe=PickedEntity()
			EndIf
			
		EndIf
	EndIf



	
	;### Menu (SIDEBAR)
	If in_editor_sidebar=1 Then
	
		DrawImage gfx_sidebar,0,0
		
		;Main Options (New,Load,Save,Settings,Quit)
		If gui_ibutton(5,5,Cicon_new,se$(1)+"...") Then editor_setmenu(Cmenu_ed_new)
		If gui_ibutton(5+37,5,Cicon_load,se$(2)+"...") Then editor_setmenu(Cmenu_ed_load)
		If gui_ibutton(5+37+37,5,Cicon_save,se$(3)+"...") Then editor_setmenu(Cmenu_ed_save)
		If gui_ibutton(5+37+37+37,5,Cicon_options,se$(4)+"...") Then editor_setmenu(Cmenu_ed_settings)
		If gui_ibutton(5+37+37+37+37,5,Cicon_x,se$(5)+"...") Then editor_setmenu(Cmenu_ed_quit)
		
		DrawImage gfx_border(1),-40,32+5+5
		DrawImage gfx_border(0),194,32+5+5
		
		;Sections (Terrain,Objects,Units,Items,Infos)
		If gui_opt(5,63,se$(6),0,0) Then editor_setmode(0)
		If gui_opt(5,63+20,se$(7),1,0) Then editor_setmode(Cclass_object)
		If gui_opt(5,63+20+20,se$(8),2,0) Then editor_setmode(Cclass_unit)
		If gui_opt(5,63+20+20+20,se$(9),3,0) Then editor_setmode(Cclass_item) 
		If gui_opt(5,63+20+20+20+20,se$(10),4,0) Then editor_setmode(Cclass_info)
		
		;List
		If gui_ibutton(5+37+37+37+37,63,Cicon_list,se$(201)) Then
			Select in_opt(0)
				Case 1 editor_setmenu(Cmenu_ed_list):in_opt(3)=1
				Case 2 editor_setmenu(Cmenu_ed_list):in_opt(3)=2
				Case 3 editor_setmenu(Cmenu_ed_list):in_opt(3)=3
				Case 4 editor_setmenu(Cmenu_ed_list):in_opt(3)=4
				Case 5 editor_setmenu(Cmenu_ed_list):in_opt(3)=5
				Default editor_setmenu(Cmenu_ed_list):in_opt(3)=1
			End Select
		EndIf
		
		;Map
		If gui_ibutton(5+37+37+37+37,63+37,Cicon_map,sm$(169)) Then
			editor_setmenu(Cmenu_ed_map)
		EndIf
		
		;Borders
		DrawImage gfx_border(1),-40,165
		DrawImage gfx_border(0),194,165
		
	EndIf
	
	Select in_opt(0)
		
		;### Terrain Menu
		Case 0
			
			If in_editor_sidebar=1 Then
			
				;Terrain Maps Options
				If gui_ibutton(5,186,Cicon_heightmap,se$(11)+"...") Then editor_setmenu(Cmenu_ed_heightmap)
				If gui_ibutton(5+37,186,Cicon_colormap,se$(12)+"...") Then editor_setmenu(Cmenu_ed_colormap)
				;gui_ibutton(5+37+37,186,Cicon_stuffmap,"Objectmap bearbeiten...")
				If gui_ibutton(5+37+37,186,Cicon_grassmap,se$(13)+"...") Then editor_setmenu(Cmenu_ed_grassmap) 
				If gui_ibutton(5+37+37+37,186,Cicon_all,se$(216)+"...") Then editor_setmenu(Cmenu_ed_random)
				
				
				;Terrain Options
				If gui_ibutton(5,186+37,Cicon_up,se$(14),m_menu=0) Then e_terraincompleteh(0.01)
				If gui_ibutton(5+37,186+37,Cicon_down,se$(15),m_menu=0) Then e_terraincompleteh(-0.01)
				If gui_ibutton(5+37+37,186+37,Cicon_smooth,se$(16),m_menu=0) Then e_terrainsmooth()
				If gui_ibutton(5+37+37+37,186+37,Cicon_distort,se$(17),m_menu=0) Then e_terraindistort(0.05)
				If gui_ibutton(5+37+37+37+37,186+37,Cicon_settoterrain,se$(18),m_menu=0) Then editor_settoterrain()
				
				;Terrain Border Options
				If gui_ibutton(5,186+37+37,Cicon_borderup,se$(19),m_menu=0) Then e_terrainborder(-1,0.01)
				If gui_ibutton(5+37,186+37+37,Cicon_borderdown,se$(20),m_menu=0) Then e_terrainborder(-1,-0.01)
				If gui_ibutton(5+37+37,186+37+37,Cicon_border,se$(21),m_menu=0) Then e_terrainborder(TerrainHeight(ter,0,0))
				
				;Terrain Instant Editing Brush
				Color 56,34,0
				size=10+(in_edbrush)
				Oval 100-1-size/2,380-1-size/2, size+2,size+2,1
				Color 0,0,0
				Oval 100-size/2,380-size/2, size,size,1
				over=0
				If in_mx>5 Then
					If in_mx<200 Then
						If in_my>300 Then
							If in_my<460 Then
								over=1
								If in_mdown(1) Then
									in_edbrush=in_edbrush+1
									If in_edbrush>99 Then in_edbrush=99
								ElseIf in_mdown(2) Then
									in_edbrush=in_edbrush-1
									If in_edbrush<0 Then in_edbrush=0
								EndIf
								If in_mzs#<>0 Then
									in_edbrush=in_edbrush+in_mzs#
									If in_edbrush>99 Then in_edbrush=99
									If in_edbrush<0 Then in_edbrush=0
									in_mzs#=0
								EndIf
								gui_tt(in_mx,in_my,se$(22))
							EndIf
						EndIf
					EndIf
				EndIf
				bmpf_txt(5,300,se$(23)+": "+(in_edbrush+1),over)
				
				;Terrain Tool
				If gui_ibutton(5,450,Cicon_updown,se$(24),1,in_edtertool=0) Then in_edtertool=0
				If gui_ibutton(5+37,450,Cicon_flatten,se$(25),1,in_edtertool=1) Then in_edtertool=1
				If gui_ibutton(5+37+37,450,Cicon_smooth,se$(26),1,in_edtertool=2) Then in_edtertool=2
				If gui_ibutton(5+37+37+37,450,Cicon_distort,se$(27),1,in_edtertool=3) Then in_edtertool=3
				
				;Terrain Color
				If gui_colorbox(in_col(0),in_col(1),in_col(2),5,500,90,32,se$(124)) Then
					in_slider(0)=Float(in_col(0))/2.55
					in_slider(1)=Float(in_col(1))/2.55
					in_slider(2)=Float(in_col(2))/2.55
					If gui_win_color() Then
						in_col(0)=in_win_color(0):in_col(1)=in_win_color(1):in_col(2)=in_win_color(2)
					EndIf
				EndIf
				
				;Terrain Coloring Mode
				If in_edterbtool=0 Then
					If gui_ibutton(100,500,Cicon_plotbrush,se$(199)) Then
						in_edterbtool=1
					EndIf
				Else
					If gui_ibutton(100,500,Cicon_linebrush,se$(200)) Then
						in_edterbtool=0
					EndIf
				EndIf
				
			EndIf
			
			
			;Transform
			If in_pe=ter Then
				;Calc
				x=(in_px#+(ter_size/2*Cworld_size))/Cworld_size
				z=(in_pz#+(ter_size/2*Cworld_size))/Cworld_size
				;Brush 3D Visualization
				;in_edbrushangle#=(in_edbrushangle#+5.*f) Mod 360
				;visx#=in_px#+(Sin(in_edbrushangle#)*(Cworld_size*(in_edbrush+1)))
				;visz#=in_pz#+(Cos(in_edbrushangle#)*(Cworld_size*(in_edbrush+1)))
				;visy#=TerrainY(ter,visx#,0,visz#)
				;p_add(visx#,visy#,visz#,Cp_risingflare,30,0.5)
				;EntityColor TCp\h,0,255,0
				;EntityOrder TCp\h,-1
				;Brush 2D Visualization
				Color 0,Abs(255.*Cos(in_ca#)),0
				For i=(10.*(in_ca#/360.)) To 360 Step 10
					visx1#=in_px#+(Sin(i)*(Cworld_size*(in_edbrush+1)))
					visz1#=in_pz#+(Cos(i)*(Cworld_size*(in_edbrush+1)))
					visy1#=e_tery(visx1#,visz1#)
					visx2#=in_px#+(Sin(i+5)*(Cworld_size*(in_edbrush+1)))
					visz2#=in_pz#+(Cos(i+5)*(Cworld_size*(in_edbrush+1)))
					visy2#=e_tery(visx2#,visz2#)
					CameraProject(cam,visx1#,visy1#,visz1#)
					lx1=ProjectedX()
					If lx1>210 Or in_editor_sidebar=0 Then
						ly1=ProjectedY()
						CameraProject(cam,visx2#,visy2#,visz2#)
						lx2=ProjectedX()
						If lx2>210 Or in_editor_sidebar=0 Then
							ly2=ProjectedY()
							Line lx1,ly1,lx2,ly2
						EndIf
					EndIf
				Next
				
				;2D Paint Brush Visualization
				pbvf=(Cworld_size*ter_size)/TextureHeight(ter_tex_color)
				editor_range(in_px#,in_py#,in_pz#,(in_edbrush)*pbvf)
				
				
				;Action HEIGHT
				If in_mzs#<>0 Then
					Select in_edtertool
						Case 0 e_terraintrans(x,z,0.01*in_mzs#,in_edbrush+1)
						Case 1 e_terrainflatten(x,z,in_edbrush+1)
						Case 2 e_terrainsmoothp(x,z,in_edbrush+1)
						Case 3 e_terraindistortp(x,z,0.01*in_mzs#,in_edbrush+1)
					End Select
				EndIf
				If KeyDown(27) Or KeyDown(78) Then
					If in_t50go Then e_terraintrans(x,z,0.01*f#,in_edbrush+1)
				EndIf
				If KeyDown(53) Or KeyDown(74) Then
					If in_t50go Then e_terraintrans(x,z,-0.01*f#,in_edbrush+1)
				EndIf
				
				;Action Paint
				If in_edterbtool=0 Then
					If in_mhit(1) Then
						editor_paintcm(in_px#,in_pz#,in_edbrush+2,in_col(0),in_col(1),in_col(2))
					EndIf
				Else
					If in_mdown(1) Then
						editor_paintcm(in_px#,in_pz#,in_edbrush+2,in_col(0),in_col(1),in_col(2))
					EndIf
				EndIf
				
				;Delete
				If KeyHit(211) Then
					;Delete Objects
					For Tobject.Tobject=Each Tobject
						If dist(in_px#,in_pz#,EntityX(Tobject\h),EntityZ(Tobject\h))<(Cworld_size*(in_edbrush+1)) Then
							free_object(Tobject\id)
						EndIf
					Next
					;Delete Units
					For Tunit.Tunit=Each Tunit
						If dist(in_px#,in_pz#,EntityX(Tunit\h),EntityZ(Tunit\h))<(Cworld_size*(in_edbrush+1)) Then
							free_unit(Tunit\id)
						EndIf
					Next
					;Delete Items
					For Titem.Titem=Each Titem
						If Titem\parent_class=0 Then
							If dist(in_px#,in_pz#,EntityX(Titem\h),EntityZ(Titem\h))<(Cworld_size*(in_edbrush+1)) Then
								free_item(Titem\id)
							EndIf
						EndIf
					Next
					;Delete Infos
					For Tinfo.Tinfo=Each Tinfo
						If dist(in_px#,in_pz#,EntityX(Tinfo\h),EntityZ(Tinfo\h))<(Cworld_size*(in_edbrush+1)) Then
							free_info(Tinfo\id)
						EndIf
					Next
				EndIf
				
			EndIf
			
		;### Object Menu
		Case 1
			
			If object_count_e=0 Then RuntimeError("No editor objects defined!")
			
			;Group Selection
			If gui_groupsel(5,190,Cclass_object) Then
			
				;List
				x=0:y=0:j=0
				editor_list(Cclass_object,ed_groupg(Cclass_object))
				For Tgroupl.Tgroupl=Each Tgroupl
					i=Tgroupl\id
					If in_object_sel=0 Then in_object_sel=i
					j=j+1
					If j>in_object_scr Then
						If in_editor_sidebar=1 Then
							DrawBlock gfx_if_itemback,5+x,223+y
							If gui_iconbox(5+x,223+y,Dobject_iconh(i),Dobject_name$(i)+" ("+i+")",i=in_object_sel) Then
								;p_pixelizeeat(5+x,223+y,Dobject_iconh(i))
								in_object_sel=i
								editor_setdummy(Cclass_object,i)
								in_frapdown=0
								in_fraph=0
							EndIf
						EndIf
						x=x+45
						If x>=180 Then
							x=0
							y=y+45
							If y>=315 Then Exit
						EndIf
					EndIf
				Next
			
				If in_editor_sidebar=1 Then
				
					;Scroll
					If gui_ibutton(5+37+37+37+37,186,Cicon_up,sm$(29),in_object_scr>0) Then
						in_object_scr=in_object_scr-4
						If in_object_scr<0 Then in_object_scr=0
					EndIf
					DrawBlock gfx_scrollspace,194,223
					in_object_scr=gui_scrollbar(158+16,228,528-228,Ceil(ed_current_c/4.),in_object_scr/4,28/4)*4
					If gui_ibutton(5+37+37+37+37,538,Cicon_down,sm$(30),tmp_slideend) Then
						in_object_scr=in_object_scr+4
					EndIf
					
					;Random Rotation
					If gui_ibutton(5,538,Cicon_update,se$(28),1,in_edrandrot) Then
						in_edrandrot=1-in_edrandrot
					EndIf
					
					;Setscript
					If gui_ibutton(5+37,538,Cicon_script,se$(226)) Then
						gui_tb_loadstring(ed_setscript$,"Ś")
						in_input(3)=-1
						editor_setmenu(Cmenu_ed_scripts)
					EndIf					
					
				EndIf
				
			EndIf	
			
			;Normal
			If in_fraph=0 Then
			
				;Set/Select
				If in_mhit(1) And m_menu=0 Then
					shift=KeyDown(42)+KeyDown(54)
					If in_pe=ter Or shift>0 Then
						serialpeak_object()
						If shift Then
							If ha_getclass(in_pe,0) Then
								If parent_pos(tmp_class,tmp_id) Then
									set_object(-1,in_object_sel,tmp_x#,tmp_z#,in_edrot)	
									If EntityY(TCobject\h)<>tmp_y# Then
										ha_setpos(TCobject\h,Cclass_object,TCobject\id,tmp_x#,tmp_y#,tmp_z#)
									EndIf
								EndIf
							Else
								set_object(-1,in_object_sel,in_px#,in_pz#,in_edrot)	
							EndIf
						Else
							set_object(-1,in_object_sel,in_px#,in_pz#,in_edrot)
						EndIf
						in_turnh=TCobject\h
						If in_edrandrot Then in_edrot#=Rand(15)*22.5
						editor_setfx(in_turnh)
						parse_editorsetscript(Cclass_object,TCobject\id)
					Else
						x=get_object(in_pe)
						If x>-1 Then
							editor_setmenu(Cmenu_ed_objects,x)
						EndIf
					EndIf
				EndIf
				
				;Draw Info Text
				If in_pe<>ter And in_turnh=0 Then
					If get_object(in_pe)>-1 Then draw_object()
				EndIf
				KeyHit(211)
				KeyHit(57)
			
			;FRAP
			Else

				;Run FRAP Function or disable FRAP
				x=get_object(in_fraph)
				If x>-1 Then
					frap_object()
				Else
					in_fraph=0
				EndIf
				
			EndIf
			
			
		;### Unit Menu
		Case 2
			
			If unit_count_e=0 Then RuntimeError("No editor units defined!")
					
			;Group Selection
			If gui_groupsel(5,190,Cclass_unit)=1 Then
			
				;List
				x=0:y=0:j=0
				editor_list(Cclass_unit,ed_groupg(Cclass_unit))
				For Tgroupl.Tgroupl=Each Tgroupl
					i=Tgroupl\id
					If in_unit_sel=0 Then in_unit_sel=i
					j=j+1
					If j>in_unit_scr Then
						If in_editor_sidebar=1 Then
							DrawBlock gfx_if_itemback,5+x,223+y
							If gui_iconbox(5+x,223+y,Dunit_iconh(i),Dunit_name$(i)+" ("+i+")",i=in_unit_sel) Then
								;p_pixelizeeat(5+x,223+y,Dunit_iconh(i))
								in_unit_sel=i
								editor_setdummy(Cclass_unit,i)
								in_frapdown=0
								in_fraph=0
							EndIf
						EndIf
						x=x+45
						If x>=180 Then
							x=0
							y=y+45
							If y>=315 Then Exit
						EndIf
					EndIf
				Next
				
				
				If in_editor_sidebar=1 Then
				
					;Scroll
					If gui_ibutton(5+37+37+37+37,186,Cicon_up,sm$(29),in_unit_scr>0) Then
						in_unit_scr=in_unit_scr-4
						If in_unit_scr<0 Then in_unit_scr=0
					EndIf
					DrawBlock gfx_scrollspace,194,223
					in_unit_scr=gui_scrollbar(158+16,228,528-228,ed_current_c/4,in_unit_scr/4,28/4)*4
					If gui_ibutton(5+37+37+37+37,538,Cicon_down,sm$(30),tmp_slideend) Then
						in_unit_scr=in_unit_scr+4
					EndIf
					
					;Random Rotation
					If gui_ibutton(5,538,Cicon_update,se$(28),1,in_edrandrot) Then
						in_edrandrot=1-in_edrandrot
					EndIf
					
					;Setscript
					If gui_ibutton(5+37,538,Cicon_script,se$(226)) Then
						gui_tb_loadstring(ed_setscript$,"Ś")
						in_input(3)=-1
						editor_setmenu(Cmenu_ed_scripts)
					EndIf	
					
				EndIf
				
			EndIf
			
			;Normal
			If in_fraph=0 Then
			
				;Set/Select
				If in_mhit(1) And m_menu=0 Then
					shift=KeyDown(42)+KeyDown(54)
					If in_pe=ter Or shift>0 Then
						serialpeak_unit()
						set_unit(-1,in_unit_sel,in_px#,in_pz#)
						RotateEntity TCunit\h,0,in_edrot,0
						in_turnh=TCunit\h
						If in_edrandrot Then in_edrot#=Rand(15)*22.5
						editor_setfx(in_turnh)
						parse_editorsetscript(Cclass_unit,TCunit\id)
					Else
						x=get_unit(in_pe)
						If x>-1 Then
							editor_setmenu(Cmenu_ed_units,x)
						EndIf
					EndIf
				EndIf
				
				;Draw Info Text
				If in_pe<>ter And in_turnh=0 Then
					If get_unit(in_pe)>-1 Then draw_unit()
				EndIf
				KeyHit(211)
				KeyHit(57)
			
			;FRAP
			Else

				;Run FRAP Function or disable FRAP
				x=get_unit(in_fraph,1)
				If x>-1 Then
					frap_unit()
				Else
					in_fraph=0
				EndIf
				
			EndIf
			
			
		;### Item Menu
		Case 3
			
			If item_count_e=0 Then RuntimeError("No editor items defined!")
						
			;Group Selection
			If gui_groupsel(5,190,Cclass_item)=1 Then
				
				;List
				x=0:y=0:j=0
				editor_list(Cclass_item,ed_groupg(Cclass_item))
				For Tgroupl.Tgroupl=Each Tgroupl
					i=Tgroupl\id
					If in_item_sel=0 Then in_item_sel=i
					j=j+1
					If j>in_item_scr Then
						If in_editor_sidebar=1 Then
							DrawBlock gfx_if_itemback,5+x,223+y
							If gui_iconbox(5+x,223+y,Ditem_iconh(i),Ditem_name$(i)+" ("+i+")",i=in_item_sel) Then
								;p_pixelizeeat(5+x,223+y,Ditem_iconh(i))
								in_item_sel=i
								editor_setdummy(Cclass_item,i)
								in_frapdown=0
								in_fraph=0
							EndIf
						EndIf
						x=x+45
						If x>=180 Then
							x=0
							y=y+45
							If y>=315 Then Exit
						EndIf
					EndIf
				Next
				
				If in_editor_sidebar=1 Then
				
					;Scroll
					If gui_ibutton(5+37+37+37+37,186,Cicon_up,sm$(29),in_item_scr>0) Then
						in_item_scr=in_item_scr-4
						If in_item_scr<0 Then in_item_scr=0
					EndIf
					DrawBlock gfx_scrollspace,194,223
					in_item_scr=gui_scrollbar(158+16,228,528-228,ed_current_c/4,in_item_scr/4,28/4)*4
					If gui_ibutton(5+37+37+37+37,538,Cicon_down,sm$(30),tmp_slideend) Then
						in_item_scr=in_item_scr+4
					EndIf
					
					;Random Rotation
					If gui_ibutton(5,538,Cicon_update,se$(28),1,in_edrandrot) Then
						in_edrandrot=1-in_edrandrot
					EndIf
					
					;Setscript
					If gui_ibutton(5+37,538,Cicon_script,se$(226)) Then
						gui_tb_loadstring(ed_setscript$,"Ś")
						in_input(3)=-1
						editor_setmenu(Cmenu_ed_scripts)
					EndIf	
					
				EndIf
				
			EndIf
			
			;Normal
			If in_fraph=0 Then
			
				;Set/Select
				If in_mhit(1) And m_menu=0 Then
					shift=KeyDown(42)+KeyDown(54)
					If in_pe=ter Or shift>0 Then
						serialpeak_item()
						set_item(-1,in_item_sel,in_px#,in_py#,in_pz#)
						RotateEntity TCitem\h,0,in_edrot,0
						in_turnh=TCitem\h
						If in_edrandrot Then in_edrot#=Rand(15)*22.5
						editor_setfx(in_turnh)
						parse_editorsetscript(Cclass_item,TCitem\id)
					Else
						x=get_item(in_pe)
						If x>-1 Then
							editor_setmenu(Cmenu_ed_items,x)
						EndIf
					EndIf
				EndIf
				
				;Draw Info Text
				If in_pe<>ter And in_turnh=0 Then
					If get_item(in_pe)>-1 Then draw_item()
				EndIf
				KeyHit(211)
				KeyHit(57)
			
			;FRAP
			Else

				;Run FRAP Function or disable FRAP
				x=get_item(in_fraph)
				If x>-1 Then
					frap_item()
				Else
					in_fraph=0
				EndIf
				
			EndIf		
		
		;### Info Menu
		Case 4
						
			;Group Selection
			If gui_groupsel(5,190,Cclass_info)=1 Then
								
				;List
				x=0:y=0:j=0
				editor_list(Cclass_info,ed_groupg(Cclass_info))
				For Tgroupl.Tgroupl=Each Tgroupl
					i=Tgroupl\id
					If in_info_sel=0 Then in_info_sel=i
					j=j+1
					If j>in_info_scr Then
						If in_editor_sidebar=1 Then
							DrawBlock gfx_if_itemback,5+x,223+y
							If gui_iconbox(5+x,223+y,0,Dinfo_name$(i)+" ("+i+")",i=in_info_sel) Then
								in_info_sel=i
								editor_setdummy(Cclass_info,i)
								in_frapdown=0
								in_fraph=0
							EndIf
							DrawImage gfx_icons,5+x+4,223+y+4,(Dinfo_frame(i))
						EndIf
						x=x+45
						If x>=180 Then
							x=0
							y=y+45
							If y>=315 Then Exit
						EndIf
					EndIf
				Next
				
				If in_editor_sidebar=1 Then
				
					;Scroll
					If gui_ibutton(5+37+37+37+37,186,Cicon_up,sm$(29),in_info_scr>0) Then
						in_info_scr=in_info_scr-4
						If in_info_scr<0 Then in_info_scr=0
					EndIf
					DrawBlock gfx_scrollspace,194,223
					in_info_scr=gui_scrollbar(158+16,228,528-228,ed_current_c/4,in_info_scr/4,28/4)*4
					If gui_ibutton(5+37+37+37+37,538,Cicon_down,sm$(30),tmp_slideend) Then
						in_info_scr=in_info_scr+4
					EndIf
					
					;Set at Cam
					If gui_ibutton(5,538,Cicon_cam,se$(29),1) Then
						serialpeak_info()
						set_info(-1,in_info_sel,EntityX(cam),EntityY(cam),EntityZ(cam))
						RotateEntity TCinfo\h,EntityPitch(cam),EntityYaw(cam),0
						parse_editorsetscript(Cclass_info,TCinfo\id)
					EndIf
					
					;Setscript
					If gui_ibutton(5+37,538,Cicon_script,se$(226)) Then
						gui_tb_loadstring(ed_setscript$,"Ś")
						in_input(3)=-1
						editor_setmenu(Cmenu_ed_scripts)
					EndIf	
					
				EndIf
				
			EndIf
			
			
			;Normal
			If in_fraph=0 Then
			
				;Set/Select
				If in_mhit(1) And m_menu=0 Then
					shift=KeyDown(42)+KeyDown(54)
					If in_pe=ter Or shift>0 Then
						serialpeak_info()
						set_info(-1,in_info_sel,in_px#,in_py#,in_pz#)
						in_turnh=TCinfo\h
						editor_setfx(in_turnh)
						parse_editorsetscript(Cclass_info,TCinfo\id)
					Else
						x=get_info(in_pe)
						If x>-1 Then
							editor_setmenu(Cmenu_ed_infos,x)
						EndIf
					EndIf
				EndIf
				
				;Info
				If in_pe<>ter And in_turnh=0 Then
					If get_info(in_pe)>-1 Then
						in_edoverinfo$=ss$(se$(197),Dinfo_name$(TCinfo\typ))
						If KeyHit(57) Then in_fraph=TCinfo\h
						If KeyHit(211) Then free_info(TCinfo\id)
					EndIf
				EndIf
				KeyHit(211)
				KeyHit(57)
				
			;FRAP
			Else

				;Run FRAP Function or disable FRAP
				x=get_info(in_fraph)
				If x>-1 Then
					frap_info()
				Else
					in_fraph=0
				EndIf
				
			EndIf	
		
		
		;### State Menu
		Case 5
		
			;List
			x=0:y=0:j=0
			For i=0 To Cstate_count
				If Dstate_name$(i)<>"" Then
					If in_state_sel=0 Then in_state_sel=i
					j=j+1
					If j>in_state_scr Then
						If in_editor_sidebar=1 Then
							DrawBlock gfx_if_itemback,5+x,223+y
							If gui_iconbox(5+x,223+y,0,Dstate_name$(i)+" ("+i+")",i=in_state_sel) Then
								in_state_sel=i
							EndIf
							DrawImage gfx_states,5+x+10,223+y+10,(Dstate_frame(i))
							DrawImage gfx_state,5+x+10,223+y+10,in_statefxf
						EndIf
						x=x+45
						If x>=180 Then
							x=0
							y=y+45
							If y>=315 Then Exit
						EndIf
					EndIf
				EndIf
			Next
			
			If in_editor_sidebar=1 Then
			
				;Scroll
				If gui_ibutton(5+37+37+37+37,186,Cicon_up,sm$(29),in_state_scr>0) Then
					in_state_scr=in_state_scr-4
					If in_state_scr<0 Then in_state_scr=0
				EndIf
				If gui_ibutton(5+37+37+37+37,538,Cicon_down,sm$(30),y>=315) Then
					in_state_scr=in_state_scr+4
				EndIf
				DrawBlock gfx_scrollspace,194,223
				If m_menu=0 Then
					in_state_scr=gui_scrollbar(158+16,228,528-228,state_count/4,in_state_scr/4,28/4)*4
				EndIf
				
			EndIf
			
		
	End Select
	
	
	;Sidebar Show/Hide
	If in_editor_sidebar=1 Then	
		;Hide Sidebar
		;If gui_ibutton(5+73,set_scry-37,Cicon_dec,se$(217)) Then
		If gui_txtb(5,set_scry-18,se$(217)) Then
			in_editor_sidebar=0
		EndIf
	Else
		;Show Sidebar
		If gui_ibutton(5,set_scry-37,Cicon_inc,se$(218)) Then
			in_editor_sidebar=1
		EndIf
	EndIf
		
		
	;Update Dummy
	If m_menu=0 Then
		If in_opt(0)>0 And in_opt(0)<5 Then
			If in_editor_dummy<>0 Then
				RotateEntity in_editor_dummy,0,in_edrot,0
				If in_mzs#<>0 Then
					If in_mzs#>0 Then
						in_edrot#=(in_edrot#+22.5) Mod 360
					Else
						in_edrot#=(in_edrot#-22.5) Mod 360
					EndIf
				EndIf
				;If in_t100go Then
					If in_pe=ter And in_turnh=0 Then
						ShowEntity in_editor_dummy
						PositionEntity in_editor_dummy,in_px#,e_tery(in_px#,in_pz#),in_pz#
						;Special Positioning / Hide in FRAP Mode
						;Objects
						If in_opt(0)=1 Then
							Select Dobject_align(in_object_sel)
								Case 0 ;-
								Case 1 If EntityY(in_editor_dummy)<1 Then PositionEntity in_editor_dummy,in_px#,1,in_pz#
								Case 2 RotateEntity in_editor_dummy,groundpitch#(in_editor_dummy,60),in_edrot,0
							End Select
							If in_fraph<>0 Then HideEntity in_editor_dummy
						;Units
						ElseIf in_opt(0)=2 Then
							Select Dunit_align(in_unit_sel)
								Case 0 ;-
								Case 1 If EntityY(in_editor_dummy)<1 PositionEntity in_editor_dummy,in_px#,1,in_pz#
							End Select
							If in_fraph<>0 Then HideEntity in_editor_dummy
						;Items
						ElseIf in_opt(0)=3 Then
							If in_fraph<>0 Then HideEntity in_editor_dummy
						;Infos
						ElseIf in_opt(0)=4 Then
							If in_fraph<>0 Then HideEntity in_editor_dummy
						EndIf
					Else
						HideEntity in_editor_dummy
					EndIf
				;EndIf
			EndIf
		EndIf
	EndIf
	
	
	;Cam Infos
	If m_menu=0 Then
		;Over Info
		If in_edoverinfo$<>"" Then
			bmpf_txt(212,set_scry-17-12,in_edoverinfo$,Cbmpf_tiny)
		EndIf
	
		;Cam Info
		bmpf_txt(212,set_scry-17,"Camera Position (X: "+Int(EntityX(cam))+" | Y: "+Int(EntityY(cam))+" | Z: "+Int(EntityZ(cam))+" ) Angle (Yaw: "+Int(EntityYaw(cam))+" )",Cbmpf_tiny)
	EndIf
	
			
	;### Menu (WINDOW)
	editor_menus()

	;Particles
	p_update()

	;Endinput
	endinput()
	
	
	;### Hotkeys
	
	;F12 - Checkmap
	If KeyHit(88) Then
		editor_testmap()
	EndIf
		
End Function
