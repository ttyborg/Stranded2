;############################################ World (Cworld)

Const Cworld_size=64						;Terrain X/Z Size
Const Cworld_height=3200					;Terrain Height
Const Cworld_mscale#=2						;Mesh Scale

Const Cworld_g#=9.81						;World Gravity
Const Cworld_facct=3000						;Fall Acceleration Time (ms)

Const Cworld_col=100						;World Collision Type (Polygonal)
Const Cworld_unitcol=1						;Units Collision Type (Radius)
Const Cworld_procol=50						;Projectile Collision Type (Radius)
Const Cworld_itemcol=2						;Items Collisions Type (Radius)

Const Cworld_userange=48					;Use Range
Const Cworld_useradius=5					;Use Radius

Const Cworld_minute_ms=500					;Game Time Minute / Real Time Millisecs - Ratio
											;200

Const Cchatshow=8000						;Chatmessage Showtime

Const Cpeakblur#=0.97						;Peak Motion Blur


;############################################ Sections (Csection)

Const Csection_menu=0						;Menu
Const Csection_game_sp=1					;Game SP
Const Csection_game_mp=2					;Game MP
Const Csection_editor=3						;Editor


;############################################ Menus (Cmenu)

;Main
Const Cmenu_main=0							;Main
Const Cmenu_set_in=50						;Settings Input
Const Cmenu_set_gfx=51						;Settings GFX
Const Cmenu_set_sfx=52						;Settings SFX
Const Cmenu_reboot=53						;Reboot
Const Cmenu_set_inscript=54					;Settings Input for Scriptkeys
Const Cmenu_sp=60							;Singleplayer
Const Cmenu_sp_load=61						;Singleplayer Load
Const Cmenu_random=70						;Random
Const Cmenu_mp=80							;Multiplayer (Online Games)
Const Cmenu_mp_lan=81						;Multiplayer (LAN Games)
Const Cmenu_mp_create=82					;Multiplayer Create Game
Const Cmenu_credits=90						;Credits
Const Cmenu_quit=100						;Quit?


;Editor
Const Cmenu_ed_new=1
Const Cmenu_ed_settings=2
Const Cmenu_ed_infolist=3
Const Cmenu_ed_options=4
Const Cmenu_ed_quit=5
Const Cmenu_ed_load=10
Const Cmenu_ed_save=20
Const Cmenu_ed_heightmap=30
Const Cmenu_ed_colormap=35
Const Cmenu_ed_grassmap=40
Const Cmenu_ed_objects=100
Const Cmenu_ed_units=101
Const Cmenu_ed_items=102
Const Cmenu_ed_infos=103
Const Cmenu_ed_scripts=104
Const Cmenu_ed_states=105
Const Cemnu_ed_defvars=109
Const Cmenu_ed_attachments=110
Const Cmenu_ed_random=120
Const Cmenu_ed_list=200
Const Cmenu_ed_map=201



;Interface
Const Cmenu_if_char=1						;Menus with Inferface + Sidebar (Game paused)
Const Cmenu_if_items=2
Const Cmenu_if_diary=3
Const Cmenu_if_loadgame=10
Const Cmenu_if_savegame=15
Const Cmenu_if_build=20
Const Cmenu_if_msg=21
Const Cmenu_if_find=22
Const Cmenu_if_exchange=23
Const Cmenu_if_map=24
Const Cmenu_if_cracklock=25
Const Cmenu_if_dlg=26
Const Cmenu_if_combine=27
Const Cmenu_if_debugmenu=40
Const Cmenu_if_debugmenu_items=41
Const Cmenu_if_debugmenu_script=42
Const Cmenu_if_quit=50

Const Cmenu_if_clickscreen=60				;Menus with Nothing (Game paused)


Const Cmenu_if_movie=100					;Menus with Nothing (Game unpaused)
Const Cmenu_if_cheat=102
Const Cmenu_if_use=200


Const Cmenu_if_buildsetup=300				;Menus with Interface only (Game unpaused)
Const Cmenu_if_selplace=301
Const Cmenu_if_debug=302
Const Cmenu_if_chat=305



;############################################ Classes (Cclass)

Const Cclass_global=0,Cclass_world=0		;Global/World
Const Cclass_object=1						;Object Class ID
Const Cclass_unit=2							;Unit Class ID
Const Cclass_item=3							;Item Class ID
Const Cclass_info=4							;Info Class ID
Const Cclass_state=5						;State Class ID

Const Cclass_building=150					;Building Class (for Groups)


;############################################ Parent Modes (Cpm)

Const Cpm_out=0								;Stored Outside (Visible Model)
Const Cpm_in=1								;Stored Inside (Invisible Model)


;############################################ Icons (Cicon)

Const Cicon_save=0
Const Cicon_load=1
Const Cicon_new=2
Const Cicon_x=3
Const Cicon_colormap=4
Const Cicon_heightmap=5
Const Cicon_stuffmap=6
Const Cicon_left=7
Const Cicon_right=8
Const Cicon_dir=9
Const Cicon_up=10
Const Cicon_down=11
Const Cicon_inc=12
Const Cicon_dec=13
Const Cicon_pw=14
Const Cicon_distort=15
Const Cicon_options=16
Const Cicon_ok=17
Const Cicon_figure=18
Const Cicon_bag=19
Const Cicon_tree=20
Const Cicon_info=21
Const Cicon_dialog=22

Const Cicon_smooth=23
Const Cicon_borderup=24
Const Cicon_borderdown=25
Const Cicon_border=26
Const Cicon_one=27
Const Cicon_all=28
Const Cicon_grassmap=29

Const Cicon_mapaction=30
Const Cicon_script=31
Const Cicon_ext=32

Const Cicon_entity=33

Const Cicon_check=41
Const Cicon_list=42

Const Cicon_build=44
Const Cicon_cam=45

Const Cicon_brush=46
Const Cicon_spray=47
Const Cicon_sun=48
Const Cicon_moon=49
Const Cicon_update=50
Const Cicon_file=51
Const Cicon_parentdir=52
Const Cicon_soundfile=53
Const Cicon_imagefile=54
Const Cicon_stranded=55
Const Cicon_zoom=56
Const Cicon_settoterrain=57
Const Cicon_flatten=58
Const Cicon_updown=59

Const Cicon_trange=60
Const Cicon_ttime=61
Const Cicon_tcount=62

Const Cicon_sleep=63

Const Cicon_ex1=64
Const Cicon_ex5=65
Const Cicon_exall=66

Const Cicon_map=67
Const Cicon_select=68

Const Cicon_s2script=73

Const Cicon_plotbrush=74
Const Cicon_linebrush=75

Const Cicon_globe=76
Const Cicon_hand=77
Const Cicon_eat=80



;############################################ Bitmap Fonts (Cbmpf)

Const Cbmpf_norm=0
Const Cbmpf_over=1,Cbmpf_yellow=1
Const Cbmpf_dark=2
Const Cbmpf_red=3
Const Cbmpf_green=4
Const Cbmpf_tiny=5
Const Cbmpf_handwriting=6,Cbmpf_hw=6


;############################################ Ressources (Cres)

Const Cres_image=0
Const Cres_texture=1
Const Cres_sound=2
Const Cres_mesh=3
Const Cres_animmesh=4
Const Cres_music=5


;############################################ Keys (Ckey)

Const Ckey_forward=0
Const Ckey_backward=1
Const Ckey_left=2
Const Ckey_right=3

Const Ckey_jump=4
Const Ckey_sleep=5

Const Ckey_attack1=6
Const Ckey_attack2=7
Const Ckey_next=8
Const Ckey_prev=9
Const Ckey_drop=10

Const Ckey_use=11
Const Ckey_chat=16
Const Ckey_char=12
Const Ckey_items=13
Const Ckey_diary=17
Const Ckey_quicksave=14
Const Ckey_quickload=15

;############################################ Materials (Cmat)

Const Cmat_c=11

Const Cmat_none=0
Const Cmat_wood=1
Const Cmat_stone=2
Const Cmat_dirt=3
Const Cmat_dust=4
Const Cmat_leaf=5
Const Cmat_metal=6
Const Cmat_flesh=7
Const Cmat_water=8
Const Cmat_lava=9
Const Cmat_fruit=10
Const Cmat_glass=11


;############################################ States (Cstate)

Const Cstate_bleeding=1
Const Cstate_intoxication=2
Const Cstate_pus=3
Const Cstate_fire=4
Const Cstate_eternalfire=5
Const Cstate_frostbite=6
Const Cstate_fracture=7
Const Cstate_electroshock=8
Const Cstate_bloodrush=9
Const Cstate_dizzy=10
Const Cstate_wet=11
Const Cstate_fuddle=12

Const Cstate_healing=16
Const Cstate_invulnerability=17
Const Cstate_tame=18

Const Cstate_action=21
Const Cstate_flare=22
Const Cstate_smoke=23
Const Cstate_light=24
Const Cstate_particles=25

Const Cstate_phy=51						;Physical Forces
Const Cstate_buildplace=52				;Object is a Buildplace
Const Cstate_link=53					;Object is linked to Another Object
Const Cstate_speedmod=54				;Unit Speedmodification
Const Cstate_ghost=55					;Object has no collision

Const Cstate_ai_stick=60				;AI does not move

Const Cstate_pc_build=100				;Process: Build
Const Cstate_pc_dig=151					;Process: Dig
Const Cstate_pc_fish=152				;Process: Fish
Const Cstate_pc_custom=153				;Process: Custom


;############################################ Particles (Cp)

;3D / Sprites
Const Cp_attack=4
Const Cp_debug=5
Const Cp_bubbles=10
Const Cp_rwave=11
Const Cp_splash=12
Const Cp_wave=15
Const Cp_hover=19
Const Cp_smoke=20
Const Cp_spark=21
Const Cp_splatter=22
Const Cp_subsplatter=23
Const Cp_wood=24
Const Cp_puddle=25
Const Cp_flames=30
Const Cp_firespark=35
Const Cp_risingflare=40
Const Cp_explode=45
Const Cp_shockwave=46
Const Cp_starflare=50
Const Cp_spawn=51
Const Cp_impact=60
Const Cp_rain=70
Const Cp_snow=71
Const Cp_fade=98
Const Cp_seqfade=99
Const Cp_flash=100
Const Cp_fadeout=101
Const Cp_fall=102
Const Cp_resfade=103
Const Cp_stuck=104

;2D
Const Cp2d_blackrect=-1
Const Cp2d_item=-2
Const Cp2d_dot=-3
Const Cp2d_cdot=-4


;############################################ Projectile "Tails"

;Normal
Const Cpro_smoke=1
Const Cpro_fire=2
Const Cpro_poison=3
Const Cpro_blood=4
Const Cpro_wet=5
Const Cpro_healing=6
Const Cpro_tame=7

;Adjustable
Const Cpro_asmoke=10
Const Cpro_asparkle=11
Const Cpro_asupersparkle=12
Const Cpro_aresfade=13



;############################################ AI Modes

;Modes
Const ai_idle=50						;Idle
Const ai_move=51						;Move
Const ai_movel=52						;Move Left
Const ai_mover=53						;Move Right
Const ai_turnl=54						;Turn Left
Const ai_turnr=55						;Turn Right

Const ai_rise=60						;Rise (Birds)
Const ai_fall=61						;Fall (Birds)

Const ai_return=101						;Return to AI Center
Const ai_sreturn=102					;Return to AI Center (Strict!)
Const ai_attack=103						;Attack
Const ai_hunt=104						;Hunt Player

Const ai_movetarget=150					;Move to Target
Const ai_getfood=151					;Move to Target + Eat it!
Const ai_flee=152						;Run Away from Target

Const ai_sani=155						;Script Animation

;Mode Levels
Const ail_superior=149					;Superior Mode Level Starting at 149

;Attitudes
Const ai_passive=0
Const ai_shy=1
Const ai_agressive=2

;Signals
Const ai_food=2
Const ai_attract=1
Const ai_distract=-1


;############################################ Animations

Const ani_idle1=1
Const ani_idle2=2
Const ani_idle3=3
Const ani_move=10
Const ani_attack=11
Const ani_use=12
Const ani_die=13
Const ani_special=14


;############################################ Sounds

Const snd_spot=1
Const snd_attack=2
Const snd_idle1=3
Const snd_idle2=4
Const snd_idle3=5
Const snd_die=6
Const snd_flee=7
Const snd_move=8
