package org.anyframe.oden.perforce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.anyframe.oden.perforce.domain.CfgBuildDetail;
import org.anyframe.oden.perforce.domain.BuildInfo;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Task;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.IFix;
import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.exception.RequestException;
import com.perforce.p4java.option.server.GetFixesOptions;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;

public class PerforceAdaptor extends Task{
	
	//IServer server; 
	IClient iclient;
	IOptionsServer ioptionServer;
		
	/*private String serverUrl = "p4java://localhost:1666";//필요
	private String userName = "junsung3";//필요
	private String password = "qqqq1111";//필요
	//private String userName = "openpms";//필요
	//private String password = "openpms";//필요
	
	private String client = "junsung3_SDS-PC_273";//필요
	//private String client = "aa";//필요
	//private String client = "park";//openpms_perforce	
	//private String workspace = "C:/Users/SDS/Perforce/junsung3_SDS-PC_273"; //iclient에서 받아와도 될듯..
	//private String dRecode = "another10";//우리 임의값
	//private String clientRoot = "//streamsDepot/mainline";	//iclient에서 받아와도 될듯..
	public String clientRoot = "";
	//private String jobId = "hi";//필요
	//private String jobId = "[{requestId:bye,buildId:another10},{requestId:hi,buildId:another11},{requestId:hello,buildId:another12}]";
	private String jobId = "[{requestId:openpms,buildId:PROCESS_DEVELOP_7_TEMPLATE}]";
	private String finalPath = "C:/TESTPERFORCE";//우리 임의값
*/
	
	/*//private String serverUrl = "p4java://172.21.3.162:6001";//필요
	private String serverUrl = "p4java://182.192.68.72:6001";
	private String userName = "openpms";//필요
	private String password = "openpms";//필요	
	//private String client = "DEV-hudson-SES-WS-v10";//필요
	private String client = "OPENPMS";//필요
	//private String workspace = "C:/Users/SDS/Perforce/junsung3_SDS-PC_273"; //iclient에서 받아와도 될듯..
	public String clientRoot = "";
	//private String jobId = "[{requestId:PROCS20141006104301109_1_TEMPLATE001,buildId:PROCESS_DEVELOP_7_TEMPLATE}]";
	private String jobId = "[{requestId:PROCESS_DEVELOP_PJT201411251036_14_PJT201411251036,buildId:PROCESS_DEVELOP_PJT201411251036_14_PJT201411251036}]";
	//private String finalPath = "/opt/openpms/testDetination";//우리 임의값
	//private String finalPath = "C:/testPerforce";
	//private String finalPath = "C:/Users/SDS/Desktop/준비사항/1. 기본설치 파일/AnyframeCTIP-3.1.0_lite/jenkins/jobs/perforce/workspace/dist";
	*/
	private String serverUrl;
	private String userName;
	private String password;
	private String client;
	//private String workspace;		
	//private String dRecode;
	//private String clientRoot;		
	private String clientRoot = "";
	private String finalPath;
	//private String jobId = "[{requestId:bye,buildId:another10},{requestId:hi,buildId:another11},{requestId:hello,buildId:another12}]";
	private String jobId;
	//private String jobId = "[{requestId:PROCESS_DEVELOP_PJT201411251036_14_PJT201411251036,buildId:PROCESS_DEVELOP_PJT201411251036_14_PJT201411251036}]";

	String workspace;
	String cmdName = "sync";
	String[] cmdArgs = new String[2];
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setClient(String client) {
		this.client = client;
	}

	/*public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}*/

	/*public void setdRecode(String dRecode) {
		this.dRecode = dRecode;
	}*/

	/*public void setClientRoot(String clientRoot) {
		this.clientRoot = clientRoot;
	}*/
	
	public void setFinalPath(String finalPath) {
		this.finalPath = finalPath;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}	
	

	public void execute(){

		try {
			// 0. transfer vo object
			//System.out.println("Request: " + requestId);
			/*BuildInfo build = new BuildInfo(userId, password, dbName, dbConnection,
					server, convertJson(requestId), productName, projectName, targetPath);*/
			BuildInfo build = new BuildInfo(userName, password, "", "",
					serverUrl, convertJson(jobId), client, "" , "");
			System.out.println("Request: " + build.getRequestId());
			
			// 1. checkout task
			checkOut(build);
		} catch (Exception e) {
			getProject().fireBuildFinished(e);
			System.exit(-1);
		}

	}
	
	private void checkOut(BuildInfo build){
		try {			
			validateConfig();
			//connect(serverUrl, userName, password, client);
			connect(build.getServer(),build.getUserId(),build.getPassword(),build.getProductName());
			
			
			iclient.getHostName();
			iclient.getRoot();
			
			//clientRoot = iclient.getStream();//로컬전용	
			clientRoot = "//";//SC전용
			
			List<IFileSpec> fileSpec = FileSpecBuilder.makeFileSpecList(new String[]{clientRoot+"..."}); //clientRoot에서 fileSpec가져옴
			
			List<CfgBuildDetail> requestList = build.toBuildObject();
			
			for(int k=0; k < requestList.size(); k++){			
				GetFixesOptions getFixes = new GetFixesOptions();			
				//getFixes.setJobId(jobId);
				getFixes.setJobId(requestList.get(k).getRequestId());
				
				//1.해당하는 job의 모든 changeList 등 정보를 불러온다.			
				List<IFix> ifix = ioptionServer.getFixes(fileSpec, getFixes);	
				
				//2. 모든 changelistId의 file들을 fileList에 담는다.
				List<String> fileList = fileListSum(ifix);
				
				//방법2. job별로 mapping된 최신 rev만 checkout 				
				List<String> tempFileList = fileList;
				List<String> resultFile = new ArrayList<String>();
				
				
				for(int i = 0; i < fileList.size(); i++){
					String[] sourceFile = fileList.get(i).toString().split("#");
					for(int j = 0; j < tempFileList.size(); j++){
						String[] targetFile = tempFileList.get(j).toString().split("#");
						if(sourceFile[0].equals(targetFile[0])){
							if(Integer.parseInt(sourceFile[1]) < Integer.parseInt(targetFile[1])){
								sourceFile[0] = targetFile[0];
								sourceFile[1] = targetFile[1];
							}
						}
					}
					
					if(!resultFile.contains(sourceFile[0]+"#"+sourceFile[1])){
						System.out.println("다운로드 파일 : " + sourceFile[0]+"#"+sourceFile[1]);
						resultFile.add(sourceFile[0]+"#"+sourceFile[1]);
					}								
				}				
				//방법2 종료
				
				
				
				//3. clientRoot로 파일 Copy하고 다시 파일을 사용자 지정 경로로 Copy한다.			
				fileCopy(resultFile, requestList.get(k).getBuildId());
				//4. Perforce에 open된 file들을 revert 시킨다.
				//fileRevert(requestList.get(k).getBuildId());
				
				//5. client workspace directory 삭제
				//File deleteTarget = new File(workspace + "/" + requestList.get(k).getBuildId());
				//File deleteTarget = new File(workspace);
				//deleteFolder(deleteTarget);
			}
			
			
		/*	cmdArgs[0] = "//streamsDepot/mainline/anyframe-query-pi/PMD_ruleset_SDS_Standard_20140521.xlsx";
			cmdArgs[1] = "//streamsDepot/mainline/another4/anyframe-query-pi/PMD_ruleset_SDS_Standard_20140521.xlsx";*/			
			
			//logging
			//System.out.println("execute "+ cmdName + " args:" + Arrays.asList(cmdArgs));
						
			close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	private void fileRevert(String dRecode) throws Exception {
		//cmdArgs[0] = clientRoot+"/"+dRecode+"/...";//로컬전용
		cmdArgs[0] = dRecode+"/...";//SC전용
		executeCmd("revert",cmdArgs);
	}

	private void fileCopy(List<String> fileList, String dRecode) throws Exception, IOException {
		workspace = iclient.getRoot();
		
		
		
		for(int i = 0; i < fileList.size(); i++){
		
			//방법2 job별로 mapping된 최신 rev만 checkout 시작
			String[] targetPath = fileList.get(i).split("#");
			cmdArgs[0] = "-f";
			cmdArgs[1] = fileList.get(i);//EX) //streamsDepot/mainline/anyframe-query-pi/src/main/java/org/anyframe/plugin/common/MovieFinderException.java
			//cmdArgs[1] = clientRoot+"/"+dRecode+targetPath[0].replaceAll(clientRoot, ""); //EX) /anyframe-query-pi/src/main/java/org/anyframe/plugin/common/MovieFinderException.java
			
			
			//sync명령어 실행
	
			executeCmd(cmdName,cmdArgs);
			
			//빌드 할 workspace로 복사 
			//String temp = targetPath[0].replace(clientRoot+"/", "");//로컬전용		
			String temp = targetPath[0].replace(clientRoot, "");//SC전용
			int index = temp.indexOf("/");			
			String resultTemp = temp.substring(index, temp.length());
		
			//File source = new File(workspace + "/" + targetPath[0].replace(clientRoot, ""));//임시
			String sourcePath = targetPath[0].replace(clientRoot, "");
			String[] tempArr = sourcePath.split("/");
			String resultPath = "";
			
			for(int k = 1; k < tempArr.length; k++){
				if(k != tempArr.length-1){
					resultPath = resultPath + tempArr[k] + "/"; 
				}
				else{
					resultPath = resultPath + tempArr[k];
				}
			}			
			
			// DEMO/sample/anyframe-query-pi/src/main/java/org/anyframe/plugin/common/MovieFinderException.java
					
					
			File source = new File(workspace + "/" + resultPath);//SC전용
			System.out.println("sorurce : " + source.getCanonicalPath());
			File target = new File(finalPath+ "/" + dRecode + resultTemp);
			//방법2 종료			
			copyFileUsingApacheCommonsIO(source, target);
			
			
			//최신 sync로 변경하기위함
			cmdArgs[0] = targetPath[0];
			executeCmd(cmdName,cmdArgs);
			
		}
	}

	private List<String> fileListSum(List<IFix> ifix)
			throws ConnectionException, RequestException, AccessException {		
		List<IFileSpec> changeListFiles;
		List<String> fileList = new ArrayList<String>();
		for(int i = 0; i < ifix.size(); i++){
			changeListFiles =  ioptionServer.getChangelistFiles(ifix.get(i).getChangelistId());				
			
			// job별로 mapping된 최신 rev만 checkout 
			for(int j = 0; j < changeListFiles.size(); j++){
				fileList.add(changeListFiles.get(j).toString());
			}			
		}
		return fileList;
	}
	
	private void connect(String serverUriString, String userName, String password, String client) throws Exception{
		
		try {
			ioptionServer = (IOptionsServer) ServerFactory.getServer(serverUriString, null);
			//ioptionServer.setCharsetName("utf8");//로컬전용
			ioptionServer.connect();
			ioptionServer.setUserName(userName);
			ioptionServer.login(password);
			iclient = ioptionServer.getClient(client);
			//iclient.setRoot("C:/perforceRootTest");
			if (client != null) {
				//ioptionServer.updateClient(iclient, true);
				ioptionServer.setCurrentClient(iclient);			
				//ioptionServer.getClient(iclient);
			}else{
				throw new Exception("error. client is null clientFolder:" + client + ", serverUriString:" + serverUriString);
			}
			
		} catch (Exception e) {
			System.out.println("connection fail : url " + serverUriString);
			
			//e.printStackTrace();
			if(ioptionServer!=null){
				try{
					ioptionServer.disconnect();
				}catch(Exception ee){
				// ignore it 
				}
			}
			throw e;
		}
	}

	private void close(){
		if (ioptionServer != null) {
			try {
				ioptionServer.disconnect();
			} catch (Exception e) {
				System.out.println("disconnect error " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private void executeCmd(String cmdName, String[] cmdArgs) throws Exception{
		ioptionServer.execInputStringMapCmd(cmdName, cmdArgs, null);		
	}
	
	
	private void validateConfig() throws Exception{
		if(serverUrl==null 
			|| userName==null
			|| password==null
			|| client==null
			//|| workspace==null
				){
			
			String configstr = "serverUrl:"+serverUrl + " userName:"+userName +" password:"+password +" client:"+client; //+" workspace:"+workspace;
			
			throw new Exception(" config invalid => " + configstr);
		}		
	}

	//파일 복사
	private void copyFileUsingApacheCommonsIO(File source, File dest) throws IOException { 
	    FileUtils.copyFile(source, dest); 
	}
	
	private String convertJson(String request) {
		String trans = request;

		trans = trans.replace("[{", "[{\"");
		trans = trans.replace(":", "\":\"");
		trans = trans.replace(",", "\",\"");
		trans = trans.replace("}", "\"}");
		
		trans = trans.replace("\"{", "{\"");
		trans = trans.replace("}\"", "}");
		
		return trans;
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();		    
	}
}
