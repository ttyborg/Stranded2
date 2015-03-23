;############################################ PARENT/CHILD STUFF


;### Parent H (Get Handle of Parent)
Function parent_h(parentclass,parentid,model=0)
	Select parentclass
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then Return Tobject\h
			Next
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then
					If model=1 Then
						Return Tunit\mh
					Else
						Return Tunit\h
					EndIf
				EndIf
			Next
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then Return Titem\h
			Next
		Case Cclass_info
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=parentid Then
					If model=1 Then
						If FindChild (Tinfo\h,"model")<>0 Then
							Return FindChild(Tinfo\h,"model")
						Else
							Return Tinfo\h
						EndIf
					Else
						Return Tinfo\h
					EndIf
				EndIf
			Next
	End Select
	Return 0
End Function


;### Parent Position (Get Position of Parent)
Function parent_pos(parentclass,parentid)
	Select parentclass
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then
					tmp_x#=EntityX(Tobject\h)
					tmp_y#=EntityY(Tobject\h)
					tmp_z#=EntityZ(Tobject\h)
					Return 1
				EndIf
			Next
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then
					tmp_x#=EntityX(Tunit\h)
					tmp_y#=EntityY(Tunit\h)
					tmp_z#=EntityZ(Tunit\h)
					Return 1
				EndIf
			Next
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then
					tmp_x#=EntityX(Titem\h)
					tmp_y#=EntityY(Titem\h)
					tmp_z#=EntityZ(Titem\h)
					Return 1
				EndIf
			Next
		Case Cclass_info
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\h=parentid Then
					tmp_x#=EntityX(Tinfo\h)
					tmp_y#=EntityY(Tinfo\h)
					tmp_z#=EntityZ(Tinfo\h)
					Return 1
				EndIf
			Next
	End Select
	Return 0
End Function


;### Parent Con (Cache in Container Var)
Function parent_con(parentclass,parentid)
	Select parentclass
		Case Cclass_object
			For TCobject.Tobject=Each Tobject
				If TCobject\id=parentid Then Return 1
			Next
		Case Cclass_unit
			For TCunit.Tunit=Each Tunit
				If TCunit\id=parentid Then Return 1
			Next
		Case Cclass_item
			For TCitem.Titem=Each Titem
				If TCitem\id=parentid Then Return 1
			Next
		Case Cclass_info
			For TCinfo.Tinfo=Each Tinfo
				If TCinfo\id=parentid Then Return 1
			Next
	End Select
	Return 0
End Function


;### Parent State Position (Get State Position of an Object)
Function parent_statepos(parentclass,parentid)
	Select parentclass
		;Object
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then
					tmp_ry#=EntityY(Tobject\h)+Dobject_state#(Tobject\typ,2)
					If Dobject_state#(Tobject\typ,0)=1 Then
						tmp_x#=EntityX(Tobject\h)+Dobject_state#(Tobject\typ,1)
						tmp_y#=EntityY(Tobject\h)+Dobject_state#(Tobject\typ,2)
						tmp_z#=EntityZ(Tobject\h)+Dobject_state#(Tobject\typ,3)
						Return 1
					Else
						surf=GetSurface(Tobject\h,Rand(1,CountSurfaces(Tobject\h)))
						vertices=CountVertices(surf)-1
						vertex=Rand(0,vertices)
						tmp_x#=VertexX(surf,vertex)
						tmp_y#=VertexY(surf,vertex)
						tmp_z#=VertexZ(surf,vertex)
						TFormPoint tmp_x#,tmp_y#,tmp_z#,Tobject\h,0
						tmp_x#=TFormedX()
						tmp_y#=TFormedY()
						tmp_z#=TFormedZ()
						Return 1
					EndIf
				EndIf
			Next
		;Unit
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then
					tmp_ry#=EntityY(Tunit\h)+Dunit_state#(Tunit\typ,2)
					If Dunit_state#(Tunit\typ,0)=1 Then
						tmp_x#=EntityX(Tunit\h)+Dunit_state#(Tunit\typ,1)
						tmp_y#=EntityY(Tunit\h)+Dunit_state#(Tunit\typ,2)
						tmp_z#=EntityZ(Tunit\h)+Dunit_state#(Tunit\typ,3)
						Return 1
					Else
						surf=GetSurface(Tunit\mh,1)
						vertices=CountVertices(surf)-1
						vertex=Rand(0,vertices)
						tmp_x#=VertexX(surf,vertex)
						tmp_y#=VertexY(surf,vertex)
						tmp_z#=VertexZ(surf,vertex)
						TFormPoint tmp_x#,tmp_y#,tmp_z#,Tunit\mh,0
						tmp_x#=TFormedX()
						tmp_y#=TFormedY()
						tmp_z#=TFormedZ()
						Return 1
					EndIf
				EndIf
			Next
		;Item
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then
					tmp_ry#=EntityY(Titem\h)+Ditem_state#(Titem\typ,2)
					If Ditem_state#(Titem\typ,0)=1 Then
						tmp_x#=EntityX(Titem\h)+Ditem_state#(Titem\typ,1)
						tmp_y#=EntityY(Titem\h)+Ditem_state#(Titem\typ,2)
						tmp_z#=EntityZ(Titem\h)+Ditem_state#(Titem\typ,3)
						Return 1
					Else
						surf=GetSurface(Titem\h,1)
						vertices=CountVertices(surf)-1
						vertex=Rand(0,vertices)
						tmp_x#=VertexX(surf,vertex)
						tmp_y#=VertexY(surf,vertex)
						tmp_z#=VertexZ(surf,vertex)
						TFormPoint tmp_x#,tmp_y#,tmp_z#,Titem\h,0
						tmp_x#=TFormedX()
						tmp_y#=TFormedY()
						tmp_z#=TFormedZ()
						Return 1
					EndIf
				EndIf
			Next
		;Info
		Case Cclass_info
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=parentid Then
					tmp_x#=EntityX(Tinfo\h)
					tmp_y#=EntityY(Tinfo\h)
					tmp_z#=EntityZ(Tinfo\h)
					tmp_ry#=tmp_y#
					Return 1
				EndIf
			Next
	End Select
	Return 0
End Function


;### Is Parent State Position Static?
Function parent_stateposstatic(parentclass,parentid)
	Select parentclass
		;Object
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then
					If Dobject_state#(Tobject\typ,0)=1 Then
						Return 1
					Else
						Return 0
					EndIf
				EndIf
			Next
		;Unit
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then
					If Dunit_state#(Tunit\typ,0)=1 Then
						Return 1
					Else
						Return 0
					EndIf
				EndIf
			Next
		;Item
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then
					tmp_ry#=EntityY(Titem\h)+Ditem_state#(Titem\typ,2)
					If Ditem_state#(Titem\typ,0)=1 Then
						Return 1
					Else
						Return 0
					EndIf
				EndIf
			Next
		;Info
		Case Cclass_info
			Return 1	
	End Select
End Function


;### Parent State Position by Vertex
Function parent_stateposvertex(parentclass,parentid,randomoffset#=5.)
	Select parentclass
		;Object
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then
					tmp_ry#=EntityY(Tobject\h)+Dobject_state#(Tobject\typ,2)
					surf=GetSurface(Tobject\h,Rand(1,CountSurfaces(Tobject\h)))
					vertices=CountVertices(surf)-1
					vertex=Rand(0,vertices)
					tmp_x#=VertexX(surf,vertex)
					tmp_y#=VertexY(surf,vertex)
					tmp_z#=VertexZ(surf,vertex)
					TFormPoint tmp_x#,tmp_y#,tmp_z#,Tobject\h,0
					tmp_x#=TFormedX()
					tmp_y#=TFormedY()
					tmp_z#=TFormedZ()
					Return 1
				EndIf
			Next
		;Unit
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then
					tmp_ry#=EntityY(Tunit\h)+Dunit_state#(Tunit\typ,2)
					surf=GetSurface(Tunit\mh,1)
					vertices=CountVertices(surf)-1
					vertex=Rand(0,vertices)
					tmp_x#=VertexX(surf,vertex)
					tmp_y#=VertexY(surf,vertex)
					tmp_z#=VertexZ(surf,vertex)
					TFormPoint tmp_x#,tmp_y#,tmp_z#,Tunit\mh,0
					tmp_x#=TFormedX()
					tmp_y#=TFormedY()
					tmp_z#=TFormedZ()
					Return 1
				EndIf
			Next
		;Item
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then
					tmp_ry#=EntityY(Titem\h)+Ditem_state#(Titem\typ,2)
					surf=GetSurface(Titem\h,1)
					vertices=CountVertices(surf)-1
					vertex=Rand(0,vertices)
					tmp_x#=VertexX(surf,vertex)
					tmp_y#=VertexY(surf,vertex)
					tmp_z#=VertexZ(surf,vertex)
					TFormPoint tmp_x#,tmp_y#,tmp_z#,Titem\h,0
					tmp_x#=TFormedX()
					tmp_y#=TFormedY()
					tmp_z#=TFormedZ()
					Return 1
				EndIf
			Next
		;Info
		Case Cclass_info
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=parentid Then
					tmp_x#=EntityX(Tinfo\h)+Rnd(-randomoffset#,randomoffset#)
					tmp_y#=EntityY(Tinfo\h)+Rnd(-randomoffset#,randomoffset#)
					tmp_z#=EntityZ(Tinfo\h)+Rnd(-randomoffset#,randomoffset#)
					tmp_ry#=tmp_y#
					Return 1
				EndIf
			Next
	End Select
	Return 0
End Function


;### Parent Material
Function parent_material(parentclass,parentid)
	Select parentclass
		Case Cclass_object
			For Tobject.Tobject=Each Tobject
				If Tobject\id=parentid Then Return Dobject_mat(Tobject\typ)
			Next
		Case Cclass_unit
			For Tunit.Tunit=Each Tunit
				If Tunit\id=parentid Then Return Dunit_mat(Tunit\typ)
			Next
		Case Cclass_item
			For Titem.Titem=Each Titem
				If Titem\id=parentid Then Return Ditem_mat(Titem\typ)
			Next
	End Select
	Return 0	
End Function


;### Drop Childs (Childs=Items)
Function drop_childs(parentclass,parentid,in=0,h=0)
	;Drop Outside Object
	For Titem.Titem=Each Titem
		If Titem\parent_class=parentclass Then
			If Titem\parent_id=parentid Then
				If Titem\parent_mode=Cpm_out Then
					Titem\parent_class=0
					Titem\parent_id=0
					Titem\phy_pause=0
				EndIf
			EndIf
		EndIf
	Next
	;Drop Inside Objects
	If in=1 Then
		xs#=MeshWidth(h)/2.
		ys#=MeshHeight(h)
		zs#=MeshDepth(h)/2.
		For Titem.Titem=Each Titem
			If Titem\parent_class=parentclass Then
				If Titem\parent_id=parentid Then
					If Titem\parent_mode=Cpm_in Then
						PositionEntity Titem\h,EntityX(h)+Rnd(-xs#,xs#),EntityY(h)+Rnd(xs#),EntityZ(h)+Rnd(-zs#,sz#)
						ShowEntity Titem\h
						Titem\parent_class=0
						Titem\parent_id=0
						Titem\phy_pause=0
					EndIf
				EndIf
			EndIf
		Next		
	EndIf
End Function


;### Free Childs
Function free_childs(parent_class,parent_id)
	;Scan
	Local items=1
	Local states=1
	Local scripts=1
	Select parent_class
		Case Cclass_object
		Case Cclass_unit
		Case Cclass_item items=0
		Case Cclass_info
		Case Cclass_state items=0:states=0
	End Select
	
	;Free Child Items
	If items Then
		For Titem.Titem=Each Titem
			If Titem\parent_class=parent_class Then
				If Titem\parent_id=parent_id Then
					free_item(Titem\id)
				EndIf
			EndIf
		Next
	EndIf
	;Free Child States
	If states Then
		For Tstate.Tstate=Each Tstate
			If Tstate\parent_class=parent_class Then
				If Tstate\parent_id=parent_id Then
					free_state(Tstate\typ,parent_class,parent_id)
				EndIf
			EndIf
		Next
	EndIf
	;Free Child Scripts and Vars
	If scripts Then
		For Tx.Tx=Each Tx	
			If Tx\parent_class=parent_class Then
				If Tx\parent_id=parent_id Then
					If (Tx\mode Mod 2=0) Then
						If Tx\mode<6 Then
							;Mark Locals to be deleted (allows access for on:kill event!)
							If Tx\mode=4 Then
								Tx\typ=1
								tmp_dumpvars=1
							;Delete other Stuff
							Else
								Delete Tx
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	EndIf
	;Free Child Timers
	For Ttimer.Ttimer=Each Ttimer
		If Ttimer\parent_class=parent_class Then
			If Ttimer\parent_id=parent_id Then
				Delete Ttimer
			EndIf
		EndIf
	Next
	;Free Child FRAPs
	For Tfrap.Tfrap=Each Tfrap
		If Tfrap\parent_class=parent_class Then
			If Tfrap\parent_id=parent_id Then
				Delete Tfrap
				Exit
			EndIf
		EndIf
	Next
	;Free Units Paths
	If parent_class=Cclass_unit Then
		For Tup.Tup=Each Tup
			If parent_id=Tup\unit Then
				Delete Tup
			EndIf
		Next
	EndIf
End Function
