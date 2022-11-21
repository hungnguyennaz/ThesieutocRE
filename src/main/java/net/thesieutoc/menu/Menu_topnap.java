package net.thesieutoc.menu;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Menu_topnap implements Listener {
   private static long menu_update_ms = 0L;
   private static Inventory menu;

   public static Inventory get() {
      FileConfiguration fc = Thesieutoc.getInstance().getMenu();
      if (menu_update_ms < System.currentTimeMillis()) {
         menu = Bukkit.createInventory(
            null, Thesieutoc.getInstance().getMenu().getInt("top.Size"), ChatColor.translateAlternateColorCodes('&', fc.getString("top.Name", "Top nạp thẻ"))
         );
         Map<String, Integer> cashcharged = Thesieutoc.getInstance().tsttop.get("ALL", 0L, 0L);
         HashMap<Integer, String> top = Utils.sortByComparator(cashcharged, false, 10);

         for(String key : fc.getConfigurationSection("top.Inv").getKeys(false)) {
            int topnb = -1;

            try {
               topnb = Integer.parseInt(key.substring(3));
            } catch (Exception var11) {
            }

            ItemStack i = new ItemStack(Material.matchMaterial(fc.getString("top.Inv." + key + ".Type", "EMERALD_BLOCK")));
            ItemMeta im = i.getItemMeta();
            List<String> lorelist = new LinkedList<>();

            for(String lore : fc.getStringList("loaithe.Inv." + key + ".Lore")) {
               if (topnb != -1) {
                  lore = MessageFormat.format(lore, topnb, top.get(topnb), new DecimalFormat("#,###").format(cashcharged.get(top.get(topnb))));
               }

               lorelist.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            String displayName = ChatColor.translateAlternateColorCodes('&', fc.getString("top.Inv." + key + ".Name"));
            if (topnb != -1) {
               String lebaongu = top.getOrDefault(topnb, "Trống");
               displayName = MessageFormat.format(displayName, topnb, lebaongu, new DecimalFormat("#,###").format(cashcharged.getOrDefault(lebaongu, 0)));
            }

            im.setDisplayName(displayName);
            im.setLore(lorelist);
            i.setItemMeta(im);
            menu.setItem(fc.getInt("top.Inv." + key + ".Slot"), i);
         }

         menu_update_ms = System.currentTimeMillis() + 10000L;
      }

      return menu;
   }

   @EventHandler
   public void a(InventoryClickEvent e) {
      FileConfiguration fc = Thesieutoc.getInstance().getMenu();
      String topMenuName = ChatColor.translateAlternateColorCodes('&', fc.getString("top.Name", "Top nạp thẻ"));
      if (e.getView().getTitle().equals(topMenuName)) {
         e.setCancelled(true);
      }
   }
}
