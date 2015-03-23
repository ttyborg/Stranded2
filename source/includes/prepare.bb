;############################################ Prepare

;Cache Viewrange
Function prepare_cache_viewranges()
	For i=0 To Cobject_count
		Dobject_afc(i)=(Dobject_autofade(i)*set_viewfac#)+300
	Next
	For i=0 To Cunit_count
		Dunit_afc(i)=(Dunit_autofade(i)*set_viewfac#)+300
	Next
	For i=0 To Citem_count
		Ditem_afc(i)=(Ditem_autofade(i)*set_viewfac#)+300
	Next
End Function
