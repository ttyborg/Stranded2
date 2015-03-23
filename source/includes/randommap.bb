;Random Map Stuff

;### Load Randommap Stuff

Function load_randommap(loadid=0)
	;Delete
	For Tran.Tran=Each Tran
		Delete Tran
	Next
	;Load
	Local stream
	Local in$,var$,val$
	Local i,equal
	Local r1#=-1000000
	Local r2#=1000000
	Local a=100
	Local cp$=""
	Local profilec=0
	Local readok=0
	Local id=0
	Local dir=ReadDir("sys")
	random_script$=""
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,6)="random" Then	
			stream=ReadFile("sys\"+file$)
			If stream=0 Then RuntimeError("Unable to read sys\"+file$)
	
			While Not Eof(stream)
				in$=ReadLine(stream)
				equal=Instr(in$,"=")
				If Left(in$,1)="#" Then equal=0
				If equal>0 Then
					var$=Trim(Left(in$,equal-1))
					val$=Trim(Mid(in$,equal+1,-1))
					Select var$
						Case "id"
							id=Int(val)
							If (id<1 Or id>Crandom_c) Then RuntimeError("ID between 1 and "+Crandom_c+" expected!")
							If id=loadid Then
								readok=1
							Else
								readok=0
							EndIf
							random_c=random_c+1
							Drprofile$(id)="Unnamed Profile (#"+id+")"
							
						Case "name"
							Drprofile$(id)=val
					
						Case "amount","ratio"
							If readok=1 Then
								a=Int(val)
								If a<1 Then RuntimeError("Invalid AMOUNT Value '"+val$+"' - has to be at least 1 (sys\"+file$+")") 
								If a>100 Then RuntimeError("Invalid AMOUNT Value '"+val$+"' - has to be maximally 100 (sys\"+file$+")")
							EndIf
					
						Case "range"
							If readok=1 Then
								If Instr (val$,",") Then
									split$(val$,",",2)
									r1#=Float(splits(0))
									r2#=Float(splits(1))
									If r1#>r2# Then RuntimeError("Invalid RANGE Value '"+val$+"' - first value has to be smaller than the second one (sys\"+file$+")")
								Else
									Select val$
										Case "land"
											r1#=0
											r2#=1000000
										Case "land and water"
											r1#=-1000000
											r2#=1000000
										Case "water"
											r1#=-10000000
											r2#=0
										Case "shore"
											r1#=-3
											r2#=3
										Case "hill"
											r1#=100
											r2#=1000000
										Case "shallow water"
											r1#=-3
											r2#=-0.1
										Default RuntimeError("Invalid RANGE Value '"+val$+"' (sys\"+file$+")")
									End Select
								EndIf
							EndIf
							
						Case "object","objects"
							If readok=1 Then
								If Instr(val,",") Then
									c=split$(val$,",",0)
								Else
									c=0
									Dim splits$(0)
									splits$(0)=Int(val)
								EndIf
								For i=0 To c
									If Int(splits$(i))>0 Then
										Tran.Tran=New Tran
										Tran\r1#=r1#
										Tran\r2#=r2#
										Tran\a=a
										Tran\class=Cclass_object
										Tran\id=Int(splits$(i))
									EndIf
								Next
							EndIf
							
						Case "unit","units"
							If readok=1 Then
								If Instr(val,",") Then
									c=split$(val$,",",0)
								Else
									c=0
									Dim splits$(0)
									splits$(0)=Int(val)
								EndIf
								For i=0 To c
									If Int(splits$(i))>0 Then
										Tran.Tran=New Tran
										Tran\r1#=r1#
										Tran\r2#=r2#
										Tran\a=a
										Tran\class=Cclass_unit
										Tran\id=Int(splits$(i))
									EndIf
								Next
							EndIf
						
						Case "item","items"
							If readok=1 Then
								If Instr(val,",") Then
									c=split$(val$,",",0)
								Else
									c=0
									Dim splits$(0)
									splits$(0)=Int(val)
								EndIf
								For i=0 To c
									If Int(splits$(i))>0 Then
										Tran.Tran=New Tran
										Tran\r1#=r1#
										Tran\r2#=r2#
										Tran\a=a
										Tran\class=Cclass_item
										Tran\id=Int(splits$(i))
									EndIf
								Next
							EndIf
							
						Case "script"
							If readok=1 Then
								If val$="start" Then
									While Not Eof(stream)
										val$=ReadLine(stream)
										If val$="script=end" Then
											Exit
										Else
											random_script$=random_script$+Trim(val$)+"Ś"
										EndIf
									Wend
								EndIf
							Else
								If val$="start" Then
									While Not Eof(stream)
										val$=ReadLine(stream)
										If val$="script=end" Then
											Exit
										EndIf
									Wend
								EndIf
							EndIf
						
						Default RuntimeError "Invalid RANDOM Property '"+var$+"' (sys\"+file$+")"
					End Select
				EndIf
			Wend
			CloseFile(stream)
			
		ElseIf file$="" Then
			Exit
		EndIf
	Forever
	CloseDir(dir)
End Function

load_randommap()


;Fill Map
Function randommap(stuffcount,objects=1,units=1,items=0)
	
	;Count
	Local t=0
	For Tran.Tran=Each Tran
		Select Tran\class
			;Objects
			Case Cclass_object
				If objects=1 Then t=t+Tran\a
			;Units
			Case Cclass_unit
				If units=1 Then t=t+Tran\a
			;Items
			Case Cclass_item
				If items=1 Then t=t+Tran\a
		End Select
	Next
	
	If t=0 Then
		con_add("ERROR: random.inf contains not enough information for random map filling (or does not exist)",Cbmpf_red)
		Return 0
	EndIf
	
	;Create and Fill Pool
	Dim Drpool(t)
		
	Local i=0
	For Tran.Tran=Each Tran
		Select Tran\class
			;Objects
			Case Cclass_object
				If objects=1 Then
					For j=i To i+Tran\a-1
						Drpool(j)=Handle(Tran)
					Next
					i=i+Tran\a
				EndIf
			;Units
			Case Cclass_unit
				If units=1 Then
					For j=i To i+Tran\a-1
						Drpool(j)=Handle(Tran)
					Next
					i=i+Tran\a
				EndIf
			;Items
			Case Cclass_item
				If items=1 Then
					For j=i To i+Tran\a-1
						Drpool(j)=Handle(Tran)
					Next
					i=i+Tran\a
				EndIf
		End Select
	Next
		
	;Create Stuff
	Local x#,y#,z#,yaw#
	Local size=(ter_size*Cworld_size)/2
	For i=1 To stuffcount
		
		;Select Thingy
		rid=Rand(0,t-1)
		Tran.Tran=Object.Tran(Drpool(rid))
		;If Tran<>Null Then
		
			;Set it
			For retry=0 To 99
				
				;Position
				x#=Rand(-size,size)
				z#=Rand(-size,size)
				yaw#=Rand(360)
				
				;Can be Set here?
				y#=e_tery(x#,z#)
				If y#>=Tran\r1# Then
					If y#<=Tran\r2# Then
				
						;All Okay! Set!
						Select Tran\class
				
							;Object
							Case Cclass_object
								set_object(-1,Tran\id,x#,z#,yaw#)
								defvar_oncreate(Cclass_object,TCobject\id,Tran\id)
								
								
							;Unit
							Case Cclass_unit
								set_unit(-1,Tran\id,x#,z#)
								defvar_oncreate(Cclass_unit,TCunit\id,Tran\id)
								RotateEntity TCunit\h,0,yaw#,0
								If m_section<>Csection_editor Then ai_ini()
							
							;Item
							Case Cclass_item
								set_item(-1,Tran\id,x#,y#,z#)
								defvar_oncreate(Cclass_item,TCitem\id,Tran\id)
								RotateEntity TCitem\h,0,yaw#,0
								
							;Info	
							Case Cclass_info
								set_info(-1,typ,x#,y#,z#)
								defvar_oncreate(Cclass_info,TCinfo\id,Tran\id)
						
						End Select
						
						Exit
						
					EndIf
				EndIf
				
			Next
			
		;EndIf
	
	Next
End Function


;Create Randommap
Function randommapcreate()
	bmpf_loadscreen("Setup")
	;Clear
	e_clear()
	
	;Section
	m_section=Csection_game_sp
	
	;Generate Island
	;in_opt(3) for mapsize!
	editor_defaultmapsettings()
	editor_genmap()
	editor_gencolormap()
	grass_map()
	grass_heightmap()
	e_environment()
	
	;Map Script
	map_briefing$=random_script$
	preparse()
	
	;Scripts preload
	parse_globalevent("preload")
	parse_sel_event("preload")
	
	;Fill
	bmpf_loadscreen("Filling Island")
	tmp_loading=1
	count=(ter_size*ter_size)/4
	randommap(count,1,1,1)
	tmp_loading=0
	
	;Player
	g_player=1
	game_playerspawn(1)
	game_setplayer(1)
	
	;Scripts start/load
	parse_globalevent("start")
	parse_globalevent("load")
	parse_sel_event("start")
	parse_sel_event("load")
	
	
	;Flush
	FlushKeys()
	FlushMouse()
	;Blackframe
	blackframe=10
	
	;Unit Physics Stuff
	lastms=MilliSecs()
	ms=lastms
	gt=0
	
	;Setcam
	m=m_menu
	m_menu=0
	game_setcam()
	m_menu=m
End Function


;Create Random Objects (Scriptcommand)
Function randomcreate(class,typ,miny#,maxy#,count=0)
	Local size=(ter_size*Cworld_size)/2
	Local x#,y#,z#,yaw#
	Local id=0
	For retry=0 To 9999
				
		;Position
		x#=Rand(-size,size)
		z#=Rand(-size,size)
		yaw#=Rand(360)
			
		;Can be Set here?
		y#=e_tery(x#,z#)
		If y#>=miny# Then
			If y#<=maxy# Then
			
				;Set
				Select class
		
					;Object
					Case Cclass_object
						TCobject=Null
						set_object(-1,typ,x#,z#,yaw#)
						If  TCobject<>Null Then
							id=TCobject\id
						EndIf
						
					;Unit
					Case Cclass_unit
						TCunit=Null
						set_unit(-1,typ,x#,z#)
						If TCunit<>Null Then
							id=TCunit\id
							RotateEntity TCunit\h,0,yaw#,0
							If m_section<>Csection_editor Then ai_ini()
						EndIf
					
					;Item
					Case Cclass_item
						TCitem=Null
						set_item(-1,typ,x#,y#,z#,count)
						If TCitem<>Null Then
							id=TCitem\id
							RotateEntity TCitem\h,0,yaw#,0
						EndIf
					
					;Info	
					Case Cclass_info
						TCinfo=Null
						set_info(-1,typ,x#,y#,z#)
						If TCinfo<>Null Then
							id=TCinfo\id
						EndIf
				
				End Select
				
				Return id
				
			EndIf
		EndIf
			
	Next
	Return 0
End Function
