package hintahaukka.database;

import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest {
    
    @Test
    public void databaseInitializationAndClearing() {
        Database database = new Database();
        try{
            database.clearDatabase("public");
            database.initializeDatabaseIfUninitialized("public");
            
            assertEquals("Database initialization failed.", 
                database.createTableStatements("public").length, 
                database.countTables("public"));
            
            database.clearDatabase("public");
            
            assertEquals("Database clearing failed.", 
                0, 
                database.countTables("public"));
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
}
