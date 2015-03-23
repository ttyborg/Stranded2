;############################################ SETUP

;Check and Set: Min. Resolution
If set_scrx<800 Or set_scry<600 Then					;Resolution smaller than allowed?
	set_scrx=800										;Set min. X Resolution
	set_scry=600										;Set min. Y Resolution
EndIf

;Check and Set: Color Depth
Select set_scrbit
	Case 16,32											;16,32: Is valid - do nothing
	Default set_scrbit=16								;Default: Not valid - set to 16
End Select

;Check Windowed
If set_scrwin>1 Then
	If Windowed3D()=0 Then
		con_add("ERROR: Unable to run Stranded II in windowed mode",Cbmpf_red)
		set_scrwin=0
	EndIf
EndIf

;Check and Set: Graphics
;Try the selected settings and 4 alternate settings until one works
;Reply with an error message when no setting worked!
If GfxMode3DExists(set_scrx,set_scry,set_scrbit) Then	;Selected Settings possible?
	Graphics3D set_scrx,set_scry,set_scrbit,set_scrwin	;Set!
Else
	If GfxMode3DExists(800,600,16) Then					;800x600x16?
		Graphics3D 800,600,16,set_scrwin				;Set!
		set_scrx=800
		set_scry=600
		set_scrbit=16
	ElseIf GfxMode3DExists(800,600,32) Then				;800x600x32?
		Graphics3D 800,600,32,set_scrwin				;Set!
		set_scrx=800
		set_scry=600
		set_scrbit=32
	ElseIf GfxMode3DExists(1024,768,16) Then			;1024x768x16?
		Graphics3D 1024,768,16,set_scrwin				;Set!
		set_scrx=1024
		set_scry=768
		set_scrbit=16
	ElseIf GfxMode3DExists(1024,768,32) Then			;1024x768x32?
		Graphics3D 1024,768,32,set_scrwin				;Set!
		set_scrx=1024
		set_scry=768
		set_scrbit=32
	Else
		RuntimeError("Unable to initiale Graphics"+Chr(13)+"Please check DirectX and GFX Drivers"+Chr(13)+Chr(13)+"Fehler beim Initialisieren der Grafik"+Chr(13)+"Bitte überprüfe DirectX und Grafiktreiber")
	EndIf
EndIf

;Title + Pointer
If set_moddir$="Stranded II" Then
	AppTitle("Stranded II")
Else
	AppTitle(set_moddir$+" - Stranded II Mod")
EndIf
HidePointer()

;Filters
TextureFilter("",8)			;Mipmapping in every Texture
TextureFilter("_a",2)		;_a = Alpha
TextureFilter("_m",4)		;_m = Masked
TextureFilter("_c",16+32)	;_c = Clamp u and v
TextureFilter("_u",16)		;_u = Clamp u
TextureFilter("_v",32)		;_v = Clamp v
TextureFilter("_h",256)		;_h = High Quality (Vram)
TextureFilter("_hc",512)	;_hc = Highcolor
TextureFilter("_sm",64)		;_sm = Spherical Reflection Map

;Camera + Listener
cam=CreateCamera()
CameraRange cam,1,3000
listener=CreateListener(cam,0.02,1,1)
camxz=CreatePivot()

;Light
env_light=CreateLight(1)
LightColor env_light,50,50,50
;LightColor env_light,255,50,50

;Random
SeedRnd(MilliSecs())

;Backbuffer
SetBuffer BackBuffer()

;Motionblur
mb_setup()

;Check Directories
If FileType("saves")<>2 Then CreateDir("saves")
If FileType("maps")<>2 Then CreateDir("maps")
