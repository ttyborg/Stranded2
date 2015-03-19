;Stay?
If TCunit\states=1
	If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
		ai_mode(ai_idle)
	EndIf
EndIf

;Action
Select TCunit\ai_mode
	Case ai_movel
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_mover
		ai_movesound()
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_sani
		ai_dosani()
End Select
