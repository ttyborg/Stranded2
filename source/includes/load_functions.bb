;######################################################################################################

;############################################ FUNCTIONS

;### Load Image
Function load_image(path$,masked=1)
	If set_debug Then con_add("Loading Image: "+path$)
	Local image=LoadImage(path$)
	If image=0 Then RuntimeError("Unable to load Image "+path$)
	If masked Then MaskImage image,255,0,255
	Return image
End Function

;### Load Anim Image
Function load_animimage(path$,w,h,f,masked=1)
	If set_debug Then con_add("Loading animated Image: "+path$)
	Local image=LoadAnimImage(path$,w,h,0,f)
	If image=0 Then RuntimeError("Unable to load Image "+path$)
	If masked Then MaskImage image,255,0,255
	Return image
End Function

;### Load Texture
Function load_texture(path$,mode=1)
	If set_debug Then con_add("Loading Texture: "+path$)
	Local image=LoadTexture(path$,mode)
	If image=0 Then RuntimeError("Unable to load Texture "+path$)
	Return image
End Function

;### Load Mesh
Function load_mesh(path$)
	If set_debug Then con_add("Loading Mesh: "+path$)
	Local mesh
	mesh=LoadMesh(path$)
	If mesh=0 Then RuntimeError("Unable to load Model "+path$)
	Return mesh
End Function

;### Load Animmesh
Function load_animmesh(path$)
	If set_debug Then con_add("Loading animated Mesh: "+path$)
	Local mesh
	mesh=LoadAnimMesh(path$)
	If mesh=0 Then RuntimeError("Unable to load (animated) Model "+path$)
	Return mesh
End Function

;### Load Mesh Primitives
Function mesh_primitives(path$)
	p=Instr(path$,":")
	DebugLog Right(Left(path$,p-1),9)
	If Right(Left(path$,p-1),9)="primitive" Then
		typ$=Mid(path$,p+1,-1)
		typ$=Trim(typ$)
		Select typ$
			Case "cube" Return CreateCube()
			Case "sphere" Return CreateSphere()
			Case "cylinder" Return CreateCylinder()
			Case "cone" Return CreateCone()
			Default RuntimeError("'"+typ$+"' is no valid primitive!")
		End Select
	EndIf
	Return 0
End Function


;############################################ PROGRESS

Global load_objects_c
Global load_units_c
Global load_items_c

Function load_progress_get()
	load_objects_c=255
	load_units_c=255
	load_items_c=255
	If FileType("sys\load.cache")=1 Then
		Local stream=ReadFile("sys\load.cache")
		If stream<>0 Then
			load_objects_c=ReadShort(stream)
			load_units_c=ReadShort(stream)
			load_items_c=ReadShort(stream)
			CloseFile(stream)
		EndIf
	EndIf
End Function

Function load_progress_set()
	Local stream=WriteFile("sys\load.cache")
	If stream<>0 Then
		WriteShort stream,object_count
		WriteShort stream,unit_count
		WriteShort stream,item_count
		CloseFile(stream)
	EndIf
End Function
