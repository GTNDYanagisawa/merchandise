ACC.accountdocuments = {

	infiniteScrollingConfig : {
		offset : '100%'
	},
	sort : 'asc',
	currentPage : 0,
	numberOfPages : Number.MAX_VALUE,
	pageSize : $("input#listViewPageSize").val(),
	previousQuery : '',
	unit : $("input#unit").val(),
	searchBy : 'documentNumber',
	selectedSearchType : 'class java.lang.String',
	status : 'status_open',
	currentNumberOfResult : 0,
	inCall: false,
	
	$showMoreResultsArea : $('#accountSummaryFooter'),
	$spinner : $(".acc_summary #spinner"),
	$dateMin :  $(".acc_summary #dateMin"),
	$dateMax :  $(".acc_summary #dateMax"),
	$amountMin : $('.acc_summary #amountMin'),
	$amountMax : $('.acc_summary #amountMax'),
	$searchValue : $(".acc_summary #searchValue"),
	$resultsBody : $('.acc_summary #resultsBody'),
	$date_criteria : $('.acc_summary #date_criteria'),
	$amount_criteria : $('.acc_summary #amount_criteria'),
	$errorMessage : $('.acc_summary div#errorMessage'),
	$noResultFound : $('.acc_summary div#emptyResultMessage'),
	$documentType : $('.acc_summary #document_type'),
	$documentTypeCode : $('.acc_summary #documentTypeCode'),
	$resultview : 'list',
	
	triggerLoadMoreResults: function() {
		if (ACC.accountdocuments.currentPage < ACC.accountdocuments.numberOfPages) {
			// show the page loader
			ACC.accountdocuments.$spinner.show();
			ACC.accountdocuments.loadMoreResults(parseInt(ACC.accountdocuments.currentPage) + 1);
			
			
		}
	},

	scrollingHandler: function(direction) {
		if (direction === "down") {
			ACC.accountdocuments.triggerLoadMoreResults();
		}
	},

	loadMoreResults: function(page) {
		// Hide message
		ACC.accountdocuments.$noResultFound.hide();
		//Display show more result area
		ACC.accountdocuments.$showMoreResultsArea.show();
		
		var query = ACC.accountdocuments.buildQuery();
		if(query != ACC.accountdocuments.previousQuery){
			page = 0;
			ACC.accountdocuments.currentNumberOfResult=0;
			ACC.accountdocuments.previousQuery = query;
		}
		
		if(ACC.accountdocuments.inCall == false){
			ACC.accountdocuments.inCall = true;
			$.ajax({
				dataType: "json",
				url: 'details/documents?page= ' + page + query,
				success: function(data) {
					
					
					if (data.pagination !== undefined) {
						if(data.error != ''){
							ACC.accountdocuments.$errorMessage.html($("#amountRangeInvalidParameter").html());
							ACC.accountdocuments.$errorMessage.show();
						}else{
							if(page === 0){
								if (ACC.accountdocuments.$resultview === "grid"){
									ACC.accountdocuments.$resultsBody.html(data.documentGridHtml);									
								}
								else{
									ACC.accountdocuments.$resultsBody.html(data.documentListerHeaderHtml);
									ACC.accountdocuments.$resultsBody.append(data.documentListerHtml);
								}
							}else{
								if (ACC.accountdocuments.$resultview === "grid"){
									ACC.accountdocuments.$resultsBody.append(data.documentGridHtml);
								}
								else{
									ACC.accountdocuments.$resultsBody.append(data.documentListerHtml);
								}
							}
							ACC.accountdocuments.$errorMessage.html('');
							ACC.accountdocuments.$errorMessage.hide();
						}
											
						ACC.accountdocuments.updatePaginationInfos(data.pagination);

						ACC.accountdocuments.$showMoreResultsArea.waypoint(ACC.accountdocuments.infiniteScrollingConfig); // reconfigure waypoint eventhandler
						ACC.accountdocuments.$spinner.hide();
						ACC.accountdocuments.updateResultsCount(data.resultSize ,data.pagination.totalNumberOfResults);
						
						ACC.accountdocuments.inCall = false;
						
						//Update sort column				
						$(".results th").removeClass("sort-up");
						$(".results th").removeClass("sort-down");
						
						if (ACC.accountdocuments.sort === "asc") {
							$("th#" + ACC.accountdocuments.searchBy).addClass("sort-down");
							$("h3#gridheader").addClass("sort-down");
						}else{
							$("th#" + ACC.accountdocuments.searchBy).addClass("sort-up");
							$("h3#gridheader").addClass("sort-up");
						}
						
						
	
					}
					
					ACC.PaymentRedirect.initialize();
					
					/* on double click on a selectable document */
					$(".documentSelectable").off();
					$(".documentSelectable").on("dblclick" , function(e){
					
						documentNumber = $(this).attr("documentNumber");
						$("[documentNumber='" + documentNumber + "']").toggleClass("acc_document_selected") ;	 						 

					 
					});
					/* on detail click */
					$(".bndetail").on("click" , function (){
						
						// get document number and doctype
						docnumber = $(this).attr("value");
						doctype  = $(this).attr("doctype");      
						
						$.colorbox({href:'history?documentNumber=' + docnumber 
									, scrolling : true  
									, onComplete : function (){
										$("#document_payment_history_title").text( doctype + " - " + docnumber );
									}});
						
						return false ;
						
					} );
					
				},
				error:function(data){
					ACC.accountdocuments.inCall = false;
				}
			});
		}
	},
	
	updateResultsCount : function(resultCount, totalResult){
		ACC.accountdocuments.currentNumberOfResult = parseInt(resultCount) + parseInt(ACC.accountdocuments.currentNumberOfResult);
		$("#resultCount #numberOfResult").html(ACC.accountdocuments.currentNumberOfResult);
		$("#resultCount #totalResult").html(totalResult);
		
		if(ACC.accountdocuments.currentNumberOfResult === parseInt(totalResult)){
			//Hide show more result link
			ACC.accountdocuments.$showMoreResultsArea.hide();
		}
		
		if(parseInt(totalResult) === 0){
			ACC.accountdocuments.$noResultFound.show();
		}
	},

	buildQuery: function(){
	  	 
		var searchValue = '';
		var searchRangeMin = '';
		var searchRangeMax = '';
		
		if(ACC.accountdocuments.searchBy === ACC.accountdocuments.$documentTypeCode.attr('value')){
			var searchValue = ACC.accountdocuments.$documentType.val();
		}else if(ACC.accountdocuments.selectedSearchType === 'class java.util.Date'){
			var searchRangeMin = ACC.accountdocuments.$dateMin.val();
			var searchRangeMax = ACC.accountdocuments.$dateMax.val();
		}else if(ACC.accountdocuments.selectedSearchType === 'class java.math.BigDecimal'){
			var searchRangeMin = ACC.accountdocuments.$amountMin.val();
			var searchRangeMax = ACC.accountdocuments.$amountMax.val();
		}else if(ACC.accountdocuments.selectedSearchType === 'class java.lang.String'){
			var searchValue = ACC.accountdocuments.$searchValue.val();
		}
		
		if (ACC.accountdocuments.$resultview === "grid"){
			ACC.accountdocuments.pageSize = $("input#gridViewPageSize").val();
		}
		return '&unit=' + ACC.accountdocuments.unit + '&status=' + ACC.accountdocuments.status + '&searchBy=' + ACC.accountdocuments.searchBy + '&searchValue=' + searchValue + '&searchRangeMin=' + searchRangeMin + '&searchRangeMax='+ searchRangeMax +'&sort=' + ACC.accountdocuments.sort +'&pageSize='+ ACC.accountdocuments.pageSize;
		
	},

	updatePaginationInfos: function(paginationInfo) {
		ACC.accountdocuments.currentPage   = parseInt(paginationInfo.currentPage);
		ACC.accountdocuments.numberOfPages = parseInt(paginationInfo.numberOfPages);
	},

	bindShowMoreResults: function(showMoreResultsArea) {
		showMoreResultsArea.live("click", function() {
			ACC.accountdocuments.triggerLoadMoreResults();
		});

		showMoreResultsArea.waypoint(ACC.accountdocuments.scrollingHandler,
									ACC.accountdocuments.infiniteScrollingConfig);
	},

	bindSortingSelector: function() {
		$('li.sort').click( function(){
			ACC.accountdocuments.sort = $(this).attr('direction');
			ACC.accountdocuments.currentNumberOfResult=0;
			ACC.accountdocuments.loadMoreResults(0);
		});
	},
	
	bindSearchButton: function() {
		$('button#search').click(function(){
			ACC.accountdocuments.currentNumberOfResult=0;
			ACC.accountdocuments.loadMoreResults(0);
		});
	},
	
	bindViewSwitch: function() {
		$('ul#view-switch li').click(function(){
			ACC.accountdocuments.$resultview = $(this).attr('resultview');
			ACC.accountdocuments.currentNumberOfResult=0;
			ACC.accountdocuments.loadMoreResults(0);
			ACC.accountdocuments.setCookie("accountSummaryViewMode", ACC.accountdocuments.$resultview);
		});
	},
	
	bindSearchCriteria: function(){
		
		//Init
		$('select#search_by #documentNumber').attr('selected', 'selected');
		
		//Search by
		$("select#search_by").live("change", function(){
			ACC.accountdocuments.searchBy = $(this).find("option:selected").attr('id');
			ACC.accountdocuments.selectedSearchType = $('select#search_by option:selected').attr('type');
			//Clean all fields
			ACC.accountdocuments.cleanSearchFields();
			//hide field
			$('.criterias').hide();

			if(ACC.accountdocuments.searchBy === ACC.accountdocuments.$documentTypeCode.attr('value')){
				ACC.accountdocuments.$documentType.css('display','inline');
			}else if(ACC.accountdocuments.selectedSearchType === 'class java.util.Date'){
				ACC.accountdocuments.$date_criteria.css('display','inline');
			}else if(ACC.accountdocuments.selectedSearchType === 'class java.math.BigDecimal'){
				ACC.accountdocuments.$amount_criteria.css('display','inline');
			}else if(ACC.accountdocuments.selectedSearchType === 'class java.lang.String'){
				ACC.accountdocuments.$searchValue.css('display','inline');
			}
		});
		
		// Status
		$("input[name=status]").live("click", function(){
			ACC.accountdocuments.status = $("input[name=status]:checked").attr('id');
		});
		
		//Bind enter keypress
		$(".search_key :input").keypress(function( event ) {
			if ( event.which == 13 ) {
				$("button#search").click();
			}
		});
		
	},
	
	cleanSearchFields: function(){
		$('.searchcriteria').val('');
	},
	
	initdatepicker: function(){
		ACC.accountdocuments.$dateMin.datepicker({
		 	 changeMonth: true,
			 numberOfMonths: 1,
			 onClose: function( selectedDate ) {
				 ACC.accountdocuments.$dateMax.datepicker( "option", "minDate", selectedDate );
			 }
		 });
		 
		ACC.accountdocuments.$dateMax.datepicker({
			 changeMonth: true,
			 numberOfMonths: 1,
			 onClose: function( selectedDate ) {
				 ACC.accountdocuments.$dateMin.datepicker( "option", "maxDate", selectedDate );
				}
		 });
	},
	
	
	initialize : function() {
		with (ACC.accountdocuments) {
			bindShowMoreResults($showMoreResultsArea);
			bindSortingSelector();
			bindSearchButton();
			bindSearchCriteria();
			initdatepicker();
			bindViewSwitch();
			
			if(getCookie("accountSummaryViewMode") !== null){
				ACC.accountdocuments.$resultview = getCookie("accountSummaryViewMode");
			}else{
				setCookie("accountSummaryViewMode", ACC.accountdocuments.$resultview);
			}
		}
	},
	
	setCookie : function (c_name,value)
	{
		var exdays = 10;
		var exdate=new Date();
		exdate.setDate(exdate.getDate() + exdays );
		var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
		document.cookie=c_name + "=" + c_value;
	},	
	
	getCookie : function (c_name)
	{
		var c_value = document.cookie;
		var c_start = c_value.indexOf(" " + c_name + "=");
		if (c_start == -1) {
			c_start = c_value.indexOf(c_name + "=");
		}
		if (c_start == -1){
			c_value = null;
		}else{
		  c_start = c_value.indexOf("=", c_start) + 1;
		  var c_end = c_value.indexOf(";", c_start);
		  if (c_end == -1){
			  c_end = c_value.length;
		 }
		 c_value = unescape(c_value.substring(c_start,c_end));
		}
		return c_value;
	}
};


//============================= PAYMENT REDIRECT FROM DOCUMENT LISTING PAGE  =============================
ACC.PaymentRedirect = {
		
		 initialize: function(){
			
			
			
			// when click on PAY button
			$("#bnPay").click( function(){
				
				// make form data
				var formData = '' ;
				
				//  ============================= if no document is selected to credit card payment page  =============================
				if ( $(".acc_document_selected").length == 0 ){
					
					formData = '<form action="document/payment" method="post">' ;
					
					$("[useorpay='PAY']").each( function (){ 
						
						formData = formData + '<input type=hidden name="selectedDocuments" value="' + $(this).attr("documentNumber") + '" />'
						
					});
					
				} else 
				
				//  ============================= if interface is turn off then go directly to credit card payment page  =============================
				if (   $(this).attr("payanduse") === "false"  )
				{
				
					formData = '<form action="document/payment" method="post">' ;
										
					// for each selected document
					$(".acc_document_selected[useorpay='PAY']").each( function (){ 
				
						formData = formData + '<input type=hidden name="selectedDocuments" value="' + $(this).attr("documentNumber") + '" />'
						
					});
			
				
				} else
				//  ============================= if interface is turn on then go directly to drag and drop page  =============================
				{
					
					formData = '<form action="document/using" method="post">' ;
					
					// pay documents
					var payDocs = $(".acc_document_selected[useorpay='PAY']").length==0 ? $("[useorpay='PAY']") : $(".acc_document_selected[useorpay='PAY']");
					// use documents
					var useDocs = $(".acc_document_selected[useorpay='USE']").length==0 ? $("[useorpay='USE']") : $(".acc_document_selected[useorpay='USE']");
					
					payDocs.each( function (){ 
						
						formData = formData + '<input type=hidden name="selectedDocuments" value="' + $(this).attr("documentNumber") + '" />'
						
					});
					
					useDocs.each( function (){ 
						
						formData = formData + '<input type=hidden name="selectedDocuments" value="' + $(this).attr("documentNumber") + '" />'
						
					});
				}
				
				// make form and submit
				formData = formData + '</form>' ;
				var form = $( formData );
				$('body').append(form);
				$(form).submit();
			
			});
			
			
		} // end initialize
} // end payment redirect namespace




$(document).ready(function() {
	$(document).on('cbox_complete', function (){ $.colorbox.resize(); });
	ACC.accountdocuments.initialize();

	

	
	
});


