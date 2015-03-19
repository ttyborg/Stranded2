;############################################ GAME Weapons

;None
Function game_wpn_none(player)
	If con_unit(player) Then
		p_skipevent=0
		parse_globalevent("usehand")
		parse_sel_event("usehand")
		If p_skipevent=0 Then
	
			If gt-TCunit\player_lastattack<400 Then Return 0
			TCunit\player_lastattack=gt
			
			;Exhaust
			game_exhaust(game_exh_attack#(0),game_exh_attack#(1),game_exh_attack#(2))
			
			;Pick
			game_wpnfunc_pick(cam,Dunit_attackrange(1),Dunit_damage#(1),player)
			If in_pe<>0 Then
				If get_object(in_pe)<>-1 Then find_object(TCobject\id)
			EndIf
		
		EndIf
			
		;Okay
		Return 1
	EndIf
End Function


;Blade
Function game_wpn_blade(player,range#=50)
	If con_unit(player) Then
		speed=Ditem_rate(TCunit\player_weapon)
		damage#=Ditem_damage(TCunit\player_weapon)
		If gt-TCunit\player_lastattack<speed Then Return 0
		TCunit\player_lastattack=gt
		
		;Exhaust
		game_exhaust(game_exh_attack#(0),game_exh_attack#(1),game_exh_attack#(2))
		
		;Pick
		game_wpnfunc_pick(cam,range#,damage#,player)
		If in_pe<>0 Then
			If get_object(in_pe)<>-1 Then find_object(TCobject\id)
		EndIf
			
		;Okay
		Return 1
	EndIf
End Function


;Distance
Function game_wpn_distance(player)
	If con_unit(player) Then
		speed=Ditem_rate(TCunit\player_weapon)
		If gt-TCunit\player_lastattack<speed Then Return 0
		TCunit\player_lastattack=gt
		
		;Ammo
		Local gotammo=0
		For Titem.Titem=Each Titem
			If Titem\parent_class=Cclass_unit Then
				If Titem\parent_id=TCunit\id Then
					If Titem\typ=TCunit\player_ammo Then
						;Decrease 1
						free_item(Titem\id,1)
						;Found Ammo!
						gotammo=1
					EndIf
				EndIf
			EndIf
		Next
		;Spawn Projectile
		If gotammo Then 
			pro_add(player,TCunit\player_ammo,EntityPitch(cam),EntityYaw(cam))
			pro_ini()
			Return 1
		Else
			;Noammo
			If get_stored_item(g_cplayer\player_weapon,Cclass_unit,g_cplayer\id)
				If Instr(Ditem_scriptk$(TCitem\typ),"Śnoammo") Then
					parse_task(Cclass_item,TCitem\id,"noammo","",Ditem_script(TCitem\typ))
				EndIf
			EndIf
			Return 0
		EndIf
	EndIf
End Function


;Distance (Fire)
Function game_wpn_distancef(player)
	If con_unit(player) Then
		speed=Ditem_rate(TCunit\player_weapon)
		If gt-TCunit\player_lastattack<speed Then Return 0
		TCunit\player_lastattack=gt
		
		;Ammo
		Local gotammo=0
		For Titem.Titem=Each Titem
			If Titem\parent_class=Cclass_unit Then
				If Titem\parent_id=TCunit\id Then
					If Titem\typ=TCunit\player_ammo Then
						;Decrease 1
						free_item(Titem\id,1)
						;Found Ammo!
						gotammo=1
					EndIf
				EndIf
			EndIf
		Next
		;Fire
		If gotammo Then
			;Script Vars
			pv_attack_damage#=Float(Ditem_damage(g_cplayer\player_weapon))*Float(Ditem_damage(g_cplayer\player_ammo))
			pv_attack_weapon=g_cplayer\player_weapon
			pv_attack_ammo=g_cplayer\player_ammo
		
			;Start
			Local h=CreatePivot()
			RotateEntity h,EntityPitch(cam),EntityYaw(cam),0
			PositionEntity h,EntityX(TCunit\h),EntityY(TCunit\h)+Dunit_eyes#(TCunit\typ),EntityZ(TCunit\h)
			If g_drive<>0 Then
				PositionEntity h,EntityX(cam),EntityY(cam),EntityZ(cam)
			EndIf
			MoveEntity h,0,0,Dunit_colxr#(TCunit\typ)+5.
			;Pick
			in_pe=0
			EntityPickMode env_sea2,0
			EntityPickMode g_cplayer\h,0
			in_pe=EntityPick(h,Ditem_speed#(TCunit\player_weapon))
			EntityPickMode g_cplayer\h,2,1
			in_px#=PickedX()
			in_py#=PickedY()
			in_pz#=PickedZ()
			
			;Muzzle Smoke
			If set_effects>0 Then
				For i=1 To 5
					p_add(EntityX(h)+Rnd(-3,3),EntityY(h)+Rnd(-3,3),EntityZ(h)+Rnd(-3,3),Cp_smoke,Rnd(5,7),Rnd(0.15,0.4))
				Next
			EndIf
			
			;Water
			If (EntityY(h)<0)Or(in_py#<0) Then
				Local water=0
				Local target=CreatePivot()
				Local sx#,sy#,sz#
				sx#=EntityX(h)
				sy#=EntityY(h)
				sz#=EntityZ(h)
				Local cx#,cy#,cz#
				PositionEntity target,in_px#,in_py#,in_pz#
				If EntityY(h)<0 Then water=1
				For i=1 To 10000
					MoveEntity h,0,0,10
					;Over Water
					If water=0 Then
						If EntityY(h)<0 Then
							water=1
							p_add(EntityX(h),1,EntityZ(h),Cp_rwave,Rnd(5,10),Rnd(0.9,1.5))
							p_add(EntityX(h),1,EntityZ(h),Cp_splash,Rnd(15,20),1.)
							cx#=EntityX(h)
							cy#=EntityY(h)
							cz#=EntityZ(h)
						EndIf
					;Under Water
					Else
						If Rand(2)=1 Then 
							p_add(EntityX(h),EntityY(h),EntityZ(h),Cp_bubbles,Rnd(1,3),Rnd(0.3,0.5))
						EndIf
						If EntityY(h)>0 Then
							water=0
							p_add(EntityX(h),1,EntityZ(h),Cp_rwave,Rnd(5,10),Rnd(0.9,1.5))
							cx#=EntityX(h)
							cy#=EntityY(h)
							cz#=EntityZ(h)
							Exit
						EndIf
					EndIf
					;Exit Loop
					If EntityDistance(h,target)<10 Then Exit
				Next
				;Line
				If sy#>0 Then
					lineh=meshline(cx#,cy#,cz#,in_px#,in_py#,in_pz#,0.4)
				Else
					If water=0 Then
						lineh=meshline(sx#,sy#,sz#,cx#,cy#,cz#,0.4)
					Else
						lineh=meshline(sx#,sy#,sz#,in_px#,in_py#,in_pz#,0.4)
					EndIf
				EndIf
				If lineh<>0 Then
					EntityBlend lineh,3
					tmp_ph=lineh
					p_add(0,0.2,0.011,Cp_fadeout)
				EndIf
				
				FreeEntity target
			EndIf
			
			;Free Pivot
			FreeEntity h				

			;Damage
			If in_pe<>0 Then
				;Terrain
				If in_pe=e_ter Then
					p_add(in_px#,in_py#,in_pz#,Cp_smoke,Rnd(5,10),Rnd(0.3,1.5))
					pv_impact_class=0
					pv_impact_id=0
					pv_impact_kill=0
					pv_impact_num=1
					pv_impact_amount=1
					pv_impact_x#=in_px#
					pv_impact_y#=in_py#
					pv_impact_z#=in_pz#
					pv_impact_ground=1
					If get_stored_item(g_cplayer\player_weapon,Cclass_unit,g_player) Then
						set_parsecache(Cclass_item,TCitem\id,"impact")
						If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
							parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
						EndIf
					EndIf
					If get_stored_item(g_cplayer\player_ammo,Cclass_unit,g_player) Then
						set_parsecache(Cclass_item,TCitem\id,"impact")
						If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
							parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
						EndIf
					EndIf
				;Object?
				Else
					;State (by weapon)
					If Ditem_wstate(g_cplayer\player_weapon)<>0 Then
						attach_state(in_pe,Ditem_wstate(g_cplayer\player_weapon))
					EndIf
					;State (by ammo)
					If Ditem_wstate(g_cplayer\player_ammo)<>0 Then
						attach_state(in_pe,Ditem_wstate(g_cplayer\player_ammo))
					EndIf			
					;Damage
					pv_impact_class=0
					pv_impact_id=0
					pv_impact_kill=0
					pv_impact_num=1
					pv_impact_amount=1
					pv_impact_x#=in_px#
					pv_impact_y#=in_py#
					pv_impact_z#=in_pz#
					pv_impact_ground=0
					If game_damage(in_pe,pv_attack_damage#,player,1) Then
						pv_impact_class=tmp_class
						pv_impact_id=tmp_id
						pv_impact_kill=tmp_kill
						If get_stored_item(g_cplayer\player_weapon,Cclass_unit,g_player) Then
							set_parsecache(Cclass_item,TCitem\id,"impact")
							If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
								parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
							EndIf
						EndIf
						If get_stored_item(g_cplayer\player_ammo,Cclass_unit,g_player) Then
							set_parsecache(Cclass_item,TCitem\id,"impact")
							If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
								parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			Return 1
		Else
			;Noammo
			If get_stored_item(g_cplayer\player_weapon,Cclass_unit,g_cplayer\id)
				If Instr(Ditem_scriptk$(TCitem\typ),"Śnoammo") Then
					parse_task(Cclass_item,TCitem\id,"noammo","",Ditem_script(TCitem\typ))
				EndIf
			EndIf
			Return 0
		EndIf
		
	EndIf	
End Function


;Selfthrow
Function game_wpn_selfthrow(player)
	If con_unit(player) Then
		;Find
		For Titem.Titem=Each Titem
			If Titem\parent_class=Cclass_unit Then
				If Titem\parent_id=player Then
					If Titem\typ=TCunit\player_weapon Then
					
						;Stuff
						speed=Ditem_rate(TCunit\player_weapon)
						damage#=Ditem_damage(TCunit\player_weapon)
						If gt-TCunit\player_lastattack<speed Then Return 0
						TCunit\player_lastattack=gt
						
						;Exhaust
						game_exhaust(game_exh_attack#(0),game_exh_attack#(1),game_exh_attack#(2))
						
						;Dcrease
						free_item(Titem\id,1)

						;Spawn Projectile
						pro_add(player,TCunit\player_weapon,EntityPitch(cam),EntityYaw(cam))
						pro_ini()
							
						;Okay
						Return 1
						
					EndIf
				EndIf
			EndIf
		Next
	EndIf
End Function


;Weapon Function: Pick
Function game_wpnfunc_pick(h,range#,damage#,player)
	;Pick
	Local target=CreatePivot()
	PositionEntity target,EntityX(cam),EntityY(cam),EntityZ(cam)
	RotateEntity target,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
	MoveEntity target,0,0,range#
	EntityPickMode g_cplayer\h,0
	EntityPickMode env_sea2,0
	in_pe=0
	in_pe=LinePick(EntityX(cam),EntityY(cam),EntityZ(cam),EntityX(target)-EntityX(cam),EntityY(target)-EntityY(cam),EntityZ(target)-EntityZ(cam),Cworld_useradius)
	in_px#=PickedX()
	in_py#=PickedY()
	in_pz#=PickedZ()
	FreeEntity target
	EntityPickMode g_cplayer\h,2,1
	
	;Attack FX
	p_add(Rand(-1,2),0,0,Cp_attack,12,0.5)
	
	If in_pe<>0 Then
	
		;Script Vars
		pv_attack_damage#=damage#
		pv_attack_weapon=g_cplayer\player_weapon
		pv_attack_ammo=0
		
		;State
		If Ditem_wstate(g_cplayer\player_weapon)<>0 Then
			attach_state(in_pe,Ditem_wstate(g_cplayer\player_weapon),-1,0)
		EndIf
		
		;Damage
		pv_impact_class=0
		pv_impact_id=0
		pv_impact_kill=0
		pv_impact_num=1
		pv_impact_amount=1
		pv_impact_x#=in_px#
		pv_impact_y#=in_py#
		pv_impact_z#=in_pz#
		If in_pe=ter Then
			pv_impact_ground=1
		Else
			pv_impact_ground=0
		EndIf
		If game_damage(in_pe,damage#,player,1) Then
			;Impact FX
			p_add(in_px,in_py,in_pz,Cp_impact,Rnd(4.,12.),Rnd(0.4,0.6))
			EntityOrder TCp\h,-1
			;Event
			pv_impact_class=tmp_class
			pv_impact_id=tmp_id
			pv_impact_kill=tmp_kill
			If get_stored_item(g_cplayer\player_weapon,Cclass_unit,g_player) Then
				set_parsecache(Cclass_item,TCitem\id,"impact")
				If Instr(Ditem_scriptk$(TCitem\typ),"Śimpact") Then
					parse_task(Cclass_item,TCitem\id,"impact","",Ditem_script(TCitem\typ))
				EndIf
			EndIf
		EndIf
	
	EndIf
End Function
