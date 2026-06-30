package smarty.filters;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import smarty.core.SessionVars;


/**
 * Servlet Filter implementation class Modifiers
 */
public class Modifiers implements Filter {
    /**
     * Default constructor. 
     */
    public Modifiers() {
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		// pass the request along the filter chain
		
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding("UTF-8");
		}
		HttpServletRequest rqs = ((HttpServletRequest) request);
		String uri = rqs.getRequestURI();
		if (uri != null && (uri.contains("/webapi/") || uri.contains("/rest/"))) {
			chain.doFilter(request, response);
			return;
		}
		SessionVars sessionVars = null;
		if (rqs.getSession().getAttribute("sessionVars")==null){
			sessionVars = new SessionVars();
			rqs.getSession().setAttribute("sessionVars", sessionVars);
		}else{
			sessionVars =(SessionVars)rqs.getSession().getAttribute("sessionVars");
		}
		sessionVars.setCurrentPageURL(rqs.getRequestURI().toString());
		
		
		
		if (request.getParameter("divCode")!=null){
			sessionVars.setDivision(request.getParameter("divCode")); 
		}

		if (sessionVars.getLang() == null){
			sessionVars.setLang("AR");	
		}
		if (sessionVars.getDivision() == null){
			sessionVars.setDivision("MC");
		}
		
		boolean changeURI = false;
		if (rqs.getParameter("SYS_LANG")!=null){
			String requestURI = rqs.getRequestURI();
			String oldLang =sessionVars.getLang();
			sessionVars.setLang(request.getParameter("SYS_LANG"));
			if (requestURI.contains("/"+oldLang)){
				String toReplace = requestURI.substring(requestURI.indexOf("/"+oldLang), requestURI.length());
				String newURI = requestURI.replace(toReplace, "/"+sessionVars.getLang());
				changeURI = true;
				if (rqs.getQueryString()!=null)
					newURI +="?"+rqs.getQueryString();
				
				((HttpServletResponse)response).sendRedirect(newURI);
				
				//request.getRequestDispatcher(newURI).forward(rqs, response);
			}
		}
		if (sessionVars.getLang().equalsIgnoreCase("AR")){
			sessionVars.setBodyDir("rtl");
		}else{
			sessionVars.setBodyDir("ltr");
		}
		if (!changeURI)
			chain.doFilter(rqs, response);
		
		
	
		//System.out.println("modifiers");
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
