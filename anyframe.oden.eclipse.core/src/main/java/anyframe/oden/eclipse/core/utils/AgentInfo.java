package anyframe.oden.eclipse.core.utils;

public class AgentInfo {
	private String nickname;
	private String url;
	private String port;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	public AgentInfo(){
		
	}
	
	public AgentInfo(String nickName, String url, String port){
		this.nickname = nickName;
		this.url = url;
		this.port = port;
	}

}
