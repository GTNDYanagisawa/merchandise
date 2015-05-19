<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:if test="${requestScope.captcaEnabledForCurrentStore}">
	<li>
		<div id="recaptcha_widget" style="display:none" data-recaptcha-public-key="${requestScope.recaptchaPublicKey}">
			<div id="captcha_error">
				<div id="recaptcha_image" class="right"></div>
				<div class="right">
					<a href="javascript:Recaptcha.reload()" class="cicon reload"></a>

					<div class="recaptcha_only_if_image"><a href="javascript:Recaptcha.switch_type('audio')" class="cicon audio"></a></div>
					<div class="recaptcha_only_if_audio"><a href="javascript:Recaptcha.switch_type('image')" class="cicon image"></a></div>
					<a href="javascript:Recaptcha.showhelp()" class="cicon help left"></a>
				</div>
				<div class="recaptcha_only_if_incorrect_sol" style="color:red"><spring:theme code="recaptch.incorrect.text" text="Incorrect please try again"/></div>
				<div data-role="fieldcontain" data-theme="b">
					<label for="recaptcha_response_field">
						<span class="recaptcha_only_if_image"><spring:theme code="recaptch.enterwords.text" text="Enter the words above:"/></span>
						<span class="recaptcha_only_if_audio"><spring:theme code="recaptch.enternumbers.text" text="Enter the numbers you hear:"/></span>
					</label>
					<input type="text" id="recaptcha_response_field" name="recaptcha_response_field" class="left"/>
				</div>
			</div>
		</div>
	</li>
</c:if>
