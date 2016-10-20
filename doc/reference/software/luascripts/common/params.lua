--[[
 params.lua - dbkpack의 몇몇 유틸리티들이 사용하는 .conf 파일을
 파싱하는 함수들 모음.
 Ryu, Gwang (http://occamsrazr.net/)
 이 파일의 끝에 저작권 및 사용권(MIT License) 공지가 있습니다.
]]

Params = {}
function Params.make_param_str(k, v, param_tpl, stringparam_tpl)
	local result = ''
	if type(tonumber(v))=="number" then
		result = param_tpl:gsub('$name', k)
		result = result:gsub('$value', v)
	else
		v = string.gsub(v, "(.-)%s*$", "%1")
		result = stringparam_tpl:gsub('$name', k)
		result = result:gsub('$value', v)
			:gsub('{DBK_BASE_URL}', os.getenv('DBK_BASE_URL'))
			:gsub('{DOCBOOK_HOME}', os.getenv('DOCBOOK_HOME'))
	end
	return result
end

function Params.get_params(filename, param_tpl, stringparam_tpl, additional_params)
	param_tpl = param_tpl or "--param $name $value"
	stringparam_tpl = stringparam_tpl or '--stringparam $name "$value"'
	additional_params = additional_params or {}

	local result = ''
	local k, v
	
	f = io.open(filename, "r")
	if not f then return nil end
	
	for line in f:lines() do
		if string.sub(line, 1, 1) ~= '#' then -- #는 주석
			_, _, k, v = string.find(line, "([.%w]+)%s*=%s*(.+)")
			if k and not additional_params[k] then
				additional_params[k] = v
			end
		end
	end
	f:close()

	for k, v in pairs(additional_params) do
		result = result .. ' ' .. 
			Params.make_param_str(k, v, param_tpl, stringparam_tpl)
	end

	if result ~= '' then
		return result
	else
		return nil
	end
end

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
