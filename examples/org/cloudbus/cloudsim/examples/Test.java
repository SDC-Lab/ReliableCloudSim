package org.cloudbus.cloudsim.examples;
import java.util.*;

import org.cloudbus.cloudsim.failure.BinPacking;

import java.text.NumberFormat;

public class Test{	
	public static void main(String[] args){		
		Test2 t = new Test2(10, 20); 
		t.performSum();		
		System.out.println("Sum is " +t.returnSum());
		System.out.println("Sum is " +t.returnMult());
	}
}