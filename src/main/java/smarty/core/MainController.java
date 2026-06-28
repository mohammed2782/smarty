package smarty.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import smarty.security.LoginUser;

/**
 * Servlet implementation class MainController
 */
@WebServlet("/Mainctrl/*")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		HttpSession sessionRQS = request.getSession();
		LoginUser user =(LoginUser)sessionRQS.getAttribute("lu");
        // Getting servlet request query string.
        String queryString = request.getQueryString();

        // Getting request information without the hostname.
        String uri = request.getRequestURI();

        // Below we extract information about the request object path
        // information.
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int portNumber = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        /*System.out.println("-------------->"+scheme);
        System.out.println("serverName-------------->"+serverName);
        System.out.println("serverName-------------->"+serverName);
        System.out.println("portNumber-------------->"+portNumber);
        System.out.println("contextPath-------------->"+contextPath);
        System.out.println("servletPath-------------->"+servletPath);
        
        System.out.println("------------->"+request.getPathInfo());*/
        String pathInfo = request.getPathInfo().replaceFirst("/", "");
        String query = request.getQueryString();
        String [] pathArr = pathInfo.split("/");
        //System.out.println("Query: " + query);
        RequestDispatcher dispatcher = null;
        if (pathArr.length<2) {
        	dispatcher = request.getRequestDispatcher("../../login.jsp");
        	sessionRQS.removeAttribute("lu");
        }else
        	dispatcher = request.getRequestDispatcher("/WEB-INF/views/"+pathArr[0]+"/"+pathArr[1]+".jsp?"+query);
        
      //  System.out.println("------------>"+"/WEB-INF/views/"+pathArr[0]+"/"+pathArr[1].replace(".jsp","")+".jsp?"+query);
        dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}



/*package com.app.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.app.site.security.LoginUser;

*//**
 * Servlet implementation class MainController
 *//*
@WebServlet("/Mainctrl/*")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    *//**
     * @see HttpServlet#HttpServlet()
     *//*
    public MainController() {
        super();
        // TODO Auto-generated constructor stub
    }

	*//**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *//*
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		HttpSession sessionRQS = request.getSession();
		LoginUser user =(LoginUser)sessionRQS.getAttribute("lu");
        // Getting servlet request query string.
        String queryString = request.getQueryString();

        // Getting request information without the hostname.
        String uri = request.getRequestURI();

        // Below we extract information about the request object path
        // information.
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int portNumber = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        System.out.println("-------------->"+scheme);
        System.out.println("serverName-------------->"+serverName);
        System.out.println("serverName-------------->"+serverName);
        System.out.println("portNumber-------------->"+portNumber);
        System.out.println("contextPath-------------->"+contextPath);
        System.out.println("servletPath-------------->"+servletPath);
        
        System.out.println("------------->"+request.getPathInfo());
        String pathInfo = request.getPathInfo().replaceFirst("/", "");
        String query = request.getQueryString();
        String [] pathArr = pathInfo.split("/");
        //System.out.println("Query: " + query);
        RequestDispatcher dispatcher = null;
        if (pathArr.length<2) {
        	System.out.println("in less2");
        	dispatcher = request.getRequestDispatcher("../../login.jsp");
        	request.getSession().invalidate();
        }else {
        	if (user!=null) {
		    	if (user.getMenuPermissionsList().containsKey(pathArr[0]) && user.getMenuPermissionsList().get(pathArr[0]).getSubMenuList().containsKey(pathArr[1])) {
		    		dispatcher = request.getRequestDispatcher("/WEB-INF/views/"+pathArr[0]+"/"+pathArr[1]+".jsp?"+query);
			
		    	}else {
		    		dispatcher = request.getRequestDispatcher("/WEB-INF/views/"+pathArr[0]+"/"+pathArr[1]+".jsp?"+query);
		    	
		    		request.getSession().invalidate();
		    	}
        	}else {
        		dispatcher = request.getRequestDispatcher("/login.jsp");
            	request.getSession().invalidate();
        	}
        
        }
        dispatcher.forward(request, response);
	}

	*//**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 *//*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
*/