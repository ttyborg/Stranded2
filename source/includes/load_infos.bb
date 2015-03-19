;############################################ Load Infos

Global info_count=0
Const Cinfo_count=100

Dim Dinfo_name$(Cinfo_count)			;Name
Dim Dinfo_frame(Cinfo_count)			;Icon Frame

Dim Dinfo_script$(Cinfo_count)			;Script Stuff?
Dim Dinfo_scriptk$(Cinfo_count)			;Script Key
Dim Dinfo_scripto$(Cinfo_count)			;Script Override

Dim Dinfo_group$(Cinfo_count)			;Group

Dim Dinfo_descr$(Cinfo_count)			;Description



;### Load Infos

Function load_infos()
	Local in$,var$,val$
	Local i,equal
	Local stream
	Local state_count=0
	Local file$

	Local dir=ReadDir("sys")
	If dir=0 Then RuntimeError("Unable to read sys dir")
	Repeat
		file$=NextFile(dir)
		If Left(file$,5)="infos" And Lower(Right(file$,4))=".inf" Then
			
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
						Case "id"
							info_count=info_count+1
							i=Int(val$)
							If (i<1 Or i>Cinfo_count) Then RuntimeError("ID between 1 and "+Cinfo_count+" expected!")
						
						Case "const" const_set(val$,i)
						Case "name" Dinfo_name$(i)=val$
						Case "frame"
							Dinfo_frame(i)=Int(val$)
							If (Dinfo_frame(i)<0 Or Dinfo_frame(i)>99) Then RuntimeError("Frame between 0 and 99 expected!")
							
						Case "var"
							split(val$)
							defvar_set(Cclass_info,i)
							
						Case "script"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="script=end" Then
										Exit
									Else
										Dinfo_script$(i)=Dinfo_script$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
								Dinfo_scripto$(i)=Dinfo_script$(i)
								Dinfo_scriptk$(i)=preparse_string$(Dinfo_script$(i))
							EndIf
							
						
						;Group
						Case "group"
							Dinfo_group$(i)=group_set$(Cclass_info,val$)						
						
						;Description
						Case "description"
							If val$="start" Then
								While Not Eof(stream)
									val$=ReadLine(stream)
									If val$="description=end" Then
										Exit
									Else
										Dinfo_descr$(i)=Dinfo_descr$(i)+Trim(val$)+"Ś"
									EndIf
								Wend
							EndIf
						
						Default RuntimeError "Invalid INFO Property '"+var$+"'"
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

load_infos()
