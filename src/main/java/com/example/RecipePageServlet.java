package com.example;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RecipePageServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
        String recipeId = request.getParameter("id");
        try {
            Integer intRecipeId = Integer.parseInt(recipeId);
        } catch (NumberFormatException nfe) {}

        
    }

}
