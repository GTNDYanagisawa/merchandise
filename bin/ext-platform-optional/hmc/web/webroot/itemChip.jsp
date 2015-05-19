<%@ page import="de.hybris.platform.hmc.jalo.WizardBusinessItem" %>
<%@include file="head.inc"%>
<%
	ItemChip theChip = (ItemChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean isWizard = (theChip instanceof GenericItemChip) ? ((GenericItemChip) theChip).isWizard() : false;
	boolean isProcessing = isWizard && ((GenericItemChip) theChip).getEditor().isProcessingMode();
	
	String closeErrorMessage = isWizard ? ((GenericItemChip) theChip).getCloseErrorMessage() : "";
%>
<table class="itemChip" cellspacing="0" cellpadding="0">
	<tr <%= isWizard && !isProcessing ? "class=\"hidden\"" : "" %>>
		<td class="icToolbar" style="<%= !theChip.isPartOf() ? "height:27px; background-color:#D3D3D8;" : "" %>"><% theChip.getToolbar().render(pageContext); %></td>
	</tr>
	<tr>
		<td class="icEditor"><% theChip.getEditor().render(pageContext); %></td>
	</tr>
<%
	if( theChip.hasFooter() )
	{
%>
	<tr>
		<td class="icFooter"><% theChip.getFooter().render(pageContext); %></td>
	</tr>
<%
	}
	else if( !theChip.isPartOf() )
	{
%>
	<tr class="icTreeCorners">
		<td>
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td class="left"><img src="images/tree_corner_ll.gif"></td>
					<td class="middle"> </td>
					<td class="right"><img src="images/tree_corner_lr.gif"></td>
				</tr>
			</table>
		</td>
	</tr>
<%	
	}
	else
	{
%>	
	<tr class="icEditorCorners">
		<td>
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td class="left"><img src="images/editortab_corner_bl.gif"/></td>
					<td class="middle"> </td>
					<td class="right"><img src="images/editortab_corner_br.gif"/></td>
				</tr>
			</table>
		</td>
	</tr>
<%
	}
%>
</table>
<%
	if( isWizard && !((GenericItemChip) theChip).getWizardBusinessItem().isCloseAllowed() )
	{
		// prevent window closing
%>
		<SCRIPT language="Javascript">
			exit = true;
			
			var oldLeft, oldTop;
			var ie5 = document.all && document.getElementById;
			if( ie5 )
			{
				oldLeft = window.screenLeft;
				oldTop = window.screenTop;
			}
			else
			{
				oldLeft = window.screenX;
				oldTop = window.screenY;
			}

			function reload()
			{ 
				if( exit )
				{
					alert("<%= closeErrorMessage %>");
					
					var myname = window.name;
					var newLeft, newTop, newWidth, newHeight;
					
					if( ie5 )
					{
						newLeft = (window.screenLeft < 10000 ?  window.screenLeft : oldLeft) - 4;
						newTop = (window.screenTop < 10000 ?  window.screenTop : oldTop) - 23;
						
						newWidth = document.body.offsetWidth - 4;
						newHeight = document.body.offsetHeight - 4;
					}
					else
					{
						newLeft = (window.screenX < 10000 ?  window.screenX : oldLeft);
						newTop = (window.screenY < 10000 ?  window.screenY : oldTop);
						
						newWidth = window.innerWidth;
						newHeight = window.innerHeight; 
					}
					
					window.name = "old";					
					window.close();
					var newWindow = window.open(self.location.href, myname,  "resizable=yes"
																								+ ",width=" + newWidth
																								+ ",height=" + newHeight
																								+ ",left=" + newLeft
																								+ ",top=" + newTop
																								+ ",location=no,menubar=no,status=yes,toolbar=no,scrollbars=yes");					
				}
			}
			
			window.onunload = reload;
		</SCRIPT>		
<%
	}
%>
