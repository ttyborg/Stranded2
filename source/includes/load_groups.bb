;############################################ Load Groups

Function load_groups()
	Local stream=ReadFile("sys\groups.inf")
	If stream=0 Then Return 0
	Local in$,var$,val$
	Local i,equal
	While Not Eof(stream)
		in$=ReadLine(stream)
		equal=Instr(in$,"=")
		If Left(in$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(in$,equal-1))
			val$=trimspace(Mid(in$,equal+1,-1))
			split(val$,",")
			Select var$
			
				Case "object" group_add(Cclass_object,splits(0),splits(1))
				Case "unit" group_add(Cclass_unit,splits(0),splits(1))
				Case "item" group_add(Cclass_item,splits(0),splits(1))
				Case "info" group_add(Cclass_info,splits(0),splits(1))
				Case "building" group_add(Cclass_building,splits(0),splits(1))
				
				Default
					RuntimeError "Invalid GROUP Property '"+var$+"' - only object,unit,item,info and building are allowed"
			End Select
		EndIf
	Wend
	CloseFile(stream)
End Function

load_groups()


Function group_add(class,group$,name$)
	If name$="" Then name$=group$
	If group$="" Then RuntimeError "Missing Group Name"
	For g.Tgroup=Each Tgroup
		If g\class=class Then
			If g\group$=group$ Then
				RuntimeError "Group '"+group$+"' has already been defined"
			EndIf
		EndIf
	Next
	g.Tgroup=New Tgroup
	g\class=class
	g\group$=group$
	g\name$=name$
	;DebugLog "add group "+group$+","+name$
End Function

Function group_set$(class,group$)
	Return group$
	;For g.Tgroup=Each Tgroup
	;	If g\class=class Then
	;		If g\group$=group$ Then
	;			Return g\group$
	;		EndIf
	;	EndIf
	;Next
	;RuntimeError "Group '"+group$+"' has not been defined and cannot be assigned"
	;Return ""
End Function
