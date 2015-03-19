;############################################ LOAD KEYNAMES

Function load_keynames()
	Local stream=ReadFile("sys\keys.inf")
	If stream=0 Then RuntimeError("Unable to read sys\keys.inf")
	Local in$,var$,val$
	Local i,equal
	While Not Eof(stream)
		in$=ReadLine(stream)
		equal=Instr(in$,"=")
		If Left(in$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(in$,equal-1))
			val$=trimspace(Mid(in$,equal+1,-1))
			If Len(var$)<4 Then
				If Int(var$)<0 Or Int(var$)>262 Then
					RuntimeError "Invalid KEY ID '"+var$+"' - value between 0 and 262 expected"
				EndIf
				in_keyname$(Int(var$))=val$
			Else
				Select var$
					Case "mouse1" in_keyname$(256)=val$
					Case "mouse2" in_keyname$(257)=val$
					Case "mouse3" in_keyname$(258)=val$
					Case "mouse4" in_keyname$(259)=val$
					Case "mouse5" in_keyname$(260)=val$
					Case "mwheelup" in_keyname$(261)=val$
					Case "mwheeldown" in_keyname$(262)=val$
					Default RuntimeError "Invalid KEY '"+var$+"'"
				End Select
			EndIf
		EndIf
	Wend
	CloseFile(stream)
	in_keyname$(0)="-"
End Function

load_keynames()
