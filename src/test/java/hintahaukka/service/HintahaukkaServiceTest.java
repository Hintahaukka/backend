package hintahaukka.service;

import hintahaukka.domain.bundles.InfoAndPrices;
import hintahaukka.domain.bundles.NicknameAndPoints;
import hintahaukka.domain.bundles.PointsAndPrices;
import hintahaukka.domain.bundles.PriceTransferUnit;
import hintahaukka.domain.bundles.PricesOfStore;
import hintahaukka.domain.bundles.PointsAndPricesOfStores;
import hintahaukka.database.*;
import hintahaukka.domain.*;
import java.util.ArrayList;
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
        StorePointsDao storePointsDao = new StorePointsDao(database);
        service = new HintahaukkaService(priceDao, productDao, storeDao, userDao, storePointsDao);
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
            String tokenAndId = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 110, "1", tokenAndId, "test");
            service.addThePriceOfGivenProductToDatabase("1", 120, "2", tokenAndId, "test");
            service.addThePriceOfGivenProductToDatabase("1", 130, "3", tokenAndId, "test");
            ptuList = service.priceOfGivenProductInDifferentStores("1", "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(3, ptuList.getPrices().size());
    }

    @Test
    public void returnPricesOnlyForProductQueried() {
        InfoAndPrices ptuList = null;
        try {
            String tokenAndId = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 110, "1", tokenAndId, "test");
            service.addThePriceOfGivenProductToDatabase("1", 120, "2", tokenAndId, "test");
            service.addThePriceOfGivenProductToDatabase("2", 130, "3", tokenAndId, "test");
            ptuList = service.priceOfGivenProductInDifferentStores("1", "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(2, ptuList.getPrices().size());
    }

    @Test
    public void returnOnlyLatestPriceForProduct() {
        InfoAndPrices ptuList = null;
        try {
            String tokenAndId = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 110, "1", tokenAndId, "test");
            service.addThePriceOfGivenProductToDatabase("1", 99, "1", tokenAndId, "test");
            ptuList = service.priceOfGivenProductInDifferentStores("1", "test");
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
        User first = service.addThePriceOfGivenProductToDatabase("1", 110, "1", tokenAndId, "test");
        User second = service.addThePriceOfGivenProductToDatabase("1", 120, "1", tokenAndId, "test");
        assertTrue(first.getPointsTotal() == second.getPointsTotal());
        assertTrue(first.getPointsUnused() == second.getPointsUnused());
    }

    @Test
    public void pointsAreAddedToUserIfPriceForTheSameProductInTheSameStoreWasNeverAddedBefore() {
        String tokenAndId = service.getNewId("test");
        User user = service.addThePriceOfGivenProductToDatabase("1", 110, "1", tokenAndId, "test");
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
    public void productNameIsUpdatedAndPointsAreGiven() {
        String tokenAndId = service.getNewId("test");
        User before = service.addThePriceOfGivenProductToDatabase("1", 100, "1", tokenAndId, "test");
        
        service.updateProductNameAndAddPoints("1", tokenAndId, "Fazer Sininen", "test");
        
        String productName = service.getProductFromDbAddProductToDbIfNecessary("1", "test").getName();
        assertEquals("Fazer Sininen", productName);
        
        User after = service.addPointsToUser(tokenAndId, 0, "test");
        assertTrue(after.getPointsTotal() - before.getPointsTotal() == 5);
        assertTrue(after.getPointsUnused() - before.getPointsUnused() == 5);
    }
    
    @Test
    public void return4pricesFromCorrectStoresAndConsumePoints() {
        String tokenAndId = service.getNewId("test");
        service.addPointsToUser(tokenAndId, 10, "test");
        
        PointsAndPricesOfStores result = null;
        ArrayList<PricesOfStore> storesResult = null;
        try {
            String tokenAndIdOfAdder = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 100, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 200, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 300, "2", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("3", 300, "2", tokenAndIdOfAdder, "test");
            result = service.pricesOfGivenProductsInDifferentStores(new String[]{"1","2","3","4"}, tokenAndId, "test");
            storesResult = result.getPricesOfStores();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals("1", storesResult.get(0).getGoogleStoreId());
        assertEquals("2", storesResult.get(1).getGoogleStoreId());
        assertEquals(600, storesResult.get(0).getStoreCentsTotal());
        assertEquals(700, storesResult.get(1).getStoreCentsTotal());
        
        assertEquals(3, storesResult.get(0).getPricesInStore().size());
        assertEquals(3, storesResult.get(1).getPricesInStore().size());
        
        assertEquals(100, storesResult.get(0).getPricesInStore().get(0).getCents());
        assertEquals(200, storesResult.get(0).getPricesInStore().get(1).getCents());
        assertEquals(300, storesResult.get(0).getPricesInStore().get(2).getCents());
        
        assertEquals(100, storesResult.get(1).getPricesInStore().get(0).getCents());
        assertEquals(300, storesResult.get(1).getPricesInStore().get(1).getCents());
        assertEquals(300, storesResult.get(1).getPricesInStore().get(2).getCents());
        
        assertEquals("1", storesResult.get(0).getPricesInStore().get(0).getEan());
        assertEquals("2", storesResult.get(0).getPricesInStore().get(1).getEan());
        assertEquals("3", storesResult.get(0).getPricesInStore().get(2).getEan());
        
        assertEquals("1", storesResult.get(1).getPricesInStore().get(0).getEan());
        assertEquals("2", storesResult.get(1).getPricesInStore().get(1).getEan());
        assertEquals("3", storesResult.get(1).getPricesInStore().get(2).getEan());
        
        assertTrue(!storesResult.get(0).getPricesInStore().get(0).getTimestamp().equals(""));
        assertTrue(!storesResult.get(0).getPricesInStore().get(1).getTimestamp().equals(""));
        assertTrue(storesResult.get(0).getPricesInStore().get(2).getTimestamp().equals(""));
        
        assertTrue(storesResult.get(1).getPricesInStore().get(0).getTimestamp().equals(""));
        assertTrue(!storesResult.get(1).getPricesInStore().get(1).getTimestamp().equals(""));
        assertTrue(!storesResult.get(1).getPricesInStore().get(2).getTimestamp().equals(""));
        
        // User has correct points after the query:
        assertEquals(10, result.getPointsTotal());
        assertEquals(6, result.getPointsUnused());
    }
    
    @Test
    public void returnNullIfUserDoesNotHaveEnoughPointsForPriceQuery() {
        String tokenAndId = service.getNewId("test");
        service.addPointsToUser(tokenAndId, 3, "test");
        
        PointsAndPricesOfStores result = null;
        try {
            String tokenAndIdOfAdder = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 100, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 200, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 300, "2", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("3", 300, "2", tokenAndIdOfAdder, "test");
            result = service.pricesOfGivenProductsInDifferentStores(new String[]{"1","2","3","4"}, tokenAndId, "test");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        assertNull(result);
    }
    
    @Test
    public void return2pricesFromCorrectStoresAndConsumePoints() {
        String tokenAndId = service.getNewId("test");
        service.addPointsToUser(tokenAndId, 10, "test");
        
        PointsAndPrices result = null;
        ArrayList<PriceTransferUnit> prices = null;
        try {
            String tokenAndIdOfAdder = service.getNewId("test");
            service.addThePriceOfGivenProductToDatabase("1", 100, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 200, "1", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("2", 300, "2", tokenAndIdOfAdder, "test");
            service.addThePriceOfGivenProductToDatabase("3", 300, "2", tokenAndIdOfAdder, "test");
            result = service.priceOfGivenProductInDifferentStoresWithNoInfo("2", tokenAndId, "test");
            prices = result.getPrices();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals(2, prices.size());
        
        // User has correct points after the query:
        assertEquals(10, result.getPointsTotal());
        assertEquals(9, result.getPointsUnused());
    }
    
    @Test
    public void StorePointsAreAddedToUserAndLeaderboardIsCorrect(){
        // Add few users:
        String tokenAndId1 = service.getNewId("test");
        service.updateNickname(tokenAndId1, "user1", "test");
        String tokenAndId2 = service.getNewId("test");
        service.updateNickname(tokenAndId2, "user2", "test");
        String tokenAndId3 = service.getNewId("test");
        service.updateNickname(tokenAndId3, "user3", "test");
        
        String storeId = "1";
        // Add a random price to the store so that the store is added to the database at the same time.
        service.addThePriceOfGivenProductToDatabase("12345678", 100, storeId, tokenAndId1, "test");
        
        // Users get points:
        
        User user = service.addPointsToUser(tokenAndId1, 9, "test");
        service.addStorePointsToUser(user, storeId, 9, "test");
        
        user = service.addPointsToUser(tokenAndId2, 9, "test");
        service.addStorePointsToUser(user, storeId, 9, "test");
        
        user = service.addPointsToUser(tokenAndId3, 8, "test");
        service.addStorePointsToUser(user, storeId, 8, "test");
        
        user = service.addPointsToUser(tokenAndId1, 1, "test");
        service.addStorePointsToUser(user, storeId, 1, "test");
        
        // Leaderboard is correct:
        ArrayList<NicknameAndPoints> leaderboard = service.getLeaderboardForStore(storeId, "test");
        assertEquals(3, leaderboard.size());
        assertEquals("user1", leaderboard.get(0).getNickname());
        assertEquals("user2", leaderboard.get(1).getNickname());
        assertEquals("user3", leaderboard.get(2).getNickname());
        assertEquals(20, leaderboard.get(0).getPoints());
        assertEquals(9, leaderboard.get(1).getPoints());
        assertEquals(8, leaderboard.get(2).getPoints());
    }
  
    @Test
    public void globalLeaderboardIsCorrect(){
        // Add few users:
        String tokenAndId1 = service.getNewId("test");
        service.updateNickname(tokenAndId1, "user1", "test");
        String tokenAndId2 = service.getNewId("test");
        service.updateNickname(tokenAndId2, "user2", "test");
        String tokenAndId3 = service.getNewId("test");
        service.updateNickname(tokenAndId3, "user3", "test");

        service.addPointsToUser(tokenAndId1, 100, "test");
        service.addPointsToUser(tokenAndId2, 20, "test");
        service.addPointsToUser(tokenAndId3, 3, "test");

        // Leaderboard is correct:
        ArrayList<NicknameAndPoints> leaderboard = service.getLeaderboard("test");
        assertEquals(3, leaderboard.size());
        assertEquals("user1", leaderboard.get(0).getNickname());
        assertEquals("user2", leaderboard.get(1).getNickname());
        assertEquals("user3", leaderboard.get(2).getNickname());
        assertEquals(100, leaderboard.get(0).getPoints());
        assertEquals(20, leaderboard.get(1).getPoints());
        assertEquals(3, leaderboard.get(2).getPoints());
    }
}
