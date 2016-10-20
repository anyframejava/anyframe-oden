<?xml version="1.0" encoding="euc-kr"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<!-- �ں� �ѱ� ��Ÿ�Ͻ�Ʈ (http://kldp.net/projects/docbook/) -->
<!-- $Id: dbk-html-chunk.xsl,v 1.4 2003/08/09 22:28:01 minskim Exp $ -->
	   
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/xhtml/chunk.xsl"/>

<xsl:import href="dbk-html-common.xsl"/>

<!-- Saxon 6 + Kimber �ε��� -->
<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/xhtml/autoidx-kimber.xsl"/>
<xsl:param name="index.method">kimber</xsl:param>

<!-- Xalan���� �����ϴ� HTML�� ���ڵ��� �߰��ϱ� ���� �ʿ� -->
<xsl:output method="xml" encoding="utf-8"/>

<xsl:param name="chunker.output.encoding" select="'utf-8'"/>

<xsl:param name="chunk.first.sections" select="'1'"/>

<!-- email.nospam�� 1�� �����ϸ� email �ּ��� '@'�� ' (at) '�� �ٲپ� ��� -->
<!-- <xsl:param name="email.nospam" select="'1'"/> -->

</xsl:stylesheet>
