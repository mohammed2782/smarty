package smarty.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DashBoard
 */
public class DashBoard extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashBoard() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginUser user = (LoginUser) request.getSession().getAttribute("lu");
		
		if (user.isFirstTimeLogin()){
			RequestDispatcher view = request.getRequestDispatcher("./portfolio/changepassword.jsp");
			view.forward(request, response);
		}else{
			response.sendRedirect("./portfolio/home/home.jsp");
		}
		//RequestDispatcher view = request.getRequestDispatcher("./portfolio/home/home.jsp");
		///view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PasswordMgm pm = new PasswordMgm();
		LoginUser user = (LoginUser)request.getSession().getAttribute("lu");
		boolean error = 
				pm.changeStudentPassword
					(user.getUserID(), 
					 request.getParameter("password1"), 
					 request.getParameter("password2")
					 );
		if (error){
			request.setAttribute("error", pm.getErrorReason());
			RequestDispatcher view = request.getRequestDispatcher("./portfolio/changepassword.jsp");
			view.forward(request, response);
		}else{
			response.sendRedirect("./portfolio/home/home.jsp");
		}
	}

}
