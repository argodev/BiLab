<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

 <!-- COMMENTS -->

 <xsl:template name='comment_contentModel'>
  <xsl:for-each select='following::elementDecl[1]'>
   <xsl:call-template name='comment_elementDecl'/>
  </xsl:for-each>
 </xsl:template>

 <xsl:template name='comment_elementDecl'>
  <xsl:comment>
   <xsl:text>&lt;!ELEMENT </xsl:text>
   <xsl:value-of select='@ename'/>
   <xsl:text> </xsl:text>
   <xsl:value-of select='@model'/>
   <xsl:text>&gt;</xsl:text>
  </xsl:comment>
 </xsl:template>

 <xsl:template name='comment_attributeDecl'>
  <xsl:comment>
   <xsl:text>&lt;!ATTLIST </xsl:text>
   <xsl:value-of select='@ename'/>
   <xsl:text> </xsl:text>
   <xsl:value-of select='@aname'/>
   <xsl:text> </xsl:text>
   <xsl:value-of select='@atype'/>
   <xsl:if test='@atype="NOTATION"'>
    <xsl:text> (</xsl:text>
    <xsl:for-each select='enumeration'>
     <xsl:if test='position()&gt;1'>|</xsl:if>
     <xsl:value-of select='@value'/>
    </xsl:for-each>
    <xsl:text>)</xsl:text>
   </xsl:if>
   <xsl:choose>
    <xsl:when test='@required'> #REQUIRED</xsl:when>
    <xsl:when test='@fixed'> #FIXED</xsl:when>
    <xsl:when test='not(@default)'> #IMPLIED</xsl:when>
   </xsl:choose>
   <xsl:if test='@default'>
    <xsl:text> "</xsl:text>
    <xsl:call-template name='escape'>
     <xsl:with-param name='s'><xsl:value-of select='@default'/></xsl:with-param>
    </xsl:call-template>
    <xsl:text>"</xsl:text>
   </xsl:if>
   <xsl:text>&gt;</xsl:text>
  </xsl:comment>
 </xsl:template>

 <xsl:template name='escape'>
  <xsl:param name='s'/>
  <xsl:param name='c'>"</xsl:param>
  <xsl:param name='C'>&amp;#34;</xsl:param>
  <xsl:if test='string-length($s)&gt;0'>
   <xsl:choose>
    <xsl:when test='contains($s,$c)'>
     <xsl:value-of select='substring-before($s,$c)'/>
     <xsl:value-of select='$C'/>
     <xsl:call-template name='escape'>
      <xsl:with-param name='s'>
       <xsl:value-of select='substring(substring-after($s,$c),2)'/>
      </xsl:with-param>
     </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
     <xsl:value-of select='$s'/>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:if>
 </xsl:template>

</xsl:stylesheet>