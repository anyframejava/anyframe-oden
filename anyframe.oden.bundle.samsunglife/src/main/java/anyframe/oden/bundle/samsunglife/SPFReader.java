package anyframe.oden.bundle.samsunglife;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import anyframe.oden.bundle.common.OdenException;
/**
 * 
 * Read and parse request form file to use conveniently.
 * 
 * @author joon1k
 *
 */
public class SPFReader {
	
	public static SPFFile read(File f) throws OdenException{
		SPFFile result = new SPFFile();
		SAXBuilder builder = new SAXBuilder();
		try{
			Document doc = builder.build(f);
			
			Element root = doc.getRootElement();
			Element dfl = root.getChild("deployFileList");
			if(dfl != null)
				for(Object deployFile : dfl.getChildren()){
					result.add(parseDeployFile((Element)deployFile));
				}
		}catch(Exception e){
			throw new OdenException(e);
		}
		return result;
	}
	
	private static SPFDeployFile parseDeployFile(Element df) {
		List<SPFTargetGroup> list = new ArrayList<SPFTargetGroup>();
		Element tsl = df.getChild("targetServerList");
		if(tsl != null)
			for(Object ts : tsl.getChildren()){
				list.add(parseTargetServer((Element)ts));
			}
		return new SPFDeployFile(
				nonEmptyText(df, "resrcId"),
				nonEmptyText(df, "reqGb"), 
				nonEmptyText(df, "reqFileNm"), 
				df.getChildText("reqFilePath"), 
				nonEmptyText(df, "reqFileAbsolutePath"), 
				list,
				df.getChildText("chgApplyId"), 
				df.getChildText("chgApplyNm"), 
				df.getChildText("chgApplyResrcTpCd"), 
				df.getChildText("chgApplier"));
	}
	
	private static SPFTargetGroup parseTargetServer(Element ts) {
		List<SPFTarget> list = new ArrayList<SPFTarget>();
		Element lil = ts.getChild("locationInfoList");
		if(lil != null)
			for(Object o : lil.getChildren()){
				Element li = (Element)o;
				list.add(new SPFTarget(
						nonEmptyText(li, "serverIP"),
						nonEmptyText(li, "absolutePath")));
			}
		return new SPFTargetGroup(
				ts.getChildText("targetServerOperGb"),
				ts.getChildText("targetServerGrp"),
				list);
	}
	
	private static String nonEmptyText(Element e, String tag){
		String s = e.getChildText(tag);
		if(s == null || s.length() == 0)
			throw new RuntimeException("Couldn't find any value for " + tag);
		return s;
	}
}
