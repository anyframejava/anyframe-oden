<%@ page language="java" errorPage="/common/error.jsp"
	pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	String roles = (String) session.getAttribute("userrole");
	String userid = (String) session.getAttribute("userid");
	String groupid = (String) session.getAttribute("groupid");
	String currentSelectedTab = (String)request.getParameter("para1");
%>
<script type="text/javascript">

var t; //timer
var tabCount;
var groups = '${groupUngroups.groups}';
var unGroups = '${groupUngroups.unGroups}';
var currentSelectedTab = '<%=currentSelectedTab%>';
var groupId = encodeURI('<%=groupid%>');
var deleteTabId; //deleteTab Id
var disableTab;

jQuery(document).ready(function() {
	jQuery("#grid_job").jqGrid( {

		url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findList(cmd, buildName, group)&viewName=jsonView'/>",
		
		mtype : 'POST',
		postData : {
			cmd : 	encodeURI('<%=roles%>'),
			buildName : '',
			group : currentSelectedTab
		},		
		datatype : "json",
		colNames : [ '<spring:message code="job.grid.job"/>',
		     		 '<spring:message code="job.grid.action"/>',
		     		 '<spring:message code="job.grid.buildstatus"/>',
		     		 '<spring:message code="job.grid.status"/>',
		     		 '<spring:message code="job.grid.id"/>'],
		jsonReader : {
			repeatitems : false
		},
		colModel : [{
			name : 'name',
			index : 'name',
			align : 'center',
			width : 190,
			sortable:false
		}, {
			name : 'mode',
			index : 'mode',
			align : 'center',
			width : 240,
			sortable:false
		}, {
			name : 'buildDate',
			index : 'buildDate',
			align : 'center',
			width : 210,
			sortable:false
		}, {
			name : 'date',
			index : 'date',
			align : 'center',
			width : 210,
			sortable:false
		}, {
			key : true, 
			name : 'txId', 
			hidden : true
		}
		 ],
		width : 888,
		height : 375,
		scroll : false,
		pgbuttons: false,
	   	pgtext: false,
	   	shrinkToFit : false,
	   	pginput:false,
		forceFit : true,
		viewrecords : false,
		sortable : true,
		rowNum : -1,
		pager : jQuery('#pager_job'),

		loadError : function(xhr, st, err) {
			alert('<spring:message code="job.load.error"/>');
		},
		gridComplete: function() {
			// 2014.11.21 Tab disable 처리 Start
			for(var i=0; i<tabCount; i++) {
				$("#jobTabs").tabs("enable", i);
			}
			$("a[href='" +disableTab+ "']").parent().addClass('ui-state-default').removeClass('ui-state-disabled');
			// 2014.11.21 End
			
			$.ajax( {
				url : "<c:url value='/simplejson.do?layout=jsonLayout&service=statusService.checkRunning(cmd)&viewName=jsonView'/>",
				dataType : "json",
				async : true,
				success : function(result) {
					if(result.autoData == false ) {
						stopTimer();
					} else {
						startTimer();
					}
				}
			});
		}
	});
	
	$('[name=addlink]').click( function(){
		fn_addTab('03job', 'JobDetail', 'jobdetail', '""', '&initdataService=groupService.findGroupAndBuildJob()&initdataResult=groupBuildJobs', currentSelectedTab);
	});
	
	$("#grid_job").closest(".ui-jqgrid-bdiv").css({ 'overflow-y' : 'scroll' });
	jQuery("#grid_job").jqGrid('navGrid','#pager_pager_job',{edit:false,add:false,del:false,search:false});
	
	groupSplit();
	
	$("#jobTabs").tabs({
		//탭 추가 영역
		show : function(event, ui) {
			$("#tabDialog").dialog('option', 'title', '<spring:message code="job.tab.addtitle"/>');
			var activeTab = $('#jobTabs').tabs('option', 'selected');
			tabCount = $('#jobTabs').tabs('length');
			
			// add Tab Select
			if (groupId == 'GRP-0001' && activeTab + 1 == tabCount) {
				$('#jobList').empty();
				 if (unGroups != '') {
					for(var i=0; i<unGroups.length; i++){
		    			$('#jobList').append('<input type="checkbox" name="checkbox" value="' + trim(unGroups[i]) + '"' + 'class="ui-widget-content ui-corner-all" style="vertical-align:middle;" /> ' + unGroups[i] + '<br />');
					}
				}
				$('.ui-dialog-buttonpane button:contains("Add")').show();
				$('.ui-dialog-buttonpane button:contains("Update")').hide();
				$( "#tabDialog" ).dialog( "open" );
			}
		},
		
		select: function(event, ui) {
			if($(ui.tab).text() != '+'){
				currentSelectedTab = trim($(ui.tab).text());
				
				// 2014.11.21 Tab disable 처리 Start
				var activeTab = $('#jobTabs').tabs('option', 'selected');
				for(var i=0; i<tabCount; i++) {
					$("#jobTabs").tabs( "disable", i);
				}
				// ALL Tab Disable
				if(activeTab == 0) {
					disableTab = "#a";
					$("a[href='" +disableTab+ "']").parent().removeClass('ui-tabs-selected ui-state-active').addClass('ui-state-disabled');
				// Another Tab Disable	
				} else {
					var disableTabNum = activeTab+1;
					disableTab = "#tabs-"+disableTabNum;
					$("a[href='" +disableTab+ "']").parent().removeClass('ui-tabs-selected ui-state-active').addClass('ui-state-disabled');		
				}
				// 2014.11.21 End
				
				jQuery("#grid_job").jqGrid("setGridParam", { 
					aysnc : false,
	    			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findList(cmd, buildName, group)&viewName=jsonView'/>",
	    			
	    			mtype : 'POST',
	    			postData : {
	    				cmd : 	encodeURI('<%=roles%>'),
	    				buildName : '',
	    				group : $(ui.tab).text()
	    			}
				}).trigger("reloadGrid");
				
			}
		}
	});
	
	// Admin role login 
	// gear icon: udating the tab on click
    $( "#jobTabs" ).delegate( "span.ui-icon-gear", "click", function() {
    	$('.ui-dialog-buttonpane button:contains("Update")').show();
    	$('.ui-dialog-buttonpane button:contains("Add")').hide();
		var tabName = trim($(this).closest("li").text());
		
		$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=groupService.findByName(tabname)&viewName=jsonView'/>",
				{
					tabname  : tabName
			
				}, function(data) {
					var retList = data.autoData;
					//alert(retList);
					$('#jobList').empty();
					if (retList != '') {
						for(var i=0; i<retList.length; i++){
			    			$('#jobList').append('<input type="checkbox" name="checkbox" checked="checked" value="' + trim(retList[i]) + '"' + 'class="ui-widget-content ui-corner-all" style="vertical-align:middle;" /> ' + retList[i] + '<br />');
						}
					}
					if (unGroups != '') {
						for(var i=0; i<unGroups.length; i++){
				    		$('#jobList').append('<input type="checkbox" name="checkbox" value="' + trim(unGroups[i]) + '"' + 'class="ui-widget-content ui-corner-all" style="vertical-align:middle;" /> ' + unGroups[i] + '<br />');
						}
					} 
		}); 
		$("#tab_title").val(tabName);
 		$("#tabDialog").dialog('option', 'title', '<spring:message code="job.tab.edittitle"/>');
		$("#tabDialog").dialog('open');
    });
	
	// close icon: removing the tab on click
    $( "#jobTabs" ).delegate( "span.ui-icon-close", "click", function() {
    	var tabName = $(this).closest("li").text();
    	var result = confirm('<spring:message code="job.confirm.delete"/>');
    	if(result){
    		/* var panelId = $(this).closest("li").remove().attr( "aria-controls" );
    		$( "#" + panelId ).remove(); */
    		$( "#jobTabs" ).tabs("refresh");
    		$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=groupService.remove(tabName)&viewName=jsonView'/>",
    				{
    				tabName  : tabName
    				}, function(data) {
    				fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', 'ALL');
    			}); 
    	}
    });
    var currSelectedTab = currentSelectedTab;
	//최초 tab 로딩 영역
	if (groups != '') {
		for(var i=0; i<groups.length; i++){
			$("#jobTabs").tabs("remove", tabCount-1);
			var tabTitle = trim(groups[i]);
			var tabTemplate = '';
			if(groupId == 'GRP-0001') {
				tabTemplate = "<li class='ui-corner-top'><a href='<%='#'%>{href}'><%='#'%>{label}</a><span class='ui-icon ui-icon-gear' role='presentation' style='float:left; margin-right:-3px'></span><span class='ui-icon ui-icon-close' role='presentation'></span></li>";
			} else {
				tabTemplate = "<li class='ui-corner-top'><a href='<%='#'%>{href}'><%='#'%>{label}</a></li>";
			}

			var tabs = $( "#jobTabs" ).tabs(),
		    label = tabTitle || "Tab " + tabCount,
		    id = "tabs-" + tabCount,
		    li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );

		    tabs.find( ".ui-tabs-nav" ).append( li ); 
		    tabs.append( "<div id='" + id + "' style='display:none;' ><p></p></div>" );
		    
		    $("#jobTabs").tabs("add", "#c", "+");
		    $("#jobTabs").tabs("refresh");
		    $("#jobTabs").tabs('select', tabCount-1);
		    
		    if(groupId != 'GRP-0001') {
		    	$("#jobTabs").tabs("remove", tabCount-1);
		    }
		}
		// 2014.11.25 Start
		// 모든 탭 최초 로딩 후 ALL탭에서 시작하도록 초기화
		$("a[href='#a']").parent().addClass('ui-state-default').removeClass('ui-state-disabled');
		$("#jobTabs").tabs('select', 0);
		// 2014.11.25 End
	}
	currentSelectedTab = currSelectedTab;
	// modal dialog init: custom buttons and a "close" callback resetting the form inside
	var tabDialog = $( "#tabDialog" ).dialog({
		autoOpen: false,
		modal: true,
		resizable:false,
		height:230,
		buttons: {
			Cancel: function() {
				$(this).dialog( "close" );
				$("#jobTabs").tabs('select', 0);
			},
			Add: function() {
				addTab();
				//$(this).dialog( "close" );
			},
			
			Update: function() {
			 	updateTab();
			}
		},
	 
		close: function() {
			form[ 0 ].reset();
			$("#jobTabs").tabs('select', 0);
		}
	});
	
	// 중복 dialog 삭제
	var dialogs = $('.ui-dialog');
	if(dialogs.length > 1) {
		$('#tabDialog').remove();
	}
	
	// addTab form: calls addTab function on submit and closes the dialog
	var form = tabDialog.find( "form" ).submit(function( event ) {
	  addTab();
	  tabDialog.dialog( "close" );
	  event.preventDefault();
	});
	
	// 이전 tab select
	var tab = $('#jobTabs a').filter(function(){
        return $(this).text() == currentSelectedTab;
    }).parent();
    var index = $( 'li', '#jobTabs' ).index(tab);
    $("#jobTabs").tabs('select', index);
    
    // 탭 추가 팝업에서 Enter 제한
    $("form").bind("keypress", function(e) {
        if(e.keyCode == 13) return false;
  	});
});

function groupSplit() {
	groups = groups.slice(1);
	groups = groups.slice(0, -1);
	groups = groups.split(',');
	
	unGroups = unGroups.slice(1);
	unGroups = unGroups.slice(0, -1);
	unGroups = unGroups.split(',');
}

function addTab() {
	//add Tab 영역 화면 Service
	var groupName = trim(replacePath($("#tab_title").val()));
	var isDupGroup = false;
	
	$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=groupService.existGroup(groupName)&viewName=jsonView'/>",
	       {
			groupName : groupName
	       }, function(data){
	    	   
	    	   isDupGroup = data.autoData;
	    	   
	    		if(isDupGroup) {
	    			alert('<spring:message code="job.alert.dulplicategroupname"/>');
	    			return;
	    		}
	    		var checkeds = new Array();
	    		var unCheckeds = new Array();
	    		var checkBoxs = $("#jobList [name=checkbox]");
	    		for(var i=0; i<checkBoxs.length; i++){
	    			if(checkBoxs[i].checked) {
	    				checkeds.push(checkBoxs[i].value);
	    			} else {
	    				unCheckeds.push(checkBoxs[i].value);
	    			}
	    		}
	    		
	    		if(checkeds.length == 0) {
	    			alert('<spring:message code="job.alert.selectitem"/>');
	    			return;
	    		}
	    		
	    		if(unCheckeds.length == 0) {
	    			unCheckeds.push("");
	    		}
	    		
	    		var tabTitle = $( "#tab_title" );
	    		if(tabTitle.val() == "" || tabTitle.val() == null) {
	    			alert('<spring:message code="job.alert.entertitle"/>');
	    			return;
	    		}
	    		
	    	 	//add Tab 영역 화면
	    	 	$("#jobTabs").tabs("remove", tabCount-1);
	    		//var tabTitle = $( "#tab_title" ),
	    	    tabContent = $( "#tab_content" );
	    	    tabTemplate = "<li class='ui-corner-top'><a href='<%='#'%>{href}'><%='#'%>{label}</a><span class='ui-icon ui-icon-gear' role='presentation' style='float:left; margin-right:-3px'></span><span class='ui-icon ui-icon-close' role='presentation'></span></li>";
	    		var tabs = $( "#jobTabs" ).tabs(),
	    	    label = tabTitle.val() || "Tab " + tabCount,
	    	    id = "tabs-" + tabCount,
	    	    li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	    	    tabContentHtml = tabContent.val() || "Tab " + tabCount + " content.";

	    	    tabs.find( ".ui-tabs-nav" ).append( li );
	    	    tabs.append( "<div id='" + id + "' style='display:none;' ><p></p></div>" );
	    	    
	    	    $("#jobTabs").tabs("add", "#c", "+");
	    	    $("#jobTabs").tabs("refresh");
	    	    $("#jobTabs").tabs('select', tabCount-1);
	    	    tabCount++;
	    	    
	    		$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=groupService.createGroupByJob(groupName,checkeds,unCheckeds)&viewName=jsonView'/>",
	    			{
	    			groupName  : groupName,
	    			checkeds   : checkeds,
	    			unCheckeds : unCheckeds
	    		
	    			}, function(data) {
	    			fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', 'ALL');
	    		}); 
	       });
	
}

function updateTab() {
	var groupName = trim(replacePath($("#tab_title").val()));
	var checkeds = new Array();
	var unCheckeds = new Array();
	var checkBoxs = $("#jobList [name=checkbox]");
	for(var i=0; i<checkBoxs.length; i++){
		if(checkBoxs[i].checked) {
			checkeds.push(checkBoxs[i].value);
		} else {
			unCheckeds.push(checkBoxs[i].value);
		}
	}
	
	if(checkeds.length == 0) {
		alert('<spring:message code="job.alert.selectitem"/>');
		return;
	}
	
	if(unCheckeds.length == 0) {
		unCheckeds.push("");
	}
	
	var result = confirm('<spring:message code="job.confirm.update"/>');
	if(result) {
		$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=groupService.createGroupByJob(groupName,checkeds,unCheckeds)&viewName=jsonView'/>",
			{
			groupName  : groupName,
			checkeds   : checkeds,
			unCheckeds : unCheckeds
		
			}, function(data) {
			fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', 'ALL');
		}); 
	}
}
  
function cleanDeploy(job){
	var rowArray = new Array();
	rowArray[0] = "."+"@oden@"+"."+"@oden@"+".";
	
	if(confirm('<spring:message code="job.confirm.cleandeploy"/>')){
		$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.run(items,opt,job,page,cmd,user)&viewName=jsonView'/>",
		       {
	       		items : rowArray,
	       		opt : "id",
	       		job : job,
	       		page : '0',
	       		cmd: "",
	       		user: '<%=userid%>'
	       		}, function(data) {
	     });
		jQuery("#grid_job").trigger("reloadGrid");
	}
}

function delJob(jobName){
	var allIDs = $('#grid_job').jqGrid('getDataIDs');
	var result = '';
	if(allIDs.length == 1) {
		result = confirm('<spring:message code="job.confirm.deletejobandgroup"/>');
		currentSelectedTab = 'ALL';
	} else {
		result = confirm('<spring:message code="job.confirm.deletejob"/>');
	}
	
	if(result){
	     $.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.remove(id)&viewName=jsonView'/>",
			       {id : jobName}, function(data) {
			    	   fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', currentSelectedTab);		    	  
		     });
	}
}

function runBuild(buildName) {
	//해당 bulidJob 이미지 변경
	$grid = $('#grid_job');
	var ids = $grid.getGridParam('selrow');
	$grid.jqGrid("setCell", ids, 'mode', '<img src="images/ico_monitor_d.png" style="vertical-align:middle;"/>');
	$grid.jqGrid("setCell", ids, 'buildDate', '<img src="images/progress.gif" style="vertical-align:middle;"/>');
	
	//build를 돌지않는 Job에 대해 disable처리
	var allIDs = $grid.jqGrid('getDataIDs');
	for(var i=0; i<allIDs.length; i++) {
		var selRow = $grid.jqGrid("getCell", allIDs[i], 'mode');
		if(selRow.indexOf('images/ico_build') > -1 && selRow.indexOf('images/ico_build_d') < 0) {
			selRow = selRow.replace('images/ico_build', 'images/ico_build_d');
		}
		selRow = selRow.replace('images/ico_deploy', 'images/ico_deploy_d');
		selRow = selRow.replace('images/ico_celandeploy', 'images/ico_celandeploy_d');
		if(selRow.indexOf('images/ico_compare') > -1 && selRow.indexOf('images/ico_compare_d') < 0) {
			selRow = selRow.replace('images/ico_compare', 'images/ico_compare_d');
		}
		if(selRow.indexOf('images/ico_runscript') > -1 && selRow.indexOf('images/ico_runscript_d') < 0) {
			selRow = selRow.replace('images/ico_runscript', 'images/ico_runscript_d');
		}
		if (groupId == 'GRP-0001') {
				selRow = selRow.replace('images/ico_del', 'images/ico_del_d');
		}
		if(selRow.indexOf('images/ico_rollback') > -1 && selRow.indexOf('images/ico_rollback_d') < 0) {
			selRow = selRow.replace('images/ico_rollback', 'images/ico_rollback_d');
		}
		
		$grid.jqGrid("setCell", allIDs[i], 'mode', selRow);
	}
	
	
  	$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=buildService.runBuild(buildName)&viewName=jsonView'/>",
		{buildName : buildName}, function(data) {});
  	
  	jQuery("#grid_job").jqGrid("setGridParam", {
 		mtype : "POST",
 		postData : {
 			cmd : encodeURI("<%=roles%>"),
 			buildName : buildName,
 			group : currentSelectedTab
 			}
 	}).trigger("reloadGrid");
}

function stopDeployJob(txid){
	if(confirm('<spring:message code="job.confirm.stopjob"/>')){
		$.ajax( {
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.stop(id)&viewName=jsonView&id='/>"+txid ,
			dataType : "json",
			async : true,
			success : function(result) {
				setTimeout('fn_addTab("03job", "Job", "job", "&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups", currentSelectedTab)', 500);
			},
			error : function() {
			}
		});
	}
}

function rollbackJob(txid){
	if(confirm('<spring:message code="job.confirm.rollbackjob"/>')){
		$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.rollback(txid)&viewName=jsonView'/>",
		       {
	       		txid : txid
	       		}, function(data) {
	    });
	    jQuery("#grid_job").trigger("reloadGrid");
	} 
}

function startTimer() {    
	var now = new Date();    
	var minutes = now.getMinutes();    
	var seconds = now.getSeconds();    
	t = setTimeout('jQuery("#grid_job").jqGrid("setGridParam", {mtype : "POST",	postData : {cmd : encodeURI("<%=roles%>"), buildName : "", group : currentSelectedTab}}).trigger("reloadGrid");', 2000);
	t = setTimeout('jQuery("#grid_job").jqGrid("setGridParam", {mtype : "POST",	postData : {cmd : encodeURI("<%=roles%>"), buildName : "", group : currentSelectedTab}}).trigger("reloadGrid");', 5000);
}

function stopTimer(){
	t = setTimeout('jQuery("#grid_job").jqGrid("setGridParam", {mtype : "POST",	postData : {cmd : encodeURI("<%=roles%>"), buildName : "", group : currentSelectedTab}}).trigger("reloadGrid");', 5000);
	clearTimeout(t);
}
function popupOpen(popURL){
	var cw=screen.availWidth;     //화면 넓이
	var ch=screen.availHeight;    //화면 높이
	
	var sw=500;    //띄울 창의 넓이
	var sh=500;    //띄울 창의 높이
	
	var ml=(cw-sw)/2;        //가운데 띄우기위한 창의 x위치
	var mt=(ch-sh)/2;         //가운데 띄우기위한 창의 y위치
	var popOption = "resizable=no, scrollbars=yes, status=no width="+sw+",height="+sh+",top="+mt+",left="+ml;    //팝업창 옵션(optoin)
		window.open(popURL,"",popOption);
}
</script>
<div id="jobTabs">
	<ul>
		<li><a href="#a">ALL</a></li>
		<li><a href="#c">+</a></li>
	</ul>
	<div id="a" style="display: none;">blank</div>
	<div id="c" style="display: none;">blank</div>
</div>

<div id="a">
	<div id="button" style="padding-bottom: 5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div class="pageSubtitle" style="padding-top: 10px;">
						<h3 class="subtitle_h3">
							<spring:message code="job.page.subtitle" />
						</h3>
					</div>
					<!-- end pageSubtitle -->
				</td>
				<iam:access hasPermission="${iam:getPermissionMask(\"CREATE\")}"
					viewName="addUser">
					<td align=right style="padding-top: 10px;"><a name="addlink"
						href="#"><img src="<c:url value='/images/btn_job.gif'/>"
							alt="add" /></a></td>
				</iam:access>
			</tr>
		</table>
	</div>
	<div id="body_job">
		<form method="post" id="searchForm" name="searchForm">
			<div class="listbox" style="padding-top: 2px;">
				<table id="grid_job" class="scroll" cellpadding="0" cellspacing="0">
					<tr>
						<td />
					</tr>
				</table>
			</div>
			<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
			<div id="pager_job" class="scroll" style="text-align: center;"></div>
			<a id="getLink" name="getLink"></a>
		</form>
	</div>
	<div id="tabDialog" title="Add Tab">
	<form>
		<fieldset class="ui-helper-reset">
			<table summary="addBuild">
				<colgroup>
					<col width="10%">
					<col width="40%">
					<col width="10%">
					<col width="40%">
				</colgroup>
				<tbody>
					<tr>
						<th scope="row"><label for="tab_title"><spring:message code="job.tab.title" /></label></th>
						<td><input type="text" name="tab_title" id="tab_title"
							value="Tab Title" class="ui-widget-content ui-corner-all" maxLength='30'></td>
					</tr>
					<tr>
						<th scope="row"><label for="tab_title"><spring:message code="job.tab.jobs" /></label></th>
						<td id="jobList">
							<!--  <input type="checkbox" id="check_all" class="ui-widget-content ui-corner-all" style="vertical-align:middle;"><label> 전체선택</label><br>-->
						</td>
					</tr>
				</tbody>
			</table>
		</fieldset>
	</form>
	</div>
</div>
