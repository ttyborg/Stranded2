;############################################ Parser Brace

;### Jump to next opening Brace
Function parse_openbrace()
	Local ps=p_p
	;Rows
	For row=p_pl To p_lines-1
		;Cols
		l=Len(Dpc$(row))
		For col=ps To l
			
			;Get Char
			txt$=Mid(Dpc$(row),col,1)
			
			;Ignore Space
			If txt$<>" " Then
				;Brace? -> ok!
				If txt$="{" Then
					parse_setl(row)
					p_p=col
					;DebugLog "OPENBRACE: "+Dpc$(row)
					Return 1
				;Other Stuff -> Error!
				Else
					Return 0
				EndIf
			EndIf
		
		Next
		ps=1
	Next
	;Failed -> Error!
	parse_error("expect","opening brace '{'")
	Return 0
End Function


;### Jump to next closing Brace
Function parse_closebrace(equalcount=1)
	;DebugLog "BRACE: find closing"
	;Stop
	Local instring=0
	Local comment=0
	Local brace=1
	Local ps=p_p
	
	;DebugLog "CURRENT LINE: "+p_l$+" @ "+p_p
	
	;Rows
	For row=p_pl To p_lines-1
		;Cols
		l=Len(Dpc$(row))
		;DebugLog "BRACE: scanline "+Dpc$(row)
		For col=ps To l
			
			;Get Char
			txt$=Mid(Dpc$(row),col,1)
			
			;Comment
			If txt$="/" Or txt$="*" Then
				If Mid(Dpc$(row),col,2)="//" Then Exit
				If comment=1 Then
					If Mid(Dpc$(row),col,2)="*/" Then comment=0
				Else
					If Mid(Dpc$(row),col,2)="/*" Then comment=1
				EndIf
			
			;InString
			ElseIf txt$=Chr(34) Then
				instring=1-instring
			EndIf
			
			;Check
			If comment=0 Then
				If instring=0 Then
					;{ Brace
					If txt$="{" Then
						;DebugLog "BRACE: {"
						brace=brace+1
					;} Brace
					ElseIf txt$="}" Then
						;DebugLog "BRACE: }"
						brace=brace-1
						If brace<=0 Then
							If equalcount=1 And brace<0 Then
								Return 0
							Else
								parse_setl(row)
								p_p=col
								;DebugLog "BRACE: skip to "+p_pl+"|"+p_p+" ("+Dpc$(row)+")"
								Return 1
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		
		Next
		ps=1
	Next
	;Failed -> Error!
	Return 0	
End Function
