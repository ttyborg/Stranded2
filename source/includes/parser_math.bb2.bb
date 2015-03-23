;############################################ Parser Math


;### Parse Math String
Function parse_math#(txt$)
	;Kill Whitespaces
	txt$=Replace(txt$," ","")
	;Scan (scans brackets and presence of math operators)
	Local bro
	Local brc
	Local l,i,s
	Local req=0
	l=Len(txt$)
	For i=1 To l
		s=Asc(Mid(txt$,i,1))
		If (s<48 Or s>57) Then			;No Number?
			If s<>46 Then				;No .
				req=1
				Select s
					Case 40 bro=bro+1	;( Opening
					Case 41 brc=brc+1	;) Closing
				End Select
			EndIf
		EndIf
	Next
	;No Math Scan required!
	If req=0 Then Return Float(txt$)
	DebugLog ""
	DebugLog "Parse Math: "+txt$
	If bro<>brc Then parse_error("brackets")
	;No Brackets -> Simple Term Function
	If bro=0 Then
		;Calc
		Return parse_math_st#(txt$)
	;Brackets -> Split
	Else
		Local depth,maxdepth
		;Dissolve all brackets
		While Instr(txt$,"(")>0 
			DebugLog ""
			DebugLog "Dissolving: "+txt$
			l=Len(txt$)
			;Find "deepest" bracket
			depth=0
			maxdepth=0
			For i=1 To l
				s=Asc(Mid(txt$,i,1))
				If s=40 Or s=41 Then
					If s=40 Then
						depth=depth+1
						If depth>maxdepth Then maxdepth=depth
					Else
						depth=depth-1
					EndIf
				EndIf
			Next
			DebugLog "Bracket depth: "+maxdepth
			;Extract deepest bracket
			depth=0
			Local brs	;Bracket start
			Local bre	;Bracket end
			For i=1 To l
				s=Asc(Mid(txt$,i,1))
				If s=40 Or s=41 Then
					If s=40 Then
						depth=depth+1
						If depth=maxdepth Then brs=i+1
						
					Else
						If depth=maxdepth Then bre=i-1:Exit
						depth=depth-1
					EndIf
				EndIf
			Next
			Local deepc$=Mid(txt$,brs,(bre+1)-brs)
			;Replace
			DebugLog "Bracket cont.: "+deepc$
			Local deepv#=parse_math_st#(deepc$)
			;Beginning - just replace
			If brs-1=1 Then
				txt$=Mid(txt$,Len(deepc$)+3,-1)
				txt$=Str(deepv#)+txt$
			;Replace
			Else
				txt$=Replace(txt$,Mid(txt$,brs-2,1)+"("+deepc$+")",Mid(txt$,brs-2,1)+Str(deepv#))
			EndIf
		Wend
		;Calc
		Return parse_math_st#(txt$)
	EndIf
End Function






;### Final "Simple Term" Calculation (Used for Terms without brackets)
Function parse_math_st#(txt$)
	DebugLog ""
	DebugLog "Calc -> Term: "+txt$
	Local l=Len(txt$)
	Local i
	
	;Get Base
	For i=1 To l
		If i=1 And Mid(txt$,1,1)="-" Then i=2
		If parse_math_op(Mid(txt$,i,3))>0 Or i=l Then
			p_m_base#=Float(Mid(txt$,1,i))
			If i=l Then Return p_m_base#
			Exit
		EndIf
	Next
	
	;Extract Stuff
	For Tpm.Tpm=Each Tpm
		Delete Tpm
	Next
	Local pos
	Local op			;Operators
	Local ns,ne			;Number Start/Number End
	Local number#		;Number
	For i=(i-1) To l
		op=parse_math_op(Mid(txt$,i,3))
		If op>0 Or i=l Then
			;Determine Start
			If ns=0 Then
				;Set Start
				ns=i
				;If not at End
				If i+Len(p_m_o$)<l Then
					If parse_math_op(Mid(txt$,i+Len(p_m_o$),3))>0 Then
						i=i+Len(p_m_o$)
						If Mid(txt$,i,1)<>"-" Then parse_error("operators")
					EndIf
				EndIf
			;Determine End
			Else
				;Set End
				ne=i-1
				If i=l Then ne=l
				;Get Operator (and change Start in relation to its length)
				parse_math_op(Mid(txt$,ns,3))
				ns=ns+Len(p_m_o$)
				;Extract Value / Var
				If Mid(txt$,ns,1)="$" Then
					number#=Float(var_get$( Mid(txt$,ns+1,(ne+1)-(ns+1)) ))
				Else
					number#=Float( Mid(txt$,ns,(ne+1)-(ns)) )
				EndIf
				;Create Type
				pos=pos+1
				Tpm.Tpm=New Tpm
				Tpm\pos=pos
				Tpm\txt$=p_m_o$ ;Mid(txt$,ns,1)
				Tpm\value#=number#
				ns=0
				;Exit on End
				If i=l Then Exit
				i=i-1
			EndIf
		EndIf
	Next
	
	;Calc
	p_m_value#=p_m_base#
	Local prev#
	
	;Calc for * and / Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "*"
				prev#=parse_math_prev(Tpm\pos)
				If p_m_basec=0 Then
					DebugLog "Calc -> Operation: "+p_m_value#+"+"+prev#+"*"+Tpm\value#
					p_m_value#=p_m_value#+(prev*Tpm\value#)
				Else
					DebugLog "Calc -> Operation: "+p_m_value#+"*"+Tpm\value#
					p_m_value#=(prev*Tpm\value#)
				EndIf
				Delete Tpm
			Case "/"
				prev#=parse_math_prev(Tpm\pos)
				If Tpm\value#=0 Then parse_error("zerodiv","'"+prev#+"/"+Tpm\value#+"'")
				If p_m_basec=0 Then
					DebugLog "Calc -> Operation: "+p_m_value#+"+"+prev#+"/"+Tpm\value#
					p_m_value#=p_m_value#+(prev#/Tpm\value#)
				Else
					DebugLog "Calc -> Operation: "+prev#+"/"+Tpm\value#
					p_m_value#=(prev/Tpm\value#)
				EndIf
				Delete Tpm
		End Select
	Next
	
	;Calc for + and - Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "+"
				DebugLog "Calc -> Operation: "+p_m_value#+"+"+Tpm\value#
				p_m_value#=p_m_value#+Tpm\value#
				Delete Tpm
			Case "-"
				DebugLog "Calc -> Operation: "+p_m_value#+"-"+Tpm\value#
				p_m_value#=p_m_value#-Tpm\value#
				Delete Tpm
		End Select
	Next
	
	;Calc for logical Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "=="
				DebugLog "Calc -> Operation: "+p_m_value#+" == "+Tpm\value#
				If p_m_value#=Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "!="
				DebugLog "Calc -> Operation: "+p_m_value#+" != "+Tpm\value#
				If p_m_value#<>Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case ">"
				DebugLog "Calc -> Operation: "+p_m_value#+" > "+Tpm\value#
				If p_m_value#>Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "<"
				DebugLog "Calc -> Operation: "+p_m_value#+" < "+Tpm\value#
				If p_m_value#>Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf	
				Delete Tpm
			Case ">=","=>"
				DebugLog "Calc -> Operation: "+p_m_value#+" >= "+Tpm\value#
				If p_m_value#>=Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "<=","=<"
				DebugLog "Calc -> Operation: "+p_m_value#+" <= "+Tpm\value#
				If p_m_value#<=Tpm\value# Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "&&","and"
				DebugLog "Calc -> Operation: "+p_m_value#+" and "+Tpm\value#
				If (p_m_value#>0) And (Tpm\value#>0) Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "||","or"
				DebugLog "Calc -> Operation: "+p_m_value#+" or "+Tpm\value#
				If (p_m_value#>0) Or (Tpm\value#>0) Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm
			Case "xor"
				DebugLog "Calc -> Operation: "+p_m_value#+" xor "+Tpm\value#
				If (p_m_value#>0) Xor (Tpm\value#>0) Then
					p_m_value#=1
				Else
					p_m_value#=0
				EndIf
				Delete Tpm	
			Default
				DebugLog "Calc -> Operation: UNKNOWN! '"+Tpm\txt$+"'"
		End Select
	Next
	
	;Return Value
	DebugLog "Calc -> Result: "+p_m_value#
	Return p_m_value#
End Function


;### Is String an Operator?
Function parse_math_op(txt$)
	;Check Length 1
	Local sub$=Left(txt$,1)
	p_m_o$=sub$
	Select sub$
		Case "+" Return 1
		Case "-" Return 2
		Case "*" Return 3
		Case "/" Return 4
		Case ">" Return 5
		Case "<" Return 5
	End Select
	;Check Length 2
	sub$=Left(txt$,2)
	p_m_o$=sub$
	Select sub$
		Case "==" Return 5
		Case "!=" Return 5
		Case ">=" Return 5
		Case "=>" Return 5
		Case "<=" Return 5
		Case "=<" Return 5
		Case "&&" Return 5
		Case "||" Return 5
		Case "or" Return 5
	End Select
	;Check Length 3
	sub$=Left(txt$,3)
	p_m_o$=sub$
	Select sub$
		Case "and" Return 5
		Case "xor" Return 5
	End Select
	;None
	p_m_o$=""
	Return 0
End Function

;### Prev. Tpm (Get previous part of a term for * or / and delete it)
Function parse_math_prev(pos)
	Local temp#
	Local tpos=pos-1
	;Use Sub Value 
	For Tpm.Tpm=Each Tpm
		If Tpm\typ=0 Then
			If Tpm\pos=tpos Then
				p_m_basec=0
				temp#=Tpm\value#
				Delete Tpm
				Return temp#
			EndIf
		EndIf
	Next
	;Use Base Value
	p_m_basec=1
	Return p_m_value#
End Function
