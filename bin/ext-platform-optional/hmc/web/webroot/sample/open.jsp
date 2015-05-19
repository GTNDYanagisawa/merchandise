<%@include file="../head.inc"%>


<!--
 sample for a jsp which opens an editor of a given item
 put the following line into the <explorertree> area in 
 your hmc.tmpl of your extension
 
    <explorertree>
   	<staticcontent name="open current session user" uri="sample/open.jsp" />
	</explorertree>
-->

<%
		//get the item you want to open (e.g. perform a search).
		//in this sample we just use the current session user
		Item item = JaloSession.getCurrentSession().getUser();		
%>
		<input type="hidden" name="open" value="<%= item.getPK().toString() %>">
		<script language = "JavaScript1.2">
			setScrollAndSubmit();
		</script>
