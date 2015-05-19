<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<div class="item_container_holder span-15">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>
			<spring:theme code="register.new.customer"/>
		</h2>
	</div>

	<div class="item_container">
		<p class="required">
			<spring:theme code="form.required"/>
		</p>
        <form:form method="post" id="registerForm" commandName="registrationForm" action="${action}">
			<div class="form_field-elements left">
				<div class="newCustomerRegister left span-7">
					<formUtil:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}"/>
					<formUtil:formInputBox idKey="secureportal.firstAndLastName" labelKey="secureportal.firstAndLastName" path="name" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="secureportal.position" labelKey="secureportal.position" path="position" inputCSS="text" mandatory="true"/>
					<div class="left span-4 phone">
							<formUtil:formInputBox idKey="storeDetails.table.telephone" labelKey="storeDetails.table.telephone" path="telephone" inputCSS="text" mandatory="true"/>
						</div>
					<div class="left span-2 extension">
							<formUtil:formInputBox idKey="secureportal.extension" labelKey="secureportal.extension" path="telephoneExtension" inputCSS="text" mandatory="false"/>
					</div>
				</div>
				<div class="newCustomerRegister right span-7">
					<formUtil:formInputBox idKey="secureportal.companyName" labelKey="secureportal.companyName" path="companyName" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.line1" labelKey="address.line1" path="companyAddressStreet" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.line2" labelKey="address.line2" path="companyAddressStreetLine2" inputCSS="text" mandatory="false"/>
					<formUtil:formInputBox idKey="address.townCity" labelKey="address.townCity" path="companyAddressCity" inputCSS="text" mandatory="true"/>
					<formUtil:formInputBox idKey="address.postcode" labelKey="address.postcode" path="companyAddressPostalCode" inputCSS="text" mandatory="true"/>
					<formUtil:formSelectBox idKey="address.country_del" labelKey="address.country" path="companyAddressCountryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${countries}" itemValue="isocode"/>
				</div>
				<div class="msg">
					<formUtil:formTextArea idKey="secureportal.message" labelKey="secureportal.message" path="message" areaCSS="textarea" mandatory="false"/>
				</div>
			</div>

	</div>
    <li>
				<span style="display: block; clear: both;">
					<ycommerce:testId code="register_Register_button">
                        <button data-theme="b" class="form">
                            <spring:theme code='${actionNameKey}' />
                        </button>
                    </ycommerce:testId>
				</span>
    </li>

	</form:form>
</div>

