package anyframe.oden.bundle.samsunglife;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * Control files about samsunglife request form file.
 * 
 * @author joon1k
 *
 */
public class SPFFile {
	private List<SPFDeployFile> deployFiles = new ArrayList<SPFDeployFile>();

	public SPFFile() {
	}

	public void add(SPFDeployFile f){
		deployFiles.add(f);
	}

	public List<SPFDeployFile> deployFiles(){
		return deployFiles;
	}
}
