;############################################ Unreal Software

Function unrealsoftware()
	Local logotex=load_texture("sys\gfx\logo.bmp",256)
	Local logo=CreateSprite()
	EntityTexture logo,logotex
	Local a#=-0.3
	ScaleSprite logo,350,150
	PositionEntity logo,((set_scrx/2)*2)-set_scrx,-((set_scry/2)*2)+set_scry,set_scrx
	EntityBlend logo,3
	Local fade=0
	Local ms
	PositionEntity cam,0,0,0
	
	FlushKeys()
	FlushMouse()
	FlushJoy()
	
	Repeat
		
		ms=MilliSecs()		
		
		If fade=0 Then
			a#=a#+0.025
			If a#>2.5 Then fade=1
		Else
			a#=a#-0.025
			If a#<-0.5 Then Exit
		EndIf
		EntityAlpha logo,a#
		RenderWorld()
		
		Flip
				
		While MilliSecs()-ms<20
		 	Delay 1
		Wend		
		
		If KeyDown(31) Then
			a#=1.5 fade=0
			FlushKeys()
		ElseIf GetKey()<>0 Or GetMouse()<>0 Or GetJoy()<>0 Then
			Exit
		EndIf
	
	Forever
	
	FreeEntity logo
	FreeTexture logotex
End Function
