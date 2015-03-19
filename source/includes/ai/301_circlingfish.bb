;Action
Select TCunit\ai_mode
	Case ai_movel
		;Move
		ai_movesound()
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		;Collision?
		If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
			MoveEntity TCunit\h,0,0,-unit_speed#()*f#
			TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
			ai_watertest()
		ElseIf EntityCollided(TCunit\h,Cworld_col) Then
			TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
		EndIf
	Case ai_mover
		;Move
		ai_movesound()
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
		MoveEntity TCunit\h,0,0,unit_speed#()*f#
		;Collision?
		If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))<=(EntityY(TCunit\h)-Dunit_colyr#(TCunit\typ)) Then
			MoveEntity TCunit\h,0,0,-unit_speed#()*f#
			TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
			ai_watertest()
		ElseIf EntityCollided(TCunit\h,Cworld_col) Then
			TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
		EndIf
	Case ai_sani
		ai_dosani()
	Default
		;If Rand(1,2)=1 Then ai_mode(ai_movel) Else ai_mode(ai_mover)
End Select
