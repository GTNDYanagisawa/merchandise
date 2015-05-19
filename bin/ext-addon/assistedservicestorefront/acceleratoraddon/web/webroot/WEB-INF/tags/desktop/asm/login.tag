<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="error" required="true" type="java.lang.String" %>
<%@ attribute name="disabledButton" required="false" type="java.lang.Boolean" %>

<c:set var="usernameLabel"><spring:theme code="asm.login.username.placeholder"/></c:set>
<c:set var="passwordLabel"><spring:theme code="asm.login.password.placeholder"/></c:set>

<form action="${action}" method="post" id="asmLoginForm" class="asmForm" autocomplete="off">
	<fieldset>

        <div class="ASM_input_holder">
            <label for="username">${usernameLabel}</label>
		    <input name="username" type="text" value="${username}" class="ASM-input ${error}" placeholder="${usernameLabel}" autocomplete="off">
        </div>
        <div class="ASM_input_holder">
            <label for="password">${passwordLabel}</label>
            <input name="password" type="password" class="ASM-input ${error}" placeholder="${passwordLabel}" autocomplete="off">
        </div>
		<input type="hidden" name="CSRFToken" value="${CSRFToken}">
		<button type="submit" class="ASM-btn ASM-btn-login" <c:if test="${disabledButton}">disabled</c:if>><spring:theme code="${actionNameKey}"/></button>
	</fieldset>
</form>