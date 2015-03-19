;############################################ Handle states


;### Set State
Function set_state(typ,parent_class,parent_id)
	;Free same States
	value#=0.
	For Tstate.Tstate=Each Tstate
		If Tstate\parent_class=parent_class Then
			If Tstate\parent_id=parent_id Then
				If Tstate\typ=typ Then
					value#=value#+Tstate\value_f#
					free_state(Tstate\typ,Tstate\parent_class,Tstate\parent_id)
				EndIf
			EndIf
		EndIf
	Next
	
	;Create
	Tstate.Tstate=New Tstate
	Tstate\typ=typ
	Tstate\parent_class=parent_class
	Tstate\parent_id=parent_id
	Tstate\light=0
	TCstate.Tstate=Tstate
	
	;Power/Damage
	Tstate\value_f#=3.+value#

	;Stuff
	Select typ
		
		;Fire
		Case Cstate_fire
			;Do not allow Fire during Rain/Snow
			If env_cweather=1 Or env_cweather=2 Then
				Delete Tstate
				Return 0
			EndIf
			;Do not allow Fire on Fire Resistant Material
			If Dmat_nofire(parent_material(parent_class,parent_id))=1 Then
				Delete Tstate
				Return 0
			;Do not allow Fire under Water
			Else
				If tmp_loading=0 Then
					h=parent_h(parent_class,parent_id)
					If h<>0 Then
						If EntityY(h)<0 Then
							Delete Tstate
							Return 0
						EndIf
					EndIf
				EndIf
			EndIf
			;Wet State
			wet#=0
			ia=0
			For c.Tstate=Each Tstate
				If c\parent_class=parent_class Then
					If c\parent_id=parent_id Then
						If c\typ=Cstate_wet Then
							ia=1
							wet#=c\value_f#
							c\value_f#=c\value_f#-Tstate\value_f#
							If c\value_f#<=0. Then
								free_state(Cstate_wet,parent_class,parent_id)
							EndIf
							Exit
						EndIf
					EndIf
				EndIf
			Next
			If ia=1 Then
				Tstate\value_f#=Tstate\value_f#-wet#
				h=parent_h(parent_class,parent_id)
				If h<>0 Then
					sfx_emit(sfx_fizzle,h)
					p_add(EntityX(h),EntityY(h),EntityZ(h),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
					EntityColor TCp\h,240,240,240
					EntityBlend TCp\h,3
				EndIf
				If Tstate\value_f#<=0. Then
					Delete Tstate
					Return 0
				EndIf
			EndIf
			;Light
			If parent_stateposstatic(parent_class,parent_id)=1 Then
				Tstate\light=CreateLight(2)
				LightColor Tstate\light,255,255,0
				LightRange Tstate\light,50
				parent_statepos(parent_class,parent_id)
				PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
			EndIf
		
		;Wet
		Case Cstate_wet
			;Fire State
			ia=0
			fire#=0
			For c.Tstate=Each Tstate
				If c\parent_class=parent_class Then
					If c\parent_id=parent_id Then
						If c\typ=Cstate_fire Then
							ia=1
							fire#=c\value_f#
							c\value_f#=c\value_f#-Tstate\value_f#
							If c\value_f#<=0. Then
								free_state(Cstate_fire,parent_class,parent_id)
							EndIf
							Exit
						EndIf
					EndIf
				EndIf
			Next
			If ia=1 Then
				Tstate\value_f#=Tstate\value_f#-fire#
				h=parent_h(parent_class,parent_id)
				If h<>0 Then
					sfx_emit(sfx_fizzle,h)
					p_add(EntityX(h),EntityY(h),EntityZ(h),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
					EntityColor TCp\h,240,240,240
					EntityBlend TCp\h,3
				EndIf
				If Tstate\value_f#<=0. Then
					Delete Tstate
					Return 0
				EndIf
			EndIf
			;Eternal Fire
			For c.Tstate=Each Tstate
				If c\parent_class=parent_class Then
					If c\parent_id=parent_id Then
						If c\typ=Cstate_eternalfire Then
							h=parent_h(parent_class,parent_id)
							If h<>0 Then
								sfx_emit(sfx_fizzle,h)
								For i=1 To 3
									p_add(EntityX(h)+Rnd(-5,5),EntityY(h)+Rnd(-5,5),EntityZ(h)+Rnd(-5,5),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
									EntityColor TCp\h,240,240,240
									EntityBlend TCp\h,3
								Next
							EndIf
							Exit
							Delete Tstate
							Return 0
						EndIf
					EndIf
				EndIf
			Next
			
		
		;Eternal Fire
		Case Cstate_eternalfire
			Tstate\value_f#=Tstate\value_f#-3.
			;Light
			If parent_stateposstatic(parent_class,parent_id)=1 Then
				Tstate\light=CreateLight(2)
				LightColor Tstate\light,255,255,0
				LightRange Tstate\light,50
				parent_statepos(parent_class,parent_id)
				PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
			EndIf
			;Wet State
			For c.Tstate=Each Tstate
				If c\parent_class=parent_class Then
					If c\parent_id=parent_id Then
						If c\typ=Cstate_wet Then
							free_state(Cstate_wet,parent_class,parent_id)
							h=parent_h(parent_class,parent_id)
							If h<>0 Then
								sfx_emit(sfx_fizzle,h)
								For i=1 To 3
									p_add(EntityX(h)+Rnd(-5,5),EntityY(h)+Rnd(-5,5),EntityZ(h)+Rnd(-5,5),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
									EntityColor TCp\h,240,240,240
									EntityBlend TCp\h,3
								Next
							EndIf
							Exit
						EndIf
					EndIf
				EndIf
			Next
		
		;Electro Shock
		Case Cstate_electroshock
			;Light
			Tstate\light=CreateLight(2)
			LightColor Tstate\light,0,0,255
			LightRange Tstate\light,75
			parent_stateposvertex(parent_class,parent_id)
			PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
			
		;Bloodrush
		Case Cstate_bloodrush
			Tstate\value_f#=2.
		
		;Flare
		Case Cstate_flare
			Tstate\h=CreateSprite()
			Tstate\value=50
			ScaleSprite Tstate\h,Tstate\value,Tstate\value
			EntityTexture Tstate\h,gfx_p_flare(0)
			EntityBlend Tstate\h,3
			parent_statepos(parent_class,parent_id)
			PositionEntity Tstate\h,tmp_x#,tmp_y#,tmp_z#
			;EntityParent Tstate\h,parent_h(parent_class,parent_id)
			EntityAlpha Tstate\h,0.25
			Tstate\value_s$="255,255,255"
			
		;Light
		Case Cstate_light
			Tstate\h=CreateLight(2)
			parent_statepos(parent_class,parent_id)
			PositionEntity Tstate\h,tmp_x#,tmp_y#,tmp_z#
			Tstate\value_s$="255,255,255"
			Tstate\value=10
			LightRange Tstate\h,Tstate\value

		;Particles
		Case Cstate_particles
			Tstate\value_s$="-1,-1,-1"
			Tstate\r=-1
		
	End Select
	
	;Unit States Stuff
	If parent_class=Cclass_unit
		If Dstate_a(typ)=1 Then
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parent_id Then
					Tunit\states=1
					Exit
				EndIf
			Next
		EndIf
	EndIf
	
	;Event
	pv_state=typ
	parse_emuevent(parent_class,parent_id,"addstate")
	;parse_sel(parent_class,parent_id,"addstate")
	
	;Okay
	Return 1
End Function


;## Alter State Look by its values
Function look_state()
	Select TCstate\typ
					
		;Flare
		Case Cstate_flare
			split(TCstate\value_s$,",")
			If TCstate\h<>0 Then
				EntityColor TCstate\h,splits$(0),splits$(1),splits$(2)
				ScaleSprite TCstate\h,TCstate\value,TCstate\value
			EndIf
	
		;Light
		Case Cstate_light
			split(TCstate\value_s$,",")
			If TCstate\h<>0 Then
				LightColor TCstate\h,splits$(0),splits$(1),splits$(2)
				If TCstate\value>0 Then
					LightRange TCstate\h,TCstate\value
				EndIf
			EndIf
		
		;Particles
		Case Cstate_particles
			split(TCstate\value_s$,",")
			TCstate\r=splits$(0)
			TCstate\g=splits$(1)
			TCstate\b=splits$(2)
		
	End Select
End Function


;### Attach State to Handle
Function attach_state(h,typ,onlyonclass=-1,bycollisionpivot=1)
	If ha_getclass(h,bycollisionpivot) Then
		If onlyonclass=-1 Or onlyonclass=tmp_class Then
			If set_state(typ,tmp_class,tmp_id) Then
				Return 1
			EndIf
		EndIf
	EndIf
	Return 0
End Function


;### Free State
Function free_state(typ,parent_class,parent_id)
	For Tstate.Tstate=Each Tstate
		If Tstate\parent_class=parent_class Then
			If Tstate\parent_id=parent_id Then
				If Tstate\typ=typ Then
					
					;State Stuff
					Select Tstate\typ
						Case Cstate_link
							free_object(Tstate\value)
					End Select
					
					;if_msg("FREE STATE "+Dstate_name$(typ))
				
					;Model
					If Tstate\h<>0 Then
						FreeEntity Tstate\h
					EndIf
					;Light
					If Tstate\light<>0 Then
						FreeEntity Tstate\light
						;if_msg("FREE LIGHT")
					EndIf
					
					;Delete
					Delete Tstate
					
					;Unit States Stuff
					If parent_class=Cclass_unit
						set=0
						For t2.Tstate=Each Tstate
							If t2\parent_class=Cclass_unit Then
								If t2\parent_id=parent_id Then
									If Dstate_a(t2\typ)=1 Then
										set=1
										Exit
									EndIf
								EndIf
							EndIf
						Next
						For Tunit.Tunit=Each Tunit
							If Tunit\id=parent_id Then
								Tunit\states=set
								Exit
							EndIf
						Next
					EndIf
					
					;Event
					pv_state=typ
					parse_emuevent(parent_class,parent_id,"freestate")
					;parse_sel(parent_class,parent_id,"freestate")
					
					;Ok
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Get State
Function get_state(typ,parent_class,parent_id)
	For Tstate.Tstate=Each Tstate
		If Tstate\parent_class=parent_class Then
			If Tstate\parent_id=parent_id Then
				If Tstate\typ=typ Then
					TCstate.Tstate=Tstate
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Get Process State (typ > 99)
Function get_state_process(parent_class,parent_id)
	For Tstate.Tstate=Each Tstate
		If Tstate\parent_class=parent_class Then
			If Tstate\parent_id=parent_id Then
				If Tstate\typ>99 Then
					TCstate.Tstate=Tstate
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Get State Position
Function state_inrange(rooth,typ,range=Cworld_userange)
	Local p1,p2
	p1=CreatePivot()
	p2=CreatePivot()
	PositionEntity p1,EntityX(rooth),EntityY(rooth),EntityZ(rooth)
	For Tstate.Tstate=Each Tstate
		If Tstate\typ=typ Then
			If parent_statepos(Tstate\parent_class,Tstate\parent_id) Then
				PositionEntity p2,tmp_x#,tmp_y#,tmp_z#
				If EntityDistance(p1,p2)<=range Then
					FreeEntity p1
					FreeEntity p2
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	FreeEntity p1
	FreeEntity p2
	Return 0
End Function


;### State Impact FX
Function state_impactfx(state,h)
	Local x#=EntityX(h)
	Local y#=EntityY(h)
	Local z#=EntityZ(h)
	Select state
		Case Cstate_bleeding
		Case Cstate_intoxication
			For i=0 To Rand(3,5)
				p_add(x#,y#,z#,Cp_spark,Rand(1,2),3)
				EntityColor TCp\h,0,255,0
			Next
			p_add(x#,y#,z#,Cp_smoke,Rnd(8,14),Rnd(1,1.5))
			EntityColor TCp\h,0,Rand(150,255),0
			EntityBlend TCp\h,3
		Case Cstate_pus
		Case Cstate_fire,Cstate_eternalfire
			p_add(x#,y#,z#,Cp_flames,5,1)
			For i=0 To Rand(3,5)
				p_add(x#,y#,z#,Cp_firespark,Rand(2,6),1)
			Next
			For i=0 To Rand(3,5)
				p_add(x#,y#,z#,Cp_spark,Rand(1,2),3)
			Next
		Case Cstate_frostbite
		Case Cstate_fracture
		Case Cstate_electroshock
		Case Cstate_bloodrush
		Case Cstate_dizzy
		Case Cstate_wet
		Case Cstate_fuddle
		Case Cstate_healing
		Case Cstate_invulnerability
	End Select
End Function


;### Update States
Function update_state()
	c=0
	ct=MilliSecs()
	set_mb_alpha_override#=0.
	set_xinvert=0
	For Tstate.Tstate=Each Tstate
		c=c+1
		Select Tstate\typ
			
			;Physiks
			Case Cstate_phy
				;Move
				Select Tstate\parent_class
					;Unit Physiks
					Case Cclass_unit
						con_unit(Tstate\parent_id)
						TranslateEntity TCunit\h,Tstate\fx#,Tstate\fy#,Tstate\fz#
						
					;Items Physiks
					Case Cclass_item
						con_item(Tstate\parent_id)
						TranslateEntity TCitem\h,Tstate\fx#,Tstate\fy#,Tstate\fz#
					
				End Select
				;Modify
				If in_gt100go Then
					Tstate\fx#=Tstate\fx#*Tstate\x#
					Tstate\fy#=Tstate\fy#*Tstate\y#
					Tstate\fz#=Tstate\fz#*Tstate\z#
					;Delete
					If Abs(Tstate\fy#)<0.001 Then
						If Abs(Tstate\fx#)<0.001 Then
							If Abs(Tstate\fz#)<0.001 Then
								Delete Tstate
							EndIf
						EndIf
					EndIf
				EndIf

			;Bleeding
			Case Cstate_bleeding
				;Blood
				If in_gt500go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						For i=1 To 3
							p_add(tmp_x#,tmp_y#,tmp_z#,Cp_splatter,Rnd(1,5),Rnd(0.9,1.5))
						Next
					EndIf
				EndIf
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						If Tstate\value_f#<>0 Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
				
			;Intoxication
			Case Cstate_intoxication
				;Poison Clouds
				If in_gt500go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#,tmp_y#,tmp_z#,Cp_smoke,Rnd(1,5),Rnd(0.9,1.5))
						EntityColor TCp\h,Rand(0,255),Rand(150,255),0
						EntityBlend TCp\h,3
					EndIf
				EndIf
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						If Tstate\value_f#<>0 Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
				
			;Pus
			Case Cstate_pus
							
			;Fire
			Case Cstate_fire
				;Flames
				If in_gt100go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#,tmp_y#,tmp_z#,Cp_flames,5,1)
						If Rand(5)=1 Then
							p_add(tmp_x#,tmp_y#,tmp_z#,Cp_firespark,Rand(2,6),1)
						EndIf
						If in_gt5000go Then
							If Rand(1)=1 Then
								p_add(tmp_x#,tmp_y#,tmp_z#,Cp_spark,Rand(1,2),3)
							EndIf
						EndIf
					EndIf
				EndIf
				;SFX
				If Not ChannelPlaying(Tstate\chan) Then
					h=parent_h(Tstate\parent_class,Tstate\parent_id)
					If EntityDistance(cam,h)<100 Then
						Tstate\chan=sfx_emit(sfx_fire,h)
					EndIf
				EndIf
				;Light
				If in_gt50go Then
					If Tstate\light<>0 Then
						LightColor Tstate\light,game_firelightbrightness,Rnd(game_firelightbrightness-50,game_firelightbrightness),0
						range=game_firelightsize+Rnd(-5,5)
						If range<0 Then range=0
						LightRange Tstate\light,range
						parent_statepos(Tstate\parent_class,Tstate\parent_id)
						PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
						If EntityDistance(cam,Tstate\light)<((300.*set_viewfac#)+300) Then
							If EntityInView(Tstate\light,cam) Then
								ShowEntity Tstate\light
							ElseIf EntityDistance(cam,Tstate\light)<(500) Then
								ShowEntity Tstate\light
							Else
								HideEntity Tstate\light
							EndIf
						Else
							HideEntity Tstate\light
						EndIf
					EndIf
				EndIf
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						parent_pos(Tstate\parent_class,Tstate\parent_id)
						If Tstate\value_f#<>0 Then
							If ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)=2 Then
								;End of Fire - Expand?!
								For Tobject.Tobject=Each Tobject
									If dist#(tmp_x#,tmp_z#,EntityX(Tobject\h),EntityZ(Tobject\h))<=game_firerange Then
										set_state(Cstate_fire,Cclass_object,Tobject\id)
									EndIf
								Next
								;Prevent Double Deletion!
								tmp_ry#=1
							EndIf
						EndIf
					EndIf
				EndIf
				;Kill Under Water
				If in_gt50go Then
					If tmp_ry#<0 Then
						h=parent_h(Tstate\parent_class,Tstate\parent_id)
						If h<>0 Then
							sfx_emit(sfx_fizzle,h)
							p_add(EntityX(h),EntityY(h),EntityZ(h),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
							EntityColor TCp\h,240,240,240
							EntityBlend TCp\h,3
						EndIf
						free_state(Tstate\typ,Tstate\parent_class,Tstate\parent_id)
					EndIf
				EndIf
			
			;Eternal Fire
			Case Cstate_eternalfire
				;Flames
				If in_gt100go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#,tmp_y#,tmp_z#,Cp_flames,5,1)
						If Rand(5)=1 Then
							p_add(tmp_x#,tmp_y#,tmp_z#,Cp_firespark,Rand(2,6),1)
						EndIf
						If in_gt5000go Then
							If Rand(1)=1 Then
								p_add(tmp_x#,tmp_y#,tmp_z#,Cp_spark,Rand(1,2),3)
							EndIf
						EndIf
					EndIf
				EndIf
				;SFX
				If Not ChannelPlaying(Tstate\chan) Then
					h=parent_h(Tstate\parent_class,Tstate\parent_id)
					If EntityDistance(cam,h)<100 Then
						Tstate\chan=sfx_emit(sfx_fire,h)
					EndIf
				EndIf
				;Light
				If in_gt50go Then
					If Tstate\light<>0 Then
						LightColor Tstate\light,game_firelightbrightness,Rnd(game_firelightbrightness-50,game_firelightbrightness),0
						range=game_firelightsize+Rnd(-5,5)
						If range<0 Then range=0
						LightRange Tstate\light,range
						parent_statepos(Tstate\parent_class,Tstate\parent_id)
						PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
						If EntityDistance(cam,Tstate\light)<((300.*set_viewfac#)+300) Then
							If EntityInView(Tstate\light,cam) Then
								ShowEntity Tstate\light
							ElseIf EntityDistance(cam,Tstate\light)<(500) Then
								ShowEntity Tstate\light
							Else
								HideEntity Tstate\light
							EndIf
						Else
							HideEntity Tstate\light
						EndIf
					EndIf
				EndIf
				;Damage
				If in_gt5000go Then
					If Tstate\value_f#<>0 Then
						If m_section<>Csection_editor Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
			
			;Frostbite
			Case Cstate_frostbite
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						If Tstate\value_f#<>0 Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
			
			;Fracture
			Case Cstate_fracture
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						If Tstate\value_f#<>0 Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
				
			;Electroshock
			Case Cstate_electroshock
				;Light
				If in_gt50go Then
					If Tstate\light<>0 Then
						LightColor Tstate\light,50,50,Rand(150,200)
						LightRange Tstate\light,Rnd(50,100)
						parent_stateposvertex(Tstate\parent_class,Tstate\parent_id)
						PositionEntity Tstate\light,tmp_x#,tmp_y#,tmp_z#
						If EntityDistance(cam,Tstate\light)<((100.*set_viewfac#)+300) Then
							ShowEntity Tstate\light
						Else
							HideEntity Tstate\light
						EndIf
						
						;Lightning / Sparks
						If Rand(3=1) Then
							If EntityDistance(cam,Tstate\light)<500 Then
								If incam(tmp_x#,tmp_z#) Then						
									p_lightning(tmp_x#,tmp_y#,tmp_z#,Rand(3,6),Rnd(4,8))
									If Rand(2)=1 Then
										p_add(tmp_x#,tmp_y#,tmp_z#,Cp_spark,Rand(1,2),1)
										EntityColor TCp\h,255,255,150
									EndIf
									If Rand(4)=1 Then
										sfx_3d(tmp_x#,tmp_y#,tmp_z#,sfx_spark(Rand(0,3)))
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				;Damage
				If in_gt5000go Then
					If m_section<>Csection_editor Then
						If Tstate\value_f#<>0 Then
							ha_damage(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
				
			
			;Bloodrush
			Case Cstate_bloodrush
				;Particles
				If in_gt50go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#+Rnd(-5,5),tmp_y#+Rnd(5),tmp_z#+Rnd(-5,5),Cp_starflare,Rnd(1,5),Rnd(0.4,1))
						EntityColor TCp\h,255,Rand(0,100),0
					EndIf
				EndIf
				;Motionblur
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\parent_id=g_player Then
						mb_override(0.4)
					EndIf
				EndIf
					
			;Dizzy
			Case Cstate_dizzy
				;Motionblur
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\parent_id=g_player Then
						mb_override(0.75)
						set_xinvert=1
					EndIf
				EndIf
				
			;Wet
			Case Cstate_wet
				;Drips
				If in_gt500go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#,tmp_y#,tmp_z#,Cp_spark,Rand(1,2),3)
						EntityColor TCp\h,Rand(230,240),Rand(230,240),255
					EndIf
				EndIf
				
			;Fuddle
			Case Cstate_fuddle
				;Colorstuff
				If in_gt500go Then
					If Tstate\value_f#>=10. Then
						p_add(0,0,0,Cp_fade,Rnd(0.03,0.05),Rnd(0.4,0.6))
						EntityColor TCp\h,Rnd(255),Rnd(255),Rnd(255)
						EntityBlend TCp\h,3
					EndIf
				EndIf
				;Motionblur
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\parent_id=g_player Then
						 mb_override(0.90)
					EndIf
				EndIf
			
			;Healing
			Case Cstate_healing
				;Particles
				If in_gt50go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#+Rnd(-5,5),tmp_y#+Rnd(5),tmp_z#+Rnd(-5,5),Cp_starflare,Rnd(1,5),Rnd(0.4,1))
						EntityColor TCp\h,Rand(0,100),255,0
					EndIf
				EndIf
				;Heal
				If in_gt1000go Then
					If Tstate\value_f#<>0 Then
						If m_section<>Csection_editor Then
							ha_heal(Tstate\parent_class,Tstate\parent_id,Tstate\value_f#)
						EndIf
					EndIf
				EndIf
				
			;Invulnerability
			Case Cstate_invulnerability
				
			;Tame
			Case Cstate_tame
				;Particles
				If in_gt100go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#+Rnd(-5,5),tmp_y#+Rnd(5),tmp_z#+Rnd(-5,5),Cp_starflare,Rnd(1,5),Rnd(0.4,1))
						EntityColor TCp\h,255,75,Rnd(100,200)
					EndIf
				EndIf
			
			;Flare
			Case Cstate_flare
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\h<>0 Then
						parent_statepos(Tstate\parent_class,Tstate\parent_id)
						PositionEntity Tstate\h,tmp_x#,tmp_y#,tmp_z#
					EndIf
				EndIf
			
			;Smoke
			Case Cstate_smoke
				;Smoke Clouds
				If in_gt500go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#,tmp_y#,tmp_z#,Cp_smoke,Rnd(1,5),Rnd(0.9,2.4))
					EndIf
				EndIf
			
			;Light
			Case Cstate_light
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\h<>0 Then
						parent_statepos(Tstate\parent_class,Tstate\parent_id)
						PositionEntity Tstate\h,tmp_x#,tmp_y#,tmp_z#
						If EntityDistance(cam,Tstate\h)<((300.*set_viewfac#)+300) Then
							If EntityInView(Tstate\h,cam) Then
								ShowEntity Tstate\h
							ElseIf EntityDistance(cam,Tstate\h)<(500) Then
								ShowEntity Tstate\h
							Else
								HideEntity Tstate\h
							EndIf
						Else
							HideEntity Tstate\h
						EndIf
					EndIf
				EndIf
				
			;Particles
			Case Cstate_particles
				;Particles
				If in_gt50go Then
					parent_statepos(Tstate\parent_class,Tstate\parent_id)
					If incam(tmp_x#,tmp_z#) Then
						p_add(tmp_x#+Rnd(-3,3),tmp_y#+Rnd(-3,3),tmp_z#+Rnd(-3,3),Cp_starflare,Rnd(1,5),Rnd(0.9,2.4))
						If Tstate\r>-1 Then
							EntityColor TCp\h,Tstate\r,Tstate\g,Tstate\b
						EndIf
					EndIf
				EndIf
				
			;Ghost
			Case Cstate_ghost
				If in_gt50go Then
					;Check
					If Tstate\parent_class=Cclass_object Then
						For Tobject.Tobject=Each Tobject
							If Tstate<>Null Then		
								If Tobject\id=Tstate\parent_id Then
									If Tobject\h<>0 Then
										If EntityDistance(Tobject\h,cam)>75 Then
											;Reset Collision
											If Dobject_col(Tobject\typ)>0 Then
												If Tobject\ch<>0 Then
													EntityType Tobject\ch,Cworld_col
												Else
													EntityType Tobject\h,Cworld_col
												EndIf
											EndIf
											;Delete State
											Delete Tstate
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf
			
		End Select
	Next
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_states$=(MilliSecs()-ct)+" ms States ("+c+")"
	EndIf
End Function


;Depleted?
Function state_depleted(state,water)
	If water=1 Then
		Select state
			Case Cstate_fire Return 1
			Default Return 0
		End Select
	Else
		Return 0
	EndIf
End Function
