<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="node() | @*">
  		<xsl:copy>
	        <xsl:apply-templates select="node() | @*" />
	    </xsl:copy>
	</xsl:template>

	<!-- ignore all id attributes and elements by always setting their value to -1 -->
	<xsl:template match="@id">
   		<xsl:attribute name="id">-1</xsl:attribute> 
	</xsl:template>
	
	<xsl:template match="id">
   		<id>-1</id> 
	</xsl:template>
	
</xsl:stylesheet>