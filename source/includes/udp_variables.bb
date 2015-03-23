;################################################# Variables

;Stream
Global udp							;UDP Stream
Global udp_port						;UDP Stream Port
Global udp_ip						;UDP Stream IP (own IP)
Global udp_host						;UDP System is Host?
Global udp_hostip					;UDP Host IP
Global udp_hostport					;UDP Host Port
Global udp_indb						;UDP In Data Bank
Global udp_indbp					;UDP In Data Bank Position
Global udp_indbs					;UDP In Data Bank Size

;Timers
Global udp_ms						;UDP Millisecs
Global udp_sendtimer				;UDP Send Timer
Global udp_sendrate=100				;UDP Packet Send Rate (ms)
Global udp_request=100				;UDP Packet Request Rate (ms)
Global udp_rerequest=500			;UDP Packet Re-Request Rate (ms)
Global udp_ack=2000					;UDP Packet Acknowledgement Rate (ms)
Global udp_timeout=20000			;UDP Connection Timeout


;################################################# Types

;Connection Data
Type Tudp_con
	;Address
	Field ip						;IP of Connection
	Field port						;Port of Connection
	;IDs
	Field iid						;Incoming Packet ID Counter
	Field sid						;Sending Packet ID Counter
	;Timers
	Field ack						;Acknowledgement Timer
	Field lastaction				;Last Action
	Field resend					;Resend Timer
End Type

;Incoming Packet Queue
Type Tudp_ipq
	Field ip						;IP of Sender
	Field port						;Port of Sender
	Field id						;ID
	Field db						;Data Bank
	Field reliable					;Is Reliable?
	Field system					;Is System Message?
	Field checked
	Field time
End Type

;Sended Packet Queue
Type Tudp_spq
	Field ip						;IP of Receiver
	Field port						;Port of Receiver
	Field id						;ID
	Field db						;Data Bank
	Field sended					;Sended?
	Field reliable					;Is Reliable?
	Field time
End Type

;Sended Message
Type Tudp_msg
	Field ip						;IP of Receiver
	Field port						;Port of Receiver
	Field db						;Data Bank
	Field reliable					;Is Reliable?
	Field system					;Is System Message?
End Type

;Connection Establishing Answers (cached to avoid stream overwrite caused by direct send)
Type Tudp_cest
	Field ip						;IP of Receiver
	Field port						;Port of Receiver
	Field msg						;Message Byte
End Type

;Resends (cached to avoid stream overwrite caused by direct send)
Type Tudp_resend
	Field ip						;IP
	Field port						;Port
	Field id						;ID
	Field db						;Data
End Type
