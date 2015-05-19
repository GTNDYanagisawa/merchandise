<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>	
<div class="acc_summary">

	<div class="headline"><spring:theme code="text.company.viewAccountSummary" text="View/Edit Summary"/></div>



	<div class="siteLogo" id="acc_header_logo">
		<cms:pageSlot position="SiteLogo" var="logo" limit="1">
		<cms:component component="${logo}"/>
		</cms:pageSlot>
	</div>


	<div class="description">
		<a href="javascript:printPage()" id="bnPrint"><spring:theme code="text.company.accountsummary.print.page" text="Print Page"/></a>
	</div>



	<div class="acc_info">
		<h2>${b2bUnitData.name}</h2>
		<input id="unit" type="hidden" value="${b2bUnitData.name}" />
		<input id="listViewPageSize" type="hidden" value="${listViewPageSize}" />
		<input id="gridViewPageSize" type="hidden" value="${gridViewPageSize}" />
		<div class="acc_info_box clearfix">
			<div class="col first">
				<dl class="opt1">
					<dt><spring:theme code="text.company.accountsummary.businessunitid.label" text="Business Unit ID:"/></dt>
					<dd>${b2bUnitData.uid}</dd>
					<dt><spring:theme code="text.company.accountsummary.creditrep.label" text="Credit Rep:"/></dt>
					<dd>
						<c:choose>
							<c:when test="${not empty accountManagerName}">
								<c:if test="${not empty email}">
						        	<a href="mailto:${email}" target="_top">
							    </c:if>
							    ${accountManagerName}
								<c:if test="${not empty email}">
									</a>
								</c:if>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</dd>										
					<dt><spring:theme code="text.company.accountsummary.creditline.label" text="Credit Line:"/></dt>
					<dd>${creditLimit }</dd>
				</dl>
				<dl class="opt2">
					<dt><spring:theme code="text.company.accountsummary.currentbalance.label" text="Current Balance"/></dt>
					<dd>${amountBalanceData.currentBalance}</dd>
					<dt><spring:theme code="text.company.accountsummary.openbalance.label" text="Open Balance:"/></dt>
					<dd>${amountBalanceData.openBalance}</dd>
				</dl>
			</div>
			<div class="col">
				<dl class="opt3">
					<dt><spring:theme code="text.company.accountsummary.b2bunit.label" text="B2B Unit:"/></dt>
					<dd>${billingAddress.title},&nbsp;${billingAddress.firstName}&nbsp;${billingAddress.lastName}<br>
						${billingAddress.formattedAddress}<br>
						${billingAddress.country.name}<br>
					</dd>
				</dl>
			</div>
			<div class="col last">
				<dl>
					<c:forEach items="${amountBalanceData.dueBalance}" var="range">						
						<c:choose>
						    <c:when test="${empty  range.key.maxBoundery}">
						        <c:set var="maxBoundery" value="+"/>
						    </c:when>
						    <c:otherwise>
						        <c:set var="maxBoundery" value="-${range.key.maxBoundery}"/>
						    </c:otherwise>
						</c:choose>
						
						<dt>${range.key.minBoundery} ${maxBoundery}&nbsp;<spring:theme code="text.company.accountsummary.days.label" text="Days"/></dt>
						<dd>${range.value}</dd>
					</c:forEach>
					
					<dt class="last"><spring:theme code="text.company.accountsummary.pastduebalance.label" text="Past Due Balance:"/></dt>
					<dd class="last">${amountBalanceData.pastDueBalance}</dd>
				</dl>
			</div>
		</div>
	</div>

	<div class="search clearfix">
		<div class="search_by">
			<select id="search_by" name="search_by">
				<c:forEach items="${searchByList}" var="searchBy">
					<option id="${searchBy.key}" type="${searchBy.value}"><spring:theme code="text.company.accountsummary.${searchBy.key}.label"/></option>
				</c:forEach>
			</select>
		</div>
		<div class="search_key">
			<button id="search" name="" type="button" value="Search" class="positive"><spring:theme code="text.company.accountsummary.button.search" text="Search"/></button>
			<div id="date_criteria" class="hidden criterias">
				<spring:theme code="text.company.accountsummary.from.label" text="From:"/>
				<input id="dateMin" class="date searchcriteria" type="text" placeholder="mm/dd/yyyy"  />
				<spring:theme code="text.company.accountsummary.to.label" text="To:"/>
				<input id="dateMax" class="date searchcriteria" type="text" placeholder="mm/dd/yyyy"  />
			</div>
			<div id="amount_criteria" class="hidden criterias">
				<spring:theme code="text.company.accountsummary.from.label" text="From:"/>
				<input id="amountMin" class="searchcriteria" type="text"   />
				<spring:theme code="text.company.accountsummary.to.label" text="To:"/>
				<input id="amountMax" class="searchcriteria" type="text"   />
			</div>
			<select id="document_type" name="document_type" class="hidden criterias">
				<c:forEach items="${documentTypeList}" var="documentType">
					<option id="${documentType.code}" value="${documentType.code}">${documentType.name}</option>
				</c:forEach>
			</select>
			<input id="documentTypeCode" type="hidden" name="documentTypeCode" value="${documentTypeCode}">
			<input id="searchValue" class="searchcriteria criterias" name="searchValue" type="text">
			
			
		</div>
	</div>

	<div id="errorMessage" class="error hidden"></div>
	<div id="emptyResultMessage" class="info hidden">
		<spring:theme code="text.company.accountsummary.noresult.info" text="No result found."/>
	</div> 
							


	<div class="filter clearfix">
		<ul class="opt1">
			<li class="sort first" direction="desc"><a href="#"><spring:theme code="text.company.accountsummary.sorthighest.label" text="Highest"/></a></li>
			<li class="sort last" direction="asc"><a href="#"><spring:theme code="text.company.accountsummary.sortlowest.label" text="Lowest"/></a></li>
		</ul>
		<ul class="opt2">
			<li>
				<input name="status" type="radio" id="status_open" checked="checked">
				<label for="status_open"><spring:theme code="text.company.accountsummary.openstatus.label" text="Open Obligations"/></label>
			</li>
			<li>
				<input name="status" type="radio" id="status_closed">
				<label for="status_closed"><spring:theme code="text.company.accountsummary.closedstatus.label" text="Closed Documents"/></label>
			</li>
			<li>
				<input name="status" type="radio" id="status_all" >
				<label for="status_all"><spring:theme code="text.company.accountsummary.allstatus.label" text="All"/></label>
			</li>
		</ul>
		<ul id = "view-switch" class="opt3">
			<li class="first" resultview="grid"><a href="#"></a></li>
			<li class="last" resultview="list"><a href="#"></a></li>
		</ul>
		<ul id="resultCount" class="right">
			<li>
				<spring:theme code="text.company.accountsummary.resultcount.label" />
			</li>
		</ul>
	</div>
							
	<div class="results list">
			<table border="0" cellspacing="0" cellpadding="0" id="resultsBody">
			</table>
		<div id="spinner" class="spinner"/></div>

		<div id="accountSummaryFooter" class="center"><a href="#"><spring:theme code="text.company.accountsummary.showmoreresultlink.label" text="Show More Result"/></a></div>
	</div>
						
	<div class="hidden">
		<span id="amountRangeInvalidParameter" >
			<spring:theme code="text.company.accountsummary.amountrange.error"/>
		</span>
		<span id="dateRangeInvalidParameter" >
			<spring:theme code="text.company.accountsummary.daterange.error"/>
		</span>
	</div>

</div>