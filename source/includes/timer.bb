;############################################ TIMER

;### Timer add
Function timer_add()
	class$=param()									;Class
	If class$="self" Or class$="-1" Or class="0" Then
		classint=-1
		If class="0" Then classint=0
	Else
		classint=parse_getclass(class$,0)
	EndIf
	If classint<1 Then
		If classint=-1 Then
			classint=p_env_class
			id=p_env_id
		EndIf
	Else
		parse_comma(1)
		id=param()									;ID
	EndIf
	Ttimer.Ttimer=New Ttimer
	Ttimer\parent_class=classint
	Ttimer\parent_id=id
	parse_comma(1)
	Ttimer\duration=param()							;Duration
	Ttimer\start=gt
	
	Ttimer\mode=1									;Mode
	If parse_comma(0) Then
		Ttimer\mode=param()
	EndIf
	
	If parse_comma(0) Then							;Source
		source$=params()								
		txt$=0
		;Info as Source
		If Int(source$)<>0 Then
			For Tx.Tx=Each Tx
				If Tx\parent_class=Cclass_info Then
					If Tx\parent_id=Int(source$) Then
						txt$=Tx\value$
						Ttimer\typ=1
						Exit
					EndIf
				EndIf
			Next
		Else
			If Instr(txt$,".")>0 Then
				txt$=parse_loadscript$(source$)
				Ttimer\typ=1
			EndIf
		EndIf
		If txt$=0 Or txt$="" Then
			txt$=source$
			Ttimer\typ=0
		EndIf
	Else
		txt$="timer"
		Ttimer\typ=0
	EndIf
	Ttimer\scr$=txt$
	
	;DebugLog "Timer "+Ttimer\duration+" ms @ "+Ttimer\parent_class+", "+Ttimer\parent_id+" typ: "+Ttimer\typ
	
	;Parent exists? Delete when not
	If Ttimer\parent_class<>0 Then
		If Not parent_con(classint,id) Then
			parse_error("object",id)
			Delete Ttimer
		EndIf
	EndIf
End Function


;### Free Timer
Function timer_free(parent_class,parent_id,mode$="")
	For Ttimer.Ttimer=Each Ttimer
		If Ttimer\parent_class=parent_class Then
			If Ttimer\parent_id=parent_id Then
				If mode$="" Then
					Delete Ttimer
				ElseIf Ttimer\scr$=mode$ Then
					Delete Ttimer
				EndIf
			EndIf
		EndIf
	Next
End Function

;### Count Timers
Function timer_count(parent_class,parent_id)
	Local c
	For Ttimer.Ttimer=Each Ttimer
		If Ttimer\parent_class=parent_class Then
			If Ttimer\parent_id=parent_id Then
				c=c+1
			EndIf
		EndIf
	Next
	Return c
End Function


;### Timer Update
Function timer_update()
	For Ttimer.Ttimer=Each Ttimer
		If gt-(Ttimer\start+Ttimer\duration)>0 Then
			;Execute Timer
			If Ttimer\typ=0 Then
				;Event
				If Ttimer\parent_class=0 Then
					;Global Event
					parse_globalevent(Ttimer\scr$,"triggerd by timer")
				Else
					;Local Event
					parse_emuevent(Ttimer\parent_class,Ttimer\parent_id,Ttimer\scr$,"triggered by timer")	
				EndIf
			Else
				;Script
				parse_task(Ttimer\parent_class,Ttimer\parent_id,"timer","timer script",Ttimer\scr$)
			EndIf
			
			;Mode
			Ttimer\mode=Ttimer\mode-1
			
			;Free or Reset?
			If Ttimer\mode=0 Then
				;Free Timer
				Delete Ttimer
			Else
				;Reset Timer
				Ttimer\start=gt
			EndIf
		EndIf
	Next
End Function
