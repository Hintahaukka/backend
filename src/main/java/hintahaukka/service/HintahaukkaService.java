package hintahaukka.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hintahaukka.domain.*;
import hintahaukka.database.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class HintahaukkaService {
    
    private PriceDao priceDao;
    private ProductDao productDao;
    private StoreDao storeDao;

    public HintahaukkaService(PriceDao priceDao, ProductDao productDao, StoreDao storeDao) {
        this.priceDao = priceDao;
        this.productDao = productDao;
        this.storeDao = storeDao;
    }    
    
    public Product addThePriceOfGivenProductToDatabase(String ean, int cents, String storeId, String schemaName) {
        Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
        
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
        }
        
        return product;
    }
    
    public InfoAndPrices priceOfGivenProductInDifferentStores(String ean, String schemaName) {
        Product product = getProductFromDbAddProductToDbIfNecessary(ean, schemaName);
        
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
        }
        
        return new InfoAndPrices(product.getEan(), product.getName(), ptuList);
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
