;######################################################################################################
;
;Stranded II
;
;Šby Unreal Software 2004-2008
;
;WWW:		www.unnrealsoftware.de
;ICQ:		136125220
;Mail:		unrealsoftware@gmx.de
;
;######################################################################################################


;############################################ BASIC CONSTS

Const Cversion$="1.0.0.1"

;############################################ INCLUDES

;Includes
Include "includes\load_const.bb"			;Load Constants
Include "includes\load_vars.bb"				;Load Vars
Include "includes\load_commandline.bb"		;Load Commandline
Include "includes\load_functions.bb"		;Load Functions
Include "includes\load_settings.bb"			;Load Settings
Include "includes\load_strings.bb"			;Load Strings
Include "includes\load_setup.bb"			;Load & Setup Graphics, Title, Camera, Random
Include "includes\load_bmpf.bb"				;Load Bitmapfonts
bmpf_loadscreen(s$(1),6)
Include "includes\load_media.bb"			;Load Media
bmpf_loadscreen("Loading Materials",18)
Include "includes\load_materials.bb"		;Load Materials
bmpf_loadscreen(s$(2),19)
Include "includes\load_keynames.bb"			;Load Keynames
Include "includes\load_states.bb"			;Load States
Include "includes\load_lightcycle.bb"		;Load Lightcycle
Include "includes\load_game.bb"				;Load Game
Include "includes\load_groups.bb"			;Load Groups

load_progress_get()
bmpf_loadscreen(s$(3),20)
Include "includes\load_objects.bb"
bmpf_loadscreen(s$(4),60)
Include "includes\load_units.bb"
bmpf_loadscreen(s$(5),80)
Include "includes\load_items.bb"
bmpf_loadscreen(s$(6),98)
Include "includes\load_infos.bb"
load_progress_set()

Include "includes\load_combinations.bb"		;Load Combinations
Include "includes\load_buildings.bb"		;Load Buildings

Include "includes\load_fps.bb"				;FPS Factor Data

Include "includes\handle_objects.bb"		;Object Functions
Include "includes\handle_units.bb"			;Unit Functions
Include "includes\handle_items.bb"			;Item Functions
Include "includes\handle_infos.bb"			;Info Functions
Include "includes\handle_states.bb"			;State Functions
Include "includes\handle_all.bb"			;Handle All

Include "includes\e_clear.bb"				;Engine Clear
Include "includes\e_environment.bb"			;Engine Environment
Include "includes\e_save_map.bb"			;Map Save
Include "includes\e_load_map.bb"			;Map Load

Include "includes\gui.bb"					;GUI
Include "includes\gui_tb.bb"				;GUI Textbox
Include "includes\input.bb"					;Input
Include "includes\debug.bb"					;Debug Outputs
Include "includes\functions.bb"				;Functions
Include "includes\ressources.bb"			;Ressources
Include "includes\parentchild.bb"			;Parent/Child Stuff
Include "includes\grasspread.bb"			;Grass Spread
Include "includes\motionblur.bb"			;Motionblur

Include "includes\menu.bb"					;Menu

Include "includes\parser.bb"				;Parser
Include "includes\parser_pre.bb"			;Parser Pre
Include "includes\parser_functions.bb"		;Parser Functions
Include "includes\parser_brace.bb"			;Parser Brace
Include "includes\parser_commands.bb"		;Parser Commands (Command Index)
Include "includes\parser_vars.bb"			;Parser Variables
Include "includes\parser_math.bb"			;Parser Math
Include "includes\parser_dialogue.bb"		;Parser Dialogue
Include "includes\parser_loop.bb"			;Parser Loop

Include "includes\editor.bb"				;Editor
Include "includes\editor_menus.bb"			;Editor Menus
Include "includes\editor_functions.bb"		;Editor Functions

Include "includes\game.bb"					;Game
Include "includes\game_functions.bb"		;Game Functions
Include "includes\game_build.bb"			;Game Build
Include "includes\game_input.bb"			;Game Input
Include "includes\game_input_vehicles.bb"	;Game Input for Vehicles
Include "includes\game_focus.bb"			;Game Focus
Include "includes\game_weapons.bb"			;Game Weapons
Include "includes\game_sequences.bb"		;Game Sequences
Include "includes\game_changeday.bb"		;Game Changeday

Include "includes\interface.bb"				;Interface
Include "includes\console.bb"				;Console
Include "includes\skills.bb"				;Skills

Include "includes\particles.bb"				;Particles
Include "includes\projectiles.bb"			;Projectiles
Include "includes\timer.bb"					;Timer

Include "includes\physics.bb"				;Physics

Include "includes\collisions.bb"			;Collisions

Include "includes\sfx.bb"					;SFX Stuff

Include "includes\unrealsoftware.bb"		;Unrealsoftware

Include "includes\cull.bb"					;Cull

Include "includes\ai_units.bb"				;AI/behaviour for Units
Include "includes\ai_signals.bb"			;AI Signals
Include "includes\unitpath.bb"				;Unit Paths

Include "includes\tcpcommands.bb"			;TCP Commands

Include "includes\udp.bb"					;UDP Library

Include "includes\mp_usgn.bb"				;Multiplayer USGN Stuff
Include "includes\mp_game.bb"				;Multiplayer Game Loop
Include "includes\mp_game_functions.bb"		;Multiplayer Game Functions

Include "includes\mods.bb"

Include "includes\randommap.bb"

Include "includes\speech.bb"

Include "includes\prepare.bb"


;############################################ PREPARE

prepare_cache_viewranges()

Global br#=1.0
If 1=0

For i=0 To Citem_count
	br=1
	If Ditem_icon$(i)<>"" Then brightness(Ditem_icon$(i),0,0,0,br)
Next

For i=0 To Cobject_count
	br=1
	If Dobject_icon$(i)<>"" Then brightness(Dobject_icon$(i),0,0,0,br)
Next
For i=0 To Cunit_count
	br=1
	If Dunit_icon$(i)<>"" Then brightness(Dunit_icon$(i),0,0,0,br)
Next
EndIf



;############################################ START

con_welcome()
unrealsoftware()




;######################################################################################################


;############################################ RUNTIME

menu_start()

While Not m_performquit
	
	loopspeedlimit=MilliSecs()
	
	
	;Stuff
	Select m_section
	
		Case Csection_menu
			
			menu()
	
		Case Csection_game_sp
			
			game()
	
		Case Csection_game_mp
		
			game_mp()
		
		Case Csection_editor
			
			editor()
					
	End Select


	;Everywhere
	gframe=gframe+1
	console()
	;While (MilliSecs()-loopspeedlimit)<19
	;	Delay 1
	;Wend
	Flip
	
Wend



;############################################ SHUTDOWN

e_clear()
EndGraphics()
End
