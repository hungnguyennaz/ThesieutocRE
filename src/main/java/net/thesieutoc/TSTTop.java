package net.thesieutoc;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import net.thesieutoc.utils.FNum;

public class TSTTop {
   public Map<String, Integer> get(String target, long startDate, long endDate) {
      Map<String, Integer> cashcharged = new HashMap<>();
      if (Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable")) {
         String s = target.isEmpty() ? "" : " AND name = '" + target + "'";
         ResultSet rs;
         if (startDate != 0L && endDate != 0L) {
            rs = Thesieutoc.getInstance()
               .db
               .mysql
               .query("SELECT * FROM napthe_log WHERE note = 'thanh cong' AND time >= " + startDate + " AND time <= " + endDate + s);
         } else {
            rs = Thesieutoc.getInstance().db.mysql.query("SELECT * FROM napthe_log WHERE note = 'thanh cong'" + s);
         }

         if (rs != null) {
            try {
               while(rs.next()) {
                  String name = rs.getString("name");
                  int cash = rs.getInt("menhgia");
                  int totalcash = cashcharged.getOrDefault(name, 0);
                  totalcash += cash;
                  cashcharged.put(name, totalcash);
               }
            } catch (SQLException var17) {
               var17.printStackTrace();
            }
         }
      } else {
         File log = new File(Thesieutoc.getInstance().getDataFolder(), "log_success.txt");
         if (!log.exists()) {
            return cashcharged;
         }

         try {
            Scanner scanner = new Scanner(log);

            while(scanner.hasNextLine()) {
               String line = scanner.nextLine();
               if (line.contains("thanh cong")) {
                  SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                  String f = line.substring(1).split("\\] ")[0];
                  Date date = df.parse(f);
                  if (startDate == 0L || endDate == 0L || date.getTime() / 1000L >= startDate && date.getTime() / 1000L <= endDate) {
                     line = line.split("\\] ")[1];
                     String name = line.split(" \\| ")[0];
                     if (target.equalsIgnoreCase("ALL") || target.isEmpty() || name.equals(target)) {
                        int cash = FNum.ri(line.split(" \\| ")[3]);
                        int totalcash = cashcharged.getOrDefault(name, 0);
                        totalcash += cash;
                        cashcharged.put(name, totalcash);
                     }
                  }
               }
            }
         } catch (Exception var161) {
            var161.printStackTrace();
         }
      }

      return cashcharged;
   }
}
