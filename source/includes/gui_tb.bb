;############################################ GUI TEXTBOX FUNCTIONS (gui_tb_)

;### Textbox (Main Function, Draws Textbox, in_inputfocus=1337)
Function gui_tb(rx,ry,w,rowsh,info=0,scripthelper=0)
	Local h=rowsh*15
	;BG
	Color 0,0,0
	Rect rx,ry,w,h	
	;Text
	Local y=ry
	Local i
	Local cursorline$
	Local cursorx,cursory
	Local cursordraw=0
	Local rows=Floor(h/15)
	;Scripthelper Update?
	Local shu=0
	If in_sh_update=1 Then
		shu=1
		in_sh_update=0
	EndIf
	
	;Mark
	;Bring Mark Start/End Row and Col in correct Order!
	If in_mark>0 Then
		Local ms_row,ms_col
		Local me_row,me_col
		If in_ms_row<in_me_row Then
			ms_row=in_ms_row:ms_col=in_ms_col
			me_row=in_me_row:me_col=in_me_col
		ElseIf in_ms_row>in_me_row Then
			ms_row=in_me_row:ms_col=in_me_col
			me_row=in_ms_row:me_col=in_ms_col
		Else
			If in_ms_col<in_me_col Then
				ms_row=in_ms_row:ms_col=in_ms_col
				me_row=in_me_row:me_col=in_me_col
			Else
				ms_row=in_me_row:ms_col=in_me_col
				me_row=in_ms_row:me_col=in_ms_col
			EndIf
		EndIf
	EndIf
	
	;Draw Lines
	i=0
	For Ttbt.Ttbt=Each Ttbt
		;Cache selected Row
		If i=in_tb_row Then cursorline$=Ttbt\txt$
		;Is Line in visible Area?
		If i>=in_tb_scry And y<=(ry+h-15) Then
			;Darkback
			Color 28,17,0
			Rect rx+1,y+1,w-2,14,1
			
			;Markback if marked
			If in_mark>0 Then
				If i>ms_row And i<me_row Then
					Color 84,51,0
					markbacklen=bmpf_txt_ml_len(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3)+2
					If markbacklen>(w-2) Then markbacklen=w-2
					Rect rx+1,y+1,markbacklen,14,1
				EndIf
			EndIf
			
			;Clickline/Releaseline - Set Cursorpos
			If in_mx>=rx-15 And in_mx<=rx+w Then
				If in_my>=y And in_my<y+15 Then
				
					in_cursorf=5
					
					;Click
					If in_mhit(1) Then
						;Set Cursor
						cursorline$=Ttbt\txt$
						in_tb_row=i
						in_tb_col=in_tb_scrx+bmpf_txt_pos(Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,in_mx-(rx+2))
						If in_tb_col<0 Then in_tb_col=0
						If in_tb_col>Len(cursorline$) Then in_tb_col=Len(cursorline$)
						;Set Mark Start
						in_mark=1
						in_ms_col=in_tb_col
						in_ms_row=in_tb_row
						in_me_col=999999
						in_me_row=999999
						;Script Helper Update
						shu=1
					EndIf
					
					;Down
					If in_mdown(1) Then
						If in_mark=1 Then
							cursorline$=Ttbt\txt$
							in_tb_row=i
							in_tb_col=in_tb_scrx+bmpf_txt_pos(Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,in_mx-(rx+2))
							If in_tb_col<0 Then in_tb_col=0
							If in_tb_col>Len(cursorline$) Then in_tb_col=Len(cursorline$)
							;Set
							in_me_col=in_tb_col
							in_me_row=in_tb_row
							;Script Helper Reset
							shu=-1
						EndIf
					EndIf
					
					;Release
					If in_mrelease(1) Then
						in_inputfocus=1337
						If in_mark=1 Then
							;Set Cursor
							cursorline$=Ttbt\txt$
							in_tb_row=i
							in_tb_col=in_tb_scrx+bmpf_txt_pos(Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,in_mx-(rx+2))
							If in_tb_col<0 Then in_tb_col=0
							If in_tb_col>Len(cursorline$) Then in_tb_col=Len(cursorline$)
							;Set Mark End
							in_mark=2
							in_me_col=in_tb_col
							in_me_row=in_tb_row
							;Swap Mark Start/End?
							If (in_me_row<in_ms_row) Or ((in_ms_row=in_me_row)And(in_me_col<in_ms_col)) Then
								x=in_me_row:in_me_row=in_ms_row:in_ms_row=x
								x=in_me_col:in_me_col=in_ms_col:in_ms_col=x
							EndIf
							;Nothing Selected?
							If in_ms_row=in_me_row Then
								If in_ms_col=in_me_col Then in_mark=0
							EndIf
							
							;DebugLog "MARK: "+in_mark+" FROM: "+in_ms_row+","+in_ms_col+" TO "+in_me_row+","+in_me_col
							
							;Script Helper Update
							shu=1
						EndIf
					EndIf
				EndIf
			EndIf
			
			;Draw Text
			If in_mark>0 Then
				If i=ms_row Or i=me_row Then
					If i=ms_row And i=me_row Then
						bmpf_txt_ml_marked(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3,ms_col,me_col)
					ElseIf i=ms_row Then
						bmpf_txt_ml_marked(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3,ms_col,999999)
					Else
						bmpf_txt_ml_marked(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3,0,me_col)
					EndIf
				Else
					bmpf_txt_ml(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3)
				EndIf
			Else
				bmpf_txt_ml(rx+2,y,Mid(Ttbt\txt$,in_tb_scrx+1,-1),Cbmpf_tiny,rx+w-3)
			EndIf
			
			;Cursor
			If i=in_tb_row Then
				If in_tb_col-in_tb_scrx>=0 Then
					cursordraw=1
					cursorx=rx+1+bmpf_len(Mid(cursorline$,in_tb_scrx+1,in_tb_col-in_tb_scrx),Cbmpf_tiny)
					cursory=y-1
					If in_inputfocus=1337 Then
						If in_cursor Then bmpf_txt(cursorx,cursory,"|",Cbmpf_tiny)
					EndIf
				EndIf
			EndIf
			
			;Inc Y
			y=y+15	
			
		EndIf
		
		;Inc Line
		i=i+1
	Next
	
	;Input - Control Cursor
	If in_inputfocus=1337 And in_console=0 Then
		If KeyHit(200) Then in_tb_row=in_tb_row-1:in_mark=0:shu=1 			;Cursor Up
		If KeyHit(208) Then in_tb_row=in_tb_row+1:in_mark=0:shu=1			;Cursor Down
		If KeyHit(203) Then in_tb_col=in_tb_col-1:in_mark=0:shu=1			;Cursor Left
		If KeyHit(205) Then in_tb_col=in_tb_col+1:in_mark=0:shu=1			;Cursor Right
		If KeyHit(199) Then in_tb_col=0:in_tb_scrx=0:in_mark=0:shu=1		;Cursor Pos 1
		If KeyHit(207) Then in_tb_col=Len(cursorline$):in_mark=0:shu=1		;Cursor End
		If KeyHit(201) Then in_tb_row=0:in_mark=0:shu=1						;Cursor PgUp
		If KeyHit(209) Then in_tb_row=i-1:in_mark=0:shu=1					;Cursor PgDown
		If in_mzs#<>0.0 Then												;Cursor Up/Down Wheel
			in_tb_row=in_tb_row+in_mzs#*(-5)
		EndIf
	EndIf

	;Check
	If in_tb_row<0 Then in_tb_row=0
	If in_tb_row>i-1 Then in_tb_row=i-1
	If in_tb_col<0 Then in_tb_col=0
	If in_tb_col>Len(cursorline$) Then in_tb_col=Len(cursorline$)
	
	;Get Cursorline
	i=0
	For gl.Ttbt=Each Ttbt
		If i=in_tb_row Then
			cursorline$=gl\txt$
			Exit
		EndIf
		i=i+1
	Next
	
	;Input - Write
	If in_inputfocus=1337 And in_console=0 Then
		
		;Get Ctrl
		Local ctrlkey=0
		ctrlkey=(KeyDown(29) Or KeyDown(157))
		
		;Cut / Copy / Paste
		Local cutkey=KeyHit(45)
		Local copykey=KeyHit(46)
		Local pastekey=KeyHit(47)
		
		;##################################### UNMARKED
		If in_mark=0 Then
			
			Local key=in_key
			Local changed=0
			;INS
			If (key>31 And key<127) Or (Instr("ÄäÖöÜüß",Chr(key))>0) Then
				cursorline$=gui_tb_act_ins$(cursorline$,in_tb_col,Chr(key))
				changed=1
				shu=1
			;TAB
			ElseIf key=9 Then
				cursorline$=gui_tb_act_ins$(cursorline$,in_tb_col,"   ")
				changed=1
				shu=1
			;DEL
			ElseIf key=4 Then
				shu=1
				cache$=cursorline$
				cursorline$=gui_tb_act_del$(cursorline$,in_tb_col)
				If cache=cursorline$ Then
					j=0
					For Ttbt.Ttbt=Each Ttbt
						If j=in_tb_row+1 Then
							cache$=Ttbt\txt$
							Delete Ttbt
							Ttbt.Ttbt=Before Ttbt
							Ttbt\txt$=Ttbt\txt$+cache$
							in_tb_row=in_tb_row-1
							in_tb_col=Len(Ttbt\txt)
							Exit
						EndIf
						j=j+1
					Next
				Else
					changed=1
				EndIf
			;BS
			ElseIf key=8 Then
				shu=1
				cache$=cursorline$
				cursorline$=gui_tb_act_bs$(cursorline$,in_tb_col)
				If cache=cursorline$ Then
					If in_tb_row>0 Then
						j=0
						For Ttbt.Ttbt=Each Ttbt
							If j=in_tb_row Then
								cache$=Ttbt\txt$
								Delete Ttbt
								Ttbt.Ttbt=Before Ttbt
								Ttbt\txt$=Ttbt\txt$+cache$
								in_tb_row=in_tb_row-1
								in_tb_col=Len(Ttbt\txt)
								Exit
							EndIf
							j=j+1
						Next
					EndIf
				Else
					changed=1
				EndIf
			;Break
			ElseIf key=13 Then
				shu=1
				gui_tb_act_break()
			EndIf
			;Ctrl
			If ctrlkey Then
				shu=1
				;Paste
				If pastekey Then
					cursorline$=gui_tb_act_ins$(cursorline$,in_tb_col,gui_tb_tr2(paste()))
					jmod=0
					For Tmodify.Ttbt=Each Ttbt 
						If jmod=in_tb_row Then
							Tmodify\txt$=cursorline$
							Exit
						EndIf
						jmod=jmod+1
					Next				
					gui_tb_resplit()
				EndIf
			EndIf			
			;Modify
			If changed=1 Then
				jmod=0
				For Tmodify.Ttbt=Each Ttbt 
					If jmod=in_tb_row Then
						Tmodify\txt$=cursorline$
						Exit
					EndIf
					jmod=jmod+1
				Next
				changed=0
			EndIf
			
		;##################################### MARKED
		Else
			
			key=in_key
			;INS
			If (key>31 And key<127) Then
				shu=1
				gui_tb_replacemarked(Chr(key))
				in_mark=0
			;TAB
			ElseIf key=9 Then
				shu=1
				gui_tb_replacemarked("   ")
				in_mark=0
			;DEL
			ElseIf key=4 Then
				shu=1
				gui_tb_replacemarked("")
				in_mark=0
			;BS
			ElseIf key=8 Then
				shu=1
				gui_tb_replacemarked("")
				in_mark=0
			;Break
			ElseIf key=13 Then
				shu=1
				gui_tb_replacemarked("")
				in_mark=0
			EndIf
			
			;Ctrl
			If ctrlkey Then
				;Cut
				If cutkey Then
					copy(gui_tb_tr1(gui_tb_tostrmarked$()))
					gui_tb_replacemarked("")
					in_mark=0
					shu=1
				;Copy
				ElseIf copykey Then
					copy(gui_tb_tr1(gui_tb_tostrmarked$()))
					shu=1
				;Paste
				ElseIf pastekey Then
					gui_tb_replacemarked(gui_tb_tr2(paste()))
					gui_tb_resplit()
					in_mark=0
					shu=1
				EndIf
			EndIf
			
		EndIf
		
	EndIf
		
	;Scroll to Cursor X
	While cursorx>=(rx+w-5)
		in_tb_scrx=in_tb_scrx+1
		cursorx=rx+bmpf_len(Mid(cursorline$,in_tb_scrx+1,in_tb_col-in_tb_scrx),Cbmpf_tiny)
	Wend
	While cursorx<(rx)
		in_tb_scrx=in_tb_scrx-1
		If in_tb_scrx<=0 Then in_tb_scrx=0:Exit
		cursorx=rx+bmpf_len(Mid(cursorline$,in_tb_scrx+1,in_tb_col-in_tb_scrx),Cbmpf_tiny)
	Wend
	;Scroll to Cursor Y
	If cursordraw=0 Then
		While in_tb_row<in_tb_scry
			in_tb_scry=in_tb_scry-1
			If in_tb_scry<=0 Then in_tb_scry=0:Exit
		Wend
		While in_tb_row>(in_tb_scry+rows-1)
			in_tb_scry=in_tb_scry+1
			If in_tb_scry>=i-1 Then in_tb_scry=i-1:Exit
		Wend
	EndIf
	
	;Info
	If info Then bmpf_txt(rx,ry+h+1,sm$(37)+": "+in_tb_row+" "+sm$(38)+": "+in_tb_col,Cbmpf_tiny)
	
	;Scripthelper
	If in_inputfocus=1337 Then
		If scripthelper Then
			If shu=1 Then
				gui_tb_scripthelper(cursorline$,in_tb_col,cursorx,cursory)
			ElseIf shu=-1 Then
				in_sh=0
			EndIf
		EndIf
	EndIf
	
End Function

;### Textbox Clear
Function gui_tb_clear(oneline=0)
	in_tb_row=0
	in_tb_col=0
	in_tb_scrx=0
	in_tb_scry=0
	For Ttbt.Ttbt=Each Ttbt
		Delete Ttbt
	Next
	If oneline Then Ttbt.Ttbt=New Ttbt
End Function

;### Textbox Add Line
Function gui_tb_addline(txt$)
	Ttbt.Ttbt=New Ttbt
	Ttbt\txt$=txt$
End Function

;### Textbox Action: Insert
Function gui_tb_act_ins$(txt$,pos,ins$)
	Local strlen=Len(txt$)
	Local p1$,p2$
	in_tb_col=in_tb_col+Len(ins$)
	If pos=0 Then Return ins$+txt$
	If pos>=strlen Then Return txt$+ins$
	p1$=Mid(txt$,1,pos)
	p2$=Mid(txt$,pos+1,-1)
	Return (p1$+ins$+p2$)
End Function

;### Textbox Action: Del
Function gui_tb_act_del$(txt$,pos)
	Local strlen=Len(txt$)
	Local p1$,p2$
	If pos=0 Then Return Mid(txt$,2,-1)
	If pos>=strlen Then Return txt$
	If pos=strlen-1 Then Return Left(txt$,strlen-1)
	p1$=Mid(txt$,1,pos)
	p2$=Mid(txt$,pos+2,-1)
	Return (p1$+p2$)
End Function

;### Textbox Action: BS
Function gui_tb_act_bs$(txt$,pos)
	Local strlen=Len(txt$)
	Local p1$,p2$
	If pos=0 Then Return txt$
	If pos>=strlen Then in_tb_col=in_tb_col-1:Return Left(txt$,strlen-1)
	If pos=1 Then in_tb_col=in_tb_col-1:Return Mid(txt$,2,-1)
	p1$=Mid(txt$,1,pos-1)
	p2$=Mid(txt$,pos+1,-1)
	in_tb_col=in_tb_col-1
	Return (p1$+p2$)
End Function

;### Textbox Action: break
Function gui_tb_act_break()
	Local j=0
	Local p1$,p2$
	Local pos=in_tb_col
	For Ttbt.Ttbt=Each Ttbt
		If j=in_tb_row Then
			;New empty Line after current
			If in_tb_col=Len(Ttbt\txt$) Then
				TtbtNEW.Ttbt=New Ttbt
				Insert TtbtNEW.Ttbt After Ttbt
				in_tb_row=in_tb_row+1
				in_tb_col=0:in_tb_scrx=0
			;New empty Line before current
			ElseIf  in_tb_col=0 Then
				TtbtNEW.Ttbt=New Ttbt
				Insert TtbtNEW.Ttbt Before Ttbt
				in_tb_col=0:in_tb_scrx=0
			;New Line with splittet content
			Else
				p1$=Mid(Ttbt\txt$,1,pos)
				p2$=Mid(Ttbt\txt$,pos+1,-1)
				Ttbt\txt$=p1$
				TtbtNEW.Ttbt=New Ttbt
				TtbtNEW\txt$=p2$
				Insert TtbtNEW.Ttbt After Ttbt
				in_tb_row=in_tb_row+1
				in_tb_col=0:in_tb_scrx=0
			EndIf
			Exit
		EndIf
		j=j+1
	Next
End Function

;### Replace Marked
Function gui_tb_replacemarked(txt$)
	Local i
	Local s$,e$
	For Ttbt.Ttbt=Each Ttbt
		;Start or End Row
		If i=in_ms_row Or i=in_me_row Then
			
			;One Line
			If i=in_ms_row And i=in_me_row Then
				s$=Left(Ttbt\txt$,in_ms_col)
				e$=Mid(Ttbt\txt$,in_me_col+1,-1)
				Ttbt\txt$=s$+txt$+e$
				Return 1
			;Start
			ElseIf i=in_ms_row Then
				If in_ms_col>0 Then
					s$=Left(Ttbt\txt$,in_ms_col+1)
				EndIf
			;End
			Else
				If in_me_col<Len(Ttbt\txt$+1) Then
					e$=Mid(Ttbt\txt$,in_me_col+1,-1)
				EndIf
			EndIf
		EndIf
		
		;Delete Row
		If i=>in_ms_row And i<=in_me_row Then Delete Ttbt
		
		;Add
		If i=in_me_row Then
			Ttbt.Ttbt=New Ttbt
			Ttbt\txt$=s$+txt$+e$
			Return 1
		EndIf
		
		i=i+1
	Next
End Function


;### To String Marked
Function gui_tb_tostrmarked$()
	Local i
	Local s$,e$,full$
	Local endingbreak=0
	For Ttbt.Ttbt=Each Ttbt
		;Start or End Row
		If i=in_ms_row Or i=in_me_row Then
			
			;One Line
			If i=in_ms_row And i=in_me_row Then
				Return Mid(Ttbt\txt$,in_ms_col+1,(in_me_col-in_ms_col))
			;Start
			ElseIf i=in_ms_row Then
				s$=Mid(Ttbt\txt$,in_ms_col+1,-1)
			;End
			Else
				e$=Left(Ttbt\txt$,in_me_col+1)
				If e$=Ttbt\txt$ Then endingbreak=1
			EndIf
		EndIf
		
		;Add Row
		If i>in_ms_row And i<in_me_row Then
			full$=full$+"Ś"+Ttbt\txt$
		EndIf
		
		i=i+1
	Next
	
	If full$="" Then
		txt$=s$+"Ś"+e$
	Else
		txt$=s$+full$+"Ś"+e$
	EndIf
	If endingbreak=1 Then txt$=txt$+"Ś"
	
	Return txt$
End Function


;### Resplit (after Paste!)
Function gui_tb_resplit()
	tmp$=gui_tb_savestring("Ś")
	gui_tb_loadstring(tmp$,"Ś",0)
End Function


;### Textbox Loadstring
Function gui_tb_loadstring(txt$,sep$="",clearmode=1)
	If clearmode=1 Then
		gui_tb_clear()
	Else
		For Ttbt.Ttbt=Each Ttbt
			Delete Ttbt
		Next
	EndIf
	If sep$="" Or txt$="" Then
		Ttbt.Ttbt=New Ttbt:Ttbt\txt$=txt$
	Else
		Local substr$=txt$
		Local pos
		While Instr(substr$,sep$)
			pos=Instr(substr$,sep$)
			Ttbt.Ttbt=New Ttbt
			Ttbt\txt$=Left(substr$,pos-1)
			substr$=Mid(substr$,pos+1,-1)
		Wend
		If substr$<>"" Then
			Ttbt.Ttbt=New Ttbt
			Ttbt\txt$=substr$
		EndIf
	EndIf
End Function


;### Textbox Savestring
Function gui_tb_savestring$(sep$="")
	Local txt$
	For Ttbt.Ttbt=Each Ttbt
		txt$=txt$+Ttbt\txt$+sep$
	Next
	Return txt$
End Function


;### Textbox Loadfile
Function gui_tb_loadfile(file$)
	stream=ReadFile(file$)
	If stream=0 Then Return 0
	For Ttbt.Ttbt=Each Ttbt
		Delete Ttbt
	Next
	While Not Eof(stream)
		Ttbt.Ttbt=New Ttbt
		Ttbt\txt$=ReadLine(stream)
		Ttbt\txt$=Replace(Ttbt\txt$,Chr(9),"   ")
	Wend
	CloseFile(stream)
	Return 1
End Function


;### Textbox Savefile
Function gui_tb_savefile(file$)
	stream=WriteFile(file$)
	If stream=0 Then Return 0
	For Ttbt.Ttbt=Each Ttbt
		txt$=Replace(Ttbt\txt$,"   ",Chr(9))
		WriteLine stream,txt$
	Next
	CloseFile(stream)
	Return 1
End Function


;### Textbox filled?
Function gui_tb_filled()
	For Ttbt.Ttbt=Each Ttbt
		If Len(Trim(Ttbt\txt$))>0 Then Return 1
	Next
	Return 0
End Function


;### Textbox insert
Function gui_tb_insert(txt$)
	;Replace
	txt$=Replace(txt$,Chr(9),"   ")			;Tabs
	txt$=Replace(txt$,Chr(13)+Chr(10),"Ś")	;Linebreaks
	
	;DebugLog "insert: "+txt$
	Local row=0
	Local p1$,p2$
	For Ttbt.Ttbt=Each Ttbt
		;DebugLog row+" -> "+in_tb_row
		If row=in_tb_row Then
			p1$=Left(Ttbt\txt$,in_tb_col)
			p2$=Mid(Ttbt\txt$,in_tb_col+1,-1)
			Ttbt\txt$=p1$+txt$+p2$
			in_tb_col=in_tb_col+Len(txt$)
			Return 1
		EndIf
		row=row+1
	Next
	Return 0
End Function



;### Textbox Scripthelper
Function gui_tb_scripthelper(txt$,col,x,y)
	;Reset
	in_sh=0
	in_sh_txt$=""
	in_sh_cmd$=""
	;Cancel on Empty / 1 Sign Line
	If col<2 Then Return 0
	;Extract possible Command
	txt$=Left(txt$,col)
	Local cmd$
	Local s
	For i=col To 1 Step -1
		s=Asc(Mid(txt$,i,1))
		If s<97 Then
			If s<>95 Then Exit
		EndIf
		If s>122 Then Exit
	Next
	If i=0 Then
		cmd$=txt$
	Else
		cmd$=Mid(txt$,i+1,-1)
	EndIf
	If Len(cmd$)<2 Then Return 0
	in_sh_cmd$=cmd$
	;Find Command in Reference
	For Tsc.Tsc=Each Tsc
		If Tsc\cmd$=cmd$ Then
			in_sh=1
			in_sh_txt$=Tsc\cmd$+Tsc\params$ ;+" ("+Tsc\groups$+")"
			in_sh_x=x+20
			in_sh_y=y-20
			Exit
		EndIf
	Next
	
	;DebugLog cmd$
End Function


;### Textbox Scripthelper List
Function gui_tb_scripthelper_list()
	Local l=Len(in_sh_cmd$)
	If l<2 Then Return 0
	Local ble
	Local x=236+37+37+37+5+37+37
	Local y=0
	;Find Similiar Commands
	For Tsc.Tsc=Each Tsc
		If Left(Tsc\cmd$,l)=in_sh_cmd$ Then
			ble=bmpf_len(Tsc\cmd$,Cbmpf_tiny)+3
			If x+ble>704 Then
				x=236+37+37+37+5+37+37
				y=y+1
				If y=2 Then Return 2	
			EndIf
			If gui_ttb(x,541+(y*19),Tsc\cmd$,0,Tsc\cmd$+Tsc\params$) Then
				gui_tb_insert(Mid(Tsc\cmd$,l+1,-1))
				in_sh=0
				in_sh_cmd$=""
				in_sh_update=1
			EndIf
			x=x+ble
		EndIf
	Next
End Function


;### Transform Functions

;SII -> Normal
Function gui_tb_tr1$(t$)
	;Tab
	t$=Replace(t$,"   ",Chr(9))
	;Break
	t$=Replace(t$,"Ś",Chr(13)+Chr(10))
	;Return
	Return t$
End Function

;Normal -> SII
Function gui_tb_tr2$(t$)
	;Tab
	t$=Replace(t$,Chr(9),"   ")
	;Break
	t$=Replace(t$,Chr(13)+Chr(10),"Ś")
	;Return
	Return t$
End Function
