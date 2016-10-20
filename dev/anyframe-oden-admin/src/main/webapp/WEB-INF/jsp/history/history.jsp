<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%
	String roles = (String) session.getAttribute("userrole");
	String[] roleArr = roles.split(",");
	String role = roleArr[0];
	String cmd = role.equals("ROLE_ADMIN") ? "-failonly" : "-job" + " " + role + " "+"-failonly";
%>
<script type="text/javascript">
	var cmd = '<%=cmd%>';
	jQuery(document).ready(function() {
		
		jQuery("#grid_history").jqGrid( {
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=historyService.findByPk(page,cmd)&viewName=jsonView&cmd='/>" + cmd  ,
			mtype : 'GET',
			datatype : "json",
			colNames : [ '<spring:message code="history.grid.statustxid"/>', 
			 			 '<spring:message code="history.grid.job"/>', 
			 			 '<spring:message code="history.grid.date"/>', 
			 			 '<spring:message code="history.grid.counts"/>',
			 			'<spring:message code="history.grid.user"/>'],
			jsonReader : {
				repeatitems : false
			},
			colModel : [ {
				name : 'txid',
				index : 'txid',
				align : 'center',
				width : 100,
				sortable : false
			}, {
				name : 'job',
				index : 'job',
				align : 'center',
				width : 100,
				sortable : false
			}, {
				name : 'date',
				index : 'date',
				align : 'center',
				width : 120,
				sortable : false
			}, {
				name : 'counts',
				index : 'counts',
				align : 'center',
				width : 100,
				sortable : false
			}, {
				name : 'user',
				index : 'user',
				align : 'center',
				width : 80,
				sortable : false
			} ],
			width : 888,
			height : 310,
			scroll : false,
			forceFit : true,
			viewrecords : true,
			pager : jQuery('#npager_history'),
			rowNum : '<spring:message code="common.page.size"/>',
			sortable : true,
			
			loadError : function(xhr, st, err) {
				alert('<spring:message code="history.load.error"/>');
			}
		});

		jQuery("#grid_history").jqGrid('navGrid','#npager_history',{edit:false,add:false,del:false,search:false});
	});

	$("#searchHistory").click( function() {
		search();
	});
		
	function search() {
		// search condition command combination
		var params = new Array();
		var param = "";
		var x;

		if(isValidString(trim($("#itemname").val())) && trim($("#itemname").val()) !== ""
			|| isValidString(trim($("#userid").val())) && trim($("#userid").val()) !== ""
				){
			alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
		}else{
			if(trim($("#itemname").val()) !== "") {
				params.push("-path" +" " + $("#itemname").val() + " "); 
			}
	
			if(trim($("#userid").val()) !== "") {
				params.push("-user" +" " + $("#userid").val() + " ");
			}
			
			if(trim($("#jobname").val()) !== "" && trim($("#jobname").val()) !== "all") {
				params.push("-job" +" " + $("#jobname").val() + " ");
			}
			// must undefined check
			if($("#failed:checked").val() != undefined) {
				params.push("-failonly");
			}
	
			for(x in params) {
				param = param + params[x];
			}
			cmd = param;
	
			jQuery("#grid_history")
					.jqGrid(
							'setGridParam',
							{
								page:1,
								url : "<c:url value='/simplejson.do?layout=jsonLayout&service=historyService.findByPk(page,cmd)&viewName=jsonView&cmd='/>" + encodeURI(param)
							}).trigger("reloadGrid");
		}
		
	}

	function getHistoryDownload() {
		document.historyForm.cmd.value=cmd;
		document.historyForm.method='POST';
		document.historyForm.action="<c:url value='/excel.do?method=history'/>";
		document.historyForm.target='_blank';
		document.historyForm.submit();
	}
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><spring:message code="history.page.subtitle"/></h3>
</div><!-- end pageSubtitle --> 
<div id="body_history">
<form name="historyForm">
	<!--START: input table-->
	<fieldset>
		<legend>search</legend>
		<div class="input_table" style="padding-top:2px;">
			<table summary="job">
				<caption>history search</caption>
				<tbody>
					<tr>
						<th scope="row"><label for="itemname"><spring:message code="history.label.itemname"/></label></th>
						<td><input type="text" id="itemname" name="itemname" value="" style="width:130px; height:17px;" onKeyPress="if(event.keyCode==13) search();"/></td>
						<th scope="row"><label for="user"><spring:message code="history.label.user"/></label></th>
						<td><input type="text" id="userid" name="userid" value="" style="width:100px; height:17px;" onKeyPress="if(event.keyCode==13) search();"/></td>
						<th scope="row"><label for="jobname"><spring:message code="history.label.jobname"/></label></th>
						<td>
							<select name="jobname" id="jobname" class="selectbox" style='width:150'>
								<iam:access hasPermission="${iam:getPermissionMask(\"CREATE\")}" viewName="addUser">
									<option value="all"><spring:message code="history.select.all"/></option>
								</iam:access>
								<c:forEach var="job" items="${jobs}" varStatus="status">
									<option value="${job.jobname}"><c:out value="${job.jobname}"></c:out>
								</c:forEach>
							</select>
						</td>
						<th scope="row" style="align:right;"><label for="fail"><spring:message code="history.label.fail"/></label></th>
						<td><input type="checkbox"  id="failed" value="true" class="checkbox" checked/></td>
						<td width="80" align="right"><a id="searchHistory" name="searchHistory" href="#"><img src="<c:url value='/images/btn_search.gif'/>" width="73" height="20" alt="search" /></a></td>
					</tr>
				</tbody>
			</table>
		</div>
	</fieldset>	
<!--END: input table-->
	<div class="listbox" style="padding-top:10px;">
		<table id="grid_history" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>
	<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
	<div id="npager_history" class="scroll" style="text-align: center;"></div>
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align=right><a href="javascript:getHistoryDownload();"><img src="<c:url value='/images/btn_exceldown.gif'/>" alt="download" /></a></td>
			</tr>
		</table>
	</div>
	<input type="hidden" name="cmd" value=""/>
</form>
</div>