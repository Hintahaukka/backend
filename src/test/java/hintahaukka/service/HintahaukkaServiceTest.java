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
            database.clearDatabase();
            database.initializeDatabaseIfUninitialized();
        } catch(Exception e) {
            fail("Some database operations failed while initialization.");
        }        
    }

    @Test
    public void return3pricesForProductFromDifferentStores() {
        PriceTransferUnit ptu1 = new PriceTransferUnit("1", 110, "1", "Timestamp added by database");
        PriceTransferUnit ptu2 = new PriceTransferUnit("1", 120, "2", "Timestamp added by database");
        PriceTransferUnit ptu3 = new PriceTransferUnit("1", 130, "3", "Timestamp added by database");
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase(ptu1);
            Product product2 = service.addThePriceOfGivenProductToDatabase(ptu2);
            Product product3 = service.addThePriceOfGivenProductToDatabase(ptu3);
            ptuList = service.priceOfGivenProductInDifferentStores(product3);
        } catch(Exception e) {
            fail("Some database operations failed.");
        }
        
        assertEquals(3, ptuList.size());
    }
    
    @Test
    public void returnPricesOnlyForProductQueried() {
        PriceTransferUnit ptu1 = new PriceTransferUnit("1", 110, "1", "Timestamp added by database");
        PriceTransferUnit ptu2 = new PriceTransferUnit("1", 120, "2", "Timestamp added by database");
        PriceTransferUnit ptu3 = new PriceTransferUnit("2", 130, "3", "Timestamp added by database");
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase(ptu1);
            Product product2 = service.addThePriceOfGivenProductToDatabase(ptu2);
            Product product3 = service.addThePriceOfGivenProductToDatabase(ptu3);
            ptuList = service.priceOfGivenProductInDifferentStores(product2);
        } catch(Exception e) {
            fail("Some database operations failed.");
        }
        
        assertEquals(2, ptuList.size());
    }
    
    @Test
    public void returnOnlyLatestPriceForProduct() {
        PriceTransferUnit ptu1 = new PriceTransferUnit("1", 110, "1", "Timestamp added by database");
        PriceTransferUnit ptu2 = new PriceTransferUnit("1", 99, "1", "Timestamp added by database");
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            Product product1 = service.addThePriceOfGivenProductToDatabase(ptu1);
            Product product2 = service.addThePriceOfGivenProductToDatabase(ptu2);
            ptuList = service.priceOfGivenProductInDifferentStores(product2);
        } catch(Exception e) {
            fail("Some database operations failed.");
        }
        
        assertEquals(1, ptuList.size());
        assertEquals(99, ptuList.get(0).getCents());
    }
    
}
