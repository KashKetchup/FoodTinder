package tomcatIntegration;

import java.net.URI;

import database.DatabaseService;
import jakarta.servlet.ServletContext;
import tinderBack.FoodTinder;

public class TomcatIntegration {
    public static DatabaseService getDataBase(ServletContext ctx) 
    throws TomcatIntegrationException {
        DatabaseService result = (DatabaseService)ctx.getAttribute("database.DatabaseService");
        if (result == null) {
            try {
                URI uri = new URI(System.getenv("DB_URI"));
                String user = System.getenv("DB_USER");
                String pass = System.getenv("DB_PASS");
                result = new DatabaseService(uri, user, pass);
            } catch (Exception e) {
                throw new TomcatIntegrationException();
            }
            ctx.setAttribute("database.DatabaseService", result);
        }
        
        return result;
    }
    public static FoodTinder getFoodTinder(ServletContext ctx) throws TomcatIntegrationException {
        FoodTinder result = (FoodTinder)ctx.getAttribute("tinderBack.FoodTinder");
        if (result == null) {
            DatabaseService dbs = getDataBase(ctx);
            result = new FoodTinder(dbs);
            ctx.setAttribute("tinderBack.FoodTinder", result);
        }
        
        return result;
    }
}
