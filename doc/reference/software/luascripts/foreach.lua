--[[
 foreach.lua
 Ryu, Gwang (http://occamsrazr.net/)
 �� ������ ���� ���۱� �� ����(MIT License) ������ �ֽ��ϴ�.
]]

require"common.lfsutils"

function usage()
	print[[Ư�� ���͸��� �ִ� Ư���� ������ ���ϵ� ��ü�� ������ �����
�����ϴ� �� ����ϴ� ��ƿ��Ƽ�Դϴ�.
	
����: runlua foreach [-n|--nosub] "����_����" ���͸� "���"

-n
--nosub: �� �ɼ��� �����ϸ� �־��� ���͸��� ���� ���͸����� ��������� 
   ó������ �ʽ��ϴ�.(�� �־��� ���͸��� ���ϵ鸸 ó���մϴ�.)

����_����: ����� ������ ���ϵ��� ����. ���� ��� "*.xml"
  (�ݵ�� ū ����ǥ�� ���ξ� �մϴ�.)

���͸�: ����� ������ ���ϵ��� �ִ� ���͸�. 
  ��: �� ���͸��� ��� ���� ���͸������ ��������� ó���˴ϴ�.

���: ���Ͽ� �ش��ϴ� ���Ͽ� ������ ���. �ݵ�� ū����ǥ�� ���ξ� �մϴ�.
   ������ ���� ��ũ�ε��� ����� �� �ֽ��ϴ�.
   %i : �Ϸ� ��ȣ. ù ��° ������ 1, ���ķ� ���ϸ��� 1�� ����.
   %F : ���� Ȯ���ڸ� ������ ���� �̸�.
   %f : ���� �̸���.
   %e : ���� Ȯ����.
   %p : ������ �ִ� ��θ�.
   %P : ���� ���, ���� �̸�, ���� Ȯ���ڸ� ������ ��ü ����̸�

   ��ü ��� �̸��� c:\docs\test.xml �̶� �� ��
   %F�� test.xml, %f�� test, %e�� xml, %p�� c:\docs, %P�� c:\docs\test.xml

��: runlua foreach "*.xml" c:\docs "docbook2pdf %P %p\%f.pdf"
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
