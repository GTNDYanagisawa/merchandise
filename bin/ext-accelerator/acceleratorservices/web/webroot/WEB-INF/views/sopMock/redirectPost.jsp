<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/_ui/hop-mock/css/common.css" var="stylesheetPath"/>
<c:url value="/_ui/hop-mock/images/favicon.ico" var="favIconPath"/>
<c:url value="/_ui/hop-mock/images/logo.png" var="imgLogoPath"/>
<c:url value="/_ui/hop-mock/images/spinner.gif" var="imgSpinnerPath"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<title>Mocked Silent Order Post Page</title>
	<link rel="shortcut icon" href="${favIconPath}" type="image/x-icon"/>
	<link rel="stylesheet" type="text/css" media="screen" href="${stylesheetPath}"/>
</head>
<body onload="${showDebugPage ? '' : 'document.silentOrderPostForm.submit()'}">
	<div id="mockwrapper">
		<div id="mockpage">
			<div id="mockHeader">
				<div class="logo">
					<img alt="logo" src="${imgLogoPath}"/>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="item_container_holder">
				<div class="item_container">
					<div id="debugWelcome">
						<h3>
							<img src="${imgSpinnerPath}"/>&nbsp;
							<spring:message code="text.header.wait"/>
							
						</h3>
					</div>
				</div>
				<c:if test="${showDebugPage}">
					<div class="item_container">
						<div id="infoBox">
							<h3>
								<spring:message code="text.header.debug"/>
							</h3>
						</div>
					</div>
				</c:if>
				<div class="item_container">
					<form:form id="silentOrderPostForm" name="silentOrderPostForm" action="${postUrl}" method="post">
						<div id="postFormItems">
							<dl>
								<c:forEach items="${postParams}" var="entry" varStatus="status">
									<c:choose>
										<c:when test="${showDebugPage}">
											<dt><label for="${entry.key}" class="required">${entry.key}</label></dt>
											<dd><input type="text" id="${entry.key}" name="${entry.key}" value="${entry.value}" tabindex="${status.count + 1}"/></dd>
										</c:when>
										<c:otherwise>
											<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}" />
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</dl>
						</div>
						<c:if test="${showDebugPage}">
							<div class="rightcol">
								<spring:message code="button.submit" var="submitButtonLabel"/>
								<input id="button.submit" class="submitButtonText" type="submit" title="${submitButtonLabel}" value="${submitButtonLabel}"/>
							</div>
						</c:if>
					</form:form>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="footer">
			</div>
		</div>
	 </div>
</body>
</html>
