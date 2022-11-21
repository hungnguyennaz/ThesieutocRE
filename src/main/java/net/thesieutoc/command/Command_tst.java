package net.thesieutoc.command;

import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.card.CardPrice;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command_tst implements CommandExecutor {
   Thesieutoc m = Thesieutoc.getInstance();

   public Command_tst() {
      this.m.cmd_tst = this;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String a, String[] args) {
      if (args.length == 0) {
         sender.sendMessage("§e/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
         sender.sendMessage("§e/thesieutoc top§f: Xem top nạp thẻ");
         sender.sendMessage("§e/thesieutoc reload§f: Tải lại các file config.");
      }

      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("thesieutoc.admin") && sender instanceof Player) {
            sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
            return false;
         }

         this.m.reloadConfig();
         this.m.reloadLang();
         this.m.reloadMenu();
         this.m.reloadNapTheoMoc();
         sender.sendMessage("§eReload config TheSieuToc thanh cong!");
      }

      if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
         sender.sendMessage(PlaceholderAPI.setPlaceholders((Player)sender, args[1]));
      }

      if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
         if (!sender.hasPermission("thesieutoc.admin") && sender instanceof Player) {
            sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
            return false;
         }

         switch(args.length) {
            case 2:
               sender.sendMessage("§c/thesieutoc give [tên người chơi] [mệnh giá]§f: Nạp cho người chơi số tiền tương ứng");
               break;
            case 3:
               Player target = Bukkit.getPlayer(args[1]);
               if (target != null) {
                  int menhgia = Integer.parseInt(args[2]);
                  if (CardPrice.getPrice(menhgia).getId() == -1) {
                     sender.sendMessage("§cMệnh giá không hợp lệ!");
                     return false;
                  }

                  JsonObject json = new JsonObject();
                  json.addProperty("cardtype", "GIVE");
                  json.addProperty("cardprice", menhgia);
                  json.addProperty("seri", "0");
                  json.addProperty("pin", "0");
                  json.addProperty("msg", "thanh cong");
                  this.m.WEB_CALLBACK.napthanhcong(target, json);
                  Thesieutoc.getInstance().db.writeLog(target, json);
                  sender.sendMessage("§aNạp thành công §f" + menhgia + "§a VNĐ cho " + target.getName() + "!");
               } else {
                  sender.sendMessage("§cNgười chơi §e" + args[1] + "§c không online!");
               }
         }
      }

      if (args.length >= 1 && args[0].equalsIgnoreCase("top")) {
         if (!sender.hasPermission("thesieutoc.admin") && !sender.hasPermission("thesieutoc.top") && sender instanceof Player) {
            sender.sendMessage("§cBạn không có quyền để sử dụng lệnh này!");
            return false;
         }

         Task.asyncTask(() -> this.top(sender, args));
      }

      return true;
   }

   public void top(CommandSender sender, String[] args) {
      switch(args.length) {
         case 1:
            this.sendHelp(sender);
            break;
         case 2:
            String var3 = args[1].toLowerCase();
            switch(var3) {
               case "help":
                  this.sendHelp(sender);
                  break;
               case "today": {
                  Date date = new Date();
                  long startDate = this.day(date).get(0) / 1000L;
                  long endDate = this.day(date).get(1) / 1000L;
                  this.printTop(sender, startDate, endDate);
                  break;
               }
               case "month": {
                  Date date = new Date();
                  long startDate = this.month(date).get(0) / 1000L;
                  long endDate = this.month(date).get(1) / 1000L;
                  this.printTop(sender, startDate, endDate);
                  break;
               }
               case "year": {
                  long startDate = this.year().get(0) / 1000L;
                  long endDate = this.year().get(1) / 1000L;
                  this.printTop(sender, startDate, endDate);
                  break;
               }
               case "total":
                  this.printTop(sender, 0L, 0L);
                  break;
               default:
                  SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

                  try {
                     Date datex = dateformat.parse(args[1]);
                     long startDatex = this.day(datex).get(0) / 1000L;
                     long endDatex = this.day(datex).get(1) / 1000L;
                     this.printTop(sender, startDatex, endDatex);
                  } catch (ParseException var12) {
                     sender.sendMessage("§cThời gian không hợp lệ!");
                  }
            }
      }
   }

   public void sendHelp(CommandSender sender) {
      sender.sendMessage("§e/thesieutoc top total:§f Tính tổng top từ trước đến nay");
      sender.sendMessage("§e/thesieutoc top today:§f Tính top của hôm nay");
      sender.sendMessage("§e/thesieutoc top month:§f Tính top của tháng này");
      sender.sendMessage("§e/thesieutoc top year:§f Tính top của năm nay");
      sender.sendMessage("§e/thesieutoc top [ngày/tháng/năm]:§f Tính top của ngày được chỉ định");
   }

   public void printTop(CommandSender sender, long startDate, long endDate) {
      Task.asyncTask(() -> {
         Map<String, Integer> cashcharged = this.m.tsttop.get("ALL", startDate, endDate);
         if (cashcharged.isEmpty()) {
            sender.sendMessage(this.m.getLang("top_empty"));
         } else {
            HashMap<Integer, String> top = Utils.sortByComparator(cashcharged, false, 10);
            String format = this.m.getLang("top_format");

            for(int t : top.keySet()) {
               sender.sendMessage(MessageFormat.format(format, t, top.get(t), new DecimalFormat("#,###").format(cashcharged.get(top.get(t)))));
            }
         }
      });
   }

   public List<Long> day(Date date) {
      List<Long> dd = new LinkedList<>();
      date.setHours(0);
      date.setMinutes(0);
      date.setSeconds(0);
      dd.add(date.getTime());
      date.setHours(23);
      date.setMinutes(59);
      date.setSeconds(59);
      dd.add(date.getTime());
      return dd;
   }

   public List<Long> month(Date date) {
      List<Long> dd = new LinkedList<>();
      Calendar calendar = getCalendar(date);
      calendar.set(5, calendar.getActualMinimum(5));
      setTimeToBeginningOfDay(calendar);
      Date beginning = calendar.getTime();
      calendar = getCalendar(date);
      calendar.set(5, calendar.getActualMaximum(5));
      setTimeToEndofDay(calendar);
      Date end = calendar.getTime();
      dd.add(this.day(beginning).get(0));
      dd.add(this.day(end).get(1));
      return dd;
   }

   public List<Long> year() {
      List<Long> dd = new LinkedList<>();
      Calendar cal = Calendar.getInstance();
      cal = Calendar.getInstance();
      cal.set(6, 1);
      Date yearStartDate = cal.getTime();
      dd.add(this.day(yearStartDate).get(0));
      cal.set(6, cal.getActualMaximum(6));
      Date yearEndDate = cal.getTime();
      dd.add(this.day(yearEndDate).get(1));
      return dd;
   }

   public static Calendar getCalendar(Date date) {
      Calendar calendar = GregorianCalendar.getInstance();
      calendar.setTime(date);
      return calendar;
   }

   public static void setTimeToBeginningOfDay(Calendar calendar) {
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      calendar.set(14, 0);
   }

   public static void setTimeToEndofDay(Calendar calendar) {
      calendar.set(11, 23);
      calendar.set(12, 59);
      calendar.set(13, 59);
      calendar.set(14, 999);
   }
}
