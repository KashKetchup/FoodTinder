package tomcatIntegration;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import tinderBack.FoodTinder;

public class FTServlet extends HttpServlet {
    protected static FoodTinder foodTinder;

    @Override 
    public void init(ServletConfig config) throws TomcatIntegrationException, ServletException {
        super.init(config);
        foodTinder = TomcatIntegration.getFoodTinder(getServletContext());
    }

}
