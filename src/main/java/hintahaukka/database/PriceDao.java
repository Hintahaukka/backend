package hintahaukka.database;

import hintahaukka.domain.Price;
import hintahaukka.domain.Product;
import hintahaukka.domain.Store;
import java.util.ArrayList;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class PriceDao {

    private Database database;

    public PriceDao(Database database) {
        this.database = database;
    }
    
    public Boolean addWithCurrentTimestamp(Product product, Store store, int cents, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "INSERT INTO " + schemaName + ".Price (product_id, store_id, cents) VALUES (?, ?, ?) RETURNING id", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());
            statement.setInt(3, cents);     
        }, resultSet -> 
            Boolean.TRUE);
    }

    public Boolean delete(Product product, Store store, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "DELETE FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ? RETURNING id", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());   
        }, resultSet -> 
            Boolean.TRUE);
    }
    
    public Price findOne(Product product, Store store, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectOneResult(
                "SELECT * FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ?", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());        
        }, resultSet -> 
            new Price(resultSet.getInt("id"), resultSet.getInt("product_id"), resultSet.getInt("store_id"), resultSet.getInt("cents"), resultSet.getTimestamp("created").toString()));
    }
    
    public ArrayList<Price> findAllForProduct(Product product, String schemaName) throws URISyntaxException, SQLException {
        return database.executeQueryAndExpectMultipleResults(
                "SELECT * FROM " + schemaName + ".Price WHERE product_id = ?", statement -> {
            statement.setInt(1, product.getId());
        }, resultSet -> 
            new Price(resultSet.getInt("id"), resultSet.getInt("product_id"), resultSet.getInt("store_id"), resultSet.getInt("cents"), resultSet.getTimestamp("created").toString()));
    }

}
