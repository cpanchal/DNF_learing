package edu.cmu.cs.JavaDNF.lib;

public class Utils {

    /**
    *
    * @param b1
    * @param b2
    * @return
    */
   public static boolean[] AND(boolean[] b1, boolean[] b2) {
       int size = b1.length;
       boolean[] result = new boolean[size];
       for (int i = 0; i < size; ++i) {
           result[i] = b1[i] && b2[i];
       }
       return result;
   }

   /**
    *
    * @param b1
    * @param b2
    * @return
    */
   public static boolean[] OR(boolean[] b1, boolean[] b2) {
       int size = b1.length;
       boolean[] result = new boolean[size];
       for (int i = 0; i < size; ++i) {
           result[i] = b1[i] || b2[i];
       }
       return result;
   }
   
   /**
   *
   * @param s
   */
  public static <E> void debug(E s) {
      System.out.print(s);
  }

  /**
   *
   * @param s
   */
  public static <E> void debugln(E s) {
      System.out.println(s);
  }

   
   /**
    * 
    * @param <E>
    */
   public static <E> void debugln() {
       System.out.println();
   }
}
