;Stay?
If TCunit\states=1
	If TCunit\ai_mode<>ai_idle Then
		If get_state(Cstate_ai_stick,Cclass_unit,TCunit\id)=1 Then
			ai_mode(ai_idle)
		EndIf
	EndIf
EndIf

;Get Food
If in_t3000go=1 Then
	If TCunit\ai_mode<>ai_getfood Then
		
		hdist=500
		dist=0
		
		;Items
		For Titem.Titem=Each Titem
			If Titem\parent_class=0 Then
				dist=EntityDistance(Titem\h,TCunit\h)
				If dist<hdist Then
					If Ditem_behaviour$(Titem\typ)="plague_target" Then
						hdist=dist
						TCunit\ai_target_class=Cclass_item
						TCunit\ai_target_id=Titem\id
						ai_mode(ai_getfood,20000)
					EndIf
				EndIf
			EndIf
		Next
		
		
		If TCunit\ai_mode<>ai_getfood Then
			;Objects
			For Tobject.Tobject=Each Tobject
				dist=EntityDistance(Tobject\h,TCunit\h)
				If dist<hdist Then
					If Dobject_behaviour$(Tobject\typ)="plague_target" Then
						hdist=dist
						TCunit\ai_target_class=Cclass_object
						TCunit\ai_target_id=Tobject\id
						ai_mode(ai_getfood,20000)
					EndIf
				EndIf
			Next
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
		ai_flee()
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
If in_gt50go Then ai_check(ai_passive)
