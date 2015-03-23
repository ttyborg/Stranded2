;######################################################################################################

;############################################ FUNCTIONS

;### Load Ressource
Function load_res(path$,typ,mode=1)
	;Link
	If FileType(path$)=0 Then
		Local dot=Instr(path$,".")
		If dot=0 Then Return 0
		path$=Left(path$,dot-1)+".inf"
		If FileType(path$)=1 Then
			Local stream=ReadFile(path$)
			Local in$
			If stream<>0 Then
				in$=ReadLine(stream)
				path$=path$(path$)+Trim(in$)
				CloseFile(stream)
			Else
				con_add("ERROR: Unable to read Resource Link from "+path$,Cbmpf_red)
				Return 0
			EndIf
		Else
			Return 0
		EndIf
	EndIf
	;Exists
	For Tres.Tres=Each Tres
		If Tres\typ=typ Then
			If Tres\file$=path$ Then
				Return Tres\h
			EndIf
		EndIf
	Next
	;Load
	If set_debug Then con_add("Loading Res.: "+path$+" (Type: "+typ+", Mode: "+mode+")")
	Tres.Tres=New Tres
	Tres\file$=path$
	Tres\typ=typ
	debug=set_debug:set_debug=0
	Select typ
		Case Cres_image Tres\h=load_image(path$,mode)
		Case Cres_texture Tres\h=load_texture(path$)
		Case Cres_sound Tres\h=Load3DSound(path$)
		Case Cres_mesh Tres\h=load_mesh(path$)
		Case Cres_animmesh Tres\h=load_animmesh(path$)
		Case Cres_music Tres\h=LoadSound(path$)
	End Select
	set_debug=debug
	;Return
	If Tres\h=0 Then
		If Left(path$,11)<>"sfx\speech\" Then
			con_add("ERROR: Unable to load "+path$,Cbmpf_red)
		EndIf
		Delete Tres
		Return 0
	Else
		Return Tres\h
	EndIf
End Function


;### Load Soundset
Function load_soundset(name$)
	;Exists?
	For Tsoundset.Tsoundset=Each Tsoundset
		If Tsoundset\name$=name$ Then
			Return 1
		EndIf
	Next
	;Load
	Tsoundset.Tsoundset=New Tsoundset
	Tsoundset\name$=name$
	Tsoundset\spot=load_res("sfx\"+name$+"_spot.wav",Cres_sound)
	Tsoundset\attack=load_res("sfx\"+name$+"_attack.wav",Cres_sound)
	Tsoundset\idle1=load_res("sfx\"+name$+"_idle1.wav",Cres_sound)
	Tsoundset\idle2=load_res("sfx\"+name$+"_idle2.wav",Cres_sound)
	Tsoundset\idle3=load_res("sfx\"+name$+"_idle3.wav",Cres_sound)
	Tsoundset\die=load_res("sfx\"+name$+"_die.wav",Cres_sound)
	Tsoundset\flee=load_res("sfx\"+name$+"_flee.wav",Cres_sound)
	Tsoundset\move=load_res("sfx\"+name$+"_move.wav",Cres_sound)
	;DebugLog "loadsoundset "+name$
	Return 1
End Function


;### Play Soundset Sound
Function play_soundset(name$,sound,entity)
	;Find
	For Tsoundset.Tsoundset=Each Tsoundset
		If Tsoundset\name$=name$ Then
			;DebugLog "playsoundset "+name$+" sound: "+sound
			Select sound
				Case snd_spot Return sfx_emit(Tsoundset\spot,entity)
				Case snd_attack Return sfx_emit(Tsoundset\attack,entity)
				Case snd_idle1 Return sfx_emit(Tsoundset\idle1,entity)
				Case snd_idle2 Return sfx_emit(Tsoundset\idle2,entity)
				Case snd_idle3 Return sfx_emit(Tsoundset\idle3,entity)
				Case snd_die Return sfx_emit(Tsoundset\die,entity)
				Case snd_flee Return sfx_emit(Tsoundset\flee,entity)
				Case snd_move Return sfx_emit(Tsoundset\move,entity)
			End Select
			Return 0
		EndIf
	Next
	;Failed
	Return 0
End Function
