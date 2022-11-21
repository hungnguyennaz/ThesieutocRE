package net.thesieutoc.card;

import com.google.gson.JsonObject;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.thesieutoc.Thesieutoc;
import net.thesieutoc.event.PlayerCardChargedEvent;
import net.thesieutoc.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WebCallback {
   public HashMap<String, HashMap<String, JsonObject>> queue = new HashMap<>();
   Thesieutoc m;

   public WebCallback(Thesieutoc m) {
      this.m = m;
      Task.asyncTask(
         () -> {
            for(String name : this.queue.keySet()) {
               Player p = Bukkit.getPlayer(name);
               if (p != null && p.isOnline()) {
                  HashMap<String, JsonObject> card_queue = this.queue.get(name);
                  Iterator<String> transid_ite = card_queue.keySet().iterator();
                  if (transid_ite.hasNext()) {
                     String trans_id = transid_ite.next();
                     JsonObject card_response = m.WEB_REQUEST
                        .checkCard(m.getConfig().getString("TheSieuToc-API.key"), m.getConfig().getString("TheSieuToc-API.secret"), trans_id);
                     JsonObject cardinfo = card_queue.get(trans_id);
                     if (card_response.get("status").getAsString().equals("-9")) {
                        int retry = cardinfo.has("retry") ? cardinfo.get("retry").getAsInt() : 0;
                        if (retry > 100) {
                           card_queue.remove(trans_id);
                        } else {
                           cardinfo.addProperty("retry", retry + 1);
                           card_queue.put(trans_id, cardinfo);
                        }
                     } else {
                        if (card_response.get("status").getAsString().equals("-10")) {
                           p.sendMessage(m.getLang("nap_the_that_bai"));
                           cardinfo.addProperty("msg", "that bai");
                        }
   
                        if (card_response.get("status").getAsString().equals("10")) {
                           p.sendMessage(m.getLang("sai_menh_gia"));
                           cardinfo.addProperty("msg", "sai menh gia");
                        }
   
                        if (card_response.get("status").getAsString().equals("00")) {
                           try {
                              int cardprice = cardinfo.get("cardprice").getAsInt();
                              p.sendMessage(MessageFormat.format(m.getLang("nap_the_thanh_cong"), cardprice));
                              this.napthanhcong(p, cardinfo);
                           } catch (Exception var111) {
                              var111.printStackTrace();
                              Bukkit.getLogger()
                                 .warning("§a[Thesieutoc] §cCo loi xay ra khi duyet the (thieu gia tri the), vui long bao cao cho staff TheSieuToc");
                              Bukkit.getLogger().warning("§a[Thesieutoc] §c" + p.getName() + cardinfo.toString());
                           }
                        }
   
                        card_queue.remove(trans_id);
                        Thesieutoc.getInstance().db.writeLog(p, cardinfo);
                     }
                  }
               }
            }
         },
         0,
         100
      );
   }

   public void addQueue(Player p, String trans_id, JsonObject cardinfo) {
      HashMap<String, JsonObject> card_queue = this.queue.containsKey(p.getName()) ? this.queue.get(p.getName()) : new HashMap<>();
      card_queue.put(trans_id, cardinfo);
      this.queue.put(p.getName(), card_queue);
   }

   public void napthanhcong(Player p, JsonObject cardinfo) {
      int cardprice = cardinfo.get("cardprice").getAsInt();
      List<String> commands = (List<String>)(this.m.getConfig().contains("card.command." + cardprice)
         ? this.m.getConfig().getStringList("card.command." + cardprice)
         : new LinkedList<>());
      if (commands.isEmpty()) {
         Bukkit.getLogger().warning("§a[Thesieutoc] §cKhong co lenh thuc thi cho card menh gia §f" + cardprice + "§c VND trong config, vui long kiem tra!");
      } else {
         for(String command : commands) {
            Thesieutoc.getInstance().dispatchCommand(p, command);
         }
      }

      cardinfo.addProperty("msg", "thanh cong");
      int total_charged = Thesieutoc.getInstance().db.getPlayerTotalCharged(p) + cardprice;
      Task.syncTask(() -> new PlayerCardChargedEvent(p, cardinfo.get("cardtype").getAsString(), cardprice, total_charged));
   }
}
