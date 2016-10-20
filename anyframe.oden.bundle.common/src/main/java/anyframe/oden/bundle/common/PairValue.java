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

import java.io.Serializable;

public class PairValue<T1, T2> implements Serializable{
	private T1 v1;
	private T2 v2;
	
	public PairValue(T1 v1, T2 v2){
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public T1 value1(){
		return v1; 
	}
	
	public T2 value2(){
		return v2;
	}
	
	public void setValue1(T1 v1){
		this.v1 = v1;
	}
	
	public void setValue2(T2 v2){
		this.v2 = v2;
	}
	
	@Override
	public String toString(){
		return v1.toString() + "(" + v2.toString() + ")";
	}
}
