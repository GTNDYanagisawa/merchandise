<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.mobileservices.hmc.QRCodeDeeplinkChip"%>
<%@page import="java.util.*"%>
<%
	QRCodeDeeplinkChip chip = (QRCodeDeeplinkChip)request.getAttribute("theChip");

%>






<!-- Start Hybris header -->


<table cellspacing="0" cellpadding="0" width="100%" style="height:100%;">
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" class="propertytable" width="100%">
				<tr class="propertyrow">
					<td class="sectionheader"><div class="sh">
					<%= chip.localized("header") %>
					</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table cellspacing="0" cellpadding="20" width="100%" height="10px">
				<tr nowrap>
					<td width="100%">
						<div id="product-mobileTab">
<!-- End Hybris header -->
<script type="text/javascript">
barcodes={
   <% 
    Map<String,String> campaigns =chip.getCampaigns();
     for (String k: campaigns.keySet()) {
	   out.println("'"+k+"': '"+chip.barcode(k)+"',");
   }
   %>
   dummy: ''	
} 


function mbshow(element) {
   
	state='inline';
	var style;
	if('string' == typeof element) {
		if(document.getElementById) {
			element = document.getElementById(element);
		}  else if(document.all) {
			element = document.all[element];
		} else if(document.layers) {
			element = document.layers[element];
		} 
	}
	if(element) {
		style = element.style || element;
		style.display = state;
	}
} 
function mbhide(element) { 
	state='none'; 
	var style;
	if('string' == typeof element) {
		if(document.getElementById) {
			element = document.getElementById(element);
		}  else if(document.all) {
			element = document.all[element];
		} else if(document.layers) {
			element = document.layers[element];
		} 
	}
	if(element) {
		style = element.style || element;
		style.display = state;
	}
 
}
function setText(thenode,text)
{
	var span = document.createElement('span');
	span.innerHTML=text;
	removeChildNodes(thenode);
	thenode.appendChild(span);
}
function removeChildNodes(obj)
{
	if(obj.hasChildNodes() && obj.childNodes) {
		while(obj.firstChild) {
			obj.removeChild(obj.firstChild);
		}
	}
}
function selectcampaign()
{
	
	var select = document.getElementById("cvselect");
	   
	var c=select.options[select.selectedIndex].value;
	if (c == "") {
		return;
	}
	  
	var current = document.getElementById("pbarcode");
	var barcode = barcodes[c] == "" ? false: true;
	   
	if (barcode) {
	    setText(current,
		"<p align='center'>"+
		"<p align='center'> <img src='"+barcodes[c]+"' alt='barcode' /></p>"+
		 "<p align='center'> <a href='"+barcodes[c]+"'><%= chip.localized("download") %></a></p>"+
		 "</p>");
		 mbhide("generate-button");
		 mbshow("regenerate-button");
		 
	} else {
	    setText(current,"<p align='center'></p>");
		
		 mbhide("regenerate-button");
		 mbshow("generate-button");
	}
	    
}
</script>

                           <p align="center"><select name='<%=chip.getCommandID(chip.DEEPLINK)%>' id='cvselect' onchange="selectcampaign()">
                           <option value="" selected="selected"><%= chip.localized("selectdeeplink") %></option>
                           <% 
                           Map<String,String> cs =chip.getCampaigns(); 
                           for (String k: cs.keySet()) { %>
                           <option value="<%=k %>"><%=cs.get(k) %></option>
                           <% } %>
                           </select>
                            <p align="center" id="pbarcode"></p>
                            
                            
                             <div id="generate-button" style="display:none" class="chip-event">
                            <a href="#"   title='<%= chip.localized("generate") %>'  name='<%= chip.getCommandID(chip.GENERATE) %>' hidefocus="true">
                            <p align="center">
                            <button><%= chip.localized("generate") %></button>
	               			</p>
	               			</a>
		                    </div>
		 
                            <div id="regenerate-button" style="display:none" class="chip-event">
                            <a href="#"   title='<%= chip.localized("regenerate") %>'  name='<%= chip.getCommandID(chip.REGENERATE) %>' hidefocus="true">
                            <p align="center">
                            <button><%= chip.localized("regenerate") %></button>
	               			</p>
	               			</a>
		                    </div>
		                    
		                    
                           							

<!-- Start Hybris footer -->
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<!-- End hybris footer -->