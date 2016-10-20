--[[
 xslt.lua
 Ryu, Gwang (http://occamsrazr.net/)
 이 파일의 끝에 저작권 및 사용권(MIT License) 공지가 있습니다.
]]

require"common.params"
function usage()
	print[[
파일에 담긴 매개변수들을 이용해서 xsltproc를 실행합니다.

사용법: runlua xslt [-c 설정파일] xsl_파일 xml_파일

설정파일은 다음 항목들이 한 줄에 하나씩 있는 텍스트 파일이어야 
합니다.

매개변수이름 = 값
]]
end

if #arg < 2 then
	usage()
	return
end

have_config = false
for i, str in ipairs(arg) do
	if str == '-c' then
		config_file = arg[i+1]
		xslt_file = arg[i+2]
		xml_file = arg[i+3]
		have_config = true
		break
	end
end

if(not have_config) then
	xslt_file = arg[1]
	xml_file = arg[2]
end


if not xslt_file or not xml_file then
	usage()
	return
end

params_str = ''
if(config_file) then
	config_file = string.gsub(config_file, "^{(.-)}$", 
		os.getenv('DOCBOOK_HOME') .. '\\conf\\%1')
	params_str = Params.get_params(config_file) 
end

if params_str == nil then
	print(config_file .. " 파일을 열 수 없습니다.")
	return
end

xslt_file = string.gsub(xslt_file, "^{(.-)}$", '%%DOCBOOK_HOME%%\\stylesheet\\%1')

if string.lower(string.sub(xslt_file, -4, -1)) ~= ".xsl" then
	xslt_file = xslt_file .. ".xsl"
end

command_str = "xsltproc" .. params_str .. " " .. xslt_file .. " " .. xml_file
--print(command_str, "\n")

os.execute(command_str)


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
