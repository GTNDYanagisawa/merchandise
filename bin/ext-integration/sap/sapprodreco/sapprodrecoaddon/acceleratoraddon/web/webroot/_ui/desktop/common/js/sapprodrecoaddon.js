function addRecommendation(data) {
	if (data !== ''){
		$("#reco").append(data);
		jQuery('#recommendationUL').jcarousel({
			vertical: false
		});
	}	
	else {
		$("#reco").removeClass();
	}
};	


function retrieveRecommendations(title,recommendationModel,productCode,itemType,includeCart){	
	ajaxUrl = '/yacceleratorstorefront/action/recommendations/' + title+ '/'+ recommendationModel+ '/' + productCode+ '/' + itemType + '/' + includeCart;
	$.get(ajaxUrl,addRecommendation);
};

