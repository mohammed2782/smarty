package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TESTSSE
 */
@WebServlet("/TESTSSE")
public class TESTSSE extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    /**
     * Default constructor.
     */
    public TESTSSE() {
    }
 
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		   throws ServletException, IOException {

    		      //content type must be set to text/event-stream
    		      response.setContentType("text/event-stream");

    		      //encoding must be set to UTF-8
    		      response.setCharacterEncoding("UTF-8");

    		      PrintWriter writer = response.getWriter();

    		      for (int i = 0; i < 10; i++) {
    		         writer.write("data: " + System.currentTimeMillis() + "\n\n");
    		         writer.flush();
    		         try {
    		            Thread.sleep(1000);
    		         } catch (InterruptedException e) {
    		            e.printStackTrace();
    		         }
    		      }
    		      writer.close();
    		   }
    		
 
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
       ;
    }
 
    
}