<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<% String resId = (String)request.getParameter("tabId"); 
   String resNm = (String)request.getParameter("tabName");
   String resPara = (String)request.getParameter("keyParam"); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Anyframe Oden Admin</title>

	<!-- dojo -->
	<script type="text/javascript" src="<c:url value="/resources/dojo/dojo.js" />"></script>  
    <script type="text/javascript" src="<c:url value="/resources/dojo/io/iframe.js" />"></script>  
    <script type="text/javascript" src="<c:url value="/resources/org/anyframe/spring/Anyframe-Spring.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/org/anyframe/spring/Anyframe-Spring-Dojo.js" />"></script>
    
	<!-- for jquery -->
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.4.2.min.js'/>"></script>
	
	<!-- jquery ui, jqGrid -->
	<script type="text/javascript" src="<c:url value='/jquery/jqgrid/i18n/grid.locale-en.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jquery/jquery-ui/jquery-ui-1.7.2.custom.min.js'/>"></script>
	<link href="<c:url value='/jquery/jqgrid/ui.jqgrid.css'/>" rel="stylesheet" type="text/css" /> 
	<script type="text/javascript" src="<c:url value='/jquery/jqgrid/jquery.jqGrid.min.js'/>"></script>
	
	<!-- jquery form -->
	<script type="text/javascript" src="<c:url value='/jquery/form/jquery.form.js'/>"></script>
	
	<!-- jquery tab -->
	<link href="<c:url value='/jquery/jquery-ui/jquery-ui.css'/>" rel="stylesheet" type="text/css" />
		
	<!-- custom -->
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/dijit/themes/tundra/tundra.css" />" />
	<link rel="stylesheet" href="<c:url value='/css/left.css'/>" type="text/css">
	<link rel="stylesheet" href="<c:url value='/css/tundra-customized.css'/>" type="text/css">
	<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/jquery/jquery-ui/ui-lightness/jquery-ui-1.7.2.custom.css'/>" type="text/css">
    
<script>
jQuery(document).ready(function() {
	$('#body').show();
	$('#body_img').hide();
	fn_addTab('03job', 'Job', '', '&initdataService=groupService.findGroupAndUngroup()&initdataResult=groupUngroups', 'ALL');
});
</script>
</head>
<body class="tundra spring">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td height="79">
				<tiles:insertAttribute  name="top"/>
		</td>
	</tr>
	<tr>
		<td valign="top">
		<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="65" valign="top" class="leftline">
					<tiles:insertAttribute  name="left"/>
				</td>
				<td align="left" valign="top" style="padding: 0px 0px 0px 15px;">
					<!---- Body ------>
						<input type="hidden" id="keyParam" name="keyParam" value=""/>
						<input type="hidden" id="nameParam" name="nameParam" value=""/>
						<input type="hidden" id="openCnt" name="openCnt" value=""/>
						
						<div id="body_img" style="top:150px; width:100%; padding-top:10px; margin:0 auto; text-align:center;">
							<img src="<c:url value='/images/welcome.jpg'/>" width="674" height="380" alt="welcome" />
						</div><!-- end pageSubtitle --> 
			
						<div id="body">
							<ul></ul>
						</div>
				</td>
			</tr>
		</table> 
		</td>
	</tr>
</table>

<script type="text/javascript">

	document.charset="UTF-8"; //
  	
    function fn_addTab(id, name, data, para, para1, para2){
    		$('#body').show();
    		$('#body_img').hide();
    		$('#nameParam').val(name);
    		
    		var tabId = "";
    		var dtlURL = "";
			
    		if(data == null || data.length == 0){
        		tabId = id.substring(0,2);
        		dtlURL = id.substring(2);
        	}else{
    			tabId = id.substring(0,2);
        		dtlURL = data;
    		}

    		var appendPara = "";
    		var appendPara1 = "";
    		
    		if(para == null || para.length == 0){
    		}else{
    			appendPara = "&para="+para;
    		}

    		if(para1 !== null || para1.length !== 0){
    			appendPara1 = "&para1="+para1;
        	}
    		
    		if(para2 !== null || para2.length !== 0){
    			appendPara2 = "&para2="+para2;
        	}
    		var url = "<c:url value='/simplejson.do?layout="+dtlURL+"Layout" + appendPara + appendPara1 + appendPara2 + "'/>";
    		//alert(url);
			var maintab =jQuery('#body').tabs({
		        	add: function(event, ui) {
			            $(ui.tab).parents('li:first')
			                .append('<span class="ui-tabs-close ui-icon ui-icon-close" title="Close Tab"></span>')
			                .find('span.ui-tabs-close')
			                .click(function() {
			                    maintab.tabs('remove', $('li', maintab).index($(this).parents('li:first')[0]));
			                   if($('#body li:first').size()==0){
			                    	$('#body').hide();
			                    	$('#body_img').show();
			                    }
			                });
						maintab.tabs('select', '#' + ui.panel.id);
			        },
			        select: function(event, ui){
			        	var gridName = '#grid_' + dtlURL;
			    		$(gridName).trigger('reloadGrid');
					},
			        remote: true, 
			        cache: true, 
			        ajaxOptions: { async: true }, 
			        collapsible: false
		    });

	    	var st = "#anyPF"+tabId;
    		var gridName = '#grid_' + dtlURL;
    		
	    	if($(st).html() != null){ //already open tab
		    	// change page in tab
	    		var sendUrl = encodeURI(url);
	    		$(st, '#body').load(sendUrl);
	    		$('#body').tabs('select',st);
	    		$(gridName).trigger('reloadGrid');
	    	}else{
	    		var sendUrl = encodeURI(url);
				$('#body').tabs('add', st , name);
				$(st, '#body').load(sendUrl);
				$('#body').tabs('select',st);
			}

	}

	function trim(str) {
	    return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
	}

	function replacePath(str) {
		var sl = "\\";
		var result = "";
		if(str.indexOf(sl) != -1) {
			while(str.indexOf(sl) != -1){
				str = str.replace(sl, "/");
			}
			result = str;
		}else{
			result = str;
		}
		return result;
	}
	
	String.prototype.replaceAll = function(tgt, repl) {
		if(arguments.length != 2) return this;

		var result = this;
		if(this != "" && tgt != repl) {
			while(result.indexOf(tgt) != -1){
				result = result.replace(tgt, repl);
			}
		}
		return result;
	};

	function chgDate(tgt) {
		if(arguments.length != 1) return this;
		return tgt.substr(0,4) + "-" + tgt.substr(4,2) + "-" + tgt.substr(6,2);
	}

	function isValidString(str){

		var strArray = new Array("\"","<",">","!","`","@","#","%","^","&",";","'","+","$");
		
		for(var i=0; i<strArray.length; i++){
			if(str.indexOf(strArray[i]) != -1){
				return true;
			}else{
			}
		}
		return false;
	}
  </script>
  
</body>
</html>