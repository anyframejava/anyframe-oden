--[[
 lfsutils - LuaFileSystem�� ��ƿ��Ƽ �Լ���
 Ryu, Gwang (http://occamsrazr.net/)
 �� ������ ���� ���۱� �� ����(MIT License) ������ �ֽ��ϴ�.
]]

require "lfs"  
lfsx = {}
local function get_lua_pattern(pattern)
	local lua_pattern = string.lower(pattern)
	lua_pattern = string.gsub(pattern, "%.", "%%.")
	lua_pattern = string.gsub(lua_pattern, "%*", "(.*)")
	return '^' .. lua_pattern .. '$'
end

local function build_list(path, pattern, list, recursive)
	for file in lfs.dir(path) do
		if file ~= "." and file ~=".." then
			local f = path..'\\'..file
			local attr = lfs.attributes (f)
			if recursive and attr.mode == "directory" then
				build_list(f, pattern, list, recursive)
			else
				if(string.find(string.lower(file), pattern)) then
					table.insert(list, {path, file})
				end
			end
		end
	end
end

local function enum_files(list)
	for i, v in ipairs(list) do
		coroutine.yield(v[1], v[2])
	end
end

-- �־��� ���� ������ �����ϴ� ���ϵ��� ��ο� �̸� ���� �����ϴ� �����⸦ ��ȯ
-- for ������ ����� �� ����.
-- ��: for path, file in lfsx.files("test", "*.xml") do
--	     print(path, file)
--     end
function lfsx.files(dir, file_pattern, recursive)
	if recursive == nil then recursive = true end
	local pattern = get_lua_pattern(file_pattern)
	local list = {}
	build_list(dir, pattern, list, recursive)
	return coroutine.wrap(function () enum_files(list) end)
end

-- �־��� ��ΰ� ���͸��̸� true, true�� ��ȯ
-- ���͸��� �ƴϳ� �־��� ��ΰ� �����ϸ� nil, true�� ��ȯ
-- �ƿ� �������� �ʴ� ����̸� nil, nil�� ��ȯ

function lfsx.is_dir(path)
	local fileattr = lfs.attributes(luascript_path) or {mode=nil}
	if fileattr.mode == 'directory' then return true, true end
	if mode ~= nil then return false, true end
end

-- �־��� ��ΰ� �����̸� true�� ��ȯ
-- ������ �ƴϳ� �־��� ��ΰ� �����ϸ� false, true�� ��ȯ
-- �ƿ� �������� �ʴ� ����̸� nil, nil�� ��ȯ
function lfsx.is_file(path)
	local fileattr = lfs.attributes(path) or {mode=nil}
	if fileattr.mode == 'file' then 
		return true, true 
	end
	if mode ~= nil then 
		return false, true 
	end
end

function lfsx.get_extension(path)
	ext = '';
	if not string.find(path, '%.') then return ext end
	
	for w in string.gmatch(path, '[^%.]+') do
		ext = w
	end
	ext = string.gsub(ext, '%-', '%%-')
	return ext
end

function lfsx.get_filename(path)
	local ext = lfsx.get_extension(path)
	if ext ~= '' then
		_, _, filename = string.find(path, '(.-)%.'..ext..'$')
	else
		filename = path
	end
	return filename
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
