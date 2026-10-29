package com.example;

import tinderBack.FTException;
import tinderBack.Recipe;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tomcatIntegration.*;

public class RecipeServlet extends FTServlet{
    
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {

        //Настройка для работы с React

        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        //Отправляем запрос к БД и парсим ответ, затем отправляем на React

        PrintWriter out = resp.getWriter();

        try {
            
            Set<Integer> includeIngredient = parseNumbers(req.getParameter("in_ingr"));
            Set<Integer> includeTag = parseNumbers(req.getParameter("in_tag"));
            Set<Integer> excludeIngredient = parseNumbers(req.getParameter("ex_ingr"));
            Set<Integer> excludeTag = parseNumbers(req.getParameter("ex_tag"));

            List<Recipe> recipes = foodTinder.findRecomendRecipes(includeTag, excludeTag,
                includeIngredient, excludeIngredient);

            for (Recipe recipe : recipes) {
                String jsonOut = String.format(
                    "{\"id\": \"%d\"" + 
                    ", \"title\": \"%s\"" + 
                    ", \"description\": \"%s\"" + 
                    ", \"instruction\": \"%s\"" +
                    ", \"image_url\": \"%s\"" +
                    ", \"cookingTime\": \"%d\"" +
                    ", \"rate\": \"%d\"" +
                    ", \"calories\": \"%f\"" +
                    ", \"difficulty\": \"%d\"}",
                    recipe.recipeId(), recipe.name(), recipe.description(),
                    recipe.instruction(), recipe.imagePath(),
                    recipe.cookingTime(), recipe.rate(), 
                    recipe.calories(), recipe.difficulty()
                );

                out.print(jsonOut);
                //Либо собрать в массив и отправить разом
            }

        } catch (NumberFormatException | FTException e) {
            //TODO Подумать над реализацией исключений
        }
        
    }

    private static Set<Integer> parseNumbers(String argumentValue) {

        Set<Integer> result = new HashSet<>();
        try {
            if (argumentValue != null) {
                for(String strNumber : argumentValue.split(",")) {
                    result.add(Integer.parseInt(strNumber));
                }
            }
        } catch (NumberFormatException nfe) {} // Хотим просто вернуть, что было

        return result;
    }
}

