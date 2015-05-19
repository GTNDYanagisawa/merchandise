<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="item_container_holder secure_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:message code="login.title"/></h2>
	</div>

	<div class="item_container">
		<p><spring:message code="login.description"/></p>

		<form:form action="${action}" method="post" commandName="loginForm">
			<c:if test="${not empty message}">
				<span class="errors">
					<spring:message code="${message}"/>
				</span>
			</c:if>
			<dl>
				<c:if test="${not empty accErrorMsgs}">
					<span class="form_field_error">
				</c:if>

				<formUtil:formInputBox idKey="j_username" labelKey="login.username" path="j_username" inputCSS="text" mandatory="true"/>
				<formUtil:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="text password" mandatory="true"/>

				<div class="form_link_password_register">
					<a href="javascript:void(0)" data-url="<c:url value='/login/pw/request'/>" class="password-forgotten"><spring:theme code="login.link.forgottenPwd"/></a>
					<c:if test="${enableRegistration}">
						<br />
						<a href="<c:url value='/register'/>"  class="password-forgotten"><spring:theme code="secureportal.link.createAccount"/></a>
					</c:if>
				</div>


				
				<c:if test="${not empty accErrorMsgs}">
					</span>
				</c:if>
			</dl>
			<span style="display: block; clear: both;">
			<ycommerce:testId code="login_Login_button">
				<button type="submit" class="form right"><spring:message code="${actionNameKey}"/></button>
			</ycommerce:testId>
			</span>
		</form:form>
	</div>
</div>
