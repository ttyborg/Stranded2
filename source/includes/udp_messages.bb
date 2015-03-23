;################################################# Initiale Message for Sending

;Send Message
Function udp_send(ip,port,msgid,reliable=1)
	;Create new Message
	Tudp_msg.Tudp_msg=New Tudp_msg
	Tudp_msg\ip=ip
	Tudp_msg\port=port
	;Reliability
	Tudp_msg\reliable=reliable
	;Bank
	Tudp_msg\db=CreateBank()
	;Message ID
	udp_w_byte(msgid)
	;Set to Start
	If msgid>=Cudp_sys Then
		Tudp_msg\system=1
	EndIf
End Function


;################################################# Write Commands

;Write Byte
Function udp_w_byte(value)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	ResizeBank Tudp_msg\db,size+Cudp_size_byte
	PokeByte Tudp_msg\db,size,value
End Function

;Write Short
Function udp_w_short(value)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	ResizeBank Tudp_msg\db,size+Cudp_size_short
	PokeShort Tudp_msg\db,size,value
End Function

;Write Int
Function udp_w_int(value)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	ResizeBank Tudp_msg\db,size+Cudp_size_int
	PokeShort Tudp_msg\db,size,value
End Function

;Write Float
Function udp_w_float(value#)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	ResizeBank Tudp_msg\db,size+Cudp_size_float
	PokeFloat Tudp_msg\db,size,value#
End Function

;Write Tiny Text
Function udp_w_tiny(value$)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	l=Len(value$)
	If l>255 Then l=255
	ResizeBank Tudp_msg\db,size+Cudp_size_tiny+l
	PokeByte Tudp_msg\db,size,l
	For i=1 To l
		PokeByte Tudp_msg\db,size+i,Asc(Mid(value$,i,1))
	Next
End Function

;Write Text
Function udp_w_txt(value$)
	Tudp_msg.Tudp_msg=Last Tudp_msg
	size=BankSize(Tudp_msg\db)
	l=Len(value$)
	If l>65535 Then l=65535
	ResizeBank Tudp_msg\db,size+Cudp_size_txt+l
	PokeShort Tudp_msg\db,size,l
	For i=1 To l
		PokeByte Tudp_msg\db,size+i+1,Asc(Mid(value$,i,1))
	Next
End Function


;################################################# Read Commands

;Read Byte
Function udp_r_byte()
	If udp_indbp<=udp_indbs-Cudp_size_byte Then
		value=PeekByte(udp_indb,udp_indbp)
		udp_indbp=udp_indbp+Cudp_size_byte
		Return value
	Else
		Return 0
	EndIf
End Function

;Read Short
Function udp_r_short()
	If udp_indbp<=udp_indbs-Cudp_size_short Then
		value=PeekShort(udp_indb,udp_indbp)
		udp_indbp=udp_indbp+Cudp_size_short
		Return value
	Else
		Return 0
	EndIf
End Function

;Read Int
Function udp_r_int()
	If udp_indbp<=udp_indbs-Cudp_size_int Then
		value=PeekInt(udp_indb,udp_indbp)
		udp_indbp=udp_indbp+Cudp_size_int
		Return value
	Else
		Return 0
	EndIf
End Function

;Read Float
Function udp_r_float#()
	If udp_indbp<=udp_indbs-Cudp_size_float Then
		value#=PeekFloat(udp_indb,udp_indbp)
		udp_indbp=udp_indbp+Cudp_size_float
		Return value#
	Else
		Return 0
	EndIf
End Function

;Read Tiny
Function udp_r_tiny$()
	l=PeekByte(udp_indb,udp_indbp)
	value$=""
	For i=1 To l
		byte=PeekByte(udp_indb,udp_indbp+i)
		value$=value$+Chr(byte)
	Next
	udp_indbp=udp_indbp+Cudp_size_tiny+l
	Return value$
End Function

;Read Text
Function udp_r_txt$()
	l=PeekShort(udp_indb,udp_indbp)
	value$=""
	For i=1 To l
		byte=PeekByte(udp_indb,udp_indbp+i+1)
		value$=value$+Chr(byte)
	Next
	udp_indbp=udp_indbp+Cudp_size_txt+l
	Return value$
End Function
