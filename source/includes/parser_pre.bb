;############################################ Parser Preparse


;### Pre Parse all Scripts
Function preparse()
	Local l,i,tot$,sub$,onend
	
	;Scan Global Script
	map_briefing_key$=preparse_string$(map_briefing$)
	
	;Scan Scripts
	For Tx.Tx=Each Tx
		If Tx\mode=0 Then
			Tx\key$=preparse_string$(Tx\value$)
		EndIf
	Next
	
	;Kill Keys of Text Container Infos
	;to avoid, that their Scripts are parsed
	For Tinfo.Tinfo=Each Tinfo
		If Tinfo\typ=37 Then
			For Tx.Tx=Each Tx
				If Tx\mode=0 Then
					If Tx\parent_class=Cclass_info
						If Tx\parent_id=Tinfo\id Then
							Tx\key$=""
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	Next
	
End Function


;### Pre Parse a String
Function preparse_string$(txt$)
	Local l,i,s,sub$,onend,key$
	
	;Scan String for Constants
	;If Instr(txt$,"#")>0 Then
	;	Local j,c
	;	s=1
	;	While Instr(txt$,"#",s)>0
	;		i=Instr(txt$,"#",s)+1
	;		sub$=""
	;		For j=i To Len(txt$)
	;			c=Chr(Mid(txt$,j,1))
	;			If c=95 Or (c>=97 And c<=122) Then
	;				sub$=sub$+Asc(c)
	;			Else
	;				s=j
	;				Exit
	;			EndIf
	;		Next
	;		If sub$<>"" Then
	;			If const_get(sub$) Then
	;				reppart$(txt$,"#"+sub$,i,tmp_constv)
	;			EndIf
	;		EndIf
	;	Wend
	;EndIf
	
	;Scan String for Keys
	If Instr(txt$,"on:")>0 Then
		l=Len(txt$)
		For i=1 To l
			sub$=Mid(txt$,i,3)
			;Scan for on
			If sub$="on:" Then
				i=i+3
				onend=parse_end(txt$,i)
				sub$=Mid(txt$,i,onend-i)
				;Add Key
				key$=key$+"Ś"+sub$
			EndIf
		Next
	EndIf
	
	;Return
	Return key$
End Function
