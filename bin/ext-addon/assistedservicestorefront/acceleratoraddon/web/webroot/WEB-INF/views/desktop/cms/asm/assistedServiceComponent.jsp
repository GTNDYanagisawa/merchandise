<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="asm" tagdir="/WEB-INF/tags/addons/assistedservicestorefront/desktop/asm"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="assistedserviceutils" uri="http://hybris.com/tld/assistedserviceutils" %>

<div id="_asm">
	<script>document.getElementsByTagName("body")[0].insertBefore(document.getElementById('_asm'), document.getElementById('page'))</script>	
    <div class="ASM_header">
        <div class="container">
            <asm:redirect />
            <button class="ASM_close ASM_close_all closeBtn" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>

            <c:if test="${not empty asm_message}">
                <div class="ASM_alert ${asm_alert_class}"><spring:theme code="${asm_message}"/></div>
            </c:if>

            <%-- logo text --%>
            <div class="ASM_logo">
                <span class="ASM_icon ASM_icon-logo"></span>
                <spring:theme code="asm.logo.keyLetterA"/>
                <span class="ASM_cut_text"><spring:theme code="asm.logo.cutTextA"/></span><spring:theme code="asm.logo.keyLetterS"/>
                <span class="ASM_cut_text"><spring:theme code="asm.logo.cutTextS"/></span><spring:theme code="asm.logo.keyLetterM"/>
                <span class="ASM_cut_text"><spring:theme code="asm.logo.cutTextM"/></span>
            </div>
            <c:choose>
                <c:when test="${empty agentUID}">
                    <%-- login --%>
                    <div id="_asmLogin" class="ASM_login">
                        <c:choose>
                            <%-- input with error and without --%>
                            <c:when test="${empty asm_message}">
                                <c:url value="/assisted-service/login" var="loginActionUrl" />
                                <asm:login actionNameKey="asm.login" action="${loginActionUrl}" error="" disabledButton="true"/>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/assisted-service/login" var="loginActionUrl" />
                                <asm:login actionNameKey="asm.login" action="${loginActionUrl}" error="ASM-input-error" disabledButton="true"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="_asmLogged" class="ASM_loggedin">
                        <div class="ASM_timer">
                            <span id="sessionTimer" class="ASM_loggedin_text_name"><span class="hidden-xs hidden-sm hidden-md">Session timeout: </span><span class='ASM_timer_count' id="timerCount"><script>document.getElementById('timerCount').innerHTML=Math.floor(${agentTimer}/60)+":00";</script></span> min</span>
                            <button type="submit" id="resetButton" class="ASM-btn ASM-btn-reset" disabled><spring:theme code="asm.emulate.reset"/></button>
                            <script>var timer=${agentTimer};</script>
                        </div>

                        <div class="ASM_loggedin_text">
                            <span class="hidden-xs hidden-sm hidden-md"><spring:theme code="asm.login.logged"/> </span>
                            <span class="ASM_loggedin_text_name">${agentName}</span>
                        </div>

                        <c:url value="/assisted-service/logoutasm" var="logoutActionUrl" />
                        <form action="${logoutActionUrl}" method="post" id="asmLogoutForm" class="asmForm">
                            <fieldset>
                                <input type="hidden" name="CSRFToken" value="${CSRFToken}">
                                <button type="submit" class="ASM-btn ASM-btn-logout" disabled><spring:theme code="asm.logout"/></button>
                            </fieldset>
                        </form>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <c:if test="${not empty agentUID}">
        <div class="ASM_session">
            <div class="container" id="_asmCustomer">
                <c:choose>
                    <c:when test="${empty emulateMode}">
                        <c:url value="/assisted-service/personify-customer" var="personifyActionUrl" />
                        <asm:emulateform actionNameKey="asm.emulate.start" actionNameKeyEnding="asm.emulate.start.ending" action="${personifyActionUrl}" disabledButton="true"/>
                    </c:when>

                    <c:otherwise>
						<c:url value="/assisted-service/bind-cart" var="bindActionUrl" />
						<form action="${bindActionUrl}" method="post" class="asmForm" id="_asmBindForm">
							<fieldset>
                                <span class="ASM_icon ASM_icon-contacts hidden-xs hidden-sm hidden-md"></span>
                                <div class="ASM_input_holder customerId">
									<c:choose>
										<c:when test="${emulatedUser.uid ne 'anonymous'}">
                                            <label for="customerName"><spring:theme code="asm.emulate.username.label"/></label>
											<input name="customerName" type="text" value="${emulatedUser.name}" class="ASM-input" readonly
												data-hover='{"name":"${emulatedUser.name}","email":"${emulatedUser.uid}","card":"${assistedserviceutils:shortCardNumber(emulatedUser)}","date":"${assistedserviceutils:creationDate(emulatedUser)}"}'/>
											<input name="customerId" type="hidden" value="${emulatedUser.uid}" />									
										</c:when>
										<c:otherwise>
											<c:set var="usernamePlaceholder"><spring:theme code="asm.emulate.username.placeholder"/></c:set>
                                            <label for="customerName"><spring:theme code="asm.emulate.username.label"/></label>
											<input name="customerId" type="hidden" value="${customerId}" placeholder="${usernamePlaceholder}" class="ASM-input"/>
                                            <input name="customerName" type="text" value="${customerName}" placeholder="${usernamePlaceholder}" class="ASM-input"/>
                                         </c:otherwise>
									</c:choose>
								</div>
                                <span class="ASM_icon ASM_icon-chain invisible"></span>
								<c:choose>
									<c:when test="${not empty cart}">
	                                    <span class="ASM_icon ASM_icon-cart hidden-xs hidden-sm hidden-md"></span>
	                                    <div class="ASM_input_holder cartId">
	                                        <label><spring:theme code="asm.emulate.cart.label"/></label>
	                                        <input type="text" value="${cart.code}" class="ASM-input" disabled/>
	                                    </div>
									</c:when>
									<c:otherwise>
										<span class="ASM_icon ASM_icon-cart hidden-xs hidden-sm hidden-md"></span>
										<div class="ASM_input_holder cartId">
                                            <c:set var="cartPlaceholder"><spring:theme code="asm.emulate.cart.placeholder"/></c:set>
                                            <label><spring:theme code="asm.emulate.cart.label"/></label>
	                                        <input name="cartId" type="text" value="${cart.code}" placeholder="${cartPlaceholder}" class="ASM-input" autocomplete="off"/>
	                                    </div>
									</c:otherwise>
								</c:choose>
								<input type="hidden" name="CSRFToken" value="${CSRFToken}">

                                <c:choose>
                                    <c:when test="${emulatedUser.uid ne 'anonymous'}">
                                        <button type="submit" class="ASM-btn ASM-btn-bind-cart hidden"><spring:theme code="asm.emulate.cart.bind"/><span class="hidden-xs hidden-sm hidden-md"><spring:theme code="asm.emulate.cart.bind.ending"/></span></button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="ASM-btn ASM-btn-bind-cart hidden"><spring:theme code="asm.emulate.cart.bind"/><span class="hidden-xs hidden-sm hidden-md"><spring:theme code="asm.emulate.customer.bind.ending"/></span></button>
                                    </c:otherwise>
                                </c:choose>
                            </fieldset>
						</form>

                        <span class="ASM_end_session">
                            <c:url value="/assisted-service/personify-stop" var="sessionEndActionUrl" />
                            <form action="${sessionEndActionUrl}" method="post" id="_asmSessionEndForm" class="asmForm">
                                <fieldset>
                                    <button type="submit" id="stopEmulate" class="ASM-btn ASM-btn-end-session" disabled>
                                        <spring:theme code="asm.emulate.end"/><span class="hidden-xs hidden-sm hidden-md"><spring:theme code="asm.emulate.end.ending"/></span>
                                    </button>
                                </fieldset>
                            </form>
                        </span>
                    </c:otherwise>
                </c:choose>
                <asm:createcustomerform/>
            </div>
        </div>
    </c:if>
    <div id="asmAutoComplete" class="asmAutoComplete"></div>
    <div id="asmAutoCompleteCartId" class="asmAutoComplete"></div>
</div>