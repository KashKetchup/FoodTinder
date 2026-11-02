package com.example;

import java.io.IOException;

import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tinderBack.FTException;
import tinderBack.FoodTag;
import tinderBack.Ingredient;
import tomcatIntegration.FTServlet;

@WebServlet(name = "search", urlPatterns = {"/search"})
public class SearchServlet extends FTServlet {
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {

        

        try {
            List<FoodTag> tags = (List<FoodTag>)foodTinder.getAllTags();
            List<Ingredient> ingr = (List<Ingredient>)foodTinder.getAllIngredients(); 

            req.setAttribute("tags", tags);
            req.setAttribute("ingr", ingr);

            RequestDispatcher dispatcher = 
                req.getRequestDispatcher("WEB-INF/views/searchPage.jsp");
            dispatcher.forward(req, resp);

        } catch (NumberFormatException | FTException e) {
            //TODO Подумать над реализацией исключений
        }
        
        
    }
    
}
