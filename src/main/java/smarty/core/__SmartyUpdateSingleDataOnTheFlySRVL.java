package smarty.core;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.UtilitiesAyatLina;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class __SmartyUpdateSingleDataOnTheFlySRVL
 */
@WebServlet("/__SmartyUpdateSingleDataOnTheFlySRVL")
public class __SmartyUpdateSingleDataOnTheFlySRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public __SmartyUpdateSingleDataOnTheFlySRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn1 = null;
		CoreUtilities utal = new CoreUtilities();
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		String tableName = request.getParameter("t");
		String pkCol =  request.getParameter("k");
		int pkVal = Integer.parseInt(request.getParameter("kv"));
		String columnToChange = request.getParameter("cto");
		String newValue = request.getParameter("nv");
		String fromWhere = request.getParameter("fw");
		try {
			conn1 = mysql.getConn();
			utal.updateSingleDataOnTheFly(conn1, tableName,  pkCol, pkVal, columnToChange, newValue , fromWhere,  lu.getUsid());
			conn1.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn1.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn1.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
