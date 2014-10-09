package com.blevinstein.util;

import org.apache.commons.math3.linear.RealMatrix;

// DEBUG tool
//
// Some quick classes written to "pretty print" certain types.
// Should be systematized or deleted eventually.
class PP {
  public static void pp(double d[]) {
    String output = "[ ";
    for (int i = 0; i < d.length; i++) {
      output += String.format(" %4.2f ", d[i]);
    }
    output += "]";
    System.out.println(output);
  }
  
  public static void pp(RealMatrix m) {
    String output = "";
    for(int i = 0; i < m.getRowDimension(); i++) {
      output += "[";
      for(int j = 0; j < m.getColumnDimension(); j++) {
        output += String.format(" %5.2f ", m.getEntry(i, j));
      }
      output += "]\n";
    }
    System.out.println(output);
  }
  
  // DEBUG (debugging tools, remove later)
  public static String dimOf(RealMatrix r) {
    return "[" + r.getRowDimension() + "," + r.getColumnDimension() + "]";
  }
  
  // DEBUG
  public static String dimOf(RealMatrix weights[]) {
    String output = "[" + weights[0].getRowDimension();
    for (int i = 0; i < weights.length; i++) {
      output += "x" + weights[i].getColumnDimension();
    }
    output += "]";
    return output;
  }
}
