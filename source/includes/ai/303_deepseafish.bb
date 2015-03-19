;Stay?
If TCunit\states=1
	If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
		ai_mode(ai_idle)
	EndIf
EndIf

;Sink (DEEP sea :)
TranslateEntity TCunit\h,0,-1.*f#,0
;Action
Select TCunit\ai_mode
	Case ai_move
		;Move
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		;Collision?
		If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
			If Rand(1,2)=1 Then ai_mode(ai_movel) Else ai_mode(ai_mover)
		ElseIf EntityCollided(TCunit\h,Cworld_col) Then
			If Rand(1,2)=1 Then ai_mode(ai_movel) Else ai_mode(ai_mover)
		EndIf
	Case ai_movel
		;Move
		ai_movesound()
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		;Collision?
		If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
			MoveEntity TCunit\h,0,0,-unit_speed#()*f#
			TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#*2,0
			ai_watertest()
		ElseIf EntityCollided(TCunit\h,Cworld_col) Then
			TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#*2,0
		EndIf
	Case ai_mover
		;Move
		ai_movesound()
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		;Collision?
		If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
			MoveEntity TCunit\h,0,0,-unit_speed#()*f#
			TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#*2,0
			ai_watertest()
		ElseIf EntityCollided(TCunit\h,Cworld_col) Then
			TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#*2,0
		EndIf
	Case ai_return
		ai_movesound()
		ai_return(1)
	Case ai_sreturn
		ai_movesound()
		ai_return(1,2)
	Case ai_getfood
		ai_movesound()
		ai_getfood(1)
	Case ai_flee
		ai_movesound()
		ai_flee(1)
	Case ai_sani
		ai_dosani()
End Select

;Set Mode
If (gt-TCunit\ai_timer)>TCunit\ai_duration Then
	x=Rand(0,7)
	Select x
		Case 0,1 ai_mode(ai_move)
		Case 2,3,4 ai_mode(ai_movel)
		Case 5,6,7 ai_mode(ai_mover)
	End Select
EndIf
If in_gt50go Then ai_check(ai_shy,2)
