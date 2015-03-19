;Turn to Target
If EntityDistance(TCunit\h,g_cplayer\h)<Dunit_attackrange(TCunit\typ) Then
	RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+(DeltaYaw(TCunit\h,g_cplayer\h)/(5.*f#)),0
EndIf

;Action
Select TCunit\ai_mode
	Case ai_idle
		TCunit\ai_timer=0
		If Animating(TCunit\mh) Then TCunit\ai_timer=gt+10000
	Case ai_getfood,ai_flee
		TCunit\ai_timer=0
	Case ai_sani
		ai_dosani()
End Select

;Set Mode
If (gt-TCunit\ai_timer)>TCunit\ai_duration Then
	ai_mode(ai_idle)
EndIf
