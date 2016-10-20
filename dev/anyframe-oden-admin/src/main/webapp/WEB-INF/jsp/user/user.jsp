<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%
String jobName = (String)request.getParameter("para");
String roles = (String) session.getAttribute("userrole");

if(! roles.equals("ROLE_ADMIN,")) {
	response.sendRedirect(request.getContextPath() + "/j_spring_security_logout");
}
%>
<script type="text/javascript">

var isModify = false;

jQuery(document).ready(function() {
	jQuery("#grid_user").jqGrid( {

		url : "<c:url value='/simplejson.do?layout=jsonLayout&service=userService.findList(cmd)&viewName=jsonView&cmd='/>"+ '<%=roles%>',
		mtype : 'POST',
		datatype : "json",
		colNames : [ '<spring:message code="user.grid.userid"/>',
		    		 '<spring:message code="user.grid.role"/>',
		     		 '<spring:message code="user.grid.job"/>',
		     		 '<spring:message code="user.grid.action"/>',
		     		 '<spring:message code="user.grid.hiddenpw"/>'],
		jsonReader : {
			repeatitems : false
		},
		colModel : [{
			name : 'userId',
			index : 'userId',
			align : 'left',
			width : 150,
			key : true,
			sortable:false
		}, {
			name : 'role',
			index : 'role',
			align : 'left',
			width : 150,
			sortable:false
		}, {
			name : 'job',
			index : 'job',
			align : 'left',
			width : 450,
			sortable:false
		}, {
			name : 'hidden',
			index : 'hidden',
			align : 'center',
			width : 50,
			sortable:false
		}, {
			name : 'password',
			hidden : true
		}],
		width : 888,
		height : 100,
		scroll : false,
		pgbuttons: false,
	   	pgtext: false,
	   	pginput:false,
		forceFit : true,
		viewrecords : true,
		sortable : true,
		rowNum : -1,
		rownumbers: true,
		pager : jQuery('#pager_user'),

		onSelectRow: function(id){ 
			isModify = true;
		    setUserDetail(id);
		},

		loadError : function(xhr, st, err) {
			alert('<spring:message code="user.load.error"/>');
		}
	});

	$("#role_list").change(function(){
		var selected = $("#role_list").val(); 
		disOrEnableInput(selected);
	});
});

function disOrEnableInput(role){
	if(role == 'Admin'){
		disableInputs();
	}else{
		enableInputs();
	}
}

function disableInputs(){
	$('#add_job_list').attr("disabled", "disabled");
	$('#all_job_list').attr("disabled", "disabled");

	$('#addJob').attr("href", '#');
	$('#addAllJob').attr("href", '#');
	$('#removeJob').attr("href", '#');
	$('#removeAllJob').attr("href", '#');
}

function enableInputs(){
	$('#add_job_list').attr("disabled", "");
	$('#all_job_list').attr("disabled", "");

	$('#addJob').attr("href", 'javascript:addSelectJob();');
	$('#addAllJob').attr("href", 'javascript:addAllJobs();');
	$('#removeJob').attr("href", 'javascript:removeSelectJob();');
	$('#removeAllJob').attr("href", 'javascript:removeAllJobs();');
}

function addUser(){
	enableInputs();
	if(isModify){
		clearUserDetail();
		isModify = false;
	}else{
		isModify = false;
		openDetail();
	}
	openSaveButton('');
}

function setUserDetail(id){
	openDetail();
	$.getJSON("<c:url value='/simplejson.do?layout=jsonLayout&service=userService.findUser(id)&viewName=jsonView&id='/>"+ id, function(data){
		var userid = data.autoData.userId;
		$("#user_id").val(userid);
		$("#user_pw").val(data.autoData.password);
		$("#user_re_pw").val(data.autoData.password);

		openSaveButton(userid);
		
		var role = data.autoData.role;
		$("#role_list option[value="+role+"]").attr("selected", "selected");
		disOrEnableInput(role);
		$("#user_id").attr("disabled", "disabled");
		var jobs = data.autoData.job;
		setComparedJobList(jobs);
	});
}

function setComparedJobList(jobs){
	
	var allJobs = new Array();
	var substr = jobs.split(',');
	var substr_len = substr.length;
	var allJobList = '';
	var assignJobList = '';

	$.getJSON("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findJob()&viewName=jsonView'/>", function(data){
		var length = data.autoData.length;
		for(var i=0; i<length; i++){
			var flag = false;
			var jobName = data.autoData[i].name;
			if(substr_len>0){
				for(var n=0; n<substr_len; n++){
					if(trim(jobName) == trim(substr[n])){
						assignJobList += '<option>' + jobName + '</option>';
						flag = true;
						break;	
					}
				}
				if(!flag){
					allJobList += '<option>' + jobName + '</option>';
				}
			}
		}
		$("#all_job_list").html(allJobList);
		$("#add_job_list").html(assignJobList);
	});
}

function setAllJobList(){
	$.getJSON("<c:url value='/simplejson.do?layout=jsonLayout&service=jobService.findJob()&viewName=jsonView'/>", function(data){
		var length = data.autoData.length;
		var markup = '';
		for(var i=0; i<length; i++){
			markup += '<option>' + data.autoData[i].name + '</option>';
		}
		$("#all_job_list").html(markup);
	});
}

function openSaveButton(userid){
	var arr = document.getElementById("saveButton");
	if ( arr!= null ) {
		if(userid == "oden"){
			arr.style.display = 'none';
		}else{
			arr.style.display = 'block';
		}
	}
}

function openDetail(){
	var arr = document.getElementById("userDetail");
	if ( arr!= null ) { 
		if(isModify){
			if(arr.style.display == 'none'){
				arr.style.display = 'block';
			}
		}else{
			if(arr.style.display == 'none'){
				arr.style.display = 'block';
				clearUserDetail();
			}else{
				arr.style.display = 'none';
			}
		}
	} 
}

function clearUserDetail(){
	$("#role_list option:eq(1)").attr("selected", "selected");
	$("#user_id").val('');
	$("#user_pw").val('');
	$("#user_re_pw").val('');
	$("#add_job_list").html('');
	$("#user_id").attr("disabled", "");
	setAllJobList();
}

function saveUserAction(){
	if(confirm('<spring:message code="user.confirm.save"/>')){
		saveUser();
	}
}

function saveUser(){
	if($("#user_id").val()==""){
		alert('<spring:message code="user.alert.idempty"/>');
	}else if($("#user_pw").val()==""){
		alert('<spring:message code="user.alert.pwempty"/>');
	}else if($("#user_re_pw").val()==""){
		alert('<spring:message code="user.alert.repwempty"/>');
	}else if($("#user_pw").val() == $("#user_re_pw").val()){
		var role = $("#role_list").val();
		var id = $("#user_id").val();
		var pw = $("#user_pw").val();
		var jobList = new Array();
		
		var length = $("#add_job_list option").length;
		var jobListIsValid = false;
		for(var i=0; i<length; i++){
			jobList[i] = $("#add_job_list option:eq("+i+")").text();
			if(isValidString(jobList[i])){
				jobListIsValid = true;	
				break;
			}else{
			}
		}

		if(role == 'Admin'){
			jobList = new Array();
			jobList[0] = 'ROLE_ADMIN';
		}


		if(isValidString(id) || isValidString(pw) || jobListIsValid){
			alert('<spring:message code="jobdetail.alert.invalidcharacter"/>');	
		}else if(jobList.length < 1){
			alert('<spring:message code="user.alert.nonejob"/>');
		}else{
			if(isModify){
				$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=userService.updateUser(role,id,pw,jobs)&viewName=jsonView'/>",
				       {
				       role : role,
				       id : id,
				       pw : pw,
				       jobs :  jobList
				       }, function(data) {
				    	   javascript:fn_addTab('07user', 'User');
			     });
			}else{
				$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=userService.createUser(role,id,pw,jobs)&viewName=jsonView'/>",
				       {
				       role : role,
				       id : id,
				       pw : pw,
				       jobs :  jobList
				       }, function(data) {
				    	   javascript:fn_addTab('07user', 'User');
			     });
			}
		}
	}else{
		alert('<spring:message code="user.alert.wrongpassword"/>');
	}
}

function deleteUser(id){
	if(confirm('<spring:message code="user.confirm.removeuser"/>')){
		$.post("<c:url value='/simplejson.do?layout=jsonLayout&service=userService.removeUser(id)&viewName=jsonView'/>",
		       {id : id}, function(data) {
		    	   javascript:fn_addTab('07user', 'User');
	     });
	}
}

function addSelectJob(){
 	$("#all_job_list option:selected").each(function(){
     	$(this).appendTo("#add_job_list");
    });
}

function addAllJobs(){
	$("#all_job_list option").each(function(){
     	$(this).appendTo("#add_job_list");
    });
}

function removeSelectJob(){
	$("#add_job_list option:selected").each(function(){
     	$(this).appendTo("#all_job_list");
    });
}

function removeAllJobs(){
	$("#add_job_list option").each(function(){
     	$(this).appendTo("#all_job_list");
    });
}
</script>
<div id="button" style="padding-bottom: 5px;">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<div class="pageSubtitle" style="padding-top: 10px;">
					<h3 class="subtitle_h3"><spring:message code="user.page.subtitle"/> </h3>
				</div><!-- end pageSubtitle -->
			</td>
			<td align=right style="padding-top: 10px;">
				<a href="javascript:addUser();"><img src="<c:url value='/images/btn_add.gif'/>" alt="add" /></a>
			</td>
		</tr>
	</table>
</div>

<div id="body_user">
<form method="post"
	id="searchForm" name="searchForm">
	<div class="listbox" style="padding-top:2px;">
		<table id="grid_user" class="scroll" cellpadding="0" cellspacing="0"><tr><td/></tr></table>
	</div>
	<input type="hidden" id="pageIndex" name="pageIndex" value="1" />
	<div id="pager_user" class="scroll" style="text-align: center;"></div>
	<a id="getLink" name="getLink"></a>
	
	<div id="userDetail" style="display:none;">
		<div class="pageSubtitle" style="padding-top:10px;">
			<h4 class="subtitle_h4"><spring:message code="user.userdetail.subtitle"/> </h4>
		</div>
		<fieldset>
			<legend>register</legend>
			<div class="input_table" style="padding-top:2px;">
				<table summary="user">
					<caption>user</caption>
					<tbody>
						<tr>
							<th scope="row"><label for="user_id"><spring:message code="user.label.userid"/> </label></th>
							<td><input type="text" id="user_id" name="user_id" size="40" style="height:18px;width:250px;" /></td>
							<simpleweb:validate id="user_id" required="true" promptMessage="Enter User ID."/>
							<td></td>
							<th scope="row"><label for="name"><spring:message code="user.label.role"/> </label></th>
							<td>
								<select id="role_list" class="selectbox" style='width:100'>
									<option value="Admin"><spring:message code="user.select.admin"/> </option>
									<option value="Deployer"><spring:message code="user.select.deployer"/> </option>
								</select>
							</td>
						</tr>
						<tr>
							<th scope="row"><label for="user_pw"><spring:message code="user.label.passworld"/> </label></th>
							<td><input type="password" id="user_pw" name="user_pw" size="40" style="height:18px;width:300px;" /></td>
							<simpleweb:validate id="user_pw" required="true" promptMessage="Enter Password."/>
							<td></td>
							<th scope="row"><label for="user_re_pw"><spring:message code="user.label.confirmpw"/> </label></th>
							<td><input type="password" id="user_re_pw" name="user_re_pw" size="40" style="height:18px;width:250px;" /></td>
							<simpleweb:validate id="user_re_pw" required="true" promptMessage="Enter Password again."/>
						</tr>
						<tr>
							<th scope="row" colspan="5"><label for="job"><spring:message code="user.label.assignjob"/> </label></th>
						</tr>
						<tr>
							<td colspan="2" rowspan="6" align="center">
								<select id="all_job_list" size="10" class="user_job_list" ondblclick="javascript:addSelectJob();">
								</select>
							</td>
							<td></td>
							<td colspan="2" rowspan="6" align="center">
								<select id="add_job_list" size="10" class="user_job_list" ondblclick="javascript:removeSelectJob();">
								</select>
							</td>
						</tr>
						<tr>
							<td align="center"><a id="addJob" href="javascript:addSelectJob();">
								<img src='<c:url value='/images/btn_move.png'/>' style='vertical-align:middle;align:center;'/>
							</a></td>
						</tr>
						<tr>
							<td align="center"><a id="addAllJob" href="javascript:addAllJobs();">
								<img src='<c:url value='/images/btn_move_all.png'/>' style='vertical-align:middle;align:center;'/>
							</a></td>
						</tr>
						<tr>
							<td align="center"><a id="removeJob" href="javascript:removeSelectJob();">
								<img src='<c:url value='/images/btn_remove.png'/>' style='vertical-align:middle;align:center;'/>
							</a></td>
						</tr>
						<tr>
							<td align="center"><a id="removeAllJob" href="javascript:removeAllJobs();">
								<img src='<c:url value='/images/btn_remove_all.png'/>' style='vertical-align:middle;align:center;'/>
							</a></td>
						</tr>
						<tr>
							<td></td>
						</tr>
						<tr>
							<td colspan="2" align="center"><spring:message code="user.label.alljoblist"/> </td>
							<td></td>
							<td colspan="2" align="center"><spring:message code="user.label.assignedjoblist"/> </td>
						</tr>
						<tr>
							<td class="btn_pad" colspan="5" align="right">
								<div id="saveButton" align="right" style="display:block;">
									<table border="0" cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td align="right">
												<a href="javascript:saveUserAction();"><img src="<c:url value='/images/btn_save.gif'/>" width="55" height="22" alt="save" /></a> 
											</td>
										</tr>
									</table>
								</div>	
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</fieldset>	
	</div>
</form>
</div>