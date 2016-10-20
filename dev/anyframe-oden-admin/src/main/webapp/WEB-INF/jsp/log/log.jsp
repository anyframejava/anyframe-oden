<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
	var filename;
	jQuery(document).ready(function() {
		$("#date").datepicker({dateFormat: "yy-mm-dd", autoSize:true});
		filename = '${logs.filename}';
		$("#date").val(chgDate(filename));
	});	
	function search() {
		filename = trim($("#date").val()).replaceAll("-","");
		$.ajax( {
			url : "<c:url value='/simplejson.do?layout=jsonLayout&initdataService=logService.findList(cmd)&initdataResult=logs&viewName=jsonView&cmd='/>"+filename ,
			dataType : "json",
			async : true,
			success : function(result) {
				$("#contents").val(result.autoData.contents);
			},
			error : function() {
				alert('<spring:message code="log.load.error"/>');
				$("#date").val("");
				$("#contents").val("");
			}
		});
			
	}
</script>
<div class="pageSubtitle" style="padding-top:10px;">
	<h3 class="subtitle_h3"><spring:message code="log.page.subtitle"/></h3>
</div><!-- end pageSubtitle --> 
<div id="body_log">
	<!--START: input table-->
	<fieldset>
		<legend>log search</legend>
		<div class="input_table" style="padding-top:2px;">
			<table summary="log">
				<caption>log search</caption>
				<tbody>
					<tr>
						<th width="80" scope="row"><label for="DateRange"><spring:message code="log.label.date"/></label></th>
						<td><input type="text" id="date" name="date" maxlength="10" value="" style="width:130px; height:18px;" /></td>
						<td width="120" align="right"><a href="javascript:search();"><img src="<c:url value='/images/btn_search.gif'/>" width="73" height="20" alt="search" /></a></td>
					</tr>
				</tbody>
			</table>
		</div>
	</fieldset>	
	<!--END: input table-->
	<div style="padding-top:10px;" id="scrollarea">
		<textarea id="contents" name="contents" class="textarea" cols="40" rows="5" style="width:888px; height:370px;" readonly="readonly">${logs.contents}</textarea>
	</div>
</div>
