package com.example;

import tinderBack.Diet;
import tinderBack.FTException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tomcatIntegration.*;

public class DietPageServlet extends FTServlet{
    
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
            Integer id = Integer.parseInt(req.getParameter("id"));
            Diet diet = foodTinder.getDiet(id);
            String jsonOut;
            if (diet == null) {
            	jsonOut = "{}";
            } else {
            	jsonOut = ServletUtils.convertDietToJSON(diet);
            }
            out.print(jsonOut);
            out.flush();

        } catch (FTException e) {
            System.out.println("\nDietPageServlet: Error while accessing to FoodTinder:\n" 
                + e.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println("\nDietPageServlet: Unable to determine ID:\n" 
                + nfe.getMessage());
            out.print("{\"state_flag\": \"0\"}");
            out.flush();
        }finally { 
            out.close();
        }
    }
}
