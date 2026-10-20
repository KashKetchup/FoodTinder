package com.example;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tomcatIntegration.*;

public class RecipePageServlet extends FTServlet{
    
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("FOFF");
        String recipeId = req.getParameter("id");
        try {
            Integer intRecipeId = Integer.parseInt(recipeId);
            
            out.println(intRecipeId);
        } catch (NumberFormatException nfe) {}

        
    }

}
