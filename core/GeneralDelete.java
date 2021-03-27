package com.app.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.app.db.mysql;
import com.app.site.security.LoginUser;

/**
 * Servlet implementation class GeneralDelete
 */
@WebServlet("/GeneralDelete")
public class GeneralDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeneralDelete() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String className = request.getParameter("className");
		System.out.println("this class name==>"+className);
		Class userClass;
		CoreMgr mgr = null;
		Connection conn = null;
		String msg = "err";
		try {
			request.setCharacterEncoding("UTF-8"); 
			response.setCharacterEncoding("UTF-8");
			userClass = Class.forName(className);
			mgr = (CoreMgr) userClass.newInstance();
			HttpSession session = request.getSession();
			LoginUser lu = (LoginUser) session.getAttribute("lu");
			com.app.core.smartyglobals mg = (com.app.core.smartyglobals)session.getAttribute("Myglobals");
			mgr.setarrayGlobals(mg.smartyGlobalsAssArr);
			//if (mgr.canDelete) // i Stopped this because sometime i need to call delete without setting canDelete=true
				if (mgr.keyCol!=null && !mgr.keyCol.isEmpty())
					if (mgr.mainTable !=null && !mgr.mainTable.isEmpty()){
						conn = mysql.getConn();
						mgr.setConn(conn);
						msg = mgr.doDelete(request);
						if (mgr.deleteErrorFlag)
							msg = "err";
						else if (mgr.myhtmlmgr.refreshPageOnDelete)
							msg = "ref";	
					}
						
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{conn.close();}catch(Exception e){/*ignore*/}
		}
		response.getWriter().write(msg);		
	}

}
