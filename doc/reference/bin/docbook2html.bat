@echo off

xsltproc --stringparam admon.graphics 1 --stringparam admon.graphics.path http://docbook.sourceforge.net/release/xsl/current/images/ --stringparam callout.graphics.path http://docbook.sourceforge.net/release/xsl/current/images/callouts/ --stringparam callouts.extension 1 --param use.extensions 1 --xinclude %DOCBOOK_HOME%\stylesheet\dbk-html-chunk.xsl %1
