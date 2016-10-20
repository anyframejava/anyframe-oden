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
