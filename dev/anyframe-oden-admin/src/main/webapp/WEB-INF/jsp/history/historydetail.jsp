<%@ page language="java" errorPage="/sample/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<% String param = (String) request.getParameter("para");
   String param1 = ((String) request.getParameter("para1")).equals("undefined")? "" : (String) request.getParameter("para1");
   String opt = " " + "-path" + " " + param1;
   String roles = (String) session.getAttribute("userrole");
 %>

<script type="text/javascript">
	if('<%=param1%>' == "undefined") {
		var cmd = "";
	} else {
		var cmd = '<%=opt%>';
	}
	
	jQuery(document).ready(function() {
		jQuery("#history_detail").jqGrid( {
			url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=historyService.show(page,cmd,opt)&viewName=jsonView&cmd='/>" + '<%=param%>' + "&opt=" + encodeURI('<%=opt%>'),
			mtype : 'GET',
			datatype : "json",
			colNames : [ '<anyframe:message code="historydetail.grid.no"/>',
						 '<anyframe:message code="historydetail.grid.status"/>',
			             '<anyframe:message code="historydetail.grid.target"/>',
			 			 '<anyframe:message code="historydetail.grid.path"/>', 
			 			 '<anyframe:message code="historydetail.grid.mode"/>', 
			 			 '<anyframe:message code="historydetail.grid.log"/>' ],
			jsonReader : {
				repeatitems : false
			},
			colModel : [ {
				name : 'no',
				index : 'no',
				align : 'center',
				width : 50,
				sortable : false
			}, {
				name : 'success',
				index : 'success',
				align : 'center',
				width : 60,
				sortable : false
			}, {
				name : 'job',
				index : 'job',
				align : 'center',
				width : 200,
				sortable : false	
			}, {
				name : 'path',
				index : 'path',
				align : 'left',
				width : 250,
				sortable : false
			}, {
				name : 'mode',
				index : 'mode',
				align : 'center',
				width : 60,
				sortable : false
			}, {
				name : 'errorlog',
				index : 'errorlog',
				align : 'left',
				sortable : false
			} ],
			width : 888,
			height : 310,
			scroll : false,
			viewrecords : true,
			shrinkToFit:true,
			pager : jQuery('#pager_historydetail'),
			rowNum : '<anyframe:message code="common.page.size"/>',
			sortable : true,

			loadError : function(xhr, st, err) {
				alert('<anyframe:message code="historydetail.load.error"/>');
			}
		});

		jQuery("#history_detail").jqGrid('navGrid','#pager_historydetail',{edit:false,add:false,del:false,search:false});
	});

	$('[name=historycancellink]').click(function() {
		var role = '<%=roles%>';
		fn_addTab('04history', 'History','',"&initdataService=historyService.findJob(role)&initdataResult=jobs&role=" + role);
	});

	var file = '<%=param1%>';
	
	if(file !== "undefined" ) {
		$("#filename").val(file);
	} else {
		$("#filename").val("");
	}	
	
	function search() {
		var searchs = new Array();
		var search = "";
		var x;

		if(isValidString(trim($("#filename").val())) && trim($("#filename").val()) !== ""){
			alert('<anyframe:message code="jobdetail.alert.invalidcharacter"/>');	
		}else{
			if(trim($("#filename").val()) !== "") {
				searchs.push("-path" +" " + $("#filename").val() + " ");
			}
				
			if(trim($("#mode").val()) !== "" && trim($("#mode").val()) !== "all") {
				searchs.push("-mode" +" " + $("#mode").val() + " ");
			}
			
			if($("#failed:checked").val() != undefined) {
				searchs.push("-failonly");
			}
			
			for(x in searchs) {
				search = search + searchs[x];
			}
			cmd = search;
	
			jQuery("#history_detail")
					.jqGrid(
							'setGridParam',
							{
								page:1,
								url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=historyService.show(page,cmd,opt)&viewName=jsonView&cmd='/>" + encodeURI('<%=param%>')
										+ "&opt=" + encodeURI(search)
							}).trigger("reloadGrid");
		}
	}
	function getDetailDownload() {
		document.detailForm.txid.value='<%=param%>';
		document.detailForm.cmd.value=cmd;
		document.detailForm.method='POST';
		document.detailForm.action="<c:url value='/excel.do?method=historydetail'/>";
		document.detailForm.target='_blank';
		document.detailForm.submit();
	}
	
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><anyframe:message code="historydetail.page.subtitle"/></h3>
</div><!-- end pageSubtitle -->
<div id="body_history">
<form method="post" id="detailForm" name="detailForm">
	<!--START: input table-->
	<fieldset>
		<legend>search</legend>
		<div class="input_table" style="padding-top:2px;">
			<table summary="history">
				<caption>history detail search</caption>
				<tbody>
					<tr>
						<th scope="row"><label for="filename"><anyframe:message code="history.label.itemname"/></label></th>
						<td><input type="text" id="filename" name="filename" value="" style="width:120px; height:17px;" onKeyPress="if(event.keyCode==13) search();"/></td>
						<th scope="row"><label for="mode"><anyframe:message code="historydetail.label.mode"/></label></th>
						<td>
							<select name="mode" id="mode" class="selectbox" style='width:100'>
								<option value="all"><anyframe:message code="historydetail.select.all"/></option>
								<option value="A"><anyframe:message code="historydetail.select.add"/></option>
								<option value="U"><anyframe:message code="historydetail.select.upd"/></option>
								<option value="D"><anyframe:message code="historydetail.select.del"/></option>
							</select>	
						</td>
						<td width="230"></td>
						<th scope="row" style="align:right;"><label for="fail"><anyframe:message code="historydetail.search.fail"/></label></th>
						<td><input type="checkbox"  id="failed" value="true" class="checkbox"/></td>
						<td width="80" align="right"><a href="javascript:search();"><img src="<c:url value='/images/btn_search.gif'/>" width="73" height="20" alt="search" /></a></td>
					</tr>
				</tbody>
			</table>
		</div>
	</fieldset>	
<!--END: input table-->
	<div class="listbox" style="padding-top:10px;">
		<table id="history_detail" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>	
	<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
	<div id="pager_historydetail" class="scroll" style="text-align: center;"></div>
	
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align=right><a href="javascript:getDetailDownload();"><img src="<c:url value='/images/btn_exceldown.gif'/>" alt="download" /></a> 
				<a name="historycancellink" href="#"><img src="<c:url value='/images/btn_back.gif'/>" alt="back" /></a></td>
			</tr>
		</table>
	</div>
	<input type="hidden" name="cmd" value=""/>
	<input type="hidden" name="txid" value=""/>
</form></div>