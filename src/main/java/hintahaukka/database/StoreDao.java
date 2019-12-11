package hintahaukka.database;

import hintahaukka.domain.Store;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class StoreDao {
    
    private Database database;

    public StoreDao(Database database) {
        this.database = database;
    }
    
    public Store add(String googleStoreId, String name, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "INSERT INTO " + schemaName + ".Store (googleStoreId, name) VALUES (?, ?) RETURNING id", statement -> {
            statement.setString(1, googleStoreId);
            statement.setString(2, name);  
        }, resultSet -> 
            new Store(resultSet.getInt("id"), googleStoreId, name));
    }
    
    public Store findOne(String googleStoreId, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "SELECT * FROM " + schemaName + ".Store WHERE googleStoreId = ?", statement -> {
            statement.setString(1, googleStoreId);     
        }, resultSet -> 
            new Store(resultSet.getInt("id"), resultSet.getString("googleStoreId"), resultSet.getString("name")));
    }
    
    public Store findOne(int id, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "SELECT * FROM " + schemaName + ".Store WHERE id = ?", statement -> {
            statement.setInt(1, id);     
        }, resultSet -> 
            new Store(resultSet.getInt("id"), resultSet.getString("googleStoreId"), resultSet.getString("name")));
    }

}
