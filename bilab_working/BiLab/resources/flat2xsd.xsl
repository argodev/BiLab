<!-- $Id$ -->
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'
                xmlns:xsd='http://www.w3.org/2001/XMLSchema'>

 <xsl:output method='xml'/>

 <!--xsl:include href='funcs.xsl'/ PASTED BELOW -->
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
 <!-- END funcs.xsl -->

 <!-- MAIN TEMPLATES -->

 <xsl:template match='/'>
  <xsl:if test='dtd/@sysid'>
   <xsl:comment>Generated from <xsl:value-of select='dtd/@sysid'/></xsl:comment>
  </xsl:if>
  <xsd:schema>
   <xsl:apply-templates select='dtd/*'/>
  </xsd:schema>
 </xsl:template>

 <xsl:template match='externalSubset|parameterEntity|conditional'>
  <xsl:message terminate='yes'>
ERROR: The DTDx file MUST be converted to a flattened form before 
       generating the XML Schema file. Please use the dtd2flat.xsl
       stylesheet first and then process that output with this
       stylesheet.</xsl:message>
 </xsl:template>

 <!-- TOP-LEVEL TEMPLATES -->

 <xsl:template match='elementDecl'>
  <xsl:call-template name='comment_elementDecl'/>
  <xsl:variable name='ename'><xsl:value-of select='@ename'/></xsl:variable>
  <xsd:element name='{$ename}'>
   <xsd:complexType>
    <xsl:apply-templates select='(/dtd/contentModel[@ename=$ename])[1]' mode='define'/>
    <xsl:apply-templates select='/dtd/attlist[@ename=$ename]/attributeDecl' mode='define'/>
   </xsd:complexType>
  </xsd:element>
 </xsl:template>

 <xsl:template match='notationDecl'>
  <xsl:comment>
   <xsl:text>&lt;!NOTATION </xsl:text>
   <xsl:value-of select='@name'/>
   <xsl:choose>
    <xsl:when test='@pubid'>
     <xsl:text> PUBLIC "</xsl:text>
     <xsl:value-of select='@pubid'/>
     <xsl:text>"</xsl:text>
    </xsl:when>
    <xsl:otherwise> SYSTEM</xsl:otherwise>
   </xsl:choose>
   <xsl:if test='@sysid'>
    <xsl:text> "</xsl:text>
    <xsl:value-of select='@sysid'/>
    <xsl:text>"</xsl:text>
   </xsl:if>
   <xsl:text>&gt;</xsl:text>
  </xsl:comment>
  <xsd:notation name='{@name}'>
   <xsl:if test='@pubid'>
    <xsl:attribute name='public'><xsl:value-of select='@pubid'/></xsl:attribute>
   </xsl:if>
   <xsl:if test='@sysid'>
    <xsl:attribute name='system'><xsl:value-of select='@sysid'/></xsl:attribute>
   </xsl:if>
  </xsd:notation>
 </xsl:template>

 <!-- MISC. TEMPLATES -->

 <xsl:template match='processingInstruction'>
  <xsl:processing-instruction name='{@target}'><xsl:value-of select='@value'/></xsl:processing-instruction>
 </xsl:template>

 <xsl:template match='comment'>
  <xsl:comment><xsl:value-of select='.'/></xsl:comment>
 </xsl:template>

 <!-- ATTRIBUTE TEMPLATES -->

 <xsl:template match='attributeDecl' mode='define'>
  <xsl:variable name='ename'><xsl:value-of select='@ename'/></xsl:variable>
  <xsl:variable name='aname'><xsl:value-of select='@aname'/></xsl:variable>
  <xsl:if test='not(preceding::attributeDecl[@aname=$aname] and preceding::elementDecl[@ename=$ename])'>
   <xsl:call-template name='comment_attributeDecl'/>
   <xsd:attribute name='{$aname}'>
    <xsl:if test='@required="true"'>
     <xsl:attribute name='use'>required</xsl:attribute>
    </xsl:if>
    <xsl:if test='@default'>
     <xsl:choose>
      <xsl:when test='@fixed="true"'>
       <xsl:attribute name='fixed'><xsl:value-of select='@default'/></xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
       <xsl:attribute name='default'><xsl:value-of select='@default'/></xsl:attribute>
      </xsl:otherwise>
     </xsl:choose>
    </xsl:if>
    <xsd:simpleType>
     <xsd:restriction>
      <xsl:attribute name='base'>
       <xsl:text>xsd:</xsl:text>
       <xsl:choose>
        <xsl:when test='@atype="CDATA" or contains(@atype,"(")'>string</xsl:when>
        <xsl:otherwise><xsl:value-of select='@atype'/></xsl:otherwise>
       </xsl:choose>
      </xsl:attribute>
      <xsl:for-each select='enumeration'>
       <xsd:enumeration value='{@value}'/>
      </xsl:for-each>
     </xsd:restriction>
    </xsd:simpleType>
   </xsd:attribute>
  </xsl:if>
 </xsl:template>

 <!-- CONTENT MODEL TEMPLATES -->

 <xsl:template match='contentModel' mode='define'>
  <xsl:choose>
   <xsl:when test='any'>
    <xsl:attribute name='mixed'>true</xsl:attribute>
    <xsd:choice minOccurs='0' maxOccurs='unbounded'>
     <xsl:for-each select='/dtd/elementDecl'>
      <xsl:sort select='@ename'/>
      <xsd:element ref='{@ename}'/>
     </xsl:for-each>
    </xsd:choice>
   </xsl:when>
   <xsl:when test='empty'/>
   <xsl:otherwise>
    <xsl:choose>
     <xsl:when test='group/pcdata'>
      <xsl:attribute name='mixed'>true</xsl:attribute>
      <xsl:if test='group/element'>
       <xsd:choice minOccurs='0' maxOccurs='unbounded'>
        <xsl:for-each select='group/element'>
         <xsd:element ref='{@name}'/>
        </xsl:for-each>
       </xsd:choice>
      </xsl:if>
     </xsl:when>
     <xsl:otherwise>
      <xsl:apply-templates select='group' mode='define'/>
     </xsl:otherwise>
    </xsl:choose>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match='element' mode='define'>
  <xsd:element ref='{@name}'>
   <xsl:variable name='occurs'>
    <xsl:value-of select='following-sibling::*[1][name()="occurrence"]/@type'/>
   </xsl:variable>
   <xsl:choose>
    <xsl:when test='$occurs="?"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>1</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="*"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="+"'>
     <xsl:attribute name='minOccurs'>1</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
   </xsl:choose>
  </xsd:element>
 </xsl:template>

 <xsl:template match='group' mode='define'>
  <xsl:choose>
   <xsl:when test='separator[1]'>
    <xsl:for-each select='separator[1]'>
     <xsl:call-template name='groupType'/>
    </xsl:for-each>
   </xsl:when>
   <xsl:otherwise>
    <xsl:call-template name='choice'/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <!-- FUNCTIONS -->

 <xsl:template name='sequence'>
  <xsd:sequence>
   <xsl:variable name='occurs'>
    <xsl:value-of select='following-sibling::*[1][name()="occurrence"]/@type'/>
   </xsl:variable>
   <xsl:choose>
    <xsl:when test='$occurs="?"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>1</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="*"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="+"'>
     <xsl:attribute name='minOccurs'>1</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:for-each select='element|group'>
    <xsl:apply-templates select='.' mode='define'/>
   </xsl:for-each>
  </xsd:sequence>
 </xsl:template>

 <xsl:template name='choice'>
  <xsd:choice>
   <xsl:variable name='occurs'>
    <xsl:value-of select='following-sibling::*[1][name()="occurrence"]/@type'/>
   </xsl:variable>
   <xsl:choose>
    <xsl:when test='$occurs="?"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>1</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="*"'>
     <xsl:attribute name='minOccurs'>0</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
    <xsl:when test='$occurs="+"'>
     <xsl:attribute name='minOccurs'>1</xsl:attribute>
     <xsl:attribute name='maxOccurs'>unbounded</xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:for-each select='element|group'>
    <xsl:apply-templates select='.' mode='define'/>
   </xsl:for-each>
  </xsd:choice>
 </xsl:template>

 <xsl:template name='groupType'>
  <xsl:choose>
   <xsl:when test='@type="|"'>
    <xsl:for-each select='parent::group'>
     <xsl:call-template name='choice'/>
    </xsl:for-each>
   </xsl:when>
   <xsl:otherwise>
    <xsl:for-each select='parent::group'>
     <xsl:call-template name='sequence'/>
    </xsl:for-each>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

</xsl:stylesheet>
