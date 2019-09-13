package hintahaukka.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Database {
    
    static final String[] CREATE_TABLE_STATEMENTS = {
        "CREATE TABLE Product ("
            + "id SERIAL PRIMARY KEY, "
            + "ean TEXT, "
            + "name TEXT)"
            ,
        "CREATE TABLE Store ("
            + "id SERIAL PRIMARY KEY, "
            + "googleStoreId TEXT, "
            + "name TEXT)"
            ,
        "CREATE TABLE Price ("
            + "id SERIAL PRIMARY KEY, "
            + "product_id INTEGER NOT NULL, "
            + "store_id INTEGER NOT NULL, "
            + "cents INTEGER, "
            + "created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(product_id) REFERENCES Product (id), "
            + "FOREIGN KEY(store_id) REFERENCES Store (id))"
    };
    
    static final String[] DROP_TABLE_STATEMENTS = {
        "DROP TABLE Price",
        "DROP TABLE Product",
        "DROP TABLE Store"
    };
    
    
    public Database () {
    }
    
    public Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        System.out.println("USERINFO: " + dbUri.getUserInfo());
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
    
    public void initializeDatabaseIfUninitialized() throws URISyntaxException, SQLException {
        if (countTables() != CREATE_TABLE_STATEMENTS.length) {
            executeStatements(CREATE_TABLE_STATEMENTS);
        }
    }
    
    public void clearDatabase() throws URISyntaxException, SQLException {
        if (countTables() == CREATE_TABLE_STATEMENTS.length) {
            executeStatements(DROP_TABLE_STATEMENTS);
        }        
    }    
    
    
    int countTables() throws URISyntaxException, SQLException {
        Connection connection = this.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT count(*) FROM pg_stat_user_tables"
        );
        ResultSet result = statement.executeQuery();
        
        int tableCount = 0;
        if (result.next()) {
            tableCount = result.getInt(1);
        }
        
        result.close();
        statement.close();
        connection.close();
        
        return tableCount;
    }
    
    void executeStatements(String[] statements) throws URISyntaxException, SQLException {
        Connection connection = this.getConnection();

        for(String statement : statements) {
            PreparedStatement executableStatement = connection.prepareStatement(statement);
            executableStatement.executeUpdate();
            executableStatement.close();
        }

        connection.close();        
    }

}
