package net.thesieutoc.card;

import com.google.gson.JsonObject;
import java.util.HashMap;
import net.thesieutoc.Thesieutoc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
   public static HashMap<String, String> CHAT_CHARGE = new HashMap<>();
   Thesieutoc m = Thesieutoc.getInstance();

   @EventHandler
   public void chat(AsyncPlayerChatEvent e) {
      Player p = e.getPlayer();
      if (CHAT_CHARGE.containsKey(p.getUniqueId().toString())) {
         String msg = e.getMessage();
         e.setCancelled(true);
         e.setMessage("");
         if (msg.equalsIgnoreCase("huy") || msg.contains(" ")) {
            CHAT_CHARGE.remove(p.getUniqueId().toString());
            this.m.REQUESTS.remove(p.getName());
            p.sendMessage(this.m.getLang("nap_the_huy"));
            return;
         }

         if (CHAT_CHARGE.get(p.getUniqueId().toString()).equalsIgnoreCase("seri")) {
            JsonObject json = this.m.REQUESTS.containsKey(p.getName()) ? this.m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("seri", msg);
            this.m.REQUESTS.put(p.getName(), json);
            CHAT_CHARGE.put(p.getUniqueId().toString(), "pin");
            p.sendMessage("");
            p.sendMessage(this.m.getLang("chat_nhap_pin"));
         } else if (CHAT_CHARGE.get(p.getUniqueId().toString()).equalsIgnoreCase("pin")) {
            CHAT_CHARGE.remove(p.getUniqueId().toString());
            JsonObject json = this.m.REQUESTS.containsKey(p.getName()) ? this.m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("pin", msg);
            this.m.REQUESTS.put(p.getName(), json);
            this.m.WEB_REQUEST.send(p);
         }
      }
   }
}
