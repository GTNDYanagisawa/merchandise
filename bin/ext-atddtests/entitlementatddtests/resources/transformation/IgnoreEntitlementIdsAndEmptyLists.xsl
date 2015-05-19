<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<!-- ignore all elements with attribute class="empty-list" -->
	<xsl:template match="node()[@class='empty-list']"/>

	<xsl:template match="//entitlement/id"/>
</xsl:stylesheet>