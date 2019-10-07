package hintahaukka.service;

import hintahaukka.domain.*;
import hintahaukka.database.*;
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
        Product product = null;
        
        try{
            // Add the product to the database if it is not there already:
            product = productDao.findOne(ean, schemaName);
            if(product == null) {
                product = productDao.add(ean, "Omena", schemaName);
            }

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
    
    public ArrayList<PriceTransferUnit> priceOfGivenProductInDifferentStores(String ean, String schemaName) {
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        
        try{
            Product product = productDao.findOne(ean, schemaName);
            
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
        
        return ptuList;
    }

}
