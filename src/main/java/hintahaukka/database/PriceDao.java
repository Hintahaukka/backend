package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.net.URISyntaxException;

public class PriceDao {

    private Database database;

    public PriceDao(Database database) {
        this.database = database;
    }

    public ArrayList<Price> findAllForProduct(Product product, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".Price WHERE product_id = ?");
        stmt.setInt(1, product.getId());

        ResultSet rs = stmt.executeQuery();

        ArrayList<Price> prices = new ArrayList<>();
        while (rs.next()) {
            Price price = new Price(rs.getInt("id"), rs.getInt("product_id"), rs.getInt("store_id"), rs.getInt("cents"), rs.getTimestamp("created").toString());
            prices.add(price);
        }

        rs.close();
        stmt.close();
        conn.close();

        return prices;
    }
    
    public Price findOne(Product product, Store store, String schemaName) throws URISyntaxException, SQLException {
        return (Price) database.executeQueryAndExpectOneResult("SELECT * FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ?", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());        
        }, resultSet -> 
            new Price(resultSet.getInt("id"), resultSet.getInt("product_id"), resultSet.getInt("store_id"), resultSet.getInt("cents"), resultSet.getTimestamp("created").toString()));
    }
    
    public Boolean addWithCurrentTimestamp(Product product, Store store, int cents, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("INSERT INTO " + schemaName + ".Price (product_id, store_id, cents) VALUES (?, ?, ?) RETURNING id", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());
            statement.setInt(3, cents);     
        }, resultSet -> 
            Boolean.TRUE);
    }

    public Boolean delete(Product product, Store store, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("DELETE FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ? RETURNING id", statement -> {
            statement.setInt(1, product.getId());
            statement.setInt(2, store.getId());   
        }, resultSet -> 
            Boolean.TRUE);
    }

}
