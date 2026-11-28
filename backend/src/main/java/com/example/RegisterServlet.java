package com.example;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tinderBack.FTException;
import tomcatIntegration.FTServlet;

public class RegisterServlet extends FTServlet{


    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        String jsonOut;
        try {
            String salt = foodTinder.getSalt();
            
            if (salt == null) {
                jsonOut = "{\"state_flag\": \"0\"}";
            } else {
                jsonOut = String.format(
                    "{\"state_flag\": \"1\", \"salt\": \"%s\"}", salt);
            }
            out.print(jsonOut);
            out.flush();
        } catch (FTException fte) {
            out.print("{\"state_flag\": \"0\"}");
            System.out.println("\nRegisterServlet: Error while accessing to FoodTinder:\n" 
                + fte.getMessage());
        } finally {
            out.close();
        }
    }


    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        String login = req.getParameter("Login");
        String pass = req.getParameter("Password");
        
        PrintWriter out = resp.getWriter();
        try {
            foodTinder.addUserQuery(login, pass);
            out.print("{\"state_flag\": \"1\"}");

        } catch (FTException fte) {
            out.print("{\"state_flag\": \"0\"}");
            System.out.println("RegisterServlet: Cannot add new User: \n" + fte.getMessage());
        }
    }
}
