<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<%@ attribute name="bindResult" required="true"
	type="org.springframework.validation.BindingResult"%>
<%@ attribute name="addMode" required="true"
	type="java.lang.Boolean"%>

<%@ attribute name="config" required="true"
	type="de.hybris.platform.sap.productconfig.facades.ConfigurationData"%>
<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>

<%@ taglib prefix="conf" uri="sapproductconfig.tld"%>

<c:set var="conflicts" value="${conf:conflictError(bindResult)}" />
<c:set var="missingMandatoryFields" value="${conf:mandatoryFieldError(bindResult)}" />
<c:set var="conflictCount" value="${fn:length(conflicts)}" />
<c:set var="mandatoryCount" value="${fn:length(missingMandatoryFields)}" />
<c:set var="warningsCount" value="${conflictCount + mandatoryCount}"/>
<c:set var="warningsExist" value="${warningsCount gt 0}"/>

<div id="product-config-addToCartPopup" class="product-config-addToCartPopup">
	<div id="product-config-addToCartPopupDialog" style="display:none">
		<div class="header">
			<div class="title">
				<c:choose>
					<c:when test="${addMode}">
						<spring:message code="sapproductconfig.addtocartpopup.title"
							text="Added to Your Shopping Cart" />
					</c:when>
					<c:otherwise>
						<spring:message code="sapproductconfig.addtocartpopup.update.title"
							text="Shopping Cart Updated" />
					</c:otherwise>
				</c:choose>
			</div>
			<div class="close">

			</div>
		</div>
		<div class="main">
			<div class="entrymessage">
				<c:choose>
						<c:when test="${addMode}">
							<c:choose>
								<c:when test="${warningsExist}">
									<spring:message
										code="sapproductconfig.addtocartpopup.entrytext.conflicts"
										arguments="${warningsCount}"
										text="The following item has been added to your cart. Your configuration contains {0} issues that you must resolve before you can go to checkout."/><br/>
								</c:when>
								<c:otherwise>
									<spring:message code="sapproductconfig.addtocartpopup.entrytext.noconflicts"
										text="The following item has been added to your cart:" />
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${warningsExist}">
									<spring:message
										code="sapproductconfig.addtocartpopup.update.entrytext.conflicts"
										arguments="${warningsCount}"
										text="The following item has been updated in your cart. Your configuration contains {0} issues that you must resolve before you can go to checkout."/><br/>
								</c:when>
								<c:otherwise>
									<spring:message code="sapproductconfig.addtocartpopup.update.entrytext.noconflicts"
										text="The following item has been updated in your cart:" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
			</div>
			<div class="productname">
				<c:out value="${product.name}" />
			</div>
			<c:if test="${warningsExist}">
				<div class="warnings">
					<div class="warning-title">
						<c:choose>
							<c:when test="${conflictCount == 0}">
								<spring:message
									code="sapproductconfig.addtocartpopup.warning.title.mandatory"
									arguments="${mandatoryCount}"
									text="{0} missing mandatory inputs" />
							</c:when>
							<c:when test="${mandatoryCount == 0}">
								<spring:message
									code="sapproductconfig.addtocartpopup.warning.title.conflict"
									arguments="${conflictCount}"
									text="{0} conflicts:" />
							</c:when>
							<c:otherwise>
								<spring:message
									code="sapproductconfig.addtocartpopup.warning.title.conflict.and.mandatory"
									arguments="${conflictCount}, ${mandatoryCount}"
									text="{0} conflicts and {1} missing mandatory inputs:" />
							</c:otherwise>
						</c:choose>
					</div>
					<c:forEach var="warning" items="${conflicts}" varStatus="status">
						<div class="warning-msg <c:if test="${status.index gt 2}">more</c:if>">
							<spring:message code="${warning.code}" arguments="${warning.args}" text="${warning.message}" />
						</div>
					</c:forEach>
					<c:if test="${conflictCount gt 3}">
						<div class="moreLink">
							<a id="showMoreLink"><spring:message code="sapproductconfig.addtocartpopup.more.link"
									text="more >" /></a>
						</div>
					</c:if>
					<div class="resolve">
						<div class="button">
							<a id="resolveLink"><spring:message code="sapproductconfig.addtocartpopup.resolve.button"
									text="Resolve Issues Now" /></a>
						</div>
					</div>
				</div>
			</c:if>
			<p>
			<div class="image">
				<product:productPrimaryImage product="${product}" format="product"/>
			</div>
			<div class="productLinks">
				<div class="productlinkheader"><spring:message code="sapproductconfig.addtocartpopup.createconfig"
					text="Create another configuration for this product:" /></div>
				<c:choose>
					<c:when test="${addMode}">
						<div class="link"><a id="sameConfigLink"><spring:message
								code="sapproductconfig.addtocartpopup.sameConfiglink"
								text="Based on the one added to the cart" /></a> </div>
					</c:when>
					<c:otherwise>
						<div class="link"><a id="sameConfigLink"><spring:message
								code="sapproductconfig.addtocartpopup.update.sameConfiglink"
								text="Based on the one updated in the cart" /></a> </div>
					</c:otherwise>
				</c:choose>
				<div class="link"><a id="resetLink"><spring:message
						code="sapproductconfig.addtocartpopup.resetlink"
						text="Based on standard configuration" /></a></div>
			</div>
			<div class="homelink">
				<div class="link"><a id="homeLink"><spring:message
						code="sapproductconfig.addtocartpopup.homelink"
						text="Go to start page" /></a></div>
			</div>
			<div class="gotocart">
				<div class="button <c:if test="${warningsExist}">second</c:if>">
					<a id="checkoutLink" ><spring:message
							code="cart.checkout"
							text="Checkout" /></a>
				</div> 
			</div>
			</p>
		</div>
	</div>
</div>