<%@ include file="/WEB-INF/jsp/layout/top.jsp"%>
  	
  	<script type="text/javascript">
  	$(document).ready(function() {
  		// set header menu selected style
  		setNavActive("nav_job"); 
  	}); 
  	
	app.controller("JobListController", function ($scope, $http, ngTableParams) {
		
		$http.post("<c:url value='/job/list.do'/>", {
		}).success(function(data, status, headers, config) {
	 		$scope.data = data;
			
	 		$scope.tableParams = new ngTableParams({
	             page: 1,            // show first page
	             count: 50           // count per page
	         }, {
	             total: 2, // length of data
	             counts: [],
	             getData: function($defer, params) {
	                 params.total(data.length);
	                 $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	             }
	         });
			
		}).error(function(data, status, headers, config) {
		    console.log("Loading data failed. ");
		    return status;
		});
		
		$scope.columns = [
            { title: "Job", field: "name", visible: true, width: "10%"},
            { title: "Build", field: "buildHistory", visible: true, width: "25%" },
            { title: "Deploy", field: "deployHistory", visible: true, width: "25%" },
            { title: "Build", field: "enableBuildService", visible: true, width: "5%" },
            { title: "Deploy", field: "enableDeploy", visible: true, width: "5%" },
            { title: "Clean", field: "enableCleanDeploy", visible: true, width: "5%" },
            { title: "Compare", field: "enableCompare", visible: true, width: "5%" },
            { title: "Script", field: "enableRunScript", visible: true, width: "5%" },
            { title: "Rollback", field: "enableRollback", visible: true, width: "5%" }
        ];
		
	});
	</script>
	
	<div>
		<h1 class="page-header"><spring:message code="job.page.subtitle" /></h1>
		
	    <div ng-controller="JobListController">
	    	<table ng-table="tableParams" class="table">
		    	<thead>
			        <tr>
			            <th ng-repeat="column in columns" ng-show="column.visible" class="text-center" width="column.width">
			                <div>{{column.title}}</div>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			    	<tr ng-repeat="job in $data">
		                <td class="text-center">
		                    <a href="#">{{job.name}}</a>
		                </td>
		                <td class="text-center">
		                	<div ng-if=!job.buildHistory></div>
		                	<div ng-if=job.buildHistory>
			                	<div ng-switch on="job.buildHistory.success">
				                	<span ng-switch-when="true"><img src="<c:url value='/images/accept.png'/>"/><a href="{{job.buildHistory.consoleUrl}}">{{job.buildHistory.date}}</a></span>
				                	<span ng-switch-when="false"><img src="<c:url value='/images/exclamation.png'/>"/><a href="{{job.buildHistory.consoleUrl}}">{{job.buildHistory.date}}</a></span>
			                	</div>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-if=!job.deployHistory></div>
		                	<div ng-if=job.deployHistory>
			                	<div ng-switch on="job.deployHistory.status">
				                	<span ng-switch-when="S"><img src="<c:url value='/images/accept.png'/>"/><a href="${ctx}/history/detailView.do?txId={{job.deployHistory.txid}}">{{job.deployHistory.date}}</a></span>
				                	<span ng-switch-when="F"><img src="<c:url value='/images/exclamation.png'/>"/><a href="${ctx}/history/detailView.do?txId={{job.deployHistory.txid}}">{{job.deployHistory.date}}</a></span>
			                	</div>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableBuildService">
			                	<span ng-switch-when="true"><a href="#"><img src="<c:url value='/images/ico_build.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_build_d.gif'/>"/></span>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableDeploy">
			                	<span ng-switch-when="true"><a href="${ctx}/job/deployView.do?job={{job.name}}"><img src="<c:url value='/images/ico_deploy.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_deploy_d.gif'/>"/></span>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableCleanDeploy">
			                	<span ng-switch-when="true"><a href="#"><img src="<c:url value='/images/ico_cleandeploy.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_cleandeploy_d.gif'/>"/></span>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableCompare">
			                	<span ng-switch-when="true"><a href="#"><img src="<c:url value='/images/ico_compare.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_compare_d.gif'/>"/></span>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableRunScript">
			                	<span ng-switch-when="true"><a href="#"><img src="<c:url value='/images/ico_runscript.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_runscript_d.gif'/>"/></span>
		                	</div>
		                </td>
		                <td class="text-center">
		                	<div ng-switch on="job.enableRollback">
			                	<span ng-switch-when="true"><a href="#"><img src="<c:url value='/images/ico_rollback.gif'/>"/></a></span>
			                	<span ng-switch-when="false"><img src="<c:url value='/images/ico_rollback_d.gif'/>"/></span>
		                	</div>
		                </td>
		            </tr>
			    </tbody>
	        </table>
	    </div>
	</div>
<%@ include file="/WEB-INF/jsp/layout/bottom.jsp"%>	