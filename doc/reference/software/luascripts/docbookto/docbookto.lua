--[[
 docbookto - 범용 닥북 변환 도구
 Ryu, Gwang (http://occamsrazr.net/)
 이 파일의 끝에 저작권 및 사용권(MIT License) 공지가 있습니다.
]]

require"common.lfsutils"
require"common.params"

--_DEBUG_ = true

--------------------
-- 함수들
--------------------
function usage()
	print[[DocBook XML 파일을 다른 형식의 파일로 변환하는 명령입니다.
	
사용법:

docbookto html [옵션] <입력파일.xml>
      여러 개의 HTML 파일들을 생성합니다.

docbookto onehtml [옵션] <입력파일.xml> [출력파일.html]
      하나의 HTML 파일을 출력합니다. [출력파일]에 출력 HTML 파일 이름을
      지정하면 그 파일에 저장됩니다. 지정하지 않으면 표준 출력(콘솔)로
      출력됩니다.

docbookto pdf [옵션] <입력파일.xml 또는 입력파일.fo> [출력파일.pdf]
       PDF 파일을 생성합니다. [출력파일.pdf]을 생략하며 <입력파일.pdf>가
       쓰입니다.

docbookto fo [옵션] <입력파일.xml> [출력파일.fo]
       XSL-FO 파일을 생성합니다. [출력파일.fo]로 출력 FO 파일 이름을
       지정하면 그 파일에 저장됩니다. 지정하지 않으면 표준 출력(콘솔)로
       출력됩니다.

옵션:
-c <설정 파일>
    지정된 파일에 담긴 XSLT 매개변수 설정들이 XSLT 처리기에 적용됩니다.
    설정 파일은
        매개변수이름 = 값
    형태의 항목들이 한 줄에 하나씩 존재하는 파일이어야 합니다.
    이 옵션을 지정하지 않으면 기본 설정(%DOCBOO_HOME%\conf\에 있는 *.conf 파일
    들 중 하나)가 적용됩니다.
-s <XSL 스타일시트 파일>
    기본 XSL 스타일시트 파일 대신 지정된 파일을 사용합니다. {XSL파일.xsl} 
    형태로 지정하면 %DOCBOO_HOME%\stylesheet\XSL파일.xsl 로 간주됩니다.
-p <XSLT 매개변수> <값>
    XSLT 처리기에 전달할 추가적인 XSLT 매개변수와 값을 지정합니다. -c 옵션으로
    지정한 설정 파일에 같은 이름의 매개변수가 있는 경우 설정 파일의 것이 무시됩
    니다. 여러 개의 매개변수들을 지정하는 경우 매번 -p를 지정해야 합니다.
--xalan
    XSLT 처리기로 기본 xsltproc 대신 Xalan을 사용합니다.
--saxon
    XSLT 처리기로 기본 xsltproc 대신 Saxon을 사용합니다.
--debug
    변환 명령을 실제로 실행하지는 않고 명령문만 출력합니다.
]]
	os.exit()
end

function get_args(args)
	local conf_file = nil
	local proc_type = 'xsltproc'
	local xsl_file = nil
	local skip = "__DBK_DOCBOOK_TO_LUA_SKIP__"
	local result = {}
	local params = {}
	for i=2, #args do
		v = args[i]
		if v == '-c' then
			if not args[i+1] then
				die('-c 다음에 설정 파일 이름을 지정해야합니다.')
			end
			conf_file = args[i+1]
			args[i+1] = skip
		elseif v == '-s' then
			if not args[i+1] then
				die('-s 다음에 스타일시트 파일 이름을 지정해야합니다.')
			end
			xsl_file = args[i+1]
			args[i+1] = skip
		elseif v == '-p' then
			if not(args[i+1] and args[i+2]) then
				die('-p 다음에 DocBook XSLT 매개변수 이름과 값을 지정해야합니다.')
			end
			params[args[i+1]] = args[i+2]
			args[i+1] = skip
			args[i+2] = skip
		elseif v == '--xalan' then
			proc_type = 'xalan'
		elseif v == '--saxon' then
			proc_type = 'saxon'
		elseif v == '--debug' then
			_DEBUG_ = true
		else
			if v ~= skip then table.insert(result, v) end
		end
	end

	return args[1], result[1], result[2], conf_file, proc_type, xsl_file, params
end

function run_command(cmd)
	if _DEBUG_ then
		print(cmd)
	else
		os.execute(cmd)
	end
end

function make_pdf(fofile, outfile)
	fofile = fofile or '_temp.fo'
	print('REM ' .. outfile .. '를 생성합니다.\n')
	if _DEBUG_ then
		print('fo2pdf ' .. fofile .. ' ' ..  outfile)
	else
		os.execute('fo2pdf ' .. fofile .. ' ' ..  outfile)
		if fofile == '_temp.fo' then
			os.remove('_temp.fo')
		end
	end
end

function check_files(...)
	local all_ok = true
	for _, f in ipairs(arg) do
		if not lfsx.is_file(f) then
			print(f .. ' 파일이 없습니다.')
			all_ok = false
		end
	end
	return all_ok
end

function die(msg)
	print(msg)
	os.exit()
end

---------------------
-- 메인
---------------------
if #arg < 2 then usage() end

out_type, infile, outfile, conf_file, proc_type, xsl_file, additional_params = get_args(arg)

if out_type == 'pdf' then
	if outfile == nil then
		outfile = lfsx.get_filename(infile) .. ".pdf"
	end
	if lfsx.get_extension(infile) == 'fo' then
		if not lfsx.is_file(infile) then
			die(infile .. '파일이 없습니다.\n작업을 중단합니다.')
		end
		make_pdf(infile, outfile)
		os.exit()
	end
end

dbk_home = os.getenv('DOCBOOK_HOME')

stylesheets = {
	html = 'dbk-html-chunk.xsl',
	onehtml = 'dbk-html.xsl',
	htmlhelp = 'dbk-htmlhelp.xsl',
	fo = 'dbk-fo.xsl',
	pdf = 'dbk-fo.xsl'
}

commands  = {
	xsltproc = {
		all = 'xsltproc --xinclude $PARAMS $OUTFILE $XSLFILE $INFILE', 
		param = '--param $name $value',
		stringparam = '--stringparam $name "$value"',
		input = '$file',
		output = '-o $file',
		xsl = '$file'
	},
	xalan = {
		all = 'xalan $PARAMS $XSLFILE $INFILE $OUTFILE',
		param = '-PARAM $name $value',
		stringparam = '-PARAM $name "$value"',
		input = '-IN $file',
		output = '-OUT $file',
		xsl = '-XSL $file'
	},
	saxon = {
		all = 'saxon $OUTFILE $INFILE $XSLFILE $PARAMS',
		param = '"$name=$value"',
		stringparam = '"$name=$value"',
		input = '$file',
		output = '-o $file',
		xsl = '$file'
	}
}

if not stylesheets[out_type] then
	die(out_type .. " 형식은 지원하지 않습니다.")
end

xsl_file = xsl_file or (dbk_home .. '\\stylesheet\\' .. stylesheets[out_type])
xsl_file = string.gsub(xsl_file, "^{(.-)}$", dbk_home .. '\\stylesheet\\%1')

if not conf_file then
	if out_type == 'fo' then
		conf_file = '{dbk-pdf.conf}'
	else
		conf_file = string.format('{dbk-%s.conf}', out_type)
	end
end

conf_file = string.gsub(conf_file, "^{(.-)}$", dbk_home .. '\\conf\\%1')

if not check_files(infile, conf_file, xsl_file) then
	die('작업을 중단합니다.')
end

cmd = commands[proc_type]
cmd_string = cmd.all

params_str = Params.get_params(
		conf_file, cmd.param, cmd.stringparam, additional_params)
	or ''

cmd_string = cmd_string:gsub('$PARAMS', params_str)

if outfile then
	if out_type == 'pdf' then
		pdffile = outfile
		outfile = lfsx.get_filename(infile) .. ".fo"

	end
	cmd_string = cmd_string:gsub('$OUTFILE', cmd.output:gsub('$file', outfile))
else
	cmd_string = cmd_string:gsub('$OUTFILE', '')
end

cmd_string = cmd_string:gsub('$XSLFILE', cmd.xsl:gsub('$file', xsl_file))
cmd_string = cmd_string:gsub('$INFILE', cmd.input:gsub('$file', infile))

run_command(cmd_string)
if out_type == 'pdf' then make_pdf(outfile, pdffile) end

--[[
Copyright (C) 2008 Ryu, Gwang.  All rights reserved.

Permission is hereby granted, free of charge, to any person 
obtaining a copy of this software and associated documentation 
files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, 
and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
]]
