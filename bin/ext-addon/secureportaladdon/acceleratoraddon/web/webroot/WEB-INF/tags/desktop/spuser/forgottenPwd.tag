<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="span-20 last">
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2><spring:theme code="forgottenPwd.title"/></h2>
		</div>

		<div class="item_container">
			<p><spring:theme code="forgottenPwd.description"/></p>
			<p class="required"><spring:theme code="form.required"/></p>
			<form:form method="post" commandName="forgottenPwdForm">
                <div class="form_field-elements">
					<div class="form_field-input">
						<formUtil:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" inputCSS="text" mandatory="true"/>
					</div>
				</div>
				<span style="display: block; clear: both;">
					<button class="form"><spring:theme code="forgottenPwd.submit"/></button>
				</span>
			</form:form>
		</div>
	</div>
</div>
