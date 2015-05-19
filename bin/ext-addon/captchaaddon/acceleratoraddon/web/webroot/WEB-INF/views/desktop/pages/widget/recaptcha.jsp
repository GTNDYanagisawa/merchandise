<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${requestScope.captcaEnabledForCurrentStore}">
	<div id="recaptcha_widget" style="display:none"  data-recaptcha-public-key="${requestScope.recaptchaPublicKey}">

		<div id="recaptcha_image" class="left"></div>
		<div class="left">
			<a href="javascript:Recaptcha.reload()" class="cicon reload"></a>
			<div class="recaptcha_only_if_image"><a href="javascript:Recaptcha.switch_type('audio')" class="cicon audio"></a></div>
			<div class="recaptcha_only_if_audio"><a href="javascript:Recaptcha.switch_type('image')" class="cicon image"></a></div>
		</div>

		<div class="recaptcha_only_if_incorrect_sol" style="color:red"><spring:theme code="recaptch.incorrect.text" text="Incorrect please try again"/></div>

		<div class="control-group clear clearfix">

			<label class="control-label " for="recaptcha_response_field">
				<span class="recaptcha_only_if_image"><spring:theme code="recaptch.enterwords.text" text="Enter the words above:"/></span>
				<span class="recaptcha_only_if_audio"><spring:theme code="recaptch.enternumbers.text" text="Enter the numbers you hear:"/></span>
			</label>
			<div class="controls">
				<input type="text" id="recaptcha_response_field" name="recaptcha_response_field" class="left" style="width:242px;" />
				<a href="javascript:Recaptcha.showhelp()" class="cicon help left" ></a>
			</div>
		</div>

	</div>
</c:if>
