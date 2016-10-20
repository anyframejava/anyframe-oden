require"lfs"
if #arg < 1 then
	print([[
wget을 이용해서 위키 페이지의 XML 파일과 이미지 파일들을
가져옵니다. 이미지 파일들은 files 하위 디렉터리에 저장됩니다.

사용법: runlua wgetdbwiki 페이지이름 [위키 기준 URL]

[위키 기준 URL]은 페이지 이름을 제외한 위키 URL.
생략 시 http://docbook.or.kr/wiki/index.php/

예: runlua wgetdbwiki DocBookExample
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
