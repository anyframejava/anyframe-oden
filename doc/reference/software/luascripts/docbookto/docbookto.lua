--[[
 docbookto - ���� �ں� ��ȯ ����
 Ryu, Gwang (http://occamsrazr.net/)
 �� ������ ���� ���۱� �� ����(MIT License) ������ �ֽ��ϴ�.
]]

require"common.lfsutils"
require"common.params"

--_DEBUG_ = true

--------------------
-- �Լ���
--------------------
function usage()
	print[[DocBook XML ������ �ٸ� ������ ���Ϸ� ��ȯ�ϴ� ����Դϴ�.
	
����:

docbookto html [�ɼ�] <�Է�����.xml>
      ���� ���� HTML ���ϵ��� �����մϴ�.

docbookto onehtml [�ɼ�] <�Է�����.xml> [�������.html]
      �ϳ��� HTML ������ ����մϴ�. [�������]�� ��� HTML ���� �̸���
      �����ϸ� �� ���Ͽ� ����˴ϴ�. �������� ������ ǥ�� ���(�ܼ�)��
      ��µ˴ϴ�.

docbookto pdf [�ɼ�] <�Է�����.xml �Ǵ� �Է�����.fo> [�������.pdf]
       PDF ������ �����մϴ�. [�������.pdf]�� �����ϸ� <�Է�����.pdf>��
       ���Դϴ�.

docbookto fo [�ɼ�] <�Է�����.xml> [�������.fo]
       XSL-FO ������ �����մϴ�. [�������.fo]�� ��� FO ���� �̸���
       �����ϸ� �� ���Ͽ� ����˴ϴ�. �������� ������ ǥ�� ���(�ܼ�)��
       ��µ˴ϴ�.

�ɼ�:
-c <���� ����>
    ������ ���Ͽ� ��� XSLT �Ű����� �������� XSLT ó���⿡ ����˴ϴ�.
    ���� ������
        �Ű������̸� = ��
    ������ �׸���� �� �ٿ� �ϳ��� �����ϴ� �����̾�� �մϴ�.
    �� �ɼ��� �������� ������ �⺻ ����(%DOCBOO_HOME%\conf\�� �ִ� *.conf ����
    �� �� �ϳ�)�� ����˴ϴ�.
-s <XSL ��Ÿ�Ͻ�Ʈ ����>
    �⺻ XSL ��Ÿ�Ͻ�Ʈ ���� ��� ������ ������ ����մϴ�. {XSL����.xsl} 
    ���·� �����ϸ� %DOCBOO_HOME%\stylesheet\XSL����.xsl �� ���ֵ˴ϴ�.
-p <XSLT �Ű�����> <��>
    XSLT ó���⿡ ������ �߰����� XSLT �Ű������� ���� �����մϴ�. -c �ɼ�����
    ������ ���� ���Ͽ� ���� �̸��� �Ű������� �ִ� ��� ���� ������ ���� ���õ�
    �ϴ�. ���� ���� �Ű��������� �����ϴ� ��� �Ź� -p�� �����ؾ� �մϴ�.
--xalan
    XSLT ó����� �⺻ xsltproc ��� Xalan�� ����մϴ�.
--saxon
    XSLT ó����� �⺻ xsltproc ��� Saxon�� ����մϴ�.
--debug
    ��ȯ ����� ������ ���������� �ʰ� ��ɹ��� ����մϴ�.
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
				die('-c ������ ���� ���� �̸��� �����ؾ��մϴ�.')
			end
			conf_file = args[i+1]
			args[i+1] = skip
		elseif v == '-s' then
			if not args[i+1] then
				die('-s ������ ��Ÿ�Ͻ�Ʈ ���� �̸��� �����ؾ��մϴ�.')
			end
			xsl_file = args[i+1]
			args[i+1] = skip
		elseif v == '-p' then
			if not(args[i+1] and args[i+2]) then
				die('-p ������ DocBook XSLT �Ű����� �̸��� ���� �����ؾ��մϴ�.')
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
	print('REM ' .. outfile .. '�� �����մϴ�.\n')
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
			print(f .. ' ������ �����ϴ�.')
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
-- ����
---------------------
if #arg < 2 then usage() end

out_type, infile, outfile, conf_file, proc_type, xsl_file, additional_params = get_args(arg)

if out_type == 'pdf' then
	if outfile == nil then
		outfile = lfsx.get_filename(infile) .. ".pdf"
	end
	if lfsx.get_extension(infile) == 'fo' then
		if not lfsx.is_file(infile) then
			die(infile .. '������ �����ϴ�.\n�۾��� �ߴ��մϴ�.')
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
	die(out_type .. " ������ �������� �ʽ��ϴ�.")
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
	die('�۾��� �ߴ��մϴ�.')
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
