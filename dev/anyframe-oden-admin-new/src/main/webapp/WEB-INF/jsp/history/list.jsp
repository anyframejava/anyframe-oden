<%@ include file="/WEB-INF/jsp/layout/top.jsp"%>
  	
  	<script type="text/javascript">
  	$(document).ready(function() {
  		// set header menu selected style
  		setNavActive("nav_history"); 
  	}); 
  	
	app.controller("HistoryListController", function ($scope, $http, ngTableParams) {
		
		$http.post("<c:url value='/history/list.do'/>", {
		}).success(function(data, status, headers, config) {
	 		$scope.data = data;
			
	 		$scope.tableParams = new ngTableParams({
	             page: 1,            // show first page
	             count: 100           // count per page
	         }, {
	             total: 0, // length of data
	             counts : [],
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
            { title: "Status", field: "status", visible: true, width: "20%"},
            { title: "Job", field: "job", visible: true, width: "30%" },
            { title: "Date", field: "date", visible: true, width: "20%" },
            { title: "Count", field: "nsuccess", visible: true, width: "15%" },
            { title: "User", field: "user", visible: true, width: "15%" }
        ];
		
	});
	</script>
	
	<div>
		<h1 class="page-header"><spring:message code="history.page.subtitle" /></h1>
		
	    <div ng-controller="HistoryListController">
	    	<table ng-table="tableParams" class="table">
		    	<thead>
			        <tr>
			            <th ng-repeat="column in columns" ng-show="column.visible" class="text-center" width="column.width">
			                <div>{{column.title}}</div>
			            </th>
			        </tr>
			    </thead>
			    <tbody>
			    	<tr ng-repeat="history in data">
			            <td ng-repeat="column in columns" ng-show="column.visible" sortable="column.field" class="text-center">
			            	<div ng-if="column.field == 'status'">
			            		<div ng-switch on="history.status">
				                	<span ng-switch-when="S"><img src="<c:url value='/images/accept.png'/>"/><a href="${ctx}/history/detailView.do?txId={{history.txid}}">{{history.txid}}</a></span>
				                	<span ng-switch-when="F"><img src="<c:url value='/images/exclamation.png'/>"/><a href="${ctx}/history/detailView.do?txId={{history.txid}}">{{history.txid}}</a></span>
			                	</div>
			            	</div>
			            	<div ng-if="column.field == 'nsuccess'">
			            		<span ng-class="{red: history.nsuccess !== history.total}">{{history.nsuccess}}/{{history.total}}</span>
			            	</div>
			            	<div ng-if="column.field !== 'status' && column.field !== 'nsuccess'">
				            	{{history[column.field]}}
			            	</div>
			               
			            </td>
			        </tr>
			    </tbody>
		    </table>
	    </div>
	</div>
<%@ include file="/WEB-INF/jsp/layout/bottom.jsp"%>	