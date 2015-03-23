;############################################ PARTICLES

;### Particle Add (3D)
Function p_add(x#,y#,z#,typ,size#=1.,a#=1.)
	Tp.Tp=New Tp
	If typ<101 Then
		Tp\h=CreateSprite()
		PositionEntity Tp\h,x#,y#,z#
	EndIf
	Tp\typ=typ
	Tp\size#=size#
	Select typ
		
		;4 Attack
		Case Cp_attack
			If x#>0. Then
				EntityTexture Tp\h,gfx_p_attack(0)
				Tp\r#=1
				Tp\rot#=20 ;-70
			Else
				EntityTexture Tp\h,gfx_p_attack(1)
				Tp\r#=0
				Tp\rot#=-20 ;70
			EndIf
			RotateSprite Tp\h,Tp\rot#
			ScaleSprite Tp\h,size#,size#
			Local p=CreatePivot()
			RotateEntity p,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
			PositionEntity p,EntityX(cam),EntityY(cam),EntityZ(cam)
			MoveEntity p,0,0,10
			MoveEntity p,0,-7,0
			PositionEntity Tp\h,EntityX(p),EntityY(p),EntityZ(p)
			FreeEntity p
			EntityBlend Tp\h,3
			EntityAlpha Tp\h,a#
			Tp\a#=a#
			EntityOrder Tp\h,-5
			EntityParent Tp\h,cam
		
		;5 Debug
		Case Cp_debug
			Tp\a#=a#
		
		;10 Bubbles
		Case Cp_bubbles
			EntityTexture Tp\h,gfx_p_bubbles(Rand(0,1))
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			Tp\fy#=Rnd(0.3,1)
			Tp\a#=a#
		
		;11 Roundwave
		Case Cp_rwave
			EntityTexture Tp\h,gfx_p_roundwave(0)
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			SpriteViewMode Tp\h,2
			RotateSprite Tp\h,Rand(360)
			Tp\fy#=Rnd(0.05,0.1)
			Tp\a#=a#
			If g_dive=1 Then
				PositionEntity Tp\h,x#,-1,z#
				RotateEntity Tp\h,-90,0,-90
			Else
				PositionEntity Tp\h,x#,2,z#
				RotateEntity Tp\h,90,0,90
			EndIf
		
		;12 Splash
		Case Cp_splash
			EntityTexture Tp\h,gfx_p_splash(0)
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			SpriteViewMode Tp\h,4
			PositionEntity Tp\h,x#,y#+1,z#
			EntityAlpha Tp\h,a#
			Tp\x#=size#
			Tp\y#=size#
			Tp\a#=a#
			Tp\fx#=size#/15.
		
		;15 Wave
		Case Cp_wave
			PositionEntity Tp\h,x#,1,z#
			EntityTexture Tp\h,gfx_p_wave(0)
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			RotateEntity Tp\h,90,0,90
			SpriteViewMode Tp\h,2
			RotateSprite Tp\h,a#-90.
			Tp\fy#=a#+Rnd(-5,5)
			Tp\a#=Rnd(-0.1,0.01)
			EntityAlpha Tp\h,Tp\a#
			TranslateEntity Tp\h,Sin(Tp\fy#)*Rnd(40,50),0,-Cos(Tp\fy#)*Rnd(40,50)
			If Rand(10)=1 Then
				TranslateEntity Tp\h,Sin(Tp\fy#)*Rnd(20,30),0,-Cos(Tp\fy#)*Rnd(20,30)
				If Rand(3)=1 Then
					TranslateEntity Tp\h,Sin(Tp\fy#)*Rnd(20,30),0,-Cos(Tp\fy#)*Rnd(20,30)
				EndIf
			EndIf
			EntityAutoFade Tp\h,1000,1500
			Tp\fadein#=Rnd(0.7,1.)
		
		;19	Hover
		Case Cp_hover
			Select Rand(9)
				Case 0,1,2,3
					EntityTexture Tp\h,gfx_p_spark(0)
				Case 4,5,6,7,8
					EntityTexture Tp\h,gfx_p_splatter(Rand(0,2))
				Case 9
					EntityTexture Tp\h,gfx_p_woodfrag(Rand(0,4))
			End Select
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			EntityColor Tp\h,Rand(200,255),Rand(240,255),Rand(220,255)
			Tp\a#=0
			EntityAlpha Tp\h,0
			Tp\fadein#=Rnd(0.2,0.45)
			Tp\r#=Rnd(-5,5)
			Tp\fx#=Rnd(-0.5,0.5)
			Tp\fy#=Rnd(-0.5,0.5)
			Tp\fz#=Rnd(-0.5,0.5)
			
		;20 Smoke
		Case Cp_smoke
			EntityTexture Tp\h,gfx_p_smoke(Rand(0,1))
			ScaleSprite Tp\h,size#,size#
			Local c=Rand(100,150)
			EntityColor Tp\h,c,c,c
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			Tp\fx#=Rnd(0.05,0.1)
			Tp\fy#=Rnd(0.15,0.35)
			Tp\fz#=Rnd(-1.5,1.5)
			Tp\r#=0.007
			Tp\a#=0.
			Tp\fadein#=a#
			EntityAlpha Tp\h,0.
			
		;21 Spark
		Case Cp_spark
			EntityTexture Tp\h,gfx_p_spark(0)
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,255,Rand(50,255),0
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			Tp\fx#=Rnd(-2,2)
			Tp\fy#=Rnd(-2,2)
			Tp\fz#=Rnd(-2,2)
			Tp\a#=a#
			EntityBlend Tp\h,3
			EntityRadius Tp\h,2
			EntityType Tp\h,Cworld_unitcol
			
		;22 Splatter
		Case Cp_splatter
			EntityTexture Tp\h,gfx_p_splatter(Rand(0,2))
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,Rand(150,200),0,0
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			Tp\fx#=Rnd(-2,2)
			Tp\fy#=Rnd(-2,2)
			Tp\fz#=Rnd(-2,2)
			Tp\a#=a#
			
		;23 Subplatter
		Case Cp_subsplatter
			EntityTexture Tp\h,gfx_p_splatter(Rand(0,2))
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,Rand(150,200),0,0
			RotateSprite Tp\h,Rand(360)
			Tp\a#=a#
			
		;24 Wood
		Case Cp_wood
			EntityTexture Tp\h,gfx_p_woodfrag(Rand(0,4))
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,Rand(65,100),40,0
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			Tp\fx#=Rnd(-2,2)
			Tp\fy#=Rnd(-2,1)
			Tp\fz#=Rnd(-2,2)
			Tp\a#=a#
			EntityRadius Tp\h,2
			EntityType Tp\h,Cworld_unitcol
			Tp\fadein#=Rnd(-35,35)
			
		;25 Puddle
		Case Cp_puddle
			FreeEntity Tp\h
			
			r=Rand(360)			
			m=CreateMesh()
			PositionEntity m,x,0,z
			surf=CreateSurface(m)

			tx#=Sin(r Mod 360)*size
			tz#=Cos(r Mod 360)*size
			v1=AddVertex(surf,tx,e_tery(x+tx,z+tz)+1.5,tz,0,1)
			tx#=Sin((r+90) Mod 360)*size
			tz#=Cos((r+90) Mod 360)*size
			v2=AddVertex(surf,tx,e_tery(x+tx,z+tz)+1.5,tz,0,0)
			tx#=Sin((r+180) Mod 360)*size
			tz#=Cos((r+180) Mod 360)*size
			v3=AddVertex(surf,tx,e_tery(x+tx,z+tz)+1.5,tz,1,0)
			tx#=Sin((r+270) Mod 360)*size
			tz#=Cos((r+270) Mod 360)*size
			v4=AddVertex(surf,tx,e_tery(x+tx,z+tz)+1.5,tz,1,1)
					
			AddTriangle(surf,v3,v2,v1)
			AddTriangle(surf,v4,v1,v3)
			
			UpdateNormals m

			tp\h=m
			If Rand(2)=1 Then
				EntityTexture Tp\h,gfx_p_puddle(0)
			Else
				EntityTexture Tp\h,gfx_p_splatter(Rand(0,2))
			EndIf
			EntityColor Tp\h,Rand(100,160),0,0
			EntityFX Tp\h,16		
			Tp\a#=a#
			EntityAlpha Tp\h,a#
			
			
		;30 Flames
		Case Cp_flames
			TranslateEntity Tp\h,Rnd(-1.8,1.8),Rnd(-1.8,1.8),Rnd(-1.8,1.8)
			EntityTexture Tp\h,gfx_p_flames(0),0
			ScaleSprite Tp\h,size#,size#*1.7
			;SpriteViewMode Tp\h,4
			EntityBlend Tp\h,3
			Tp\fy#=size#*1.7
			Tp\a#=a#
			;Underwaterspawn
			If y#<0 Then
				FreeEntity Tp\h
				Delete Tp
				If Rand(3)=1 Then p_add(x#,y#,z#,Cp_smoke,Rnd(3,5),Rnd(0.3,0.5))
				If Rand(20)=1 Then p_add(x#,y#,z#,Cp_bubbles,Rnd(1,3))
			EndIf
			
		;35 Firespark
		Case Cp_firespark
			TranslateEntity Tp\h,Rnd(-2,2),Rnd(-2,2),Rnd(-2,2)
			EntityTexture Tp\h,gfx_p_spark(0)
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,255,Rand(50,255),0
			RotateSprite Tp\h,Rand(360)
			Tp\a#=a#
			Tp\fy#=Rnd(0.5,2)
			Tp\fx#=Rand(360)
			Tp\fz#=Rand(360)
			EntityBlend Tp\h,3
			
		;40 Risingflare
		Case Cp_risingflare
			EntityTexture Tp\h,gfx_p_flare(1)
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,255,0,0
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			EntityBlend Tp\h,3
			Tp\fx#=Rnd(0.05,0.1)
			Tp\fy#=Rnd(0.3,0.5)
			Tp\fz#=Rnd(-5,5)
			Tp\a#=a#
			
		;45 - Explode
		Case Cp_explode
			FreeEntity Tp\h
			Tp\h=CreateSphere()
			PositionEntity Tp\h,x#,y#,z#
			EntityTexture Tp\h,gfx_p_smoke(Rand(0,1))
			;ScaleSprite Tp\h,size#,size#
			ScaleEntity Tp\h,size#,size#,size#
			EntityColor Tp\h,255,Rand(50,255),0
			Tp\rot#=Rand(360)
			;RotateSprite Tp\h,Tp\rot#
			RotateEntity Tp\h,Rnd(360),Rnd(360),Rnd(360)
			EntityBlend Tp\h,3
			EntityAlpha Tp\h,a#
			Tp\a#=a#
			
		;46 - Shockwave
		Case Cp_shockwave
			EntityTexture Tp\h,gfx_p_shockwave
			ScaleSprite Tp\h,size#,size#
			EntityBlend Tp\h,3
			SpriteViewMode Tp\h,2
			RotateSprite Tp\h,Rand(360)
			Tp\fy#=Rnd(0.5,0.7)
			Tp\a#=a#
			RotateEntity Tp\h,90,0,90
			EntityColor Tp\h,255,Rand(50,200),0
			
		;50 - Starflare
		Case Cp_starflare
			EntityTexture Tp\h,gfx_p_starflare
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,Rand(50,150),150,0
			EntityBlend Tp\h,3
			Tp\rot#=Rand(360)
			RotateSprite Tp\h,Tp\rot#
			Tp\fx#=Rnd(0.02,0.06)
			Tp\fy#=Rnd(0.10,0.20)
			Tp\fz#=Rnd(-5,5)
			Tp\r#=Rnd(0.007,0.01)
			Tp\a#=a#
			
		;51 - Spawn
		Case Cp_spawn
			EntityTexture Tp\h,gfx_p_starflare
			ScaleSprite Tp\h,size#,size#*2.5
			EntityColor Tp\h,Rand(50,150),150,0
			EntityBlend Tp\h,3
			SpriteViewMode Tp\h,4
			Tp\fy#=size#*2.5
			Tp\a#=a#
			Tp\fz#=1.
			Tp\fx#=1.
			
		;60 - Impact
		Case Cp_impact
			EntityTexture Tp\h,gfx_p_flare(0)	
			ScaleSprite Tp\h,size#,size#
			EntityColor Tp\h,255,Rand(150,255),150
			EntityBlend Tp\h,3
			RotateSprite Tp\h,Rand(360)
			EntityAlpha Tp\h,a#
			Tp\a#=a#
						
		;70 - Rain
		Case Cp_rain
			Tp\a#=0.1
			EntityTexture Tp\h,gfx_rain
			ScaleSprite Tp\h,size,size*2
			EntityBlend Tp\h,3
			EntityAlpha Tp\h,a#
			PositionEntity Tp\h,x#,EntityY(cam)+Rand(100,150),z#
			Tp\fy#=Rnd(6,8)
			SpriteViewMode Tp\h,4
			RotateSprite Tp\h,Rnd(-3,3)
			EntityParent Tp\h,camxz
			
		;71 - Snow
		Case Cp_snow
			Tp\a#=0.1
			EntityTexture Tp\h,gfx_snow
			ScaleSprite Tp\h,size,size
			EntityBlend Tp\h,3
			EntityAlpha Tp\h,a#
			PositionEntity Tp\h,x#,EntityY(cam)+Rand(100,200),z#
			Tp\fy#=Rnd(3,4)
			;SpriteViewMode Tp\h,4
			RotateSprite Tp\h,Rnd(360)
			EntityParent Tp\h,camxz
		
		;98 - Fade
		Case Cp_fade
			ScaleSprite Tp\h,10,10
			EntityParent Tp\h,cam,0
			MoveEntity Tp\h,0,0,5
			EntityFX Tp\h,1
			EntityOrder Tp\h,-5
			Tp\fx#=size#
			Tp\a#=0.
			Tp\fadein#=a#
			EntityAlpha Tp\h,0.
				
		;99 - Sequence Fade
		Case Cp_seqfade
			ScaleSprite Tp\h,10,10
			EntityParent Tp\h,cam,0
			MoveEntity Tp\h,0,0,5
			EntityFX Tp\h,1
			EntityOrder Tp\h,-5
			
		;100 - Flash
		Case Cp_flash
			ScaleSprite Tp\h,10,10
			EntityParent Tp\h,cam,0
			MoveEntity Tp\h,0,0,5
			EntityFX Tp\h,1
			EntityOrder Tp\h,-5
			Tp\a#=a#
			Tp\fx#=size#
			
		;101 - Fadeout
		Case Cp_fadeout
			Tp\h=tmp_ph
			Tp\a#=y#
			Tp\fx#=z#
			
		;102 - Fall
		Case Cp_fall
			Tp\h=tmp_ph
			;Fall Directon
			Tp\fx#=1
			If Rand(0,1) Then Tp\fx=-1
			Tp\fz#=1
			If Rand(0,1) Then Tp\fz=-1
			
		;103 - Resfade
		Case Cp_resfade
			Tp\h=tmp_ph
			Tp\r#=y#
			Tp\g#=z#
			
		;104 - Stuck
		Case Cp_stuck
			Tp\h=tmp_ph
			Tp\a#=a#
			
	End Select
	
	TCp.Tp=Tp
End Function

;### Particle Add (2D)
Function p_add2d(x,y,typ,size#=0)
	;If set_2dfx=0 Then Return
	Tp.Tp=New Tp
	Tp\typ=typ
	Tp\x#=x
	Tp\y#=y
	Select typ
		;-1 Black Rect
		Case Cp2d_blackrect
			Tp\fx#=Rnd(-5,5)
			Tp\fy#=Rnd(5,-5)
			Tp\a#=50
		;-2 Item
		Case Cp2d_item
			Tp\x#=Tp\x#-20
			Tp\y#=Tp\y#-20
			Tp\fx#=Rnd(-10,10)
			Tp\fy#=Rnd(-5,-10)
			Tp\a#=Int(size#)
		;-3 Dot
		Case Cp2d_dot
			Tp\age=Int(size#)
			Tp\fx#=Rnd(-0.8,0.8)
			Tp\fy#=-Rnd(3.5,7)
			Tp\frame=Rand(0,1)
		;-4 Controlled Dot
		Case Cp2d_cdot
			;Created with p_combine Function
	End Select
	TCp.Tp=Tp
	Return 1
End Function


;### Particle Update
Function p_update()
	c=0
	ct=MilliSecs()
	For Tp.Tp=Each Tp
		c=c+1
				
		Select Tp\typ
			;############################## 3D
			
			;Attack
			Case Cp_attack
				If Tp\r#=1 Then
					Tp\rot#=Tp\rot#-(30.*f#)
				Else
					Tp\rot#=Tp\rot#+(30.*f#)
				EndIf
				RotateSprite Tp\h,Tp\rot#
				Tp\a#=Tp\a#-(0.075*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
			
			;5 Debug
			Case Cp_debug
				Tp\a#=Tp\a#-(0.03*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;10 Bubbles
			Case Cp_bubbles
				TranslateEntity Tp\h,0,(Tp\fy#*f#),0
				Tp\a#=Tp\a#-(0.01*f#)
				EntityAlpha Tp\h,Tp\a#
				If EntityY(Tp\h)>(-3) Or Tp\a#<0.0 Then
					If EntityY(Tp\h)>(-3) Then
						p_add(EntityX(Tp\h),1,EntityZ(Tp\h),Cp_rwave,Tp\size#*Rnd(1,2),0.4)
					EndIf
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;11 Roundwave
			Case Cp_rwave
				Tp\size#=Tp\size#+(Tp\fy#*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\size#
				Tp\a#=Tp\a#-(0.02*f#) 
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;12 Splash
			Case Cp_splash
				Tp\a#=Tp\a#-(0.05*f#) 
				If Tp\a#>0.6 Then
					Tp\x#=Tp\x#+((Tp\fx#)*f#)
					Tp\y#=Tp\y#+((Tp\fx#*2.)*f#)
				Else
					Tp\x#=Tp\x#+((Tp\fx#*2.)*f#)
					Tp\y#=Tp\y#-((Tp\fx#*2.)*f#)
				EndIf
				ScaleSprite Tp\h,Tp\x#,Tp\y#
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;15 Wave
			Case Cp_wave
				Tp\size#=Tp\size#+(0.1*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\size#
				If Tp\fx#=0 Then
					Tp\a#=Tp\a#+(0.01*f#) 
					If Tp\a#>Tp\fadein# Then 
						Tp\a#=Tp\fadein#
						Tp\fx#=1
					EndIf
				Else
					Tp\a#=Tp\a#-(0.04*f#)
				EndIf
				EntityAlpha Tp\h,Tp\a#
				TranslateEntity Tp\h,-Sin(Tp\fy#)*f#*0.4,0,Cos(Tp\fy#)*f#*0.4
				If Tp\fx#=1 Then
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				EndIf
			
			;19	Hover
			Case Cp_hover
				TranslateEntity Tp\h,(Tp\fx#*f#),(Tp\fy#*f#),(Tp\fz#*f#)
				Tp\rot#=Tp\rot#+(Tp\r#*f#)
				RotateSprite Tp\h,Tp\rot#
				If Tp\fadein#>0. Then
					Tp\a#=Tp\a#+(0.01*f#)
					EntityAlpha Tp\h,Tp\a#
					If Tp\a#>=Tp\fadein# Then
						Tp\a#=Tp\fadein#
						Tp\fadein#=0.
					EndIf
					If EntityY(Tp\h)>-10 Then
						PositionEntity Tp\h,EntityX(Tp\h),-10,EntityZ(Tp\h)
						Tp\fy#=0
					EndIf
				Else
					Tp\a#=Tp\a#-(0.002*f#)
					EntityAlpha Tp\h,Tp\a#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					ElseIf EntityY(Tp\h)>-10 Then
						PositionEntity Tp\h,EntityX(Tp\h),-10,EntityZ(Tp\h)
						Tp\fy#=0
					EndIf
				EndIf
				
			;20 Smoke
			Case Cp_smoke
				If Tp\fadein#>0. Then
					Tp\a#=Tp\a#+(0.034*f#)
					If Tp\a#>=Tp\fadein# Then
						Tp\a#=Tp\fadein#
						Tp\fadein#=0.
					EndIf
					EntityAlpha Tp\h,Tp\a#
					Tp\size#=Tp\size#+(Tp\fx#*f#)
					ScaleSprite Tp\h,Tp\size#,Tp\size#
					TranslateEntity Tp\h,0,(Tp\fy#*f#),0
					Tp\rot#=Tp\rot#+(Tp\fz#*f#)
					RotateSprite Tp\h,Tp\rot#
				Else
					Tp\size#=Tp\size#+(Tp\fx#*f#)
					ScaleSprite Tp\h,Tp\size#,Tp\size#
					TranslateEntity Tp\h,0,(Tp\fy#*f#),0
					Tp\a#=Tp\a#-(Tp\r#*f#)
					EntityAlpha Tp\h,Tp\a#
					Tp\rot#=Tp\rot#+(Tp\fz#*f#)
					RotateSprite Tp\h,Tp\rot#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				EndIf
				
			;21 Spark / 24 Wood
			Case Cp_spark,Cp_wood
				;Rotate
				Tp\rot=Tp\rot#+(Tp\fadein#*f#)
				RotateSprite Tp\h,Tp\rot#
				;Move
				Tp\fx#=Tp\fx#*0.96
				Tp\fz#=Tp\fz#*0.96
				TranslateEntity Tp\h,Float(Tp\fx#*(2.5*f#)),Float(Tp\fy#*(2.5*f#)),Float(Tp\fz#*(2.5*f#))
				;Fade
				Tp\a#=Tp\a#-(0.025*f#)
				EntityAlpha Tp\h,Tp\a#
				;Fall
				If CountCollisions(Tp\h)>0 Then
					Tp\fx#=0
					Tp\fy#=0
					Tp\fz#=0
					Tp\fadein#=0
				Else
					If e_tery(EntityX(Tp\h),EntityZ(Tp\h))>EntityY(Tp\h) Then
						PositionEntity Tp\h,EntityX(Tp\h),e_tery(EntityX(Tp\h),EntityZ(Tp\h)),EntityZ(Tp\h)
						Tp\fx#=0
						Tp\fy#=0
						Tp\fz#=0
						Tp\fadein#=0
					Else
						Tp\fy#=Tp\fy#-(0.3*f#)
					EndIf
				EndIf
				;Water / Free
				If EntityY(Tp\h)<0 Then
					p_add(EntityX(Tp\h),EntityY(Tp\h),EntityZ(Tp\h),20,Rnd(1,3),Rnd(0.5,0.9))
					p_add(EntityX(Tp\h),EntityY(Tp\h),EntityZ(Tp\h),10,Rnd(1,3),Rnd(0.5,0.9))
					Tp\a#=-1.
				EndIf
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;22 Splatter
			Case Cp_splatter
				Tp\fx#=Tp\fx#*0.96
				Tp\fy#=Tp\fy#-(0.3*f#)
				Tp\fz#=Tp\fz#*0.96
				;Surface
				If EntityY(Tp\h)>-2 Then
					Tp\a#=Tp\a#-(0.05*f#)
					TranslateEntity Tp\h,Float(Tp\fx#*(1.5*f#)),Float(Tp\fy#*(2.5*f#)),Float(Tp\fz#*(1.5*f#))
					;Spawn Subsplatter
					p_add(EntityX(Tp\h),EntityY(Tp\h),EntityZ(Tp\h),Cp_subsplatter,Rnd(2,3),Rnd(0.5,1))
					;Terrain
					If e_tery(EntityX(Tp\h),EntityZ(Tp\h))>=EntityY(Tp\h) Then
						p_add(EntityX(Tp\h),0,EntityZ(Tp\h),Cp_puddle,Rnd(4,10),Rnd(0.5,0.8))
						FreeEntity Tp\h
						Delete Tp
					Else
						EntityAlpha Tp\h,Tp\a#
						If Tp\a#<0.0 Then
							FreeEntity Tp\h
							Delete Tp
						EndIf		
					EndIf				
				;Water
				Else
					Tp\size#=Tp\size#+(0.6*f#)
					ScaleSprite Tp\h,Tp\size#,Tp\size#
					Tp\a#=Tp\a#-(0.01*f#)
					If Tp\a#>0.5 Then Tp\a#=0.5
					EntityAlpha Tp\h,Tp\a#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				EndIf
				
			;23 Subsplatter
			Case Cp_subsplatter
				Tp\a#=Tp\a#-(0.07*f#)
				If e_tery(EntityX(Tp\h),EntityZ(Tp\h)) >= EntityY(Tp\h) Then
					If Tp\frame<2 Then
						If Rand(3)=1 Then
							p_add(EntityX(Tp\h),0,EntityZ(Tp\h),Cp_puddle,Rnd(3,5),Rnd(0.3,0.5))
							If Tp\frame<>0 Then
								EntityColor TCp\h,Tp\r#,Tp\g#,Tp\b#
							EndIf
						EndIf
					Else
						p_add(EntityX(Tp\h),0,EntityZ(Tp\h),Cp_puddle,Rnd(4,10),Rnd(0.5,0.8))
						EntityColor TCp\h,Tp\r#,Tp\g#,Tp\b#
					EndIf
					FreeEntity Tp\h
					Delete Tp
				Else
					TranslateEntity Tp\h,0,-2.*f#,0
					EntityAlpha Tp\h,Tp\a#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				EndIf
				
			;25 Puddle
			Case Cp_puddle
				Tp\a#=Tp\a#-(0.005*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
			
			;30 Flames
			Case Cp_flames
				TranslateEntity Tp\h,0,0.5*f#,0
				Tp\size#=Tp\size#-(0.15*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\fy#
				Tp\a#=Tp\a#-(0.03*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Or Tp\size#<0.0 Then
					If Rand(3)=1 Then
						p_add(EntityX(Tp\h),EntityY(Tp\h),EntityZ(Tp\h),Cp_smoke,Rnd(3,5),Rnd(0.1,0.3))
						TCp\r#=0.002
						TCp\fx#=Rnd(0.1,0.2)
						TCp\fy#=Rnd(0.4,0.6)
					EndIf
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;35 Firespark
			Case Cp_firespark
				Tp\fx#=(Tp\fx#+10.*f#) Mod 360
				Tp\fz#=(Tp\fz#+10.*f#) Mod 360
				TranslateEntity Tp\h,Sin(Tp\fx#)*f#,(Tp\fy#*f#),Cos(Tp\fz#)*f#
				Tp\a#=Tp\a#-(0.02*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;40 Risingflare
			Case Cp_risingflare
				Tp\size#=Tp\size#+(Tp\fx#*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\size#
				TranslateEntity Tp\h,0,(Tp\fy#*f#),0
				Tp\a#=Tp\a#-(0.01*f#)
				EntityAlpha Tp\h,Tp\a#
				Tp\rot#=Tp\rot#+(Tp\fz#*f#)
				RotateSprite Tp\h,Tp\rot#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;45 - Explode
			Case Cp_explode
				Tp\size#=Tp\size#+(5.*f#)
				;ScaleSprite Tp\h,Tp\size#,Tp\size#
				ScaleEntity Tp\h,Tp\size#,Tp\size#,Tp\size#
				Tp\a#=Tp\a#-(0.05*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;46 - Shockwave
			Case Cp_shockwave
				Tp\size#=Tp\size#+(Tp\fy#*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\size#
				Tp\a#=Tp\a#-(0.04*f#) 
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;50 - Starflare
			Case Cp_starflare
				Tp\size#=Tp\size#-(Tp\fx#*f#)
				If Tp\size#>0. Then
					ScaleSprite Tp\h,Tp\size#,Tp\size#
					TranslateEntity Tp\h,0,(Tp\fy#*f#),0
					Tp\a#=Tp\a#-(Tp\r#*f#)
					EntityAlpha Tp\h,Tp\a#
					Tp\rot#=Tp\rot#+(Tp\fz#*f#)
					RotateSprite Tp\h,Tp\rot#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				Else
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;51 - Spawn
			Case Cp_spawn
				TranslateEntity Tp\h,0,(0.1*Tp\fx#)*f#*Tp\fz#,0
				Tp\size#=Tp\size#-((0.01*Tp\fx#)*f#*Tp\fz#)
				Tp\fy#=Tp\fy#+((1.8*Tp\fx#)*f#*Tp\fz#)
				ScaleSprite Tp\h,Tp\size#,Tp\fy#
				Tp\a#=Tp\a#-((0.02*Tp\fx#)*f#*Tp\fz#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\parentid>0 Then p_toparent(Tp)
				If Tp\a#<0.0 Or Tp\size#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
			
			;60 - Impact
			Case Cp_impact
				Tp\size#=Tp\size#+(1.5*f#)
				ScaleSprite Tp\h,Tp\size#,Tp\size#
				Tp\a#=Tp\a#-(0.05*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;70 - Rain
			Case Cp_rain
				Tp\a#=Tp\a#+(0.05*f#)
				EntityAlpha Tp\h,Tp\a#
				TranslateEntity Tp\h,0,-Tp\fy#*f#,0
				If EntityY(Tp\h)<0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				;ElseIf (e_tery(EntityX(Tp\h,1),EntityZ(Tp\h,1))-(Tp\size#))>EntityY(Tp\h) Then
					;FreeEntity Tp\h
					;Delete Tp
				;EndIf
				
			;71 - Snow
			Case Cp_snow
				Tp\a#=Tp\a#+(0.02*f#)
				EntityAlpha Tp\h,Tp\a#
				TranslateEntity Tp\h,0,-Tp\fy#*f#,0
				If EntityY(Tp\h)<0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				;ElseIf (e_tery(EntityX(Tp\h,1),EntityZ(Tp\h,1))-(Tp\size#/2.))>EntityY(Tp\h) Then
					;FreeEntity Tp\h
					;Delete Tp
				;EndIf
			
			;98 - Fade
			Case Cp_fade
				If Tp\fadein#>0. Then
					Tp\a#=Tp\a#+(Tp\fx#*f#)
					If Tp\a#>Tp\fadein# Then
						Tp\a#=Tp\fadein#
						Tp\fadein#=0.
					EndIf
					EntityAlpha Tp\h,Tp\a#
				Else
					Tp\a#=Tp\a#-(Tp\fx#*f#)
					EntityAlpha Tp\h,Tp\a#
					If Tp\a#<0.0 Then
						FreeEntity Tp\h
						Delete Tp
					EndIf
				EndIf
			
			;99 - Sequence Fade
			Case Cp_seqfade
				time=(MilliSecs()-seq_start)+1
				If time>Int(Tp\y#) Then
					FreeEntity Tp\h
					Delete Tp
				Else
					;Mode
					t1=time-Int(Tp\x#)
					t2=Int(Tp\y#)-Int(Tp\x#)
					Select Int(Tp\fx#)
						;0 - 1 - 0
						Case 0
							t3=t2/2
							If t1<=t3 Then
								p#=Float(Float(t1)/Float(t3))
								EntityAlpha Tp\h,p#
							Else
								p#=Float(Float(t1)/Float(t3))
								EntityAlpha Tp\h,1.-p#
							EndIf
						;0 - 1
						Case 1
							p#=Float(Float(t1)/Float(t2))
							EntityAlpha Tp\h,p#
						;1 - 0
						Case 2
							p#=Float(Float(t1)/Float(t2))
							EntityAlpha Tp\h,1.-p#
					End Select
					;Free
					If m_menu<>Cmenu_if_movie Then
						If m_section<>Csection_menu Then
							FreeEntity Tp\h
							Delete Tp
						EndIf
					EndIf
				EndIf
					
				
			;100 - Flash
			Case Cp_flash
				Tp\a#=Tp\a#-(Tp\fx#*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;101 - Fadeout
			Case Cp_fadeout
				;TurnEntity Tp\h,0,0,20.*f#
				Tp\a#=Tp\a#-(Tp\fx#*f#)
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;102 - Fall
			Case Cp_fall
				TranslateEntity Tp\h,0,-0.9,0
				TurnEntity Tp\h,0.55*Tp\fx#,0,0.27*Tp\fz#
				If (EntityY(Tp\h)+300)<e_tery(EntityX(Tp\h),EntityZ(Tp\h)) Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;103 - Resfade
			Case Cp_resfade
				Tp\a#=Tp\a#-(Tp\r#*f#)
				Tp\fx#=Tp\fx#+(Tp\g#*f#)
				Tp\fy#=Tp\fy#+(Tp\g#*f#)
				Tp\fz#=Tp\fz#+(Tp\g#*f#)
				EntityAlpha Tp\h,Tp\a#
				ScaleEntity Tp\h,Tp\fx#,Tp\fy#,Tp\fz#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				
			;104 - Stuck
			Case Cp_stuck
				Tp\a#=Tp\a#-(0.01*f#) 
				EntityAlpha Tp\h,Tp\a#
				If Tp\a#<0.0 Then
					FreeEntity Tp\h
					Delete Tp
				EndIf
				

			
			;############################## 2D
			
			;-1 Black Rect
			Case Cp2d_blackrect
				Color 0,0,0
				Rect Tp\x#,Tp\y#,Tp\a#,Tp\a#,1
				Tp\x#=Tp\x#+1.*f#
				Tp\y#=Tp\y#+1.*f#
				Tp\a#=Tp\a#-2.*f#
				If Int(Tp\a#)<=0 Then
					Delete Tp 
				EndIf
				
			;-2 Itemcollect
			Case Cp2d_item
				If Ditem_iconh(Int(Tp\a#))<>0 Then
					DrawImage Ditem_iconh(Int(Tp\a#)),Tp\x#,Tp\y#
				EndIf
				Tp\fy#=Tp\fy#+(1.*f#)
				Tp\fx#=Tp\fx#*0.96
				Tp\y#=Tp\y#+Float(Tp\fy#*(2.5*f#))
				Tp\x#=Tp\x#+Float(Tp\fx#*(2.5*f#))
				If Tp\y>set_scry Then Delete Tp
				
			;-3 Dot
			Case Cp2d_dot
				Tp\x#=Tp\x#+(Tp\fx#*f#)
				Tp\y#=Tp\y#+(Tp\fy#*f#)
				LockBuffer BackBuffer()
				If Tp\x>1 Then
					If Tp\y>1 Then
						If Tp\x<(set_scrx-2)
							If Tp\y<(set_scry-2)
								WritePixelFast Tp\x,Tp\y,Tp\age
								If Tp\frame Then
									WritePixelFast Tp\x+1,Tp\y,Tp\age
									WritePixelFast Tp\x,Tp\y+1,Tp\age
									WritePixelFast Tp\x+1,Tp\y+1,Tp\age
								EndIf
							Else
								Delete Tp
							EndIf
						Else
							Delete Tp
						EndIf
					Else
						Delete Tp
					EndIf
				Else
					Delete Tp
				EndIf
				UnlockBuffer BackBuffer()
				
			;-4 Controlled Dot
			Case Cp2d_cdot
				angle=ATan2(Tp\fx#-Tp\x#,Tp\y#-Tp\fy#)
				Tp\x#=Tp\x#+Sin(angle)*Tp\fz#*f#
				Tp\y#=Tp\y#-Cos(angle)*Tp\fz#*f#
				LockBuffer BackBuffer()
				If Tp\x>1 Then
					If Tp\y>1 Then
						If Tp\x<(set_scrx-2)
							If Tp\y<(set_scry-2)
								WritePixelFast Tp\x,Tp\y,Tp\age
								If Tp\frame Then
									WritePixelFast Tp\x+1,Tp\y,Tp\age
									WritePixelFast Tp\x,Tp\y+1,Tp\age
									WritePixelFast Tp\x+1,Tp\y+1,Tp\age
								EndIf
							Else
								Delete Tp
							EndIf
						Else
							Delete Tp
						EndIf
					Else
						Delete Tp
					EndIf
				Else
					Delete Tp
				EndIf
				UnlockBuffer BackBuffer()
				If Abs(Tp\x-Tp\fx)<10 Then
					If Abs(Tp\y-Tp\fy)<10 Then
						Delete Tp
					EndIf
				EndIf
				
				
		End Select
	Next
	;Debug Times
	If set_debug_rt Then
		set_debug_rt_particles$=(MilliSecs()-ct)+" ms Particles ("+c+")"
	EndIf
End Function


;### Particle Update 2D
Function p_update_2d()
	For Tp.Tp=Each Tp
		If Tp\typ<0 Then
			Select Tp\typ
				
				;-1 Black Rect
				Case Cp2d_blackrect
					Color 0,0,0
					Rect Tp\x#,Tp\y#,Tp\a#,Tp\a#,1
					Tp\x#=Tp\x#+1.*f#
					Tp\y#=Tp\y#+1.*f#
					Tp\a#=Tp\a#-2.*f#
					If Int(Tp\a#)<=0 Then
						Delete Tp 
					EndIf
					
				;-2 Itemcollect
				Case Cp2d_item
					If Ditem_iconh(Int(Tp\a#))<>0 Then
						DrawImage Ditem_iconh(Int(Tp\a#)),Tp\x#,Tp\y#
					EndIf
					Tp\fy#=Tp\fy#+(1.*f#)
					Tp\fx#=Tp\fx#*0.96
					Tp\y#=Tp\y#+Float(Tp\fy#*(2.5*f#))
					Tp\x#=Tp\x#+Float(Tp\fx#*(2.5*f#))
					If Tp\y>set_scry Then Delete Tp
					
				;-3 Dot
				Case Cp2d_dot
					Tp\x#=Tp\x#+(Tp\fx#*f#)
					Tp\y#=Tp\y#+(Tp\fy#*f#)
					LockBuffer BackBuffer()
					If Tp\x>1 Then
						If Tp\y>1 Then
							If Tp\x<(set_scrx-2)
								If Tp\y<(set_scry-2)
									WritePixelFast Tp\x,Tp\y,Tp\age
									If Tp\frame Then
										WritePixelFast Tp\x+1,Tp\y,Tp\age
										WritePixelFast Tp\x,Tp\y+1,Tp\age
										WritePixelFast Tp\x+1,Tp\y+1,Tp\age
									EndIf
								Else
									Delete Tp
								EndIf
							Else
								Delete Tp
							EndIf
						Else
							Delete Tp
						EndIf
					Else
						Delete Tp
					EndIf
					UnlockBuffer BackBuffer()
					
				;-4 Controlled Dot
				Case Cp2d_cdot
					angle=ATan2(Tp\fx#-Tp\x#,Tp\y#-Tp\fy#)
					Tp\x#=Tp\x#+Sin(angle)*Tp\fz#*f#
					Tp\y#=Tp\y#-Cos(angle)*Tp\fz#*f#
					LockBuffer BackBuffer()
					If Tp\x>1 Then
						If Tp\y>1 Then
							If Tp\x<(set_scrx-2)
								If Tp\y<(set_scry-2)
									WritePixelFast Tp\x,Tp\y,Tp\age
									If Tp\frame Then
										WritePixelFast Tp\x+1,Tp\y,Tp\age
										WritePixelFast Tp\x,Tp\y+1,Tp\age
										WritePixelFast Tp\x+1,Tp\y+1,Tp\age
									EndIf
								Else
									Delete Tp
								EndIf
							Else
								Delete Tp
							EndIf
						Else
							Delete Tp
						EndIf
					Else
						Delete Tp
					EndIf
					UnlockBuffer BackBuffer()
					If Abs(Tp\x-Tp\fx)<10 Then
						If Abs(Tp\y-Tp\fy)<10 Then
							Delete Tp
						EndIf
					EndIf
					
			End Select
		EndIf
	Next
End Function



;### Rectscreen
Function p_rectscreen()
	For Tp.Tp=Each Tp
		If Tp\typ=Cp2d_blackrect Then Delete Tp
	Next
	For x=0 To (set_scrx+50) Step 50
		For y=0 To (set_scry+50) Step 50
			p_add2d(x,y,Cp2d_blackrect)
		Next
	Next
End Function


;### Kill P2D
Function p_kill2d()
	For Tp.Tp=Each Tp
		If Tp\typ<0 Then
			Delete Tp
		EndIf
	Next
End Function


;### Pixelize Eat / Drink
Function p_pixelizeeat(rx,ry,img,er=0,eg=1,eb=0)
	Return 0
	Local x,y
	Local h,w
	Local r,g,b,rgb,f
	w=ImageWidth(img)-1
	h=ImageHeight(img)-1
	SetBuffer ImageBuffer(img)
	LockBuffer ImageBuffer(img)
	For x=0 To w Step 2
		For y=0 To h Step 2
			rgb=ReadPixelFast(x,y)
			r=(rgb And $FF0000)/$10000
			g=(rgb And $FF00)/$100
			b=rgb And $FF
			If (r+g+b)>3 Then
				f=100+(r+g+b)/3
				If f>255 Then f=255
				If er Then
					r=f
				Else
					r=0
				EndIf
				If eg Then
					g=f
				Else
					g=0
				EndIf
				If eb Then
					b=f
				Else
					b=0
				EndIf
				rgb=255*$1000000 + r*$10000 + g*$100 + b
				p_add2d(rx+x,ry+y,Cp2d_dot,rgb)
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(img)
	SetBuffer BackBuffer()
End Function


;### Explosion
Function p_explosion(x#,y#,z#,size#)
	;3D Explosion
	For i=1 To 3
		p_add(x#,y#,z#,Cp_explode,size#,Rnd(1,1.5))
	Next
	;Shockwave
	p_add(x#,y#+15,z#,Cp_shockwave,size#*3.)
	;Fire/Smoke Sprites
	For i=1 To 10
		p_add(x#+Rnd(-size#*5.,size#*5.),y#+Rnd(size#*3.),z#+Rnd(-size#*3.,size#*3.),Cp_smoke,Rnd(10,30),Rnd(0.9,1.5))
		EntityColor TCp\h,255,Rand(50,200),0
		EntityBlend TCp\h,3
		TCp\a#=TCp\fadein#
		TCp\fadein#=0
		If Rand(4)<>1 Then
			EntityAlpha TCp\h,TCp\a#
			p_add(x#+Rnd(-size#*3.,size#*3.),y#+Rnd(size#,size#*3.),z#+Rnd(-size#*3.,size#*3.),Cp_smoke,Rnd(10,30),Rnd(0.9,1.5))
		EndIf
	Next
	;SFX
	sfx_3d(x#,y#,z#,sfx_explode(Rand(0,3)))
End Function


;### Combine
Function p_combine(source,target)
	Return 0
	If p_itemposition(source) Then
		sx=tmp_x
		sy=tmp_y
		If p_itemposition(target) Then
			tx=tmp_x
			ty=tmp_y
			;Add
			For i=0 To 15
				Tp.Tp=New Tp
				Tp\typ=Cp2d_cdot
				Tp\x#=sx+10+Rand(20)
				Tp\y#=sy+10+Rand(20)
				Tp\fx#=tx+10+Rand(20)
				Tp\fy#=ty+10+Rand(20)
				Tp\frame=Rand(0,1)
				Tp\fz#=Rnd(2,7)
				Tp\age=255*$1000000 + 255*$10000 + Rand(150,255)*$100 + 0
			Next
		EndIf
	EndIf
End Function


;### Get Item Position in Inventory
Function p_itemposition(typ)
	x=0:y=0:j=0
	For Titem.Titem=Each Titem
		If Titem\parent_class=Cclass_unit Then
			If Titem\parent_id=g_player Then
				j=j+1
				If j>in_scr_scr Then
					If Titem\typ=typ Then
						tmp_x=236+x
						tmp_y=63+y
						Return 1
					EndIf
					x=x+45
					If x>=495 Then
						x=0
						y=y+45
						If y>=225 Then Exit
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	If y<225 Then
		tmp_x=236+x
		tmp_y=63+y
		Return 1
	Else
		Return 0
	EndIf
End Function


;### Lightning
Function p_lightning(rx#,ry#,rz#,steps=5,size#=5.)
	Local p=CreatePivot()
	PositionEntity p,rx#,ry#,rz#
	Local thickness#=0.3
	Local h,nh
	For i=1 To steps
		x1#=EntityX(p)
		y1#=EntityY(p)
		z1#=EntityZ(p)
		RotateEntity p,Rnd(360),Rnd(360),Rnd(360)
		MoveEntity p,0,0,Rnd(1.)*size#
		x2#=EntityX(p)
		y2#=EntityY(p)
		z2#=EntityZ(p)
		nh=meshline(x1#,y1#,z1#,x2#,y2#,z2#,thickness#)
		EntityBlend nh,3
		thickness#=thickness#*0.6
		
		;If i=1 Then
		;	h=nh
		;Else
		;	RotateMesh nh,EntityPitch(nh),EntityYaw(nh),EntityRoll(nh)
		;	PositionMesh nh,EntityX(nh),EntityY(nh),EntityZ(nh)
		;	PositionEntity nh,0,0,0
		;	AddMesh nh,h
		;	FreeEntity nh
		;EndIf
		
		tmp_ph=nh
		p_add(0,1.,0.2,Cp_fadeout)
	Next
	
	;tmp_ph=h
	;p_add(0,1.,0.2,Cp_fadeout)
	
	FreeEntity p
End Function


;### Projectile (Stuck)
Function p_projectile(oldh,typ,parent_class,parent_id)
	Local h=CopyEntity(Ditem_modelh(typ))
	;Adjust
	ScaleEntity h,Ditem_size#(typ,0),Ditem_size#(typ,1),Ditem_size#(typ,2)
	EntityColor h,Ditem_color(typ,0),Ditem_color(typ,1),Ditem_color(typ,2)
	If Ditem_fx(typ)<>0 Then EntityFX h,Ditem_fx(typ)
	If Ditem_autofade(typ)<>0 Then EntityAutoFade h,Float(Ditem_autofade(typ))*set_viewfac#,(Float(Ditem_autofade(typ))*set_viewfac#)+50
	EntityAlpha h,Ditem_alpha#(typ)
	If Ditem_shininess#(typ)>0.0 EntityShininess h,Ditem_shininess#(typ)
	If Ditem_blend(typ)<>0 Then EntityBlend h,Ditem_blend(typ)
	;Position \ Rotation
	PositionEntity h,EntityX(oldh,1),EntityY(oldh,1),EntityZ(oldh,1)
	RotateEntity h,EntityPitch(oldh,1),EntityYaw(oldh,1),EntityRoll(oldh,1)
	;Setup Particle
	tmp_ph=h
	p_add(0,0,0,Cp_stuck,1.,Ditem_alpha#(typ))
	TCp\age=parent_class
	TCp\timer=parent_id
End Function


;### Fragment
Function p_fragment(h)
	Local surfs=CountSurfaces(h)
	Local tris,j,v0,v1,v2
	Local surf
	Local verts
	For i=1 To surfs
		surf=GetSurface(h,i)
		tris=CountTriangles(surf)
		verts=CountVertices(surf)
		For j=1 To tris
			v0=TriangleVertex(surf,j,0)
			v1=TriangleVertex(surf,j,1)
			v2=TriangleVertex(surf,j,2)
			If v0<=verts And v1<=verts And v2<=verts Then
				con_add("addmesh")
				;Create New
				nh=CreateMesh()
				ns=CreateSurface(nh)
				nv0=AddVertex(ns,VertexNX(surf,v0),VertexNY(surf,v0),VertexNZ(surf,v0))
				nv1=AddVertex(ns,VertexNX(surf,v1),VertexNY(surf,v1),VertexNZ(surf,v1))
				nv2=AddVertex(ns,VertexNX(surf,v2),VertexNY(surf,v2),VertexNZ(surf,v2))
				AddTriangle(ns,nv0,nv1,nv2)
				UpdateNormals nh
				EntityColor nh,Rand(50),Rand(50),Rand(50)
				;tmp_ph=nh
				;p_add(0,1.,Rnd(0.05,0.2),Cp_fadeout)
				;PositionEntity nh,EntityX(h),EntityY(h),EntityZ(h)
			EndIf
		Next
	Next
End Function


;### Parent
Function p_parent(id,Tp.Tp)
	If con_unit(id) Then
		Tp\parentid=id
		Tp\parentx#=EntityX(Tp\h)-EntityX(TCunit\h)
		Tp\parentz#=EntityZ(Tp\h)-EntityZ(TCunit\h)
	EndIf
End Function

;### Settoparent
Function p_toparent(Tp.Tp)
	If con_unit(Tp\parentid) Then
		PositionEntity Tp\h,EntityX(TCunit\h)+Tp\parentx#,EntityY(Tp\h),EntityZ(TCunit\h)+Tp\parentz#
	EndIf
End Function
