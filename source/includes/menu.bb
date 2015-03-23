;############################################ MENU

;### Menu Start
Function menu_start()
	;Section & Menu
	m_section=Csection_menu
	;Load Map
	load_map("maps\menu\menu.s2","")
	;Setup Player
	game_playerspawn(1)
	game_setplayer(1)
	PositionEntity TCunit\h,-1000000,-1000000,-1000000
End Function


;### Menu Set
Function menu_set(menu,save=1)
	;Save GUI Stuff @ Vars from current Menu
	If save=1
		Select m_menu
			;Settings Controls
			Case Cmenu_set_in
				set_msens#=0.02*Float(in_slider(0))
				set_minvert=1
				If in_check(0)=1 Then set_minvert=-1
				set_msmooth=in_check(1)
				
			;Settings GFX
			Case Cmenu_set_gfx
				Select in_slider(0)
					Case 0 set_scrx_c=800:set_scry_c=600
					Case 1 set_scrx_c=1024:set_scry_c=768
					Case 2 set_scrx_c=1152:set_scry_c=864
					Case 3 set_scrx_c=1280:set_scry_c=960
					Case 4 set_scrx_c=1600:set_scry_c=1200
				End Select
				Select in_slider(1)
					Case 0 set_scrbit_c=16
					Case 1 set_scrbit_c=24
					Case 2 set_scrbit_c=32
				End Select
				set_viewrange=in_slider(2)
				set_terrain=in_slider(3)
				set_water=in_slider(4)
				set_sky=in_slider(5)
				set_effects=in_slider(6)
				set_grass=in_slider(7)
				set_2dfx=in_check(0)
				set_lightfx=in_check(1)
				set_windsway=in_check(2)
				set_fog=in_check(3)
				set_hwmultitex=in_check(4)
				set_mb=in_check(5)
				set_mb_alpha#=Float(in_slider(8))/100.
				;Reboot?
				If set_scrx<>set_scrx_c Or set_scry<>set_scry_c Or set_scrbit<>set_scrbit_c Then
					set_reboot=1
				Else
					set_reboot=0
				EndIf
				
			;Settings SFX
			Case Cmenu_set_sfx
				set_musicvolume#=Float(in_slider(0))/100.
				set_fxvolume#=Float(in_slider(1))/100.
				set_name$=in_input(0)
				set_serverport=in_input(1)
				
				If amb_chan<>0 Then
					ChannelVolume amb_chan,set_fxvolume#
				EndIf
			
			;Multiplayer
			Case Cmenu_mp
				;Shutdown Multiplayer Stuff
				udp_clear()
			
			;Multiplayer Create Game
			Case Cmenu_mp_create
				set_sv_name$=in_input(0)
				set_sv_port=in_input(1)
				set_sv_pw$=in_input(2)
				set_sv_playerlimit=in_input(3)
				set_sv_map$=in_win_map$
				
			;Credits
			Case Cmenu_credits
				For Ttbt.Ttbt=Each Ttbt
					Delete Ttbt
				Next
				
		End Select
	EndIf
	
	;Set Menu
	m_menu=menu
	
	;Fill new GUI Stuff with Var Stuff ;)
	Select m_menu
		;Settings Controls
		Case Cmenu_set_in
			in_slider(0)=set_msens#/0.02
			in_check(0)=0
			If set_minvert=-1 Then in_check(0)=1
			in_check(1)=set_msmooth
			
		;Settings GFX
		Case Cmenu_set_gfx
			Select set_scrx_c
				Case 800 in_slider(0)=0
				Case 1024 in_slider(0)=1
				Case 1152 in_slider(0)=2
				Case 1280 in_slider(0)=3
				Case 1600 in_slider(0)=4
			End Select
			Select set_scrbit_c
				Case 16 in_slider(1)=0
				Case 24 in_slider(1)=1
				Case 32 in_slider(1)=2
			End Select
			in_slider(2)=set_viewrange
			in_slider(3)=set_terrain
			in_slider(4)=set_water
			in_slider(5)=set_sky
			in_slider(6)=set_effects
			in_slider(7)=set_grass
			in_check(0)=set_2dfx
			in_check(1)=set_lightfx
			in_check(2)=set_windsway
			in_check(3)=set_fog
			in_check(4)=set_hwmultitex
			in_check(5)=set_mb
			in_slider(8)=set_mb_alpha#*100.
		;Settings SFX
		Case Cmenu_set_sfx
			in_slider(0)=set_musicvolume#*100.
			in_slider(1)=set_fxvolume#*100.
			in_input(0)=set_name$
			in_input(1)=set_serverport
		
		;Singleplayer
		Case Cmenu_sp
			in_scr_scr=0
			;Load Maps
			; 1 = Load first Map Image
			;-1 = Load only Singleplayer Maps
			;load_maps(1,-1)
			load_maps(1,0)
		
		;Singleplayer Load
		Case Cmenu_sp_load
			in_scr_scr=0
		
		;Random Map Settings
		Case Cmenu_random
			in_opt(3)=1
			in_opt(4)=0
			in_opt(5)=1
			in_scr_scr=0
		
		;Multiplayer
		Case Cmenu_mp
			usgn_setup()
			
		;Multiplayer Create Game
		Case Cmenu_mp_create
			If set_sv_name$="" Then set_sv_name$=ss$(sm$(39),set_name$)
			in_input(0)=set_sv_name$
			If set_sv_port=0 Then set_sv_port=set_serverport
			in_input(1)=set_serverport
			in_input(2)=set_sv_pw$
			If set_sv_playerlimit=0 Then set_sv_playerlimit=4
			in_input(3)=set_sv_playerlimit
			load_maps(1,1)
			If set_sv_map$<>"" Then in_win_map$=set_sv_map$
			
		;Credits
		Case Cmenu_credits
			in_input(0)=set_scry
			For Ttbt.Ttbt=Each Ttbt
				Delete Ttbt
			Next
			Local stream=ReadFile("sys\credits.inf")
			If stream=0 Then RuntimeError("Unable to read sys\credits.inf")
			While Not Eof(stream)
				Ttbt.Ttbt=New Ttbt
				Ttbt\txt$=ReadLine(stream)
			Wend
			CloseFile(stream)
	End Select
End Function


;### Menu
Function menu()
	
	;Unpaused?
	g_unpaused=1
	
	;Input
	getinput()
	
	;Cam Movement
	;MoveEntity cam,3.*f,0,0
	;Local center=CreatePivot()
	;PointEntity cam,center
	;FreeEntity center

	;Update Items
	cull_items()
	
	;Render
	UpdateWorld(f#)
	seq_update()
	
	e_environment_update_water()
	set_debug_rt_render=MilliSecs()
	RenderWorld()
	set_debug_rt_render=MilliSecs()-set_debug_rt_render
	mb_update()
	e_environment_water2dfx()
	
	;Update
	cull()
	
	grass_spread()
	e_environment_update()
	update_state()
	sfx_update()
	up_update()
	
	;Buttons
	Select m_menu
		
		;Main
		Case Cmenu_main
			
			;Title
			DrawImage gfx_title,200+((set_scrx-200)/2),200
			
			y=0
			
			
			;Adventure
			If game_m_adventure=1 Then
				If gui_button(5,5,sm$(40)) Then
					m_section=Csection_game_sp
					set_debug_testmap=0
					load_map("maps\adventure\map01.s2","")
				EndIf
				y=y+50
			EndIf
			
			;Random
			If game_m_random=1 Then
				If gui_button(5,5+y,sm$(41)) Then
					;randommapcreate()
					menu_set(Cmenu_random)
				EndIf
				y=y+50
			EndIf
			
			;Singleplayer
			If game_m_singleplayer=1 Then
				If gui_button(5,5+y,sm$(42)) Then menu_set(Cmenu_sp)
				y=y+50
			EndIf
				
			;Load Savegame
			If game_m_loadsave=1 Then
				If gui_button(5,5+y,sm$(144)) Then
					menu_set(Cmenu_sp_load)
					load_savegames()
					in_scr_scr=0
				EndIf
				y=y+50
			EndIf
			
			;Multiplayer
			If game_m_multiplayer=1 Then
				If gui_button(5,5+y,sm$(43)) Then menu_set(Cmenu_mp)
				y=y+50
			EndIf
			
			;Settings
			If gui_button(5,(set_scry/2)-25,sm$(44)) Then menu_set(Cmenu_set_in)
			
			;Editor
			If game_m_editor=1 Then
				If gui_button(5,(set_scry/2)+25,sm$(45)) Then editor_ini():m_section=Csection_editor
			EndIf
			
			;Credits
			If game_m_credits=1 Then
				If gui_button(5,(set_scry/2)+75,sm$(46)) Then menu_set(Cmenu_credits)
			EndIf
			
			;Quit
			If gui_button(5,(set_scry)-50,sm$(47),1,Cicon_x) Or in_escape Then
				If gui_msgdecide( ss$(sm$(48),set_moddir$) ,Cbmpf_red,sm$(2),sm$(1)) Then
					m_performquit=1
				EndIf
			EndIf
			
			;Version
			bmpf_txt_r(set_scrx-3,set_scry-17,Cversion$,Cbmpf_tiny)
			
			
			;Rightclick Explode Fun Stuff ;D
			If in_mhit(2)=1 Then
				pick=CameraPick(cam,in_mx,in_my)
				game_explosion(PickedX(),PickedY(),PickedZ(),50.,10.,1)
			EndIf
			
		;Options Input
		Case Cmenu_set_in,Cmenu_set_gfx,Cmenu_set_sfx,Cmenu_set_inscript
			;Buttons
			If game_scriptkeys>0 Then
				If gui_button(5,5,sm$(49)+" (1)") Then menu_set(Cmenu_set_in)
				If gui_button(5,55,sm$(49)+" (2)") Then menu_set(Cmenu_set_inscript)
				If gui_button(5,105,sm$(50)) Then menu_set(Cmenu_set_gfx)
				If gui_button(5,155,sm$(51)) Then menu_set(Cmenu_set_sfx)
			Else
				If gui_button(5,5,sm$(49)) Then menu_set(Cmenu_set_in)
				If gui_button(5,55,sm$(50)) Then menu_set(Cmenu_set_gfx)
				If gui_button(5,105,sm$(51)) Then menu_set(Cmenu_set_sfx)
			EndIf
			
			If gui_button(5,(set_scry)-100,sm$(3),1,Cicon_x) Then
				menu_set(Cmenu_main,0)
				load_settings()
				prepare_cache_viewranges()
				set_reboot=0
			EndIf
			If gui_button(5,(set_scry)-50,sm$(4),1,Cicon_ok) Then
				menu_set(Cmenu_main)
				save_settings()
				prepare_cache_viewranges()
				;Reboot
				If set_reboot=1 Then
					menu_set(Cmenu_reboot)
				EndIf
			EndIf
			
			;Window
			Select m_menu
				;Controls
				Case Cmenu_set_in
					DrawImage gfx_win,215,0
					DrawImage gfx_icons,236,5,Cicon_options
					If game_scriptkeys>0 Then
						bmpf_txt(236+37,10,sm$(49)+" (1)")
					Else
						bmpf_txt(236+37,10,sm$(49))
					EndIf
					DrawImage gfx_winbar,215,42
					
					bmpf_txt(236,63,sm$(52)+":",2)
					bmpf_txt(506,63,sm$(53)+":",2)
					
					menu_keysetting(0,sm$(54),Ckey_forward)
					menu_keysetting(20,sm$(55),Ckey_backward)
					menu_keysetting(40,sm$(56),Ckey_left)
					menu_keysetting(60,sm$(57),Ckey_right)
					menu_keysetting(80,sm$(58),Ckey_jump)
					;menu_keysetting(100,sm$(59),Ckey_laystay)
					
					menu_keysetting(130,sm$(60),Ckey_attack1)
					menu_keysetting(150,sm$(61),Ckey_attack2)
					menu_keysetting(170,sm$(62),Ckey_next)
					menu_keysetting(190,sm$(63),Ckey_prev)
					;menu_keysetting(210,sm$(64),Ckey_drop)
					
					menu_keysetting(240,sm$(65),Ckey_use)
					menu_keysetting(260,sm$(66),Ckey_chat)
					menu_keysetting(280,sm$(67),Ckey_char)
					menu_keysetting(300,sm$(68),Ckey_items)
					menu_keysetting(320,sm$(69),Ckey_diary)
					menu_keysetting(340,sm$(59),Ckey_sleep)
					
					If game_m_loadsave=1 Then
						menu_keysetting(360,sm$(70),Ckey_quickload)
						menu_keysetting(380,sm$(71),Ckey_quicksave)
					EndIf
					
					bmpf_txt(236,510,sm$(72)+":")
					If in_slider(0)<1 Then in_slider(0)=1
					bmpf_txt(436,510,sm$(73)+" "+(0.02*Float(in_slider(0))))
					gui_slider(636,510,100,0)
					gui_check(436,540,sm$(74),0)	;Invert
					;gui_check(636,540,sm$(75),1)	;Smooth
					
					
				;Graphics
				Case Cmenu_set_gfx
					DrawImage gfx_win,215,0
					DrawImage gfx_icons,236,5,Cicon_options
					bmpf_txt(236+37,10,sm$(50))
					DrawImage gfx_winbar,215,42
					
					;Resolution
					bmpf_txt(236,63,sm$(76)+":")
					gui_slider(636,63,4,0)
					Select in_slider(0)
						Case 0 bmpf_txt(436,63,"800x600")
						Case 1 bmpf_txt(436,63,"1024x768")
						Case 2 bmpf_txt(436,63,"1152x864")
						Case 3 bmpf_txt(436,63,"1280x960")
						Case 4 bmpf_txt(436,63,"1600x1200")
					End Select
					
					;Color Depth
					bmpf_txt(236,83,sm$(77)+":")
					gui_slider(636,83,2,1)
					Select in_slider(1)
						Case 0 bmpf_txt(436,83,"16 Bit")
						Case 1 bmpf_txt(436,83,"24 Bit")
						Case 2 bmpf_txt(436,83,"32 Bit")
					End Select
					
					bmpf_txt(236,123,sm$(78)+":")
					gui_slider(636,123,4,2)
					Select in_slider(2)
						Case 0 bmpf_txt(436,123,sm$(79))
						Case 1 bmpf_txt(436,123,sm$(80))
						Case 2 bmpf_txt(436,123,sm$(81))
						Case 3 bmpf_txt(436,123,sm$(82))
						Case 4 bmpf_txt(436,123,sm$(83))
					End Select
					
					bmpf_txt(236,163,sm$(84)+":")
					gui_slider(636,163,2,3)
					Select in_slider(3)
						Case 0 bmpf_txt(436,163,sm$(86))
						Case 1 bmpf_txt(436,163,sm$(87))
						Case 2 bmpf_txt(436,163,sm$(88))
					End Select
					
					bmpf_txt(236,183,sm$(90)+":")
					gui_slider(636,183,6,4)
					Select in_slider(4)
						Case 0 bmpf_txt(436,183,sm$(91))
						Case 1 bmpf_txt(436,183,sm$(85))
						Case 2 bmpf_txt(436,183,sm$(86))
						Case 3 bmpf_txt(436,183,sm$(87))
						Case 4 bmpf_txt(436,183,sm$(88))
						Case 5 bmpf_txt(436,183,sm$(89))
						Case 6 bmpf_txt(436,183,sm$(92))
					End Select
					
					bmpf_txt(236,203,sm$(93)+":")
					gui_slider(636,203,2,5)
					Select in_slider(5)
						Case 0 bmpf_txt(436,203,sm$(86))
						Case 1 bmpf_txt(436,203,sm$(87))
						Case 2 bmpf_txt(436,203,sm$(88))
					End Select
					
					bmpf_txt(236,223,sm$(94)+":")
					gui_slider(636,223,2,6)
					Select in_slider(6)
						Case 0 bmpf_txt(436,223,sm$(86))
						Case 1 bmpf_txt(436,223,sm$(87))
						Case 2 bmpf_txt(436,223,sm$(88))
					End Select
					
					bmpf_txt(236,243,sm$(95)+":")
					gui_slider(636,243,4,7)
					Select in_slider(7)
						Case 0 bmpf_txt(436,243,sm$(96))
						Case 1 bmpf_txt(436,243,sm$(97))
						Case 2 bmpf_txt(436,243,sm$(98))
						Case 3 bmpf_txt(436,243,sm$(99))
						Case 4 bmpf_txt(436,243,sm$(100))
					End Select
					
					bmpf_txt(236,283,sm$(101)+":")
					gui_check(436,283,sm$(102),0)
					gui_check(436,303,sm$(103),1)
					gui_check(436,323,sm$(104),2)
					gui_check(436,343,sm$(105),3)
					;gui_check(436,363,sm$(106),4) Multitexturing Stuff
					
					;Motion Blur
					bmpf_txt(236,383,sm$(107)+":")
					gui_check(436,383,"",5)
					bmpf_txt(436+20,383,se$(153))
					bmpf_txt(436,403,sm$(108))
					gui_slider(636,403,100,8)
					
					
					
				;Sounds & MP
				Case Cmenu_set_sfx
					;Sounds
					DrawImage gfx_win,215,0
					DrawImage gfx_icons,236,5,Cicon_options
					bmpf_txt(236+37,10,sm$(110))
					DrawImage gfx_winbar,215,42
					
					bmpf_txt(236,63,sm$(111)+":")
					gui_slider(636,63,100,0)
					If in_slider(0)>0 Then
						bmpf_txt(436,63,in_slider(0)+"%")
					Else
						bmpf_txt(436,63,sm$(112))
					EndIf
					
					bmpf_txt(236,93,sm$(113)+":")
					gui_slider(636,93,100,1)
					If in_slider(1)>0 Then
						bmpf_txt(436,93,in_slider(1)+"%")
					Else
						bmpf_txt(436,93,sm$(112))
					EndIf
					
					;Multiplayer
					If game_m_multiplayer=1 Then
					
						DrawImage gfx_winbar,215,200
						DrawImage gfx_icons,236,221,Cicon_options
						bmpf_txt(236+37,226,sm$(43))
						DrawImage gfx_winbar,215,258
						
						bmpf_txt(236,279,sm$(114)+":")
						gui_input(436,279,256,0)
						
						bmpf_txt(236,309,sm$(115)+":")
						gui_input(436,309,128,1)
						
						
					EndIf
				
				;Controls Script
				Case Cmenu_set_inscript
					DrawImage gfx_win,215,0
					DrawImage gfx_icons,236,5,Cicon_options
					If game_scriptkeys>0 Then
						bmpf_txt(236+37,10,sm$(49)+" (2)")
					Else
						bmpf_txt(236+37,10,sm$(49))
					EndIf
					DrawImage gfx_winbar,215,42
					
					bmpf_txt(236,63,sm$(52)+":",2)
					bmpf_txt(506,63,sm$(53)+":",2)
					
					For i=0 To 20
						If game_scriptkeyn$(i)<>"" Then
							menu_keysettingscript(i*20,game_scriptkeyn$(i),i)
						EndIf
					Next
				
					
			End Select
			
		;Reboot
		Case Cmenu_reboot
			
			If gui_button(5,(set_scry)-50,sm$(47),1,Cicon_ok) Then
				m_performquit=1
			EndIf
			
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,sm$(120))
			DrawImage gfx_winbar,215,42
					
			bmpf_txt(236,63,sm$(121))
			bmpf_txt(236,63+20,sm$(122))
			bmpf_txt(236,63+60,sm$(123))
			bmpf_txt(236,63+80,sm$(124))
			
		;Singleplayer
		Case Cmenu_sp
			
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,sm$(42))
			DrawImage gfx_winbar,215,42
					
			;List Maps
			Color 0,0,0
			Rect 236,195,500,379,1
			i=0:j=0
			y=0
			For Tsg.Tsg=Each Tsg
				j=j+1
				If j>in_scr_scr Then
					If gui_opt(236+5,200+i*20,Tsg\name$,j,0) Then
						load_map_header("maps\"+Tsg\name$+".s2")
					EndIf
					i=i+1
					If i>18 Then Exit
				EndIf
			Next
					
			;Scroll
			If gui_ibutton(742,200,Cicon_up,sm$(29),in_scr_scr>0) Then
				in_scr_scr=in_scr_scr-1
			EndIf
			If gui_ibutton(742,542-32-5,Cicon_down,sm$(30),i>18) Then
				in_scr_scr=in_scr_scr+1
			EndIf
			If in_mzs#>0. And in_scr_scr>0 Then in_scr_scr=in_scr_scr-1
			If in_mzs#<0. And i>18 Then in_scr_scr=in_scr_scr+1
			
			c=0
			For Tsg.Tsg=Each Tsg
				c=c+1
			Next
			in_scr_scr=gui_scrollbar(742,237,500-237,c,in_scr_scr,19)
					
			;Determine & Show
			loadtemp$=""
			i=0
			For Tsg.Tsg=Each Tsg
				i=i+1
				If i=in_opt(0) Then
					loadtemp$=Tsg\name$
				EndIf
			Next
			If loadtemp$="" Then
				Color 0,0,0
				Rect 236,63,96,72,1
				bmpf_txt(336,63,sm$(156),Cbmpf_red)
			Else
				If map_image<>0 Then DrawBlock map_image,236,63
				bmpf_txt(336,63,sm$(33)+":")
				bmpf_txt(450,63,loadtemp$)
				bmpf_txt(336,83,sm$(158)+":")
				bmpf_txt(450,83,sg_date$)
				bmpf_txt(336,103,sm$(35)+":")
				bmpf_txt(450,103,sg_time$+" "+sm$(36))
			EndIf
					
			;Load
			If gui_ibutton(742,542,Cicon_load,sm$(5),loadtemp$<>"") Then
				m_section=Csection_game_sp
				m_menu=0
				set_debug_testmap=0
				load_map("maps\"+loadtemp$+".s2","")
			EndIf
			
			;Buttons
			If gui_button(5,5,sm$(5),loadtemp$<>"") Then
				m_section=Csection_game_sp
				m_menu=0
				set_debug_testmap=0
				load_map("maps\"+loadtemp$+".s2","")
			EndIf
			
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then menu_set(Cmenu_main)

		;Singleplayer Load
		Case Cmenu_sp_load
			
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_load
			bmpf_txt(236+37,10,sm$(154))
			DrawImage gfx_winbar,215,42
			
			;List Savegames
			Color 0,0,0
			Rect 236,195,500,379,1
			i=0:j=0
			y=0
			For Tsg.Tsg=Each Tsg
				j=j+1
				If j>in_scr_scr Then
					If gui_opt(236+5,200+i*20,Tsg\name$,j,0) Then
						load_map_header("saves\"+Tsg\name$+".sav")
					EndIf
					i=i+1
					If i>18 Then Exit
				EndIf
			Next
			
			;Scroll
			If gui_ibutton(742,200,Cicon_up,sm$(29),in_scr_scr>0) Then
				in_scr_scr=in_scr_scr-1
			EndIf
			If gui_ibutton(742,542-37-37,Cicon_down,sm$(30),i>18) Then
				in_scr_scr=in_scr_scr+1
			EndIf
			If in_mzs#>0. And in_scr_scr>0 Then in_scr_scr=in_scr_scr-1
			If in_mzs#<0. And i>18 Then in_scr_scr=in_scr_scr+1
			
			c=0
			For Tsg.Tsg=Each Tsg
				c=c+1
			Next
			in_scr_scr=gui_scrollbar(742,237,500-237-37,c,in_scr_scr,19)
			
			;Determine & Show
			loadtemp$=""
			i=0
			For Tsg.Tsg=Each Tsg
				i=i+1
				If i=in_opt(0) Then
					loadtemp$=Tsg\name$
					Exit
				EndIf
			Next
			If loadtemp$="" Then
				Color 0,0,0
				Rect 236,63,96,72,1
				bmpf_txt(336,63,sm$(156),Cbmpf_red)
			Else
				If map_image<>0 Then DrawBlock map_image,236,63
				bmpf_txt(336,63,sm$(157)+":")
				bmpf_txt(450,63,loadtemp$)
				bmpf_txt(336,83,sm$(158)+":")
				bmpf_txt(450,83,sg_date$)
				bmpf_txt(336,103,sm$(35)+":")
				bmpf_txt(450,103,sg_time$+" "+sm$(36))
			EndIf
			
			;Delete
			If gui_ibutton(742,542-37,Cicon_x,sm$(189),loadtemp$<>"") Then
				If gui_msgdecide(ss$(sm$(190),loadtemp$),Cbmpf_red,sm$(2),sm$(1)) Then
					DeleteFile("saves\"+loadtemp$+".sav")
					load_savegames()
					in_scr_scr=0
				EndIf
			EndIf
						
			;Load
			If gui_ibutton(742,542,Cicon_load,sm$(154),loadtemp$<>"") Then
				m_section=Csection_game_sp
				m_menu=0
				set_debug_testmap=0
				load_map("saves\"+loadtemp$+".sav","")
			EndIf

			;Buttons
			If gui_button(5,5,sm$(5),loadtemp$<>"") Then
				m_section=Csection_game_sp
				m_menu=0
				set_debug_testmap=0
				load_map("saves\"+loadtemp$+".sav","")
			EndIf
			
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then menu_set(Cmenu_main)
		
		;Random Map Settings
		Case Cmenu_random
			
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_all
			bmpf_txt(236+37,10,sm$(41))
			DrawImage gfx_winbar,215,42
			
			;Size
			bmpf_txt(236,63,se$(30)+":")
			gui_opt(436,63,se$(31),1,3)
			gui_opt(436,63+20,se$(32),3,3)
			gui_opt(436,63+40,se$(33),4,3)
			
			;Terrain
			bmpf_txt(236,143,se$(34)+":")
			gui_opt(436,143,se$(35),0,4)
			gui_opt(436,143+20,se$(36),1,4)
			gui_opt(436,143+40,se$(37),2,4)
			gui_opt(436,143+60,se$(38),3,4)
			gui_opt(436,143+80,se$(39),4,4)
			gui_opt(436,143+100,se$(40),5,4)
			
			;Profile
			bmpf_txt(236,283,se$(129)+":")
			y=0
			Color 0,0,0
			Rect 431,278,306,298,1
			j=0
			y=0
			For i=1 To Crandom_c
				If Drprofile$(i)<>"" Then
					j=j+1
					If j>in_scr_scr
						gui_opt(436,283+(y*20),Drprofile$(i),i,5)
						y=y+1
						If y>13 Then Exit
					EndIf
				EndIf
			Next
			
			;Scroll
			If gui_ibutton(742,283,Cicon_up,sm$(29),in_scr_scr>0) Then
				in_scr_scr=in_scr_scr-1
			EndIf
			If gui_ibutton(742,542-37,Cicon_down,sm$(30),y>13) Then
				in_scr_scr=in_scr_scr+1
			EndIf
			
			If in_mzs#>0. And in_scr_scr>0 Then in_scr_scr=in_scr_scr-1
			If in_mzs#<0. And y>13 Then in_scr_scr=in_scr_scr+1
			
			c=0
			For i=1 To Crandom_c
				If Drprofile$(i)<>"" Then c=c+1
			Next
			in_scr_scr=gui_scrollbar(742,320,180,c,in_scr_scr,14)
			

			;Load
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				load_randommap(in_opt(5))
				randommapcreate()
			EndIf
			
			;Buttons
			If gui_button(5,5,sm$(5)) Then
				load_randommap(in_opt(5))
				randommapcreate()
			EndIf
			
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then menu_set(Cmenu_main)
		
		;Multiplayer
		Case Cmenu_mp
			
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,sm$(125))
			DrawImage gfx_winbar,215,42
			
			;Buttons
			If gui_button(5,5,sm$(125)) Then menu_set(Cmenu_mp)
			If gui_button(5,5+50,sm$(126),0) Then menu_set(Cmenu_mp_lan)
			If gui_button(5,5+100,sm$(127)) Then menu_set(Cmenu_mp_create)
			
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then menu_set(Cmenu_main)
			
			;Scrolltext
			bmpf_txt_scroll(236,60,in_usgn_msg$,0,740,in_usgn_msgscr#)
			in_usgn_msgscr#=in_usgn_msgscr#-1.*f#
			If in_t500go Then
				If in_usgn_msgscr#<-bmpf_len(in_usgn_msg$) Then
					in_usgn_msgscr#=500
				EndIf
			EndIf
			
			;Descriptions
			bmpf_txt(260,80,sm$(128),Cbmpf_dark)
			bmpf_txt(450,80,sm$(33),Cbmpf_dark)
			bmpf_txt(620,80,sm$(129),Cbmpf_dark)
			bmpf_txt(690,80,sm$(130),Cbmpf_dark)
			
			;Black Rect
			Color 0,0,0
			Rect 236,100,500,473,1
			
			;List Servers
			i=0:j=0
			For Tserver.Tserver=Each Tserver
				If Tserver\name<>"" Then
					j=j+1
					If j>in_scr_scr Then
						
						;Over / Click
						over=0
						If in_mx>=236 Then
							If in_my>=105+(i*20) Then
								If in_mx<=736 Then
									If in_my<105+(i*20)+20 Then
										over=1
										gui_tt(in_mx,in_my,Tserver\name$+" ("+DottedIP(Tserver\ip)+":"+Tserver\port+") "+sm$(132))
										;Click
										If in_mhit(1) Then
											usgn_joinserver(Tserver\ip,Tserver\port)
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						
						;Password
						If Tserver\password=1 Then
							If over Then
								DrawImage gfx_icons,233,99+(i*20),Cicon_pw
							Else
								DrawImage gfx_icons_passive,233,99+(i*20),Cicon_pw
							EndIf
						EndIf
						
						;Name
						bmpf_txt_ml(260,105+(i*20),Tserver\name$,over,450-5)
						
						;Map
						bmpf_txt_ml(450,105+(i*20),Tserver\map$,over,620-5)
						
						;Players
						col=Cbmpf_yellow
						If Tserver\players=Tserver\maxplayers Then col=Cbmpf_red
						bmpf_txt(620,105+(i*20),Tserver\players+"/"+Tserver\maxplayers,col)
						
						
						;Ping
						If Tserver\ping<>0 Then
							ping=Tserver\ping
							col=Cbmpf_green
							If ping>100 Then col=Cbmpf_yellow
							If ping>200 Then col=Cbmpf_red
							bmpf_txt(690,105+(i*20),ping,col)
						EndIf
							
						
						i=i+1
						If i>22 Then Exit
					EndIf
				EndIf
			Next
			
			;Scroll
			If gui_ibutton(742,100,Cicon_up,sm$(29),in_scr_scr>0) Then
				in_scr_scr=in_scr_scr-1
			EndIf
			If gui_ibutton(742,542,Cicon_down,sm$(30),i>22) Then
				in_scr_scr=in_scr_scr+1
			EndIf
			
			;Update Net Stuff
			If udp<>0 Then
				usgn_update()
			EndIf
			
		;Multiplayer Create Game
		Case Cmenu_mp_create
		
			;Window
			DrawImage gfx_win,215,0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,sm$(127))
			DrawImage gfx_winbar,215,42
			
			;Buttons
			If gui_button(5,5,sm$(125)) Then menu_set(Cmenu_mp)
			If gui_button(5,5+50,sm$(126),0) Then menu_set(Cmenu_mp_lan)
			If gui_button(5,5+100,sm$(127)) Then menu_set(Cmenu_mp_create)
			
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then menu_set(Cmenu_main)
			
			;Server Name
			bmpf_txt(236,63,sm$(133)+":")
			gui_input(436,63,256,0)
			
			;Server Port		
			bmpf_txt(236,93,sm$(115)+":")
			gui_input(436,93,64,1)
			
			;Server Map
			bmpf_txt(236,133,sm$(33)+":")
			
			If in_win_map$="" Then
				bmpf_txt(436,133,sm$(134),Cbmpf_red)
				Color 0,0,0
				Rect 436,163,96,72,1
			Else
				bmpf_txt(436,133,in_win_map$)
				If map_image<>0 Then DrawBlock map_image,436,163
			EndIf
			
			;Browse
			If gui_ibutton(742,133,Cicon_dir,sm$(135)) Then
				If Not gui_win_map(1,in_win_map$) Then
					If in_win_map$<>"" Then load_map_header("maps\"+in_win_map$+".s2")
				EndIf
			EndIf
			
			;Server Password
			bmpf_txt(236,243,sm$(136)+":")
			gui_input(436,243,128,2)
			
			;Player Limit
			bmpf_txt(236,273,sm$(137)+":")
			gui_input(436,273,64,3)
			
			;Start
			If gui_ibutton(742,542,Cicon_ok,sm$(127)) Then
				;Cache Values
				set_sv_name$=Trim(in_input$(0))
				If Len(set_sv_name$)>20 Then set_sv_name$=Trim(Left(set_sv_name$,20))
				If set_sv_name$="" Then set_sv_name$="Server"
				set_sv_port=Abs(Int(in_input$(1)))
				set_sv_ip=0
				set_sv_map$=in_win_map$
				set_sv_pw$=in_input$(2)
				set_sv_playerlimit=Int(in_input$(3))
				If set_sv_playerlimit<1 Then set_sv_playerlimit=1
				If set_sv_playerlimit>4 Then set_sv_playerlimit=4
				;Setup
				menu_set(Cmenu_main)
				usgn_hostserver(set_sv_port)
			EndIf
		
						
		;Credits
		Case Cmenu_credits	
			
			;Scroll
			in_input(0)=Float(in_input(0))-Float(1.*f#)
			
			;Draw
			y=in_input(0)
			For Ttbt.Ttbt=Each Ttbt
				If (y+20)>0 And y<set_scry Then
					If Left(Ttbt\txt$,1)="!" Then
						bmpf_txt_c(set_scrx/2,y,Mid(Ttbt\txt$,4,-1),Int(Mid(Ttbt\txt$,2,1)))
					Else
						bmpf_txt_c(set_scrx/2,y,Ttbt\txt$)
					EndIf
				EndIf
				y=y+20
			Next
			If y<0 Then in_input(0)=set_scry
			
			;Quit Button
			If gui_button(5,(set_scry)-50,sm$(131),1,Cicon_x) Then
				menu_set(Cmenu_main)
			EndIf
		
	
	End Select
	
	;Particles
	p_update()
	
	;Sequence Blend
	seq_blend()
	
	;Endinput
	endinput()

	
	;### Hotkeys
	
	;Checkmap
	If KeyHit(88) Then
		editor_testmap(1)
	EndIf
	
	
End Function



;### Menu Keysetting
Function menu_keysetting(y,name$,id)
	If in_getkey<>id Then
		;Input
		over=0
		If in_getkey=-1 Then
			If in_my>=83+y Then
				If in_my<=83+y+20 Then
					If in_mx>=236 Then
						If in_mx<=775 Then
							over=1
							If in_mhit(1) Then
								in_getkey=id
								play(sfx_click)
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		;Draw
		bmpf_txt(236,83+y,name$,over)
		bmpf_txt(506,83+y,in_keyname$(in_keys(id)),over)
	Else
		;Draw
		bmpf_txt(236,83+y,name$,1)
		bmpf_txt(506,83+y,sm$(138),in_cursor)
		
		;Getcode
		Local gotkey=0
		If in_mhit(1) Then gotkey=256:in_mhit(1)=0:in_mdown(1)=0
		If in_mhit(2) Then gotkey=257
		If in_mhit(3) Then gotkey=258
		If in_mhit(4) Then gotkey=259
		If in_mhit(5) Then gotkey=260
		If Int(in_mzs#)<>0 Then
			If Int(in_mzs#)>0 Then
				gotkey=261
			Else
				gotkey=262
			EndIf
		EndIf
		If gotkey=0 Then
			For i=2 To 255
				If KeyHit(i) Then
					If in_keyname$(i)<>"" Then gotkey=i:Exit
				EndIf
			Next
			If in_escape=1 Then
				in_getkey=-1
				gotkey=0
				play(sfx_fail)
			EndIf
		EndIf
		
		;Set
		If gotkey<>0 Then
			For i=0 To 20
				If in_keys(i)=gotkey Then in_keys(i)=0
				If game_scriptkey(i)=gotkey Then game_scriptkey(i)=0
			Next
			in_keys(id)=gotkey
			in_getkey=-1
			play(sfx_click)
		EndIf
	EndIf
End Function


;### Menu Keysetting Script
Function menu_keysettingscript(y,name$,id)
	If in_getkey<>id Then
		;Input
		over=0
		If in_getkey=-1 Then
			If in_my>=83+y Then
				If in_my<=83+y+20 Then
					If in_mx>=236 Then
						If in_mx<=775 Then
							over=1
							If in_mhit(1) Then
								in_getkey=id
								play(sfx_click)
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		;Draw
		bmpf_txt(236,83+y,name$,over)
		bmpf_txt(506,83+y,in_keyname$(game_scriptkey(id)),over)
	Else
		;Draw
		bmpf_txt(236,83+y,name$,1)
		bmpf_txt(506,83+y,sm$(138),in_cursor)
		
		;Getcode
		Local gotkey=0
		If in_mhit(1) Then gotkey=256:in_mhit(1)=0:in_mdown(1)=0
		If in_mhit(2) Then gotkey=257
		If in_mhit(3) Then gotkey=258
		If in_mhit(4) Then gotkey=259
		If in_mhit(5) Then gotkey=260
		If Int(in_mzs#)<>0 Then
			If Int(in_mzs#)>0 Then
				gotkey=261
			Else
				gotkey=262
			EndIf
		EndIf
		If gotkey=0 Then
			For i=2 To 255
				If KeyHit(i) Then
					If in_keyname$(i)<>"" Then gotkey=i:Exit
				EndIf
			Next
			If in_escape=1 Then
				in_getkey=-1
				gotkey=0
				play(sfx_fail)
			EndIf
		EndIf
		
		;Set
		If gotkey<>0 Then
			For i=0 To 20
				If in_keys(i)=gotkey Then in_keys(i)=0
				If game_scriptkey(i)=gotkey Then game_scriptkey(i)=0
			Next
			game_scriptkey(id)=gotkey
			in_getkey=-1
			play(sfx_click)
		EndIf
	EndIf
End Function
