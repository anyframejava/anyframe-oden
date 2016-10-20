<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%  request.setCharacterEncoding("UTF-8");
	String jobName = (String)request.getParameter("para");
	String userid = (String) session.getAttribute("userid");
%>
<script type="text/javascript">
	var toggleID = "";
	var cmd = "";		
	// Dialog setting
	$(function() {
		$( "#dialog:ui-dialog" ).dialog( "destroy" );

		cmd = $("#command"), allFields = $( [] ).add( cmd );
				
		jQuery.fn.orderDialogButtons = function() {
			var $buttonPane=this.next();
		    var $buttons=$buttonPane.children();
		    $('<div style="float:'+$buttons.css('float')+'">').appendTo($buttonPane).append($buttons);
		    $buttons.css('float','left');
		    return this;
		};
			
		$( "#dialog-form" ).dialog({
			autoOpen: false,
			//height: 200,
			width: 400,
			modal: true,
			resizable:false,
			buttons: {
				'Deploy all': function() {
					
					allFields.removeClass( "ui-state-error" );
					
					// deploy all option 실행
					deployAll(cmd.val());

					$( this ).dialog( "close" ).remove();

					$( "#dialog-form" ).dialog( "destroy" );

					
					setTimeout('fn_addTab("03job", "Job", "job")', 500);
					//fn_addTab("03job", "Job", "job")
				},
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			},
			close: function() {
				allFields.val( "" ).removeClass( "ui-state-error" );
			}
		});
			
	});
		
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
						url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=jobService.test(page,cmd,opt)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')+"&opt="+option,
						mtype : 'GET',
						datatype : "json",
						colNames : ['<spring:message code="deploy.grid.mode"/>', 
									'<spring:message code="deploy.grid.file"/>', 
									'<spring:message code="deploy.grid.dest"/>',
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
						rowNum : '<spring:message code="deploy.page.size"/>',
						pager : jQuery('#pager_deploy'),

						loadError : function(xhr, st, err) {
							alert('<spring:message code="deploy.load.error"/>');
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
			
			if(confirm('<spring:message code="deploy.confirm.deployitem"/>')){
				$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.run(items,opt,job,page,cmd,userid)&viewName=jsonView'/>",
					       {
				       		items : rowArray,
				       		opt : option,
				       		job : '<%=jobName%>',
				       		page : page,
				       		cmd : "",
				       		userid : '<%=userid%>'
				       		}, function(data) {
				       			jQuery("#grid_deploy").jqGrid('setGridParam',{page:1}).trigger("reloadGrid");
				     });
					//setTimeout('fn_addTab("03job", "Job", "job")', 500);
			}
		}else{
			alert('<spring:message code="deploy.alert.empty"/>');
		}

	}

	function deployAll(cmd) {
		var mode = $("#deploy_mode").val();
		var del = $("#checkbox_d").attr("checked");
		var compress = $("#checkbox_c").attr("checked");

		var option = "";

		if(mode == "include"){
			option += "i";
		}else if(mode == "update"){
			option += "u";
		}
		if(del){
			option += "d";
		}

		if(compress){
			option += "c";
		}

		if(cmd == "none") {
			cmd = "";
		}
		
		var rowArray = new Array();
		rowArray[0] = ".."+"@oden@"+".."+"@oden@"+"..";
		
		var page = 1;

		$.get("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.run(items,opt,job,page,cmd,userid)&viewName=jsonView'/>",
		       {
	       		items : rowArray,
	       		opt : option,
	       		job : '<%=jobName%>',
	       		page : page,
	      		cmd : cmd,
	       		userid : '<%=userid%>'
	      		}, function(data) {
	    });
	}
	
	$('[name=deployall]').click( function(){
		// deploy all form dialog open
		$('#dialog-form').orderDialogButtons();
		
		$( "#dialog-form" ).dialog( "open" );
	});	
	
	$('[name=deploycancellink]').click(function() {
		fn_addTab('03job', 'Job', 'job');
	});

	
	 
	
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h4"><spring:message code="deploy.page.subtitle"/><%=jobName %></h3>
</div><!-- end pageSubtitle --> 
<div id="body_deploy">
<form method="get" id="detailDeployForm" name="detailDeployForm">
	<div id="hiddenDiv"></div>
	<!--START: input table-->
	<fieldset>
		<legend>search</legend>
		<div class="input_table" style="padding-top:10px;">
			<table summary="deploy">
				<caption>deploy select</caption>
				<tbody>
					<tr>
						<th><label for="deploy"><spring:message code="deploy.text.select"/></label></th>
						<td>
							<select name="deploy_mode" id="deploy_mode" class="selectbox" style="width:120">
								<option value="include" selected="selected"><spring:message code="deploy.text.include"/></option>
								<option value="update"><spring:message code="deploy.text.update"/></option>
							</select>
						</td>
						<td width="500"></td>
						<th scope="row"><label for="fail"><spring:message code="deploy.text.delete"/></label></th>
						<td><input type="checkbox" id="checkbox_d" name="checkbox_opt" value="Delete" class="checkbox" unchecked></td>
						<th scope="row"><label for="fail"><spring:message code="deploy.text.compress"/></label></th>
						<td><input type="checkbox" id="checkbox_c" name="checkbox_copt" value="Compress" class="checkbox" unchecked></td>
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
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align=right>
					<a name="deploylink" href="#"><img src="<c:url value='/images/btn_deploy.gif'/>" alt="deploy" /></a> 
					<a name="deployall" href="#"><img src="<c:url value='/images/btn_deploy_all.gif'/>" alt="deployall" /></a>
					<a name="deploycancellink" href="#"><img src="<c:url value='/images/btn_cancel.gif'/>" width="69" height="22" alt="cancel" /></a>
				</td>
			</tr>
		</table>
	</div>
	
	<!-- dialog form start -->
	<div id="dialog-form" title='<spring:message code="deploy.confirm.deployalltitle"/>'>
		<fieldset>
			<table summary="deploy">
				<caption>running script select</caption>
				<tbody>
					<tr>
						<td><label for="deploy"><spring:message code="deploy.confirm.label"/></label></td>
						<td>
							<select name="command" id="command" class="selectbox" style='width:100'>
								<option value="none" selected="selected">None</option>
								<c:forEach var="cmd" items="${cmds}" varStatus="status">
									<option value="${cmd.name}"><c:out value="${cmd.name}"></c:out></option>
								</c:forEach>
							</select>
						</td>
					</tr>
				</tbody>
			</table>
		</fieldset>
		<p><spring:message code="deploy.confirm.deployallitem"/></p>
	</div>
	<!-- dialog form end -->
</form></div>