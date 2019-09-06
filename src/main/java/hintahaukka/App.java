/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hintahaukka;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        
        post("/", (req, res) -> {
            
            int ean = Integer.parseInt(req.queryParams("ean"));
            int cents = Integer.parseInt(req.queryParams("cents"));
            int storeId = Integer.parseInt(req.queryParams("storeId"));
            
            PriceEntry onePrice1 = new PriceEntry(ean, cents, storeId, 123456789);
            PriceEntry onePrice2 = new PriceEntry(ean, cents, storeId, 123456789);
            ArrayList<PriceEntry> listOfPrices = new ArrayList<>();
            listOfPrices.add(onePrice1);
            listOfPrices.add(onePrice2);
            
            Gson gson = new Gson();
            String listOfPricesAsJSON = gson.toJson(listOfPrices);
            
            res.type("application/json");
            return listOfPricesAsJSON;
        });
        
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
