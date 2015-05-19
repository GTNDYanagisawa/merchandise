<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:functx="http://www.xsltfunctions.com/xsl/functx-1.0-doc-2007-01.xsl"> 

<xsl:key name="k1" match="violation" use="@priority"/>
<xsl:key name="k2" match="violation" use="concat(@priority, '|', @rule)"/>

<xsl:template match="/">
<xsl:text disable-output-escaping="yes"><![CDATA[
<testsuites>
    <testsuite failures="0" errors="]]></xsl:text><xsl:value-of select="count(//violation[@priority = 1])"/>
<xsl:text disable-output-escaping="yes"><![CDATA[" name="" tests="104">
]]></xsl:text>
  <xsl:apply-templates select="//violation[generate-id() = generate-id(key('k1', @priority)[1])]"/>
 <xsl:text disable-output-escaping="yes"><![CDATA[</testsuite> 
</testsuites> 
]]></xsl:text>
</xsl:template>

<xsl:template match="violation">
<xsl:text disable-output-escaping="yes"><![CDATA[       <testcase classname="Priorität" name="]]></xsl:text>
<xsl:value-of select="@priority"/>
<xsl:text disable-output-escaping="yes"><![CDATA[" time="]]></xsl:text>
<xsl:value-of select="count(key('k2', concat(@priority, '|', @rule)))"/>
<xsl:text disable-output-escaping="yes"><![CDATA[">
<failure type="">]]></xsl:text>
  <xsl:apply-templates select="key('k1', @priority)[generate-id() = generate-id(key('k2', concat(@priority, '|', @rule))[1])]" mode="rule"/>
  <xsl:text disable-output-escaping="yes"><![CDATA[</failure>
  </testcase>
]]></xsl:text>
</xsl:template>

<xsl:template match="violation" mode="rule">
  <xsl:value-of select="concat(count(key('k2', concat(@priority, '|', @rule))), ' ', @rule, ' ')"/>
</xsl:template>

</xsl:stylesheet> 