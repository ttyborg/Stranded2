;############################################ Load Materials

;### Material Swim Array (0=no (sink),1=yes, 2=hover in water)
Dim Dmat_swim(Cmat_c)
Dmat_swim(Cmat_wood)=1
Dmat_swim(Cmat_leaf)=1

;### Material Fire Resistance Array
Dim Dmat_nofire(Cmat_c)
Dmat_nofire(Cmat_stone)=1
Dmat_nofire(Cmat_dirt)=1
Dmat_nofire(Cmat_dust)=1
Dmat_nofire(Cmat_metal)=1
Dmat_nofire(Cmat_water)=1
Dmat_nofire(Cmat_glass)=1

;### Material Hard Array
;0 = Not Hard
;1 = Hard (muffling)
;2 = Really Hard (clinking)
Dim Dmat_hard(Cmat_c)
Dmat_hard(Cmat_none)=1
Dmat_hard(Cmat_wood)=1
Dmat_hard(Cmat_stone)=2
Dmat_hard(Cmat_dirt)=1
Dmat_hard(Cmat_dust)=1
Dmat_hard(Cmat_metal)=2
Dmat_hard(Cmat_flesh)=1
Dmat_hard(Cmat_fruit)=1
Dmat_hard(Cmat_glass)=2


;### Get Material by Name
Function get_material(name$)
	name$=Lower(name$)
	Select name$
		Case "none":Return Cmat_none
		Case "wood":Return Cmat_wood
		Case "stone":Return Cmat_stone
		Case "dirt":Return Cmat_dirt
		Case "dust":Return Cmat_dust
		Case "leaf":Return Cmat_leaf
		Case "metal":Return Cmat_metal
		Case "flesh":Return Cmat_flesh
		Case "water":Return Cmat_water
		Case "lava":Return Cmat_lava
		Case "fruit":Return Cmat_fruit
		Case "glass":Return Cmat_glass
		Default Return Int(name$)
	End Select
End Function


;### Spawn Material FX
Function material_fx(x#,y#,z#,material,sfx=1)
	Select material
		;None
		Case Cmat_none
			If set_effects>0 Then p_add(x#+Rnd(-5,5),y#+Rnd(-3,3),z#+Rnd(-5,5),Cp_smoke,Rnd(5,10),Rnd(0.3,1.5))
		;Wood
		Case Cmat_wood
			If set_effects>0 Then 
				p_add(x#+Rnd(-3,3),y#+Rnd(-3,3),z#+Rnd(-3,3),Cp_smoke,Rnd(3,5),Rnd(0.3,0.7))
				EntityColor TCp\h,Rand(65,100),50,0
				For i=1 To (set_effects*3)
					p_add(x#,y#,z#,Cp_wood,Rand(1,3),3)
				Next
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_mat_wood(Rand(0,1)))
		;Stone
		Case Cmat_stone
			If set_effects>0 Then 
				p_add(x#+Rnd(-3,3),y#+Rnd(-3,3),z#+Rnd(-3,3),Cp_smoke,Rnd(3,5),Rnd(0.3,0.7))
				If Rand(2)=1 Then p_add(x#,y#,z#,Cp_spark,Rand(1,5),3)
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_mat_stone(0))
		;Dirt
		Case Cmat_dirt
		;Dust
		Case Cmat_dust
			If sfx Then sfx_3d(x,y,z,sfx_mat_dust(0))
		;Leaf
		Case Cmat_leaf
			If set_effects>0 Then 
				For i=1 To (set_effects*3)
					;p_add(x#+Rnd(-5,5),y#+Rnd(-3,3),z#+Rnd(-5,5),Cp_subsplatter,Rnd(1,5),Rnd(0.9,1.5))
					;EntityColor TCp\h,0,Rand(100,255),0
				Next
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_mat_leaf(Rand(0,3)))
		;Metal
		Case Cmat_metal
			If set_effects>0 Then 
				If Rand(2)=1 Then p_add(x#,y#,z#,Cp_spark,Rand(1,5),3)
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_mat_metal(0))
		;Flesh
		Case Cmat_flesh
			If set_effects>0 Then 
				If set_gore=1 Then
					For i=1 To (set_effects*3)
						p_add(x#+Rnd(-5,5),y#+Rnd(-3,3),z#+Rnd(-5,5),Cp_splatter,Rnd(1,5),Rnd(0.9,1.5))
					Next
				EndIf
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_mat_flesh(Rand(0,4)))
		;Water
		Case Cmat_water
			If set_effects>0 Then
				For i=1 To (set_effects*3)
					p_add(x,y,z,Cp_spark,Rand(1,2),3)
					EntityColor TCp\h,Rand(230,240),Rand(230,240),255
				Next
			EndIf
			If sfx Then
				Select Rand(0,2)
					Case 0 sfx_3d(x,y,z,sfx_startdive)
					Case 1 sfx_3d(x,y,z,sfx_splash)
					Case 2 sfx_3d(x,y,z,sfx_splash2)
				End Select
			EndIf
		;Lava
		Case Cmat_lava
			If set_effects>0 Then
				For i=1 To (set_effects*3)
					p_add(x#,y#,z#,Cp_spark,Rand(1,5),3)
				Next
			EndIf
			If sfx Then sfx_3d(x,y,z,sfx_explode(4)) 
		;Fruit
		Case Cmat_fruit
			If sfx Then sfx_3d(x,y,z,sfx_mat_fruit(Rand(0,1)))
		;Glass
		Case Cmat_glass
			If sfx Then sfx_3d(x,y,z,sfx_mat_glass(Rand(0,1)))
	End Select
End Function


;### Material Step SFX
Function material_stepsfx(material)
	;No Material Step FX in Sequences
	If m_menu<>Cmenu_if_movie Then
		Select material
			;Wood
			Case Cmat_wood
				sfx(sfx_woodstep(Rand(0,1)))
			;Stone
			Case Cmat_stone 
				sfx(sfx_stonestep(Rand(0,1)))
			;Water
			Case Cmat_water
				sfx(sfx_waterstep)
			;Default
			Default
				sfx(sfx_step(Rand(0,3)))
		End Select
	EndIf
End Function
