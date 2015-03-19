;############################################ AI

;### Get Behaviour (int) out of Behaviour-String
Function ai_getbehaviour(behaviour$)
	Select Lower(behaviour$)
		Case "","player" Return 0
		
		;Land Stuff - 1
		Case "normal","animal" Return 1
		Case "raptor","predator" Return 2
		Case "standandsnap" Return 3
		Case "idle" Return 4
		Case "idleturn" Return 5
		Case "standandshoot" Return 6
		Case "monkey" Return 7
		Case "shy" Return 8
		Case "plague" Return 9
		Case "landbird" Return 10
		
		;Land/Water Stuff
		Case "crab" Return 200
		Case "amphibian" Return 201
		
		;Fish Stuff - 300
		Case "fish" Return 300
		Case "circlingfish" Return 301
		Case "predatorfish" Return 302
		Case "deepseafish" Return 303
		
		;Bird Stuff - 400
		Case "bird" Return 400
		Case "circlingbird" Return 401
		Case "predatorbird" Return 402
		Case "flyinginsect" Return 404
		Case "highbird" Return 405
		Case "lowbird" Return 406
		Case "landskybird" Return 407
		Case "killerbird" Return 408
		
		;Vehicle Stuff - 500
		Case "vehicle" Return 500
		Case "watercraft" Return 501
		Case "aircraft" Return 502
		
		Default
			If set_debug Then con_add("ERROR: Invalid unit behaviour '"+behaviour$+"'",Cbmpf_red)
			Return 0
	End Select
End Function


;### AI Agressive
Function ai_agressive(behaviour)
	Select behaviour
		Case 2 Return 1
		Case 3 Return 1
		Case 6 Return 1
		Case 302 Return 1
		Case 402 Return 1
		Case 408 Return 1
	End Select
	Return 0
End Function


;### Initialize AI Stuff (called in load_map function)
Function ai_ini(setupcenter=1)
	;Setup AI Center
	If setupcenter=1 Then
		PositionEntity TCunit\ai_ch,TCunit\ai_cx#,0,TCunit\ai_cz#
	EndIf
	
	;Setup Start Behaviour
	Select Dunit_behaviour(TCunit\typ)
		
		;300,301,302 - fish,predatorfish,deepseafish
		Case 300,302,303
			x=Rand(0,3)
			Select x
				Case 0 ai_mode(ai_move)
				Case 1 ai_mode(ai_movel)
				Case 2 ai_mode(ai_mover)
			End Select
		
		;301 - circlingfish,circlingbird
		Case 301,401
			x=Rand(0,1)
			Select x
				Case 0 ai_mode(ai_movel)
				Case 1 ai_mode(ai_mover)
			End Select
		
		
		;404 - Flying Insect
		Case 404
			x=Rand(0,3)
			Select x
				Case 0 ai_mode(ai_move)
				Case 1 ai_mode(ai_movel)
				Case 2 ai_mode(ai_mover)
			End Select
			
	End Select
	TCunit\ai_timer=gt
	
	;Dead?
	If TCunit\health<=0 Then
		TCunit\freeze=0
		TCunit\ani=Dunit_ani_die(TCunit\typ,1)
		Animate TCunit\mh,3,Dunit_ani_die(TCunit\typ,0),Dunit_ani_die(TCunit\typ,1)
		TCunit\health_max#=0.
		EntityType TCunit\h,0
		EntityPickMode TCunit\mh,2,1
	EndIf
End Function


;### AI Animate
Function ai_ani(typ)
	Select typ
		;Idle 1
		Case ani_idle1
			;Animate
			ani_unit(3,Dunit_ani_idle1(TCunit\typ,0),Dunit_ani_idle1(TCunit\typ,1))
			;Event
			set_parsecache(Cclass_unit,TCunit\id,"ai_idle1")
			If Instr(Dunit_scriptk$(TCunit\typ),"Śai_idle1") Then
				parse_task(Cclass_unit,TCunit\id,"ai_idle1","",Dunit_script(TCunit\typ))
			EndIf
		;Idle 2
		Case ani_idle2
			;Animate
			ani_unit(3,Dunit_ani_idle2(TCunit\typ,0),Dunit_ani_idle2(TCunit\typ,1))
			;Event
			set_parsecache(Cclass_unit,TCunit\id,"ai_idle2")
			If Instr(Dunit_scriptk$(TCunit\typ),"Śai_idle2") Then
				parse_task(Cclass_unit,TCunit\id,"ai_idle2","",Dunit_script(TCunit\typ))
			EndIf
		;Idle 3
		Case ani_idle3
			;Animate
			ani_unit(3,Dunit_ani_idle3(TCunit\typ,0),Dunit_ani_idle3(TCunit\typ,1))
			;Event
			set_parsecache(Cclass_unit,TCunit\id,"ai_idle3")
			If Instr(Dunit_scriptk$(TCunit\typ),"Śai_idle3") Then
				parse_task(Cclass_unit,TCunit\id,"ai_idle3","",Dunit_script(TCunit\typ))
			EndIf
		;Move
		Case ani_move
			If Dunit_loopmoveani(TCunit\typ)=1 Then
				ani_unit(1,Dunit_ani_move(TCunit\typ,0),Dunit_ani_move(TCunit\typ,1))
			Else
				ani_unit(2,Dunit_ani_move(TCunit\typ,0),Dunit_ani_move(TCunit\typ,1))
			EndIf
		;Attack
		Case ani_attack
			ani_unit(3,Dunit_ani_attack(TCunit\typ,0),Dunit_ani_attack(TCunit\typ,1),1.,1)
		;Use
		Case ani_use
			ani_unit(3,Dunit_ani_use(TCunit\typ,0),Dunit_ani_use(TCunit\typ,1))
		;Die
		Case ani_die
			ani_unit(3,Dunit_ani_die(TCunit\typ,0),Dunit_ani_die(TCunit\typ,1))
	End Select
End Function


;### Set AI Mode
Function ai_mode(mode,duration=0,setani=1)
	If duration=0 Then
		duration=Rand(1500,3500)
	EndIf
	TCunit\ai_mode=mode
	TCunit\ai_duration=duration
	TCunit\ai_timer=gt
	
	;Stay?
	If TCunit\states=1
		If mode<>ai_idle Then
			If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
				mode=ai_idle
			EndIf
		EndIf
	EndIf
	
	;Animation
	If setani=1 Then
		Select mode
			;Idle
			Case ai_idle
				Local playidle=0
				x=Rand(1,3)
				Select x
					Case 1
						If Dunit_ani_idle1(TCunit\typ,0)<>0. Then
							ai_ani(ani_idle1)
							play_soundset(Dunit_sfx(TCunit\typ),snd_idle1,TCunit\h)
							playidle=1
						EndIf
					Case 2
						If Dunit_ani_idle2(TCunit\typ,0)<>0. Then
							ai_ani(ani_idle2)
							play_soundset(Dunit_sfx(TCunit\typ),snd_idle2,TCunit\h)
							playidle=1
						EndIf
					Case 3
						If Dunit_ani_idle3(TCunit\typ,0)<>0. Then
							ai_ani(ani_idle3)
							play_soundset(Dunit_sfx(TCunit\typ),snd_idle3,TCunit\h)
							playidle=1
						EndIf
				End Select
				;Animation Failed?
				If playidle=0 Then
					Animate TCunit\mh,0
					TCunit\ani=0
					idleanis=0
					If Dunit_ani_idle1(TCunit\typ,0)<>0 Then idleanis=idleanis+1
					If Dunit_ani_idle2(TCunit\typ,0)<>0 Then idleanis=idleanis+1
					If Dunit_ani_idle3(TCunit\typ,0)<>0 Then idleanis=idleanis+1
					TCunit\ai_duration=0
				EndIf
			;Move
			Case ai_move,ai_movel,ai_mover
				ai_ani(ani_move)
			;Turn
			Case ai_turnl,ai_turnr
				ai_ani(ani_move)
			;Rise / Fall
			Case ai_rise,ai_fall
				ai_ani(ani_move)
			;Movetarget
			Case ai_movetarget
				ai_ani(ani_move)
			;Return
			Case ai_return,ai_sreturn
				ai_ani(ani_move)
			;Attack
			Case ai_attack
				ai_ani(ani_attack)
			;Hunt
			Case ai_hunt
				ai_ani(ani_move)
			;Get Food
			Case ai_getfood
				ai_ani(ani_move)
			;Flee
			Case ai_flee
				ai_ani(ani_move)
		End Select
	EndIf
End Function


;### Move
Function ai_move(speed#,diroff#,landcheck=0)
	;Speed Mod.
	If TCunit\states=1 Then
		For Tstate.Tstate=Each Tstate
			If Tstate\typ<=Cstate_count Then
				If Tstate\parent_class=Cclass_unit Then
					If Tstate\parent_id=TCunit\id Then
						If Dstate_a(Tstate\typ) Then
							Select Tstate\typ
								Case Cstate_frostbite speed#=speed#*0.5
								Case Cstate_fracture speed#=speed#*0.5
								Case Cstate_electroshock speed#=speed#*0.5
								Case Cstate_bloodrush speed#=speed#*Tstate\value_f#
								Case Cstate_speedmod speed#=Tstate\value_f# 
							End Select
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	EndIf
	;Move
	speed#=speed#*f#
	h=TCunit\h
	Local yaw#=EntityYaw(h)
	Local mx#=Cos(yaw#+diroff#)*speed#
	Local mz#=Sin(yaw#+diroff#)*speed#
	Local tery1#=e_tery(EntityX(h),EntityZ(h))
	Local tery2#=e_tery(EntityX(h)+mx#,EntityZ(h)+mz#)
	Local dif#=tery2#-tery1#
	
	;If dif#<(climbmax#*f#) Then		
		TranslateEntity h,mx#,0,mz#
		Return 1
	;EndIf
	
	;Landcheck
	;If landcheck<>0 Then
	;	Select landcheck
	;		;1 - No Water
	;		Case 1
	;		;2 - No Land
	;		Case 2
	;	End Select
	;EndIf
	
	Return 0
End Function


;### AI Check Stuff
Function ai_check(attitude=0,landcheck=1,centercheck=1)
	;Don't change mode during attacking-animation!
	If TCunit\ai_mode<>ai_attack Then
		
		;Land Check
		If landcheck=1 Then
			If EntityY(TCunit\mh,1)<0 Then
				;if_msg("water! "+EntityY(TCunit\mh,1))
				;p_add(EntityX(TCunit\h),EntityY(TCunit\h),EntityZ(TCunit\h),Cp_starflare,10,5)
				ai_mode(ai_sreturn)
				Return
			EndIf
		ElseIf landcheck=2 Then
			If EntityY(TCunit\mh,1)>-5 Then
				ai_mode(ai_sreturn)
				Return
			EndIf
		EndIf
		
		;Center Check
		If centercheck=1
			If TCunit\ai_mode<>ai_return Then
				If Sqr((EntityX(TCunit\h)-TCunit\ai_cx#)^2+(EntityZ(TCunit\h)-TCunit\ai_cz#)^2)>Dunit_range(TCunit\typ) Then
					If Dunit_behaviour(TCunit\typ)=408 Then
						If TCunit\ai_mode<>ai_hunt Then
							If TCunit\ai_mode<>ai_rise Then
								ai_mode(ai_return)
							EndIf
						EndIf
					Else
						ai_mode(ai_return)
					EndIf
				EndIf
			EndIf
		EndIf
		
		;Movement based on Player
		Select attitude
			;0 - Passive
			Case 0
			;1 - Shy
			Case 1
				If TCunit\ai_mode<>ai_flee Then
					For Tunit.Tunit=Each Tunit
						If Tunit\id=g_player Then
							If EntityDistance (Tunit\h,TCunit\h)<100 Then
								ai_mode(ai_flee,10000)
								TCunit\ai_target_class=Cclass_unit
								TCunit\ai_target_id=g_player
							EndIf
						EndIf
					Next
				Else
					TCunit\ai_duration=10000
					TCunit\ai_timer=gt
				EndIf
			;2 - Aggressive
			Case 2
				;Tame?
				tame=0
				If TCunit\states=1 Then
					If get_state(Cstate_tame,Cclass_unit,TCunit\id)=1 Then
						tame=1
					EndIf
				EndIf
			
				If tame=0 Then
					;No Unitpath?
					If TCunit\ai_mode<>-1 Then
						;Attack
						If TCunit\ai_mode<>ai_attack Then
							If TCunit\ai_mode<ail_superior Then
								;In Attack Range?
								If EntityDistance(TCunit\h,g_cplayer\h)<Dunit_attackrange(TCunit\typ) Then
									;Alive?
									If g_cplayer\health#>0. Then
										;Not Tame?
										doit=0
										
										;Check Line
										If checkattackline()=1 Then
											;Damage
											tempunit.Tunit=TCunit
											Select Dunit_behaviour(TCunit\typ)
												Case 0	game_damage(g_cplayer\mh,Dunit_damage#(TCunit\typ),TCunit\h):doit=1
												Case 6	ai_shoot():doit=1
												Case 302
													If EntityY(g_cplayer\h)<0 Then
														game_damage(g_cplayer\mh,Dunit_damage#(TCunit\typ),TCunit\h)
														doit=1
													EndIf
												Case 408
													If TCunit\ai_mode<>ai_rise Then
														If TCunit\ai_mode<>ai_sreturn And TCunit\ai_mode<>ai_return Then
															game_damage(g_cplayer\mh,Dunit_damage#(TCunit\typ),TCunit\h)
															doit=1
														EndIf
													EndIf
												Default	game_damage(g_cplayer\mh,Dunit_damage#(TCunit\typ),TCunit\h):doit=1
											End Select
											TCunit.Tunit=tempunit
											If tame=1 Then doit=0
											If doit=1 Then
												;Sound / FX
												play_soundset(Dunit_sfx(TCunit\typ),snd_attack,TCunit\h)
												ai_mode(ai_attack)
												;Event
												set_parsecache(Cclass_unit,TCunit\id,"ai_attack")
												If Instr(Dunit_scriptk$(TCunit\typ),"Śai_attack") Then
													parse_task(Cclass_unit,TCunit\id,"ai_attack","",Dunit_script(TCunit\typ))
												EndIf
												parse_sel(Cclass_unit,TCunit\id,"ai_attack")
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						;Hunt
						If TCunit\ai_mode<>ai_attack Then
							If TCunit\ai_mode<>ai_hunt Then
								If TCunit\ai_mode<ail_superior Then
									;In Perception Range?
									If EntityDistance(TCunit\h,g_cplayer\h)<Dunit_range(TCunit\typ) Then
										;Alive?
										If g_cplayer\health#>0. Then
											;Reachable?
											;Player is in Water
											If EntityY(g_cplayer\h)<0.1 Then
												If Dunit_behaviour(TCunit\typ)>=300 Then
													;Killerbird does not Attack Waterstuff!
													If Dunit_behaviour(TCunit\typ)<>408 Then
														ai_mode(ai_hunt,10000)
														If ms-TCunit\lastspot>5000 Then
															TCunit\lastspot=ms
															play_soundset(Dunit_sfx(TCunit\typ),snd_spot,TCunit\h)
														EndIf
													EndIf
												EndIf
											;Player is on Land
											Else
												If Dunit_behaviour(TCunit\typ)<300 Then
													ai_mode(ai_hunt,10000)
													If ms-TCunit\lastspot>5000 Then
														TCunit\lastspot=ms
														play_soundset(Dunit_sfx(TCunit\typ),snd_spot,TCunit\h)
													EndIf
												;Killerbird (408) Attack
												ElseIf Dunit_behaviour(TCunit\typ)=408 Then
													If TCunit\ai_mode<>ai_rise Then
														ai_mode(ai_hunt,10000)
														If ms-TCunit\lastspot>5000 Then
															TCunit\lastspot=ms
															play_soundset(Dunit_sfx(TCunit\typ),snd_spot,TCunit\h)
														EndIf
													EndIf
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				
		End Select
		
	EndIf
End Function


;### AI Return to Center
Function ai_return(water=0,turndelay#=30.)
	;Turn to Center
	RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,TCunit\ai_ch)/(turndelay#*f#)),0
	;Move towards Center
	If water=0 Then
		ai_move(Dunit_speed#(TCunit\typ),90)
	Else
		MoveEntity TCunit\h,0,0,unit_speed()*f#
	EndIf
	;Cancel Return Process at a low Distance
	If Sqr((EntityX(TCunit\h)-TCunit\ai_cx#)^2+(EntityZ(TCunit\h)-TCunit\ai_cz#)^2)<75 Then
		TCunit\ai_timer=0
	EndIf
End Function


;### AI Hunt Player
Function ai_hunt(water=0)
	;Cancel Process at a low Distance
	If Sqr((EntityX(TCunit\h)-EntityX(g_cplayer\h))^2+(EntityZ(TCunit\h)-EntityZ(g_cplayer\h))^2)<(Float(Dunit_attackrange(TCunit\typ))/2) Then
		TCunit\ai_timer=0
	Else
		;Turn to Player
		RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,g_cplayer\h)/(5.*f#)),0
		;Move towards Player
		If water=0 Then
			ai_move(Dunit_speed#(TCunit\typ),90)
		Else
			MoveEntity TCunit\h,0,0,unit_speed#()*f#
			If Abs(EntityY(TCunit\h)-EntityY(g_cplayer\h))>20. Then
				If EntityY(TCunit\h)>EntityY(g_cplayer\h) Then
					TranslateEntity TCunit\h,0,-unit_speed#()/3.*f#,0
				Else
					TranslateEntity TCunit\h,0,unit_speed#()/3.*f#,0
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;### AI Move to Target
Function ai_movetarget(water=0)
	;Get Target Handle
	Local target=parent_h(TCunit\ai_target_class,TCunit\ai_target_id)
	If target=0 Then
		TCunit\ai_timer=0
	Else
		;Cancel Move Target Process at a low Distance
		If Sqr((EntityX(TCunit\h)-EntityX(target))^2+(EntityZ(TCunit\h)-EntityZ(target))^2)<(10) Then
			TCunit\ai_timer=0
		Else
			;Turn to Target
			RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,target)/(5.*f#)),0
			;Move towards Target
			If water=0 Then
				ai_move(Dunit_speed#(TCunit\typ),90)
			Else
				MoveEntity TCunit\h,0,0,unit_speed#()*f#
				If Abs(EntityY(TCunit\h)-EntityY(g_cplayer\h))>20. Then
					If EntityY(TCunit\h)>EntityY(g_cplayer\h) Then
						TranslateEntity TCunit\h,0,-unit_speed#()/3.*f#,0
					Else
						TranslateEntity TCunit\h,0,unit_speed#()/3.*f#,0
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;### AI Get Food
Function ai_getfood(water=0)
	;Get Target Handle
	Local target=parent_h(TCunit\ai_target_class,TCunit\ai_target_id)
	If target=0 Then
		;Cancel if target is not found
		TCunit\ai_timer=0
		TCunit\ai_duration=0
	Else
		;Stored Item -> Cancel
		If TCunit\ai_target_class=Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=TCunit\ai_target_id Then
					If Titem\parent_class<>0 Then
						TCunit\ai_timer=0
						TCunit\ai_duration=0
						;con_add("target is stored")
						Return 0
					EndIf
				EndIf
			Next
		EndIf
		;Cancel Move Target Process at a low Distance (AND EAT!)
		If Sqr((EntityX(TCunit\h)-EntityX(target))^2+(EntityZ(TCunit\h)-EntityZ(target))^2)<(30) Then
			If TCunit\ani<>Dunit_ani_attack(TCunit\typ,1) Or Animating(TCunit\mh)=0 Then
				;EAT!
				TCunit\ai_mode=ai_idle
				TCunit\ai_duration=5000
				TCunit\ai_timer=gt
				ai_ani(ani_attack)
				play_soundset(Dunit_sfx(TCunit\typ),snd_attack,TCunit\h)
				sfx_emit sfx_eat,TCunit\h
				
				;Script
				pv_ai_eater=TCunit\id
				p_skipevent=0
				parse_emuevent(TCunit\ai_target_class,TCunit\ai_target_id,"ai_eat")
				parse_sel(TCunit\ai_target_class,TCunit\ai_target_id,"ai_eat")
				If p_skipevent=0 Then
					;Eat/Destory Food
					Select TCunit\ai_target_class
						;Object
						Case Cclass_object
							If con_object(TCunit\ai_target_id) Then
								damage_object(Dunit_damage(TCunit\typ))
								If con_object(TCunit\ai_target_id) Then
									TCunit\ai_mode=ai_getfood
									TCunit\ai_duration=20000
									TCunit\ai_timer=gt
								EndIf 
							EndIf
						;Unit
						Case Cclass_unit
							Local temp.Tunit=TCunit
							If con_unit(TCunit\ai_target_id) Then kill_unit()
							TCunit.Tunit=temp
						;Item
						Case Cclass_item
							If con_item(TCunit\ai_target_id) Then
								damage_item(Dunit_damage(TCunit\typ),1)
								If con_item(TCunit\ai_target_id) Then 
									TCunit\ai_mode=ai_getfood
									TCunit\ai_duration=20000
									TCunit\ai_timer=gt
								EndIf
							EndIf
						;Info
						Case Cclass_info
							free_info(TCunit\ai_target_id)
					End Select
				EndIf
			EndIf
		Else
			;Turn to Target
			RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,target)/(5.*f#)),0
			;Move towards Target
			If water=0 Then
				ai_move(Dunit_speed#(TCunit\typ),90)
			Else
				MoveEntity TCunit\h,0,0,unit_speed#()*f#
				If Abs(EntityY(TCunit\h)-EntityY(g_cplayer\h))>20. Then
					If EntityY(TCunit\h)>EntityY(g_cplayer\h) Then
						TranslateEntity TCunit\h,0,-unit_speed#()/3.*f#,0
					Else
						TranslateEntity TCunit\h,0,unit_speed#()/3.*f#,0
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;### AI Flee
Function ai_flee(water=0)
	;Get Target Handle
	Local target=parent_h(TCunit\ai_target_class,TCunit\ai_target_id)
	If target=0 Then
		TCunit\ai_timer=0
	Else
		;Cancel Flee Process at a high Distance
		If Sqr((EntityX(TCunit\h)-EntityX(target))^2+(EntityZ(TCunit\h)-EntityZ(target))^2)>(50) Then
			If TCunit\ai_duration<>3000 Then
				TCunit\ai_duration=3000
				TCunit\ai_timer=gt
			EndIf
		EndIf
		
		;Flee Pivot
		Local fp=CreatePivot()
		PositionEntity fp,EntityX(target),EntityY(target),EntityZ(target)
		RotateEntity fp,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,target))+180,0
		MoveEntity fp,0,0,300.
		
		;Turn away from Target
		RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,fp)/(10.*f#)),0
		;Move away from Target
		If water=0 Then
			;Land
			ai_move(Dunit_speed#(TCunit\typ),90)
		ElseIf water=-1 Then
			;Sky
			MoveEntity TCunit\h,0,0,unit_speed#()*f#*4.0
			TranslateEntity TCunit\h,0,unit_speed#()*f#*0.5,0
		Else
			;Water
			MoveEntity TCunit\h,0,0,unit_speed#()*f#
			;Change Height
			If Abs(EntityY(TCunit\h)-EntityY(g_cplayer\h))>20. Then
				If EntityY(TCunit\h)>EntityY(g_cplayer\h) Then
					TranslateEntity TCunit\h,0,-unit_speed#()/3.*f#,0
				Else
					TranslateEntity TCunit\h,0,unit_speed#()/3.*f#,0
				EndIf
			EndIf
			;Collision?
			If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
				TurnEntity TCunit\h,0,unit_speed#()*f#,0
				ai_watertest()
			ElseIf EntityCollided(TCunit\h,Cworld_col) Then
				TurnEntity TCunit\h,0,unit_speed#()*f#,0
			EndIf
		EndIf	
		;Free Flee Pivot
		FreeEntity fp
	EndIf
End Function


;### AI Attack
Function ai_attack(turndelay#=30.)
	;Turn to Target
	RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,g_cplayer\h)/(5.*f#)),0
	;Keep mode while animating
	If Animating(TCunit\mh) Then
		;Keep mode
		TCunit\ai_timer=gt+10000
	Else
		;Attacking Finished, go on with hunting or attacking, based on distance
		TCunit\ai_mode=0
		TCunit\ai_timer=0
		TCunit\ai_duration=-1
		ai_check(ai_agressive)
	EndIf
End Function


;### AI Water Test (only swim on if water is deeper in this direction)
Function ai_watertest()
	ty#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
	MoveEntity TCunit\h,0,0,unit_speed#()*f#
	If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))>ty# Then
		MoveEntity TCunit\h,0,0,-unit_speed#()*f#
		Return 1
	Else
		Return 0
	EndIf
End Function


;### AI Shoot
Function ai_shoot()
	;Target
	t=CreatePivot()
	PositionEntity t,EntityX(g_cplayer\h),EntityY(g_cplayer\h),EntityZ(g_cplayer\h)
	;Start
	Local h=CreatePivot()
	PositionEntity h,EntityX(TCunit\h),EntityY(TCunit\h)+Dunit_eyes#(TCunit\typ),EntityZ(TCunit\h)
	PointEntity h,t
	FreeEntity t
	MoveEntity h,0,0,Dunit_colxr#(TCunit\typ)+5.
	;Pick
	in_pe=0
	EntityPickMode env_sea2,0
	EntityPickMode TCunit\h,0
	ShowEntity g_cplayer\mh
	in_pe=EntityPick(h,Dunit_attackrange(TCunit\typ))
	EntityPickMode TCunit\h,2,1
	HideEntity g_cplayer\mh
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
			;if_msg("Terrain: "+in_pe)
		;Object?
		Else
			Local tempunit.Tunit=TCunit
			game_damage(in_pe,Dunit_damage#(TCunit\typ),TCunit\id,1)
			TCunit.Tunit=tempunit
			;if_msg(in_pe)
		EndIf
		Return 1
	Else
		Return 0
		;if_msg("no pick")
	EndIf
End Function


;### Obstacle Check
Function ai_obstacle()
	;if_msg(EntityCollided(TCunit\h,Cworld_col))
	If EntityCollided(TCunit\h,Cworld_col) Then Return 1
	Local c=CountCollisions(TCunit\h)
	If c>0 Then
		Return 1
		Local i
		Local y#=EntityY(TCunit\h)-(Dunit_colyr#(TCunit\h)/2.)
		For i=1 To c
			If CollisionY(TCunit\h,i)>y# Then Return 1
		Next
	EndIf
	Return 0
End Function


;### Turn Away
Function ai_turn()
	If Rand(1,2)=1 Then
		ai_mode(ai_movel)
	Else
		ai_mode(ai_mover)
	EndIf
End Function


;### Scan Environment for Free Direction
Function ai_scanenv()
	p=CreatePivot()
	HideEntity TCunit\h
	EntityPickMode ter,0
	For angle=0 To 270 Step 45
		PositionEntity p,EntityX(TCunit\h),EntityY(TCunit\h),EntityZ(TCunit\h)
		RotateEntity p,0,angle,0
		MoveEntity p,0,0,Dunit_colxr#(TCunit\typ)
		;If LinePick (EntityX(p),EntityY(p),EntityZ(p),1,1,1,15)=0 Then
		If EntityPick(p,300)=0 Then
			RotateEntity TCunit\h,0,angle,0
			p_add(EntityX(p),EntityY(p),EntityZ(p),Cp_debug)
			EntityColor TCp\h,0,255,0
		Else
			p_add(EntityX(p),EntityY(p),EntityZ(p),Cp_debug)
			EntityColor TCp\h,255,0,0
		EndIf
	Next
	EntityPickMode ter,2,1
	ShowEntity TCunit\h
	FreeEntity p
End Function


;### Update Units
Function update_unit()
	behaviour=Dunit_behaviour(TCunit\typ)
	
	;MODES
	;0 - Land/Swim
	;1 - Land/Water Ground
	;2 - Water
	;3 - Sky
	;4 - Water Surface
	;5 - Aircraft
	;6 - Land / Skyflee
	
	Select behaviour
		Case 1,2,3,7,8,9 mode=1
		Case 10 mode=6
		Case 200,201 mode=1
		Case 300,301,302,303 mode=2
		Case 400,401,402,403,404,405,406,407,408 mode=3
		Case 500 mode=1
		Case 501 mode=4
		Case 502 mode=5
		Default mode=0
	End Select
	
	
	;########################################################################## LIVING?!
	If TCunit\health#>0.0 Then
		
		;Frozen?
		If TCunit\freeze=0 Then
		
			;Behaviour related Stuff
			Select Dunit_behaviour(TCunit\typ)
				
				;#################################### Land Stuff
				
				;0 - Passive
				Case 0
				
				;1 - Normal / Animal
				Case 1
					Include "includes\ai\001_normal_animal.bb"
				
				;2 - Raptor / Predator
				Case 2
					Include "includes\ai\002_raptor_predator.bb"
					
				;3 - Stand and Snap
				Case 3
					Include "includes\ai\003_standandsnap.bb"
					
				;4 - Idle	
				Case 4
					Include "includes\ai\004_idle.bb"
					
				;5 - Idle Turn
				Case 5
					Include "includes\ai\005_idleturn.bb"
					
				;6 - Stand and Shoot
				Case 6
					Include "includes\ai\006_standandshoot.bb"
					
				;7 - Monkey
				Case 7
					Include "includes\ai\007_monkey.bb"
					
				;8 - Shy
				Case 8
					Include "includes\ai\008_shy.bb"
					
				;9 - Plague
				Case 9
					Include "includes\ai\009_plague.bb"
					
				;10 - Landbird
				Case 10
					Include "includes\ai\010_landbird.bb"
				
				;#################################### Land/Water Stuff
					
				;200 - Crab
				Case 200
					Include "includes\ai\200_crab.bb"
					
				;201 - Amphibian
				Case 201
					Include "includes\ai\201_amphibian.bb"
					
				;#################################### Fish Stuff
				
				;300 - Fish	
				Case 300
					Include "includes\ai\300_fish.bb"
					
				;301 - circlingfish	
				Case 301
					Include "includes\ai\301_circlingfish.bb"
					
				;302 - Predatorfish
				Case 302
					Include "includes\ai\302_predatorfish.bb"
					
				;303 - Deepseafish
				Case 303
					Include "includes\ai\303_deepseafish.bb"
				
				;#################################### Air Stuff
					
				;400 - Bird / 405 - Highbird / 406 - Lowbird
				Case 400,405,406
					Include "includes\ai\400_405_406_bird.bb"
					
				;401 - Circling Bird
				Case 401
					Include "includes\ai\401_circlingbird.bb"		
					
				;404 - Flying Insect
				Case 404
					Include "includes\ai\404_flyinginsect.bb"
					
				;407 - Land Sky Bird
				Case 407
					Include "includes\ai\407_landskybird.bb"
					
				;408 - Killerbird
				Case 408
					Include "includes\ai\408_killerbird.bb"
					
					
					
				;#################################### Vehicle Stuff
				
				;500 - Vehicle
				Case 500
					;Stop Sound
					If TCunit\id<>g_drive Then
						If ChannelPlaying(TCunit\chan)<>0 Then
							StopChannel TCunit\chan
						EndIf
					EndIf
				
				;501 - Watercraft
				Case 501
					;Water Wobble!
					RotateEntity TCunit\h,Sin(in_wa#+(TCunit\id*20)),EntityYaw(TCunit\h),Cos(in_wa#+(TCunit\id*20))
					;Vehicle Model
					PositionEntity TCunit\vh,EntityX(TCunit\mh,1),EntityY(TCunit\mh,1),EntityZ(TCunit\mh,1)
					RotateEntity TCunit\vh,0,EntityYaw(TCunit\h),0
					;Stop Sound
					If TCunit\id<>g_drive Then
						If ChannelPlaying(TCunit\chan)<>0 Then
							StopChannel TCunit\chan
						EndIf
					EndIf
				
				;502 - Aircraft
				Case 502
					;Stop Sound
					If TCunit\id<>g_drive Then
						If ChannelPlaying(TCunit\chan)<>0 Then
							StopChannel TCunit\chan
						EndIf
					EndIf
					
	
			End Select
			
			
			;########################################################################## PHYSICAL
			
			Select mode
				;0 - Land/Swim
				Case 0
					;Land
					If EntityY(TCunit\h)>0.-(Dunit_colyr(TCunit\typ)/2.) Then
						;Jump
						If (gt-TCunit\phy_jump#)<0. Then
							par=TCunit\phy_jump-gt
							tot=game_jumptime
							perc#=Float(Float(par)/Float(tot))
							;perc#=1.-perc#
							;perc#=Sin(perc#*360.)
							;TranslateEntity TCunit\h,0,((4.*perc#)*f#),0
							;TranslateEntity TCunit\h,0,(Cworld_g#*f#*0.39),0
							TranslateEntity TCunit\h,0,((4.*perc#)*f#),0
							;On Ground / Keep over Terrain
							tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
							If EntityY(TCunit\h)<=(tery#+Dunit_colyr(TCunit\typ)) Then
								PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
								TCunit\phy_ft=gt
								TCunit\phy_jump=0
							ElseIf EntityCollided(TCunit\h,Cworld_col) Then
								TCunit\phy_ft=gt
								TCunit\phy_jump=0
							EndIf
							TCunit\phy_fall=gt
							TCunit\phy_fally#=EntityY(TCunit\h)
						Else
							;Fall
							par=gt-TCunit\phy_fall
							If par>Cworld_facct Then par=Cworld_facct
							tot=Cworld_facct
							perc#=Float(Float(par)/Float(tot))
							TranslateEntity TCunit\h,0,-(Cworld_g#*f#*(3.*perc#)),0
							tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
							col=0
							;On Ground / Keep over Terrain
							If EntityY(TCunit\h)<=(tery#+Dunit_colyr(TCunit\typ)) Then
								;If TCunit\phy_fally#-EntityY(TCunit\h)>100 Then game_falldamage()
								PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
								If gt-TCunit\phy_fall>300 Then col=1
								TCunit\phy_ft=gt
								TCunit\phy_fall=gt
								TCunit\phy_fally#=EntityY(TCunit\h)
							ElseIf EntityCollided(TCunit\h,Cworld_col) Then
								;If TCunit\phy_fally#-EntityY(TCunit\h)>100 Then game_falldamage()
								If gt-TCunit\phy_fall>300 Then col=1
								TCunit\phy_ft=gt
								TCunit\phy_fall=gt
								TCunit\phy_fally#=EntityY(TCunit\h)
							EndIf
							If col=1 Then
								;Hit Ground! - Player Movement Sounds
								If TCunit\id=g_player Then
									If g_drive=0 Then
										If EntityCollided(TCunit\h,Cworld_col) Then
											If get_object(CollisionEntity(TCunit\h,1))<>-1 Then
												If Handle(TCobject) Then
													material_stepsfx(Dobject_mat(TCobject\typ))	
												EndIf
											ElseIf get_item(CollisionEntity(TCunit\h,1))<>-1 Then
												If Handle(TCitem) Then
													material_stepsfx(Ditem_mat(TCitem\typ))	
												EndIf
											Else
												colwithunit=0
												For Tunit.Tunit=Each Tunit
													If Tunit\vh=CollisionEntity(TCunit\h,1) Then
														material_stepsfx(Dunit_mat(Tunit\typ))	
														colwithunit=1
														Exit
													EndIf
												Next
												If colwithunit=0 Then
													If m_menu<>Cmenu_if_movie Then
														sfx(sfx_step(Rand(0,3)))
													EndIf
												EndIf
											EndIf
										Else
											If m_menu<>Cmenu_if_movie Then
												sfx(sfx_step(Rand(0,3)))
											EndIf
										EndIf
										g_player_mst=ms
									EndIf
								EndIf
							EndIf
						EndIf
					;Water
					Else
						;No Gravity and Jump Stuff
						TCunit\phy_ft=gt
						TCunit\phy_fall=gt
						TCunit\phy_jump#=0.
						;Keep over Terrain
						tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
						If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
							PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
						EndIf
					EndIf
				;1 - Land/Water Ground
				Case 1
					;Land
					If EntityY(TCunit\h)>0. Then
						;Fall
						TranslateEntity TCunit\h,0,-(Cworld_g#*f#*0.6),0
						tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
						;On Ground / Keep over Terrain
						If EntityY(TCunit\h)<=(tery#+Dunit_colyr(TCunit\typ)) Then
							PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
							TCunit\phy_ft=gt
						ElseIf EntityCollided(TCunit\h,Cworld_col) Then
							TCunit\phy_ft=gt
						EndIf
					;Water
					Else
						;No Gravity and Jump Stuff
						TCunit\phy_ft=gt
						TCunit\phy_fall=gt
						TCunit\phy_jump#=0.
						;Fall
						TranslateEntity TCunit\h,0,-(Cworld_g#*f#*0.6),0
						;Keep over Terrain
						tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
						If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
							PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
						EndIf
					EndIf			
				;2 - Water
				Case 2
					;Keep over Terrain
					tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
					If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
						PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
					EndIf
					;Keep under Water Surface (push down when too high)
					If EntityY(TCunit\h)>-10 Then
						PositionEntity TCunit\h,EntityX(TCunit\h),-10,EntityZ(TCunit\h)
					EndIf
				;3 - Sky
				Case 3
					freecon=0
					;Keep over Terrain / Water Surface
					tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
					If tery#<1 Then tery#=1
					rtery#=tery#
					Select Dunit_behaviour(TCunit\typ)
							;Bird
							Case 400 tery#=tery#+350
							Case 401 tery#=tery#+350
							;Flying Insect
							Case 404 tery#=tery#+20
							;Highbird
							Case 405 tery#=tery#+500
							;Lowbird
							Case 406 tery#=tery#+180
							;Land/Sky Bird
							Case 407 tery#=tery#+210:freecon=1
							;Killerbird
							Case 408 tery#=tery#+400:freecon=1
					End Select
					;No Free Control
					If freecon=0 Then
						;Avoid from Flying into Ground
						If EntityY(TCunit\h)<tery# Then
							PositionEntity TCunit\h,EntityX(TCunit\h),tery#,EntityZ(TCunit\h)
						;Avoid from Flying to high
						Else
							Select Dunit_behaviour(TCunit\typ)
								;Birds
								Case 400,401,405,406 TranslateEntity TCunit\h,0,-0.2*f#,0
								;Flying Insect
								Case 404 TranslateEntity TCunit\h,0,-0.3*f#,0
							End Select
							;Post-Translation-Check
							If EntityY(TCunit\h)<tery# Then
								PositionEntity TCunit\h,EntityX(TCunit\h),tery#,EntityZ(TCunit\h)
							EndIf
						EndIf
					;Free Control
					Else
						;Ground?
						If EntityY(TCunit\h)<rtery# Then
							If TCunit\ai_mode=ai_fall Then
								;Don't land at water!
								If rtery#>2 Then
									;Land! - (after falling)
									ai_mode(ai_idle)
									PositionEntity TCunit\h,EntityX(TCunit\h),rtery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
								Else
									;Rise!
									ai_mode(ai_rise,Rand(3000,10000))
									PositionEntity TCunit\h,EntityX(TCunit\h),rtery#,EntityZ(TCunit\h)
								EndIf
							Else
								;Rise!
								ai_mode(ai_rise,Rand(3000,10000))
								PositionEntity TCunit\h,EntityX(TCunit\h),rtery#,EntityZ(TCunit\h)
							EndIf
						;Avoid from Flying to high
						ElseIf EntityY(TCunit\h)>tery# Then 
							TranslateEntity TCunit\h,0,-0.2*f#,0
							If TCunit\ai_mode<>ai_fall Then
								If TCunit\ai_mode<>ai_return And TCunit\ai_mode<>ai_sreturn Then
									If TCunit\ai_mode=ai_rise Then
										;Select Rand(0,4)
										;	Case 0 ai_mode(ai_move,Rand(2000,4000))
										;	Case 1 ai_mode(ai_movel,Rand(2000,4000))
										;	Case 2 ai_mode(ai_mover,Rand(2000,4000))
										;	Case 3 ai_mode(ai_fall,Rand(2000,4000))
										;End Select
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				;4 - Water Surface
				Case 4
					PositionEntity TCunit\h,EntityX(TCunit\h),1+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
				;5 - Aircraft
				Case 5
					If TCunit\vh<>0 Then HideEntity TCunit\vh
					;Fall
					If g_drive<>TCunit\id Then
						TranslateEntity TCunit\h,0,-(Cworld_g#*f#*2.),0
					EndIf
					;Keep over Terrain
					tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
					If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
						PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
					EndIf
					If TCunit\vh<>0 Then ShowEntity TCunit\vh
				;6 - Land / Skyflee
				Case 6
					;Land
					If TCunit\ai_mode<>ai_flee Then
						TranslateEntity TCunit\h,0,-(Cworld_g#*f#*0.6),0
						tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
						;On Ground / Keep over Terrain
						If EntityY(TCunit\h)<=(tery#+Dunit_colyr(TCunit\typ)) Then
							PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
							TCunit\phy_ft=gt
						ElseIf EntityCollided(TCunit\h,Cworld_col) Then
							TCunit\phy_ft=gt
						EndIf
					Else
						TCunit\phy_ft=gt
					EndIf
			End Select
		
		
		Else
		
			;Frozen, no Animation
			If TCunit\ani<>0
				TCunit\ani=0
				Animate TCunit\mh,0
			EndIf
		
		EndIf
		
		
	;########################################################################## DEAD
	Else
	
		;0 - Land/Swim
		;1 - Land/Water Ground
		;2 - Water
		;3 - Sky
		;4 - Water Surface
		;5 - Aircraft
		;6 - Land / Sky Flee
		
		Select mode
			;0 - Land/Swim
			Case 0
				;Fall
				TranslateEntity TCunit\h,0,-(Cworld_g#*f#*0.6),0 
				;Keep over Terrain / Water Surface
				tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
				If tery#<-2 Then tery#=-2
				If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
					PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
				EndIf
			;1 - Land/Water Ground	
			Case 1,6
				;Fall / Swim Up
				If EntityY(TCunit\h)<=-3+Dunit_colyr(TCunit\typ) Then
					TranslateEntity(TCunit\h,0,0.5*f#,0)
					If EntityY(TCunit\h)>-3+Dunit_colyr(TCunit\typ) Then
						PositionEntity TCunit\h,EntityX(TCunit\h),-3+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
					EndIf
				Else
					TranslateEntity TCunit\h,0,-(Cworld_g#*f#*0.6),0
				EndIf
				;Keep over Terrain / Under Water Surface
				tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
				If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
					PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
				EndIf
			;2 - Water
			Case 2
				;Swim to Water Surface
				If EntityY(TCunit\h)<-2 Then
					TranslateEntity(TCunit\h,0,0.5*f#,0)
				Else
					PositionEntity TCunit\h,EntityX(TCunit\h),-2,EntityZ(TCunit\h)
					If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))>EntityY(TCunit\h) Then
						PositionEntity TCunit\h,EntityX(TCunit\h),e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
					EndIf
				EndIf
			;3 - Sky	
			Case 3
				;Fall
				TranslateEntity TCunit\h,0,-(Cworld_g#*f#*2.),0
				;Keep over Terrain / Water Surface
				tery#=e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))
				If tery#<-2 Then tery#=-2
				If EntityY(TCunit\h)<(tery#+Dunit_colyr(TCunit\typ)) Then
					PositionEntity TCunit\h,EntityX(TCunit\h),tery#+Dunit_colyr(TCunit\typ),EntityZ(TCunit\h)
				EndIf
					
		End Select
	
	EndIf
End Function


;Play Movesound
Function ai_movesound(mode=1)
	If TCunit<>Null Then
		If mode=1 Then
			;Play
			If TCunit\chan<>0 Then
				If ChannelPlaying(TCunit\chan)=0 Then
					TCunit\chan=play_soundset(Dunit_sfx(TCunit\typ),snd_move,TCunit\h)
					;If TCunit\chan<>0 Then LoopSound TCunit\chan
				EndIf
			Else
				TCunit\chan=play_soundset(Dunit_sfx(TCunit\typ),snd_move,TCunit\h)
				;If TCunit\chan<>0 Then LoopSound TCunit\chan
			EndIf
		Else
			;Stop
			If TCunit\chan<>0 Then
				If ChannelPlaying(TCunit\chan)=1 Then
					StopChannel(TCunit\chan)
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;Set AI Center tu crrent Position of Unit
Function ai_center()
	TCunit\ai_cx#=EntityX(TCunit\h)
	TCunit\ai_cz#=EntityZ(TCunit\h)
	PositionEntity TCunit\ai_ch,TCunit\ai_cx#,0,TCunit\ai_cz#
End Function


;Do Script Animation
Function ai_dosani()
	TCunit\ai_timer=0
	If Animating(TCunit\mh) Then
		TCunit\ai_timer=gt+10000
	EndIf
End Function


;Return AI Mode
Function ai_modetxt$(mode)
	Select mode
		Case ai_idle Return ai_idle+": idle"
		Case ai_move Return ai_move+": move"
		Case ai_movel Return ai_movel+": move left"
		Case ai_mover Return ai_mover+": move right"
		Case ai_turnl Return ai_turnl+": turn left"
		Case ai_turnr Return ai_turnr+": turn right"
		Case ai_rise Return ai_rise+": rise"
		Case ai_fall Return ai_fall+": fall"
		Case ai_return Return ai_return+": return"
		Case ai_sreturn Return ai_sreturn+": strict return"
		Case ai_attack Return ai_idle+": attack player"
		Case ai_hunt Return ai_idle+": hunt player"
		Case ai_movetarget Return ai_movetarget+": move to target"
		Case ai_getfood Return ai_getfood+": get food"
		Case ai_flee Return ai_flee+": flee"
		Case ai_sani Return ai_sani+": script animation"
		Default Return mode+": UNKNOWN MODE"
	End Select
End Function
