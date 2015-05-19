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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>


<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
	</div>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<nav:myCompanyNav selected="accountsummary" />

	<div class="span-20 last">


		<div class="span-20 last">
			<div class="cust_acc">

				<div class="acc_summary">


					<div id="acc_header">
						<div id="acc_header_title">
							<h1>
								<spring:theme code="text.company.documentPayAndUse"
									text="Apply Use Document" />
							</h1>
						</div>
					</div>
					
					

					<!-- ================================ PAY DOCUMENT TABLE ============================ -->
					<div id="acc_dragdrop_left">
						<table>
							<caption>
								<h2><spring:theme code="text.company.accountsummary.paydocument.label"  text="Document to pay" /> <h2>
							</caption>
							<tbody>
								<c:forEach items="${searchPageData.results}" var="result">
								<c:if test="${result.documentType.payableOrUsable == 'PAY' }">	
									<tr>
										<td>
										<div docNumber="${result.documentNumber }"  amount="${result.openAmount }"  class="acc_dragdrop_item item_droppable">
										
											<div style="width:100% ;">${result.documentType.name}</div>												
									  		<div style="width:80% ; "><h4> ${result.documentNumber } </h4></div>									  		
  									      <div> 	<h4><span class="currentamount">  </span> </h4>	 </div>
									 	 	<div style="width:100% ;" class="acc_action_list" />
									 	 	
									 	 </div>
									 	 
									</td>									 
									</tr>
									</c:if>
								</c:forEach>								
							</tbody>
						</table>
					</div>
					<!-- ================================ USE DOCUMENT TABLE ============================ -->
						<div id="acc_dragdrop_right">
						<table>
							<caption>
									<h2><spring:theme code="text.company.accountsummary.usedocument.label"  text="Document to use" /> <h2>
							</caption>
							<tbody>
								<c:forEach items="${searchPageData.results}" var="result">
								<c:if test="${result.documentType.payableOrUsable == 'USE' }">								
									<tr>
										<td>
											<div docNumber="${result.documentNumber }" amount="${result.openAmount }"  class="acc_dragdrop_item item_draggable" >
												<div style="width:100% ;" > ${result.documentType.name} </div>
										  		<div style="width:80% ;" > <h4> ${result.documentNumber } </h4> </div>							  										    								  
										  	   <div> <h4> <span class="currentamount">  </span> </h4></div>											  
										 	 </div>
									 	 </div>
									</td>									 
									</tr>
									</c:if>
								</c:forEach>								
							</tbody>
						</table>
					</div>
					<!-- ================================ SUMMARY ============================ -->
					

						<div class="acc_dragdrop_info">						
							<div style="float:left; width:130px;"><spring:theme code="text.company.accountsummary.previousopenbalance.label" text="Previous Open Balance"/></div> 
							<div style="float:left" >  <h4><span id="previous_open_balance">00 </span></h4>  </div> 			
						</div>

						<div class="acc_dragdrop_info">						
							<div style="float:left; width:130px;"><spring:theme code="text.company.accountsummary.currentopenbalance.label" text="Current Open Balance"/></div> 
							<div style="float:left" > <h4><span id="current_open_balance">0.0</span></h4>  </div> 			
						</div>
						
					
					<!-- ================================ BUTTON PANEL ============================ -->
					
					<div class="acc_dragdrop_buttons">		
						<button id="bnApply"  type="button"  class="positive large">
							<spring:theme code="text.company.accountsummary.button.apply" text="Apply"/>
						</button>
						<button id="bnApplyAndPay"  type="button" class="positive large">
							<spring:theme code="text.company.accountsummary.button.applyandpay" text="Apply & Pay Balance"/>
						</button>
						
					</div>
				</div>
				
				
							
				
			</div>
		</div>
	</div>
</template:page>
