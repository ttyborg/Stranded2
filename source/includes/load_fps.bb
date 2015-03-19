Global looplimit=20

;Calculate FPS Factor
Function calcfpsfactor()
	
	;If KeyHit(201) Then looplimit=looplimit+1
	;If KeyHit(209) Then looplimit=looplimit-1
	
	;<<< WAIT >>>
	While MilliSecs()-ms<(looplimit)
		Delay 1
	Wend
	
	;Looptime
	Local looptime=MilliSecs()-ms
	
	;DebugLog looptime+" l: "+looplimit
	
	;FPS Factor
	f#=looptime
	f#=f#*in_gtmod#
	f#=f#/20.0
	in_gtmod#=1.
		
End Function
