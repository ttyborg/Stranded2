;############################################ Engine Clear

Function e_clear()
	
	;Frame
	frame=0
	
	;### Temp
	tmp_x#=0
	tmp_y#=0
	tmp_z#=0
	tmp_rx#=0
	tmp_ry#=0
	tmp_rz#=0
	tmp_h=0
	tmp_class=0
	tmp_id=0
	tmp_ty#=0
	tmp_iselx=0
	tmp_isely=0
	tmp_iseltyp=0
	tmp_kill=0
	tmp_killclass=0
	tmp_killid=0
	tmp_killmodel=0
	tmp_ph=0
	tmp_lines=0
	tmp_linesvisible=0
	tmp_spawnid=0
	tmp_dumpvars=0
	tmp_skipevent=0
	tmp_loading=0
	tmp_setcam=0
	tmp_tarclass=0
	tmp_tarid=0
	tmp_tarx#=0
	tmp_tary#=0
	tmp_tarz#=0
	
	;### Menu
	m_menu=0
	
	;### TERRAIN
	
	;Terrain
	If ter<>0 Then
		FreeEntity(ter)
		ter=0
	EndIf
	;Terrain Tex Color
	If ter_tex_color<>0 Then
		FreeTexture ter_tex_color
		ter_tex_color=0
	EndIf
	
	;### ENVIRONMENT
	
	;Environment Sea
	If env_sea<>0 Then
		FreeEntity env_sea
		env_sea=0
	EndIf
	If env_sea2<>0 Then
		FreeEntity env_sea2
		env_sea2=0
	EndIf
	If gfx_waterimg_custom<>0 Then
		FreeImage gfx_waterimg_custom
		SetBuffer TextureBuffer(gfx_water)
		DrawBlock gfx_waterimg,0,0
		SetBuffer BackBuffer()
		gfx_waterimg_custom=0
	EndIf
	env_wcol(0)=220
	env_wcol(1)=110
	env_wcol(2)=90
	;Environment Sky
	If env_sky<>0 Then
		FreeEntity env_sky
		env_sky=0
	EndIf
	;Ground
	If env_ground<>0 Then
		FreeEntity env_ground
		env_ground=0
	EndIf
	;Environment Light
	;If env_light<>0 Then
	;	FreeEntity env_light
	;	env_light=0
	;EndIf
	;Environment Weather
	env_cweather=0
	env_weathery#=0
	If env_weatherchan<>0 Then
		If ChannelPlaying(env_weatherchan) Then
			StopChannel env_weatherchan
			env_weatherchan=0
		EndIf
	EndIf
	env_wa#=0
	;Itemlight
	If env_itemlight<>0 Then
		FreeEntity env_itemlight
		env_itemlight=0
	EndIf
	;Itemchan
	If env_itemchan<>0 Then
		If ChannelPlaying(env_itemchan) Then StopChannel env_itemchan
		env_itemchan=0
	EndIf
	;Moon
	If env_moon<>0 Then
		FreeEntity env_moon
		env_moon=0
	EndIf
	

	;### Grass
	grass_free()
	
	
	;### Stuff
		
	;OBJECT
	For Tobject.Tobject=Each Tobject
		If Tobject\ch<>0 Then FreeEntity Tobject\ch
		If Tobject\h<>0 Then FreeEntity Tobject\h
		Delete Tobject
	Next
	g_objectlistc=0
	Dim g_objectlist(0)
	object_serial=0

	;UNIT
	For Tunit.Tunit=Each Tunit
		If Tunit\mh<>0 Then FreeEntity Tunit\mh
		If Tunit\h<>0 Then FreeEntity Tunit\h
		If Tunit\ai_ch<>0 Then FreeEntity Tunit\ai_ch
		If Tunit\vh<>0 Then FreeEntity Tunit\vh
		If Tunit\chan<>0 Then StopChannel Tunit\chan
		Delete Tunit
	Next
	g_unitlistc=0
	Dim g_unitlist(0)
	unit_serial=99

	;ITEM
	For Titem.Titem=Each Titem
		If Titem\h<>0 Then FreeEntity Titem\h
		Delete Titem
	Next
	item_serial=0
	
	;INFO
	For Tinfo.Tinfo=Each Tinfo
		If Tinfo\h<>0 Then FreeEntity Tinfo\h
		If Tinfo\typ=47
			If Tinfo\ints[0]<>0 Then FreeSound Tinfo\ints[0]
			If Tinfo\ints[1]<>0 Then
				StopChannel Tinfo\ints[1]
			EndIf
		EndIf
		Delete Tinfo
	Next
	info_serial=0
	
	;STATE
	For Tstate.Tstate=Each Tstate
		If Tstate\h<>0 Then FreeEntity Tstate\h
		If Tstate\light<>0 Then FreeEntity Tstate\light
		Delete Tstate
	Next
	
	;EXTENSION
	For Tx.Tx=Each Tx
		Delete Tx
	Next
	
	;PARTICLE
	For Tp.Tp=Each Tp
		If Tp\h<>0 Then FreeEntity Tp\h
		Delete Tp
	Next
	
	;PROJECTILE
	For Tpro.Tpro=Each Tpro
		If Tpro\h<>0 Then FreeEntity Tpro\h
		Delete Tpro
	Next
	
	;MSG
	For Tmsg.Tmsg=Each Tmsg
		Delete Tmsg
	Next
	
	;SEQUENCES
	seq_blend=0
	seq_blendp#=0
	seq_start=0
	For Tseq.Tseq=Each Tseq
		Delete Tseq
	Next
	If seq_cpoint_p=0 Then 
		seq_cpoint_p=CreatePivot()
	EndIf
	Dim seq_msg$(2,1)
	If seq_img<>0 Then
		FreeImage seq_img
		seq_img=0
	EndIf
	seq_flw_class=0
	
	;PARSE TASKS
	For Tpt.Tpt=Each Tpt
		Delete Tpt
	Next
	
	;Stuff
	map_timeupdate=0
	map_briefing$=""
	map_briefing_key$=""
	
	;Player
	g_player=0
	g_player_dead=0
	g_airtimer=0
	g_dive=0
	g_swim=0
	g_drive=0
	g_sleep=0
		
	;GT
	gt=0
	in_tgametime=0
	in_gtmod=1.
	
	;GT Timers
	in_gt20=0
	in_gt20go=0
	in_gt50=0
	in_gt50go=0
	in_gt100=0
	in_gt100go=0
	in_gt500=0
	in_gt500go=0
	in_gt1000=0
	in_gt1000go=0
	in_gt5000=0
	in_gt5000go=0
	in_waverate=0
	
	;Process / Focus
	in_focus=0
	in_fo_x=0
	in_fo_y=0
	in_fo_txt$=0
	in_fo_perc=0
	in_fo_id=0
	in_fo_timer=0
	pc_typ=0
	pc_value=0
	pc_child=0
	pc_txt$=""
	pc_gt=0
	in_pc_custom$=""
	in_pc_customt=0
	in_pc_event$=0
	in_pc_menu=0
	in_pc_menu_x=0
	in_pc_menu_y=0
	
	;Compass
	in_compass=1
	
	;Sky Override
	Dim in_skyoverride(4)
	
	;Last Jump
	in_lastjump=0
	
	;Teleport
	in_tpforce=0
	
	;Editor
	in_fraph=0
	in_turnh=0
	
	;Drag and Drop
	in_dnd=0
	in_dnd_pivotx=0
	in_dnd_pivoty=0
	in_dnd_src=0
	in_dnd_typ=0
	
	;Interface
	in_scr_scr=0
	in_scr_scr2=0
	in_scr_scr3=0
	in_scr_descr=0
	in_scr_sel=-1
	in_scr_sel2=-1
	in_ex_sel=0
	in_ex_class=0
	in_ex_id=0
	in_accelerate#=0
	in_getkey=-1
	in_msgtitle$=""
	in_msgtext$=""
	in_chat$=""
	in_sticktoslide=0
	in_quicksave=0
	For Tin_quicksave.Tin_quicksave=Each Tin_quicksave
		Delete Tin_quicksave
	Next
	in_wpnrdy=1
	in_wpnrdyg#=0
	in_blockclose=0
	in_jumprehit=0
	If in_clickscreenimage<>0 Then
		FreeImage in_clickscreenimage
		in_clickscreenimage=0
	EndIf
	For Tcscr.Tcscr=Each Tcscr
		If Tcscr\image<>0 Then FreeImage Tcscr\image
		Delete Tcscr
	Next
	
	;Stop/Free AMB Channel and Sound
	If amb_chan<>0 Then
		StopChannel amb_chan
		amb_chan=0
	EndIf
	If amb_file<>0 Then
		FreeSound(amb_file)
		amb_file=0
	EndIf
	amb_chan=0
	amb_mode=0
	
	;MUSIC
	If mfx_chan<>0 Then
		If ChannelPlaying(mfx_chan) Then
			StopChannel(mfx_chan)
		EndIf
		mfx_chan=0
	EndIf
	If mfx_file<>0 Then FreeSound(mfx_file)
	mfx_fade=0
	mfx_file=0
	mfx_filename$=""
	
	;Attachments
	For Tat.Tat=Each Tat
		Delete Tat
	Next
	
	;Map Image
	If map_mapimage<>0 Then
		FreeImage map_mapimage
		map_mapimage=0
	EndIf
	
	;Map Ratios
	map_rainratio=game_rainratio
	map_snowratio=game_snowratio
	
	;Timers
	For Ttimer.Ttimer=Each Ttimer
		Delete Ttimer
	Next
	
	;FRAPs (free rotation and position)
	For Tfrap.Tfrap=Each Tfrap
		Delete Tfrap
	Next
	
	;Unit Paths
	For Tup.Tup=Each Tup
		Delete Tup
	Next
	
	;Reset Definition Scripts
	For i=1 To Cobject_count
		Dobject_script$(i)=Dobject_scripto$(i)
		Dobject_scriptk$(i)=preparse_string$(Dobject_script$(i))
	Next
	For i=1 To Cunit_count
		Dunit_script$(i)=Dunit_scripto$(i)
		Dunit_scriptk$(i)=preparse_string$(Dunit_script$(i))
	Next
	For i=1 To Citem_count
		Ditem_script$(i)=Ditem_scripto$(i)
		Ditem_scriptk$(i)=preparse_string$(Ditem_script$(i))
	Next
	For i=1 To Cinfo_count
		Dinfo_script$(i)=Dinfo_scripto$(i)
		Dinfo_scriptk$(i)=preparse_string$(Dinfo_script$(i))
	Next
		
	;Reset Player Stuff
	Dunit_speed#(1)=Dunit_speed#(0)
	Dunit_damage#(1)=Dunit_damage#(0)
	Dunit_attackrange(1)=Dunit_attackrange(0)
	Dunit_maxweight(1)=Dunit_maxweight(0)
	Dunit_mat(1)=Dunit_mat(0)
	
	;Reset Game Inf Stuff
	game_jumptime=game_jumptime2
	game_jumpfac#=game_jumpfac2#
	
	;Reset Parse Stuff
	p_parseinprogress=0
	
	;Reset Motion Blur Override
	set_mb_alpha_override#=0.
	in_scriptblur#=0
	
	;Flare
	set_flarefx=1
	
	;Reset Interface Buttons/Text/Image
	Dim in_sb(9)
	Dim in_sitxt(19)
	For i=0 To 39
		If in_siimg(i)<>0 Then FreeImage in_siimg(i)
	Next
	Dim in_siimg(39)
	
	;Bmpf Scroll Images
	For Tbmpf_img.Tbmpf_img=Each Tbmpf_img
		If Tbmpf_img\h<>0 Then
			FreeImage Tbmpf_img\h
		EndIf
		Delete Tbmpf_img
	Next
	
	;Text 3D
	For t3d.t3d=Each t3d
		Delete t3d
	Next
	
	;Parse Vars
	pv_x#=0
	pv_y#=0
	pv_z#=0
	pv_buffer$=""
	pv_attack_damage#=0
	pv_attack_weapon=0
	pv_attack_ammo=0
	pv_impact_class=0
	pv_impact_id=0
	pv_impact_kill=0
	pv_impact_num=0
	pv_impact_amount=0
	pv_impact_x#=0
	pv_impact_y#=0
	pv_impact_z#=0
	pv_impact_ground=0
	pv_ai_eater=0
	p_return$=""
	p_internal=0
	p_internaltxt$=""
	pv_lastbuilding=0
	
	;Editor Setscript
	ed_setscript$=""
	
	;Clear Blur Buffer
	If set_mb Then
		If mb_tex<>0 Then
			SetBuffer TextureBuffer(mb_tex)
			Cls
			SetBuffer BackBuffer()
		EndIf
	EndIf
	
	;Clear Copy
	For Tcopy.Tcopy=Each Tcopy
		Delete Tcopy
	Next
	
	;FPS Factor
	f#=1.
	
End Function
