;############################################ Load Units

Global unit_count=0
Global unit_count_e=0

Dim Dunit_name$(Cunit_count)		;Name

Dim Dunit_icon$(Cunit_count)		;Icon Path
Dim Dunit_iconh(Cunit_count)		;Icon Handle

Dim Dunit_model$(Cunit_count)		;Model Path
Dim Dunit_modelh(Cunit_count)		;Model Handle
Dim Dunit_size#(Cunit_count,2)		;Model Size (x,y,z)
Dim Dunit_color(Cunit_count,2)		;Model Color (r,g,b)
Dim Dunit_fx(Cunit_count)			;Model FX
Dim Dunit_autofade(Cunit_count)		;Model Autofade
Dim Dunit_alpha#(Cunit_count)		;Model Alpha
Dim Dunit_shininess#(Cunit_count)	;Model Shininess

Dim Dunit_col(Cunit_count)			;Collision
Dim Dunit_colxr#(Cunit_count)		;Collision X Radius
Dim Dunit_colyr#(Cunit_count)		;Collision Y Radius
Dim Dunit_mat(Cunit_count)			;Material
Dim Dunit_health#(Cunit_count)		;Health
Dim Dunit_healthchange#(Cunit_count);Health Change
Dim Dunit_store#(Cunit_count)		;Amount of max. "storable" Food/Water (Hunger/Thirst)
Dim Dunit_maxweight(Cunit_count)	;Max Weight
Dim Dunit_gt(Cunit_count)			;Game Timer
Dim Dunit_state#(Cunit_count,3)		;State Mode (0) + Offsets
Dim Dunit_ani_idle1#(Cunit_count,1)	;Ani Idle 1
Dim Dunit_ani_idle2#(Cunit_count,1)	;Ani Idle 2
Dim Dunit_ani_idle3#(Cunit_count,1)	;Ani Idle 3
Dim Dunit_ani_move#(Cunit_count,1)	;Ani Move
Dim Dunit_ani_attack#(Cunit_count,1);Ani Attack
Dim Dunit_ani_use#(Cunit_count,1)	;Ani Use
Dim Dunit_ani_die#(Cunit_count,1)	;Ani Die
Dim Dunit_ani_spcl#(Cunit_count,1)	;Ani Special
Dim Dunit_loopmoveani(Cunit_count)	;Loop Move Animation?

Dim Dunit_sfx$(Cunit_count)			;Sfx Source

Dim Dunit_behaviour(Cunit_count)	;Behaviour

Dim Dunit_searchratio#(Cunit_count)	;Search Ratio

Dim Dunit_speed#(Cunit_count)		;Movement Speed
Dim Dunit_tspeed#(Cunit_count)		;Turn Speed
Dim Dunit_eyes#(Cunit_count)		;Height of the Eyes
Dim Dunit_damage#(Cunit_count)		;Attack Damage

Dim Dunit_range(Cunit_count)		;Range of Perception
Dim Dunit_attackrange(Cunit_count)	;Attack Range
Dim Dunit_territory(Cunit_count)	;Territory

Dim Dunit_driveoffset#(Cunit_count)	;Drive Offset (Player Position when driving)

Dim Dunit_script$(Cunit_count)		;Script Stuff?
Dim Dunit_scriptk$(Cunit_count)		;Script Key
Dim Dunit_scripto$(Cunit_count)		;Script Override

Dim Dunit_align(Cunit_count)		;Unit Alignment (Set by behaviour)
									;0 - Normal
									;1 - Water Surface
									;
									
Dim Dunit_v$(Cunit_count)			;Vehicle Stuff

Dim Dunit_afc(Cunit_count)			;Autofade Cache

Dim Dunit_descr$(Cunit_count)		;Description

Dim Dunit_ined(Cunit_count)			;In Editor?

Dim Dunit_group$(Cunit_count)		;Group


;### Load units

Function load_units()
	unit_count=0
	Dim Dunit_iconh(Cunit_count)
	Dim Dunit_modelh(Cunit_count)
	Local in$,var$,val$
	Local i,equal
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,5)="units" And Lower(Right(file$,4))=".inf" Then
	
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
							unit_count=unit_count+1
							i=Int(val$)
							If (i<1 Or i>Cunit_count) Then RuntimeError("ID between 1 and "+Cunit_count+" expected!")
							If Dunit_name$(i)<>"" Then RuntimeError("Unit with ID "+i+" is already defined as '"+Dunit_name$(i)+"'!")
							Dunit_iconh(i)=gfx_defaulticon
							Dunit_size#(i,0)=1
							Dunit_size#(i,1)=1
							Dunit_size#(i,2)=1
							Dunit_color(i,0)=255
							Dunit_color(i,1)=255
							Dunit_color(i,2)=255
							Dunit_alpha#(i)=1
							Dunit_autofade(i)=500
							Dunit_col(i)=1
							Dunit_colxr#(i)=1
							Dunit_colyr#(i)=1
							Dunit_health#(i)=5
							Dunit_store#(i)=100
							Dunit_mat(i)=Cmat_flesh
							Dunit_damage#(i)=1
							Dunit_maxweight(i)=100000
							Dunit_state#(i,0)=1
							
							Dunit_speed#(i)=2
							Dunit_tspeed#(i)=2
							
							Dunit_range(i)=300
							Dunit_attackrange(i)=50
							
							Dunit_driveoffset#(i)=30
							
							Dunit_ined(i)=1
							
							;Vehicle Stuff
							acceleration#=0.03
							friction#=0.04
							steering=2
							maxd=3
							flyspeed#=3.
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#
							
							;Progress 60 - 80
							If load_units_c>0 Then
								perc#=Float(Float(unit_count)/Float(load_units_c))
								bmpf_loadscreen(s$(4),60+(20.*perc#))
							EndIf
							
						Case "const" const_set(val$,i)	
						Case "name" Dunit_name$(i)=val$
						Case "icon"
							Dunit_icon$(i)=val$
							If Dunit_iconh(i)<>0 Then
								If Dunit_iconh(i)<>gfx_defaulticon Then FreeImage Dunit_iconh(i):Dunit_iconh(i)=0
							EndIf
							Dunit_iconh(i)=load_res(Dunit_icon$(i),Cres_image,0)
						Case "model"
							Dunit_model$(i)=val$
							If Dunit_modelh(i)<>0 Then FreeEntity Dunit_modelh(i):Dunit_modelh(i)=0
							Dunit_modelh(i)=load_res(Dunit_model$(i),Cres_animmesh)
							If Dunit_modelh(i)=0 Then RuntimeError("Unable to load "+Dunit_model$(i))
							HideEntity Dunit_modelh(i)		 
						Case "x" Dunit_size#(i,0)=Float(val$)
						Case "y" Dunit_size#(i,1)=Float(val$)
						Case "z" Dunit_size#(i,2)=Float(val$)
						Case "scale" Dunit_size#(i,0)=Float(val$):Dunit_size#(i,1)=Float(val$):Dunit_size#(i,2)=Float(val$)
						Case "r" Dunit_color(i,0)=Int(val$) 
						Case "g" Dunit_color(i,1)=Int(val$)
						Case "b" Dunit_color(i,2)=Int(val$)
						Case "color"
							split$(val$,",",2)
							Dunit_color(i,0)=Int(Trim(splits$(0)))
							Dunit_color(i,1)=Int(Trim(splits$(1)))
							Dunit_color(i,2)=Int(Trim(splits$(2)))
						Case "fx" Dunit_fx(i)=Int(val$)
						Case "autofade" Dunit_autofade(i)=Int(val$)
						Case "alpha" Dunit_alpha#(i)=Float(val$)
						Case "shine" Dunit_shininess#(i)=Float(val$)
						
						Case "col" Dunit_col(i)=Int(val$)
						Case "colxr" Dunit_colxr#(i)=Float(val$)
						Case "colyr" Dunit_colyr#(i)=Float(val$)
						Case "mat" Dunit_mat(i)=get_material(val$)
						Case "health"	Dunit_health#(i)=Float(val$)
										Dunit_healthchange#(i)=Dunit_health#(i)/5.
						Case "healthchange" Dunit_healthchange#(i)=Float(val$)
						Case "store" Dunit_store#(i)=Float(val$)
						Case "maxweight" Dunit_maxweight(i)=Int(val$)
						Case "gt" Dunit_gt(i)=Int(val$)
						
						Case "state"
							If val$="random" Then
								Dunit_state#(i,0)=0
							Else
								split$(val$,",",2)
								Dunit_state#(i,0)=1
								Dunit_state#(i,1)=Float(Trim(splits$(0)))
								Dunit_state#(i,2)=Float(Trim(splits$(1)))
								Dunit_state#(i,3)=Float(Trim(splits$(2)))	
							EndIf
						
						Case "ani_idle1"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_idle1(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_idle1(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_idle2"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_idle2(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_idle2(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_idle3"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_idle3(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_idle3(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_move"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_move(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_move(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_attack"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_attack(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_attack(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_use"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_use(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_use(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_die"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_die(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_die(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						Case "ani_special"
							split$(val$,",",2)
							f1=Int(Trim(splits$(0)))
							f2=Int(Trim(splits$(1)))
							Dunit_ani_spcl(i,0)=Float(Trim(splits$(2)))
							Dunit_ani_spcl(i,1)=ExtractAnimSeq(Dunit_modelh(i),f1,f2)
						
						Case "loopmoveani"
							Dunit_loopmoveani(i)=Int(val$)
						
						Case "sfx" Dunit_sfx$(i)=val$
							load_soundset(val$)
							
						Case "behaviour"
							Dunit_behaviour(i)=ai_getbehaviour(val$)
							If Dunit_behaviour(i)=0 Then
								If set_debug Then con_add("ERROR: Invalid unit behaviour '"+val$+"'",Cbmpf_red)
							EndIf
							Select Dunit_behaviour(i)
								;Watercraft
								Case 501 Dunit_align(i)=1
							End Select
						
						
						Case "loot"
							;Add
							split$(val$,",",1)
							Tfind.Tfind=New Tfind
							Tfind\class=Cclass_unit
							Tfind\parent=i
							Tfind\typ=Int(Trim(splits$(0)))
							Tfind\max=Int(Trim(splits$(1)))
		
							;Ceck
							If Tfind\max<=1 Then Tfind\max=1
						
						
						Case "script"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="script=end" Then
										Exit
									Else
										Dunit_script$(i)=Dunit_script$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
								Dunit_scripto$(i)=Dunit_script$(i)
								Dunit_scriptk$(i)=preparse_string$(Dunit_script$(i))
							EndIf
						
						Case "speed" Dunit_speed#(i)=Float(val$)
						Case "turnspeed" Dunit_tspeed#(i)=Float(val$)
						Case "eyes" Dunit_eyes#(i)=Float(val$)
						Case "damage" Dunit_damage#(i)=Float(val$)
						
						Case "range" Dunit_range(i)=Int(val$)
						Case "attackrange" Dunit_attackrange(i)=Int(val$)
						
						Case "rideoffset" Dunit_driveoffset#(i)=Float(val$)
						
						;Vehicle Values
						Case "acceleration"
							acceleration#=Float(val$)
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#	
						Case "friction"
							friction#=Float(val$)
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#
						Case "steering"
							steering=Int(val$)
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#
						Case "maxdepth"
							maxd=Int(val$)
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#
						Case "flyspeed"
							flyspeed#=Float(val$)
							Dunit_v$(i)=acceleration#+","+friction#+","+steering+","+maxd+","+flyspeed#
													
						;Description
						Case "description"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="description=end" Then
										Exit
									Else
										Dunit_descr$(i)=Dunit_descr$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
							EndIf
							
						;In Editor
						Case "editor"
							Dunit_ined(i)=Int(val$)
							
						;Def Param
						Case "param"
							split(val$)
							Tdefparam.Tdefparam=New Tdefparam
							Tdefparam\class=Cclass_unit
							Tdefparam\typ=i
							Tdefparam\key$=splits$(0)
							Tdefparam\value=splits$(1) ;Int(splits$(1))
							
						;Var
						Case "var"
							split(val$)
							defvar_set(Cclass_unit,i)

						;Group
						Case "group"
							Dunit_group$(i)=group_set$(Cclass_unit,val$)
							
						Default RuntimeError "Invalid UNIT Property '"+var$+"'"
					End Select
				EndIf
			Wend
			CloseFile(stream)
	
		ElseIf file$="" Then
			Exit
		EndIf
	Forever
	CloseDir(dir)
	
	;Cache Player Stuff
	Dunit_speed#(0)=Dunit_speed#(1)
	Dunit_damage#(0)=Dunit_damage#(1)
	Dunit_attackrange(0)=Dunit_attackrange(1)
	Dunit_maxweight(0)=Dunit_maxweight(1)
	Dunit_mat(0)=Dunit_mat(1)
	
	;Editor Count
	For i=0 To Cunit_count
		If Dunit_name(i)<>"" Then
			If Dunit_ined(i)<>0 Then
				unit_count_e=unit_count_e+1
			EndIf
		EndIf
	Next
End Function

load_units()

;No Units created? (one needed as player unit!)
If unit_count=0 Then
	If FileType("sys\units.inf")=1 Then
		RuntimeError("Unit with ID 1 must be defined (used as player unit)!")
	Else
		mod_copy_template("units.template",set_rootdir$+"mods\"+set_moddir$+"\","sys\units.inf")
		load_units()
	EndIf
EndIf
