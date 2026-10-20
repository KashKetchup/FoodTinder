package tomcatIntegration;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import tinderBack.FoodTinder;

public class FTServlet extends HttpServlet {
    protected static FoodTinder foodTinder;

    @Override 
    public void init(ServletConfig config) throws TomcatIntegrationException {
        foodTinder = TomcatIntegration.getFoodTinder(getServletContext());
    }

}
