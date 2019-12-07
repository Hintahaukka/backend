package hintahaukka;

import static hintahaukka.Validators.*;
import hintahaukka.database.*;
import hintahaukka.domain.bundles.NicknameAndPoints;
import hintahaukka.domain.bundles.PointsAndPrices;
import hintahaukka.domain.bundles.InfoAndPrices;
import hintahaukka.domain.bundles.PointsAndPricesOfStores;
import hintahaukka.domain.User;
import hintahaukka.service.HintahaukkaService;
import java.util.ArrayList;
import static spark.Spark.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

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
        
        get("/getNewId", (req, res) -> {
            return getNewIdWithGivenSchema("public", req, res);
        });
        
        post("/updateNickname", (req, res) -> {
            return updateNicknameToGivenSchema("public", req, res);
        });
        
        post("/updateProductName", (req, res) -> {
            return updateProductNameToGivenSchema("public", req, res);
        });
        
        post("/getPricesForManyProducts", (req, res) -> {
            return getPricesForManyProductsFromGivenSchema("public", req, res);
        });
        
        post("/getPricesForOneProduct", (req, res) -> {
            return getPricesForOneProductFromGivenSchema("public", req, res);
        });
      
        post("/getLeaderboardForStore", (req, res) -> {
            return getLeaderboardForStoreFromGivenSchema("public", req, res);
        });
      
        get("/getLeaderboard", (req, res) -> {
            return getLeaderboardFromGivenSchema("public", req, res);
        });
        
        
        post("/test/getInfoAndPrices", (req, res) -> {
            return getInfoAndPricesFromGivenSchema("test", req, res);
        });
        
        post("/test/addPrice", (req, res) -> {
            return addPriceToGivenSchema("test", req, res);
        });
        
        get("/test/getNewId", (req, res) -> {
            return getNewIdWithGivenSchema("test", req, res);
        });
        
        post("/test/updateNickname", (req, res) -> {
            return updateNicknameToGivenSchema("test", req, res);
        });
        
        post("/test/updateProductName", (req, res) -> {
            return updateProductNameToGivenSchema("test", req, res);
        });
        
        post("/test/getPricesForManyProducts", (req, res) -> {
            return getPricesForManyProductsFromGivenSchema("test", req, res);
        });
        
        post("/test/getPricesForOneProduct", (req, res) -> {
            return getPricesForOneProductFromGivenSchema("test", req, res);
        });
        
        post("/test/getLeaderboardForStore", (req, res) -> {
            return getLeaderboardForStoreFromGivenSchema("test", req, res);
        });
      
        get("/test/getLeaderboard", (req, res) -> {
            return getLeaderboardFromGivenSchema("test", req, res);
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
        PriceDao priceDao = new PriceDao(database);
        ProductDao productDao = new ProductDao(database);
        StoreDao storeDao = new StoreDao(database);
        UserDao userDao = new UserDao(database);
        StorePointsDao storePointsDao = new StorePointsDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao, userDao, storePointsDao);        
    }
    
    static String getInfoAndPricesFromGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!eanOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String ean = req.queryParams("ean");

        // Hintahaukka logic:
        InfoAndPrices infoAndPrices = service.priceOfGivenProductInDifferentStoresAndProductInfo(ean, schemaName);

        // Build and send HTTP response:
        if(infoAndPrices == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(infoAndPrices);
        return ptuListAsJSON;
    }
    
    static String addPriceToGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!eanCentsStoreIdIdOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String ean = req.queryParams("ean");
        int cents = Integer.parseInt(req.queryParams("cents"));
        String storeId = req.queryParams("storeId");
        String tokenAndId = req.queryParams("id");
        
        // Hintahaukka logic:
        User user = service.addThePriceOfGivenProductToDatabase(ean, cents, storeId, tokenAndId, schemaName);
        
        // Build and send HTTP response:
        if(user == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        return "" + user.getPointsTotal() + ":" + user.getPointsUnused();
    }
    
    static String getNewIdWithGivenSchema(String schemaName, Request req, Response res) {
        // Hintahaukka logic:
        String newId = service.getNewId(schemaName);

        // Build and send HTTP response:
        if(newId == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        return newId;
    } 
    
    static String updateNicknameToGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!IdNicknameOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String tokenAndId = req.queryParams("id");
        String newNickname = req.queryParams("nickname");

        // Hintahaukka logic:
        Boolean success = service.updateNickname(tokenAndId, newNickname, schemaName);

        // Build and send HTTP response:
        if(success == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        return "success";
    } 
    
    static String updateProductNameToGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!eanIdProductNameOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String ean = req.queryParams("ean");
        String tokenAndId = req.queryParams("id");
        String newProductName = req.queryParams("productName");

        // Hintahaukka logic:
        Boolean success = service.updateProductNameAndAddPoints(ean, tokenAndId, newProductName, schemaName);

        // Build and send HTTP response:
        if(success == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        return "success";
    }
    
    static String getPricesForManyProductsFromGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!IdEansOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String tokenAndId = req.queryParams("id");
        String[] eans = new String[req.queryParams().size() - 1];
        int i = 1;
        while(i < req.queryParams().size()) {  
            eans[i - 1] = req.queryParams("ean" + i);
            ++i;
        }
        
        // Hintahaukka logic:
        PointsAndPricesOfStores result = service.pricesOfGivenProductsInDifferentStoresAndUserPoints(eans, tokenAndId, schemaName);

        // Build and send HTTP response:
        if(result == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(result);
        return ptuListAsJSON;
    }
    
    static String getPricesForOneProductFromGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!IdEanOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String tokenAndId = req.queryParams("id");
        String ean = req.queryParams("ean");

        // Hintahaukka logic:
        PointsAndPrices result = service.priceOfGivenProductInDifferentStoresAndUserPoints(ean, tokenAndId, schemaName);

        // Build and send HTTP response:
        if(result == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(result);
        return ptuListAsJSON;
    }
    
    static String getLeaderboardForStoreFromGivenSchema(String schemaName, Request req, Response res) {
        // Input validation:
        if(!storeIdOk(req)) {
            res.status(400);
            return "Input error!";
        }
        
        // Extract information from the HTTP POST request:
        String storeId = req.queryParams("storeId");
        
        // Hintahaukka logic:
        ArrayList<NicknameAndPoints> leaderboard = service.getLeaderboardForStore(storeId, schemaName);

        // Build and send HTTP response:
        if(leaderboard == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(leaderboard);
        return ptuListAsJSON;
    }
  
    static String getLeaderboardFromGivenSchema(String schemaName, Request req, Response res) {
        // Hintahaukka logic:
        ArrayList<NicknameAndPoints> result = service.getLeaderboard(schemaName);

        // Build and send HTTP response:
        if(result == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(result);
        return ptuListAsJSON;
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    
}
