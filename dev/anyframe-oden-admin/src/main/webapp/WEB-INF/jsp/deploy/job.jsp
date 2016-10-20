<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%
	String roles = (String) session.getAttribute("userrole");
	String userid = (String) session.getAttribute("userid");
%>
<script type="text/javascript">

var t; //timer

jQuery(document).ready(function() {
	jQuery("#grid_job").jqGrid( {

		url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findList(cmd)&viewName=jsonView&cmd='/>"+ encodeURI('<%=roles%>'),
		mtype : 'GET',
		datatype : "json",
		colNames : [ '<spring:message code="job.grid.job"/>', 
		     		 '<spring:message code="job.grid.action"/>',
		     		 '<spring:message code="job.grid.status"/>', 
		     		 '<spring:message code="job.grid.id"/>'],
		jsonReader : {
			repeatitems : false
		},
		colModel : [{
			name : 'jobname',
			index : 'jobname',
			align : 'center',
			width : 200,
			sortable:false
		}, {
			name : 'mode',
			index : 'mode',
			align : 'center',
			width : 400,
			sortable:false
		}, {
			name : 'date',
			index : 'date',
			align : 'center',
			width : 240,
			sortable:false
		}, {
			key : true, 
			name : 'txId', 
			hidden : true}
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
			
			$.ajax( {
				url : "<c:url value='/simplejson.do?layout=jsonLayout&service=statusService.findList(cmd)&viewName=jsonView'/>",
				dataType : "json",
				async : true,
				success : function(result) {
					if(result.rows.length == 0 ) {
						stopTimer();
					} else {
						startTimer();
					}
				}
			});	
		}
	});
	
	$('[name=addlink]').click( function(){
		fn_addTab('03job', 'JobDetail', 'jobdetail', '""');
	});
	
	$("#grid_job").closest(".ui-jqgrid-bdiv").css({ 'overflow-y' : 'scroll' });
	jQuery("#grid_job").jqGrid('navGrid','#pager_pager_job',{edit:false,add:false,del:false,search:false});
});

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
	if(confirm('<spring:message code="job.confirm.deletejob"/>')){
		$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.remove(id)&viewName=jsonView'/>",
			       {id : jobName}, function(data) {
			    	   jQuery("#grid_job").trigger("reloadGrid");
		     });
	}
}

function stopDeployJob(txid){
	if(confirm('<spring:message code="job.confirm.stopjob"/>')){
		$.ajax( {
			url : "<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.stop(id)&viewName=jsonView&id='/>"+txid ,
			dataType : "json",
			async : true,
			success : function(result) {
				setTimeout('fn_addTab("03job", "Job", "job")', 500);
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
	t = setTimeout('jQuery("#grid_job").trigger("reloadGrid");', 1000);
}

function stopTimer(){
	clearTimeout(t);	
}
</script>
<div id="button" style="padding-bottom: 5px;">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<div class="pageSubtitle" style="padding-top: 10px;">
					<h3 class="subtitle_h3"><spring:message code="job.page.subtitle"/></h3>
				</div><!-- end pageSubtitle -->
			</td>
			<iam:access hasPermission="${iam:getPermissionMask(\"CREATE\")}" viewName="addUser">
				<td align=right style="padding-top: 10px;">
					<a name="addlink" href="#"><img src="<c:url value='/images/btn_job.gif'/>" alt="add" /></a>
				</td>
			</iam:access>
		</tr>
	</table>
</div>
<div id="body_job">
<form method="post"
	id="searchForm" name="searchForm">
	<div class="listbox" style="padding-top:2px;">
		<table id="grid_job" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>
	<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
	<div id="pager_job" class="scroll" style="text-align: center;"></div>
	<a id="getLink" name="getLink"></a>
</form></div>