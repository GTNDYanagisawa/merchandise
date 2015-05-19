/**
 * Drag and Drop payment page
 */
//-----------------------------------  FORMAT CURRENCY  -----------------------------------
function formatCurrency( value ){
	
	if ($.isNumeric(value)){
		
		return Number(value).toFixed(2);
	}
	return null;
}

ACC.accountpayment = {
		
//-----------------------------------  GLOBAL VARIABLES OF DRAG & DROP INTERFACE  -----------------------------------

mapReduceAmount : {} ,
currentOpenAmount  : {} ,		
		


//-----------------------------------  UPDATE PAY AMOUNT  -----------------------------------
updatePartialPayment : function(){
	
	// count total open balance
	var totalOpenBalance = 0.0 ;
	$('.partialPayment').each(  
			function (){
				if ($.isNumeric( $(this).attr("maxvalue") ))
				totalOpenBalance = totalOpenBalance + Number( $(this).attr("maxvalue") );
			}
	);
	$("#lbTotalOpenBalance").text( formatCurrency(totalOpenBalance) );
	
	// count total open balance
	var payAmount = 0.0 ;
	$('.partialPayment').each(  
			function (){
				if ($.isNumeric( $(this).attr("value") ))
					payAmount = payAmount + Number( $(this).attr("value") );
			}
	);
	
	// origin payment
	$("#lbTotalPayment").text( formatCurrency(payAmount) );
	
	// new payment
	$("#lbTotalNewBalance").text( formatCurrency(totalOpenBalance - payAmount) );
	
},
//----------------------------------------  APPLY USING DOCUMENT  ------------------------------------

applyUsingDocument: function( creditPage  ){
	
	var jsonData = [];
	var submittable = false ;
	
	// collecting apply actions
	$('.acc_dragdrop_item.item_droppable').each( function (){
		
		var payNumber =  $(this).attr("docNumber");
		
		$('.acc_dragdrop_item.item_draggable').each( function (){
			
			var useNumber    = $(this).attr("docNumber");
			var reduceAmount = ACC.accountpayment.mapReduceAmount[ payNumber ][ useNumber ];
			
			if ( $.isNumeric( reduceAmount ) && reduceAmount > 0.0  ){
			
				jsonData.push({  
						"payNumber" : payNumber ,
						"useNumber" : useNumber ,
						"amount"    : reduceAmount					
				});
				
				submittable = true ;
								
			}			
		});
	});
	
	
	if (submittable || creditPage ){
				
		$.ajax({
			  type: "POST",
			  url: "apply",	
			  contentType:"application/json; charset=utf-8",
			  dataType : "json",
			  data: JSON.stringify(jsonData),
			  async : false ,
			  cache : false 
			}).done(function ( res ){

				if ( ! creditPage )
					// go back previous page
					window.location.replace( document.referrer );
				else {
					// go to credit card payment
					var formData = '<form action="../document/payment" method="post">' ;
					// collect to pay document
					$('.acc_dragdrop_item.item_droppable').each(  
							function (){				
								
								var docNumber    = $(this).attr("docNumber");
								// if have positive amount
								if (  ACC.accountpayment.currentOpenAmount[ docNumber ] > 0.0  ){											

										formData = formData + '<input type=hidden name="selectedDocuments" value="' + docNumber + '" />'
								}												
							}
					);
					
					
					// make form and submit
					formData = formData + '</form>' ;
					var form = $( formData );
					$('body').append(form);
					$(form).submit();
				} 
			});
	}
		
	
},

//-----------------------------------  UPDATE DRAG AND DROP INTERFACE  -----------------------------------

updateDragAndDropInterface: function(){
		
	var currentOpenBalance  = 0.0 ;
		
	//  update open amount
	$('.acc_dragdrop_item').each(  
			function (){				

				// if have valid amount
				if ($.isNumeric( $(this).attr("amount") )){
					
					var docNumber = $(this).attr("docNumber");
					var isPaid = false ;
										
					ACC.accountpayment.currentOpenAmount[ docNumber  ] = Number(  $(this).attr("amount"));			
					// apply actions
					$.each(  ACC.accountpayment.mapReduceAmount[ docNumber ] , function (index,value) {  
						if ( value > 0.0 ){
							ACC.accountpayment.currentOpenAmount[ docNumber ]  -= value ;
							isPaid = true ;
						}
					});
					
					// if pay document
					if ($(this).hasClass("item_droppable")){
						
						currentOpenBalance += ACC.accountpayment.currentOpenAmount[ docNumber ];
					}
					
					// if completely empty
					if (ACC.accountpayment.currentOpenAmount[ docNumber ] <= 0.0001)	{					
						$(this).addClass("item_dragdrop_disable");	
					}
					 else {						
						$(this).removeClass("item_dragdrop_disable");	
					 }
					
					$(this).find(".currentamount").text( formatCurrency( ACC.accountpayment.currentOpenAmount[ docNumber  ] ) );
					
				
				}												
			}
	);
	
	
	$("#current_open_balance").text( formatCurrency(currentOpenBalance));
	
	if ( currentOpenBalance <= 0.0001 )
		$("#bnApplyAndPay").addClass("buttonDisable");
	else 
		$("#bnApplyAndPay").removeClass("buttonDisable");
		
},

//-----------------------------------  INITIALIZE  -----------------------------------
initialize: function(){
	
	// restrict partialPayment class to input number
	$('.partialPayment').bind("keyup blur"  , function (e) {
		
		 if (e.type == 'keyup' && e.which != 13) return ;
		 if (e.type != 'keyup' && e.type != 'blur' ) return ;
		 
		 e.preventDefault();
		 
	    if (this.value != this.value.replace(/[^0-9\.]/g, '')) {
	       this.value = this.value.replace(/[^0-9\.]/g, '');
	    }
	    
	    if ($.isNumeric( this.value )   ){
	    	
	    	this.value = Math.max( 0 , Number( this.value)  );
	    	this.value = Math.min(  Number( this.value) , Number( $(this).attr("maxvalue") )  );
	    	this.value = formatCurrency(Number( this.value ));
	    }
	    	
	    ACC.accountpayment.updatePartialPayment();
	});
	
	
	ACC.accountpayment.updatePartialPayment();
	
	// drag and drop interface ========================================================
	
	$(".item_draggable").draggable({ 
		revert: true ,
		start: function(event, ui) { $(this).addClass("item_dragging"); },
        stop: function(event, ui) { $(this).removeClass("item_dragging"); }     
	});
	
	
	$(".item_droppable").droppable({ 
		accept : ".item_draggable",
		hoverClass: "item_dropping",
		drop: function( event, ui ) {
	       
			 var dropNumber =  $(this).attr("docNumber")   ;				 
			 var dragNumber = ui.draggable.attr("docNumber") ;			
			 
			 // calculate apply amount 
			 var reduceAmount = Math.min( ACC.accountpayment.currentOpenAmount[ dropNumber ] , ACC.accountpayment.currentOpenAmount[ dragNumber ] );
	
			 if ( reduceAmount > 0){
				 								 
				 // add new action and update interface				 
				 ACC.accountpayment.mapReduceAmount[  dragNumber ][ dropNumber ] = reduceAmount ; 				
				 ACC.accountpayment.mapReduceAmount[  dropNumber ][ dragNumber ] = reduceAmount ;
				 
				 var actionLine =  $("<div style='width:100%;' />");
				 actionLine.append("<div style='width:20% ;' />");
				 actionLine.append("<div style='width:40% ;'><h5>" + dragNumber + "</h5></div>");
				 actionLine.append("<div style='width:20% ;'><h5>" + formatCurrency(reduceAmount) + "</h5></div>");	 
				 
				 // setup checkbox
				 var checkbox = $("<div><input type='checkbox' />");
				 
				 checkbox.find("input[type=checkbox]").prop('checked', true );
				 checkbox.find("input[type=checkbox]").click( function(){
						
				
						if( ! $(this).is(':checked') ) {
							
							ACC.accountpayment.mapReduceAmount[  dragNumber ][ dropNumber ] = null ;
							ACC.accountpayment.mapReduceAmount[  dropNumber ][ dragNumber ] = null ;
							
							ACC.accountpayment.updateDragAndDropInterface();
							actionLine.remove();
							
						}
						  
					});
				 actionLine.append( checkbox );  	 
		 	 	
				 $(this).find(".acc_action_list").append( actionLine ) ;
		

				
				 
				 ACC.accountpayment.updateDragAndDropInterface();
				 
				 
				 				
			 }
			 
	         return false ;
	      }
					
	});
	
	// initialize map reduce amount & current open account
	$('.acc_dragdrop_item').each( function (){		
		if ($.isNumeric( $(this).attr("amount") )){	
			
			var docNumber = $(this).attr("docNumber") ;
			ACC.accountpayment.mapReduceAmount[  docNumber  ] = {};
			ACC.accountpayment.currentOpenAmount[ docNumber ] = Number(  $(this).attr("amount"));

		}
	});
	
	// calculate previous open balance --------------------------------------
	var previousOpenBalance = 0.0 ;
	$('.acc_dragdrop_item.item_droppable').each(  
			function (){				
				// if have valid amount
				if ($.isNumeric( $(this).attr("amount") )){											
					previousOpenBalance +=  Number( $(this).attr("amount") );																			
				}												
			}
	);
	$("#previous_open_balance").text( formatCurrency(previousOpenBalance));
	
	// apply documents
	$("#bnApply").click( function (){  ACC.accountpayment.applyUsingDocument( false );  });
	
	// apply documents and go to credit card payment page
	$("#bnApplyAndPay").click( function (){	
		
		if ( ! $(this).hasClass("buttonDisable")  )
			ACC.accountpayment.applyUsingDocument( true ); 
	
	});
	
	ACC.accountpayment.updateDragAndDropInterface();

	
}
//-----------------------------------  END INITIALIZE  -----------------------------------				
}


// document ready
$(document).ready(function() {
	ACC.accountpayment.initialize();
});
