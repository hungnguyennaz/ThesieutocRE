package net.thesieutoc.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.md_5.bungee.api.ChatColor;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
   public static HashMap<Integer, String> sortByComparator(Map<String, Integer> unsortMap, boolean order, int maxtop) {
      List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
      list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()));
      HashMap<Integer, String> sortedMap = new LinkedHashMap<>();

      for(Entry<String, Integer> entry : list) {
         for(int i = 1; i <= maxtop; ++i) {
            if (!sortedMap.containsKey(i)) {
               sortedMap.put(i, entry.getKey());
               break;
            }
         }
      }

      return sortedMap;
   }

   public static ItemStack getMenuIcon(String path) {
      FileConfiguration fc = Thesieutoc.getInstance().getMenu();
      ItemStack i = new ItemStack(Material.matchMaterial(fc.getString(path + ".Type", "DIRT")));
      i.setDurability((short)fc.getInt(path + ".Data", 0));
      ItemMeta im = i.getItemMeta();
      String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      int subVersion = Integer.parseInt(version.split("_")[1]);
      if (subVersion >= 14) {
         im.setCustomModelData(fc.getInt(path + ".Model", 0));
      }

      List<String> lorelist = new LinkedList<>();

      for(String lore : fc.getStringList(path + ".Lore")) {
         lorelist.add(ChatColor.translateAlternateColorCodes('&', lore));
      }

      im.setDisplayName(ChatColor.translateAlternateColorCodes('&', fc.getString(path + ".Name")));
      im.setLore(lorelist);
      i.setItemMeta(im);
      return i;
   }
}
