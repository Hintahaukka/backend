package hintahaukka;

import hintahaukka.database.*;
import hintahaukka.domain.*;
import hintahaukka.service.*;
import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.ArrayList;

public class App {
    
    private static HintahaukkaService service;
    
    public static void main(String[] args) {
        
        // Service initialization:
        Database database = new Database();
        try{
            database.initializeDatabaseIfUninitialized("public");
            database.initializeDatabaseIfUninitialized("test");
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        PriceDao priceDao = new PriceDao(database);
        ProductDao productDao = new ProductDao(database);
        StoreDao storeDao = new StoreDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao);
        
        port(getHerokuAssignedPort());
        
        post("/:schemaName", (req, res) -> {
            
            // Extract information from the HTTP POST request:
            String ean = req.queryParams("ean");
            int cents = Integer.parseInt(req.queryParams("cents"));
            String storeId = req.queryParams("storeId");
            PriceTransferUnit ptu = new PriceTransferUnit(ean, cents, storeId, "Timestamp added by database");
            
            String schemaName = req.params(":schemaName");
            if(!schemaName.equals("public") && !schemaName.equals("test")) return "Error!";
            
            // Hintahaukka logic:
            Product product = service.addThePriceOfGivenProductToDatabase(ptu, schemaName);
            ArrayList<PriceTransferUnit> ptuList = service.priceOfGivenProductInDifferentStores(product, schemaName);

            // Build and send HTTP response:
            res.type("application/json");
            String ptuListAsJSON = new Gson().toJson(ptuList);
            return ptuListAsJSON;
        });
        
        get("/reset/:schemaName", (req, res) -> {
            String schemaName = req.params(":schemaName");
            if(!schemaName.equals("public") && !schemaName.equals("test")) return "Error!";
            
            try{
                database.clearDatabase(schemaName);
                database.initializeDatabaseIfUninitialized(schemaName);
            } catch(Exception e) {
                System.out.println(e.toString());
                return "Database reset failed.";
            }
            return "Database reseted.";
        });
        
        get("/wake", (req, res) -> {
            return "Heroku is awake!";
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
