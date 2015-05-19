<%@include file="head.inc"%>

<script src="js/scriptaculous-test.js" type="text/javascript" language="JavaScript1.2"></script>	



<div>
	<div style="margin:5px 0 0 5px; vertical-align:middle;">
		<div style="float:left; margin-top:5px;">Product Code (startswith):</div>
		<div style="margin-left:150px;">
			<input type="text" id="productsearch" style="width:200px;"/>
			<span id="resultproduct">&nbsp;</span>
			<div id="productlist" class="autocomplete">
			</div>
		</div>

		<script language="JavaScript1.2">
			
			new Ajax.Autocompleter("productsearch", "productlist", "prototype?method=search&typecode=product&attributename=code&operator=startswith", { paramName: "value", afterUpdateElement: (new AutocompleterCallback(document.getElementById("resultproduct").firstChild)).callback });
		
		</script>	

<!--
		<a href="#" onClick="sc_search(document.getElementById('productsearch').value)">
			klick
		</a>
-->
	</div>

	<div style="margin:5px 0 0 5px; vertical-align:middle;">
		<div style="float:left; margin-top:5px;">User ID (contains):</div>
		<div style="margin-left:150px;">
			<input type="text" id="usersearch" style="width:200px;"/>
			<span id="resultuser">&nbsp;</span>
			<div id="userlist" class="autocomplete">
			</div>
		</div>

		<script language="JavaScript1.2">
			
			new Ajax.Autocompleter("usersearch", "userlist", "prototype?method=search&typecode=user&attributename=uid", { paramName: "value", afterUpdateElement: (new AutocompleterCallback(document.getElementById("resultuser").firstChild)).callback});
		
		</script>	
	</div>
</div>
