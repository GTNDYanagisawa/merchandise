ACC.langcurrency = {

	bindLangCurrencySelector: function (){

		$('#lang-selector').change(function(){
			$('#lang-form').submit();
		});

		$('#currency-selector').change(function(){
			$('#currency-form').submit();
		});
	}
};


$(function(){
	with(ACC.langcurrency){
		bindLangCurrencySelector();
	}
});