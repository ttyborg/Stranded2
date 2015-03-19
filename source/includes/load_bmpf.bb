;############################################ Vars

Const bmpf_count=6
Dim bmpf_image(bmpf_count)
Dim bmpf_frames(bmpf_count)
Dim bmpf_framesx(bmpf_count)
Dim bmpf_framesy(bmpf_count)
Dim bmpf_framesw(255,bmpf_count)
Dim bmpf_framesc(255,bmpf_count)

Global gfx_bar=load_image("sys\gfx\progress.bmp")

bmpf_load_font("sys\gfx\font_tiny.bmp","sys\gfx\font_tiny.bmpf",5)
bmpf_loadscreen("Loading Fonts",0)

bmpf_load_font("sys\gfx\font_norm.bmp","sys\gfx\font_norm.bmpf",0)
bmpf_loadscreen("Loading Fonts",1)
bmpf_load_font("sys\gfx\font_norm_over.bmp","sys\gfx\font_norm.bmpf",1)
bmpf_loadscreen("Loading Fonts",2)
bmpf_load_font("sys\gfx\font_norm_dark.bmp","sys\gfx\font_norm.bmpf",2)
bmpf_loadscreen("Loading Fonts",3)
bmpf_load_font("sys\gfx\font_norm_bad.bmp","sys\gfx\font_norm.bmpf",3)
bmpf_loadscreen("Loading Fonts",4)
bmpf_load_font("sys\gfx\font_norm_good.bmp","sys\gfx\font_norm.bmpf",4)
bmpf_loadscreen("Loading Fonts",5)
bmpf_load_font("sys\gfx\font_handwriting.bmp","sys\gfx\font_norm.bmpf",6)

;### Rect Scroll Type
Type Tbmpf_s
	Field s$
End Type

;### Rect Image Type
Type Tbmpf_img
	Field file$
	Field h
End Type


;############################################ Load Font
Function bmpf_load_font(bmp$,bmpf$,id)
	Local def,i,sign
	If set_debug Then con_add("Loading Font: "+bmp$+" (Inf: "+bmpf+")")
	;Definition laden
	def=ReadFile(bmpf$)
	If Not def Then RuntimeError("Unable to load Bitmapfont "+bmp$)
	ReadLine(def)
	bmpf_frames(id)=ReadShort(def)
	bmpf_framesx(id)=ReadShort(def)
	bmpf_framesy(id)=ReadShort(def)
	For i=1 To bmpf_frames(id)
		sign=ReadByte(def)
		bmpf_framesc(sign,id)=i-1
		bmpf_framesw(sign,id)=ReadShort(def)
	Next
	;Image laden
	bmpf_image(id)=LoadAnimImage(bmp$,bmpf_framesx(id),bmpf_framesy(id),0,bmpf_frames(id))
	MaskImage bmpf_image(id),255,0,255
	;Ok
	Return 1
End Function


;############################################ Draw Font (left)
Function bmpf_txt(x,y,txt$,id=0)
	Local strlen=Len(txt$)
	Local i,sign
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
End Function


;############################################ Draw Font (center)
Function bmpf_txt_c(x,y,txt$,id=0)
	Local strlen=Len(txt$)
	Local i,sign,offset
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			offset=offset+bmpf_framesw(sign,id)
		Else
			offset=offset+bmpf_framesx(id)/2
		EndIf
	Next
	x=x-(offset/2)
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
End Function


;############################################ Draw Font (right)
Function bmpf_txt_r(x,y,txt$,id=0)
	Local strlen=Len(txt$)
	Local i,sign
	For i=strlen To 1 Step -1
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			x=x-bmpf_framesw(sign,id)
			DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
		Else
			x=x-bmpf_framesx(id)/2
		EndIf
	Next
End Function


;############################################ Font len
Function bmpf_len(txt$,id=0)
	Local strlen=Len(txt$)
	Local x,i,sign
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
	Return x
End Function

;############################################ Trimlen
Function bmpf_trim$(txt$,id,maxlen)
	Local strlen=Len(txt$)
	Local x,i,sign
	
	;Länge ermitteln
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
	
	;Letztes Zeichen weg, bis maxlen erreicht
	While x>maxlen
		If Len(txt$)=0 Then Return ""
		sign=Asc(Right(txt$,1))
		If sign<>32 Then
			x=x-bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
		txt$=Left(txt$,Len(txt$)-1)
	Wend
	
	Return txt$
End Function


;############################################ Draw Font in Rect
Function bmpf_txt_rect(x,y,w,h,txt$,id=0,scroll=0)
	Local strlen=Len(txt$)
	Local rx=x
	Local ry=y
	Local i,lastspace,sign,add
	Local lastbreak=1
	
	;Alte Lines in Types löschen
	For Tbmpf_s.Tbmpf_s=Each Tbmpf_s
		Delete Tbmpf_s
	Next
	
	;String Keyvars Replacen
	If Instr(txt$,"$key_") Then
		txt$=Replace(txt$,"$key_forward",in_keyname$(in_keys(Ckey_forward)))
		txt$=Replace(txt$,"$key_backward",in_keyname$(in_keys(Ckey_backward)))
		txt$=Replace(txt$,"$key_left",in_keyname$(in_keys(Ckey_left)))
		txt$=Replace(txt$,"$key_right",in_keyname$(in_keys(Ckey_right)))
		
		txt$=Replace(txt$,"$key_jump",in_keyname$(in_keys(Ckey_jump)))
		txt$=Replace(txt$,"$key_sleep",in_keyname$(in_keys(Ckey_sleep)))
		
		txt$=Replace(txt$,"$key_attack1",in_keyname$(in_keys(Ckey_attack1)))
		txt$=Replace(txt$,"$key_attack2",in_keyname$(in_keys(Ckey_attack2)))
		txt$=Replace(txt$,"$key_next",in_keyname$(in_keys(Ckey_next)))
		txt$=Replace(txt$,"$key_prev",in_keyname$(in_keys(Ckey_prev)))
		txt$=Replace(txt$,"$key_drop",in_keyname$(in_keys(Ckey_drop)))
		
		txt$=Replace(txt$,"$key_use",in_keyname$(in_keys(Ckey_use)))
		txt$=Replace(txt$,"$key_chat",in_keyname$(in_keys(Ckey_chat)))
		txt$=Replace(txt$,"$key_char",in_keyname$(in_keys(Ckey_char)))
		txt$=Replace(txt$,"$key_items",in_keyname$(in_keys(Ckey_items)))
		txt$=Replace(txt$,"$key_diary",in_keyname$(in_keys(Ckey_diary)))
		txt$=Replace(txt$,"$key_quicksave",in_keyname$(in_keys(Ckey_quicksave)))
		txt$=Replace(txt$,"$key_quickload",in_keyname$(in_keys(Ckey_quickload)))
	EndIf
	
	;String nach anderen Vars durchgehen
	Local offset
	Repeat
		If offset>0 Then
			i=Instr(txt$,"$",offset)
		Else
			i=Instr(txt$,"$")
		EndIf
		If i=0 Then
			Exit
		Else
			offset=i+1
			nomorecheck=0
			;Image
			If Mid(txt$,i,5)="$img=" Then
				nomorecheck=1
				pend=Instr(txt$,"Ś",i+1)
				path$=Trim(Mid(txt$,i+5,pend-(i+5)))
				found=0
				For Tbmpf_img.Tbmpf_img=Each Tbmpf_img
					If Tbmpf_img\file$=path$ Then
						found=1
						imgh=ImageHeight(Tbmpf_img\h)
						Exit
					EndIf
				Next
				If found=0 Then
					Tbmpf_img.Tbmpf_img=New Tbmpf_img
					Tbmpf_img\file$=path$
					Tbmpf_img\h=load_image(path$,1)
					If Tbmpf_img\h<>0 Then
						imgh=ImageHeight(Tbmpf_img\h)
					Else
						imgh=0
					EndIf
				EndIf
				If imgh>20 Then
					imgh=Ceil(Float(imgh)/20.)-1
					addstr$=""
					For i=1 To imgh
						addstr$=addstr$+"Ś"
					Next
				EndIf
				txt$=Left(txt$,pend)+addstr$+Mid(txt$,pend,-1)
			EndIf
			;Normal Vars (per Line)
			If nomorecheck=0 Then
				pend=Instr(txt$,"Ś",i+1)
				If pend>0 Then
					path$=Mid(txt$,i,pend-i)
					path$=var_rep$(path$)
					If i=1 Then
						txt$=path$+Mid(txt$,pend,-1)
					Else
						txt$=Left(txt$,i-1)+path$+Mid(txt$,pend,-1)
					EndIf
				Else
					path$=Mid(txt$,i,-1)
					path$=var_rep$(path$)
					txt$=Left(txt$,i-1)+path$
				EndIf
			EndIf
		EndIf
	Forever
	
	
	;Lines in Types anlegen
	x=0
	tmp_lines=0
	tmp_linesvisible=0
	strlen=Len(txt$)
	For i=1 To strlen
		;Zeichenlänge
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
		;Nextline/Lastspace
		If sign=166 Then lastspace=i:x=w+1:add=1
		If x>w Then
			;Nextline
			If lastspace=0 Then lastspace=i-1
			x=0
			Tbmpf_s.Tbmpf_s=New Tbmpf_s
			Tbmpf_s\s$=trimspace(Mid(txt$,lastbreak,lastspace-lastbreak))
			i=lastspace
			lastbreak=lastspace+add
			lastspace=0
			tmp_lines=tmp_lines+1
		Else
			;Lastspace
			add=0
			If sign<33 Then			;Whitespaces
				lastspace=i
				add=1
			ElseIf sign=45 Then		;-
				lastspace=i
			ElseIf sign=182			;Forced Break (ś)
				lastspace=i
				add=1
			EndIf 
		EndIf
	Next
	;Lastline
	Tbmpf_s.Tbmpf_s=New Tbmpf_s
	Tbmpf_s\s$=trimspace(Mid(txt$,lastbreak,-1))
	tmp_lines=tmp_lines+1
		
	;Ausgeben unter Berücksichtigung des Scrolls
	x=rx
	y=ry
	i=0
	For Tbmpf_s.Tbmpf_s=Each Tbmpf_s
		If i>=scroll Then
			tmp_linesvisible=tmp_linesvisible+1
			;Image
			If Left(Tbmpf_s\s$,5)="$img=" Then
				path$=Trim(Mid(Tbmpf_s\s$,6,-1))
				For Tbmpf_img.Tbmpf_img=Each Tbmpf_img
					If Tbmpf_img\file$=path$ Then
						DrawImageRect Tbmpf_img\h,x,y,0,0,w,(ry+h)-y
						Exit
					EndIf
				Next
				y=y+20
				If y+18>ry+h Then Return 0
			;Text
			Else
				If Left(Tbmpf_s\s$,1)="!" Then
					tid=Int(Mid(Tbmpf_s\s$,2,1))
					If tid<0 Then tid=id
					If tid>6 Then tid=id
					bmpf_txt(x,y,Mid(Tbmpf_s\s$,3,-1),tid)
				Else
					bmpf_txt(x,y,Tbmpf_s\s$,id)
				EndIf
				y=y+20
				If y+18>ry+h Then Return 0
			EndIf
		EndIf
		i=i+1
	Next
	;Lines Left
	Repeat
		tmp_linesvisible=tmp_linesvisible+1
		y=y+20
		If y>rh+h Then Return 1
	Forever
	Return 1
End Function


;############################################ Draw Font (scroll)
Function bmpf_txt_scroll(x#,y,txt$,id=0,ml,scroll#)
	Local sx#=x#
	Local strlen=Len(txt$)
	Local i,sign
	x#=x#+scroll#
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			If x#>=sx# And x#+20<ml Then
				DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
			EndIf
			x#=x#+bmpf_framesw(sign,id)
		Else
			x#=x#+bmpf_framesx(id)/2
		EndIf
	Next
End Function


;############################################ Loadscreen
Function bmpf_loadscreen(txt$,percent=-1)
	SetBuffer BackBuffer()
	ClsColor 0,0,0
	Cls
	bmpf_txt_c(set_scrx/2,set_scry/2-20,txt$,5)
	;Local stripes$=""
	;For i=1 To percent
	;	stripes$=stripes$+"|"
	;Next
	;bmpf_txt_c(set_scrx/2,set_scry/2,stripes$,5)
	
	If percent>-1 Then
		If percent>100 Then percent=100
		Color 60,60,60
		Rect set_scrx/2-300,set_scry/2+5,600,5,1
		DrawImageRect gfx_bar,set_scrx/2-300,set_scry/2+5,0,0,percent*6,5
	
		bmpf_txt_c(set_scrx/2,set_scry/2+20,percent+"%",5)
	EndIf	
	
	Flip
	If in_conlastloadmsg$<>txt$ Then
		in_conlastloadmsg$=txt$
		con_add(txt$)
	EndIf
	p_rectscreen()
End Function


;############################################ Draw Font (left, maxlength)
Function bmpf_txt_ml(x,y,txt$,id,maxlen)
	Local strlen=Len(txt$)
	Local i,sign
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If (sign<>32)
			If (x+bmpf_framesw(sign,id))>maxlen Then Return 0
			DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
	Return 1
End Function


;############################################ Maxlength Text lenght
Function bmpf_txt_ml_len(x,y,txt$,id,maxlen)
	Local strlen=Len(txt$)
	Local i,sign
	Local sx=x
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If (sign<>32)
			If (x+bmpf_framesw(sign,id))>maxlen Then Return (x-sx)
			x=x+bmpf_framesw(sign,id)
		Else
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
	 Return (x-sx)
End Function


;############################################ Draw Font Marked (left, maxlength)
Function bmpf_txt_ml_marked(x,y,txt$,id,maxlen,ms,me)
	Color 84,51,0
	Local strlen=Len(txt$)
	Local i,sign
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If (sign<>32)
			If (x+bmpf_framesw(sign,id))>maxlen Then Return 0
			If i>ms And i<=me Then
				Rect x-1,y+1,bmpf_framesw(sign,id)+2,14,1
			EndIf
			DrawImage bmpf_image(id),x,y,bmpf_framesc(sign,id)
			x=x+bmpf_framesw(sign,id)
		Else
			If i>ms And i<=me Then
				Rect x-1,y+1,(bmpf_framesx(id)/2)+2,14,1
			EndIf
			x=x+bmpf_framesx(id)/2
		EndIf
	Next
	Return 1
End Function


;############################################ Position
Function bmpf_txt_pos(txt$,id,pos)
	Local strlen=Len(txt$)
	Local i,sign,os
	If pos<=3 Then Return 0
	For i=1 To strlen
		sign=Asc(Mid(txt$,i,1))
		If sign<>32 Then
			os=bmpf_framesw(sign,id)
		Else
			os=bmpf_framesx(id)/2
		EndIf
		x=x+os
		If x-(os/2)>=pos Then Return i-1
		If x>=pos Then Return i
	Next
	Return strlen	
End Function
