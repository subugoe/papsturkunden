<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:saxon="http://saxon.sf.net/" exclude-result-prefixes="saxon xs">

  <xsl:output method="xml" indent="yes" />

  <xsl:template match="/">
    <add>
      <xsl:apply-templates select="html/body/div/p[text()]" />
    </add>
  </xsl:template>
  
  <xsl:template match="p">
    <doc>
      <field name="id">
        <xsl:value-of select="position()" />
      </field>
      <field name="page">
        <xsl:value-of select="count(preceding::div[@class='page']) - 31" />
      </field>
      <field name="section">
        <xsl:value-of select="position()" />
      </field>
      <field name="text">
        <xsl:value-of select="text()" />
      </field>
      <field name="text_spaceless">
        <xsl:value-of select="replace(text(), '\s', '')" />
      </field>
    </doc>
  </xsl:template>
  
</xsl:stylesheet>