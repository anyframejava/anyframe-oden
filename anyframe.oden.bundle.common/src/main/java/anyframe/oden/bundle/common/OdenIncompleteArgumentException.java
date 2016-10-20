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
package anyframe.oden.bundle.common;

import java.util.Arrays;

import anyframe.oden.bundle.common.OdenException;

/**
 * There some bugs causes by incompleted arguments, you can use this.
 * 
 * @author joon1k
 *
 */
public class OdenIncompleteArgumentException extends OdenException {

	private static final long serialVersionUID = 842892188973715366L;

	public OdenIncompleteArgumentException(String args) {
		super(args + " is incomplete.");
	}

	public OdenIncompleteArgumentException(String[] args) {
		super(Arrays.toString(args) + " is incomplete.");
	}
	
}
