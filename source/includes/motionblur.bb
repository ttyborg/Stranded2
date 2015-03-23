;############################################ Setup Motionblur
Function mb_setup()
	Local os#=1.005
	os#=1.
	;Sprite
	mb_sprite=CreateSprite(cam)
	SpriteViewMode mb_sprite,2
	PositionEntity mb_sprite,0,0,10
	EntityOrder mb_sprite,-200
	ratio#=Float(set_scry)/Float(set_scrx)
	ScaleSprite mb_sprite, 1.*10.*os#, ratio#*10.*os#
	;Texture
	mb_tex=CreateTexture(set_scrx,set_scry,1+16+32+256)
	If mb_tex=0 Then
		con_add("ERROR: Failed to setup motion blur buffer",Cbmpf_red)
		If set_mb=1 Then
			set_mb=0
			con_add("WARNING: Motion blur has been disabled automatically",Cbmpf_red)
		EndIf
	Else
		TextureBlend mb_tex,2
		ScaleTexture mb_tex, (Float TextureWidth(mb_tex)/Float set_scrx),(Float TextureHeight(mb_tex)/Float set_scry)
		;Final Setup
		EntityTexture mb_sprite,mb_tex
		EntityAlpha mb_sprite,set_mb_alpha#
		If set_mb_alpha#>Cpeakblur# Then EntityAlpha mb_sprite,Cpeakblur#
	EndIf
End Function


;############################################ Update Motionblur
Function mb_update()
	;Script Motionblur?
	If in_scriptblur#>0. Then
		mb_override(in_scriptblur#)
	EndIf
	;Update
	If set_mb Then
		If mb_tex=0 Then
			set_mb=0
			con_add("WARNING: Motion blur has been disabled automatically",Cbmpf_red)
			gui_msg("Motion Blur Buffer Error",Cbmpf_red)
		Else
			;Sprite
			ShowEntity mb_sprite
			CopyRect 0,0,set_scrx,set_scry,0,0,BackBuffer(),TextureBuffer(mb_tex)
			;Alpha
			If set_mb_alpha_override#>set_mb_alpha# Then
				EntityAlpha mb_sprite,set_mb_alpha_override#
			Else
				EntityAlpha mb_sprite,set_mb_alpha#
				If set_mb_alpha#<=0. Then HideEntity mb_sprite
			EndIf
		EndIf
	Else
		HideEntity mb_sprite
	EndIf
End Function


;############################################ Motionblur Alpha Override
Function mb_override(a#)
	If a#>set_mb_alpha_override# Then
		set_mb_alpha_override#=a#
	EndIf
End Function
