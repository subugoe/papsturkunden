<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xpath-default-namespace="http://www.tei-c.org/ns/1.0" xmlns:fwb="http://sub.fwb.de"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:saxon="http://saxon.sf.net/" exclude-result-prefixes="saxon xs">

  <xsl:output method="xml" indent="yes" />

  <xsl:param name="currentArticleId" />

  <xsl:template match="/">
    <add>
      <doc>
        <field name="id">
          <xsl:value-of select="$currentArticleId" />
        </field>
      </doc>
    </add>
  </xsl:template>

</xsl:stylesheet>