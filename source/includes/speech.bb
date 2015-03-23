;############################################ SPEECH

Function speech(file$,override=0,forceid=0)
	;Playing?
	If ChannelPlaying(speech_chan) Then
		If override=0 Then
			Return 0
		Else
			StopChannel(speech_chan)
		EndIf
	EndIf
	;Locals
	Local found=0
	Local s.Tspeech
	;Speech Type
	For s.Tspeech=Each Tspeech
		If s\file$=file$ Then
			found=1
			Exit
		EndIf
	Next
	;New
	If found=0 Then
		s.Tspeech=New Tspeech
		s\file$=file$
		s\limit=0
		s\lastplayed=0
		For i=1 To 99
			If FileType("sfx\speech\"+file$+i+".ogg")=1 Then
				s\limit=i
			Else
				Exit
			EndIf
		Next
	EndIf
	;Load and Play Random	
	If s\limit>0 Then
		If s\limit=1 Then
			i=1
		Else
			Local newplay=0
			While newplay=0
				i=Rand(1,s\limit)
				If i<>s\lastplayed Then
					newplay=1
				EndIf
			Wend
		EndIf
		If forceid>0 Then i=forceid
		s\lastplayed=i
		speech=load_res("sfx\speech\"+file$+i+".ogg",Cres_sound)
		If speech<>0 Then
			speech_chan=sfx_emit(speech,cam)
		EndIf
	;Load and Play
	Else
		speech=load_res("sfx\speech\"+file$+".ogg",Cres_sound)
		If speech<>0 Then
			speech_chan=sfx_emit(speech,cam)
		EndIf
	EndIf
End Function
