package hintahaukka;

import hintahaukka.database.*;
import hintahaukka.domain.*;
import hintahaukka.service.*;
import static spark.Spark.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import java.util.ArrayList;

public class App {
    
    private static Database database;
    private static HintahaukkaService service;
    
    public static void main(String[] args) {
        serviceInitialization();
        port(getHerokuAssignedPort());
        
        post("/getInfoAndPrices", (req, res) -> {
            return getInfoAndPricesFromGivenSchema("public", req, res);
        });
        
        post("/addPrice", (req, res) -> {
            return addPriceToGivenSchema("public", req, res);
        });
        
        post("/test/getInfoAndPrices", (req, res) -> {
            return getInfoAndPricesFromGivenSchema("test", req, res);
        });
        
        post("/test/addPrice", (req, res) -> {
            return addPriceToGivenSchema("test", req, res);
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
    
    static void serviceInitialization() {
        // Service initialization:
        database = new Database();
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
    }
    
    static String getInfoAndPricesFromGivenSchema(String schemaName, Request req, Response res) {
        // Extract information from the HTTP POST request:
        String ean = req.queryParams("ean");

        // Hintahaukka logic:
        ArrayList<PriceTransferUnit> ptuList = service.priceOfGivenProductInDifferentStores(ean, schemaName);
        
        InfoAndPrices infoAndPrices = new InfoAndPrices(ean, "Omena", ptuList);

        // Build and send HTTP response:
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(infoAndPrices);
        return ptuListAsJSON;
    }
    
    static String addPriceToGivenSchema(String schemaName, Request req, Response res) {
        // Extract information from the HTTP POST request:
        String ean = req.queryParams("ean");
        int cents = Integer.parseInt(req.queryParams("cents"));
        String storeId = req.queryParams("storeId");

        // Hintahaukka logic:
        Product product = service.addThePriceOfGivenProductToDatabase(ean, cents, storeId, schemaName);

        // Build and send HTTP response:
        return "success";
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
