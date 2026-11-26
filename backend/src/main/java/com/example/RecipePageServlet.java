package com.example;

import tinderBack.FTException;
import tinderBack.Recipe;

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

        //Настройка для работы с React

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        //Отправляем запрос к БД и парсим ответ, затем отправляем на React

        PrintWriter out = resp.getWriter();

        try {
            Integer id = Integer.parseInt(req.getParameter("id"));
            Recipe recipe = foodTinder.getRecipe(id);
            String jsonOut;
            if (recipe == null) {
            	jsonOut = "{}";
            } else {
            	jsonOut = String.format(
	                "{\"id\": \"%d\"" + 
	                ", \"title\": \"%s\"" + 
	                ", \"description\": \"%s\"" + 
	                ", \"instruction\": \"%s\"" +
	                ", \"image_url\": \"%s\"" +
	                ", \"cookingTime\": \"%d\"" +
	                ", \"rate\": \"%d\"" +
                    ", \"calories\": \"%f\"" +
                    ", \"proteins\": \"%f\"" +
                    ", \"fats\": \"%f\"" +
                    ", \"carbs\": \"%f\"" +
	                ", \"difficulty\": \"%d\"" +
                    ", \"instruction\": \"%s\"}",
	                recipe.recipeId(), recipe.name(), recipe.description(),
	                recipe.instruction(), recipe.imagePath(),
	                recipe.cookingTime(), recipe.rate(), 
	                recipe.calories(), recipe.proteins(),
	                recipe.fats(), recipe.carbs(), recipe.difficulty(),
	                recipe.instruction());
            }
            out.print(jsonOut);
            out.flush();

        } catch (FTException e) {
            System.out.println("\nError while accessing to FoodTinder:\n" + e.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println("\nUnable to determine ID:\n" + nfe.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        }
        
        
    }

}



        // //Настройка для работы с React

        // resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        // resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
        // resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // resp.setContentType("application/json");
        // resp.setCharacterEncoding("UTF-8");