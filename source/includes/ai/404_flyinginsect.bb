;Stay?
If TCunit\states=1
	If TCunit\ai_mode<>ai_idle And TCunit\ai_mode<>ai_attack Then
		If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
			ai_mode(ai_idle)
		EndIf
	EndIf
EndIf

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
	Case ai_return,ai_sreturn
		ai_movesound()
		ai_return(1)
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
If in_gt50go Then ai_check(ai_shy,0)
