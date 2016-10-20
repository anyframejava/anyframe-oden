<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%	request.setCharacterEncoding("UTF-8");
	String jobName = (String)request.getParameter("para");
	String currentSelectedTab = (String)request.getParameter("para1");
%>

<script type="text/javascript">

	var headerVar;
	
	$.ajax( {
		url :  "<c:url value= '/simplejson.do?layout=jsonLayout&service=jobService.compareHeader(cmd)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>'),
		dataType : "json",
		async : true,
		success : function(result) {
				headerVar = result;
				//setChooser(result);
				drawCompareGrid();
				},
			error : function() {
				alert('<spring:message code="compare.load.error"/>');
			}
	});

	function drawCompareGrid(){

		$("#grid_compare")
		.jqGrid(
				{
					url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=jobService.compare(page,cmd,opt)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')+"&opt=-failonly",
					datatype : "json",
					colNames : headerVar.autoData.header,
					colModel : headerVar.autoData.model,
					width : 888,
					height : 310,
					scroll : false,
					viewrecords : true,
					sortable : false,
					rowNum : '<spring:message code="common.page.size"/>',
					viewrecords : true,
					shrinkToFit:false,
					pager : jQuery('#pager_compare'),
					postData : {
						oper : "grid"
					},
					afterInsertRow:function(rowid, aData){
						if(aData.status == "F"){
							var cols = new String(headerVar.autoData.header);
							colList = cols.split(',');
							for(var i=0; i<colList.length; i++){
								jQuery("#grid_compare").jqGrid('setCell',rowid,colList[i],'',{color:'red'});
							}
						}else{
						}
					},
					jsonReader : {
						repeatitems : false
					}
		}); // end of grid

		$("#grid_compare").closest(".ui-jqgrid-bdiv").css({ 'overflow-x' : 'scroll' });
		$("#grid_compare").closest(".ui-jqgrid-bdiv").css({ 'overflow-y' : 'scroll' });
		
		jQuery("#grid_compare").navGrid('#pager_compare', {
					edit : false,
					add : false,
					del : false,
					search : false
				});
	}

	function openSelectColumnDlg() {
		jQuery("#grid_compare").jqGrid('setColumns');
	}
	
	function ok() {
		fn_addTab('03job', 'Job', 'job', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', currentSelectedTab);
	}

	function setChooser(result){
		var bodyArea = document.getElementById("div_grid_compare");
		
		var array = new String(result.autoData.header);
		numList = array.split(",");
		
		var columnArea = document.getElementById("div_select_column");

		var ihtml = "";
		
		for(var i=2; i<numList.length; i++){
			var name = numList[i];
			ihtml += "<span><th scope='row'><label>"+name+": </label></th>";
			ihtml += "<td><input type='checkbox' id='"+name+"' name='columnCheckBox' style='height:17px;' class='checkbox' checked ></td>&nbsp;&nbsp;</span>";
		}
		ihtml+= "";
		columnArea.innerHTML = ihtml;
	}

	function toggleCheck(array,name){
		var bodyArea = document.getElementById("div_grid_compare");
		var theadArea = bodyArea.getElementsByTagName("th");
		var rowArray = new Array();
		for(var i=0; i<theadArea.length; i++){
			var _columnStyle = theadArea[i].getAttribute('style');
			var columnStyle;
			if (typeof _columnStyle == 'string') { //FF
				columnStyle = _columnStyle;
				if(columnStyle.indexOf("display") == -1){
					rowArray[i] = theadArea[i].getElementsByTagName("div")[0].childNodes[0].nodeValue;
				}
			}
			else if (typeof _columnStyle == 'object') { //IE
				columnStyle = _columnStyle.cssText;
				if(columnStyle.indexOf("DISPLAY") == -1){
					rowArray[i] = theadArea[i].getElementsByTagName("div")[0].childNodes[0].nodeValue;
				}
			}
		}
		numList = array.split(",");

		var columnArea = document.getElementById("div_select_column");
		columnArea.innerHTML = "";
		
		var ihtml = "<fieldset><legend>Select Column</legend>";

		for(var i=0; i<numList.length; i++){
			var boolShow = false;
			if(rowArray.length == 0){
				if(numList[i] == name){  
					boolShow = true;
				}
			}else{
				for(var n=0; n<rowArray.length; n++){
					if(numList[i] == rowArray[n]){ //show column
						if(numList[i] == name){    //select
							boolShow = false;      //hidden
						}else{
							boolShow = true;       //show
						}
						break;
					}else{                         //hide column
						if(numList[i] == name){    //select
							boolShow = true;       //show
						}else{
							boolShow = false;      //hidden
						}
					}
				}
			}
			if(boolShow){
				jQuery("#grid_compare").jqGrid('showCol',[numList[i]]);
				ihtml += "<input type='checkbox' name='columnCheckBox' value='true' checked onClick=\"javascript:toggleCheck('"+array+"','"+numList[i]+"');\">"+numList[i]+"</input>&nbsp;&nbsp;&nbsp;&nbsp;";
			}else{
				jQuery("#grid_compare").jqGrid('hideCol',[numList[i]]);
				ihtml += "<input type='checkbox' name='columnCheckBox' value='true' onClick=\"javascript:toggleCheck('"+array+"','"+numList[i]+"');\">"+numList[i]+"</input>&nbsp;&nbsp;&nbsp;&nbsp;";
			}
		}
		
		ihtml+= "</fieldset>";
		columnArea.innerHTML = ihtml;
	}

	function gridCompareReload() {
		var search = "";

		if($("#comparefail:checked").val() != undefined) {
			search = "-failonly";
		}
		
		jQuery("#grid_compare")
				.jqGrid(
						'setGridParam',
						{
							page:1,
							url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=jobService.compare(page,cmd,opt)&viewName=jsonView&cmd='/>"+encodeURI('<%=jobName%>')+"&opt="+search
						}).trigger("reloadGrid");
	}
	
	function getExcelDownload() {
		var mya = new Array();
		var cmds = new Array();
		var cmd = "";
		
		var colnames = "";
		var targets = " " + "-t";
		var ii=0;
		var iii=0;
		var colTitles=new Array();
		
		mya = jQuery('#grid_compare').getDataIDs();
		
		var data=jQuery('#grid_compare').getRowData(mya[0]);     // Get First row to get the labels
		for (var i in data){colTitles[ii++]=i;}    // capture col names
		
		for(var i in data) {
			if(! jQuery('#grid_compare').getColProp(i).hidden) {
				if(colTitles.length == iii) {
					colnames = colnames + i;
					targets = targets + " " + i;
				} else if(iii > 1) {
					colnames = colnames + i + ',';
					targets = targets + " " + i;
				}
			}
			iii++; 	
		}
		
		// job name
		cmds.push('<%=jobName%>');
		// target
		cmds.push(targets);
		// failonly check
		if($("#comparefail:checked").val() != undefined) {
			cmds.push(" " + "-failonly");
		}
		for(x in cmds) {
			cmd = cmd + cmds[x];
		}
		
		document.compareForm.cmd.value=cmd;
		document.compareForm.tgt.value=colnames;
		document.compareForm.method='POST';
		document.compareForm.action="<c:url value='/excel.do?method=compare'/>";
		document.compareForm.target='_blank';
		document.compareForm.submit();
	}
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><spring:message code="compare.page.subtitle"/><%=jobName %></h3>
</div><!-- end pageSubtitle -->
<div id="body_compare">
<form method="post" target="hiddenDiv" id="compareForm" name="compareForm">
	<!--START: input table-->
	<fieldset>
		<legend>Options</legend>
		<div id="div_compare_option" class="input_table" style="padding-top:2px;">
			<fieldset><legend></legend>
			<table summary="compare">
				<caption>compare select</caption>
				<tbody>
					<tr>
						<td><a id="showColumns" href="javascript:openSelectColumnDlg();" style="text-decoration:underline;">Select Servers</a></td>
<!--						<td height="auto"><div id="div_select_column"></div></td>-->
						<td width="550"></td>
						<th scope="row"><label for="fail"><spring:message code="history.label.fail"/></label></th>
						<td align="left"><input type="checkbox" id="comparefail" value="true" class="checkbox" checked/></td>
						<td width="80" align="right"><a id="showGrid" href="javascript:gridCompareReload();"><img src="<c:url value='/images/btn_search.gif'/>" width="73" height="20" alt="search" /></a></td>
					</tr>
				</tbody>
			</table>
			</fieldset>
		</div>
	</fieldset>	
	<!--END: input table-->
	<div class="listbox" style="padding-top:10px;" id="div_grid_compare">
			<table id="grid_compare" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>
	<input type="hidden" id="hiddenDiv" value="">
	<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
	<div id="pager_compare" class="scroll" style="text-align: center;"></div>
	<a id="getLink" name="getLink"></a>
	<div id="button" style="padding-top:5px;">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align=right><a href="javascript:getExcelDownload();"><img src="<c:url value='/images/btn_exceldown.gif'/>" alt="download" /></a> <a href="javascript:ok();"><img src="<c:url value='/images/btn_back.gif'/>" alt="back" /></a></td>
			</tr>
		</table>
	</div>
	<input type="hidden" name="cmd" value="">
	<input type="hidden" name="tgt" value="">
</form></div>
