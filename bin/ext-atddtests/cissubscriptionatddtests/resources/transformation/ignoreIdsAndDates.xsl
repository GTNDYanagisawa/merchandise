<!-- [y] hybris Platform Copyright (c) 2000-2013 hybris AG All rights reserved. 
	This software is the confidential and proprietary information of hybris ("Confidential 
	Information"). You shall not disclose such Confidential Information and shall 
	use it only in accordance with the terms of the license agreement you entered 
	into with hybris. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="node() | @*">
  		<xsl:copy>
	        <xsl:apply-templates select="node() | @*" />
	    </xsl:copy>
	</xsl:template>

	<!-- ignore id attribute of element subscriptionorder by always setting their value to -1 -->
	<xsl:template  match="subscriptionorder/@id">
   		<xsl:attribute name="id">-1</xsl:attribute> 
	</xsl:template>
	
	<!-- ignore id attribute of element subscriptionorder by always setting their value to -1 -->
	<xsl:template  match="subscriptionPlan/@id">
   		<xsl:attribute name="id">-1</xsl:attribute> 
	</xsl:template>
	
	<!-- ignore all id elements by always setting their value to -1 -->
	<xsl:template match="id">
   		<id>-1</id> 
	</xsl:template>
	
	<!-- ignore all date elements by always setting their value to current date -->
	<xsl:template match="date">
   		<date>current Date</date> 
	</xsl:template>
	
</xsl:stylesheet>