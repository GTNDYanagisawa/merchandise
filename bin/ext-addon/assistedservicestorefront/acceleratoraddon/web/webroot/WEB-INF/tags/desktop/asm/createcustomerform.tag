<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:url value="/assisted-service/create-account" var="createAccountActionUrl" />
<form action="${createAccountActionUrl}" method="post" id="_asmCreateAccountForm" class="asmForm">
	<input name="customerName" type="hidden" value="${customerId}" class="ASM-input"/>
	<input name="customerId" type="hidden" value="${customerId}" class="ASM-input"/>
	<button type="submit" class="ASM-btn ASM-btn-create-account hidden" disabled><spring:theme code="asm.emulate.createAccount"/><span class="hidden-xs hidden-sm hidden-md"><spring:theme code="asm.emulate.createAccount.ending"/></span></button>
</form>