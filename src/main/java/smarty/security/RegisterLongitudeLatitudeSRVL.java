package smarty.security;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import smarty.db.mysql;

/**
 * Servlet implementation class RegisterLongitudeLatitudeSRVL
 */
@WebServlet("/RegisterLongitudeLatitudeSRVL")
public class RegisterLongitudeLatitudeSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterLongitudeLatitudeSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		String longitude = request.getParameter("longitude");
		String latitude = request.getParameter("latitude");
		String whichInterface = request.getParameter("whichInterface");
		Connection conn = null;
		try {
			conn = mysql.getConn();
			lu.logUserAccess(conn, lu.getUsid(), whichInterface, latitude, longitude);
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
	}

}
