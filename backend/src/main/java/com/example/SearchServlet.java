package com.example;

import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tinderBack.FTException;
import tinderBack.FoodTag;
import tinderBack.Ingredient;

import tomcatIntegration.FTServlet;

@WebServlet(name = "search", urlPatterns = {"/backend/search"})
public class SearchServlet extends FTServlet {
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        try {
            List<FoodTag> tags = (List<FoodTag>)foodTinder.getAllTags();
            List<Ingredient> ingr = (List<Ingredient>)foodTinder.getAllIngredients(); 

            String strTagList = "\"tags\": " + StringUtils.join('[', 
				tags.stream().map(tag -> String.format(
                    "{\"id\": \"%d\"" + 
                    ", \"title\": \"%s\"}",
                    tag.id(), tag.name()))
				.collect(Collectors.joining(", ")), ']');

            String strIngrList = "\"ingredients\": " + StringUtils.join('[', 
				ingr.stream().map(ingredient -> String.format(
                    "{\"id\": \"%d\"" + 
                    ", \"title\": \"%s\"}",
                    ingredient.id(), ingredient.name()))
				.collect(Collectors.joining(", ")), ']');

            out.print("{\"state_flag\": \"1\"," + strTagList + ", " + strIngrList + "}");
            out.flush();
            

        } catch (FTException e) {
            System.out.println("\nSearchServlet: Error while accessing to FoodTinder:\n" + e.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println("\nSearchServlet: Unable to determine ID:\n" + nfe.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        } finally {
            out.close();
        }
        
        
    }
    
}
