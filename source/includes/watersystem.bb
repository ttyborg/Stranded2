Function updatewatersystem

	For Tobject.Tobject=Each Tobject
		Select Dobject_behaviour(Tobject\typ)
			Case "fountain"
			Case "pipe_full","pipe_empty"
		End Select
	Next


End Function
