package anyframe.oden.bundle.samsunglife;

import anyframe.oden.bundle.common.OdenException;
/**
 * 
 * Keep spectrum's additional information temporary.
 * 
 * @author joon1k
 *
 */
public class SpectrumComment {
	private final static String SEP = ";";
	
	private String applyId;
	private String resourceId;
	private String serverGroup;
	private String server;
	private String operCd;		// 운영 : PRD, 검증계 : TST, 개발계 : DEV 
	
	public SpectrumComment(String applyId, String resourceId, String serverGroup,
			String server, String operCd){
		this.applyId = applyId;
		this.resourceId = resourceId;
		this.serverGroup = serverGroup;
		this.server = server;
		this.operCd = operCd;
	}
	
	public SpectrumComment(String comment) throws OdenException{
		parse(comment);
	}

	private void parse(String comment) throws OdenException {
		String[] toks = comment.split(SEP);
		if(toks.length != 5)
			throw new OdenException("Fail to parse: " + comment);
		this.applyId = toks[0];
		this.resourceId = toks[1];
		this.serverGroup = toks[2];
		this.server = toks[3];
		this.operCd = toks[4];
	}
	
	public String getApplyId() {
		return applyId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getServerGroup() {
		return serverGroup;
	}

	public String getServer() {
		return server;
	}

	public String getOperCd() {
		return operCd;
	}

	@Override
	public String toString() {
		return applyId + SEP + resourceId + SEP + serverGroup + SEP +
				server + SEP + operCd;
	}
	
	public String xNull(String s){
		return s == null ? "" : s;
	}
}
