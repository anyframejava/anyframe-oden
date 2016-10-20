@echo off

xsltproc -stringparam admon.graphics 1 -stringparam admon.graphics.path %DBK_BASE_URL%/stylesheet/docbook-xsl/images/ -stringparam callout.graphics.path %DBK_BASE_URL%/stylesheet/docbook-xsl/images/callouts/ --xinclude -o %2 %DOCBOOK_HOME%\stylesheet\dbk-fo.xsl %1 
