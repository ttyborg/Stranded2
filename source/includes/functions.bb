;############################################ FUNCTIONS


;### Pickedheight
Function pheight#()
	Return e_tery(in_px#,in_pz#)
End Function


;### Draw Axes
Function drawaxes(h,size#=20)
	Local piv=CreatePivot()
	Local rx#=EntityX(h)
	Local ry#=EntityY(h)
	Local rz#=EntityZ(h)
	CameraProject(cam,rx#,ry#,rz#)
	Local rx2d=ProjectedX()
	Local ry2d=ProjectedY()
	RotateEntity piv,EntityPitch(h,1),EntityYaw(h,1),EntityRoll(h,1)
	;Red Pitch
	Color 255,0,0
	PositionEntity piv,rx#,ry#,rz#
	MoveEntity piv,size#,0,0
	CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
	Line rx2d,ry2d,ProjectedX(),ProjectedY()
	;Blue Roll
	Color 0,0,255
	PositionEntity piv,rx#,ry#,rz#
	MoveEntity piv,0,0,size#
	CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
	Line rx2d,ry2d,ProjectedX(),ProjectedY()
	;Green Yaw
	Color 0,255,0
	PositionEntity piv,rx#,ry#,rz#
	MoveEntity piv,0,size#,0
	CameraProject(cam,EntityX(piv),EntityY(piv),EntityZ(piv))
	Line rx2d,ry2d,ProjectedX(),ProjectedY()
End Function


;### Count Instr
Function cinstr(txt$,sign$)
	Local i,l
	Local c=0
	l=Len(txt$)
	For i=1 To c
		If Mid(txt$,i,1)=sign$ Then
			c=c+1
		EndIf
	Next
	Return c
End Function

;### Split String
Function split$(txt$, sign$=",",limit=10)
	Local num, p1, p2
	num=0
	If limit<=0 Then limit=Len(txt$)
	Dim splits$(limit)
	For num=0 To limit
		p1=p2
		p2=Instr(txt$, sign$, p1+1)
		splits$(num)=Mid(txt$, p1+1, p2-(p1+1))
		If p2=0 Then Exit
	Next
	Return limit
End Function


;### Play
Function play(sound)
	Local chan=PlaySound(sound)
	ChannelVolume chan,set_fxvolume#
End Function



;### Code
;Encode by:
;- shifting the ascii codes by adding key value
;- reversion of the string
Function code$(txt$,key,mode=0)
	Local char,out$
	;Encode
	If mode=0 Then
		For i=1 To Len(txt$)
			char=Asc(Mid(txt$,i,1))
			char=char+key
			While char>255
				char=char-256
			Wend
			While char<0
				char=char+256
			Wend
			out$=Chr(char)+out$
		Next
	;Decode
	Else
		For i=1 To Len(txt$)
			char=Asc(Mid(txt$,i,1))
			char=char-key
			While char>255
				char=char-256
			Wend
			While char<0
				char=char+256
			Wend
			out$=Chr(char)+out$
		Next
	EndIf
	;Return
	Return out$
End Function


;### Dark Backbuffer
Function darkbuffer()
	Local x,y,rgb,r,g,b
	SetBuffer FrontBuffer()
	LockBuffer FrontBuffer()
	For x=0 To set_scrx-1
		For y=0 To set_scry-1
			rgb=ReadPixelFast(x,y)
			r=(rgb And $FF0000)/$10000
			g=(rgb And $FF00)/$100
			b=rgb And $FF
			r=Float(r+40)/2.5
			g=Float(g+20)/2.5
			b=Float(b+0)/2.5
			rgb=r*$10000 + g*$100 + b
			WritePixelFast(x,y,rgb)
		Next
	Next
	UnlockBuffer FrontBuffer()
	SetBuffer BackBuffer()
End Function


;### New Trim (because of error with "Umlaute" Ä.Ö.Ü in normal trim functions)
Function ntrim$(txt$)
	While Right(txt$,1)=" "
		txt$=Left(txt$,Len(txt$)-1)
	Wend
	Return txt$
End Function

;### Copy
Function copy(txt$)
	Local cb_TEXT=1
	If txt$="" Then Return 
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData cb_TEXT,txt$
		CloseClipboard
	EndIf
End Function

;### Paste
Function paste$()
	Local cb_TEXT=1
	Local txt$=""
	If OpenClipboard(0)
		If ExamineClipboard(cb_TEXT) 
			txt$=GetClipboardData$(cb_TEXT)
		EndIf
		CloseClipboard
	EndIf
	For i=1 To Len(txt)
		DebugLog Mid(txt,i,1)+" "+Asc(Mid(txt,i,1))
	Next
	Return txt$
End Function

;### Strip Extension
Function stripext$(txt$)
	p=Instr(txt$,".")
	If p<>0 Then
		Return(Left(txt$,p-1))
	Else
		Return(txt$)
	EndIf
End Function

;### Extreme Value (Limit a value to a maximum/minimum value)
Function extval#(val#,ext#)
	If val#>=0 Then
		If val#>ext# Then
			Return ext#
		Else
			Return val#
		EndIf
	Else
		If val#<ext# Then
			Return ext#
		Else
			Return val#
		EndIf
	EndIf
End Function

;### Extract Path
Function path$(txt$)
	txt$=Replace(txt$,"/","\")
	If Instr(txt$,"\")=0 Then
		Return txt$
	Else
		l=Len(txt$)
		For i=l To 1 Step -1
			If Mid(txt$,i,1)="\" Then
				Return Left(txt$,i)
			EndIf
		Next
	EndIf
End Function

;### Meshline
Function meshline(x1#,y1#,z1#,x2#,y2#,z2#,thick#=0.3)
	;Start Pivot
	Local s=CreatePivot()
	PositionEntity s,x1#,y1#,z1#
	;End Pivot
	Local e=CreatePivot()
	PositionEntity e,x2#,y2#,z2#
	;Center Pivot
	Local c=CreatePivot()
	PositionEntity c,x1#,y1#,z1#
	
	;Find Center
	Local dist#=EntityDistance(s,e)
	PointEntity(c,e)
	MoveEntity c,0,0,dist#/2
	
	;Cube
	h=CreateCube()
	ScaleEntity h,thick#,thick#,dist#/2.
	PositionEntity h,EntityX(c),EntityY(c),EntityZ(c)
	PointEntity(h,e)
	
	;p_add(x1#,y1#,z1#,Cp_risingflare,10,3):EntityColor TCp\h,0,255,0
	;p_add(x2#,y2#,z2#,Cp_risingflare,10,3):EntityColor TCp\h,0,255,0
	;p_add(EntityX(c),EntityY(c),EntityZ(c),Cp_risingflare,10,3):EntityColor TCp\h,255,255,0
	
	;Free
	FreeEntity s
	FreeEntity e
	FreeEntity c
	
	;Return
	Return h
End Function


;### Invertstring
Function strinv$(txt$)
	l=Len(txt$)
	nt$=""
	For i=1 To l
		byte=Asc(Mid(txt$,i,1))
		byte=255-byte
		nt$=nt$+Chr(byte)
	Next
	Return nt$
End Function

;### Numsep
Function numsep$(txt$)
	If Int(txt)>=0 Then
		l=Len(txt$)
		If l>3 Then
			c=0
			n$=""
			For i=l To 1 Step -1
				c=c+1
				n$=Mid(txt$,i,1)+n$
				If c=3 Then
					If i>1 Then
						n$=s$(33)+n$
					EndIf
					c=0
				EndIf
			Next
			Return n$
		Else
			Return txt$
		EndIf
	Else
		txt$=Mid(txt$,2,-1)
		l=Len(txt$)
		If l>3 Then
			c=0
			n$=""
			For i=l To 1 Step -1
				c=c+1
				n$=Mid(txt$,i,1)+n$
				If c=3 Then
					If i>1 Then
						n$=s$(33)+n$
					EndIf
					c=0
				EndIf
			Next
			Return "-"+n$
		Else
			Return "-"+txt$
		EndIf
	EndIf
End Function

;### Distance
Function dist#(x1#,z1#,x2#,z2#)
	Return Sqr((x1#-x2#)^2+(z1#-z2#)^2)
End Function

;### Incamrange
Function incam(x1#,z1#)
	If Sqr((x1#-EntityX(cam))^2+(z1#-EntityZ(cam))^2) < (500.*set_viewfac#) Then
		Return 1
	Else
		Return 0
	EndIf
End Function

;### Trim Text
;Remove one space on the left/right
Function trimtext$(txt$)
	;Left
	If Left(txt$,1)=Chr(34) Then txt$=Mid(txt$,2,-1)
	;Right
	If Right(txt$,1)=Chr(34) Then txt$=Left(txt$,Len(txt$)-1)
	;Return
	Return txt$
End Function

;### Zerofill
Function zerofill$(txt$,fill)
	While Len(txt$)<fill
		txt$="0"+txt$
	Wend
	Return txt$
End Function

;### Map
Function genmap(size=256)
	;Tempvars
	Local rx=-((ter_size/2)*Cworld_size)
	Local ry=-((ter_size/2)*Cworld_size)
	Local fac#=Float(Float(ter_size*Cworld_size)/Float(size-1))

	;Free
	If map_mapimage<>0 Then
		FreeImage map_mapimage
		map_mapimage=0
	EndIf
	
	;Create
	map_mapimage=CreateImage(size,size)
	SetBuffer ImageBuffer(map_mapimage)
	LockBuffer ImageBuffer(map_mapimage)
	
	For x=0 To size-1
		For y=0 To size-1
			;Color
			h#=Float(TerrainY(ter,rx+(x*fac#),0,ry+(y*fac#))/Float(Cworld_height))
			r=118.+(137.*h#)
			g=105.+(137.*h#)
			b=52.+(137.*h#)
			;Underwater
			If h#<0. Then
				r=(r*3+41)/4
				g=(g*3+221)/4
				b=(b*3+241)/4
			EndIf
			;Grid darken
			If (x Mod 64=0) Or (y Mod 64=0) Then
				r=Float(r)/Rnd(1.1,1.2)
				g=Float(g)/Rnd(1.1,1.2)
				b=Float(b)/Rnd(1.1,1.2)
			ElseIf (x Mod 16=0) Or (y Mod 16=0) Then
				r=Float(r)/Rnd(1.02,1.05)
				g=Float(g)/Rnd(1.02,1.05)
				b=Float(b)/Rnd(1.02,1.05)
			EndIf
			;Draw
			rgb=255*$1000000+r*$10000+g*$100+b
			;WritePixelFast x,size-(y+1),rgb
			WritePixelFast size-(x+1),y,rgb
		Next
	Next
	
	UnlockBuffer ImageBuffer(map_mapimage)
	SetBuffer BackBuffer()
End Function


;### Get File Name out of a Path (with 1 slash max.!)
Function getfilename$(path$)
	path$=Replace(path$,"/","\")
	Local slash=Instr(path$,"\")
	Local dot
	If slash>0 Then
		dot=Instr(path$,".",slash)
	Else
		dot=Instr(path$,".")
	EndIf
	If dot<>0 Then
		If slash=0 Then
			Return Left(path$,dot-1)
		Else
			path$=Left(path$,dot-1)
			path$=Mid(path$,slash+1,-1)
			Return path$
		EndIf
	Else
		If slash=0 Then
			Return path$
		Else
			Return Mid(path$,slash+1,-1)
		EndIf
	EndIf
End Function


;### Trim exclusive Quote
;trim everything in a text besides passages which are quoted
Function trimexquote$(txt$)
	Local l=Len(txt$)
	Local s$
	Local quo=0
	Local out$=""
	;Cache To Array
	For i=1 To l
		s$=Mid(txt$,i,1)
		If s$=Chr(34) Then
			quo=1-quo
			out$=out$+Chr(34)
		Else
			If quo=0 Then
				If s$<>" " Then
					out$=out$+s$
				EndIf
			Else
				out$=out$+s$
			EndIf
		EndIf
	Next
	;Return
	Return out$
End Function


;### Trimspace
Function trimspace$(txt$)
	While Left(txt$,1)=" "
		txt$=Mid(txt$,2,-1)
	Wend
	While Right(txt$,1)=" "
		txt$=Mid(txt$,1,Len(txt$)-1)
	Wend
	Return txt$
End Function


;### In Time between Curren Map Time and Last Map Time Update?
Function intime(day,hour=0,minute=0)
	check=(day*100*100)+(hour*100)+minute
	current=(map_day*100*100)+(map_hour*100)+map_minute
	lastupdate=(map_lday*100*100)+(map_lhour*100)+map_lminute
	;DebugLog "lastupdate: "+lastupdate
	;DebugLog "check: "+check
	;DebugLog "current: "+current
	If check>=lastupdate Then
		If check<=current Then
			Return 1
		EndIf
	EndIf
	Return 0
End Function


;### In Time between Curren Map Time and Last Map Time Update (without days)?
Function indailytime(hour,minute=0)
	check=(map_day*100*100)+(hour*100)+minute
	current=(map_day*100*100)+(map_hour*100)+map_minute
	lastupdate=(map_lday*100*100)+(map_lhour*100)+map_lminute
	If check>=lastupdate Then
		If check<=current Then
			Return 1
		EndIf
	EndIf
	If map_lday<map_day Then
		check=(map_lday*100*100)+(hour*100)+minute
		If check>=lastupdate Then
			If check<=current Then
				Return 1
			EndIf
		EndIf		
	EndIf
	Return 0
End Function


;### Y Rect
Function drawy(img,x,y,h)
	xsize=ImageWidth(img)
	ysize=ImageHeight(img)
	pos=y
	rest=h Mod ysize
	While pos<(y+h-rest)
		DrawImage img,x,pos
		pos=pos+ysize
	Wend
	If rest<>0 Then
		DrawImageRect img,x,y+h-rest,0,0,xsize,rest
	EndIf
End Function

;### Align Pitch to Ground
Function groundpitch#(h,dist#=100)
	;Cache
	Local rx#=EntityX(h)
	Local ry#=EntityY(h)
	Local rz#=EntityZ(h)
	Local ryaw#=EntityYaw(h)
	;Setup
	Local p1=CreatePivot()
	Local p2=CreatePivot()
	;Position Pivot 1
	PositionEntity p1,rx#,ry#,rz#
	RotateEntity p1,0,ryaw#,0
	MoveEntity p1,0,0,dist#
	PositionEntity p2,EntityX(p1),e_tery(EntityX(p1),EntityZ(p1)),EntityZ(p1)
	;Position Pivot 2
	PositionEntity p2,rx#,ry#,rz#
	RotateEntity p2,0,ryaw#,0
	MoveEntity p2,0,0,-dist#
	PositionEntity p2,EntityX(p2),e_tery(EntityX(p2),EntityZ(p2)),EntityZ(p2)
	;Get Pitch
	PointEntity p2,p1
	pitch#=EntityPitch(p2)
	;visualization
	;p_add(EntityX(p1),EntityY(p1),EntityZ(p1),Cp_flames)
	;p_add(EntityX(p2),EntityY(p2),EntityZ(p2),Cp_flames)
	;Free
	FreeEntity p1
	FreeEntity p2
	;Return Pitch
	Return pitch#
End Function


Function brightness(file$,mr,mg,mb,c#)
	img=LoadImage(file$)
	Local w,h
	Local r,g,b
	Local x,y
	w=ImageWidth(img)
	h=ImageHeight(img)
	SetBuffer ImageBuffer(img)
	For x=0 To w
		For y=0 To h
			GetColor(x,y)
			r=ColorRed()
			g=ColorGreen()
			b=ColorBlue()
			If r<>mr Or g<>mr Or b<>mr Then
				r=Float(r)*c
				g=Float(g)*c
				b=Float(b)*c
				If r>255 Then r=255
				If g>255 Then g=255
				If b>255 Then b=255
				If r<0 Then r=0
				If g<0 Then g=0
				If b<0 Then b=0
				Color r,g,b
				Plot x,y
			EndIf
		Next
	Next
	SetBuffer BackBuffer()
	Cls
	DrawImage img,200,200
	Color 255,255,255
	Text 200,250,"brlvl: "+br
	Flip
	x=WaitKey()
	If x=43 Then
		br=br+0.1
		brightness(file$,0,0,0,br)
		FreeImage img
		Return
	EndIf
	If x=45 Then
		br=br-0.1
		brightness(file$,0,0,0,br)
	    FreeImage img
		Return
	EndIf
	If x=32 Then SaveImage(img,file$): FreeImage img: Return 
	If x=120 Then  FreeImage img: Return 
End Function


Function viewline(x1#,y1#,z1#,x2#,y2#,z2#)
	Local p1=CreateCube()
	PositionEntity p1,x1#,y1#,z1#
	EntityPickMode p1,2,1
	Local p2=CreateCube()
	PositionEntity p2,x2#,y2#,z2#
	EntityPickMode p2,2,1
	Local v=EntityVisible(p1,p2)
	FreeEntity p1
	FreeEntity p2
	Return v
End Function


Function cachedata(free=1,ot_skills=0,ot_items=0,ot_vars=0,ot_diary=0,ot_states=0,ot_locks=0)
	;Free Old Caches
	If free=1 Then
		For Txc.Txc=Each Txc
			Delete Txc
		Next
	EndIf
	;Cache Player Skills?
	If ot_skills Then
		For Tx.Tx=Each Tx
			If Tx\mode=5 Then
				Txc.Txc=New Txc
				Txc\typ=Tx\typ
				Txc\parent_class=Tx\parent_class
				Txc\parent_id=Tx\parent_id
				Txc\mode=Tx\mode
				Txc\key$=Tx\key$
				Txc\value$=Tx\value$
				Txc\stuff$=Tx\stuff$
			EndIf
		Next
	EndIf
	;Cache Player Items?
	If ot_items Then
		;Items
		For Titem.Titem=Each Titem
			If Titem\parent_class=Cclass_unit Then
				If Titem\parent_id=g_player Then
					Txc.Txc=New Txc
					Txc\mode=7
					Txc\typ=Titem\typ
					Txc\value$=Titem\count
				EndIf
			EndIf
		Next
		;Quickslot
		For i=0 To 9
			Txc.Txc=New Txc
			Txc\typ=i
			Txc\mode=8
			Txc\value$=in_quickslot(i)
		Next
		;Weapon and Ammo
		If Handle(g_cplayer) Then
			;Weapon
			Txc.Txc=New Txc
			Txc\typ=10
			Txc\mode=8
			Txc\value$=g_cplayer\player_weapon
			;Ammo
			Txc.Txc=New Txc
			Txc\typ=11
			Txc\mode=8
			Txc\value$=g_cplayer\player_ammo
		EndIf
	EndIf
	;Cache Global Vars?
	If ot_vars Then
		For Tx.Tx=Each Tx
			If Tx\mode=1 Then
				Txc.Txc=New Txc
				Txc\typ=Tx\typ
				Txc\parent_class=Tx\parent_class
				Txc\parent_id=Tx\parent_id
				Txc\mode=Tx\mode
				Txc\key$=Tx\key$
				Txc\value$=Tx\value$
				Txc\stuff$=Tx\stuff$
			EndIf
		Next
	EndIf
	;Cache Diary Entries?
	If ot_diary Then
		For Tx.Tx=Each Tx
			If Tx\mode=2 Then
				Txc.Txc=New Txc
				Txc\typ=Tx\typ
				Txc\parent_class=Tx\parent_class
				Txc\parent_id=Tx\parent_id
				Txc\mode=Tx\mode
				Txc\key$=Tx\key$
				Txc\value$=Tx\value$
				Txc\stuff$=Tx\stuff$
			EndIf
		Next
	EndIf
	;Cache Player States
	If ot_states Then
		For Tstate.Tstate=Each Tstate
			If Tstate\parent_class=Cclass_unit Then
				If Tstate\parent_id=g_player Then
					Txc.Txc=New Txc
					Txc\mode=9
					Txc\typ=Tstate\typ
					Txc\parent_class=Tstate\parent_class
					Txc\parent_id=Tstate\parent_id
					Txc\stuff$=Tstate\x#+"ż"+Tstate\y#+"ż"+Tstate\z#
					Txc\stuff$=Txc\stuff$+"ż"+Tstate\fx#+"ż"+Tstate\fy#+"ż"+Tstate\fz#
					Txc\stuff$=Txc\stuff$+"ż"+Tstate\value+"ż"+Tstate\value_f#+"ż"+Tstate\value_s$
					Txc\stuff$=Txc\stuff$+"ż"+Tstate\r+"ż"+Tstate\g+"ż"+Tstate\b
				EndIf
			EndIf
		Next
	EndIf
	;Cache Buildlocks
	If ot_locks Then
		For Tx.Tx=Each Tx
			If Tx\mode=3 Then
				Txc.Txc=New Txc
				Txc\typ=Tx\typ
				Txc\parent_class=Tx\parent_class
				Txc\parent_id=Tx\parent_id
				Txc\mode=Tx\mode
				Txc\key$=Tx\key$
				Txc\value$=Tx\value$
				Txc\stuff$=Tx\stuff$
			EndIf
		Next
	EndIf
End Function


Function reppart$(txt$,otxt$,start,ntxt$)
	Local lt$,rt$
	;Left Part
	If start<=1 Then
		lt$=""
	Else
		lt$=Left(txt$,start-1)
	EndIf
	;Right Part
	If (start+Len(otxt$)-1)>=Len(txt$) Then
		rt$=""
	Else
		rt$=Mid(txt$,(start+Len(otxt$)),-1)
	EndIf
	;Replace
	Return lt$+ntxt$+rt$
End Function


Function screenshot(out=1)
	Local id=1
	Repeat
		name$="screen_"+zerofill$(id,5)+".bmp"
		If FileType(name$)=0 Then
			SaveBuffer(BackBuffer(),name$)
			Exit
		Else
			id=id+1
		EndIf
	Forever
	If out=1 Then
		if_msg("< screenshot saved as '"+name$+"' >",Cbmpf_yellow)
	EndIf
End Function
