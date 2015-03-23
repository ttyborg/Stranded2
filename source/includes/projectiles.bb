;############################################ PROJECTILES
;Stuff that comes out of weapons/enemies ;)

;### Projectile add
Function pro_add(spawner,typ,pitch#,yaw#)
	Local unitspawn=0
	;Con Spawner
	If con_unit(spawner) Then
		unitspawn=1
	Else
		unitspawn=0
	EndIf

	;Check Model
	If Ditem_modelh(typ)=0 Then
		load_item_model(typ)
	EndIf

	;Create
	Tpro.Tpro=New Tpro
	Tpro\spawner=spawner
	Tpro\typ=typ
	Local h=CopyEntity(Ditem_modelh(typ))
	Tpro\mh=h
	Tpro\age=gt
	Tpro\wc=0
	If unitspawn=1 Then Tpro\weapon=TCunit\player_weapon
	
	;Adjust
	ScaleEntity h,Ditem_size#(typ,0),Ditem_size#(typ,1),Ditem_size#(typ,2)
	EntityColor h,Ditem_color(typ,0),Ditem_color(typ,1),Ditem_color(typ,2)
	If Ditem_fx(typ)<>0 Then EntityFX h,Ditem_fx(typ)
	;If Ditem_autofade(typ)<>0 Then EntityAutoFade h,Float(Ditem_autofade(typ))*set_viewfac#,(Float(Ditem_autofade(typ))*set_viewfac#)+50
	EntityAlpha h,Ditem_alpha#(typ)
	If Ditem_shininess#(typ)>0.0 EntityShininess h,Ditem_shininess#(typ)
	If Ditem_blend(typ)<>0 Then EntityBlend h,Ditem_blend(typ)
	
	;Position & Rotation
	If unitspawn=1 Then
		RotateEntity h,pitch#,yaw#,Rnd(0,360)
		If TCunit\id=g_player Then
			;Spawn Player Shots at cam (due to y pos bug while riding)
			PositionEntity h,EntityX(cam),EntityY(cam),EntityZ(cam)
		Else
			;Spawn Shots at Unit
			PositionEntity h,EntityX(TCunit\h),EntityY(TCunit\h)+Dunit_eyes#(TCunit\typ),EntityZ(TCunit\h)
		EndIf
		MoveEntity h,0,0,Dunit_colxr#(TCunit\typ)+5.
	EndIf
	
	;Pivot for Movement
	Tpro\h=CreatePivot()
	PositionEntity Tpro\h,EntityX(Tpro\mh),EntityY(Tpro\mh),EntityZ(Tpro\mh)
	RotateEntity Tpro\h,pitch#,yaw#,0
	EntityParent Tpro\mh,Tpro\h
	
	;Collisions
	EntityRadius Tpro\h,3
	EntityType Tpro\h,Cworld_procol
	
	;Water
	If EntityY(Tpro\h)<0 Then
		Tpro\water=1
		Tpro\wc=1
	EndIf
	
	;Tail
	;by State Weapon
	Select Ditem_wstate(Tpro\weapon)
		Case Cstate_bleeding
			Tpro\tail=Cpro_blood
		Case Cstate_intoxication
			Tpro\tail=Cpro_poison
		Case Cstate_fire,Cstate_eternalfire
			Tpro\tail=Cpro_fire
		Case Cstate_wet
			Tpro\tail=Cpro_wet
		Case Cstate_healing
			Tpro\tail=Cpro_healing
		Case Cstate_tame
			Tpro\tail=Cpro_tame
		Case Cstate_smoke
			Tpro\tail=Cpro_smoke
	End Select
	;by State Ammo
	If Tpro\typ<>Tpro\weapon Then
		Select Ditem_wstate(Tpro\typ)
			Case Cstate_bleeding
				Tpro\tail=Cpro_blood
			Case Cstate_intoxication
				Tpro\tail=Cpro_poison
			Case Cstate_fire,Cstate_eternalfire
				Tpro\tail=Cpro_fire
			Case Cstate_wet
				Tpro\tail=Cpro_wet
			Case Cstate_healing
				Tpro\tail=Cpro_healing
			Case Cstate_tame
				Tpro\tail=Cpro_tame
			Case Cstate_smoke
				Tpro\tail=Cpro_smoke
		End Select
	EndIf
	;by Behaviour
	b$=Ditem_behaviour$(Tpro\typ)
	If Left(b$,6)="notail" Then
		Tpro\tail=0
	ElseIf Left(b$,6)="rocket" Then
		Tpro\tail=Cpro_fire
	ElseIf Left(b$,11)="toxicrocket" Then
		Tpro\tail=Cpro_fire
	ElseIf Left(b$,6)="poison" Then
		Tpro\tail=Cpro_poison
	ElseIf Left(b$,5)="smoke" Then
		Tpro\tail=Cpro_smoke
	ElseIf Left(b$,6)="asmoke" Then
		Tpro\tail=Cpro_asmoke
		pro_add_style(Tpro,6)
	ElseIf Left(b$,8)="asparkle" Then
		Tpro\tail=Cpro_asparkle
		pro_add_style(Tpro,8)
	ElseIf Left(b$,13)="asupersparkle" Then
		Tpro\tail=Cpro_asupersparkle
		pro_add_style(Tpro,13)
		If Tpro\tset#[0]<=0 Then Tpro\tset#[0]=3
	ElseIf Left(b$,8)="aresfade" Then
		Tpro\tail=Cpro_aresfade
		pro_add_style(Tpro,8)
		If Tpro\tset#[0]<=0. Then Tpro\tset#[0]=0.15
		If Tpro\tset#[1]=0. Then Tpro\tset#[1]=0.2
	EndIf
	;Deplete Fire in Water
	If Tpro\tail=Cpro_fire Then
		If Tpro\water=1 Then
			Tpro\tail=Cpro_smoke
		EndIf
	EndIf
		
	;Okay
	TCpro.Tpro=Tpro
	Return 1
End Function



Function pro_ini()
	If TCpro\weapon>0 Then TCpro\speed#=Ditem_speed#(TCpro\weapon)
	TCpro\spitch#=EntityPitch(TCpro\h)
	TCpro\sx#=Sin(TCpro\spitch#)*TCpro\speed#
	TCpro\sy#=-Cos(TCpro\spitch#)*TCpro\speed#
End Function


;### Projectile Style
Function pro_add_style(Tpro.Tpro,offset)
	txt$=Mid(Ditem_behaviour$(Tpro\typ),offset+1,-1)
	If txt$="" Then Return
	If Left(txt$,1)=":" Then
		txt$=Mid(txt$,2,-1)
		;DebugLog "get colors out of "+txt$
		split$(txt$,",",5)
		;Red
		If splits$(0)<>"" Then
			Tpro\tcol[0]=Int(splits$(0))
			;Green
			If splits$(1)<>"" Then
				Tpro\tcol[1]=Int(splits$(1))
				;Blue
				If splits$(2)<>"" Then
					Tpro\tcol[2]=Int(splits$(2))
					;Blend
					If splits$(3)<>"" Then
						Tpro\tfx=Int(splits$(3))
						;Set 0
						If splits$(4)<>"" Then
							Tpro\tset#[0]=Float(splits$(4))
							;Set 1
							If splits$(5)<>"" Then
								Tpro\tset#[1]=Float(splits$(5))
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	;DebugLog Tpro\tcol[0]+","+Tpro\tcol[1]+","+Tpro\tcol[2]+" PAR COL!"
End Function


;### Projectile Update
Function pro_update()
	For Tpro.Tpro=Each Tpro
		
		;Count Collisions
		cc=CountCollisions(Tpro\h)
		
		If cc=0 Then
			;Move
			If Tpro\weapon>0 Then
				MoveEntity Tpro\h,0,0,Ditem_speed#(Tpro\weapon)*f#
			Else
				MoveEntity Tpro\h,0,0,Tpro\speed#*f#
			EndIf
			;TranslateEntity Tpro\h,Sin(Tpro\sx#*f#),Tpro\sy#*f#,Sin(Tpro\sx#*f#)
			
			;Drag (rotate downwards to simulate gravity)
			turn=0
			If Tpro\weapon=0 Then
				opitch#=EntityPitch(Tpro\h)
				TurnEntity Tpro\h,(Tpro\drag#+Ditem_drag#(Tpro\typ))*f#,0,0
				turn=1
			Else
				If Tpro\weapon<>Tpro\typ Then
					opitch#=EntityPitch(Tpro\h)
					TurnEntity Tpro\h,(Ditem_drag#(Tpro\weapon)+Ditem_drag#(Tpro\typ))*f#,0,0
					turn=1
				Else
					opitch#=EntityPitch(Tpro\h)
					TurnEntity Tpro\h,Ditem_drag#(Tpro\typ)*f#,0,0
					turn=1
				EndIf
			EndIf
			If turn=1 Then
				If EntityPitch(Tpro\h)>88. Then RotateEntity Tpro\h,88,EntityYaw(Tpro\h),0
			EndIf
			
			;Rotate (around own axis)
			TurnEntity Tpro\mh,0,0,10*f#
			
			;Water
			;Over Water
			If Tpro\water=0 Then
				If EntityY(Tpro\h)<0 Then
					Tpro\water=1
					p_add(EntityX(Tpro\h),1,EntityZ(Tpro\h),Cp_rwave,Rnd(5,10),Rnd(0.9,1.5))
					p_add(EntityX(Tpro\h),1,EntityZ(Tpro\h),Cp_splash,Rnd(15,20),1.)
					sfx_emit(sfx_blubb,Tpro\h)
					;Extinguish Fire Tail
					If Tpro\tail=Cpro_fire Then
						Tpro\tail=Cpro_smoke
						sfx_emit(sfx_fizzle,Tpro\h)
						p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(3,5),Rnd(0.6,0.9))
						EntityColor TCp\h,240,240,240
						EntityBlend TCp\h,3
					EndIf
				EndIf
			;Under Water
			Else
				Tpro\wc=1
				If set_effects>0 Then
					If in_gt20go Then
						If Rand(2)=1 Then 
							p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_bubbles,Rnd(1,3),Rnd(0.3,0.5))
						EndIf
					EndIf
				EndIf
				If EntityY(Tpro\h)>0 Then
					Tpro\water=0
					p_add(EntityX(Tpro\h),1,EntityZ(Tpro\h),Cp_rwave,Rnd(5,10),Rnd(0.9,1.5))
					sfx_emit(sfx_blubb,TCp\h)
				EndIf
			EndIf
			
			;Tail
			If set_effects>0 Then
				If in_gt20go Then
					If Tpro\tail<>0 Then
						Select Tpro\tail
							;Smoke Tail
							Case Cpro_smoke
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(1,5),Rnd(0.6,0.9))
								TCp\r#=0.025
							;Fire Tail
							Case Cpro_fire
								If set_effects=2 Then
									If Rand(3)=1 Then
										p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_firespark,Rand(2,6),1)
									EndIf
								EndIf
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(3,5),Rnd(0.9,1.5))
								EntityColor TCp\h,255,Rand(50,255),0
								EntityBlend TCp\h,3
								TCp\r#=0.04
							;Poision Tail
							Case Cpro_poison
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(1,5),Rnd(0.9,1.5))
								EntityColor TCp\h,0,Rand(150,255),0
								EntityBlend TCp\h,3
								TCp\r#=0.02
							;Blood Tail
							Case Cpro_blood
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_subsplatter,Rnd(1,5),Rnd(0.9,1.5))
							;Wet Tail
							Case Cpro_wet
								;p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_spark,Rand(1,2),3)
								;EntityColor TCp\h,Rand(230,240),Rand(230,240),255
								;
								;PROBLEMS -> PARTICLE COLLIDES WITH PROJECTILE!
								;
							;Healing
							Case Cpro_healing
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_starflare,Rnd(1,5),Rnd(0.4,1))
								TranslateEntity TCp\h,Rnd(-5,5),Rnd(-5,5),Rnd(-5,5)
								EntityColor TCp\h,Rand(0,100),255,0
							;Tame
							Case Cpro_tame
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_starflare,Rnd(1,5),Rnd(0.4,1))
								TranslateEntity TCp\h,Rnd(-5,5),Rnd(-5,5),Rnd(-5,5)
								EntityColor TCp\h,255,75,Rnd(100,200)
							;Adjustable Smoke
							Case Cpro_asmoke
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(1,5),Rnd(0.6,0.9))
								EntityColor TCp\h,Tpro\tcol[0],Tpro\tcol[1],Tpro\tcol[2]
								Select Tpro\tfx<>0
									Case 0 EntityBlend TCp\h,1
									Case 1 EntityBlend TCp\h,3
								End Select
							;Adjustable Sparkle
							Case Cpro_asparkle
								p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_starflare,Rnd(1,5),Rnd(0.9,1.4))
								TCp\r#=Rnd(0.01,0.02)
								EntityColor TCp\h,Tpro\tcol[0],Tpro\tcol[1],Tpro\tcol[2]
								Select Tpro\tfx<>0
									Case 0 EntityBlend TCp\h,1
									Case 1 EntityBlend TCp\h,3
								End Select
							;Adjustable SUPER Sparkle! YAY!
							Case Cpro_asupersparkle
								For i=1 To Int(Tpro\tset#[0])
									p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_starflare,Rnd(1,5),Rnd(0.9,1.4))
									TCp\r#=Rnd(0.02,0.04)
									TranslateEntity TCp\h,Rnd(-5,5),Rnd(-5,5),Rnd(-5,5)
									EntityColor TCp\h,Tpro\tcol[0],Tpro\tcol[1],Tpro\tcol[2]
									Select Tpro\tfx<>0
										Case 0 EntityBlend TCp\h,1
										Case 1 EntityBlend TCp\h,3
									End Select
								Next
							;Adjustable Resize Fade Tail
							Case Cpro_aresfade
								ft=0
								ft=CopyEntity(Tpro\mh)
								If ft<>0 Then
									PositionEntity ft,EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h)
									TurnEntity ft,EntityPitch(Tpro\h),EntityYaw(Tpro\h),EntityRoll(Tpro\mh),1
									;Fadespeed based on Effect Settings
									tmp_ph=ft
									p_add(0,Tpro\tset#[0],Tpro\tset#[1],Cp_resfade)
									EntityColor TCp\h,Tpro\tcol[0],Tpro\tcol[1],Tpro\tcol[2]
									EntityAlpha TCp\h,Ditem_alpha#(Tpro\typ)
									TCp\a#=Ditem_alpha#(Tpro\typ)
									TCp\fx#=Ditem_size#(Tpro\typ,0)
									TCp\fy#=Ditem_size#(Tpro\typ,1)
									TCp\fz#=Ditem_size#(Tpro\typ,2)
									ScaleEntity TCp\h,TCp\fx#,TCp\fy#,TCp\fz#
								EndIf
						End Select
					Else
						;Model Tail
						ft=0
						ft=CopyEntity(Tpro\mh)
						If ft<>0 Then
							PositionEntity ft,EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h)
							TurnEntity ft,EntityPitch(Tpro\h),EntityYaw(Tpro\h),EntityRoll(Tpro\mh),1
							EntityBlend ft,3
							EntityColor ft,255,255,255
							EntityAlpha ft,0.3
							;Fadespeed based on Effect Settings
							tmp_ph=ft
							If set_effects=1 Then
								p_add(0,0.3,0.025,Cp_fadeout)
							Else
								p_add(0,0.3,0.018,Cp_fadeout)
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		;Collision
		If cc=0 Then
			If e_tery(EntityX(Tpro\h),EntityZ(Tpro\h))>EntityY(Tpro\h) Then
				cc=-1
			EndIf
		EndIf
		If cc<>0 Then
			;Behaviour Var
			b$=Ditem_behaviour$(Tpro\typ)
			;(Script) Vars
			If Tpro\weapon=Tpro\typ Then
				pv_attack_damage#=Ditem_damage(Tpro\weapon)
			Else
				pv_attack_damage#=Float(Ditem_damage(Tpro\weapon))*Float(Ditem_damage(Tpro\typ))
			EndIf
			If Tpro\weapon=0 Then
				pv_attack_damage#=Tpro\damage#*Float(Ditem_damage(Tpro\typ))
			EndIf
			;if_msg(pv_attack_damage#)
			pv_attack_weapon=Tpro\weapon
			pv_attack_ammo=Tpro\typ
			;Pick Vars
			in_px#=EntityX(Tpro\h)
			in_py#=EntityY(Tpro\h)
			in_pz#=EntityZ(Tpro\h)
			;No Impact Damage?
			noimpactdamage=0
			If Left(b$,6)="rocket" Then
				noimpactdamage=1
			ElseIf Left(b$,11)="toxicrocket" Then
				noimpactdamage=1
			EndIf
			;Scan Collisions
			If cc>0 Then
				pv_impact_num=0
				pv_impact_amount=cc
				For i=1 To cc
					pv_impact_num=i
					h=CollisionEntity(Tpro\h,i)
					
					;Attach States to Object which was hit!
					;If Left(b$,4)="fire" Then
					;	attach_state(h,Cstate_fire)
					;ElseIf Left(b$,6)="poison" Then
					;	attach_state(h,Cstate_intoxication,Cclass_unit)
					;EndIf
					
					;State (by weapon)
					If Ditem_wstate(pv_attack_weapon)<>0 Then
						If Not state_depleted(Ditem_wstate(pv_attack_weapon),Tpro\wc) Then
							attach_state(h,Ditem_wstate(pv_attack_weapon))
						EndIf
					EndIf
					;State (by ammo)
					If pv_attack_ammo<>pv_attack_weapon Then
						If Ditem_wstate(pv_attack_ammo)<>0 Then
							If Not state_depleted(Ditem_wstate(pv_attack_ammo),Tpro\wc) Then
								attach_state(h,Ditem_wstate(pv_attack_ammo))
							EndIf
						EndIf	
					EndIf
					
					;Collision / Damage
					pv_impact_class=0
					pv_impact_id=0
					pv_impact_kill=0
					pv_impact_x#=CollisionX(Tpro\h,i)
					pv_impact_y#=CollisionY(Tpro\h,i)
					pv_impact_z#=CollisionZ(Tpro\h,i)
					pv_impact_ground=0
					;Damage
					If noimpactdamage=1 Then
						tmpdmg#=pv_attack_damage#
						pv_attack_damage#=0
					EndIf
					If game_damage(h,pv_attack_damage#,Tpro\spawner,1,1) Then
						pv_impact_class=tmp_class
						pv_impact_id=tmp_id
						pv_impact_kill=tmp_kill
						;Projectile Stuck Particle
						If set_debug=1 Then
							If i=1 Then
								p_projectile(Tpro\mh,Tpro\typ,tmp_class,tmp_id)
							EndIf
						EndIf
						;Impact Event at Weapon which fired projectile
						If get_stored_item(pv_attack_weapon,Cclass_unit,g_player) Then
							set_parsecache(Cclass_item,TCitem\id,"impact")
							If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
								parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
							EndIf
						Else
							set_parsecache(-2,0,"impact")
							If Instr(Ditem_scriptk$(pv_attack_weapon),"Śimpact") Then
								parse_task(-2,0,"impact","",Ditem_script(pv_attack_weapon))
							EndIf
						EndIf
						;Impact Event at Projectile Itself
						If pv_attack_ammo<>pv_attack_weapon Then
							If get_stored_item(pv_attack_ammo,Cclass_unit,g_player) Then
								set_parsecache(Cclass_item,TCitem\id,"impact")
								If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
									parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
								EndIf
							Else
								set_parsecache(-2,0,"impact")
								If Instr(Ditem_scriptk$(pv_attack_ammo),"Śimpact") Then
									parse_task(-2,0,"impact","",Ditem_script(pv_attack_ammo))
								EndIf
							EndIf
						EndIf
					EndIf
					If noimpactdamage=1 Then pv_attack_damage#=tmpdmg#
				Next
			EndIf
			
			;Ground Collision Event
			If cc=-1 Then
				pv_impact_class=0
				pv_impact_id=0
				pv_impact_kill=0
				pv_impact_num=1
				pv_impact_amount=1
				pv_impact_x#=EntityX(Tpro\h)
				pv_impact_y#=EntityY(Tpro\h)
				pv_impact_z#=EntityZ(Tpro\h)
				pv_impact_ground=1
				If get_stored_item(pv_attack_weapon,Cclass_unit,g_player) Then
					set_parsecache(Cclass_item,TCitem\id,"impact")
					If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
						parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
					EndIf
				Else
					set_parsecache(-2,0,"impact")
					If Instr(Ditem_scriptk$(pv_attack_weapon),"Śimpact") Then
						parse_task(-2,0,"impact","",Ditem_script(pv_attack_weapon))
					EndIf
				EndIf
				If pv_attack_ammo<>pv_attack_weapon Then
					If get_stored_item(pv_attack_ammo,Cclass_unit,g_player) Then
						set_parsecache(Cclass_item,TCitem\id,"impact")
						If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
							parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
						EndIf
					Else
						set_parsecache(-2,0,"impact")
						If Instr(Ditem_scriptk$(pv_attack_ammo),"Śimpact") Then
						parse_task(-2,0,"impact","",Ditem_script(pv_attack_ammo))
						EndIf
					EndIf
				EndIf
			EndIf
			
			;Item FX for Projectile
			material_fx(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Ditem_mat(Tpro\typ))
			
			;Behaviour Stuff
			;Rocket			
			If Left(b$,6)="rocket" Then
				;FX / Damage
				game_explosion(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),100,pv_attack_damage#,1)
			;Toxicrocket
			ElseIf Left(b$,11)="toxicrocket" Then
				;FX
				For i=1 To 3
					p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_explode,Rnd(2,5),Rnd(1,1.5))
					EntityColor TCp\h,0,Rand(150,255),0
				Next
				For i=0 To Rand(3,5)
					p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_spark,Rand(1,2),3)
					EntityColor TCp\h,0,255,0
				Next
				p_add(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),Cp_smoke,Rnd(3,5),Rnd(1,1.5))
				EntityColor TCp\h,0,Rand(150,255),0
				EntityBlend TCp\h,3
				sfx_3d(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),sfx_explode(Rand(0,3)))
				;Damage
				game_explosion(EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),100,pv_attack_damage#,0)
				;Intoxication State
				For Tunit.Tunit=Each Tunit
					If EntityDistance(Tunit\h,Tpro\h)<100 Then
						set_state(Cstate_intoxication,Cclass_unit,Tunit\id)
					EndIf
				Next
			;Throw
			ElseIf Left(b$,5)="throw" Then
				;Spawn Item
				If p_skipevent=0 Then
					set_item(-1,Tpro\typ,EntityX(Tpro\h),EntityY(Tpro\h),EntityZ(Tpro\h),1)
					;Event: on:drop
					p_skipevent=0
					If TCitem<>Null Then
						If set_parsecache(Cclass_item,TCitem\id,"drop") Then parse()
						If Instr(Ditem_scriptk$(TCitem\typ),"Śdrop") Then
							parse_env(Cclass_item,TCitem\id,"drop")
							loadstring_parsecache(Ditem_script(TCitem\typ))
							parse()
						EndIf
					EndIf
				EndIf
			;Kill Throw
			ElseIf Left(b$,9)="killthrow" Then
				;Nothing!
			EndIf
			
			;State Stuff
			;by weapon
			If Ditem_wstate(pv_attack_weapon)<>0 Then
				If Not state_depleted(Ditem_wstate(pv_attack_weapon),Tpro\wc) Then
					state_impactfx(Ditem_wstate(pv_attack_weapon),Tpro\h)
				EndIf
			EndIf
			;by ammo
			If pv_attack_ammo<>pv_attack_weapon
				If Ditem_wstate(pv_attack_ammo)<>0 Then
					If Not state_depleted(Ditem_wstate(pv_attack_ammo),Tpro\wc) Then
						state_impactfx(Ditem_wstate(pv_attack_ammo),Tpro\h)
					EndIf
				EndIf
			EndIf
			
			;Free
			FreeEntity Tpro\mh
			FreeEntity Tpro\h
			Delete Tpro
			
		;Age Timeout
		ElseIf gt-Tpro\age>(game_projectiletimeout) Then
		
			;Free
			FreeEntity Tpro\mh
			FreeEntity Tpro\h
			Delete Tpro
			
		EndIf
	Next
	
End Function


;### Projectile add Position
Function pro_add_script(typ,x#,y#,z#,pitch#,yaw#,offset,weapon=0,speed#=1,damage#=1,drag#=1)
	If pro_add(0,typ,0,0) Then
		
		;Disable Collisions
		EntityType TCpro\h,0
		
		;Positioning/Rotating
		EntityParent TCpro\mh,0
		PositionEntity TCpro\mh,x#,y#,z#
		RotateEntity TCpro\mh,pitch#,yaw#,0
		PositionEntity TCpro\h,x#,y#,z#
		RotateEntity TCpro\h,pitch#,yaw#,0
		EntityParent TCpro\mh,TCpro\h
		TCpro\spitch#=pitch#
		
		;Offset
		MoveEntity TCpro\h,0,0,offset
		
		;Enable Collisions
		EntityType TCpro\h,Cworld_procol
		
		;Set Values
		TCpro\weapon=weapon
		TCpro\speed#=speed#
		TCpro\damage#=damage#
		TCpro\drag#=drag#
		
		pro_ini()
		
		Return 1
	Else
		Return 0
	EndIf
End Function
