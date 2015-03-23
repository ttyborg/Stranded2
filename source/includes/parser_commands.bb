;############################################ Parse Commands

Function parse_commands(jumptosemi=1,internal=0)
	p_return$=""
	p_internal=internal
	
	;Seq Debug
	;If set_debug_seq
	;	con_add("debug seq cmd: "+p_com$)
	;EndIf
		
		
	Select p_com$
		
		
		;################################################# BASIC
		
		Case "terrain"
			xpos#=param()									;X
			parse_comma(1)
			zpos#=param()									;Z
			parse_comma(1)
			mode=Int(param())								;Mode
			x#=(xpos#+(ter_size/2*Cworld_size))/Cworld_size
			z#=(zpos#+(ter_size/2*Cworld_size))/Cworld_size
			Select mode					
				Case 0										;Get Height					
					p_return$=TerrainHeight(ter,x,z)*100.
				Case 1										;Set Height
					parse_comma(1)	
					height#=param()		
					ModifyTerrain(ter,x,z,height#/100.)
				Case 2										;Modify Height								
					parse_comma(1)	
					height#=param()
					height#=(TerrainHeight(ter,x,z)*100)+height#
					If height#<0 Then height#=0
					If height#>1000 Then height#=100
					ModifyTerrain(ter,x,z,height#/100.)
				Case 3										;Terrain Matrix X
					p_return$=Int(x)
				Case 4										;Terrain Matrix Z
					p_return$=Int(z)
			End Select
		
		Case "compass"
			in_compass=param()
		
		;Eat/Drink/Consume
		Case "eat","drink","consume"
			;FX
			Select p_com$
				Case "eat"
					sfx_emit sfx_eat,cam
					If m_menu=Cmenu_if_items Then
						If Ditem_iconh(tmp_iseltyp)<>0 Then
							p_pixelizeeat(tmp_iselx,tmp_isely,Ditem_iconh(tmp_iseltyp),0,1,0)
						EndIf
					EndIf
				Case "drink"
					sfx_emit sfx_drink,cam
					If m_menu=Cmenu_if_items Then
						If Ditem_iconh(tmp_iseltyp)<>0 Then
							p_pixelizeeat(tmp_iselx,tmp_isely,Ditem_iconh(tmp_iseltyp),0,1,1)
						EndIf
					EndIf
			End Select
			;Decrease Item Count
			If p_env_class=Cclass_item Then
				free_item(p_env_id,1)
			EndIf
			;Parameter Stuff
			If parse_param() Then
				cunit=g_player
				chealth#=param()									;P1 - Health
				If parse_comma(0) Then chunger#=param()				;P2 - Hunger
				If parse_comma(0) Then cthirst#=param()				;P3 - Thirst
				If parse_comma(0) Then cexhaustion#=param()			;P4 - Exhaustion
				If parse_comma(0) Then cunit=param()				;P5 - Unit ID
				If con_unit(cunit) Then
					TCunit\health#=TCunit\health#+chealth#
					TCunit\hunger#=TCunit\hunger#-chunger#
					TCunit\thirst#=TCunit\thirst#-cthirst#
					TCunit\exhaustion#=TCunit\exhaustion#-cexhaustion#
					unit_values()
				EndIf
			EndIf
			
		;Fry
		Case "fry"
			sfx(sfx_fizzle)
			If m_menu=Cmenu_if_items Then
				If Ditem_iconh(tmp_iseltyp)<>0 Then
					p_pixelizeeat(tmp_iselx,tmp_isely,Ditem_iconh(tmp_iseltyp),1,1,0)
				EndIf
			EndIf
			
		;Max Health
		Case "maxhealth"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			value#=0
			If parse_comma(0) Then
				value#=param()									;Maxhealth Change
			EndIf
			p_return$=0
			Select classint
				Case Cclass_unit
					For Tunit.Tunit=Each Tunit
						If Tunit\id=id Then
							Tunit\health_max#=Tunit\health_max#+value#
							If Tunit\health_max#<0 Then Tunit\health_max#=0
							If Tunit\health#>Tunit\health_max# Then Tunit\health#=Tunit\health_max#
							p_return$=Tunit\health_max#
							Exit
						EndIf
					Next
				Case Cclass_object
					For Tobject.Tobject=Each Tobject
						If Tobject\id=id Then
							Tobject\health_max#=Tobject\health_max#+value#
							If Tobject\health_max#<0 Then Tobject\health_max#=0
							If Tobject\health#>Tobject\health_max# Then Tobject\health#=Tobject\health_max#
							p_return$=Tobject\health_max#
							Exit
						EndIf
					Next
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\id=id Then
							p_return$=Ditem_health#(Titem\typ)
							Exit
						EndIf
					Next
			End Select
			
		;Jade
		Case "jade"
			cvalue#=param()
			If con_unit(g_player) Then
				TCunit\hunger#=TCunit\hunger#+cvalue#
				TCunit\thirst#=TCunit\thirst#+cvalue#
				TCunit\exhaustion#=TCunit\exhaustion#+cvalue#
				unit_values()
			EndIf
			
		;Con
		Case "con"
			in_conin$=params()
			con_enter()
			
		;Exec
		Case "exec"
			exec$=params()
			If gui_msgdecide("Execute '"+exec$+"'?",Cbmpf_yellow) Then
				con_add("Executing '"+exec$+"' ...",Cbmpf_yellow)
				ExecFile(exec$)
			Else
				con_add("Permission denied to execute '"+exec$+"'.",Cbmpf_red)
			EndIf
						
		;Definition Extend
		Case "def_extend"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Typ
			parse_comma(1)
			source$=params()								;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							txt$=Tx\value$
							Exit
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			Else
				Select classint
					Case Cclass_object
						If typ<1 Or typ>Cobject_count Then parse_error("typerange",typ)
						Dobject_script$(typ)=Dobject_script$(typ)+"Ś"+txt$
						Dobject_scriptk$(typ)=preparse_string$(Dobject_script$(typ))
					Case Cclass_unit
						If typ<1 Or typ>Cunit_count Then parse_error("typerange",typ)
						Dunit_script$(typ)=Dunit_script$(typ)+"Ś"+txt$
						Dunit_scriptk$(typ)=preparse_string$(Dunit_script$(typ))
					Case Cclass_item
						If typ<1 Or typ>Citem_count Then parse_error("typerange",typ)
						Ditem_script$(typ)=Ditem_script$(typ)+"Ś"+txt$
						Ditem_scriptk$(typ)=preparse_string$(Ditem_script$(typ))
					Case Cclass_info
						If typ<1 Or typ>Cinfo_count Then parse_error("typerange",typ)
						Dinfo_script$(typ)=Dinfo_script$(typ)+"Ś"+txt$
						Dinfo_scriptk$(typ)=preparse_string$(Dinfo_script$(typ))
					Default
						parse_error("wrongclass",parse_getclasstxt$(classint))
				End Select
			EndIf
			
		;Definition Override
		Case "def_override"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Typ
			parse_comma(1)
			source$=params()								;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							txt$=Tx\value$
							Exit
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			Else
				Select classint
					Case Cclass_object
						If typ<1 Or typ>Cobject_count Then parse_error("typerange",typ)
						Dobject_script$(typ)=txt$
						Dobject_scriptk$(typ)=preparse_string$(Dobject_script$(typ))
					Case Cclass_unit
						If typ<1 Or typ>Cunit_count Then parse_error("typerange",typ)
						Dunit_script$(typ)=txt$
						Dunit_scriptk$(typ)=preparse_string$(Dunit_script$(typ))
					Case Cclass_item
						If typ<1 Or typ>Citem_count Then parse_error("typerange",typ)
						Ditem_script$(typ)=txt$
						Ditem_scriptk$(typ)=preparse_string$(Ditem_script$(typ))
					Case Cclass_info
						If typ<1 Or typ>Cinfo_count Then parse_error("typerange",typ)
						Dinfo_script$(typ)=txt$
						Dinfo_scriptk$(typ)=preparse_string$(Dinfo_script$(typ))
					Default
						parse_error("wrongclass",parse_getclasstxt$(classint))
				End Select
			EndIf
			
		;Defintion Free
		Case "def_free"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Typ
			Select classint
				Case Cclass_object
					If typ<1 Or typ>Cobject_count Then parse_error("typerange",typ)
					Dobject_script$(typ)=""
					Dobject_scriptk$(typ)=""
				Case Cclass_unit
					If typ<1 Or typ>Cunit_count Then parse_error("typerange",typ)
					Dunit_script$(typ)=""
					Dunit_scriptk$(typ)=""
				Case Cclass_item
					If typ<1 Or typ>Citem_count Then parse_error("typerange",typ)
					Ditem_script$(typ)=""
					Ditem_scriptk$(typ)=""
				Case Cclass_info
					If typ<1 Or typ>Cinfo_count Then parse_error("typerange",typ)
					Dinfo_script$(typ)=""
					Dinfo_scriptk$(typ)=""
				Default
					parse_error("wrongclass",parse_getclasstxt$(classint))
			End Select
			
		;Create
		Case "create"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Typ
			If parse_comma(0)
				xpos#=param()									;X
				parse_comma(1)
				zpos#=param()									;Z
			Else
				xpos#=EntityX(g_cplayer\h)
				zpos#=EntityZ(g_cplayer\h)
			EndIf
			p_return$=0
			Select classint
				Case Cclass_object
					set_object(-1,typ,xpos#,zpos#)
					If Handle(TCobject)<>0 Then
						p_return$=TCobject\id
					EndIf
				Case Cclass_unit
					set_unit(-1,typ,xpos#,zpos#)
					If Handle(TCunit)<>0 Then
						p_return$=TCunit\id
						ai_ini()
					EndIf
				Case Cclass_item
					count=1
					If parse_comma(0) Then
						count=param()
					EndIf
					set_item(-1,typ,xpos#,e_tery(xpos#,zpos#),zpos#,count)
					If Handle(TCitem)<>0 Then
						p_return$=TCitem\id
					EndIf
				Case Cclass_info
					set_info(-1,typ,xpos#,e_tery(xpos#,zpos#),zpos#)
					If Handle(TCinfo)<>0 Then
						p_return$=TCinfo\id
					EndIf
				Default
					parse_error("wrongclass",parse_getclasstxt$(classint))
			End Select
			
		;randomcreate
		Case "randomcreate"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Typ
			xpos#=0
			zpos#=1000000
			count=1
			If parse_comma(0)
				xpos#=param()									;Y Min
				If parse_comma(0) Then
					zpos#=param()									;Y Max
					If parse_comma(0) Then
						count=param()									;Count
					EndIf
				EndIf
			EndIf
			;Create
			p_return$=randomcreate(classint,typ,xpos#,zpos#,count)
			
			
		;Skipevent
		Case "skipevent"
			p_skipevent=1
			tmp_skipevent=1
			
		;Event
		Case "event"
			event$=params$()
			If parse_comma(0) Then
				class$=param()									;Class
				If class$="global" Then
					parse_globalevent(event$,"triggered by event command")
				Else
					classint=parse_getclass(class$)
					parse_comma(1)
					id=param()										;ID
					parse_emuevent(classint,id,event$,"triggered by event command")
				EndIf
			Else
				parse_emuevent(p_env_class,p_env_id,event$,"triggered by event command")
			EndIf
			
		;Areal Event
		Case "areal_event"
			event$=params$()									;Event
			parse_comma(1)
			x#=param()											;X
			parse_comma(1)
			y#=param()											;Y
			parse_comma(1)
			z#=param()											;Z
			range=50
			eventlimit=1
			If parse_comma(0) Then
				range=param()									;Range
			EndIf
			If parse_comma(0) Then
				eventlimit=param()								;Eventlimit
			EndIf
			Local tempp=CreatePivot()
			PositionEntity tempp,x#,y#,z#
			skip=0
			c=0
			;Objects
			For Tobject.Tobject=Each Tobject
				If EntityDistance(Tobject\h,tempp)<range Then
					skip=set_parsecache(Cclass_object,Tobject\id,event$)
					If Instr(Dobject_scriptk$(Tobject\typ),"Ś"+event$) Then
						parse_task(Cclass_object,Tobject\id,event$,"",Dobject_script(Tobject\typ))
						skip=1
					EndIf
					If skip Then
						c=c+1
						If c>=eventlimit And eventlimit>0 Then Exit
					EndIf
				EndIf
			Next
			;Units
			If c<eventlimit Or eventlimit=0 Then
				For Tunit.Tunit=Each Tunit
					If EntityDistance(Tunit\h,tempp)<range Then
						skip=set_parsecache(Cclass_unit,Tunit\id,event$)
						If Instr(Dunit_scriptk$(Tunit\typ),"Ś"+event$) Then
							parse_task(Cclass_unit,Tunit\id,event$,"",Dunit_script(Tunit\typ))
							skip=1
						EndIf
						If skip Then
							c=c+1
							If c>=eventlimit And eventlimit>0 Then Exit
						EndIf
					EndIf
				Next
			EndIf
			;Items
			If c<eventlimit Or eventlimit=0 Then
				For Titem.Titem=Each Titem
					If Titem\parent_mode=Cpm_out Then
						If EntityDistance(Titem\h,tempp)<range Then
							skip=set_parsecache(Cclass_item,Titem\id,event$)
							If Instr(Ditem_scriptk$(Titem\typ),"Ś"+event$) Then
								parse_task(Cclass_item,Titem\id,event$,"",Ditem_script(Titem\typ))
								skip=1
							EndIf
							If skip Then
								c=c+1
								If c>=eventlimit And eventlimit>0 Then Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			;Infos
			If c<eventlimit Or eventlimit=0 Then
				For Tinfo.Tinfo=Each Tinfo
					If EntityDistance(Tinfo\h,tempp)<range Then
						skip=set_parsecache(Cclass_info,Tinfo\id,event$)
						If Instr(Dinfo_scriptk$(Tinfo\typ),"Ś"+event$) Then
							parse_task(Cclass_info,Tinfo\id,event$,"",Dinfo_script(Tinfo\typ))
							skip=1
						EndIf
						If skip Then
							c=c+1
							If c>=eventlimit And eventlimit>0 Then Exit
						EndIf
					EndIf
				Next
			EndIf
			FreeEntity tempp
			
			
		;Quicksave
		Case "quicksave"
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=1
			in_quicksave=1
			;Quicksave performed in cull.bb (to avoid crossparsing)
		
		;Quickload
		Case "quickload"
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=2
			in_quicksave=2
			;Quickload performed in cull.bb (to avoid crossparsing)
			
		;Autosave
		Case "autosave"
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=3
			in_quicksave=3
			;Autosave performed in cull.bb (to avoid crossparsing)
			
		;Autoload
		Case "autoload"
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=4
			in_quicksave=4
			;Autoload performed in cull.bb (to avoid crossparsing)
			
		;Loadmap
		Case "loadmap"
			path$=params()												;Path
			ot_skills=0
			ot_items=0
			ot_vars=0
			ot_diary=0
			ot_states=0
			ot_locks=0
			If parse_comma(0) Then
				ot_skills=param()										;Skills
				If parse_comma(0) Then
					ot_items=param()									;Items
					If parse_comma(0) Then
						ot_vars=param()									;Vars
						If parse_comma(0) Then
							ot_diary=param()							;Diary
							If parse_comma(0) Then
								ot_states=param()						;States
								If parse_comma(0) Then
									ot_locks=param()					;Locks
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf	
			EndIf
			cachedata(1,ot_skills,ot_items,ot_vars,ot_diary,ot_states,ot_locks)
			;Perform
			tmp_loadmapcmdmap$=path$
			in_quicksave=5
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=5
			Tin_quicksave\map=tmp_loadmapcmdmap$
			;Loadmap performed in cull.bb (to avoid crossparsing)
			
		;Loadmap Takeover
		Case "loadmaptakeover"
			p_return$=pv_loadmaploaded
		
		;savemap
		Case "savemap"
			path$=params()												;Path
			ot_skills=1
			ot_items=1
			ot_vars=1
			ot_diary=1
			ot_states=1
			ot_locks=1
			If parse_comma(0) Then
				ot_skills=param()										;Skills
				If parse_comma(0) Then
					ot_items=param()									;Items
					If parse_comma(0) Then
						ot_vars=param()									;Vars
						If parse_comma(0) Then
							ot_diary=param()							;Diary
							If parse_comma(0) Then
								ot_states=param()						;States
								If parse_comma(0) Then
									ot_locks=param()					;Locks
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf	
			EndIf
			;Perform
			tmp_loadmapcmdmap$=path$
			in_quicksave=6
			Tin_quicksave.Tin_quicksave=New Tin_quicksave
			Tin_quicksave\mode=6
			Tin_quicksave\map=tmp_loadmapcmdmap$
			Tin_quicksave\skills=ot_skills
			Tin_quicksave\items=ot_items
			Tin_quicksave\vars=ot_vars
			Tin_quicksave\diary=ot_diary
			Tin_quicksave\states=ot_states
			Tin_quicksave\locks=ot_locks
			;Savemap performed in cull.bb (to avoid crossparsing)
			
		;Echo
		Case "echo"
			con_add(params())
			
		;alteritem
		Case "alteritem"
			c=param()													;Req. Count
			parse_comma(1)
			newtype=param()												;New Type / Old type
			newcount=1
			newnewtype=0
			If parse_comma(0) Then
				newcount=param()										;New Count
				If parse_comma(0) Then
					newnewtype=param()									;New Type!
				EndIf
			EndIf
			;Replace Current Item
			If newnewtype=0 Then
				If p_env_class=Cclass_item And p_env_event$="use" Then
					If con_item(p_env_id) Then
						If TCitem\count>=c Then
							;FX
							p_combine(TCitem\typ,newtype)
							;Free C
							free_item(p_env_id,c)
							;New
							itemid=set_item(-1,newtype,0,0,0,newcount)
							If itemid>-1 Then
								store_item(itemid,Cclass_unit,g_player)
							EndIf
						EndIf
					EndIf
				Else
					parse_error("'alteritem' only works in 'on:use'-events when it has got 3 or less parameters")
				EndIf
			;Replace Specific Item
			Else
				For Titem.Titem=Each Titem
					If Titem\parent_class=Cclass_unit Then
						If Titem\parent_id=g_player Then
							If Titem\typ=newtype Then
								If Titem\count>=c Then
									;FX
									p_combine(newtype,newnewtype)
									;Free C
									free_item(Titem\id,c)
									;New
									itemid=set_item(-1,newnewtype,0,0,0,newcount)
									If itemid>-1 Then
										store_item(itemid,Cclass_unit,g_player)
									EndIf
								EndIf
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;Quit
		Case "quit"
			menu_start()
			jumptosemi=0
			
		;Credits
		Case "credits"
			menu_start()
			menu_set(Cmenu_credits,0)
			jumptosemi=0
		
		;Addscript
		Case "addscript"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			source$=params()								;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							txt$=Tx\value$
							Exit
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			Else
				;Global
				If classint=0 Then
					;map_briefing$=map_briefing$+Chr(166)+txt$
					map_briefing$=txt$
					map_briefing_key$=preparse_string$(map_briefing$)
				EndIf
				;Local
				scriptsaved=0
				
				;For Tx.Tx=Each Tx
				;	If Tx\parent_class=classint Then
				;		If Tx\parent_id=id Then
				;			scriptsaved=1
				;			Tx\value$=Tx\value$+Chr(166)+txt$
				;			Tx\key$=preparse_string$(Tx\value$)
				;		EndIf
				;	EndIf
				;Next
				For Tx.Tx=Each Tx
					If Tx\parent_class=classint Then
						If Tx\parent_id=id Then
							If Tx\mode=0 Then
								Delete Tx
							EndIf
						EndIf
					EndIf
				Next
				
				If scriptsaved=0 Then
					Tx.Tx=New Tx
					Tx\parent_class=classint
					Tx\parent_id=id
					Tx\value$=txt$
					Tx\key$=preparse_string$(Tx\value$)
				EndIf
			EndIf

		;Extendscript
		Case "extendscript"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			source$=params()								;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							If Tx\mode=0 Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			Else
				scriptsaved=0
				;Global
				If classint=0 Then
					map_briefing$=map_briefing$+Chr(166)+txt$
					;map_briefing$=txt$
					map_briefing_key$=preparse_string$(map_briefing$)
					scriptsaved=1
				EndIf
				;Local
				For Tx.Tx=Each Tx
					If Tx\parent_class=classint Then
						If Tx\parent_id=id Then
							If Tx\mode=0 Then
								scriptsaved=1
								Tx\value$=Tx\value$+Chr(166)+txt$
								Tx\key$=preparse_string$(Tx\value$)
								Exit
							EndIf
						EndIf
					EndIf
				Next
				If scriptsaved=0 Then
					Tx.Tx=New Tx
					Tx\parent_class=classint
					Tx\parent_id=id
					Tx\value$=txt$
					Tx\key$=preparse_string$(Tx\value$)
					Tx\mode=0
				EndIf
			EndIf
			
		;Setpos / setat
		Case "setpos","setat"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If p_com$="setpos" Then
				parse_comma(1):setpx$=param()					;X
				parse_comma(1):setpy$=param()					;Y
				parse_comma(1):setpz$=param()					;Z
			Else
				parse_comma(1):class2=parse_getclass(param())	;Class 2
				parse_comma(1):id2=param()						;ID 2
				setath=parent_h(class2,id2)
				If setath<>0 Then
					setpx$=EntityX(setath)
					setpy$=EntityY(setath)
					setpz$=EntityZ(setath)
				EndIf
			EndIf
			setph=parent_h(classint,id)
			If setph<>0 Then
				If setpx$="self" Then setpx$=EntityX(setph)
				If setpy$="self" Then setpy$=EntityY(setph)
				If setpz$="self" Then setpz$=EntityZ(setph)
				;Setpos
				If classint=Cclass_item Then
					;Items can only set to a position if they are not stored inside sth.
					For Titem.Titem=Each Titem
						If Titem\id=id Then
							If Titem\parent_class=0 Or Titem\parent_mode=Cpm_out Then
								ha_setpos(setph,classint,id,setpx$,setpy$,setpz$)
							EndIf
							Exit
						EndIf
					Next
				Else
					;Everything als can be set to any position anytime
					ha_setpos(setph,classint,id,setpx$,setpy$,setpz$)
				EndIf
			Else
				parse_error("object",id)
			EndIf
			
		;Setrot
		Case "setrot"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			pitch$=param()									;Pitch
			parse_comma(1)
			yaw$=param()									;Yaw
			parse_comma(1)
			roll$=param()									;Roll
			setph=parent_h(classint,id)
			If setph<>0 Then
				If pitch$="self" Then pitch$=EntityPitch(setph)
				If yaw$="self" Then yaw$=EntityYaw(setph)
				If roll$="self" Then roll$=EntityRoll(setph)
				RotateEntity setph,pitch$,yaw$,roll$
				;Update FRAP Data
				If classint=Cclass_object Then
					For Tobject.Tobject=Each Tobject
						If Tobject\id=id Then
							If Tobject\ch<>0 Then
								RotateEntity Tobject\ch,pitch$,yaw$,roll$
							EndIf
							Exit
						EndIf
					Next
					update=0
					For Tfrap.Tfrap=Each Tfrap
						If Tfrap\parent_class=Cclass_object Then
							If Tfrap\parent_id=id Then
								Tfrap\y#=EntityY(setph)
								Tfrap\pitch#=EntityPitch(setph)
								Tfrap\roll#=EntityRoll(setph)
								update=1
								Exit
							EndIf
						EndIf
					Next
					If update=0 Then
						Tfrap.Tfrap=New Tfrap
						Tfrap\parent_class=Cclass_object
						Tfrap\parent_id=id
						Tfrap\y#=EntityY(setph)
						Tfrap\pitch#=EntityPitch(setph)
						Tfrap\roll#=EntityRoll(setph)
					EndIf
				EndIf
				;Player? -> Rotate Cam
				If classint=Cclass_unit Then
					If id=1 Then
						RotateEntity cam,EntityPitch(setph),EntityYaw(setph),EntityRoll(setph)
					EndIf
				EndIf
			Else
				parse_error("object",id)
			EndIf
			
		;Rpos
		Case "rpos"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1):setpx$=param()					;X
			parse_comma(1):setpy$=param()					;Y
			parse_comma(1):setpz$=param()					;Z
			If parse_comma(0) Then
				pitch$=param()									;Pitch
				parse_comma(1):yaw$=param()						;Yaw
				parse_comma(1):roll$=param()					;Roll
			EndIf
			setph=parent_h(classint,id)
			If setph<>0 Then
				If pitch$="self" Then pitch$=EntityPitch(setph)
				If yaw$="self" Then yaw$=EntityYaw(setph)
				If roll$="self" Then roll$=EntityRoll(setph)
				;Pivot Movement
				Local p=CreatePivot()
				PositionEntity p,EntityX(setph),EntityY(setph),EntityZ(setph)
				RotateEntity p,Float(pitch$),Float(yaw$),Float(roll$)
				MoveEntity p,Float(setpx$),Float(setpy$),Float(setpz$)
				tmp_x#=EntityX(p)
				tmp_y#=EntityY(p)
				tmp_z#=EntityZ(p)
				FreeEntity p
				;Setpos
				If classint=Cclass_item Then
					;Items can only set to a position if they are not stored inside sth.
					For Titem.Titem=Each Titem
						If Titem\id=id Then
							If Titem\parent_class=0 Or Titem\parent_mode=Cpm_out Then
								ha_setpos(setph,classint,id,tmp_x#,tmp_y#,tmp_z#)
							EndIf
							Exit
						EndIf
					Next
				Else
					;Everything als can be set to any position anytime
					ha_setpos(setph,classint,id,tmp_x#,tmp_y#,tmp_z#)
				EndIf
			Else
				parse_error("object",id)
			EndIf			
			
				
		;Store
		Case "store"
			idstring$=param()								;Item ID
			If idstring$="self" Then
				If p_env_class=Cclass_item Then
					idstring$=p_env_id
				EndIf
			EndIf
			parse_comma(1)
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			mode=0											;Inside?
			If parse_comma(0) Then
				mode=param()
			EndIf
			If mode=0 Then
				p_return$=store_item(idstring$,classint,id)
			Else
				tmp_x#=0
				tmp_y#=0
				tmp_z#=0
				If con_item(id) Then
					tmp_x#=EntityX(TCitem\h)
					tmp_y#=EntityY(TCitem\h)
					tmp_z#=EntityZ(TCitem\h)
				EndIf
				If mode=2 Then
					If classint=Cclass_object Then
						object_spawnpos(id)
					EndIf
				EndIf
				p_return$=store_item(idstring$,classint,id,Cpm_out,x#,y#,z#)
			EndIf
			
		;Unstore
		Case "unstore"
			idstring$=param()								;Item ID
			If idstring$="self" Then
				If p_env_class=Cclass_item Then
					idstring$=p_env_id
				EndIf
			EndIf
			count=1
			If parse_comma(0) Then
				count=param()									;Count
			EndIf
			unstore_item(idstring$,count,1)
			If TCitem<>Null Then
				p_return$=TCitem\id
			EndIf
			
		;Kill
		Case "kill"
			idstring$=param()								;ID
			If idstring$="self" Then
				If p_env_class=Cclass_unit Then
					idstring$=p_env_id
				EndIf
			EndIf
			If con_unit(Int(idstring$)) Then
				kill_unit()
			EndIf
			
		;Damage
		Case "damage"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			damage#=param()									;Damage
			ha_damage(classint,id,damage#)
			
		;Heal
		Case "heal","repair"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			damage#=param()									;Damage
			ha_heal(classint,id,damage#)
			
		;Setcount
		Case "setcount","setamount"
			class$=param()									;ID
			If class$="self" Then
				id=p_env_id
			Else
				id=Int(class$)
			EndIf
			parse_comma(1)
			count=param()									;Count
			For Titem.Titem=Each Titem
				If Titem\id=id Then
					If Titem\parent_class=0 Then
						If Abs(count)=0 Then count=1
						Titem\count=Abs(count)
					Else
						parse_error("'setcount' has to be used with an unstored item")
					EndIf
					Exit
				EndIf
			Next
			
		;Ride
		Case "ride","drive"
			If parse_param() Then
				id=param()										;ID
			Else
				id=p_env_id
			EndIf
			If con_unit(id) Then
				;Set Drive
				g_drive=id
				;Rotate Cam to View of Vehicle
				RotateEntity cam,EntityPitch(cam),EntityYaw(TCunit\h),EntityRoll(cam)
				;Get Vehicle Stuff
				game_input_vehicledata(TCunit\typ)
				;Set Mode
				TCunit\ai_mode=0
				TCunit\ai_duration=5000
				TCunit\ai_timer=gt
				;Set Ani
				Animate TCunit\mh,0
				TCunit\ani=0
			EndIf
			
		;Getoff
		Case "getoff"
			If con_unit(g_drive) Then
				ai_mode(ai_idle)
			EndIf
			g_drive=0
			
		;Climate
		Case "climate"
			newclimate$=param()
			Select newclimate$
				Case 0,"normal"
					map_climate=0
					If env_cweather>1 Then
						e_environment_setweather(Rand(0,1))
					EndIf
				Case 1,"arctic"
					map_climate=1
					If env_cweather<>0 And env_cweather<>2 Then
						e_environment_setweather(Rand(0,1)*2)
					EndIf
				Case 2,"sun"
					map_climate=2
					e_environment_setweather(0)
				Case 3,"rain"
					map_climate=3
					e_environment_setweather(1)
				Case 4,"snow"
					map_climate=4
					e_environment_setweather(2)
				Case 5,"thunder"
					map_climate=5
					e_environment_setweather(3)
				Default
					parse_error("'"+newclimate$+"' is no valid climate")
			End Select
			
		;Weather
		Case "weather"
			newclimate$=param()
			Select newclimate$
				Case 0,"sun"
					e_environment_setweather(0)
				Case 1,"rain"
					e_environment_setweather(1)
				Case 2,"snow"
					e_environment_setweather(2)
				Case 3,"thunder"
					e_environment_setweather(3)
				Default
					parse_error("'"+newclimate$+"' is no valid weather")
			End Select
			
		;Rainratio
		Case "rainratio"
			map_rainratio=param()
		
		;Snowratio
		Case "snowratio"
			map_snowratio=param()
			
		;Timer
		Case "timer"
			timer_add()
			
		;Free Timer
		Case "freetimers"
			class$=param()									;Class
			If class$="0" Then
				class$=""
				If parse_comma(0) Then						;Timer Source
					class$=param()
				EndIf
				timer_free(0,0,class$)
			Else
				classint=parse_getclass(class$)
				If classint=-1 Then
					classint=p_env_class
					id=p_env_id
				Else
					parse_comma(1)
					id=param()								;ID
				EndIf
				class$=""
				If parse_comma(0) Then						;Timer Source
					class$=param()
				EndIf
				timer_free(classint,id,class$)
			EndIf
			
		;Timer Count
		Case "timercount"
			class$=param()									;Class
			If class$="0" Then
				p_return$=timer_count(0,0)
			Else
				classint=parse_getclass(class$)
				If classint=-1 Then
					classint=p_env_class
					id=p_env_id
				Else
					parse_comma(1)
					id=param()								;ID
				EndIf
				p_return$=timer_count(classint,id)
			EndIf
			
		;downloadfile
		Case "downloadfile"
			server$=param()									;Server
			parse_comma(1)
			path$=param()									;Path
			parse_comma(1)
			loc$=param()									;Local
			tcp_loadfile(server$,path$,loc$)
			
		;callscript
		Case "callscript"
			server$=param()									;Server
			parse_comma(1)
			path$=param()									;Path
			tcp_callscript(server$,path$)
			If parse_comma(0) Then
				If param()=1 Then							;Execute
					parse_task(0,0,"download","script loaded with callscript",pv_buffer$)
				EndIf
			EndIf
			
		;fileexists
		Case "fileexists"
			path$=param()
			If FileType(path$)<>0 Then
				p_return$=1
			Else
				p_return$=0
			EndIf
		
		;spawntimer
		Case "spawntimer"
			p_return$=0
			idstring$=param()
			If idstring$="self" Then
				id=p_env_id
			Else
				id=Int(idstring$)
			EndIf
			;Set
			If parse_comma(0) Then
				daytimervalue=param()
				For Tobject.Tobject=Each Tobject
					If Tobject\id=id Then
						Tobject\daytimer=daytimervalue
						grow_object(Tobject)
						p_return$=Tobject\daytimer
						;con_add("set daytimer "+daytimervalue)
						Exit
					EndIf
				Next
			;Get
			Else
				For Tobject.Tobject=Each Tobject
					If Tobject\id=id Then
						p_return$=Tobject\daytimer
						;con_add("get daytimer "+Tobject\daytimer)
						Exit
					EndIf
				Next
				If tmp_killclass=Cclass_object And tmp_killid=id Then
					p_return$=tmp_killspawntimer
				Else
					p_return$=parse_kill_get(Cclass_object,id,1)
				EndIf
			EndIf
			
		;growtime
		Case "growtime"
			id=param()
			If id>=0 And id<=Cobject_count
				p_return$=Dobject_growtime(id)
			Else
				p_return$=0
			EndIf
			
		;unitpath
		Case "unitpath"
			unitidstr$=param()						;Unit
			If unitidstr$="self" Then
				unitid=p_env_id
			Else
				unitid=Int(unitidstr$)
			EndIf
			up_setup(unitid)
			parse_comma(1)
			id=param()								;Node 1
			up_addnode(unitid,id)
			While parse_comma(0)
				id=param()							;Node X
				up_addnode(unitid,id)
			Wend
			
		;free unitpath
		Case "freeunitpath"
			unitid=param()							;Unit
			up_free(unitid)
			
		;wateralpha
		Case "wateralpha"
			EntityAlpha env_sea2,Float(param())		;Alpha
			
		;watertexture
		Case "watertexture"
			path$=params()							;Path
			If gfx_waterimg_custom<>0 Then FreeImage gfx_waterimg_custom
			gfx_waterimg_custom=0
			gfx_waterimg_custom=load_image(path$)
			If gfx_waterimg_custom<>0 Then
				If ImageWidth(gfx_waterimg_custom)<>TextureWidth(gfx_water) Then
					If ImageHeight(gfx_waterimg_custom)<>TextureHeight(gfx_water) Then
						ResizeImage gfx_waterimg_custom,TextureWidth(gfx_water),TextureHeight(gfx_water)
					EndIf
				EndIf
				SetBuffer TextureBuffer(gfx_water)
				DrawBlock gfx_waterimg_custom,0,0
				GetColor 0,0
				env_wcol(0)=255-ColorRed()
				env_wcol(1)=255-ColorGreen()
				env_wcol(2)=255-ColorBlue()
				SetBuffer BackBuffer()
			Else
				parse_error("unable to load water texture '"+path$+"'")
			EndIf
			
		;terraintexture
		Case "terraintexture"
			path$=params()							;Path
			grass=1
			If parse_comma(0) Then
				grass=param()
			EndIf
			If Left(path$,8)="generate" Then
				in_opt(4)=0
				If Len(path$)>8 Then
					Select Mid(path$,10,-1)
						Case "desert"
							in_opt(4)=3
							editor_gencolormap(1)
						Case "snow"
							in_opt(4)=4
							editor_gencolormap(1)
						Case "swamp"
							in_opt(4)=5
							editor_gencolormap(1)
						Default
							split(Mid(path$,10,-1))
							e_tercol(splits(0),splits(1),splits(2),0)
							e_tercol(splits(3),splits(4),splits(5),1)
							e_tercol(splits(6),splits(7),splits(8),2)
							editor_gencolormap(0)
					End Select
				Else
					editor_gencolormap(1)
				EndIf
				;Grass
				If grass=1 Then
					grass_map()
					grass_heightmap()
				EndIf
				;Ground
				e_terrainground()
			Else
				If e_terraincm(path$,0) Then
					;Grass
					If grass=1 Then
						grass_map()
						grass_heightmap()
					EndIf
					;Ground
					e_terrainground()
				Else
					parse_error("unable to load terrain texture '"+path$+"'")
				EndIf
			EndIf
			
		;skytexture
		Case "skytexture"
			path$=params()							;Path
			map_skybox$=path$
			If env_sky<>0 Then FreeEntity env_sky
			If map_skybox$="" Then
				env_sky=e_skybox("skies\sky")
			Else
				env_sky=e_skybox("skies\"+map_skybox$)
			EndIf
			EntityFX env_sky,1+8
			EntityOrder env_sky,3
			PositionEntity env_sky,0,500,0
			
		;grasscolor
		Case "grasscolor"
			r=param()
			parse_comma()
			g=param()
			parse_comma()
			b=param()
			grass_colors(r,g,b)
			
			
		;alterobject
		Case "alterobject"
			idstr$=param()									;ID
			If idstr$="self" Then idstr$=p_env_id
			parse_comma()
			newtype=param()									;New Type
			If con_object(Int(idstr$)) Then
				xp#=EntityX(TCobject\h)
				zp#=EntityZ(TCobject\h)
				yawrot#=EntityYaw(TCobject\h)
				id=TCobject\id
				free_object(id)
				set_object(id,newtype,xp#,zp#,yawrot#)
			EndIf
			
		;exit
		Case "exit"
			If p_loopmode>0 Then
				p_loopi=0
				p_loopmode=0
				While p_bracelevel>p_looplevel
					parse_closebrace(0)
					p_bracelevel=p_bracelevel-1
					p_p=p_p+1
				Wend
				jumptosemi=0
			EndIf
			
		;speech
		Case "speech"
			idstr$=param()									;Speechfile
			override=0
			id=0
			If parse_comma(0) Then
				override=param()							;Override?
				If parse_comma(0) Then
					id=param()								;ID
				EndIf
			EndIf
			speech(idstr$,override,id)
		
		;revive
		Case "revive"
			unitidstr$=param()									;Unit
			If unitidstr$="self" Then
				unitid=p_env_id
			Else
				unitid=Int(unitidstr$)
			EndIf
			For TCunit.Tunit=Each Tunit
				If TCunit\id=unitid Then
					;Health
					If TCunit\health_max#>0. Then
						TCunit\health#=Dunit_health#(TCunit\typ)
					Else
						TCunit\health_max#=Dunit_health#(TCunit\typ)
						TCunit\health#=Dunit_health#(TCunit\typ)
					EndIf
					;Picking/Collisions
					If Dunit_col(TCunit\typ)>0 Then
						EntityType TCunit\h,Dunit_col(TCunit\typ)
						EntityRadius TCunit\h,Dunit_colxr#(TCunit\typ),Dunit_colyr#(TCunit\typ)	
					EndIf
					EntityPickMode TCunit\mh,2,1
					;AI Initiale (without setting center)
					ai_ini(0)
					;Player?
					If TCunit\id=g_player Then
						g_player_dead=0
					EndIf
					Exit
				EndIf
			Next
		
		;Debug
		Case "debug"
			dbg1$=param()
			If parse_comma(0) Then
				dbg2$=param()
			EndIf
			dbugcmd(dbg1$,dbg2$)
			
		;Freeze
		Case "freeze"
			p_return$=0
			id=0
			mode=1
			If parse_param() Then
				idstr$=param()							;ID
				If idstr$="self" Then
					id=p_env_id
				Else
					id=Int(idstr$)
				EndIf
				If parse_comma(0) Then
					mode=param()						;Mode
				EndIf
			EndIf
			If id=0 Then
				For Tunit.Tunit=Each Tunit
					freeze_unit(Tunit,mode)
				Next
			Else
				If mode<2 Then
					For Tunit.Tunit=Each Tunit
						If Tunit\id=id Then
							freeze_unit(Tunit,mode)
							Exit
						EndIf
					Next
				Else
					For Tunit.Tunit=Each Tunit
						If Tunit\id=id Then
							p_return$=Tunit\freeze
							Exit	
						EndIf
					Next
				EndIf
			EndIf
			
		;Intersect
		Case "intersect"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If parse_comma(0) Then
				class2=parse_getclass(param())					;Class 2
				parse_comma(1):id2=param()						;ID 2
				p_return$=0
				h1=parent_h(classint,id,1)
				h2=parent_h(class2,id2,1)
				If h1<>0 Then
					If h2<>0 Then
						p_return$=MeshesIntersect(h1,h2)
					Else
						parse_error("object",id2)
					EndIf
				Else
					parse_error("object",id)
				EndIf
			Else
				p_return$=0
				h1=parent_h(classint,id,1)
				If h1<>0 Then
					p_return$=ha_intersect(classint,id)
				Else
					parse_error("object",id)
				EndIf
			EndIf
		
		;storage
		Case "storage"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			mode=0
			If parse_comma(0) Then
				mode=param()
			EndIf
			p_return$=0
			Select classint
				Case Cclass_object
					typ=0
					For Tobject.Tobject=Each Tobject
						If Tobject\id=id Then typ=Tobject\typ:Exit
					Next
					Select mode
						Case 0 p_return$=capacity(classint,id,typ)
						Case 1 p_return$=(capacity(classint,id,typ)-Dobject_maxweight(typ))
						Case 2 p_return$=Dobject_maxweight(typ)
					End Select
				Case Cclass_unit
					For Tunit.Tunit=Each Tunit
						If Tunit\id=id Then typ=Tunit\typ:Exit
					Next
					Select mode
						Case 0 p_return$=capacity(classint,id,typ)
						Case 1 p_return$=(capacity(classint,id,typ)-Dunit_maxweight(typ))
						Case 2 p_return$=Dunit_maxweight(typ)
					End Select
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\id=id Then
							p_return$=Ditem_weight(Titem\typ)
							Exit
						EndIf
					Next
				Default
					p_return$=0
			End Select
		
		;Projectile
		Case "projectile"
			typ=param()									;Type
			parse_comma(1)
			x=param()									;X
			parse_comma(1)
			y=param()									;Y
			parse_comma(1)
			z=param()									;Z
			mode=0
			parse_comma(1)
			mode=param()								;Mode
			offset=0
			pr_wpn=0
			pr_spd#=1
			pr_dmg#=1
			pr_drag#=0
			p=CreatePivot()
			p2=CreatePivot()
			Select mode
				Case 1									;Target Object
					parse_comma(1)
					class$=param()							;Class
					classint=parse_getclass(class$)
					If classint=-1 Then
						classint=p_env_class
						id=p_env_id
					Else
						parse_comma(1)
						id=param()							;ID
					EndIf
					If parse_comma(0) Then
						offset=param()						;Offset
					EndIf
					If parent_pos(classint,id) Then
						PositionEntity p,x,y,z
						PositionEntity p2,tmp_x#,tmp_y#,tmp_z#
						PointEntity p,p2
						If parse_comma(0) Then
							pr_wpn=Int(param())
							If parse_comma(0) Then
								pr_spd#=Float(param())
								If parse_comma(0) Then
									pr_dmg#=Float(param())
									If parse_comma(0) Then
										pr_drag#=Float(param())
									EndIf
								EndIf
							EndIf
						EndIf
						If classint=Cclass_unit Then
							For Tunit.Tunit=Each Tunit
								If Tunit\id=id Then
									PositionEntity p2,tmp_x#,tmp_y#-(Dunit_colyr#(Tunit\typ)/2.0),tmp_z#
									PointEntity p,p2
									Exit
								EndIf
							Next
						EndIf
						pro_add_script(typ,x,y,z,EntityPitch(p),EntityYaw(p),offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
					Else
					EndIf
				Case 2									;Target Position
					parse_comma(1)
					x2=param()								;X
					parse_comma(1)
					y2=param()								;Y
					parse_comma(1)
					z2=param()								;Z
					If parse_comma(0) Then
						offset=param()						;Offset
					EndIf
					PositionEntity p,x,y,z
					PositionEntity p2,x2,y2,z2
					PointEntity p,p2	
					If parse_comma(0) Then
						pr_wpn=Int(param())
						If parse_comma(0) Then
							pr_spd#=Float(param())
							If parse_comma(0) Then
								pr_dmg#=Float(param())
								If parse_comma(0) Then
									pr_drag#=Float(param())
								EndIf
							EndIf
						EndIf
					EndIf			
					pro_add_script(typ,x,y,z,EntityPitch(p),EntityYaw(p),offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
				Case 3									;Direction								
					parse_comma(1)	
					pitch=param()							;Pitch
					parse_comma(1)
					yaw=param()								;Yaw
					If parse_comma(0) Then
						offset=param()						;Offset
					EndIf
					If parse_comma(0) Then
						pr_wpn=Int(param())
						If parse_comma(0) Then
							pr_spd#=Float(param())
							If parse_comma(0) Then
								pr_dmg#=Float(param())
								If parse_comma(0) Then
									pr_drag#=Float(param())
								EndIf
							EndIf
						EndIf
					EndIf
					pro_add_script(typ,x,y,z,pitch,yaw,offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
				Case 4									;Target Player
					If parse_comma(0) Then
						offset=param()						;Offset
					EndIf
					If g_cplayer<>Null Then
						PositionEntity p,x,y,z
						PointEntity p,g_cplayer\h
					EndIf
					If parse_comma(0) Then
						pr_wpn=Int(param())
						If parse_comma(0) Then
							pr_spd#=Float(param())
							If parse_comma(0) Then
								pr_dmg#=Float(param())
								If parse_comma(0) Then
									pr_drag#=Float(param())
								EndIf
							EndIf
						EndIf
					EndIf
					pro_add_script(typ,x,y,z,EntityPitch(p),EntityYaw(p),offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
				Case 5
					PositionEntity p,x,y,z
					parse_comma()
					found=0
					lowdist=Int(param())+1
					For Tunit.Tunit=Each Tunit
						If Tunit\id>=100 Then
							If Tunit\health>0 Then
								If EntityDistance(Tunit\h,p)<lowdist Then
									lowdist=EntityDistance(Tunit\h,p)
									PositionEntity p2,EntityX(Tunit\h),EntityY(Tunit\h)-(Dunit_colyr#(Tunit\typ)/2.0),EntityZ(Tunit\h)
									found=1
								EndIf
							EndIf
						EndIf
					Next
					If parse_comma(0) Then
						offset=param()
					EndIf
					If parse_comma(0) Then
						pr_wpn=Int(param())
						If parse_comma(0) Then
							pr_spd#=Float(param())
							If parse_comma(0) Then
								pr_dmg#=Float(param())
								If parse_comma(0) Then
									pr_drag#=Float(param())
								EndIf
							EndIf
						EndIf
					EndIf
					If found=1 Then
						PointEntity p,p2
						pro_add_script(typ,x,y,z,EntityPitch(p),EntityYaw(p),offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
					EndIf
				Case 6
					PositionEntity p,x,y,z
					parse_comma()
					found=0
					lowdist=Int(param())+1
					For Tunit.Tunit=Each Tunit
						If Tunit\id>=100 Then
							If Tunit\health>0 Then
								If EntityDistance(Tunit\h,p)<lowdist Then
									If ai_agressive(Dunit_behaviour(Tunit\typ))=1 Then
										tame=0
										If Tunit\states=1 Then
											If get_state(Cstate_tame,Cclass_unit,Tunit\id)=1 Then
												tame=1
											EndIf
										EndIf
										If tame=0 Then
											lowdist=EntityDistance(Tunit\h,p)
											PositionEntity p2,EntityX(Tunit\h),EntityY(Tunit\h)-(Dunit_colyr#(Tunit\typ)/2.0),EntityZ(Tunit\h)
											found=1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					Next
					If parse_comma(0) Then
						offset=param()
					EndIf
					If parse_comma(0) Then
						pr_wpn=Int(param())
						If parse_comma(0) Then
							pr_spd#=Float(param())
							If parse_comma(0) Then
								pr_dmg#=Float(param())
								If parse_comma(0) Then
									pr_drag#=Float(param())
								EndIf
							EndIf
						EndIf
					EndIf
					If found=1 Then
						PointEntity p,p2
						pro_add_script(typ,x,y,z,EntityPitch(p),EntityYaw(p),offset,pr_wpn,pr_spd#,pr_dmg#,pr_drag#)
					EndIf
			End Select
			FreeEntity p
			FreeEntity p2
		
		;Load Animation
		Case "loadani"
			id=param()
			parse_comma(1)
			f1=param()
			parse_comma(1)
			f2=param()
			p_return$=unit_loadani(id,f1,f2)
		
		;Animate
		Case "animate"
			id=param()
			parse_comma(1)
			f1=param()
			parse_comma(1)
			f2=param()
			parse_comma(1)
			fval#=param()
			mode=3
			If parse_comma(0) Then
				mode=param()
			EndIf
			p_return$=unit_scriptani(id,f1,f2,fval#,mode)
			
		;Viewline
		Case "viewline"
			x=param()									;X
			parse_comma(1)
			y=param()									;Y
			parse_comma(1)
			z=param()									;Z
			parse_comma
			x2=param()									;X 2
			parse_comma(1)
			y2=param()									;Y 2
			parse_comma(1)
			z2=param()									;Z 2
			p_return=viewline(x,y,z,x2,y2,z2)
		
		;Text3D
		Case "text3d"
			class$=param()									;Class
			classint=parse_getclass(class$)
			col=0
			y=0
			z=300
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma()
			txt$=param()									;Text
			If parse_comma(0) Then col=param()				;Color
			If parse_comma(0) Then y=param()				;Offset
			If parse_comma(0) Then z=param()				;Range
			If ha_type(classint,id)>0 Then
				found=0
				For t3d.t3d=Each t3d
					If t3d\class=classint Then
						If t3d\id=id Then
							If txt$<>"" Then
								t3d\txt$=txt$
								t3d\typ=col
								t3d\offset=y
								t3d\range=z
							Else
								Delete t3d
							EndIf
							found=1
							Exit
						EndIf
					EndIf
				Next
				If found=0 Then
					t3d.t3d=New t3d
					t3d\class=classint
					t3d\id=id
					t3d\txt$=txt$
					t3d\typ=col
					t3d\offset=y
					t3d\range=z
				EndIf
			EndIf
			
		;Lensflares
		Case "lensflares"
			set_flarefx=param()
			
		;Skycolor
		Case "skycolor"
			in_skyoverride(4)=0
			in_skyoverride(0)=param()
			If parse_comma(0) Then
				in_skyoverride(1)=param()		;R
				parse_comma()
				in_skyoverride(2)=param()		;G
				parse_comma()
				in_skyoverride(3)=param()		;B
				If parse_comma(0) Then
					in_skyoverride(4)=param()	;MIX
				EndIf
			EndIf
			e_environment_update_light()
		
		;Behaviour
		Case "behaviour"
			p_return$=""
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()
			Select classint
				Case Cclass_object
					If id>=0 And id<=Cobject_count Then
						p_return$=Dobject_behaviour(id)
					Else
						parse_error("typerange",id)
					EndIf
				Case Cclass_unit
					If id>=0 And id<=Cunit_count Then
						p_return$=Dunit_behaviour(id)
					Else
						parse_error("typerange",id)
					EndIf
				Case Cclass_item
					If id>=0 And id<=Citem_count Then
						p_return$=Ditem_behaviour(id)
					Else
						parse_error("typerange",id)
					EndIf
				Default parse_error("wrongclass",class$)
			End Select
			
		;Name
		Case "name"
			p_return$=""
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()
			Select classint
				Case Cclass_object
					If id>=0 And id<=Cobject_count Then
						p_return$=Dobject_name(id)
					Else
						parse_error("typerange",id)
					EndIf
				Case Cclass_unit
					If id>=0 And id<=Cunit_count Then
						p_return$=Dunit_name(id)
					Else
						parse_error("typerange",id)
					EndIf
				Case Cclass_item
					If id>=0 And id<=Citem_count Then
						p_return$=Ditem_name(id)
					Else
						parse_error("typerange",id)
					EndIf
				Default parse_error("wrongclass",class$)
			End Select
			
		;Make Dir
		Case "mkdir"
			class$=param()
			p_return$=0
			class$=Replace(class$,"/","\")
			If Right(class$,1)="\" Then class$=Left(class$,Len(class$)-1)
			CreateDir(class$)
			If FileType(class$)=2 Then
				p_return$=1
			EndIf
				
		;copychilds
		Case "copychilds","copychildren"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			cvars=1
			citems=1
			cstates=1
			cscript=0
			addtocache=0
			If parse_comma(0) Then cvars=param()
			If parse_comma(0) Then citems=param()
			If parse_comma(0) Then cstates=param()
			If parse_comma(0) Then cscript=param()
			If parse_comma(0) Then addtocache=param()
			ha_copychilds(classint,id,cvars,citems,cstates,cscript,addtocache)

		;pastechilds
		Case "pastechilds","pastechildren"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			cvars=1
			citems=1
			cstates=1
			cscript=0
			If parse_comma(0) Then cvars=param()
			If parse_comma(0) Then citems=param()
			If parse_comma(0) Then cstates=param()
			If parse_comma(0) Then cscript=param()
			ha_pastechilds(classint,id,cvars,citems,cstates,cscript)			
			
		;################################################# PLAYER
		
		;Player Speed
		Case "player_speed"
			fval#=param()
			Dunit_speed#(1)=fval#
			
		;Player Damage
		Case "player_damage"
			fval#=param()
			Dunit_damage#(1)=fval#				
			
		;Player Attackrange
		Case "player_attackrange"
			fval#=param()
			Dunit_attackrange(1)=fval#			
		
		;Maxweight
		Case "player_maxweight"
			fval#=param()
			Dunit_maxweight(1)=fval#
			
		;Mat
		Case "player_mat"
			Dunit_mat(1)=get_material(param())
			
		;Player Weapon
		Case "player_weapon"
			typ=param()
			game_weapon_bytype(typ)
		
		;Player Ammo
		Case "player_ammo"
			typ=param()
			game_ammo_bytype(typ)
		
		;Jumptime
		Case "jumptime"
			fval#=param()
			game_jumptime=Int(fval#)
			
		;Jumpfactpr
		Case "jumpfactor"
			fval#=param()
			game_jumpfac#=fval#
			
		;Player Spotted
		Case "playerspotted"
			p_return$=0
			For Tunit.Tunit=Each Tunit
				If ai_agressive(Dunit_behaviour(Tunit\typ))=1 Then
					If Tunit\ai_mode=ai_movetarget Or Tunit\ai_mode=ai_attack Then
						If Tunit\health>0. Then
							If EntityDistance(Tunit\h,cam)<300 Then
								p_return$=1
								Exit
							EndIf
						EndIf
					EndIf			
				EndIf
			Next
		
		;Get Player Value
		Case "getplayervalue"
			typ=param()
			If Handle(g_cplayer) Then
				Select typ
					Case 1 p_return$=g_cplayer\health#
					Case 2 p_return$=g_cplayer\hunger#
					Case 3 p_return$=g_cplayer\thirst#
					Case 4 p_return$=g_cplayer\exhaustion#
					Default parse_error("expect"," 1,2,3 or 4 as parameter for 'getplayervalue'")
				End Select
			EndIf
		
		;scantarget
		Case "scantarget"
			If parse_param() Then
				range=param()
				game_target(range)
			Else
				game_target()
			EndIf
				
		;targetx
		Case "targetx" p_return$=tmp_tarx#
		Case "targety" p_return$=tmp_tary#
		Case "targetz" p_return$=tmp_tarz#
		
		;targetclass
		Case "targetclass"
			p_return$=tmp_tarclass 
		
		;targetid
		Case "targetid"
			p_return$=tmp_tarid
			
		;targetdistance
		Case "targetdistance"
			p=CreatePivot()
			PositionEntity p,tmp_tarx#,tmp_tary#,tmp_tarz#
			p_return$=EntityDistance(cam,p)
			FreeEntity p
		
		;air
		Case "air"
			p=param()
			If g_dive<>0 Then
				g_airtimer=g_airtimer+p
				If g_airtimer>gt Then
					g_airtimer=gt
				EndIf
			EndIf
			
		;sleep
		Case "sleep"
			game_sleep()
			
		;find
		Case "find"
			typ=param()
			count=1
			If parse_comma(0) Then
				count=param()
			EndIf
			id=set_item(-1,typ,EntityX(cam),EntityY(cam),EntityZ(cam),count)
			store_item(id,Cclass_unit,1)
			p_add2d(set_scrx/2,set_scry/2,-2,typ)
			if_msg(ss$(sm$(180),Ditem_name$(typ),(count)),Cbmpf_green)
			sfx sfx_collect
		
		;################################################# BUFFER
		
		;Add - add buffer line
		Case "add"
			If pv_buffer$="" Then
				pv_buffer$=params()
			Else
				pv_buffer$=pv_buffer$+"Ś"+params()
			EndIf

		;Clear - free buffer
		Case "clear"
			pv_buffer$=""
			
		;Loadfile - load file to buffer
		Case "loadfile"
			filename$=params()								;Filename
			pv_buffer$=""
			pv_buffer$=parse_loadscript$(filename$)
			p_return$=pv_buffer$
			
		;Buffer
		Case "buffer"
			p_return$=pv_buffer$
		
		
		;################################################# Interface
		
		;CloseMenu
		Case "closemenu"
			If m_section<>Csection_menu Then
				m_menu=0
				in_pc_menu=0
			EndIf
		
		;Map
		Case "map"
			If m_section<>Csection_menu Then
				m_menu=Cmenu_if_map
			EndIf
		
		;Equip
		Case "equip"
			If parse_param() Then
				typ=param()
				If typ>0 And typ<=Citem_count Then
					game_useasweapon(typ)
				EndIf
			Else
				If m_menu=Cmenu_if_items Then if_quickslot()
			EndIf
			
		;Message
		Case "msg"
			msg$=params()											;P1 - Message
			col=0:time=3000
			If parse_comma(0) Then col=param()						;P2 - Color
			If parse_comma(0) Then time=param()						;P3 - Time
			If col<0 Then
				col=0
			ElseIf col>6 Then
				col=0
			EndIf
			if_msg(msg$,col,time)
			
		;Messagebox
		Case "msgbox"
			in_msgtitle$=params()									;Title
			If parse_comma(0) Then
				source$=param()										;Source
				txt$=0
				If Int(source$)<>0 Then
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					txt$=parse_loadscript$(source$)
				EndIf
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				Else
					in_msgtext$=txt$
					If m_section<>Csection_menu Then
						m_menu=Cmenu_if_msg
					EndIf
				EndIf
			Else
				in_msgtext$=pv_buffer$:pv_buffer$=""					;Use Buffer as Source
				If m_section<>Csection_menu Then
					m_menu=Cmenu_if_msg
				EndIf
			EndIf
			;Free Buttons
			For i=0 To 9
				in_sb(i)=0
			Next
			;Closeblock
			in_blockclose=ms
			
		;Message Extend
		Case "msg_extend"
			If parse_comma(0) Then
				source$=param()										;Source
				txt$=0
				If Int(source$)<>0 Then
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					txt$=parse_loadscript$(source$)
				EndIf
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				Else
					in_msgtext$=in_msgtext$+"Ś"+txt$
				EndIf
			Else
				in_msgtext$=in_msgtext$+"Ś"+pv_buffer$:pv_buffer$=""	;Use Buffer as Source
			EndIf
			
		;Message Replace
		Case "msg_replace"
			txt$=param()											;From
			parse_comma()
			title$=param()											;To
			in_msgtext$=Replace(in_msgtext$,txt$,title$)
		
			
		;Message Win
		Case "msgwin"
			col=0
			title$=params()												;Title
			If parse_comma(0) Then col=param()								;Color
			If parse_comma(0) Then image$=params()							;Image
			gui_msg(title$,col,image$)
			
		;Image Win
		Case "imagewin"
			image$=params()												;Image
			gui_win_image(image$)
			
		;Decision Win
		Case "decisionwin"
			;con_add("DECISIONWIN",Cbmpf_green)
			col=0
			cancel$=""
			okay$=""
			title$=params()												;Title
			;con_add("TITLE: "+title$)
			If parse_comma(0) Then col=param()								;Color
			If parse_comma(0) Then cancel$=params()							;Cancle Text
			If parse_comma(0) Then okay$=params()							;Okay Text
			If parse_comma(0) Then image$=params()							;Image
			p_return$=gui_msgdecide(title$,col,cancel$,okay$,image$)
			;con_add("RETURN: "+p_return$)
			
		;Input Win
		Case "inputwin"
			;con_add("DECISIONWIN",Cbmpf_green)
			col=0
			cancel$=""
			okay$=""
			title$=params()												;Title
			;con_add("TITLE: "+title$)
			If parse_comma(0) Then col=param()								;Color
			If parse_comma(0) Then cancel$=params()							;Cancle Text
			If parse_comma(0) Then okay$=params()							;Okay Text
			If parse_comma(0) Then image$=params()							;Image
			p_return$=gui_msginput(title$,col,cancel$,okay$,image$)
			;con_add("RETURN: "+p_return$)			
			
		;Button
		Case "button"
			id=param()													;ID
			frame$=Cicon_ok
			If id>=0 And id<10 Then
				parse_comma(1)
				title$=params()												;Text
				If parse_comma(0) Then
					frame$=param()											;Button Frame
				EndIf
				If parse_comma(0) Then
					source$=param()											;Source
					txt$=0
					If Int(source$)<>0 Then
						;Info Script Source
						For Tx.Tx=Each Tx
							If Tx\parent_class=Cclass_info Then
								If Tx\parent_id=Int(source$) Then
									txt$=Tx\value$
									Exit
								EndIf
							EndIf
						Next
					Else
						;File Source
						;con_add("Loading Script Source...")
						txt$=parse_loadscript$(source$)
					EndIf
					;Error
					If txt$=0 Or txt$="" Then
						parse_error("txtsource",source$)
					;Assign
					Else
						in_sb_scr$(id)=txt$
					EndIf
				Else
					;Buffer Source
					in_sb_scr$(id)=pv_buffer$:pv_buffer$=""					;Use Buffer as Source
				EndIf
				;Setup Button
				in_sb(id)=1
				in_sb_icon(id)=frame
				in_sb_txt$(id)=title$
				in_sb_handle(id)=0
				If Len(frame$)>2 Then
					in_sb_handle(id)=load_res(frame$,Cres_image,1)
					If in_sb_handle(id)=0 Then
						in_sb_icon(id)=Cicon_ok
					Else
						in_sb_icon(id)=-1
					EndIf
				EndIf
			Else
				parse_error("Button ID between 0 and 9 expected!")
			EndIf
					
		;Exchange
		Case "exchange"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If parent_con(classint,id) Then
				typ=1
				If parse_comma(0) Then typ=param()			;Allowstore
				class$=""
				While parse_comma(0)>0
					If class$="" Then class$="|"
					class$=class$+param()+"|"
				Wend
				if_exchange(classint,id,typ,class$)
				;Closeblock
				in_blockclose=ms
			Else
				If set_debug=1 Then con_add("ERROR: exchange - invalid class/id",Cbmpf_red)
			EndIf
			
		;SelectPlace
		Case "selectplace"
			pc_sp_txt$=params()											;Title
			pc_sp_height#=100
			If parse_comma(0) Then
				pc_sp_height#=param()
			EndIf
			pc_sp_x#=0
			pc_sp_y#=0
			pc_sp_z#=0
			pc_sp_class=p_env_class
			pc_sp_id=p_env_id
			If m_section<>Csection_menu Then
				m_menu=Cmenu_if_selplace
			EndIf
			;Closeblock
			in_blockclose=ms
			
		;Selectplace Stuff
		Case "selectplace_x"
			p_return$=pc_sp_x#
			
		;Selectplace Stuff
		Case "selectplace_y"
			p_return$=pc_sp_y#
			
		;Selectplace Stuff
		Case "selectplace_z"
			p_return$=pc_sp_z#
			
		;Process
		Case "process"
			in_pc_custom$=params()										;Title
			If parse_comma(0) Then
				in_pc_customt=param()									;Time
			Else
				in_pc_customt=5000
			EndIf
			If parse_comma(0) Then										;Event
				in_pc_event$=params()
			Else
				in_pc_event$=""
			EndIf
			;Start Custom Process
			pc_typ=Cstate_pc_custom
			pc_gt=gt
			in_focus=pc_typ
			in_pc_menu=0
			If m_section<>Csection_menu Then
				If m_menu<>0 Then
					in_pc_menu=m_menu
					in_pc_menu_x=in_mx
					in_pc_menu_y=in_my
					m_menu=0
				EndIf
			EndIf
			
		;Crack Lock
		Case "cracklock"
			FlushKeys()
			pl_cl_title$=params()										;Title
			parse_comma()
			pc_cl_mode=param()											;Mode
			parse_comma()
			pc_cl_code$=params()										;Code
			pc_cl_class=p_env_class
			pc_cl_id=p_env_id
			pc_cl_pos=1
			If m_section<>Csection_menu Then
				m_menu=Cmenu_if_cracklock
			EndIf
			;Closeblock
			in_blockclose=ms
			
		;interface txt
		Case "text"
			id=param()													;ID
			If id>=0 And id<20 Then
				;Setup interface text
				in_sitxt(id)=1
				parse_comma(1)
				in_sitxt_txt$(id)=param()								;Text
				in_sitxt_font(id)=0
				in_sitxt_align(id)=-1
				in_sitxt_x(id)=0
				in_sitxt_y(id)=0
				If parse_comma(0) Then
					in_sitxt_font(id)=param()							;Font
				EndIf
				If parse_comma(0) Then
					in_sitxt_x(id)=param()								;X
					parse_comma(1)
					in_sitxt_y(id)=param()								;Y
					in_sitxt_align(id)=1
					If parse_comma(0) Then
						in_sitxt_align(id)=param()						;Ausrichtung
					EndIf
				EndIf
			Else
				parse_error("Interface Text ID between 0 and 19 expected!")
			EndIf
			
		;interface image
		Case "image"
			id=param()													;ID
			If id>=0 And id<39 Then
				;Setup interface image
				If in_siimg(id)<>0 Then
					FreeImage in_siimg(id)
					in_siimg(id)=0
				EndIf
				parse_comma(1)
				path$=param()											;Image
				in_siimg_mask(id)=1
				If path$<>"" Then
					in_siimg(id)=load_image(path$)
					parse_comma(1)										;X
					in_siimg_x(id)=param()
					parse_comma(1)										;Y
					in_siimg_y(id)=param()
					If parse_comma(0) Then
						in_siimg_mask(id)=param()						;Mask
					EndIf
				EndIf
			Else
				parse_error("Interface Image ID between 0 and 39 expected!")
			EndIf
			
		;Dialogue
		Case "dialogue"
			in_msgtitle$="Dialogue"
			in_dlg_page$=param()										;Page
			parse_comma(1)
			source$=param()												;Source
			txt$=0
			If Int(source$)<>0 Then
				For Tx.Tx=Each Tx
					If Tx\parent_class=Cclass_info Then
						If Tx\parent_id=Int(source$) Then
							txt$=Tx\value$
							Exit
						EndIf
					EndIf
				Next
			Else
				txt$=parse_loadscript$(source$)
			EndIf
			If txt$=0 Or txt$="" Then
				parse_error("txtsource",source$)
			Else
				in_dlg$=txt$
				If m_menu=Cmenu_if_buildsetup Then
					FreeEntity in_buildsetup_dummy
				EndIf
				If m_section<>Csection_menu Then
					m_menu=Cmenu_if_dlg
				EndIf
				in_scr_scr=0
				;Parse
				parse_dialogue(in_dlg_page)
				;Closeblock
				in_blockclose=ms
			EndIf
			
		;Menu
		Case "menu"
			p_return$=m_menu
		
		
		;Clickscreen
		Case "cscr"
			If in_clickscreenimage<>0 Then
				FreeImage in_clickscreenimage
				in_clickscreenimage=0
			EndIf
			For Tcscr.Tcscr=Each Tcscr
				If Tcscr\image<>0 Then FreeImage Tcscr\image
				Delete Tcscr
			Next
			in_clickscreenx=1
			image$=""
			If parse_param() Then
				image$=params()										;Image
				If parse_comma(0) Then
					in_clickscreenx=param()							;Close
				EndIf
			EndIf
			If image$<>"" Then
				in_clickscreenimage=load_image(image$)
			EndIf
			m_menu=Cmenu_if_clickscreen
		
		;Clickscreen Text
		Case "cscr_text"
			Tcscr.Tcscr=New Tcscr
			Tcscr\typ=1
			Tcscr\txt$=param()										;Text
			parse_comma(1)											;X
			Tcscr\x=param()
			parse_comma(1)											;Y
			Tcscr\y=param()
			Tcscr\col=-1
			If parse_comma(0) Then									;Col
				Tcscr\col=param()
			EndIf
			If parse_comma(0) Then
				Tcscr\align=param()									;Alignment
			EndIf
			If parse_comma(0) Then
				Tcscr\tooltip$=param()								;Tooltip
			EndIf
			If parse_comma(0) Then									;Event
				source$=param()
				txt$=0
				If Int(source$)<>0 Then
				;Info Script Source
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					;File Source
					txt$=parse_loadscript$(source$)
				EndIf
				;Error
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				;Assign
				Else
					Tcscr\event$=txt$
				EndIf
			Else
				;Buffer Source
				Tcscr\event$=pv_buffer$:pv_buffer$=""					;Use Buffer as Source
			EndIf
		
		;Clickscreen Image
		Case "cscr_image"
			Tcscr.Tcscr=New Tcscr
			Tcscr\typ=2
			path$=param()											;Image
			Tcscr\image=load_image(path$)
			parse_comma(1)											;X
			Tcscr\x=param()
			parse_comma(1)											;Y
			Tcscr\y=param()
			If parse_comma(0) Then
				Tcscr\tooltip$=param()								;Tooltip
			EndIf
			If parse_comma(0) Then									;Event
				source$=param()
				txt$=0
				If Int(source$)<>0 Then
				;Info Script Source
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					;File Source
					txt$=parse_loadscript$(source$)
				EndIf
				;Error
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				;Assign
				Else
					Tcscr\event$=txt$
				EndIf
			Else
				;Buffer Source
				Tcscr\event$=pv_buffer$:pv_buffer$=""					;Use Buffer as Source
			EndIf
		
		;Savemapimage
		Case "savemapimage"
			path$=param()
			classint=256
			If parse_comma(0) Then
				classint=param()
			EndIf
			If path$<>0 Then
				genmap(classint)
				SaveImage map_mapimage,path$
				If classint<>256 Then genmap(256)
			EndIf
			
		;lockcombi
		Case "lockcombi"
			combistring$=params()
			classint=0
			For Tcom.Tcom=Each Tcom
				If Tcom\mode=2 Then
					If Tcom\aid$=combistring$ Then
						classint=1
						Exit
					EndIf
				EndIf
			Next
			If classint=1 Then
				For Tx.Tx=Each Tx
					If Tx\mode=50 And Tx\key$=combistring Then Delete Tx
				Next
				Tx.Tx=New Tx
				Tx\mode=50
				Tx\key$=combistring$
			EndIf
		
		;lockcombis - lock all combinations
		Case "lockcombis"
			For Tx.Tx=Each Tx
				If Tx\mode=50 Then Delete Tx
			Next
			For Tcom.Tcom=Each Tcom
				If Tcom\mode=2 Then
					Tx.Tx=New Tx
					Tx\mode=50
					Tx\key$=Tcom\aid$
				EndIf
			Next
			
		;unlockcombi
		Case "unlockcombi"
			combistring$=params()
			For Tx.Tx=Each Tx
				If Tx\mode=50 And Tx\key$=combistring Then Delete Tx
			Next
		
		;lockcombis - lock all combinations
		Case "unlockcombis"
			For Tx.Tx=Each Tx
				If Tx\mode=50 Then Delete Tx
			Next

		
				
		;################################################# Diary
		
		;Diary
		Case "diary"
			title$=params()												;Entry Title
			If parse_comma(0) Then
				source$=params()										;Source
				txt$=0
				If Int(source$)<>0 Then
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					txt$=parse_loadscript$(source$)
				EndIf
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				Else
					Tx.Tx=New Tx
					Tx\mode=2
					Tx\key$=title$
					Tx\value$=txt$
				EndIf
			Else				
				Tx.Tx=New Tx											;Use Buffer as Source
				Tx\mode=2
				Tx\key$=title$
				Tx\value$=pv_buffer$
				pv_buffer$=""
			EndIf
			;Set Menu
			If m_menu<Cmenu_if_movie Then
				If m_section<>Csection_menu Then
					m_menu=Cmenu_if_diary
				EndIf
				if_centerpos()
				in_scr_scr=0
				in_scr_scr2=0
				in_opt(0)=0
				i=0
				For Tx.Tx=Each Tx
					If Tx\mode=2 Then
						i=i+1
						If Tx\key$=title$ Then
							in_opt(0)=i
							If i>4 Then in_scr_scr=i-4
							Exit
						EndIf
					EndIf
				Next
			EndIf
			;FX
			sfx sfx_diary
			if_msg(sm$(182),Cbmpf_yellow)
			;Closeblock
			in_blockclose=ms
			
			
		;Free Entry
		Case "freeentry"
			If parse_param() Then
				entry$=params()											;Entry Title
				For Tx.Tx=Each Tx
					If Tx\mode=2 Then 
						If Tx\key$=entry$ Then
							Delete Tx
							Exit
						EndIf
					EndIf
				Next
			Else
				For Tx.Tx=Each Tx
					If Tx\mode=2 Then 
						Delete Tx
					EndIf
				Next				
			EndIf
		
		;Modify Entry
		Case "modifyentry"
			entry$=params()												;Entry Title
			If parse_comma(0) Then
				source$=params()										;Source
				txt$=0
				If Int(source$)<>0 Then
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					txt$=parse_loadscript$(source$)
				EndIf
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				Else
					For Tx.Tx=Each Tx
						If Tx\mode=2 Then 
							If Tx\key$=entry$ Then
								Tx\value$=txt$
								Exit
							EndIf
						EndIf
					Next
				EndIf
			Else				
				For Tx.Tx=Each Tx										;Use Buffer as Source
					If Tx\mode=2 Then 
						If Tx\key$=entry$ Then
							Tx\value$=pv_buffer$
							pv_buffer$=""
							Exit
						EndIf
					EndIf
				Next
			EndIf
			
		;Modify Entry Line
		Case "modifyentryline"
			entry$=params()												;Entry Title
			parse_comma(1)
			entryl=param()												;Line
			parse_comma(1)
			newline$=params()											;New Line
			For Tx.Tx=Each Tx
				If Tx\mode=2 Then 
					If Tx\key$=entry$ Then
						c=cinstr(Tx\value$,"Ś")
						split$(Tx\value$,"Ś",c)
						splits$(entryl)=newline$
						Tx\value$=""
						For i=0 To c
							Tx\value$=Tx\value$+splits$(i)+"Ś"
						Next
						Exit
					EndIf
				EndIf
			Next
			
		;Extend Entry
		Case "extendentry"
			entry$=params()												;Entry Title
			If parse_comma(0) Then
				source$=params()										;Source
				txt$=0
				If Int(source$)<>0 Then
					For Tx.Tx=Each Tx
						If Tx\parent_class=Cclass_info Then
							If Tx\parent_id=Int(source$) Then
								txt$=Tx\value$
								Exit
							EndIf
						EndIf
					Next
				Else
					txt$=parse_loadscript$(source$)
				EndIf
				If txt$=0 Or txt$="" Then
					parse_error("txtsource",source$)
				Else
					For Tx.Tx=Each Tx
						If Tx\mode=2 Then 
							If Tx\key$=entry$ Then
								Tx\value$=Tx\value$+"Ś"+txt$
								Exit
							EndIf
						EndIf
					Next
				EndIf
			Else				
				For Tx.Tx=Each Tx										;Use Buffer as Source
					If Tx\mode=2 Then 
						If Tx\key$=entry$ Then
							Tx\value$=Tx\value$+"Ś"+pv_buffer$
							pv_buffer$=""
							Exit
						EndIf
					EndIf
				Next
			EndIf
			
		;Show Entiry
		Case "showentry"
			entry$=params()												;Entry Title
			;Set Menu
			If m_menu<>Cmenu_if_movie Then
				If m_section<>Csection_menu Then
					m_menu=Cmenu_if_diary
				EndIf
				if_centerpos()
			EndIf
			in_scr_scr=0
			in_scr_scr2=0
			in_opt(0)=0
			i=0
			For Tx.Tx=Each Tx
				If Tx\mode=2 Then
					i=i+1
					If Tx\key$=entry$ Then
						in_opt(0)=i
						If i>4 Then in_scr_scr=i-4
						Exit
					EndIf
				EndIf
			Next
			;FX
			sfx=1
			If parse_comma(0) Then
				sfx=param()												;SFX
			EndIf
			If sfx Then
				sfx sfx_diary
			EndIf
			;Closeblock
			in_blockclose=ms
			
		;setday
		Case "setday"
			map_day=Abs(Int(param()))
			
		;sethour
		Case "sethour"
			map_hour=Abs(Int(param()))
			If map_hour>23 Then map_hour=0
			e_environment_update_light()
			
		;setminute
		Case "setminute"
			map_minute=Abs(Int(param()))
			If map_minute>59 Then map_minute=0
			e_environment_update_light()
			
		;################################################# MAP INDICATORS
		
		;hideindicator
		Case "hideindicator"
			id=param()													;ID
			If con_info(id) Then
				If TCinfo\typ=36 Then
					TCinfo\ints[1]=0
					info_vts()
				Else
					parse_error("infotype",36)
				EndIf
			Else
				parse_error("object",id)
			EndIf
		
		;showindicator
		Case "showindicator"
			id=param()													;ID
			If con_info(id) Then
				If TCinfo\typ=36 Then
					TCinfo\ints[1]=1
					info_vts()
				Else
					parse_error("infotype",36)
				EndIf
			Else
				parse_error("object",id)
			EndIf
		
		;hideindicators
		Case "hideindicators"
			For TCinfo.Tinfo=Each Tinfo
				If TCinfo\typ=36 Then
					TCinfo\ints[1]=0
					info_vts()
				EndIf
			Next
		
		;showindicators
		Case "showindicators"
			For TCinfo.Tinfo=Each Tinfo
				If TCinfo\typ=36 Then
					TCinfo\ints[1]=1
					info_vts()
				EndIf
			Next
			
		;setindicatorlook
		Case "setindicatorlook"
			id=param()													;ID
			parse_comma(1)
			frame=param()												;Frame
			If con_info(id) Then
				If TCinfo\typ=36 Then
					Select frame
						Case 0,1 TCinfo\ints[0]=0
						Case 2 TCinfo\ints[0]=9
						Case 3 TCinfo\ints[0]=10
						Case 4 TCinfo\ints[0]=11
					End Select
					info_vts()
				Else
					parse_error("infotype",36)
				EndIf
			Else
				parse_error("object",id)
			EndIf
			
		;setindicatorinfo
		Case "setindicatorinfo"
			id=param()													;ID
			parse_comma(1)
			infotxt$=params()											;Info
			If con_info(id) Then
				If TCinfo\typ=36 Then
					TCinfo\strings[0]=infotxt$
					info_vts()
				Else
					parse_error("infotype",36)
				EndIf
			Else
				parse_error("object",id)
			EndIf
		
			
		
		;################################################# INFOS
		
		;stoptrigger
		Case "stoptrigger"
			idstring$=param()											;ID
			If idstring$="self" Then
				id=p_env_id
			Else
				id=Int(idstring$)
			EndIf
			If con_info(id) Then
				TCinfo\floats[1]=0
				info_vts()
			Else
				parse_error("object",id)
			EndIf
			
		;starttrigger
		Case "starttrigger"
			idstring$=param()											;ID
			If idstring$="self" Then
				id=p_env_id
			Else
				id=Int(idstring$)
			EndIf
			If con_info(id) Then
				TCinfo\floats[1]=1
				info_vts()
			Else
				parse_error("object",id)
			EndIf
		
		;stoptriggers
		Case "stoptriggers"
			For TCinfo.Tinfo=Each Tinfo
				If TCinfo\typ>=10 And TCinfo\typ<30 Then
					TCinfo\floats[1]=0
					info_vts()
				EndIf
			Next
		
		;starttriggers
		Case "starttriggers"
			For TCinfo.Tinfo=Each Tinfo
				If TCinfo\typ>=10 And TCinfo\typ<30 Then
					TCinfo\floats[1]=1
					info_vts()
				EndIf
			Next
			
		;trigger
		Case "trigger"
			id=param()
			If con_info(id) Then
				info_spawncontrol(TCinfo,1)
				set_parsecache(Cclass_unit,id,"trigger","triggered by trigger command")
			Else
				parse_error("object",id)
			EndIf
			
		;info_sprite
		Case "info_sprite"
			id=0
			file$=""
			x#=1.
			y#=1.
			r=255
			g=255
			b=255
			alpha#=1.0
			mode=0
			fix=0
			id=param()						;ID
			If parse_comma(0) Then
				file$=param()					;File
				If parse_comma(0) Then
					x#=param()						;X
					parse_comma(1)
					y#=param()						;y
					If parse_comma(0) Then
						r=param()						;R
						parse_comma(1)
						g=param()						;G
						parse_comma(1)
						b=param()						;B
						If parse_comma(0) Then
							alpha#=param()					;Alpha
							If parse_comma(0) Then
								mode=param()					;Blendmode
								If parse_comma(0) Then
									fix=param()					;Fix
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=id Then
					If Tinfo\typ=5 Then
						If file$<>"self" Then Tinfo\strings[0]=file$
						Tinfo\floats[0]=x#
						Tinfo\floats[1]=y#
						Tinfo\ints[0]=r
						Tinfo\ints[1]=g
						Tinfo\ints[2]=b
						Tinfo\floats[2]=alpha#
						Tinfo\strings[1]=mode
						Tinfo\strings[2]=fix
						info_vts()
						info_setupsprite(Tinfo)
					Else
						parse_error("'info_sprite' expects a sprite info")
					EndIf
				EndIf
			Next
			
		;info_sprite
		Case "info_loudspeaker"
			x#=-1.0
			id=param()						;ID
			parse_comma(1)
			file$=param()					;File
			If parse_comma(0) Then
				x#=param()
			EndIf
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=id Then
					If Tinfo\typ=47 Then
						If file$<>"self" Then Tinfo\strings[0]=file$
						If x#<>-1.0 Then
							Tinfo\floats[0]=x#
						EndIf	
						info_vts()
						info_setuploudspeaker(Tinfo)
					Else
						parse_error("'info_loudspeaker' expects a loudspeaker info")
					EndIf
				EndIf
			Next
		
		;info_spawncontrol
		Case "info_spawncontrol"
			id=param()						;ID
			parse_comma(1)
			x#=param()						;Radius
			parse_comma(1)
			class=param()					;Class
			class=parse_getclass(class$,0)
			parse_comma(1)					
			typ=param()						;Typ
			parse_comma(1)
			part=param()					;Part
			parse_comma(1)
			max=param()						;Max
			parse_comma(1)
			days=param()					;Days
			For Tinfo.Tinfo=Each Tinfo
				If Tinfo\id=id Then
					If Tinfo\typ=45 Then
						Tinfo\floats[0]=x#
						Tinfo\ints[0]=class
						Tinfo\ints[1]=typ
						Tinfo\strings[0]=part
						Tinfo\ints[2]=max
						Tinfo\strings[1]=days
						info_vts()
					Else
						parse_error("'info_spawncontrol' expects a spawncontrol info")
					EndIf
				EndIf
			Next
			
		
		;################################################# BUILDINGS
		
		;lockbuilding
		Case "lockbuilding"
			lockbuildingid=param()
			For Tx.Tx=Each Tx
				If Tx\mode=3 And Tx\key$=lockbuildingid Then Delete Tx
			Next
			Tx.Tx=New Tx
			Tx\mode=3
			Tx\key$=lockbuildingid
		
		;lockbuildings - lock all buildings
		Case "lockbuildings"
			For Tx.Tx=Each Tx
				If Tx\mode=3 Then Delete Tx
			Next
			For Tbui.Tbui=Each Tbui
				If Tbui\mode=2 Then
					Tx.Tx=New Tx
					Tx\mode=3
					Tx\key$=Tbui\id
				EndIf
			Next
		
		;unlockbuilding
		Case "unlockbuilding"
			lockbuildingid=param()
			For Tx.Tx=Each Tx
				If Tx\mode=3 And Tx\key$=lockbuildingid Then Delete Tx
			Next
			
		;unlockbuildings
		Case "unlockbuildings"
			For Tx.Tx=Each Tx
				If Tx\mode=3 Then Delete Tx
			Next
			
		;builtat
		Case "builtat"
			p_return$=0
			idstring$=param()
			If idstring$="self" Then
				id=p_env_id
			Else
				id=Int(idstring$)
			EndIf
			For Tstate.Tstate=Each Tstate
				If Tstate\typ=Cstate_link Then
					If Tstate\value=id Then
						p_return$=Tstate\parent_id
						Exit
					EndIf
				EndIf
			Next
			
		;locked
		Case "locked"
			p_return$=0
			lockid$=param()
			If Int(lockid)>0 Then
				For Tx.Tx=Each Tx
					If Tx\mode=3 And Int(Tx\key$)=Int(lockid) Then
						p_return$=1
						Exit
					EndIf
				Next
			Else
				For Tx.Tx=Each Tx
					If Tx\mode=50 And Tx\key$=lockid Then
						p_return$=1
						Exit
					EndIf
				Next				
			EndIf
			
		;Lastbuildingsite
		Case "lastbuildingsite"
			p_return$=pv_lastbuilding
			
		;Buildsetuo
		Case "buildsetup"
			lockid$=param()				;Building ID
			If parse_comma(0) Then
				tmp_y#=param()
			Else
				tmp_y#=0
			EndIf
			game_buildsetup_start(Int(lockid$),tmp_y#)
			
		;################################################# SEQUENCE
		
		;seqstart
		Case "seqstart"
			seq_start()
			
		;seqtimemode
		Case "seqtimemode"
			seq_tmod=param()									;Time Mode
			If seq_tmod=0 Then seq_tmod=1
			seq_tmod=Abs(seq_tmod)
			seq_tabs=1
			If parse_comma(0) Then
				seq_tabs=param()								;Absolute
			EndIf
			
		;Seq Events - Basic
		Case "seqend" seq_addevent("end")
		
		;Seq Events - Messages
		Case "seqmsg" seq_addevent("msg")
		Case "seqmsgclear" seq_addevent("msgclear")
		
		;Seq Events - FX
		Case "seqsound" seq_addevent("sound")
		Case "seqbar" seq_addevent("bar")
		Case "hidbar" seq_addevent("hidebar")
		Case "showbar" seq_addevent("showbar")
		Case "seqflash" seq_addevent("flash")
		Case "seqfade"	seq_addevent("fade")
		Case "seqcls"	seq_addevent("cls")
		Case "seqimage"	seq_addevent("image")
		Case "seqimagetext"	seq_addevent("itxt")	
		
		;Seq Events - Stuff
		Case "seqevent" seq_addevent("event")
		Case "seqscript" seq_addevent("script")
		Case "seqhideplayer" seq_addevent("hideplayer")
		
		;Seq Events - Camera
		Case "setcam","sc" seq_addevent("setcam") 
		Case "movecam","mc" seq_addevent("movecam")
		Case "campath" seq_addevent("campath")
		Case "timedcampath" seq_addevent("timedcampath")
		Case "cammode" seq_addevent("cammode")
		Case "camfollow" seq_addevent("camfollow")


		;################################################# EFFECTS

		;Flash
		Case "flash"
			r=255
			g=255
			b=255
			flashspeed#=0.05
			flasha#=1.1
			r=param():parse_comma()							;R
			g=param():parse_comma()							;G
			b=param()										;B
			If parse_comma(0) Then flashspeed#=param()		;Fadespeed
			If parse_comma(0) Then flasha#=param()			;Alpha
			p_add(0,0,0,Cp_flash,flashspeed#,flasha#)
			;DebugLog "Flash "+flashspeed#+" / "+flasha#
			EntityColor TCp\h,r,g,b
			
		;Corona
		Case "corona"
			x#=param()										;X
			parse_comma()
			z#=param()										;Z
			y#=e_tery(x#,z#)
			size#=20.
			speed#=1.
			id=0
			If parse_comma(0) Then
				size#=param()								;Size
			EndIf
			If parse_comma(0) Then
				r=param()									;R
				parse_comma()
				g=param()									;G
				parse_comma()
				b=param()									;B
				If parse_comma(0) Then						;Speed
					speed#=param()
				EndIf
				If parse_comma(0) Then						;Stick to Unit
					idstring$=param()						;ID
					If idstring$="self" Then
						id=p_env_id
					Else
						id=Int(idstring$)
					EndIf
				EndIf
				For i=1 To 10+(set_effects*10)
					p_add(x#+Rnd(-size#,size#),y#+Rnd(-5.,5.),z#+Rnd(-size#,size#),Cp_spawn,Rnd(3,6),Rnd(0.5,2))
					EntityColor TCp\h,r,g,b
					TCp\fx#=speed#
					If id>0 Then p_parent(id,TCp)
				Next
			Else
				For i=1 To 10+(set_effects*10)
					p_add(x#+Rnd(-size#,size#),y#+Rnd(-5.,5.),z#+Rnd(-size#,size#),Cp_spawn,Rnd(3,6),Rnd(0.5,2))
					TCp\fx#=speed#
					If id>0 Then p_parent(id,TCp)
				Next
			EndIf
			
		;Thunder
		Case "thunder"
			sfx sfx_thunder(Rand(0,2))
			p_add(0,0,0,Cp_flash,Rnd(0.1,0.3),Rnd(1))
			EntityColor TCp\h,255,255,255
			EntityBlend TCp\h,3
		
		;Explosion	
		Case "explosion","explode"
			x#=param()											;X
			parse_comma(1)
			y#=param()											;Y
			parse_comma(1)
			z#=param()											;Z
			range=50
			If parse_comma(0) Then
				range=param()										;Range
			EndIf
			damage#=50
			If parse_comma(0) Then
				damage#=param()										;Damage
			EndIf
			exstyle=1
			If parse_comma(0) Then
				exstyle=param()										;Style
			EndIf
			game_explosion(x#,y#,z#,range,damage#,exstyle)
			
		;Blur
		Case "blur"
			in_scriptblur#=Float(param())						;Set Blur
			If in_scriptblur#<0. Then
				in_scriptblur#=0.
			ElseIf in_scriptblur#>Cpeakblur# Then
				in_scriptblur=Cpeakblur#
			EndIf
			
		;Vomit
		Case "vomit"
			unitidstr$=param()									;Unit
			If unitidstr$="self" Then
				unitid=p_env_id
			Else
				unitid=Int(unitidstr$)
			EndIf
			If con_unit(unitid) Then
				For i=1 To 15
					p_add(EntityX(TCunit\h)+Rnd(-5,5),EntityY(TCunit\h)+Rnd(-5,5)+Dunit_eyes#(TCunit\typ),EntityZ(TCunit\h)+Rnd(-5,5),Cp_subsplatter,Rnd(4,5),Rnd(0.9,3.))
					TCp\frame=2
					TCp\r#=Rnd(50,150)
					TCp\g#=TCp\r#+Rnd(0,55)
					TCp\b#=0
					EntityColor TCp\h,TCp\r#,TCp\g#,TCp\b#
				Next
			EndIf
			
		;Particle
		Case "particle"
			size#=1.
			alpha#=1.
			x#=param()										;X
			parse_comma()
			y#=param()										;Y
			parse_comma()
			z#=param()										;Z
			parse_comma()
			typ=param()										;Typ
			If parse_comma(0) Then
				size#=param()									;Size
				If parse_comma(0) Then
					alpha#=param()									;Alpha
					p_add(x#,y#,z#,typ,size#,alpha#)
				EndIf
			EndIf
			
		;Particle Color
		Case "particlec"
			r=param()										;Red
			parse_comma(1)
			g=param()										;Green
			parse_comma(1)
			b=param()										;Blue
			If TCp<>Null Then
				If TCp\h<>0 Then
					EntityColor TCp\h,r,g,b
				EndIf
			EndIf
			

		;################################################# SOUNDS
		
		;Play
		Case "play"
			file$=""
			vol#=1.
			pan#=0.
			pitch=0
			file$=params()									;File
			If parse_comma(0) Then vol#=param()				;Volume
			If parse_comma(0) Then pan#=param()				;Pan
			If parse_comma(0) Then pitch=param()			;Pitch
			sfx_play(file$,vol#,pan#,pitch)
			
		;Stopsounds
		Case "stopsounds"
			stopsounds()
			
		;Music
		Case "music"
			file$=""
			vol#=1.
			fade=0
			file$=params()									;File
			If parse_comma(0) Then vol#=param()				;Volume
			sfx_music(file$,vol#)
			If parse_comma(0) Then							;Fade
				fade=param()
				mfx_fade=1
				mfx_fadestart=gt
				mfx_fadeend=Abs(fade)
			EndIf
			
		;Stopmusic
		Case "stopmusic"
			If mfx_file<>0 Then FreeSound(mfx_file)
			mfx_file=0
			mfx_chan=0
			
		;Fademusic
		Case "fademusic"
			fade=param()
			mfx_fade=-1
			mfx_fadestart=gt
			mfx_fadeend=Abs(fade)
			
		;Musicvolume
		Case "musicvolume"
			mfx_fade=0
			mfx_peakvol#=param()
			
		;Ambientsfx
		Case "ambientsfx"
			If amb_chan<>0 Then StopChannel amb_chan
			If amb_file<>0 Then FreeSound amb_file
			map_music$=param()
			If map_music$<>"" Then
				amb_file=LoadSound("sfx\"+map_music$)
			EndIf
			
			
		;################################################# MODEL
		
		;Color
		Case "color"
			r=param()										;Red
			parse_comma(1)
			g=param()										;Green
			parse_comma(1)
			b=param()										;Blue
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				h=parent_h(p_env_class,p_env_id,1)
			EndIf
			If h<>0 Then
				EntityColor h,r,g,b
			Else
				parse_error("model","color")
			EndIf
			
		;Blend
		Case "blend"
			mode=param()									;Blendmode
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				h=parent_h(p_env_class,p_env_id,1)
			EndIf
			If h<>0 Then
				EntityBlend h,mode
			Else
				parse_error("model","blend")
			EndIf
			
		;Alpha
		Case "alpha"
			alpha#=param()									;Alpha
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				h=parent_h(p_env_class,p_env_id,1)
			EndIf
			If h<>0 Then
				EntityAlpha h,alpha#
				If p_env_class=Cclass_object Then
					alpha_object(p_env_id,alpha#)
				EndIf
			Else
				parse_error("model","alpha")
			EndIf
			
		;Scale
		Case "scale"
			sx#=param()										;Scale X
			parse_comma(1)
			sy#=param()										;Scale Y
			parse_comma(1)
			sz#=param()										;Scale Z
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				classint=p_env_class
				id=p_env_id
				h=parent_h(classint,id,1)
			EndIf
			If h<>0 Then
				Select classint
					;Object
					Case Cclass_object
						For Tobject.Tobject=Each Tobject
							If Tobject\id=id Then
								sx#=sx#*Dobject_size#(Tobject\typ,0)
								sy#=sy#*Dobject_size#(Tobject\typ,1)
								sz#=sz#*Dobject_size#(Tobject\typ,2)
								;Scale Collisionmodel
								If Tobject\ch<>0 Then
									ScaleEntity Tobject\ch,sx#,sy#,sz#,1
								EndIf
								Exit
							EndIf
						Next
					;Unit
					Case Cclass_unit
						For Tunit.Tunit=Each Tunit
							If Tunit\id=id Then
								If Dunit_col(Tunit\typ)>0 Then
									EntityRadius Tunit\h,Dunit_colxr#(Tunit\typ)*sx#,Dunit_colyr#(Tunit\typ)*sy#
									If set_debug Then
										EntityParent Tunit\mh,0
										ScaleEntity Tunit\h,Dunit_colxr#(Tunit\typ)*sx#,Dunit_colyr#(Tunit\typ)*sy#,Dunit_colxr#(Tunit\typ)*sx#
										EntityParent Tunit\mh,Tunit\h
									EndIf
								EndIf
								sx#=sx#*Dunit_size#(Tunit\typ,0)
								sy#=sy#*Dunit_size#(Tunit\typ,1)
								sz#=sz#*Dunit_size#(Tunit\typ,2)
								Exit
							EndIf
						Next
					;Item
					Case Cclass_item
						For Titem.Titem=Each Titem
							If Titem\id=id Then
								sx#=sx#*Ditem_size#(Titem\typ,0)
								sy#=sy#*Ditem_size#(Titem\typ,1)
								sz#=sz#*Ditem_size#(Titem\typ,2)
								If Ditem_radius#(Titem\typ)=0. Then
									EntityRadius Titem\h,5.*sx#
								Else
									EntityRadius Titem\h,Ditem_radius#(Titem\typ)*sx#
								EndIf
								Exit
							EndIf
						Next
				End Select
				;Scale Model
				ScaleEntity h,sx#,sy#,sz#,1
			Else
				parse_error("model","scale")
			EndIf
			
		;FX
		Case "fx"
			mode=param()									;FX
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				h=parent_h(p_env_class,p_env_id,1)
			EndIf
			If h<>0 Then
				EntityFX h,mode
			Else
				parse_error("model","fx")
			EndIf
			
		;Shininess
		Case "shininess"
			shininess#=param()									;Shininess
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
			Else
				h=parent_h(p_env_class,p_env_id,1)
			EndIf
			If h<>0 Then
				EntityShininess h,shininess#
			Else
				parse_error("model","shininess")
			EndIf
			
		;Model
		Case "model"
			model$=param()
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
				modelclass=classint
				modelid=id
			Else
				h=parent_h(p_env_class,p_env_id,1)
				modelclass=p_env_class
				modelid=p_env_id
			EndIf
			If h<>0 Then
				If modelclass=Cclass_object Or modelclass=Cclass_item Then
					newmodel=load_res(model$,Cres_mesh)
					If newmodel<>0 Then
						HideEntity newmodel
						tmp_x#=EntityX(h)
						tmp_y#=EntityY(h)
						tmp_z#=EntityZ(h)
						tmp_yaw#=EntityYaw(h)
						FreeEntity h
						;Object
						If modelclass=Cclass_object Then
							con_object(modelid)
							If TCobject\ch<>0 Then
								FreeEntity TCobject\ch
								TCobject\ch=0
							EndIf
							TCobject\h=CopyEntity(newmodel)
							PositionEntity TCobject\h,tmp_x#,tmp_y#,tmp_z#
							RotateEntity TCobject\h,0,tmp_yaw#,0
							If Dobject_col(TCobject\typ)>0 Then EntityPickMode TCobject\h,2,1
							If Dobject_col(TCobject\typ)=1 Then EntityType TCobject\h,Cworld_col
							range=Float(Float(Dobject_autofade(TCobject\typ))*set_viewfac#)
							If Dobject_autofade(TCobject\typ)<>0 Then EntityAutoFade TCobject\h,range,range+300	
							;Collisions
							;1 / 4
							If Dobject_col(TCobject\typ)=1 Or Dobject_col(TCobject\typ)=4 Then
								If Dobject_swayspeed#(TCobject\typ)<>0. Then
									TCobject\ch=CopyEntity(newmodel)
									PositionEntity TCobject\ch,tmp_x#,tmp_y#,tmp_z#
									RotateEntity TCobject\ch,0,tmp_yaw#,0
									EntityAlpha TCobject\ch,0.0
									EntityType TCobject\ch,Cworld_col
								Else
									EntityType TCobject\h,Cworld_col
								EndIf
							;3
							ElseIf Dobject_col(TCobject\typ)=3 Then
								TCobject\ch=CopyEntity(newmodel)
								PositionEntity TCobject\ch,tmp_x#,tmp_y#,tmp_z#
								RotateEntity TCobject\ch,0,tmp_yaw#,0
								EntityAlpha TCobject\ch,0.0
								EntityType TCobject\ch,Cworld_col
							EndIf
						;Item
						ElseIf modelclass=Cclass_item Then
							con_item(modelid)
							TCitem\h=CopyEntity(newmodel)
							PositionEntity TCitem\h,tmp_x#,tmp_y#,tmp_z#
							RotateEntity TCobject\h,0,tmp_yaw#,0
							If Ditem_radius#(TCitem\typ)=0. Then
								EntityPickMode TCitem\h,2,1
								EntityRadius TCitem\h,5
								EntityType TCitem\h,Cworld_col
							Else
								EntityRadius TCitem\h,Ditem_radius#(TCitem\typ)
								EntityPickMode TCitem\h,1,1
								EntityType TCitem\h,Cworld_col
							EndIf
							If Ditem_autofade(TCitem\typ)<>0 Then EntityAutoFade TCitem\h,Float(Ditem_autofade(TCitem\typ))*set_viewfac#,(Float(Ditem_autofade(TCitem\typ))*set_viewfac#)+50
						EndIf
					EndIf
				Else
					parse_error("'model' command needs to be executed at an object or item")
				EndIf
			Else
				parse_error("model","model")
			EndIf
			
		;Texture
		Case "texture"			
			texture$=param()
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
				h=parent_h(classint,id,1)
				modelclass=classint
				modelid=id
			Else
				h=parent_h(p_env_class,p_env_id,1)
				modelclass=p_env_class
				modelid=p_env_id
			EndIf
			If h<>0 Then
				newtexture=load_res(texture$,Cres_texture)
				If newtexture<>0 Then
					EntityTexture h,newtexture
				EndIf
			Else			
				parse_error("model","fx")
			EndIf
			
			
		;################################################# STATES
		
		;Add State
		Case "addstate"
			p_return$=0
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			If stateint<>0 Then
				If parent_con(classint,id) Then
					p_return$=set_state(stateint,classint,id)
				Else
					parse_error("object",id)
				EndIf
			EndIf
			
		;State Color
		Case "statecolor"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			parse_comma(1)
			r=param()										;R
			parse_comma(1)
			g=param()										;G
			parse_comma(1)
			b=param()										;B
			If stateint<>0 Then
				For TCstate.Tstate=Each Tstate
					If TCstate\parent_class=classint Then
						If TCstate\parent_id=id Then
							If TCstate\typ=stateint Then
								TCstate\value_s$=r+","+g+","+b
								TCstate\r=r
								TCstate\g=g
								TCstate\b=b
								look_state()
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;State Size
		Case "statesize"		
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			parse_comma(1)
			size=param()									;Size
			If stateint<>0 Then
				For TCstate.Tstate=Each Tstate
					If TCstate\parent_class=classint Then
						If TCstate\parent_id=id Then
							If TCstate\typ=stateint Then
								TCstate\value=size
								look_state()
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;State Value
		Case "statevalue"		
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			parse_comma(1)
			statevalue#=param()								;Value
			If stateint<>0 Then
				For TCstate.Tstate=Each Tstate
					If TCstate\parent_class=classint Then
						If TCstate\parent_id=id Then
							If TCstate\typ=stateint Then
								TCstate\value_f#=statevalue#
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;getstatevalue
		Case "getstatevalue"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			valuetype=0
			If parse_comma(0) Then
				valuetype=param()
			EndIf
			If stateint<>0 Then
				For TCstate.Tstate=Each Tstate
					If TCstate\parent_class=classint Then
						If TCstate\parent_id=id Then
							If TCstate\typ=stateint Then
								Select valuetype
									Case 0 p_return$=TCstate\value_f#
									Case 1 p_return$=TCstate\value
									Case 2 p_return$=TCstate\r
									Case 3 p_return$=TCstate\g
									Case 4 p_return$=TCstate\b
								End Select
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;areal_state
		Case "areal_state"
			state$=param()									;State
			stateint=parse_getstate(state$)
			parse_comma(1)
			x#=param()										;X
			parse_comma(1)
			y#=param()										;Y
			parse_comma(1)
			z#=param()										;Z
			range=50
			If parse_comma(0) Then
				range=param()								;Radius
			EndIf
			tempp=CreatePivot()
			PositionEntity tempp,x#,y#,z#
			For Tobject.Tobject=Each Tobject
				If EntityDistance(Tobject\h,tempp)<range Then
					set_state(stateint,Cclass_object,Tobject\id)
				EndIf
			Next
			For Tunit.Tunit=Each Tunit
				If EntityDistance(Tunit\h,tempp)<range Then
					set_state(stateint,Cclass_unit,Tunit\id)
				EndIf
			Next
			For Titem.Titem=Each Titem
				If Titem\parent_mode=Cpm_out Then
					If EntityDistance(Titem\h,tempp)<range Then				
						set_state(stateint,Cclass_item,Titem\id)
					EndIf
				EndIf
			Next
			FreeEntity tempp
						

		;################################################# SKILLS
		
		;Set Skill
		Case "setskill"
			skill$=params()												;Skill
			skillval=0
			caption$=skill$
			If parse_comma(0) Then skillval=param()						;Value
			If parse_comma(0) Then caption$=param()						;Caption
			set_skill(skill$,skillval,caption$)
			
		;Inc Skill
		Case "incskill"
			skill$=params()												;Skill
			skillval=1
			caption$=skill$
			If parse_comma(0) Then skillval=param()						;Value
			If parse_comma(0) Then caption$=param()						;Caption
			inc_skill(skill$,skillval,caption$)
		
		;Got Skill
		Case "gotskill"
			skill$=params()												;Skill
			p_return$=free_skill(skill$)
			
		;Skill Value
		Case "skillvalue"
			skill$=params()												;Skill
			p_return$=skill_value(skill$)
			
		;Skill Name
		Case "skillname"
			skill$=param()												;Skill
			parse_comma(1)
			caption$=param()
			p_return$=skill_name(skill$,caption$)
		

		;################################################# FREE
		
		;freediary - delete all diary entries
		Case "freediary"
			For Tx.Tx=Each Tx
				If Tx\mode=2 Then Delete Tx
			Next
		
		;freescripts	
		Case "freescripts"
			For Tx.Tx=Each Tx
				If Tx\mode=0 Then Delete Tx
			Next
			
		;freescript
		Case "freescript"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			For Tx.Tx=Each Tx
				If Tx\parent_class=classint Then
					If Tx\parent_id=id Then
						Delete Tx
					EndIf
				EndIf
			Next
		
		;freevars - delete all vars
		Case "freevars"
			For Tx.Tx=Each Tx
				If Tx\mode=1 Then Delete Tx
			Next
			freelocals=1
			If parse_param() Then
				freelocals=param()
			EndIf
			If freelocals=1 Then
				For Tx.Tx=Each Tx
					If Tx\mode=4 Then Delete Tx
				Next
			EndIf
			
		;freevar
		Case "freevar","unset"
			var$=param(1)
			If Left(var$,1)="$" Then
				var$=Mid(var$,2,-1)
			EndIf
			var_free(var$)
			While parse_comma(0)
				var$=param(1)
				If Left(var$,1)="$" Then
					var$=Mid(var$,2,-1)
				EndIf
				var_free(var$)
			Wend
			
		;free
		Case "free"
			class$=param()
			classint=parse_getclass(class$)
			;con_add("FREE! - Class: "+class$+" int: "+classint)
			;Item
			If (((classint=-1) And (p_env_class=Cclass_item)) Or (classint=Cclass_item)) Then
				If classint=-1 Then
					classint=p_env_class
					id=p_env_id
				Else
					parse_comma(0)
					id=param()
				EndIf
				count=-1
				If parse_comma(0) Then
					count=param()
				EndIf
				free_item(id,count)
			;Other Stuff
			Else
				If classint=0 Then
					Select class$
						Case "particles"
							For Tp.Tp=Each Tp
								If Tp\h<>0 Then FreeEntity Tp\h
								Delete Tp
							Next
					End Select
				Else
					If classint<>-1 Then
						parse_comma(0)
						ha_free(classint,param())
					Else
						ha_free(classint,-1)	;Self
					EndIf
				EndIf
			EndIf
			
		;Free State
		Case "freestate"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If parse_comma(0) Then
				state$=param()									;State
				stateint=parse_getstate(state$)
				If stateint<>0 Then
					If parent_con(classint,id) Then
						free_state(stateint,classint,id)
					Else
						parse_error("object",id)
					EndIf
				EndIf
			Else
				For Tstate.Tstate=Each Tstate
					If Tstate\parent_class=classint Then
						If Tstate\parent_id=id Then
							free_state(Tstate\typ,classint,id)
						EndIf
					EndIf
				Next
			EndIf
			
		;Free Skill
		Case "freeskill"
			skill$=param()												;Skill
			free_skill(skill$)
			
			
		;Free Stored Item
		Case "freestored"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If parse_comma(0) Then
				typstr$=param()								;Typ
			Else
				typstr$="all"
			EndIf
			;All
			If typstr$="all" Then
				For Titem.Titem=Each Titem
					If Titem\parent_class=classint Then
						If Titem\parent_id=id Then
							free_item(Titem\id,-1)
						EndIf
					EndIf
				Next
			;Special
			Else
				count=-1
				If parse_comma(0) Then
					count=param()						;Count
				EndIf
				For Titem.Titem=Each Titem
					If Titem\parent_class=classint Then
						If Titem\parent_id=id Then
							If Titem\typ=Int(typstr$) Then
								free_item(Titem\id,count)
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
		;Free Button
		Case "freebutton"
			id=param()													;ID
			If id>=0 And id<10 Then
				in_sb(id)=0
			Else
				parse_error("Button ID between 0 and 9 expected!")
			EndIf
			
		;free interface txt
		Case "freetext"
			id=param()													;ID
			If id>=0 And id<19 Then
				in_sitxt(id)=0
			Else
				parse_error("Interface Text ID between 0 and 19 expected!")
			EndIf
			
			
					
		;################################################# RETURN
		
		
		;Loop ID
		Case "loop_id"
			p_return$=p_loopi
		
		
		;Return (rather senseless - for debug purposes only)
		Case "return"
			p_return$=param()
			
		;Int
		Case "int"
			tointvalue=Int(param())
			p_return$=tointvalue
		
		;Playergotitem
		Case "playergotitem"
			itemtyp=param()									;Item Typ
			itemcount=countstored_items(Cclass_unit,g_player,itemtyp)
			p_return$=itemcount
			
		;Day
		Case "day"
			p_return$=map_day
		
		;Hour
		Case "hour"
			p_return$=map_hour
		
		;Minute
		Case "minute"
			p_return$=map_minute
			
		;Random
		Case "random"
			peak=param()									;Peak
			If parse_comma(0) Then
				bottom=param()									;Bottom
				p_return$=Rand(bottom,peak)
				;con_add("rand "+bottom+"-"+peak+" -> "+p_return$)
			Else
				p_return$=Rand(peak)
				;con_add("rand "+peak+" -> "+p_return$)
			EndIf
				
		;In Range
		Case "inrange"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			If parse_comma(0)
				range=param()									;Range
				If parse_comma(0) Then
					class2=parse_getclass(param())				;Class 2
					parse_comma(1)
					id2=param()									;ID 2
					If ha_distance(classint,id,class2,id2)<=range Then
						p_return$=1
					Else
						p_return$=0
					EndIf
				Else
					p_return$=ha_idinrange(cam,range,classint,id)
				EndIf
			Else
				range=300
				p_return$=ha_idinrange(cam,range,classint,id)
			EndIf
			
		
		;Count In Range
		Case "count_inrange"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			typ=param()										;Type
			If parse_comma(0) Then
				range=param()									;Range
				If parse_comma(0) Then
					parse_classid()
					class2=tmp_gclass							;Class 2
					id2=tmp_gid									;ID 2
					countinrangehandle=parent_h(class2,id2)
					If countinrangehandle<>0 Then
						p_return$=ha_inrange(countinrangehandle,range,classint,typ)
						;con_add("count_inrange object "+range+","+classint+","+typ+" -> "+p_return$)
					Else
						p_return$=0
					EndIf
				Else
					p_return$=ha_inrange(cam,range,classint,typ)
					;con_add("count_inrange cam "+range+","+classint+","+typ+" -> "+p_return$)
				EndIf
			Else
				range=300
				p_return$=ha_inrange(cam,range,classint,typ)
				;con_add("count_inrange cam "+range+","+classint+","+typ+" -> "+p_return$)
			EndIf
		
		;Count Behaviourinrange
		Case "count_behaviourinrange"
			countinrangehandle=cam
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			behaviour$=params()								;Behaviour
			If parse_comma(0) Then
				range=param()									;Range
				If parse_comma(0) Then
					class2=parse_getclass(param())				;Class 2
					parse_comma(1)
					id2=param()									;ID 2
					countinrangehandle=parent_h(class2,id2)
				EndIf
			Else
				range=300
			EndIf
			p_return$=0
			Select classint
				;Object
				Case Cclass_object
					For Tobject.Tobject=Each Tobject
						If Dobject_behaviour$(Tobject\typ)=behaviour$ Then
							If EntityDistance(countinrangehandle,Tobject\h)<=range Then
								p_return$=Int(p_return$)+1
							EndIf
						EndIf
					Next
				;Unit
				Case Cclass_unit	
					behaviourint=ai_getbehaviour(behaviour$)
					For Tunit.Tunit=Each Tunit
						If Dunit_behaviour(Tunit\typ)=behaviourint Then
							If EntityDistance(countinrangehandle,Tunit\h)<=range Then
								p_return$=Int(p_return$)+1
							EndIf
						EndIf
					Next
				;Item
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\parent_class=0 Then
							If Ditem_behaviour$(Tunit\typ)=behaviour$ Then
								If EntityDistance(countinrangehandle,Tunit\h)<=range Then
									p_return$=Int(p_return$)+1
								EndIf
							EndIf
						EndIf
					Next
				;Default
				Default 
					parse_error("wrongclass",parse_getclasstxt$(classint))
			End Select
			
		;Hit Damage
		Case "hit_damage"
			p_return$=pv_attack_damage#
			
		;Hit Weapon
		Case "hit_weapon"
			p_return$=pv_attack_weapon
			
		;Hit Ammo
		Case "hit_ammo"
			p_return$=pv_attack_ammo
			
		;Impact Class
		Case "impact_class"
			p_return$=pv_impact_class
		
		;Impact ID
		Case "impact_id"
			p_return$=pv_impact_id
			
		;Impact Kill
		Case "impact_kill"
			p_return$=pv_impact_kill
			
		;Impact First
		Case "impact_first"
			If pv_impact_num=1 Then
				p_return$=1
			Else
				p_return$=0
			EndIf
			
		;Impact Amount
		Case "impact_amount"
			p_return$=pv_impact_amount
			
		;Impact Ground
		Case "impact_ground"
			p_return$=pv_impact_ground
			
		;Impact X
		Case "impact_x"
			p_return$=pv_impact_x#
			
		;Impact Y
		Case "impact_y"
			p_return$=pv_impact_y#
		
		;Impact Z
		Case "impact_z"
			p_return$=pv_impact_z#
			
		;Count Stored
		Case "count_stored"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			p_return$=0
			If parse_comma(0) Then
				;Count Special One
				typ=param()										;Item Type
				For Titem.Titem=Each Titem
					If Titem\parent_class=classint Then
						If Titem\parent_id=id Then
							If Titem\typ=typ Then
								If Titem\parent_mode=Cpm_in Then
									p_return$=Int(p_return$)+Titem\count
								EndIf
							EndIf
						EndIf
					EndIf
				Next
			Else
				;Count All
				For Titem.Titem=Each Titem
					If Titem\parent_class=classint Then
						If Titem\parent_id=id Then								
							If Titem\parent_mode=Cpm_in Then
								p_return$=Int(p_return$)+Titem\count
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			;con_add("count stored @"+classint+","+id+" = "+p_return$)
			
		;Got State
		Case "gotstate"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			state$=param()									;State
			stateint=parse_getstate(state$)
			p_return$=0
			For Tstate.Tstate=Each Tstate
				If Tstate\parent_class=classint Then
					If Tstate\parent_id=id Then
						If Tstate\typ=stateint Then
							p_return$=1
							Exit
						EndIf
					EndIf
				EndIf
			Next
			
		;Count States
		Case "count_state"
			state$=param()									;State
			stateint=parse_getstate(state$)
			p_return$=0
			For Tstate.Tstate=Each Tstate
				If Tstate\typ=stateint Then
					p_return$=Int(p_return$)+1
				EndIf
			Next
			
		;Distance
		Case "distance"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma(1)
			class$=param()									;Class 2
			classint2=parse_getclass(class$)
			parse_comma(1)
			id2=param()										;ID 2
			h1=parent_h(classint,id)
			h2=parent_h(classint2,id2)
			If h1<>0 And h2<>0 Then
				p_return$=EntityDistance(h1,h2)
			Else
				If h1=0 Then
					parse_error("object",id)
				Else
					parse_error("object",id2)
				EndIf
				p_return$=0
			EndIf
			
		;Player Distance
		Case "playerdistance"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h1=parent_h(classint,id)
			If h1<>0 Then
				If g_cplayer\h<>0 Then
					p_return$=EntityDistance(g_cplayer\h,h1)
				Else
					p_return$=0
				EndIf
			Else
				parse_error("object",id)
				p_return$=0
			EndIf
			
		;In View
		Case "inview"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h1=parent_h(classint,id,1)
			If h1<>0 Then
				p_return$=EntityInView(h1,cam)
			Else
				parse_error("object",id)
				p_return$=0
			EndIf
			
		;Get X / Y / Z / pitch / yaw, roll
		Case "getx","gety","getz","getpitch","getyaw","getroll"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h=parent_h(classint,id)
			If h=0 Then
				;If tmp_killclass=classint Then
				;	If tmp_killid=id Then
				;		h=tmp_killmodel
				;	EndIf
				;EndIf
				h=parse_kill_get(classint,id)
			EndIf
			;Position of Parent in Case of stored Item
			If classint=Cclass_item Then
				For Titem.Titem=Each Titem
					If Titem\id=id Then
						If Titem\parent_mode=Cpm_in Then
							h=parent_h(Titem\parent_class,Titem\parent_id)
						EndIf
					EndIf
				Next
			EndIf
			If h<>0 Then
				Select p_com$
					Case "getx" p_return$=EntityX(h)
					Case "gety" p_return$=EntityY(h)
					Case "getz" p_return$=EntityZ(h)
					Case "getpitch" p_return$=EntityPitch(h)
					Case "getyaw" p_return$=EntityYaw(h)
					Case "getroll" p_return$=EntityRoll(h)
				End Select
			Else
				parse_error("object",id)
				p_return$=0
			EndIf
			
		;Exists
		Case "exists"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			p_return$=parent_con(classint,id)
			
		;Lives
		Case "lives"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf									
			p_return$=(1-ha_killed(classint,id))
			
		;gt
		Case "gt"
			p_return$=gt
			
		;currentclass
		Case "currentclass","cclass","current_class"
			p_return$=p_env_class
		
		;currentid
		Case "currentid","cid","current_id"
			p_return$=p_env_id
			
		;inarea_freshwater
		Case "inarea_freshwater"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h=parent_h(classint,id)
			If h<>0 Then
				p_return$=info_area(41,EntityX(h),EntityZ(h))
			EndIf
		
		;inarea_dig
		Case "inarea_dig"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h=parent_h(classint,id)
			If h<>0 Then
				p_return$=info_area(42,EntityX(h),EntityZ(h))
			EndIf
		
		;inarea_fish
		Case "inarea_fish"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h=parent_h(classint,id)
			If h<>0 Then
				p_return$=info_area(43,EntityX(h),EntityZ(h))
			EndIf
		
		;inarea
		Case "inarea"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			h=parent_h(classint,id)
			If h<>0 Then
				p_return$=info_area(44,EntityX(h),EntityZ(h))
			EndIf
			
		;Get Stored Item ID
		Case "getstored"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()										;ID
			typ=0
			If parse_comma(0) Then
				typ=param()										;Typ
			EndIf
			p_return$=0
			For Titem.Titem=Each Titem
				If Titem\parent_class=classint Then
					If Titem\parent_id=id Then
						If Titem\typ=typ Or typ=0 Then
							p_return$=Titem\id
							Exit
						EndIf
					EndIf
				EndIf
			Next
			
		;Get Amount
		Case "getamount"
			id=param()										;ID
			For Titem.Titem=Each Titem
				If Titem\id=id Then
					p_return$=Titem\count
					Exit
				EndIf
			Next
			
		;Riding
		Case "riding"
			p_return$=g_drive
			
		;Player Weapon
		Case "getplayerweapon"
			If Handle(g_cplayer)<>0 Then p_return$=g_cplayer\player_weapon
		
		;Player Ammo
		Case "getplayerammo"
			If Handle(g_cplayer)<>0 Then p_return$=g_cplayer\player_ammo
			
		;Weather
		Case "getweather"
			p_return$=env_cweather
			
		;Freespace
		Case "freespace"
			x=param()									;X
			parse_comma(1)
			y=param()									;Y
			parse_comma(1)
			z=param()									;Z
			range=300
			check_objects=1
			check_units=1
			check_items=1
			check_infos=0
			If parse_comma(0) Then
				range=param()							;Range
				If parse_comma(0) Then
					check_objects=param()				;Objects
					If parse_comma(0) Then
						check_units=param()				;Units
						If parse_comma(0) Then
							check_items=param()			;Items
							If parse_comma(0) Then
								check_infos=param()		;Infos
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			p_return$=ha_freespace(x,y,z,range,check_objects,check_units,check_items,check_infos)

		;Compare Material
		Case "compare_material"
			p_return$=0
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma()
			mat=get_material(param$())						;Compare Value (Material)
			Select classint
				Case Cclass_object
					If con_object(id) Then
						If Dobject_mat(TCobject\typ)=mat Then p_return$=1
					EndIf
				Case Cclass_unit
					If con_unit(id) Then
						If Dunit_mat(TCunit\typ)=mat Then p_return$=1
					EndIf
				Case Cclass_item
					If con_item(id) Then
						If Ditem_mat(TCitem\typ)=mat Then p_return$=1
					EndIf
			End Select
		
		;Compare Behaviour
		Case "compare_behaviour"
			p_return$=0
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			parse_comma()
			behaviour$=param()								;Compare Value (Behaviour)
			Select classint
				Case Cclass_object
					If con_object(id) Then
						If Dobject_behaviour$(TCobject\typ)=behaviour$ Then p_return$=1
					EndIf
				Case Cclass_unit
					If con_unit(id) Then
						If Dunit_behaviour(TCunit\typ)=ai_getbehaviour(behaviour$) Then p_return$=1
					EndIf
				Case Cclass_item
					If con_item(id) Then
						If Ditem_behaviour$(TCitem\typ)=behaviour$ Then p_return$=1
					EndIf
			End Select
			
		;Health
		Case "health"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			value#=0
			If parse_comma(0) Then
				value#=param()								;Health Change
			EndIf
			p_return$=0
			Select classint
				Case Cclass_unit
					For Tunit.Tunit=Each Tunit
						If Tunit\id=id Then
							Tunit\health#=Tunit\health#+value#
							If Tunit\health#<0 Then Tunit\health#=0
							If Tunit\health#>Tunit\health_max# Then Tunit\health#=Tunit\health_max#
							p_return$=Tunit\health#
							Exit
						EndIf
					Next
				Case Cclass_object
					For Tobject.Tobject=Each Tobject
						If Tobject\id=id Then
							Tobject\health#=Tobject\health#+value#
							If Tobject\health#<0 Then Tobject\health#=0
							If Tobject\health#>Tobject\health_max# Then Tobject\health#=Tobject\health_max#
							p_return$=Tobject\health#
							Exit
						EndIf
					Next
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\id=id Then
							Titem\health#=Titem\health#+value#
							If Titem\health#<0 Then Titem\health#=0
							If Titem\health#>Ditem_health(Titem\typ) Then Titem\health#=Ditem_health(Titem\typ)
							p_return$=Titem\health#
							Exit
						EndIf
					Next
			End Select
			
		;Type
		Case "type"
			class$=param()									;Class
			classint=parse_getclass(class$)
			If classint=-1 Then
				classint=p_env_class
				id=p_env_id
			Else
				parse_comma(1)
				id=param()									;ID
			EndIf
			p_return$=ha_type(classint,id)
			
		;Use Stuff
		Case "use_x"
			p_return$=pc_use_x#
			
		;Use Stuff
		Case "use_y"
			p_return$=pc_use_y#
			
		;Use Stuff
		Case "use_z"
			p_return$=pc_use_z#
			
		;TerrainY
		Case "terrainy"
			x#=param()										;X
			parse_comma(1)
			z#=param()										;Z
			p_return$=e_tery(x#,z#)
			
		;Sin
		Case "sin"
			x#=param()
			classint=1
			If parse_comma(0) Then classint=param()
			If classint>0 Then
				p_return$=Sin(x#)*100.0
			Else
				p_return$=Sin(x#)
			EndIf
		
		;Cos
		Case "cos"
			x#=param()
			classint=1
			If parse_comma(0) Then classint=param()
			If classint>0 Then
				p_return$=Cos(x#)*100.0
			Else
				p_return$=Cos(x#)
			EndIf
		
		;Tan
		Case "tan"
			x#=param()
			classint=1
			If parse_comma(0) Then classint=param()
			If classint>0 Then
				p_return$=Tan(x#)*100.0
			Else
				p_return$=Tan(x#)
			EndIf
			
		;Parent Class / Parent ID
		Case "parent_class","parent_id"
			idstring$=param()								;Item ID
			If idstring$="self" Then
				If p_env_class=Cclass_item Then
					idstring$=p_env_id
				EndIf
			EndIf
			p_return$=0
			For Titem.Titem=Each Titem
				If Titem\id=Int(idstring$) Then
					If p_com$="parent_class" Then
						p_return$=Titem\parent_class
					ElseIf p_com$="parent_id" Then
						p_return$=Titem\parent_id
					EndIf
				EndIf
			Next
			
		;Mapsize
		Case "mapsize"
			p_return$=(ter_size*Cworld_size)
		
		;Count
		Case "count"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()										;Type
			;con_add("count "+classint+" id "+id)
			p_return$=0
			Select classint
				Case Cclass_object
					For Tobject.Tobject=Each Tobject
						If Tobject\typ=id Then p_return$=Int(p_return$)+1
					Next
				Case Cclass_unit
					For Tunit.Tunit=Each Tunit
						If Tunit\typ=id Then
							If Tunit\health#>0. Then
								p_return$=Int(p_return$)+1
							EndIf
						EndIf
					Next
					con_add "result "+p_return$
				Case Cclass_item
					For Titem.Titem=Each Titem
						If Titem\typ=id Then p_return$=Int(p_return$)+Titem\count
					Next
				Case Cclass_info
					For Tinfo.Tinfo=Each Tinfo
						If Tinfo\typ=id Then p_return$=Int(p_return$)+1
					Next
				Case Cclass_state
					For Tstate.Tstate=Each Tstate
						If Tstate\typ=id Then p_return$=Int(p_return$)+1
					Next
			End Select
		
		;Defparam
		Case "defparam"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()										;Type
			parse_comma(1)
			file$=param()
			p_return$=0
			For Tdefparam.Tdefparam=Each Tdefparam
				If Tdefparam\class=classint Then
					If Tdefparam\typ=id Then
						If Tdefparam\key$=file$ Then
							p_return$=Tdefparam\value$
							Exit
						EndIf
					EndIf
				EndIf
			Next
			
		;Impact Class
		Case "sleeping"
			p_return$=g_sleep
		
			
		;################################################# VARS
		
		;Abs
		Case "abs"	
			p_return$=Abs(Float(param()))
			
		;Save Vars
		Case "savevars"
			file$="varcache"
			varlist$="-"
			If parse_param() Then
				file$=param(1)
				If parse_comma(0) Then
					varlist$=param(1)
				EndIf
			EndIf
			p_return$=var_cache_save(file$,varlist$)
		
		;Load Vars
		Case "loadvars"
			file$="varcache"
			If parse_param() Then
				file$=param(1)
			EndIf
			p_return$=var_cache_load(file$)
			
		;Var Exists
		Case "varexists"
			var$=param(1)
			If Left(var$,1)="$" Then
				var$=Mid(var$,2,-1)
			EndIf
			p_return$=var_exists(var$)
			
		;Rename
		Case "rename"
			var$=param(1)
			If Left(var$,1)="$" Then
				var$=Mid(var$,2,-1)
			EndIf
			parse_comma(1)
			name$=param()
			For Tx.Tx=Each Tx
				If Tx\mode=1 Then
					If Tx\key$=var$ Then
						Tx\key$=name$
						Exit
					EndIf
				EndIf
			Next
			
		;temp
		Case "temp"
			var$=param(1)
			var_temp(var$)
			While parse_comma(0)
				var$=param(1)
				var_temp(var$)
			Wend
			
		;tempall
		Case "tempall"
			For Tx.Tx=Each Tx
				If Tx\mode=1 Then
					Tx\stuff$="temp"
				EndIf
			Next
			
		;local
		Case "local"
			var$=param(1)
			var_local(var$)
			While parse_comma(0)
				var$=param(1)
				var_local(var$)
			Wend
		
		;getlocal
		Case "getlocal"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()										;ID
			parse_comma(1)
			var$=param(1)									;Var
			If Left(var$,1)="$" Then
				var$=Mid(var$,2,-1)
			EndIf
			p_return$=var_get_local(classint,id,var$)
		
		;setlocal
		Case "setlocal"
			class$=param()									;Class
			classint=parse_getclass(class$)
			parse_comma(1)
			id=param()										;ID
			parse_comma(1)
			var$=param(1)									;Var
			If Left(var$,1)="$" Then
				var$=Mid(var$,2,-1)
			EndIf
			strvalue$=0
			If parse_comma(0) Then							;Value
				strvalue=param()
			EndIf
			DebugLog "set local  "+strvalue
			var_set_local(classint,id,var$,strvalue)
			
		;getsetting
		Case "getsetting"
			p_return$=0
			class$=param()
			Select class$
				Case "xres" p_return$=set_scrx
				Case "yres" p_return$=set_scry
				Case "depth" p_return$=set_scrbit
				Case "debug" p_return$=set_debug
				Case "viewrange" p_return$=set_viewrange
				Case "gore" p_return$=set_gore
				Case "commandline" p_return$=set_cmd$
				Case "chat" p_return$=in_chat$
				Case "lastchat" p_return$=in_lastchat$
				Case "time" p_return$=CurrentTime()
				Case "date" p_return$=CurrentDate()
				Case "version" p_return$=Cversion$
				Default parse_error("Settings '"+class$+"' does not exist!")
			End Select
		
		;state
		Case "state"
			p_return$=pv_state
		
		
		;################################################# AI
		
		;ai_signal
		Case "ai_signal"
			mode=parse_getaisignal(param())					;Mode
			rangeadd=300
			If parse_comma(0)
				rangeadd=param()								;Range
			EndIf
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
			Else
				classint=p_env_class
				id=p_env_id
			EndIf
			ai_signal(classint,id,mode,rangeadd)
			
		;ai_typesignal
		Case "ai_typesignal"
			mode=parse_getaisignal(param())					;Mode
			parse_comma(1)
			unittype=param()								;Type
			rangeadd=300
			If parse_comma(0)
				rangeadd=param()								;Rangeadd
			EndIf
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
			Else
				classint=p_env_class
				id=p_env_id
			EndIf
			ai_typesignal(classint,id,mode,unittype,rangeadd)
			
		;ai_behavioursignal
		Case "ai_behavioursignal"
			mode=parse_getaisignal(param())					;Mode
			parse_comma(1)
			unittype=ai_getbehaviour(param())				;Type
			rangeadd=300
			If parse_comma(0)
				rangeadd=param()								;Rangeadd
			EndIf
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
			Else
				classint=p_env_class
				id=p_env_id
			EndIf
			ai_behavioursignal(classint,id,mode,unittype,rangeadd)	
		
		;ai_mode
		Case "ai_mode"
			unitidstr$=param()								;Unit
			If unitidstr$="self" Then
				unitid=p_env_id
			Else
				unitid=Int(unitidstr$)
			EndIf
			parse_comma(1)
			mode=parse_getaimode(param())					;Mode
			classint=0
			id=0
			If parse_comma(0) Then
				class$=param()									;Class
				classint=parse_getclass(class$)
				parse_comma(1)
				id=param()										;ID
			EndIf
			ai_unitmode(unitid,mode,classint,id)
			
		;ai_eater
		Case "ai_eater"
			;con_add("ai_eater: "+pv_ai_eater)
			p_return$=pv_ai_eater
			
		;ai_stay
		Case "ai_stay"
			unitidstr$=param()								;Unit
			If unitidstr$="self" Then
				unitid=p_env_id
			Else
				unitid=Int(unitidstr$)
			EndIf
			id=1
			If parse_comma(0) Then							;Set/Delete
				id=param()
			EndIf
			If id=1 Then
				;Set
				set_state(Cstate_ai_stick,Cclass_unit,unitid)
			Else
				;Delete
				free_state(Cstate_ai_stick,Cclass_unit,unitid)
			EndIf
			
		;ai_center
		Case "ai_center"
			If parse_param() Then
				unitidstr$=param()								;Unit
				If unitidstr$="self" Then
					unitid=p_env_id
				Else
					unitid=Int(unitidstr$)
				EndIf
			Else
				unitid=p_env_id
			EndIf
			For Tunit.Tunit=Each Tunit
				If Tunit\id=unitid Then
					Tunit\ai_cx#=EntityX(Tunit\h)
					Tunit\ai_cz#=EntityZ(Tunit\h)
					If Tunit\ai_ch<>0 Then
						PositionEntity Tunit\ai_ch,Tunit\ai_cx#,0,Tunit\ai_cz#
					EndIf
					Exit
				EndIf
			Next			
			
		
		;##################################### Strings
		
		;length
		Case "length"
			unitidstr$=param()								;String
			p_return$=Len(unitidstr$)
			
		;trim
		Case "trim"
			unitidstr$=param()								;String
			p_return$=trimtext(unitidstr$)
			
		;replace
		Case "replace"
			unitidstr$=param()								;String
			parse_comma(1)
			search$=param()									;Search
			parse_comma(1)
			repl$=param()									;Replace
			p_return$=Replace(unitidstr$,search$,repl$)

		;split
		Case "split"
			unitidstr$=param()								;String
			parse_comma(1)
			search$=param()									;Sign
			parse_comma(1)
			repl$=param()									;Substring
			split$(unitidstr$,search$,Int(repl$)+1)
			p_return$=splits(Int(repl$))
			
		;extract
		Case "extract"
			unitidstr$=param()								;String
			parse_comma(1)
			search$=param()									;Start
			repl$=-1
			If parse_comma(0) Then repl$=param()			;Length
			If Int(search$)<=0 Then search$=1
			If Int(repl$)=-1 Or Int(repl$)>0 Then
				p_return$=Mid(unitidstr$,Int(search$),Int(repl$))
			Else
				parse_error("extract length has to be either -1 or >0")
			EndIf
			
		;join
		Case "join"
			p_return$=""
			p_return$=params()								;Var
			While parse_comma(0)
				var$=params()
				p_return$=p_return$+var$
			Wend
		
		;DEFAULT ERROR	
		Default parse_error("uk")
	End Select
	
	;Find and jump to Semi (;)
	If jumptosemi=1 Then
		;con_add "parse semi for cmd '"+p_com$+"'"
		parse_semi()
	EndIf
	
	;Disbale Internal Parsing
	p_internal=0
End Function
