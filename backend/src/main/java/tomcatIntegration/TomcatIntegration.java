package tomcatIntegration;

import database.DatabaseService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import tinderBack.FoodTinder;

public class TomcatIntegration {
    public static DatabaseService getDataBase(ServletContext ctx) 
    throws TomcatIntegrationException {
        DatabaseService result = (DatabaseService)ctx.getAttribute("database.DatabaseService");
        if (result == null) {
            try {
            	String url = System.getenv("DB_URL");
                String user = System.getenv("DB_USER");
                String pass = System.getenv("DB_PASS");
                result = new DatabaseService(url, user, pass);
            } catch (Exception e) {
                throw new TomcatIntegrationException(e.getMessage());
            }
            ctx.setAttribute("database.DatabaseService", result);
        }
        
        return result;
    }
    public static FoodTinder getFoodTinder(ServletContext ctx) throws TomcatIntegrationException, ServletException {
        FoodTinder result = (FoodTinder)ctx.getAttribute("tinderBack.FoodTinder");
        if (result == null) {
            DatabaseService dbs = getDataBase(ctx);
            result = new FoodTinder(dbs);
            ctx.setAttribute("tinderBack.FoodTinder", result);
        }
        return result;
    }
}
