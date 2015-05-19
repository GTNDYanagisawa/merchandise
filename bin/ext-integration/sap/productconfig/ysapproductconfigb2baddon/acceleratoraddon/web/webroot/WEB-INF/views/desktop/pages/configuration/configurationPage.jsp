<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration"%>

<spring:theme code="text.addToCart" var="addToCartText"
	text="Add To Cart" />
<spring:theme code="sapproductconfig.updateCart" var="updateCartText"
	text="Update Cart" />

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
		<product:productDetailsJavascript />
	</jsp:attribute>


	<jsp:body>
		<c:if test="${not empty message}">
			<spring:theme code="${message}" />
		</c:if>
		<div id="breadcrumb" class="breadcrumb">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
		</div>
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<cms:pageSlot id="section1Slot" position="Section1" var="comp"
			element="div" class="span-24 section1 cms_disp-img_slot">
			<cms:component component="${comp}" />
		</cms:pageSlot>
		<cms:pageSlot id="configHeaderSlot" position="ConfigHeader" var="comp" element="div" class="span-24-pc configHeader cms_disp-img_slot">
			<cms:component component="${comp}"/>
		</cms:pageSlot>
		<cms:pageSlot id="configTitleSlot" position="ConfigTitle" var="comp"
			element="div" class="span-24 configTitle cms_disp-img_slot">
			<cms:component component="${comp}" />
		</cms:pageSlot>

		<cms:pageSlot id="configNavBarSlot" position="ConfigNavBar" var="comp"
			element="div" class="span-24 configNavBar cms_disp-img_slot">
			<cms:component component="${comp}" />
		</cms:pageSlot>	
		
		<div id="configContent" class="span-24">
			<div>
				<cms:pageSlot id="configContentSlot" position="ConfigContent"
					var="feature" element="div"
					class="span-16 configContent cms_disp-img_slot">
					<cms:component component="${feature}" />
				</cms:pageSlot>
			</div>
			<div id="configSidebar" class="product-config-sidebar">
				<cms:pageSlot id="configSidebarSlot" position="ConfigSidebar"
					var="feature" element="div"
					class="span-8 configSidebar cms_disp-img_slot last">
					<cms:component component="${feature}" />
				</cms:pageSlot>					
			</div>
		</div>
		
		<cms:pageSlot id="section2Slot" position="Section2" var="feature"
			element="div" class="span-24 section2 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<cms:pageSlot id="section3Slot" position="Section3" var="feature"
			element="div" class="span-24 section3 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		
		<cms:pageSlot id="section4Slot" position="Section4" var="feature"
			element="div" class="span-24 section4 cms_disp-img_slot">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		
		<script type="text/javascript">
			var counter = 0;
			function scrollAfterTabNav() {
				counter++;
				var footer = document.getElementById('footer');
				if(footer || counter > 10){
					var tabNav = document.getElementById('configTabNavigation');
					if (tabNav != null) {
						var offset = tabNav.offsetTop;
						window.scrollTo(0, offset);
					}
					//console.log(counter +" scrolling");
				}else{
					// wait for footer to load before scrolling
					setTimeout(scrollAfterTabNav, 10);
					//console.log(counter +" waiting");
				}
			}
			var param = new RegExp('[\\?&amp;]' + 'tab' + '=([^&amp;#]*)')
					.exec(window.location.href);
			if (param != null) {
				scrollAfterTabNav(0);
			}
		</script>
			
	</jsp:body>


</template:page>
<spring:url value="${product.code}" var="baseUrl" />

<script type="text/javascript">
	ysapproductconfigb2baddonConstants.init('${baseUrl}', '${addToCartText}',
			'${updateCartText}');
	initConfigPage();
</script>