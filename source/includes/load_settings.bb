;############################################ SETTINGS

;### Load Settings
Function load_settings()
	;Controls
	Dim in_keys(20)
	Local stream=ReadFile("sys\controls.cfg")
	If stream=0 Then
		;Failed? Set Defaults!
		set_msens#=0.2
		set_minvert=1
		set_msmooth=0
	Else
		;Load
		ReadLine(stream)
		For i=0 To 20
			in_keys(i)=ReadShort(stream)
		Next
		set_msens#=ReadFloat(stream)
		set_minvert=ReadByte(stream)-128
		set_msmooth=ReadByte(stream)
		CloseFile(stream)
	EndIf
	;Scriptcontrols
	Dim game_scriptkey(20)
	stream=ReadFile("sys\scriptcontrols.cfg")
	If stream<>0 Then
		ReadLine(stream)
		For i=0 To 20
			game_scriptkey(i)=ReadShort(stream)
		Next
		CloseFile(stream)
	EndIf
	;Settings
	stream=ReadFile("sys\settings.cfg")
	If stream=0 Then
		;Failed? Set Defaults!
		set_scrx=800
		set_scry=600
		set_scrbit=32
		set_hwmultitex=1
		set_mb=1
		set_mb_alpha#=0.35
	Else
		;Load
		ReadLine(stream)
		set_scrx=ReadLine(stream):set_scrx_c=set_scrx
		set_scry=ReadLine(stream):set_scry_c=set_scry
		set_scrbit=ReadLine(stream):set_scrbit_c=set_scrbit
		set_viewrange=ReadLine(stream)
		set_terrain=ReadLine(stream)
		set_water=ReadLine(stream)
		set_sky=ReadLine(stream)
		set_effects=ReadLine(stream)
		set_musicvolume#=ReadLine(stream)
		set_fxvolume#=ReadLine(stream)
		set_grass=ReadLine(stream)
		set_2dfx=ReadLine(stream)
		set_lightfx=ReadLine(stream)
		set_windsway=ReadLine(stream)
		set_name$=ReadLine(stream)
		set_serverport=ReadLine(stream)
		set_fog=ReadLine(stream)
		set_hwmultitex=ReadLine(stream)
		set_mb=ReadLine(stream)
		set_mb_alpha#=ReadLine(stream)
		CloseFile(stream)
	EndIf
	
	;Setup
	
	;Viewrange
	Select set_viewrange
		Case 0 set_viewfac#=0.5
		Case 1 set_viewfac#=1.0
		Case 2 set_viewfac#=1.5
		Case 3 set_viewfac#=2.5
		Case 4 set_viewfac#=4.0
		Default set_viewfac#=1.5
	End Select
	
	;Motion Blur
	If mb_sprite<>0 Then
		EntityAlpha mb_sprite,set_mb_alpha#
		If set_mb_alpha#>Cpeakblur# Then EntityAlpha mb_sprite,Cpeakblur#
	EndIf
	
End Function

load_settings()


;### Save Settings
Function save_settings()
	;Controls
	Local stream=WriteFile("sys\controls.cfg")
	If stream=0 Then RuntimeError("Unable to write sys\controls.cfg")
	WriteLine(stream,"### "+set_moddir$+" Control Settings (Warning: Editing this File may cause Errors)")
	For i=0 To 20
		WriteShort(stream,in_keys(i))
	Next
	WriteFloat(stream,set_msens#)
	WriteByte(stream,set_minvert+128)
	WriteByte(stream,set_msmooth)
	CloseFile(stream)
	;Scriptcontrols
	If game_scriptkeys>0 Then
		stream=WriteFile("sys\scriptcontrols.cfg")
		If stream=0 Then RuntimeError("Unable to write sys\scriptcontrols.cfg")
		WriteLine(stream,"### "+set_moddir$+" Script Control Settings (Warning: Editing this File may cause Errors)")
		For i=0 To 20
			WriteShort(stream,game_scriptkey(i))
		Next
		CloseFile(stream)
	EndIf
	;Settings
	stream=WriteFile("sys\settings.cfg")
	If stream=0 Then RuntimeError("Unable to write sys\settings.cfg")
	WriteLine(stream,"### "+set_moddir$+" Settings (Warning: Editing this File may cause Errors)")
	WriteLine(stream,set_scrx_c)
	WriteLine(stream,set_scry_c)
	WriteLine(stream,set_scrbit_c)
	WriteLine(stream,set_viewrange)
	WriteLine(stream,set_terrain)
	WriteLine(stream,set_water)
	WriteLine(stream,set_sky)
	WriteLine(stream,set_effects)
	WriteLine(stream,set_musicvolume#)
	WriteLine(stream,set_fxvolume#)
	WriteLine(stream,set_grass)
	WriteLine(stream,set_2dfx)
	WriteLine(stream,set_lightfx)
	WriteLine(stream,set_windsway)
	set_name$=mp_checkname(set_name$,0)
	WriteLine(stream,set_name$)
	WriteLine(stream,set_serverport)
	WriteLine(stream,set_fog)
	WriteLine(stream,set_hwmultitex)
	WriteLine(stream,set_mb)
	WriteLine(stream,set_mb_alpha#)
	CloseFile(stream)
	
	;Setup
	
	;Viewrange
	Select set_viewrange
		Case 0 set_viewfac#=0.5
		Case 1 set_viewfac#=1.
		Case 2 set_viewfac#=1.5
		Case 3 set_viewfac#=2.
		Case 4 set_viewfac#=2.5
		Default set_viewfac#=1.5
	End Select
	
	;Motion Blur
	If mb_sprite<>0 Then
		EntityAlpha mb_sprite,set_mb_alpha#
		If set_mb_alpha#>Cpeakblur# Then EntityAlpha mb_sprite,Cpeakblur#
	EndIf
	
	;Water
	e_environment_setup_water()
	
	;Grass
	grass_map()
	grass_heightmap()
	grass_x=-2147483648
	grass_y=-2147483648
	grass_spread()
End Function
