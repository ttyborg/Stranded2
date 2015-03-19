;############################################ COMMANDLINE

set_cmd$=Trim(CommandLine())

;### Scan Commandline
Function parse_commandline()
	;Default Mod
	set_moddir$="Stranded II"
	;Locals
	Local pp=1
	Local plen
	Local space
	Local cmd$,param$
	Local s$,partend
	Local inbb
	Local setupmods=0
	set_inbb=0
	plen=Len(set_cmd$)
	If plen>0 Then
		;Scan sign by sign
		While pp<plen
			s$=Mid(set_cmd$,pp,1)
			
			;Get Command Line Param introduced by "-"
			If s$="-" Then
				If Instr(set_cmd$,"-",pp+1)=0 Then
					cmd$=Mid(set_cmd$,pp,-1)
				Else
					cmd$=Mid(set_cmd$,pp,-1)
					partend=Instr(cmd$,"-",2)
					cmd$=Left(cmd$,partend-1)
				EndIf
				If Instr(cmd$," ") Then
						cmd$=Left(cmd$,Instr(cmd$," ")-1)
					EndIf
				cmd$=Trim(Lower(cmd$))
				Select cmd$
				
					;inbb
					Case "-inbb"
						con_add("COMMANDLINE: In BB IDE",Cbmpf_green):inbb=1: set_inbb=1
						pp=pp+Len(cmd$)
					;debug
					Case "-debug"
						con_add("COMMANDLINE: Debug mode is ON",Cbmpf_green):set_debug=1
						pp=pp+Len(cmd$)
					;win
					Case "-win"
						con_add("COMMANDLINE: Using windowed mode",Cbmpf_green):set_scrwin=2
						pp=pp+Len(cmd$)
					;setup
					Case "-setup"
						setupmods=1
						pp=pp+Len(cmd$)
					;mod
					Case "-mod"
						pp=pp+Len(cmd$)
						modstart=0
						modend=0
						modquote=0
						mods$=""
						For i=pp To plen
							mods$=Mid(set_cmd$,i,1)
							;Start
							If modstart=0 Then
								If mods$<>" " Then
									If mods$=Chr(34) Then
										modquote=1
										modstart=i+1
									Else
										modstart=i
									EndIf
								EndIf
							;End
							Else
								If modquote=0 Then
									If mods$=" " Then
										modend=i
										Exit
									EndIf
								Else
									If mods$=Chr(34) Then
										modend=i
										Exit
									EndIf
								EndIf
							EndIf
						Next
						If modend=0 Then modend=plen+1
						changed=0
						;Get Mod
						If modstart>0 And modstart<=plen Then
							If modstart<modend Then
								set_moddir$=Mid(set_cmd$,modstart,modend-modstart)
								pp=modend
								If modquote=1 Then pp=pp+1
								changed=1
							EndIf
						EndIf
						If changed=0 Then
							con_add("COMMANDLINE: Unable to get mod name",Cbmpf_red)
						EndIf
					;-default
					Default
						con_add("COMMANDLINE: '"+cmd$+"' is no valid Command Line Parameter" ,Cbmpf_red)
						pp=pp+Len(cmd$)
				
				End Select
			;No Command - Increase Position
			Else
				pp=pp+1
			EndIf
			
		Wend
	EndIf
	;Root Dir
	If inbb=1 Then
		set_rootdir$=CurrentDir()
	Else
		set_rootdir$=SystemProperty("APPDIR")
	EndIf
	If Right(set_rootdir$,1)<>"\" Then set_rootdir$=set_rootdir$+"\"
	;Mod Dir
	If set_moddir$<>"Stranded II"
		If FileType(set_rootdir$+"mods\"+set_moddir$)<>2 Then
			con_add( "COMMANDLINE: Mod '"+set_moddir$+"' does not exist - loading Stranded II" ,Cbmpf_red)
			set_moddir$="Stranded II"
		Else
			con_add( "COMMANDLINE: Loading mod '"+set_moddir$+"'" ,Cbmpf_green)
		EndIf
	EndIf
	;Set Path
	If FileType(set_rootdir$+"mods\"+set_moddir$)<>2 Then RuntimeError("Stranded II Mod dir/game files are missing")
	ChangeDir(set_rootdir$+"mods\"+set_moddir$)
	If set_debug Then con_add("Current Dir: "+CurrentDir())
	con_add()
	;Check Mod
	If setupmods=1 Then
		If FileType(set_rootdir$+"core\mods.inf")=1 Then
			DeleteFile(set_rootdir$+"core\mods.inf")
		EndIf
	EndIf
	mod_check()
End Function

parse_commandline()
