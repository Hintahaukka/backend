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
    
    public ArrayList<Price> findAllForProduct(Product product) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Price WHERE product_id = ?");
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
    
    public void addWithCurrentTimestamp(Product product, Store store, int cents) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Price (product_id, store_id, cents) VALUES (?, ?, ?)");
        stmt.setInt(1, product.getId());
        stmt.setInt(2, store.getId());
        stmt.setInt(3, cents);

        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }
    
    public void delete(Product product, Store store) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Price WHERE product_id = ? AND store_id = ?");
        stmt.setInt(1, product.getId());
        stmt.setInt(2, store.getId());
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }

}
