package com.example;

import tinderBack.Diet;
import tinderBack.FTException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tomcatIntegration.*;

public class DietsServlet extends FTServlet{
    
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {

        //Настройка для работы с React

//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
//        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        //Отправляем запрос к БД и парсим ответ, затем отправляем на React

        PrintWriter out = resp.getWriter();

        try {
            int daysCount = Integer.parseInt(req.getParameter("days_count"));
            Set<Integer> includeIngredient = parseNumbers(req.getParameter("in_ingr"));
            Set<Integer> includeTag = parseNumbers(req.getParameter("in_tag"));
            Set<Integer> excludeIngredient = parseNumbers(req.getParameter("ex_ingr"));
            Set<Integer> excludeTag = parseNumbers(req.getParameter("ex_tag"));

            List<Diet> diets = foodTinder.findRecomendDiets(daysCount, 
            		includeTag, excludeTag, includeIngredient, excludeIngredient);
            /*
             * Преобразование рационов в массив объектов JSON
             * Реализуется как StringUtils.join('[', str, ']'), где
             * str формируется из recipes с помощью Stream API с разделителями ", "
             */
            out.print(StringUtils.join('[',
            		diets.stream().map(diet -> ServletUtils.convertDietToJSON(diet))
            		.collect(Collectors.joining(", ")), ']'));
            out.flush();
        } catch (FTException e) {
            System.out.println("\nError while accessing to FoodTinder:\n" + e.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("\nUnable to determine ID:\n" + nfe.getMessage());
        }
        
    }

    private static Set<Integer> parseNumbers(String argumentValue) {
        Set<Integer> result = new HashSet<>();
        try {
            if (argumentValue != null && !argumentValue.isEmpty()) {
                for(String strNumber : argumentValue.split(",")) {
                    result.add(Integer.parseInt(strNumber));
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("\nUnable to determine ID:\n" + nfe.getMessage());
        } // Хотим просто вернуть, что было

        return result;
    }
}

