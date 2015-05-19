<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="entitlements" />
	<div class="span-20 last">
		<div class="span-20 wide-content-slot advert">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>

        <div class="item_container_holder entitlements">
            <div class="title_holder">
                <div class="title">
                    <div class="title-top">
                        <span></span>
                    </div>
                </div>
                <h2><spring:theme code="text.account.entitlements" text="Access & Entitlements"/></h2>
            </div>
            <div class="item_container">
                <c:if test="${not empty grants}">

                    <table id="entitlements">
                        <thead>
                            <tr>
                                <th id="header1"><spring:theme code="text.account.entitlements.entitlementName" text="Entitlement Name"/></th>
                                <th id="start"><spring:theme code="text.account.entitlements.date.start" text="start date"/></th>
                                <th id="end"><spring:theme code="text.account.entitlements.date.end" text="end date"/></th>
                                <th id="header4"><spring:theme code="text.account.entitlements.status" text="Status"/></th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${grants}" var="grant">

                            <tr>
                                <td headers="header1">
                                    <ycommerce:testId code="entitlements_entitlementType_link">
                                        <p>${grant.name}</p>
                                    </ycommerce:testId>
                                </td>
                                <td headers="start">
                                    <ycommerce:testId code="redundant">
                                        <p>${grant.startTime}</p>
                                    </ycommerce:testId>
                                </td>
                                <td headers="end">
                                    <ycommerce:testId code="redundant">
                                        <c:choose>
                                            <c:when test="${empty grant.endTime}">
                                                <p><spring:theme code="text.account.entitlements.date.end.unlimited" text="Unlimited"/></p>
                                            </c:when>
                                            <c:otherwise>
                                                <p>${grant.endTime}</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </ycommerce:testId>
                                </td>
                                <td headers="header4">
                                    <ycommerce:testId code="entitlements_status_label">
                                        <p>${grant.status}</p>
                                    </ycommerce:testId>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${empty grants}">
                    <p><spring:theme code="text.account.entitlements.noEntitlements" text="You have no entitlements"/></p>
                </c:if>
            </div>
        </div>
	</div>
</template:page> 