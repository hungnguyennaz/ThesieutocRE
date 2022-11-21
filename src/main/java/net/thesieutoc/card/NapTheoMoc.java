package net.thesieutoc.card;

import net.thesieutoc.Thesieutoc;
import net.thesieutoc.event.PlayerCardChargedEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NapTheoMoc implements Listener {
   @EventHandler
   public void a(PlayerCardChargedEvent e) {
      FileConfiguration fc = Thesieutoc.getInstance().getNapTheoMoc();
      Player p = e.getPlayer();
      if (fc.getBoolean("enable", false)) {
         for(String bruh : fc.getConfigurationSection("command").getKeys(false)) {
            int lmao = Integer.parseInt(bruh);
            if (e.getTotalCharged() - e.getCardPrice() < lmao && e.getTotalCharged() >= lmao) {
               for(String command : fc.getStringList("command." + lmao)) {
                  Thesieutoc.getInstance().dispatchCommand(p, command);
               }
            }
         }
      }
   }
}
