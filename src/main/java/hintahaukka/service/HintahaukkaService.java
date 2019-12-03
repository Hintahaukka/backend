package hintahaukka.service;

import hintahaukka.domain.*;
import hintahaukka.database.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomStringUtils;

public class HintahaukkaService {
    
    private PriceDao priceDao;
    private ProductDao productDao;
    private StoreDao storeDao;
    private UserDao userDao;
    private StorePointsDao storePointsDao;

    public HintahaukkaService(PriceDao priceDao, ProductDao productDao, StoreDao storeDao, UserDao userDao, StorePointsDao storePointsDao) {
        this.priceDao = priceDao;
        this.productDao = productDao;
        this.storeDao = storeDao;
        this.userDao = userDao;
        this.storePointsDao = storePointsDao;
    }    
    
    /**
     * After scanning a product with the Hintahaukka app, this method implements logic
     * to serve the second http query made by the app.
     * @param ean A string that represents the EAN code of the scanned product.
     * @param cents the price of the scanned product in cents
     * @param storeId A string that represents the store ID of the store where the product was scanned.
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return 
     */
    public Product addThePriceOfGivenProductToDatabase(String ean, int cents, String storeId, String schemaName) {
        Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
        if(product == null) return null;
        
        try{
            // Add the store to the database if it is not there already:
            Store store = storeDao.findOne(storeId, schemaName);            
            if(store == null) {
                store = storeDao.add(storeId, "K-Supermarket Kamppi", schemaName);
            }

            // Delete the old price of the product in the given store from the database if old price exists.
            priceDao.delete(product, store, schemaName);
            
            // Add the new price of the product in the given store to the database.
            priceDao.addWithCurrentTimestamp(product, store, cents, schemaName);   
            
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        return product;
    }
    
    /**
     * After scanning a product with the Hintahaukka app, this method implements logic
     * to serve the first http query made by the app.
     * @param ean A string that represents the EAN code of the scanned product.
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return An InfoAndPrices object that contains the name of the product and a list of the product's prices in different stores.
     */
    public InfoAndPrices priceOfGivenProductInDifferentStores(String ean, String schemaName) {
        Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
        if(product == null) return null;
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            // Get all prices for the product from the database.
            ArrayList<Price> prices = priceDao.findAllForProduct(product, schemaName);

            // For every price of the product, find out from what store the price is from.
            for(Price price : prices) {
                ptuList.add(new PriceTransferUnit(
                        price.getCents(), 
                        storeDao.findOne(price.getStoreId(), schemaName).getGoogleStoreId(),
                        price.getCreated()
                ));
            }            
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        return new InfoAndPrices(product.getEan(), product.getName(), ptuList);
    }
    
    public PricesOfStoresAndPoints pricesOfGivenProductsInDifferentStores(String[] eans, String tokenAndId, String schemaName) {
        HashMap<Integer, HashMap<String, PriceInStore>> stores = new HashMap<>();
        
        HashMap<String, Integer> averagePrices = new HashMap<>();
        
        // A loop which main task is to find the prices of a given product in different stores, and then assort these
        // prices to the corresponfing stores.
        for(String ean : eans) {
            
            Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
            if(product == null) return null;

            ArrayList<Price> prices;
            try{
                // Find the prices of a given product in different stores.
                prices = priceDao.findAllForProduct(product, schemaName);
            } catch(Exception e) {
                System.out.println(e.toString());
                return null;
            }
            
            if(prices.isEmpty()) continue;
            
            int centsTotal = 0;
            
            // Assort prices to the corresponfing stores.
            for(Price price : prices) {
                HashMap<String, PriceInStore> store = stores.getOrDefault(price.getStoreId(), new HashMap<>());
                store.put(product.getEan(), new PriceInStore(product.getEan(), price.getCents(), price.getCreated()));
                stores.put(price.getStoreId(), store);

                centsTotal += price.getCents();
            }

            // Average price for the product is also calculated.
            int averagePrice = centsTotal / prices.size();
            averagePrices.put(ean, averagePrice);
        }
        
        ArrayList<PricesOfStore> storesResult = new ArrayList<>();

        // A loop which main task is to order the prices of a store in correct order and to
        // calculate the total sum of the prices in the store.
        // If a store is missing a price for requested product, the price is replaced with the average price of the product.
        for(Map.Entry<Integer, HashMap<String, PriceInStore>> store : stores.entrySet()) {
            HashMap<String, PriceInStore> pricesInStore = store.getValue();
            
            // Stores have been handled with database row IDs up until now.
            // Here we find out the googleStoreIDs for stores.
            String googleStoreId;
            try{
                googleStoreId = storeDao.findOne(store.getKey(), schemaName).getGoogleStoreId();
            } catch(Exception e) {
                System.out.println(e.toString());
                return null;
            }
            
            int storeCentsTotal = 0;
            ArrayList<PriceInStore> pricesInStoreAL = new ArrayList<>();
            
            // Main task of the loop:
            for(String ean : eans) {
                if(averagePrices.containsKey(ean)){
                    pricesInStoreAL.add(pricesInStore.getOrDefault(ean, new PriceInStore(ean, averagePrices.get(ean), "")));
                    storeCentsTotal += pricesInStoreAL.get(pricesInStoreAL.size() - 1).getCents();
                }
            }
            
            storesResult.add(new PricesOfStore(googleStoreId, storeCentsTotal, pricesInStoreAL));
        }
        
        // The results are ordered according to the total sum of the prices in the store.
        storesResult.sort((pos1, pos2) -> {
            if(pos1.getStoreCentsTotal() < pos2.getStoreCentsTotal()) return -1;
            else if(pos1.getStoreCentsTotal() == pos2.getStoreCentsTotal()) return 0;
            else return 1;
        });
        
        // Price information query consumes user's points.
        User user = this.consumePointsFromUser(tokenAndId, eans.length, schemaName);
        if(user == null) return null;
        
        PricesOfStoresAndPoints resultWithPoints = new PricesOfStoresAndPoints(user.getPointsTotal(), user.getPointsUnused(), storesResult);
        
        return resultWithPoints;
    }
    
    public PointsAndPrices priceOfGivenProductInDifferentStoresWithNoInfo(String ean, String tokenAndId, String schemaName) {
        Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
        if(product == null) return null;
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            // Get all prices for the product from the database.
            ArrayList<Price> prices = priceDao.findAllForProduct(product, schemaName);

            // For every price of the product, find out from what store the price is from.
            for(Price price : prices) {
                ptuList.add(new PriceTransferUnit(
                        price.getCents(), 
                        storeDao.findOne(price.getStoreId(), schemaName).getGoogleStoreId(),
                        price.getCreated()
                ));
            }            
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        User user = this.consumePointsFromUser(tokenAndId, 1, schemaName);
        if(user == null) return null;
        
        PointsAndPrices result = new PointsAndPrices(user.getPointsTotal(), user.getPointsUnused(), ptuList);
        
        return result;
    }
    
    /**
     * When Hintahaukka app is launched for the first time on a phone, this method implements logic
     * to serve the http query which the app made in order to get a unique ID which is later used to identify the user.
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return The unique ID
     */
    public String getNewId(String schemaName) {
        String newId = null;
        
        try{
            User newUser = userDao.add(RandomStringUtils.random(32, true, true), schemaName);
            newId = newUser.getToken() + newUser.getId();
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        return newId;
    }
    
    public Boolean updateNickname(String tokenAndId, String newNickname, String schemaName) {
        int id = Integer.parseInt(tokenAndId.substring(32));
        String token = tokenAndId.substring(0, 32);
        
        Boolean success = null;
        
        try{
            success = userDao.updateNickname(id, token, newNickname, schemaName);
        } catch(Exception e) {
            System.out.println(e.toString());
            return false;
        }
        
        return success;
    }
    
    /**
     * Adds points to the user depending on how long ago a price was previously added for the same product in the same store.
     * @param tokenAndId User id
     * @param newPoints Amount of points to be given to the user.
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return User object with added points
     */
    public User addPointsToUser(String tokenAndId, int newPoints, String schemaName) {
        int id = Integer.parseInt(tokenAndId.substring(32));
        String token = tokenAndId.substring(0, 32);
        
        try {
            User user = userDao.findOne(id, token, schemaName);
            if (user == null) {
                return null;
            }
            int pointsTotal = user.getPointsTotal() + newPoints;
            int pointsUnused = user.getPointsUnused() + newPoints;
            userDao.updatePointsTotal(id, token, pointsTotal, schemaName);
            userDao.updatePointsUnused(id, token, pointsUnused, schemaName);
            return userDao.findOne(id, token, schemaName);
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    /**
     * Consumes points from the user if the user has sufficient amount of unused points.
     * @param tokenAndId User id
     * @param pointsConsumed Amount of unused points to be consumed from the user.
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return User object with updated points, or null if the user didn't have sufficient amount of unused points.
     */
    public User consumePointsFromUser(String tokenAndId, int pointsConsumed, String schemaName) {
        int id = Integer.parseInt(tokenAndId.substring(32));
        String token = tokenAndId.substring(0, 32);
        
        try {
            User user = userDao.findOne(id, token, schemaName);
            if (user == null) {
                return null;
            }
            
            if(user.getPointsUnused() < pointsConsumed) return null;
            
            user.setPointsUnused(user.getPointsUnused() - pointsConsumed);
            
            if(!userDao.updatePointsUnused(id, token, user.getPointsUnused(), schemaName)){
                return null;
            }
            
            return user;
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    /**
     * Counts points based on how far apart the timestamps of two price objects are.
     * @param before The old price
     * @param after The new price
     * @return points deserved
     */
    public static int countPoints(Price before, Price after) {
        if (after == null) {
            return 0;
        }
        // price was never added before
        if (before == null) {
            return 10;
        }
        String dateFirst = before.getCreated();
        String dateLast = after.getCreated();
        long differenceInDays = differenceInDays(dateFirst, dateLast);
        if (differenceInDays >= 90) {
            return 9;
        } else if (differenceInDays >= 20) {
            return (int) differenceInDays / 10;
        } else if (differenceInDays >= 1) {
            return 1;
        } else {
            return 0;
        }
    }
    
    private static long differenceInDays(String dateFirst, String dateLast) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date firstDate = dateFormat.parse(dateFirst);
            Date lastDate = dateFormat.parse(dateLast);
            long diffInMillis = lastDate.getTime() - firstDate.getTime();
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.println(e.toString());
            return 0l;
        }
    }
    
    /**
     * Finds the latest price added for a product in a store.
     * @param ean The EAN code of the product
     * @param storeId The id of the store
     * @param schemaName A string switch that dictates which database is used to serve the query, "public" for production database, "test" for test database.
     * @return The latest price
     */
    public Price latestPrice(String ean, String storeId, String schemaName) {
        Price price;
        try {
            Product product = productDao.findOne(ean, schemaName);
            Store store = storeDao.findOne(storeId, schemaName);
            price = priceDao.findOne(product, store, schemaName);
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        return price;
    }
    
    public Boolean updateProductNameAndAddPoints(String ean, String tokenAndId, String newProductName, String schemaName) {
        Boolean success = null;
        
        try{
            Product product = productDao.findOne(ean, schemaName);
            if(product.getName().equals("")){  // Check that product name is not already added.
                addPointsToUser(tokenAndId, 5, schemaName);
                success = productDao.updateName(ean, newProductName, schemaName);
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return false;
        }
        
        return success;
    }
    
    public ArrayList<NicknameAndStorePoints> getLeaderboardForStore(String storeId, String schemaName) {
        ArrayList<NicknameAndStorePoints> leaderboard = new ArrayList<>();
        
        try{
            Store store = storeDao.findOne(storeId, schemaName);
            if(store == null) return null;
            
            ArrayList<StorePoints> storePointsOfStore = storePointsDao.find10LargestForStore(store, schemaName);
            
            for(StorePoints storePoints : storePointsOfStore) {
                User user = userDao.findOne(storePoints.getUserId(), schemaName);
                if(user == null) return null;
                
                leaderboard.add(new NicknameAndStorePoints(user.getNickname(), storePoints.getPoints()));
            }
            
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        return leaderboard;
    }
        
    Product getProductFromDbAddProductToDbIfNecessary(String ean, String schemaName) {
        Product product = null;
        
        try{
            product = productDao.findOne(ean, schemaName);
            
            // Add the product to the database if it is not there already:
            if(product == null) {
                String productName = "";
                try{
                    if(schemaName.equals("public")) productName = getProductNameFromApi(ean);
                }catch(Exception e){}
                
                product = productDao.add(ean, productName, schemaName);
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
        return product;
    }
    
    static String getProductNameFromApi(String barcode) throws MalformedURLException, IOException {
        String productName;
        String urlString = "https://api.barcodelookup.com/v2/products?barcode=" + barcode + "&formatted=y&key=kcz6mpkh3x2rblgh46b2cpcda9p2xy";
        URL url = new URL (urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();

        if (request.getResponseCode() == 404) {
            return "";
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream()));

        String inputLine;

        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        } reader.close();

        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(response.toString());
        JsonObject jObject = element.getAsJsonObject();
        JsonArray jArray = jObject.getAsJsonArray("products");
        jObject = jArray.get(0).getAsJsonObject();
        productName = jObject.get("product_name").toString();

        return productName;
    }

}
