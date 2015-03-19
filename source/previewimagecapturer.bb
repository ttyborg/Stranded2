;Preview Image Capturer
;-------------------------------- 
;Läd ein Model welches danach in Pose gebracht werden kann,
;um ein Thumbnail/Preview Bild zu erstellen.
;
;ACHTUNG:	Thumbnail wird unter dem Dateinamen + ".bmp" gespeichert
;			Achte darauf, dass diese Datei nicht existiert sonst wird sie überschrieben
;
;
;STEUERUNG: Linke Maustaste drücken + Maus bewegen: Model bewegen
;			Rechte Maustaste drücken + Maus bewegen: Model drehen
;			Mausrad: ein-/auszoomen
;			1: Obere linke Ecke des Capturebereiches setzen (an der Position der Maus)
;			2: Untere rechte Ecke des Capturebereiches setzen (an der Position der Maus)
;			Enter: Bild 'capturen'. Bild wird danach oben links angezeigt und sofort gespeichert.



;-------------------------------- File Name (ohne .b3d Extension)
file$="parrot"


;-------------------------------- File Path
path$="mods/Stranded II/gfx/"


;-------------------------------- Setup
Graphics3D 800,600,32,2

;Filters
TextureFilter("_a",2)
TextureFilter("_m",4)
TextureFilter("_c",16+32)
TextureFilter("_hc",512)
TextureFilter("_sm",64)

;Cam
cam=CreateCamera()
If FileType(path$+file$+".b3d")<>1 Then RuntimeError("NO FILE "+path$+file$+".b3d")
mesh=LoadMesh(path$+file$+".b3d")
CameraClsColor cam,0,0,0
PositionEntity cam,0,0,-50
SetBuffer BackBuffer()

;Light
ambl=255
AmbientLight ambl,ambl,ambl
sun=CreateLight(1)
PositionEntity sun,0,1000,0
;LightColor sun,155,155,155
LightColor sun,50,50,50
CameraClsColor  cam,0,0,0

;Image
Global x,y,w,h,image
image=CreateImage(32,32)



;-------------------------------- FX (Hier bei Bedarf das Model modifizieren!)
;EntityBlend mesh,3
;EntityShininess mesh,0.1
;EntityColor mesh,0,50,255
EntityFX mesh,16


;-------------------------------- Runtime
While Not KeyHit(1)

	mxs=MouseXSpeed()
	mys=MouseYSpeed()
	mzs=MouseZSpeed()
	md1=MouseDown(1)
	md2=MouseDown(2)
	
	If mzs<>0 Then TranslateEntity cam,0,0,mzs
	If md1 Then TranslateEntity cam,-mxs,mys,0
	If md2 Then RotateEntity mesh,EntityPitch(mesh),EntityYaw(mesh)+mxs,EntityRoll(mesh)+mys
	
	If KeyDown(2) Then x=MouseX():y=MouseY()
	If KeyDown(3) Then w=MouseX()-x:h=MouseY()-y
	
	If KeyHit(28) Then
		FreeImage(image)
		image=CreateImage(w-2,h-2)
		CopyRect(x+1,y+1,w-2,h-2,0,0,BackBuffer(),ImageBuffer(image))
		TFormFilter 0
		ResizeImage image,40,40
		;file$="coffeefruits"
		SaveImage(image,path$+file$+".bmp")
	EndIf
	
	If KeyDown(210) Then TurnEntity mesh,1,0,0
	If KeyDown(211) Then TurnEntity mesh,-1,0,0
	If KeyDown(199) Then TurnEntity mesh,0,1,0
	If KeyDown(207) Then TurnEntity mesh,0,-1,0
	If KeyDown(201) Then TurnEntity mesh,0,0,1
	If KeyDown(209) Then TurnEntity mesh,0,0,-1
	
	If KeyHit(4) Then ambl=ambl-50
	If KeyHit(5) Then ambl=ambl+50
	If ambl<50 Then ambl=50
	If ambl>255 Then ambl=255
	AmbientLight ambl,ambl,ambl
	
	RenderWorld()
	Rect x,y,w,h,0
	Rect 0,0,41,41,1
	DrawImage image,0,0
	Flip
	
Wend
