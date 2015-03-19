;############################################ Handle Objects


;### Set Object
Function set_object(id=-1,typ,x#,z#,yaw#=0)
	TCobject.Tobject=Null

	;Invalid typ? Cancel
	If Len(Dobject_name$(typ))=0 Then Return -1
	
	;Add at End
	If id=-1 Then
		object_serial=object_serial+1
		id=object_serial
	EndIf
	
	;Check Model
	If Dobject_modelh(typ)=0 Then
		load_object_model(typ)
	EndIf
	
	;Create
	Tobject.Tobject=New Tobject
	Tobject\id=id
	Tobject\typ=typ
	Local h=CopyEntity(Dobject_modelh(typ))
	Tobject\h=h
	
	;Adjust
	ScaleEntity h,Dobject_size#(typ,0),Dobject_size#(typ,1),Dobject_size#(typ,2)
	EntityColor h,Dobject_color(typ,0),Dobject_color(typ,1),Dobject_color(typ,2)
	If Dobject_fx(typ)<>0 Then EntityFX h,Dobject_fx(typ)
	range=Float(Float(Dobject_autofade(typ))*set_viewfac#)
	If Dobject_autofade(typ)<>0 Then EntityAutoFade h,range,range+300
	EntityAlpha h,Dobject_alpha#(typ)
	Tobject\alpha#=Dobject_alpha#(typ)
	If Dobject_shininess#(typ)>0.0 EntityShininess h,Dobject_shininess#(typ)
	Tobject\windsway#=Rand(0,360)
	
	;Detail Texture
	If set_terrain>1 Then
		If Dobject_detailtex$(typ)<>0 Then
			If Dobject_detailtex$(typ)="1" Then
				EntityTexture Tobject\h,gfx_struc,0,2
			Else
				Local detailtex=load_res(Dobject_detailtex$(typ),Cres_texture,256+512)
				If detailtex<>0 Then
					TextureBlend detailtex,5
					EntityTexture Tobject\h,detailtex,0,2
				EndIf
			EndIf
		EndIf
	EndIf
	
	;Values
	Tobject\health#=Dobject_health#(typ)
	Tobject\health_max#=Dobject_health#(typ)
	
	;Rotate
	RotateEntity h,0,yaw#,0
	
	;Position
	PositionEntity h,x#,e_tery(x#,z#),z#
	Select Dobject_align(typ)
		Case 0 ;-
		Case 1 If EntityY(h)<1 Then PositionEntity h,x#,1,z#
		Case 2 RotateEntity h,groundpitch#(h,60),yaw#,0
	End Select
	
	;Pick
	If m_section=Csection_editor Then
		EntityPickMode h,2,1
	Else
		If Dobject_col(typ)>0 Then
			EntityPickMode h,2,1
		EndIf
	EndIf
	
	;Collision and Collision Handle
	If m_section<>Csection_editor Then
		;1 / 4
		If Dobject_col(typ)=1 Or Dobject_col(typ)=4 Then
			If Dobject_swayspeed#(typ)<>0. Then
				Tobject\ch=CopyEntity(Dobject_modelh(typ))
				ScaleEntity Tobject\ch,Dobject_size#(typ,0),Dobject_size#(typ,1),Dobject_size#(typ,2)
				EntityAlpha Tobject\ch,0.0
				EntityType Tobject\ch,Cworld_col
				PositionEntity Tobject\ch,EntityX(h),EntityY(h),EntityZ(h)
				RotateEntity Tobject\ch,EntityPitch(h),EntityYaw(h),EntityRoll(h)
			Else
				EntityType h,Cworld_col
			EndIf
		;3
		ElseIf Dobject_col(typ)=3 Then
			Tobject\ch=CopyEntity(Dobject_modelh(typ))
			ScaleEntity Tobject\ch,Dobject_size#(typ,0),Dobject_size#(typ,1),Dobject_size#(typ,2)
			EntityAlpha Tobject\ch,0.0
			EntityType Tobject\ch,Cworld_col
			PositionEntity Tobject\ch,EntityX(h),EntityY(h),EntityZ(h)
			RotateEntity Tobject\ch,EntityPitch(h),EntityYaw(h),EntityRoll(h)
			If set_debug=1 Then
				;EntityAlpha Tobject\ch,0.5
			EndIf
		EndIf
	EndIf
	
	;Event: on:create
	If tmp_loading=0 Then
		set_parsecache(Cclass_object,Tobject\id,"create")
		If Instr(Dobject_scriptk$(Tobject\typ),"Ścreate") Then
			parse_task(Cclass_object,Tobject\id,"create","",Dobject_script(Tobject\typ))
		EndIf
	EndIf
	
	;Defvars
	If tmp_loading=0 Then defvar_oncreate(Cclass_object,id,typ)
	
	;Okay
	TCobject.Tobject=Tobject
End Function


;### Get Object ID by Handle
Function get_object(searchhandle)
	If searchhandle=0 Then Return -1
	For Tobject.Tobject=Each Tobject
		If Tobject\h=searchhandle Then
			TCobject.Tobject=Tobject
			Return Tobject\id
		ElseIf Tobject\ch=searchhandle Then
			TCobject.Tobject=Tobject
			Return Tobject\id
		EndIf
	Next
	Return -1
End Function


;### Set Object Container
Function con_object(id)
	For Tobject.Tobject=Each Tobject
		If Tobject\id=id Then TCobject.Tobject=Tobject:Return 1
	Next
	Return 0
End Function


;### Alpha Object
Function alpha_object(id,alpha#)
	For Tobject.Tobject=Each Tobject
		If Tobject\id=id Then Tobject\alpha#=alpha#:Return 1
	Next
	Return 0	
End Function


;### Draw Object
Function draw_object()
	Tobject.Tobject=TCobject
	If EntityInView(Tobject\h,cam) Then
		;Name
		CameraProject(cam,EntityX(Tobject\h),EntityY(Tobject\h),EntityZ(Tobject\h))
		bmpf_txt_c(ProjectedX(),ProjectedY()-30,"#"+Tobject\id+" "+Dobject_name$(Tobject\typ),Cbmpf_tiny)
		;Free Rotation and Position
		If KeyHit(57) Then
			found=0
			For Tfrap.Tfrap=Each Tfrap
				If Tfrap\parent_class=Cclass_object Then
					If Tfrap\parent_id=Tobject\id Then
						found=1
						Exit
					EndIf
				EndIf
			Next
			;New FRAP
			If found=0 Then
				Tfrap.Tfrap=New Tfrap
				Tfrap\parent_class=Cclass_object
				Tfrap\parent_id=Tobject\id
				Tfrap\y#=EntityY(Tobject\h)
				Tfrap\pitch#=0.
				Tfrap\roll#=0.
				in_fraph=Tobject\h
			;Existing FRAP
			Else
				in_fraph=Tobject\h
			EndIf
		EndIf
		;Info
		in_edoverinfo$=ss$(se$(197),Dobject_name$(Tobject\typ))
		If KeyHit(211) Then free_object(Tobject\id)
	EndIf
End Function


;### Use Object
Function use_object(id)
	For Tobject.Tobject=Each Tobject
		If Tobject\id=id Then
		
			;Event: on:use
			set_parsecache(Cclass_object,Tobject\id,"use")
			If Instr(Dobject_scriptk$(Tobject\typ),"Śuse") Then
				parse_task(Cclass_object,Tobject\id,"use","",Dobject_script(Tobject\typ))
			EndIf
			
			;Search
			;For Tfind.Tfind=Each Tfind
			;	If Tfind\parent=Tobject\typ Then
			;		;Search Menu
			;		in_input$(0)=Tobject\typ
			;		in_input$(1)=Tobject\id
			;		m_menu=Cmenu_if_find
			;		Exit
			;	EndIf
			;Next
			
			Return 1
		EndIf
	Next
	Return 0
End Function


;### Search Item at Object
Function search_object(id,itemtyp)
	If con_object(id)=0 Then Return 0
	;Find
	For Tfind.Tfind=Each Tfind
		If Tfind\parent=TCobject\typ Then
			If Tfind\typ=itemtyp Then
				;Damage
				;damage_object(Dobject_searchdamage#(TCobject\typ))
				;Find?
				Local randomval=Rand(1,100)
				If randomval<=Tfind\ratio Then
					Local findcount=Rand(Tfind\min,Tfind\max)
					set_item(-1,Tfind\typ,0,0,0,findcount)
					p_add2d(in_mx,in_my,-2,TCitem\typ)
					if_msg(Ditem_name$(TCitem\typ)+" gefunden!",Cbmpf_green)
					store_item(TCitem\id,Cclass_unit,g_player,Cpm_in)
				EndIf
				;Close Screen when object is killed?
				If con_object(id)=0 Then m_menu=0
				Return 0		
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Find Item at Object
Function find_object(id)
	Local wpn
	If con_unit(g_player)
		wpn=TCunit\player_weapon
	Else
		Return 0
	EndIf
	If con_object(id)=0 Then Return 0
	;Total
	Local total=0
	;Count
	For Tfind.Tfind=Each Tfind
		If Tfind\class=Cclass_object Then
			If Tfind\parent=TCobject\typ Then
				;do only consider stuff which can be found with used or no weapon
				If wpn=Tfind\reqtyp Or Tfind\reqtyp=0 Then
					total=total+Tfind\ratio
				EndIf
			EndIf
		EndIf
	Next
	If total=0 Then Return 0 ;Nothing to find here
	;Ratio
	If Rand(1,100)<=Int(Dobject_searchratio#(TCobject\typ)) Then
		;Random
		Local total_r=Rand(0,total)
		Local total_t=0
		;Find
		For Tfind.Tfind=Each Tfind
			If Tfind\class=Cclass_object Then
				If Tfind\parent=TCobject\typ Then
					If wpn=Tfind\reqtyp Or Tfind\reqtyp=0 Then
						If total_r>=total_t Then
							If total_r<=(total_t+Tfind\ratio) Then
								Local findcount=Rand(Tfind\min,Tfind\max)
								set_item(-1,Tfind\typ,EntityX(cam),EntityY(cam),EntityZ(cam),findcount)
								p_add2d(in_mx,in_my,-2,TCitem\typ)
								itemtyp=TCitem\typ
								oldcount=countstored_items(Cclass_unit,g_player,itemtyp,0)
								If store_item(TCitem\id,Cclass_unit,g_player,Cpm_in)<>0 Then
									count=countstored_items(Cclass_unit,g_player,itemtyp,0)
									if_msg(ss$(sm$(180),Ditem_name$(itemtyp),(count-oldcount)),Cbmpf_green)
									sfx sfx_collect
								Else
									if_msg(sm$(181),Cbmpf_red)
									sfx sfx_fail
									speech("nospace")
								EndIf
								Return 1
							EndIf
						EndIf
						total_t=total_t+Tfind\ratio
					EndIf
				EndIf
			EndIf
		Next
	EndIf
End Function



;### Free Object
Function free_object(id,update=1,childs=1,entity=1)
	For Tobject.Tobject=Each Tobject	
		If Tobject\id=id Then
			;Delete Entity (Model)
			If Tobject\ch<>0 Then FreeEntity Tobject\ch
			If entity=1 Then
				If Tobject\h<>0 Then FreeEntity Tobject\h
			Else
				tmp_h=Tobject\h
				EntityType tmp_h,0
			EndIf
			;Delete
			Delete Tobject
			;Childs
			If childs Then free_childs(Cclass_object,id)
			;Items
			item_phyreset()
			;Ok
			Return 1
		EndIf
	Next
	Return 0
End Function


;### Update Object (windsway only)
Function update_object()
	;Windsway
	If set_windsway Then
		h=TCobject\h
		angle#=TCobject\windsway#
		angle#=(angle#+(Dobject_swayspeed#(TCobject\typ)*f#)) Mod 360
		TCobject\windsway#=angle#
		RotateEntity h,EntityPitch(h),EntityYaw(h),Sin(angle#)*Dobject_swaypower#(TCobject\typ)
	EndIf
End Function

;### Update Object (based on behaviour)
Function update_object_behaviour()
	Select Dobject_behaviour$(TCobject\typ)
		;Fountain
		Case "fountain"
			;FX
			If in_gt50go=1 Then
				p_add(EntityX(TCobject\h)+Rnd(-3,3),EntityY(TCobject\h)+5,EntityZ(TCobject\h)+Rnd(-3,3),Cp_splash,Rnd(15,18),1.)
				EntityColor TCp\h,150,190,255
			EndIf
			;Sound
			If Not ChannelPlaying(TCobject\chan) Then
				If EntityDistance(cam,TCobject\h)<200 Then
					TCobject\chan=sfx_emit(sfx_fountain,TCobject\h)
				EndIf
			EndIf
		;Closeexplode
		Case "closekill"
			;Distance
			If in_gt100go=1 Then
				If g_cplayer<>Null Then
					If EntityDistance(g_cplayer\h,TCobject\h)<50 Then
						damage_object(0,1)
					EndIf
				EndIf
			EndIf
		;Closetrigger
		Case "closetrigger"
			;Distance
			If in_gt500go=1 Then
				If g_cplayer<>Null Then
					If EntityDistance(g_cplayer\h,TCobject\h)<50 Then
						;Event: on:trigger
						set_parsecache(Cclass_object,TCobject\id,"trigger")
						If Instr(Dobject_scriptk$(TCobject\typ),"Śtrigger") Then
							parse_task(Cclass_object,TCobject\id,"trigger","",Dobject_script(TCobject\typ))
						EndIf
					EndIf
				EndIf
			EndIf		
	End Select
End Function


;### Determine Serial Peak
Function serialpeak_object()
	Local peak=0
	For Tobject.Tobject=Each Tobject
		If Tobject\id>peak Then peak=Tobject\id
	Next
	object_serial=peak
End Function

;### Damage Object
Function damage_object(damage#,kill=0,pickcords=0)
	;Decrease Health
	If get_state(Cstate_invulnerability,Cclass_object,TCobject\id)=0 Then
		TCobject\health#=TCobject\health#-damage#
	EndIf
	If pickcords=1 Then
		material_fx(in_px#,in_py#,in_pz#,Dobject_mat(TCobject\typ))
	ElseIf pickcords=0
		material_fx(EntityX(TCobject\h),EntityY(TCobject\h),EntityZ(TCobject\h),Dobject_mat(TCobject\typ))
	EndIf
	;Kill?
	If TCobject\health#<=0. Or kill=1 Then
		Tkill.Tobject=TCobject
		;Event: on:kill
		set_parsecache(Cclass_object,TCobject\id,"kill")
		If Instr(Dobject_scriptk$(TCobject\typ),"Śkill") Then
			parse_task(Cclass_object,TCobject\id,"kill","",Dobject_script(TCobject\typ))
			;parse_sel(Cclass_object,TCobject\id,"kill")
		EndIf
		;Drop Child Items
		drop_childs(Cclass_object,Tkill\id,1,Tkill\h)
		;Behaviour based Stuff
		Select Dobject_behaviour$(Tkill\typ)
			Case "tree" sfx_emit(sfx_treefall,Tkill\h)
		End Select
		;Free
		tmp_h=0
		tmp_killclass=Cclass_object
		tmp_killid=Tkill\id
		tmp_killspawntimer=Tkill\daytimer
		free_object(Tkill\id,1,1,0)
		Local tmp_killpivot=CreatePivot()
		If tmp_h<>0 Then
			PositionEntity tmp_killpivot,EntityX(tmp_h),EntityY(tmp_h),EntityZ(tmp_h)
			RotateEntity tmp_killpivot,EntityPitch(tmp_h),EntityYaw(tmp_h),EntityRoll(tmp_h)
		EndIf
		tmp_killmodel=tmp_killpivot
		parse_kill_add(tmp_killclass,tmp_killid,tmp_killmodel,tmp_killspawntimer)
		;Death Animation "Particle"
		If tmp_h<>0 Then
			tmp_ph=tmp_h
			p_add(0,0,0,Cp_fall)
			EntityType tmp_h,0		
			EntityPickMode tmp_h,0,0
		EndIf
		tmp_kill=1
		item_phyreset()
		Return 2
	EndIf
	Return 1
End Function


;### Grow Style
Function grow_object(o.Tobject)
	Local perc#=1.0
	If o\daytimer<0 Then
		If Dobject_growtime(o\typ)>0 Then
			;Determine Growth Percentage
			part=Dobject_growtime(o\typ)+o\daytimer
			If part<1 Then part=1
			perc#=Float( Float(part) / Float(Dobject_growtime(o\typ)) )
			If perc#>1.0 Then perc#=1.0
		EndIf
	EndIf

	;Setup Size
	ScaleEntity o\h,Dobject_size#(o\typ,0)*perc#,Dobject_size#(o\typ,1)*perc#,Dobject_size#(o\typ,2)*perc#
	If o\ch<>0 Then
		ScaleEntity o\ch,Dobject_size#(o\typ,0)*perc#,Dobject_size#(o\typ,1)*perc#,Dobject_size#(o\typ,2)*perc#
	EndIf
	
	;Setup Color
	r=Dobject_color(o\typ,1)
	r=r+(255.0*(1.0-perc#))
	EntityColor o\h,Dobject_color(o\typ,0),r,Dobject_color(o\typ,2)
	
	;Setup Health
	If o\health_max#<>Ceil(Dobject_health#(o\typ)*perc#)
		o\health_max#=Ceil(Dobject_health#(o\typ)*perc#)
		o\health#=o\health_max#
	EndIf
End Function


;### Frap Routine
Function frap_object()
	Tobject.Tobject=TCobject
	
	;Setup
	Local h=Tobject\h
	Local size#=20.
	Local piv=CreatePivot()
	Local rx#=EntityX(h)
	Local ry#=EntityY(h)
	Local rz#=EntityZ(h)
	CameraProject(cam,rx#,ry#,rz#)
	Local rx2d=ProjectedX()
	Local ry2d=ProjectedY()
	Local px
	Local py
	RotateEntity piv,EntityPitch(h,1),EntityYaw(h,1),EntityRoll(h,1)
	
	Local s#=0.25
	Local shift=KeyDown(42)+KeyDown(54)
	
	If EntityInView(Tobject\h,cam) Then
		
		If rx2d>(in_editor_sidebar*210) Then
			If ry2d>0 Then
				
				;(1) Red Pitch
				Color 255,0,0
				PositionEntity piv,rx#,ry#,rz#
				MoveEntity piv,size#,0,0
				CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
				px=ProjectedX()
				py=ProjectedY()
				If px>210 Then
					If py>0 Then
				
						Line rx2d,ry2d,px,py
						DrawImage gfx_ex,px,py
						
						If in_frapdown=0 Then
							If in_mx>px-5 Then
								If in_my>py-5 Then
									If in_mx<px+5 Then
										If in_my<py+5 Then
											DrawImage gfx_esel,px,py
											If in_mdown(1) Then
												in_mx=px
												in_mxo=in_mx
												in_my=py
												in_myo=in_my
												in_frapdown=1											
												If shift=0 Then
													in_cursorf=4
													RotateEntity h,EntityPitch(h)+Float(Float(in_mxs#+in_mys#)*s#),EntityYaw(h),EntityRoll(h)
												Else
													in_cursorf=2
													MoveEntity h,Float(Float(in_mxs#+in_mys#)*s#),0,0
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						ElseIf in_frapdown=1 Then
							DrawImage gfx_esel,px,py
							If in_mdown(1) Then
								in_mx=px
								in_mxo=in_mx
								in_my=py
								in_myo=in_my
								in_frapdown=1
								If shift=0 Then
									in_cursorf=4
									RotateEntity h,EntityPitch(h)+Float(Float(in_mxs#+in_mys#)*s#),EntityYaw(h),EntityRoll(h)
								Else
									in_cursorf=2
									MoveEntity h,Float(Float(in_mxs#+in_mys#)*s#),0,0
								EndIf
							EndIf
						EndIf
						
					EndIf
				EndIf
					
				;(2) Green Yaw
				Color 0,255,0
				PositionEntity piv,rx#,ry#,rz#
				MoveEntity piv,0,size#,0
				CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
				px=ProjectedX()
				py=ProjectedY()
				If px>(in_editor_sidebar*210) Then
					If py>0 Then
				
						Line rx2d,ry2d,px,py
						DrawImage gfx_ey,px,py
						
						If in_frapdown=0 Then
							If in_mx>px-5 Then
								If in_my>py-5 Then
									If in_mx<px+5 Then
										If in_my<py+5 Then
											DrawImage gfx_esel,px,py
											If in_mdown(1) Then
												in_mx=px
												in_mxo=in_mx
												in_my=py
												in_myo=in_my
												in_frapdown=2
												If shift=0 Then
													in_cursorf=4
													RotateEntity h,EntityPitch(h),EntityYaw(h)+Float(Float(in_mxs#+in_mys#)*s#),EntityRoll(h)
												Else
													in_cursorf=2
													MoveEntity h,0,Float(Float(in_mxs#+in_mys#)*s#),0
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						ElseIf in_frapdown=2 Then
							DrawImage gfx_esel,px,py
							If in_mdown(1) Then
								in_mx=px
								in_mxo=in_mx
								in_my=py
								in_myo=in_my
								in_frapdown=2
								If shift=0 Then
									in_cursorf=4
									RotateEntity h,EntityPitch(h),EntityYaw(h)+Float(Float(in_mxs#+in_mys#)*s#),EntityRoll(h)
								Else
									in_cursorf=2
									MoveEntity h,0,Float(Float(in_mxs#+in_mys#)*s#),0
								EndIf
							EndIf
						EndIf
						
					EndIf
				EndIf
				
				;(3) Blue Roll
				Color 0,0,255
				PositionEntity piv,rx#,ry#,rz#
				MoveEntity piv,0,0,size#
				CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
				px=ProjectedX()
				py=ProjectedY()
				If px>(in_editor_sidebar*210) Then
					If py>0 Then
						
						Line rx2d,ry2d,px,py
						
						If Dobject_swayspeed#(Tobject\typ)=0. Then
						
							DrawImage gfx_ez,px,py
							
							If in_frapdown=0 Then
								If in_mx>px-5 Then
									If in_my>py-5 Then
										If in_mx<px+5 Then
											If in_my<py+5 Then
												DrawImage gfx_esel,px,py
												If in_mdown(1) Then
													in_mx=px
													in_mxo=in_mx
													in_my=py
													in_myo=in_my
													in_frapdown=3
													If shift=0 Then
														in_cursorf=4
														RotateEntity h,EntityPitch(h),EntityYaw(h),EntityRoll(h)+Float(Float(in_mxs#+in_mys#)*s#) 
													Else
														in_cursorf=2
														MoveEntity h,0,0,Float(Float(in_mxs#+in_mys#)*s#)
													EndIf
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							ElseIf in_frapdown=3 Then
								DrawImage gfx_esel,px,py
								If in_mdown(1) Then
									in_mx=px
									in_mxo=in_mx
									in_my=py
									in_myo=in_my
									in_frapdown=3
									If shift=0 Then
										in_cursorf=4
										RotateEntity h,EntityPitch(h),EntityYaw(h),EntityRoll(h)+Float(Float(in_mxs#+in_mys#)*s#) 
									Else
										in_cursorf=2
										MoveEntity h,0,0,Float(Float(in_mxs#+in_mys#)*s#)
									EndIf
								EndIf	
							EndIf
							
						EndIf
						
					EndIf
				EndIf
					
				;(4) Green Center Position
				px=rx2d
				py=ry2d
				DrawImage gfx_ey,px,py
				
				If in_frapdown=0 Then
					If in_mx>px-5 Then
						If in_my>py-5 Then
							If in_mx<px+5 Then
								If in_my<py+5 Then
									DrawImage gfx_esel,px,py
									If in_mdown(1) Then
										in_mx=px
										in_mxo=in_mx
										in_my=py
										in_myo=in_my
										in_frapdown=4
										in_cursorf=2
										TranslateEntity h,0,-in_mys#*s#,0
										mhp=CreatePivot()
										PositionEntity mhp,EntityX(h),0,EntityZ(h)
										RotateEntity mhp,0,EntityYaw(cam),0
										MoveEntity mhp,-in_mxs#*s#,0,in_mzs#*s#
										PositionEntity h,EntityX(mhp),EntityY(h),EntityZ(mhp)
										FreeEntity mhp
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				ElseIf in_frapdown=4 Then
					DrawImage gfx_esel,px,py
					If in_mdown(1) Then
						in_mx=px
						in_mxo=in_mx
						in_my=py
						in_myo=in_my
						in_frapdown=4
						in_cursorf=2
						TranslateEntity h,0,-in_mys#*s#,0
						mhp=CreatePivot()
						PositionEntity mhp,EntityX(h),0,EntityZ(h)
						RotateEntity mhp,0,EntityYaw(cam),0
						MoveEntity mhp,-in_mxs#*s#,0,in_mzs#*s#
						PositionEntity h,EntityX(mhp),EntityY(h),EntityZ(mhp)
						FreeEntity mhp
					EndIf
				EndIf
				
				;Name
				If in_frapdown=0 Then
					CameraProject(cam,EntityX(Tobject\h),EntityY(Tobject\h),EntityZ(Tobject\h))
					bmpf_txt_c(ProjectedX(),ProjectedY()-30,"#"+Tobject\id+" "+Dobject_name$(Tobject\typ),Cbmpf_tiny)
				EndIf
				
				;Info
				in_edoverinfo$=ss$(se$(197),Dobject_name$(Tobject\typ))
				
			EndIf
		EndIf			
	EndIf
	
	;Update FRAP Data
	For Tfrap.Tfrap=Each Tfrap
		If Tfrap\parent_class=Cclass_object Then
			If Tfrap\parent_id=Tobject\id Then
				Tfrap\y#=EntityY(Tobject\h)
				Tfrap\pitch#=EntityPitch(Tobject\h)
				Tfrap\roll#=EntityRoll(Tobject\h)
				Exit
			EndIf
		EndIf
	Next
				
	;Free
	If KeyHit(211) Then free_object(Tobject\id):in_fraph=0
		
	;Leave FRAP Mode
	If KeyHit(57) Then in_fraph=0
		
End Function


;Get Spawn Position of Object
Function object_spawnpos(id)
	;Find
	For Tobject.Tobject=Each Tobject
		If Tobject\id=id Then
			If Dobject_spawn$(Tobject\typ)<>"" Then
				;Split
				split(Dobject_spawn$(TCobject\typ))
				s_item=Int(splits$(0))
				s_rate=Int(splits$(1))
				s_xzr#=Float(splits$(2))
				s_yr#=Float(splits$(3))
				s_yo#=Float(splits$(4))
				s_limit=Int(splits$(5))
				s_count=Int(splits$(6))
				;Position
				Local p=CreatePivot()
				PositionEntity p,EntityX(Tobject\h),EntityY(Tobject\h)+Rnd(-s_yr#,s_yr#)+s_yo#,EntityZ(Tobject\h)
				RotateEntity p,0,Rand(360),0
				MoveEntity p,0,0,Rnd((s_xzr#/2.5),s_xzr#)
				tmp_x#=EntityX(p)
				tmp_y#=EntityY(p)
				tmp_z#=EntityZ(p)
				FreeEntity p
				Return 1
			EndIf
			Return 0
		EndIf
	Next
	Return 0
End Function
