goto --dbk_configure--
= goto

require"luaiconv"

function current_dir()
	local output = io.popen("cd")
	if output then
		return output:read()
	else
		return false
	end
end

function make_file_url(dir)
	local converted = 'file:///' .. dir
	converted = converted:gsub('\\', '/')
	local cd = luaiconv.new("utf-8//TRANSLIT", "euc-kr")
	converted = cd:iconv(converted)

	local encoded = ''

	for i =1, converted:len() do
		byte = converted:byte(i)
		if byte > 127 then
			encoded = encoded .. string.format('%%%%%X', byte)
		else
			encoded = encoded .. converted:sub(i, i)
		end
	end
	return encoded
end

function save_file(filename, text)
	local outfile = assert(io.open(filename, "w"))
	outfile:write(text)
	assert(outfile:close())
end

function text_replace(patterns, template)
	local result = template
	for search, replace in pairs(patterns) do
		result = result:gsub(search, replace)
	end
	return result
end

curdir = current_dir()
if (not curdir) then
	print("심각한 오류입니다. http://docbook.or.kr/의 자유 게시판에 보고해 주세요")
	os.exit()
end

curdir_url = make_file_url(curdir)

print "dbkshell.bat을 생성합니다."

--------------------------------------------------------------------
--------------------------------------------------------------------
--  dbkshell.bat                
--------------------------------------------------------------------
--------------------------------------------------------------------

dbkshell_tpl = [[
@echo off
set DOCBOOK_HOME={DOCBOOK_HOME}
set DBK_BASE_URL={DBK_BASE_URL}
set PATH=%PATH%;%DOCBOOK_HOME%\bin
set XML_CATALOG_FILES=%DOCBOOK_HOME%\conf\catalog.xml
set LUA_PATH=?;?.lua;%DOCBOOK_HOME%\bin\?.lua;%DOCBOOK_HOME%\software\luascripts\?.lua;%DOCBOOK_HOME%\software\luascripts\?\?.lua
set LUA_CPATH=?;?.dll;%DOCBOOK_HOME%\bin\?.dll

echo ----- 닥북 작업 환경 -----
echo HTML 생성:
echo docbook2html 입력파일.xml
echo PDF 생성:
echo docbook2pdf 입력파일.xml 출력.pdf
echo --------------------------

if "%OS%"=="Windows_NT" (
	cmd.exe
) else (
	command.com
)
]]

dbkshell = text_replace({
	['{DOCBOOK_HOME}'] = curdir,
	['{DBK_BASE_URL}'] = curdir_url:gsub('%%', '%%%%')}, dbkshell_tpl)

save_file('dbkshell.bat', dbkshell)

print "conf/fop-config.xml을 생성합니다."

--------------------------------------------------------------------
--------------------------------------------------------------------
--  conf/fop-config.xml
--------------------------------------------------------------------
--------------------------------------------------------------------

fop_conf_tpl = [[
<?xml version="1.0"?>
<!--
This is an example configuration file for FOP.
This file contains the same settings as the default values
and will have no effect if used unchanged.

Relative config url's will be resolved relative to
the location of this file.
-->
<!-- NOTE: This is the version of the configuration -->
<fop version="1.0">
<!-- Base URL for resolving relative URLs -->
  <base>{DBK_BASE_URL}</base>
<!-- Source resolution in dpi (dots/pixels per inch) for determining
 the size of pixels in SVG and bitmap images, default: 72dpi -->
  <source-resolution>72</source-resolution>
<!-- Target resolution in dpi (dots/pixels per inch) for specifying
 the target resolution for generated bitmaps, default: 72dpi -->
  <target-resolution>72</target-resolution>
<!-- Default page-height and page-width, in case
 value is specified as auto -->
  <default-page-settings height="11in" width="8.26in"/>
<!-- Information for specific renderers -->
<!-- Uses renderer mime type for renderers -->
  <renderers>
    <renderer mime="application/pdf">
      <filterList>
<!-- provides compression using zlib flate (default is on) -->
        <value>flate</value>
<!-- encodes binary data into printable ascii characters (default off)
This provides about a 4:5 expansion of data size -->
<!-- <value>ascii-85</value> -->
<!-- encodes binary data with hex representation (default off)
This filter is not recommended as it doubles the data size -->
<!-- <value>ascii-hex</value> -->
      </filterList>
      <fonts>
<!-- embedded fonts -->
<!--
        This information must exactly match the font specified
        in the fo file. Otherwise it will use a default font.

        For example,
        <fo:inline font-family="Arial" font-weight="bold" font-style="normal">
            Arial-normal-normal font
        </fo:inline>
        for the font triplet specified by:
        <font-triplet name="Arial" style="normal" weight="bold"/>

        If you do not want to embed the font in the pdf document
        then do not include the "embed-url" attribute.
        The font will be needed where the document is viewed
        for it to be displayed properly.

        possible styles: normal | italic | oblique | backslant
        possible weights: normal | bold | 100 | 200 | 300 | 400
                          | 500 | 600 | 700 | 800 | 900
        (normal = 400, bold = 700)
-->
<!--
        <font metrics-url="arial.xml" kerning="yes" embed-url="arial.ttf">
          <font-triplet name="Arial" style="normal" weight="normal"/>
          <font-triplet name="ArialMT" style="normal" weight="normal"/>
        </font>
        <font metrics-url="arialb.xml" kerning="yes" embed-url="arialb.ttf">
          <font-triplet name="Arial" style="normal" weight="bold"/>
          <font-triplet name="ArialMT" style="normal" weight="bold"/>
        </font>
-->
<!-- +++ font configuration for [UnBatang] +++ -->
        <font kerning="yes" metrics-url="fonts/UnBatang.xml" embed-url="fonts/UnBatang.ttf">
          <font-triplet style="normal" weight="normal" name="serif"/>
          <font-triplet style="italic" weight="normal" name="serif"/>
          <font-triplet style="normal" weight="normal" name="UnBatang"/>
          <font-triplet style="italic" weight="normal" name="UnBatang"/>
        </font>
<!-- +++ font configuration for [UnBatangBold] +++ -->
        <font kerning="yes" metrics-url="fonts/UnBatangBold.xml" embed-url="fonts/UnBatangBold.ttf">
          <font-triplet style="normal" weight="bold" name="serif"/>
          <font-triplet style="normal" weight="bold" name="UnBatang"/>
          <font-triplet style="normal" weight="bold" name="UnBatangBold"/>
        </font>
<!-- +++ font configuration for [UnDotum] +++ -->
        <font kerning="yes" metrics-url="fonts/UnDotum.xml" embed-url="fonts/UnDotum.ttf">
          <font-triplet style="normal" weight="normal" name="sans-serif"/>
          <font-triplet style="italic" weight="normal" name="sans-serif"/>
          <font-triplet style="normal" weight="normal" name="UnDotum"/>
          <font-triplet style="italic" weight="normal" name="UnDotum"/>
        </font>
<!-- +++ font configuration for [UnDotumBold] +++ -->
        <font kerning="yes" metrics-url="fonts/UnDotumBold.xml" embed-url="fonts/UnDotumBold.ttf">
          <font-triplet style="normal" weight="bold" name="sans-serif"/>
          <font-triplet style="normal" weight="bold" name="UnDotum"/>
          <font-triplet style="normal" weight="bold" name="UnDotumBold"/>
        </font>
<!-- +++ font configuration for [UnGraphic] +++ -->
        <font kerning="yes" metrics-url="fonts/UnGraphic.xml" embed-url="fonts/UnGraphic.ttf">
          <font-triplet style="normal" weight="normal" name="UnGraphic"/>
          <font-triplet style="italic" weight="normal" name="UnGraphic"/>
        </font>
<!-- +++ font configuration for [UnGraphicBold] +++ -->
        <font kerning="yes" metrics-url="fonts/UnGraphicBold.xml" embed-url="fonts/UnGraphicBold.ttf">
          <font-triplet style="normal" weight="bold" name="UnGraphic"/>
          <font-triplet style="normal" weight="bold" name="UnGraphicBold"/>
        </font>
<!-- +++ font configuration for [UnGungseo] +++ -->
        <font kerning="yes" metrics-url="fonts/UnGungseo.xml" embed-url="fonts/UnGungseo.ttf">
          <font-triplet style="normal" weight="normal" name="UnGungseo"/>
          <font-triplet style="italic" weight="normal" name="UnGungseo"/>
          <font-triplet style="normal" weight="bold" name="UnGungseo"/>
        </font>
<!-- +++ font configuration for [UnPilgi] +++ -->
        <font kerning="yes" metrics-url="fonts/UnPilgi.xml" embed-url="fonts/UnPilgi.ttf">
          <font-triplet style="normal" weight="normal" name="UnPilgi"/>
          <font-triplet style="italic" weight="normal" name="UnPilgi"/>
        </font>
<!-- +++ font configuration for [UnPilgiBold] +++ -->
        <font kerning="yes" metrics-url="fonts/UnPilgiBold.xml" embed-url="fonts/UnPilgiBold.ttf">
          <font-triplet style="normal" weight="bold" name="UnPilgi"/>
          <font-triplet style="normal" weight="bold" name="UnPilgiBold"/>
        </font>
<!-- +++ font configuration for [UnTaza] +++ -->
        <font kerning="yes" metrics-url="fonts/UnTaza.xml" embed-url="fonts/UnTaza.ttf">
          <font-triplet style="normal" weight="normal" name="monospace"/>
          <font-triplet style="italic" weight="normal" name="monospace"/>
          <font-triplet style="normal" weight="bold" name="monospace"/>
          <font-triplet style="normal" weight="normal" name="UnTaza"/>
          <font-triplet style="italic" weight="normal" name="UnTaza"/>
          <font-triplet style="normal" weight="bold" name="UnTaza"/>
        </font>
<!-- +++ font configuration for [hline] +++ -->
        <font kerning="yes" metrics-url="fonts/hline.xml" embed-url="fonts/hline.ttf">
          <font-triplet style="normal" weight="normal" name="helvetica"/>
          <font-triplet style="italic" weight="normal" name="helvetica"/>
          <font-triplet style="normal" weight="bold" name="helvetica"/>
          <font-triplet style="normal" weight="normal" name="hline"/>
          <font-triplet style="italic" weight="normal" name="hline"/>
          <font-triplet style="normal" weight="bold" name="hline"/>
        </font>
      </fonts>
<!-- This option lets you specify additional options on an XML handler -->
<!--
      <xml-handler namespace="http://www.w3.org/2000/svg">
        <stroke-text>false</stroke-text>
      </xml-handler>
-->
    </renderer>
    <renderer mime="application/postscript">
<!-- This option forces the PS renderer to rotate landscape pages -->
<!--
      <auto-rotate-landscape>true</auto-rotate-landscape>-->
<!-- This option lets you specify additional options on an XML handler -->
<!--
      <xml-handler namespace="http://www.w3.org/2000/svg">
        <stroke-text>false</stroke-text>
      </xml-handler>
      -->
    </renderer>
    <renderer mime="application/vnd.hp-PCL"/>
<!-- MIF does not have a renderer
    <renderer mime="application/vnd.mif">
    </renderer>
    -->
    <renderer mime="image/svg+xml">
      <format type="paginated"/>
      <link value="true"/>
      <strokeText value="false"/>
    </renderer>
    <renderer mime="application/awt"/>
    <renderer mime="text/xml"/>
<!-- RTF does not have a renderer
    <renderer mime="text/rtf">
    </renderer>
    -->
    <renderer mime="text/plain">
      <pageSize columns="80"/>
    </renderer>
  </renderers>
</fop>
]]

save_file('conf/fop-config.xml', fop_conf_tpl:gsub('{DBK_BASE_URL}', curdir_url))
print"설정이 끝났습니다. dbkshell.bat을 실행하세요."
--[[
:--dbk_configure--
@echo off
command /C
@bin\lua5.1.exe configure.bat
pause
::]]
