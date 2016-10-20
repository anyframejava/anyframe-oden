--[[
 foreach.lua
 Ryu, Gwang (http://occamsrazr.net/)
 이 파일의 끝에 저작권 및 사용권(MIT License) 공지가 있습니다.
]]

require"common.lfsutils"

function usage()
	print[[특정 디렉터리에 있는 특정한 형태의 파일들 전체에 동일한 명령을
수행하는 데 사용하는 유틸리티입니다.
	
사용법: runlua foreach [-n|--nosub] "파일_패턴" 디렉터리 "명령"

-n
--nosub: 이 옵션을 지정하면 주어진 디렉터리의 하위 디렉터리들을 재귀적으로 
   처리하지 않습니다.(즉 주어진 디렉터리의 파일들만 처리합니다.)

파일_패턴: 명령을 적용할 파일들의 패턴. 예를 들면 "*.xml"
  (반드시 큰 따옴표로 감싸야 합니다.)

디렉터리: 명령을 적용할 파일들이 있는 디렉터리. 
  주: 이 디렉터리의 모든 하위 디렉터리들까지 재귀적으로 처리됩니다.

명령: 패턴에 해당하는 파일에 적용할 명령. 반드시 큰따옴표로 감싸야 합니다.
   다음과 같은 매크로들을 사용할 수 있습니다.
   %i : 일련 번호. 첫 번째 파일은 1, 이후로 파일마다 1씩 증가.
   %F : 파일 확장자를 포함한 파일 이름.
   %f : 파일 이름만.
   %e : 파일 확장자.
   %p : 파일이 있는 경로만.
   %P : 파일 경로, 파일 이름, 파일 확장자를 포함한 전체 경로이름

   전체 경로 이름이 c:\docs\test.xml 이라 할 때
   %F는 test.xml, %f는 test, %e는 xml, %p는 c:\docs, %P는 c:\docs\test.xml

예: runlua foreach "*.xml" c:\docs "docbook2pdf %P %p\%f.pdf"
]]
	os.exit()
end

function get_options()
	if #arg < 3 then usage() end
	for i, v in ipairs(arg) do
		if v == '-n' or v == '--nosub' then
			nosub = true
			table.remove(arg, i)
		end
	end
	return arg[1], arg[2], arg[3], nosub
end

pattern, dir, command, nosub = get_options()

i = 1

for path, file in lfsx.files(dir, pattern, not nosub) do

	cmd = string.gsub(command, "%%F", file)
	cmd = string.gsub(cmd, "%%f", lfsx.get_filename(file))	
	cmd = string.gsub(cmd, "%%e", lfsx.get_extension(file))
	cmd = string.gsub(cmd, "%%P", path .. '\\' .. file)		
	cmd = string.gsub(cmd, "%%p", path)
	cmd = string.gsub(cmd, "%%i", i)
	i = i+1
 	os.execute(cmd)
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
