;############################################ SFX Stuff


;### Update SFX Stuff
Function sfx_update()

	;Music
	If mfx_chan<>0 Then
		;Repeat
		If Not ChannelPlaying(mfx_chan) Then
			mfx_chan=PlaySound(mfx_file)
		EndIf
		;Fading
		If mfx_fade<>0 Then
			Local perc#
			If mfx_fade=1 Then
				;In
				perc#=Float(gt-mfx_fadestart)/Float(mfx_fadeend)
				If (gt-mfx_fadestart)>=mfx_fadeend Then
					mfx_vol#=mfx_peakvol#*set_musicvolume#
					mfx_fade=0
				Else
					mfx_vol#=Float(mfx_peakvol#*perc#)*set_musicvolume#
				EndIf
			Else
				;Out
				perc#=1.-Float(gt-mfx_fadestart)/Float(mfx_fadeend)
				If (gt-mfx_fadestart)>=mfx_fadeend Then
					mfx_fade=0
					FreeSound(mfx_file)
					mfx_file=0
					mfx_filename$=""
					mfx_chan=0
				Else
					mfx_vol#=Float(mfx_peakvol#*perc#)*set_musicvolume#
				EndIf
			EndIf
		Else
			;Use Peak-Volume @ no Fading
			mfx_vol#=mfx_peakvol#*set_musicvolume#
		EndIf
		;Set Volume
		ChannelVolume mfx_chan,mfx_vol#*set_musicvolume#
	EndIf
	
	;SFXs (free played sfx)
	For Tsfx.Tsfx=Each Tsfx
		If Not ChannelPlaying(Tsfx\chan) Then
			FreeSound Tsfx\file
			Delete Tsfx
		EndIf
	Next
	
	;Ambient
	If EntityY(cam)>0 Then
		;Over Water
		If amb_mode<>1 Then
			If amb_chan<>0 Then StopChannel amb_chan
			If amb_file<>0 Then
				amb_chan=sfx(amb_file)
			EndIf
			amb_mode=1
		Else
			If amb_chan<>0 Then
				If ChannelPlaying(amb_chan)=0 Then
					If amb_file<>0 Then
						amb_chan=sfx(amb_file)
					EndIf
				EndIf
			Else
				If amb_file<>0 Then
					amb_chan=sfx(amb_file)
				EndIf
			EndIf
		EndIf
	Else
		;Under Water
		If amb_mode<>-1 Then
			If amb_chan<>0 Then StopChannel amb_chan
			amb_chan=sfx(sfx_dive)
			amb_mode=-1
		EndIf
	EndIf

End Function


;### Play
Function sfx_play(file$,vol#=1.,pan#=1.,pitch=0)
	;con_add("play "+file$+" vol:"+vol+" pan:"+pan+" pitch:"+pitch)
	Tsfx.Tsfx=New Tsfx
	Tsfx\file=LoadSound("sfx\"+file$)
	If Tsfx\file=0 Then
		Delete Tsfx
		Return 0
	EndIf
	Tsfx\chan=PlaySound(Tsfx\file)
	ChannelVolume Tsfx\chan,vol#*set_fxvolume#
	ChannelPan Tsfx\chan,pan#
	If pitch<>0 Then
		ChannelPitch Tsfx\chan,pitch
	EndIf
End Function


;### Music
Function sfx_music(file$,vol#=1.)
	If mfx_file<>0 Then FreeSound(mfx_file)
	mfx_file=LoadSound("sfx\"+file$)
	If mfx_file=0 Then
		Return 0
	EndIf
	mfx_filename$=file$
	mfx_chan=PlaySound(mfx_file)
	mfx_fade=0
	mfx_peakvol#=vol#
	ChannelVolume mfx_chan,mfx_peakvol#*set_musicvolume#
End Function


;### Stopsounds
Function stopsounds()
	For Tsfx.Tsfx=Each Tsfx
		FreeSound Tsfx\file
		Delete Tsfx
	Next
End Function


;### SFX 3D
Function sfx_3d(x,y,z,sound)
	Local emitter=CreatePivot()
	PositionEntity emitter,x,y,z
	Local chan=EmitSound(sound,emitter)
	If chan<>0 Then
		ChannelVolume chan,set_fxvolume#
	EndIf
	FreeEntity emitter
End Function


;### SFX
;Alternative Function: play (functions.bb, no volume control)
Function sfx(sound,volume#=1.)
	Local chan=PlaySound(sound)
	If chan<>0 Then
		ChannelVolume chan,set_fxvolume#*volume#
	EndIf
	Return chan
End Function

;### SFX Emit
Function sfx_emit(sound,emitter,volume#=1.)
	Local chan=EmitSound(sound,emitter)
	If chan<>0 Then
		ChannelVolume chan,set_fxvolume#*volume#
	EndIf
	Return chan
End Function
