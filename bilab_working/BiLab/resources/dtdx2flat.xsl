<!-- $Id -->
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

 <xsl:template match='/'>
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match='@*|dtd|elementDecl|attlist|attributeDecl|enumeration'>
  <xsl:copy>
   <xsl:apply-templates select='@*|*'/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match='internalEntityDecl|externalEntityDecl|unparsedEntityDecl|notationDecl'>
  <xsl:copy>
   <xsl:apply-templates select='@*|*'/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match='group[count(*)=1][group]'>
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match='contentModel|any|empty|group|pcdata|element|separator|occurrence'>
  <xsl:copy>
   <xsl:apply-templates select='@*|*'/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match='*'>
  <xsl:apply-templates/>
 </xsl:template>

</xsl:stylesheet>