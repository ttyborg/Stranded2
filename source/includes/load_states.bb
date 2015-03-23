;############################################ Load States

Global state_count=0
Const Cstate_count=70

Dim Dstate_name$(Cstate_count)			;Name
Dim Dstate_frame(Cstate_count)			;Icon Frame
Dim Dstate_icon(Cstate_count)			;Icon Image
Dim Dstate_script$(Cstate_count)		;Script

;Active?
Dim Dstate_a(Cstate_count)
Dstate_a(Cstate_frostbite)=1
Dstate_a(Cstate_fracture)=1
Dstate_a(Cstate_electroshock)=1
Dstate_a(Cstate_bloodrush)=1
Dstate_a(Cstate_tame)=1
Dstate_a(Cstate_ai_stick)=1
Dstate_a(Cstate_speedmod)=1


;### Load States

Function load_states()
	state_count=0
	Local stream=ReadFile("sys\states.inf")
	If stream=0 Then RuntimeError("Unable to read sys\states.inf")
	Local in$,var$,val$
	Local i,equal
	While Not Eof(stream)
		in$=ReadLine(stream)
		equal=Instr(in$,"=")
		If Left(in$,1)="#" Then equal=0
		If equal>0 Then
			var$=Trim(Left(in$,equal-1))
			val$=trimspace(Mid(in$,equal+1,-1))
			Select var$
				Case "id"
					state_count=state_count+1
					i=Int(val$)
					If (i<1 Or i>Cstate_count) Then RuntimeError("ID between 1 and "+Cstate_count+" expected!")
					Dstate_icon(i)=gfx_states
					
				
				Case "const" const_set(val$,i)	
				Case "name" Dstate_name$(i)=val$
				Case "frame"
					Dstate_frame(i)=Int(val$)
					If (Dstate_frame(i)<0 Or Dstate_frame(i)>30) Then RuntimeError("Frame between 0 and 30 expected!")
					
				Case "icon"
					Dstate_icon(i)=load_res(val$,Cres_image,0)
					If Dstate_icon(i)<>0 Then
						Dstate_frame(i)=0
					Else
						Dstate_icon(i)=gfx_states
					EndIf
					
				Case "script"
					If i<29 Then RuntimeError("States from 1-29 are fixed states which cannot have a script")
					If val$="start" Then
						While Not Eof(stream)
							val$=ReadLine(stream)
							If val$="script=end" Then
								Exit
							Else
								Dstate_script$(i)=Dstate_script$(i)+Trim(val$)+"Ś"
							EndIf
						Wend
					EndIf
					
				
				Default RuntimeError "Invalid STATE Property '"+var$+"'"
			End Select
		EndIf
	Wend
	CloseFile(stream)
End Function

load_states()
