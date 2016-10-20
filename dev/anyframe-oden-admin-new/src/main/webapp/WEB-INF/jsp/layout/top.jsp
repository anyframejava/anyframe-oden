<%@ include file="/common/taglibs.jsp"%>

<!doctype html>
<html ng-app="oden">
	<head>
	
	<link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css'/>" type="text/css"/>
	<link rel="stylesheet" href="<c:url value='/css/bootswatch-sandstone.css'/>" type="text/css"/>
	<link rel="stylesheet" href="<c:url value='/js/ngtable-0.3.2/ng-table.min.css'/>" type="text/css"/>
	
	<script type="text/javascript" src="<c:url value='/js/jquery-2.1.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/angularjs-1.3.6/angular-1.3.6.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/angularjs-1.3.6/angular-resource-1.3.6.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/bootstrap.min-3.3.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/ngtable-0.3.2/ng-table.min.js'/>"></script>

	<script type="text/javascript" src="<c:url value='/js/common.js'/>"></script>

	<title>Anyframe Oden Admin</title>
	
	<style type="text/css">
	div {display : block;}
	body > .container { padding: 100px 15px; min-height: 700px;}
	
	.ng-table {overflow-x:scroll;}
	
	.center {text-align: center; vertical-align: middle;}
	.left {text-align : left; vertical-align: middle;}
	.right {text-align : right; vertical-align: middle;}
	.red {color : #FF0000; }
	
	footer {margin: 0;}
	
	.footer {
    	position: relative;
		bottom: 0;
		width: 100%;
		height: 60px;
		background-color: #f5f5f5;
	}
	
	</style>
	
	<script type="text/javascript"> 
	function setNavActive(id){ // active navigation header menu
		$("ul#nav_menu li").removeClass("active");
		$("ul#nav_menu li#" + id).addClass("active");
	}
	</script>
	
	</head>
	
	<body>
		<header>
			<div class="navbar navbar-default navbar-fixed-top" role="navigation">
				<div class="container">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle collapsed"
							data-toggle="collapse" data-target="#navbar" aria-expanded="false"
							aria-controls="navbar">
							<span class="sr-only">Toggle navigation</span> <span
								class="icon-bar"></span> <span class="icon-bar"></span> <span
								class="icon-bar"></span>
						</button>
						<div class="navbar-brand" href="#">ODEN</div>
					</div>
					<div id="navbar" class="navbar-collapse collapse">
						<ul id="nav_menu" class="nav navbar-nav">
							<li id="nav_home"><a href="#">Home</a></li>
							<li id="nav_job"><a href="<c:url value='/job/listView.do'/>">Job</a></li>
							<li id="nav_history"><a href="<c:url value='/history/listView.do'/>">History</a></li>
							<li id="nav_status"><a href="#">Status</a></li>
							<li id="nav_log"><a href="#">Log</a></li>
							<li id="nav_user"><a href="#">User</a></li>
						</ul>
						<ul class="nav navbar-nav navbar-right">
							<li class="navbar-text">${userid} Logged in.</li>
							<li><a href="#">Logout</a></li>
						</ul>
					</div>
				</div>
			</div>
		</header>

		<div class="container">
		
