package net.thesieutoc.database;

import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.thesieutoc.Thesieutoc;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MySQL {
   Connection connection;

   public MySQL() {
      if (Thesieutoc.getInstance().getConfig().getBoolean("mysql.enable")) {
         this.start();

         try {
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(
               "CREATE TABLE if not exists `napthe_log` ( `id` MEDIUMINT(11) NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `uuid` VARCHAR(100) NOT NULL , `seri` VARCHAR(255) NOT NULL , `pin` VARCHAR(255) NOT NULL , `loai` VARCHAR(255) NOT NULL , `time` INT(11) NOT NULL , `menhgia` VARCHAR(10) NOT NULL , `note` VARCHAR(255) NOT NULL , `server_port` VARCHAR(15) NOT NULL , PRIMARY KEY (`id`))"
            );
         } catch (SQLException var21) {
            var21.printStackTrace();
         }
      }
   }

   public void start() {
      if (this.connection != null) {
         try {
            this.connection.close();
         } catch (SQLException var3) {
            var3.printStackTrace();
         }
      }

      try {
         FileConfiguration config = Thesieutoc.getInstance().getConfig();
         this.connection = this.getConnection(
            "jdbc:mysql://"
               + config.getString("mysql.host")
               + ":"
               + config.getInt("mysql.port")
               + "/"
               + config.getString("mysql.database")
               + "?autoReconnect=true",
            config.getString("mysql.user"),
            config.getString("mysql.password")
         );
         if (this.connection == null) {
            Bukkit.getLogger().warning("§a[Thesieutoc] §cKet noi MySQL that bai, plugin se bi tat!");
            Thesieutoc.getInstance().getPluginLoader().disablePlugin(Thesieutoc.getInstance());
         }
      } catch (Exception var21) {
         var21.printStackTrace();
         Bukkit.getLogger().warning("§a[Thesieutoc] §cKet noi MySQL that bai, vui long lien he staff TheSieuToc.");
      }
   }

   public Connection getConnection(String dbURL, String userName, String password) {
      Connection conn = null;

      try {
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection(dbURL, userName, password);
      } catch (Exception var7) {
      }

      if (conn == null) {
         try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
         } catch (Exception var6) {
            Bukkit.getLogger().warning("§a[Thesieutoc] §cKet noi MySQL that bai, vui long lien he staff TheSieuToc.");
            var6.printStackTrace();
         }
      }

      return conn;
   }

   public ResultSet query(String query) {
      try {
         if (this.connection.isClosed()) {
            this.start();
         }

         Statement stmt = this.connection.createStatement();
         return stmt.executeQuery(query);
      } catch (SQLException var31) {
         var31.printStackTrace();
         return null;
      }
   }

   public int getPlayerTotalCharged(Player p) {
      int total = 0;

      try {
         if (this.connection.isClosed()) {
            this.start();
         }

         ResultSet rs = this.query("SELECT * FROM napthe_log WHERE note = 'thanh cong' AND name = '" + p.getName() + "'");

         while(rs.next()) {
            total += rs.getInt("menhgia");
         }

         rs.close();
      } catch (Exception var41) {
         var41.printStackTrace();
         Bukkit.getLogger().warning("§a[Thesieutoc] §cXuat hien loi ve MySQL, vui long lien he staff TheSieuToc.");
      }

      return total;
   }

   public void writeLog(Player player, JsonObject json) {
      try {
         if (this.connection.isClosed()) {
            this.start();
         }

         Statement stmt = this.connection.createStatement();
         stmt.executeUpdate(
            "INSERT INTO napthe_log(name,uuid,loai,time,menhgia,pin,seri,server_port,note) VALUE (\""
               + player.getName()
               + "\", \""
               + player.getUniqueId().toString()
               + "\", \""
               + json.get("cardtype").getAsString()
               + "\", "
               + System.currentTimeMillis() / 1000L
               + ", \""
               + json.get("cardprice").getAsInt()
               + "\", \""
               + json.get("pin").getAsString()
               + "\", \""
               + json.get("seri").getAsString()
               + "\", \""
               + Bukkit.getPort()
               + "\", '"
               + json.get("msg").getAsString()
               + "')"
         );
         stmt.close();
      } catch (Exception var41) {
         var41.printStackTrace();
         Bukkit.getLogger().warning("§a[Thesieutoc] §cCo loi xay ra khi ghi log nap the, vui long lien he staff TheSieuToc.");
      }
   }
}
