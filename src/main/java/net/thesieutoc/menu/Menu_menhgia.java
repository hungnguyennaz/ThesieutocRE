package net.thesieutoc.menu;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.card.CardPrice;
import net.thesieutoc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu_menhgia implements Listener {
   private static Thesieutoc m;

   public Menu_menhgia() {
      m = Thesieutoc.getInstance();
   }

   public static Inventory get() {
      FileConfiguration fc = m.getMenu();
      Inventory inv = Bukkit.createInventory(
         null, Thesieutoc.getInstance().getMenu().getInt("menhgia.Size"), ChatColor.translateAlternateColorCodes('&', fc.getString("menhgia.Name"))
      );

      for(String cardtype : fc.getConfigurationSection("menhgia.Inv").getKeys(false)) {
         inv.setItem(fc.getInt("menhgia.Inv." + cardtype + ".Slot"), Utils.getMenuIcon("menhgia.Inv." + cardtype));
      }

      return inv;
   }

   @EventHandler
   public void a(InventoryClickEvent e) {
      if (e.getView().getTitle().equals(m.getMenu().getString("menhgia.Name"))) {
         e.setCancelled(true);
         Player p = (Player)e.getWhoClicked();
         ItemStack i = e.getCurrentItem();
         if (i == null || i.getType() == Material.AIR) {
            return;
         }

         if (e.getClickedInventory() != e.getView().getTopInventory()) {
            return;
         }

         FileConfiguration fc = m.getMenu();
         String cardprice = "";
         JsonObject json = m.REQUESTS.containsKey(p.getName()) ? m.REQUESTS.get(p.getName()) : new JsonObject();

         for(String key : fc.getConfigurationSection("menhgia.Inv").getKeys(false)) {
            if (fc.getInt("menhgia.Inv." + key + ".Slot") == e.getRawSlot()) {
               cardprice = key;
            }
         }

         try {
            int xx = Integer.parseInt(cardprice);
            if (CardPrice.getPrice(xx).getId() == -1) {
               return;
            }

            json.addProperty("cardprice", xx);
            m.REQUESTS.put(p.getName(), json);
            Menu_seripin.seri(p);
         } catch (NumberFormatException var9) {
         }
      }
   }
}
