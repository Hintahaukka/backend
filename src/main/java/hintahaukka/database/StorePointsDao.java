package hintahaukka.database;

import hintahaukka.domain.Store;
import hintahaukka.domain.StorePoints;
import hintahaukka.domain.User;
import java.util.ArrayList;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class StorePointsDao {

    private Database database;

    public StorePointsDao(Database database) {
        this.database = database;
    }
    
    public Boolean add(User user, Store store, int points, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "INSERT INTO " + schemaName + ".StorePoints (user_id, store_id, points) VALUES (?, ?, ?) RETURNING id", statement -> {
            statement.setInt(1, user.getId());
            statement.setInt(2, store.getId());
            statement.setInt(3, points);     
        }, resultSet -> 
            Boolean.TRUE);
    }
    
    public StorePoints findOne(User user, Store store, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "SELECT * FROM " + schemaName + ".StorePoints WHERE user_id = ? AND store_id = ?", statement -> {
            statement.setInt(1, user.getId());
            statement.setInt(2, store.getId()); 
        }, resultSet -> 
            new StorePoints(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getInt("store_id"), resultSet.getInt("points")));
    }
    
    public ArrayList<StorePoints> find10LargestForStore(Store store, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectMultipleResults(
                "SELECT * FROM " + schemaName + ".StorePoints WHERE store_id = ? ORDER BY points DESC LIMIT 10", statement -> {
            statement.setInt(1, store.getId());
        }, resultSet -> 
            new StorePoints(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getInt("store_id"), resultSet.getInt("points")));
    }
    
    public Boolean updatePoints(StorePoints storePoints, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "UPDATE " + schemaName + ".StorePoints SET points = ? WHERE id = ? RETURNING id", statement -> {
            statement.setInt(1, storePoints.getPoints());
            statement.setInt(2, storePoints.getId());
        }, resultSet -> 
            Boolean.TRUE);
    }

}
