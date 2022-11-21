package net.thesieutoc.placeholder;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.utils.Task;
import net.thesieutoc.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TSTPlaceholder extends PlaceholderExpansion {
   Thesieutoc m;
   HashMap<String, Map<String, Integer>> cache = new HashMap<>();
   HashMap<String, Long> cache_gay = new HashMap<>();

   public TSTPlaceholder(Thesieutoc m) {
      this.m = m;
      Task.asyncTask(() -> {
         for(Object placeholder : new HashSet<>(this.cache_gay.keySet())) {
            if (System.currentTimeMillis() - this.cache_gay.get(placeholder) >= 60000L) {
               this.cache.remove(placeholder);
               this.cache_gay.remove(placeholder);
            }
         }
      }, 0, 1200);
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   @NotNull
   public String getAuthor() {
      return this.m.getDescription().getAuthors().toString();
   }

   @NotNull
   public String getIdentifier() {
      return "tst";
   }

   @NotNull
   public String getVersion() {
      return this.m.getDescription().getVersion();
   }

   public String onPlaceholderRequest(Player p, @NotNull String placeholder) {
      if (p == null) {
         return "";
      } else {
         placeholder = placeholder.toLowerCase();
         DecimalFormat value_format = new DecimalFormat(this.m.getLang("placeholder.value_format"));
         String[] args = placeholder.split("_");
         Date date = new Date();
         String type = args[0];
         String supcua = !type.equals("top") && !type.equals("total") ? p.getName() : "";
         long startDate = 0L;
         long endDate = 0L;
         int index = 0;
         if (args.length > 1) {
            String lums = args[1];
            switch(lums) {
               case "today":
                  startDate = this.m.cmd_tst.day(date).get(0) / 1000L;
                  endDate = this.m.cmd_tst.day(date).get(1) / 1000L;
                  break;
               case "month":
                  startDate = this.m.cmd_tst.month(date).get(0) / 1000L;
                  endDate = this.m.cmd_tst.month(date).get(1) / 1000L;
                  break;
               case "year":
                  startDate = this.m.cmd_tst.year().get(0) / 1000L;
                  endDate = this.m.cmd_tst.year().get(1) / 1000L;
            }

            try {
               index = Integer.parseInt(args[args.length - 1]);
            } catch (Exception var20) {
            }
         }

         long lums = this.cache_gay.getOrDefault(placeholder, 0L);
         if (lums < System.currentTimeMillis()) {
            this.cache.put(placeholder, this.m.tsttop.get(supcua, startDate, endDate));
            this.cache_gay.put(placeholder, System.currentTimeMillis() + 3000L);
         }

         Map<String, Integer> cashcharged = this.cache.getOrDefault(placeholder, this.m.tsttop.get(supcua, startDate, endDate));
         this.cache.put(placeholder, cashcharged);
         if (type.equals("top")) {
            if (index == 0) {
               return "Placeholder thiếu giá trị hạng #";
            } else {
               HashMap<Integer, String> dx = Utils.sortByComparator(cashcharged, false, index);
               String wow = dx.getOrDefault(index, "");
               int cursed = cashcharged.getOrDefault(wow, 0);
               String placeholder_top_format = wow.isEmpty() ? this.m.getLang("placeholder.top_empty") : this.m.getLang("placeholder.top");
               return MessageFormat.format(placeholder_top_format, index, wow, value_format.format((long)cursed));
            }
         } else {
            int total = 0;

            for(String key : cashcharged.keySet()) {
               total += cashcharged.get(key);
            }

            String placeholder_format = this.m.getLang("placeholder." + type);
            return MessageFormat.format(placeholder_format, total);
         }
      }
   }
}
