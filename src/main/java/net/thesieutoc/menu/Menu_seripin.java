package net.thesieutoc.menu;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Menu_seripin implements Listener {
   private static Thesieutoc m;

   public Menu_seripin() {
      m = Thesieutoc.getInstance();
   }

   public static ItemStack get(String key) {
      FileConfiguration fc = m.getMenu();
      return Utils.getMenuIcon("seri_pin.Inv." + key);
   }

   public static void seri(Player p) {
      new AnvilGUI.Builder()
         .onComplete((player, reply) -> {
            reply = ChatColor.stripColor(reply.replaceAll(ChatColor.stripColor(get("Seri").getItemMeta().getDisplayName()), ""));
            JsonObject json = m.REQUESTS.containsKey(p.getName()) ? m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("seri", reply);
            m.REQUESTS.put(p.getName(), json);
            return AnvilGUI.Response.close();
         })
         .onClose(player -> Bukkit.getScheduler().runTask(m, () -> pin(p)))
         .title(get("Seri").getItemMeta().getDisplayName())
         .itemLeft(get("Seri"))
         .text(ChatColor.stripColor(get("Seri").getItemMeta().getDisplayName()))
         .plugin(m)
         .open(p);
   }

   public static void pin(Player p) {
      new AnvilGUI.Builder()
         .onComplete((player, reply) -> {
            reply = ChatColor.stripColor(reply.replaceAll(ChatColor.stripColor(get("Pin").getItemMeta().getDisplayName()), ""));
            JsonObject json = m.REQUESTS.containsKey(p.getName()) ? m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("pin", reply);
            Task.asyncTask(() -> {
               m.REQUESTS.put(p.getName(), json);
               m.WEB_REQUEST.send(player);
            });
            return AnvilGUI.Response.close();
         })
         .title(get("Pin").getItemMeta().getDisplayName())
         .itemLeft(get("Pin"))
         .text(ChatColor.stripColor(get("Pin").getItemMeta().getDisplayName()))
         .plugin(m)
         .open(p);
   }
}
