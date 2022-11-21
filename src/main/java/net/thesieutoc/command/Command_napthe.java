package net.thesieutoc.command;

import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.card.ChatListener;
import net.thesieutoc.menu.Menu_loaithe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_napthe implements CommandExecutor {
   Thesieutoc m = Thesieutoc.getInstance();

   public boolean onCommand(CommandSender sender, Command cmd, String a, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("§cCommand chi xai duoc trong game!");
         return false;
      } else {
         Player p = (Player)sender;
         if (args.length == 5 && args[0].equalsIgnoreCase("nhanh")) {
            JsonObject json = new JsonObject();
            if (!this.m.getConfig().getStringList("card.enable").contains(args[1])) {
               return false;
            }

            json.addProperty("cardtype", args[1]);
            json.addProperty("cardprice", args[2]);
            json.addProperty("seri", args[3]);
            json.addProperty("pin", args[4]);
            this.m.REQUESTS.put(p.getName(), json);
            this.m.WEB_REQUEST.send(p);
         }

         if (args.length == 0) {
            if (this.m.getConfig().getBoolean("menu", true)) {
               p.openInventory(Menu_loaithe.get());
            } else {
               TextComponent txtcomponent = new TextComponent("");

               for(String cardtype : this.m.getConfig().getStringList("card.enable")) {
                  String display = ChatColor.translateAlternateColorCodes('&', this.m.getMenu().getString("loaithe.Inv." + cardtype + ".Name"));
                  String lore = this.list2string(this.m.getMenu().getStringList("loaithe.Inv." + cardtype + ".Lore"));
                  TextComponent message = new TextComponent(display);
                  message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(lore)));
                  message.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/donate choosecard " + cardtype));
                  txtcomponent.addExtra(message);
                  txtcomponent.addExtra("§r   ");
               }

               p.sendMessage("");
               p.sendMessage(this.m.getLang("chat_chon_loai_the"));
               p.spigot().sendMessage(txtcomponent);
            }
         }

         if (args.length == 2 && args[0].equals("choosecard")) {
            JsonObject json = this.m.REQUESTS.containsKey(p.getName()) ? this.m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("cardtype", args[1]);
            this.m.REQUESTS.put(p.getName(), json);
            TextComponent txtcomponent = new TextComponent("");

            for(String cardprice : this.m.getMenu().getConfigurationSection("menhgia.Inv").getKeys(false)) {
               String display = ChatColor.translateAlternateColorCodes('&', this.m.getMenu().getString("menhgia.Inv." + cardprice + ".Name"));
               String lore = this.list2string(this.m.getMenu().getStringList("menhgia.Inv." + cardprice + ".Lore"));
               TextComponent message = new TextComponent(display);
               message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(lore)));
               message.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/donate choosecardprice " + cardprice));
               txtcomponent.addExtra(message);
               txtcomponent.addExtra("§r   ");
            }

            p.sendMessage(MessageFormat.format(this.m.getLang("chat_da_chon_loai_the"), args[1]));
            p.sendMessage("");
            p.sendMessage(this.m.getLang("chat_chon_menh_gia"));
            p.spigot().sendMessage(txtcomponent);
         }

         if (args.length == 2 && args[0].equals("choosecardprice")) {
            JsonObject json = this.m.REQUESTS.containsKey(p.getName()) ? this.m.REQUESTS.get(p.getName()) : new JsonObject();
            json.addProperty("cardprice", Integer.parseInt(args[1]));
            this.m.REQUESTS.put(p.getName(), json);
            p.sendMessage(MessageFormat.format(this.m.getLang("chat_da_chon_menh_gia"), new DecimalFormat("#,###").format((long)Integer.parseInt(args[1]))));
            p.sendMessage("");
            p.sendMessage(this.m.getLang("chat_nhap_seri"));
            ChatListener.CHAT_CHARGE.put(p.getUniqueId().toString(), "seri");
         }

         return true;
      }
   }

   private String list2string(List<String> list) {
      StringBuilder string = new StringBuilder();

      for(String l : list) {
         string.append("\n").append(ChatColor.translateAlternateColorCodes('&', l));
      }

      string = new StringBuilder(string.substring(1));
      return string.toString();
   }
}
