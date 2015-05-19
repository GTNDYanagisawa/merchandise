<span class="promo-stylesheetChipMarker"></span>
<script type="text/javascript">

function injectPromotionsStylesheet()
{
	var head=document.getElementsByTagName('head').item(0);

	var link = document.createElement("link");
	link.setAttribute("rel", "stylesheet");
	link.setAttribute("type", "text/css");
	link.setAttribute("media", "all");
	link.setAttribute("href", "css/promotions.css");

	head.appendChild(link);
}

if (!document.injectedPromotionsStylesheet)
{
	document.injectedPromotionsStylesheet = true;
	injectPromotionsStylesheet();
}

/* Squash the table row containing this chip as we don't want to display anything and it has fixed height */
var matchedElements = domQuery('tr > td > div > span.promo-stylesheetChipMarker');
for(var i=0; i<matchedElements.length; i++)
{
	var trNode = matchedElements[i].parentNode.parentNode.parentNode;
	trNode.setAttribute("style","display:none;");
}
</script>
