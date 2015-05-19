<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
 
{
	"pagination" : {
		"currentPage":	"${searchPageData.pagination.currentPage}",
		"numberOfPages": "${searchPageData.pagination.numberOfPages}",
		"totalNumberOfResults" : "${searchPageData.pagination.totalNumberOfResults}"
	},
	"resultSize":	"${fn:length(searchPageData.results)}",
	"error" : "${error}",
	"documentListerHeaderHtml" : "<spring:escapeBody javaScriptEscape="true">
						<thead>
						<tr>
								<th id="status"><spring:theme code="text.company.accountsummary.status.label" text="Status"/></th>
								<th id="documentType"><spring:theme code="text.company.accountsummary.documentType.label" text="Type"/></th>
								<th id="documentNumber"><spring:theme code="text.company.accountsummary.documentNumber.label" text="Invoice#"/></th>
								<th id="date"><spring:theme code="text.company.accountsummary.date.label" text="Incoice Date"/></th>
								<th id="dueDate"><spring:theme code="text.company.accountsummary.dueDate.label" text="Incoice Date"/></th>
								<th id="amount"><spring:theme code="text.company.accountsummary.amount.label" text="Original Amt."/></th>
								<th id="openAmount"><spring:theme code="text.company.accountsummary.openAmount.label" text="Open Amount"/></th>
								<th id="documentMedia"><spring:theme code="text.company.accountsummary.documentMedia.label" text="View Document"/></th>
								<th class="checkbox-input">&nbsp;</th>
						</tr>
					</thead>
				</spring:escapeBody>",
	"documentListerHtml" :  "<spring:escapeBody javaScriptEscape="true">
							<tbody>
						<c:forEach items="${searchPageData.results}" var="result">
							<tr>
								<td><spring:theme code="text.company.accountsummary.${result.status }.label"/></td>
								<td>${result.documentType.name }</td>
								<td>${result.documentNumber }</td>
								<td><fmt:formatDate value="${result.date }" dateStyle="long" timeStyle="short" type="both"/></td>
								<td><fmt:formatDate value="${result.dueDate }" dateStyle="long" timeStyle="short" type="both"/></td>
								<td>${result.formattedAmount }</td>
								<td>${result.formattedOpenAmount }</td>
								<td>
								<c:if test="${not empty result.documentMedia.downloadURL}">
									<a href="${result.documentMedia.downloadURL }" target=_blank><button class="form"><spring:theme code="text.company.accountsummary.documentMedia.view.label" text="View"/></button></a>
								</c:if>	
								</td>
								<td>&nbsp;</td>
							</tr>
						</c:forEach>
						</tbody>
				</spring:escapeBody>",
				
	"documentGridHtml" :  "<spring:escapeBody javaScriptEscape="true">
						<c:set var="count" scope="page" value="0"/>						
						<c:forEach items="${searchPageData.results}" var="result">
							<c:set var="count" scope="page" value="${count + 1}"/>
							<c:if test="${count % 3 == 1}">
							<tr class="nobg">
							</c:if>							
							<td>
								<div class="col">
									<c:choose>
    									<c:when test="${searchBy == 'status'}">
    										<h3 id="gridheader">${result.status}</h3>
    									</c:when>
    									<c:when test="${searchBy == 'documentType'}">
    										<h3 id="gridheader">${result.documentType.name}</h3>
    									</c:when>
    									<c:when test="${searchBy == 'documentNumber'}">
    										<h3 id="gridheader">${result.documentNumber}</h3>
    									</c:when>
    									<c:when test="${searchBy == 'date'}">
    										<h3 id="gridheader"><fmt:formatDate value="${result.date }" dateStyle="long" timeStyle="short" type="both"/></h3>
    									</c:when>
    									<c:when test="${searchBy == 'dueDate'}">
    										<h3 id="gridheader"><fmt:formatDate value="${result.dueDate }" dateStyle="long" timeStyle="short" type="both"/></h3>
    									</c:when>
    									<c:when test="${searchBy == 'amount'}">
    										<h3 id="gridheader">${result.formattedAmount}</h3>
    									</c:when>
    									<c:when test="${searchBy == 'openAmount'}">
    										<h3 id="gridheader">${result.formattedOpenAmount}</h3>
    									</c:when>
    									<c:otherwise>
									        <h3 id="gridheader">${result.documentNumber}</h3>
									    </c:otherwise>
    								</c:choose>
									<dl>
										<dt><spring:theme code="text.company.accountsummary.status.label" text="Status"/>: </dt>
										<dd><spring:theme code="text.company.accountsummary.${result.status }.label"/></dd>
										
										<dt><spring:theme code="text.company.accountsummary.documentType.label" text="Type"/>: </dt>
										<dd>${result.documentType.name }</dd>
										
										<dt><spring:theme code="text.company.accountsummary.documentNumber.label" text="Invoice#"/></dt>
										<dd>${result.documentNumber }</dd>
										
										<dt><spring:theme code="text.company.accountsummary.date.label" text="Incoice Date"/></dt>
										<dd><fmt:formatDate value="${result.date }" dateStyle="long" timeStyle="short" type="both"/></dd>
										
										<dt><spring:theme code="text.company.accountsummary.dueDate.label" text="Incoice Date"/></dt>
										<dd><fmt:formatDate value="${result.dueDate }" dateStyle="long" timeStyle="short" type="both"/></dd>
										
										<dt><spring:theme code="text.company.accountsummary.amount.label" text="Original Amt."/></dt>
										<dd>${result.formattedAmount }</dd>
										
										<dt><spring:theme code="text.company.accountsummary.openAmount.label" text="Open Amount"/></dt>
										<dd>${result.formattedOpenAmount }</dd>
																				
									</dl>
									<div class="btn">
										<c:if test="${not empty result.documentMedia.downloadURL}">
											<a href="${result.documentMedia.downloadURL }" target="_blank>${result.documentMedia.realFileName }">
											<button class="form"><spring:theme code="text.company.accountsummary.documentMedia.view.label" text="View"/></button>
											</a>
										</c:if>
									</div>
								</div>
							</td>
							<c:choose>
								<c:when test="${count == fn:length(searchPageData.results)}">
									<c:if test="${count % 3 != 0}">
										<c:forEach begin="0" end="${2-(count % 3)}" varStatus="loop"><td><div class="col-blank"/></td></c:forEach>
									</c:if>
									</tr>
								</c:when>
								<c:otherwise>
									<c:if test="${count % 3 == 0}">
									</tr>
									</c:if>					
								</c:otherwise>
							</c:choose>
						</c:forEach>
				</spring:escapeBody>"

}
