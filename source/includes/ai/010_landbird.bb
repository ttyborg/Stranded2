;Stay?
If TCunit\states=1
	If TCunit\ai_mode<>ai_idle Then
		If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
			ai_mode(ai_idle)
		EndIf
	EndIf
EndIf

;Action
Select TCunit\ai_mode
	Case ai_idle
		TCunit\ai_timer=0
		If Animating(TCunit\mh) Then TCunit\ai_timer=gt+10000
	Case ai_move
		ai_movesound()
		ai_move(Dunit_speed#(TCunit\typ),90)
	Case ai_movel
		ai_movesound()
		ai_move(Dunit_speed#(TCunit\typ),90)
		TurnEntity TCunit\h,0,Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_mover
		ai_movesound()
		ai_move(Dunit_speed#(TCunit\typ),90)
		TurnEntity TCunit\h,0,-Dunit_tspeed#(TCunit\typ)*f#,0
	Case ai_movetarget
		ai_movesound()
		ai_movetarget()
	Case ai_return
		ai_movesound()
		ai_return()
	Case ai_sreturn
		ai_movesound()
		ai_return(0,2)
	Case ai_attack
		ai_attack()	
	Case ai_hunt
		ai_movesound()
		ai_hunt()
	Case ai_getfood
		ai_movesound()
		ai_getfood()
	Case ai_flee
		ai_movesound()
		ai_flee(-1)
	Case ai_sani
		ai_dosani()
End Select

;Set Mode
If (gt-TCunit\ai_timer)>TCunit\ai_duration Then
	x=Rand(0,15)
	Select x
		Case 0,1,2,3 ai_mode(ai_move)
		Case 4,5,6 ai_mode(ai_movel)
		Case 7,8,9 ai_mode(ai_mover)
		Default ai_mode(ai_idle)
	End Select
EndIf
If in_gt50go Then ai_check(ai_shy)
