/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.common;

import java.util.Arrays;

/**
 * This is FileUtilTest class.
 * 
 * @author Junghwan Hong
 */
public class FileUtilTest {

	public static void main(String[] args) {
		try {

			toRegexTest();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void toRegexTest() {
		System.out.println(FileUtil.toRegex("/Users/asdf/**"));
		System.out.println(FileUtil.toRegex("**/*"));
		System.out.println(FileUtil.toRegex("*"));
		System.out.println(FileUtil.matched(
				"C:\\eclipse\\plugins\\webapp\\asdf.xml",
				Arrays.asList(new String[] { "**" })));

		System.out
				.println(FileUtil.matched(
						"/Users/asdf/aa/asfds.xml",
						Arrays.asList(new String[] { "/Users",
								"/Users/**/aa/*.xml" })));

		System.out.println(FileUtil.matched("eclipse\\plugins\\webapp\\",
				Arrays.asList(new String[] { "**/app/*", "**/plugins/**" })));

		System.out.println(FileUtil.matched("C:\\eclipse\\plugins\\webap",
				Arrays.asList(new String[] { "**/*" })));
	}

}
