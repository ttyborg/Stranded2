;############################################ Parse Cache


;### Clear Parse Cache
Function clear_parsecache()
	For Tpc.Tpc=Each Tpc
		Delete Tpc
	Next
End Function


;### Add Parse Cache Line
Function add_parsecache(txt$)
	txt$=Trim(txt$)
	If txt$="" Then Return 0
	If Left(txt$="//",2) Then Return 0
	Tpc.Tpc=New Tpc
	Tpc\txt$=txt$
	Return 1
End Function


;### String to Parse Cache
Function loadstring_parsecache(txt$,sep$="Ś",clear=1)
	If clear Then clear_parsecache()
	Local substr$=txt$
	Local pos,temp$
	While Instr(substr$,sep$)
		pos=Instr(substr$,sep$)
		temp$=Left(substr$,pos-1)
		temp$=Trim(temp$)
		If temp$<>"" Then
			Tpc.Tpc=New Tpc
			Tpc\txt$=temp$
		EndIf
		substr$=Mid(substr$,pos+1,-1)
	Wend
	If substr$<>"" Then
		Tpc.Tpc=New Tpc
		Tpc\txt$=substr$
	EndIf
End Function


;### Set Parse Environment
Function parse_env(class=0,id=0,event$="",info$="")
	p_env_class=class
	p_env_id=id
	p_env_event$=event$
	p_env_info$=info$
End Function

;### Set Parse Cache and Environment by Script Source
Function set_parsecache(class,id,event$="",info$="")
	;Scripts
	For Tx.Tx=Each Tx
		If Tx\mode=0 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					;Event Check
					If event$<>"" Then
						If Instr(Tx\key$,"Ś"+event$)=0 Then Return 0
					EndIf
					;Add Parse Task
					Tpt.Tpt=New Tpt
					Tpt\class=class
					Tpt\id=id
					Tpt\event$=event$
					Tpt\info$=info$
					Tpt\script$=Tx\value$
					;K!
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	;Global Script
	If class=0 Then
		If id=0 Then
			If Instr(map_briefing_key$,"Ś"+event$) Then
				;Add Parse Task
				Tpt.Tpt=New Tpt
				Tpt\class=0
				Tpt\id=0
				Tpt\event$=event$
				Tpt\info$=info$
				Tpt\script$=map_briefing$
			EndIf
		EndIf
	EndIf
	;Failed!
	Return 0
End Function

;### Parse an Global Event
Function parse_globalevent(event$,info$="")
	;Game Script
	If Instr(game_scriptk$,"Ś"+event$) Then
		;Add Parse Task
		Tpt.Tpt=New Tpt
		Tpt\class=-1
		Tpt\id=0
		Tpt\event$=event$
		Tpt\info$=info$
		Tpt\script$=game_script$
	EndIf
	;Global Script
	If Instr(map_briefing_key$,"Ś"+event$) Then
		;Add Parse Task
		Tpt.Tpt=New Tpt
		Tpt\class=0
		Tpt\id=0
		Tpt\event$=event$
		Tpt\info$=info$
		Tpt\script$=map_briefing$
	EndIf
	;Scripts
	For Tx.Tx=Each Tx
		If Tx\mode=0
			If Instr(Tx\key$,"Ś"+event$) Then
				;Add Parse Task
				Tpt.Tpt=New Tpt
				Tpt\class=Tx\parent_class
				Tpt\id=Tx\parent_id
				Tpt\event$=event$
				Tpt\info$=info$
				Tpt\script$=Tx\value$
			EndIf
		EndIf
	Next
	;Object Definitions
	For i=1 To Cobject_count
		If Instr(Dobject_scriptk$(i),"Ś"+event$) Then
			For Tobject.Tobject=Each Tobject
				If Tobject\typ=i Then
					;Add Parse Task
					Tpt.Tpt=New Tpt
					Tpt\class=Cclass_object
					Tpt\id=Tobject\id
					Tpt\event$=event$
					Tpt\info$=info$
					Tpt\script$=Dobject_script$(i)
				EndIf
			Next
		EndIf
	Next
	;Unit Definitions
	For i=1 To Cunit_count
		If Instr(Dunit_scriptk$(i),"Ś"+event$) Then
			For Tunit.Tunit=Each Tunit
				If Tunit\typ=i Then
					;Add Parse Task
					Tpt.Tpt=New Tpt
					Tpt\class=Cclass_unit
					Tpt\id=Tunit\id
					Tpt\event$=event$
					Tpt\info$=info$
					Tpt\script$=Dunit_script$(i)
				EndIf
			Next
		EndIf
	Next
	;Item Definitions
	For i=1 To Citem_count
		If Instr(Ditem_scriptk$(i),"Ś"+event$) Then
			For Titem.Titem=Each Titem
				If Titem\typ=i Then
					;Add Parse Task
					Tpt.Tpt=New Tpt
					Tpt\class=Cclass_item
					Tpt\id=Titem\id
					Tpt\event$=event$
					Tpt\info$=info$
					Tpt\script$=Ditem_script$(i)
				EndIf
			Next
		EndIf
	Next
	;Info Definitions
	For i=1 To Cinfo_count
		If Instr(Dinfo_scriptk$(i),"Ś"+event$) Then
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\typ=i Then
					;Add Parse Task
					Tpt.Tpt=New Tpt
					Tpt\class=Cclass_info
					Tpt\id=Tinfo\id
					Tpt\event$=event$
					Tpt\info$=info$
					Tpt\script$=Dinfo_script$(i)
				EndIf
			Next
		EndIf
	Next
End Function


;### Add a Parse Task
Function parse_task(class,id,event$,info$,script$)
	;Add Parse Task
	Tpt.Tpt=New Tpt
	Tpt\class=class
	Tpt\id=id
	Tpt\event$=event$
	Tpt\info$=info$
	Tpt\script$=script$
End Function


;### Parse All
Function parse_all()
	;Parse All Parse Tasks
	For Tpt.Tpt=Each Tpt
		If set_debug=1 Then con_add("PARSE: "+p_env_event$+ " @ "+p_env_class+","+p_env_id+" "+p_env_info$,Cbmpf_yellow)
		p_env_class=Tpt\class
		p_env_id=Tpt\id
		p_env_event$=Tpt\event$
		p_env_info$=Tpt\info$
		loadstring_parsecache(Tpt\script$,"Ś")
		parse()
		Delete Tpt
	Next
	;Dump Vars (Locals for Kill Events)
	If tmp_dumpvars=1 Then
		For Tx.Tx=Each Tx
			If Tx\mode=4 Then
				If Tx\typ=1 Then
					Delete Tx
				EndIf
			EndIf
		Next
		tmp_dumpvars=0
	EndIf
	;Dump Kill Cache
	For Tpkill.Tpkill=Each Tpkill
		FreeEntity Tpkill\h
		Delete Tpkill
	Next
End Function


;### Parse Selective
Function parse_sel(class,id,event$)
	tasks=0
	For Tpt.Tpt=Each Tpt
		If Tpt\class=class Then
			If Tpt\id=id Then
				If Tpt\event$=event$ Then
					p_env_class=Tpt\class
					p_env_id=Tpt\id
					p_env_event$=Tpt\event$
					p_env_info$=Tpt\info$
					loadstring_parsecache(Tpt\script$,"Ś")
					If set_debug=1 Then con_add("PARSE (OBJECT,EVENT): "+p_env_event$+ " @ "+p_env_class+","+p_env_id+" "+p_env_info$,Cbmpf_yellow)
					parse()
					Delete Tpt
					tasks=tasks+1
				EndIf
			EndIf
		EndIf
	Next
	Return tasks
End Function


;### Parse Selective Event
Function parse_sel_event(event$,noskip=0)
	For Tpt.Tpt=Each Tpt
		If Tpt\event$=event$ Then
			p_env_class=Tpt\class
			p_env_id=Tpt\id
			p_env_event$=Tpt\event$
			p_env_info$=Tpt\info$
			loadstring_parsecache(Tpt\script$,"Ś")
			If set_debug=1 Then con_add("PARSE (EVENT): "+p_env_event$+ " @ "+p_env_class+","+p_env_id+" "+p_env_info$,Cbmpf_yellow)
			parse(noskip)
			Delete Tpt
		EndIf
	Next
End Function


;### Emulate Event
Function parse_emuevent(class,id,event$,info$="")
	;Definition
	Select class
		Case Cclass_object
			If con_object(id) Then
				If Instr(Dobject_scriptk$(TCobject\typ),"Ś"+event$) Then
					parse_task(class,id,event$,info$,Dobject_script(TCobject\typ))
				EndIf
			EndIf
		Case Cclass_unit
			If con_unit(id) Then
				If Instr(Dunit_scriptk$(TCunit\typ),"Ś"+event$) Then
					parse_task(class,id,event$,info$,Dunit_script(TCunit\typ))
				EndIf
			EndIf
		Case Cclass_item
			If con_item(id) Then
				If Instr(Ditem_scriptk$(TCitem\typ),"Ś"+event$) Then
					parse_task(class,id,event$,info$,Ditem_script(TCitem\typ))
				EndIf
			EndIf
		Case Cclass_info
			If con_info(id) Then
				If Instr(Dinfo_scriptk$(TCinfo\typ),"Ś"+event$) Then
					parse_task(class,id,event$,info$,Dinfo_script(TCinfo\typ))
				EndIf
			EndIf
	End Select
	;Normal Scripts
	set_parsecache(class,id,event$,info$)
End Function


;############################################ Parse Parameters


;### Extract Parameter
Function param$(rawvar=0)
	Local i,s
	Local start
	Local brace
	Local quote
	
	Local con_numerics=0
	Local con_strings=0
	Local con_dot=0
	Local con_braces=0
	
	Local got=0
	
	If p_internal=0 Then
		
		p_gotparam=0
		
		;Scan
		For i=p_p To p_len
			s=Asc(Mid(p_l$,i,1))
			
			;Find Start
			If start=0 Then
				If s<>32 Then start=i
			EndIf
			
			;Numerics
			If s>47 And s<58 Then
				con_numerics=con_numerics+1
				
			;Dot
			ElseIf s=46 Then
				con_dot=1
			
			;Quotes
			ElseIf s=34 Then
				quote=1-quote
				con_strings=con_strings+1
				If quote=0 Then
					If brace=0 Then
						got=1
						Exit
					EndIf
				EndIf
			
			;Brace
			ElseIf s=40 Then
				brace=brace+1
			ElseIf s=41 Then
				con_braces=con_braces+1
				brace=brace-1
				If brace<=0 Then
					If quote=0 Then
						If brace<0 Then
							got=2
						Else
							got=1
						EndIf
						Exit
					EndIf
				EndIf
			
			;End on ; or ,
			ElseIf s=59 Or s=44 Then
				If quote=0 Then
					If brace=0 Then
						got=2
						Exit
					EndIf
				EndIf
						
			EndIf
				
		Next
		
		;Got
		Local pend
		If got=1 Then
			pend=1	
		Else
			pend=0
		EndIf
		
		;Extract
		txt$=Mid(p_l$,start,i-start+pend)
		p_p=i+pend
		
		;Remove @
		If Left(txt$,1)="@" Then txt$=Mid(txt$,2,-1)
		
		;Skip
		If rawvar=2 Then Return txt$
		
		;Vars
		If rawvar=0 Then
			If Instr(txt$,"$")		
				txt$=var_rep(txt$)
			EndIf
		Else
			con_strings=1
		EndIf
				
		;Math
		If con_strings=0 Then
			If con_dot=0 Then
				txt$=parse_math(txt$)
			Else
				If Len(txt$)>(con_numerics+1) Then
					txt$=parse_math(txt$)
				EndIf
			EndIf
		;Text?
		Else
			If con_braces>0 Then
				txt$=parse_math(txt$)
			Else
				txt$=trimtext$(txt$)
			EndIf
		EndIf

		;Return
		p_gotparam=1
		Return txt$
		
	;Internal
	Else
		
		;Scan
		Local l=Len(p_internaltxt$)
		For i=1 To l
			s=Asc(Mid(p_internaltxt$,i,1))
			
			;Find Start
			If start=0 Then
				If s<>32 Then start=i
			EndIf
			
			;Numerics
			If s>47 And s<58 Then
				con_numerics=con_numerics+1

			;Dot
			ElseIf s=46 Then
				con_dot=1
				
			;Quotes
			ElseIf s=34 Then
				quote=1-quote
				con_strings=con_strings+1
				If quote=0 Then
					If brace=0 Then
						got=1
						Exit
					EndIf
				EndIf
			
			;Brace
			ElseIf s=40 Then
				brace=brace+1
			ElseIf s=41 Then
				con_braces=con_braces+1
				brace=brace-1
				If brace<=0 Then
					If quote=0 Then
						got=2
						Exit
					EndIf
				EndIf
			
			;End on ; or ,
			ElseIf s=59 Or s=44 Then
				If quote=0 Then
					If brace=0 Then
						got=2
						Exit
					EndIf
				EndIf
				
			EndIf
		
		Next
		
		;Got
		If got=1 Then
			pend=1
		Else
			pend=0
		EndIf
		
		;Extract
		If p_internaltxt$="" Then
			txt$=""
		Else
			txt$=Mid(p_internaltxt$,start,i-start+pend)
		EndIf
		
		;Remove @
		If Left(txt$,1)="@" Then txt$=Mid(txt$,2,-1)
		
		;con_add("internal param raw text: '"+txt$+"' <- "+p_internaltxt$)
		
		;Skip
		If rawvar=2 Then Return txt$
		
		;Vars
		If rawvar=0 Then
			If Instr(txt$,"$")		
				txt$=var_rep(txt$)
			EndIf
		Else
			con_strings=1
		EndIf
		
		;Math
		If con_strings=0 Then
			If con_dot=0 Then
				txt$=parse_math(txt$)
			Else
				If Len(txt$)>(con_numerics+1) Then
					txt$=parse_math(txt$)
				EndIf
			EndIf
		;Text?
		Else
			If con_braces>0 Then
				txt$=parse_math(txt$)
			Else
				txt$=trimtext$(txt$)
			EndIf
		EndIf
		
		;Cut away
		p_internaltxt$=Mid(p_internaltxt$,i+pend,-1)
		
		;Return
		Return txt$
		
	EndIf
End Function


;### Extract String Parameter
Function params$()
	Local i,s
	Local start
	Local brace
	Local quote
	
	Local con_strings=0
	
	Local got=0
	
	If p_internal=0 Then
		
		p_gotparam=0
		
		;Scan
		For i=p_p To p_len
			s=Asc(Mid(p_l$,i,1))
			
			;Find Start
			If start=0 Then
				If s<>32 Then start=i
			EndIf
						
			;Quotes
			If s=34 Then
				quote=1-quote
				con_strings=con_strings+1
				If quote=0 Then
					got=1
					Exit
				EndIf
			EndIf
			
			;End
			If start>0 Then
				If quote=0 Then
					Select s
						;Cancel on Space ) , ;
						Case 32,41,44,59
							got=2
							Exit
					End Select
				EndIf
			EndIf
				
		Next
		
		;Got
		Local pend
		If got=1 Then
			pend=1	
		Else
			pend=0
		EndIf
		
		;Extract
		txt$=Mid(p_l$,start,i-start+pend)
		p_p=i+pend
		
		;Vars
		If Instr(txt$,"$")		
			txt$=var_rep(txt$)
		EndIf
		
		;Text
		txt$=trimtext$(txt$)
		
		;Return
		p_gotparam=1
		Return txt$
		
	;Internal
	Else
		
		;Scan
		Local l=Len(p_internaltxt$)
		For i=1 To l
			s=Asc(Mid(p_internaltxt$,i,1))
			
			;Find Start
			If start=0 Then
				If s<>32 Then start=i
			EndIf
			
			;Quotes
			If s=34 Then
				quote=1-quote
				con_strings=con_strings+1
				If quote=0 Then
					got=1
					Exit
				EndIf		
			EndIf
			
			;End
			If start>0 Then
				If quote=0 Then
					Select s
						;Cancel on Space ) , ;
						Case 32,41,44,59
							got=2
							Exit
					End Select
				EndIf
			EndIf
		
		Next
		
		;Got
		If got=1 Then
			pend=1
		Else
			pend=0
		EndIf
		
		;Extract
		txt$=Mid(p_internaltxt$,start,i-start+pend)
		
		;Vars
		If Instr(txt$,"$")		
			txt$=var_rep(txt$)
		EndIf

		;Text
		txt$=trimtext$(txt$)

		;Cut away
		p_internaltxt$=Mid(p_internaltxt$,i+pend,-1)
		
		;Return
		Return txt$
		
	EndIf
End Function








;############################################ Parser Functions


;### Find and Jump to Brace (Mode Search for: 0 - opening&closing, 1 - opening, 2 - closing)
Function parse_brace(mode=0,direct=1,matching=1)
	Local s
	Local tl,tp
	Local temp_p_p=p_p
	Local temp_p_len=p_len
	Local comment=0,instring=0
	Local level=1
	For tl=p_pl To p_lines-1
		temp_p_len=Len(Dpc$(tl))
		For tp=temp_p_p To temp_p_len
		
			s=Asc(Mid(Dpc$(tl),tp,1))
			
			;Strings / Comments
			If s=34 And comment=0 Then instring=1-instring		;Ignore Strings
			If instring=0 Then
				If (Mid(Dpc$(tl),tp,2))="//" Then Exit			;Skip everything after //
				If comment=0 Then
					If (Mid(Dpc$(tl),tp,2))="/*" Then comment=1	;Start Comment
				Else
					If (Mid(Dpc$(tl),tp,2))="*/" Then comment=0	;End Comment
				EndIf
			EndIf
			
			;Scan for brace if not in comment or string
			If instring=0 And comment=0 Then
				;Find matching brace?
				If matching Then
					If s=123 Then level=level+1;:DebugLog "+{ BRACE @ "+tl+"|"+tp+" -> "+level
					If s=125 Then level=level-1;:DebugLog "-} BRACE @ "+tl+"|"+tp+" -> "+level
				EndIf
			
				;Opening Brace - Jump & Return 1
				If s=123 Then
					If mode<2 Then
						;DebugLog "*{ BRACE @ "+tl+"|"+tp
						parse_setl(tl):p_p=tp:Return 1
					EndIf
				;Closing Brace - Jump & Return 2
				ElseIf s=125 Then
					If mode=0 Or mode=2 Then
						If ((matching=1 And level=0) Or matching=0) Then
							;DebugLog "*} BRACE @ "+tl+"|"+tp
							parse_setl(tl):p_p=tp:Return 2
						EndIf
					EndIf
				EndIf
			EndIf
			
			;Direct Check (error if something besides the expected brace is found)
			If direct=1 Then
				If s<>32 Then
					If s<>9 Then
						If comment=0 Then
							If mode=0 Then
								parse_error("expect","'{' or '}'")
							ElseIf mode=1 Then
								parse_error("expect","'{'")
							ElseIf mode=2 Then
								parse_error("expect","'}'")
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		ppt=1
	Next
	;No Brace - Return 0
	Return 0
End Function


;### Skip Spaces in Line, Return 0 if there is nothing beside spaces
Function parse_skipspace()
	Local s
	For p_p=p_p To p_len
		s=Asc(Mid(p_l$,p_p,1))
		If s<>32 Then
			If s<>9 Then
				Return 1
			EndIf
		EndIf
	Next
	Return 0
End Function

;### Extract Command at current String Position
Function parse_com()
	Local s
	Local start=p_p
	Local endc=0
	p_com$=""
	For p_p=p_p To p_len
		s=Asc(Mid(p_l$,p_p,1))
		
		;End at Position that is not A-Z,a-z,0-9,_
		If s<48 Then endc=p_p
		If s>57 And s<65 Then endc=p_p
		If s>90 And s<95 Then endc=p_p
		If s=96 Then endc=p_p
		If s>122 Then endc=p_p
		
		If endc<>0 Then
			If s=36 Then p_com$="$":Return 1
			If s=64 Then p_com$="@":Return 1
			Exit
		EndIf
	Next
	
	If endc=0 Then endc=p_p
	If (endc)-start>0 Then
		p_com$=Lower(Mid(p_l$,start,(endc)-start))
		Return 1
	Else
		Return 0
	EndIf
End Function


;### Parse Find End of Expression Part
Function parse_end(txt$,pos)
	Local i,s,l
	l=Len(txt$)
	For i=pos To l
		s=Asc(Mid(txt$,i,1))
		
		;End at Position that is not A-Z,a-z,0-9,_
		If s<48 Then Return i 
		If s>57 And s<65 Then Return i 
		If s>90 And s<95 Then Return i
		If s=96 Then Return i
		If s>122 Then Return i
		
	Next
	Return i
End Function

;### Parameter (1) or Semikolon (0)
Function parse_param()
	Local s,i
	For i=p_p To p_len
		s=Asc(Mid(p_l$,i,1))
		If s<>32 Then
			;Semikolon
			If s=59 Then Return 0
			;Other
			Exit
		EndIf
	Next
	Return 1
End Function


;### Find and Jump to Semikolon
Function parse_semi(error=1)
	Local semitxt$=Mid(p_l$,p_p,-1)
	Local s,i
	For i=p_p To p_len
		s=Asc(Mid(p_l$,i,1))
		If s<>32 Then
			;Semikolon
			If s=59 Then p_p=i+1:Return 1
			;Other
			Exit
		EndIf
	Next
	If error=1 Then parse_error("expect","semicolon")
	Return 0
End Function


;### Find and Jump to Comma
Function parse_comma(error=1)
	Local s,i
	
	If p_internal=0 Then
	
		For i=p_p To p_len
			s=Asc(Mid(p_l$,i,1))
			If s<>32 Then
				;Comma (Return 1)
				If s=44 Then p_p=i+1:Return 1
				;Other
				If error=1 Then Exit
			EndIf
		Next
		;Nothing
		If error=1 Then parse_error("expect","parameter resp. ','")
		Return 0
		
	Else
		
		;DebugLog "PARSE COMMA INTERNAL ("+error+") -> "+p_internaltxt$+" ("+p_com$+")"
		
		l=Len(p_internaltxt$)
		For i=1 To l
			s=Asc(Mid(p_internaltxt$,i,1))
			If s<>32 Then
				;Comma (Return 1)
				If s=44 Then
					;Cut off
					p_internaltxt$=Mid(p_internaltxt$,i+1,-1)
					Return 1
				EndIf
				;Other
				If error=1 Then
					Exit
				;Do not try to get more Optional Params after )
				ElseIf s=41 Then
					Exit
				EndIf
			EndIf
		Next
		;Nothing
		If error=1 Then parse_error("expect","parameter resp. ','")
		Return 0
	
	EndIf
		
End Function


;### Find and Jump to Param/Semikolon
Function parse_comsemi(error=1)
	Local s
	For p_p=p_p To p_len
		s=Asc(Mid(p_l$,p_p,1))
		;Comma (Return 1)
		If s=44 Then p_p=p_p+1:Return 1
		;Semikolon (Return 2)
		If s=59 Then p_p=p_p+1:Return 2
	Next
	;Nothing
	If error Then parse_error("expect","parameter or semicolon")
	Return 0
End Function

;### Parser Set Jump Point
Function parse_setjp(l,pos,level,typ$,name$)
	Tpj.Tpj=New Tpj
	Tpj\l=l
	Tpj\pos=pos
	Tpj\level=level
	Tpj\typ$=typ$
	Tpj\name$=name$
End Function

;### Parser Free Last Jump Point
Function parse_freelastjp()
	lastpj.Tpj=Last Tpj
	Delete lastpj
End Function

;### Parse Set Line
Function parse_setl(l)
	p_pl=l					;Line
	p_l$=Dpc$(p_pl)			;Line Text
	p_len=Len(p_l$)			;Line Length
End Function


;### Get Bool of last Condition JP
Function parse_getjpbool(level)
	Local bool=-1
	For Tpj.Tpj=Each Tpj
		;DebugLog "* Level: "+Tpj\level+" Typ: "+Tpj\typ$+" - Scanlevel: "+level
		If Tpj\level=level Then
			If Tpj\typ$="if" Or Tpj\typ$="elseif" Then
				bool=Int(Tpj\name$)
			EndIf
		EndIf
	Next
	If bool=-1 Then parse_error("else")
	Return bool
End Function


;### Get Class out of String
Function parse_getclass(txt$,mode=1)
	Select txt$
		Case "object",Cclass_object Return Cclass_object
		Case "unit",Cclass_unit Return Cclass_unit
		Case "item",Cclass_item Return Cclass_item
		Case "info",Cclass_info Return Cclass_info
		Case "state",Cclass_state Return Cclass_state
	End Select
	If mode=1 Then
		If txt$="self" Or txt$="-1" Then Return -1
	EndIf
	If mode=0 Then parse_error("class",txt$)
End Function


;### Get Class Text
Function parse_getclasstxt$(class)
	Select class
		Case Cclass_object Return "object"
		Case Cclass_unit Return "unit"
		Case Cclass_item Return "item"
		Case Cclass_info Return "info"
		Case Cclass_state Return "state"
		Case -1 Return "self"
		Default Return "unknown class"
	End Select
End Function


;### Get State out of String
Function parse_getstate(txt$)
	Select txt$
		Case "bleeding",Cstate_bleeding Return Cstate_bleeding
		Case "intoxication",Cstate_intoxication Return Cstate_intoxication
		Case "pus",Cstate_pus Return Cstate_pus
		Case "fire",Cstate_fire Return Cstate_fire
		Case "eternalfire","eternal fire",Cstate_eternalfire Return Cstate_eternalfire
		Case "frostbite",Cstate_frostbite Return Cstate_frostbite
		Case "fracture",Cstate_fracture Return Cstate_fracture
		Case "electroshock",Cstate_electroshock Return Cstate_electroshock
		Case "bloodrush",Cstate_bloodrush Return Cstate_bloodrush
		Case "dizzy",Cstate_dizzy Return Cstate_dizzy
		Case "wet",Cstate_wet Return Cstate_wet
		Case "fuddle",Cstate_fuddle Return Cstate_fuddle
		
		Case "healing",Cstate_healing Return Cstate_healing
		Case "invulnerability","invulnerable",Cstate_invulnerability Return Cstate_invulnerability
		Case "tame",Cstate_tame Return Cstate_tame
		
		Case "action",Cstate_action Return Cstate_action
		Case "flare",Cstate_flare Return Cstate_flare
		Case "smoke",Cstate_smoke Return Cstate_smoke
		Case "light",Cstate_light Return Cstate_light
		Case "particles",Cstate_particles Return Cstate_particles

		;Internal State Stuff
		Case "physics",Cstate_phy Return Cstate_phy
		Case "buildplace",Cstate_buildplace Return Cstate_buildplace
		Case "link",Cstate_link Return Cstate_link
		Case "ai_stick",Cstate_ai_stick Return Cstate_ai_stick
		Case "speed","speedmod",Cstate_speedmod Return Cstate_speedmod
		Case "ghost",Cstate_ghost Return Cstate_ghost
		
		Default
			parse_error("state",txt$)
			Return 0
	End Select
End Function


;### Get AI Signal
Function parse_getaisignal(txt$)
	Select txt$
		Case "food","eat",ai_food Return ai_food
		Case "attract","goto",ai_attract Return ai_attract
		Case "distract","flee",ai_distract Return ai_distract
	
		Default
			parse_error("signal",txt$)
			Return 0
	End Select
End Function


;### Get AI Mode
Function parse_getaimode(txt$)
	Select txt$
		Case "food","eat" Return ai_getfood
		Case "attract","goto" Return ai_movetarget
		Case "distract","flee" Return ai_flee
		Case "idle",ai_idle Return ai_idle
		Case "move",ai_move Return ai_move
		Case "movel",ai_movel Return ai_movel
		Case "mover",ai_mover Return ai_mover
		Case "turnl",ai_turnl Return ai_turnl
		Case "turnr",ai_turnr Return ai_turnr
		Case "return",ai_return Return ai_return
		Case "sreturn",ai_return Return ai_sreturn
		Case "hunt",ai_hunt Return ai_hunt
		Default
			parse_error("aimode",txt$)
			Return 0
	End Select	
End Function


;### Is Int?
Function parse_isint(txt$)
	Local l=Len(txt$)
	Local s
	For i=1 To l
		s=Asc(Mid(txt$,i,1))
		;Return 0 if it is out auf Number ASCII Range
		If s>57 Then Return 0
		If s<48 Then Return 0
	Next
	;Check Ok
	Return 1
End Function


;### Parse Kill Add
Function parse_kill_add(class,id,model,spawntimer=0)
	Tpkill.Tpkill=New Tpkill
	Tpkill\class=class
	Tpkill\id=id
	Tpkill\h=model
	Tpkill\spawntimer=spawntimer
End Function


;### Parse Kill Get
Function parse_kill_get(class,id,value=0)
	For Tpkill.Tpkill=Each Tpkill
		If Tpkill\class=class Then
			If Tpkill\id=id Then
				Select value
					;0 - Model
					Case 0 Return Tpkill\h
					;1 - Spawntimer
					Case 1 Return Tpkill\spawntimer
					;Default
					Default Return 0
				End Select
			EndIf
		EndIf
	Next
	Return 0
End Function


;### Load Script
Function parse_loadscript$(file$,getstartl=1,startl$="")
	If Instr(file$,".")=0 Then Return file$
	;con_add("loadscript "+file$)
	stream=ReadFile(file$)
	If stream=0 Then
		If set_debug=1 Then con_add("ERROR: Unable to load external script '"+file$+"'",Cbmpf_red)
		Return ""
	EndIf
	Local txt$=""
	;Get Startline
	If getstartl=1 Then
		If parse_comma(0) Then							;Startline
			startl$=params()
		EndIf
	EndIf
	;con_add("loadscript "+file$+" - startl: "+startl$)
	;Normal Read
	If startl$="" Then
		While Not Eof(stream)
			txt$=txt$+ReadLine(stream)+"Ś"
		Wend
	;Partial Read
	Else
		Local cacheit=0
		Local in$
		While Not Eof(stream)
			in$=ReadLine(stream)
			If cacheit=1 Then
				If Left(in$,3)="//~" Then Exit
				txt$=txt$+in$+"Ś"
			Else
				If in$="//~"+startl$ Then cacheit=1
			EndIf
		Wend
	EndIf
	;Remove End Break
	If Right(txt$,1)="Ś" Then
		txt$=Left(txt$,Len(txt$)-1)
	EndIf
	;Close
	CloseFile stream
	;con_add("loadscript txt: "+txt$)
	Return txt$
End Function


;### Parse Error
Function parse_error(id$,param$="")
	;Error!
	p_error=1
	
	;Oppress?
	If p_noerror>0 Then Return 0
	
	;Reset
	Dim p_erd$(10)

	;Error Message
	Select id$
		Case "uk"
			p_erd$(0)="'"+p_com$+"' is an unknown script command"
		Case "brackets"
			p_erd$(0)="Mismatched brackets"
		Case "wrongbrace"
			p_erd$(0)="Opening brace '{' is not expected"
		Case "expect"
			p_erd$(0)="Expecting "+param$
		Case "var_name"
			p_erd$(0)="'"+param$+"' is an invalid name for a variable!"
			p_erd$(1)="You can use 'A-Z','a-z','0-9' And '_'"
		Case "zerodiv"
			p_erd$(0)="Division by zero is not allowed "+param$
		Case "operators"
			p_erd$(0)="Invalid term syntax: too many operators in '"+param$+"'"
		Case "else"
			p_erd$(0)="'else' or 'elseif' without matching 'if'"
		Case "class"
			p_erd$(0)="'"+param$+"' is no valid class (object,unit,item,info,state[,self])"
		Case "wrongclass"
			p_erd$(0)="Class '"+param$+"' is not expected"
		Case "model"
			p_erd$(0)="'"+param$+"' command needs to be executed at a model (object,unit,item)"
		Case "object"
			p_erd$(0)="Object/Unit/Item/Info with ID '"+param$+"' does not exist"
		Case "infotype"
			p_erd$(0)="Info of type '"+Dinfo_name$(Int(param$))+"' expected"
		Case "txtsource"
			p_erd$(0)="'"+param$+"' is no valid text source"
			p_erd$(1)="Use either an info id or a text file"
		Case "typerange"
			p_erd$(0)="Type '"+param$+"' is out of range"
		Case "state"
			p_erd$(0)="'"+param$+"' is no valid state"
		Case "signal"
			p_erd$(0)="'"+param$+"' is no valid AI signal"
		Case "aimode"
			p_erd$(0)="'"+param$+"' is no valid AI mode"
		Case "bwo"
			p_erd$(0)="Expression '"+param$+"' begins with an unexpected operator!"
			p_erd$(1)="Probably it is a part of a longer expression. Please set additional"
			p_erd$(2)="brackets and/or use variables instead of functions."
		Case "alreadylooping"
			p_erd$(0)="'loop' can not be used within a loop"
		Case "looptype"
			p_erd$(0)="'"+param$+"' is no valid loop type"
		Case "unexpected"
			p_erd$(0)="Unexpected '"+param$+"'"
		Default
			p_erd$(0)=id$
	End Select
	
	;Error Script & Location Details
	p_erd$(5)="Script: "
	Select p_env_class
		Case -2
			p_erd$(5)=p_erd$(5)+"Virtual Script (for object which has already been deleted)"
		Case -1
			p_erd$(5)=p_erd$(5)+"Game Script (game.inf)"
		Case Cclass_global
			p_erd$(5)=p_erd$(5)+"Global Map Script"
		Case Cclass_object
			If con_object(p_env_id) Then
				p_erd$(5)=p_erd$(5)+"Object "+p_env_id+" ("+Dobject_name$(TCobject\typ)+" type "+TCobject\typ+")"
			Else
				p_erd$(5)=p_erd$(5)+"Object "+p_env_id+" (DOES NOT EXIST!)"
			EndIf
		Case Cclass_unit
			If con_unit(p_env_id) Then
				p_erd$(5)=p_erd$(5)+"Unit "+p_env_id+" ("+Dunit_name$(TCunit\typ)+" type "+TCunit\typ+")"
			Else
				p_erd$(5)=p_erd$(5)+"Unit "+p_env_id+" (DOES NOT EXIST!)"
			EndIf
		Case Cclass_item
			If con_item(p_env_id) Then
				p_erd$(5)=p_erd$(5)+"Item "+p_env_id+" ("+Ditem_name$(TCitem\typ)+" type "+TCitem\typ+")"
			Else
				p_erd$(5)=p_erd$(5)+"Item "+p_env_id+" (DOES NOT EXIST!)"
			EndIf
		Case Cclass_info
			If con_info(p_env_id) Then
				p_erd$(5)=p_erd$(5)+"Info "+p_env_id+" ("+Dinfo_name$(TCinfo\typ)+" type "+TCinfo\typ+")"
			Else
				p_erd$(5)=p_erd$(5)+"Info "+p_env_id+" (DOES NOT EXIST!)"
			EndIf
		Case Cclass_state
			If con_object(p_env_id) Then
				p_erd$(5)=p_erd$(5)+"State "+p_env_id+" ("+Dstate_name$(TCstate\typ)+" type "+TCstate\typ+")"
			Else
				p_erd$(5)=p_erd$(5)+"State "+p_env_id+" (DOES NOT EXIST!)"
			EndIf
		Default
			p_erd$(5)=p_erd$(5)+"UNKNOWN SCRIPT ("+p_env_class+", "+p_env_id+")"
	End Select
	If p_env_info$<>"" Then p_erd$(6)="Info: "+p_env_info$
	If p_env_event$<>"" Then p_erd$(7)="Event: "+p_env_event$
	p_erd$(8)="Row: "+(p_pl+1)
	p_erd$(9)="Col: "+p_p
	p_erd$(10)="Script: "+p_l$
	
	;Output
	If set_debug=1 Then
		sfx(sfx_fail)
		con_open()
		con_add()
		con_add("SCRIPT ERROR:",Cbmpf_red)
		For i=0 To 10
			If p_erd$(i)<>"" Then
				con_add(p_erd$(i),Cbmpf_red)
			EndIf
		Next
	Else
		con_add("SCRIPT ERROR: "+p_erd$(5)+" Row:"+(p_pl+1)+" Col:"+p_p+" ('debug' for details)",Cbmpf_red)
	EndIf
	
	;>>>>> DEBUG STOP
	;Stop
	;<<<<<
End Function


Function parse_isevent(txt$)
	If Instr(txt$,";") Then Return 0
	Return 1
End Function


Function parse_classid()
	tmp_gclass=0
	tmp_gid=0
	;Class
	Local class$=param()
	Select class$
		Case "object",Cclass_object tmp_gclass=Cclass_object
		Case "unit",Cclass_unit tmp_gclass=Cclass_unit
		Case "item",Cclass_item tmp_gclass=Cclass_item
		Case "info",Cclass_info tmp_gclass=Cclass_info
		Case "state",Cclass_state tmp_gclass=Cclass_state
		Case "self" tmp_gclass=p_env_class : tmp_gid=p_env_id : Return
		Case "player" tmp_gclass=Cclass_unit : tmp_gid=g_player : Return
	End Select
	;ID
	parse_comma(1)
	tmp_gid=param()
	Return
End Function


;Parse Setscript
Function parse_editorsetscript(class,id)
	;Clear Parsecache
	clear_parsecache()
	;Execute SetScript
	If ed_setscript$<>"" Then
		p_env_class=class
		p_env_id=id
		p_env_event$="edset"
		p_env_info$="editor setscript"
		loadstring_parsecache(ed_setscript$,"Ś")
		parse(1)
	EndIf
	;Edset-Event
	parse_emuevent(class,id,"edset","editor setscript")
	parse_sel_event("edset",1)
End Function
