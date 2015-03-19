;############################################ Render Culling
;Show only objects which really need to be rendered
;and do all the updating for all stuff

Function cull()
	ccull=MilliSecs()
	con_unit(g_player)
	
	;Object
	c=0
	cv=0
	ct=MilliSecs()
	For TCobject.Tobject=Each Tobject
		c=c+1
		If TCobject\ch=0 Then
			;Distance
			dist=EntityDistance(TCobject\h,cam)
			;Hide at large Distance
			If dist>Dobject_afc(TCobject\typ) Then
				HideEntity TCobject\h
			Else
				;Show when in View
				If EntityInView(TCobject\h,cam) Then
					cv=cv+1
					ShowEntity TCobject\h
					EntityAlpha TCobject\h,TCobject\alpha#
					If Dobject_swayspeed#(TCobject\typ)<>0. Then
						;update_object()
						;Windsway
						If set_windsway Then
							h=TCobject\h
							angle#=TCobject\windsway#
							angle#=(angle#+(Dobject_swayspeed#(TCobject\typ)*f#)) Mod 360
							TCobject\windsway#=angle#
							RotateEntity h,EntityPitch(h),EntityYaw(h),Sin(angle#)*Dobject_swaypower#(TCobject\typ)
						EndIf
					EndIf
					If Dobject_active(TCobject\typ)=1 Then
						update_object_behaviour()
					EndIf
				;Hide by Alpha for Collision at short Distance and not in View
				ElseIf dist<500 Then
					ShowEntity TCobject\h
					EntityAlpha TCobject\h,0
				;Hide when not in View and not short Distance
				Else
					HideEntity TCobject\h
				EndIf
			EndIf
		Else
			;Distance
			dist=EntityDistance(TCobject\h,cam)
			;Hide at large Distance
			If dist>Dobject_afc(TCObject\typ) Then
				HideEntity TCobject\h
				If Dobject_col(TCobject\typ)<>3 Then HideEntity TCobject\ch
			Else
				;Show when in View
				If EntityInView(TCobject\h,cam) Then
					cv=cv+1
					ShowEntity TCobject\h
					ShowEntity TCobject\ch
					;EntityAlpha TCobject\h,TCobject\alpha#
					If Dobject_swayspeed#(TCobject\typ)<>0. Then
						;update_object()
						;Windsway
						If set_windsway Then
							h=TCobject\h
							angle#=TCobject\windsway#
							angle#=(angle#+(Dobject_swayspeed#(TCobject\typ)*f#)) Mod 360
							TCobject\windsway#=angle#
							RotateEntity h,EntityPitch(h),EntityYaw(h),Sin(angle#)*Dobject_swaypower#(TCobject\typ)
						EndIf
					EndIf
					If Dobject_active(TCobject\typ)=1 Then
						update_object_behaviour()
					EndIf
				;Only show Collision Thingy for Collision at short Distance and not in View
				ElseIf dist<500 Then
					HideEntity TCobject\h
					ShowEntity TCobject\ch
				;Hide when not in View and not short Distance
				Else
					HideEntity TCobject\h
					If Dobject_col(TCobject\typ)<>3 Then HideEntity TCobject\ch
				EndIf
			EndIf
		EndIf
	Next
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_objects$=(MilliSecs()-ct)+" ms Objects ("+cv+"/"+c+")"
	EndIf
	
	;Unit
	c=0
	cv=0
	ct=MilliSecs()
	If m_section<>Csection_editor Then
		For TCunit.Tunit=Each Tunit
			c=c+1
			If TCunit\id<>g_player Then
				;Hide at large Distance
				If EntityDistance(TCunit\h,cam)>Dunit_afc(TCunit\typ) Then
					HideEntity TCunit\h
				;Show at short Distance
				Else
					cv=cv+1
					ShowEntity TCunit\h
					If g_unpaused Then update_unit()
				EndIf
			Else
				If g_unpaused Then update_unit()
			EndIf
		Next
	Else
		For TCunit.Tunit=Each Tunit
			c=c+1
			;Hide at large Distance
			If EntityDistance(TCunit\h,cam)>Dunit_afc(TCunit\typ) Then
				HideEntity TCunit\h
			;Show at short Distance
			Else
				If EntityInView(TCunit\h,cam) Then
					cv=cv+1
					ShowEntity TCunit\h
				Else
					HideEntity TCunit\h
				EndIf
			EndIf
		Next
	EndIf
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_units$=(MilliSecs()-ct)+" ms Units ("+cv+"/"+c+")"
	EndIf
	
	;Info
	If m_section<>Csection_editor Then
		;Update Infos (Game)
		If in_t1000go Then
			If g_unpaused=1 Then
				c=0
				cv=0
				ct=MilliSecs()
				For Tinfo.Tinfo=Each Tinfo
					c=c+1
					If Tinfo\typ>=10 Then
						If Tinfo\typ<30 Then
							cv=cv+1
							TCinfo.Tinfo=Tinfo
							update_info()
						ElseIf Tinfo\typ>40 Then
							cv=cv+1
							TCinfo.Tinfo=Tinfo
							update_info()
						EndIf
					EndIf
				Next
				If map_timeupdate=1 Then
					g_sleep=0
					map_timeupdate=0
				EndIf
			EndIf
		EndIf
	Else
		;Cull Infos (Editor)
		For Tinfo.Tinfo=Each Tinfo
			If EntityDistance(Tinfo\h,cam)<2000 Then
				If EntityInView(Tinfo\h,cam) Then
					ShowEntity Tinfo\h
				Else
					HideEntity Tinfo\h
				EndIf
			Else
				HideEntity Tinfo\h
			EndIf
		Next
	EndIf
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_infos$=(MilliSecs()-ct)+" ms Infos ("+cv+"/"+c+")"
	EndIf
	
	;Timers
	If m_section<>Csection_editor Then
		;Update Timers
		timer_update()
	EndIf
	
	;Parse Scripts
	parse_all()
	
	;Perform Quicksave/Quickload/Autosave/Autoload
	If in_quicksave>0 Then
		For Tin_quicksave.Tin_quicksave=Each Tin_quicksave
			tmp_loadmapcmdmap$=Tin_quicksave\map$
			Select Tin_quicksave\mode
				;Quicksave
				Case 1
					Delete Tin_quicksave
					If game_m_loadsave=1 Then
						save_map("saves\QUICKSAVE.sav","sav","")
					EndIf
				;Quickload
				Case 2
					Delete Tin_quicksave
					If FileType("saves\QUICKSAVE.sav")=1 Then
						If game_m_loadsave=1 Then
							load_map("saves\QUICKSAVE.sav","")
						EndIf
					EndIf
				;Autosave
				Case 3
					Delete Tin_quicksave
					If game_m_loadsave=1 Then
						save_map("saves\AUTOSAVE.sav","sav","")
						For Tp.Tp=Each Tp
							If Tp\typ=-1 Then Delete Tp
						Next
					EndIf
				;Autoload
				Case 4
					Delete Tin_quicksave
					If FileType("saves\AUTOSAVE.sav")=1 Then
						If game_m_loadsave=1 Then
							load_map("saves\AUTOSAVE.sav","")
						EndIf
					EndIf
				;Loadmap
				Case 5
					Delete Tin_quicksave
					load_map(tmp_loadmapcmdmap$,"")
				;Savemap
				Case 6
					If save_map(tmp_loadmapcmdmap$,"map","",Tin_quicksave\skills,Tin_quicksave\items,Tin_quicksave\vars,Tin_quicksave\diary,Tin_quicksave\states,Tin_quicksave\locks)=0 Then
						parse_error("Failed to save "+tmp_loadmapcmdmap$)
					EndIf
					For Tp.Tp=Each Tp
						If Tp\typ=-1 Then Delete Tp
					Next
					Delete Tin_quicksave
			End Select
		Next
		in_quicksave=0
	EndIf
	
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_cull$=(MilliSecs()-ccull)+" ms Cull"
	EndIf
End Function


Function cull_items()
	;Items
	c=0
	cv=0
	ct=MilliSecs()
	;InGame Culling
	If m_section<>Csection_editor Then
		For Titem.Titem=Each Titem
			c=c+1
			;Update and show only Items which are NOT stored inside
			If Titem\parent_class=0 Or Titem\parent_mode=Cp_out Then
				;Hide at large Distance
				If EntityDistance(Titem\h,cam)>Ditem_afc(Titem\typ) Then
					HideEntity Titem\h
					Titem\phy_fall=gt
				Else
					;Update and Show at Short Distance
					
					;Unstored Items
					If Titem\parent_class=0 Then
						cv=cv+1
						ShowEntity Titem\h
						
						;Game Unpaused
						If g_unpaused Then
							
							;Physics Unpaused
							If Titem\phy_pause<=0 Then
							
								;Fall!
								par=gt-Titem\phy_fall
								If par>Cworld_facct Then par=Cworld_facct
								tot=Cworld_facct
								perc#=Float(Float(par)/Float(tot))
								swim=Dmat_swim(Ditem_mat(Titem\typ))
								If swim<>0 Then
									;Swim
									If swim=1 Then
										;Position
										If EntityY(Titem\h)>-1 Then
											;Fall
											TranslateEntity Titem\h,0,-(Cworld_g#*f#*(1.5*perc#)),0
										ElseIf EntityY(Titem\h)<>-1 Then
											;Escalate
											TranslateEntity Titem\h,0,0.45*f#,0
											If EntityY(Titem\h)>-1 Then
												PositionEntity Titem\h,EntityX(Titem\h),-1,EntityZ(Titem\h)
												Titem\phy_fall=gt
											EndIf
										Else
											;Wooble
											RotateEntity Titem\h,Sin(in_wa#+(Titem\id*20)),EntityYaw(Titem\h),Cos(in_wa#+(Titem\id*20))
										EndIf
									;Hover
									ElseIf swim=2 Then
										;Position
										If EntityY(Titem\h)>-1 Then
											;Fall
											TranslateEntity Titem\h,0,-(Cworld_g#*f#*(1.5*perc#)),0
										Else
											;Hover
											Titem\phy_fall=gt
											;Wooble
											RotateEntity Titem\h,Sin(in_wa#+(Titem\id*20)),EntityYaw(Titem\h),Cos(in_wa#+(Titem\id*20))
										EndIf
									EndIf
								Else
									;Just Fall!
									TranslateEntity Titem\h,0,-(Cworld_g#*f#*(1.5*perc#)),0
								EndIf
								
								;On Ground (prevent Item from falling through the earth)
								tery#=e_tery(EntityX(Titem\h),EntityZ(Titem\h))
								If EntityY(Titem\h)<(tery#+3) Then
									PositionEntity Titem\h,EntityX(Titem\h),tery#+1.,EntityZ(Titem\h)
									Titem\phy_fall=gt
									If swim=0 Then
										Titem\phy_pause=1
									EndIf
								EndIf
								
								;Collisions
								If EntityCollided(Titem\h,Cworld_col)<>0 Then
									Titem\phy_fall=gt
									If swim=0 Then
										If Titem\phy_pause>=0 Then
											Titem\phy_pause=1
										Else
											Titem\phy_pause=Titem\phy_pause+1
										EndIf
									EndIf
								EndIf
							
							;Physics Paused
							Else
								
								;Hide
								If Not EntityInView(Titem\h,cam) Then	
									HideEntity Titem\h
								EndIf
								
							EndIf
						
						;Game Paused
						Else
							
							;Hide
							If Not EntityInView(Titem\h,cam) Then	
								HideEntity Titem\h
							EndIf
								
						EndIf
					;Hanging Item -> Show if in View
					Else
						If EntityInView(Titem\h,cam) Then
							cv=cv+1
							ShowEntity Titem\h
						Else
							HideEntity Titem\h
						EndIf
						Titem\phy_fall=gt
					EndIf
	
				EndIf
			;Item stored Inside -> Hide
			Else
				HideEntity Titem\h
				Titem\phy_fall=gt
			EndIf
		Next
	;Editor Culling
	Else
		For Titem.Titem=Each Titem
			c=c+1
			;Update and show only Items which are NOT stored inside
			If Titem\parent_class=0 Or Titem\parent_mode=Cp_out Then
				;Hide at large Distance
				If EntityDistance(Titem\h,cam)>Ditem_afc(Titem\typ) Then
					HideEntity Titem\h
					Titem\phy_fall=gt
				Else
					;Update and Show at Short Distance
					
					;Unstored Items
					If Titem\parent_class=0 Then
						cv=cv+1
						ShowEntity Titem\h
					;Hanging Items - Show if in View
					Else
						If EntityInView(Titem\h,cam) Then
							cv=cv+1
							ShowEntity Titem\h
						Else
							HideEntity Titem\h
						EndIf
						Titem\phy_fall=gt
					EndIf
				EndIf
			Else
				HideEntity Titem\h
				Titem\phy_fall=gt
			EndIf
		Next
	EndIf
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_items$=(MilliSecs()-ct)+" ms Items ("+cv+"/"+c+")"
	EndIf
End Function
