;############################################ LOAD STRINGS

Dim s$(50)
Dim sm$(250)
Dim se$(300)

Function load_strings()
	Local stream=ReadFile("sys\strings.inf")
	If stream=0 Then RuntimeError("Unable to read sys\strings.inf")
	Local in$,var$,val$
	Local i,equal
	While Not Eof(stream)
		in$=ReadLine(stream)
		equal=Instr(in$,"=")
		If Left(in$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(in$,equal-1))
			val$=Mid(in$,equal+1,-1)
			Select Left(var$,1)
				Case "0"
					If Int(var$)<0 Or Int(var$)>50 Then
						RuntimeError "Invalid String ID '"+var$+"'
					EndIf
					s$(Int(var$))=val$
				Case "m"
					var$=Right(var$,3)
					If Int(var$)<0 Or Int(var$)>250 Then
						RuntimeError "Invalid String ID '"+var$+"'
					EndIf
					sm$(Int(var$))=val$
				Case "e"
					var$=Right(var$,3)
					If Int(var$)<0 Or Int(var$)>300 Then
						RuntimeError "Invalid String ID '"+var$+"'
					EndIf
					se$(Int(var$))=val$
				Default
					RuntimeError "Invalid String ID '"+var$+"'
			End Select
		EndIf
	Wend
	CloseFile(stream)
End Function

load_strings()


;### Show String
Function ss$(txt$,p1$="",p2$="",p3$="")
	txt$=Replace(txt$,"$1",p1$)
	txt$=Replace(txt$,"$2",p2$)
	txt$=Replace(txt$,"$3",p3$)
	Return txt$
End Function
