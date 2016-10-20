<?xml version="1.0" encoding="euc-kr"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

<!-- 닥북 한글 스타일시트 (http://kldp.net/projects/docbook/) -->
<!-- $Id: $ -->

<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl"/>
<xsl:import href="dbk-common.xsl"/>

<!-- Saxon 6 + Kimber 인덱스 -->
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/autoidx-kimber.xsl"/>
<xsl:param name="index.method">kimber</xsl:param>

<xsl:output method="xml" encoding="utf-8"/>

<xsl:param name="paper.type" select="'A4'"/>

<xsl:param name="dingbat.font.family" select="'Times Roman'"/>
<xsl:param name="hyphenate">false</xsl:param>
<xsl:param name="fop1.extensions" select="1"></xsl:param>

<!-- fo/lists.xsl 1.37 -->
<xsl:template match="itemizedlist/listitem">
  <xsl:variable name="id"><xsl:call-template name="object.id"/></xsl:variable>

  <xsl:variable name="itemsymbol">
    <xsl:call-template name="list.itemsymbol">
      <xsl:with-param name="node" select="parent::itemizedlist"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="item.contents">
    <fo:list-item-label end-indent="label-end()">
      <fo:block>
        <fo:inline font-family="'Times Roman'">
        <xsl:choose>
          <xsl:when test="$itemsymbol='disc'">&#x2022;</xsl:when>
          <xsl:when test="$itemsymbol='bullet'">&#x2022;</xsl:when>
          <!-- why do these symbols not work? -->
          <!--
          <xsl:when test="$itemsymbol='circle'">&#x2218;</xsl:when>
          <xsl:when test="$itemsymbol='round'">&#x2218;</xsl:when>
          <xsl:when test="$itemsymbol='square'">&#x2610;</xsl:when>
          <xsl:when test="$itemsymbol='box'">&#x2610;</xsl:when>
          -->
          <xsl:otherwise>&#x2022;</xsl:otherwise>
        </xsl:choose>
        </fo:inline>
      </fo:block>
    </fo:list-item-label>
    <fo:list-item-body start-indent="body-start()">
      <xsl:apply-templates/>
    </fo:list-item-body>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="parent::*/@spacing = 'compact'">
      <fo:list-item id="{$id}" xsl:use-attribute-sets="compact.list.item.spacing">
        <xsl:copy-of select="$item.contents"/>
      </fo:list-item>
    </xsl:when>
    <xsl:otherwise>
      <fo:list-item id="{$id}" xsl:use-attribute-sets="list.item.spacing">
        <xsl:copy-of select="$item.contents"/>
      </fo:list-item>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- othercredit/email이 있는 경우, 이름 뒤에 이메일 주소를 출력한다. -->
<!-- fo/titlepage.xsl 1.23 -->
<xsl:template match="othercredit" mode="titlepage.mode">
  <xsl:variable name="contrib" select="string(contrib)"/>
  <xsl:choose>
    <xsl:when test="contrib">
      <xsl:if test="not(preceding-sibling::othercredit[string(contrib)=$contrib])">
        <fo:block>
          <xsl:apply-templates mode="titlepage.mode" select="contrib"/>
          <xsl:text>: </xsl:text>
          <xsl:call-template name="person.name"/>
          <!-- 이름 뒤에 이메일 주소 출력 -->
          <xsl:choose>
            <xsl:when test="email">
              <xsl:text> </xsl:text>
              <xsl:apply-templates select="email"/>
            </xsl:when>
          </xsl:choose>
          <xsl:apply-templates mode="titlepage.mode" select="affiliation"/>
          <xsl:apply-templates select="following-sibling::othercredit[string(contrib)=$contrib]" mode="titlepage.othercredits"/>
        </fo:block>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <fo:block><xsl:call-template name="person.name"/></fo:block>
      <xsl:apply-templates mode="titlepage.mode" select="./affiliation"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
