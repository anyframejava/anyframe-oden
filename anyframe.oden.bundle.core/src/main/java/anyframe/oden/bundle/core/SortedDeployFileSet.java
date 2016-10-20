package anyframe.oden.bundle.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class SortedDeployFileSet extends TreeSet<DeployFile>{
	private static final long serialVersionUID = 4055187636013787208L;

	public SortedDeployFileSet(Collection<DeployFile> s){
		super(new DeployFileComparator());
		addAll(s);		
	}
}

class DeployFileComparator implements Comparator<DeployFile>, Serializable{
	private static final long serialVersionUID = 7745117582884274026L;

	public int compare(DeployFile d1, DeployFile d2) {
		int ret = d1.getRepo().toString().compareTo(d2.getRepo().toString());
		if(ret != 0) return ret;
		
		ret = d1.getPath().compareTo(d2.getPath());
		if(ret != 0) return ret;
		
		return d1.getAgent().agentName().compareTo(d2.getAgent().agentName());
	}
}
