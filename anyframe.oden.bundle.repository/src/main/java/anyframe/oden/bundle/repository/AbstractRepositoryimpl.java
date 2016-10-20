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
package anyframe.oden.bundle.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Abstract class having common method for the RepositoryService.
 * 
 * @author joon1k
 *
 */
public abstract class AbstractRepositoryimpl implements RepositoryService {

	public boolean matchedURI(String[] args) {
		if(args.length > 0)
			return args[0].startsWith(getProtocol());
		return false;
	}
	
	protected String stripProtocol(String uri) {
		StringBuffer buf = new StringBuffer(uri);
		
		// remove protocol
		if(uri.startsWith(getProtocol())){
			buf.delete(0, getProtocol().length());
		}
		return buf.toString();
	}

	
	/**
	 * close시 파일도 삭제
	 * @author joon1k
	 *
	 */
	protected class TmpFileInputStream extends FileInputStream {
		private File file;
		public TmpFileInputStream(File file) throws FileNotFoundException {
			super(file);
			this.file = file;
		}

		@Override
		public void close() throws IOException {
			try{
				super.close();
			} finally {
				file.delete();
			}	// end of try catch
			
		}
		
	}
	
}
