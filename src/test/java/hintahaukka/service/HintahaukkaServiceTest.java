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
    public void returning3pricesForProductFromDifferentStores() {
        PriceTransferUnit ptu1 = new PriceTransferUnit("1", 110, "1", "Timestamp added by database");
        PriceTransferUnit ptu2 = new PriceTransferUnit("1", 120, "2", "Timestamp added by database");
        PriceTransferUnit ptu3 = new PriceTransferUnit("1", 130, "3", "Timestamp added by database");
        
        ArrayList<PriceTransferUnit> ptuList = new ArrayList<>();
        try{
            Product product = service.addThePriceOfGivenProductToDatabase(ptu1);
            product = service.addThePriceOfGivenProductToDatabase(ptu2);
            product = service.addThePriceOfGivenProductToDatabase(ptu3);
            ptuList = service.pricesOfGivenProductInDifferentStores(product);
        } catch(Exception e) {
            fail("Some database operations failed.");
        }
        
        assertEquals("Database clearing failed.", 
                3, 
                ptuList.size());
    }
    
}
