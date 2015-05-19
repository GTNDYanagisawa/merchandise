<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryAddress" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ attribute name="costCenter" required="true" type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout/single" %>
<%@ taglib prefix="addon-checkout-single" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/desktop/checkout/single" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<spring:url value="/checkout/single/summary/getDeliveryAddresses.json" var="getDeliveryAddressesUrl" />
<spring:url value="/checkout/single/summary/setDeliveryAddress.json" var="setDeliveryAddressUrl" />
<spring:url value="/checkout/single/summary/getDeliveryAddressForm.json" var="getDeliveryAddressFormUrl" />
<spring:url value="/checkout/single/summary/setDefaultAddress.json" var="setDefaultAddressUrl" />

<script type="text/javascript">
	var getDeliveryAddressesUrl = '${getDeliveryAddressesUrl}';
		setDeliveryAddressUrl = '${setDeliveryAddressUrl}';
		getDeliveryAddressFormUrl = '${getDeliveryAddressFormUrl}';
		setDefaultAddressUrl = '${setDefaultAddressUrl}';
</script>



<c:set value="${not empty deliveryAddress}" var="deliveryAddressOk"/>

	<div class="summaryDeliveryAddress summarySection ${deliveryAddressOk ? 'complete' : ''}" 
		data-get="${getDeliveryAddressesUrl}" 
		data-get-form="${getDeliveryAddressFormUrl}" 
		data-set="${setDeliveryAddressUrl}" 
		data-set-default="${setDefaultAddressUrl}"
	>
	<script id="deliveryAddressSummaryTemplate" class="sectionTemplate" type="text/x-jquery-tmpl">
	
	<div class="summaryDeliveryAddress">

			<div class="headline"><span class="number">2</span><spring:theme code="checkout.summary.deliveryAddress.header" htmlEscape="false"/></div>
				<ul>
					{{if data.deliveryAddress}}
						<li>{{= data.deliveryAddress.title}} {{= data.deliveryAddress.firstName}} {{= data.deliveryAddress.lastName}}</li>
						<li>{{= data.deliveryAddress.line1}}</li>
						<li>{{= data.deliveryAddress.line2}}</li>
						<li>{{= data.deliveryAddress.town}}</li>
						{{if data.deliveryAddress.region}}
							<li>{{= data.deliveryAddress.region.name}}</li>
						{{/if}}
						<li>{{= data.deliveryAddress.postalCode}}</li>
						{{if data.deliveryAddress.country}}
							<li>{{= data.deliveryAddress.country.name}}</li>
						{{/if}}
					{{else}}
						<li><spring:theme code="checkout.summary.deliveryAddress.noneSelected"/></li>
					{{/if}}
				</ul>

				{{if data.deliveryAddress}}
						<ycommerce:testId code="checkout_changeAddress_element">
						<button type="button" class="positive editButton" data-address="{{= data.deliveryAddress.id}}"><spring:theme code="checkout.summary.deliveryAddress.editDeliveryAddressButton"/></a>
						</ycommerce:testId>
				{{else}}
						<ycommerce:testId code="checkout_changeAddress_element">
						<button type="button" class="positive editButton" ><spring:theme code="checkout.summary.deliveryAddress.enterDeliveryAddressButton"/></button>
						</ycommerce:testId>
				{{/if}}
	</div>

	</script>

	<script id="deliveryAddressesTemplate" class="colorboxTemplate"  type="text/x-jquery-tmpl">
		<div class="headline">Addressbook</div>
		<div id="savedAddressList">
		{{if !addresses.length}}
			<spring:theme code="checkout.summary.deliveryAddress.noExistingAddresses"/>
		{{/if}}
		{{if addresses.length}}
		<div class="addressList">
		
				{{each addresses}}
			
					<div class="addressEntry  {{if defaultAddress}} default {{/if}}">
					
						<div class="left edit_add_area">	
						<ul>
								<li>{{= title}} {{= firstName}} {{= lastName}}</li>
								<li>{{= line1}}</li>
								<li>{{= line2}}</li>
								<li>{{= town}}</li>
								{{if region}}
									<li>{{= region.name}}</li>
								{{/if}}
								<li>{{= postalCode}}</li>
								{{if country}}
									<li>{{= country.name}}</li>
								{{/if}}
							</ul>
						</div>
						
						
						<div class="right edit_btn_area">
							<button type="button" class="positive  useAddress" data-address="{{= $value.id}}"><spring:theme code="checkout.summary.deliveryAddress.useThisAddress"/></button>

						</div>
					
					
					</div>
				{{/each}}
			</div>
		{{/if}}
		</div>
	</script>
	
	<div class="contentSection"></div>


	<addon-checkout-single:deliveryAddressPopup />
</div>






