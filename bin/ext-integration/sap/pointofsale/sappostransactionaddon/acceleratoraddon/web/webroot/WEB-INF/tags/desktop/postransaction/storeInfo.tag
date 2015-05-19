<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

   <div class="orderBox address">
   
	<div class="headline"><spring:theme code="text.account.posTransaction.storeDetails" text="Store Details"/></div>

		<div class="span-7">
			<spring:theme code="text.account.posTransaction.storeName" arguments="${order.store.storeName}"/>
			<br/>
			<spring:theme code="text.account.posTransaction.storeAddress"/>
			<dt>
			  <dd>
	           <c:if test="${not empty order.store.address.houseNumber}">	
						${fn:escapeXml(order.store.address.houseNumber)}&nbsp; 
	           </c:if>
	           <c:if test="${not empty order.store.address.street}">
	               ${fn:escapeXml(order.store.address.street)}
	           </c:if>       
			  </dd>					    				
			  <dd>
				  <c:if test="${not empty order.store.address.poBox}">	
						${fn:escapeXml(order.store.address.poBox)}
				  </c:if>
			  </dd>
			  <dd>
	           <c:if test="${not empty order.store.address.city}">	
	         	   ${fn:escapeXml(order.store.address.city)}&nbsp; 
	           </c:if>
	           <c:if test="${not empty order.store.address.zip}">
	               ${fn:escapeXml(order.store.address.zip)}
	           </c:if>       
			  </dd>
			  <dd>
	           <c:if test="${not empty order.store.address.countryCode}">	
	         	   ${fn:escapeXml(order.store.address.countryCode)}
	           </c:if>      
			  </dd>
			</dt>
			<spring:theme code="text.account.posTransaction.operatorName" arguments="${order.operatorId}"/>	
		</div>	
   </div>
		
