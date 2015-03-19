;Action
Select TCunit\ai_mode
	Case ai_idle
		TCunit\ai_timer=0
		If Animating(TCunit\mh) Then TCunit\ai_timer=gt+10000
	Case ai_attack
		ai_attack()
	Case ai_getfood,ai_flee
		TCunit\ai_timer=0
	Case ai_sani
		ai_dosani()
End Select

;Set Mode
If (gt-TCunit\ai_timer)>TCunit\ai_duration Then
	ai_mode(ai_idle)
EndIf
If in_gt50go Then ai_check(ai_agressive,0,0)
