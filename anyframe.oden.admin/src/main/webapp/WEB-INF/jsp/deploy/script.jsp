<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<% String jobName = (String)request.getParameter("para"); %>

<script type="text/javascript">

jQuery(document).ready(function() {

	jQuery("#grid_script1").jqGrid(	{
		url : "<c:url value='/simplejson.do?layout=jsonLayout&service=serverService.findListByPk(cmd)&viewName=jsonView&cmd='/>"+'<%=jobName%>',
		mtype : 'POST',
		datatype : "json",
		colNames : [ '<anyframe:message code="script.grid.target"/>'],
		jsonReader : {
			repeatitems : false
		},
		colModel : [ {
			name : 'name',
			index : 'name',
			align : 'center',
			width : 150,
			sortable : false
		} ],
		width : 250,
		height : 200,
		scroll : false,
		multiselect : true,
		viewrecords : false,
		sortable : false,
		pgbuttons: false,
	   	pgtext: false,
	   	pginput:false,
		rowNum : -1,
		pager : jQuery('#pager_script1'),

		loadError : function(xhr, st, err) {
			alert('<anyframe:message code="script.load.error"/>');
		},

		gridComplete: function() { 
			selectAllTargets(); 
		} 
	});
	jQuery("#grid_script1").jqGrid('navGrid','#pager_script1',{edit:false,add:false,del:false,search:false});
	$("#grid_script1").closest(".ui-jqgrid-bdiv").css({ 'overflow-y' : 'scroll' });

	jQuery("#grid_script2").jqGrid(	{
		url : "<c:url value='/simplejson.do?layout=jsonLayout&service=scriptService.findListByPk(cmd,opt)&viewName=jsonView&cmd='/>"+'<%=jobName%>'+"&opt=run",
		mtype : 'POST',
		datatype : "json",
		colNames : [ '<anyframe:message code="script.grid.name"/>',
		     		 '<anyframe:message code="script.grid.path"/>',
		     		 '<anyframe:message code="script.grid.script"/>',
		     		 '<anyframe:message code="script.grid.task"/>'],
		jsonReader : {
			repeatitems : false
		},
		colModel : [ {
			name : 'name',
			index : 'name',
			align : 'center',
			width : 130,
			sortable : false
		},{
			name : 'path',
			index : 'path',
			align : 'left',
			width : 210,
			sortable : false
		},{
			name : 'cmd',
			index : 'cmd',
			align : 'center',
			width : 100,
			sortable : false
		},{
			name : 'hidden',
			index : 'hidden',
			align : 'center',
			width : 70,
			sortable : false
		} ],
		width : 590,
		height : 200,
		scroll : false,
		viewrecords : false,
		sortable : false,
		pgbuttons: false,
	   	pgtext: false,
	   	pginput:false,
		rowNum : -1,
		pager : jQuery('#pager_script2'),

		loadError : function(xhr, st, err) {
			alert('<anyframe:message code="script.load.error"/>');
		},
		onSelectRow: function(name) {
			if(name == null) {
				alert('<anyframe:message code="script.alert.selectserver"/>');
			}else{
				rowData = jQuery("#grid_script2").getRowData(name);
				$("#script_name").val(rowData.name);
				$("#script_path").val(rowData.path);
				$("#script_script").val(rowData.cmd);
			}
		}
	});
	jQuery("#grid_script2").jqGrid('navGrid','#pager_script2',{edit:false,add:false,del:false,search:false});
	$("#grid_script2").closest(".ui-jqgrid-bdiv").css({ 'overflow-y' : 'scroll' });

});

function selectAllTargets(){
	var data = new String(jQuery("#grid_script1").getDataIDs()); 
	rowNumList = data.split(","); 

	if(rowNumList==""){ 
		alert("Select Targer servers!!"); 
	}else{ 
		for(var i = 0 ; i < rowNumList.length ; i++){ 
			$("#grid_script1").jqGrid('setSelection', i+1); 
		} 
	} 
		
}

function runScript(script){
	var rowNum;
	var rowData;
	var rowArray = new Array();
	rowNum = new String(jQuery("#grid_script1").getGridParam('selarrrow'));
	
	if(rowNum == null || rowNum ==""){
		alert('<anyframe:message code="script.alert.selectserver"/>');
	} else {
		rowNumList = rowNum.split(",");
		if(confirm('<anyframe:message code="script.confirm.run"/>')){
			for(var i = 0 ; i < rowNumList.length ; i++){
				var rowData = jQuery("#grid_script1").getRowData(rowNumList[i]);
				rowArray[i] = rowData.name;

				var scriptconsole = $('#scriptResult');
				var script1 = $("#scriptResult").val();
				var msgIng = "\r\r["+rowArray[i]+"]\r";
				msgIng += ">> " + script + " command is launched.\r";
				msgIng += ">> Waiting to finish...\r\r\r";
		       	$("#scriptResult").val(script1 + msgIng);

		       	scriptconsole.scrollTop(
		       			scriptconsole[0].scrollHeight - scriptconsole.height()
	            );
			}
	
			$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=scriptService.run(list,jobname,script)&viewName=jsonView'/>",
			       {
			       list : rowArray,
			       jobname : '<%=jobName %>',
			       script : script
			       }, function(data) {
				       var scriptText = $("#scriptResult").val();
				       $("#scriptResult").val(scriptText + data.autoData);

				       var scriptconsole = $('#scriptResult');
				       scriptconsole.scrollTop(
				    		   scriptconsole[0].scrollHeight - scriptconsole.height()
			            );
		     });
		}
	}
}

function cleanScriptTextArea(){
	 $("#scriptResult").val('');
}

function cancelScript(){
	fn_addTab('03job', 'Job', 'job');   
}

</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><anyframe:message code="script.page.subtitle"/><%=jobName %></h3>
</div><!-- end pageSubtitle -->

<div id="body_script">
<form method="post" target="hiddenDiv" id="searchForm" name="searchForm">
	<table>
		<tr>
			<td style="align:top">
				<table id="grid_script1" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
				<div id="pager_script1" class="scroll" style="text-align: center;"></div>
			</td>
			<td width="40">&nbsp;</td>
			<td style="align:top">
				<table id="grid_script2" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
				<div id="pager_script2" class="scroll" style="text-align: center;"></div>
			</td>
		</tr>
		<tr>
			<td colspan="3" height="10"></td>
		</tr>
		<tr>
			<td colspan="3">
				<textarea id="scriptResult" style="padding-top:10px; width:100%; height:150px;" readonly="readonly"></textarea>
			</td>
		</tr>
	</table>
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				
				<td align=left><a href="javascript:cleanScriptTextArea();"><img src="<c:url value='/images/page_white.png'/>" style="vertical-align:middle;padding-left:3px;" alt="Clear"/>Clean</a></td>
				<td align=right><a href="javascript:cancelScript();"><img src="<c:url value='/images/btn_back.gif'/>" alt="back" /></a></td>
			</tr>
		</table>
	</div>	
</form></div>