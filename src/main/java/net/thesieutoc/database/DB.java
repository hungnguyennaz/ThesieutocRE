package net.thesieutoc.database;

import com.google.gson.JsonObject;
import net.thesieutoc.Thesieutoc;
import org.bukkit.entity.Player;

public class DB {
   public Flatfile flatfile = new Flatfile();
   public MySQL mysql = new MySQL();

   public void writeLog(Player p, JsonObject json) {
      if (Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable")) {
         this.mysql.writeLog(p, json);
      } else {
         this.flatfile.writeLog(p, json);
      }
   }

   public int getPlayerTotalCharged(Player p) {
      return Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable") ? this.mysql.getPlayerTotalCharged(p) : this.flatfile.getPlayerTotalCharged(p);
   }
}
