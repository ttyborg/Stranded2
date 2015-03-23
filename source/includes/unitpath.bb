;############################################ UNIT PATH

;Setup new Unit Path Controller
Function up_setup(unit)
	;Delete old nodes/controller
	For Tup.Tup=Each Tup
		If Tup\unit=unit Then
			Delete Tup
		EndIf
	Next
	;Create
	Tup.Tup=New Tup
	Tup\unit=unit
	Tup\info=1
	Tup\mode=0
End Function


;Add Node
Function up_addnode(unit,info)
	;Count existing nodes
	c=0
	For Tup.Tup=Each Tup
		If Tup\unit=unit Then
			c=c+1
		EndIf
	Next
	;Create
	Tup.Tup=New Tup
	Tup\unit=unit
	Tup\info=info
	Tup\mode=c
	;Limit
	If c>255 Then
		If set_debug Then
			con_add("ERROR: Unitpath nodes are limited to 256 per unit!",Cbmpf_red)
		EndIf
		Delete Tup
	EndIf
	;con_add("unitpath add "+unit+" -> "+info)
End Function


;Free Unitpath
Function up_free(unit)
	For Tup.Tup=Each Tup
		If Tup\unit=unit Then
			Delete Tup
		EndIf
	Next
End Function


;Unit has a Unitpath?
Function up_controlled(unit)
	For Tup.Tup=Each Tup
		If Tup\unit=unit Then
			If Tup\mode=0 Then
				Return 1
			EndIf
		EndIf
	Next
	Return 0
End Function


;Unit Path Update
Function up_update()
	If g_unpaused=1 Then
		;Go trough Unit Path Controllers
		For Troot.Tup=Each Tup
			If Troot\mode=0 Then
				;Con Unit?
				If con_unit(Troot\unit)
				
					;Find Node
					found=0
					For Tn.Tup=Each Tup
						If Tn\unit=Troot\unit Then
							If Tn\mode=Troot\info Then
								found=1
								Exit
							EndIf
						EndIf
					Next
					
					;Node found
					If found=1 Then
					
						;Con Info?
						If con_info(Tn\info) Then
							
							;Animation
							ai_ani(ani_move)
							TCunit\ai_mode=-1
							TCunit\ai_duration=1000
							TCunit\ai_timer=gt
							
							;Cache
							predist#=EntityDistance(TCunit\h,TCinfo\h)
							If TCunit\states=1 Then
								speed#=unit_speed#()*f
							Else
								speed#=Dunit_speed#(TCunit\typ)*f#
							EndIf
							
							If predist#>100. Then
								angle#=DeltaYaw(TCunit\h,TCinfo\h)/5.
							Else
								angle#=DeltaYaw(TCunit\h,TCinfo\h)
							EndIf

							
							;Move to Info
							Select Dunit_behaviour(TCunit\typ)
								
								;Watercraft
								Case 501
									RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+angle#,0
									MoveEntity TCunit\h,0,0,speed#
									If e_tery(EntityX(TCunit\h),EntityZ(TCunit\h))>-1 Then
										MoveEntity TCunit\h,0,0,-speed#
									Else
										If in_gt100go Then
										p_add(EntityX(TCunit\h),1,EntityZ(TCunit\h),Cp_rwave,Rnd(10,20),Rnd(0.6,0.9))
									EndIf
								EndIf
									
								;Normal Movement
								Default
									RotateEntity TCunit\h,0,EntityYaw(TCunit\h)+angle#,0
									MoveEntity TCunit\h,0,0,speed#
							
							End Select
							

							;Info Node reached?
							postdist#=EntityDistance(TCunit\h,TCinfo\h)
							If predist#<postdist# Then
								If dist#(EntityX(TCunit\h),EntityZ(TCunit\h),EntityX(TCinfo\h),EntityZ(TCinfo\h))<(Dunit_speed#(TCunit\typ)*50.0) Then
									;Event @ Unit
									If set_debug=1 Then con_add("unitpath "+Troot\unit+": reached node "+Troot\info+" @ info "+TCinfo\id,Cbmpf_green)
									;con_add("reach node "+TCinfo\id+" predist: "+Int(predist#)+" postdist: "+Int(EntityDistance(TCunit\h,TCinfo\h)))
									eventname$=zerofill(TCinfo\id,4)
									set_parsecache(Cclass_unit,TCunit\id,"node"+eventname$)
									If Instr(Dunit_scriptk$(TCunit\typ),"Śnode"+eventname$) Then
										parse_task(Cclass_unit,TCunit\id,"node"+eventname$,"reached by unit "+TCunit\id,Dunit_script(TCunit\typ))
										parse_sel(Cclass_unit,TCunit\id,"node"+eventname$)
									EndIf
									;Event @ Info Node
									If TCinfo<>Null Then set_parsecache(Cclass_info,TCinfo\id,"reach")
									;Next Info Node!
									If Troot<>Null Then Troot\info=Troot\info+1
									;Delete Info Node
									If Tn<>Null Then Delete Tn
									;Undo Last Step
									If TCunit<>Null Then
										RotateEntity TCunit\h,0,EntityYaw(TCunit\h)-angle#,0
										MoveEntity TCunit\h,0,0,-speed#							
										ai_center()
									EndIf
									
									;End?
									If TCunit<>Null Then
										c=0
										For Tend.Tup=Each Tup
											If Tend\unit=TCunit\id Then
												c=c+1
											EndIf
										Next
										If c=1 Then
											If set_debug=1 Then con_add("unitpath "+Troot\unit+": end (no more nodes found)",Cbmpf_red)
											For Tend.Tup=Each Tup
												If Tend\unit=TCunit\id Then
													Delete Tend
												EndIf
											Next
										EndIf
									EndIf
									
								EndIf
							EndIf
							
						Else
							If set_debug=1 Then con_add("unitpath "+Troot\unit+": skipping node "+Troot\info+" @ info "+Tn\info+" (info not found)",Cbmpf_red)
							Troot\info=Troot\info+1
						EndIf
					;Node not found -> delete Unit Path
					Else
						If set_debug=1 Then con_add("unitpath "+Troot\unit+": end (no more nodes found)",Cbmpf_red)
						delid=Troot\unit
						For Tdel.Tup=Each Tup
							If Tdel\unit=delid Then
								Delete Tdel
							EndIf
						Next	
					EndIf
				Else
					;Unit does not exist -> delete Unit Path
					If set_debug=1 Then con_add("unitpath "+Troot\unit+": End (units "+Troot\unit+" does Not exist)",Cbmpf_red)
					delid=Troot\unit
					For Tdel.Tup=Each Tup
						If Tdel\unit=delid Then
							Delete Tdel
						EndIf
					Next
				EndIf
			EndIf
		Next
	EndIf
End Function
