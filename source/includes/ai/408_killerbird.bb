;Stay?
If TCunit\states=1
	If TCunit\ai_mode<>ai_idle And TCunit\ai_mode<>ai_attack Then
		If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
			ai_mode(ai_idle)
		EndIf
	EndIf
EndIf

pitch=0

;Action
Select TCunit\ai_mode
	Case ai_move
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
	Case ai_movel
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_mover
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_rise
		ai_movesound()
		pitch=-35
		RotateEntity TCunit\h,pitch,EntityYaw(TCunit\h),0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_fall
		ai_movesound()
		pitch=35
		RotateEntity TCunit\h,pitch,EntityYaw(TCunit\h),0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_return
		ai_movesound()
		ai_return(1)
	Case ai_sreturn
		ai_movesound()
		ai_return(1)
	Case ai_hunt
		ai_movesound()
		pitch=EntityPitch(TCunit\h)+(DeltaPitch(TCunit\h,g_cplayer\h)/(6.*f#))
		RotateEntity TCunit\h,pitch,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,g_cplayer\h)/(9.*f#)),0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		If EntityY(TCunit\h)<e_tery(EntityX(TCunit\h)+30,EntityZ(TCunit\h)) Then
			PositionEntity TCunit\h,EntityX(TCunit\h),e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))+30,EntityZ(TCunit\h)
		EndIf
		If EntityY(g_cplayer\h)<0.1 Then
			ai_mode(ai_rise)
		EndIf
	Case ai_attack
		ai_mode(ai_rise,Rand(3000,5000))
	Case ai_getfood
		ai_movesound()
		ai_getfood(1)
	Case ai_flee
		ai_movesound()
		ai_flee(1)
	Case ai_idle
		;Fly away
		If in_t100go Then
			For Tunit.Tunit=Each Tunit
				If Tunit\id=g_player Then
					If EntityDistance (Tunit\h,TCunit\h)<100 Then
						ai_mode(ai_rise,Rand(2500,4000))
					EndIf
				EndIf
			Next
		EndIf
	Case ai_sani
		ai_dosani()
End Select

;Rotate
If pitch=0 Then
	RotateEntity TCunit\h,pitch,EntityYaw(TCunit\h),0
EndIf

;Set Mode
If (gt-TCunit\ai_timer)>TCunit\ai_duration Then
	If TCunit\ai_mode=ai_idle Then
		x=Rand(10)
		Select x
			Case 0,1,2,3,4,5,6,7 ai_mode(ai_idle)
			Default ai_mode(ai_rise)
		End Select
	Else
		x=Rand(0,10)
		Select x
			Case 0,1 ai_mode(ai_move)
			Case 2,3,4 ai_mode(ai_movel)
			Case 5,6,7 ai_mode(ai_mover)
			Case 8 ai_mode(ai_rise)
			Case 9,10 ai_mode(ai_fall,Rand(8000,15000))
		End Select
	EndIf
EndIf
If in_gt50go Then ai_check(ai_agressive,0)
