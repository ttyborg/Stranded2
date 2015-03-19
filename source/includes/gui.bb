;######################################################################################################

;############################################ FUNCTIONS

;### Tooltip
Function gui_tt(x,y,txt$)
	If (x+20)<(set_scrx-bmpf_len(txt$,5)) Then
		in_ttx=x+20
	Else
		in_ttx=x-5-bmpf_len(txt$,5)
	EndIf
	If (y+25)<(set_scry) Then
		in_tty=y+5
	Else
		in_tty=y-15
	EndIf
	in_ttt$=txt$
End Function


;### Button
Function gui_button(x,y,txt$,active=1,icon=-1)
	If active Then
		;Active
		Local over
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+190 Then
					If in_my<=y+45 Then
						over=1
					EndIf
				EndIf
			EndIf
		EndIf
		DrawImage gfx_bigbutton(over),x,y
		;Icon
		If icon>-1 Then
			If over And in_mdown(1) Then
				DrawImage gfx_icons,x+5+1,y+6+1,icon
			Else
				DrawImage gfx_icons,x+5,y+6,icon
			EndIf
		EndIf
		;Text
		x=x+(190/2)
		y=y+(45/2)-bmpf_framesy(0)/2
		If over Then
			bmpf_txt_c(x+in_mdown(1),y+in_mdown(1),txt$,over)
		Else
			bmpf_txt_c(x,y,txt$,over)
		EndIf
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_click)
				flushinput()
				Return 1
			EndIf
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		DrawImage gfx_bigbutton(0),x,y
		If icon>-1 Then DrawImage gfx_icons_passive,x+5,y+6,icon
		x=x+(190/2)
		y=y+(45/2)-bmpf_framesy(0)/2
		bmpf_txt_c(x,y,txt$,2)
		Return 0
	EndIf
End Function


;### Icon Button
Function gui_ibutton(x,y,icon,txt$,active=1,forceover=0,hotkey=0)
	Local over=0
	If active Then
		;Active
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+32 Then
					If in_my<=y+32 Then
						over=1
						;Tool Tip
						If hotkey>0 Then
							gui_tt(in_mx,in_my,txt$+" ["+in_keyname$(hotkey)+"]")
						Else
							gui_tt(in_mx,in_my,txt$)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		If forceover Then
			DrawImage gfx_iconbutton(1),x,y
		Else
			DrawImage gfx_iconbutton(over),x,y
		EndIf
		If over And in_mdown(1) Then
			DrawImage gfx_icons,x+1,y+1,icon
		Else
			DrawImage gfx_icons,x,y,icon
		EndIf
	
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_click)
				flushinput()
				Return 1
			EndIf
		EndIf
		;Hotkey
		If hotkey>0 Then
			If inputhit(hotkey) Then Return 1
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		DrawImage gfx_iconbutton(0),x,y
		DrawImage gfx_icons_passive,x,y,icon
		Return 0
	EndIf
End Function


;### Icon Button with alternative Icon Image
Function gui_ibuttona(x,y,icon,txt$,active=1,forceover=0,hotkey=0)
	Local over=0
	If active Then
		;Active
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+32 Then
					If in_my<=y+32 Then
						over=1
						;Tool Tip
						If hotkey>0 Then
							gui_tt(in_mx,in_my,txt$+" ["+in_keyname$(hotkey)+"]")
						Else
							gui_tt(in_mx,in_my,txt$)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		If forceover Then
			DrawImage gfx_iconbutton(1),x,y
		Else
			DrawImage gfx_iconbutton(over),x,y
		EndIf
		If over And in_mdown(1) Then
			DrawImage icon,x+1,y+1
		Else
			DrawImage icon,x,y
		EndIf
	
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_click)
				flushinput()
				Return 1
			EndIf
		EndIf
		;Hotkey
		If hotkey>0 Then
			If inputhit(hotkey) Then Return 1
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		DrawImage gfx_iconbutton(0),x,y
		DrawImage icon,x,y
		Return 0
	EndIf
End Function


;### Checkbox
Function gui_check(x,y,txt$,id,active=1)
	If active Then
		;Active
		Local over
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+16+3+bmpf_len(txt$) Then
					If in_my<=y+16 Then
						over=1
					EndIf
				EndIf
			EndIf
		EndIf
		DrawImage gfx_check(over),x,y
		If in_check(id) Then DrawImage gfx_check(2),x,y
		x=x+16+3
		y=y+(8)-bmpf_framesy(0)/2
		bmpf_txt(x,y,txt$,over)
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_switch)
				in_check(id)=Not in_check(id)
				Return 1
			EndIf
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		DrawImage gfx_check(0),x,y
		If in_check(id) Then DrawImage gfx_check(2),x,y
		x=x+16+3
		y=y+(8)-bmpf_framesy(0)/2
		bmpf_txt(x,y,txt$,2)
		Return 0
	EndIf
End Function


;### Options
Function gui_opt(x,y,txt$,id,groupid,active=1)
	If active Then
		;Active
		Local over
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+16+3+bmpf_len(txt$) Then
					If in_my<=y+16 Then
						over=1
					EndIf
				EndIf
			EndIf
		EndIf
		DrawImage gfx_opt(over),x,y
		If in_opt(groupid)=id Then DrawImage gfx_opt(2),x,y 
		x=x+16+3
		y=y+(8)-bmpf_framesy(0)/2
		bmpf_txt(x,y,txt$,over)
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_switch)
				in_opt(groupid)=id
				Return 1
			EndIf
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		DrawImage gfx_opt(0),x,y
		If in_opt(groupid)=id Then DrawImage gfx_opt(2),x,y 
		x=x+16+3
		y=y+(8)-bmpf_framesy(0)/2
		bmpf_txt(x,y,txt$,2)
		Return 0
	EndIf
End Function


;### Text Button
Function gui_txtb(x,y,txt$,active=1,col=-1,tooltip$="")
	If active Then
		;Active
		Local over
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+bmpf_len(txt$) Then
					If in_my<=y+16 Then
						over=1
						If tooltip$<>"" Then gui_tt(in_mx,in_my,tooltip$)
					EndIf
				EndIf
			EndIf
		EndIf
		If col=-1 Then
			bmpf_txt(x,y,txt$,over)
		Else
			bmpf_txt(x,y,txt$,col)
		EndIf
		;Click
		If over Then
			If in_mrelease(1) Then
				play(sfx_click)
				flushinput()
				Return 1
			EndIf
		EndIf
		;No Click
		Return 0
	Else
		;Passive
		bmpf_txt(x,y,txt$,2)
		Return 0
	EndIf
End Function


;### Slider
Function gui_slider(x,y,steps,id,active=1)
	If steps<31 Then
		For i=0 To steps
			DrawImage gfx_slider(3),x+(i*(100/steps)),y+8
		Next
	EndIf
	If active Then
		;Active
		Local over
		If in_mx>=x-10 Then
			If in_my>=y Then 
				If in_mx<=x+100+10 Then
					If in_my<=y+16 Then
						over=1
					EndIf
				EndIf
			EndIf
		EndIf
		;Click / Down
		If in_slidefocus=id Or (over=1 And in_mdown(1)) Then
			If in_mhit(1) Or in_mdown(1) Then
				over=1
				in_slidefocus=id
				in_slider(id)=(in_mx-x)/(100/steps)
				If in_slider(id)<0 Then
					in_slider(id)=0
				ElseIf in_slider(id)>steps Then
					in_slider(id)=steps
				EndIf
			EndIf
		EndIf
		If in_slidefocus<>id Then over=0
		DrawImage gfx_slider(over),x,y
		DrawImage gfx_slider(2),x+in_slider(id)*(100/steps),y+8
	Else
		;Passive
		DrawImage gfx_slider(0),x,y
	EndIf	
End Function


;### Input
Function gui_input(x,y,w,id,active=1,tt$="")
	If active Then
		;Active
		Local over
		If in_mx>=x Then
			If in_my>=y Then 
				If in_mx<=x+w Then
					If in_my<=y+22 Then
						over=1
						in_cursorf=5
						;Tool Tip
						If tt$<>"" Then gui_tt(in_mx,in_my,tt$)
					EndIf
				EndIf
			EndIf
		EndIf
		;Click / Down
		If over Then
			If in_mrelease(1) Then
				in_inputfocus=id
			EndIf
		EndIf
		;Input
		If in_inputfocus=id Then
			Local key=in_key
			Local pre$
			;Add
			If (key>31 And key<127) Or (Instr("ÄäÖöÜüß",Chr(key))>0) Then
				pre$=in_input$(id)
				in_input$(id)=in_input$(id)+Chr(key)
				If bmpf_len(in_input$(id),0)>w-3 Then
					in_input$(id)=pre$
				EndIf
			;DEL
			ElseIf key=4 Then
				in_input$(id)=""
			;BS
			ElseIf key=8 Then
				If Len(in_input$(id))>0 Then in_input$(id)=LSet(in_input$(id), Len(in_input$(id))-1)
			EndIf
			;Ctrl
			If KeyDown(29) Or KeyDown(157) Then
				;Cut
				If KeyHit(45) Then copy(in_input(id)):in_input$(id)=""
				;Copy
				If KeyHit(46) Then copy(in_input(id))
				;Paste
				If KeyHit(47) Then in_input$(id)=paste()
			EndIf
		EndIf
	EndIf
	;Draw
	Local ds=0	;Drawstep
	Local dw=0	;Drawwidth
	For dw=x To x+w Step 8
		Select ds
			;0 - Left
			Case 0
				DrawImage gfx_input(0+over),dw,y
				ds=1
			;1 - Middle
			Case 1
				DrawImage gfx_input(2+over),dw,y
				If (x+w)-dw<=8 Then ds=2
			;2 - Right
			Case 2
				DrawImage gfx_input(4+over),dw,y
		End Select
	Next
	If active Then
		If in_inputfocus=id Then
			If in_cursor Then
				bmpf_txt(x+3,y+1,in_input$(id)+"|",1)
			Else
				bmpf_txt(x+3,y+1,in_input$(id),1)
			EndIf
		Else
			bmpf_txt(x+3,y+1,in_input$(id),0)
		EndIf
	Else
		bmpf_txt(x+3,y+1,in_input$(id),2)
	EndIf
End Function


;### Colorbox
Function gui_colorbox(r,g,b,x,y,w,h,txt$="")
	Local r2,g2,b2
	Local dif1=80
	Local dif2=180
	;Mainrect
	Color r,g,b
	Rect x+1,y+1,w-2,h-2,1
	;Highlight
	r2=r+dif1:If r2>255 Then r2=255
	g2=g+dif1:If g2>255 Then g2=255
	b2=b+dif1:If b2>255 Then b2=255
	Color r2,g2,b2
	Rect x,y,w,1,1
	Rect x,y,1,h,1
	;Shadow
	r2=r-dif2:If r2<0 Then r2=0
	g2=g-dif2:If g2<0 Then g2=0
	b2=b-dif2:If b2<0 Then b2=0
	Color r2,g2,b2
	Rect x,y+h-1,w,1,1
	Rect x+w-1,y,1,h,1
	;Click
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+w Then
				If in_my<=y+h Then
					;Tool Tip
					gui_tt(in_mx,in_my,txt$)
					;Click
					If in_mrelease(1) Then
						play(sfx_switch)
						flushinput()
						Return 1
					ElseIf in_mrelease(2) Then
						play(sfx_switch)
						flushinput()
						Return 2
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;### Iconbox
Function gui_iconbox(x,y,img,txt$,sel)
	;Over
	Local over
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+40 Then
				If in_my<=y+40 Then
					over=1
					;Tool Tip
					gui_tt(in_mx,in_my,txt$)
				EndIf
			EndIf
		EndIf
	EndIf
	;Draw
	If over Then
		If in_mdown(1) Or in_mdown(2) Then
			If img<>0 Then DrawImageRect img,x+1,y+1,0,0,39,39
			If sel Then Color 0,Abs(255.*Sin(in_ca#)),0:Rect x,y,40,40,0
		Else
			If img<>0 Then DrawImage img,x,y
			If sel Then Color 0,Abs(255.*Sin(in_ca#)),0:Rect x,y,40,40,0
		EndIf
	Else
		If img<>0 Then DrawImage img,x,y
		If sel Then Color 0,Abs(255.*Sin(in_ca#)),0:Rect x,y,40,40,0
	EndIf
	;Click / Down
	If over Then
		If in_mrelease(1) Then
			play(sfx_switch)
			flushinput()
			Return 1
		ElseIf in_mrelease(2) Then
			play(sfx_switch)
			flushinput()
			Return 2
		EndIf
	EndIf
	;None
	Return 0
End Function


;### Iconbox Count
Function gui_iconboxc(x,y,c,showeveryc=0,forcered=0)
	If forcered=0 Then
		;Show only >1
		If showeveryc=0 Then
			If c>1 Then
				bmpf_txt(x+2,y+25,c,Cbmpf_tiny)
			EndIf
		;Show Every Count, 0 = Red
		Else
			If c=0 Then
				bmpf_txt(x+2,y+23,c,Cbmpf_red)
			Else
				bmpf_txt(x+2,y+25,c,Cbmpf_tiny)
			EndIf
		EndIf
	;Red
	Else
		bmpf_txt(x+2,y+23,c,Cbmpf_red)
	EndIf
End Function


;### Iconbox Quickslot
Function gui_iconboxqs(x,y,s)
	If s>0 Then
		bmpf_txt(x+2,y,s,0) ;Cbmpf_tiny)
	EndIf
End Function


;### Healthbar
Function gui_healthbar#(x,y,current#,max#,blink=1,image=0)
	;Image
	If image=0 Then image=gfx_bars
	;Draw
	perc=(current#/max#)*100.
	DrawBlock gfx_if_barback,x,y
	If blink=1 Then
		If perc<=30 Then
			DrawBlockRect image,x+1,y+1,0,0,perc,5,in_cursor
		Else
			DrawBlockRect image,x+1,y+1,0,0,perc,5
		EndIf
	Else
		DrawBlockRect image,x+1,y+1,0,0,perc,5
	EndIf
	;Action
	If in_mx>=x-10 Then
		If in_my>=y Then 
			If in_mx<=x+110 Then
				If in_my<=y+5 Then
					;Tool Tip
					gui_tt(in_mx,in_my,sm$(6))
					;Down
					If in_mdown(1) Then
						x=in_mx-x
						If x<0 Then x=0
						If x>100 Then x=100
						Return max#*(Float(x)/100.)
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	Return -1
End Function


;### Classselbox
Function gui_classselbox(x,y,class,in)
	;Over
	Local over
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+40 Then
				If in_my<=y+40 Then
					over=1
					;Tool Tip
					gui_tt(in_mx,in_my,sm$(7))
				EndIf
			EndIf
		EndIf
	EndIf
	;Class
	Local img=0
	Select class
		Case Cclass_object
			in_opt(0)=1
			If in_input$(in)=0 Then in_input$(in)=in_object_sel
			img=Dobject_iconh(Int(in_input$(in)))
		Case Cclass_unit
			in_opt(0)=2
			If in_input$(in)=0 Then in_input$(in)=in_unit_sel
			img=Dunit_iconh(Int(in_input$(in)))
		Case Cclass_item
			in_opt(0)=3
			If in_input$(in)=0 Then in_input$(in)=in_item_sel
			img=Ditem_iconh(Int(in_input$(in)))
		Case Cclass_state
			in_opt(0)=5
			If in_input$(in)=0 Then in_input$(in)=in_state_sel
		Default Return 0
	End Select	
	If in_input$(in)=0 Then Return 0
	;Draw
	If img=0 Then
		If over Then
			If in_mdown(1) Then
				DrawImage gfx_states,x+11,y+11,Dstate_frame(Int(in_input$(in)))
			Else
				DrawImage gfx_states,x+10,y+10,Dstate_frame(Int(in_input$(in)))
			EndIf
		Else
			DrawImage gfx_states,x+10,y+10,Dstate_frame(Int(in_input$(in)))
		EndIf
	Else
		If over Then
			If in_mdown(1) Then
				DrawImageRect img,x+1,y+1,0,0,39,39
			Else
				DrawImage img,x,y
			EndIf
		Else
			DrawImage img,x,y
		EndIf
	EndIf
	;Click / Down
	If over Then
		If in_mrelease(1) Then
			play(sfx_switch)
			flushinput()
			Select class
				Case Cclass_object
					in_input$(in)=in_object_sel
				Case Cclass_unit
					in_input$(in)=in_unit_sel
				Case Cclass_item
					in_input$(in)=in_item_sel
				Case Cclass_state
					in_input$(in)=in_state_sel
			End Select
			Return 1
		EndIf
	EndIf
	;None
	Return 0
End Function


;### Operator
Function gui_operator(x,y,id,active=1)
	Local txt$
	Select Int(in_input$(id))
		Case 0 txt$=sm$(8)
		Case 1 txt$=sm$(9)
		Case 2 txt$=sm$(10)
		Case 3 txt$=sm$(11)
		Case 4 txt$=sm$(12)
		Case 5 txt$=sm$(13)
	End Select
	;Over
	Local over
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+bmpf_len(txt$) Then
				If in_my<=y+20 Then
					over=1
				EndIf
			EndIf
		EndIf
	EndIf
	;Draw
	If active Then
		bmpf_txt(x,y,txt$,over)
	Else
		bmpf_txt(x,y,txt$,Cbmpf_dark)
		Return 0
	EndIf
	;Click
	If over Then
		If in_mrelease(1) Then
			play(sfx_switch)
			flushinput()
			in_input$(id)=Int(in_input$(id))+1
			If Int(in_input$(id))>5 Then in_input$(id)=0
			Return 1
		ElseIf in_mrelease(2) Then
			play(sfx_switch)
			flushinput()
			in_input$(id)=Int(in_input$(id))-1
			If Int(in_input$(id))<0 Then in_input$(id)=5
			Return 2
		EndIf
	EndIf
	;None
	Return 0
End Function


;### Input String (Modify a String by User Input and Return it)
Function gui_inputstr$(txt$,ml=-1)
	Local key=in_key
	Local pre$
	;Add
	If (key>31 And key<127) Then
		txt$=txt$+Chr(key)
	;DEL
	ElseIf key=4 Then
		txt$=""
	;BS
	ElseIf key=8 Then
		If Len(txt$)>0 Then txt$=LSet(txt$, Len(txt$)-1)
	EndIf
	;Ctrl
	If KeyDown(29) Or KeyDown(157) Then
		;Cut
		If KeyHit(45) Then copy(txt$):txt$=""
		;Copy
		If KeyHit(46) Then copy(txt$)
		;Paste
		If KeyHit(47) Then txt$=paste()
	EndIf
	;Length Trim
	If ml>-1 Then
		While bmpf_len(txt$,0)>ml
			txt$=LSet(txt$, Len(txt$)-1)
		Wend
	EndIf
	;Return
	Return txt$
End Function


;### Tiny Text Button
Function gui_ttb(x,y,txt$,forceover=0,tt$="")
	l=bmpf_len(txt$,Cbmpf_tiny)+2
	;Over
	Local over
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+l Then
				If in_my<=y+18 Then
					over=1
					If tt$<>"" Then
						gui_tt(in_mx,in_my,tt$)
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	If over Or forceover Then
		Color 84,51,0
	Else
		Color 0,0,0
	EndIf
	Rect x,y,l,18,1
	bmpf_txt(x+1,y,txt$,Cbmpf_tiny)
	;Click
	If over Then
		If in_mrelease(1) Then
			play(sfx_click)
			flushinput()
			Return 1
		ElseIf in_mrelease(2) Then
			flushinput()
			Return 2
		EndIf
	Else
		Return 0
	EndIf
End Function


;### GUI Message
Function gui_msg(txt$,col=0,image$="")
	Local img
	If image$<>"" Then img=load_image(image$,0)
	If img<>0 Then MidHandle img
	ClsColor 0,0,0
	Cls
	If img<>0 Then DrawBlock img,set_scrx/2,set_scry/2
	bmpf_txt_c(set_scrx/2,set_scry/2-10,txt$,col)
	bmpf_txt_c(set_scrx/2,set_scry/2+15,sm$(14),Cbmpf_tiny)
	Flip
	If in_conlastloadmsg$<>txt$ Then
		in_conlastloadmsg$=txt$
		con_add(txt$,col)
	EndIf
	flushinput()
	Repeat
		key=GetKey()
		mouse=GetMouse()
		If key<>0 Or mouse<>0 Then Exit
	Forever
	flushinput()
	If img<>0 Then FreeImage(img)
	p_rectscreen()
End Function

;### GUI Input
Function gui_msginput$(txt$,col=0,cancel$="",okay$="",image$="",id=9)
	in_input(id)=""
	Local img
	If image$<>"" Then img=load_image(image$,0)
	If img<>0 Then MidHandle img
	If cancel$="" Then cancel$=sm$(3)
	If okay$="" Then okay$=sm$(5)
	flushinput()
	Repeat
		getinput(0)
		ClsColor 0,0,0
		Cls
		If img<>0 Then DrawBlock img,set_scrx/2,set_scry/2
		bmpf_txt_c(set_scrx/2,set_scry/2-45,txt$,col)
		gui_input(set_scrx/2-128,set_scry/2-22,256,id)
		
		;Cancel
		If gui_ibutton(set_scrx/2-32-3,set_scry/2+5,Cicon_x,cancel$,1,0,1) Then
			flushinput()
			Return ""
		EndIf
		;Okay
		If gui_ibutton(set_scrx/2+3,set_scry/2+5,Cicon_ok,okay$,1,0,28) Then
			flushinput()
			Return in_input(id)
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
	If img<>0 Then FreeImage(img)
	p_rectscreen()
End Function

;### GUI Decision
Function gui_msgdecide(txt$,col=0,cancel$="",okay$="",image$="")
	Local img
	If image$<>"" Then img=load_image(image$,0)
	If img<>0 Then MidHandle img
	If cancel$="" Then cancel$=sm$(3)
	If okay$="" Then okay$=sm$(5)
	flushinput()
	Repeat
		getinput(0)
		ClsColor 0,0,0
		Cls
		If img<>0 Then DrawBlock img,set_scrx/2,set_scry/2
		bmpf_txt_c(set_scrx/2,set_scry/2-20,txt$,col)
		
		;Cancel
		If gui_ibutton(set_scrx/2-32-3,set_scry/2+5,Cicon_x,cancel$,1,0,1) Then
			flushinput()
			Return 0
		EndIf
		;Okay
		If gui_ibutton(set_scrx/2+3,set_scry/2+5,Cicon_ok,okay$,1,0,28) Then
			flushinput()
			Return 1
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
	If img<>0 Then FreeImage(img)
	p_rectscreen()
End Function



;### Color Window
Function gui_win_color()
	Local gt
	Local temp=CreateImage(set_scrx,set_scry)
	SetBuffer FrontBuffer():GrabImage temp,0,0
	;Generate Colorboard
	Local colorboard=CreateImage(300,256)
	SetBuffer ImageBuffer(colorboard):LockBuffer ImageBuffer(colorboard)
	i=0
	For y=0 To 255
		st=0
		r#=255:g#=0:b#=0
		For x=0 To 299
			If x Mod 50=0 Then st=st+1
			Select st
				Case 1 g#=g#+5.1
				Case 2 r#=r#-5.1
				Case 3 b#=b#+5.1
				Case 4 g#=g#-5.1
				Case 5 r#=r#+5.1
				Case 6 b#=b#-5.1
			End Select
			rt=r#-y:gt=g#-y:bt=b#-y
			If rt<0 Then rt=0
			If gt<0 Then gt=0
			If bt<0 Then bt=0
			rgb=255*$1000000+rt*$10000+gt*$100+bt
			WritePixelFast x,y,rgb
		Next
	Next
	UnlockBuffer ImageBuffer(colorboard):SetBuffer BackBuffer()
	;Loop
	Local quit=0,ret=0
	While quit=0
		getinput(0)
		If KeyHit(1) Then quit=1
		DrawBlock temp,0,0
		
		;Head
		DrawImage gfx_win,215,0
		If gui_ibutton(742,5,Cicon_x,sm$(15)) Then quit=1
		DrawImage gfx_icons,236,5,Cicon_colormap
		bmpf_txt(236+37,10,sm$(16))
		DrawImage gfx_winbar,215,42
		
		;Colorboard
		Color 0,0,0
		Rect 236,63,302,258,0
		DrawBlock colorboard,237,64
		If in_mx>=237 Then
			If in_my>=64 Then
				If in_mx<=237+300 Then
					If in_my<=64+256 Then
						If in_mdown(1) Then
							rgb=ReadPixel(in_mx,in_my)
							in_slider(0)=Float((rgb And $FF0000)/$10000)/2.55
							in_slider(1)=Float((rgb And $FF00)/$100)/2.55
							in_slider(2)=Float(rgb And $FF)/2.55
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		;Big Color Matrix
		x=0:y=0
		Local br,bg,bb
		For br=0 To 255 Step 51
			For bg=0 To 255 Step 51
				For bb=0 To 255 Step 51
					If gui_colorbox(br,bg,bb,543+(x*21),63+(y*13),16,8,Int(br)+","+Int(bg)+","+Int(bb)) Then
						in_slider(0)=Float(br)/2.55
						in_slider(1)=Float(bg)/2.55
						in_slider(2)=Float(bb)/2.55
					EndIf
					x=x+1
					If x>=11 Then
						x=0
						y=y+1
					EndIf
				Next
			Next
		Next
		
		;Red
		in_win_color(0)=in_slider(0)*2.55
		gui_colorbox(255,0,0,236,370,29,32)
		gui_colorbox(in_win_color(0),0,0,236+34,370,29,32)
		gui_slider(236+64+10,370+5,100,0)
		bmpf_txt(336+64+20,370+2,in_win_color(0))
		;Green
		in_win_color(1)=in_slider(1)*2.55
		gui_colorbox(0,255,0,236,370+37,29,32)
		gui_colorbox(0,in_win_color(1),0,236+34,370+37,29,32)
		gui_slider(236+64+10,370+5+37,100,1)
		bmpf_txt(336+64+20,370+2+37,in_win_color(1))
		;Blue
		in_win_color(2)=in_slider(2)*2.55
		gui_colorbox(0,0,255,236,370+37+37,29,32)
		gui_colorbox(0,0,in_win_color(2),236+34,370+37+37,29,32)
		gui_slider(236+64+10,370+5+37+37,100,2)
		bmpf_txt(336+64+20,370+2+37+37,in_win_color(2))
		;Mixed
		gui_colorbox(in_win_color(0),in_win_color(1),in_win_color(2),236,328,537,30)
		
		;Aufhellen
		If gui_ibutton(236,542,Cicon_sun,sm$(17)) Then 
			For i=0 To 2
				in_slider(i)=in_slider(i)+5
				If in_slider(i)>100 Then in_slider(i)=100
			Next
		EndIf
		;Abdunkeln
		If gui_ibutton(236+37,542,Cicon_moon,sm$(18)) Then 
			For i=0 To 2
				in_slider(i)=in_slider(i)-5
				If in_slider(i)<0 Then in_slider(i)=0
			Next
		EndIf
		;Umkehren
		If gui_ibutton(236+37+37,542,Cicon_update,sm$(19)) Then 
			For i=0 To 2
				in_slider(i)=100-in_slider(i)
			Next
		EndIf
		
		;Small Color Matrix
		x=0:y=0
		For r=0 To 255 Step 255
			For g=0 To 255 Step 255
				For b=0 To 255 Step 255
					If r>255 Then r=255
					If g>255 Then g=255
					If b>255 Then b=255
					If gui_colorbox(r,g,b,543+(x*29),370+(y*37),24,32,Int(r)+","+Int(g)+","+Int(b)) Then
						in_slider(0)=Float(r)/2.55
						in_slider(1)=Float(g)/2.55
						in_slider(2)=Float(b)/2.55
					EndIf
					x=x+1
					If x>=8 Then
						x=0
						y=y+1
					EndIf
				Next
			Next
		Next
		x=0:y=0
		For r=0 To 128 Step 128 
			For g=0 To 128 Step 128 
				For b=0 To 128 Step 128 
					If gui_colorbox(r,g,b,543+(x*29),370+37+(y*37),24,32,Int(r)+","+Int(g)+","+Int(b)) Then
						in_slider(0)=Float(r)/2.55
						in_slider(1)=Float(g)/2.55
						in_slider(2)=Float(b)/2.55
					EndIf
					x=x+1
					If x>=8 Then
						x=0
						y=y+1
					EndIf
				Next
			Next
		Next
		
		
		;Cancel
		If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then quit=1
		;Ok
		If gui_ibutton(742,542,Cicon_ok,sm$(5)) Then quit=1:ret=1
		
		endinput(1,1,0)
		console()
		Flip
	Wend
	;End
	FreeImage temp
	FreeImage colorboard
	;Return
	flushinput()
	Return ret
End Function


;### File Window
Function gui_win_file(info$="",startdir$="",startfile$="",lockeddir=0,save=0)
	Local temp=CreateImage(set_scrx,set_scry)
	SetBuffer FrontBuffer():GrabImage temp,0,0:SetBuffer BackBuffer()
	gui_win_file_listdir(startdir$)
	If FileType(in_win_path$+startfile$)=2 Then
		in_win_file$=startfile$
	Else
		in_win_file$=""
	EndIf
	;Loop
	Local quit=0,ret=0
	While quit=0
		getinput(0)
		If KeyHit(1) Then quit=1
		DrawBlock temp,0,0
		
		;Head
		DrawImage gfx_win,215,0
		If gui_ibutton(742,5,Cicon_x,sm$(15)) Then quit=1
		DrawImage gfx_icons,236,5,Cicon_dir
		bmpf_txt(236+37,10,info$)
		DrawImage gfx_winbar,215,42
		
		;Path
		If in_win_path$<>"" Then
			bmpf_txt_ml(236,63,sm$(20)+": "+in_win_path$,0,236+537-37)
			If gui_ibutton(742,63,Cicon_parentdir,sm$(21),(1-lockeddir)) Then
				gui_win_file_parentdir()
				in_win_file$=""
			EndIf
		Else
			bmpf_txt(236,63,sm$(22)+":")
			gui_ibutton(742,63,Cicon_parentdir,sm$(21),0)
		EndIf
		
		;Black Rect
		Color 0,0,0
		Rect 236,100,537,435,1
		
		;List Files
		Local x=0,y=0,j=0
		For Tfile.Tfile=Each Tfile
			j=j+1
			If j>in_scr_scr Then
				;Click / Over
				over=0
				If in_mx>=236+(x*165) Then
					If in_my>=100+(y*20) Then
						If in_mx<=236+(x*165)+165 Then
							If in_my<=100+(y*20)+19 Then
								over=1
								;Tooltip
								gui_tt(in_mx,in_my,Tfile\name$)
								;Klick
								If in_mhit(1) Then
									If Tfile\typ=2 Then
										If lockeddir=0 Then
											gui_win_file_listdir(Tfile\path$+Tfile\name$)
											in_win_file$=""
											Exit
										EndIf
									Else
										If in_win_file$=Tfile\name$ Then
											quit=1
											ret=1
										Else
											in_win_file$=Tfile\name$
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				If Tfile\typ=1 Then
					If in_win_file$<>"" Then				
						If Tfile\name$=in_win_file$ Then
							over=1
						EndIf
					EndIf
				EndIf
				;Draw Icon
				If over=0 Then
					DrawImage gfx_icons_passive,236+(x*165),100+(y*20),Tfile\icon
				Else
					DrawImage gfx_icons,236+(x*165),100+(y*20),Tfile\icon
				EndIf
				
				;Name
				bmpf_txt_ml(236+(x*165)+32,100+(y*20)+5,Tfile\name$,over,236+(x*165)+32+140)	
				
				;Pos
				y=y+1
				If y>=21 Then
					y=0
					x=x+1
					If x>2 Then Exit
				EndIf
			EndIf
		Next
		
		
		;Scroll
		If gui_ibutton(236,542,Cicon_left,sm$(23),in_scr_scr>0) Then
			in_scr_scr=in_scr_scr-22
			If in_scr_scr<0 Then in_scr_scr=0
		EndIf
		If gui_ibutton(742-37-37-37,542,Cicon_right,sm$(24),x>2) Then
			in_scr_scr=in_scr_scr+22
		EndIf
		
		;File
		If save=1 Then
			in_input$(10)=in_win_file$
			gui_input(236+37,548,320,10,1,sm$(25))
			in_win_file$=in_input$(10)
		Else
			If in_win_file$<>"" Then
				bmpf_txt_ml(236+37,550,sm$(26)+": "+in_win_file$,0,236+300)
			Else
				bmpf_txt(236+37,550,sm$(27))
			EndIf
		EndIf
		
		
		;Cancel
		If gui_ibutton(742-37,542,Cicon_x,sm$(3)) Then quit=1
		;Ok
		If gui_ibutton(742,542,Cicon_ok,sm$(5),in_win_file$<>"") Then
			quit=1
			ret=1
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Wend
	;End
	FreeImage temp
	;Return
	flushinput()
	MoveMouse 742+16,542-4
	Return ret
End Function


;### List dir
Function gui_win_file_listdir(path$)
	;Check Path
	If path$<>"" Then
		path$=ntrim$(path$)
		path$=Replace(path$,"/","\")
		If Right(path$,1)<>"\" Then path$=path$+"\"
		;Is Path?
		If FileType(path$)=2 Then
			;Delete Old
			in_scr_scr=0
			For Tfile.Tfile=Each Tfile
				Delete Tfile
			Next
			;Set Path
			in_win_path$=path$
			;Open Dir
			stream=ReadDir(path$)
			;Read and add all files/dirs
			Repeat
				file$=NextFile(stream)
				If Len(file$)>2 Then
					;Add
					Tfile.Tfile=New Tfile
					Tfile\name$=file$
					Tfile\path$=path$
					Tfile\typ=FileType(path$+file$)
					If Tfile\typ=1 Then
						extp=Instr(file$,".")
						ext$=Lower(Mid(file$,extp+1,-1))
						Select ext$
							Case "wav","mp3","raw","ogg" Tfile\icon=Cicon_soundfile
							Case "bmp","jpg","jpeg","png","pcx","tga","iff" Tfile\icon=Cicon_imagefile
							Case "s2" Tfile\icon=Cicon_stranded
							Case "s2s" Tfile\icon=Cicon_s2script
							Default Tfile\icon=Cicon_file						
						End Select
					Else
						Tfile\icon=Cicon_dir
					EndIf
				ElseIf file$="" Then
					Exit
				EndIf
			Forever
			;Close Dir
			CloseDir(stream)
			;Okay
			Return 1
		EndIf
		;Failed
		Return 0
	;Empty Path - Get Drives! Yeah!
	Else
		;Delete Old
		in_scr_scr=0
		For Tfile.Tfile=Each Tfile
			Delete Tfile
		Next
		in_win_path$=""
		;Add A: without scan because floppy disk drive noise is crap!
		Tfile.Tfile=New Tfile:Tfile\name$="A:":Tfile\typ=2:Tfile\icon=Cicon_dir
		;Scan letters C to Z		
		For driveasc=67 To 90
			If FileType(Chr(driveasc)+":")=2 Then
				Tfile.Tfile=New Tfile
				Tfile\name$=Chr(driveasc)+":"
				Tfile\typ=2
				Tfile\icon=Cicon_dir
			EndIf
		Next
		;Okay
		Return 1
	EndIf
End Function


;### Goto Parent Dir
Function gui_win_file_parentdir()
	If Len(in_win_path$)<=3 Then
		gui_win_file_listdir("")
	Else
		Local l=Len(in_win_path$)-1
		Local lastslash=0
		For i=1 To l
			If Mid(in_win_path$,i,1)="\" Then
				lastslash=i
			EndIf
		Next
		If lastslash=0 Then
			gui_win_file_listdir("")
		Else
			in_win_path$=Left(in_win_path$,lastslash)
			gui_win_file_listdir(in_win_path$)
		EndIf
	EndIf
End Function


;### Map Window
Function gui_win_map(mp=0,jumpto$="")
	Local temp=CreateImage(set_scrx,set_scry)
	SetBuffer FrontBuffer():GrabImage temp,0,0
	;Load Maps
	load_maps(1,mp)
	;Jump to Map
	If jumpto$<>"" Then
		j=0
		For Tsg.Tsg=Each Tsg
			j=j+1
			If Tsg\name$=jumpto$ Then
				load_map_header("maps\"+Tsg\name$+".s2")
				in_opt(0)=j
				Exit
			EndIf
		Next
	EndIf
	;Loop
	Local quit=0,ret=0
	While quit=0
		getinput(0)
		If KeyHit(1) Then quit=1
		DrawBlock temp,0,0
		
		;Head
		DrawImage gfx_win,215,0
		If gui_ibutton(742,5,Cicon_x,sm$(15)) Then quit=1
		DrawImage gfx_icons,236,5,Cicon_dir
		bmpf_txt(236+37,10,sm$(28))
		DrawImage gfx_winbar,215,42
		
		;List Maps
		Local forceok=0
		Color 0,0,0
		Rect 236,195,500,379,1
		i=0:j=0
		y=0
		For Tsg.Tsg=Each Tsg
			j=j+1
			If j>in_scr_scr Then
				cache=in_opt(0)
				;Click
				If gui_opt(236+5,200+i*20,Tsg\name$,j,0) Then
					load_map_header("maps\"+Tsg\name$+".s2")
					;Click at already selected
					If in_opt(0)=cache Then
						forceok=1
					EndIf
				EndIf
				i=i+1
				If i>18 Then Exit
			EndIf
		Next
		
		;Scroll
		If gui_ibutton(742,200,Cicon_up,sm$(29),in_scr_scr>0) Then
			in_scr_scr=in_scr_scr-1
		EndIf
		If gui_ibutton(742,542-32-5,Cicon_down,sm$(30),i>18) Then
			in_scr_scr=in_scr_scr+1
		EndIf
		
		;Determine & Show
		loadtemp$=""
		i=0
		For Tsg.Tsg=Each Tsg
			i=i+1
			If i=in_opt(0) Then
				loadtemp$=Tsg\name$
			EndIf
		Next
		If loadtemp$="" Then
			Color 0,0,0
			Rect 236,63,96,72,1
			If mp=0 Then
				bmpf_txt(336,63,sm$(31),Cbmpf_red)
			Else
				bmpf_txt(336,63,sm$(32),Cbmpf_red)
			EndIf
		Else
			DrawBlock map_image,236,63
			bmpf_txt(336,63,sm$(33)+":")
			bmpf_txt(450,63,loadtemp$)
			bmpf_txt(336,83,sm$(34)+":")
			bmpf_txt(450,83,sg_date$)
			bmpf_txt(336,103,sm$(35)+":")
			bmpf_txt(450,103,sg_time$+" "+sm$(36))
		EndIf
		
		;Load
		If gui_ibutton(742,542,Cicon_ok,sm$(5),loadtemp$<>"") Or forceok=1 Then
			in_win_map$=loadtemp$
			quit=1:ret=1
		EndIf
			
		endinput(1,1,0)
		console()
		Flip
	Wend
	;End
	FreeImage temp
	;Return
	flushinput()
	Return ret
End Function

;### COMMAND WINDOW
Function gui_win_command()
	FlushKeys()
	FlushMouse()
	Local letter$="a"
	If in_sh_cmd$<>"" Then
		letter$=Lower(Left(in_sh_cmd$,1))
	EndIf
	Local updated=0
	Local group$=""
	Repeat
		getinput(0)
		TileBlock gfx_bg(1)
		bmpf_txt(5,5,se$(179))
		
		;Border
		DrawImage gfx_border(3),0,25
		
		;Letters
		Local x=5
		Local ble
		For i=97 To 122
			ble=bmpf_len(" "+Upper(Chr(i))+" ",Cbmpf_tiny)+3
			If gui_ttb(x,45," "+Upper(Chr(i))+" ",Asc(letter$)=i) Then
				letter$=Chr(i)
				group$=""
			EndIf
			x=x+ble
		Next
		
		;Border
		DrawImage gfx_border(3),0,68
		
		;Groups
		x=5
		yo=85
		For Tsgr.Tsgr=Each Tsgr
			ble=bmpf_len(" "+Tsgr\group$+" ",Cbmpf_tiny)+3
			If x+ble>795 Then
				x=5
				yo=yo+19
			EndIf
			If gui_ttb(x,yo," "+Tsgr\group$+" ",group$=Tsgr\group$) Then
				letter$="1"
				group$=Tsgr\group$
			EndIf
			x=x+ble
		Next
		
		;Border
		DrawImage gfx_border(3),0,yo+23
		
		
		;Commands
		Local comc=0
		x=5
		Local y=1
		Local sel
		Local selmode=0
		If letter$="1" Then
			selmode=1
		Else
			selmode=0
		EndIf
		For Tsc.Tsc=Each Tsc
			sel=0
			If selmode=0 Then
				If Left(Tsc\cmd$,1)=letter$ Then sel=1
			Else
				If Instr(Tsc\groups$,group$) Then sel=1
			EndIf
			If sel=1 Then
				comc=comc+1
				ble=bmpf_len(" "+Tsc\cmd$+" ",Cbmpf_tiny)+3
				If x+ble>795 Then
					x=5
					y=y+1
				EndIf
				If gui_ttb(x,yo+50+(y*19)," "+Tsc\cmd$+" ",0,Tsc\cmd$+Tsc\params$) Then
					Local l=Len(in_sh_cmd$)
					If l=0 Then
						gui_tb_insert(Tsc\cmd$)
					Else
						If Left(Tsc\cmd$,l)=in_sh_cmd$ Then
							gui_tb_insert(Mid(Tsc\cmd$,l+1,-1))
						Else
							gui_tb_insert(Tsc\cmd$)
						EndIf
					EndIf
					in_sh=0
					in_sh_cmd$=""
					in_sh_update=1
					Return 1
				EndIf
				x=x+ble
			EndIf
		Next
		
		;Count
		If comc=0 Then
			bmpf_txt(5,yo+46,"0 Commands",Cbmpf_tiny)
		ElseIf comc=1 Then
			bmpf_txt(5,yo+46,"1 Command",Cbmpf_tiny)
		Else
			bmpf_txt(5,yo+46,comc+" Commands",Cbmpf_tiny)
		EndIf
		
		;Update
		If set_inbb=1 Then
			updated=1
			If gui_ibutton(5,set_scry-37,Cicon_update,sm$(171),updated=1) Then
				con_open()
				con_add("")
				con_add("Updating...")
				console()
				Local tcp=OpenTCPStream("http://www.stranded.unrealsoftware.de",80)
				CopyFile("..\..\core\scriptcommands.inf","..\..\core\scriptcommands.bkp")
				Local stream=WriteFile("..\..\core\scriptcommands.inf")
				Local loadedlines=0
				If stream<>0 Then
					If tcp Then
						If 0=0 Then
							;Request A
							WriteLine tcp,"GET http://stranded.unrealsoftware.de/int_rawcmdlist.php HTTP/1.1"
							WriteLine tcp,"Host: stranded.unrealsoftware.de"
							WriteLine tcp,"User-Agent: Stranded II"
							WriteLine tcp,"Accept: */*"
							WriteLine tcp,""
							;Header
							Repeat
								in$=ReadLine(tcp)
								If in$="" Then Exit
							Forever
						Else
							;Request B
							WriteLine tcp,"GET http://stranded.unrealsoftware.de/int_rawcmdlist.php"
						EndIf
						;Data
						While Not Eof(tcp)
							console()
							in$=ReadLine(tcp)
							If set_debug=1 Then con_add(in$)
							;If in$="" Then
							;	Exit
							;Else
								If Len(in$)>2 Then
									If (Instr(in$,"Ś")>0) Then
										WriteLine(stream,in$)
										split(in$,"Ś",1)
										con_add("Loading '"+splits$(0)+"'...")
										loadedlines=loadedlines+1
									Else
										con_add("ERROR: Unexpected data '"+in$+"'",Cbmpf_red)
									EndIf
								EndIf
							;EndIf
							Flip
						Wend
						CloseTCPStream(tcp)
						If loadedlines=0 Then
							con_add("ERROR: Failed to read data",Cbmpf_red)
						Else
							con_add("Update DONE! ("+loadedlines+" commands loaded)",Cbmpf_green)
						EndIf
						updated=1
					Else
						con_add("ERROR: Failed to connect to Update Server",Cbmpf_red)
					EndIf
					CloseFile(stream)
				Else
					con_add("ERROR: Failed to write 'core\scriptcommands.inf'",Cbmpf_red)
				EndIf
				;Restore Backup?
				If loadedlines=0 Then
					CopyFile("..\..\core\scriptcommands.bkp","..\..\core\scriptcommands.inf")
				EndIf
				;Initiale Script Commands
				editor_ini_sc()
				;End
				console()
				Flip
				Delay 1500
				con_close()
			EndIf
		EndIf
		
		;Cancel
		If gui_ibutton(set_scrx-37,set_scry-37,Cicon_x,sm$(3),1,0,1) Then
			Return 0
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
	p_rectscreen()
End Function


;### GUI Window Defvars
Function gui_win_defvars(class,typ,id)
	FlushKeys()
	FlushMouse()
	Local i=0
	;Fill with values
	For i=10 To 30
		in_input$(i)=""
	Next
	i=10
	For Tdefvar.Tdefvar=Each Tdefvar
		If Tdefvar\class=class Then
			If Tdefvar\typ=typ Then
				in_input$(i)=Tdefvar\defaultval
				;Local
				If Tdefvar\isglobal=0 Then
					For Tx.Tx=Each Tx
						If Tx\mode=4 Then
							If Tx\parent_class=class Then
								If Tx\parent_id=id Then
									If Tx\key$=Tdefvar\key$ Then
										in_input$(i)=Tx\value$
										Exit
									EndIf
								EndIf
							EndIf
						EndIf
					Next
				;Global
				Else
					For Tx.Tx=Each Tx
						If Tx\mode=1 Then
							If Tx\key$=Tdefvar\key$ Then
								in_input$(i)=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				EndIf
				i=i+1
				If i>30 Then Exit
			EndIf
		EndIf
	Next
	;Draw
	Repeat
		getinput(0)
		TileBlock gfx_bg(1)
		bmpf_txt(5,5,se$(223))
		
		;Border
		DrawImage gfx_border(3),0,25
		
		;List
		i=10
		y=45
		For Tdefvar.Tdefvar=Each Tdefvar
			If Tdefvar\class=class Then
				If Tdefvar\typ=typ Then
					;Description
					bmpf_txt(5,y,Tdefvar\descr$+":")
					;Value Input
					gui_input(300,y,128,i)
					;Show / Reset Default
					If in_input$(i)<>Tdefvar\defaultval$ Then
						If gui_txtb(445,y,"Default: "+Tdefvar\defaultval$) Then
							in_input$(i)=Tdefvar\defaultval$
						EndIf
					Else
						bmpf_txt(445,y,"Default: "+Tdefvar\defaultval$,Cbmpf_dark)
					EndIf
					;Local / Global Var
					bmpf_txt(620,y,"$"+Tdefvar\key$,Cbmpf_tiny)
					If Tdefvar\isglobal=0 Then
						bmpf_txt_r(set_scrx-5,y,"L",Cbmpf_yellow)
					Else
						bmpf_txt_r(set_scrx-5,y,"G",Cbmpf_red)
					EndIf
					i=i+1
					y=y+25
					If i>30 Then Exit
				EndIf
			EndIf
		Next
		
		;Okay
		If gui_ibutton(set_scrx-37,set_scry-37,Cicon_ok,sm$(4),1,0,1) Then
			;Save
			i=10
			For Tdefvar.Tdefvar=Each Tdefvar
				If Tdefvar\class=class Then
					If Tdefvar\typ=typ Then
						;Default - Delete Vars
						If in_input$(i)=Tdefvar\defaultval$ Then
							If Tdefvar\isglobal=0 Then
								For Tx.Tx=Each Tx
									If Tx\mode=4 Then
										If Tx\parent_class=class Then
											If Tx\parent_id=id Then
												If Tx\key$=Tdefvar\key$ Then
													Delete Tx
												EndIf
											EndIf
										EndIf
									EndIf
								Next			
							Else
								For Tx.Tx=Each Tx
									If Tx\mode=1 Then
										If Tx\key$=Tdefvar\key$ Then
											Delete Tx
										EndIf
									EndIf
								Next							
							EndIf
						;Not Default - Create Vars
						Else
							If Tdefvar\isglobal=0 Then
								var_set_local(class,id,Tdefvar\key$,Int(in_input$(i)))
							Else
								var_set(Tdefvar\key$,Int(in_input$(i)))
							EndIf
						EndIf
						i=i+1
					EndIf
				EndIf
			Next
			Return 1
		EndIf
						
		;Cancel
		If gui_ibutton(set_scrx-37-37,set_scry-37,Cicon_x,sm$(3),1,0,1) Then
			Return 0
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
	p_rectscreen()
End Function


;### GUI Window Image
Function gui_win_image(path$,inimage=0)
	Local image
	If inimage<>0 Then
		image=inimage
	Else
		image=load_image(path$,0)
	EndIf
	If image<>0 Then MidHandle image
	FlushKeys()
	FlushMouse()
	Repeat
		getinput(0)
		ClsColor 0,0,0
		Cls
		
		;Image
		If image<>0 Then
			DrawImage image,set_scrx/2,set_scry/2
		EndIf

		;Okay
		If gui_ibutton(set_scrx/2-16,set_scry-37,Cicon_ok,sm$(5),1,0,28) Then
			Return 1
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
	If image<>0 Then FreeImage(image)
	p_rectscreen()
End Function


;### GUI Window Text
Function gui_win_text(txt$)
	FlushKeys()
	FlushMouse()
	Local scr=0
	Repeat
		getinput(0)
		ClsColor 0,0,0
		Cls
		
		;Description
		scrolldown=bmpf_txt_rect(5,5,set_scrx-15-32,set_scry-10,txt$,2,scr)
		If gui_ibutton(set_scrx-37,5,Cicon_up,sm$(29),scr>0) Then
			scr=scr-1
		EndIf
		If gui_ibutton(set_scrx-37,set_scry-37-37,Cicon_down,sm$(30),scrolldown=0) Then
			scr=scr+1
		EndIf		
		
		;Okay
		If gui_ibutton(set_scrx-37,set_scry-37,Cicon_ok,sm$(5),1,0,28) Then
			Return 1
		EndIf
		
		endinput(1,1,0)
		console()
		Flip
	Forever
	flushinput()
End Function


;### GUI Scrollbar
Function gui_scrollbar(x,y,h,total,offset,visible)
	tmp_slideend=0
	;No Scrollbar required
	If total<visible Then
		;Color 0,0,0
		;Rect x+8,y,16,h,0
		drawy(gfx_scroll(0),x+14,y,h)
		Return
	EndIf
	;Vars
	newo=offset
	overbar=0
	over=0
	range=(total+1)-visible
	slideh=Float(h)/Float(range)
	If slideh<16 Then slideh=16
	nh=h-slideh
	;Slider
	slidey=Float(Float(offset)/Float(range))*Float(nh)
	slidey=slidey+y
	If in_mx>=x Then
		If in_mx<=x+32 Then
			If in_my>=slidey Then
				If in_my<=slidey+slideh Then
					over=1
					If in_mhit(1) Then
						in_sticktoslide=1
						in_sticktoslidex=x
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	If in_sticktoslide=1 Then
		If in_sticktoslidex>=x And in_sticktoslidex<=x+32 Then
			in_mx=x+16
			yo=in_my-y
			yo=yo-(slideh/2)
			newo=Float(Float(yo)/Float(nh))*Float(range)
			If newo<0 Then newo=0
			If newo>range Then newo=range
			over=1
		EndIf
	EndIf
	;Scrollbar
	If over=0 Then
		If in_mx>=x Then
			If in_mx<=x+32 Then
				If in_my>=y Then
					If in_my<=y+h Then
						overbar=1
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	If overbar=1 Then
		;Color 50,50,50
		;Click
		If over=0 Then
			If in_mhit(1) Then
				yo=in_my-y
				yo=yo-(slideh/2)
				newo=Float(Float(yo)/Float(nh))*Float(range)
				If newo<0 Then newo=0
				If newo>range Then newo=range
			EndIf
		EndIf
	Else
		;Color 0,0,0
	EndIf
	;Rect x+14,y,4,h,1
	drawy(gfx_scroll(over),x+14,y,h)
	;Draw Slider
	If over=1 Then
		;Color 50,50,50
	Else
		;Color 0,0,0
	EndIf
	;Rect x+8,slidey,16,slideh
	DrawImage gfx_scroll_bar(0),x+8,slidey
	drawy(gfx_scroll_bar(1),x+8,slidey+4,slideh-8)
	DrawImage gfx_scroll_bar(2),x+8,slidey+slideh-4
	If in_sticktoslide=1 Then
		If in_sticktoslidex>=x And in_sticktoslidex<=x+32 Then
			in_my=slidey+(slideh/2)
			in_sticktoslidex=in_mx
			in_sticktoslidey=in_my
			in_cursorf=1
		EndIf
	EndIf
	;Return
	If newo<range Then tmp_slideend=1
	Return newo
End Function

;Drag and Drop Watch (returns 1 when clicked)
Function gui_dndwatch(x,y)
	;Over
	Local over
	If in_mx>=x Then
		If in_my>=y Then 
			If in_mx<=x+40 Then
				If in_my<=y+40 Then
					over=1
				EndIf
			EndIf
		EndIf
	EndIf
	If over=1 Then
		If in_mhit(1) Then
			Return 1
		EndIf
	EndIf
	Return 0
End Function

;GUI Groupsel
Function gui_groupsel(x,y,class)
	txt$=ed_groupn$(class)
	If txt$="" Then txt$=se$(225)
	If in_edselgroup=0 Then
		If in_editor_sidebar=1 Then
			If gui_txtb(x,y,txt$) Then
				in_edselgroup=1
			EndIf
		EndIf
		Return 1
	Else
		If in_editor_sidebar=1 Then
			If gui_txtb(x,y,txt$,1,Cbmpf_dark) Then
				in_edselgroup=0
			EndIf	
			y=y+30
			If gui_txtb(x,y,se$(225)) Then
				ed_groupg$(class)=""
				ed_groupn$(class)=""
				in_edselgroup=0
			EndIf
			For g.Tgroup=Each Tgroup
				If g\class=class Then
					y=y+20
					If gui_txtb(x,y,g\name) Then
						ed_groupg$(class)=g\group
						ed_groupn$(class)=g\name
						in_edselgroup=0
						Select class
							Case Cclass_object in_object_scr=0
							Case Cclass_unit in_unit_scr=0
							Case Cclass_item in_item_scr=0
							Case Cclass_info in_info_scr=0
						End Select
					EndIf				
				EndIf
			Next
		EndIf
		Return 0
	EndIf
End Function
