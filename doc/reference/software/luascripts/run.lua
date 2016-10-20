if #arg < 1 then
	print[[
닥북 모음집 표준 루아 스크립트 디렉터리(docbook\software\luascripts)에 있는
루아 스크립트 파일을 실행합니다.

사용법: runlua 스크립트이름 [옵션들]

스크립트 이름에 .lua를 붙이면 안 됩니다.]]
	os.exit()
end
script = arg[1]
for i = 1, #arg do
	arg[i-1] = arg[i]
end
arg[#arg] = nil

require(arg[0])
