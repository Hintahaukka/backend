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
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ?");
        stmt.setInt(1, product.getId());
        stmt.setInt(2, store.getId());
        
        ResultSet rs = stmt.executeQuery();
        
        Price price = null;
        if (rs.next()) {
            price = new Price(rs.getInt("id"), rs.getInt("product_id"), rs.getInt("store_id"), rs.getInt("cents"), rs.getTimestamp("created").toString());
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return price;
    }
    
    public boolean addWithCurrentTimestamp(Product product, Store store, int cents, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + schemaName + ".Price (product_id, store_id, cents) VALUES (?, ?, ?) RETURNING id");
        stmt.setInt(1, product.getId());
        stmt.setInt(2, store.getId());
        stmt.setInt(3, cents);

        ResultSet rs = stmt.executeQuery();
        
        boolean inserted = false;
        if (rs.next()) inserted = true;
        
        rs.close();
        stmt.close();
        conn.close();
        
        return inserted;
    }

    public boolean delete(Product product, Store store, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + schemaName + ".Price WHERE product_id = ? AND store_id = ? RETURNING id");
        stmt.setInt(1, product.getId());
        stmt.setInt(2, store.getId());

        ResultSet rs = stmt.executeQuery();
        
        boolean deleted = false;
        if (rs.next()) deleted = true;
        
        rs.close();
        stmt.close();
        conn.close();
        
        return deleted;
    }

}
