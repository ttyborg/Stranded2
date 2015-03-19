;############################################ Load Lightcycle

Dim Dlightcycle(24,2)					;Lightcycle Array (0-23), 24 as cache for current color


;### Load Lighcycle

Function load_lightcycle()
	Dim Dlightcycle(24,2)
	Local stream=ReadFile("sys\lightcycle.inf")
	If stream=0 Then RuntimeError("Unable to read sys\lightcycle.inf")
	Local in$,var$,val$,varint
	Local i,equal
	While Not Eof(stream)
		in$=ReadLine(stream)
		equal=Instr(in$,"=")
		If Left(in$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(in$,equal-1))
			val$=Trim(Mid(in$,equal+1,-1))
			varint=Int(var)
			If varint>=0 And varint<=23 Then
				split$(val$,",",2)
				Dlightcycle(varint,0)=Int(Trim(splits$(0)))
				Dlightcycle(varint,1)=Int(Trim(splits$(1)))
				Dlightcycle(varint,2)=Int(Trim(splits$(2)))
			Else	
				RuntimeError "Invalid lighcycle Property '"+var$+"' - Use a number between 0 and 23"
			EndIf
		EndIf
	Wend
	CloseFile(stream)
End Function

load_lightcycle()
