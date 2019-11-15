package hintahaukka.service;

import hintahaukka.database.*;
import hintahaukka.domain.*;
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
        UserDao userDao = new UserDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao, userDao);
    }

    @Before
    public void setUp() {
        try {
            database.clearDatabase("test");
            database.initializeDatabaseIfUninitialized("test");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void return3pricesForProductFromDifferentStores() {
        InfoAndPrices ptuList = null;
        try {
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 120, "2", "test");
            Product product3 = service.addThePriceOfGivenProductToDatabase("1", 130, "3", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product3.getEan(), "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(3, ptuList.getPrices().size());
    }

    @Test
    public void returnPricesOnlyForProductQueried() {
        InfoAndPrices ptuList = null;
        try {
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 120, "2", "test");
            Product product3 = service.addThePriceOfGivenProductToDatabase("2", 130, "3", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product2.getEan(), "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(2, ptuList.getPrices().size());
    }

    @Test
    public void returnOnlyLatestPriceForProduct() {
        InfoAndPrices ptuList = null;
        try {
            Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
            Product product2 = service.addThePriceOfGivenProductToDatabase("1", 99, "1", "test");
            ptuList = service.priceOfGivenProductInDifferentStores(product2.getEan(), "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(1, ptuList.getPrices().size());
        assertEquals(99, ptuList.getPrices().get(0).getCents());
    }

    @Test
    public void returnProperIds() {
        String id1 = service.getNewId("test");
        String id2 = service.getNewId("test");

        assertEquals(33, id1.length());
        assertEquals(33, id2.length());

        assertEquals(1, Integer.parseInt(id1.substring(32)));
        assertEquals(2, Integer.parseInt(id2.substring(32)));
    }

    @Test
    public void nicknameIsUpdated() {
        String tokenAndId = service.getNewId("test");

        assertTrue(service.updateNickname(tokenAndId, "Haukka", "test"));
    }

    @Test
    public void noPointsAreAddedToUserIfPriceForTheSameProductInTheSameStoreWasAlreadyAddedToday() {
        String tokenAndId = service.getNewId("test");
        Product oldProduct = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
        Price priceBefore = service.latestPrice("1", "1", "test");
        Product product2 = service.addThePriceOfGivenProductToDatabase("1", 120, "2", "test");
        Price priceAfter = service.latestPrice("1", "1", "test");
        User user = service.addPointsToUser(tokenAndId, priceBefore, priceAfter, false, "test");
        assertTrue(user.getPointsTotal() == 0);
        assertTrue(user.getPointsUnused() == 0);
    }

    @Test
    public void pointsAreAddedToUserIfPriceForTheSameProductInTheSameStoreWasNeverAddedBefore() {
        String tokenAndId = service.getNewId("test");
        Price priceBefore = service.latestPrice("1", "1", "test");
        Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
        Price priceAfter = service.latestPrice("1", "1", "test");
        User user = service.addPointsToUser(tokenAndId, priceBefore, priceAfter, false, "test");
        assertFalse(user.getPointsTotal() == 0);
        assertFalse(user.getPointsUnused() == 0);
    }
    
    @Test
    public void comparingTwoTimestampsReturnsCorrectAmountOfPoints() {
        Price before = new Price(1, 1, 1, 110, "2019-10-07 19:48:56.9918");
        Price after = new Price(1, 1, 1, 120, "2019-10-08 20:01:12.066");
        assertTrue(service.countPoints(before, after) == 1);
        
        after = new Price(1, 1, 1, 120, "2019-10-28 20:01:12.066");
        assertTrue(service.countPoints(before, after) == 2);
        
        after = new Price(1, 1, 1, 120, "2019-11-10 20:01:12.066");
        assertTrue(service.countPoints(before, after) == 3);
        
        after = new Price(1, 1, 1, 120, "2020-03-01 20:01:12.066");
        assertTrue(service.countPoints(before, after) == 9);
    }
    
    @Test
    public void productNameIsUpdated() {
        service.addThePriceOfGivenProductToDatabase("1", 100, "1", "test");
        service.updateProductName("1", "Fazer Sininen", "test");
        String productName = service.getProductFromDbAddProductToDbIfNecessary("1", "test").getName();
        assertEquals("Fazer Sininen", productName);
    }
    
    @Test
    public void productNamePointsAreGiven() {
        String tokenAndId = service.getNewId("test");
        Price priceBefore = service.latestPrice("1", "1", "test");
        Product product1 = service.addThePriceOfGivenProductToDatabase("1", 110, "1", "test");
        Price priceAfter = service.latestPrice("1", "1", "test");
        User user = service.addPointsToUser(tokenAndId, priceBefore, priceAfter, true, "test");
        assertTrue(user.getPointsTotal() == 15);
        assertTrue(user.getPointsUnused() == 15);
    }
}
