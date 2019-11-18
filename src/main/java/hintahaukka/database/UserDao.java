package hintahaukka.database;

import hintahaukka.domain.*;
import java.sql.SQLException;
import java.net.URISyntaxException;

public class UserDao {

    private Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    public User add(String token, String schemaName) throws URISyntaxException, SQLException {
        return (User) database.executeQueryAndExpectOneResult("INSERT INTO " + schemaName + ".User (token) VALUES (?) RETURNING id", statement -> {
            statement.setString(1, token);       
        }, resultSet -> 
            new User(resultSet.getInt("id"), token, null, 0, 0));
    }

    public User findOne(int id, String token, String schemaName) throws URISyntaxException, SQLException {
        return (User) database.executeQueryAndExpectOneResult("SELECT * FROM " + schemaName + ".User WHERE id = ? AND token = ?", statement -> {
            statement.setInt(1, id);
            statement.setString(2, token);     
        }, resultSet -> 
            new User(resultSet.getInt("id"), resultSet.getString("token"), resultSet.getString("nickname"), resultSet.getInt("pointsTotal"), resultSet.getInt("pointsUnused")));
    }

    public Boolean updatePointsTotal(int id, String token, int newPointsTotal, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("UPDATE " + schemaName + ".User SET pointsTotal = ? WHERE id = ? AND token = ? RETURNING id", statement -> {
            statement.setInt(1, newPointsTotal);
            statement.setInt(2, id);
            statement.setString(3, token);
        }, resultSet -> 
            Boolean.TRUE);
    }

    public Boolean updatePointsUnused(int id, String token, int newPointsUnused, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("UPDATE " + schemaName + ".User SET pointsUnused = ? WHERE id = ? AND token = ? RETURNING id", statement -> {
            statement.setInt(1, newPointsUnused);
            statement.setInt(2, id);
            statement.setString(3, token);
        }, resultSet -> 
            Boolean.TRUE);
    }

    public Boolean updateNickname(int id, String token, String newNickname, String schemaName) throws URISyntaxException, SQLException {
        return (Boolean) database.executeQueryAndExpectOneResult("UPDATE " + schemaName + ".User SET nickname = ? WHERE id = ? AND token = ? RETURNING id", statement -> {
            statement.setString(1, newNickname);
            statement.setInt(2, id);
            statement.setString(3, token);
        }, resultSet -> 
            Boolean.TRUE);
    }

}
