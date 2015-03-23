;############################################ Load Objects

Global object_count=0
Global object_count_e=0					;Object Count Editor

Dim Dobject_name$(Cobject_count)		;Name

Dim Dobject_icon$(Cobject_count)		;Icon Path
Dim Dobject_iconh(Cobject_count)		;Icon Handle

Dim Dobject_model$(Cobject_count)		;Model Path
Dim Dobject_modelh(Cobject_count)		;Model Handle
Dim Dobject_size#(Cobject_count,2)		;Model Size (x,y,z)
Dim Dobject_color(Cobject_count,2)		;Model Color (r,g,b)
Dim Dobject_fx(Cobject_count)			;Model FX
Dim Dobject_autofade(Cobject_count)		;Model Autofade
Dim Dobject_alpha#(Cobject_count)		;Model Alpha
Dim Dobject_shininess#(Cobject_count)	;Model Shininess

Dim Dobject_detailtex$(Cobject_count)	;Detail Texture

Dim Dobject_col(Cobject_count)			;Collision Mode
Dim Dobject_mat(Cobject_count)			;Material
Dim Dobject_health#(Cobject_count)		;Health
Dim Dobject_healthchange#(Cobject_count);Health Change
Dim Dobject_swayspeed#(Cobject_count)	;Swayspeed
Dim Dobject_swaypower#(Cobject_count)	;Swaypower
Dim Dobject_maxweight(Cobject_count)	;Max Weight
Dim Dobject_gt(Cobject_count)			;Game Timer
Dim Dobject_state#(Cobject_count,3)		;State Mode (0) + Offsets
Dim Dobject_searchratio#(Cobject_count)	;Search Ratio

Dim Dobject_active(Cobject_count)		;Active?
Dim Dobject_behaviour$(Cobject_count)	;Behaviour

Dim Dobject_spawn$(Cobject_count)		;Spawn (item typ, day rate, xz-range, y-range, y-offset, limit, count)

Dim Dobject_script$(Cobject_count)		;Script Stuff?
Dim Dobject_scriptk$(Cobject_count)		;Script Key
Dim Dobject_scripto$(Cobject_count)		;Script Override

Dim Dobject_align(Cobject_count)		;Align

Dim Dobject_afc(Cobject_count)			;Autofade Cache

Dim Dobject_descr$(Cobject_count)		;Description

Dim Dobject_ined(Cobject_count)			;In Editor?

Dim Dobject_growtime(Cobject_count)		;Growtime

Dim Dobject_group$(Cobject_count)		;Group


;### Load Objects

Function load_objects()
	object_count=0
	Dim Dobject_iconh(Cobject_count)
	Dim Dobject_modelh(Cobject_count)
	Local in$,var$,val$
	Local i,equal
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,7)="objects" And Lower(Right(file$,4))=".inf" Then	
	
			stream=ReadFile("sys\"+file$)
			If stream=0 Then RuntimeError("Unable to read sys\"+file$)
			While Not Eof(stream)
				in$=ReadLine(stream)
				equal=Instr(in$,"=")
				If Left(in$,1)="#" Then equal=0
				If equal>0 Then
					var$=Trim(Left(in$,equal-1))
					val$=trimspace(Mid(in$,equal+1,-1))
					Select var$
						Case "id"
							object_count=object_count+1
							i=Int(val$)
							If (i<1 Or i>Cobject_count) Then RuntimeError("ID between 1 and "+Cobject_count+" expected!")
							If Dobject_name$(i)<>"" Then RuntimeError("Object with ID "+i+" is already defined as '"+Dobject_name$(i)+"'!")
							Dobject_iconh(i)=gfx_defaulticon
							Dobject_size#(i,0)=1
							Dobject_size#(i,1)=1
							Dobject_size#(i,2)=1
							Dobject_color(i,0)=255
							Dobject_color(i,1)=255
							Dobject_color(i,2)=255
							Dobject_alpha#(i)=1
							Dobject_autofade(i)=500
							Dobject_swaypower#(i)=1
							Dobject_health#(i)=500
							Dobject_col(i)=1
							Dobject_searchratio#(i)=30
							Dobject_maxweight(i)=100000
							Dobject_ined(i)=1
							Dobject_growtime(i)=10
														
							;Progress 20 - 60
							If load_objects_c>0 Then
								perc#=Float(Float(object_count)/Float(load_objects_c))
								bmpf_loadscreen(s$(3),20+(40.*perc#))
							EndIf
						
						Case "const" const_set(val$,i)
						Case "name" Dobject_name$(i)=val$
						
						Case "icon"
							Dobject_icon$(i)=val$
							If Dobject_iconh(i)<>0 Then
								If Dobject_iconh(i)<>gfx_defaulticon Then FreeImage Dobject_iconh(i):Dobject_iconh(i)=0
							EndIf
							Dobject_iconh(i)=load_res(Dobject_icon$(i),Cres_image,0)
						Case "model"
							Dobject_model$(i)=val$
							;If Dobject_modelh(i)<>0 Then FreeEntity Dobject_modelh(i):Dobject_modelh(i)=0
							;Dobject_modelh(i)=load_res(Dobject_model$(i),Cres_mesh)
							;If Dobject_modelh(i)=0 Then RuntimeError("Unable to load "+Dobject_model$(i))
							;HideEntity Dobject_modelh(i)		 
						Case "x" Dobject_size#(i,0)=Float(val$)
						Case "y" Dobject_size#(i,1)=Float(val$)
						Case "z" Dobject_size#(i,2)=Float(val$)
						Case "scale"
							Dobject_size#(i,0)=Float(val$)
							Dobject_size#(i,1)=Float(val$)
							Dobject_size#(i,2)=Float(val$)
						Case "r" Dobject_color(i,0)=Int(val$) 
						Case "g" Dobject_color(i,1)=Int(val$)
						Case "b" Dobject_color(i,2)=Int(val$)
						Case "color"
							split$(val$,",",2)
							Dobject_color(i,0)=Int(Trim(splits$(0)))
							Dobject_color(i,1)=Int(Trim(splits$(1)))
							Dobject_color(i,2)=Int(Trim(splits$(2)))
						Case "fx" Dobject_fx(i)=Int(val$)
						Case "autofade" Dobject_autofade(i)=Int(val$)
						Case "alpha" Dobject_alpha#(i)=Float(val$)
						Case "shine" Dobject_shininess#(i)=Float(val$)
						
						Case "detailtex" Dobject_detailtex$(i)=Trim(val$)
						
						Case "col" Dobject_col(i)=Int(val$)
									;0 - no collision
									;1 - normal collision
									;2 - climbable collision
									;3 - wall collision
							
						Case "mat" Dobject_mat(i)=get_material(val$)
						Case "health"	Dobject_health#(i)=Float(val$)
										Dobject_healthchange#(i)=Dobject_health#(i)/10.
						Case "healthchange" Dobject_healthchange#(i)=Float(val$)
						Case "swayspeed" Dobject_swayspeed#(i)=Float(val$):Dobject_active(i)=1
						Case "swaypower" Dobject_swaypower#(i)=Float(val$):Dobject_active(i)=1
						;Case "gt" Dobject_gt(i)=Int(val$)
						Case "maxweight" Dobject_maxweight(i)=Int(val$)
						
						Case "state"
							If val$="random" Then
								Dobject_state#(i,0)=0
							Else
								split$(val$,",",2)
								Dobject_state#(i,0)=1
								Dobject_state#(i,1)=Float(Trim(splits$(0)))
								Dobject_state#(i,2)=Float(Trim(splits$(1)))
								Dobject_state#(i,3)=Float(Trim(splits$(2)))	
							EndIf
						
						;Case "active" Dobject_active(i)=1
						Case "behaviour"
							Dobject_behaviour$(i)=val$
							If val$<>"" Then
								Select val$
									Case "buildingsite" set_buildplaceid=i
									Case "buildingsite_water" set_buildplacewaterid=i:Dobject_align(i)=1
									Case "aligntowater" Dobject_align(i)=1
									Case "fountain" Dobject_active(i)=1
									Case "closekill" Dobject_active(i)=1
									Case "waterpipe_empty" Dobject_align(i)=2
									Case "waterpipe_full" Dobject_align(i)=2
									Case "closetrigger" Dobject_active(i)=1 
								End Select
							EndIf
							
						Case "script"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="script=end" Then
										Exit
									Else
										Dobject_script$(i)=Dobject_script$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
								Dobject_scripto$(i)=Dobject_script$(i)
								Dobject_scriptk$(i)=preparse_string$(Dobject_script$(i))
							EndIf
						
						Case "findratio" Dobject_searchratio#(i)=Float(Trim(val$))
						
						Case "find"
							;Add
							split$(val$,",",4)
							Tfind.Tfind=New Tfind
							Tfind\class=Cclass_object
							Tfind\parent=i
							Tfind\typ=Int(Trim(splits$(0)))
							Tfind\ratio=Int(Trim(splits$(1)))
							Tfind\max=Int(Trim(splits$(2)))
							Tfind\min=Int(Trim(splits$(3)))
							Tfind\reqtyp=Int(Trim(splits$(4)))
							
							;Ceck
							If Tfind\ratio<=0 Then Tfind\ratio=1
							If Tfind\max<=1 Then Tfind\max=1
							If Tfind\min<=1 Then Tfind\min=1
							If Tfind\min>=Tfind\max Then Tfind\min=Tfind\max
							
						Case "spawn"
							split(val$)
							s_item=Int(splits$(0))				;Item (Def. ID)
							s_rate=Int(splits$(1))				;Rate (Days)
							If s_rate<1 Then s_rate=1
							s_xzr#=Float(splits$(2))			;XZ Spawn Radius
							s_yr#=Float(splits$(3))				;Y Spawn Radius
							s_yo#=Float(splits$(4))				;Y Spawn Offset
							s_limit=Int(splits$(5))				;Spawn Limit
							If s_limit<1 Then s_limit=3
							s_count=Int(splits$(6))				;Count (per spawned Item)
							If s_count<0 Then s_count=1
							Dobject_spawn$(i)=s_item+","+s_rate+","+s_xzr#+","+s_yr#+","+s_yo#+","+s_limit+","+s_count
							
						;Description
						Case "description"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="description=end" Then
										Exit
									Else
										Dobject_descr$(i)=Dobject_descr$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
							EndIf
							
						;In Editor
						Case "editor"
							Dobject_ined(i)=Int(val$)
							
						;Def Param
						Case "param"
							split(val$)
							Tdefparam.Tdefparam=New Tdefparam
							Tdefparam\class=Cclass_object
							Tdefparam\typ=i
							Tdefparam\key$=splits$(0)
							Tdefparam\value=splits$(1) ;Int(splits$(1))
							
						;Growtime
						Case "growtime"
							Dobject_growtime(i)=Int(val$)
							
						;Var
						Case "var"
							split(val$)
							defvar_set(Cclass_object,i)
							
						;Group
						Case "group"
							Dobject_group$(i)=group_set$(Cclass_object,val$)
							
												
						Default RuntimeError "Invalid OBJECT Property '"+var$+"'"
					End Select
				EndIf
			Wend
			CloseFile(stream)
			
		ElseIf file$="" Then
			Exit
		EndIf
	Forever
	CloseDir(dir)
	
	;Editor Count
	For i=0 To Cobject_count
		If Dobject_name(i)<>"" Then
			If Dobject_ined(i)<>0 Then
				object_count_e=object_count_e+1
			EndIf
		EndIf
	Next
End Function

load_objects()

;Load Object Model
Function load_object_model(i)
	If Dobject_modelh(i)=0 Then
		Dobject_modelh(i)=load_res(Dobject_model$(i),Cres_mesh)
		If Dobject_modelh(i)=0 Then
			If Dobject_model$(i)<>"" Then
				RuntimeError("Unable to load "+Dobject_model$(i))
			EndIf
		Else
			HideEntity Dobject_modelh(i)
		EndIf
	EndIf
End Function
