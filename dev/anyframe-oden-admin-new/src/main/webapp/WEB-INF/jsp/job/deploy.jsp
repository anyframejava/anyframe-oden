<%@ include file="/WEB-INF/jsp/layout/top.jsp"%>
  	
  	<script type="text/javascript">
  	$(document).ready(function() {
  		// set header menu selected style
  		setNavActive("nav_job"); 
  	}); 
  	
	app.controller("DeployListController", function ($scope, $http, ngTableParams) {

		$scope.columns = [
            { title: "Path", field: "path", visible: true, width: "60%"},
            { title: "Agents", field: "targets", visible: true, width: "30%" },
            { title: "Mode", field: "mode", visible: true, width: "10%" }
        ];
		
		$scope.init = function(){
			$scope.deploytype = "-i";
			$scope.chk_delete = "false";
			$scope.chk_zip = "false";
		};
		
		$scope.search = function(){
			console.log($scope.deploytype);
			console.log($scope.chk_delete);
			console.log($scope.chk_zip);
		};
		
		$http.post("${ctx}/job/deployList.do?job=${job}", {
		}).success(function(data, status, headers, config) {
	 		$scope.data = data;
			
	 		$scope.tableParams = new ngTableParams({
	             page: 1,            // show first page
	             count: 50           // count per page
	         }, {
	             total: 0, // length of data
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
		
	});             
	
	</script>
	
	<div>
		<h1 class="page-header"><spring:message code="deploy.page.subtitle" /> ${job} </h1>
		
	    <div ng-controller="DeployListController">
	    	<div id="filter-panel" class="filter-panel">
	            <div class="panel panel-default">
	                <div class="panel-body" ng-init="init()">
	                    <form class="form-inline" role="form">
                            <label class="filter-col" style="margin-right:0;" for="pref-deploytype">Deploy Type:</label>
                            <select id="pref-deploytype" class="form-control" ng-model="deploytype">
                                <option selected="selected" value="-i">Include</option>
                                <option value="-u">Update</option>
                            </select>
                            <div class="pull-right">
	                            <div class="checkbox" style="margin-left:10px; margin-right:10px;">
	                                <label><input type="checkbox" ng-model="chk_delete"> Delete</label>
	                            </div>
	                            <div class="checkbox" style="margin-left:10px; margin-right:10px;">
	                                <label><input type="checkbox" ng-model="chk_zip"> Zip</label>
	                            </div>
	                            <button ng-click="search()" class="btn btn-default filter-col" >Search</button>  
                            </div>
	                    </form>
	                </div>
	            </div>
	        </div>
	    
	    	<table ng-table="tableParams" class="table">
		    	<thead>
			        <tr>
			            <th ng-repeat="column in columns" ng-show="column.visible" class="text-center" width="column.width">{{column.title}}</th>
			        </tr>
			    </thead>
			    <tbody>
			    	<tr ng-repeat="rsc in data">
			            <td ng-repeat="column in columns" ng-show="column.visible" sortable="column.field">
			            	<div ng-if="column.field == 'path'" style="word-break:break-all;">{{rsc[column.field]}}</div>
			            	<div ng-if="column.field == 'targets'" class="text-center">
			            		<span ng-repeat="target in rsc.targets">[{{target}}]</span>
			            	</div>
			            	<div ng-if="column.field == 'mode'" class="text-center">
			            		<!-- image / text 결정 필요 -->
			            		<!-- 1. text -->
			            		{{rsc[column.field]}}
			            		<!-- 2. image -->
			            		<!-- 
			            		<div ng-switch on="rsc.mode">
				                	<span ng-switch-when="ADD"><img title="Add" src="<c:url value='/images/ico_mode_add.png'/>"/></span>
				                	<span ng-switch-when="UPDATE"><img title="Update" src="<c:url value='/images/ico_mode_update.png'/>"/></span>
				                	<span ng-switch-when="DELETE"><img title="Delete" src="<c:url value='/images/ico_mode_delete.png'/>"/></span>
			                	</div>
			                	-->
			            	</div>
			            </td>
			        </tr>
			    </tbody>
		    </table>
	    </div>
	</div>
<%@ include file="/WEB-INF/jsp/layout/bottom.jsp"%>	