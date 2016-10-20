@echo off
REM xalan 과 xerces 를 쓰기위한 설정
set JAXP_XERCES=-Djavax.xml.parsers.SAXParser=org.apache.xerces.jaxp.SAXParserImpl
set JAXP_XERCES=%JAXP_XERCES% -Djavax.xml.parsers.DocumentBuilder=org.apache.xerces.jaxp.DocumentBuilderImpl
set JAXP_XERCES=%JAXP_XERCES% -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl
set JAXP_XERCES=%JAXP_XERCES% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
set JAXP_XALAN=-Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl
set JAXP_XALAN=%JAXP_XALAN% -Djavax.xml.xpath.XPathFactory=org.apache.xpath.jaxp.XPathFactoryImpl

REM 카탈로그 기능을 위한 설정
set XALAN_CATALOG=-ENTITYRESOLVER  org.apache.xml.resolver.tools.CatalogResolver
set XALAN_CATALOG=%XALAN_CATALOG% -URIRESOLVER org.apache.xml.resolver.tools.CatalogResolver

set LOCAL_FOP_HOME=%DOCBOOK_HOME%\software\fop\
set LIBDIR=%LOCAL_FOP_HOME%lib

set LOCAL_CP=%LIBDIR%\xml-apis-1.3.02.jar
set LOCAL_CP=%LOCAL_CP%;%LIBDIR%\xercesImpl-2.7.1.jar
set LOCAL_CP=%LOCAL_CP%;%LIBDIR%\xalan-2.7.0.jar
set LOCAL_CP=%LOCAL_CP%;%LIBDIR%\serializer-2.7.0.jar
set LOCAL_CP=%LOCAL_CP%;%LIBDIR%\resolver.jar
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\conf
set LOCAL_CP=%LOCAL_CP%;%DOCBOOK_HOME%\stylesheet\docbook-xsl\extensions\xalan27.jar

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
java -Djava.endorsed.dirs=%DOCBOOK%\software\fop\lib %JAXP_XERCES% %JAXP_XALAN% -cp %LOCAL_CP% org.apache.xalan.xslt.Process %XALAN_CATALOG% %CMD_LINE_ARGS%
