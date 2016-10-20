<%@ page language="java" errorPage="/common/error.jsp"
	pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
var timer; //timer
var holdTheInterval;

jQuery(document).ready(function() {
	jQuery("#grid_status")
	.jqGrid(
			{
				url : "<c:url value= '/simplejson.do?layout=jsonLayout&service=statusService.findList(cmd)&viewName=jsonView'/>",
				mtype : 'GET',
				datatype : "json",
				colNames : [ '<anyframe:message code="status.grid.job"/>',
							 '<anyframe:message code="status.grid.date"/>', 
							 '<anyframe:message code="status.grid.counts"/>', 
							 '<anyframe:message code="status.grid.action"/>',
							 'txId'
							 ],
				jsonReader : {
					repeatitems : false
				},
				colModel : [ {
					name : 'desc',
					index : 'desc',
					align : 'center',
					width : 100,
					sortable : false
				}, {
					name : 'date',
					index : 'date',
					align : 'center',
					width : 200,
					sortable : false
				}, {
					name : 'totalWorks',
					index : 'totalWorks',
					align : 'center',
					width : 100,
					sortable : false		
				}, {
					name : 'progress',
					index : 'progress',
					align : 'center',
					width : 388,
					sortable : false
				}, {
					key : true,
					name : 'id',
					hidden : true
				}],
				width : 888,
				height : 380,
				scroll : false,
				pgbuttons: false,
				pgtext: false,
				forceFit : true,
				multiselect : false,
				viewrecords : false,
				sortable : true,
				rowNum : -1,
				pager : jQuery('#pager_status'),
				
				loadError : function(xhr, st, err) {
					//jQuery("#grid_job").clearGridData(true);
					jQuery("#grid_status").trigger("reloadGrid");
					alert('<anyframe:message code="status.load.error"/>');
				},
				gridComplete: function() {
					$.ajax( {
						url : "<c:url value='/simplejson.do?layout=jsonLayout&service=statusService.findList(cmd)&viewName=jsonView'/>",
						dataType : "json",
						async : true,
						success : function(result) {
							if(result.rows.length == 0 ) {
								// 10 seconds
								timer = setTimeout(jobTimer, 10000);
							} else {
								// 1 seconds
								timer = setTimeout(jobTimer, 1000); 
							}
						}
					});	
				}						
			});					
			jQuery("#grid_status").jqGrid('navGrid','#pager_status',{edit:false,add:false,del:false,search:false});
		});
	function jobstartTimer() {    
		timer = setTimeout('jQuery("#grid_status").trigger("reloadGrid");', 1000);
	}

	function jobstopTimer(){
		clearTimeout(timer);	
	}

	function jobTimer() {
		jQuery("#grid_status").trigger("reloadGrid");
	}
	
	function stopDeploy(txid){
		if(confirm('<anyframe:message code="status.confirm.stopjob"/>')){
			$.ajax( {
				url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.stop(id)&viewName=jsonView&id='/>"+txid ,
				dataType : "json",
				async : true,
				success : function(result) {
					jQuery("#grid_status").trigger("reloadGrid");
				},
				error : function() {
					alert('<anyframe:message code="status.load.error"/>');
				}
			});
		}
	}
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><anyframe:message code="status.page.subtitle"/></h3>
</div><!-- end pageSubtitle -->

<div id="body_policy">
<form method="get" id="searchForm" name="searchForm">
	<div class="listbox" style="padding-top:2px;">
		<table id="grid_status" class="scroll" cellpadding="0" cellspacing="0">
			<tr>
				<td></td>
			</tr>
		</table>
	</div>
	<div id="pager_status" class="scroll" style="text-align: center;"></div>
</form>
</div>
