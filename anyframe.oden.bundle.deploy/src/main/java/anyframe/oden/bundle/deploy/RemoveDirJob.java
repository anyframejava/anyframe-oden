/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.deploy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.common.FileUtil;

/**
 * 
 * Job to remove files.
 * 
 * @author joon1k
 *
 */
public class RemoveDirJob extends StoppableJob{
	String dir;
	String bak;
	
	public RemoveDirJob(String id, String dir, String bak){
		super(id);
		this.dir = dir;
		this.bak = bak;
	}
	
	@Override
	protected Object execute() throws IOException {
		List<DoneFileInfo> infos = new ArrayList<DoneFileInfo>();
		File f = new File(dir);
		if(!f.isAbsolute())
			throw new IOException("Absolute path is allowed only: " + f);
		backupRemoveDir(infos, f, dir, bak);
		return infos;
	}

	/**
	 * used before restoring snapshots.
	 * 
	 * @param infos
	 * @param dir
	 * @param root
	 * @param bak
	 * @throws IOException
	 */
	private void backupRemoveDir(List<DoneFileInfo> infos, File dir, 
			final String root, final String bak){
		if(stop)
			return;
		
		if(dir != null && dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(int i=0; i<files.length; i++) {
				if(stop)
					break;
				
				if(files[i].isDirectory()) {
					backupRemoveDir(infos, files[i], root, bak);
				}else {
					String path = FileUtil.getRelativePath(root, files[i].getPath());
					boolean isUpdate = false;
					boolean success = false;
					try{
						isUpdate = DeployerUtils.undoBackup(root, path, bak);
						success = files[i].delete();
					}catch(IOException e){
					}
					DoneFileInfo info = new DoneFileInfo(
							path, 
							false, 
							files[i].lastModified(), 
							files[i].length(), isUpdate, success);
					infos.add(info);
				}
			}
			if(!stop)
				dir.delete();
        }
	}
}
