package anyframe.oden.bundle.samsunglife;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * Information about target server group.
 * 
 * @author joon1k
 *
 */
public class SPFTargetGroup {
	private String operGb;
	private String name;
	private List<SPFTarget> targets = new ArrayList<SPFTarget>();
		
	public SPFTargetGroup(String operGb, String name, List<SPFTarget> targets) {
		super();
		this.operGb = operGb;
		this.name = name;
		this.targets = targets;
	}
	public String getOperGb() {
		return operGb;
	}
	public String getName() {
		return name;
	}
	public List<SPFTarget> getTargets() {
		return targets;
	}
	public void add(SPFTarget target) {
		targets.add(target);
	}
		
}
