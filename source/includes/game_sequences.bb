;############################################ SEQUENCES


;### Sequence Blend and Text (2D Stuff)
Function seq_blend()
	;Modify Blend Position
	If seq_blend=1 Then
		If Int(seq_blendp#)<80 Then
			seq_blendp#=seq_blendp+(2.*f#)
			If seq_blendp#>80. Then seq_blendp#=80.
		EndIf
	Else
		If Int(seq_blendp#)>0 Then
			seq_blendp#=seq_blendp-(2.*f#)
		EndIf
	EndIf
	;CLS
	If seq_cls=1 Then
		ClsColor seq_cls_r,seq_cls_g,seq_cls_b
		Cls
		ClsColor 0,0,0
	EndIf
	;Image
	If seq_img<>0 Then
		If seq_img_masked=1 Then
			DrawImage seq_img,set_scrx/2,set_scry/2
		Else
			DrawBlock seq_img,set_scrx/2,set_scry/2
		EndIf
	EndIf
	;Draw Blend?
	If Int(seq_blendp#)>0 Then
		Color 0,0,0
		;Top Blend
		Rect 0,0,set_scrx,seq_blendp#,1
		;Bottom Blend
		Rect 0,set_scry-seq_blendp#,set_scrx,seq_blendp#,1
	EndIf
	;Draw Messages
	If m_section=Csection_game_sp Then 
		If m_menu=Cmenu_if_movie
			If seq_msg$(0,0)<>"" Then bmpf_txt_c(set_scrx/2,set_scry-50,seq_msg$(0,0),seq_msg$(0,1))
			If seq_msg$(1,0)<>"" Then bmpf_txt_c(set_scrx/2,30,seq_msg$(1,0),seq_msg$(1,1))
			If seq_msg$(2,0)<>"" Then bmpf_txt_c(set_scrx/2,set_scry/2-10,seq_msg$(2,0),seq_msg$(2,1))
		EndIf
	EndIf
End Function


;### Sequence Start (called by parse_commands)
Function seq_start()
	;Params
	seq_blend=1
	seq_skipable=0
	;Blend?
	If parse_param() Then
		seq_blend=param()
		;Skipable?
		If parse_comma(0) Then
			seq_skipable=Int(param())
			If seq_skipable<>1 Then
				seq_skipable=0
			EndIf
		EndIf
	EndIf
	;Reset Sequence Stuff
	seq_cpoint=0
	seq_cpoint_class=0
	seq_cpoint_id=0
	Dim seq_msg$(2,1)
	seq_tmod=1
	seq_tabs=1
	seq_tlast=0
	seq_cls=0
	seq_cstart=0
	seq_cend=0
	seq_cstartt=0
	seq_cendt=0
	seq_hideplayer=1
	seq_flw_class=0
	;Image
	If seq_img<>0 Then
		FreeImage seq_img
		seq_img=0
	EndIf
	;Old Seq Stuff
	For Tseq.Tseq=Each Tseq
		Delete Tseq
	Next
	;Kill 2D Particles
	p_kill2d()
	;Start
	If m_section=Csection_game_sp Then m_menu=Cmenu_if_movie
	seq_start=MilliSecs()
	;Activate Sequence Debug?
	If set_debug=1 Then
		set_debug_seq=1
	EndIf
End Function


;### Sequence End
Function seq_end()
	;End
	If m_menu=Cmenu_if_movie Then m_menu=0
	;Blend off
	seq_blend=0
	;CLS off
	seq_cls=0
End Function


;### Sequence Update (Main Sequence Stuff called by game_setcam)
Function seq_update()
	
	;Update Camera and its Movement / Rotation
	seq_cammovement()
	seq_camrotation()
	
	;Show/Hide Player
	If seq_hideplayer=1 Then
		HideEntity g_cplayer\mh
	Else
		ShowEntity g_cplayer\mh
	EndIf
	
	;Sequence Events
	Local time=(MilliSecs()-seq_start)+1
	;DebugLog "ST: "+time
	For Tseq.Tseq=Each Tseq
		;Handle if time is come
		If time>Tseq\t Then
			
			;Debug
			If set_debug=1 Then
				If set_debug_seq=1 Then con_add("Sequence: "+Tseq\event$+" @ "+Tseq\t+" ms",Cbmpf_yellow)
			EndIf
			
			Select Tseq\event$
				;End
				Case "end" seq_end()
				;Show a Message
				Case "msg"
					seq_msg$(Tseq\in[1],0)=Tseq\txt$[0]	;Text
					seq_msg$(Tseq\in[1],1)=Tseq\in[0]	;Color
				;Message Clear
				Case "msgclear"
					If Tseq\in[0]=-1 Then
						seq_msg$(0,0)="":seq_msg$(1,0)="":seq_msg$(2,0)=""
					Else
						seq_msg$(Tseq\in[0],0)=""
					EndIf
				;Play a Sound
				Case "sound"
					sfx_play(Tseq\txt$[0],Tseq\fl[0],Tseq\fl[1],Tseq\in[0])
				;Set Bar Mode
				Case "bar" seq_blend=Tseq\in[0]
				;Hide Bar
				Case "hidebar" seq_blend=0:seq_blendp#=0
				;Show Bar
				Case "showbar" seq_blend=1:seq_blendp#=80
				
				;Event
				Case "event"
					If Tseq\in[0]=0 Then
						parse_globalevent(Tseq\txt$[0],"performed by seqevent")
					Else
						parse_emuevent(Tseq\in[0],Tseq\in[1],Tseq\txt$[0])
					EndIf	
				;Script
				Case "script"
					parse_task(0,0,"sequence","triggered by seqscript command",Tseq\txt$[0])
				;Hideplayer
				Case "hideplayer"
					seq_hideplayer=Tseq\in[0]
				
				;Setcam
				Case "setcam"
					If con_info(Tseq\in[0]) Then
						;Positiion
						PositionEntity cam,EntityX(TCinfo\h),EntityY(TCinfo\h),EntityZ(TCinfo\h)
						RotateEntity cam,EntityPitch(TCinfo\h),EntityYaw(TCinfo\h),0
						;Rotation Pivot
						PositionEntity seq_cpoint_p,EntityX(cam),EntityY(cam),EntityZ(cam)
						RotateEntity seq_cpoint_p,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
						MoveEntity  seq_cpoint_p,0,0,100000
						;Movement Vars
						seq_cstart=0
						seq_cend=0
					Else
						con_add("ERROR: "+Tseq\event$+" @ "+Tseq\t+" ms - info "+Tseq\in[0]+" does not exist",Cbmpf_red)
					EndIf
				;Movecam
				Case "movecam"
					;Setup
					If seq_cstart=0 Then seq_cstart=CreatePivot()
					If seq_cend=0 Then seq_cend=CreatePivot()
					seq_cstartt=Tseq\t
					seq_cendt=Tseq\in[0]
					;Setup Start
					PositionEntity seq_cstart,EntityX(cam),EntityY(cam),EntityZ(cam)
					RotateEntity seq_cstart,EntityPitch(cam),EntityYaw(cam),0
					;Setup End
					If con_info(Tseq\in[1]) Then
						PositionEntity seq_cend,EntityX(TCinfo\h),EntityY(TCinfo\h),EntityZ(TCinfo\h)
						RotateEntity seq_cend,EntityPitch(TCinfo\h),EntityYaw(TCinfo\h),0
					EndIf
					;Rotation Pivot
					PositionEntity seq_cpoint_p,EntityX(cam),EntityY(cam),EntityZ(cam)
					RotateEntity seq_cpoint_p,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
					MoveEntity  seq_cpoint_p,0,0,100000
					;Update
					seq_cammovement()
					seq_camrotation()	
				;Cammode
				Case "cammode"
					seq_cpoint=Tseq\in[0]
					seq_cpoint_class=Tseq\in[1]
					seq_cpoint_id=Tseq\in[2]
					;Rotation Pivot
					PositionEntity seq_cpoint_p,EntityX(cam),EntityY(cam),EntityZ(cam)
					RotateEntity seq_cpoint_p,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
					MoveEntity  seq_cpoint_p,0,0,100000
					;Update
					seq_camrotation()
				;Flash
				Case "flash"
					p_add(0,0,0,Cp_flash,Tseq\fl#[0],Tseq\fl#[1])
					EntityColor TCp\h,Tseq\in[0],Tseq\in[1],Tseq\in[2]
					
				;Fade
				Case "fade"
					p_add(0,0,0,Cp_seqfade)
					EntityColor TCp\h,Tseq\in[1],Tseq\in[2],Tseq\in[3]
					TCp\x#=Tseq\t
					TCp\y#=Tseq\in[0]
					TCp\fx#=Tseq\in[4]
					Select Tseq\in[4]
						Case 0 EntityAlpha TCp\h,0.
						Case 1 EntityAlpha TCp\h,0.
						Case 2 EntityAlpha TCp\h,1.
					End Select
					
				;CLS
				Case "cls"
					seq_cls=Tseq\in[0]
					seq_cls_r=Tseq\in[1]
					seq_cls_g=Tseq\in[2]
					seq_cls_b=Tseq\in[3]
					
				;Image
				Case "image"
					If seq_img<>0 Then FreeImage seq_img
					If Tseq\txt$[0]="0" Or Tseq\txt$[0]="" Then
						seq_img=0
					Else
						seq_img=load_image(Tseq\txt$[0],Tseq\in[0])
						seq_img_masked=Tseq\in[0]
						If seq_img<>0 Then
							MidHandle seq_img
						EndIf
					EndIf
					
				;itxt
				Case "itxt"
					If seq_img<>0 Then
						SetBuffer ImageBuffer(seq_img)
						Select Tseq\in[3]
							;0 - left
							Case 0
								bmpf_txt(Tseq\in[0],Tseq\in[1],Tseq\txt$[0],Tseq\in[2])
							;1 - middle
							Case 1
								bmpf_txt_c(Tseq\in[0],Tseq\in[1],Tseq\txt$[0],Tseq\in[2])
							;2 - right
							Case 2
								bmpf_txt_r(Tseq\in[0],Tseq\in[1],Tseq\txt$[0],Tseq\in[2])
							;default
							Default
								con_add("seqimagetext invalid alignment (only 0,1,2 allowed)",Cbmpf_red)
						End Select
						SetBuffer BackBuffer()
					EndIf
				;Camfollow
				Case "camfollow"
					If Tseq\in[0]=0 Then
						seq_flw_class=0
					Else
						seq_cpoint=1
						seq_cpoint_class=Tseq\in[0]
						seq_cpoint_id=Tseq\in[1]
						;Rotation Pivot
						PositionEntity seq_cpoint_p,EntityX(cam),EntityY(cam),EntityZ(cam)
						RotateEntity seq_cpoint_p,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam)
						MoveEntity  seq_cpoint_p,0,0,100000
						;Update
						seq_camrotation()
						;Follow
						seq_flw_class=Tseq\in[0]
						seq_flw_id=Tseq\in[1]
						seq_flw_x#=Tseq\fl#[0]
						seq_flw_y#=Tseq\fl#[1]
						seq_flw_z#=Tseq\fl#[2]
						seq_cammovement()

					EndIf
				
				;Default
				Default con_add("INTERNAL ERROR: invalid event '"+Tseq\event$+"' for seq_update",Cbmpf_red)
				
			End Select
			Delete Tseq
		EndIf
	Next
	
End Function




;### Sequence Camera Movement
Function seq_cammovement()
	;Movement?
	If seq_cstart<>0 Then
		If seq_cend<>0 Then
			
			;Get Start Coordinates
			Local x1#=EntityX(seq_cstart)
			Local y1#=EntityY(seq_cstart)
			Local z1#=EntityZ(seq_cstart)
			;Get End Coordinates
			Local x2#=EntityX(seq_cend)
			Local y2#=EntityY(seq_cend)
			Local z2#=EntityZ(seq_cend)
			
			;Get Percent of Process
			Local time=(MilliSecs()-seq_start)
			Local t1=time-seq_cstartt
			Local t2=seq_cendt-seq_cstartt
			Local perc#=Float(Float(t1)/Float(t2))
			If perc#<0. Then
				perc#=0.
			ElseIf perc#>1. Then
				perc#=1.
			EndIf
			
			;Get Distances between Start and End
			Local dx#=x2#-x1#
			Local dy#=y2#-y1#
			Local dz#=z2#-z1#
			;Set Position based on Startposition, Distances and Percentage
			PositionEntity cam,x1+(dx*perc#),y1+(dy*perc#),z1+(dz*perc#)
			
			;Finished?
			If time>=seq_cendt Then
				FreeEntity seq_cstart:seq_cstart=0
				FreeEntity seq_cend:seq_cend=0
			EndIf
			
		EndIf
	EndIf
	
	;Follow
	If seq_flw_class<>0 Then
		If parent_pos(seq_flw_class,seq_flw_id) Then
			tmp_x#=tmp_x#+seq_flw_x#
			tmp_y#=tmp_y#+seq_flw_y#
			tmp_z#=tmp_z#+seq_flw_z#
			PositionEntity cam,tmp_x#,tmp_y#,tmp_z#
		EndIf
	EndIf
	
	;Keep Cam Over Terrain
	If (EntityY(cam)-30)<e_tery(EntityX(cam),EntityZ(cam)) Then
		PositionEntity cam,EntityX(cam),e_tery(EntityX(cam),EntityZ(cam))+30,EntityZ(cam)
	EndIf
End Function


;### Camera Rotation
Function seq_camrotation()
	;Rotatecam
	Select seq_cpoint
		;0 - Center
		Case 0
			center=CreatePivot()
			PointEntity cam,center
			FreeEntity center
		;1 - Object
		Case 1
			h=parent_h(seq_cpoint_class,seq_cpoint_id)
			If h<>0 Then
				PointEntity cam,h
			EndIf
			;DebugLog "point "+seq_cpoint_class+" #"+seq_cpoint_id+" h:"+h
		;2 - Info Path
		Case 2
			If seq_cend<>0 Then
					;Create and Setup temp. Rotation Target
					Local rt=CreatePivot()
					PositionEntity rt,EntityX(seq_cend),EntityY(seq_cend),EntityZ(seq_cend)
					RotateEntity rt,EntityPitch(seq_cend),EntityYaw(seq_cend),EntityRoll(seq_cend)
					MoveEntity rt,0,0,100000
					;Get Start Coordinates
					x1#=EntityX(seq_cpoint_p)
					y1#=EntityY(seq_cpoint_p)
					z1#=EntityZ(seq_cpoint_p)
					;Get End Coordinates
					x2#=EntityX(rt)
					y2#=EntityY(rt)
					z2#=EntityZ(rt)
					;Get Distances between Start and End
					dx#=x2#-x1#
					dy#=y2#-y1#
					dz#=z2#-z1#
					;Get Percent of Process
					Local time=(MilliSecs()-seq_start)
					Local t1=time-seq_cstartt
					Local t2=seq_cendt-seq_cstartt
					Local perc#=Float(Float(t1)/Float(t2))
					If perc#<0. Then
						perc#=0.
					ElseIf perc#>1. Then
						perc#=1.
					EndIf
					;Set Position based on Startposition, Distances and Percentage
					PositionEntity rt,x1+(dx*perc#),y1+(dy*perc#),z1+(dz*perc#)
					;Point and Free
					PointEntity cam,rt
					FreeEntity rt
			EndIf

			
		;3 - Nothing
		Case 3
		
		;4 - Free Look
		Case 4
			
			;Mouselook
			If set_xinvert=1 Then
				;Invert X-Axis
				in_mxs#=-in_mxs#
			EndIf
			;X-Axis
			If in_mxs<>0 Then RotateEntity cam,EntityPitch(cam),EntityYaw(cam)+(in_mxs#*set_msens#),EntityRoll(cam)
			;Y-Axis
			If in_mys<>0 Then
				;Invert?
				in_mys#=(in_mys#*set_msens#)*set_minvert
				;Rotate
				If in_mys>0 Then
					If EntityPitch(cam)+in_mys>85
						in_mys=85-EntityPitch(cam)
					EndIf
					RotateEntity cam,EntityPitch(cam)+in_mys,EntityYaw(cam),EntityRoll(cam)
				ElseIf in_mys<0 Then
					If EntityPitch(cam)+in_mys<-85
						in_mys=-85-EntityPitch(cam)
					EndIf
					RotateEntity cam,EntityPitch(cam)+in_mys,EntityYaw(cam),EntityRoll(cam)
				EndIf
			EndIf
			
			
	End Select
End Function



;### Sequence Add Event (called by parse_commands)
Function seq_addevent(event$)
	;New Tseq
	Tseq.Tseq=New Tseq
	;Set Time
	Local t=param()
	If seq_tabs=1 Then
		;Absolute
		Tseq\t=t*seq_tmod
	Else
		;Relative
		Tseq\t=seq_tlast+(t*seq_tmod)
	EndIf
	seq_tlast=Tseq\t
	;Set Event
	Tseq\event$=event$
	;Get Params
	Select event$
		;End
		Case "end"
		;Show a Message
		Case "msg"
			parse_comma(1)
			Tseq\txt$[0]=param()								;Message
			Tseq\in[0]=0:Tseq\in[1]=0
			If parse_comma(0) Then Tseq\in[0]=param()			;Color
			If parse_comma(0) Then Tseq\in[1]=param()			;Pos
		;Message Clear
		Case "msgclear"
			Tseq\in[0]=-1
			If parse_comma(0) Then Tseq\in[0]=param()			;Pos
		;Play a Sound
		Case "sound"
			Tseq\fl#[0]=1.
			Tseq\fl#[1]=0.
			Tseq\in[0]=0
			parse_comma(1)
			Tseq\txt$[0]=param()								;File
			If parse_comma(0) Then Tseq\fl#[0]=param()			;Volume
			If parse_comma(0) Then Tseq\fl#[1]=param()			;Pan
			If parse_comma(0) Then Tseq\in[0]=param()			;Pitch
		;Parse an event
		Case "event"
			parse_comma(1)
			Tseq\txt$[0]=param()								;Event
			If parse_comma(0) Then
				class$=param()
				Tseq\in[0]=parse_getclass(class$)				;Class
				parse_comma(1)
				Tseq\in[1]=param()								;ID
			EndIf
		;Script
		Case "script"
			parse_comma(1)
			source$=params()									;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							txt$=Tx\value$
							Exit
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			EndIf
			Tseq\txt$[0]=txt$
			;con_add("seqscript: "+txt$)
		;Hideplayer
		Case "hideplayer"
			Tseq\in[0]=1
			If parse_comma(1) Then
				Tseq\in[0]=param()
			EndIf
		
		;Set Bar Mode
		Case "bar" If parse_comma(0) Then Tseq\in[0]=param()	;Barmode
		;Hide Bar
		Case "hidebar"
		;Show Bar
		Case "showbar"
		
		;Setcam
		Case "setcam"
			parse_comma(1):Tseq\in[0]=param()					;Target
		;Movecam
		Case "movecam"
			parse_comma(1):Tseq\in[0]=param()					;Endtime
			parse_comma(1):Tseq\in[1]=param()					;Target
		;Campath
		Case "campath"
			Tseq\event$="movecam"
			parse_comma(1):steptime=param()						;Steptime
			Tseq\in[0]=t+steptime								;Endtime
			parse_comma(1):Tseq\in[1]=param()					;Target
			i=1
			While parse_comma(0)									;Get more?
				Tseq.Tseq=New Tseq
				Tseq\event$="movecam"								;Event
				Tseq\t=t+steptime*(i)								;Time
				i=i+1
				Tseq\in[0]=t+(steptime*i)							;Endtime
				Tseq\in[1]=param()									;Target
			Wend
		;Timedcampath
		Case "timedcampath"
			Tseq\event$="movecam"
			parse_comma(1):steptime=Int(param())				;Steptime
			Tseq\in[0]=t+steptime								;Endtime
			parse_comma(1):Tseq\in[1]=param()					;Target
			i=1
			While parse_comma(0)									;Get more?
				Tseq.Tseq=New Tseq
				Tseq\event$="movecam"								;Event
				Tseq\t=Int(t+Int(steptime))							;Time
				steptime=Int(steptime)+Int(param())
				Tseq\in[0]=Int(t)+Int(steptime)						;Endtime
				parse_comma(1)
				Tseq\in[1]=param()									;Target
				;con_add("tcp: @ "+Tseq\t+" to "+Tseq\in[1]+" until "+Tseq\in[0])
			Wend	
		;Cammode
		Case "cammode"
			parse_comma(1)
			Tseq\in[0]=param()									;Mode
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				If classint=-1 Then
					classint=p_env_class
					id=p_env_id
				Else
					parse_comma(1)
					id=param()									;ID
				EndIf	
				Tseq\in[1]=classint
				Tseq\in[2]=id
			EndIf
			
		;Flash
		Case "flash"
			Tseq\in[0]=255:Tseq\in[1]=255:Tseq\in[2]=255
			Tseq\fl#[0]=0.05
			Tseq\fl#[1]=1.1
			If parse_comma(0) Then Tseq\in[0]=param()			;R
			If parse_comma(0) Then Tseq\in[1]=param()			;G
			If parse_comma(0) Then Tseq\in[2]=param()			;B
			If parse_comma(0) Then Tseq\fl#[0]=param()			;Fadespeed
			If parse_comma(0) Then Tseq\fl#[1]=param()			;Alpha
			
		;Fade
		Case "fade"
			parse_comma()
			Tseq\in[1]=255:Tseq\in[2]=255:Tseq\in[3]=255
			Tseq\in[4]=0
			Tseq\in[0]=param()									;Endtime
			If parse_comma(0) Then Tseq\in[1]=param()			;R
			If parse_comma(0) Then Tseq\in[2]=param()			;G
			If parse_comma(0) Then Tseq\in[3]=param()			;B
			If parse_comma(0) Then Tseq\in[4]=param()			;Mode
			
		;CLS
		Case "cls"
			parse_comma()
			Tseq\in[1]=0:Tseq\in[2]=0:Tseq\in[3]=0
			Tseq\in[0]=param()									;Mode
			If parse_comma(0) Then Tseq\in[1]=param()			;R
			If parse_comma(0) Then Tseq\in[2]=param()			;G
			If parse_comma(0) Then Tseq\in[3]=param()			;B
			
		;Image
		Case "image"
			parse_comma()
			Tseq\in[0]=0
			Tseq\txt$[0]=param()								;Image
			If parse_comma(0) Then Tseq\in[0]=param()			;Masked
			
		;itxt
		Case "itxt"
			parse_comma()
			Tseq\txt$[0]=param()								;Text
			parse_comma(1)
			Tseq\in[0]=param()									;X
			parse_comma(1)
			Tseq\in[1]=param()									;Y
			If parse_comma(0) Then
				Tseq\in[2]=param()								;Color
			EndIf
			If parse_comma(0) Then
				Tseq\in[3]=param()								;Align
			EndIf
		;camfollow
		Case "camfollow"
			parse_comma()
			class$=param()
			If class$<>"0" Then									;Class
				classint=parse_getclass(class$)
				If classint=-1 Then
					classint=p_env_class
					id=p_env_id
				Else
					parse_comma(1)
					id=param()									;ID
				EndIf	
				Tseq\in[0]=classint
				Tseq\in[1]=id
				parse_comma()
				Tseq\fl#[0]=param()									;X
				parse_comma()
				Tseq\fl#[1]=param()									;Y
				parse_comma()
				Tseq\fl#[2]=param()									;Z
			Else
				Tseq\in[0]=0
			EndIf
		
		;Default
		Default con_add("INTERNAL ERROR: invalid event '"+event$+"' for seq_addevent",Cbmpf_red)
			
	End Select
End Function
