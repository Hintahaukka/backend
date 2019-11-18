package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class ProductDao {
    
    private Database database;

    public ProductDao(Database database) {
        this.database = database;
    }
    
    public Product findOne(String ean, String schemaName) throws URISyntaxException, SQLException {
        return (Product) database.executeQueryAndExpectOneResult("SELECT * FROM " + schemaName + ".Product WHERE ean = ?", statement -> {
            statement.setString(1, ean);
        }, resultSet -> 
            new Product(resultSet.getInt("id"), resultSet.getString("ean"), resultSet.getString("name")));
    }
    
    public Product add(String ean, String name, String schemaName) throws URISyntaxException, SQLException {
        return (Product) database.executeQueryAndExpectOneResult("INSERT INTO " + schemaName + ".Product (ean, name) VALUES (?, ?) RETURNING id", statement -> {
            statement.setString(1, ean);
            statement.setString(2, name);
        }, resultSet -> 
            new Product(resultSet.getInt("id"), ean, name));
    }
    
    public Boolean updateName(String ean, String newName, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("UPDATE " + schemaName + ".Product SET name = ? WHERE ean = ? RETURNING id", statement -> {
            statement.setString(1, newName);
            statement.setString(2, ean);
        }, resultSet -> 
            Boolean.TRUE);
    }

}
