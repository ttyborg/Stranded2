;######################################################################################################

;############################################ Editor Initialization
Function editor_ini(menu=Cmenu_ed_new)
	;Clear
	e_clear()

	;Set Menu
	in_opt(0)=0
	in_opt(3)=0
	in_opt(4)=0
	m_menu=menu

	;Determine first Stuff for Lists
	;First Object
	For i=0 To Cobject_count
		If Dobject_name$(i)<>"" Then in_object_sel=i:Exit
	Next
	;First Unit
	For i=0 To Cunit_count
		If Dunit_name$(i)<>"" Then in_unit_sel=i:Exit
	Next
	;First Item
	For i=0 To Citem_count
		If Ditem_name$(i)<>"" Then in_item_sel=i:Exit
	Next
	;First Info
	For i=0 To Cinfo_count
		If Dinfo_name$(i)<>"" Then in_info_sel=i:Exit
	Next
	
	;Group Counts
	For g.Tgroup=Each Tgroup
		c=0
		Select g\class
			Case Cclass_object
				For i=0 To Cobject_count
					If Dobject_ined(i)=1 Then
						If Dobject_group(i)=g\group Then
							c=c+1
						EndIf
					EndIf
				Next
			Case Cclass_unit
				For i=0 To Cunit_count
					If Dunit_ined(i)=1 Then
						If Dunit_group(i)=g\group Then
							c=c+1
						EndIf
					EndIf
				Next
			Case Cclass_item
				For i=0 To Cobject_item
					If Ditem_ined(i)=1 Then
						If Ditem_group(i)=g\group Then
							c=c+1
						EndIf
					EndIf
				Next
			Case Cclass_info
				For i=0 To Cinfo_count
					If Dinfo_group(i)=g\group Then
						c=c+1
					EndIf
				Next
		End Select
		g\count=c
	Next
	
	;Script Commands
	editor_ini_sc()
	
	;Default Map
	in_edrandrot=1
	editor_defaultmapsettings()
	
	;Blank Map
	editor_genmap()
	editor_gencolormap()
	grass_map()
	grass_heightmap()
	in_edmap$=""
	editor_resetcam()
	
	;Activate Sidebar
	in_editor_sidebar=1
End Function


;############################################ Editor Initialization Script Commands
Function editor_ini_sc()
	;Script Commands
	For Tsc.Tsc=Each Tsc
		Delete Tsc
	Next
	Local in$,matchpos,match$
	Local stream=ReadFile(set_rootdir$+"core\scriptcommands.inf")
	If stream<>0 Then
		While Not Eof(stream)
			in$=ReadLine(stream)
			in$=Trim(in$)
			If in$<>"" Then
				split$(in$,"Ś",3)
				Tsc.Tsc=New Tsc
				Tsc\cmd$=splits$(0)
				Tsc\groups$=splits$(1)
				Tsc\params$=splits$(2)
			EndIf
		Wend
		CloseFile(stream)
	Else
		con_add("ERROR: Unable to load 'core\scriptcommands.inf'",Cbmpf_red)
	EndIf
	;Script Groups
	For Tsgr.Tsgr=Each Tsgr
		Delete Tsgr
	Next
	For Tsc.Tsc=Each Tsc
		split(Tsc\groups$,",",5)
		For i=0 To 5
			If splits$(i)<>"" Then
				gexists=0
				group$=Trim(splits$(i))
				For Tsgr.Tsgr=Each Tsgr
					If Tsgr\group$=group$ Then
						gexists=1
						Exit
					EndIf
				Next
				If gexists=0 Then
					Tsgr.Tsgr=New Tsgr
					Tsgr\group$=group$
				EndIf
			EndIf
		Next
	Next
End Function


;############################################ Editor Quit
Function editor_quit()
	;Script Commands
	For Tsc.Tsc=Each Tsc
		Delete Tsc
	Next
	;Script Groups
	For Tsgr.Tsgr=Each Tsgr
		Delete Tsgr
	Next
	;Dummy
	If in_editor_dummy<>0 Then
		FreeEntity in_editor_dummy
		in_editor_dummy=0
	EndIf
End Function


;############################################ Set Picking
Function editor_picking(mode)
	;Options
	Local objects=0
	Local units=0
	Local items=0
	Local infos=0
	Select mode
		Case Cclass_object objects=1
		Case Cclass_unit units=1
		Case Cclass_item items=1
		Case Cclass_info infos=1
	End Select
	;Objects
	For Tobject.Tobject=Each Tobject
		EntityPickMode Tobject\h,objects*2,1
	Next
	;Units
	For Tunit.Tunit=Each Tunit
		EntityPickMode Tunit\h,units*2,1
	Next
	;Items
	For Titem.Titem=Each Titem
		If Ditem_radius#(Titem\typ)=0. Then
			EntityPickMode Titem\h,items*2,1
		Else
			EntityPickMode Titem\h,items*1,1
		EndIf
	Next
	;Infos
	For Tinfo.Tinfo=Each Tinfo
		If Tinfo\h<>0 Then EntityPickMode Tinfo\h,infos*2,1
	Next
End Function


;############################################ Generate Map
Function editor_genmap(ctexsize=128)
	;Clear
	e_clear()
	;Get Size
	Local size
	Select in_opt(3)
		Case 0 size=16
		Case 1 size=32
		Case 3 size=64 ;Case 2 size=64
		Case 4 size=128 ;Case 3 size=128
		Case 6 size=256 ;Case 4 size=256
		Case 7 size=512 ;Case 5 size=512
		;Case 6 size=1024
		;Case 7 size=2048
		;Case 8 size=4096
		Default size=32
	End Select
	;Create Terrain Color Texture
	bmpf_loadscreen("Initialing")
	ter_tex_color=CreateTexture(ctexsize,ctexsize)
	;Initiale Terrain and Environment
	e_terrain(size,"generated")
	e_environment()
	
	;### Generate Hills
	bmpf_loadscreen("Generating Terrain")
	;Set Seaground
	Local seaground#=0.45
	For x=0 To size
		For z=0 To size
			ModifyTerrain ter,x,z,seaground#
		Next
	Next
	;Set Landground
	Local landground#=0.55
	Local hdif#=landground#-seaground#
	Local dist
	Local perc#
	Local c=(size/2)
	Local h=(size/2)-2
	Local coast=h
	Select size
		Case 16 coast=coast-1
		Case 32 coast=coast-3
		Case 64 coast=coast-5
		Default coast=coast-7
	End Select
	For x=2 To size-2
		For z=2 To size-2
			;Distance
			dist=Sqr((c-x)^2+(c-z)^2)
			;Land Cirlce?
			If dist<h Then
				;Coast Area? -> slope
				If dist>coast Then
					perc#=Float(dist-coast)/Float(h-coast)
					perc#=1.-perc#
					ModifyTerrain ter,x,z,seaground#+(perc#*hdif#)
				;Mainland
				Else		
					ModifyTerrain ter,x,z,landground#
				EndIf
			EndIf
		Next
	Next
	;General Island Transforming
	Local transc=Rand(15,25)
	Local transmin=h/5
	Local transmax=h/2
	For i=1 To transc
		angle#=Rnd(0,360)
		x=c+Sin(angle#)*coast
		z=c-Cos(angle#)*coast
		If Rand(1,2)=1 Then
			editor_genmap_hill(x,z,Rand(transmin,transmax),size,landground#)
		Else
			editor_genmap_hill(x,z,Rand(transmin,transmax),size,seaground#)
		EndIf
	Next
	e_terraindistort(0.05)
	
	;Modifications Based on Landscape Type
	Select in_opt(4)
		;Normal
		Case 0
			For x=1 To size-1
				For z=1 To size-1
					If Rand(3)=1 Then e_terrainrandom(x,z,0.03)
				Next
			Next
			For i=1 To 5:e_terrainsmooth():Next
		;Hilly/Desert/Snow
		Case 1,3,4
			For i=1 To 3:e_terrainsmooth():Next
			For x=1 To size-1
				For z=1 To size-1
					If Rand(3)=1 Then e_terrainrandom(x,z,0.05)
				Next
			Next
			For i=1 To 3:e_terrainsmooth():Next
		;Mountains
		Case 2
			For x=4 To size-4
				For z=4 To size-4
					ModifyTerrain ter,x,z,0.6
				Next
			Next
			For i=1 To 5:e_terrainsmooth():Next
			For x=1 To size-1
				For z=1 To size-1
					If Rand(3)=1 Then e_terrainrandom(x,z,0.25)
				Next
			Next
			For i=1 To 3:e_terrainsmooth():Next
		;Swamp
		Case 5
			For x=1 To size-1
				For z=1 To size-1
					If Rand(3)=1 Then e_terrainrandom(x,z,0.05)
				Next
			Next
			For x=1 To size-1
				For z=1 To size-1
					If Rand(5)=1 Then e_terrainrandom(x,z,0.2,-1)
				Next
			Next
			For i=1 To 2:e_terrainsmooth():Next
	End Select
End Function

Function editor_genmap_hill(cx,cz,size,tersize,h#)
	size=size/2
	If size<1 Then size=1
	For x=cx-size To cx+size
		For z=cz-size To cz+size
			If x>=2 And x<=tersize-2 Then
				If z>=2 And z<=tersize-2 Then
					dist=Sqr((cx-x)^2+(cz-z)^2)
					If dist<size Then
						ModifyTerrain ter,x,z,h#
					EndIf
				EndIf
			EndIf
		Next
	Next
End Function

;############################################ Generate Color Map
Function editor_gencolormap(updatecolorset=1)
	If m_section=Csection_editor Then bmpf_loadscreen("Generating Colormap")
	Local ctexsize=TextureHeight(ter_tex_color)
	Local size=ter_size
	If updatecolorset=1 Then
		;Set Colorset: Normal
		;e_tercol(230,223,145,0)
		;e_tercol(69,138,13,1)
		;e_tercol(160,172,125,2)
		e_tercol(game_tercol(0,0),game_tercol(1,0),game_tercol(2,0),0)
		e_tercol(game_tercol(3,0),game_tercol(4,0),game_tercol(5,0),1)
		e_tercol(game_tercol(6,0),game_tercol(7,0),game_tercol(8,0),2)
		Select in_opt(4)
			;Set Colorset: Desert
			Case 3
				;e_tercol(230,223,145,0)
				;e_tercol(254,243,118,1)
				;e_tercol(245,204,61,2)
				e_tercol(game_tercol(0,1),game_tercol(1,1),game_tercol(2,1),0)
				e_tercol(game_tercol(3,1),game_tercol(4,1),game_tercol(5,1),1)
				e_tercol(game_tercol(6,1),game_tercol(7,1),game_tercol(8,1),2)
			;Set Colorset: Snow
			Case 4
				;e_tercol(200,224,230,0)
				;e_tercol(255,255,255,1)
				;e_tercol(100,100,100,2)
				e_tercol(game_tercol(0,2),game_tercol(1,2),game_tercol(2,2),0)
				e_tercol(game_tercol(3,2),game_tercol(4,2),game_tercol(5,2),1)
				e_tercol(game_tercol(6,2),game_tercol(7,2),game_tercol(8,2),2)
			;Set Colorset: Swamp
			Case 5
				;e_tercol(159,206,0,0)
				;e_tercol(135,174,0,1)
				;e_tercol(70,104,72,2)
				e_tercol(game_tercol(0,3),game_tercol(1,3),game_tercol(2,3),0)
				e_tercol(game_tercol(3,3),game_tercol(4,3),game_tercol(5,3),1)
				e_tercol(game_tercol(6,3),game_tercol(7,3),game_tercol(8,3),2)
		End Select
	EndIf
	SetBuffer TextureBuffer(ter_tex_color)
	Local h#
	Local a#,r,g,b
	Local r2,g2,b2
	Local rgb
	Local texsize=ctexsize-1
	Local rx=-((size/2)*Cworld_size)
	Local ry=-((size/2)*Cworld_size)
	Local fac#=Float(Float(size*Cworld_size)/Float(ctexsize))
	r2=in_tercol(0,1):g2=in_tercol(1,1):b2=in_tercol(2,1)
	
	If m_section=Csection_editor Then UpdateWorld()
	
	For x=0 To texsize
		For y=0 To texsize
			;Get Height
			h#=Float(TerrainY(ter,rx+(x*fac#),0,ry+(y*fac#))/Float(Cworld_height))+0.5
			
			;Normal Height
			a#=1
			r=in_tercol(0,1)
			g=in_tercol(1,1)
			b=in_tercol(2,1)
			
			;Deep
			If h#<0.52 Then
				If h#>=0.5 Then a#=((h#-0.5)/0.02)
				r=in_tercol(0,0):g=in_tercol(1,0):b=in_tercol(2,0)
				
			;High
			ElseIf h#>0.55 Then
				If h#<=0.60 Then a#=1.-((h#-0.55)/0.05)
				r=in_tercol(0,2):g=in_tercol(1,2):b=in_tercol(2,2)
			EndIf
			
			;Random Color Modification
			r=r+Rand(-5,5)
			g=g+Rand(-5,5)
			b=b+Rand(-5,5)
			If r<0 Then
				r=0
			ElseIf r>255 Then
				r=255
			EndIf
			If g<0 Then
				g=0
			ElseIf g>255 Then
				g=255
			EndIf
			If b<0 Then
				b=0
			ElseIf b>255 Then
				b=255
			EndIf
			
			;Draw Normal
			If a#=1 Then
				rgb=255*$1000000 + r*$10000 + g*$100 + b
				WritePixel x,y,rgb
			;Draw Alphamixed
			Else
				editor_gencolormap_apm(r,g,b,r2,g2,b2,a#,x,y)
			EndIf
			
			;RED DEBUG PIXELS
			;If x Mod 4=0 And y Mod 4=0 Then
			;	WritePixelFast x,y,$FFFF0000		
			;	test=CreateCube()
			;	PositionEntity test,rx+(x*fac#),TerrainY(ter,rx+(x*fac#),0,ry+(y*fac#)),ry+(y*fac#)
			;EndIf
		Next
	Next
	
	SetBuffer BackBuffer()
End Function


;############################################ Draw Alpha Pixel Mixed for editor_gencolormap Function
Function editor_gencolormap_apm(r#,g#,b#,r2#,g2#,b2#,a#,x,y)
	Local nr=(r2#*a#)+(r#*(1.-a#))
	Local ng=(g2#*a#)+(g#*(1.-a#))
	Local nb=(b2#*a#)+(b#*(1.-a#))
	If nr<0 Then
		nr=0
	ElseIf nr>255 Then
		nr=255
	EndIf
	If ng<0 Then
		ng=0
	ElseIf ng>255 Then
		ng=255
	EndIf
	If nb<0 Then
		nb=0
	ElseIf nb>255 Then
		nb=255
	EndIf
	Local rgb=255*$1000000+nr*$10000+ng*$100+nb
	WritePixel x,y,rgb
End Function


;############################################ Set Dummy
Function editor_setdummy(class,typ)
	If in_editor_dummy<>0 Then FreeEntity in_editor_dummy
	Select class
		Case Cclass_object
			load_object_model(typ)
			in_editor_dummy=CopyEntity(Dobject_modelh(typ))
			ScaleEntity in_editor_dummy,Dobject_size#(typ,0),Dobject_size#(typ,1),Dobject_size#(typ,2)
		Case Cclass_unit
			in_editor_dummy=CopyEntity(Dunit_modelh(typ))
			ScaleEntity in_editor_dummy,Dunit_size#(typ,0),Dunit_size#(typ,1),Dunit_size#(typ,2)		
		Case Cclass_item
			load_item_model(typ)
			If Ditem_modelh(typ)<>0 Then
				in_editor_dummy=CopyEntity(Ditem_modelh(typ))
			Else
				in_editor_dummy=CopyEntity(game_defaultitemmodel)
			EndIf
			ScaleEntity in_editor_dummy,Ditem_size#(typ,0),Ditem_size#(typ,1),Ditem_size#(typ,2)
		Case Cclass_info
			in_editor_dummy=CreateCube()
			ScaleEntity in_editor_dummy,5,5,5
	End Select
	EntityFX in_editor_dummy,16
	EntityBlend in_editor_dummy,3
	EntityColor in_editor_dummy,0,255,0
End Function

;############################################ Set Mode
Function editor_setmode(mode,picking=1)
	Select mode
		;0 - Terrain
		Case 0
			If picking Then editor_picking(0)
		;1 - Objects
		Case Cclass_object
			If object_count=0 Then RuntimeError("No objects defined!")
			If picking Then editor_picking(Cclass_object)
			editor_setdummy(Cclass_object,in_object_sel)
		;2 - Units
		Case Cclass_unit
			If picking Then editor_picking(Cclass_unit)
			editor_setdummy(Cclass_unit,in_unit_sel)
		;3 - Items
		Case Cclass_item
			If item_count=0 Then RuntimeError("No items defined!")
			If picking Then editor_picking(Cclass_item)
			editor_setdummy(Cclass_item,in_item_sel)
		;4 - Infos
		Case Cclass_info
			If picking Then editor_picking(Cclass_info)
			editor_setdummy(Cclass_info,in_info_sel)
	End Select
	FlushKeys()
End Function

;############################################ Set Default Map Settings
Function editor_defaultmapsettings()
	;Time
	map_day=1
	map_hour=9
	map_minute=0
	map_freezetime=0
	;Sky
	map_skybox$="sky"
	;MP
	map_multiplayer=0
	;Climate
	map_climate=0
	;Music
	map_music$="amb_jungle.mp3"
	;Fog
	map_fog(0)=255-50
	map_fog(1)=255-45
	map_fog(2)=255-10
	map_fog(3)=0
	;Briefing
	map_briefing$=""
	;PW
	map_password$=""
End Function


;############################################ Fill Buffer with Texture
Function editor_textobuffer(tex)
	Local size=TextureHeight(tex)
	If in_editor_buffer<>0 Then FreeImage in_editor_buffer
	in_editor_buffer=CreateImage(size,size)
	LockBuffer TextureBuffer(tex)
	LockBuffer ImageBuffer(in_editor_buffer)
	For x=0 To size-1
		For y=0 To size-1
			CopyPixelFast x,y,TextureBuffer(tex),x,y,ImageBuffer(in_editor_buffer)
		Next
	Next
	UnlockBuffer TextureBuffer(tex)
	UnlockBuffer ImageBuffer(in_editor_buffer)
End Function


;############################################ Fill Texture with Buffer
Function editor_buffertotex(tex)
	Local size=TextureHeight(tex)
	LockBuffer TextureBuffer(tex)
	LockBuffer ImageBuffer(in_editor_buffer)
	For x=0 To size-1
		For y=0 To size-1
			CopyPixelFast x,y,ImageBuffer(in_editor_buffer),x,y,TextureBuffer(tex)
		Next
	Next
	UnlockBuffer TextureBuffer(tex)
	UnlockBuffer ImageBuffer(in_editor_buffer)
End Function


;############################################ Buffer to Heightmap
Function editor_buffertoheightmap()
	Local size=ImageHeight(in_editor_buffer)
	SetBuffer ImageBuffer(in_editor_buffer)
	LockBuffer ImageBuffer(in_editor_buffer)
	For x=0 To size-1
		For y=0 To size-1
			rgb=ReadPixelFast(x,y)
			r=(rgb And $FF0000)/$10000
			h#=Float(r)/255.
			ModifyTerrain(ter,x,y,h#)
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
	SetBuffer BackBuffer()
End Function


;############################################ Heightmap to Buffer
Function editor_heightmaptobuffer()
	Local size=TerrainSize(ter)
	If in_editor_buffer<>0 Then FreeImage in_editor_buffer
	in_editor_buffer=CreateImage(size,size)
	LockBuffer ImageBuffer(in_editor_buffer)
	For x=0 To size-1
		For y=0 To size-1
			h#=TerrainHeight(ter,x,y)
			r=h#*255.
			rgb=255*$1000000 + r*$10000 + r*$100 + r
			WritePixelFast x,y,rgb,ImageBuffer(in_editor_buffer)
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
End Function

;############################################ Grassmap to Buffer
Function editor_grassmaptobuffer()
	;Update Grassmap Colors
	SetBuffer TextureBuffer(ter_tex_color)
	LockBuffer TextureBuffer(ter_tex_color)
	For x=0 To grass_mapsize-1
		For y=0 To grass_mapsize-1
			rgb=ReadPixelFast(x,y)
			grass_rgb(x,y,0)=(rgb And $FF0000)/$10000
			grass_rgb(x,y,1)=(rgb And $FF00)/$100
			grass_rgb(x,y,2)=rgb And $FF
		Next
	Next
	UnlockBuffer TextureBuffer(ter_tex_color)
	;Save to Buffer
	Local size=grass_mapsize
	If in_editor_buffer<>0 Then FreeImage in_editor_buffer
	in_editor_buffer=CreateImage(size,size)
	SetBuffer ImageBuffer(in_editor_buffer)
	LockBuffer ImageBuffer(in_editor_buffer)
	For x=0 To size-1
		For y=0 To size-1
			If grass_rgb(x,y,3)=1 Then
				r=grass_rgb(x,y,0)
				g=grass_rgb(x,y,1)
				b=grass_rgb(x,y,2)
			Else
				r=255:g=0:b=255
			EndIf
			rgb=255*$1000000 + r*$10000 + g*$100 + b
			WritePixelFast x,y,rgb
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
	SetBuffer BackBuffer()
End Function


;############################################ Buffer Win
Function editor_bufferwin(x,y,w,h)
	Local mx,my
	Color 0,0,0
	Rect x-1,y-1,w+2,h+2,1
	Local size=ImageHeight(in_editor_buffer)
	;Zoomed or normal?
	If in_edzoom>1 Then
		;Setup Zoom Buffer
		If in_editor_bufferz=0 Then
			in_editor_bufferz=CreateImage(size*in_edzoom,size*in_edzoom)
		ElseIf ImageHeight(in_editor_bufferz)<>size*in_edzoom Then
			FreeImage in_editor_bufferz
			in_editor_bufferz=CreateImage(size*in_edzoom,size*in_edzoom)
		EndIf
		;Zoom!
		For zx=0 To size
			For i=0 To in_edzoom
				CopyRect zx,0,1,size,zx*in_edzoom+i,0,ImageBuffer(in_editor_buffer),ImageBuffer(in_editor_bufferz)
			Next
		Next
		For zy=size To 0 Step -1
			For i=0 To in_edzoom
				CopyRect 0,zy,size*in_edzoom,1,0,zy*in_edzoom+i,ImageBuffer(in_editor_bufferz),ImageBuffer(in_editor_bufferz)
			Next
		Next
		;Draw Zoomed
		DrawBlockRect in_editor_bufferz,x,y,in_editor_bufferx,in_editor_buffery,w,h
	Else
		;Draw Normal
		DrawBlockRect in_editor_buffer,x,y,in_editor_bufferx,in_editor_buffery,w,h
	EndIf
	;Cache
	Local brush=in_edbrush
	If in_edbrush<3 Then brush=3
	;In Area?
	If in_mx>=x Then
		If in_my>=y Then
			If in_mx<=x+w Then
				If in_my<=y+h Then
					;Modify Brush
					If in_mzs#<>0 Then
						in_edbrush=in_edbrush+in_mzs#
						If in_edbrush>99 Then in_edbrush=99
						If in_edbrush<0 Then in_edbrush=0
					EndIf
					;Cache
					brush=in_edbrush
					If in_edbrush<3 Then brush=3
					;Scroll Around with Right Mouse Button
					If in_mdown(2) Then
						in_cursorf=2
						in_editor_bufferx=in_editor_bufferx+in_mxs
						in_editor_buffery=in_editor_buffery-in_mys
					Else
						in_cursorf=-1
						Color 255,255,255
						Oval in_mx-((brush/2)*in_edzoom),in_my-((brush/2)*in_edzoom),brush*in_edzoom,brush*in_edzoom,0
					EndIf
					
					;Stuff
					mx=((in_mx-x)+in_editor_bufferx)/in_edzoom
					my=((in_my-y)+in_editor_buffery)/in_edzoom
					Select m_menu
						;Heightmap
						Case Cmenu_ed_heightmap,Cmenu_ed_heightmap+1
							If ((in_mdown(1) And ((in_mxs<>0) Or (in_mys<>0)))Or in_mhit(1)) Then
								grey=Float(in_slider(0))*2.55
								Select in_edtool
									Case 0
										editor_bufferpaint(mx,my,brush,grey,grey,grey,0,in_slider(10))
									Case 1
										editor_bufferpaint(mx,my,brush,grey,grey,grey,1,in_slider(10))
									Case 2
										editor_buffergamma(mx,my,brush,1,Float(in_slider(10))/10.)
									Case 3
										editor_buffergamma(mx,my,brush,0,Float(in_slider(10))/10.)
									Case 4
										editor_buffergetcolor(mx,my)
								End Select
							EndIf
						;Colormap
						Case Cmenu_ed_colormap
							If ((in_mdown(1) And ((in_mxs<>0) Or (in_mys<>0)))Or in_mhit(1)) Then
								Select in_edtool
									Case 0
										editor_bufferpaint(mx,my,brush,in_col(0),in_col(1),in_col(2),0,in_slider(10))
									Case 1
										editor_bufferpaint(mx,my,brush,in_col(0),in_col(1),in_col(2),1,in_slider(10))
									Case 2
										editor_buffergamma(mx,my,brush,1,Float(in_slider(10))/10.)
									Case 3
										editor_buffergamma(mx,my,brush,0,Float(in_slider(10))/10.)
									Case 4
										editor_buffergetcolor(mx,my)
								End Select
							EndIf
						;Grassmap
						Case Cmenu_ed_grassmap
							If ((in_mdown(1) And ((in_mxs<>0) Or (in_mys<>0)))Or in_mhit(1)) Then
								editor_bufferpaintgrassmap(mx,my,brush)
							EndIf
					End Select
					
				EndIf
			EndIf
		EndIf
	EndIf
End Function

;############################################ Buffer Paint
Function editor_bufferpaint(x,y,size,r,g,b,spray=0,a#)
	Local bsize=ImageHeight(in_editor_buffer)
	SetBuffer ImageBuffer(in_editor_buffer)
	LockBuffer ImageBuffer(in_editor_buffer)
	Local rad=size/2
	Local sx=x-rad
	Local sy=y-rad
	Local ex=x+rad
	Local ey=y+rad
	Local rx=x
	Local ry=y
	Local rgb,r2,g2,b2
	Local dist#, perc#
	a#=a#/100.
	a#=1.-a#
	For x=sx To ex
		For y=sy To ey
			If x>=0 Then
				If y>=0 Then
					If x<bsize Then
						If y<bsize Then
							dist#=Sqr((x-rx)^2+(y-ry)^2)
							If dist#<(rad) Then
								If (spray=0) Or Rand(10)=1 Then
									perc#=(dist#/Float(rad))
									If perc#<a# Then perc#=a#
									If perc#<0. Then perc#=0.
									If perc#>1. Then perc#=1.
									rgb=ReadPixelFast(x,y)
									r2=(rgb And $FF0000)/$10000
									g2=(rgb And $FF00)/$100
									b2=rgb And $FF
									editor_bufferpaint_apm(r,g,b,r2,g2,b2,perc#,x,y)
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
	SetBuffer BackBuffer()
End Function


;############################################ Buffer Getcolor
Function editor_buffergetcolor(x,y)
	SetBuffer ImageBuffer(in_editor_buffer)	
	GetColor x,y
	r=ColorRed()
	g=ColorGreen()
	b=ColorBlue()
	
	If m_menu=Cmenu_ed_colormap
		in_col(0)=r
		in_col(1)=g
		in_col(2)=b
	ElseIf m_menu=Cmenu_ed_heightmap
		in_slider(0)=Float(r)/2.55
	EndIf
	
	SetBuffer BackBuffer()
End Function

;############################################ Buffer Gamma
Function editor_buffergamma(x,y,size,mode,power#=10.)
	Local bsize=ImageHeight(in_editor_buffer)
	SetBuffer ImageBuffer(in_editor_buffer)
	LockBuffer ImageBuffer(in_editor_buffer)
	Local rad=size/2
	Local sx=x-rad
	Local sy=y-rad
	Local ex=x+rad
	Local ey=y+rad
	Local rx=x
	Local ry=y
	Local rgb,r,g,b
	Local dist#, perc#
	For x=sx To ex
		For y=sy To ey
			If x>=0 Then
				If y>=0 Then
					If x<bsize Then
						If y<bsize Then
							dist#=Sqr((x-rx)^2+(y-ry)^2)
							If dist#<(rad) Then
								If (spray=0) Or Rand(10)=1 Then
									perc#=(dist#/Float(rad))
									If perc#>1. Then perc#=1.
									perc#=1.-perc#
									rgb=ReadPixelFast(x,y)
									r=(rgb And $FF0000)/$10000
									g=(rgb And $FF00)/$100
									b=rgb And $FF
									If mode=1 Then
										r=r+power#*perc#
										g=g+power#*perc#
										b=b+power#*perc#
										If r>255 Then r=255
										If g>255 Then g=255
										If b>255 Then b=255
									Else
										r=r-power#*perc#
										g=g-power#*perc#
										b=b-power#*perc#
										If r<0 Then r=0
										If g<0 Then g=0
										If b<0 Then b=0
									EndIf
									rgb=255*$1000000+r*$10000+g*$100+b
									WritePixelFast x,y,rgb
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
	SetBuffer BackBuffer()
End Function


;############################################ Buffer Paint Grassmap
Function editor_bufferpaintgrassmap(x,y,size)
	Local bsize=ImageHeight(in_editor_buffer)
	SetBuffer ImageBuffer(in_editor_buffer)
	LockBuffer ImageBuffer(in_editor_buffer)
	Local rad=size/2
	Local sx=x-rad
	Local sy=y-rad
	Local ex=x+rad
	Local ey=y+rad
	Local rx=x
	Local ry=y
	Local rgb,r,g,b
	Local dist#
	For x=sx To ex
		For y=sy To ey
			If x>=0 Then
				If y>=0 Then
					If x<bsize Then
						If y<bsize Then
							dist#=Sqr((x-rx)^2+(y-ry)^2)
							If dist#<(rad) Then
								;Toolbased Change
								If in_edtool=0 Then
									grass_rgb(x,y,3)=in_edgrassmode
								Else
									If Rand(5)=1 Then grass_rgb(x,y,3)=in_edgrassmode
								EndIf
								
								;Draw
								If grass_rgb(x,y,3)=1 Then
									r=grass_rgb(x,y,0)
									g=grass_rgb(x,y,1)
									b=grass_rgb(x,y,2)
								Else
									r=255:g=0:b=255
								EndIf
								rgb=255*$1000000+r*$10000+g*$100+b
								WritePixelFast x,y,rgb
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(in_editor_buffer)
	SetBuffer BackBuffer()
End Function


;############################################ Draw Alpha Pixel Mixed for bufferpaint Function
Function editor_bufferpaint_apm(r#,g#,b#,r2#,g2#,b2#,a#,x,y)
	Local nr=(r2#*a#)+(r#*(1.-a#))
	Local ng=(g2#*a#)+(g#*(1.-a#))
	Local nb=(b2#*a#)+(b#*(1.-a#))
	Local rgb=255*$1000000+nr*$10000+ng*$100+nb
	WritePixelFast x,y,rgb
End Function


;############################################ Get Skybox out of Filename
Function editor_getskybox$(name$)
	Local s
	;Is Skybox File?
	s=Instr(name$,"_bk.")
	If s=0 Then s=Instr(name$,"_dn.")
	If s=0 Then s=Instr(name$,"_do.")
	If s=0 Then s=Instr(name$,"_fr.")
	If s=0 Then s=Instr(name$,"_lf.")
	If s=0 Then s=Instr(name$,"_rt.")
	If s=0 Then s=Instr(name$,"_up.")
	;No!
	If s=0 Then
		Return ""
	;Yep!
	Else
		Return Left(name$,s-1)
	EndIf
End Function

;############################################ Set to Terrain
Function editor_settoterrain()
	Local x#,z#
	
	;Objects
	For Tobject.Tobject=Each Tobject
		x#=EntityX(Tobject\h)
		z#=EntityZ(Tobject\h)
		PositionEntity Tobject\h,x#,e_tery(x#,z#),z#
	Next
	
	;Units
	For Tunit.Tunit=Each Tunit
		x#=EntityX(Tunit\h)
		z#=EntityZ(Tunit\h)
		PositionEntity Tunit\h,x#,e_tery(x#,z#),z#
	Next
	
	;Items
	For Titem.Titem=Each Titem
		If Titem\parent_id=0 Then
			x#=EntityX(Titem\h)
			z#=EntityZ(Titem\h)
			PositionEntity Titem\h,x#,e_tery(x#,z#),z#
		EndIf
	Next

End Function


;############################################ Editor Reset Cam
Function editor_resetcam()
	;Position
	Local x=-(ter_size*Cworld_size)/2
	Local z=-(ter_size*Cworld_size)/2
	Local y=e_tery(x,z)
	If y<0 Then
		y=150
	Else
		y=y+150
	EndIf
	PositionEntity cam,x,y,z
	;Angle
	Local anglepiv=CreatePivot()
	PositionEntity anglepiv,0,e_tery(0,0),0
	PointEntity cam,anglepiv
	FreeEntity anglepiv
End Function


;############################################ Editor Range
Function editor_range(rx#,ry#,rz#,range)
	If range=0 Then Return
	Color 0,Abs(255.*Cos(in_ca#)),0
	Local lx1,ly1,lx2,ly2
	Local p=CreatePivot()
	For i=(20.*(in_ca#/360.)) To 360 Step 20
		PositionEntity p,rx#,ry#,rz#
		RotateEntity p,0,i,0
		MoveEntity p,0,0,range
		CameraProject(cam,EntityX(p),EntityY(p),EntityZ(p))
		lx1=ProjectedX()
		If lx1>210 Then
			ly1=ProjectedY()
			If ly1>0 Then
				PositionEntity p,rx#,ry#,rz#
				RotateEntity p,0,i+5,0
				MoveEntity p,0,0,range
				CameraProject(cam,EntityX(p),EntityY(p),EntityZ(p))
				lx2=ProjectedX()
				If lx2>210 Then
					ly2=ProjectedY()
					If ly2>0 Then
						Line lx1,ly1,lx2,ly2
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	FreeEntity p
End Function


;############################################ Editor Paint Colormap
Function editor_paintcm(x3d#,z3d#,size,r,g,b)
	;Translate 3D to 2D Coords
	Local sc3d#=Cworld_size*ter_size
	Local sc2d#=TextureHeight(ter_tex_color)
	Local tfac#=sc3d#/sc2d#
	Local x=(x3d#+(sc3d#/2.))/tfac#
	Local y=(z3d#+(sc3d#/2.))/tfac#
	;con_add("paint @ "+x+"|"+y+"  - fac: "+tfac#)
	
	;Paint
	Local bsize=Int(sc2d#)
	SetBuffer TextureBuffer(ter_tex_color)
	LockBuffer TextureBuffer(ter_tex_color)
	Local rad=size/2
	Local sx=x-rad
	Local sy=y-rad
	Local ex=x+rad
	Local ey=y+rad
	Local rx=x
	Local ry=y
	Local rgb,r2,g2,b2
	Local dist#, perc#
	a#=0.
	For x=sx To ex
		For y=sy To ey
			If x>=0 Then
				If y>=0 Then
					If x<bsize Then
						If y<bsize Then
							dist#=Sqr((x-rx)^2+(y-ry)^2)
							If dist#<(rad) Then
								perc#=(dist#/Float(rad))
								If perc#<a# Then perc#=a#
								If perc#<0. Then perc#=0.
								If perc#>1. Then perc#=1.
								rgb=ReadPixelFast(x,y)
								r2=(rgb And $FF0000)/$10000
								g2=(rgb And $FF00)/$100
								b2=rgb And $FF
								editor_bufferpaint_apm(r,g,b,r2,g2,b2,perc#,x,y)
								;rgb=255*$1000000+r*$10000+g*$100+b
								;WritePixelFast x,y,rgb
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
	UnlockBuffer TextureBuffer(ter_tex_color)
	SetBuffer BackBuffer()	
End Function


;############################################ Editor Jump to Object
Function editor_jumpto(class,id,dist#=50)
	h=parent_h(class,id)
	If h<>0 Then
		PositionEntity cam,EntityX(h),EntityY(h),EntityZ(h)
		TranslateEntity cam,-dist#,dist#*2.,-dist#
		PointEntity cam,h
		For i=-10 To 10 Step 5
			p_add(EntityX(h),EntityY(h)+i,EntityZ(h),Cp_shockwave,30-(Abs(i)*2),0.5)
			EntityColor TCp\h,0,255,0
		Next
	EndIf
	m_menu=0
End Function


;############################################ Set FX
Function editor_setfx(h)
	For i=-10 To 10 Step 5
		p_add(EntityX(h),EntityY(h)+i,EntityZ(h),Cp_shockwave,30-(Abs(i)*2),0.5)
		EntityColor TCp\h,0,255,0
	Next
End Function


;############################################ Test Map
Function editor_testmap(forcecheckmap=0)
	;Change Menu to Save Stuff of current Menu
	If forcecheckmap=0 Then editor_setmenu(Cmenu_ed_check)
	
	;Debug on!
	set_debug=1
	
	;Forced Checkmap
	If forcecheckmap=1 Then
		m_section=Csection_game_sp
		set_debug_testmap=1
		set_debug_testmapfile$="editor\checkmap"
		load_map("maps\editor\checkmap.s2","")
		Return 1
	EndIf
	
	;Use Saved Map File
	If in_edmap$<>"" Then
		If gui_msgdecide(se$(206),0,sm$(2),sm$(1)) Then
			;Save
			If Instr(in_edmap$,":") Then
				save_map(in_edmap$+".s2","map",map_password$)
			Else
				save_map("maps\"+in_edmap$+".s2","map",map_password$)
			EndIf
			editor_quit()
			m_section=Csection_game_sp
			set_debug_testmap=1
			set_debug_testmapfile$=in_edmap$
			If Instr(in_edmap$,":") Then
				load_map(in_edmap$+".s2",map_password$)
			Else
				load_map("maps\"+in_edmap$+".s2",map_password$)
			EndIf
			Return 1
		EndIf
	EndIf
	
	;Use Checkmap
	If save_map("maps\editor\checkmap.s2","map","") Then
		editor_quit()
		m_section=Csection_game_sp
		set_debug_testmap=1
		set_debug_testmapfile$="editor\checkmap"
		load_map("maps\editor\checkmap.s2","")
		Return 1
	Else
		gui_msg(se$(205),Cbmpf_red)
	EndIf
End Function


;############################################ Get Skybox out of Filename
Function editor_list(class,group$)
	If class<>ed_current_class Or group$<>ed_current_group$ Then
		ed_current_class=class
		ed_current_group=group
		ed_current_c=0
		For Tgroupl.Tgroupl=Each Tgroupl
			Delete Tgroupl
		Next
		group$=group$+","
		Local tmp$
		Select class
			Case Cclass_object
				For i=0 To Cobject_count
					If Dobject_name$(i)<>"" Then
						If Dobject_ined(i)<>0 Then
							tmp$=Dobject_group(i)+","
							tmp$=Replace(tmp$," ","")
							If Instr(tmp$,group$)>0 Or group$="," Then
								Tgroupl=New Tgroupl
								Tgroupl\id=i
								ed_current_c=ed_current_c+1
							EndIf
						EndIf
					EndIf
				Next
			Case Cclass_unit
				For i=0 To Cunit_count
					If Dunit_name$(i)<>"" Then
						If Dunit_ined(i)<>0 Then
							tmp$=Dunit_group(i)+","
							tmp$=Replace(tmp$," ","")
							If Instr(tmp$,group$)>0 Or group$="," Then
								Tgroupl=New Tgroupl
								Tgroupl\id=i
								ed_current_c=ed_current_c+1
							EndIf
						EndIf
					EndIf
				Next
			Case Cclass_item
				For i=0 To Citem_count
					If Ditem_name$(i)<>"" Then
						If Ditem_ined(i)<>0 Then
							tmp$=Ditem_group(i)+","
							tmp$=Replace(tmp$," ","")
							If Instr(tmp$,group$)>0 Or group$="," Then
								Tgroupl=New Tgroupl
								Tgroupl\id=i
								ed_current_c=ed_current_c+1
							EndIf
						EndIf
					EndIf
				Next
			Case Cclass_info
				For i=0 To Cinfo_count
					If Dinfo_name$(i)<>"" Then
						tmp$=Dinfo_group(i)+","
						tmp$=Replace(tmp$," ","")
						If Instr(tmp$,group$)>0 Or group$="," Then
							Tgroupl=New Tgroupl
							Tgroupl\id=i
							ed_current_c=ed_current_c+1
						EndIf
					EndIf
				Next
		End Select
	EndIf
End Function
