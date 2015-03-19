;############################################ GAME INPUT FOR VEHICLES

;Vehicle Input
Function game_input_vehicles()
	Local tx#,ty#,tz#
	
	;Find Vehicle Unit
	For Tunit.Tunit=Each Tunit
		If Tunit\id=g_drive Then
			
			;Vehicle is follows a unit path?
			If up_controlled(Tunit\id)=0 Then
				
				Select Dunit_behaviour(Tunit\typ)
					
					;Vehicle
					Case 500
						game_input_vehicle(Tunit)
						If EntityY(Tunit\mh,1)<-v_maxdepth Then TCunit=Tunit:kill_unit()
						
					;Watercraft
					Case 501
						game_input_watercraft(Tunit)
						If EntityY(Tunit\mh,1)<-v_maxdepth Then TCunit=Tunit:kill_unit()
										
					;Aircraft
					Case 502
						game_input_aircraft(Tunit)
						;If EntityY(Tunit\mh,1)<-v_maxdepth Then TCunit=Tunit:kill_unit()
					
					;Default Control (Animal)
					Default
						game_input_animal(Tunit)
						If EntityY(Tunit\mh,1)<-v_maxdepth Then TCunit=Tunit:kill_unit()
				
				End Select
				
			Else
				
				;Vehicle is not controllable because it follows a unit path!
				
			EndIf	
			
			;Draw Vehicle Direction
			If m_menu=0 Then
				If set_drawinterface=1 Then
				
					;Local yawv#=DeltaYaw(Tunit\mh,cam)
					;DrawImage gfx_arrows,(set_scrx/2)+Sin(yawv#)*40.0,(set_scry/2)-Cos(yawv#)*40.0,0
					
				EndIf
			EndIf
			
			Exit
		EndIf
	Next

End Function


;############################################ Animal
Function game_input_animal(Tunit.Tunit)
	anim=0

	;Rotate Left/Right
	tspeed#=v_turnspeed#
	If v_steering=1 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
	ElseIf v_steering=2 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
		tspeed#=tspeed#+(v_turnspeed#/2.)
		If tspeed#>v_turnspeed# Then tspeed#=v_turnspeed#
	EndIf
	If in_keydown(Ckey_left) Then
		v_steerpos=v_steerpos+7.0*f#
		If v_steerpos>100. Then v_steerpos=100.
	ElseIf in_keydown(Ckey_right) Then
		v_steerpos=v_steerpos-7.0*f#	
		If v_steerpos<-100. Then v_steerpos=-100.
	Else
		If v_steerpos>0. Then
			v_steerpos=v_steerpos-7.0*f#
		ElseIf v_steerpos<0 Then
			v_steerpos=v_steerpos+7.0*f#
		EndIf
	EndIf
	If v_steerpos<>0. Then
		TurnEntity Tunit\h,0,tspeed*f*Float(v_steerpos/100.),0
	EndIf
	
	;Move
	moved=0
	If in_keydown(Ckey_forward) Then
		v_speed#=v_speed#+v_acceleration#
		If v_speed#>v_topspeed# Then v_speed#=v_topspeed#
		moved=1
	ElseIf in_keydown(Ckey_backward) Then
		v_speed#=v_speed#-v_acceleration#
		If v_speed#<-v_topspeed# Then v_speed#=-v_topspeed#
		moved=1
	EndIf
	
	;Perform
	If moved=0 Then
		If v_speed#>0. Then
			v_speed#=v_speed#-v_friction#
			If v_speed#<0. Then v_speed#=0.
		ElseIf v_speed#<0. Then
			v_speed#=v_speed#+v_friction#
			If v_speed#>0. Then v_speed#=0.
		EndIf
	EndIf
	If v_speed#<>0. Then
		MoveEntity Tunit\h,0,0,v_speed#*f
		anim=1
		;Sound
		If Tunit\chan<>0 Then
			If ChannelPlaying(Tunit\chan)=0 Then
				Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			Else
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			EndIf
		Else
			Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
			ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
		EndIf
	EndIf
	
	;Animate
	If anim=0 Then
		Animate Tunit\mh,0
		Tunit\ani=0
	Else
		If Tunit\ani<>Dunit_ani_move(Tunit\typ,1) Then
			Tunit\ani=Dunit_ani_move(Tunit\typ,1)
			If Dunit_loopmoveani(Tunit\typ)=1 Then
				Animate Tunit\mh,1,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			Else
				Animate Tunit\mh,2,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			EndIf
		EndIf	
	EndIf
	
End Function

;############################################  Vehicle
Function game_input_vehicle(Tunit.Tunit)
	If Tunit\vh<>0 Then HideEntity Tunit\vh
	anim=0

	;Rotate Left/Right
	tspeed#=v_turnspeed#
	If v_steering=1 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
	ElseIf v_steering=2 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
		tspeed#=tspeed#+(v_turnspeed#/2.)
		If tspeed#>v_turnspeed# Then tspeed#=v_turnspeed#
	EndIf
	
	If in_keydown(Ckey_left) Then
		v_steerpos=v_steerpos+6.0*f#
		If v_steerpos>100. Then v_steerpos=100.
	ElseIf in_keydown(Ckey_right) Then
		v_steerpos=v_steerpos-6.0*f#	
		If v_steerpos<-100. Then v_steerpos=-100.
	Else
		If v_steerpos>0. Then
			v_steerpos=v_steerpos-5.5*f#
		ElseIf v_steerpos<0 Then
			v_steerpos=v_steerpos+5.5*f#
		EndIf
	EndIf
	If v_steerpos<>0. Then
		TurnEntity Tunit\h,0,tspeed*f*Float(v_steerpos/100.),0
	EndIf
	
	;Move
	moved=0
	If in_keydown(Ckey_forward) Then
		v_speed#=v_speed#+v_acceleration#
		If v_speed#>v_topspeed# Then v_speed#=v_topspeed#
		moved=1
	ElseIf in_keydown(Ckey_backward) Then
		v_speed#=v_speed#-v_acceleration#
		If v_speed#<-v_topspeed# Then v_speed#=-v_topspeed#
		moved=1
	EndIf
	
	;Perform
	If moved=0 Then
		If v_speed#>0. Then
			v_speed#=v_speed#-v_friction#
			If v_speed#<0. Then v_speed#=0.
		ElseIf v_speed#<0. Then
			v_speed#=v_speed#+v_friction#
			If v_speed#>0. Then v_speed#=0.
		EndIf
	EndIf
	If v_speed#<>0. Then
		MoveEntity Tunit\h,0,0,v_speed#*f
		anim=1
		;Sound
		If Tunit\chan<>0 Then
			If ChannelPlaying(Tunit\chan)=0 Then
				Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			Else
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			EndIf
		Else
			Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
			ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
		EndIf
	EndIf
	
	;Animate
	If anim=0 Then
		Animate Tunit\mh,0
		Tunit\ani=0
	Else
		If Tunit\ani<>Dunit_ani_move(Tunit\typ,1) Then
			Tunit\ani=Dunit_ani_move(Tunit\typ,1)
			If Dunit_loopmoveani(Tunit\typ)=1 Then
				Animate Tunit\mh,1,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			Else
				Animate Tunit\mh,2,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			EndIf
		EndIf	
	EndIf
	
	If Tunit\vh<>0 Then ShowEntity Tunit\vh
End Function

;############################################ Watercraft
Function game_input_watercraft(Tunit.Tunit)
	If Tunit\vh<>0 Then HideEntity Tunit\vh
	anim=0
	
	;Rotate Left/Right
	tspeed#=v_turnspeed#
	If v_steering=1 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
	ElseIf v_steering=2 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
		tspeed#=tspeed#+(v_turnspeed#/2.)
		If tspeed#>v_turnspeed# Then tspeed#=v_turnspeed#
	EndIf
	
	If in_keydown(Ckey_left) Then
		v_steerpos=v_steerpos+2.1*f#
		If v_steerpos>100. Then v_steerpos=100.
	ElseIf in_keydown(Ckey_right) Then
		v_steerpos=v_steerpos-2.1*f#	
		If v_steerpos<-100. Then v_steerpos=-100.
	Else
		If v_steerpos>0. Then
			v_steerpos=v_steerpos-2.0*f#
		ElseIf v_steerpos<0 Then
			v_steerpos=v_steerpos+2.0*f#
		EndIf
	EndIf
	If v_steerpos<>0. Then
		TurnEntity Tunit\h,0,tspeed*f*Float(v_steerpos/100.),0
	EndIf
	
	;Move
	moved=0
	If in_keydown(Ckey_forward) Then
		v_speed#=v_speed#+v_acceleration#
		If v_speed#>v_topspeed# Then v_speed#=v_topspeed#
		moved=1
	ElseIf in_keydown(Ckey_backward) Then
		v_speed#=v_speed#-v_acceleration#
		If v_speed#<-v_topspeed# Then v_speed#=-v_topspeed#
		moved=1
	EndIf
	
	;Perform
	If moved=0 Then
		If v_speed#>0. Then
			v_speed#=v_speed#-v_friction#
			If v_speed#<0. Then v_speed#=0.
		ElseIf v_speed#<0. Then
			v_speed#=v_speed#+v_friction#
			If v_speed#>0. Then v_speed#=0.
		EndIf
	EndIf
	If v_speed#<>0. Then
		MoveEntity Tunit\h,0,0,v_speed#*f
		anim=1
		;Landcheck
		If e_tery(EntityX(Tunit\h),EntityZ(Tunit\h))>-1 Then
			MoveEntity Tunit\h,0,0,-v_speed#*f
			v_speed#=0
		Else
			;Waves
			If in_gt100go Then
				p_add(EntityX(Tunit\h),1,EntityZ(Tunit\h),Cp_rwave,Rnd(10,20),Rnd(0.6,0.9))
			EndIf
		EndIf
		;Sound
		If Tunit\chan<>0 Then
			If ChannelPlaying(Tunit\chan)=0 Then
				Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			Else
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			EndIf
		Else
			Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
			ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
		EndIf
	EndIf
	
	;Animate
	If anim=0 Then
		Animate Tunit\mh,0
		Tunit\ani=0
	Else
		If Tunit\ani<>Dunit_ani_move(Tunit\typ,1) Then
			Tunit\ani=Dunit_ani_move(Tunit\typ,1)
			If Dunit_loopmoveani(Tunit\typ)=1 Then
				Animate Tunit\mh,1,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			Else
				Animate Tunit\mh,2,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			EndIf
		EndIf	
	EndIf
	
	If Tunit\vh<>0 Then ShowEntity Tunit\vh
End Function

;############################################ Aircraft
Function game_input_aircraft(Tunit.Tunit)
	If Tunit\vh<>0 Then HideEntity Tunit\vh
	anim=0

	;Rotate Left/Right
	tspeed#=v_turnspeed#
	If v_steering=1 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
	ElseIf v_steering=2 Then
		perc#=Abs(v_speed#)/v_topspeed#
		tspeed#=tspeed#*perc#
		tspeed#=tspeed#+(v_turnspeed#/2.)
		If tspeed#>v_turnspeed# Then tspeed#=v_turnspeed#
	EndIf
	
	If in_keydown(Ckey_left) Then
		v_steerpos=v_steerpos+6.0*f#
		If v_steerpos>100. Then v_steerpos=100.
	ElseIf in_keydown(Ckey_right) Then
		v_steerpos=v_steerpos-6.0*f#	
		If v_steerpos<-100. Then v_steerpos=-100.
	Else
		If v_steerpos>0. Then
			v_steerpos=v_steerpos-5.5*f#
		ElseIf v_steerpos<0 Then
			v_steerpos=v_steerpos+5.5*f#
		EndIf
	EndIf
	If v_steerpos<>0. Then
		TurnEntity Tunit\h,0,tspeed*f*Float(v_steerpos/100.),0
	EndIf
	
	;Move
	moved=0
	If in_keydown(Ckey_forward) Then
		v_speed#=v_speed#+v_acceleration#
		If v_speed#>v_topspeed# Then v_speed#=v_topspeed#
		moved=1
	ElseIf in_keydown(Ckey_backward) Then
		v_speed#=v_speed#-v_acceleration#
		If v_speed#<-v_topspeed# Then v_speed#=-v_topspeed#
		moved=1
	EndIf
	
	;Perform
	If moved=0 Then
		If v_speed#>0. Then
			v_speed#=v_speed#-v_friction#
			If v_speed#<0. Then v_speed#=0.
		ElseIf v_speed#<0. Then
			v_speed#=v_speed#+v_friction#
			If v_speed#>0. Then v_speed#=0.
		EndIf
	EndIf
	If v_speed#<>0. Then
		MoveEntity Tunit\h,0,0,v_speed#*f
		anim=1
		;Sound
		If Tunit\chan<>0 Then
			If ChannelPlaying(Tunit\chan)=0 Then
				Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			Else
				ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
			EndIf
		Else
			Tunit\chan=play_soundset(Dunit_sfx(Tunit\typ),snd_move,Tunit\h)
			ChannelVolume Tunit\chan,Float((Float(Abs(v_speed#))/v_topspeed#))*set_fxvolume#
		EndIf
	EndIf
	
	;Animate
	If anim=0 Then
		Animate Tunit\mh,0
		Tunit\ani=0
	Else
		If Tunit\ani<>Dunit_ani_move(Tunit\typ,1) Then
			Tunit\ani=Dunit_ani_move(Tunit\typ,1)
			If Dunit_loopmoveani(Tunit\typ)=1 Then
				Animate Tunit\mh,1,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			Else
				Animate Tunit\mh,2,Dunit_ani_move(Tunit\typ,0),Dunit_ani_move(Tunit\typ,1),1.
			EndIf
		EndIf	
	EndIf
	
	;Rise/Fall
	perc#=v_speed#/v_topspeed#
	;Rise
	If perc#>0.5 Then
		;perc#=perc#*2.
		;perc#=1.-perc#
	
		TranslateEntity Tunit\h,0,3.*f#*perc#,0
	;Fall
	Else
		perc#=1.-perc#
		TranslateEntity Tunit\h,0,-10.*f#*perc#,0
		
		;Keep over Terrain
		tery#=e_tery(EntityX(Tunit\h),EntityZ(Tunit\h))
		If EntityY(Tunit\h)<(tery#+Dunit_colyr(Tunit\typ)) Then
			PositionEntity Tunit\h,EntityX(Tunit\h),tery#+Dunit_colyr(Tunit\typ),EntityZ(Tunit\h)
		EndIf
	EndIf
	
	;Max Height
	If EntityY(Tunit\h)>900 Then PositionEntity Tunit\h,EntityX(Tunit\h),900,EntityZ(Tunit\h)
	
	If Tunit\vh<>0 Then ShowEntity Tunit\vh
End Function

;############################################ Load Vehicle Data
Function game_input_vehicledata(typ)
	;Start at 0 Speed!
	v_speed#=0
	v_steerpos=0
	;Stuff
	v_topspeed#=Dunit_speed#(typ)
	v_turnspeed#=Dunit_tspeed#(typ)
	split(Dunit_v$(typ),",",4)
	v_acceleration#=Float(splits$(0))
	v_friction#=Float(splits$(1))
	v_steering=Int(splits$(2))
	v_maxdepth=Int(splits$(3))
	v_flyspeed#=Float(splits$(4))
End Function
