package org.cloudbus.cloudsim;

public class Test3 extends Test2{
	public int i;
	
	public void assignvalue(int i){
		this.i = i;
	}
	
	public void displayvalue(){
		System.out.println(i);
	}
	
	public static void main(String args[]){
		Test abc = new Test3();
		abc.function();
	}

}
