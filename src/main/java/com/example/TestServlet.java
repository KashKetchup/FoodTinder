package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.ServerException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet{
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServerException, IOException 
    {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>");
        out.println("</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello I am your Servlet ");
        out.println("</body>");
        out.println("</html>");
    }

    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
    throws ServerException, IOException {}
        
    
}
