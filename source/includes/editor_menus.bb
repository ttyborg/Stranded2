;############################################ EDITOR MENUS

Function editor_menus()

	Select m_menu
			
		;Blank
		Case 0
		
		;New
		Case Cmenu_ed_new
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_new
			bmpf_txt(236+37,10,se$(1)+"...")
			DrawImage gfx_winbar,215,42
			
			;Size
			bmpf_txt(236,63,se$(30)+":")
			
			bmpf_txt(450,63,se$(31)+":",4)
			gui_opt(450,63+20,"16x16",0,3)
			gui_opt(450,63+40,"32x32",1,3)
			;gui_opt(450,63+60,"64x64",2,3)
			
			bmpf_txt(550,63,se$(32)+":",1)
			gui_opt(550,63+20,"64x64",3,3) ;gui_opt(550,63+20,"128x128",3,3)
			gui_opt(550,63+40,"128x128",4,3) ;gui_opt(550,63+40,"256x256",4,3)
			;gui_opt(550,63+60,"512x512",5,3)
			
			bmpf_txt(650,63,se$(33)+":",3)
			gui_opt(650,63+20,"256x256",6,3) ;gui_opt(650,63+20,"1024x1024",6,3)
			;gui_opt(650,63+40,"512x512",7,3,0) ;gui_opt(650,63+40,"2048x2048",7,3,0)
			;gui_opt(650,63+60,"4096x4096",8,3,0)
			
			;Terrain
			bmpf_txt(236,163,se$(34)+":")
			gui_opt(450,163,se$(35),0,4)
			gui_opt(450,163+20,se$(36),1,4)
			gui_opt(450,163+40,se$(37),2,4)
			gui_opt(450,163+60,se$(38),3,4)
			gui_opt(450,163+80,se$(39),4,4)
			gui_opt(450,163+100,se$(40),5,4)
			
			;Vegetation
			;bmpf_txt(236,303,se$(41)+":")
			;gui_opt(450,303,se$(42),0,5)
			;gui_opt(450,303+20,se$(43),1,5)
			;gui_opt(450,303+40,se$(44),2,5)
			;gui_opt(450,303+60,se$(45),3,5)
			;gui_opt(450,303+80,se$(46),4,5)
			;gui_opt(450,303+100,se$(47),5,5)
			
			;gui_check(450,303+140,se$(48),5,in_opt(5)>0)
			;gui_check(450,303+160,se$(49),6,in_opt(5)>0)
			;gui_check(450,303+180,se$(50),7,in_opt(5)>0)
			;gui_check(450,303+200,se$(51),8,in_opt(5)>0)
			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				editor_defaultmapsettings()				;Standardmapeinstellungen
				editor_genmap()							;Map generieren (Zufall und so)
				editor_gencolormap()					;Colormap generieren (aus Heightmap)
				grass_map()								;Grassmap generieren (aus Colormap)
				grass_heightmap()						;Grassmap anpassen (an Heightmap)
				m_menu=0
				in_edmap$=""
				editor_resetcam()
			EndIf
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			
		;Settings
		Case Cmenu_ed_settings
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(4)+"...")
			DrawImage gfx_winbar,215,42
			
			;Time
			bmpf_txt(236,63,se$(52)+":")
			gui_input(450,63,128,3)
			gui_check(590,63,se$(53),5)
			
			;Skybox
			bmpf_txt(236,88,se$(54)+":")
			gui_input(450,88,256,4,1,se$(55))
			;Browse
			If gui_ibutton(742,83,Cicon_dir,se$(75)) Then
				If gui_win_file(se$(56),set_rootdir$+"mods\"+set_moddir$+"\skies","",1) Then
					in_input$(4)=editor_getskybox$(in_win_file$)
				EndIf
			EndIf
			
			;Multiplayer
			bmpf_txt(236,118,sm$(43)+":")
			gui_check(450,118,se$(57),6)
			
			;Climate
			bmpf_txt(236,143,se$(58)+":")
			gui_opt(450,143,se$(59),0,3)			;Normal (Sun/Rain)
			gui_opt(450,143+20,se$(60),1,3)			;Arctic (Sun/Snow)
			gui_opt(450,143+40,se$(61),2,3)			;Sun only
			gui_opt(450,143+60,se$(62),3,3)			;Rain only
			gui_opt(580,143+40,se$(63),4,3)			;Snow only
			gui_opt(580,143+60,se$(64),5,3)			;Lightning only
			
			;Background Music
			bmpf_txt(236,225,se$(65)+":")
			gui_input(450,225,256,5,1,se$(66))
			;Browse
			If gui_ibutton(742,220,Cicon_dir,se$(75)) Then
				If gui_win_file(se$(67),set_rootdir$+"mods\"+set_moddir$+"\sfx","",1) Then
					in_input$(5)=in_win_file$
				EndIf
			EndIf
			
			;Fog
			bmpf_txt(236,255,se$(68)+":")
			If gui_colorbox(map_fog(0),map_fog(1),map_fog(2),450,255,32,16,se$(76))
				in_slider(0)=Float(map_fog(0))/2.55
				in_slider(1)=Float(map_fog(1))/2.55
				in_slider(2)=Float(map_fog(2))/2.55
				If gui_win_color() Then
					map_fog(0)=in_win_color(0)
					map_fog(1)=in_win_color(1)
					map_fog(2)=in_win_color(2)
				EndIf
			EndIf
			
			gui_check(500,255,se$(69),7)
			
			;Global
			bmpf_txt(236,285,se$(70))
			gui_tb(450,285,285,16)
			If gui_ibutton(742,285,Cicon_script,se$(71)) Then 
				editor_savescript(0)
				in_input(3)=0
				editor_setmenu(Cmenu_ed_scripts)
			EndIf
			
			;Checkmap
			If gui_ibutton(236,542,Cicon_check,se$(72)) Then
				editor_testmap()
			EndIf
			;Options
			If gui_ibutton(236+37,542,Cicon_options,se$(73)) Then editor_setmenu(Cmenu_ed_options)
			;Infolist
			If gui_ibutton(236+37+37,542,Cicon_list,se$(74)) Then editor_setmenu(Cmenu_ed_infolist)
			;Attachments			
			If gui_ibutton(236+37+37+37,542,Cicon_dir,se$(186)) Then editor_setmenu(Cmenu_ed_attachments) 
			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				editor_savesettings()
				m_menu=0
				;Sky
				If env_sky<>0 Then FreeEntity env_sky
				If map_skybox$="" Then
					env_sky=e_skybox("skies\sky")
				Else
					env_sky=e_skybox("skies\"+map_skybox$)
				EndIf
				EntityFX env_sky,1+8
				EntityOrder env_sky,3
				PositionEntity env_sky,0,500,0
				e_environment_update_light()
			EndIf
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			
		;Infolist
		Case Cmenu_ed_infolist
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_list
			bmpf_txt(236+37,10,se$(74))
			DrawImage gfx_winbar,215,42
			
			;Size (Terrain)
			bmpf_txt(236,63,se$(77)+":")
			bmpf_txt(450,63,ter_size+"x"+ter_size)
			;Size (Colormap)
			bmpf_txt(236,83,se$(78)+":")
			If ter_tex_color<>0 Then
				bmpf_txt(450,83,TextureWidth(ter_tex_color)+"x"+TextureHeight(ter_tex_color)+" Pixel")
			Else
				bmpf_txt(450,83,"N/A",Cbmpf_red)
			EndIf
			
			
			;Counts
			bmpf_txt(236,123,se$(7)+":")
			c1=0:For Tobject.Tobject=Each Tobject:c1=c1+1:Next
			bmpf_txt(450,123,c1)
			bmpf_txt(236,143,se$(8)+":")
			c2=0:For Tunit.Tunit=Each Tunit:c2=c2+1:Next
			bmpf_txt(450,143,c2)
			bmpf_txt(236,163,se$(9)+":")
			c3=0
			cstored=0
			For Titem.Titem=Each Titem
				c3=c3+1
				If Titem\parent_class<>0 Then cstored=cstored+1
			Next
			bmpf_txt(450,163,c3+" "+ss$(se$(79),cstored))
			bmpf_txt(236,183,se$(10)+":")
			c4=0:For Tinfo.Tinfo=Each Tinfo:c4=c4+1:Next
			bmpf_txt(450,183,c4)
			bmpf_txt(236,203,se$(80)+":")
			c5=0:For Tstate.Tstate=Each Tstate:c5=c5+1:Next
			bmpf_txt(450,203,c5)
			bmpf_txt(236,223,se$(81)+":")
			c6=0:For Tx.Tx=Each Tx:c6=c6+1:Next
			bmpf_txt(450,223,c6)
			
			bmpf_txt(236,243,"----------",Cbmpf_dark)
			bmpf_txt(450,243,"----------",Cbmpf_dark)
			
			bmpf_txt(236,263,se$(82)+":")
			bmpf_txt(450,263,(c1+c2+c3+c4+c5+c6)+ss$(se$(83),(c1+c2+c3)))  
			
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then m_menu=0
			
		;Options
		Case Cmenu_ed_options
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(73))
			DrawImage gfx_winbar,215,42
			
			;Health
			If gui_ibutton(236,63,Cicon_ok,se$(84)) Then
				If gui_msgdecide(se$(85))
					Local objectfit
					Local unitfit
					For Tobject.Tobject=Each Tobject
						If Tobject\health_max#>Dobject_health#(Tobject\typ) Then
							Tobject\health_max#=Dobject_health#(Tobject\typ)
							If Tobject\health#>Tobject\health_max# Then
								Tobject\health#=Tobject\health_max#
							EndIf
							objectfit=objectfit+1
						ElseIf Tobject\health_max#<Dobject_health#(Tobject\typ) Then
							Tobject\health_max#=Dobject_health#(Tobject\typ)
							Tobject\health#=Tobject\health_max#
							objectfit=objectfit+1
						EndIf
					Next
					For Tunit.Tunit=Each Tunit
						If Tunit\health_max#>Dunit_health#(Tunit\typ) Then
							Tunit\health_max#=Dunit_health#(Tunit\typ)
							If Tunit\health#>Tunit\health_max# Then
								Tunit\health#=Tunit\health_max#
							EndIf
							unitfit=unitfit+1
						ElseIf Tunit\health_max#<Dunit_health#(Tunit\typ) Then 
							Tunit\health_max#=Dunit_health#(Tunit\typ)
							Tunit\health#=Tunit\health_max#
							unitfit=unitfit+1
						EndIf
					Next
					If objectfit+unitfit>0 Then
						gui_msg(ss$(se$(86),objectfit,unitfit),Cbmpf_green)
					Else
						gui_msg(se$(87),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			bmpf_txt(273,63,se$(84))
			bmpf_txt(273,79,se$(88),Cbmpf_tiny)
			
			;Free
			If gui_ibutton(236,100,Cicon_ok,se$(89),in_opt(0)>0) Then
				Local name$
				Select in_opt(0)
					Case Cclass_object name$=Dobject_name$(in_object_sel)
					Case Cclass_unit name$=Dunit_name$(in_unit_sel)
					Case Cclass_item name$=Ditem_name$(in_item_sel)
					Case Cclass_info name$=Dinfo_name$(in_info_sel)
				End Select
				If gui_msgdecide(ss$(se$(90),name$))
					Local delcount
					Select in_opt(0)
						Case Cclass_object
							For Tobject.Tobject=Each Tobject
								If Tobject\typ=in_object_sel Then
									free_object(Tobject\id)
									delcount=delcount+1
								EndIf
							Next
						Case Cclass_unit
							For Tunit.Tunit=Each Tunit
								If Tunit\typ=in_unit_sel Then
									free_unit(Tunit\id)
									delcount=delcount+1
								EndIf
							Next
						Case Cclass_item
							Local delstored=gui_msgdecide(se$(91),0,sm$(2),sm$(1))
							For Titem.Titem=Each Titem
								If Titem\typ=in_item_sel Then
									If delstored=1 Or Titem\parent_class=0 Then
										free_item(Titem\id)
										delcount=delcount+1
									EndIf
								EndIf
							Next
						Case Cclass_info
							For Tinfo.Tinfo=Each Tinfo
								If Tinfo\typ=in_info_sel Then
									free_info(Tinfo\id)
									delcount=delcount+1
								EndIf
							Next
					End Select
					If delcount>0 Then
						gui_msg( ss$(se$(92),delcount,name$) ,Cbmpf_green)
					Else
						gui_msg( ss$(se$(93),name$) ,Cbmpf_red)
					EndIf
				EndIf
			EndIf
			bmpf_txt(273,100,se$(89))
			bmpf_txt(273,116,se$(94),Cbmpf_tiny)
			
			;Free
			If gui_ibutton(236,137,Cicon_ok,se$(95)) Then
				Local statedel
				Local scriptdel
				Local itemdel
				If gui_msgdecide(se$(96)) Then
					For Tstate.Tstate=Each Tstate
						free_state(Tstate\typ,Tstate\parent_class,Tstate\parent_id)
						statedel=statedel+1
					Next
				EndIf
				If gui_msgdecide(se$(97)) Then
					For Tx.Tx=Each Tx
						If Tx\mode=0 Then
							Delete Tx
							scriptdel=scriptdel+1
						EndIf
					Next
				EndIf
				If gui_msgdecide(se$(98)) Then
					For Titem.Titem=Each Titem
						If Titem\parent_class<>0 Then
							free_item(Titem\id)
							itemdel=itemdel+1
						EndIf
					Next
				EndIf
				If statedel+scriptdel+itemdel>0 Then
					gui_msg( ss$(se$(99),statedel,scriptdel,itemdel),Cbmpf_green)
				Else
					gui_msg(se$(101),Cbmpf_red)
				EndIf
			EndIf
			bmpf_txt(273,137,se$(95))
			bmpf_txt(273,153,se$(100),Cbmpf_tiny)

			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then editor_setmenu(Cmenu_ed_settings)
			
			
		;Attachments
		Case Cmenu_ed_attachments
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_dir
			bmpf_txt(236+37,10,se$(186))
			DrawImage gfx_winbar,215,42
			
			;Black Rect
			Color 0,0,0
			Rect 236,63,537,435+37,1
			
			;List Attachments
			Local y=0
			For Tat.Tat=Each Tat
				
				;Name
				bmpf_txt(236+37+5,63+(y*37)+12,Tat\path$)
				
				;Button
				If gui_ibutton(236+5,63+(y*37)+5,Cicon_x,se$(188)) Then
					Delete Tat
				EndIf
				
				;Pos
				y=y+1
				If y>=12 Then
					y=-1
					Exit
				EndIf
			Next
			
			;Add
			If gui_ibutton(236,542,Cicon_inc,se$(187)+"...",y<>-1) Then
				If gui_win_file(se$(187)+"...",set_rootdir$+"mods\"+set_moddir$,"") Then
				;gui_win_file(se$(67),set_rootdir$+"mods\"+set_moddir$+"\sfx","",1)
					Tat.Tat=New Tat
					If Instr(in_win_path$,set_rootdir$+"mods\"+set_moddir$+"\")=1 Then
						in_win_path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$+"\","")
						Tat\path$=in_win_path$+in_win_file$
					Else
						gui_msg(se$(189),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then editor_setmenu(Cmenu_ed_settings)
		
		
		
		;Random
		Case Cmenu_ed_random
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_all
			bmpf_txt(236+37,10,se$(216))
			DrawImage gfx_winbar,215,42
			
			;Amount
			bmpf_txt(236,63,se$(143)+":")
			gui_input(450,63,256,3)
		
			;Objects
			bmpf_txt(236,100,se$(203)+":")
			gui_check(450,100,se$(7),5)
			gui_check(450,100+20,se$(8),6)
			gui_check(450,100+20+20,se$(9),7)
			
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
			
			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If Int(in_input(3))>0 Then
					load_randommap(in_opt(5))
					randommap(Int(in_input(3)),in_check(5),in_check(6),in_check(7))
				EndIf
				m_menu=0
			EndIf
		
			
		
		;Quit
		Case Cmenu_ed_quit
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_x
			bmpf_txt(236+37,10,se$(5)+"...")
			DrawImage gfx_winbar,215,42
			
			;Text
			bmpf_txt(236,63,se$(102))
			bmpf_txt(236,83,se$(103),3)
			
			;Cancel/Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				editor_quit()
				menu_start()
			EndIf
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			
		;Load
		Case Cmenu_ed_load
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_load
			bmpf_txt(236+37,10,se$(2)+"...")
			DrawImage gfx_winbar,215,42
			
			;Name/PW
			bmpf_txt(236,63,se$(104)+":")
			gui_input(450,63,256,3)
			bmpf_txt(236,88,se$(105)+":")
			gui_input(450,88,128,4)
			
			;Browse
			If gui_ibutton(742,63,Cicon_dir,se$(75)) Then
				If gui_win_file(se$(2)+"...",set_rootdir$+"mods\"+set_moddir$+"\maps","",0) Then
					path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$+"\maps","")
					If Left(path$,1)="\" Then path$=Mid(path$,2,-1)
					in_input$(3)=path$+stripext(in_win_file$)
				EndIf
			EndIf
			
			;Recent
			y=1
			doload=0
			For Trecent.Trecent=Each Trecent
				If gui_txtb(236,110+(y*20),Trecent\file$) Then
					in_input(3)=Trecent\file$
					If Left(in_input(3),5)="maps\" Or Left(in_input(3),5)="maps/" Then
						in_input(3)=Mid(in_input(3),6,-1)
					EndIf
					doload=1
				EndIf
				y=y+1
			Next
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Or doload Then
				in_input(3)=Replace(in_input(3),".S2","")
				in_input(3)=Replace(in_input(3),".s2","")
				If Instr(in_input(3),":") Then
					If load_map(in_input(3)+".s2",in_input$(4)) Then
						load_recent(in_input(3)+".s2")
						in_edmap$=in_input(3)
						m_menu=0
						editor_resetcam()
					Else
						gui_msg(se$(106),Cbmpf_red)
						editor_ini(menu=Cmenu_ed_new)
					EndIf
				Else
					If load_map("maps\"+in_input(3)+".s2",in_input$(4)) Then
						load_recent("maps\"+in_input(3)+".s2")
						in_edmap$=in_input(3)
						m_menu=0
						editor_resetcam()
					Else
						gui_msg(se$(106),Cbmpf_red)
						editor_ini(menu=Cmenu_ed_new)
					EndIf
				EndIf
			EndIf
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			
			
		;Save
		Case Cmenu_ed_save
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_save
			bmpf_txt(236+37,10,se$(3)+"...")
			DrawImage gfx_winbar,215,42
			
			;Name/PW
			bmpf_txt(236,63,se$(104)+":")
			gui_input(450,63,256,3)
			bmpf_txt(236,88,se$(105)+":")
			gui_input(450,88,128,4)
			
			;Browse
			If gui_ibutton(742,63,Cicon_dir,se$(75)) Then
				If gui_win_file(se$(3)+"...",set_rootdir$+"mods\"+set_moddir$+"\maps","",0,1) Then
					path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$+"\maps","")
					If Left(path$,1)="\" Then path$=Mid(path$,2,-1)
					in_input$(3)=path$+stripext(in_win_file$)
				EndIf
			EndIf
			
			;Recent
			y=1
			dosave=0
			For Trecent.Trecent=Each Trecent
				If gui_txtb(236,110+(y*20),Trecent\file$) Then
					in_input(3)=Trecent\file$
					If Left(in_input(3),5)="maps\" Or Left(in_input(3),5)="maps/" Then
						in_input(3)=Mid(in_input(3),6,-1)
					EndIf
					dosave=1
				EndIf
				y=y+1
			Next
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Or dosave Then
				in_input(3)=Replace(in_input(3),".S2","")
				in_input(3)=Replace(in_input(3),".s2","")
				If Instr(in_input(3),":") Then
					If FileType(in_input(3)+".s2")=1 Then
						If gui_msgdecide(se$(107),Cbmpf_red) Then
							If save_map(in_input(3)+".s2","map",in_input$(4)) Then
								load_recent(in_input(3)+".s2")
								in_edmap$=in_input(3)
								m_menu=0
							Else
								gui_msg(se$(108),Cbmpf_red)
							EndIf
						EndIf
					Else
						If save_map(in_input(3)+".s2","map",in_input$(4)) Then
							load_recent(in_input(3)+".s2")
							in_edmap$=in_input(3)
							m_menu=0
						Else
							gui_msg(se$(108),Cbmpf_red)
						EndIf
					EndIf
				Else
					If FileType("maps\"+in_input(3)+".s2")=1 And in_edmap$<>in_input(3) Then
						If gui_msgdecide(se$(107),Cbmpf_red) Then
							If save_map("maps\"+in_input(3)+".s2","map",in_input$(4)) Then
								load_recent("maps\"+in_input(3)+".s2")
								in_edmap$=in_input(3)
								m_menu=0
							Else
								gui_msg(se$(108),Cbmpf_red)
							EndIf
						EndIf
					Else
						If save_map("maps\"+in_input(3)+".s2","map",in_input$(4)) Then
							load_recent("maps\"+in_input(3)+".s2")
							in_edmap$=in_input(3)
							m_menu=0
						Else
							gui_msg(se$(108),Cbmpf_red)
						EndIf
					EndIf
				EndIf
			EndIf
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
		
		;Heightmap
		Case Cmenu_ed_heightmap
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_heightmap
			bmpf_txt(236+37,10,se$(11)+"...")
			DrawImage gfx_winbar,215,42
			
			;Map/Scale
			bmpf_txt(236,63,se$(109)+":")
			gui_input(702,63,64,4)
							
			;Import
			If gui_ibutton(742-37,103,Cicon_load,se$(110)) Then
				If gui_win_file(se$(110),set_rootdir$,"",0,0) Then
					in_input$(3)=in_win_path$+in_win_file$
					If e_terrainhm(in_input$(3),Float(in_input$(4))) Then
						editor_heightmaptobuffer()
					Else
						gui_msg(se$(119),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			;Export
			If gui_ibutton(742,103,Cicon_save,se$(111)) Then
				If gui_win_file(se$(111),set_rootdir$,"",0,1) Then
					in_input$(3)=in_win_path$+in_win_file$
					If SaveImage(in_editor_buffer,in_input$(3)) Then
					Else
						gui_msg(se$(119),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			
			;Bar
			DrawImage gfx_winbar,215,140
			
			;Bufferwin
			editor_bufferwin(236,160,500,375)
			
			;Colors
			grey=Float(in_slider(0))*2.55
			gui_colorbox(grey,grey,grey,236,542,64,32,se$(112)+": "+grey)
			gui_slider(236+64+10,550,100,0)
			
			;Alpha
			a=in_slider(10)
			bmpf_txt_c(460+32,542,se$(113)+":",Cbmpf_tiny)
			bmpf_txt_c(460+32,542+16,a+"%",Cbmpf_tiny)
			gui_slider(460+64+10,550,100,10)
			
			;Tools
			If gui_ibutton(742,160,Cicon_brush,se$(114),1,in_edtool=0) Then
				in_edtool=0
			EndIf
			If gui_ibutton(742,160+37,Cicon_spray,se$(115),1,in_edtool=1) Then
				in_edtool=1
			EndIf
			If gui_ibutton(742,160+37+37,Cicon_sun,se$(116),1,in_edtool=2) Then
				in_edtool=2
			EndIf
			If gui_ibutton(742,160+37+37+37,Cicon_moon,se$(117),1,in_edtool=3) Then
				in_edtool=3
			EndIf
			If gui_ibutton(742,160+37+37+37+37,83,se$(224),1,in_edtool=4) Then
				in_edtool=4
			EndIf
			If gui_ibutton(742,160+37+37+37+37+37+37,Cicon_zoom,se$(118),1,in_edzoom>1) Then
				in_edzoom=in_edzoom*2
				If in_edzoom>8 Then in_edzoom=1
				in_editor_bufferx=-5
				in_editor_buffery=-5
			EndIf
			bmpf_txt(742+12,160+37+37+37+37+37+37+15,in_edzoom+"x",Cbmpf_tiny)
				
			;Close
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			;Ok
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				editor_buffertoheightmap()
				m_menu=0
			EndIf
					
		
		;Colormap
		Case Cmenu_ed_colormap
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_colormap
			bmpf_txt(236+37,10,se$(12)+"...")
			DrawImage gfx_winbar,215,42
			
			;Map/Scale
			bmpf_txt(236,63,se$(120)+":")
			gui_input(702-37,63,64,4)
			If gui_ibutton(742,63,Cicon_ok,se$(121)) Then
				in_input$(4)=e_terraincmscale(in_input$(4))
			EndIf
					
			;Import
			If gui_ibutton(742-37,103,Cicon_load,se$(122)) Then
				If gui_win_file(se$(122),set_rootdir$,"",0,0) Then
					in_input$(3)=in_win_path$+in_win_file$
					If e_terraincm(in_input$(3)) Then
						editor_textobuffer(ter_tex_color)
						in_input$(4)=TextureHeight(ter_tex_color)
						grass_map()
					Else
						gui_msg(se$(119),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			;Export
			If gui_ibutton(742,103,Cicon_save,se$(123)) Then
				If gui_win_file(se$(123),set_rootdir$,"",0,1) Then
					in_input$(3)=in_win_path$+in_win_file$
					If SaveImage(in_editor_buffer,in_input$(3)) Then
					Else
						gui_msg(se$(119),Cbmpf_red)
					EndIf
				EndIf
			EndIf
			
			;Bar
			DrawImage gfx_winbar,215,140
			
			;Bufferwin
			editor_bufferwin(236,160,500,375)
			
			;Maincolor
			If gui_colorbox(in_col(0),in_col(1),in_col(2),236,542,64,32,se$(124)) Then
				in_slider(0)=Float(in_col(0))/2.55
				in_slider(1)=Float(in_col(1))/2.55
				in_slider(2)=Float(in_col(2))/2.55
				If gui_win_color() Then
					in_col(0)=in_win_color(0):in_col(1)=in_win_color(1):in_col(2)=in_win_color(2)
				EndIf
			EndIf
			;Deepcolor
			x=gui_colorbox(in_tercol(0,0),in_tercol(1,0),in_tercol(2,0),236+64+15,542,32,32,se$(125))
			If x=1 Then
				in_col(0)=in_tercol(0,0):in_col(1)=in_tercol(1,0):in_col(2)=in_tercol(2,0)
			ElseIf x=2 Then
				in_slider(0)=Float(in_tercol(0,0))/2.55
				in_slider(1)=Float(in_tercol(1,0))/2.55
				in_slider(2)=Float(in_tercol(2,0))/2.55
				If gui_win_color() Then
					e_tercol(in_win_color(0),in_win_color(1),in_win_color(2),0)
				EndIf
			EndIf
			;Basecolor
			x=gui_colorbox(in_tercol(0,1),in_tercol(1,1),in_tercol(2,1),236+64+15+37,542,32,32,se$(126))
			If x=1 Then
				in_col(0)=in_tercol(0,1):in_col(1)=in_tercol(1,1):in_col(2)=in_tercol(2,1)
			ElseIf x=2 Then
				in_slider(0)=Float(in_tercol(0,1))/2.55
				in_slider(1)=Float(in_tercol(1,1))/2.55
				in_slider(2)=Float(in_tercol(2,1))/2.55
				If gui_win_color() Then
					e_tercol(in_win_color(0),in_win_color(1),in_win_color(2),1)
				EndIf
			EndIf
			;Heightcolor
			x=gui_colorbox(in_tercol(0,2),in_tercol(1,2),in_tercol(2,2),236+64+15+37+37,542,32,32,se$(127))
			If x=1 Then
				in_col(0)=in_tercol(0,2):in_col(1)=in_tercol(1,2):in_col(2)=in_tercol(2,2)
			ElseIf x=2 Then
				in_slider(0)=Float(in_tercol(0,2))/2.55
				in_slider(1)=Float(in_tercol(1,2))/2.55
				in_slider(2)=Float(in_tercol(2,2))/2.55
				If gui_win_color() Then
					e_tercol(in_win_color(0),in_win_color(1),in_win_color(2),2)
				EndIf
			EndIf
			
			;Alpha
			a=in_slider(10)
			bmpf_txt_c(460+32,542,se$(113)+":",Cbmpf_tiny)
			bmpf_txt_c(460+32,542+16,a+"%",Cbmpf_tiny)
			gui_slider(460+64+10,550,100,10)
			
			;Tools
			If gui_ibutton(742,160,Cicon_brush,se$(114),1,in_edtool=0) Then
				in_edtool=0
			EndIf
			If gui_ibutton(742,160+37,Cicon_spray,se$(115),1,in_edtool=1) Then
				in_edtool=1
			EndIf
			If gui_ibutton(742,160+37+37,Cicon_sun,se$(116),1,in_edtool=2) Then
				in_edtool=2
			EndIf
			If gui_ibutton(742,160+37+37+37,Cicon_moon,se$(117),1,in_edtool=3) Then
				in_edtool=3
			EndIf
			If gui_ibutton(742,160+37+37+37+37,83,se$(224),1,in_edtool=4) Then
				in_edtool=4
			EndIf
			If gui_ibutton(742,160+37+37+37+37+37+37,Cicon_zoom,se$(118),1,in_edzoom>1) Then
				in_edzoom=in_edzoom*2
				If in_edzoom>8 Then in_edzoom=1
				in_editor_bufferx=-5
				in_editor_buffery=-5
			EndIf
			bmpf_txt(742+12,160+37+37+37+37+37+37+15,in_edzoom+"x",Cbmpf_tiny)
			
			
			;Update
			If gui_ibutton(742-37-47,542,Cicon_update,se$(128)) Then
				editor_gencolormap(0)
				editor_textobuffer(ter_tex_color)
				p_kill2d()
			EndIf
			;Close
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			;Ok
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				editor_buffertotex(ter_tex_color)
				m_menu=0
			EndIf
			
		
		;Grassmap
		Case Cmenu_ed_grassmap
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_grassmap
			bmpf_txt(236+37,10,se$(13)+"...")
			DrawImage gfx_winbar,215,42
					
			;Bar
			DrawImage gfx_winbar,215,140
			
			;Bufferwin
			editor_bufferwin(236,160,500,375)
			
			;Grassmode
			If gui_ibutton(236,542,Cicon_grassmap,se$(129),1,in_edgrassmode=1) Then
				in_edgrassmode=1-in_edgrassmode
			EndIf
			If in_edgrassmode=1 Then
				bmpf_txt(236+37,542+5,se$(129)+": "+se$(130),Cbmpf_green)
			Else
				bmpf_txt(236+37,542+5,se$(129)+": "+se$(131),Cbmpf_red)
			EndIf
			
			;Tools
			If gui_ibutton(742,160,Cicon_brush,se$(114),1,in_edtool=0) Then
				in_edtool=0
			EndIf
			If gui_ibutton(742,160+37,Cicon_spray,se$(115),1,in_edtool=1) Then
				in_edtool=1
			EndIf
			If gui_ibutton(742,160+37+37+37+37+37+37,Cicon_zoom,se$(118),1,in_edzoom>1) Then
				in_edzoom=in_edzoom*2
				If in_edzoom>8 Then in_edzoom=1
				in_editor_bufferx=-5
				in_editor_buffery=-5
			EndIf
			bmpf_txt(742+12,160+37+37+37+37+37+37+15,in_edzoom+"x",Cbmpf_tiny)
			
			;Update
			If gui_ibutton(742-47,542,Cicon_update,se$(132)) Then
				grass_heightmap()
				editor_grassmaptobuffer()
				p_kill2d()
			EndIf
			;Ok
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				m_menu=0
			EndIf
			
		;Object Properties
		Case Cmenu_ed_objects
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0:in_opt(0)=Cclass_object:editor_setmode(Cclass_object)
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(133))
			DrawImage gfx_winbar,215,42
			
			;ID
			bmpf_txt(236,63,se$(137)+":")
			gui_input(450,63,64,3)
			in_input(3)=Int(in_input(3))
			If Len(in_input(3))>6 Then in_input(3)=Left(in_input(3),6)
			If in_inputfocus=3 Then editor_setmenu(Cmenu_ed_objects,in_input(3))
			
			;Exists?
			i=0
			For Tobject.Tobject=Each Tobject
				If Tobject\id=Int(in_input(3)) Then
					i=1
					
					bmpf_txt(236,93,se$(138)+":")
					typ=Tobject\typ
					DrawBlock gfx_if_itemback,450,93
					DrawImage Dobject_iconh(typ),450,93
					bmpf_txt(495,93,Dobject_name$(typ)+" ("+typ+")")
					h=Tobject\h
					
					bmpf_txt(236,140,se$(139)+":")
					gui_input(450,140,128,4)
					
					bmpf_txt(236,165,se$(140)+":")
					gui_input(450,165,128,5)
					bmpf_txt(590,165,"/")
					gui_input(600,165,128,6)
					x=gui_healthbar#(450,190,Float(in_input(5)),Float(in_input(6)))
					If x<>-1 Then in_input(5)=x
					If Float(in_input(5))>Float(in_input(6)) Then in_input(5)=Float(in_input(6))
					
					;Description
					scrolldown=bmpf_txt_rect(236,210,500,160,Dobject_descr(typ),2,in_scr_descr)
					If gui_ibutton(742,210,Cicon_up,sm$(29),in_scr_descr>0) Then
						in_scr_descr=in_scr_descr-1
					EndIf
					If gui_ibutton(742,370-32,Cicon_down,sm$(30),scrolldown=0) Then
						in_scr_descr=in_scr_descr+1
					EndIf
					
					editor_childs(Cclass_object,Int(in_input(3)))
					
					TCobject.Tobject=Tobject
					
					Exit						
				EndIf
			Next
				
			If Not i Then bmpf_txt(236,93,"N/A",3)
			
			;Jump to Parent
			If gui_ibutton(742-37,5,Cicon_cam,se$(202),i) Then
				editor_jumpto(Cclass_object,in_input(3))
			EndIf
			
			;Spawn
			If i=1 Then
				If Dobject_spawn$(TCobject\typ)<>"" Then
					If gui_ibutton(236+37,542,78,"Spawn") Then
						game_cd_object()
					EndIf
				Else
					gui_ibutton(236+37,542,78,"Spawn",0)
				EndIf
			Else
				gui_ibutton(236+37,542,78,"Spawn",0)
			EndIf
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141),i) Then
				free_object(Int(in_input(3)))
				m_menu=0:in_opt(0)=Cclass_object:editor_setmode(Cclass_object)
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If i Then
					;Rotation
					RotateEntity Tobject\h,EntityPitch(Tobject\h),Float(in_input(4)),EntityRoll(Tobject\h)
					;Health
					Tobject\health#=Float(in_input(5))
					Tobject\health_max#=Float(in_input(6))
					;Script
					editor_savescript(Cclass_object,Int(in_input(3)))
				EndIf
				m_menu=0:in_opt(0)=Cclass_object:editor_setmode(Cclass_object)
			EndIf
			
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0:in_opt(0)=Cclass_object:editor_setmode(Cclass_object)
			
			;Defvars
			defvars=0
			If i Then
				For Tdefvar.Tdefvar=Each Tdefvar
					If Tdefvar\class=Cclass_object
						If Tdefvar\typ=typ Then
							defvars=1
							Exit
						EndIf
					EndIf
				Next
			EndIf
			If gui_ibutton(742-(37*3),542,Cicon_options,se$(223),defvars) Then
				gui_win_defvars(Cclass_object,typ,Int(in_input(3)))
			EndIf
			
			;Info
			If gui_ibutton(742-(37*4),542,Cicon_info,se$(144),Len(Dobject_descr(typ))>0) Then
				gui_win_text(Dobject_descr(typ))
			EndIf
		
		;Unit Properties
		Case Cmenu_ed_units
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0:in_opt(0)=Cclass_unit:editor_setmode(Cclass_unit)
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(134))
			DrawImage gfx_winbar,215,42
			
			;ID
			bmpf_txt(236,63,se$(137)+":")
			gui_input(450,63,64,3)
			in_input(3)=Int(in_input(3))
			If Len(in_input(3))>6 Then in_input(3)=Left(in_input(3),6)
			If in_inputfocus=3 Then editor_setmenu(Cmenu_ed_units,in_input(3))
			
			;Exists?
			i=0
			For Tunit.Tunit=Each Tunit
				If Tunit\id=Int(in_input(3)) Then
					i=1
					
					bmpf_txt(236,93,se$(138)+":")
					typ=Tunit\typ
					DrawBlock gfx_if_itemback,450,93
					DrawImage Dunit_iconh(typ),450,93
					bmpf_txt(495,93,Dunit_name$(typ)+" ("+typ+")")
					h=Tunit\h
					
					bmpf_txt(236,140,se$(139)+":")
					gui_input(450,140,128,4)
					
					bmpf_txt(236,165,se$(140)+":")
					gui_input(450,165,128,5)
					bmpf_txt(590,165,"/")
					gui_input(600,165,128,6)
					x=gui_healthbar#(450,190,Float(in_input(5)),Float(in_input(6)))
					If x<>-1 Then in_input(5)=x
					If Float(in_input(5))>Float(in_input(6)) Then in_input(5)=Float(in_input(6))
					
					;Description
					scrolldown=bmpf_txt_rect(236,210,500,160,Dunit_descr(typ),2,in_scr_descr)
					If gui_ibutton(742,210,Cicon_up,sm$(29),in_scr_descr>0) Then
						in_scr_descr=in_scr_descr-1
					EndIf
					If gui_ibutton(742,370-32,Cicon_down,sm$(30),scrolldown=0) Then
						in_scr_descr=in_scr_descr+1
					EndIf
					
					editor_childs(Cclass_unit,Int(in_input(3)))
					
					Exit					
				EndIf
			Next
				
			If Not i Then bmpf_txt(236,93,"N/A",3)
			
			;Jump to Parent
			If gui_ibutton(742-37,5,Cicon_cam,se$(202),i) Then
				editor_jumpto(Cclass_unit,in_input(3))
			EndIf
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141),i) Then
				free_unit(Int(in_input(3)))
				m_menu=0:in_opt(0)=Cclass_unit:editor_setmode(Cclass_unit)
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If i Then
					;Rotation
					RotateEntity Tunit\h,EntityPitch(Tunit\h),Float(in_input(4)),EntityRoll(Tunit\h)
					;Health
					Tunit\health#=Float(in_input(5))
					Tunit\health_max#=Float(in_input(6))
					;Script
					editor_savescript(Cclass_unit,Int(in_input(3)))
				EndIf
				m_menu=0:in_opt(0)=Cclass_unit:editor_setmode(Cclass_unit)
			EndIf
			
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0:in_opt(0)=Cclass_unit:editor_setmode(Cclass_unit)			
			
			;Defvars
			defvars=0
			If i Then
				For Tdefvar.Tdefvar=Each Tdefvar
					If Tdefvar\class=Cclass_unit
						If Tdefvar\typ=typ Then
							defvars=1
							Exit
						EndIf
					EndIf
				Next
			EndIf
			If gui_ibutton(742-(37*3),542,Cicon_options,se$(223),defvars) Then
				gui_win_defvars(Cclass_unit,typ,Int(in_input(3)))
			EndIf
			
			;Info
			If gui_ibutton(742-(37*4),542,Cicon_info,se$(144),Len(Dunit_descr(typ))>0) Then
				gui_win_text(Dunit_descr(typ))
			EndIf
			
			
		;Item Properties
		Case Cmenu_ed_items
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0:in_opt(0)=3:editor_picking(Cclass_item)
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(135))
			DrawImage gfx_winbar,215,42
			
			;ID
			bmpf_txt(236,63,se$(137)+":")
			gui_input(450,63,64,3)
			in_input(3)=Int(in_input(3))
			If Len(in_input(3))>6 Then in_input(3)=Left(in_input(3),6)
			If in_inputfocus=3 Then editor_setmenu(Cmenu_ed_items,in_input(3))
			
			;Exists?
			i=0
			For Titem.Titem=Each Titem
				If Titem\id=Int(in_input(3)) Then
					i=1
					
					;Item
					bmpf_txt(236,93,se$(138)+":")
					typ=Titem\typ
					DrawBlock gfx_if_itemback,450,93
					DrawImage Ditem_iconh(typ),450,93
					bmpf_txt(495,93,Ditem_name$(typ)+" ("+typ+")")
					h=Titem\h
					If Titem\parent_class<>0 Then
						If Titem\parent_mode=Cpm_in Then
							bmpf_txt(495,113,"("+se$(142)+")")
						EndIf
					EndIf
					
					;Rotation
					bmpf_txt(236,140,se$(139)+":")
					gui_input(450,140,128,4)
					
					;Health
					bmpf_txt(236,165,se$(140)+":")
					gui_input(450,165,128,5)
					bmpf_txt(590,165,"/ "+Ditem_health#(typ))
					x=gui_healthbar#(450,190,Float(in_input(5)),Ditem_health#(typ))
					If x<>-1 Then in_input(5)=x
					
					;Count
					bmpf_txt(236,205,se$(143)+":")
					gui_input(450,205,128,6)
					
					;Attach to Objects
					x=1
					If Titem\parent_class<>0 Then
						If Titem\parent_mode=Cpm_in Then x=0
					EndIf
					If x=1 Then
						bmpf_txt(236,235,se$(219)+":")
					Else
						bmpf_txt(236,235,se$(219)+":",Cbmpf_dark)
					EndIf
					gui_input(450,235,128,7,x)
					
					;Description
					scrolldown=bmpf_txt_rect(236,260,500,120,Ditem_descr(typ),2,in_scr_descr)
					If gui_ibutton(742,260,Cicon_up,sm$(29),in_scr_descr>0) Then
						in_scr_descr=in_scr_descr-1
					EndIf
					If gui_ibutton(742,380-32,Cicon_down,sm$(30),scrolldown=0) Then
						in_scr_descr=in_scr_descr+1
					EndIf
					
					;Childs
					editor_childs(Cclass_item,Int(in_input(3)),1,0,1)
					
					Exit
				EndIf
			Next
				
			If Not i Then bmpf_txt(236,93,"N/A",3)
			
			;Jump to Parent
			If gui_ibutton(742-37,5,Cicon_cam,se$(202),i) Then
				If Titem\parent_class=0 Then
					editor_jumpto(Cclass_item,in_input(3))
				Else
					editor_jumpto(Titem\parent_class,Titem\parent_id)
				EndIf
			EndIf
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141),i) Then
				free_item(Int(in_input(3)))
				m_menu=0:in_opt(0)=3:editor_picking(Cclass_item)
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If i Then
					;Rotation
					RotateEntity Titem\h,EntityPitch(Titem\h),Float(in_input(4)),EntityRoll(Titem\h)
					;Health
					Titem\health#=Float(in_input(5))
					;Count
					If Int(in_input(6))<1 Then in_input(6)=1
					Titem\count=Int(in_input(6))
					;Save Object
					x=1
					If Titem\parent_class<>0 Then
						If Titem\parent_mode=Cpm_in Then
							x=0
						EndIf
					EndIf
					If x=1 Then
						If in_input(7)<>0 Then
							Titem\parent_class=Cclass_object
							Titem\parent_id=Int(in_input(7))
							Titem\parent_mode=Cpm_out
							RotateEntity Titem\h,-90,EntityYaw(Titem\h),0
						Else
							Titem\parent_class=0
							Titem\parent_id=0
							Titem\parent_mode=0
							RotateEntity Titem\h,0,EntityYaw(Titem\h),0
						EndIf
					EndIf
					;Script
					editor_savescript(Cclass_item,Int(in_input(3)))
				EndIf
				m_menu=0:in_opt(0)=3:editor_picking(Cclass_item)
			EndIf
			
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0:in_opt(0)=3:editor_picking(Cclass_item)
			
			;Defvars
			defvars=0
			If i Then
				For Tdefvar.Tdefvar=Each Tdefvar
					If Tdefvar\class=Cclass_item
						If Tdefvar\typ=typ Then
							defvars=1
							Exit
						EndIf
					EndIf
				Next
			EndIf
			If gui_ibutton(742-(37*3),542,Cicon_options,se$(223),defvars) Then
				gui_win_defvars(Cclass_item,typ,Int(in_input(3)))
			EndIf
			
			;Info
			If gui_ibutton(742-(37*4),542,Cicon_info,se$(144),Len(Ditem_descr(typ))>0) Then
				gui_win_text(Ditem_descr(typ))
			EndIf
			
		;Info Properties
		Case Cmenu_ed_infos
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(136))
			DrawImage gfx_winbar,215,42
			
			;ID
			bmpf_txt(236,63,se$(137)+":")
			gui_input(450,63,64,3)
			in_input(3)=Int(in_input(3))
			If Len(in_input(3))>6 Then in_input(3)=Left(in_input(3),6)
			If in_inputfocus=3 Then editor_setmenu(Cmenu_ed_infos,in_input(3))
						
			;Exists?
			i=0
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=Int(in_input(3)) Then
					i=1
					
					bmpf_txt(236,93,se$(138)+":")
					typ=Tinfo\typ
					DrawBlock gfx_if_itemback,450,93
					DrawImage gfx_icons,450+4,93+4,Dinfo_frame(typ)
					bmpf_txt(495,93,Dinfo_name$(typ)+" ("+typ+")")
					h=Tinfo\h
					
					;Infostuff
					Select typ
						;Spawnpoint
						Case 1
							bmpf_txt(236,140,se$(144)+":")
							bmpf_txt(450,140,se$(145))
							
						;Sprite
						Case 5
							;Image
							bmpf_txt(236,140,se$(207)+":")
							gui_input(450,140,256,4)

							;Browse
							If gui_ibutton(742,140,Cicon_dir,se$(75)) Then
								If gui_win_file(se$(2)+"...",set_rootdir$+"mods\"+set_moddir$,"",0) Then
									path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$,"")
									If Left(path$,1)="\" Then path$=Mid(path$,2,-1)
									in_input$(4)=path$+in_win_file$
								EndIf
							EndIf
							
							;Size
							bmpf_txt(236,170,se$(30)+":")
							gui_input(450,170,64,5)
							bmpf_txt(450+64+5,170,"x")
							gui_input(450+64+15,170,64,6)
							
							;Color
							bmpf_txt(236,200,se$(194)+":")
							If gui_colorbox(in_scol(0),in_scol(1),in_scol(2),450,200,64,32) Then
								in_slider(0)=Float(in_scol(0))/2.55
								in_slider(1)=Float(in_scol(1))/2.55
								in_slider(2)=Float(in_scol(2))/2.55
								If gui_win_color() Then
									in_scol(0)=in_win_color(0)
									in_scol(1)=in_win_color(1)
									in_scol(2)=in_win_color(2)
								EndIf
							EndIf
							
							;Alpha
							bmpf_txt(236,240,se$(196)+":")
							gui_slider(450,240,100,0)
							
							;Blend
							bmpf_txt(236,270,se$(208)+":")
							gui_check(450,270,se$(208),0)
							
							;Fix
							bmpf_txt(236,300,se$(209)+":")
							gui_check(450,300,se$(210),1)
							
						
						;Trigger Range
						Case 10
							bmpf_txt(236,140,se$(146)+":")
							If gui_opt(450,140,se$(147),0,3) Then in_input$(4)=0
							If gui_opt(450,160,se$(148),Cclass_object,3) Then in_input$(4)=0
							If gui_opt(450,180,se$(149),Cclass_unit,3) Then in_input$(4)=0
							If gui_opt(450,200,se$(150),Cclass_item,3) Then in_input$(4)=0
							If gui_opt(450,220,se$(151),Cclass_state,3) Then in_input$(4)=0
							
							If in_opt(3)>0 Then
								bmpf_txt(236,240,se$(138)+":")
							Else
								bmpf_txt(236,240,se$(138)+":",Cbmpf_dark)
							EndIf
							DrawBlock gfx_if_itemback,450,240
							gui_classselbox(450,240,in_opt(3),4)
							
							bmpf_txt(236,287,se$(143)+":")
							gui_operator(450,287,7)
							gui_input(636,287,64,5)
							
							bmpf_txt(236,312,se$(152)+":")
							gui_input(450,312,64,6)
							
							bmpf_txt(236,337,se$(153)+":")
							gui_check(450,337,se$(154),0)
							
						;Trigger Time
						Case 11
							bmpf_txt(236,140,se$(146)+":")
							gui_opt(450,140,se$(155),0,3)
							gui_opt(450,160,se$(156),1,3)
							gui_opt(450,180,se$(157),2,3)
							gui_opt(450,200,se$(158),3,3)
							;gui_opt(450,220,se$(159),4,3)
							
							Select in_opt(3)
								Case 0
									bmpf_txt(236,240,se$(160)+":")
									gui_input(450,240,64,4)
								Case 1
									bmpf_txt(236,240,se$(161)+":")
									gui_input(450,240,64,4)
								Case 2
									bmpf_txt(236,240,se$(162)+":")
									gui_input(450,240,64,4)
									bmpf_txt(236,265,se$(163)+":")
									gui_input(450,265,64,5)
									bmpf_txt(236,290,se$(164)+":")
									gui_input(450,290,64,6)
								Case 3
									bmpf_txt(236,250,se$(163)+":")
									gui_input(450,250,64,4)
									bmpf_txt(236,275,se$(164)+":")
									gui_input(450,275,64,5)
							End Select
							
							bmpf_txt(236,315,se$(153)+":")
							gui_check(450,315,se$(154),0)
							
						;Trigger Item Count
						Case 12
							bmpf_txt(236,140,se$(146)+":")
							gui_opt(450,140,se$(147),0,3)
							gui_opt(450,160,se$(165),Cclass_object,3)
							gui_opt(450,180,se$(166),Cclass_unit,3)
							gui_opt(450,200,se$(167),Cclass_info,3)
							
							If in_opt(3)>0 Then
								bmpf_txt(236,220,se$(137)+":")
							Else
								bmpf_txt(236,220,se$(137)+":",Cbmpf_dark)
							EndIf
							gui_input(450,220,64,4,in_opt(3)>0)
							
							bmpf_txt(236,245,se$(168)+":")
							DrawBlock gfx_if_itemback,450,245
							gui_classselbox(450,245,Cclass_item,5)
							
							bmpf_txt(236,292,se$(143)+":")
							gui_operator(450,292,7)
							gui_input(636,292,64,6)
							
							bmpf_txt(236,317,se$(153)+":")
							gui_check(450,317,se$(154),0)
						
						;Map Indication
						Case 36
							bmpf_txt(236,140,se$(190)+":")
							gui_opt(450,140,"      ",0,3):DrawImage gfx_arrows,475,147,0
							gui_opt(450,160,"      ",9,3):DrawImage gfx_arrows,475,167,9
							gui_opt(450,180,"      ",10,3):DrawImage gfx_arrows,475,187,10
							gui_opt(450,200,"      ",11,3):DrawImage gfx_arrows,475,207,11
							
							bmpf_txt(236,225,se$(144)+":")
							gui_input(450,225,256,4)
							
							bmpf_txt(236,317,se$(153)+":")
							gui_check(450,317,se$(154),0)
							
						;Freshwater Area
						Case 41
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
						;Dig Area
						Case 42
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
						;Fishing Area
						Case 43
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
						;Area
						Case 44
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
						;Spawn-Control
						Case 45
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
							bmpf_txt(236,175,se$(203)+":")
							If gui_opt(450,175,se$(165),Cclass_object,3) Then
								in_opt(0)=1
								in_input$(5)=0
							EndIf
							If gui_opt(450,195,se$(166),Cclass_unit,3) Then
								in_opt(0)=2
								in_input$(5)=0
							EndIf
							If gui_opt(450,215,se$(168),Cclass_item,3) Then
								in_opt(0)=3
								in_input$(5)=0
							EndIf
							
							DrawBlock gfx_if_itemback,450,240
							img=0
							Select in_opt(3)
								Case Cclass_object img=Dobject_iconh(Int(in_input$(5)))
								Case Cclass_unit img=Dunit_iconh(Int(in_input$(5)))
								Case Cclass_item img=Ditem_iconh(Int(in_input$(5)))
							End Select
							If gui_iconbox(450,240,img,"",0)<>0 Then
								Select in_opt(3)
									Case Cclass_object
										If in_opt(0)=1 Then
											in_input$(5)=in_object_sel
										Else
											 in_opt(0)=1
										EndIf
									Case Cclass_unit
										If in_opt(0)=2 Then
											in_input$(5)=in_unit_sel
										Else
											in_opt(0)=2
										EndIf
									Case Cclass_item
										If in_opt(0)=3 Then
											in_input$(5)=in_item_sel
										Else
											in_opt(0)=3
										EndIf
								End Select
							EndIf
							
							bmpf_txt(236,295,se$(215)+":")
							gui_input(450,295,64,7)
							bmpf_txt(450+90,295,"/")
							gui_input(450+100,295,64,6)
							
							bmpf_txt(236,327,se$(214)+":")
							gui_input(450,327,64,8)
							
							bmpf_txt(236,359,se$(153)+":")
							gui_check(450,359,se$(154),0)
						
						;AI Area
						Case 46
							bmpf_txt(236,140,se$(152)+":")
							gui_input(450,140,64,4)
							
							bmpf_txt(236,170,se$(211)+":")
							gui_opt(450,170,se$(212),0,3)
							gui_opt(450,190,se$(213),1,3)
							
						;Loudspeaker
						Case 47
							;Sound
							bmpf_txt(236,140,se$(220)+":")
							gui_input(450,140,256,4)

							;Browse
							If gui_ibutton(742,140,Cicon_dir,se$(75)) Then
								If gui_win_file(se$(2)+"...",set_rootdir$+"mods\"+set_moddir$,"",0) Then
									path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$,"")
									If Left(path$,1)="\" Then path$=Mid(path$,2,-1)
									in_input$(4)=path$+in_win_file$
								EndIf
							EndIf
							
							;Radius
							bmpf_txt(236,170,se$(152)+":")
							gui_input(450,170,64,5)
							bmpf_txt(450,200,se$(221))
							
						;Previewimage
						Case 48
							;Image
							bmpf_txt(236,140,se$(207)+":")
							gui_input(450,140,256,4)

							;Browse
							If gui_ibutton(742,140,Cicon_dir,se$(75)) Then
								If gui_win_file(se$(2)+"...",set_rootdir$+"mods\"+set_moddir$,"",0) Then
									path$=Replace(in_win_path$,set_rootdir$+"mods\"+set_moddir$,"")
									If Left(path$,1)="\" Then path$=Mid(path$,2,-1)
									in_input$(4)=path$+in_win_file$
								EndIf
							EndIf
							
							
						;Default
						Default
							If Dinfo_descr(typ)="" Then
								;Nothing Special
								bmpf_txt(236,140,se$(144)+":")
								bmpf_txt(450,140,se$(169))
							Else
								;Description
								scrolldown=bmpf_txt_rect(236,140,500,230,Dinfo_descr(typ),2,in_scr_descr)
								If gui_ibutton(742,140,Cicon_up,sm$(29),in_scr_descr>0) Then
									in_scr_descr=in_scr_descr-1
								EndIf
								If gui_ibutton(742,370-32,Cicon_down,sm$(30),scrolldown=0) Then
									in_scr_descr=in_scr_descr+1
								EndIf
							EndIf
							
					End Select			
					
					;Childs
					editor_childs(Cclass_info,Int(in_input(3)),1,1,1)
					
					Exit
				EndIf
			Next
			
			If Not i Then bmpf_txt(236,93,"N/A",3)
			
			;Jump to Parent
			If gui_ibutton(742-37,5,Cicon_cam,se$(202),i) Then
				editor_jumpto(Cclass_info,in_input(3))
			EndIf
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141),i) Then
				free_info(Int(in_input(3)))
				m_menu=0:in_opt(0)=4:editor_picking(Cclass_info)
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If i=1 Then
					editor_saveinfo(Tinfo)
				EndIf
				m_menu=0:in_opt(0)=4:editor_picking(Cclass_info)
			EndIf
			
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0:in_opt(0)=4:editor_picking(Cclass_info)
				
			;Defvars
			defvars=0
			If i Then
				For Tdefvar.Tdefvar=Each Tdefvar
					If Tdefvar\class=Cclass_info
						If Tdefvar\typ=typ Then
							defvars=1
							Exit
						EndIf
					EndIf
				Next
			EndIf
			If gui_ibutton(742-(37*3),542,Cicon_options,se$(223),defvars) Then
				gui_win_defvars(Cclass_info,typ,Int(in_input(3)))
			EndIf
			
			;Info
			If gui_ibutton(742-(37*4),542,Cicon_info,se$(144),Len(Dinfo_descr(typ))>0) Then
				gui_win_text(Dinfo_descr(typ))
			EndIf
			
		;Script Editor
		Case Cmenu_ed_scripts
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(71))
			DrawImage gfx_winbar,215,42
			
			;Focus
			in_inputfocus=1337
			
			;ID
			i=0
			For Tx.Tx=Each Tx
				If Tx\parent_class=Int(in_input(3)) Then
					If Tx\parent_id=Int(in_input(4)) Then
						i=1
						Exit
					EndIf
				EndIf
			Next
			
			bmpf_txt(236,63,se$(170)+":")
			Select Int(in_input(3))
				Case 0 bmpf_txt(450,63,se$(171))
				Case -1 bmpf_txt(450,63,se$(226))
				Case Cclass_object bmpf_txt(450,63, ss$(se$(172),in_input(4)) )
				Case Cclass_unit bmpf_txt(450,63, ss$(se$(173),in_input(4)) )
				Case Cclass_item bmpf_txt(450,63, ss$(se$(174),in_input(4)) )
				Case Cclass_info bmpf_txt(450,63, ss$(se$(175),in_input(4)) )
			End Select
				
			gui_tb(236,85,538,29,1,1)
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok, sm$(5)) Then
				editor_savescript(Int(in_input(3)),Int(in_input(4)))
				editor_setmenu(in_prevmenu,Int(in_input(4)))
			EndIf
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then
				editor_setmenu(in_prevmenu,Int(in_input(4)))
			EndIf
			
			;Jump to Parent
			If gui_ibutton(742-37,5,Cicon_cam,se$(202)) Then
				editor_jumpto(in_input(3),in_input(4))
			EndIf
			
			;Help
			x=0
			If in_sh=1 Then
				If in_sh_cmd$<>0 Then
					x=1
				EndIf
			EndIf
			If gui_ibutton(236+37,542,Cicon_zoom,ss(se$(222),in_sh_cmd$),x,0,59) Then
				ExecFile("http://www.stranded.unrealsoftware.de/s2_commands.php?cmd="+in_sh_cmd$+"#cmd")
			EndIf
			
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141)) Then
				If gui_msgdecide(se$(176),Cbmpf_red,sm$(2),sm$(1))
					If Int(in_input(3))=0 Then
						map_briefing$=""
					ElseIf Int(in_input(3))=-1 Then
						ed_setscript$=""
					Else
						If i Then Delete Tx
					EndIf
					editor_setmenu(in_prevmenu,Int(in_input(4)))
				EndIf
			EndIf
			
			;List
			;If gui_ibutton(236+37,542,Cicon_list,se$(201)) Then
			;	editor_setmenu(Cmenu_ed_list)
			;EndIf
			
			;Load
			If gui_ibutton(236+37+37,542,Cicon_load,se$(177)) Then
				If gui_win_file(se$(177),set_rootdir$,"",0,0) Then
					gui_tb_loadfile(in_win_path$+in_win_file$)
				EndIf
			EndIf
			;Export
			If gui_ibutton(236+37+37+37,542,Cicon_save,se$(178)) Then
				If gui_win_file(se$(178),set_rootdir$,"",0,1) Then
					gui_tb_savefile(in_win_path$+in_win_file$)
				EndIf
			EndIf
			
			;Add Command
			gui_tb_scripthelper_list()
			If gui_ibutton(236+37+37+37+5+37,542,Cicon_inc,se$(179)) Then
				gui_win_command()
			EndIf 


		;State Properties
		Case Cmenu_ed_states
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0:in_opt(0)=3
			DrawImage gfx_icons,236,5,Cicon_options
			bmpf_txt(236+37,10,se$(192))
			DrawImage gfx_winbar,215,42
						
			;Exists?
			i=0
			For Tstate.Tstate=Each Tstate
				If Tstate\typ=Int(in_input(3)) Then
					If Tstate\parent_class=Int(in_input(4)) Then
						If Tstate\parent_id=Int(in_input(5)) Then
							i=1
							
							;Jump to Parent
							If gui_ibutton(742-37,5,Cicon_cam,se$(202)) Then
								editor_jumpto(Tstate\parent_class,Tstate\parent_id)
							EndIf
							
							bmpf_txt(236,63,se$(138)+":")
							typ=Tstate\typ
							DrawBlock gfx_if_itemback,450,63
							DrawImage gfx_states,450+10,63+10,(Dstate_frame(Tstate\typ))
							DrawImage gfx_state,450+10,63+10,in_statefxf
							
							bmpf_txt(495,63,Dstate_name$(typ)+" ("+typ+")")
							
							;Power/Damage (value_f#)
							bmpf_txt(236,140,se$(193)+":")
							gui_input(450,140,128,6)
							
							;Color
							If Tstate\typ=Cstate_flare Or Tstate\typ=Cstate_light Or Tstate\typ=Cstate_particles Then
								bmpf_txt(236,170,se$(194)+":")
								If gui_colorbox(in_scol(0),in_scol(1),in_scol(2),450,170,64,32) Then
									in_slider(0)=Float(in_scol(0))/2.55
									in_slider(1)=Float(in_scol(1))/2.55
									in_slider(2)=Float(in_scol(2))/2.55
									If gui_win_color() Then
										in_scol(0)=in_win_color(0)
										in_scol(1)=in_win_color(1)
										in_scol(2)=in_win_color(2)
									EndIf
								EndIf
							EndIf
							
							;Size
							If Tstate\typ=Cstate_flare Or Tstate\typ=Cstate_light Then
								bmpf_txt(236,210,se$(195)+":")
								gui_input(450,210,64,7)
							EndIf
																	
							Exit
						EndIf
					EndIf
				EndIf
			Next
				
			If Not i Then bmpf_txt(236,93,"N/A",3)
			
			;Delete
			If gui_ibutton(236,542,Cicon_x,se$(141),i) Then
				free_state(Int(in_input(3)),Int(in_input(4)),Int(in_input(5)))
				m_menu=0
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then
				If i Then
					;Value
					Tstate\value_f#=Float(in_input$(6))
					;Color
					If Tstate\typ=Cstate_flare Or Tstate\typ=Cstate_light Or Tstate\typ=Cstate_particles Then
						Tstate\value_s$=in_scol(0)+","+in_scol(1)+","+in_scol(2)
					EndIf
					;Size
					If Tstate\typ=Cstate_flare Or Tstate\typ=Cstate_light Then
						Tstate\value=Int(in_input$(7))
					EndIf
					;Look
					TCstate.Tstate=Tstate
					look_state()
				EndIf
				m_menu=0
			EndIf
			
			;Cancel
			If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then m_menu=0
			


		;List
		Case Cmenu_ed_list
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_icons,236,5,Cicon_list
			bmpf_txt(236+37,10,se$(201))
			DrawImage gfx_winbar,215,42
			
			;Groups
			If gui_opt(236,63,se$(7),Cclass_object,3) Then in_scr_scr=0
			If gui_opt(236,63+20,se$(8),Cclass_unit,3) Then in_scr_scr=0
			If gui_opt(236+200,63,se$(9),Cclass_item,3) Then in_scr_scr=0
			If gui_opt(236+200,63+20,se$(10),Cclass_info,3) Then in_scr_scr=0
			If gui_opt(236+400,63,se$(80),Cclass_state,3) Then in_scr_scr=0
			If gui_opt(236+400,63+20,se$(81),100,3) Then in_scr_scr=0
			
			DrawImage gfx_winbar,215,63+40
			
			y=103+16+5
			scrolldown=0
			
			;List
			Select in_opt(3)
				
				
				;Objects
				Case Cclass_object
					i=0
					For Tobject.Tobject=Each Tobject
						i=i+1
						If in_scr_scr<i Then
							;Over?
							over=0
							If in_mx>=236 Then
								If in_mx<=738 Then
									If in_my>=y Then
										If in_my<y+45
											over=1
										EndIf
									EndIf
								EndIf
							EndIf
							;Output
							typ=Tobject\typ
							DrawBlock gfx_if_itemback,236,y
							DrawImage Dobject_iconh(typ),236,y
							bmpf_txt(236+45,y,"#"+Tobject\id+" "+Dobject_name$(typ)+" ("+typ+")",over)
							;Action
							If over Then
								If in_mrelease(1) Then
									editor_setmenu(Cmenu_ed_objects,Tobject\id)
								EndIf
							EndIf
							;Pos
							y=y+45
							If y>550 Then
								scrolldown=1
								Exit
							EndIf
						EndIf
					Next
					
					c=0
					For Tobject.Tobject=Each Tobject
						c=c+1
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)					
					
				;Units
				Case Cclass_unit
					i=0
					For Tunit.Tunit=Each Tunit
						i=i+1
						If in_scr_scr<i Then
							;Over?
							over=0
							If in_mx>=236 Then
								If in_mx<=738 Then
									If in_my>=y Then
										If in_my<y+45
											over=1
										EndIf
									EndIf
								EndIf
							EndIf
							;Output
							typ=Tunit\typ
							DrawBlock gfx_if_itemback,236,y
							DrawImage Dunit_iconh(typ),236,y
							bmpf_txt(236+45,y,"#"+Tunit\id+" "+Dunit_name$(typ)+" ("+typ+")",over)
							;Action
							If over Then
								If in_mrelease(1) Then
									editor_setmenu(Cmenu_ed_units,Tunit\id)
								EndIf
							EndIf
							;Pos
							y=y+45
							If y>550 Then
								scrolldown=1
								Exit
							EndIf
						EndIf
					Next
					
					c=0
					For Tunit.Tunit=Each Tunit
						c=c+1
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)
				
				;Items
				Case Cclass_item	
					i=0
					For Titem.Titem=Each Titem
						i=i+1
						If in_scr_scr<i Then
							;Over?
							over=0
							If in_mx>=236 Then
								If in_mx<=738 Then
									If in_my>=y Then
										If in_my<y+45
											over=1
										EndIf
									EndIf
								EndIf
							EndIf
							;Output
							typ=Titem\typ
							DrawBlock gfx_if_itemback,236,y
							DrawImage Ditem_iconh(typ),236,y
							bmpf_txt(236+45,y,"#"+Titem\id+" "+Ditem_name$(typ)+" ("+typ+")",over)
							If Titem\parent_class=0 Then
								bmpf_txt(236+45,y+18,Titem\count+"x",Cbmpf_dark)
							Else
								bmpf_txt(236+45,y+18,Titem\count+"x ("+se$(142)+")",Cbmpf_dark)
							EndIf						
							;Action
							If over Then
								If in_mrelease(1) Then
									editor_setmenu(Cmenu_ed_items,Titem\id)
								EndIf
							EndIf
							;Pos
							y=y+45
							If y>550 Then
								scrolldown=1
								Exit
							EndIf
						EndIf
					Next
					
					c=0
					For Titem.Titem=Each Titem
						c=c+1
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)
				
				;Infos
				Case Cclass_info
					i=0
					For Tinfo.Tinfo=Each Tinfo
						i=i+1
						If in_scr_scr<i Then
							;Over?
							over=0
							If in_mx>=0 Then
								If in_mx<=738 Then
									If in_my>=y Then
										If in_my<y+45
											over=1
										EndIf
									EndIf
								EndIf
							EndIf
							;Output
							typ=Tinfo\typ
							DrawBlock gfx_if_itemback,236,y
							DrawImage gfx_icons,236+4,y+4,Dinfo_frame(typ)
							bmpf_txt(236+45,y,"#"+Tinfo\id+" "+Dinfo_name$(typ)+" ("+typ+")",over)
							;Action
							If over Then
								If in_mrelease(1) Then
									editor_setmenu(Cmenu_ed_infos,Tinfo\id)
								EndIf
							EndIf
							;Pos
							y=y+45
							If y>550 Then
								scrolldown=1
								Exit
							EndIf
						EndIf
					Next
					
					c=0
					For Tinfo.Tinfo=Each Tinfo
						c=c+1
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)
					
				;States
				Case Cclass_state
					i=0
					For Tstate.Tstate=Each Tstate
						i=i+1
						If in_scr_scr<i Then
							;Over?
							over=0
							If in_mx>=236 Then
								If in_mx<=738 Then
									If in_my>=y Then
										If in_my<y+45
											over=1
										EndIf
									EndIf
								EndIf
							EndIf
							;Output
							typ=Tstate\typ
							DrawBlock gfx_if_itemback,236,y
							DrawImage gfx_states,236+10,y+10,Dstate_frame(typ)
							DrawImage gfx_state,236+10,y+10,in_statefxf
							bmpf_txt(236+45,y,Dstate_name$(typ)+" ("+typ+")",over)
							Select Tstate\parent_class
								Case Cclass_object bmpf_txt(236+45,y+18,"@ "+se$(165)+" #"+Tstate\parent_id,Cbmpf_dark) 
								Case Cclass_unit bmpf_txt(236+45,y+18,"@ "+se$(166)+" #"+Tstate\parent_id,Cbmpf_dark)
								Case Cclass_item bmpf_txt(236+45,y+18,"@ "+se$(168)+" #"+Tstate\parent_id,Cbmpf_dark)
								Case Cclass_info bmpf_txt(236+45,y+18,"@ "+se$(167)+" #"+Tstate\parent_id,Cbmpf_dark)
								Default  bmpf_txt(236+45,y+18,"Global",Cbmpf_dark)
							End Select
							;Action
							If over Then
								If in_mrelease(1) Then
									in_input(3)=Tstate\typ
									in_input(4)=Tstate\parent_class
									in_input(5)=Tstate\parent_id
									editor_setmenu(Cmenu_ed_states)
								EndIf
							EndIf
							;Pos
							y=y+45
							If y>550 Then
								scrolldown=1
								Exit
							EndIf
						EndIf
					Next
					
					c=0
					For Tstate.Tstate=Each Tstate
						c=c+1
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)
					
				;Scripts
				Case 100
					i=0
					For Tx.Tx=Each Tx
						If Tx\mode=0 Then
							i=i+1
							If in_scr_scr<i Then
								;Over?
								over=0
								If in_mx>=236 Then
									If in_mx<=738 Then
										If in_my>=y Then
											If in_my<y+45
												over=1
											EndIf
										EndIf
									EndIf
								EndIf
								;Output
								DrawBlock gfx_if_itemback,236,y
								bmpf_txt(236+45,y,"Script",over)
								Select Tx\parent_class
									Case Cclass_object
										bmpf_txt(236+45,y+18,"@ "+se$(165)+" #"+Tx\parent_id,Cbmpf_dark) 
										If con_object(Tx\parent_id) Then
											DrawImage Dobject_iconh(TCobject\typ),236,y
										EndIf
									Case Cclass_unit
										bmpf_txt(236+45,y+18,"@ "+se$(166)+" #"+Tx\parent_id,Cbmpf_dark)
										If con_unit(Tx\parent_id) Then
											DrawImage Dunit_iconh(TCunit\typ),236,y
										EndIf
									Case Cclass_item
										bmpf_txt(236+45,y+18,"@ "+se$(168)+" #"+Tx\parent_id,Cbmpf_dark)
										If con_item(Tx\parent_id) Then
											DrawImage Ditem_iconh(TCitem\typ),236,y
										EndIf
									Case Cclass_info
										bmpf_txt(236+45,y+18,"@ "+se$(167)+" #"+Tx\parent_id,Cbmpf_dark)
										If con_info(Tx\parent_id) Then
											DrawImage gfx_icons,236+4,y+4,Dinfo_frame(TCinfo\typ)
										EndIf
									Default
										bmpf_txt(236+45,y+18,"Global",Cbmpf_dark)
								End Select
								DrawImage gfx_icons,236,y,Cicon_script
								;Action
								If over Then
									If in_mrelease(1) Then
										in_input(3)=Tx\parent_class
										in_input(4)=Tx\parent_id
										editor_setmenu(Cmenu_ed_scripts)
										
									EndIf
								EndIf
								;Pos
								y=y+45
								If y>550 Then
									scrolldown=1
									Exit
								EndIf
							EndIf
						EndIf
					Next
					
					c=0
					For Tx.Tx=Each Tx
						If Tx\mode=0 Then
							c=c+1
						EndIf
					Next
					in_scr_scr=gui_scrollbar(742,161,500-161,c,in_scr_scr,10)
			
			
			End Select
			
			;Scroll
			If gui_ibutton(742,124,Cicon_up,sm$(29),in_scr_scr>0) Then
				in_scr_scr=in_scr_scr-1
			EndIf
			If gui_ibutton(742,542-37,Cicon_down,sm$(30),scrolldown) Then
				in_scr_scr=in_scr_scr+1
			EndIf
			
			;Okay
			If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then m_menu=0
			
		;Map
		Case Cmenu_ed_map
			DrawImage gfx_win,215,0
			If gui_ibutton(742,5,Cicon_x,sm$(15),1,0,1) Then m_menu=0
			DrawImage gfx_winbar,215,42
				
			if_map()
			

	End Select
	
End Function


;############################################ EDITOR FILLMENU

Function editor_setmenu(menu,param=-1)
	in_prevmenu=m_menu
	m_menu=menu
	in_scr_scr=0
	in_scr_scr2=0
	in_scr_scr3=0
	in_scr_descr=0
	Select menu
		;### MAIN
	
		;New
		Case Cmenu_ed_new
			in_opt(3)=0
			in_opt(4)=0
			in_opt(5)=0
			in_check(5)=1
			in_check(6)=1
			in_check(7)=1
			in_check(8)=1
		;Load
		Case Cmenu_ed_load
			load_recent()
			in_input(3)=""
			in_input(4)=""
			in_opt(3)=0
			If in_edmap$<>"" Then in_input(3)=in_edmap$
		;Save
		Case Cmenu_ed_save
			load_recent()
			in_input(3)=""
			in_input(4)=""
			in_opt(3)=0
			If in_edmap$<>"" Then in_input(3)=in_edmap$
			
		;### TERRAIN
		
		;Heightmap
		Case Cmenu_ed_heightmap
			editor_heightmaptobuffer()
			in_editor_bufferx=-5
			in_editor_buffery=-5
			in_input(3)=""
			in_input(4)=1
			in_slider(0)=50
			in_slider(10)=100
			in_edtool=0
		;Colormap
		Case Cmenu_ed_colormap
			editor_textobuffer(ter_tex_color)
			in_editor_bufferx=-5
			in_editor_buffery=-5
			in_input(3)=""
			in_input(4)=TextureHeight(ter_tex_color)
			in_edtool=0
			in_slider(10)=100
		;Grassmap
		Case Cmenu_ed_grassmap
			editor_grassmaptobuffer()
			in_editor_bufferx=-5
			in_editor_buffery=-5
			in_edtool=0
		;Random
		Case Cmenu_ed_random
			in_input(3)=(ter_size*ter_size)/4
			in_check(5)=1
			in_check(6)=0
			in_check(7)=0
			in_opt(5)=1
			in_scr_scr=0
		
		;### PROPERTIES
		Case Cmenu_ed_settings
			;Time
			in_input$(3)=map_day+":"+map_hour+":"+map_minute
			in_check(5)=map_freezetime
			;Skybox
			in_input$(4)=map_skybox$
			;Multiplayer
			in_check(6)=map_multiplayer
			;Climate
			in_opt(3)=map_climate
			;Background Music
			in_input$(5)=map_music$
			;Fog
			in_check(7)=map_fog(3)
			;Briefing/Global Scripts
			gui_tb_loadstring(map_briefing$,"Ś")
			
		;Object Properties
		Case Cmenu_ed_objects
			If param<>-1 Then in_input(3)=param
			If con_object(Int(in_input(3))) Then
				in_input(4)=EntityYaw(TCobject\h)
				in_input(5)=TCobject\health#
				in_input(6)=TCobject\health_max#
				gui_tb_loadstring("","")
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_object Then
						If Tx\parent_id=Int(in_input(3)) Then
							If Tx\mode=0 Then
								gui_tb_loadstring(Tx\value$,"Ś")
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		;Unit Properties
		Case Cmenu_ed_units
			If param<>-1 Then in_input(3)=param
			If con_unit(Int(in_input(3))) Then
				in_input(4)=EntityYaw(TCunit\h)
				in_input(5)=TCunit\health#
				in_input(6)=TCunit\health_max#
				gui_tb_loadstring("","")
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_unit Then
						If Tx\parent_id=Int(in_input(3)) Then
							If Tx\mode=0 Then
								gui_tb_loadstring(Tx\value$,"Ś")
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		;Item Properties
		Case Cmenu_ed_items
			If param<>-1 Then in_input(3)=param
			If con_item(Int(in_input(3))) Then
				in_input(4)=EntityYaw(TCitem\h)
				in_input(5)=TCitem\health#
				in_input(6)=TCitem\count
				gui_tb_loadstring("","")
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_item Then
						If Tx\parent_id=Int(in_input(3)) Then
							If Tx\mode=0 Then
								gui_tb_loadstring(Tx\value$,"Ś")
							EndIf
						EndIf
					EndIf
				Next
				in_input(7)=0
				If TCitem\parent_class<>0 Then
					If TCitem\parent_mode=Cpm_out Then
						in_input(7)=TCitem\parent_id
					EndIf
				EndIf
			EndIf
		;Info Properties
		Case Cmenu_ed_infos
			If param<>-1 Then in_input(3)=param
			If con_info(Int(in_input(3))) Then
				Select TCinfo\typ
					;Sprite
					Case 5
						in_input(4)=TCinfo\strings[0]
						in_input(5)=TCinfo\floats[0]
						in_input(6)=TCinfo\floats[1]
						in_scol(0)=TCinfo\ints[0]
						in_scol(1)=TCinfo\ints[1]
						in_scol(2)=TCinfo\ints[2]
						in_slider(0)=Float(TCinfo\floats[2]*100.)
						in_check(0)=Int(TCinfo\strings[1])
						in_check(1)=Int(TCinfo\strings[2])
					;Trigger Range,Trigger Time,Trigger Item Count
					Case 10,11,12
						in_opt(3)=TCinfo\ints[0]
						in_input(4)=TCinfo\ints[1]
						in_input(5)=TCinfo\ints[2]
						in_input(6)=Int(TCinfo\floats[0])
						in_check(0)=Int(TCinfo\floats[1])
						in_input(7)=Int(TCinfo\floats[2])
					;Map Indication
					Case 36
						in_opt(3)=TCinfo\ints[0]
						in_check(0)=TCinfo\ints[1]
						in_input(4)=TCinfo\strings[0]
					;Areas
					Case 41,42,43,44
						in_input(4)=Int(TCinfo\floats[0])
					;Spawn Control
					Case 45
						in_input(4)=Int(TCinfo\floats[0])
						in_check(0)=Int(TCinfo\floats[1])
						in_opt(3)=TCinfo\ints[0]
						in_input(5)=TCinfo\ints[1]
						in_input(6)=TCinfo\ints[2]
						in_input(7)=Int(TCinfo\strings[0])
						in_input(8)=Int(TCinfo\strings[1])
					;AI Area
					Case 46
						in_input(4)=Int(TCinfo\floats[0])
						in_opt(3)=TCinfo\ints[0]
					;Loudspeaker
					Case 47
						in_input(4)=TCinfo\strings[0]
						in_input(5)=Int(TCinfo\floats[0])
					;Previewimage
					Case 48
						in_input(4)=TCinfo\strings[0]
				End Select
				gui_tb_loadstring("","")
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(in_input(3)) Then
							If Tx\mode=0 Then
								gui_tb_loadstring(Tx\value$,"Ś")
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;Script Editor
		Case Cmenu_ed_scripts
			;in_input(3)=Script Parent Class
			;in_input(4)=Script Parent ID
			gui_tb_loadstring("","")
			If in_prevmenu=Cmenu_ed_settings
				gui_tb_loadstring(map_briefing$,"Ś")
			ElseIf Int(in_input(3))=-1 Then
				gui_tb_loadstring(ed_setscript$,"Ś")
			Else
				For Tx.Tx=Each Tx
					If Tx\parent_class=Int(in_input(3)) Then
						If Tx\parent_id=Int(in_input(4)) Then
							If Tx\mode=0 Then
								gui_tb_loadstring(Tx\value$,"Ś")
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;State Properties
		Case Cmenu_ed_states
			;in_input(3)=State Parent Type
			;in_input(4)=State Parent Class
			;in_input(5)=State Parent ID
			For Tstate.Tstate=Each Tstate
				If Tstate\typ=Int(in_input(3)) Then
					If Tstate\parent_class=Int(in_input(4)) Then
						If Tstate\parent_id=Int(in_input(5)) Then
							in_input$(6)=Tstate\value_f#
							in_input$(7)=Tstate\value
							split(Tstate\value_s$,",",3)
							in_scol(0)=splits$(0)
							in_scol(1)=splits$(1)
							in_scol(2)=splits$(2)
						EndIf
					EndIf
				EndIf
			Next
			
		;Map
		Case Cmenu_ed_map
			If map_mapimage<>0 Then
				FreeImage map_mapimage
				map_mapimage=0
			EndIf
			
			
	End Select
End Function


;Save Script
Function editor_savescript(class,id=0)
	;Global Scripts
	If class=0 Then
		If gui_tb_filled() Then
			map_briefing$=gui_tb_savestring$("Ś")
		Else
			map_briefing$=""
		EndIf
		Return 1
	ElseIf class=-1 Then
		If gui_tb_filled() Then
			ed_setscript$=gui_tb_savestring$("Ś")
		Else
			ed_setscript$=""
		EndIf
		Return 1		
	EndIf
	;Change or Delete existing Script
	For Tx.Tx=Each Tx
		If Tx\parent_class=class Then
			If Tx\parent_id=id Then
				If Tx\mode=0 Then
					If gui_tb_filled() Then
						Tx\value$=gui_tb_savestring$("Ś")
						Return 1
					Else
						Delete Tx
						Return 0
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	;Create new Script (if needed!)
	If gui_tb_filled() Then
		Tx.Tx=New Tx
		Tx\parent_class=class
		Tx\parent_id=id
		Tx\value$=gui_tb_savestring$("Ś")
		Return 1
	Else
		Return 0
	EndIf
End Function


;Save Settings
Function editor_savesettings()
	;Time
	Local tstr$=in_input$(3)
	map_day=1
	map_hour=9
	map_minute=0
	Local d1,d2
	d1=Instr(tstr$,":")
	d2=Instr(tstr$,":",d1+1)
	If d2<>0 Then
		map_day=Int(Mid(tstr$,1,d1-1))
		map_hour=Int(Mid(tstr$,d1+1,d2-d1-1))
		map_minute=Int(Mid(tstr$,d2+1,-1))
	EndIf
	If map_day<1 Then map_day=1
	If map_hour<0 Then map_hour=0
	If map_hour>23 Then map_hour=23
	If map_minute<0 Then map_minute=0
	If map_minute>59 Then map_minute=59
	map_freezetime=in_check(5)
	;Skybox
	map_skybox$=in_input$(4)
	;Multiplayer
	map_multiplayer=in_check(6)
	;Climate
	map_climate=in_opt(3)			
	;Background Music
	map_music$=in_input$(5)
	;Fog
	map_fog(3)=in_check(7)
	;Briefing / Global Scripts
	If gui_tb_filled() Then
		map_briefing$=gui_tb_savestring$("Ś")
	Else
		map_briefing$=""
	EndIf
	;Update Light
	e_environment_update_light()
End Function


;Childs Menu to setup Childs
Function editor_childs(pclass,pid,states=1,items=1,scripts=1)
	;pclass = parent_class
	;pid = parent_id

	;##################################### States
	If states Then

		;Child States List
		x=0:y=0:j=0
		bmpf_txt(236,390,se$(180)+":")
		x=450
		For Tstate.Tstate=Each Tstate
			If Tstate\parent_class=pclass Then
				If Tstate\parent_id=pid Then
					j=j+1
					If j>in_scr_scr Then
						DrawBlock gfx_if_itemback,x,390
						ret=gui_iconbox(x,390,0,se$(181),0)
						If ret=1 Then
							free_state(Tstate\typ,Tstate\parent_class,Tstate\parent_id)
							If in_scr_scr>0 Then in_scr_scr=in_scr_scr-1
						ElseIf ret=2 Then
							in_input(3)=Tstate\typ
							in_input(4)=pclass
							in_input(5)=pid
							editor_setmenu(Cmenu_ed_states)
						Else
							DrawImage gfx_states,x+10,390+10,(Dstate_frame(Tstate\typ))
							DrawImage gfx_state,x+10,390+10,in_statefxf
						EndIf
						x=x+45
						If x>(450+45*3) Then Exit
					EndIf
				EndIf
			EndIf
		Next
		
		;Child State Options
		If gui_ibutton(742,390,Cicon_inc,se$(182)) Then
			If in_opt(0)<>Cclass_state Then
				in_opt(0)=Cclass_state
				editor_setmode(Cclass_state)
			Else
				set_state(in_state_sel,pclass,pid)
				If x>(450+45*3) Then in_scr_scr=in_scr_scr+1
			EndIf
		EndIf
		If gui_ibutton(742-37,390,Cicon_right,se$(183),x>(450+45*3)) Then in_scr_scr=in_scr_scr+1
		If gui_ibutton(742-37-37,390,Cicon_left,se$(184),in_scr_scr>0) Then in_scr_scr=in_scr_scr-1
	
	EndIf
	
	;##################################### Items
	If items Then
	
		;Child Items List
		x=0:y=0:j=0
		bmpf_txt(236,440,se$(9)+":")
		x=450
		For Titem.Titem=Each Titem
			If Titem\parent_class=pclass Then
				If Titem\parent_id=pid Then
					j=j+1
					If j>in_scr_scr2 Then
						DrawBlock gfx_if_itemback,x,440
						ret=gui_iconbox(x,440,Ditem_iconh(Titem\typ),se$(181),0)
						If ret=1 Then
							free_item(Titem\id)
							If in_scr_scr2>0 Then in_scr_scr2=in_scr_scr2-1
						ElseIf ret=2 Then
							editor_setmenu(Cmenu_ed_items,Titem\id)
						Else
							gui_iconboxc(x,440,Titem\count)
						EndIf
						x=x+45
						If x>(450+45*3) Then Exit
					EndIf
				EndIf
			EndIf
		Next
		;Child Item Options
		If gui_ibutton(742,440,Cicon_inc,se$(185)) Then
			If in_opt(0)<>Cclass_item Then
				in_opt(0)=Cclass_item
				editor_setmode(Cclass_item)
			Else
				set_item(-1,in_item_sel,0,0,0,1)
				store_item(TCitem\id,pclass,pid)
				If x>(450+45*3) Then in_scr_scr2=in_scr_scr2+1
			EndIf
		EndIf
		If gui_ibutton(742-37,440,Cicon_right,se$(183),x>(450+45*3)) Then in_scr_scr2=in_scr_scr2+1
		If gui_ibutton(742-37-37,440,Cicon_left,se$(184),in_scr_scr2>0) Then in_scr_scr2=in_scr_scr2-1
		
	
	EndIf
	
	;##################################### Scripts
	If scripts Then
	
		;Scripts
		bmpf_txt(236,490,se$(170)+":")
		gui_tb(450,490,285,3,0,1)
		If gui_ibutton(742,490,Cicon_script,se$(71)) Then
			;Save Stuff
			Select pclass
				;Object
				Case Cclass_object
					For Tobject.Tobject=Each Tobject
						If Tobject\id=pid Then
							;Rotation
							RotateEntity Tobject\h,EntityPitch(Tobject\h),Float(in_input(4)),EntityRoll(Tobject\h)
							;Health
							Tobject\health#=Float(in_input(5))
							Tobject\health_max#=Float(in_input(6))
							;Script
							editor_savescript(Cclass_object,Int(in_input(3)))
							Exit
						EndIf
					Next
				;Unit
				Case Cclass_unit
					For Tunit.Tunit=Each Tunit
						If Tunit\id=pid Then
							;Rotation
							RotateEntity Tunit\h,EntityPitch(Tunit\h),Float(in_input(4)),EntityRoll(Tunit\h)
							;Health
							Tunit\health#=Float(in_input(5))
							Tunit\health_max#=Float(in_input(6))
							;Script
							editor_savescript(Cclass_unit,Int(in_input(3)))
							Exit
						EndIf
					Next
				;Item
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\id=pid Then
							;Rotation
							RotateEntity Titem\h,EntityPitch(Titem\h),Float(in_input(4)),EntityRoll(Titem\h)
							;Health
							Titem\health#=Float(in_input(5))
							;Count
							If Int(in_input(6))<1 Then in_input(6)=1
							Titem\count=Int(in_input(6))
							;Script
							editor_savescript(Cclass_item,Int(in_input(3)))
							Exit
						EndIf
					Next
				;Info
				Case Cclass_info
					For Tinfo.Tinfo=Each Tinfo
						If Tinfo\id=pid Then
							editor_saveinfo(Tinfo)
							Exit
						EndIf
					Next
			End Select
			;Open Scriptmenu
			in_input(3)=pclass
			in_input(4)=pid
			editor_setmenu(Cmenu_ed_scripts)
		EndIf
		
	EndIf
End Function


Function editor_saveinfo(Tinfo.Tinfo)
	Select Tinfo\typ
		;Sprite
		Case 5
			Tinfo\strings[0]=in_input(4)
			Tinfo\floats[0]=in_input(5)
			Tinfo\floats[1]=in_input(6)
			Tinfo\ints[0]=in_scol(0)
			Tinfo\ints[1]=in_scol(1)
			Tinfo\ints[2]=in_scol(2)
			Tinfo\floats[2]=Float(Float(in_slider(0))/100.)
			Tinfo\strings[1]=in_check(0)
			Tinfo\strings[2]=in_check(1)
			info_vts()
			info_setupsprite(Tinfo)
		;Trigger Range,Trigger Time,Trigger Item Count
		Case 10,11,12
			Tinfo\ints[0]=in_opt(3)
			Tinfo\ints[1]=Int(in_input(4))
			Tinfo\ints[2]=Int(in_input(5))
			Tinfo\floats[0]=Int(in_input(6))
			Tinfo\floats[1]=Int(in_check(0))
			Tinfo\floats[2]=Int(in_input(7))
			info_vts()
		;Map Indication
		Case 36
			Tinfo\ints[0]=in_opt(3)
			Tinfo\ints[1]=Int(in_check(0))
			Tinfo\strings[0]=in_input(4)
			info_vts()
		;Areas
		Case 41,42,43,44
			TCinfo\floats[0]=Int(in_input(4))
			info_vts()
		;Spawn Control
		Case 45
			TCinfo\floats[0]=Int(in_input(4))
			Tinfo\floats[1]=Int(in_check(0))
			TCinfo\ints[0]=in_opt(3)
			TCinfo\ints[1]=Int(in_input(5))
			TCinfo\ints[2]=Int(in_input(6))
			TCinfo\strings[0]=Int(in_input(7))
			TCinfo\strings[1]=Int(in_input(8))
			info_vts()
		;AI Area
		Case 46
			TCinfo\floats[0]=Int(in_input(4))
			TCinfo\ints[0]=in_opt(3)
			info_vts()
		;Loudspeaker
		Case 47
			Tinfo\strings[0]=in_input(4)
			TCinfo\floats[0]=Int(in_input(5))
			info_vts()
		;Previewimage
		Case 48
			Tinfo\strings[0]=in_input(4)
			info_vts()
	End Select
	;Save Script
	editor_savescript(Cclass_info,Int(in_input(3)))
End Function
