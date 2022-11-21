package net.thesieutoc.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCardChargedEvent extends Event {
   static final HandlerList handlers = new HandlerList();
   Player p;
   String cardtype;
   int cardprice;
   int total_charged;

   public PlayerCardChargedEvent(Player p, String cardtype, int cardprice, int total_charged) {
      this.p = p;
      this.cardtype = cardtype;
      this.cardprice = cardprice;
      this.total_charged = total_charged;
      Bukkit.getServer().getPluginManager().callEvent(this);
   }

   public static final HandlerList getHandlerList() {
      return handlers;
   }

   public Player getPlayer() {
      return this.p;
   }

   public String getCardType() {
      return this.cardtype;
   }

   public int getCardPrice() {
      return this.cardprice;
   }

   public int getTotalCharged() {
      return this.total_charged;
   }

   public final HandlerList getHandlers() {
      return handlers;
   }
}
