package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class ProductDao {
    
    private Database database;

    public ProductDao(Database database) {
        this.database = database;
    }
    
    public Product findOne(String ean, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + schemaName + ".Product WHERE ean = ?");
        stmt.setString(1, ean);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Product product = new Product(rs.getInt("id"), rs.getString("ean"), rs.getString("name"));

            rs.close();
            stmt.close();
            conn.close();

            return product;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return null;
        }
    }
    
    public Product add(String ean, String name, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();           
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + schemaName + ".Product (ean, name) VALUES (?, ?) RETURNING id");      
        stmt.setString(1, ean);
        stmt.setString(2, name);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Product product = new Product(rs.getInt("id"), ean, name);

            rs.close();
            stmt.close();
            conn.close();

            return product;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return null;
        }
    }
    
    public boolean updateName(String ean, String newName, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE " + schemaName + ".Product SET name = ? WHERE ean = ? RETURNING id");
        stmt.setString(1, newName);
        stmt.setString(2, ean);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            rs.close();
            stmt.close();
            conn.close();

            return true;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return false;
        }
    }

}
