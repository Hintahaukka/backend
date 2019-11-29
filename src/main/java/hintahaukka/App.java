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
        UserDao userDao = new UserDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao, userDao);        
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
        InfoAndPrices infoAndPrices = service.priceOfGivenProductInDifferentStores(ean, schemaName);

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
        Price priceBefore = service.latestPrice(ean, storeId, schemaName);
        Product product = service.addThePriceOfGivenProductToDatabase(ean, cents, storeId, schemaName);
        Price priceAfter = service.latestPrice(ean, storeId, schemaName);
        User user = service.addPointsToUser(tokenAndId, HintahaukkaService.countPoints(priceBefore, priceAfter), schemaName);

        // Build and send HTTP response:
        if(product == null || user == null) {  // Error response.
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
        String[] eans = new String[req.queryParams().size()];
        int i = 1;
        while(i < req.queryParams().size()) {  // One of the queryParams is id, so the amount of eans is size() - 1.
            eans[i] = req.queryParams("ean" + i);
            ++i;
        }
        
        // Hintahaukka logic:
        ArrayList<PricesOfStore> storesResult = service.pricesOfGivenProductsInDifferentStores(eans, schemaName);
        User user = null;
        if(storesResult != null) user = service.consumePointsFromUser(tokenAndId, eans.length, schemaName);
        PricesOfStoresAndPoints resultWithPoints = null;
        if(user != null) resultWithPoints = new PricesOfStoresAndPoints(user.getPointsTotal(), user.getPointsUnused(), storesResult);

        // Build and send HTTP response:
        if(storesResult == null ||  user == null) {  // Error response.
            res.status(500);
            return "Server error!";
        }
        res.type("application/json");
        String ptuListAsJSON = new Gson().toJson(resultWithPoints);
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
        ArrayList<PriceTransferUnit> ptuList = service.priceOfGivenProductInDifferentStoresWithNoInfo(ean, schemaName);
        User user = null;
        if(ptuList != null) user = service.consumePointsFromUser(tokenAndId, 1, schemaName);
        PointsAndPrices result = null;
        if(user != null) result = new PointsAndPrices(user.getPointsTotal(), user.getPointsUnused(), ptuList);

        // Build and send HTTP response:
        if(ptuList == null ||  user == null) {  // Error response.
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
    
    
    // Input validators:
    
    static boolean eanOk(Request req){
        if(req.queryParams().size() != 1) {
            return false;
        }
        if(!req.queryParams().contains("ean")) {
            return false;
        }
        if(req.queryParamsValues("ean").length != 1) {
            return false;
        }
        if(req.queryParams("ean").length() < 8) {
            return false;
        }
        return true;
    }
    
    static boolean eanCentsStoreIdIdOk(Request req){
        if(req.queryParams().size() != 4 && req.queryParams().size() != 5) {
            return false;
        }
        
        if(!req.queryParams().contains("ean") || !req.queryParams().contains("cents") || !req.queryParams().contains("storeId") || !req.queryParams().contains("id")) {
            return false;
        }
        if(req.queryParams().size() == 5 && !req.queryParams().contains("productName")){
            return false;
        }
        
        if(req.queryParamsValues("ean").length != 1 || req.queryParamsValues("cents").length != 1 || req.queryParamsValues("storeId").length != 1 || req.queryParamsValues("id").length != 1) {
            return false;
        }
        if(req.queryParams().size() == 5 && req.queryParamsValues("productName").length != 1){
            return false;
        }
        
        if(req.queryParams("ean").length() < 8 || req.queryParams("storeId").length() < 1 || req.queryParams("id").length() < 33) {
            return false;
        }
        if(req.queryParams().size() == 5 && req.queryParams("productName").length() > 150){
            return false;
        }
        
        // Cents value check.
        int cents = 0;
        try{
            cents = Integer.parseInt(req.queryParams("cents"));
        }catch(Exception e){
            return false;
        }
        if(cents < 0) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean IdNicknameOk(Request req){
        if(req.queryParams().size() != 2) {
            return false;
        }
        if(!req.queryParams().contains("id") || !req.queryParams().contains("nickname")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1 || req.queryParamsValues("nickname").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33 || req.queryParams("nickname").length() < 2 || req.queryParams("nickname").length() > 20) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean eanIdProductNameOk(Request req){
        if(req.queryParams().size() != 3) {
            return false;
        }
        
        if(!req.queryParams().contains("ean") || !req.queryParams().contains("id") || !req.queryParams().contains("productName")) {
            return false;
        }
        if(req.queryParamsValues("ean").length != 1 || req.queryParamsValues("id").length != 1 || req.queryParamsValues("productName").length != 1) {
            return false;
        }
        if(req.queryParams("ean").length() < 8 || req.queryParams("id").length() < 33 || req.queryParams("productName").length() > 150 || req.queryParams("productName").length() < 2) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean IdEansOk(Request req){
        if(req.queryParams().size() < 2) {
            return false;
        }
        
        if(!req.queryParams().contains("id")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        int i = 1;
        while(i < req.queryParams().size()) {
            if(!req.queryParams().contains("ean" + i)) {
                return false;
            }
            if(req.queryParamsValues("ean" + i).length != 1) {
                return false;
            }
            if(req.queryParams("ean" + i).length() < 8) {
                return false;
            }            
            
            ++i;
        }
        
        return true;
    }
    
    static boolean IdEanOk(Request req){
        if(req.queryParams().size() != 2) {
            return false;
        }
        
        if(!req.queryParams().contains("id") || !req.queryParams().contains("ean")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1 || req.queryParamsValues("ean").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33 || req.queryParams("ean").length() < 8) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
}
