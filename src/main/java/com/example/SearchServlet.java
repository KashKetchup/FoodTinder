package com.example;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tinderBack.FTException;
import tinderBack.Recipe;
import tomcatIntegration.FTServlet;

public class SearchServlet extends FTServlet {
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {

        

        try {
            Integer id = Integer.parseInt(req.getParameter("id")); 
            Recipe recipe = foodTinder.getRecipe(id);

            req.setAttribute("recipe", recipe);

            RequestDispatcher dispatcher = 
                req.getRequestDispatcher("WEB-INF/views/recipePage.jsp");
            dispatcher.forward(req, resp);

        } catch (NumberFormatException | FTException e) {
            //TODO Подумать над реализацией исключений
        }
        
        
    }
    
}
