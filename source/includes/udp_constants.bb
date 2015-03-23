;################################################# Constants

;Encryption
Const Cudp_encr=1							;Use Encryption?
Const Cudp_encrk=5							;Encryption Key

;Type Sizes
Const Cudp_size_byte=1						;Byte Size
Const Cudp_size_short=2						;Short Size
Const Cudp_size_int=4						;Integer Size
Const Cudp_size_float=4						;Float Size
Const Cudp_size_tiny=1						;Tiny Text Size (+1 per Char)
Const Cudp_size_txt=2						;Text Size (+1 per Char)

;System Message IDs
Const Cudp_sys=250							;Start of System Messages
Const Cudp_sys_ping=253						;Ping / Heartbeat
Const Cudp_sys_req=254						;Packet Request
Const Cudp_sys_ack=255						;Packet Acknowledgement

;Connection Establishing IDs
;Request
Const Cudp_cest_req=10						;Connection Request
;Accepts
Const Cudp_cest_ack=20						;Connection Request -> Acknowledgment
Const Cudp_cest_exists=21					;Connection Request -> Connection already exists
;Denials / Errors
Const Cudp_cest_den=30						;Connection Request -> Denial
Const Cudp_cest_nohost=31					;Connection Request -> No Host
Const Cudp_cest_timeout=32					;Connection Request -> Timeout

;Delete ID Distance Thingy
Const Cudp_deld=32767
Const Cudp_win_limit=65534
Const Cudp_win_range=32767

Const Cudp_win_qtimeout=3000
