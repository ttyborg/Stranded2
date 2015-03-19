.lib "user32.dll"

GetActiveWindow%():"GetActiveWindow"
GetSystemMenu% (window, flags)
EnableMenuItem% (menu, item, flags)

GetDesktopWindow%():"GetDesktopWindow" 
GetWindowTextLength%(hWnd%):"GetWindowTextLengthA" 
GetWindowText%(hWnd%,bank*,cch%):"GetWindowTextA" 
GetWindow%(hWnd%,wCmd%):"GetWindow" 
GetWindowLong%(hWnd%,wIndx%):"GetWindowLongA" 
GetParent%(hWnd%):"GetParent" 
BringWindowToTop%(hWnd%):"BringWindowToTop" 
IsWindow%(hWnd%):"IsWindow" 
IsWindowVisible%(hWnd%):"IsWindowVisible" 
CloseWindow%(hWnd%):"CloseWindow" 
ShowWindow%(hWnd%,wCmd%):"ShowWindow" 

OpenClipboard%(hwnd%):"OpenClipboard"
CloseClipboard%():"CloseClipboard"
ExamineClipboard%(format%):"IsClipboardFormatAvailable"
EmptyClipboard%():"EmptyClipboard"
GetClipboardData$(format%):"GetClipboardData"
SetClipboardData%(format%,txt$):"SetClipboardData"

WS_EX_TOPMOST%(hwnd%):"WS_EX_TOPMOST"
