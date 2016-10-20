<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	
		<title>User List</title>
	</head>

	<body>
		<div class="header">
			<h3 class="text-muted">User List</h3>
		</div>

		<div id="container1" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
		<br/><br/>
		
		<div id="container2" style="min-width: 310px; height: 400px; margin: 0 auto"></div>		
		<br/><br/>
		
		<div id="container3" style="min-width: 310px; height: 400px; margin: 0 auto">
			<div class="input-group">
				<span class="input-group-addon">Search Filter</span>
				<input id="filter" type="text" class="form-control" placeholder="Type here..."/>
			</div>

			<table id="container3_table1" class="table header-fixed">
              <thead>
                <tr>
                  <th>Category</th>
                  <th>Rule</th>
                  <th>Valid</th>
                  <th>Resource</th>
                  <th>Line</th>
				  <th>Message</th>
                </tr>
              </thead>
			  <tfoot>
				<tr>
				  <th>Category</th>
                  <th>Rule</th>
                  <th>Valid</th>
                  <th>Resource</th>
                  <th>Line</th>
				  <th>Message</th>
				</tr>
			</tfoot>
	
              <tbody class="searchable">
                <tr>
                  <td>class</td>
                  <td>classNameRule</td>
                  <td>TRUE</td>
                  <td>myproject.common.aspect.ExceptionTransfer</td>
                  <td>44</td>
				  <td></td>
                </tr>
                <tr>
                  <td>class</td>
                  <td>classForPackage</td>
                  <td>FALSE</td>
                  <td>myproject.common.aspect.LoggingAspect</td>
                  <td>44</td>
				  <td></td>
                </tr>
              </tbody>
			</table>
		</div>
		
	</body>

</html>
