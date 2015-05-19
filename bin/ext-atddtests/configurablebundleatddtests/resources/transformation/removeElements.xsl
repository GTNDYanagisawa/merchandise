<!-- [y] hybris Platform Copyright (c) 2000-2014 hybris AG All rights reserved. 
	This software is the confidential and proprietary information of hybris ("Confidential 
	Information"). You shall not disclose such Confidential Information and shall 
	use it only in accordance with the terms of the license agreement you entered 
	into with hybris. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output omit-xml-declaration="yes" indent="yes"/>

 <xsl:template match="node()|@*">
  <xsl:copy>
   <xsl:apply-templates select="node()|@*"/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="baseOptions|url|manufacturer|soldIndividually|preselected|purchasable|price"  />
	
</xsl:stylesheet>

