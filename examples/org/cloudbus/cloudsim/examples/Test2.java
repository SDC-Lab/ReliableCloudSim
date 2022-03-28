package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.failure.BinPacking;

public class Test2 {
	private int x;
	private int y;
	private int c;
	private int m;
	public int t=30;	
	
	public void performSum(){		
		c = x + y;	
		multiply();
	}
	
	private void multiply(){
		m = x * y;
	}
	public Test2(int a, int b){
		x=a;
		y=b;
	}
	
	public int returnSum(){
		return c;
	}
	public int returnMult(){
		return m;
	}
	
	
}
