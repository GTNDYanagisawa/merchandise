<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tags/addons/ysapproductconfigb2baddon/desktop/configuration/sapproductconfig.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:useBean id="cons" class="de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler" scope="session"/>


<div class="product-config-tab-navigation" id="configTabNavigation">
	<ul class="product-config-tab-list">
		<c:forEach var="group" items="${config.groups}" varStatus="groupStatus">
		<c:if  test="${group.configurable}">
			<form:input type="hidden" path="config.groups[${groupStatus.index}].id" id="${group.id}.id" />
			<li id="${group.id}_title" class="${cssConf:groupTabStyleClasses(group)}<c:if test="${not group.collapsed}"> product-config-tab-selected</c:if>">
				<a href="javascript:firePost(toggleTab,[ undefined,'${group.id}']);">
					<c:choose>
						<c:when test="${group.name eq cons.getGeneralGroupName()}">
							<spring:message code="sapproductconfig.group.general.title" text="General (Default)" />
						</c:when>
						<c:otherwise>
							<c:out value="${group.description}"/>
						</c:otherwise>
					</c:choose>
				</a>
			</li>
			</c:if>
		</c:forEach>
	</ul>
</div>