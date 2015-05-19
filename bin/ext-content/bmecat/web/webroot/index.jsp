<%@include file="./inc/head.jspf"%>

<div class="absatz">
<a href="<%=response.encodeURL("/")%>">[Back to Administration web]</a>
</div>
<div class="absatz">
&nbsp;
</div>
<div class="absatz">

<h1>BMECat import</h1>

The assigned xml file and zip file will be imported.</br>
<p />
<p><br><p>
<table style="text-align: left; width: 100%;" border="0" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td>
				<form name="filesForm" action="fileUpload.jsp" method="post" enctype="multipart/form-data">
					xml file:&nbsp;<input size="80%" type="file" name="xmlsource" accept="text/*" value=""/>&nbsp;
					<p>
					zip file:&nbsp;<input size="80%" type="file" name="zipsource" accept="text/*" value=""/>&nbsp;
					<p></p>
					<input type="submit" name="submit" value="Import"/>
				</form>
			</td>
		</tr>
	</tbody>
</table>
<%@include file="./inc/tail.jspf"%>
