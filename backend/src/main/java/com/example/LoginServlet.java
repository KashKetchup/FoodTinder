package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tinderBack.FTException;
import tomcatIntegration.FTServlet;

public class LoginServlet extends FTServlet{
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        String jsonOut;

        try {
            String salt = foodTinder.getSalt();
            String challenge = foodTinder.getChallenge();

            if (salt == null || challenge == null) {
                jsonOut = "{\"state_flag\": \"0\"}";
            } else {
                jsonOut = String.format("{\"state_flag\": \"1\", \"salt\": \"%s\"," 
                    + " \"challenge\": \"%s\"}",
                salt, challenge
                );
            }
            out.print(jsonOut);
        } catch (FTException fte) {
            out.print("{\"state_flag\": \"0\"}");
            System.out.println("LoginServlet: Cannot get salt or challenge:\n" + fte.getMessage());
        } finally {
            out.close();
        }
    }

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
    throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        String s_challenge = req.getParameter("s_challenge");

        if (foodTinder.checkChallenge(s_challenge)) {
            String login = req.getParameter("Login");
            byte[] h_password = req.getParameter("Password").getBytes();
            String c_challenge = req.getParameter("c_challenge");
            try {

                String s_password = foodTinder.getPassByLogin(login) + c_challenge + s_challenge;
                
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] server_hash = messageDigest.digest(s_password.getBytes());

                String hex_hash = String.format( "%064x", new BigInteger( 1, server_hash));
                //Можем хранить как Хекс или как массив байт, и сравнивать тоже
                if (server_hash.equals(h_password)) {
                    foodTinder.addToken(hex_hash, System.currentTimeMillis());
                    out.print("{\"state_flag\": \"1\"}");
                } else {
                    out.print("{\"state_flag\": \"0\"}");
                }
            } catch (FTException fte) {
                out.print("{\"state_flag\": \"0\"}");
                System.out.println("LoginServlet: Cannot add token:\n" + fte.getMessage());
            } catch (NoSuchAlgorithmException ae) {
                out.print("{\"state_flag\": \"0\"}");
                System.out.println("LoginServlet: Cannot get hash(digest):\n" + ae.getMessage());

            }finally {
                out.close();
            }
        } else {
            out.print("{\"state_flag\": \"0\"}");
            System.out.println("LoginServlet: Uncorrect server challenge: " + s_challenge);
        }
    }
}
