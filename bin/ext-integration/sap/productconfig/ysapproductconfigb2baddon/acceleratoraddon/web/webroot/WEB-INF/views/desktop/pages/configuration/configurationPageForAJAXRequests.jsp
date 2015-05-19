<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
 
 <div id="start:configNavBarSlot"/>
 		<cms:pageSlot id="configNavBarSlot" position="ConfigNavBar" var="comp" element="div" class="span-24 configNavBar cms_disp-img_slot">
			<cms:component component="${comp}"/>
		</cms:pageSlot>	
<div id="end:configNavBarSlot"/>
  
 <div id="start:configContentSlot"/>
		<cms:pageSlot id="configContentSlot" position="ConfigContent" var="feature" element="div" class="span-16 configContent cms_disp-img_slot">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
 <div id="end:configContentSlot"/>
 
  <div id="start:configSidebarSlot"/>
			<cms:pageSlot id="configSidebarSlot" position="ConfigSidebar" var="feature" element="div" class="span-8 configSidebar cms_disp-img_slot last">
				<cms:component component="${feature}"/>
			</cms:pageSlot>	
 <div id="end:configSidebarSlot"/>
 