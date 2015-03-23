;############################################ Parser Math


;### Parse Math String
Function parse_math$(txt$)
	;con_add("math: "+txt$,Cbmpf_red)
	;Kill Whitespaces
	Local otxt$=txt$
	If Instr(txt$,Chr(34))=0 Then
		txt$=Replace(txt$," ","")
	Else
		txt$=trimexquote$(txt$)
	EndIf
	;Scan (scans brackets and presence of math operators)
	Local bro							;Brackets Open
	Local brc							;Brackets Closed
	Local req=0							;Math Stuff required?
	Local s,i,l
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
	If req=0 Then Return txt$
	;DebugLog ""
	;DebugLog "Parse Math: "+txt$
	If bro<>brc Then parse_error("brackets"):Return 0
	;No Brackets -> Simple Term Function
	If bro=0 Then
		;Calc
		Return parse_math_st(txt$)
	;Brackets -> Split
	Else
		l=Len(txt$)
		;Extract, Parse and Replace Return Commands with their Return Values
		combracket=0
		comstart=0
		For i=1 To l
			s=Asc(Mid(txt$,i,1))
			;(
			If s=40 Then
				If i>1 Then
					;Command Bracket
					If parse_math_op(Mid(txt$,1,i-1),2)=0 Then
						;Command Bracket!
						combracket=1
						;Parse Command
						parse_math_getcom(Mid(txt$,1,i-1))
						If p_com$<>"" Then
							comstart=i-Len(p_com$)
							p_internaltxt$=Mid(txt$,i+1,-1)
							;DebugLog "<REPLACE START>"
							;DebugLog "COMMAND: "+p_com$
							;con_add("INTERNAL STUFF "+p_com$+" -> "+p_internaltxt$)
							;DebugLog "INTERNAL PARAMS: "+p_internaltxt$
							parse_commands(0,1)
						Else
							;Nevertheless no Command Bracket!
							combracket=0
						EndIf
					EndIf
				EndIf
			;)
			ElseIf s=41 Then
				;Replace at End of Command Bracket
				If combracket=1 Then
					combracket=0
					If comstart>1 Then
						txtleft$=Left(txt$,comstart-1)
					Else
						txtleft$=""
					EndIf
					If i<l Then
						txtright$=Mid(txt$,i+1,-1)
					Else
						txtright$=""
					EndIf
					;DebugLog "<REPLACE END>"
					;DebugLog "OLD: "+txt$
					;DebugLog "NEW: "+(txtleft$+p_return$+txtright$)
					;DebugLog "COM: "+p_com$
					;DebugLog "RETURN: "+p_return$
					txt$=txtleft$+p_return$+txtright$
					l=Len(txt$)
					i=0
				EndIf
			EndIf
		Next
		Local depth,maxdepth
		;Dissolve all brackets
		While Instr(txt$,"(")>0 
			;DebugLog ""
			;DebugLog "Dissolving: "+txt$
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
			;DebugLog "Bracket depth: "+maxdepth
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
			;DebugLog "Bracket cont.: "+deepc$
			Local deepv$=parse_math_st(deepc$)
			;Beginning - just replace
			If brs-1=1 Then
				txt$=Mid(txt$,Len(deepc$)+3,-1)
				txt$=Str(deepv)+txt$
			;Replace
			Else
				txt$=Replace(txt$,Mid(txt$,brs-2,1)+"("+deepc$+")",Mid(txt$,brs-2,1)+Str(deepv$))
			EndIf
		Wend
		;Calc
		Return parse_math_st(txt$)
	EndIf
End Function






;### Final "Simple Term" Calculation (Used for Terms without brackets)
Function parse_math_st$(txt$)
	;DebugLog ""
	;DebugLog "Calc -> Term: "+txt$
	Local l=Len(txt$)
	Local i
	
	;Get Base (Number before Operator)
	For i=1 To l
		;Ignore starting minus for negative numbers
		If i=1 Then
			If Mid(txt$,1,1)="-" Then i=2
		EndIf
		;Operator/End of String? -> End of Base Number
		If parse_math_op(Mid(txt$,i,3))>0 Or i=l Then
			If i=l Then
				;Base-End = String-End? -> Skip Calculation and return Base Number
				p_m_base=Mid(txt$,1,i)
				Return p_m_base
			Else
				;Extract Base
				p_m_base=Mid(txt$,1,i-1)
				Exit
			EndIf
		EndIf
	Next
	
	;i = 1 Error
	;unable to find base
	If i=1 Then
		If txt$<>"" Then
			;Error: Begins with unexpected operator
			parse_error("bwo",txt$)
			Return 0
		Else
			;Empty Term = 0
			Return 0
		EndIf
	EndIf
		
	;Extract Stuff
	;Free Math Cache
	For Tpm.Tpm=Each Tpm
		Delete Tpm
	Next
	;Vars
	Local pos
	Local op			;Operators
	Local ns,ne			;Number Start/Number End
	Local number$		;Number
	Local pmt$			;Math Operator Temp.
	;Check whole Term
	For i=(i-1) To l
		;Reached Operator or End?
		op=parse_math_op(Mid(txt$,i,3))
		If op>0 Or i=l Then
			;Determine Start
			If ns=0 Then
				;Set Start
				ns=i
				;If not at End
				If i+Len(p_m_o$)<l Then
					;Following Operator? Has to be a minus, otherwise -> Error!
					mot$=p_m_o$
					If parse_math_op(Mid(txt$,i+Len(p_m_o$),3))>0 Then
						;Skip Operator/Minus
						i=i+Len(mot$)
						;Operator is no Minus? Error!
						If p_m_o$<>"-" Then
							parse_error("operators",txt$)
						EndIf
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
				;Extract Value
				number$=Mid(txt$,ns,(ne+1)-(ns))
				;Create Type
				pos=pos+1
				Tpm.Tpm=New Tpm
				Tpm\typ=var_type(number$)
				Tpm\pos=pos
				Tpm\txt$=p_m_o$ ;Mid(txt$,ns,1)
				Tpm\value$=number$
				ns=0
				;DebugLog "math part: "+p_m_o$+"  "+number$
				;Exit on End
				If i=l Then Exit
				;i=i-1
			EndIf
		EndIf
	Next
	
	;Calc
	p_m_value=p_m_base
	Local valuetype
	Local prev$
	Local prevtype
	
	;Calc for * and / Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "*"
				prev=parse_math_prev(Tpm\pos)
				prevtype=var_type(prev)
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				If p_m_basec=0 Then
					;DebugLog "Calc -> Operation: "+p_m_value#+"+"+prev#+"*"+Tpm\value#
					If prevtype+valuetype+ovaluetype=0 Then
						p_m_value=Int(p_m_value)+(Int(prev)*Int(Tpm\value))
					Else
						p_m_value=Float(p_m_value)+(Float(prev)*Float(Tpm\value))
					EndIf
				Else
					;DebugLog "Calc -> Operation: "+p_m_value#+"*"+Tpm\value#
					If prevtype+valuetype+ovaluetype=0 Then
						p_m_value=(Int(prev)*Int(Tpm\value))
					Else
						p_m_value=(Float(prev)*Float(Tpm\value))
					EndIf
				EndIf
				Delete Tpm
			Case "/"
				prev=parse_math_prev(Tpm\pos)
				prevtype=var_type(prev)
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				If Tpm\value=0 Then parse_error("zerodiv","'"+prev+"/"+Tpm\value+"'")
				If p_m_basec=0 Then
					;DebugLog "Calc -> Operation: "+p_m_value#+"+"+prev#+"/"+Tpm\value#
					If prevtype+valuetype+ovaluetype=0 Then
						p_m_value=Int(p_m_value)+(Int(prev)/Int(Tpm\value))
					Else
						p_m_value=Float(p_m_value)+(Float(prev)/Float(Tpm\value))
					EndIf
				Else
					;DebugLog "Calc -> Operation: "+prev#+"/"+Tpm\value#
					If prevtype+valuetype+ovaluetype=0 Then
						p_m_value=(Int(prev)/Int(Tpm\value))
					Else
						p_m_value=(Float(prev)/Float(Tpm\value))
					EndIf
				EndIf
				Delete Tpm
		End Select
	Next
	
	;Calc for + and - Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "+"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;DebugLog "Calc -> Operation: "+p_m_value#+"+"+Tpm\value#
				If valuetype+ovaluetype=0 Then
					;DebugLog "Calc -> INT Operation: "+p_m_value+"+"+Tpm\value
					p_m_value=Int(p_m_value)+Int(Tpm\value)
				Else
					If valuetype=2 Or ovaluetype=2 Then
						;DebugLog "Calc -> STRING Operation: "+p_m_value+"+"+Tpm\value
						p_m_value=p_m_value+Tpm\value
					Else
						;DebugLog "Calc -> FLOAT Operation: "+p_m_value+"+"+Tpm\value
						p_m_value=Float(p_m_value)+Float(Tpm\value)
					EndIf
				EndIf
				Delete Tpm
			Case "-"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;DebugLog "Calc -> Operation: "+p_m_value#+"-"+Tpm\value#
				If valuetype+ovaluetype=0 Then
					p_m_value=Int(p_m_value)-Int(Tpm\value)
				Else
					p_m_value=Float(p_m_value)-Float(Tpm\value)
				EndIf
				Delete Tpm
		End Select
	Next
	
	;Calc for logical Operators
	For Tpm.Tpm=Each Tpm
		Select Tpm\txt$
			Case "=="
				;con_add "Calc -> Operation: "+p_m_value+" == "+Tpm\value
				If p_m_value=Tpm\value Then
					p_m_value=1
				Else
					valuetype=var_type(Tpm\value)
					ovaluetype=var_type(p_m_value)
					If valuetype=1 Or ovaluetype=1 Then
						If Float(p_m_value)=Float(Tpm\value) Then
							p_m_value=1
						Else
							p_m_value=0
						EndIf
					Else
						p_m_value=0
					EndIf
				EndIf
				Delete Tpm
			Case "!="
				;con_add "Calc -> Operation: "+p_m_value+" != "+Tpm\value
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				If valuetype+ovaluetype=0 Then
					If Int(p_m_value)<>Int(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				ElseIf valuetype=1 Or ovaluetype=1 Then
					If Float(p_m_value)<>Float(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf
				Else
					If p_m_value<>Tpm\value Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				EndIf
				Delete Tpm
			Case ">"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;con_add "Calc -> Operation: "+p_m_value+" > "+Tpm\value
				If valuetype+ovaluetype=0 Then
					If Int(p_m_value)>Int(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf
				Else
					If Float(p_m_value)>Float(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				EndIf
				Delete Tpm
			Case "<"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;con_add "Calc -> Operation: "+p_m_value+" < "+Tpm\value
				If valuetype+ovaluetype=0 Then
					If Int(p_m_value)<Int(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf
				Else
					If Float(p_m_value)<Float(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				EndIf
				Delete Tpm
			Case ">=","=>"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;con_add "Calc -> Operation: "+p_m_value+" >= "+Tpm\value
				;dlog("Calc -> Operation: "+p_m_value+" >= "+Tpm\value)
				If valuetype+ovaluetype=0 Then
					If Int(p_m_value)>=Int(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf
				Else
					If Float(p_m_value)>=Float(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				EndIf
				Delete Tpm
			Case "<=","=<"
				valuetype=var_type(Tpm\value)
				ovaluetype=var_type(p_m_value)
				;con_add "Calc -> Operation: "+p_m_value+" <= "+Tpm\value
				;dlog("Calc -> Operation: "+p_m_value+" >= "+Tpm\value)
				If valuetype+ovaluetype=0 Then
					If Int(p_m_value)<=Int(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf
				Else
					If Float(p_m_value)<=Float(Tpm\value) Then
						p_m_value=1
					Else
						p_m_value=0
					EndIf					
				EndIf
				Delete Tpm
			Case "&&","and"
				;con_add "Calc -> Operation: "+p_m_value+" and "+Tpm\value
				If (Int(p_m_value)<>0) And (Int(Tpm\value)<>0) Then
					p_m_value=1
				Else
					p_m_value=0
				EndIf
				Delete Tpm
			Case "||","or"
				;con_add "Calc -> Operation: "+p_m_value+" or "+Tpm\value
				If (Int(p_m_value)<>0) Or (Int(Tpm\value)<>0) Then
					p_m_value=1
				Else
					p_m_value=0
				EndIf
				Delete Tpm
			Case "xor"
				;con_add "Calc -> Operation: "+p_m_value+" xor "+Tpm\value
				If (Int(p_m_value)<>0) Xor (Int(Tpm\value)<>0) Then
					p_m_value=1
				Else
					p_m_value=0
				EndIf
				Delete Tpm	
			Default
				;DebugLog "Calc -> Operation: UNKNOWN! '"+Tpm\txt$+"'"
				parse_error("'"+Tpm\txt$+"' is no allowed mathematical or logical operator of SII script")
		End Select
	Next
	
	;Return Value
	;DebugLog "Calc -> Result: "+p_m_value
	Return p_m_value
End Function


;### Is String an Operator?
Function parse_math_op(txt$,dir=1)
	;Returns:
	;0 - No Operator
	;1,2,3,4 - Mathematical Operator
	;5 - Logical Operator
	Local sub$
	;Check Length 2
	If dir=1 Then
		sub$=Left(txt$,2)
	Else
		sub$=Right(txt$,2)
	EndIf
	p_m_o$=sub$
	Select sub$
		Case "==" Return 5
		Case ">=" Return 5
		Case "<=" Return 5
		Case "=>" Return 5
		Case "=<" Return 5
		Case "!=" Return 5
		Case "&&" Return 5
		Case "||" Return 5
		Case "or" Return 5
	End Select
	;Check Length 1
	If dir=1 Then
		sub$=Left(txt$,1)
	Else
		sub$=Right(txt$,1)
	EndIf
	p_m_o$=sub$
	Select sub$
		Case "+" Return 1
		Case "-" Return 2
		Case "*" Return 3
		Case "/" Return 4
		Case ">" Return 5
		Case "<" Return 5
	End Select
	;Check Length 3
	If dir=1 Then
		sub$=Left(txt$,3)
	Else
		sub$=Right(txt$,3)
	EndIf
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
Function parse_math_prev$(pos)
	Local temp$
	Local tpos=pos-1
	;Use Sub Value 
	For Tpm.Tpm=Each Tpm
		If Tpm\typ=0 Then
			If Tpm\pos=tpos Then
				p_m_basec=0
				temp=Tpm\value
				Delete Tpm
				Return temp
			EndIf
		EndIf
	Next
	;Use Base Value
	p_m_basec=1
	Return p_m_value
End Function

;### Get Command
Function parse_math_getcom(txt$)
	;DebugLog "GET COMMAND OUT OF "+txt$
	If txt$="(" Then p_com$="":Return 0
	Local l=Len(txt$)
	Local s
	p_com$=""
	For i=l To 1 Step -1
		s=Asc(Mid(txt$,i,1))
		If (s<97 Or s>122) And s<>95 Then
			p_com$=Mid(txt$,i+1,-1)
			Return 1
		EndIf
	Next
	p_com$=txt$
	Return 0
End Function
