;######################################################################################################

;############################################ SYSTEM GFX

;### Buttons
Dim gfx_bigbutton(1)
gfx_bigbutton(0)=load_image("sys\gfx\bigbutton.bmp")
gfx_bigbutton(1)=load_image("sys\gfx\bigbutton_over.bmp")

Dim gfx_iconbutton(1)
gfx_iconbutton(0)=load_image("sys\gfx\iconbutton.bmp")
gfx_iconbutton(1)=load_image("sys\gfx\iconbutton_over.bmp")

;### Checkbox
Dim gfx_check(2)
gfx_check(0)=load_image("sys\gfx\check.bmp")
gfx_check(1)=load_image("sys\gfx\check_over.bmp")
gfx_check(2)=load_image("sys\gfx\check_sel.bmp")

;### Option
Dim gfx_opt(2)
gfx_opt(0)=load_image("sys\gfx\opt.bmp")
gfx_opt(1)=load_image("sys\gfx\opt_over.bmp")
gfx_opt(2)=load_image("sys\gfx\opt_sel.bmp")

;### Slider
Dim gfx_slider(3)
gfx_slider(0)=load_image("sys\gfx\slider.bmp")
gfx_slider(1)=load_image("sys\gfx\slider_over.bmp")
gfx_slider(2)=load_image("sys\gfx\slider_sel.bmp"):MidHandle gfx_slider(2)
gfx_slider(3)=load_image("sys\gfx\slider_sec.bmp"):MidHandle gfx_slider(3)

;### Input
Dim gfx_input(5)
gfx_input(0)=load_image("sys\gfx\input_left.bmp")
gfx_input(1)=load_image("sys\gfx\input_left_over.bmp")
gfx_input(2)=load_image("sys\gfx\input_middle.bmp")
gfx_input(3)=load_image("sys\gfx\input_middle_over.bmp")
gfx_input(4)=load_image("sys\gfx\input_right.bmp")
gfx_input(5)=load_image("sys\gfx\input_right_over.bmp")

;Scroll
Dim gfx_scroll(1)
gfx_scroll(0)=load_image("sys\gfx\scroll.bmp")
gfx_scroll(1)=load_image("sys\gfx\scroll_over.bmp")
Dim gfx_scroll_bar(2)
gfx_scroll_bar(0)=load_image("sys\gfx\scroll_bar_top.bmp")
gfx_scroll_bar(1)=load_image("sys\gfx\scroll_bar_middle.bmp")
gfx_scroll_bar(2)=load_image("sys\gfx\scroll_bar_bottom.bmp")
Global gfx_scrollspace=load_image("sys\gfx\edscrollspace.bmp")

bmpf_loadscreen(s$(1),8)

;### Icons
Global gfx_icons=load_animimage("sys\gfx\icons.bmp",32,32,100)
Global gfx_icons_passive
If  FileType("sys\gfx\icons_passive.bmp")<>1 Then
	gfx_icons_passive=CreateImage(32,32,100)
	tempimg=CreateImage(32*100,32)
	For i=0 To 99
		SetBuffer ImageBuffer(gfx_icons_passive,i)
		DrawBlock gfx_icons,0,0,i
		LockBuffer ImageBuffer(gfx_icons_passive,i)
		If 0=2
		For y=0 To 31
			j=1-j
			For x=0 To 31
				If j Then WritePixelFast x,y,$FF00FF
				j=1-j
			Next
		Next
		EndIf
		For x=0 To 31
			For y=0 To 31
				rgb=ReadPixelFast(x,y)
				rr=(rgb And $FF0000)/$10000
				rg=(rgb And $FF00)/$100
				rb=rgb And $FF
				If Not((rr=255 And rg=0 And rb=255)Or(rr=248 And rg=0 And rb=248)) Then
					rr=(rr+40)/2.5
					rg=(rg+20)/2.5
					rb=(rb+0)/2.5
					rgb=Int(rr)*$10000 + Int(rg)*$100 + Int(rb)
					WritePixelFast(x,y,rgb)
				EndIf
			Next
		Next
		UnlockBuffer ImageBuffer(gfx_icons_passive,i)
		SetBuffer ImageBuffer(tempimg)
		DrawBlock gfx_icons_passive,i*32,0,i
	Next
	SaveImage(tempimg,"sys\gfx\icons_passive.bmp")
	FreeImage tempimg
Else
	gfx_icons_passive=load_animimage("sys\gfx\icons_passive.bmp",32,32,100)
EndIf
MaskImage gfx_icons_passive,255,0,255
Global gfx_defaulticon=load_image("sys\gfx\defaulticon.bmp",0)

;### Borders
Dim gfx_border(4)
gfx_border(0)=load_image("sys\gfx\border_corn.bmp")
gfx_border(1)=load_image("sys\gfx\border_hori.bmp")
gfx_border(2)=load_image("sys\gfx\border_vert.bmp")
gfx_border(3)=CreateImage(set_scrx,16)
SetBuffer ImageBuffer(gfx_border(3))
TileBlock gfx_border(1)
MaskImage gfx_border(3),255,0,255
gfx_border(4)=CreateImage(16,set_scry)
SetBuffer ImageBuffer(gfx_border(4))
TileBlock gfx_border(2)
MaskImage gfx_border(4),255,0,255

;### Cursor
Dim gfx_cursor(6)
gfx_cursor(0)=load_image("sys\gfx\cursor.bmp")
gfx_cursor(1)=load_image("sys\gfx\cursor_height.bmp")
gfx_cursor(2)=load_image("sys\gfx\cursor_move.bmp")
gfx_cursor(3)=load_image("sys\gfx\cursor_paint.bmp")
gfx_cursor(4)=load_image("sys\gfx\cursor_rotate.bmp")
gfx_cursor(5)=load_image("sys\gfx\cursor_text.bmp"):HandleImage gfx_cursor(5),3,5
gfx_cursor(6)=load_image("sys\gfx\cursor_crosshair.bmp"):MidHandle gfx_cursor(6)

;### Background
Dim gfx_bg(5)
gfx_bg(0)=load_image("sys\gfx\woodback.bmp",0)
gfx_bg(1)=load_image("sys\gfx\woodback_dark.bmp",0)
Global gfx_paperback=load_image("sys\gfx\paperback.bmp")

;### Windows
Global gfx_sidebar=CreateImage(210,set_scry)
SetBuffer ImageBuffer(gfx_sidebar)
TileBlock gfx_bg(1)
Color 255,0,255:Rect 202,0,8,set_scry,1
DrawImage gfx_border(4),194,0
MaskImage gfx_sidebar,255,0,255

Global gfx_win=CreateImage(580,595)
SetBuffer ImageBuffer(gfx_win)
TileBlock gfx_bg(1)
Color 255,0,255:Rect 0,0,8,595,1
Rect 572,0,8,595,1:Rect 0,587,580,8,1
DrawImage gfx_border(3),0,579
DrawImage gfx_border(4),0,0
DrawImage gfx_border(4),564,0
DrawImage gfx_border(0),0,579
DrawImage gfx_border(0),564,579
MaskImage gfx_win,255,0,255

Global gfx_winbar=CreateImage(580,16)
SetBuffer ImageBuffer(gfx_winbar)
DrawBlock gfx_border(3),0,0
DrawImage gfx_border(0),0,0
DrawImage gfx_border(0),564,0
MaskImage gfx_winbar,255,0,255

bmpf_loadscreen(s$(1),10)

;### Bars
Global gfx_bars=load_animimage("sys\gfx\progress_small.bmp",100,5,2)
Global gfx_bar_hunger=load_animimage("sys\gfx\progress_hunger.bmp",100,5,2)
Global gfx_bar_thirst=load_animimage("sys\gfx\progress_thirst.bmp",100,5,2)
Global gfx_bar_exhaustion=load_animimage("sys\gfx\progress_exhaustion.bmp",100,5,2)

;### Interface
Global gfx_if_barback=load_image("sys\gfx\if_barback.bmp")
Global gfx_if_itemback=load_image("sys\gfx\if_itemback.bmp")
Global gfx_if_itemshade=load_image("sys\gfx\if_itemshade.bmp")
Global gfx_if_values=load_image("sys\gfx\if_values.bmp")
Global gfx_if_weapon=load_image("sys\gfx\if_weapon.bmp")
Global gfx_if_compass=load_image("sys\gfx\if_compass.bmp")

;### States
Global gfx_state=load_animimage("sys\gfx\state.bmp",21,21,9)
Global gfx_states=load_animimage("sys\gfx\states.bmp",21,21,30)

;### Arrows
Global gfx_arrows=load_animimage("sys\gfx\arrows.bmp",16,16,12):MidHandle gfx_arrows

;### Title
Global gfx_title=load_image("sys\gfx\title.bmp"):MidHandle gfx_title

;### Editor
Global gfx_ex=load_image("sys\gfx\editor_x.bmp"):MidHandle gfx_ex
Global gfx_ey=load_image("sys\gfx\editor_y.bmp"):MidHandle gfx_ey
Global gfx_ez=load_image("sys\gfx\editor_z.bmp"):MidHandle gfx_ez
Global gfx_esel=load_image("sys\gfx\editor_sel.bmp"):MidHandle gfx_esel

;### Tutor
Global gfx_tutor=load_image("sys\gfx\tutor.bmp")
HandleImage gfx_tutor,1,ImageHeight(gfx_tutor)

;### Structure Tex.
Global gfx_terstruc=load_texture("sys\gfx\terrainstructure.bmp",256+512)
ScaleTexture gfx_terstruc,2,2
TextureBlend gfx_terstruc,5
Global gfx_struc=load_texture("sys\gfx\structure.bmp",256+512)
ScaleTexture gfx_struc,2,2
TextureBlend gfx_struc,5

;### Terrain Dirt Tex.
Global gfx_terraindirt=load_texture("sys\gfx\terraindirt.bmp")
ScaleTexture gfx_terraindirt,0.5,0.5
TextureBlend gfx_terraindirt,5

;### Terrain Ground
Global gfx_tergrounddirt=load_texture("sys\gfx\terraindirt.bmp")
ScaleTexture gfx_tergrounddirt,30,30
TextureBlend gfx_tergrounddirt,5


;### Terrain Grassspread Stuff
Global gfx_grasspreadtex=load_texture("gfx\grasspread_a.png")
Global gfx_grasspread=load_mesh("gfx\grasspread.b3d"):HideEntity gfx_grasspread

;### Water
Global gfx_water=load_texture("gfx\water.jpg",256+512)
Global gfx_waterimg=load_image("gfx\water.jpg")
Global gfx_waterimg_custom=0

;### Weather
Global gfx_rain=load_texture("sys\gfx\rain_a.bmp")
ScaleTexture gfx_rain,1,3
Global gfx_snow=load_texture("sys\gfx\snow_a.bmp")


bmpf_loadscreen(s$(1),12)


;### Particles / Sprites
Dim gfx_p_flare(2)
For i=0 To 2
	gfx_p_flare(i)=load_texture("sprites\flare"+i+"_a.bmp")
Next
Dim gfx_p_bubbles(1)
For i=0 To 1
	gfx_p_bubbles(i)=load_texture("sprites\bubbles"+i+"_a.bmp")
Next
Dim gfx_p_roundwave(0)
For i=0 To 0
	gfx_p_roundwave(i)=load_texture("sprites\roundwave"+i+"_a.bmp")
Next
Dim gfx_p_wave(0)
For i=0 To 0
	gfx_p_wave(i)=load_texture("sprites\wave"+i+"_a.bmp")
Next
Dim gfx_p_smoke(1)
For i=0 To 1
	gfx_p_smoke(i)=load_texture("sprites\smoke"+i+"_a.bmp")
Next
Dim gfx_p_spark(0)
For i=0 To 0
	gfx_p_spark(i)=load_texture("sprites\spark"+i+"_a.bmp")
Next
Dim gfx_p_splatter(2)
For i=0 To 2
	gfx_p_splatter(i)=load_texture("sprites\splatter"+i+"_a.bmp")
Next
Dim gfx_p_woodfrag(4)
For i=0 To 4
	gfx_p_woodfrag(i)=load_texture("sprites\woodfrag"+i+"_a.bmp")
Next
Dim gfx_p_flames(0)
For i=0 To 0
	;gfx_p_flames(0)=LoadAnimTexture("sprites\flames"+i+"_a.png",2,64,64,0,4)
	gfx_p_flames(0)=load_texture("sprites\flames"+i+"_a.png")
Next
Global gfx_p_starflare=load_texture("sprites\starflare_a.bmp")
Dim gfx_p_puddle(0)
For i=0 To 0
	gfx_p_puddle(i)=load_texture("sprites\puddle"+i+"_a.bmp")
Next
Global gfx_p_shockwave=load_texture("sprites\shockwave_a.bmp")
Dim gfx_p_splash(0)
For i=0 To 0
	gfx_p_splash(i)=load_texture("sprites\splash"+i+"_a.bmp")
Next

Dim gfx_p_attack(1)
gfx_p_attack(0)=load_texture("sprites\attack1_a.bmp")
gfx_p_attack(1)=load_texture("sprites\attack2_a.bmp")

;### Moon
Global gfx_moon=load_texture("sprites\moon_a.png")



bmpf_loadscreen(s$(1),16)



;############################################ SYSTEM SFX

;Basic
Global sfx_click=load_res("sfx\click.wav",Cres_sound)
Global sfx_switch=load_res("sfx\switch.wav",Cres_sound)
Global sfx_menu=load_res("sfx\menu.wav",Cres_sound)
Global sfx_fail=load_res("sfx\fail.wav",Cres_sound)

Global sfx_build=load_res("sfx\build.wav",Cres_sound)
Global sfx_build_finish=load_res("sfx\build_finish.wav",Cres_sound)
Global sfx_eat=load_res("sfx\eat.wav",Cres_sound)
Global sfx_drink=load_res("sfx\drink.wav",Cres_sound)
Global sfx_swing_fast=load_res("sfx\swing_fast.wav",Cres_sound)
Global sfx_swing_slow=load_res("sfx\swing_slow.wav",Cres_sound)
Global sfx_dive=load_res("sfx\dive.wav",Cres_sound):If sfx_dive<>0 Then LoopSound sfx_dive
Global sfx_collect=load_res("sfx\collect.wav",Cres_sound)
Global sfx_pistol=load_res("sfx\pistol.wav",Cres_sound)
Global sfx_splash=load_res("sfx\splash.wav",Cres_sound)
Global sfx_splash2=load_res("sfx\splash2.wav",Cres_sound)
Global sfx_blubb=load_res("sfx\blubb.wav",Cres_sound)
Global sfx_sleep=load_res("sfx\sleep.wav",Cres_sound)
Global sfx_fire=load_res("sfx\fire.wav",Cres_sound)
Global sfx_gasp=load_res("sfx\gasp.wav",Cres_sound)
Global sfx_startdive=load_res("sfx\startdive.wav",Cres_sound)
Global sfx_drown=load_res("sfx\drown.wav",Cres_sound)
Global sfx_treefall=load_res("sfx\treefall.wav",Cres_sound)
Global sfx_fizzle=load_res("sfx\fizzle.wav",Cres_sound)
Global sfx_launch=load_res("sfx\launch.wav",Cres_sound)
Global sfx_fish=load_res("sfx\fish.wav",Cres_sound)
Global sfx_dig=load_res("sfx\dig.wav",Cres_sound)
Global sfx_rain=load_res("sfx\rain.wav",Cres_sound):If sfx_rain<>0 Then LoopSound sfx_rain
Global sfx_diary=load_res("sfx\diary.wav",Cres_music)
Dim sfx_step(3)
For i=0 To 3
	sfx_step(i)=load_res("sfx\step"+(i+1)+".wav",Cres_sound)
Next
Global sfx_waterstep=load_res("sfx\waterstep.wav",Cres_sound)
Dim sfx_woodstep(1)
sfx_woodstep(0)=load_res("sfx\woodstep1.wav",Cres_sound)
sfx_woodstep(1)=load_res("sfx\woodstep2.wav",Cres_sound)
Dim sfx_stonestep(1)
sfx_stonestep(0)=load_res("sfx\stonestep1.wav",Cres_sound)
sfx_stonestep(1)=load_res("sfx\stonestep2.wav",Cres_sound)
Global sfx_swim=load_res("sfx\swim.wav",Cres_sound)
Global sfx_fountain=load_res("sfx\fountain.wav",Cres_sound)
Dim sfx_thunder(2)
sfx_thunder(0)=load_res("sfx\thunder1.wav",Cres_sound)
sfx_thunder(1)=load_res("sfx\thunder2.wav",Cres_sound)
sfx_thunder(2)=load_res("sfx\thunder3.wav",Cres_sound)
Global sfx_pang=load_res("sfx\pang.wav",Cres_sound)
Dim sfx_wave(2)
sfx_wave(0)=load_res("sfx\wave1.wav",Cres_sound)
sfx_wave(1)=load_res("sfx\wave2.wav",Cres_sound)
sfx_wave(2)=load_res("sfx\wave3.wav",Cres_sound)

Dim sfx_crack(3)
For i=0 To 3
	sfx_crack(i)=load_res("sfx\crack"+(i+1)+".wav",Cres_sound)	
Next

Dim sfx_spark(3)
For i=0 To 3
	sfx_spark(i)=load_res("sfx\spark"+(i+1)+".wav",Cres_sound)
Next

Dim sfx_explode(4)
For i=0 To 4
	sfx_explode(i)=load_res("sfx\explode"+(i+1)+".wav",Cres_sound)
Next

Dim sfx_humanhit(4)
For i=0 To 4
	sfx_humanhit(i)=load_res("sfx\human_hit"+(i+1)+".wav",Cres_sound)
Next

Dim sfx_humandie(2)
For i=0 To 2
	sfx_humandie(i)=load_res("sfx\human_die"+(i+1)+".wav",Cres_sound)
Next

Dim sfx_mat_wood(1)
sfx_mat_wood(0)=load_res("sfx\mat_wood1.wav",Cres_sound)
sfx_mat_wood(1)=load_res("sfx\mat_wood2.wav",Cres_sound)

Dim sfx_mat_stone(0)
sfx_mat_stone(0)=load_res("sfx\mat_stone1.wav",Cres_sound)

Dim sfx_mat_dust(0)
sfx_mat_dust(0)=load_res("sfx\mat_dust1.wav",Cres_sound)

Dim sfx_mat_metal(0)
sfx_mat_metal(0)=load_res("sfx\mat_metal1.wav",Cres_sound)

Dim sfx_mat_flesh(4)
sfx_mat_flesh(0)=load_res("sfx\mat_flesh1.wav",Cres_sound)
sfx_mat_flesh(1)=load_res("sfx\mat_flesh2.wav",Cres_sound)
sfx_mat_flesh(2)=load_res("sfx\mat_flesh3.wav",Cres_sound)
sfx_mat_flesh(3)=load_res("sfx\mat_flesh4.wav",Cres_sound)
sfx_mat_flesh(4)=load_res("sfx\mat_flesh5.wav",Cres_sound)

Dim sfx_mat_fruit(1)
sfx_mat_fruit(0)=load_res("sfx\mat_fruit1.wav",Cres_sound)
sfx_mat_fruit(1)=load_res("sfx\mat_fruit2.wav",Cres_sound)

Dim sfx_mat_glass(1)
sfx_mat_glass(0)=load_res("sfx\mat_glass1.wav",Cres_sound)
sfx_mat_glass(1)=load_res("sfx\mat_glass2.wav",Cres_sound)

Dim sfx_mat_leaf(3)
sfx_mat_leaf(0)=load_res("sfx\mat_leaf1.wav",Cres_sound)
sfx_mat_leaf(1)=load_res("sfx\mat_leaf2.wav",Cres_sound)
sfx_mat_leaf(2)=load_res("sfx\mat_leaf3.wav",Cres_sound)
sfx_mat_leaf(3)=load_res("sfx\mat_leaf4.wav",Cres_sound)







;######################################################################################################

Function makedark(image,darkf#=4.5)
	If image=0 Then Return 0
	Local w=ImageHeight(image)
	Local h=ImageWidth(image)
	img=CreateImage(w,h)
	;If img=0 Then Return image
	w=w-1
	h=h-1
	SetBuffer ImageBuffer(img)
	DrawBlock image,0,0
	LockBuffer ImageBuffer(img)
	For x=0 To w
		For y=0 To h
			rgb=ReadPixelFast(x,y)
			rr=(rgb And $FF0000)/$10000
			rg=(rgb And $FF00)/$100
			rb=rgb And $FF
			If Not((rr=255 And rg=0 And rb=255)Or(rr=248 And rg=0 And rb=248)) Then
				rr=Float(rr)/darkf#
				rg=Float(rg)/darkf#
				rb=Float(rb)/darkf#
				rgb=Int(rr)*$10000 + Int(rg)*$100 + Int(rb)
				WritePixelFast(x,y,rgb)
			Else
				rgb=Int(255)*$1000000 + Int(255)*$10000 + Int(0)*$100 + Int(255)
				WritePixelFast(x,y,rgb)
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(img)
	;MaskImage img,255,0,255
	Return img
End Function





;### Buffer
SetBuffer BackBuffer()
