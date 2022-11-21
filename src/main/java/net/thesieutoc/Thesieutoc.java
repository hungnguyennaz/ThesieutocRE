package net.thesieutoc;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import net.thesieutoc.card.ChatListener;
import net.thesieutoc.card.NapTheoMoc;
import net.thesieutoc.card.WebCallback;
import net.thesieutoc.card.WebRequest;
import net.thesieutoc.command.Command_napthe;
import net.thesieutoc.command.Command_topnapthe;
import net.thesieutoc.command.Command_tst;
import net.thesieutoc.database.DB;
import net.thesieutoc.menu.Menu_loaithe;
import net.thesieutoc.menu.Menu_menhgia;
import net.thesieutoc.menu.Menu_seripin;
import net.thesieutoc.menu.Menu_topnap;
import net.thesieutoc.placeholder.TSTPlaceholder;
import net.thesieutoc.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Thesieutoc extends JavaPlugin {
   private static Thesieutoc m;
   public WebRequest WEB_REQUEST;
   public TSTTop tsttop;
   public WebCallback WEB_CALLBACK;
   public DB db;
   public Command_tst cmd_tst;
   public HashMap<String, JsonObject> REQUESTS = new HashMap<>();
   TSTPlaceholder placeholder;
   File menu;
   FileConfiguration menufc;
   File lang;
   FileConfiguration langfc;
   File NapTheoMoc;
   FileConfiguration NapTheoMocfc;
   private static final String MATCH = "(?ium)^(player:|op:|console:|)(.*)$";

   public static Thesieutoc getInstance() {
      return m;
   }

   public void onEnable() {
      m = this;
      this.tsttop = new TSTTop();
      this.saveDefaultConfig();
      this.saveDefaultMenu();
      this.saveDefaultLang();
      this.saveDefaultNapTheoMoc();
      this.getCommand("napthe").setExecutor(new Command_napthe());
      this.getCommand("topnapthe").setExecutor(new Command_topnapthe());
      this.getCommand("thesieutoc").setExecutor(new Command_tst());
      Bukkit.getPluginManager().registerEvents(new ChatListener(), m);
      Bukkit.getPluginManager().registerEvents(new Menu_loaithe(), m);
      Bukkit.getPluginManager().registerEvents(new Menu_menhgia(), m);
      Bukkit.getPluginManager().registerEvents(new Menu_seripin(), m);
      Bukkit.getPluginManager().registerEvents(new Menu_topnap(), m);
      Bukkit.getPluginManager().registerEvents(new NapTheoMoc(), m);
      this.db = new DB();
      this.WEB_REQUEST = new WebRequest(m);
      this.WEB_CALLBACK = new WebCallback(m);
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         try {
            this.placeholder = new TSTPlaceholder(m);
            this.placeholder.register();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      if (Bukkit.getVersion().contains("1.8")) {
         Bukkit.getLogger().info("[Thesieutoc] Tinh nang menu se bi tat do van de khong tuong thich (khong ho tro 1.8)");
         this.getConfig().set("menu", false);
         this.saveConfig();
         this.reloadConfig();
      }
   }

   public void onDisable() {
      for(String name : this.REQUESTS.keySet()) {
         Player p = Bukkit.getPlayer(name);
         if (p != null) {
            p.closeInventory();
         }
      }

      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && this.placeholder != null) {
         this.placeholder.unregister();
      }
   }

   public void saveDefaultMenu() {
      this.menu = new File(m.getDataFolder(), "menu.yml");
      if (!this.menu.exists()) {
         this.menu.getParentFile().mkdirs();
         this.saveResource("menu.yml", false);
      }

      this.menufc = YamlConfiguration.loadConfiguration(this.menu);
   }

   public FileConfiguration getMenu() {
      return this.menufc;
   }

   public void reloadMenu() {
      this.saveDefaultMenu();
   }

   public void saveDefaultLang() {
      this.lang = new File(m.getDataFolder(), "lang.yml");
      if (!this.lang.exists()) {
         this.lang.getParentFile().mkdirs();
         this.saveResource("lang.yml", false);
      }

      this.langfc = YamlConfiguration.loadConfiguration(this.lang);
   }

   public FileConfiguration getLang() {
      return this.langfc;
   }

   public void reloadLang() {
      this.saveDefaultLang();
   }

   public String getLang(String id) {
      return ChatColor.translateAlternateColorCodes('&', m.getLang().getString(id));
   }

   public void saveDefaultNapTheoMoc() {
      this.NapTheoMoc = new File(m.getDataFolder(), "naptheomoc.yml");
      if (!this.NapTheoMoc.exists()) {
         this.NapTheoMoc.getParentFile().mkdirs();
         this.saveResource("naptheomoc.yml", false);
      }

      this.NapTheoMocfc = YamlConfiguration.loadConfiguration(this.NapTheoMoc);
   }

   public FileConfiguration getNapTheoMoc() {
      return this.NapTheoMocfc;
   }

   public void reloadNapTheoMoc() {
      this.saveDefaultNapTheoMoc();
   }

   public void dispatchCommand(Player player, String command) {
      Task.syncTask(() -> {
         String type = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$1").replace(":", "").toLowerCase();
         String cmd = command.replaceAll("(?ium)^(player:|op:|console:|)(.*)$", "$2").replaceAll("(?ium)([{]Player[}])", player.getName());
         switch(type) {
            case "op":
               if (player.isOp()) {
                  player.performCommand(cmd);
               } else {
                  player.setOp(true);
                  player.performCommand(cmd);
                  player.setOp(false);
               }
               break;
            case "":
            case "player":
               player.performCommand(cmd);
               break;
            case "console":
            default:
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
         }
      });
   }
}
