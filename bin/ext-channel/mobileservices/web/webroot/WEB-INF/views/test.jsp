<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="_header.jsp"> 
</c:import>
<%!
// simple table row cycling decorator
private static int c=0;
public String row()
{
	if ((++c)%2==0) {
		return "odd";
	}
	else {
		return "table-even-row";
	}
}
%>
<style type="text/css">
.sortableTableContainer {
	overflow: auto;
	border: 1px dotted #1246C8;
	padding: 5px;
	margin: 10px 0;
}

.sortableTable th {
	padding: 2px 4px 4px 4px;
	border-right: 1px solid #eee;
}

.sortableTable td {
	padding: 2px 4px;
}

.sortableTable .even td {
	border-right: 1px solid #eee;
}

.sortableTable .odd td {
	border-right: 1px solid #ffffff;
	background-color: #eee;
}

.sortableTable td.odd {
	border-right: 1px solid #ffffff;
	background-color: #eee;
}

.table-header {
	border-bottom: solid 1px blue;
	text-align: left;
	font-size: 90%;
}

.table-column {
	text-align: left;
	font-size: 90%;
}

.<%=row()%> {
	background-color: #E7EFFC;
	text-align: left;
}

.table2009 th {
	background-color: #285ccf;
	color: #ffffff;
	padding: 4px;
	line-height: 0.9em;
}

</style>

<div id="content">
	<div class="absatz" style="padding-bottom: 20px;">
		<h1>Test Summary</h1>
	</div>
	<br/>
	<div class="absatz" style="padding-bottom: 20px;">
		Command processed. Please refresh the page (F5) to check how the message status is updated
	</div>
	
	<div class="absatz" style="padding-bottom: 20px;">
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Normalized Input Parameters
				</th>
			</tr>
			</thead>
			<tbody>
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Phone:</td><td class="table-column">(${msg.phoneCountryIsoCode})${msg.phoneNumber}</td>
				</tr>
				<c:if test="${!empty msg.shortcode}">
				<tr class="<%=row()%>">
					<td class="table-column">Shortcode:</td><td class="table-column">(${msg.countryIsoCode})${msg.shortcode }</td>
				</tr>
				</c:if>
				<tr class="<%=row()%>">
					<td class="table-column">Type:</td><td class="table-column">${msg.type }</td>
				</tr>
				<c:if test="${!empty msg.user}">
				<tr class="<%=row()%>">
					<td class="table-column">Customer:</td><td class="table-column">${msg.user }</td>
				</tr>
				</c:if>
				<c:if test="${!empty msg.outgoingShortcode}">
				<tr class="<%=row()%>">
					<td class="table-column">Output Shortcode:</td><td class="table-column">${msg.outgoingShortcode.code }</td>
				</tr>
				</c:if>
				<c:if test="${!empty msg.matchedShortcode}">
				<tr class="<%=row()%>">
					<td class="table-column">Matched Shortcode:</td><td class="table-column">${msg.matchedShortcode.code }</td>
				</tr>
				</c:if>
				
			</tbody>
		</table>
		<c:if test="${!empty msg.matchedActionAssignment}">
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Message Matching
				</th>
			</tr>
			</thead>
			<tbody>
				<c:if test="${msg.usingDefaultAction}">
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Using default action:</td><td class="table-column">YES</td>
				</tr>
				</c:if>
				<c:if test="${! msg.usingDefaultAction}">
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Using default action:</td><td class="table-column">NO</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Keyword:</td><td class="table-column">${msg.matchedActionAssignment.keyword.keyword }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Keyword type:</td><td class="table-column">${msg.matchedActionAssignment.keyword.type }</td>
				</tr>
				</c:if>
				
				<tr class="<%=row()%>">
					<td class="table-column">Action:</td><td class="table-column">${msg.matchedAction.code }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Action type:</td><td class="table-column">${msg.matchedAction.itemtype }</td>
				</tr>
				
				
			</tbody>
		</table>
		</c:if>
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Message Status
				</th>
			</tr>
			</thead>
			<tbody>
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Status:</td><td class="table-column">${msg.status }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Failures:</td><td class="table-column">${msg.failures }</td>
				</tr>
				<c:if test='${!empty msg.messageError}'>
				<tr class="<%=row()%>">
					<td class="table-column">Error:</td><td class="table-column">${msg.messageError.code }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Error text:</td><td class="table-column">${msg.messageErrorDescription}</td>
				</tr>
				</c:if>
				<c:if test='${!empty msg.aggregatorError}'>
				<tr class="<%=row()%>">
					<td class="table-column">Aggregator error:</td><td class="table-column">${msg.aggregatorError.code }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Aggregator error text:</td><td class="table-column">${msg.aggregatorErrorDescription }</td>
				</tr>
				</c:if>
				
				
			</tbody>
		</table>
		<c:if test='${msg.type eq "TWO_WAY"}'>
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Incoming message
				</th>
			</tr>
			</thead>
			<tbody>
				<tr class="<%=row()%>">
					<td class="table-column"  width="50%">MO:</td><td class="table-column">${msg.incomingText }</td>
				</tr>
				<c:if test='${!empty msg.incomingMessageId}'>
				<tr class="<%=row()%>">
					<td class="table-column" >MO Id:</td><td class="table-column">${msg.incomingMessageId }</td>
				</tr>
				</c:if>
			</tbody>
		</table>
		</c:if>
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Response Message
				</th>
			</tr>
			</thead>
			<tbody>
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Is a link:</td><td class="table-column">${msg.isLink }</td>
				</tr>
				<c:if test='${msg.isLink}'>
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">MT Subject:</td><td class="table-column">${msg.outgoingSubject }</td>
				</tr>
				</c:if>
				<tr class="<%=row()%>">
					<td class="table-column">MT:</td><td class="table-column">${msg.outgoingText }</td>
				</tr>
				<c:if test='${!empty msg.price}'>
				<tr class="<%=row()%>">
					<td class="table-column" >Price:</td><td class="table-column">(${msg.priceCurrency.code })${msg.price }</td>
				</tr>
				</c:if>
				<c:if test='${!empty msg.revenue}'>
				<tr class="<%=row()%>">
					<td class="table-column">Revenue:</td><td class="table-column">(${msg.revenueCurrency.code })${msg.revenue }</td>
				</tr>
				</c:if>
				<c:if test='${!empty msg.outgoingMessageId}'>
				<tr class="<%=row()%>">
					<td class="table-column" >MT Id:</td><td class="table-column">${msg.outgoingMessageId }</td>
				</tr>
				</c:if>
				
			</tbody>
		</table>
		<table id="row" cellspacing="0" cellpadding="4" width="75%" style="margin-top: 10px">
			<thead>
			<tr>
				<th class="table-header sorted order1" colspan="2">
					Additional Debug information
				</th>
			</tr>
			</thead>
			<tbody>
				<tr class="<%=row()%>">
					<td class="table-column" >Raw message details:</td><td class="table-column">${msg.rawMessageDetails }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Operator:</td><td class="table-column">${msg.optOperator }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column" width="50%">Tariff:</td><td class="table-column">${msg.optTariff }</td>
				</tr>
				<tr class="<%=row()%>">
					<td class="table-column">Session Id:</td><td class="table-column">${msg.optSessionId }</td>
				</tr>
				
				
			</tbody>
		</table>
		
	</div>
</div>

 <c:import url="_footer.jsp"/> 
