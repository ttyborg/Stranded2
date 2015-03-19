;############################################ Load Buildings

;### Load Buildings
Function load_buildings()
	Local in$,var$,val$
	Local i,equal
	Local head=0,req=0,gen=0
	Local stream
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,9)="buildings" And Lower(Right(file$,4))=".inf" Then

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
						Case "id"
							Tbui_serial=Tbui_serial+1
							i=Int(val$)
							For Tbui.Tbui=Each Tbui
								If Tbui\mode=2 Then
									If Tbui\id=i Then
										RuntimeError("Building with ID "+i+" is already defined!")
									EndIf
								EndIf
							Next
							Tbui.Tbui=New Tbui
							Tbui\id=i
							Tbui\mode=2
						Case "const" const_set(val$,i)
						
						Case "objectid"
							If ha_typeexists(Cclass_object,Int(val$)) Then
								For Tbui.Tbui=Each Tbui
									If Tbui\mode=2 Then
										If Tbui\id=i Then
											Tbui\typ=Int(val$)
											Exit
										EndIf
									EndIf
								Next
							Else
								RuntimeError("Invalid OBJECT ID for BUILDING ("+Tbui\typ+")")
							EndIf
							
						Case "unitid"
							If ha_typeexists(Cclass_unit,Int(val$)) Then
								For Tbui.Tbui=Each Tbui
									If Tbui\mode=2 Then
										If Tbui\id=i Then
											Tbui\typ=-Int(val$)
											Exit
										EndIf
									EndIf
								Next
							Else
								RuntimeError("Invalid UNIT ID for BUILDING ("+Tbui\typ+")")
							EndIf
							
						Case "buildspace"
							set=0
							typ=0
							Select val$
								Case "land" typ=0
								Case "land and water" typ=1
								Case "water" typ=2
								Case "shore" typ=3
								Case "hill" typ=4
								Case "shallow water" typ=5
								Case "at object" typ=6
								Default RuntimeError "Invalid BUILDSPACE for BUILDING '"+val$+"'"
							End Select
							For Tbui.Tbui=Each Tbui
								If Tbui\mode=1 Then
									If Tbui\id=i Then
										Tbui\typ=typ
										set=1
										Exit
									EndIf
								EndIf
							Next
							If set=0 Then
								Tbui.Tbui=New Tbui
								Tbui\mode=1
								Tbui\id=i
								Tbui\typ=typ
							EndIf
						
						Case "buildingsite"
							If ha_typeexists(Cclass_object,Int(val$)) Then
								For Tbui.Tbui=Each Tbui
									If Tbui\mode=4 Then
										If Tbui\id=i Then
											Tbui\typ=Int(val$)
											set=1
											Exit
										EndIf
									EndIf
								Next
								If set=0 Then
									Tbui.Tbui=New Tbui
									Tbui\mode=4
									Tbui\id=i
									Tbui\typ=Int(val$)
								EndIf
							Else
								RuntimeError("Invalid OBJECT ID for BUILDING buildplace ("+Tbui\typ+")")
							EndIf

						
						Case "req"
							Tbui.Tbui=New Tbui
							Tbui\id=i
							Tbui\mode=0
							split$(val$,",",1)
							;Typ
							Tbui\typ=Int(Trim(splits$(0)))
							If ha_typeexists(Cclass_item,Tbui\typ)=0 Then
								RuntimeError("Invalid REQ ITEM ID for BUILDING ("+Tbui\typ+")")
							EndIf
							;Count
							splits$(1)=Trim(splits$(1))
							If Int(splits$(1))<=0 Then splits$(1)=1
							Tbui\count=Int(splits$(1))
							
						Case "atobject"
							If ha_typeexists(Cclass_object,Int(val$)) Then
								Tbui.Tbui=New Tbui
								Tbui\id=i
								Tbui\mode=3
								Tbui\typ=Int(val$)
							Else
								RuntimeError("Invalid AT OBJECT ID for BUILDING ("+Tbui\typ+")")
							EndIf
							
						Case "script"
							If val$="start" Then
								For Tbui.Tbui=Each Tbui
									If Tbui\mode=2 And Tbui\id=i Then
										While Not Eof(stream)
											val$=ReadLine(stream)
											If val$="script=end" Then
												Exit
											Else
												Tbui\script$=Tbui\script$+Trim(val$)+"Ś"
											EndIf
										Wend
										Exit
									EndIf
								Next
							EndIf
							
						Case "group"
							Tbui\group=group_set$(Cclass_unit,val$)
						
						Default RuntimeError "Invalid BUILDING Property '"+var$+"'"
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

load_buildings()
