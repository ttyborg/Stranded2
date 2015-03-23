;############################################ Parser


;### Parse
Function parse(noskip=0)
	;Skip Parser in Editor
	If m_section=Csection_editor
		If noskip=0 Then
			clear_parsecache()
			Return 0
		EndIf
	EndIf
	
	;Error at Crossparsing!
	If p_parseinprogress=1 Then
		m$="Crossparsing Error"+Chr(13)
		m$=m$+"Crossparsing Event Data (triggerd by a Script)"+Chr(13)
		m$=m$+"Event: "+p_env_event$+" "+p_env_info$+Chr(13)
		m$=m$+"Class: "+p_env_class+Chr(13)
		m$=m$+"ID: "+p_env_id+Chr(13)
		m$=m$+"Avoid scripts which trigger events which need"+Chr(13)
		m$=m$+"to be parsed instantly."
		RuntimeError(m$)
	EndIf
	p_parseinprogress=1
	
	Local i
	Local comment				;Comment Mode (Ignore Code)
		
	;Error
	p_error=0
	p_noerror=0					;Oppress Errors?
	
	;Brace
	p_bracelevel=0				;{} - Brace Depth
	p_bracemode=0				;1 if expecting opening { Brace, 0 if not
	p_bracetype$=""				;Type of Brace
	p_bracepar$=""				;Brace Param. (Condition)
	
	;Loop
	p_loopmode=0				;Loop? 0 no / 1 yes
	p_looprow=0					;Loop Script Start Row
	p_loopcol=0					;Loop Script Start Col
	p_looptype=""				;Loop Type
	p_loopargs=""				;Loop Arguments
	p_looplevel=0				;Loop Level
	p_loopi=0					;Loop Index
	
	;Math
	p_m_ol=0
	p_m_or=0
	p_m_base=0
	p_m_value=0
	p_m_basec=0
	p_m_o$=""
	
	;Stuff
	p_elseif=0
	p_varname$=""
	
	
	;Free Jump Points
	For Tpj.Tpj=Each Tpj
		Delete Tpj
	Next
	
	;Free Loop Jumps
	For Tplj.Tplj=Each Tplj
		Delete Tplj
	Next
	
	;Cache Lines in Parse-Cache-Type (Tpc) to Array (Dpc)
	p_lines=0
	For Tpc.Tpc=Each Tpc
		p_lines=p_lines+1
	Next
	Dim Dpc$(p_lines-1)
	For Tpc.Tpc=Each Tpc
		Dpc$(i)=Tpc\txt$
		;DebugLog zerofill$((i+1),3)+": "+Tpc\txt$
		i=i+1
		Delete Tpc
	Next
	
	;Check all lines in Parse Cache
	p_pl=0
	While p_pl<(p_lines)
	
		;### Cache Line
		p_l$=Dpc$(p_pl)			;Line Text
		p_len=Len(p_l$)			;Line Length
		p_p=1					;Line Position (1=Start)
		
		;DebugLog "Parseline: "+p_l$
	
		;Parse Line until its end is reached
		;############################## LINE LOOP (ROW)
		While p_p<=p_len 
			;Text available in this Line?
			;-> skip Spaces and jump to Text!
			If parse_skipspace() Then
			
				;### Comment?
				twof$=Mid(p_l$,p_p,2)
				If comment=1 Then
					;Comment End?
					If twof$="*/" Then
						comment=0
						p_p=p_p+2
						parse_skipspace()
					Else
						;Skip Line if there is no "*/" in it.
						If Instr(p_l$,"*/") Then
							p_p=p_p+1
						Else
							p_p=p_len+1
						EndIf
					EndIf
				Else
					;No Comment? Check for Comments
					If twof$="//" Then p_p=p_len+1	;Skip Line @ //-Comments
					If twof$="/*" Then comment=1	;Comment Start!
				EndIf
				
				;### PARSE
				If comment=0 Then
					If p_p<=p_len Then
					
					
						;############################## CHAR LOOP (COL)
					
						;### Command?
						;Extract it.
						If parse_com() Then					
							If p_bracemode=0 Then
							
								;Basic Stuff
								Local ps,pe				;Param Start/End
								Select p_com$
									
									
									;Variable Assignment
									Case "$"
										Select var_parse$()
											Case "="
												var_set(p_varname$,param$())
											Case "+="
												var_modify$(p_varname$,param$())
											Case "++"
												var_modify$(p_varname$,1)
											Case "-="
												var_modify$(p_varname$,("-"+param$()))
											Case "--"
												var_modify$(p_varname$,-1)
										End Select
										parse_semi()
										
									;Error Oppression
									Case "@" 
										;DebugLog "got @ !!!"
										If p_noerror>0 Then
											p_noerror=0
											parse_error("'@' sequences are not allowed. Max. one '@' per command")
										Else
											p_noerror=2
											p_p=p_p+1
										EndIf
																		
									;ON Events
									Case "on"
										;Scan ':'
										If Mid(p_l$,p_p,1)<>":" Then
											parse_error("expect","':'")
										Else
											;Extract Event and set Brace Stuff
											ps=p_p+1
											pe=parse_end(p_l$,ps)
											p_bracepar$=Mid(p_l$,ps,pe-ps)
											p_bracemode=1
											p_bracetype$="on"
											p_p=pe
											;Jump to opening Brace
											;DebugLog "EVENT: "+p_env_event$+" <-> "+p_bracepar$
											parse_openbrace()
										EndIf
										;Bracelevel Error
										If p_bracelevel<>0 Then
											parse_error("'on' must not be subordinated to conditions")
										EndIf
										
									;IF Conditions
									Case "if"
										;DebugLog "GOT IF @ "+p_l$
										;Extract Condition and set Brace Stuff
										p_bracepar$=param$()
										;con_add("IF BRACEPAR: "+p_bracepar$)
										;DebugLog "IF bracepar "+p_bracepar$
										p_bracemode=1
										p_bracetype$="if"
										;Jump to opening Brace
										parse_openbrace()
										
									;ELSEIF Conditions
									Case "elseif"
										;Get Bool of last/root If
										p_rootif=parse_getjpbool(p_bracelevel+1)
										;Condition
										If p_rootif=1 Then
											;Skip Condition and jump to Brace 
											param$()
											p_bracepar$=0
											;con_add("elseif skip: "+param$(2))
											;DebugLog "elseif skip"
										Else
											;Check Condition and jump to Brace 
											p_bracepar$=param$()
											;DebugLog "elseif bracepar: "+p_bracepar
											;con_add("elseif bracepar: "+p_bracepar)
										EndIf
										;Set Brace Stuff
										p_bracemode=1
										p_bracetype$="elseif"
										;Jump to opening Brace
										parse_openbrace()
										
									;ELSE
									Case "else"
										;DebugLog "GOT ELSE"
										;Get Bool of last If
										p_bracepar$=parse_getjpbool(p_bracelevel+1)
										;Set Brace Stuff
										p_bracemode=1
										p_bracetype$="else"
										;Jump to opening Brace
										parse_openbrace()
									
									;SKIP
									Case "skip"
										p_parseinprogress=0
										Return 1
										
									;LOOP
									Case "loop"
										;Parse Loop
										parse_loop()
										;Set Brace Stuff
										p_bracemode=1
										p_bracetype$="loop"
										;Jump to opening Brace
										parse_openbrace()
										p_looprow=p_pl
										p_loopcol=p_p
										p_looplevel=p_bracelevel
										p_loopi=0
										
									;Command
									Default parse_commands()
								
								End Select
								
							Else
								parse_error("expect","'{'")
							EndIf
							
							;Oppress Errors Countdown
							p_noerror=p_noerror-1
							
							
						;### Stuff?
						Else
						
							;Determine Sign
							sign=Asc(Mid(p_l$,p_p,1))
							
							;### Opening Brace {
							If sign=123
								If p_bracemode=1 Then
									p_bracemode=0
									p_bracelevel=p_bracelevel+1
									parse_setjp(p_pl,p_p,p_bracelevel,p_bracetype$,p_bracepar$)
									p_p=p_p+1
									;Skip brace content?
									Select p_bracetype$
										Case "on"
											If p_env_event$<>p_bracepar$ Then
												;DebugLog "EVENT: "+p_env_event$+" <-> "+p_bracepar$
												parse_closebrace()
											EndIf
										Case "if"
											If Int(p_bracepar$)=0 Then parse_closebrace()
											;DebugLog "IF: "+Int(p_bracepar$)
										Case "elseif"
											If Int(p_bracepar$)=0 Then parse_closebrace()
											If p_rootif=1 Then parse_freelastjp()
											;DebugLog "ELSEIF: "+Int(p_bracepar$)
										Case "else"
											If Int(p_bracepar$)=1 Then parse_closebrace()
											;DebugLog "ELSE: "+Int(p_bracepar$)
										Case "loop"
											parse_loop_check()
										Default
											parse_error("internal parsing error. unknwon brace type.")
									End Select
								Else
									parse_error("wrongbrace")
								EndIf
								
							;### Closing Brace }
							ElseIf sign=125 Then
								p_bracelevel=p_bracelevel-1
								If p_bracelevel<0 Then parse_error("brackets")
								p_p=p_p+1
								;Loop - Jump to Start
								If p_loopmode=1 Then
									If p_looplevel=p_bracelevel Then
										parse_setl(p_looprow)
										p_p=p_loopcol
										p_bracemode=1
										p_bracetype$="loop"
									EndIf
								EndIf
							
							;### Other Stuff -> Error
							Else
								parse_error("unexpected",Chr(sign))
								If p_bracemode=1 Then
									parse_error("Expecting '{' for "+p_bracetype)
								EndIf
							EndIf
							
						EndIf
						
						
						;##############################
						
						
						;Cancel on Error
						If p_error=1 Then
							p_parseinprogress=0
							Return 0
						EndIf
				
						
					EndIf
				EndIf
				
			EndIf
		Wend
		
		;Next Line
		p_pl=p_pl+1
	Wend
	
	;Finish
	p_parseinprogress=0
	Return 1
End Function
