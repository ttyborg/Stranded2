;############################################ Load Combinations

;### Load Combinations
Function load_combinations()
	;Delete Old
	For Tcom.Tcom=Each Tcom:Delete Tcom:Next
	
	Local in$,var$,val$
	Local i,equal
	Local head=0,req=0,gen=0
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,12)="combinations" And Lower(Right(file$,4))=".inf" Then
	
			;Read
			stream=ReadFile("sys\"+file$)
			If stream=0 Then RuntimeError("Unable to read sys\"+file$)
		
			While Not Eof(stream)
				in$=ReadLine(stream)
				equal=Instr(in$,"=")
				If Left(in$,1)="#" Then equal=0
				If equal>0 Then
					var$=Trim(Left(in$,equal-1))
					val$=trimspace(Mid(in$,equal+1,-1))
					Select var$
						Case "name" ;Skip (for Editor)
						Case "combi"
							If val$="start" Then
								Tcom_serial=Tcom_serial+1
								Tcom.Tcom=New Tcom
								Tcom\id=Tcom_serial
								Tcom\mode=2
							ElseIf val$="end"
								head=0
								req=0
								gen=0
								For Tcom.Tcom=Each Tcom
									If Tcom\id=Tcom_serial Then
										Select Tcom\mode
											Case 0 req=req+1
											Case 1 gen=gen+1
											Case 2 head=head+1
										End Select
									EndIf
								Next
								If req<2 Then RuntimeError "Invalid COMBINATION. Every Combination needs at least 2 'req'-Properties!"
								If gen<1 Then RuntimeError "Invalid COMBINATION. Every Combination needs at least 1 'gen'-Property!"
								If head<>1 Then RuntimeError "Invalid COMBINATION. A Combination has to be enclosed in 'combi=start' and 'combi=end'"
							Else
								RuntimeError "Invalid COMBINATION Value for 'combi'-Property. Use either 'start' or 'end' as value!"
							EndIf
						Case "const" const_set(val$,Tcom_serial)
							
						Case "req"
							Tcom.Tcom=New Tcom
							Tcom\id=Tcom_serial
							Tcom\mode=0
							split$(val$,",",2)
							;Typ
							Tcom\typ=Int(Trim(splits$(0)))
							If ha_typeexists(Cclass_item,Tcom\typ)=0 Then
								RuntimeError("Invalid ITEM ID for COMBINATION ("+Tcom\typ+")")
							EndIf
							;Count
							splits$(1)=Trim(splits$(1))
							If Int(splits$(1))<=0 Then splits$(1)=1
							Tcom\count=Int(splits$(1))
							;Del?
							If Trim(splits$(2))="stay" Then
								Tcom\del=0
							Else
								Tcom\del=1
							EndIf
							
						Case "gen"
							Tcom.Tcom=New Tcom
							Tcom\id=Tcom_serial
							Tcom\mode=1
							split$(val$,",",1)
							;Typ
							Tcom\typ=Int(Trim(splits$(0)))
							If ha_typeexists(Cclass_item,Tcom\typ)=0 Then
								RuntimeError("Invalid ITEM ID for COMBINATION ("+Tcom\typ+")")
							EndIf
							;Count
							splits$(1)=Trim(splits$(1))
							If Int(splits$(1))<=0 Then splits$(1)=1
							Tcom\count=Int(splits$(1))
							
						Case "genname"
							For Tcom.Tcom=Each Tcom
								If Tcom\id=Tcom_serial Then
									If Tcom\mode=2 Then
										Tcom\genname$=val$
										Exit
									EndIf
								EndIf
							Next
							
						Case "script"
							For Tcom.Tcom=Each Tcom
								If Tcom\id=Tcom_serial Then
									If Tcom\mode=2 Then
										While Not Eof(stream)
											val$=ReadLine(stream)
											If val$="script=end" Then
												Exit
											Else
												Tcom\script$=Tcom\script$+Trim(val$)+"Ś"
											EndIf
										Wend
									EndIf
									Exit
								EndIf
							Next
							
						Case "id"
							For Tcom.Tcom=Each Tcom
								If Tcom\id=Tcom_serial Then
									If Tcom\mode=2 Then
										Tcom\aid$=val$
										Exit
									EndIf
								EndIf
							Next
												
						Default RuntimeError "Invalid COMBINATION Property '"+var$+"'"
					End Select
				EndIf
			Wend
			CloseFile(stream)
			
		ElseIf file$="" Then
			Exit
		EndIf
	Forever
	CloseDir(dir)
End Function

load_combinations()

Function combinations_similar()
	;Grouping Similar Combinations
	For Trcom.Tcom=Each Tcom
		If Trcom\mode=2 Then
			If Trcom\gid=0 Then
				
				;Count Reqs
				creq=0
				For Tcreq.Tcom=Each Tcom
					If Tcreq\mode=0 Then
						If Tcreq\id=Trcom\id Then
							creq=creq+1
						EndIf
					EndIf
				Next
				
				;Find Similar
				For Tsrcom.Tcom=Each Tcom
					If Tsrcom\mode=2 Then
						If Tsrcom\id<>Trcom\id Then
							;DebugLog ">>> check sim "+Tsrcom\id
							tsreq=0				;Total Req
							ssreq=0				;Similar Req
							;Req
							For Tsccom.Tcom=Each Tcom
								If Tsccom\id=Tsrcom\id Then
									If Tsccom\mode=0 Then
										tsreq=tsreq+1
										;DebugLog "> req "+Ditem_name$(Tsccom\typ)
										;Same?
										For Tscccom.Tcom=Each Tcom
											If Tscccom\id=Trcom\id Then
												If Tscccom\mode=0 Then
													If Tscccom\typ=Tsccom\typ Then
														;DebugLog "> sim req "+Ditem_name$(Tscccom\typ)
														ssreq=ssreq+1
													EndIf
												EndIf
											EndIf
										Next
									EndIf
								EndIf
							Next
							;All Same?
							If tsreq=ssreq Then
								If creq=tsreq Then
								
									;Existing group
									If Tsrcom\gid<>0 Then
										Trcom\gid=Tsrcom\gid
										;DebugLog ">>>>>>>>> SAME! Group: "+Tsrcom\gid
									ElseIf Trcom\gid<>0 Then
										Tsrcom\gid=Trcom\gid
										;DebugLog ">>>>>>>>> SAME! Group: "+Tsrcom\gid
									;New Group
									Else
										coms_serial=coms_serial+1
										Trcom\gid=coms_serial
										Tsrcom\gid=coms_serial
										;DebugLog ">>>>>>>>> SAME! NEW Group: "+Tsrcom\gid
									EndIf
								
								EndIf
							EndIf
						EndIf
					EndIf
				Next
	
			EndIf
		EndIf
	Next
	
	;Ungrouped
	negid=-1
	For Trcom.Tcom=Each Tcom
		If Trcom\mode=2 Then
			If Trcom\gid=0 Then
				Trcom\gid=negid
				negid=negid-1
			EndIf
		EndIf
	Next
End Function

combinations_similar()
