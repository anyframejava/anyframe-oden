@echo off
REM saxon 과 xerces 를 쓰기위한 설정
set JAXP_XERCES=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
set JAXP_XERCES=%JAXP_XERCES% -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl

set SAXON_CATALOG=-x org.apache.xml.resolver.tools.ResolvingXMLReader
set SAXON_CATALOG=%SAXON_CATALOG% -y org.apache.xml.resolver.tools.ResolvingXMLReader
set SAXON_CATALOG=%SAXON_CATALOG% -r org.apache.xml.resolver.tools.CatalogResolver

set LOCAL_FOP_HOME=%DOCBOOK_HOME%\software\fop\
set LIBDIR=%LOCAL_FOP_HOME%lib

set LOCAL_CP=%LIBDIR%\xercesImpl-2.7.1.jar
set LOCAL_CP=%LOCAL_CP%;%LIBDIR%\resolver.jar
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\software\saxon\saxon.jar
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\stylesheet\docbook-xsl\extensions\saxon65.jar
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\software\isogen_i18n\i18n_support.jar
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\conf

REM software/fop/fop.bat에서 가져왔음. %*를 지원하지 않는 Win9x를 위한 것임
set CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs

rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.
:doneStart
java %JAXP_XERCES% -Dcom.innodata.i18n.home=%DOCBOOK_HOME%\software\isogen_i18n -cp %LOCAL_CP% com.icl.saxon.StyleSheet %SAXON_CATALOG% %CMD_LINE_ARGS%
