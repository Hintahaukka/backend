/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hintahaukka;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        
        get("/", (req, res) -> "Hello World");
        
    }
}
