package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class StoreDao {
    
    private Database database;

    public StoreDao(Database database) {
        this.database = database;
    }
    
    public Store findOne(String googleStoreId, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();       
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".Store WHERE googleStoreId = ?");
        stmt.setString(1, googleStoreId);

        ResultSet rs = stmt.executeQuery();   
        
        Store store = null;
        if (rs.next()) {
            store = new Store(rs.getInt("id"), rs.getString("googleStoreId"), rs.getString("name"));
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return store;
    }
    
    public Store findOne(int id, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".Store WHERE id = ?");
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        
        Store store = null;
        if (rs.next()) {
            store = new Store(rs.getInt("id"), rs.getString("googleStoreId"), rs.getString("name"));
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return store;
    }
    
    public Store add(String googleStoreId, String name, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + schemaName + ".Store (googleStoreId, name) VALUES (?, ?) RETURNING id");
        stmt.setString(1, googleStoreId);
        stmt.setString(2, name);

        ResultSet rs = stmt.executeQuery();
        
        Store store = null;
        if (rs.next()) {
            store = new Store(rs.getInt("id"), googleStoreId, name);
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return store;
    }

}
