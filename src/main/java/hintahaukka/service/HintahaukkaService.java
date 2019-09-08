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
    
    public Product addThePriceOfGivenProductToDatabase(PriceTransferUnit ptu) {
        Product product = null;
        
        try{
            // Lisää tuote jos ei jo ole, hanki sen id.
            product = productDao.findOne(ptu.getEan());
            if(product == null) {
                product = productDao.add(ptu.getEan(), "Omena");
            }

            // Lisää kauppa jos ei jo ole, hanki id.
            Store store = storeDao.findOne(ptu.getStoreId());            
            if(store == null) {
                store = storeDao.add(ptu.getStoreId(), "K-Supermarket Kamppi");
            }

            // Poista näillä id:llä vanha hinta jos on, lisää uusi hinta.
            priceDao.delete(product, store);
            priceDao.addWithCurrentTimestamp(product, store, ptu.getCents());   
            
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        
        return product;
    }
    
    public ArrayList<PriceTransferUnit> pricesOfGivenProductInDifferentStores(Product product) throws SQLException {
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        
        try{
            // etsi kaikki hinnat, ja etsi jokaiselle hinnalle kauppa.
            ArrayList<Price> prices = priceDao.findAllForProduct(product);

            for(Price price : prices) {
                ptuList.add(new PriceTransferUnit(
                        product.getEan(), 
                        price.getCents(), 
                        storeDao.findOne(price.getStoreId()).getGoogleStoreId(),
                        price.getCreated()
                ));
            }            
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        
        return ptuList;
    }

}
