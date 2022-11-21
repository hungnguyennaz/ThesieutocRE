package net.thesieutoc.utils;

import java.util.Random;

public class FNum {
   public static int randomInt(int min, int max) {
      return new Random().nextInt(max - min + 1) + min;
   }

   public static int randomInt(String min, String max) {
      return randomInt(ri(min), ri(max));
   }

   public static double randomDouble(double min, double max) {
      Random r = new Random();
      return min + (max - min) * r.nextDouble();
   }

   public static double randomDouble(String min, String max) {
      return randomDouble(rd(max), rd(min));
   }

   public static double randomDoubleNnega(double d) {
      double nega = d * -1.0;
      return Math.random() * (d - nega) + nega;
   }

   public static double rd(String s) {
      return Double.valueOf(s);
   }

   public static int ri(String s) {
      return Integer.valueOf(s);
   }
}
