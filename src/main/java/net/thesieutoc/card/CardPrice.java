package net.thesieutoc.card;

public enum CardPrice {
   _10K(10000, 1),
   _20K(20000, 2),
   _30K(30000, 3),
   _50K(50000, 4),
   _100K(100000, 5),
   _200K(200000, 6),
   _300K(300000, 7),
   _500K(500000, 8),
   _1M(1000000, 9),
   UNKNOWN(0, -1);

   private final int price;
   private final int id;

   private CardPrice(int price, int id) {
      this.price = price;
      this.id = id;
   }

   public static CardPrice getPrice(int price) {
      for(CardPrice a : values()) {
         if (a.price == price) {
            return a;
         }
      }

      return UNKNOWN;
   }

   public int getprice() {
      return this.price;
   }

   public int getId() {
      return this.id;
   }
}
