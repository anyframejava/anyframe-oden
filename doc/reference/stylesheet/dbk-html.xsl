<?xml version='1.0' encoding="EUC-KR"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>
                
<!-- �ں� �ѱ� ��Ÿ�Ͻ�Ʈ (http://kldp.net/projects/docbook/) -->
<!-- $Id: dbk-html.xsl,v 1.8 2003/08/09 22:28:01 minskim Exp $ -->
                
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/xhtml/docbook.xsl"/>

<xsl:import href="dbk-html-common.xsl"/>

<!-- select euc-kr or utf-8 -->
<xsl:output method="xml" encoding="utf-8"/>

<!-- email.nospam�� 1�� �����ϸ� email �ּ��� '@'�� ' (at) '�� �ٲپ� ��� -->
<!-- <xsl:param name="email.nospam" select="'1'"/> -->

</xsl:stylesheet>
