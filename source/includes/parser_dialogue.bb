Function parse_dialogue(page$)
	;Special Button Action?
	Local dd=Instr(page$,":")
	If dd>0 Then
		Local action$=Left(page$,dd-1)
		Local param$=Trim(Mid(page$,dd+1,-1))
		Select action$
			Case "action"
				Select param
					Case "close" m_menu=0 : Return 0
				End Select
			Case "script"
				parse_task(0,0,"","dialogue button script",param$)
				Return 0
			Case "event"
				parse_globalevent(param$,"dialogue button event")
		End Select
	EndIf
	
	;Reset Text/Script/Scroll 
	in_msgtext$=""
	script$=""
	in_scr_scr=0
	;Reset Buttons
	For i=0 To 9
		in_sb(i)=0
	Next
	;Reset Trades
	For Trd.Ttrade=Each Ttrade
		Delete Trd
	Next
	;Set Dialogue Page
	in_dlg_page$=page$
	;Count Lines
	i=0
	c=1
	l=Len(in_dlg$)
	For i=1 To l
		If Mid(in_dlg$,i,1)="Ś" Then c=c+1
	Next
	;Split Lines
	split$(in_dlg$,"Ś",c)
	;Parse
	bi=0				;Button Index
	ti=0				;Trade Index
	foundpage=0			;Found right page?
	
	;Scan each Line
	i=0
	While i<c
		txt$=splits$(i)
		
		;Only Parse Lines wih Assignment (=) and without Comment (#)
		equal=Instr(txt$,"=")
		If Left(txt$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(txt$,equal-1))
			val$=Trim(Mid(txt$,equal+1,-1))
			
			;Find Page
			If foundpage=0 Then	
				If var$="page" Then
					If val$=page$ Then foundpage=1
				EndIf
			;Parse Page	if right Page has been found
			Else
				Select var$
					;-------------------- Page: End Parsing of this Page
					Case "page"
						Exit
					;-------------------- Title: Set Title
					Case "title"
						in_msgtitle$=val$
					;-------------------- Button: Setup Button
					Case "button"
						If bi<10 Then
							co=Instr(val$,",")
							If co>0 Then	
								in_sb(bi)=1
								in_sb_icon(bi)=Cicon_ok
								in_sb_txt$(bi)=trimspace(Mid(val$,co+1,-1))
								in_sb_scr$(bi)=trimspace(Left(val$,co-1))
								in_sb_handle(bi)=0
								bi=bi+1
							Else
								parse_error("dialogue 'button' expects 'page,text' (no comma found)")
							EndIf
						Else
							parse_error("there is a maximum of 10 buttons in a dialogue")
						EndIf
					;-------------------- iButton: Setup Button with certain Icon
					Case "ibutton"
						If bi<10 Then
							co=Instr(val$,",")
							co2=Instr(val$,",",co+1)
							If co>0 And co2>0 Then	
								in_sb(bi)=1
								in_sb_txt$(bi)=trimspace(Mid(val$,co2+1,-1))
								in_sb_scr$(bi)=trimspace(Mid(val$,co+1,co2-(co+1)))
								in_sb_handle(bi)=0
								frame$=Trim(Left(val$,co-1))
								If Len(frame$)>2 Then
									in_sb_handle(bi)=load_res(frame$,Cres_image,1)
									If in_sb_handle(bi)=0 Then
										in_sb_icon(bi)=Cicon_ok
									Else
										in_sb_icon(bi)=-1
									EndIf
								Else
									in_sb_icon(bi)=Int(frame$)
								EndIf
								bi=bi+1
							Else
								parse_error("dialogue 'button' expects 'icon,page,text' (not enough commas found)")
							EndIf
						Else
							parse_error("there is a maximum of 10 buttons in a dialogue")
						EndIf
					;-------------------- Script: Add a Script
					Case "script"
						If val$="start" Then
							i=i+1
							While i<c
								val$=splits$(i)
								If val$="script=end" Then
									Exit
								Else
									script$=script$+val$+"Ś"
								EndIf
								i=i+1
							Wend
						EndIf
					;-------------------- Text: Add a Text
					Case "text"
						If val$="start" Then
							i=i+1
							While i<c
								val$=splits$(i)
								If val$="text=end" Then
									Exit
								Else
									in_msgtext$=in_msgtext$+val$+"Ś"
								EndIf
								i=i+1
							Wend
						EndIf
						
					;-------------------- Trade: Add a Trade
					Case "trade"
						Local sellsc
						Local buysc
						If val$="start" Then
							sellsc=0
							buysc=0
							ti=ti+1
							i=i+1
							Trd.Ttrade=New Ttrade
							Trd\id=ti
							Trd\mode=0
							While i<c
								txt$=splits$(i)
								If txt$="trade=end" Then
									sellsc=0
									buysc=0
									Exit
								Else
									equal=Instr(txt$,"=")
									If Left(txt$,1)="#" Then equal=0
									If equal>0 Then
										var$=Trim(Left(txt$,equal-1))
										val$=Trim(Mid(txt$,equal+1,-1))
										Select var$
											Case "sell","buy"
												Trd.Ttrade=New Ttrade
												Trd\id=ti
												If var$="sell" Then
													Trd\mode=1
													sellsc=sellsc+1
													If sellsc>5 Then RuntimeError "DIALOGUE TRADE allows max 5 x 'sell' per trade"
												ElseIf var$="buy"
													Trd\mode=2
													buysc=buysc+1
													If buysc>5 Then RuntimeError "DIALOGUE TRADE allows max 5 x 'buy' per trade" 
												EndIf
												co=Instr(val$,",")
												If co>0 Then	
													Trd\typ=Abs(Int(trimspace(Left(val$,co-1))))
													Trd\count=Abs(Int(trimspace(Mid(val$,co+1,-1))))
												Else
													Trd\typ=Abs(Int(val$))
													Trd\count=1
												EndIf
												If Ditem_name$(Trd\typ)="" Or Ditem_iconh(Trd\typ)=0 Then RuntimeError "DIALOGUE TRADE item with id '"+Trd\typ+"' is not defined" 
											Default RuntimeError "Invalid DIALOGUE TRADE Property '"+var$+"'"
										End Select
									EndIf
								EndIf
								i=i+1
							Wend
						EndIf					
						
					
					;-------------------- Unknown
					Default RuntimeError "Invalid DIALOGUE Property '"+var$+"'"
				
				End Select
			EndIf
				
		EndIf
		
		i=i+1
	Wend
	
	;Clearup (Remove Ś)
	While Left(in_msgtext$,1)="Ś"
		in_msgtext$=Right(in_msgtext$,Len(in_msgtext$)-1)
	Wend
	While Right(in_msgtext$,1)="Ś"
		in_msgtext$=Left(in_msgtext$,Len(in_msgtext$)-1)
	Wend
	
	;Execute Script
	If script$<>"" Then
		parse_task(0,0,"","dialogue script",script$)
	EndIf
	
	;Page not Found
	If foundpage=0 Then
		parse_error("the dialogue page '"+page$+"' is not defined")
	EndIf
End Function
