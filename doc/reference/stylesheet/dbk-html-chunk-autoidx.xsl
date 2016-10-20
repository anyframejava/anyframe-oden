<?xml version="1.0" encoding="euc-kr"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<!-- 닥북 한글 스타일시트 (http://kldp.net/projects/docbook/) -->
<!-- $Id: dbk-html-chunk.xsl,v 1.4 2003/08/09 22:28:01 minskim Exp $ -->
	   
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/xhtml/chunk.xsl"/>

<xsl:import href="dbk-html-common.xsl"/>

<!-- Saxon 6 + Kimber 인덱스 -->
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/xhtml/autoidx-kimber.xsl"/>
<xsl:param name="index.method">kimber</xsl:param>

<!-- Xalan에서 생성하는 HTML에 인코딩을 추가하기 위해 필요 -->
<xsl:output method="xml" encoding="utf-8"/>

<xsl:param name="chunker.output.encoding" select="'utf-8'"/>

<xsl:param name="chunk.first.sections" select="'1'"/>

<!-- email.nospam을 1로 설정하면 email 주소의 '@'를 ' (at) '로 바꾸어 출력 -->
<!-- <xsl:param name="email.nospam" select="'1'"/> -->

</xsl:stylesheet>
