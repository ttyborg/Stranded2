;############################################ FOCUS

;Update Focus
Function focus_update()
	If m_menu=0 Then
		;Rescan every 300 MS
		If ms-in_fo_timer>300 Then
			in_fo_timer=ms
			in_focus=0
			
			;Get Focus by Process?
			If pc_typ<>0 Then
				in_focus=pc_typ	
			EndIf
			
			;Pick
			;If in_focus=0 Then
			;	Local target=CreatePivot()
			;	PositionEntity target,EntityX(cam),EntityY(cam),EntityZ(cam)
			;	RotateEntity target,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
			;	MoveEntity target,0,0,Cworld_userange
			;	in_pe=0
			;	in_pe=LinePick(EntityX(cam),EntityY(cam),EntityZ(cam),EntityX(target)-EntityX(cam),EntityY(target)-EntityY(cam),EntityZ(target)-EntityZ(cam),Cworld_useradius)
			;	in_px#=PickedX()
			;	in_py#=PickedY()
			;	in_pz#=PickedZ()
			;	FreeEntity target
			;	;Picked?
			;	If in_pe<>0 Then
			;		;Item?
			;		If get_item(in_pe)>-1 Then
			;			in_focus=1:in_fo_x=set_scrx/2:in_fo_y=35
			;			in_fo_txt$=Ditem_name$(TCitem\typ)
			;			in_fo_perc=(TCitem\health#/Ditem_health#(TCitem\typ))*100.
			;		;Unit?
			;		ElseIf get_unit(in_pe)>-1 Then
			;			in_focus=1:in_fo_x=set_scrx/2:in_fo_y=35
			;			If TCunit\id<100 Then
			;				in_fo_txt$="#"+TCunit\id+" "+TCunit\player_name$
			;			Else
			;				in_fo_txt$=Dunit_name$(TCunit\typ)
			;			EndIf
			;			in_fo_perc=(TCunit\health#/TCunit\health_max#)*100.
			;		EndIf
			;	EndIf
			;EndIf
			

		EndIf
	EndIf
End Function
	

;Draw Focus Stuff
Function focus_draw()
	If in_focus<>0 Then
		Select in_focus	
			;1 - Text
			Case 1
				bmpf_txt_c(in_fo_x,in_fo_y,in_fo_txt$)
				If in_perc>-1 Then
					x=set_scrx/2-51
					y=55
					;DrawBlock gfx_if_barback,x,y
					;DrawBlockRect gfx_bars,x+1,y+1,0,0,in_fo_perc,5
				EndIf
				
			;Process Build
			Case Cstate_pc_build
				game_buildfocus(pc_child)
				If (gt-pc_gt)>=3000 Then
					pc_typ=0:in_focus=0
				EndIf
				
			;Process Dig
			Case Cstate_pc_dig
				bmpf_txt_c(set_scrx/2,35,sm$(178))
				x=set_scrx/2-51:y=55
				perc=Float(Float(gt-pc_gt)/Float(game_digtime))*100.
				DrawBlock gfx_if_barback,x,y
				DrawBlockRect gfx_bars,x+1,y+1,0,0,perc,5
				If in_t500go Then sfx sfx_dig
				If (gt-pc_gt)>=game_digtime Then
					pc_typ=0:in_focus=0
					game_dig()
				EndIf
			
			;Process Fish
			Case Cstate_pc_fish
				bmpf_txt_c(set_scrx/2,35,sm$(179))
				x=set_scrx/2-51:y=55
				perc=Float(Float(gt-pc_gt)/Float(game_fishtime))*100.
				DrawBlock gfx_if_barback,x,y
				DrawBlockRect gfx_bars,x+1,y+1,0,0,perc,5
				If (gt-pc_gt)>=game_fishtime Then
					pc_typ=0:in_focus=0
					game_fish()
				EndIf
				
			;Process Custom
			Case Cstate_pc_custom
				bmpf_txt_c(set_scrx/2,35,in_pc_custom$)
				x=set_scrx/2-51:y=55
				perc=Float(Float(gt-pc_gt)/Float(in_pc_customt))*100.
				DrawBlock gfx_if_barback,x,y
				DrawBlockRect gfx_bars,x+1,y+1,0,0,perc,5
				If (gt-pc_gt)>=in_pc_customt Then
					;Quit Process
					pc_typ=0
					in_focus=0
					;Return to Menu
					If in_pc_menu<>0 Then
						m_menu=in_pc_menu
						MoveMouse(in_pc_menu_x,in_pc_menu_y)
						in_pc_menu=0
					EndIf
					;Event
					If in_pc_event$<>"" Then
						parse_globalevent(in_pc_event$)
					EndIf
				EndIf
				
				
		End Select
	EndIf
End Function
