package net.thesieutoc.database;

import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Flatfile {
   public void writeLog(Player p, JsonObject json) {
      Date now = new Date();
      String msg = p.getName()
         + " | "
         + json.get("seri").getAsString()
         + " | "
         + json.get("pin").getAsString()
         + " | "
         + json.get("cardprice").getAsString()
         + " | "
         + json.get("cardtype").getAsString()
         + " | "
         + json.get("msg").getAsString();
      SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
      File log = new File(
         Thesieutoc.getInstance().getDataFolder(), "log" + (json.get("msg").getAsString().equals("thanh cong") ? "_success" : "_failed") + ".txt"
      );
      if (!log.exists()) {
         log.getParentFile().mkdirs();
      }

      BufferedWriter writer = null;

      try {
         writer = new BufferedWriter(new FileWriter(log, true));
         writer.append("[").append(df.format(now)).append("] ").append(msg);
         writer.newLine();
         writer.flush();
         writer.close();
      } catch (IOException var9) {
         var9.printStackTrace();
      }
   }

   public int getPlayerTotalCharged(Player p) {
      int total = 0;
      File log = new File(Thesieutoc.getInstance().getDataFolder(), "log_success.txt");
      if (log.exists()) {
         try {
            Scanner scanner = new Scanner(log);

            while(scanner.hasNextLine()) {
               String line = scanner.nextLine();
               if (line.contains("thanh cong")) {
                  new SimpleDateFormat("HH:mm dd/MM/yyyy");
                  String f = line.substring(1).split("\\] ")[0];
                  line = line.split("\\] ")[1];
                  String name = line.split(" \\| ")[0];
                  if (name.equals(p.getName())) {
                     total += Integer.parseInt(line.split(" \\| ")[3]);
                  }
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
            Bukkit.getLogger().warning("§a[Thesieutoc] §cCo loi xay ra khi dang tinh tong tien da nap cua nguoi choi, vui long lien he staff TheSieuToc.");
         }
      }

      return total;
   }
}
