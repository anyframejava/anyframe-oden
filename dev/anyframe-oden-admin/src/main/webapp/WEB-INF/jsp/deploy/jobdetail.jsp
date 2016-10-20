<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%  request.setCharacterEncoding("UTF-8");
	String jobName = (String)request.getParameter("para");
	String currentSelectedTab = (String)request.getParameter("para2");
	
	boolean isNew = false; 
	if(jobName.charAt(0)=='"'){
		isNew = true; 
		jobName = jobName.substring(1, jobName.length()-1);
	}
%>

<script type="text/javascript">
	var boolSourceReload = false;
	var grid_source_data;
	var grid_table_data;
	var grid_script_data;

	var lastselSources = '0';
	var lastselTarget = '0';
	var lastselCmd = '0';
	var groups = '${groupBuildJobs.groups}';
	var buildJobs = '${groupBuildJobs.buildJobs}';
	var currentSelectedTab = '<%=currentSelectedTab%>';
	var currentJobTab = 'ALL';
	jQuery(document).ready(function(){
		getData();

		drawGrid();

		$(".btn-slide").click(function(){
			$("#panel").slideToggle("slow");
			$(this).toggleClass("active"); return false;
		});
		
		if(currentSelectedTab == 'ALL') {
			jQuery("#job_group").val(currentJobTab).attr("selected", "selected");
		}
		
		if ($('#build_job').val() != '' && $('#build_job').val() != null && $('#build_job').val() != 'None') {
			var arr = document.getElementById('buildJob_table');
				arr.style.display ='block'; 
		}
	});
	
	function getData() {
		$.ajax({
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findByName(id)&viewName=jsonView'/>",
			async : false,
			data : {
				id : "<%=jobName%>"
			},
			success : function(data) {
				
				jQuery("#job_name").val(data.autoData.name);
				jQuery("#repository").val(data.autoData.repo);
				jQuery("#excludes").val(data.autoData.excludes);
				currentJobTab = data.autoData.group;
				jQuery("#job_group").val(data.autoData.group).attr("selected", "selected");
				if(data.autoData.build != null){
					jQuery("#build_job").val(data.autoData.build).attr("selected", "selected");
				}
			} 
		});
	}

	function drawGrid() {
		drawSources();
		drawTargets();
		drawCommands();
	}

	function drawSources(){
		lastselSources = '0';
		var sourceGridSelectEdit = true;
		//var param = encodeURI('<%=jobName%>');
		
		grid_source_data = jQuery("#grid_sourcedetail").jqGrid( {

			mtype : 'GET',
			datatype : "json",
			colNames : [ '<spring:message code="jobdetail.grid.dir"/>',
			 			 '<spring:message code="jobdetail.grid.mapping"/>', 
			 			 '<spring:message code="jobdetail.grid.action"/>',
			 			 'hiddenname'],
			jsonReader : {
				repeatitems : false
			},
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.loadMappings(cmd)&viewName=jsonView&cmd='/>"+ encodeURI('<%=jobName%>'),
			colModel : [ {
				name : 'dir',
				index : 'dir',
				align : 'center',
				width : 200,
				sortable : false,
				editable:true
			}, {
				name : 'checkout',
				index : 'checkout',
				align : 'left',
				width : 500,
				sortable : false,
				editable:true
			}, {
				name : 'hidden',
				index : 'hidden',
				align : 'center',
				width : 50,
				sortable : false
			}, {
				key : true,
				name : 'hiddenname',
				hidden : true
			}],
			width : 875,
			height : 55,
			scroll : false,
			pgbuttons: false,
		   	pgtext: false,
		   	pginput:false,
			forceFit : true,
			viewrecords : true,
			rowNum : -1,
			sortable : false,
			editurl: "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.loadMappings(cmd)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>'),
			loadError : function(xhr, st, err) {
				alert('<spring:message code="jobdetail.mappingload.error"/>');
			},

			onSelectRow: function(id) {
				if(id && id!==lastselSources){
					jQuery('#grid_sourcedetail').jqGrid('saveRow',lastselSources);
					jQuery('#grid_sourcedetail').jqGrid('editRow',id,true);
					lastselSources=id;
					sourceGridSelectEdit = true;
				}else{
					if(sourceGridSelectEdit){
						jQuery('#grid_sourcedetail').jqGrid('saveRow',lastselSources);
						lastselSources=id;
						sourceGridSelectEdit = false;
					}else{
						jQuery('#grid_sourcedetail').jqGrid('editRow',id,true);
						lastselSources=id;
						sourceGridSelectEdit = true;
					}
				}
			},
			loadComplete: function(data) { 
				if(boolSourceReload){
					alert('<spring:message code="jobdetail.alert.mappingcomplete"/>');
					boolSourceReload = false;	
				}else{
					boolSourceReload = false;	
				}
		    } 
		});
		jQuery("#grid_sourcedetail").jqGrid('navGrid','#pager_sourcedetail',{edit:false,del:false,add:false,search:false});
	}

	function drawTargets(){
		lastselTarget = '0';
		var targetGridSelectEdit = true;
		
		grid_table_data = jQuery("#grid_jobdetail").jqGrid( {

			mtype : 'GET',
			datatype : "json",
			colNames : [ '<spring:message code="jobdetail.grid.status"/>',
						 '<spring:message code="jobdetail.grid.name"/>',
			 			 '<spring:message code="jobdetail.grid.url"/>', 
			 			 '<spring:message code="jobdetail.grid.path"/>', 
			 			 '<spring:message code="jobdetail.grid.action"/>',
			 			 'hiddenname'
			 			 ],
			jsonReader : {
				repeatitems : false
			},
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=serverService.findListByPk(cmd)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>'),
			colModel : [ {
				name : 'status',
				index : 'status',
				align : 'center',
				width : 70,
				sortable : false,
				editable:false
			},{
				name : 'name',
				index : 'name',
				align : 'center',
				width : 100,
				sortable : false,
				editable:true
			}, {
				name : 'url',
				index : 'url',
				align : 'center',
				width : 150,
				sortable : false,
				editable:true
			}, {
				name : 'path',
				index : 'path',
				align : 'left',
				width : 300,
				sortable : false,
				editable:true
			}, {
				name : 'hidden',
				index : 'hidden',
				align : 'center',
				width : 50,
				sortable : false
			}, {
				key : true,
				name : 'hiddenname',
				hidden : true
			}],
			width : 888,
			height : 50,
			scroll : false,
			pgbuttons: false,
		   	pgtext: false,
		   	pginput:false,
			forceFit : true,
			viewrecords : true,
			rowNum : -1,
			sortable : true,
			editurl: "<c:url value='/simplejson.do?layout=jsonLayout&service=serverService.findListByPk(cmd)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>'),
			loadError : function(xhr, st, err) {
				alert('<spring:message code="jobdetail.load.error"/>');
			},

			onSelectRow: function(id) {
				if(id && id!==lastselTarget){
					jQuery('#grid_jobdetail').jqGrid('saveRow',lastselTarget);
					jQuery('#grid_jobdetail').jqGrid('editRow',id,true);
					lastselTarget=id;
					targetGridSelectEdit = true;
				}else{
					if(targetGridSelectEdit){
						jQuery('#grid_jobdetail').jqGrid('saveRow',lastselTarget);
						lastselTarget=id;
						targetGridSelectEdit = false;
					}else{
						jQuery('#grid_jobdetail').jqGrid('editRow',id,true);
						lastselTarget=id;
						targetGridSelectEdit = true;
					}
				}
			}
	
		});
		jQuery("#grid_jobdetail").jqGrid('navGrid','#pager_jobdetail',{edit:false,del:false,add:false,search:false});
	}

	function drawCommands(){
		lastselCmd = '0';
		var cmdGridSelectEdit = true;
		
		grid_script_data = jQuery("#grid_cmddetail").jqGrid( {

			mtype : 'GET',
			datatype : "json",
			colNames : [ '<spring:message code="jobdetail.grid.name"/>',
			 			 '<spring:message code="jobdetail.grid.path"/>', 
			 			 '<spring:message code="jobdetail.grid.script"/>', 
			 			 '<spring:message code="jobdetail.grid.action"/>',
			 			 'hiddenname'],
			jsonReader : {
				repeatitems : false
			},
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=scriptService.findListByPk(cmd,opt)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')+"&opt=del",
			colModel : [ {
				name : 'name',
				index : 'name',
				align : 'center',
				width : 100,
				key : true,
				sortable : false,
				editable:true
			}, {
				name : 'path',
				index : 'path',
				align : 'left',
				width : 400,
				sortable : false,
				editable:true
			}, {
				name : 'cmd',
				index : 'cmd',
				align : 'center',
				width : 150,
				sortable : false,
				editable:true
			}, {
				name : 'hidden',
				index : 'hidden',
				align : 'center',
				width : 50,
				sortable : false
			}, {
				key : true,
				name : 'hiddenname',
				hidden : true
			}],
			width : 888,
			height : 50,
			scroll : false,
			pgbuttons: false,
		   	pgtext: false,
		   	pginput:false,
			forceFit : true,
			viewrecords : true,
			rowNum : -1,
			sortable : false,
			editurl: "<c:url value='/simplejson.do?layout=jsonLayout&service=scriptService.findListByPk(cmd,opt)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')+"&opt=del",
			loadError : function(xhr, st, err) {
				alert('<spring:message code="jobdetail.scriptload.error"/>');
			},

			onSelectRow: function(id) {
				if(id && id!==lastselCmd){
					jQuery('#grid_cmddetail').jqGrid('saveRow',lastselCmd);
					jQuery('#grid_cmddetail').jqGrid('editRow',id,true);
					lastselCmd=id;
					cmdGridSelectEdit = true;
				}else{
					if(cmdGridSelectEdit){
						jQuery('#grid_cmddetail').jqGrid('saveRow',lastselCmd);
						lastselCmd=id;
						cmdGridSelectEdit = false;
					}else{
						jQuery('#grid_cmddetail').jqGrid('editRow',id,true);
						lastselCmd=id;
						cmdGridSelectEdit = true;
					}
				}
			}
		});
		jQuery("#grid_cmddetail").jqGrid('navGrid','#pager_cmddetail',{edit:false,del:false,add:false,search:false});
	}

	function addSource(){
		var num = parseInt(jQuery("#grid_sourcedetail").getGridParam("records"));
		var paraDir = trim($("#source_dir").val());
		var paraCheckout = trim($("#source_checkout").val());
		
		paraDir = replacePath(paraDir);
		paraCheckout = replacePath(paraCheckout);
		 
		var key = paraDir + "@oden@" + paraCheckout;
		var paraHidden = "<a href=\"javascript:delSource('"+key+"');\">"+"<img src=\"<c:url value='/images/ico_del.gif'/>\"/>"+"</a>";
		var datarow = {dir:paraDir,checkout:paraCheckout,hidden:paraHidden,hiddenname:key};
		if(paraDir == null || paraDir.length == 0 || paraCheckout == null || paraCheckout.length == 0){
			alert('<spring:message code="jobdetail.alert.insertmapping"/>');
		}else{
			if(isValidString(paraDir) || isValidString(paraCheckout)){
				alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
			}else{
				if(idOfSource(key) == ""){
					var su=$("#grid_sourcedetail").jqGrid('addRowData',num+1,datarow);
					if(su){
						$("#source_dir").val('');
						$("#source_checkout").val('');
					} else 
						alert('<spring:message code="jobdetail.alert.insertfail"/>');	
				}else{
					$("#source_dir").val('');
					$("#source_checkout").val('');
					alert('duplicate');
				}
			}
		}
	}
	
	function delSource(source){
		if(confirm('<spring:message code="jobdetail.confirm.deletesource"/>')){
			var id = idOfSource(source);
			var su=jQuery("#grid_sourcedetail").jqGrid('delRowData',id);
			if(su) {} else alert('<spring:message code="jobdetail.alert.inlist"/>');
		}
	}
	
	function addServer(){
		var num = parseInt(jQuery("#grid_jobdetail").getGridParam("records"));
		var paraName = trim($("#server_name").val());
		var paraUrl = trim($("#server_url").val());
		var paraPath = trim($("#server_path").val());

		paraName = replacePath(paraName);
		paraUrl = replacePath(paraUrl);
		paraPath = replacePath(paraPath);
		
		var paraHidden = "<a href=\"javascript:delServer('"+paraName+"');\">"+"<img src=\"<c:url value='/images/ico_del.gif'/>\"/>"+"</a>";
		var datarow = {name:paraName,url:paraUrl,path:paraPath,hidden:paraHidden,hiddenname:paraName};
		if(paraName == null || paraName.length == 0 || paraUrl == null || paraUrl.length == 0 || paraPath == null || paraPath.length == 0){
			alert('<spring:message code="jobdetail.alert.inserttarget"/>');
		}else{
			if(isValidStringNames(paraName) || isValidString(paraUrl) || isValidString(paraPath)){
				alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
			}else{
				if(idOfServer(paraName) == ""){
					var su=$("#grid_jobdetail").jqGrid('addRowData',num+1,datarow);
					if(su){
						$("#server_name").val('');
						$("#server_url").val('');
						$("#server_path").val('');
					} else 
						alert('<spring:message code="jobdetail.alert.insertfail"/>');	
				}else{
					$("#server_name").val('');
					$("#server_url").val('');
					$("#server_path").val('');
					alert('<spring:message code="jobdetail.alert.duplicate"/>');
				}
			}
		}
	}
	
	function delServer(server){
		if(confirm('<spring:message code="jobdetail.confirm.deletetarget"/>')){
			var id = idOfServer(server);
			var su=jQuery("#grid_jobdetail").jqGrid('delRowData',id);
			if(su) {} else alert('<spring:message code="jobdetail.alert.inlist"/>');
		}
	}

	function addScript(){
		var num = parseInt(jQuery("#grid_cmddetail").getGridParam("records"));
		var paraName = trim($("#script_name").val());
		var paraPath = trim($("#script_path").val());
		var paraCmd = trim($("#script_script").val());

		paraName = replacePath(paraName);
		paraPath = replacePath(paraPath);
		paraCmd = replacePath(paraCmd);
		
		var paraHidden = "<a href=\"javascript:delScript('"+paraName+"');\">"+"<img src=\"<c:url value='/images/ico_del.gif'/>\"/>"+"</a>";
		var datarow = {name:paraName,path:paraPath,cmd:paraCmd,hidden:paraHidden,hiddenname:paraName};
		if(paraName == null || paraName.length == 0 || paraCmd == null || paraCmd.length == 0 || paraPath == null || paraPath.length == 0){
			alert('<spring:message code="jobdetail.alert.insertscript"/>');
		}else{
			if(isValidStringNames(paraName) || isValidString(paraPath) || isValidString(paraCmd)){
				alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
			}else{
				if(idOfScript(paraName) == ""){
					var su=$("#grid_cmddetail").jqGrid('addRowData',num+1,datarow);
					if(su){
						$("#script_name").val('');
						$("#script_path").val('');
						$("#script_script").val('');
					} else 
						alert('<spring:message code="jobdetail.alert.insertfail"/>');	
				}else{
					$("#script_name").val('');
					$("#script_path").val('');
					$("#script_script").val('');
					alert('<spring:message code="jobdetail.alert.scriptduplicate"/>');
				}
			}
		}
	}
	
	function delScript(script){
		if(confirm('<spring:message code="jobdetail.confirm.deletecmd"/>')){
			var id = idOfScript(script);
			var su=jQuery("#grid_cmddetail").jqGrid('delRowData',id);
			if(su) {} else alert('<spring:message code="jobdetail.alert.inlist"/>');
		}
	}

	function idOfSource(source){
		var same = "";
		var row = new String(jQuery("#grid_sourcedetail").getDataIDs());

		if(row==""){
			same = "";
		}else{
			rowNumList = row.split(",");
			var rowArray = new Array();
			
			for(var i = 0 ; i < rowNumList.length ; i++){
				var rowData = jQuery("#grid_sourcedetail").getRowData(rowNumList[i]);
				rowArray[i] = rowData.hiddenname;
				if(source == rowArray[i]){
					same = rowNumList[i];
					break;
				}else{
				}
			}
		}
		return same;
	}
	
	function idOfServer(selectPara){
		var same = "";
		var row = new String(jQuery("#grid_jobdetail").getDataIDs());

		if(row==""){
			same = "";
		}else{
			rowNumList = row.split(",");
			var rowArray = new Array();
			
			for(var i = 0 ; i < rowNumList.length ; i++){
				var rowData = jQuery("#grid_jobdetail").getRowData(rowNumList[i]);
				rowArray[i] = rowData.hiddenname;
				if(selectPara == rowArray[i]){
					same = rowNumList[i];
					break;
				}else{
				}
			}
		}
		return same;
	}

	function idOfScript(selectPara){
		var same = "";
		var row = new String(jQuery("#grid_cmddetail").getDataIDs());
		if(row==""){
			same = "";
		}else{
			rowNumList = row.split(",");
			var rowArray = new Array();
	
			for(var i = 0 ; i < rowNumList.length ; i++){
				var rowData = jQuery("#grid_cmddetail").getRowData(rowNumList[i]);
				rowArray[i] = rowData.hiddenname;
				if(selectPara == rowArray[i]){
					same = rowNumList[i];
					break;
				}else{
				}
			}
		}
		return same;
	}

	function saveJobAction(){
		if(confirm('<spring:message code="jobdetail.confirm.save"/>')){
			jQuery('#grid_sourcedetail').jqGrid('saveRow',lastselSources);
			jQuery('#grid_jobdetail').jqGrid('saveRow',lastselTarget);
			jQuery('#grid_cmddetail').jqGrid('saveRow',lastselCmd);

			setTimeout(saveJob, 500);
		}
	}
	
	function saveJob(){
		var rowMapping = new String(jQuery("#grid_sourcedetail").getDataIDs());
		rowMappingNumList = rowMapping.split(",");
		var rowMappingArray = new Array();
		
		var rowTarget = new String(jQuery("#grid_jobdetail").getDataIDs());
		rowTargetNumList = rowTarget.split(",");
		var rowTargetArray = new Array();

		var rowCmd = new String(jQuery("#grid_cmddetail").getDataIDs());
		rowCmdNumList = rowCmd.split(",");
		var rowCmdArray = new Array();

		if($("#job_name").val()==""){
			alert('<spring:message code="jobdetail.alert.nameempty"/>');
		}else if($("#repository").val()==""){
			alert('<spring:message code="jobdetail.alert.directoryempty"/>');
		}else if(rowTargetNumList==""){
			alert('<spring:message code="jobdetail.alert.targetempty"/>');
		}else{
			for(var i = 0 ; i < rowTargetNumList.length ; i++){
				var rowTargetData = jQuery("#grid_jobdetail").getRowData(rowTargetNumList[i]);
				if(isValidStringNames(rowTargetData.name)) {
					alert('<spring:message code="jobdetail.alert.invalidcharacter"/>' + rowTargetData.name);
					return;
				} else {
					rowTargetArray[i] = rowTargetData.name+"@oden@"+rowTargetData.url+"@oden@"+rowTargetData.path;
				}
			}

			if(rowMappingNumList!=""){
				for(var i = 0 ; i < rowMappingNumList.length ; i++){
					var rowMappingData = jQuery("#grid_sourcedetail").getRowData(rowMappingNumList[i]);
					rowMappingArray[i] = rowMappingData.dir+"@oden@"+rowMappingData.checkout;
				}
			}else{
				rowMappingArray[0] = "."+"@oden@"+".";
			}
			
			if(rowCmdNumList!=""){
				for(var i = 0 ; i < rowCmdNumList.length ; i++){
					var rowCmdData = jQuery("#grid_cmddetail").getRowData(rowCmdNumList[i]);
					rowCmdArray[i] = rowCmdData.name+"@oden@"+rowCmdData.path+"@oden@"+rowCmdData.cmd;
				}
			}else{
				rowCmdArray[0] = "."+"@oden@"+"."+"@oden@"+".";
			}

			var job_n = replacePath($("#job_name").val());
			var job_r = replacePath($("#repository").val());
			var job_e = replacePath($("#excludes").val());
			
			var group_n = replacePath($("#job_group").val());
			if(group_n == 'All') {
				group_n = '';
			}
			var build_n = replacePath($("#build_job").val());
			

			if(isValidStringJobName(job_n) || isValidString(job_r) || isValidString(job_e)){
				alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
			}else{
				var isDupJob = false;
				$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.existJob(jobName)&viewName=jsonView'/>",
					       {
							jobName : job_n
					       }, function(data){
					    	   isDupJob = data.autoData;
					    	   
					    	   <%if(isNew){ %>
								if(isDupJob == true || isDupJob == 'true'){
									alert('<spring:message code="jobdetail.alert.dulplicatejobname"/>');
								}else{
									$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.insert(list,cmd,mapping,jobname,repo,excludes,groupName,build)&viewName=jsonView'/>",
									       {
									       list : rowTargetArray,
									       cmd : rowCmdArray,
									       mapping : rowMappingArray,
									       jobname : job_n,
									       repo : job_r,
									       excludes :  job_e,
									       groupName : group_n,
									       build : build_n
									       }, function(data) {
									    	   fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', currentSelectedTab);   
								     });
								}
								<%}else{%>
								//alert("Save Start!");
								//alert("group_n : "+group_n);
								$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.update(list,cmd,mapping,jobname,repo,excludes,groupName,build)&viewName=jsonView'/>",
									       {
									       list : rowTargetArray,
									       cmd : rowCmdArray,
									       mapping : rowMappingArray,
									       jobname : job_n,
									       repo : job_r,
									       excludes :  job_e,
									       groupName : group_n,
									       build : build_n
									       }, function(data) {
									    	   fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', currentSelectedTab);   
								     });
								<%}%>
				});
				
				
			}
		}
	}
	
	function cancelJob(){
		fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', currentSelectedTab);   
	}

	function hiddenMappings(){
		var arr = document.getElementById('mapping_table');
		if ( arr!= null ) { 
			arr.style.display = (arr.style.display == 'none')? 'block':'none'; 
		} 
	}

	function hiddenCommands(){
		var arr = document.getElementById('commands_table');
		if ( arr!= null ) { 
			arr.style.display = (arr.style.display == 'none')? 'block':'none'; 
		} 
	}
	function hiddenBuildJob(){
		var arr = document.getElementById('buildJob_table');
		if ( arr!= null ) { 
			arr.style.display = (arr.style.display == 'none')? 'block':'none'; 
		} 
	}
	
	function getAutoMappings(){
		if(confirm('<spring:message code="jobdetail.confirm.loadmapping"/>')){
			boolSourceReload = true;	
			jQuery("#grid_sourcedetail")
			.jqGrid(
					'setGridParam',
					{
						url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findMappings(cmd)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')
					}).trigger("reloadGrid");
		}
	}

	function isValidStringJobName(str){

		var strArray = new Array("\"","&","<",">","!","`","@","%","^","&","*",":",";","\'","~","$","+","=","|","{","}",",",".","/","?");
		
		for(var i=0; i<strArray.length; i++){
			if(str.indexOf(strArray[i]) != -1){
				return true;
			}else{
			}
		}
		return false;
	}

	function isValidStringNames(str){

		var strArray = new Array("\"","&","<",">","!","`","@","#","%","^","&",";","\'"," ");
		
		for(var i=0; i<strArray.length; i++){
			if(str.indexOf(strArray[i]) != -1){
				return true;
			}else{
			}
		}
		return false;
	}
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><spring:message code="jobdetail.page.subtitle"/></h3>
</div><!-- end pageSubtitle --> 
<div id="body_jobdetail">
<form method="post" id="searchForm" name="searchForm">
<!--START: input table-->
<fieldset>
<legend>register</legend>
<div class="input_table source_table" style="padding-top:2px;">
	<table summary="job">
		<caption>job register</caption>
		<colgroup>
			<col width="10%">
			<col width="40%">
			<col width="10%">
			<col width="40%">
		</colgroup>
		<tbody>
			<tr>
				<th scope="row"><label for="JobName"><spring:message code="jobdetail.label.jobname"/></label></th>
				
				<%if(isNew){ %>
				<td><input type="text" id="job_name" name="job_name" size="40" value="" style="height:18px;width:300px;" /></td>
				<simpleweb:validate id="job_name" required="true" promptMessage="Enter Job Name" /> 
				<%}else{%>
				<td><input type="text" id="job_name" name="job_name" disabled="disabled" size="40" value="" style="height:18px;width:300px;" /></td>
				<%}%>
				<th scope="row"><label for="GroupName"><spring:message code="jobdetail.label.group" text="Group Name :"/></label></th>
				<td>
					<select id="job_group" name="job_group"	class="selectbox" style="height:18px;width:150px;">
						<option value="All" selected="selected">All</option>
					<c:forEach var="jobGroup" items="${groupBuildJobs.groups}" varStatus="status">
						<option value="${jobGroup}"><c:out value="${jobGroup}"></c:out></option>
					</c:forEach>
					</select>	
				</td>
			</tr>
		</tbody>
	</table>
</div>
</fieldset>	
<!--END: input table-->

<!--START: input table_source info-->
<div class="pageSubtitle" style="padding-top:10px;">
	<h4 class="subtitle_h4"><spring:message code="jobdetail.source.subtitle"/></h4>
</div><!-- end pageSubtitle --> 
<!--START: input table-->
<fieldset>
<legend>register</legend>
<div class="input_table source_table" style="padding-top:2px;">
	<table summary="job">
		<caption>source</caption>
		<colgroup>
			<col width="10%">
			<col width="40%">
			<col width="10%">
			<col width="40%">
		</colgroup>
		<tbody>
			<tr>
				<th scope="row"><label for="Repository"><spring:message code="jobdetail.label.directory"/></label></th>
				<td><input type="text" id="repository" name="repository" size="40" value="" style="height:18px;width:300px;" /></td>
				<simpleweb:validate id="repository" required="true" promptMessage="Enter Directory. ex> C:/anyframe/target" />
				<th scope="row"><label for="Excludes"><spring:message code="jobdetail.label.exclude"/></label></th>
				<td><input type="text" id="excludes" name="excludes" size="40" value="" style="height:18px;width:300px;" /></td>
			</tr>
			<tr>
				<th scope="row"><label for="Mappings"><a href="javascript:hiddenMappings();"><spring:message code="jobdetail.mappings.subtitle"/> <img src="<c:url value='/images/ico_down.gif'/>" style='vertical-align:middle;'/></a></label></th>
				<td colspan="3"></td>
			</tr>
			<tr>
				<td colspan="4">
					<div id="mapping_table" style="display:none;">
						<div class="listbox">
							<table style="width:875px;">
									<tr>
										<td width="750"></td>
										<td><a href="javascript:getAutoMappings();"><img src="<c:url value='/images/ico_mapping.png'/>" width="18" height="18" alt="<spring:message code="jobdetail.label.automapping"/>" style='vertical-align:middle;'/><spring:message code="jobdetail.label.automapping"/> </a></td>
									</tr>
							</table>
						</div>
						<div class="listbox sourcebox" style="padding-top:0px;">
							<table id="grid_sourcedetail" class="scroll" cellpadding="0" cellspacing="0"><tr><td /></tr></table>
							<div id="pager_sourcedetail" class="scroll" style="text-align: center;"></div>
						</div>	
						<fieldset>
						<legend>add</legend>
						<div class="input_table_g source_table_g" style="padding-top:5px;">
							<table summary="source">
								<caption>add</caption>
								<tbody>
									<tr>
										<th scope="row"><label for="dir"><spring:message code="jobdetail.label.dir"/></label></th>
										<td><input type="text" id="source_dir" name="source_dir" value="" style="height:18px; width:200px;" /></td>
										<simpleweb:validate id="source_dir" promptMessage="ex> WEB-INF/classes" />
										<th scope="row"><label for="mapping"><spring:message code="jobdetail.label.mapping"/></label></th>
										<td><input type="text" id="source_checkout" name="source_checkout" value="" style="height:18px; width:250px;" /></td>
										<simpleweb:validate id="source_checkout" promptMessage="ex> C:/anyframe/src/main/java" />
										<td><a class="btn3"><span onclick="javascript:addSource();" style="align:right; vertical-align:middle; height:18px;">Add</span></a></td>
									</tr>
								</tbody>
							</table>
						</div>
						</fieldset>
					</div>	
				</td>
			</tr>
		</tbody>
	</table>
</div>
</fieldset>	
<!--END: input table-->
<!--END: input table_source info-->	

<!--START: input table_target info-->
<div class="pageSubtitle" style="padding-top:10px;">
	<h4 class="subtitle_h4"><spring:message code="jobdetail.target.subtitle"/></h4>
</div><!-- end pageSubtitle --> 
<div class="listbox" style="padding-top:5px;">
	<table id="grid_jobdetail" class="scroll" cellpadding="0" cellspacing="0"><tr><td /></tr></table>
	<div id="pager_jobdetail" class="scroll" style="text-align: center;"></div>
</div>	
<fieldset>
<legend>add</legend>
<div class="input_table_g" style="padding-top:5px;">
	<table summary="job">
		<caption>add</caption>
		<tbody>
			<tr>
				<th scope="row"><label for="Name"><spring:message code="jobdetail.label.name"/></label></th>
				<td><input type="text" id="server_name" name="server_name" value="" style="height:18px; width:130px;" /></td>
				<simpleweb:validate id="server_name" required="true" promptMessage="Enter target name."/>
				<th scope="row"><label for="Url"><spring:message code="jobdetail.label.url"/></label></th>
				<td><input type="text" id="server_url" name="server_url" value="" style="height:18px; width:160px;" /></td>
				<simpleweb:validate id="server_url" required="true" promptMessage="ex> 127.0.0.1:9872" />
				<th scope="row"><label for="Path"><spring:message code="jobdetail.label.path"/></label></th>
				<td><input type="text" id="server_path" name="server_path" value="" style="height:18px; width:280px; vertical-align:middle;"/></td>
				<simpleweb:validate id="server_path" required="true" promptMessage="ex> D:/anyframe/oden/deploy/target<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deploy/target" />
				<td><a class="btn3"><span onclick="javascript:addServer();">Add</span></a></td>
			</tr>
		</tbody>
	</table>
</div>
</fieldset>	
<!--END: input table_target info-->	

<!--START: input table_command info-->
<div class="pageSubtitle" style="padding-top:10px;">
	<h4 class="subtitle_h4"><a href="javascript:hiddenCommands();"><spring:message code="jobdetail.command.subtitle"/> <img src='<c:url value='/images/ico_down.gif'/>' style='vertical-align:middle;'/></a></h4>
</div><!-- end pageSubtitle --> 
<div id="commands_table" style="display:none;">
<div class="listbox" style="padding-top:5px;">
	<table id="grid_cmddetail" class="scroll" cellpadding="0" cellspacing="0"><tr><td /></tr></table>
	<div id="pager_cmddetail" class="scroll" style="text-align: center;"></div>
</div>		
<fieldset>
<legend>add</legend>
<div class="input_table_g" style="padding-top:5px;">
	<table summary="command">
		<caption>add</caption>
		<tbody>
			<tr>
				<th scope="row"><label for="Name"><spring:message code="jobdetail.label.name"/></label></th>
				<td><input type="text" id="script_name" name="script_name" value="" style="height:18px; width:130px;" /></td>
				<simpleweb:validate id="script_name" promptMessage="Enter command name." />
				<th scope="row"><label for="Path"><spring:message code="jobdetail.label.path"/></label></th>
				<td><input type="text" id="script_path" name="script_path" value="" style="height:18px; width:280px;" /></td>
				<simpleweb:validate id="script_path" promptMessage="ex> C:/util/tomcat/bin" />
				<th scope="row"><label for="Script"><spring:message code="jobdetail.label.script"/></label></th>
				<td><input type="text" id="script_script" name="script_script" value="" style="height:18px; width:130px;"/></td>
				<simpleweb:validate id="script_script" promptMessage="ex> startup.bat" />
				<td><a class="btn3"><span onclick="javascript:addScript();" style="align:right; vertical-align:middle; height:18px;">Add</span></a></td>
			</tr>
		</tbody>
	</table>
</div>
</fieldset>
</div>	
<!--END: input table_command info-->
<!--START: build table-->
<div class="pageSubtitle" style="padding-top:10px;">
	<h4 class="subtitle_h4"><a href="javascript:hiddenBuildJob();"><spring:message code="jobdetail.build.subtitle" text="Build"/> <img src='<c:url value='/images/ico_down.gif'/>' style='vertical-align:middle;'/></a></h4>
</div><!-- end pageSubtitle --> 
<div id="buildJob_table" style="display:none;">
	<fieldset>
	<legend>register</legend>
	<div class="input_table" style="padding-top:2px;">
		<table summary="job">
			<caption>source</caption>
			<colgroup>
				<col width="10%">
				<col width="40%">
				<col width="10%">
				<col width="40%">
			</colgroup>
			<tbody>
				<tr>
					<th scope="row"><label for="Build"><spring:message code="jobdetail.label.buildname" text="Job Name"/></label></th>
					<td>
						<select id="build_job" name="build_job"	class="selectbox" style="height:18px;width:150px;">
							<option value="" selected="selected">None</option>
						<c:forEach var="buildJob" items="${groupBuildJobs.buildJobs}" varStatus="status">
							<option value="${buildJob}"><c:out value="${buildJob}"></c:out></option>
						</c:forEach>
					</td>
					<th>
					</th>
					<td>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	</fieldset>	
</div>
<!--END: build table-->
<div id="button" style="padding-top:5px;">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align=right><a href="javascript:saveJobAction();"><img src="<c:url value='/images/btn_save.gif'/>" width="55" height="22" alt="save" /></a> <a href="javascript:cancelJob();"><img src="<c:url value='images/btn_cancel.gif'/>" width="69" height="22" alt="cancel" /></a></td>
	</tr>
</table>
</div>	
</form></div>