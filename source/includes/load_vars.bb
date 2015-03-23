;############################################ STUFF

Global ms,lastms							;Millisecs
Global gt									;Gametimer
Global fps,fpsms,fpsf,fo1#,fo2#,f#			;FPS Stuff
Global tmod#=1.								;Time Modification
Global gframe								;Frame

Dim splits$(0)								;Split Cache (Split Function)

Type Tcache									;Cache Type
	Field id
End Type

;Temporary Variables
Global tmp_x#,tmp_y#,tmp_z#					;Temp Position
Global tmp_rx#,tmp_ry#,tmp_rz#				;Temp Root Position
Global tmp_h								;Temp Handle
Global tmp_class,tmp_id						;Temp Class / ID
Global tmp_ty#								;Temp Terrain Y
Global tmp_iselx,tmp_isely,tmp_iseltyp		;Temp Inventory Select
Global tmp_kill								;Temp Kill
Global tmp_killclass						;Temp Killclass
Global tmp_killid							;Temp Killid
Global tmp_killmodel						;Temp Killmodel
Global tmp_killspawntimer					;Temp Spawntimer
Global tmp_ph								;Temp Particle Handle
Global tmp_lines							;Temp Textrect Lines
Global tmp_linesvisible						;Temp Textrect Lines Visible
Global tmp_spawnid							;Temp Spawn Info ID
Global tmp_dumpvars							;Temp Dumpvars
Global tmp_skipevent						;Temp Skipevent
Global tmp_loading							;Temp Loading Map?
Global tmp_setcam							;Temp Set Cam (Sequence Stuff, Loadfunction)
Global tmp_loadmapcmdmap$					;Temp loadmap command map
Global tmp_tarclass							;Temp Target Class
Global tmp_tarid							;Temp Target ID
Global tmp_tarx#,tmp_tary#,tmp_tarz#		;Temp Target Position
Global tmp_gclass,tmp_gid					;Temp Get Class/ID
Global tmp_dvclass,tmp_dvtyp				;Temp Def Var Class/ID
Global tmp_constv							;Temp Constant Value
Global tmp_slideend							;Temp Slideend?

;Blackframe draws black frames to avoid
;that you can see ugly commencing stuff
Global blackframe							;Blackframe (countdown)



;############################################ SETTING VARS (set_)

Global set_debug							;Debug Mode
Global set_debug_menu
Global set_debug_wireframe=0				;Wireframe Mode
Global set_debug_testmap=0					;Test Map Mode?
Global set_debug_testmapfile$				;Test Map File
Global set_debug_rt							;Debug Rendertime?
Global set_debug_rt_objects$				;Rendertime Objects
Global set_debug_rt_units$					;Rendertime Units
Global set_debug_rt_items$					;Rendertime Items
Global set_debug_rt_infos$					;Rendertime Infos
Global set_debug_rt_states$					;Rendertime States
Global set_debug_rt_particles$				;Rendertime Particles
Global set_debug_rt_cull$					;Rendertime Cull
Global set_debug_rt_updateworld$			;Rendertime Update
Global set_debug_rt_poststuff$				;Rendertime Poststuff
Global set_debug_rt_render
Global set_debug_rt_update
Global set_debug_seq						;Debug Sequences
Global set_debug_load=0						;Debug Load
Global set_drawinterface=1					;Debug Drawinterface

Global set_scrx,set_scrx_c					;Screen X resolution
Global set_scry,set_scry_c					;Screen Y resolution
Global set_scrbit,set_scrbit_c				;Screen Color Depth Bit
Global set_scrwin							;Screen Windowed
Global set_reboot							;Reboot?

Global set_msens#							;Mouse Sensibility
Global set_minvert							;Mouse Invert
Global set_msmooth							;Mouse Smooth

Global set_viewrange,set_viewfac#			;Viewrange/Viewrange Factor
Global set_terrain							;Terrain Details
Global set_water							;Water Details
Global set_sky								;Sky Details
Global set_effects							;Effects
Global set_grass							;Grass

Global set_water_texsize					;Water Texsize
Global set_water_updaterate					;Water Updaterate
Global set_water_updatet					;Water Update Timer
Global set_water_updatestep					;Water Update Step

Global set_2dfx								;2D Effects?
Global set_lightfx							;Light Effects?
Global set_windsway							;Windsway?
Global set_fog								;Fog?
Global set_hwmultitex						;Hardware Multitexturing?

Global set_mb								;Motionblur?
Global set_mb_alpha#						;Motionblur Alpha
Global set_mb_alpha_override#				;Motionblur Override

Global set_musicvolume#						;Music Volume
Global set_fxvolume#						;Sound FX Volume
Global set_name$							;Playername
Global set_serverport						;Serverport

Global set_cmd$								;Commandline
Global set_rootdir$							;Stranded II Dir
Global set_moddir$							;Mod Dir
Global set_inbb								;In Blitzbasic / Devmode

Global set_buildplaceid						;ID of Building Site Object
Global set_buildplacewaterid				;ID of Building Site Object (Water)

Global set_xinvert							;X Mouse Invert (Dizzy)

Global set_gore								;Gore (Blood FX) on/off

Global set_flarefx							;Sun Lenslfares? (Script Controlled!)

;Server Settings
Global set_sv_name$
Global set_sv_port
Global set_sv_ip
Global set_sv_map$
Global set_sv_pw$
Global set_sv_playerlimit


;############################################ CAM

Global cam									;Camera
Global camxz								;Camera XZ Pivot


;############################################ LISTENER

Global listener


;############################################ MENU (m_)

Global m_section							;Section
Global m_menu,in_prevmenu					;Menu / previous Menu
Global m_performquit						;Quit?


;############################################ INPUT / INTERFACE (in_)

Dim in_mhit(5)								;Mouse Hit
Dim in_mdown(5)								;Mouse Down
Dim in_mrelease(5)							;Mouse Release
Global in_mx,in_my							;Mouse Position
Global in_mxo,in_myo,in_mpt,in_mpts			;Mouse Position Old + Timer
Global in_mxs#,in_mys#,in_mzs#				;Mouse Speed (in_mzs=Scrollspeed)
Global in_key								;Key
Global in_escape							;Escape Hit?
Global in_ttx,in_tty,in_ttt$				;Tool Tip
Dim in_check(10)							;Checkbox GUI Object
Dim in_opt(10)								;Option GUI Object
Dim in_input$(30)							;Input GUI Object
Global in_inputfocus						;Focused Input GUI Object
Dim in_slider(10)							;Slider GUI Object
Global in_slidefocus						;Focused Slider GUI Object
Global in_cursor,in_cursorms,in_cursorf		;Cursor
Global in_px#,in_py#,in_pz#,in_pe			;Picking
Global in_ca#								;"Color Angle" (0-360) for Color with sin/cos/tan
Global in_wa#								;Water Angle for Water Wobble

Global in_t50,in_t50go						;Timer 50 MS
Global in_t100,in_t100go					;Timer 100 MS
Global in_t500,in_t500go					;Timer 500 MS
Global in_t1000,in_t1000go					;Timer 1000 MS
Global in_t2000,in_t2000go					;Timer 2000 MS
Global in_t3000,in_t3000go					;Timer 3000 MS
Global in_t5000,in_t5000go					;Timer 5000 MS
Global in_t10000,in_t10000go				;Timer 10000 MS

Global in_tgametime							;Timer Gametime (InGame Gametime Clock)

Global in_gt20,in_gt20go					;GT 20 MS
Global in_gt50,in_gt50go					;GT 50 MS
Global in_gt100,in_gt100go					;GT 100 MS
Global in_gt500,in_gt500go					;GT 500 MS
Global in_gt1000,in_gt1000go				;GT 1000 MS
Global in_gt5000,in_gt5000go				;GT 5000 MS

Global in_gtmod#=1							;GT Modifier

Global in_turnh								;Turn Handle (Editor)
Global in_object_scr						;Object Scroll
Global in_object_sel						;Object Selected
Global in_unit_scr							;Unit Scroll
Global in_unit_sel							;Unit Selected
Global in_item_scr							;Item Scroll
Global in_item_sel							;Item Selected
Global in_info_scr							;Info Scroll
Global in_info_sel							;Info Selected
Global in_state_scr							;State Scroll
Global in_state_sel							;State Selected
Global in_scr_scr							;Scroll 1
Global in_scr_scr2							;Scroll 2
Global in_scr_scr3							;Scroll 3
Global in_scr_descr							;Scroll Description
Global in_scr_sel=-1						;Scroll Selected
Global in_scr_sel2=-1						;Scroll Selected 2

Global in_ex_sel							;Exchange selected Item
Global in_ex_class							;Exchange Class
Global in_ex_id								;Exchange ID
Global in_ex_allowstore						;Exchange Allowstore
Global in_ex_only$							;Exchange only the following

Global in_dnd								;Drag and Drop? 0/1
Global in_dnd_pivotx						;Drag Pivot X
Global in_dnd_pivoty						;Drag Pivot Y
Global in_dnd_src							;Drag and Drop Source (0 Player / 1 Exchange)
Global in_dnd_typ							;Drag and Drop Item Typ
Global in_dnd_x								;Drag and Drop X
Global in_dnd_y								;Drag and Drop Y
Global in_dnd_show							;Drag and Drop Show

Global in_accelerate#						;Acceleration (Editor)

Global in_getkey=-1							;Get Keycode for Action
Dim in_keys(20)								;Keycodes for Actions
Dim in_keyname$(262)						;Keynames
Dim in_keyhit(41)							;Key Hit
Dim in_keydown(41)							;Key Down
Dim in_keyrelease(41)						;Key Release

Dim game_scriptkey(20)						;Keycodes for Scriptkeys (load_game.bb)

Dim in_col(2)								;Color
Dim in_tercol(2,2)							;Terrain Colors (Mapgen)
Dim in_scol(2)								;State Color

Global in_edbrush							;Editor Brush (Mapediting Brush Size)
Global in_edtool							;Editor Tool
Global in_edgrassmode						;Editor Grassmode
Global in_edzoom=1							;Editor Zoom
Global in_edmap$							;Editor currently loaded map
Global in_edrandrot							;Editor Random rotation
Global in_edrot#							;Editor Rotation
Global in_edtertool							;Editor Terrain Tool
Global in_edbrushangle#						;Editor Brush Angle
Global in_edoverinfo$						;Editor Over Info
Global in_edterbtool						;Editor Terrain Brush Tool Mode
Global in_edselgroup						;Editor Select Group

Global in_fraph								;Free Rotating and Positioning Handle (Editor)
Global in_frapdown							;FRAP Down

Global in_tb_scrx,in_tb_scry				;Textbox Scroll (x,y)
Global in_tb_row							;Textbox Row (Cursor)
Global in_tb_col							;Textbox Column (Cursor)

Global in_input_scr							;Input Scroll
Global in_input_col							;Input Column

Global in_mark								;Mark Mode (0=None, 1=marksel, 2=marked)
Global in_ms_col,in_ms_row					;Mark Start col / line
Global in_me_col,in_me_row					;Mark End col / line

Global in_editor_dummy						;Dummy Model for Editor
Global in_editor_buffer						;Editor Image Buffer
Global in_editor_bufferx					;Buffer X Pos
Global in_editor_buffery					;Buffer Y Pos
Global in_editor_bufferz					;Editor Image Buffer Zoomed
Global in_editor_sidebar					;Editor Sidebar?

Global in_buildsetup_dummy					;Dummy Model for Buildsetup
Global in_buildsetup_typ					;Buildsetup Typ (positive=object,negative=unit)
Global in_buildsetup_watersurface			;Build at Watersurface?
Global in_buildsetup_space					;Spacemode for Building
Global in_buildsetup_id						;Buildsetup ID

Global in_combi_gid							;Combination Group ID

Global in_console							;Console On/Off
Global in_conin$							;Console Input
Global in_conlastloadmsg$					;Console Lastloadmsg
Global in_conscroll							;Console Scroll

Global in_mxsmooth#,in_mysmooth#			;Smooth Movement

Dim in_quickslot(9)							;Item Quickslots

Global in_focus								;Focus Type (0=Off)
Global in_fo_x,in_fo_y						;Focus Position
Global in_fo_txt$							;Focus Text
Global in_fo_perc							;Focus Percent
Global in_fo_id								;Focus ID
Global in_fo_timer							;Focus Rescan Timer

Global in_msgtitle$							;Messagebox Title
Global in_msgtext$							;Messagebox Text

Dim in_win_color(2)							;Win Color
Global in_win_path$							;Win Path
Global in_win_file$							;Win File
Global in_win_map$							;Win Map

Global in_usgn_version$						;USGN Version
Global in_usgn_msg$							;USGN Message
Global in_usgn_msgscr#						;USGN Message Scroll

Global in_chat$								;Chat Message
Global in_lastchat$

Global in_sh								;Script Helper
Global in_sh_x,in_sh_y						;Script Helper Position
Global in_sh_txt$							;Script Helper Text
Global in_sh_cmd$							;Script Helper Command
Global in_sh_update							;Script Helper Update? (Force)

Global in_lastjump							;Last Jump

Dim in_sb(9)								;Script Button Exists
Dim in_sb_icon(9)							;Script Button Icon
Dim in_sb_txt$(9)							;Script Button Text
Dim in_sb_scr$(9)							;Script Button Script
Dim in_sb_handle(9)							;Script Button Icon Handle

Dim in_sitxt(19)							;Script Hud
Dim in_sitxt_txt$(19)						;Script Hud Text
Dim in_sitxt_font(19)						;Script Hud Font
Dim in_sitxt_align(19)						;Script Hud Alignment
Dim in_sitxt_x(19)							;Script Hud X
Dim in_sitxt_y(19)							;Script Hud Y

Dim in_siimg(39)							;Script Image (Handle)
Dim in_siimg_mask(39)						;Script Image Masked?
Dim in_siimg_x(39)							;Script Image X
Dim in_siimg_y(39)							;Script Image Y

Global in_tpforce							;Player Teleport Force
Dim in_tp#(2)								;Player Teleport Debug

Global in_sticktoslide=0					;Move Mouse to Scrollbar
Global in_sticktoslidex						;Move to X
Global in_sticktoslidey						;Move to Y

Global in_quicksave							;Perform Quicksave/-load, Autosave/-load, load/save map
Type Tin_quicksave
	Field mode
	Field map$
	Field skills
	Field items
	Field vars
	Field diary
	Field states
	Field locks
End Type

Global in_pc_custom$						;Custom Process Text
Global in_pc_customt						;Custom Process Timer
Global in_pc_event$							;Custom Process Event
Global in_pc_menu							;Custom Process Menu Return
Global in_pc_menu_x							;Custom Process Menu Return Mouse X
Global in_pc_menu_y							;Custom Process Menu Return Mouse Y

Global in_dlg$								;Dialogue Text/Script
Global in_dlg_page$							;Dialogue Current Page

Global in_scriptblur#						;Scriptblur

Global in_tut								;Tut?
Global in_tut_y#							;Tut Y-Offset
Global in_tut_txt$							;Tut Txt
;in_tut_txt$="Hallo, Willkommen zu Stranded II! Ich werde dir erklaeren wie das Spiel funktioniert. Bringe mich einfach um, wenn du keine Lust hast, dass ich dir helfe :)"

Global in_wpnrdy							;Weapon Ready?
Global in_wpnrdyg#							;Weapon Ready Green

Global in_blockclose						;Block Menu-Closing-Attempts with Space
Global in_jumprehit							;Jump Rehit Needed

Global in_waverate							;Waverate Timer

Global in_compass							;Compass? (0/1)

Dim in_skyoverride(4)						;Sky Override

Global in_clickscreenimage					;Clickscreenimage
Global in_clickscreenx						;Clickscreenclose

Global in_buildg$							;Selected Building Group

Global in_statefxf							;State FX Frame
Global in_statefxt							;State FX Timer

Global in_buildcamh							;Build Cam Height


;############################################ PROCESS

Global pc_typ								;Process Typ
Global pc_value								;Process Value
Global pc_child								;Process Child
Global pc_txt$								;Process Text
Global pc_gt								;Process Timer


;############################################ VEHICLE

Global v_speed#								;Vehicle current Speed

Global v_topspeed#							;Vehicle Topspeed
Global v_turnspeed#							;Vehicle Turnspeed
Global v_acceleration#						;Vehicle Acceleration
Global v_friction#							;Vehicle Friction
Global v_steering							;Vehicle Steering
											;0 - normal, not speed based
											;1 - based on speed (no speed, no steering)
											;2 - relativ to speed (no speed, slow steering)
Global v_maxdepth							;Vehicle Max. Depth
Global v_flyspeed#							;Vehicle Speed req. for inc. height

Global v_steerpos							;Steerposition



;############################################ PARSER (p_ / pv_ / pc_ )

Global p_l$									;Parse Line
Global p_len								;Parse Line Length
Global p_lines								;Parse Lines
Global p_pl									;Parse Position (Line)
Global p_p									;Parse Position (Col)
Global p_gotparam							;Parse Got Parameter?
Global p_com$								;Parse Command
Global p_bracelevel							;Parse Bracelevel
Global p_bracemode							;Parse Bracemode (0=don't expect open,1=expect open)
Global p_bracetype$							;Parse current bracetype
Global p_bracepar$							;Parse current brace parameter (0/1)
Global p_elseif								;Parse ElseIF Root Bool
Global p_varname$							;Parse Varname

Global p_loopmode							;Loop? 0 no / 1 yes
Global p_looprow							;Loop Script Start Row
Global p_loopcol							;Loop Script Start Col
Global p_looptype$							;Loop Type
Global p_loopargs$	 						;Loop Arguments
Global p_looplevel							;Loop Level
Global p_loopi								;Loop Index
Global p_loop_object.Tobject				;Object Loop Handle
Global p_loop_unit.Tunit					;Unit Loop Handle
Global p_loop_item.Titem					;Item Loop Handle
Global p_loop_info.Tinfo					;Info Loop Handle
Global p_loop_state.Tstate					;State Loop Handle

Global p_parseinprogress					;Parsing?

Global p_env_class							;Parse Environment Class
Global p_env_id								;Parse Environment ID
Global p_env_event$							;Parse Environment Event
Global p_env_info$							;Parse Environment Info

Global p_skipevent							;Parse Skip Event Action

Global p_m_ol,p_m_or						;Parse Math Offset
Global p_m_base$,p_m_value$,p_m_basec		;Parse Math Base/Value
Global p_m_o$								;Parse Math Operator

Global p_error								;Parse Error?
Dim p_erd$(10)								;Parse Error Details
Global p_noerror							;Parse Without Error Output

;Parse Vars

Global pv_x#,pv_y#,pv_z#					;Parse Var X,Y,Z
Global pv_buffer$							;Parse Var Buffer
Global pv_attack_damage#					;Parse Var Attack Damage
Global pv_attack_weapon						;Parse Var Attack Weapon (Item Type)
Global pv_attack_ammo						;Parse Var Attack Ammo (Item Type)

Global pv_impact_class						;Parse Var Impact Class (of attacked Object)
Global pv_impact_id							;Parse Var Impact ID (of attacked Object)
Global pv_impact_kill						;Parse Var Impact Kill (attacked Object)
Global pv_impact_num						;Parse Var Impact Number
Global pv_impact_amount						;Parse Var Impact Amount
Global pv_impact_x#							;Parse Var Impact X
Global pv_impact_y#							;Parse Var Impact Y
Global pv_impact_z#							;Parse Var Impact Z
Global pv_impact_ground						;Parse Var Impact Ground

Global pv_ai_eater							;Parse Var AI Eater

Global pv_state								;Parse Var  

Global p_return$							;Parse Return Value

Global p_internal							;Parse Internal
Global p_internaltxt$						;Parse Internal Text

Global pv_lastbuilding						;Last Buildingsite ID

Global pv_loadmaploaded						;Loaded with Loadmap? For loadmaptakeover

;Parse Command Vars

Global pc_sp_txt$							;Parse Command - Select Place - Text
Global pc_sp_height#						;Parse Command - Select Place - Height
Global pc_sp_x#,pc_sp_y#,pc_sp_z#			;Parse Command - Select Place - X,Y,Z
Global pc_sp_class							;Parse Command - Select Place - Class
Global pc_sp_id								;Parse Command - Select Place - ID

Global pc_use_x#,pc_use_y#,pc_use_z#		;Parse Command - Use - X,Y,Z

Global pl_cl_title$							;Parse Command - Crack Lock - Title
Global pc_cl_mode							;Parse Command - Crack Lock - Mode
Global pc_cl_code$							;Parse Command - Crack Lock - Code
Global pc_cl_class							;Parse Command - Crack Lock - Class
Global pc_cl_id								;Parse Command - Crack Lock - ID
Global pc_cl_pos							;Parse Command - Crack Lock - Position


;############################################ SEQUENCE (seq_)

Global seq_blend							;Sequence Blend Mode (0=closed,1=opened)
Global seq_blendp#							;Sequence Blend Position

Global seq_start							;Sequence Start (MS)

Global seq_skipable							;Sequence Skipable

Dim seq_msg$(2,1)							;Sequence Message

Global seq_cstart							;Sequence Cam Start Point (Pivot)
Global seq_cend								;Sequence Cam End Point (Pivot)
Global seq_cstartt							;Sequence Cam Start Time
Global seq_cendt							;Sequence Cam End Time

Global seq_cpoint							;Sequence Cam Pointmode
											;0 - Center
											;1 - Object
											;2 - Info Path
											;3 - Nothing
Global seq_cpoint_class						;Sequence Cam Point Class
Global seq_cpoint_id						;Sequence Cam Point ID
Global seq_cpoint_p							;Sequence Cam Point Pivot

Global seq_tmod								;Time Modifier
Global seq_tabs								;Time Absolute?
Global seq_tlast							;Time of last Sequence Command

Global seq_cls								;Sequence CLS Mode
Global seq_cls_r,seq_cls_g,seq_cls_b		;Sequence CLS Color

Global seq_img								;Sequence Image?
Global seq_img_masked						;Sequence Image is masked?

Global seq_hideplayer						;Sequence Hideplayer

Global seq_flw_class						;Follow? (0=no, ClassID=yes)
Global seq_flw_id							;Follow ID
Global seq_flw_x#							;Follow X-Offset
Global seq_flw_y#							;Follow Y-Offset
Global seq_flw_z#							;Follow Z-Offset


;############################################ MUSIC (mfx_)

Global mfx_chan								;Music Channel
Global mfx_file								;Music File
Global mfx_filename$						;Music Filename
Global mfx_peakvol#							;Music Peak Volume
Global mfx_vol#								;Music Volume
Global mfx_fade								;Music Fade Mode (0=none, 1=in, -1=out)
Global mfx_fadestart						;Music Fade Start (gt)
Global mfx_fadeend							;Music Fade End (gt)


;############################################ TERRAIN VARS (ter_)

Global ter									;Terrain Handle
Global ter_size								;Terrain Size
Global ter_tex_color						;Terrain Colortexture
Global ter_groundy#							;Terrain Ground Y


;############################################ ENVIRONMENT VARS (env_)

Global env_sea,env_sea2,env_seascr#			;Sea Handle and Stuff
Global env_seasinus#						;Sea Sinus
Global env_sky								;Sky Handle
Global env_ground							;Ground Handle
Global env_light							;Light Handle (Sun/Moon Light)
;Global env_weather							;Weather Entity
Global env_weathery#						;Weather Scroll
Global env_cweather							;Current Weather
											;0 - Normal
											;1 - Rain
											;2 - Snow
											;3 - Thunder
Global env_weatherchan						;Weatherchan (for Weather SFX)
Global env_wbox								;Weather Box (Makes Sky Grey)
Global env_wa#								;Weather Alpha

Global env_watertex							;Water Texture (Cubemapping Stuff)
Global env_watercam							;Water Camera (Cubemapping Stuff)

Dim env_col(2)								;Environment Colors
Dim env_wcol(2)								;Water Color Reduction

Global env_watersin#						;Water Sinus

Dim env_wavematrix(0,0)						;Wave Matrix! Yeah!
Dim env_wavematrixy#(0,0)					;Wave Matrix Heights

Dim env_skytex(5)							;Skytextures

Global env_itemlight						;Itemlight (Light Handle)
Global env_itemchan							;Itemchannel (Item SFX)

Global env_lenspivot						;Lenspivot (Cam Child Pivot for Lensflares)
Global env_lenssun							;Lenssun (Virtual Sun for Lensflares)

Global env_moon								;Moon Sprite Handle




;############################################ GRAS (grass_)

Global grass_c=19							;Grass Count
Global grass_dist=15						;Grass Distance
Global grass_mapsize						;Grass Map Size
Global grass_f#								;Grass Factor Stuff
Global grass_x,grass_y						;Grass Position (Update req?)
Global grass_yaw							;Grass Yaw
Global grass_wind#							;Grass Wind
Dim grass_rgb(0,0,0)						;Grass Colors
Dim grass_h(grass_c,grass_c)				;Grass Handles


;############################################ MOTIONBLUR (mb_)

Global mb_sprite							;Motionblur Sprite
Global mb_tex								;Motionblur Texture


;############################################ GAME (g_)

Global g_player								;Player ID (Units)
Global g_player_mst							;Player MoveSoundTimer
Global g_player_dead						;Player Dead
Global g_cplayer.Tunit						;Player Container

Global g_airtimer							;Player Last Air Timer
Global g_dive								;Player Diving?
Global g_swim								;Player Swimming?

Global g_drive								;Player Drive Unit ID (0=Drive Nothing)

Dim g_objectlist.Tobject(0)					;Active Object List
Global g_objectlistc						;Active Object List Count
Global g_objecttimer						;Object Update Timer

Dim g_unitlist.Tunit(0)						;Active Unit List
Global g_unitlistc							;Active Unit List Count
Global g_unittimer							;Unit Update Timer

Dim g_itemlist.Titem(0)						;Active Item List
Global g_itemlistc							;Active Item List Count
Global g_itemtimer							;Item Update Timer

Global g_unpaused							;Game Unpaused?

Global g_sleep								;Sleep?


;############################################ MAP (map_) / SAVEGAME (sg_) / AMBIENT (amb_)

Global map_name$							;Map Name (Name of loaded Map)
Global map_path$							;Map Path (Full Name + Path of loaded Map)
Global map_day								;Map Time Day
Global map_hour								;Map Time Hour
Global map_minute							;Map Time Minute
Global map_freezetime						;Map Time frozen?
Global map_skybox$,map_skyboxh				;Map Skybox
Global map_multiplayer						;Map Multiplayer
Global map_climate							;Map Climate
Global map_music$,map_musich$				;Map Music
Global map_briefing$						;Map Briefing (Map Script)
Global map_briefing_key$					;Map Briefing Keys (Script Preparsing)
Global map_password$						;Map Editing Password
Global map_mode$							;Map Mode
Global map_image							;Map Image
Global map_timeupdate						;Map Time Update?
Global map_lday								;Map Time Update last Day
Global map_lhour							;Map Time Update last Hour
Global map_lminute							;Map Time Update last Minute
Dim map_fog(3)								;Map Fog (R,G,B,Mode)

Global map_id$								;Map Identification String

Global map_rainratio						;Map Rainratio
Global map_snowratio						;Map Snowratio

Global map_mapimage							;Map Map Image

Global sg_date$								;Savegame Date
Global sg_time$								;Savegame Time
Global sg_multiplayer						;Savegame Multiplayer

Global amb_file								;Ambient Music
Global amb_chan								;Ambient Chan
Global amb_mode								;Ambient Mode (1 Land, -1 Water)


;############################################ MULTIPLAYER (mp_)

Global mp_joinserver						;MP Joinserver Mode

Global mp_sendpostimer						;MP Send Position Timer
Global mp_sendposrate=50					;MP Send Position Rate (ms)
Global mp_sendpingtimer						;MP Send Ping Timer
Global mp_lastping							;MP Last Ping (by Server)

;Player Data Cache (Used in Join Process)
Global mp_name$								;MP Player Name
Global mp_id								;MP Player ID
Global mp_ip								;MP Player IP



;############################################ OBJECT (1)

Global object_serial						;Object serial number
Global TCobject.Tobject						;Object Container

Type Tobject
	Field id								;Serial number
	Field typ								;Typ
	Field h									;Handle
	Field ch								;Collision Handle (for swaying objects)
	Field health#,health_max#				;Health
	Field windsway#							;Windsway
	Field daytimer							;Daytimer
	Field alpha#
	Field chan
End Type


;############################################ UNIT (2)

Global unit_serial							;Unit serial number
Global TCunit.Tunit							;Unit Container

Type Tunit
	Field id								;Serial number			
	Field typ								;Typ
	Field h,mh								;Handle (Collision Pivot) / (Model)
	Field vh								;Vehicle Handle
	Field h_pivot							;Is Handle a Pivot (1) or a Model (0)
	Field health#,health_max#				;Health
	Field hunger#							;Hunger
	Field thirst#							;Thirst
	Field exhaustion#						;Exhaustion
	
	Field player_name$
	Field player_ip							;IP
	Field player_port						;Port
	Field player_ping						;Ping
	Field player_weapon						;Weapon (Item Typ)
	Field player_ammo						;Ammo (Item Typ)
	Field player_lastattack					;Lastattack (MS)
	Field player_score						;Score
	Field player_deads						;Deads
	
	Field phy_jump#							;Jump
	Field phy_ft							;Fly Time (time of last collision)
	Field phy_fall							;Fall Time
	Field phy_fally#						;Fall Y
	
	Field ani								;Current Animation
	
	Field states							;Unit got states influencing its behaviour? (0/1)
	
	Field ai_mode							;AI Mode
	Field ai_mode2							;AI Mode 2
	Field ai_target_class					;AI Target Class
	Field ai_target_id						;AI Target ID
	Field ai_timer							;AI Timer
	Field ai_duration						;AI Duration
	Field ai_cx#,ai_cz#,ai_ch				;AI Center Position & Center Pivot Handle
	
	Field chan								;(Move) Sound Channel
	
	Field freeze							;Frozen?
	
	Field lastspot							;Last Spot/Hunt Start for Spot Sound
End Type


;############################################ ITEM (3)

Global item_serial							;Item serial number
Global TCitem.Titem							;Item Container

Type Titem
	Field id								;Serial number
	Field typ								;Typ
	Field h									;Handle
	Field health#							;Health
	Field count								;Amount
	
	Field phy_pause							;Pause Physiks
	Field phy_fall							;Fall Time
	
	Field parent_class
	Field parent_mode
	Field parent_id
	
	Field if_sel							;Interface Selected?
End Type


;############################################ INFO (4)

Global info_serial							;Info serial number
Global TCinfo.Tinfo							;Info Container

Type Tinfo
	Field id								;Serial number
	Field typ								;Typ
	Field h									;Handle
	
	Field timer								;MS Timer
	
	Field vars$
	Field ints[2]							;Ints
	Field floats#[2]						;Floats
	Field strings$[2]						;Strings
End Type


;############################################ STATE (5)

Global TCstate.Tstate						;State Container

Type Tstate
	Field typ								;Typ
	
	Field parent_class						;Parent Class (0=FREE,1=OBJECT,2=UNIT,3=ITEM)
	Field parent_id							;Parent ID
	
	Field x#,y#,z#							;Position / Modification
	Field fx#,fy#,fz#						;Force
	Field value								;Value
	Field value_f#							;Value Float
	Field value_s$							;Value String
	
	Field r,g,b								;Color
	
	Field h									;Handle
	
	Field light								;Light Handle
	
	Field chan								;Channel for SFX
End Type


;############################################ EXTENSION (Script/Var/Quest)

Global TCx.Tx

Type Tx
	Field typ								;Typ
	Field parent_class						;Parent Class (0=FREE,1=OBJECT,2=UNIT,3=ITEM)
	Field parent_id							;Parent ID
	
	Field mode								;Mode
											;0=SCRIPT
											;1=VAR
											;2=DIARY
											;3=BUILDLOCK
											;4=Local VAR
											;5=Skill
											;6=Save Extension
											;7=Item (only Txc)
											;8=Interface Stuff (only Txc)
											;50=MAPLOCK
	Field key$								;Key
	Field value$							;Value
	Field stuff$							;Stuff
End Type

Type Txc
	Field typ
	Field parent_class
	Field parent_id
	Field mode
	Field key$
	Field value$
	Field stuff$
End Type


;############################################ PARTICLE

Global TCp.Tp								;Particle Container

Type Tp
	Field h									;Handle
	Field typ								;Typ
	
	Field x#,y#								;X/Y (2D only)
	Field fx#,fy#,fz#						;Speed
	Field r#,g#,b#							;Color
	Field a#								;Alpha
	Field size#								;Size
	Field rot#								;Rotation
	Field fadein#							;Fadein
	
	Field age								;Age
	Field timer								;Timer
	Field frame								;Frame
	
	Field parentid							;ID
	Field parentx#							;X
	Field parentz#							;Z
End Type


;############################################ PROJECTILE

Global TCpro.Tpro							;Projectile Container

Type Tpro
	Field mh								;Model Handel
	Field h									;Handle
	Field typ								;Item ID						
	Field spawner							;Spawner
	Field weapon							;Weapon
	
	Field fx#,fy#,fz#						;Speed
	Field age								;Age
	Field water								;0 = Over / 1 = Under
	Field wc								;Had Water Contact? 0 = no / 1 = yes
	Field tail								;Particle Tail Type (0=None)
	
	Field tcol[2]							;Tail Color
	Field tfx								;Tail Special FX
	Field tset#[1]							;Tail Additional Settings
	
	Field speed#							;Speed
	Field damage#							;Damage
	Field drag#								;Drag
	
	Field sx#
	Field sy#
	Field spitch#
End Type


;############################################ RESSOURCE

Type Tres
	Field file$								;File Path
	Field h									;Handle
	Field typ								;Typ
End Type


;############################################ SOUNDSET

;Soundset
Type Tsoundset
	Field name$								;Name of Soundset for Access
	
	Field spot								;Spotsound
	Field attack							;Attacksound
	Field idle1								;Idlesound #1
	Field idle2								;Idlesound #2
	Field idle3								;Idlesound #3
	Field die								;Diesound
	Field flee								;Fleesound
	Field move								;Movesound
End Type


;############################################ COMBINATION

Global com_serial							;Combination serial number
Type Tcom
	Field id								;Internal serial number
	Field mode								;0=Required, 1=Generated, 2=Head
	Field typ								;Item Type
	Field count								;Item Count (Required/Generated)
	Field del								;Delete? (only in Required Mode)
	Field script$							;Script
	Field genname$							;Alternative Genname
	Field gid								;Groupid
	Field aid$								;Internal serial number
End Type

Global coms_serial							;Same


;############################################ BUILDING

Global bui_serial							;Building serial number
Type Tbui
	Field id								;Serial number								
	Field mode								;0=Required
											;1=Buildspace
											;2=Head
											;3=Buildatobjectid
											;4=Buildplace Object ID
	Field typ								;Item Type / Building Type
	Field count								;Item Count (Required)
	Field script$							;Script
	Field group$							;Group
End Type


;############################################ FIND ITEM

Type Tfind
	Field class								;Class
	Field parent							;ID of Parent Object
	Field typ								;Item Typ
	Field ratio								;Find Ratio
	Field max								;Max Findcount
	Field min								;Min Findcount
	Field reqtyp							;Required Item to find
End Type


;############################################ MESSAGE (interface)

Type Tmsg
	Field msg$								;Message Text
	Field col								;Message Color
	Field age								;Message Age (MS)
	Field scroll#							;Message Scroll
End Type


;############################################ TEXTBOX Text

Type Ttbt
	Field txt$								;Text Row
End Type


;############################################ CONSOLE Text

Type Tcon
	Field txt$								;Console Row
End Type


;############################################ PARSE STUFF

Dim Dpc$(0)									;Array
Type Tpc
	Field txt$								;Script Row
End Type

;Parse Jump Point
Type Tpj
	Field l									;Line
	Field pos								;Position
	Field level								;Jump Level
	Field typ$								;Typ
	Field name$								;Name
End Type

;Parse Math
Type Tpm
	Field typ								;Type (Int/Float/String)
	Field pos								;Position
	Field txt$								;Text
	Field value$							;Value
End Type

;Parse Task
Type Tpt
	Field class								;Class where Task is executed
	Field id								;Unit where Task is executed
	Field event$							;Event which has been triggered
	Field info$								;Info
	Field script$							;Script which has to be parsed
End Type

;Parse Loop Jump
Type Tplj
	Field l									;Line
	Field pos								;Position
	Field level								;Jump Level
	Field typ$								;(Loop) Typ
	Field start								;Loop Start
End Type

;Parse Kill Data
Type Tpkill
	Field class								;Killed Class
	Field id								;Killed ID
	Field h									;Kill Pivot for Position and Rotation Commands
	Field spawntimer						;Killed Spawntimer
End Type


;############################################ SAVEGAME

;Savegame
Type Tsg
	Field name$
End Type

;############################################ SEQUENCE

;Sequence
Type Tseq
	Field t									;Trigger Time
	Field event$							;Event
	Field in[5]								;Ints
	Field fl#[5]							;Floats
	Field txt$[5]							;Texts
End Type


;############################################ SFX

;SFX
Type Tsfx
	Field file
	Field chan
End Type


;############################################ FILESTUFF

;Filestuff
Type Tfile
	Field name$								;Filename
	Field path$								;Filepath
	Field typ								;Filetype (1=File, 2=Dir/Drive)
	Field icon								;Icon Frame
End Type


;############################################ WAVESPAWN

;Wavespawn
Type Twave
	Field x,z
	Field dir
	Field h
End Type

;############################################ SCRIPT COMMAND

;Script Command
Type Tsc
	Field cmd$
	Field groups$
	Field params$
End Type

;Script Group
Type Tsgr
	Field group$
End Type


;############################################ SERVER

;Server
Type Tserver
	Field ip								;Server IP
	Field port								;Server Port
	
	Field name$								;Server Name
	Field map$								;Server Map
	Field players							;Server Players
	Field maxplayers						;Server Maxplayers
	Field password							;Server Password
	
	Field ping								;Server Ping
	Field pingstamp							;Server Pingstamp
	
	Field timer								;Server Timer
End Type

;############################################ MUTLIPLAYER POSITION

;Position
Type Tpos
	Field id								;Player ID
	Field time								;Time of arrival
	Field x#,y#,z#							;Position
	Field yaw#								;Yaw
End Type

;############################################ ATTACHMENT

;Attachment
Type Tat
	Field path$								;Pfad
End Type


;############################################ TIMER

;Timer
Type Ttimer
	Field typ								;Timer Typ
											;0 - Event Timer
											;1 - Script Timer
	Field parent_class						;Parent Class
	Field parent_id							;Parent ID
	Field duration							;Duration (MS)
	Field start								;Start (GT)
	Field mode								;Mode
											;-1 - Infinitive
											;>0 - Mode - Times
	Field scr$								;Script/Event
End Type


;############################################ FREE ROTATION AND POSITION

;FRAP
Type Tfrap
	Field parent_class						;Parent Class
	Field parent_id							;Parent ID
	Field y#								;Y
	Field pitch#							;Pitch
	Field roll#								;Yaw
End Type


;############################################ UNIT PATH

Global TCup.Tup

;UP
Type Tup
	Field unit								;Unit ID
	Field info								;Info ID
	Field mode								;Mode
											;0 	- Controller
											;>0	- Path Node
End Type


;############################################ Water System

Type Tw
	Field h
	Field id
End Type


;############################################ Speech

Global speech_chan							;Speech Channel

Type Tspeech
	Field file$								;Speech File
	Field limit								;Speech File Count
	Field lastplayed						;Speech File Last Played
End Type


;############################################ Random

;Type
Type Tran
	Field r1#								;Y-Range Min.
	Field r2#								;Y-Range Max.
	Field a									;Amount (Ratio)
	Field class								;Class
	Field id								;ID
End Type

;Random Profiles
Global Crandom_c=50							;Random Profile Count Limit
Dim Drprofile$(Crandom_c)					;Random Profile Array
Global random_c=0							;Random Profile Count

;Random Pool
Dim Drpool(0)								;Random Pool Array

;Random Map Script
Global random_script$						;Random Map Script


;############################################ Recent

Type Trecent
	Field file$
End Type


;############################################ Def Param

Type Tdefparam
	Field class
	Field typ
	Field key$
	;Field value
	Field value$
End Type

;############################################ Def Var

Type Tdefvar
	Field class
	Field typ
	Field key$
	Field descr$
	Field defaultval$
	Field isglobal
End Type


;############################################ Scriptani

Type Tsani
	Field typ
	Field h
	Field f1
	Field f2
End Type


;############################################ Lensflare

Type Tlensf
	Field h									;Handle
	Field offset#							;Offset
	Field ra#								;Real Alpha
	Field ca#								;Current Alpha
End Type


;############################################ 3D Text

Type t3d
	Field class
	Field id
	Field txt$
	Field typ
	Field offset
	Field range
End Type


;############################################ Constants

Type tconst
	Field name$
	Field value
End Type


;############################################ ClickScreen

Type Tcscr
	Field typ						;1 Text, 2 Image
	Field x
	Field y
	Field txt$
	Field col
	Field image
	Field event$
	Field align
	Field tooltip$
End Type


;############################################ Group

Dim ed_groupg$(4)
Dim ed_groupn$(4)
Dim ed_groupc(4)

Global ed_setscript$

Global ed_current_class=-5
Global ed_current_group$
Global ed_current_c

Type Tgroup
	Field class
	Field group$
	Field name$
	Field count
End Type

Type Tgroupl
	Field id
End Type


;############################################ Copy
Type Tcopy
	Field typ					;0 Script, 1 Local, 2 Item, 3 State
	Field id
	Field key$
	Field value
	Field value_f#
	Field value_s$
	Field x#,y#,z#
	Field fx#,fy#,fz#
	Field r,g,b
End Type

;############################################ Trade
Type Ttrade
	Field id					;Trade Group ID
	Field mode					;Trade Mode (1=Sell,2=Buy,3=Script)
	Field typ					;Trade Item Type
	Field count					;Trade Item Count
End Type
