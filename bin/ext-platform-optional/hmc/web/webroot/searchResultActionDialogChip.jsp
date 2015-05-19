<%@include file="head.inc" %>
<%
	final SearchResultActionDialogChip theChip = (SearchResultActionDialogChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<div class="searchResultActionDialogChip">
	<%= localized("searchresultaction.select.dialog") %>
</div>
<%
	theChip.getBooleanEditor().render(pageContext);
%>

