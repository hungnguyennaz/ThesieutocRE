package net.thesieutoc.card;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.stream.Collectors;
import net.thesieutoc.Thesieutoc;
import org.bukkit.entity.Player;

public class WebRequest {
   Thesieutoc m;

   public WebRequest(Thesieutoc m) {
      this.m = m;
   }

   public void send(Player p) {
      JsonObject json = this.m.REQUESTS.containsKey(p.getName()) ? this.m.REQUESTS.get(p.getName()) : new JsonObject();
      if (json.has("cardtype") && json.has("cardprice") && json.has("seri") && json.has("pin")) {
         JsonObject response = this.m
            .WEB_REQUEST
            .sendCard(
               this.m.getConfig().getString("TheSieuToc-API.key"),
               this.m.getConfig().getString("TheSieuToc-API.secret"),
               json.get("cardtype").getAsString(),
               json.get("cardprice").getAsInt(),
               json.get("seri").getAsString(),
               json.get("pin").getAsString()
            );
         this.m.REQUESTS.remove(p.getName());
         if (response == null) {
            p.sendMessage(this.m.getLang("nap_the_that_bai") + "§c | Không thể kết nối đến cổng nạp thẻ!");
         } else if (!response.get("status").getAsString().equals("00")) {
            p.sendMessage(this.m.getLang("nap_the_that_bai"));
            p.sendMessage("§c" + response.get("msg").getAsString());
         } else {
            p.sendMessage(this.m.getLang("nap_the_dang_xu_ly"));
            String transactionID = response.get("transaction_id").getAsString();
            this.m.WEB_CALLBACK.addQueue(p, transactionID, json);
         }
      } else {
         String reason = "";
         if (!json.has("cardtype")) {
            reason = reason + " | Thiếu thông tin loại thẻ";
         }

         if (!json.has("cardprice")) {
            reason = reason + " | Thiếu thông tin mệnh giá";
         }

         if (!json.has("seri")) {
            reason = reason + " | Chưa nhập Seri";
         }

         if (!json.has("pin")) {
            reason = reason + " | Chưa nhập mã thẻ";
         }

         p.sendMessage(this.m.getLang("nap_the_that_bai") + "§c" + reason);
      }
   }

   public JsonObject sendCard(String apiKey, String apiSecret, String cardType, int cardprice, String seri, String pin) {
      String url = MessageFormat.format(
         "https://thesieutoc.net/API/transaction?APIkey={0}&APIsecret={1}&mathe={2}&seri={3}&type={4}&menhgia={5}",
         apiKey,
         apiSecret,
         pin,
         seri,
         cardType,
         CardPrice.getPrice(cardprice).getId()
      );
      url = url.replace("\"", "");
      return this.sendRequest(url);
   }

   public JsonObject checkCard(String apiKey, String apiSecret, String transactionID) {
      String url = MessageFormat.format(
         "https://thesieutoc.net/API/get_status_card.php?APIkey={0}&APIsecret={1}&transaction_id={2}", apiKey, apiSecret, transactionID
      );
      return this.sendRequest(url);
   }

   public JsonObject sendRequest(String url) {
      try {
         CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
         HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
         connection.setRequestMethod("GET");
         connection.setRequestProperty("User-Agent", "Mozilla/5.0");
         connection.setDoInput(true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String response = reader.lines().collect(Collectors.joining());
         return (JsonObject)new JsonParser().parse(response);
      } catch (SocketTimeoutException var5) {
         var5.printStackTrace();
         return null;
      } catch (Exception var61) {
         return null;
      }
   }
}
