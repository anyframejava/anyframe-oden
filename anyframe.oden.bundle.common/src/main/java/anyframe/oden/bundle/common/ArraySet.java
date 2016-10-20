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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
/**
 * 
 * To make Data Set using ArrayList.
 * 
 * @author joon1k
 *
 * @param <E>
 */
public class ArraySet<E> extends ArrayList<E> implements Set<E>{

	private static final long serialVersionUID = -4564449261784541151L;

	@Override
	public boolean add(E o) {
		if(!contains(o))
			return super.add(o);
		return false;
	}
	
	/**
	 * Don't check if contains o. support this for performance reason.
	 * warning. this can make duplicate elements in the set.
	 * 
	 * @param o
	 * @return
	 */
	public boolean addForce(E o) {
		return super.add(o);
	}
	
	/**
	 * method to append collections. This method is fast because do not check 
	 * the duplicated elements. If you sure that the collection doesn't 
	 * have duplicated elements, use this. Unless this set do not guarante a unique elements.
	 * 
	 * @param c
	 * @return
	 */
	public boolean addForce(Collection<? extends E> c) {
		boolean result = false;
		for(E e : c)
			result = result | addForce(e);
		return result;
	}
	
	@Override
	public void add(int index, E element) {
		if(!contains(element))
			super.add(index, element);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		for(E e : c)
			result = result | add(e);
		return result;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
	throw new UnsupportedOperationException();
	}
	
}
