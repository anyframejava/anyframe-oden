<%@ include file="/WEB-INF/jsp/layout/top.jsp"%>
  	
  	<script type="text/javascript">
  	$(document).ready(function() {
  		// set header menu selected style
  		setNavActive("nav_history"); 
  	}); 
  	
	app.controller("HistoryDetailListController", function ($scope, $http, ngTableParams) {
		
		$http.post("${ctx}/history/view.do?txId=${txId}", {
		}).success(function(data, status, headers, config) {
			$scope.data = data.data;
			
			var agents = data.agents;
			
			var columnWidth = 50 / agents.length;
			if(columnWidth < 10){
        		columnWidth = "100px";
        	}else{
        		columnWidth += "%";
        	}
			
			var columns = new Array();
			columns.push({ title: "Path", field: "path", visible: true, width: "50%"});
			agents.forEach(function(entry, index){
				columns.push({ title: entry, field: entry, visible: true, width: columnWidth});
			});
			
			$scope.columns = columns;
			
	 		$scope.tableParams = new ngTableParams({
	             page: 1,            // show first page
	             count: 50           // count per page
	         }, {
	             total: 0, // length of data
	             counts : [],
	             getData: function($defer, params) {
	                 params.total(data.data.length);
	                 $defer.resolve(data.data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	             }
	         });
			
		}).error(function(data, status, headers, config) {
		    console.log("Loading data failed. ");
		    return status;
		});
	});
	
	app.directive("bsPopover", function() {
	    return function(scope, element, attrs) {
	        element.find("a[rel=popover]").popover({ placement: "bottom", html: "true"});
	    };
	});
	</script>
	
	<div>
		<h1 class="page-header"><spring:message code="history.page.subtitle" /></h1>
		
	    <div ng-controller="HistoryDetailListController">
			<table ng-table="tableParams" class="table">
		    	<thead>
			        <tr>
			            <th ng-repeat="column in columns" ng-show="column.visible" class="text-center" width="column.width">{{column.title}}</th>
			        </tr>
			    </thead>
			    <tbody>
			    	<tr ng-repeat="history in data">
			            <td ng-repeat="column in columns" ng-show="column.visible" sortable="column.field">
			            	<div ng-if="column.field == 'path'" style="word-break:break-all;">{{history[column.field]}}</div>
			            	<div ng-if="column.field !== 'path'" class="text-center">
			            		<span ng-repeat="target in history.targetList">
			            			<div ng-if="target.name == column.field && target.status == 'true'"><img src="<c:url value='/images/accept.png'/>"/>{{history.mode}}</div>
			            			<div ng-if="target.name == column.field && target.status == 'false'" bs-popover>
			            				<a href="#" rel="popover" data-original-title="Error Log" data-content="{{history.errorLog}}"><img src="<c:url value='/images/exclamation.png'/>"/>{{history.mode}}</a>
			            			</div>
			            		</span>
			            	</div>
			            </td>
			        </tr>
			    </tbody>
		    </table>
	    </div>
	</div>
<%@ include file="/WEB-INF/jsp/layout/bottom.jsp"%>	/