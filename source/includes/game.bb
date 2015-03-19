;############################################ GAME

Function game()
	
	;Unpaused?
	g_unpaused=(m_menu=0) Or (m_menu>=Cmenu_if_movie)
	
	;Input
	getinput(g_unpaused)
	If m_menu=0 Then
		MoveMouse set_scrx/2,set_scry/2				;Ingame Mouse Reset
	ElseIf m_menu=Cmenu_if_movie Then
		If seq_cpoint=4 Then
			MoveMouse set_scrx/2,set_scry/2			;Sequence Mouse Reset
		EndIf
	EndIf
	
	;Update Items
	cull_items()
	
	;Update World
	ct=MilliSecs()
	UpdateWorld(f#)
	If set_debug_rt Then
		set_debug_rt_updateworld$=(MilliSecs()-ct)+" ms Update"
	EndIf
	
	;Set Camera
	game_setcam()
	
	;Player Collision
	If m_menu=0 Then
		If g_drive=0 Then
			If Handle(g_cplayer) Then
				TCunit.Tunit=g_cplayer.Tunit
				col_player()
			EndIf
		EndIf
	EndIf
	
	;Teleport
	If in_tpforce>0 Then
		in_tpforce=in_tpforce-1
		If Handle(g_cplayer) Then
			PositionEntity g_cplayer\h,in_tp#(0),in_tp#(1),in_tp#(2)
		EndIf
	EndIf

	;Update Objects / Units / Infos / Timer / Parse Scripts
	cull()
	
	;Environment Stuff (Pre Render)
	e_environment_update_water()							;Water
	e_environment_update()									;Environemnt
	
	;Player Weapon
	game_renderweapon()
	
	;Render
	set_debug_rt_render=MilliSecs()
	RenderWorld()											;Render
	set_debug_rt_render=MilliSecs()-set_debug_rt_render
	If KeyHit(59) Then screenshot()							;F1 -> Screenshot

	
	ct=MilliSecs()
	
	;Environment Stuff (Post Render)
	mb_update()												;Motion Blur
	e_environment_water2dfx()								;Water 2D FX (Dive)
	grass_spread()											;Grass
	focus_update()											;Focus (Process etc)
	update_state()											;States
	sfx_update()											;SFX
	up_update()												;Unitpaths
	
	;Interface
	interface()
	
	;Normal Game Input
	If m_menu=0 Then
		game_input_normal()
	EndIf
	
	;Change Menu Input
	game_input_menu()
	
	
	;Debug Hotkeys
	If set_debug=1 Then
		If KeyHit(67) Then set_debug=0:if_msg("< debug off >",Cbmpf_yellow)	;F9  -> Debug Off
		If KeyHit(68) Then con_debugmenu()									;F10 -> Debug Menu
		If KeyHit(87) Then game_cd():if_msg("< change day >",Cbmpf_yellow)	;F11 -> Changeday
	EndIf
			
	;Particles & Projectiles
	If g_unpaused Then
		p_update()
		pro_update()
	Else
		p_update_2d()
	EndIf

	;Crosshair
	If m_menu=0 Then
		If set_drawinterface=1 Then
			DrawImage gfx_cursor(6),set_scrx/2,set_scry/2
		EndIf
	EndIf
	
	;Sequence Blend
	seq_blend()
	
	;Endinput
	x=((m_menu>0 And m_menu<Cmenu_if_quit)Or(m_menu>=Cmenu_if_buildsetup)Or(m_menu=Cmenu_if_clickscreen))
	endinput(x,x,0)
	
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_poststuff$=(MilliSecs()-ct)+" ms Poststuff"
	EndIf
	
End Function
