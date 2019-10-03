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
    
    public Product addThePriceOfGivenProductToDatabase(PriceTransferUnit ptu, String schemaName) {
        Product product = null;
        
        try{
            // Add the product to the database if it is not there already:
            product = productDao.findOne(ptu.getEan(), schemaName);
            if(product == null) {
                product = productDao.add(ptu.getEan(), "Omena", schemaName);
            }

            // Add the store to the database if it is not there already:
            Store store = storeDao.findOne(ptu.getStoreId(), schemaName);            
            if(store == null) {
                store = storeDao.add(ptu.getStoreId(), "K-Supermarket Kamppi", schemaName);
            }

            // Delete the old price of the product in the given store from the database if old price exists.
            priceDao.delete(product, store, schemaName);
            
            // Add the new price of the product in the given store to the database.
            priceDao.addWithCurrentTimestamp(product, store, ptu.getCents(), schemaName);   
            
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        
        return product;
    }
    
    public ArrayList<PriceTransferUnit> priceOfGivenProductInDifferentStores(Product product, String schemaName) throws SQLException {
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        
        try{
            // Get all prices for the product from the database.
            ArrayList<Price> prices = priceDao.findAllForProduct(product, schemaName);

            // For every price of the product, find out from what store the price is from.
            for(Price price : prices) {
                ptuList.add(new PriceTransferUnit(
                        product.getEan(), 
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
