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

@WebServlet(name = "search", urlPatterns = {"/search"})
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

            String strTagList = "tags: " + StringUtils.join('[', 
				tags.stream().map(tag -> String.format(
                    "{\"id\": \"%d\"" + 
                    ", \"tag_name\": \"%s\"}",
                    tag.id(), tag.name()))
				.collect(Collectors.joining(", ")), ']');

            String strIngrList = "ingrediens: " + StringUtils.join('[', 
				ingr.stream().map(ingredient -> String.format(
                    "{\"id\": \"%d\"" + 
                    ", \"ingr_name\": \"%s\"}",
                    ingredient.id(), ingredient.name()))
				.collect(Collectors.joining(", ")), ']');

            out.print(strTagList + '\n' + strIngrList);
            out.flush();
            

        } catch (NumberFormatException | FTException e) {
            out.println("");
            System.out.println(e.getMessage());
        }
        
        
    }
    
}
