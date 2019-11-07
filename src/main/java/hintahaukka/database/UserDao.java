package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class UserDao {
    
    private Database database;

    public UserDao(Database database) {
        this.database = database;
    }
    
    public User add(String token, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + schemaName + ".User (token) VALUES (?) RETURNING id");
        stmt.setString(1, token);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new User(rs.getInt("id"), token, null, 0, 0);

            rs.close();
            stmt.close();
            conn.close();

            return user;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return null;
        }
    }
    
    public boolean updateNickname(int id, String token, String newNickname, String schemaName) throws URISyntaxException, SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE " + schemaName + ".User SET nickname = ? WHERE id = ? AND token = ? RETURNING id");
        stmt.setString(1, newNickname);
        stmt.setInt(2, id);
        stmt.setString(3, token);

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
