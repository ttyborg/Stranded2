;############################################ Load Items

Global item_count=0
Global item_count_e=0

Dim Ditem_name$(Citem_count)			;Name

Dim Ditem_icon$(Citem_count)			;Icon Path
Dim Ditem_iconh(Citem_count)			;Icon Handle
Dim Ditem_icondh(Citem_count)			;Icon Dark Handle

Dim Ditem_model$(Citem_count)			;Model Path
Dim Ditem_modelh(Citem_count)			;Model Handle
Dim Ditem_size#(Citem_count,2)			;Model Size (x,y,z)
Dim Ditem_color(Citem_count,2)			;Model Color (r,g,b)
Dim Ditem_fx(Citem_count)				;Model FX
Dim Ditem_autofade(Citem_count)			;Model Autofade
Dim Ditem_alpha#(Citem_count)			;Model Alpha
Dim Ditem_shininess#(Citem_count)		;Model Shininess
Dim Ditem_blend(Citem_count)			;Model Blend

Dim Ditem_col(Citem_count)				;Collision
Dim Ditem_mat(Citem_count)				;Material
Dim Ditem_weight(Citem_count)			;Weight (g)
Dim Ditem_health#(Citem_count)			;Health
Dim Ditem_healthchange#(Citem_count)	;Health Change
Dim Ditem_gt(Citem_count)				;Game Timer
Dim Ditem_state#(Citem_count,3)			;State Mode (0) + Offsets
Dim Ditem_radius#(Citem_count)			;Radius

Dim Ditem_behaviour$(Citem_count)		;Behaviour
Dim Ditem_damage$(Citem_count)			;Weapon/Ammo Damage
Dim Ditem_speed#(Citem_count)			;Weapon Speed/Weapon Range
Dim Ditem_drag#(Citem_count)			;Weapon Drag
Dim Ditem_rate(Citem_count)				;Weapon Firerate
Dim Ditem_wstate(Citem_count)			;Weapon State

Dim Ditem_script$(Citem_count)			;Script Stuff?
Dim Ditem_scriptk$(Citem_count)			;Script Key
Dim Ditem_scripto$(Citem_count)			;Script Override

;Dim Ditem_ed$(Citem_count)

Dim	Ditem_info$(Citem_count)			;Item Info

Dim Ditem_afc(Citem_count)				;Autofade Cache

Dim Ditem_descr$(Citem_count)			;Description

Dim Ditem_ined(Citem_count)				;In Editor?

Dim Ditem_group$(Citem_count)			;Group


;### 0 Values

Ditem_speed#(0)=1
Ditem_damage(0)=1


;### Load items

Function load_items()
	item_count=0
	Dim Ditem_iconh(Citem_count)
	Dim Ditem_modelh(Citem_count)
	Local in$,var$,val$
	Local i,equal
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,5)="items" And Lower(Right(file$,4))=".inf" Then
			
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
							item_count=item_count+1
							i=Int(val$)
							If (i<1 Or i>Citem_count) Then RuntimeError("ID between 1 and "+Citem_count+" expected!")
							If Ditem_name$(i)<>"" Then RuntimeError("Item with ID "+i+" is already defined as '"+Ditem_name$(i)+"'!")
							Ditem_iconh(i)=gfx_defaulticon
							Ditem_size#(i,0)=1
							Ditem_size#(i,1)=1
							Ditem_size#(i,2)=1
							Ditem_color(i,0)=255
							Ditem_color(i,1)=255
							Ditem_color(i,2)=255
							Ditem_alpha#(i)=1
							Ditem_autofade(i)=225
							Ditem_health#(i)=15
							Ditem_radius#(i)=0.
							Ditem_speed#(i)=10
							Ditem_drag#(i)=0
							Ditem_damage$(i)="1"
							Ditem_rate(i)=500
							Ditem_healthchange#(i)=-(Ditem_health#(i)/2.)
							
							Ditem_ined(i)=1
							
							;Progress 80 - 98
							If load_items_c>0 Then
								perc#=Float(Float(item_count)/Float(load_items_c))
								bmpf_loadscreen(s$(5),80+(18.*perc#))
							EndIf
						
						Case "const" const_set(val$,i)	
						Case "name" Ditem_name$(i)=val$
						Case "icon"
							Ditem_icon$(i)=val$
							If Ditem_iconh(i)<>0 Then
								If Ditem_iconh(i)<>gfx_defaulticon Then FreeImage Ditem_iconh(i):Ditem_iconh(i)=0
							EndIf
							Ditem_iconh(i)=load_res(Ditem_icon$(i),Cres_image,0)
						Case "model"
							Ditem_model$(i)=val$
							;If Ditem_modelh(i)<>0 Then FreeEntity Ditem_modelh(i):Ditem_modelh(i)=0
							;Ditem_modelh(i)=load_res(Ditem_model$(i),Cres_mesh)
							;If Ditem_modelh(i)=0 Then RuntimeError("Unable to load "+Ditem_model$(i))
							;HideEntity Ditem_modelh(i)		 
						Case "x" Ditem_size#(i,0)=Float(val$)
						Case "y" Ditem_size#(i,1)=Float(val$)
						Case "z" Ditem_size#(i,2)=Float(val$)
						Case "scale" Ditem_size#(i,0)=Float(val$):Ditem_size#(i,1)=Float(val$):Ditem_size#(i,2)=Float(val$)
						Case "r" Ditem_color(i,0)=Int(val$) 
						Case "g" Ditem_color(i,1)=Int(val$)
						Case "b" Ditem_color(i,2)=Int(val$)
						Case "color"
							split$(val$,",",2)
							Ditem_color(i,0)=Int(Trim(splits$(0)))
							Ditem_color(i,1)=Int(Trim(splits$(1)))
							Ditem_color(i,2)=Int(Trim(splits$(2)))
						Case "fx" Ditem_fx(i)=Int(val$)
						Case "autofade" Ditem_autofade(i)=Int(val$)
						Case "alpha" Ditem_alpha#(i)=Float(val$)
						Case "shine" Ditem_shininess#(i)=Float(val$)
						Case "blend" Ditem_blend(i)=Int(val$)
						
						Case "col" Ditem_col(i)=Int(val$)
						Case "mat" Ditem_mat(i)=get_material(val$)
						Case "weight","w" Ditem_weight(i)=Int(val$)
						Case "health" 	Ditem_health#(i)=Float(val$)
										Ditem_healthchange#(i)=-(Ditem_health#(i)/2.)
						Case "healthchange" Ditem_healthchange#(i)=Float(val$)
						;Case "gt" Ditem_gt(i)=Int(val$)
						
						Case "state"
							If val$="random" Then
								Ditem_state#(i,0)=0
							Else
								split$(val$,",",2)
								Ditem_state#(i,0)=1
								Ditem_state#(i,1)=Float(Trim(splits$(0)))
								Ditem_state#(i,2)=Float(Trim(splits$(1)))
								Ditem_state#(i,3)=Float(Trim(splits$(2)))	
							EndIf
						
						Case "radius" Ditem_radius#(i)=Float(val$)
						
						Case "behaviour" Ditem_behaviour$(i)=val$
						Case "damage" Ditem_damage$(i)=val$
						Case "speed","range" Ditem_speed#(i)=Float(val$)
						Case "drag" Ditem_drag#(i)=Float(val$)
						Case "rate" Ditem_rate(i)=Int(val$)
						Case "weaponstate"
							If Int(val$)=0 Then
								Ditem_wstate(i)=parse_getstate(val$)
							Else
								Ditem_wstate(i)=Int(val$)
							EndIf
						
						Case "script"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="script=end" Then
										Exit
									Else
										Ditem_script$(i)=Ditem_script$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
								Ditem_scripto$(i)=Ditem_script$(i)
								Ditem_scriptk$(i)=preparse_string$(Ditem_script$(i))
							EndIf
							
						;Case "rm" Ditem_ed$(i)=val$
						
						Case "info" Ditem_info$(i)=val$
						
						Case "description"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="description=end" Then
										Exit
									Else
										Ditem_descr$(i)=Ditem_descr$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
							EndIf
							
						;In Editor
						Case "editor"
							Ditem_ined(i)=Int(val$)
							
						;Def Param
						Case "param"
							split(val$)
							Tdefparam.Tdefparam=New Tdefparam
							Tdefparam\class=Cclass_item
							Tdefparam\typ=i
							Tdefparam\key$=splits$(0)
							Tdefparam\value=splits$(1) ;Int(splits$(1))
							
						;Var
						Case "var"
							split(val$)
							defvar_set(Cclass_item,i)
							
						;Group
						Case "group"
							Ditem_group$(i)=group_set$(Cclass_item,val$)
						
						Default RuntimeError "Invalid ITEM Property '"+var$+"'"
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
	For i=0 To Citem_count
		If Ditem_name(i)<>"" Then
			If Ditem_ined(i)<>0 Then
				item_count_e=item_count_e+1
			EndIf
		EndIf
	Next
End Function

load_items()

;Load Item Model
Function load_item_model(i)
	If Ditem_modelh(i)=0 Then
		Ditem_modelh(i)=load_res(Ditem_model$(i),Cres_mesh)
		If Ditem_modelh(i)=0 Then
			If Ditem_model$(i)<>"" Then
				RuntimeError("Unable to load "+Ditem_model$(i))
			EndIf
		Else
			HideEntity Ditem_modelh(i)
		EndIf
	EndIf
End Function
