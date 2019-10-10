package hintahaukka.service;

import hintahaukka.database.*;
import hintahaukka.domain.*;
import hintahaukka.service.*;
import java.util.ArrayList;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class HintahaukkaServiceTest {
    
    private static Database database;
    private static HintahaukkaService service;
    
    @BeforeClass
    public static void setUpClass() {
        // Service initialization
        database = new Database();
        PriceDao priceDao = new PriceDao(database);
        ProductDao productDao = new ProductDao(database);
        StoreDao storeDao = new StoreDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao);
    }
    
    @Before
    public void setUp() {
        try{
            database.clearDatabase("test");
            database.initializeDatabaseIfUninitialized("test");
        } catch(Exception e) {
            fail(e.getMessage());
        }        
    }

    @Test
    public void return3pricesForProductFromDifferentStores() {
        InfoAndPrices ptuList = null;
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 120, "2", "test");
            Product product3 = service.addThePriceOfGivenProductToDatabase("1", 130, "3", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product3.getEan(), "test");
        } catch(Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals(3, ptuList.getPrices().size());
    }
    
    @Test
    public void returnPricesOnlyForProductQueried() {
        InfoAndPrices ptuList = null;
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 120, "2", "test");
            Product product3 = service.addThePriceOfGivenProductToDatabase("2", 130, "3", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product2.getEan(), "test");
        } catch(Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals(2, ptuList.getPrices().size());
    }
    
    @Test
    public void returnOnlyLatestPriceForProduct() {
        InfoAndPrices ptuList = null;
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 99, "1", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product2.getEan(), "test");
        } catch(Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals(1, ptuList.getPrices().size());
        assertEquals(99, ptuList.getPrices().get(0).getCents());
    }
    
}
