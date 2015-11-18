package com.berkley.homotopy.fpmath;
public class MathTest{
	public static void main(String[] args){
		
		double d1 = 100.25;
		int i1 = FPMath.FloatToFP(d1);
		System.out.println("double " + d1 + " : integer " + i1);
		System.out.println("integer " + i1 + " : FP " + FPMath.FPToFloat(i1));
		
		double d2 = 20.7525;
		int i2 = FPMath.FloatToFP(d2);
		
		int i3 = i1 + i2;
		double d3 = FPMath.FPToFloat(i3);
		System.out.println("i1 + i2 = " + i3);
		System.out.println("d1 + d2 = " + d3);
	}
}