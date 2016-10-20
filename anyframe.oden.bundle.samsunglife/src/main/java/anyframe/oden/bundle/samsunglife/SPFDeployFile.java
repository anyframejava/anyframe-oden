package anyframe.oden.bundle.samsunglife;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * Data about samsunglife request form.
 * 
 * @author joon1k
 *
 */
public class SPFDeployFile {
	private String rscId;
	private String reqGb;
	private String reqFileName;
	private String reqFilePath;
	private String reqParentPath;
	private List<SPFTargetGroup> targetGroups = new ArrayList<SPFTargetGroup>();
	private String applyId;
	private String applyName;
	private String applyRscTpCd;
	private String applier;
	
	public SPFDeployFile(String rscId, String reqGb, String reqFileName, String reqFilePath,
			String reqParentPath, List<SPFTargetGroup> targetGroups, String applyId,
			String applyName, String applyRscTpCd, String applier) {
		super();
		this.rscId = rscId;
		this.reqGb = reqGb;
		this.reqFileName = reqFileName;
		this.reqFilePath = reqFilePath;
		this.reqParentPath = reqParentPath;
		this.targetGroups = targetGroups;
		this.applyId = applyId;
		this.applyName = applyName;
		this.applyRscTpCd = applyRscTpCd;
		this.applier = applier;
	}
	
	public String getRscId() {
		return rscId;
	}
	public String getReqGb() {
		return reqGb;
	}
	public String getReqFileName() {
		return reqFileName;
	}
	public String getReqFilePath() {
		return reqFilePath;
	}
	public String getReqParentPath() {
		return reqParentPath;
	}
	public List<SPFTargetGroup> getTargetGroups() {
		return targetGroups;
	}
	public void add(SPFTargetGroup targetGroup){
		targetGroups.add(targetGroup);
	}
	public String getApplyId() {
		return applyId;
	}
	public String getApplyName() {
		return applyName;
	}
	public String getApplyRscTpCd() {
		return applyRscTpCd;
	}
	public String getApplier() {
		return applier;
	}
	
}
