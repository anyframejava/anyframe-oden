if #arg < 1 then
	print[[
�ں� ������ ǥ�� ��� ��ũ��Ʈ ���͸�(docbook\software\luascripts)�� �ִ�
��� ��ũ��Ʈ ������ �����մϴ�.

����: runlua ��ũ��Ʈ�̸� [�ɼǵ�]

��ũ��Ʈ �̸��� .lua�� ���̸� �� �˴ϴ�.]]
	os.exit()
end
script = arg[1]
for i = 1, #arg do
	arg[i-1] = arg[i]
end
arg[#arg] = nil

require(arg[0])
