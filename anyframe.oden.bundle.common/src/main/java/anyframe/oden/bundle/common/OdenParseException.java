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

/**
 * There some bugs while parsing strings, you can use this.
 * 
 * @author joon1k
 *
 */
public class OdenParseException extends OdenException{

	private static final long serialVersionUID = 7470352273080950966L;

	public OdenParseException(String line){
		super("Fail to parse string: " + line);
	}
}
