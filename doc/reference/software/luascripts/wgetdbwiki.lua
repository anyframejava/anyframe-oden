require"lfs"
if #arg < 1 then
	print([[
wget�� �̿��ؼ� ��Ű �������� XML ���ϰ� �̹��� ���ϵ���
�����ɴϴ�. �̹��� ���ϵ��� files ���� ���͸��� ����˴ϴ�.

����: runlua wgetdbwiki �������̸� [��Ű ���� URL]

[��Ű ���� URL]�� ������ �̸��� ������ ��Ű URL.
���� �� http://docbook.or.kr/wiki/index.php/

��: runlua wgetdbwiki DocBookExample
]])
	os.exit(1)
end

page_name = arg[1]

wiki_url = "http://docbook.or.kr/wiki/index.php/"
if #arg > 1 then
	wiki_url = arg[2]
end

page_url = wiki_url .. page_name

os.execute('wget -F -E -Pfiles -H -nH -nd -k -p "' .. page_url .. '"')

os.execute('wget -O "' .. page_name .. '.xml" "' .. page_url .. '?action=docbookxml&imgdir=files"')
