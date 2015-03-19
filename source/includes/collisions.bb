;############################################ Collisions

;Set Collisions
col_set()
Function col_set()
	Collisions Cworld_unitcol,Cworld_col,2,3		;UNITS -> WORLD (Radius->Polygon, slide)
	;Collisions Cworld_unitcol,Cworld_unitcol,1,3	;UNITS -> UNITS (Radius->Radius, slide)
	Collisions Cworld_procol,Cworld_col,2,3			;PROJECTILES -> WORLD (Radius->Polygon, slide)
	Collisions Cworld_procol,Cworld_unitcol,1,3		;PROJECTILES -> UNITS (Radius->Radius, slide)
	Collisions Cworld_procol,Cworld_itemcol,2,1		;PROJECTILES -> ITEMS (Radius->Polygon, stop)
	Collisions Cworld_itemcol,Cworld_col,2,3		;ITEMS -> WORLD (Radius->Polygon, slide)
End Function


;### Collision Center = Bottom?
Function col_bottom(h,radius#)
	Local c=CountCollisions(h)
	Local x#,y#,z#
	x#=EntityX(h)
	y#=EntityY(h)
	z#=EntityZ(h)
	If c>0 Then
		For i=1 To c
		Next
	EndIf
End Function


;### Jump Col Check
Function col_jump()
	Local c=CountCollisions(TCunit\h)
	If c>0 Then
		out=1
		For i=1 To c
			h=CollisionEntity(TCunit\h,i)
			If get_object(h)<>-1 Then
				;High Collision - No Jump!
				If Dobject_col(TCobject\typ)<>4 Then
					If CollisionY(TCunit\h,i)>(EntityY(TCunit\h)-(Dunit_colyr#(TCunit\typ)/1.5)) Then
						Return 0
					EndIf
				EndIf
			EndIf
		Next
		Return out
	Else
		Return 1
	EndIf
End Function


;### Player Collision
Function col_player()
	Local c=CountCollisions(TCunit\h)
	If c>0 Then
		For i=1 To c
			h=CollisionEntity(TCunit\h,i)
			If get_object(h)<>-1 Then
				;High Collision
				If Dobject_col(TCobject\typ)<>4 Then
					If CollisionY(TCunit\h,i)>(EntityY(TCunit\h)-(Dunit_colyr#(TCunit\typ)/1.5)) Then						
						;X Correction
						If CollisionX(TCunit\h,i)>EntityX(TCunit\h) Then
							TranslateEntity TCunit\h,-1,0,0
						Else
							TranslateEntity TCunit\h,1,0,0
						EndIf
						;Z Correction
						If CollisionZ(TCunit\h,i)>EntityZ(TCunit\h) Then
							TranslateEntity TCunit\h,0,0,-1
						Else
							TranslateEntity TCunit\h,0,0,1
						EndIf					
					EndIf
				EndIf
			EndIf
		Next
	EndIf
End Function
