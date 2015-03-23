;############################################ Physics

;Set Physics
Function phy_set(class,id,x#,y#,z#,mx#=1.,my#=1.,mz#=1.)
	Local found=0
	For Tstate.Tstate=Each Tstate
		If Tstate\typ=Cstate_phy Then
			If Tstate\parent_class=class Then
				If Tstate\parent_id=id Then
					found=1
					Exit
				EndIf
			EndIf
		EndIf
	Next
	If found=0 Then
		Tstate.Tstate=New Tstate
		Tstate\parent_class=class
		Tstate\parent_id=id
		Tstate\typ=Cstate_phy
	EndIf
	Tstate\fx#=x#
	Tstate\fy#=y#
	Tstate\fz#=z#
	Tstate\x#=x#
	Tstate\y#=y#
	Tstate\z#=z#
End Function
