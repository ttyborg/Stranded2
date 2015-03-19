;############################################ Load Game

Global game_healthsystem
Dim game_exh_move#(2)
Dim game_exh_swim#(2)
Dim game_exh_jump#(2)
Dim game_exh_attack#(2)
Dim game_exh_damage#(2)
Global game_divetime=8000
Global game_divedamage#=1
Global game_defaultitemmodel
Global game_projectiletimeout=15*1000
Global game_firerange=50
Global game_digtime=2500
Global game_fishtime=2500
Global game_jumptime=450
Global game_rainratio=15
Global game_snowratio=45
Global game_jumpfac#=1.7

Global game_jumptime2=game_jumptime
Global game_jumpfac2#=game_jumpfac#

Global game_falltime=850
Global game_falldamage#=1000
Global game_fallminy#=100
Global game_fallmaxy#=3000

Global game_scriptlooptimeout=5000

Global game_script$
Global game_scriptk$

Global game_scriptkeys=0
Dim game_scriptkeyn$(20)
;game_scriptkey(20) - in load_vars.bb

Global game_combiscreen=2

Global Cobject_count=255
Const Cobject_count_limit=5000
Global Cunit_count=100
Const Cunit_count_limit=500
Global Citem_count=255
Const Citem_count_limit=10000

Global game_m_adventure=1
Global game_m_random=1
Global game_m_loadsave=1
Global game_m_singleplayer=1
Global game_m_multiplayer=1
Global game_m_editor=1
Global game_m_credits=1

Global game_waverate=5000
Global game_minwavespace=300

Global game_firelightsize=50
Global game_firelightbrightness=150

Dim game_tercol(8,3)

Global game_showemptybuildinggroups=0

;### Load Game

Function load_game()
	Local in$,var$,val$
	Local i,equal
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,4)="game" And Lower(Right(file$,4))=".inf" Then
		
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
					
						Case "healthsystem" game_healthsystem=Int(val$)
						
						Case "exhaust_move"
							split(val$)
							game_exh_move#(0)=Float(splits$(0))
							game_exh_move#(1)=Float(splits$(1))
							game_exh_move#(2)=Float(splits$(2))
							
						Case "exhaust_swim"
							split(val$)
							game_exh_swim#(0)=Float(splits$(0))
							game_exh_swim#(1)=Float(splits$(1))
							game_exh_swim#(2)=Float(splits$(2))
							
						Case "exhaust_jump"
							split(val$)
							game_exh_jump#(0)=Float(splits$(0))
							game_exh_jump#(1)=Float(splits$(1))
							game_exh_jump#(2)=Float(splits$(2))
							
						Case "exhaust_attack"
							split(val$)
							game_exh_attack#(0)=Float(splits$(0))
							game_exh_attack#(1)=Float(splits$(1))
							game_exh_attack#(2)=Float(splits$(2))
							
						Case "exhausted_damage"
							split(val$)
							game_exh_damage#(0)=Float(splits$(0))
							game_exh_damage#(1)=Float(splits$(1))
							game_exh_damage#(2)=Float(splits$(2))
							
						Case "dive_time" game_divetime=Int(val$)
						
						Case "dive_damage" game_divedamage=Float(val$)
						
						Case "default_itemmodel"
							game_defaultitemmodel=load_res(val$,Cres_mesh)
							If game_defaultitemmodel=0 Then RuntimeError("Unable to load "+val$+" (default_itemmodel in sys\"+file$+")")
							HideEntity game_defaultitemmodel
						
						Case "projectile_lifetime"
							game_projectiletimeout=Int(val$)
							
						Case "firerange"
							game_firerange=Int(val$)
						
						Case "dig_time"
							game_digtime=Int(val$)
						Case "fish_time"
							game_fishtime=Int(val$)
						
						Case "jumptime"
							game_jumptime=Int(val$)
							game_jumptime2=Int(val$)
						Case "jumpfactor"
							game_jumpfac#=Float(val$)
							game_jumpfac2#=Float(val$)
						Case "falltime"							;Unused
							game_falltime=Int(val$)
						Case "falldamage"						;Unused
							game_falldamage#=Float(val$)
						Case "falldamageminy"					;Unused
							game_fallminy#=Float(val$)
						Case "falldamagemaxy"					;Unused
							game_fallmaxy#=Float(val$)

						
						Case "rainratio"
							game_rainratio=Int(val$)
						Case "snowratio"
							game_snowratio=Int(val$)
						
						Case "gore"
							set_gore=Int(val$)
						
						Case "waverate"
							game_waverate=Int(val$)
						Case "minwavespace"
							game_minwavespace=Int(val$)
						
						Case "combiscreen"
							game_combiscreen=Int(val)
						
						Case "scriptlooptimeout"
							game_scriptlooptimeout=Int(val$)
						
						Case "script"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="script=end" Then
										Exit
									Else
										game_script$=game_script$+Trim(val$)+"Ś"
									EndIf
								Wend
								game_scriptk$=preparse_string$(game_script$)
							EndIf
													
						Case "scriptkey"
							split$(val$,",",1)
							If Int(splits$(0))>=0 And Int(splits$(0))<=20 Then
								game_scriptkeys=1
								game_scriptkeyn$(Int(splits$(0)))=splits$(1)
							Else
								RuntimeError("'scriptkey' id between 0 and 20 expected!")
							EndIf
						
						Case "limit_objects"
							If Int(val$)<0 Then val$=1
							If Int(val$)>Cobject_count_limit Then
								RuntimeError("'limit_objects' is limited to a maximum of "+Cobject_count_limit+"")
							EndIf
							Cobject_count=Int(val$)
						
						Case "limit_units"
							If Int(val$)<0 Then val$=1
							If Int(val$)>Cunit_count_limit Then
								RuntimeError("'limit_units' is limited to a maximum of "+Cunit_count_limit+"")
							EndIf
							Cunit_count=Int(val$)
						
						Case "limit_items"
							If Int(val$)<0 Then val$=1
							If Int(val$)>Citem_count_limit Then
								RuntimeError("'limit_items' is limited to a maximum of "+Citem_count_limit+"")
							EndIf
							Citem_count=Int(val$)
												
						Case "menu_adventure"
							game_m_adventure=Int(val$)
						Case "menu_random"
							game_m_random=Int(val$)
						Case "menu_loadsave"
							game_m_loadsave=Int(val$)
						Case "menu_singleplayer"
							game_m_singleplayer=Int(val$)
						Case "menu_multiplayer"
							game_m_multiplayer=Int(val$)
						Case "menu_editor"
							game_m_editor=Int(val$)
						Case "menu_credits"
							game_m_credits=Int(val$)
							
						Case "firelightsize"
							game_firelightsize=Int(val$)
							
						Case "firelightbrightness"
							game_firelightbrightness=Int(val$)
							If game_firelightbrightness<0 Then game_firelightbrightness=0
							If game_firelightbrightness>255 Then game_firelightbrightness=255
							
						Case "terrain_color_normal"
							split(val$)
							For i=0 To 8
								game_tercol(i,0)=Int(splits$(i))
							Next
						Case "terrain_color_desert"
							split(val$)
							For i=0 To 8
								game_tercol(i,1)=Int(splits$(i))
							Next
						Case "terrain_color_snow"
							split(val$)
							For i=0 To 8
								game_tercol(i,2)=Int(splits$(i))
							Next
						Case "terrain_color_swamp"
							split(val$)
							For i=0 To 8
								game_tercol(i,3)=Int(splits$(i))
							Next
						
						Case "showemptybuildinggroups"
							game_showemptybuildinggroups=Int(val$)
						
						
						Default RuntimeError "Invalid GAME Property '"+var$+"'"
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

load_game()
