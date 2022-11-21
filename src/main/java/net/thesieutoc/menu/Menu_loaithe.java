package net.thesieutoc.menu;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.thesieutoc.Thesieutoc;
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

public class Menu_loaithe implements Listener {
   private static Thesieutoc m;

   public Menu_loaithe() {
      m = Thesieutoc.getInstance();
   }

   public static Inventory get() {
      FileConfiguration fc = m.getMenu();
      Inventory inv = Bukkit.createInventory(
         null, Thesieutoc.getInstance().getMenu().getInt("loaithe.Size"), ChatColor.translateAlternateColorCodes('&', fc.getString("loaithe.Name"))
      );

      for(String cardtype : fc.getConfigurationSection("loaithe.Inv").getKeys(false)) {
         inv.setItem(fc.getInt("loaithe.Inv." + cardtype + ".Slot"), Utils.getMenuIcon("loaithe.Inv." + cardtype));
      }

      return inv;
   }

   @EventHandler
   public void a(InventoryClickEvent e) {
      if (e.getView().getTitle().equals(m.getMenu().getString("loaithe.Name"))) {
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
         String cardtype = "";
         JsonObject json = m.REQUESTS.containsKey(p.getName()) ? m.REQUESTS.get(p.getName()) : new JsonObject();

         for(String key : fc.getConfigurationSection("loaithe.Inv").getKeys(false)) {
            if (fc.getInt("loaithe.Inv." + key + ".Slot") == e.getRawSlot()) {
               cardtype = key;
            }
         }

         if (!m.getConfig().getStringList("card.enable").contains(cardtype)) {
            return;
         }

         json.addProperty("cardtype", cardtype);
         m.REQUESTS.put(p.getName(), json);
         p.openInventory(Menu_menhgia.get());
      }
   }
}
