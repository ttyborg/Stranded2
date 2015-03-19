;######################################################################################################

;############################################ Initiale Grass Map
;this function creates grass meshes and saves their handles in the grass_h array
;furthermore it creates a color map (grass_rgb) array out of the terrain color texture
Function grass_map()
	;Delete old Grass
	grass_free()
	;Setup Properties by Settings (set_grass)
	Local fade_start
	Local alpha#
	Select set_grass
		Case 0 grass_c=0															
		Case 1 grass_c=10:grass_dist=23:fade_start=grass_dist*1:alpha=0.45
		Case 2 grass_c=20:grass_dist=22:fade_start=grass_dist*2:alpha=0.43
		Case 3 grass_c=40:grass_dist=21:fade_start=grass_dist*3:alpha=0.41
		Case 4 grass_c=80:grass_dist=20:fade_start=grass_dist*5:alpha=0.39
	End Select
	;Fill Array with Meshes
	Dim grass_h(grass_c,grass_c)
	For x=0 To grass_c
		For y=0 To grass_c
			grass_h(x,y)=CopyEntity(gfx_grasspread)
			ShowEntity grass_h(x,y)
			EntityFX grass_h(x,y),16
			EntityColor grass_h(x,y),0,200,0
			EntityAutoFade grass_h(x,y),fade_start,grass_dist*(grass_c/2)
			EntityAlpha grass_h(x,y),alpha
			EntityTexture grass_h(x,y),gfx_grasspreadtex
			HideEntity grass_h(x,y)
		Next
	Next
	;Generate Colormap Array (grass_rgb) out of the Terrain Color Texture (ter_tex_color)
	grass_mapsize=TextureHeight(ter_tex_color)
	Dim grass_rgb(grass_mapsize,grass_mapsize,3)
	SetBuffer TextureBuffer(ter_tex_color)
	LockBuffer TextureBuffer(ter_tex_color)
	For x=0 To grass_mapsize-1
		For y=0 To grass_mapsize-1
			rgb=ReadPixelFast(x,y)
			grass_rgb(x,y,0)=(rgb And $FF0000)/$10000
			grass_rgb(x,y,1)=(rgb And $FF00)/$100
			grass_rgb(x,y,2)=rgb And $FF
			grass_rgb(x,y,3)=1
		Next
	Next
	UnlockBuffer TextureBuffer(ter_tex_color)
	SetBuffer BackBuffer()
	;Calculate Value needed to get the Colormap Position
	grass_f#=Float(Float(Float(ter_size)/Float(grass_mapsize))*Float(Cworld_size))
	;Perform spread
	grass_x=-2147483648
	grass_y=-2147483648
	grass_yaw=-2147483648
	grass_spread()
End Function


;############################################ Deactivate Grass by Heightmap
Function grass_heightmap()
	Local size=ter_size
	Local ctexsize=TextureHeight(ter_tex_color)
	Local texsize=ctexsize-1
	Local rx=-((size/2)*Cworld_size)
	Local ry=-((size/2)*Cworld_size)
	Local fac#=Float(Float(size*Cworld_size)/Float(ctexsize))
	For x=0 To texsize
		For y=0 To texsize
			;Reset
			grass_rgb(x,y,3)=1
		
			;Get Height
			h#=Float(TerrainY(ter,rx+(x*fac#),0,ry+(y*fac#))/Float(Cworld_height))+0.5
			
			;Deep Disable
			If h#<0.54 Then
				If h#<0.52 Then
					grass_rgb(x,y,3)=0
				Else
					If Rand(3)<>1 Then grass_rgb(x,y,3)=0
				EndIf
			;Mountain Disable
			ElseIf h#>0.57 Then
				If Rand(10)<>1 Then grass_rgb(x,y,3)=0
			EndIf
		Next
	Next
End Function


;############################################ Spread Grass
;this function places and paints grass meshes arround the cam position
Function grass_spread()
	;Grass Wind Tex. Rotation
	grass_wind#=(grass_wind#+2.*f#) Mod 360
	RotateTexture gfx_grasspreadtex,0+Sin(grass_wind#)*1.5
	;Grass Activated?
	If set_grass>0 Then
		Local half=-(grass_c/2)
		Local camx=EntityX(cam)
		Local camz=EntityZ(cam)
		Local terhalf=(ter_size/2)*Cworld_size
		;Update necessary? (position changed)
		;1 - Gras Position Update (caused by cam movement)
		;2 - Gras Visibility Update (caused by cam rotation)
		Local update=0
		;X Change?
		If grass_x<>((Int(camx)/grass_dist)+half)*grass_dist Then
			update=1
		;Y Change?
		ElseIf grass_y<>((Int(camz)/grass_dist)+half)*grass_dist Then
			update=1
		;Yaw Change?
		ElseIf Abs(EntityYaw(cam)-grass_yaw)>0 Then
			update=2
		EndIf
		;Instant Update
		If update=1 Then
			grass_x=((Int(camx)/grass_dist)+half)*grass_dist
			grass_y=((Int(camz)/grass_dist)+half)*grass_dist
			For x=0 To grass_c
				For y=0 To grass_c
					;Position
					gx#=((Int(camx)/grass_dist)+half+x)*grass_dist
					gz#=((Int(camz)/grass_dist)+half+y)*grass_dist
					If Int(gx#) Mod 2=0 Then gx#=gx#+grass_dist/2
					If Int(gz#) Mod 2=0 Then gz#=gz#+grass_dist/2
					gy#=TerrainY(ter,gx#,0,gz#)
					PositionEntity grass_h(x,y),gx#,gy#,gz#
					gcx=((gx+terhalf)/grass_f#)
					gcy=((gz+terhalf)/grass_f#)
					;In Map?
					If gcx>=0 And gcx<=grass_mapsize And gcy>=0 And gcy<=grass_mapsize Then
						;Show?
						If grass_rgb(gcx,gcy,3)=1 Then
							;Assign Color
							EntityColor grass_h(x,y),grass_rgb(gcx,gcy,0),grass_rgb(gcx,gcy,1),grass_rgb(gcx,gcy,2)
							;In View?
							If EntityInView(grass_h(x,y),cam) Then
								ShowEntity(grass_h(x,y))
							Else
								HideEntity(grass_h(x,y))
							EndIf
						Else
							HideEntity(grass_h(x,y))
						EndIf
					Else
						HideEntity(grass_h(x,y))
					EndIf
				Next
			Next
		;Timed Update
		ElseIf update=2 Then
			If in_t100go Then
				grass_x=((Int(camx)/grass_dist)+half)*grass_dist
				grass_y=((Int(camz)/grass_dist)+half)*grass_dist
				For x=0 To grass_c
					For y=0 To grass_c
						;Position
						gx#=((Int(camx)/grass_dist)+half+x)*grass_dist
						gz#=((Int(camz)/grass_dist)+half+y)*grass_dist
						If Int(gx#) Mod 2=0 Then gx#=gx#+grass_dist/2
						If Int(gz#) Mod 2=0 Then gz#=gz#+grass_dist/2
						gcx=((gx+terhalf)/grass_f#)
						gcy=((gz+terhalf)/grass_f#)
						;In Map?
						If gcx>=0 And gcx<=grass_mapsize And gcy>=0 And gcy<=grass_mapsize Then
							;Show?
							If grass_rgb(gcx,gcy,3)=1 Then
								;In View?
								If EntityInView(grass_h(x,y),cam) Then
									ShowEntity(grass_h(x,y))
								Else
									HideEntity(grass_h(x,y))
								EndIf
							Else
								HideEntity(grass_h(x,y))
							EndIf
						Else
							HideEntity(grass_h(x,y))
						EndIf
					Next
				Next
			EndIf
		EndIf
	EndIf
End Function


;### Grass Free
;Deletes the grass meshes
Function grass_free()
	For x=0 To grass_c
		For y=0 To grass_c
			If grass_h(x,y)<>0 Then FreeEntity grass_h(x,y):grass_h(x,y)=0
		Next
	Next
	grass_x=-2147483648
	grass_y=-2147483648
End Function

;### Grass Colors
;Set Complete Grass to one Color
Function grass_colors(r,g,b)
	For x=0 To grass_mapsize-1
		For y=0 To grass_mapsize-1
			grass_rgb(x,y,0)=r
			grass_rgb(x,y,1)=g
			grass_rgb(x,y,2)=b
		Next
	Next
End Function
