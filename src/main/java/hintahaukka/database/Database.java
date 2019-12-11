package hintahaukka.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Database {
    
    public Database () {
    }
    
    /**
     * Establishes and gives a connection to the database.
     * Implementation of the method specific to Heroku.
     * @return The connection to the database.
     * @throws URISyntaxException
     * @throws SQLException 
     */
    public Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
    
    public void initializeDatabaseIfUninitialized(String schemaName) throws URISyntaxException, SQLException {
        String[] statements = createTableStatements(schemaName);
        if (countTables(schemaName) != statements.length) {
            executeStatements(new String[]{"DROP SCHEMA IF EXISTS " + schemaName + " CASCADE"});
            executeStatements(new String[]{"CREATE SCHEMA " + schemaName});
            executeStatements(statements);
        }
    }
    
    public void clearDatabase(String schemaName) throws URISyntaxException, SQLException {
        executeStatements(new String[]{"DROP SCHEMA IF EXISTS " + schemaName + " CASCADE"});     
    }
    
    <T> T executeQueryAndExpectOneResult(String statement, PreparedStatementHandler prepHandler, ResultSetHandler<T> resHandler) throws URISyntaxException, SQLException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(statement);
        prepHandler.handlePreparedStatement(stmt);
        
        ResultSet rs = stmt.executeQuery();

        T object = null;
        if (rs.next()) {
            object = resHandler.handleResultSet(rs);
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return object;
    }
    
    <T> ArrayList<T> executeQueryAndExpectMultipleResults(String statement, PreparedStatementHandler prepHandler, ResultSetHandler<T> resHandler) throws URISyntaxException, SQLException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(statement);
        prepHandler.handlePreparedStatement(stmt);

        ResultSet rs = stmt.executeQuery();

        ArrayList<T> objects = new ArrayList<>();
        while (rs.next()) {
            T object = resHandler.handleResultSet(rs);
            objects.add(object);
        }

        rs.close();
        stmt.close();
        conn.close();

        return objects;
    }
    
    interface PreparedStatementHandler {
        public void handlePreparedStatement(PreparedStatement statement) throws SQLException; 
    }
    
    interface ResultSetHandler<T> {
        public T handleResultSet(ResultSet rs) throws SQLException; 
    }
    
    int countTables(String schemaName) throws URISyntaxException, SQLException {
        Connection connection = this.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT count(*) FROM pg_stat_user_tables WHERE schemaname = '" + schemaName + "'"
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
    
    String[] createTableStatements(String schemaName) {
        return new String[]{
            "CREATE TABLE " + schemaName + ".Product ("
                + "id SERIAL PRIMARY KEY, "
                + "ean TEXT, "
                + "name TEXT)"
                ,
            "CREATE TABLE " + schemaName + ".Store ("
                + "id SERIAL PRIMARY KEY, "
                + "googleStoreId TEXT, "
                + "name TEXT)"
                ,
            "CREATE TABLE " + schemaName + ".Price ("
                + "id SERIAL PRIMARY KEY, "
                + "product_id INTEGER NOT NULL, "
                + "store_id INTEGER NOT NULL, "
                + "cents INTEGER, "
                + "created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(product_id) REFERENCES " + schemaName + ".Product (id), "
                + "FOREIGN KEY(store_id) REFERENCES " + schemaName + ".Store (id))"
                ,
            "CREATE TABLE " + schemaName + ".User ("
                + "id SERIAL PRIMARY KEY, "
                + "token TEXT, "
                + "nickname TEXT, "
                + "pointsTotal INTEGER DEFAULT 0, "
                + "pointsUnused INTEGER DEFAULT 0)"
                ,
            "CREATE TABLE " + schemaName + ".StorePoints ("
                + "id SERIAL PRIMARY KEY, "
                + "user_id INTEGER NOT NULL, "
                + "store_id INTEGER NOT NULL, "
                + "points INTEGER DEFAULT 0, "
                + "FOREIGN KEY(user_id) REFERENCES " + schemaName + ".User (id), "
                + "FOREIGN KEY(store_id) REFERENCES " + schemaName + ".Store (id))"
        };
    }

}
