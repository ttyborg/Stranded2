;################################################# Unreal Software UDP Library

; Unreal Software UDP Library
; Šby Unreal Software 2006
; www.unrealsoftware.de
;
; ICQ:	136125220
; Mail:	udplibrary@unrealsoftware.de
;
; Based on "A Reliable Messaging Protocol" by
; Martin Brownlow (martinbrownlow@msn.com) out
; of "Game Programming Gems 5" - Thanks!


;################################################# Reserved Names

; Every variable/type/constant/function used
; in this library begins with one of the
; following prefixes:
;
; udp			used for Globals & Functions
; Cudp			used for Constants
; Tudp			used for Types


;################################################# Includes

;Constants
Include "includes\udp_constants.bb"

;Variables (Globals and Types)
Include "includes\udp_variables.bb"

;Basic Functions
Include "includes\udp_functions.bb"

;Message Functions
Include "includes\udp_messages.bb"

;Processing
Include "includes\udp_processing.bb"
