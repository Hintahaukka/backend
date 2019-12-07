package hintahaukka.database;

import hintahaukka.domain.Store;
import hintahaukka.domain.StorePoints;
import hintahaukka.domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.net.URISyntaxException;

public class StorePointsDao {

    private Database database;

    public StorePointsDao(Database database) {
        this.database = database;
    }
    
    public Boolean add(User user, Store store, int points, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("INSERT INTO " + schemaName + ".StorePoints (user_id, store_id, points) VALUES (?, ?, ?) RETURNING id", statement -> {
            statement.setInt(1, user.getId());
            statement.setInt(2, store.getId());
            statement.setInt(3, points);     
        }, resultSet -> 
            Boolean.TRUE);
    }

    public ArrayList<StorePoints> find10LargestForStore(Store store, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".StorePoints WHERE store_id = ? ORDER BY points DESC LIMIT 10");
        stmt.setInt(1, store.getId());

        ResultSet rs = stmt.executeQuery();

        ArrayList<StorePoints> storePointsOfStore = new ArrayList<>();
        while (rs.next()) {
            StorePoints storePoints = new StorePoints(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("store_id"), rs.getInt("points"));
            storePointsOfStore.add(storePoints);
        }

        rs.close();
        stmt.close();
        conn.close();

        return storePointsOfStore;
    }
    
    public StorePoints findOne(User user, Store store, String schemaName) throws URISyntaxException, SQLException {
        return (StorePoints) database.executeQueryAndExpectOneResult("SELECT * FROM " + schemaName + ".StorePoints WHERE user_id = ? AND store_id = ?", statement -> {
            statement.setInt(1, user.getId());
            statement.setInt(2, store.getId()); 
        }, resultSet -> 
            new StorePoints(resultSet.getInt("id"), resultSet.getInt("user_id"), resultSet.getInt("store_id"), resultSet.getInt("points")));
    }
    
    public Boolean updatePoints(StorePoints storePoints, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("UPDATE " + schemaName + ".StorePoints SET points = ? WHERE id = ? RETURNING id", statement -> {
            statement.setInt(1, storePoints.getPoints());
            statement.setInt(2, storePoints.getId());
        }, resultSet -> 
            Boolean.TRUE);
    }

}
