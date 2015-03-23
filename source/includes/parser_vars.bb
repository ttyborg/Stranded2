;############################################ Var Stuff


;### Get Var
Function var_get$(name$)
	;Scan Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=name$ Then
						Return Tx\value$
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Scan Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=name$ Then
				Return Tx\value$
			EndIf
		EndIf
	Next
	;Failed
	Return 0
End Function


;### Set Var
Function var_set(name$,value$)
	;Existing Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=name$ Then
						Tx\value$=value$
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Existing Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=name$ Then
				Tx\value$=value$
				Return 1
			EndIf
		EndIf
	Next
	;New
	If Not var_name(name$) Then parse_error("var_name")
	Tx.Tx=New Tx
	Tx\mode=1
	Tx\key$=name$
	Tx\value$=value$
	Return 1
End Function


;### Free Var
Function var_free(name$)
	;Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=name$ Then
						Delete Tx
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=name$ Then
				Delete Tx
				Return 1
			EndIf
		EndIf
	Next
	;Fail
	Return 0
End Function


;### Var exists
Function var_exists(name$)
	;DebugLog "var exists: "+name$
	;Scan Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=name$ Then
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Scan Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=name$ Then
				Return 1
			EndIf
		EndIf
	Next
	;Fail
	Return 0	
End Function


;### Var Bool
Function var_bool(name$)
	Local value$=var_get(name$)
	If Int(value$)=0 Then
		Return 0
	Else
		Return 1
	EndIf
End Function


;### Check if Name is a valid Name for a Variable
Function var_name(name$)
	Local i,l,s
	l=Len(name$)
	For i=1 To l
		s=Asc(Mid(name$,i,1))
		;Invalid if not A-Z,a-z,0-9,_
		If s<48 Then Return 0
		If s>57 And s<65 Then Return 0
		If s>90 And s<95 Then Return 0
		If s=96 Then Return 0
		If s>122 Then Return 0
	Next
	;Okay
	Return 1
End Function


;### Var Parse
;Find Operator = / += / -= / ++ / --
;Cache Varname to p_varname$
;Return Operator
Function var_parse$()
	p_varname$=""
	Local start=p_p+1
	Local equal=0
	Local s
	Local op$=""
	;Find =
	For p_p=start To p_len
		s=Asc(Mid(p_l$,p_p,1))
		;=
		If s=61 Then
			op$="="
			equal=p_p
			Exit
		;+= / ++
		ElseIf s=43 Then
			If Mid(p_l$,p_p,2)="+=" Then
				op$="+="
				equal=p_p
				Exit
			ElseIf Mid(p_l$,p_p,2)="++" Then
				op$="++"
				equal=p_p
				Exit
			EndIf
		;-= / --
		ElseIf s=45 Then
			If Mid(p_l$,p_p,2)="-=" Then
				op$="-="
				equal=p_p
				Exit	
			ElseIf Mid(p_l$,p_p,2)="--" Then
				op$="--"
				equal=p_p
				Exit		
			EndIf
		EndIf
	Next
	If equal=0 Then
		parse_error("expect","variable assignment (=,+=,++,-=,--)")
		p_varname$=""
		Return ""
	Else
		;Get Name
		p_varname$=Trim(Mid(p_l$,start,equal-start))
		p_p=p_p+Len(op$)
		Return op$
	EndIf
End Function


;### Var Extract
Function var_extract()
	p_varname$=""
	Local start=p_p
	Local gotdollar=0
	Local s
	;Find $
	For p_p=start To p_len
		If Mid(p_l$,p_p,1)="$" Then
			gotdollar=p_p
			Exit
		EndIf
	Next
	;Got Dollar?
	If gotdollar>0 Then
		;Find end of Var
		For p_p=p_p To p_len
			s=Asc(Mid(p_l$,p_p,1))
			If s<48 Then Exit
			If s>57 And s<65 Then Exit
			If s>90 And s<97 Then
				If s<>95 Then Exit
			EndIf
			If s>122 Then Exit
		Next
	Else
		parse_error("expect","variable (introduced by $)")
		Return 0
	EndIf
End Function


;### Replace Vars with their values
Function var_rep$(txt$)
	Local pos=Instr(txt$,"$")
	Local l=Len(txt$)
	Local i,s,var$
	Local ignore
	;While Vars are inside
	While pos>0
		;Find end of Var
		For i=pos+1 To l
			s=Asc(Mid(txt$,i,1))
			If s<48 Then Exit
			If s>57 And s<65 Then Exit			
			If s>90 And s<97 Then
				If s<>95 Then Exit
			EndIf
			If s>122 Then Exit
		Next
		;Extract Var Name
		var$=Mid(txt$,pos,i-pos)
		;Ignore?
		ignore=0
		If pos>1 Then
			If Mid(txt$,pos-1,1)="\" Then ignore=1
		EndIf
		If ignore=0 Then
			;$ Only
			If var$="$" Then parse_error("$ without variable")
			;Replace
			If pos>1 Then
				txtl$=Left(txt$,pos-1)
			Else
				txtl$=""
			EndIf
			txtr$=Mid(txt$,pos+Len(var$),-1)
			txt$=txtl$+var_get(Mid(var$,2,-1))+txtr$
		Else
			;Remove \
			If pos>2 Then
				txtl$=Left(txt$,pos-2)
			Else
				txtl$=""
			EndIf
			txtr$=Mid(txt$,pos,-1)
			txt$=txtl$+txtr$
		EndIf
		;Determine new length
		l=Len(txt$)
		;Finde more Vars
		pos=Instr(txt$,"$",pos+1)
	Wend
	;Return repaced Stuff
	Return txt$
End Function


;### Var Modify
Function var_modify$(name$,value$)
	Local t1
	Local t2
	;-- = +
	If Left(value$,2)="--" Then
		value$=Mid(value$,3,-1)
	EndIf
	;Scan Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=name$ Then
						t1=var_type(Tx\value$)
						t2=var_type(value$)
						If t1+t2=0 Then
							Tx\value$=Int(Tx\value$)+Int(value$)
						Else
							Tx\value$=Float(Tx\value$)+Float(value$)
						EndIf
						Return Tx\value$
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Scan Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=name$ Then
				t1=var_type(Tx\value$)
				t2=var_type(value$)
				If t1+t2=0 Then
					Tx\value$=Int(Tx\value$)+Int(value$)
				Else
					Tx\value$=Float(Tx\value$)+Float(value$)
				EndIf
				Return Tx\value$
			EndIf
		EndIf
	Next
	;Create
	Tx.Tx=New Tx
	Tx\mode=1
	Tx\key$=name$
	Tx\value$=value
	Return Tx\value$
End Function


;### Var Temp
Function var_temp(var$)
	If Left(var$,1)="$" Then
		var$=Mid(var$,2,-1)
	EndIf
	;Scan Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=var$ Then
						Tx\stuff$="temp"
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Scan Global
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=var$ Then
				Tx\stuff$="temp"
				Return 1
			EndIf
		EndIf
	Next
	;Failure
	Return 0
End Function


;### Var Local
Function var_local(var$)
	If Left(var$,1)="$" Then
		var$=Mid(var$,2,-1)
	EndIf
	;Scan Global
	Local value=0
	Local gotglobal=0
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=var$ Then
				value=Int(Tx\value$)
				gotglobal=1
				Delete Tx
				Exit
			EndIf
		EndIf
	Next
	;Existing Local?
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=p_env_class Then
				If Tx\parent_id=p_env_id Then
					If Tx\key$=var$ Then
						If gotglobal=1 Then
							Tx\value$=value
						EndIf
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Create Local
	Tx.Tx=New Tx
	Tx\mode=4
	Tx\key$=var$
	Tx\value$=value
	Tx\parent_class=p_env_class
	Tx\parent_id=p_env_id
	Return 1
End Function


;### Var Cache Save
Function var_cache_save(file$,varlist$="-")
	Local stream=WriteFile(file$)
	If stream<>0 Then
		;All
		If varlist$="-" Then
			For Tx.Tx=Each Tx
				If Tx\mode=1 Then
					;con_add("save "+Tx\key$)
					WriteString stream,code$(Tx\key$,5,0)
					WriteString stream,code$(Tx\value$,7,0)
				EndIf
			Next
			CloseFile(stream)
			Return 1
		;Special
		Else
			varlist$=","+varlist$+","
			varlist$=Replace(varlist$," ","")
			For Tx.Tx=Each Tx
				If Tx\mode=1 Then
					If Instr(varlist$,","+Tx\key$+",")>0 Then
						;con_add("save "+Tx\key$)
						WriteString stream,code$(Tx\key$,5,0)
						WriteString stream,code$(Tx\value$,7,0)
					EndIf
				EndIf
			Next
			CloseFile(stream)
			Return 1
		EndIf
	Else
		Return 0
	EndIf
End Function


;### Var Cache Load
Function var_cache_load(file$)
	Local stream=ReadFile(file$)
	Local var$,value$
	If stream<>0 Then
		While Not Eof(stream)
			var$=ReadString(stream)
			var$=code$(var$,5,1)
			value$=ReadString(stream)
			value$=code$(value$,7,1)
			var_set(var$,value$)
		Wend
		CloseFile(stream)
		Return 1
	Else
		Return 0
	EndIf	
End Function


;### Get Local
Function var_get_local$(class,id,name$)
	;Scan Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					If Tx\key$=name$ Then
						Return Tx\value$ ;Int(Tx\value$)
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Failed
	Return 0
End Function


;### Set Local
Function var_set_local(class,id,name$,newvalue$)
	;Existing Local
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					If Tx\key$=name$ Then
						Tx\value$=newvalue$ ;Int(value$)
						Return 1
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;New
	If Not var_name(name$) Then parse_error("var_name")
	Tx.Tx=New Tx
	Tx\mode=4
	Tx\key$=name$
	Tx\value$=newvalue$
	Tx\parent_class=class
	Tx\parent_id=id
	Return 1
End Function


;############################################ Def Var Stuff


;### Create Def Var
Function defvar_set(class,typ)
	If splits$(0)="" Then RuntimeError("Invalid Def VAR definition (missing var name)")
	If splits$(1)="" Then splits$(1)=splits$(0)
	Tdefvar.Tdefvar=New Tdefvar
	Tdefvar\class=class
	Tdefvar\typ=typ
	Tdefvar\key$=splits$(0)
	Tdefvar\descr$=splits$(1)
	Tdefvar\defaultval=splits$(2) ;Int(splits$(2))
	Tdefvar\isglobal=Int(splits$(3))
End Function


;### Set all Def Vars on Start
Function defvar_ini()
	For Tdefvar.Tdefvar=Each Tdefvar
		Select Tdefvar\class
			;Object
			Case Cclass_object
				For Tobject.Tobject=Each Tobject
					If Tobject\typ=Tdefvar\typ Then
						If Tdefvar\isglobal=0 Then
							defvar_ini_local(Cclass_object,Tobject\id,Tdefvar\key$,Tdefvar\defaultval)
						Else
							defvar_ini_global(Tdefvar\key$,Tdefvar\defaultval)
						EndIf
					EndIf
				Next
			;Unit
			Case Cclass_unit
				For Tunit.Tunit=Each Tunit
					If Tunit\typ=Tdefvar\typ Then
						If Tdefvar\isglobal=0 Then
							defvar_ini_local(Cclass_unit,Tunit\id,Tdefvar\key$,Tdefvar\defaultval)
						Else
							defvar_ini_global(Tdefvar\key$,Tdefvar\defaultval)
						EndIf
					EndIf
				Next
			;Item
			Case Cclass_item
				For Titem.Titem=Each Titem
					If Titem\typ=Tdefvar\typ Then
						If Tdefvar\isglobal=0 Then
							defvar_ini_local(Cclass_item,Titem\id,Tdefvar\key$,Tdefvar\defaultval)
						Else
							defvar_ini_global(Tdefvar\key$,Tdefvar\defaultval)
						EndIf
					EndIf
				Next
			;Info
			Case Cclass_info
				For Tinfo.Tinfo=Each Tinfo
					If Tinfo\typ=Tdefvar\typ Then
						If Tdefvar\isglobal=0 Then
							defvar_ini_local(Cclass_info,Tinfo\id,Tdefvar\key$,Tdefvar\defaultval)
						Else
							defvar_ini_global(Tdefvar\key$,Tdefvar\defaultval)
						EndIf
					EndIf
				Next
		End Select
	Next
End Function


;### Set Def Var LOCAL if undefined
Function defvar_ini_local(class,id,key$,val$)
	For Tx.Tx=Each Tx
		If Tx\mode=4 Then
			If Tx\parent_class=class Then
				If Tx\parent_id=id Then
					If Tx\key$=key$ Then
						Return
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	Tx.Tx=New Tx
	Tx\mode=4
	Tx\key$=key$
	Tx\value$=val
	Tx\parent_class=class
	Tx\parent_id=id
End Function


;### Set Def Var GLOBAL if undefined
Function defvar_ini_global(key$,val$)
	For Tx.Tx=Each Tx
		If Tx\mode=1 Then
			If Tx\key$=key$ Then
				Return
			EndIf
		EndIf
	Next
	Tx.Tx=New Tx
	Tx\mode=1
	Tx\key$=key$
	Tx\value$=val
End Function


;### Create Defvars for one Object
Function defvar_oncreate(class,id,typ)
	For Tdefvar.Tdefvar=Each Tdefvar
		If Tdefvar\class=class Then
			If Tdefvar\typ=typ Then
				If Tdefvar\isglobal=0 Then
					defvar_ini_local(class,id,Tdefvar\key$,Tdefvar\defaultval)
				Else
					defvar_ini_global(Tdefvar\key$,Tdefvar\defaultval)
				EndIf
			EndIf
		EndIf
	Next
End Function


;### Const Set
Function const_set(n$,v)
	If Left(n$,1)="#" Then n$=Mid(n$,2,-1)
	If n$<>"" Then
		For Tconst.Tconst=Each Tconst
			If Tconst\name$=n$ Then
				Tconst\value=v
				Return
			EndIf
		Next
		Tconst.Tconst=New Tconst
		Tconst\name$=n$
		Tconst\value=v
	EndIf
End Function

;### Const Get
Function const_get(n$)
	For Tconst.Tconst=Each Tconst
		If Tconst\name$=n$ Then
			tmp_constv=Tconst\value
			Return 1
		EndIf
	Next
	Return 0
End Function


;### Var Type
Function var_type(txt$)
	;0 = Integer
	;1 = Float
	;2 = String
	If Int(txt$)=txt$ Then Return 0
	Local l=Len(txt$)
	Local c
	Local isfloat=0
	For i=1 To l
		c=Asc(Mid(txt$,i,1))
		If c<48 Or c>57 Then
			If c=46 Then
				isfloat=1
			Else
				If i=1 Then
					If c<>45 Then Return 2
				Else
					Return 2
				EndIf
			EndIf
		EndIf
	Next
	Return isfloat
End Function
