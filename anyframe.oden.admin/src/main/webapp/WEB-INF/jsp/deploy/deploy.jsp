<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<% String para = (String)request.getParameter("para");%>

<script type="text/javascript">
	var toggleID = "";

function drawGrid() {
	$("#grid_deploy").GridUnload(); 

	
	var del = $("#checkbox_d").attr("checked");

	var mode = $("#deploy_mode").val();

	var option = "";

	if(mode == "include"){
		option += "i";
	}else if(mode == "update"){
		option += "u";
	}
	if(del){
		option += "d";
	}

	jQuery("#grid_deploy")
			.jqGrid(
					{
						url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=jobService.test(page,cmd,opt)&viewName=jsonView&cmd='/>"+'<%=para%>'+"&opt="+option,
						mtype : 'post',
						datatype : "json",
						colNames : ['<anyframe:message code="deploy.grid.mode"/>', 
									'<anyframe:message code="deploy.grid.file"/>', 
									'<anyframe:message code="deploy.grid.dest"/>',
									'',
									'hidden'],
						jsonReader : {
							repeatitems : false
						},
						colModel : [ {
							name : 'mode',
							index : 'mode',
							align : 'center',
							sortable:false,
							width : 80
						}, {
							name : 'file',
							index : 'file',
							align : 'left',
							key : true,
							sortable:false,
							width : 350
						}, {
							name : 'destination',
							index : 'destination',
							align : 'center',
							sortable:false,
							width : 200
						}, {
							name : 'toggle',
							index : 'toggle',
							align : 'center',
							sortable:false,
							width : 30
						}, {
							name : 'hidden', 
							hidden : true
						} ],
						width : 888,
						height : 300,
						scroll : false,
						multiselect : false,
						viewrecords : true,
						sortable : false,
						forceFit : true,
						rowNum : '<anyframe:message code="deploy.page.size"/>',
						pager : jQuery('#pager_deploy'),

						loadError : function(xhr, st, err) {
							alert('<anyframe:message code="deploy.load.error"/>');
						},

						onSelectRow: function(id) {
							toggleID = id;
						}

					});

	jQuery("#grid_deploy").jqGrid('navGrid','#pager_deploy',{edit:false,add:false,del:false,search:false});

}
	function toggleRemoveList(){
		var hidden = jQuery("#grid_deploy").getRowData(toggleID).hidden;
		var result = "";
		var color = "";
		var deco = "";
		if(hidden==null || hidden==''){
			result = 'checked';
			color = "#999999";
			deco = "line-through";
		}else if(hidden=='checked'){
			result = '';
			color = "#58595b";
			deco = "none";
		}

		jQuery('#grid_deploy').setCell(toggleID,'mode','',{'color': color,'text-decoration':deco},'');
		jQuery('#grid_deploy').setCell(toggleID,'file','',{'color': color,'text-decoration':deco},'');
		jQuery('#grid_deploy').setCell(toggleID,'destination','',{'color': color,'text-decoration':deco},'');
		jQuery('#grid_deploy').setCell(toggleID,'toggle','',{'color': color,'text-decoration':deco},'');
		
		jQuery('#grid_deploy').setRowData(toggleID, {hidden:result}); 
		jQuery('#grid_deploy').saveRow(toggleID, false);
	}
	
	$('[name=deploylink]').click( function(){

		var mode = $("#deploy_mode").val();
		var del = $("#checkbox_d").attr("checked");

		var option = "";

		if(mode == "include"){
			option += "i";
		}else if(mode == "update"){
			option += "u";
		}
		if(del){
			option += "d";
		}

		deployWithGrid(option);
		
	});

	function deployWithGrid(option){
		var rowNum = new Array();
		rowNum = new String(jQuery('#grid_deploy').getDataIDs());
		rowNumList = rowNum.split(",");
		var rowArray = new Array();
		
		var rowData;

		var n = 0;
		if(rowNumList!=""){
			for(var i = 0 ; i < rowNumList.length ; i++){
				rowData = jQuery("#grid_deploy").getRowData(rowNumList[i]);
				if(rowData.hidden == "checked"){
					rowArray[n] = rowData.mode+'@oden@'+rowData.file+'@oden@'+rowData.destination;
					n++;
				}else{
				}
			}
			if(rowArray.length == 0){
				rowArray[0] = "."+"@oden@"+"."+"@oden@"+".";
			}

			var page = $("#grid_deploy").getGridParam("page");
			
			if(confirm('<anyframe:message code="deploy.confirm.deployitem"/>')){
				$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.run(items,opt,job,page)&viewName=jsonView'/>",
					       {
				       		items : rowArray,
				       		opt : option,
				       		job : '<%=para%>',
				       		page : page
				       		}, function(data) {
				       			jQuery("#grid_deploy").jqGrid('setGridParam',{page:1}).trigger("reloadGrid");
				     });
					//setTimeout('fn_addTab("03job", "Job", "job")', 500);
			}
		}else{
			alert('<anyframe:message code="deploy.alert.empty"/>');
		}

	}
	
	$('[name=deployalllink]').click( function(){

		var mode = $("#deploy_mode").val();
		var del = $("#checkbox_d").attr("checked");

		var option = "";

		if(mode == "include"){
			option += "i";
		}else if(mode == "update"){
			option += "u";
		}
		if(del){
			option += "d";
		}

		var rowArray = new Array();
		rowArray[0] = ".."+"@oden@"+".."+"@oden@"+"..";
		
		var page = $("#grid_deploy").getGridParam("page");
		
		if(confirm('<anyframe:message code="deploy.confirm.deployallitem"/>')){
			$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.run(items,opt,job,page)&viewName=jsonView'/>",
			       {
		       		items : rowArray,
		       		opt : option,
		       		job : '<%=para%>',
		       		page : page
		       		}, function(data) {
		     });
			setTimeout('fn_addTab("03job", "Job", "job")', 500);
		}
		
		
	});
	
	$('[name=cancellink]').click(function() {
		fn_addTab('03job', 'Job', 'job');
	});

</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h4"><anyframe:message code="deploy.page.subtitle"/><%=para %></h3>
</div><!-- end pageSubtitle --> 
<div id="body_deploy">
<form method="post" id="detailDeployForm" name="detailDeployForm">
	<div id="hiddenDiv"></div>
	<!--START: input table-->
	<fieldset>
		<legend>search</legend>
		<div class="input_table" style="padding-top:10px;">
			<table summary="deploy">
				<caption>deploy select</caption>
				<tbody>
					<tr>
						<th scope="row"><label for="deploy"><anyframe:message code="deploy.text.select"/></label></th>
						<td>
							<select name="deploy_mode" id="deploy_mode" class="selectbox" style="width:120">
								<option value="include"><anyframe:message code="deploy.text.include"/></option>
								<option value="update" selected="selected"><anyframe:message code="deploy.text.update"/></option>
							</select>
						</td>
						<td width="500"></td>
						<th scope="row"><label for="fail"><anyframe:message code="deploy.text.delete"/></label></th>
						<td><input type="checkbox" id="checkbox_d" name="checkbox_opt" value="Delete" class="checkbox" checked></td>
						<td width="80" align="right"><a href="javascript:drawGrid();"><img src="<c:url value='/images/btn_preview.gif'/>" alt="preview" /></a></td>
					</tr>
				</tbody>
			</table>
		</div>
	</fieldset>	
<!--END: input table-->
	<div style="padding-top:10px;" id="scrollarea">
		<table id="grid_deploy" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>
	<input type="hidden" id="deploy_pageIndex" name="deploy_pageIndex" value="1" />
	<div id="pager_deploy" class="scroll" style="text-align: center;"></div>
	<a id="getLink" name="getLink"></a>
	<br/>
<!--	<div style="width:888" align="right">-->
<!--		<table summary="deploy">-->
<!--			<tbody>-->
<!--				<tr>-->
<!--					<th scope="row" style="color:#4675A9; background-color:fff; text-align:right; white-space:nowrap; font-weight:bold"><label for="fail">Deploy All : </label></th>-->
<!--					<td><input type="checkbox" id="check_deploy_all" name="check_deploy_all" class="checkbox" checked></td>-->
<!--				</tr>-->
<!--			</tbody>-->
<!--		</table>-->
<!--	</div>-->
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align=right>
					<a name="deploylink" href="#"><img src="<c:url value='/images/btn_deploy.gif'/>" alt="deploy" /></a> 
					<a name="deployalllink" href="#"><img src="<c:url value='/images/btn_deploy_all.gif'/>" alt="deployall" /></a>
					<a name="cancellink" href="#"><img src="<c:url value='/images/btn_cancel.gif'/>" width="69" height="22" alt="cancel" /></a>
				</td>
			</tr>
		</table>
	</div>
</form></div>