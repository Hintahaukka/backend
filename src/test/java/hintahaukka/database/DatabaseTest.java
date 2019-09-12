package hintahaukka.database;

import hintahaukka.database.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest {
    
    @Test
    public void databaseInitializationAndClearing() {
        Database database = new Database();
        try{
            database.clearDatabase();
            database.initializeDatabaseIfUninitialized();
            
            assertEquals("Database initialization failed.", 
                Database.CREATE_TABLE_STATEMENTS.length, 
                database.countTables());
            
            database.clearDatabase();
            
            assertEquals("Database clearing failed.", 
                0, 
                database.countTables());
        } catch(Exception e) {
            fail("Some database operations failed while initialization and reset");
        }
    }
    
}
