;############################################ MOD Functions


;############################################ Setup
Function mod_setup()
	;Set Source (src) and Target (tgt) Path
	src$=set_rootdir$+"mods\Stranded II\"
	tgt$=set_rootdir$+"mods\"+set_moddir$+"\"
	;Check Validity
	If FileType(src$)<>2 Then Return
	If FileType(tgt$)<>2 Then Return
	
	;Create?
	If FileType(tgt$+"sys")=2 Then Return
	
	;Make Dirs
	mod_dir(tgt$,"gfx")
	mod_dir(tgt$,"maps")
	mod_dir(tgt$,"maps\adventure")
	mod_dir(tgt$,"maps\editor")
	mod_dir(tgt$,"maps\menu")
	mod_dir(tgt$,"saves")
	mod_dir(tgt$,"sfx")
	mod_dir(tgt$,"sfx\speech")
	mod_dir(tgt$,"skies")
	mod_dir(tgt$,"sprites")
	mod_dir(tgt$,"sys")
	mod_dir(tgt$,"sys\gfx")
	
	;Copy Settings
	mod_copy(src$,tgt$,"sys\controls.cfg")
	mod_copy(src$,tgt$,"sys\settings.cfg")
	
	;Copy Model
	mod_copy_template("model.template",set_rootdir$+"mods\"+set_moddir$+"\","gfx\model.b3d")
	
	;Copy Menu Map
	mod_copy_template("menumap.template",tgt$,"maps\menu\menu.s2")
	
	;Copy Fonts
	mod_copy(src$,tgt$,"sys\gfx\font_tiny.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_tiny.bmpf")
	mod_copy(src$,tgt$,"sys\gfx\font_norm.bmpf")
	mod_copy(src$,tgt$,"sys\gfx\font_norm.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_norm_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_norm_dark.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_norm_bad.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_norm_good.bmp")
	mod_copy(src$,tgt$,"sys\gfx\font_handwriting.bmp")
	
	;Copy Infs
	mod_copy(src$,tgt$,"sys\credits.inf")
	mod_copy_template("game.template",tgt$,"sys\game.inf")
	mod_copy(src$,tgt$,"sys\infos.inf")
	mod_copy(src$,tgt$,"sys\keys.inf")
	mod_copy(src$,tgt$,"sys\lightcycle.inf")
	mod_copy(src$,tgt$,"sys\states.inf")
	mod_copy(src$,tgt$,"sys\strings.inf")
	
	;Copy Sky
	mod_copy(src$,tgt$,"skies\sky_bk.jpg")
	mod_copy(src$,tgt$,"skies\sky_dn.jpg")
	mod_copy(src$,tgt$,"skies\sky_fr.jpg")
	mod_copy(src$,tgt$,"skies\sky_lf.jpg")
	mod_copy(src$,tgt$,"skies\sky_rt.jpg")
	mod_copy(src$,tgt$,"skies\sky_up.jpg")
	
	;Copy GFX Stuff
	mod_copy(src$,tgt$,"sys\gfx\logo.bmp")
	mod_copy(src$,tgt$,"sys\gfx\bigbutton.bmp")
	mod_copy(src$,tgt$,"sys\gfx\bigbutton_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\iconbutton.bmp")
	mod_copy(src$,tgt$,"sys\gfx\iconbutton_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\check.bmp")
	mod_copy(src$,tgt$,"sys\gfx\check_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\check_sel.bmp")
	mod_copy(src$,tgt$,"sys\gfx\opt.bmp")
	mod_copy(src$,tgt$,"sys\gfx\opt_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\opt_sel.bmp")
	mod_copy(src$,tgt$,"sys\gfx\slider.bmp")
	mod_copy(src$,tgt$,"sys\gfx\slider_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\slider_sel.bmp")
	mod_copy(src$,tgt$,"sys\gfx\slider_sec.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_left.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_left_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_middle.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_middle_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_right.bmp")
	mod_copy(src$,tgt$,"sys\gfx\input_right_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\scroll.bmp")
	mod_copy(src$,tgt$,"sys\gfx\scroll_over.bmp")
	mod_copy(src$,tgt$,"sys\gfx\scroll_bar_top.bmp")
	mod_copy(src$,tgt$,"sys\gfx\scroll_bar_middle.bmp")
	mod_copy(src$,tgt$,"sys\gfx\scroll_bar_bottom.bmp")
	mod_copy(src$,tgt$,"sys\gfx\edscrollspace.bmp")
	mod_copy(src$,tgt$,"sys\gfx\icons.bmp")
	mod_copy(src$,tgt$,"sys\gfx\defaulticon.bmp")
	mod_copy(src$,tgt$,"sys\gfx\border_corn.bmp")
	mod_copy(src$,tgt$,"sys\gfx\border_hori.bmp")
	mod_copy(src$,tgt$,"sys\gfx\border_vert.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_height.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_move.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_paint.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_rotate.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_text.bmp")
	mod_copy(src$,tgt$,"sys\gfx\cursor_crosshair.bmp")
	mod_copy(src$,tgt$,"sys\gfx\woodback.bmp")
	mod_copy(src$,tgt$,"sys\gfx\woodback_dark.bmp")
	mod_copy(src$,tgt$,"sys\gfx\paperback.bmp")
	mod_copy(src$,tgt$,"sys\gfx\progress.bmp")
	mod_copy(src$,tgt$,"sys\gfx\progress_small.bmp")
	mod_copy(src$,tgt$,"sys\gfx\progress_hunger.bmp")
	mod_copy(src$,tgt$,"sys\gfx\progress_thirst.bmp")
	mod_copy(src$,tgt$,"sys\gfx\progress_exhaustion.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_barback.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_itemback.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_itemshade.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_values.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_weapon.bmp")
	mod_copy(src$,tgt$,"sys\gfx\if_compass.bmp")
	mod_copy(src$,tgt$,"sys\gfx\state.bmp")
	mod_copy(src$,tgt$,"sys\gfx\states.bmp")
	mod_copy(src$,tgt$,"sys\gfx\arrows.bmp")
	mod_copy(src$,tgt$,"sys\gfx\title.bmp")
	mod_copy(src$,tgt$,"sys\gfx\editor_x.bmp")
	mod_copy(src$,tgt$,"sys\gfx\editor_y.bmp")
	mod_copy(src$,tgt$,"sys\gfx\editor_z.bmp")
	mod_copy(src$,tgt$,"sys\gfx\editor_sel.bmp")
	mod_copy(src$,tgt$,"sys\gfx\tutor.bmp")
	mod_copy(src$,tgt$,"sys\gfx\terrainstructure.bmp")
	mod_copy(src$,tgt$,"sys\gfx\structure.bmp")
	mod_copy(src$,tgt$,"sys\gfx\terraindirt.bmp")
	
	mod_copy(src$,tgt$,"gfx\grasspread_a.png")
	mod_copy(src$,tgt$,"gfx\grasspread.b3d")
	mod_copy(src$,tgt$,"gfx\water.jpg")
	
	mod_copy(src$,tgt$,"sys\gfx\rain_a.bmp")
	mod_copy(src$,tgt$,"sys\gfx\snow_a.bmp")
	
	;Copy all Sprites
	dir=ReadDir(src$+"sprites")
	Repeat
		file$=NextFile(dir)
		If file$="" Then Exit
		If file$<>"." And file$<>".." Then
			If FileType(src$+"sprites\"+file$)=1 Then
				mod_copy(src$,tgt$,"sprites\"+file$)
			EndIf
		EndIf
	Forever
	CloseDir dir
	
	;Copy SFX Stuff
	mod_copy(src$,tgt$,"sfx\click.wav")
	mod_copy(src$,tgt$,"sfx\switch.wav")
	mod_copy(src$,tgt$,"sfx\menu.wav")
	mod_copy(src$,tgt$,"sfx\fail.wav")
	mod_copy(src$,tgt$,"sfx\build.wav")
	mod_copy(src$,tgt$,"sfx\build_finish.wav")
	mod_copy(src$,tgt$,"sfx\eat.wav")
	mod_copy(src$,tgt$,"sfx\drink.wav")
	mod_copy(src$,tgt$,"sfx\swing_fast.wav")
	mod_copy(src$,tgt$,"sfx\swing_slow.wav")
	mod_copy(src$,tgt$,"sfx\dive.wav")
	mod_copy(src$,tgt$,"sfx\collect.wav")
	mod_copy(src$,tgt$,"sfx\pistol.wav")
	mod_copy(src$,tgt$,"sfx\splash.wav")
	mod_copy(src$,tgt$,"sfx\splash2.wav")
	mod_copy(src$,tgt$,"sfx\blubb.wav")
	mod_copy(src$,tgt$,"sfx\sleep.wav")
	mod_copy(src$,tgt$,"sfx\fire.wav")
	mod_copy(src$,tgt$,"sfx\gasp.wav")
	mod_copy(src$,tgt$,"sfx\startdive.wav")
	mod_copy(src$,tgt$,"sfx\drown.wav")
	mod_copy(src$,tgt$,"sfx\treefall.wav")
	mod_copy(src$,tgt$,"sfx\fizzle.wav")
	mod_copy(src$,tgt$,"sfx\launch.wav")
	mod_copy(src$,tgt$,"sfx\fish.wav")
	mod_copy(src$,tgt$,"sfx\dig.wav")
	mod_copy(src$,tgt$,"sfx\rain.wav")
	mod_copy(src$,tgt$,"sfx\diary.wav")
	mod_copy(src$,tgt$,"sfx\thunder1.wav")
	mod_copy(src$,tgt$,"sfx\thunder2.wav")
	mod_copy(src$,tgt$,"sfx\thunder3.wav")
	mod_copy(src$,tgt$,"sfx\wave1.wav")
	mod_copy(src$,tgt$,"sfx\wave2.wav")
	mod_copy(src$,tgt$,"sfx\wave3.wav")
	mod_copy(src$,tgt$,"sfx\crack1.wav")
	mod_copy(src$,tgt$,"sfx\crack2.wav")
	mod_copy(src$,tgt$,"sfx\crack3.wav")
	mod_copy(src$,tgt$,"sfx\crack4.wav")
	mod_copy(src$,tgt$,"sfx\step1.wav")
	mod_copy(src$,tgt$,"sfx\step2.wav")
	mod_copy(src$,tgt$,"sfx\step3.wav")
	mod_copy(src$,tgt$,"sfx\step4.wav")
	mod_copy(src$,tgt$,"sfx\woodstep1.wav")
	mod_copy(src$,tgt$,"sfx\woodstep2.wav")
	mod_copy(src$,tgt$,"sfx\stonestep1.wav")
	mod_copy(src$,tgt$,"sfx\stonestep2.wav")
	mod_copy(src$,tgt$,"sfx\waterstep.wav")
	mod_copy(src$,tgt$,"sfx\swim.wav")
	mod_copy(src$,tgt$,"sfx\fountain.wav")
	mod_copy(src$,tgt$,"sfx\thunder1.wav")
	mod_copy(src$,tgt$,"sfx\thunder2.wav")
	mod_copy(src$,tgt$,"sfx\thunder3.wav")
	mod_copy(src$,tgt$,"sfx\spark1.wav")
	mod_copy(src$,tgt$,"sfx\spark2.wav")
	mod_copy(src$,tgt$,"sfx\spark3.wav")
	mod_copy(src$,tgt$,"sfx\spark4.wav")
	mod_copy(src$,tgt$,"sfx\explode1.wav")
	mod_copy(src$,tgt$,"sfx\explode2.wav")
	mod_copy(src$,tgt$,"sfx\explode3.wav")
	mod_copy(src$,tgt$,"sfx\explode4.wav")
	mod_copy(src$,tgt$,"sfx\explode5.wav")
	mod_copy(src$,tgt$,"sfx\pang.wav")
	mod_copy(src$,tgt$,"sfx\human_hit1.wav")
	mod_copy(src$,tgt$,"sfx\human_hit2.wav")
	mod_copy(src$,tgt$,"sfx\human_hit3.wav")
	mod_copy(src$,tgt$,"sfx\human_hit4.wav")
	mod_copy(src$,tgt$,"sfx\human_hit5.wav")
	mod_copy(src$,tgt$,"sfx\human_die1.wav")
	mod_copy(src$,tgt$,"sfx\human_die2.wav")
	mod_copy(src$,tgt$,"sfx\human_die3.wav")

	;Copy all Material Sounds
	dir=ReadDir(src$+"sfx")
	Repeat
		file$=NextFile(dir)
		If file$="" Then Exit
		If Left(file$,4)="mat_" Then
			If FileType(src$+"sfx\"+file$)=1 Then
				mod_copy(src$,tgt$,"sfx\"+file$)
			EndIf
		EndIf
	Forever
	CloseDir dir
	
	;mod_copy(src$,tgt$,)

End Function


;############################################ Copy Function
Function mod_copy(src$,tgt$,file$)
	If FileType(tgt$+file$)=0 Then
		CopyFile src$+file$,tgt$+file$
		con_add("Copying '"+file$+"' ...")
	EndIf
End Function

;############################################ Copy Template
Function mod_copy_template(src$,tgt$,file$)
	If FileType(tgt$+file$)=0 Then
		CopyFile set_rootdir$+"core\"+src$,tgt$+file$
		con_add("Copying '"+file$+"' ...")
	EndIf
End Function

;############################################ Dir Function
Function mod_dir(tgt$,dir$)
	If FileType(tgt$+dir$)<>2 Then
		CreateDir(tgt$+dir$)
		con_add("Creating '"+file$+"' ...")
		If FileType(tgt$+dir$)<>2 Then
			RuntimeError("Unable to setup directory '"+tgt$+dir$+"'")
		EndIf
	EndIf
End Function


;############################################ Check Mod
Function mod_check()
	;Create new Mods file
	If FileType(set_rootdir$+"core\mods.inf")=0 Then
		stream=WriteFile(set_rootdir$+"core\mods.inf")
		If stream<>0 Then
			WriteLine stream,"### List of 'installed' mods (besides Stranded II):"
			CloseFile stream
		EndIf
	EndIf
	;Check Mods file
	If set_moddir$="Stranded II" Then Return
	stream=ReadFile(set_rootdir$+"core\mods.inf")
	If stream<>0 Then
		found=0
		While Not Eof(stream)
			cmod$=ReadLine(stream)
			If cmod$=set_moddir$ Then
				found=1
				Exit
			EndIf
		Wend
		CloseFile stream
		;Setup Mod
		If found=0 Then
			stream=OpenFile(set_rootdir$+"core\mods.inf")
			size=FileSize(set_rootdir$+"core\mods.inf")
			If stream<>0 Then
				SeekFile stream,size
				WriteLine stream,set_moddir$
				CloseFile stream
			EndIf
			mod_setup()
		EndIf
	EndIf
End Function
