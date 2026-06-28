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

/**
 * Servlet Filter implementation class MainFilter
 */
public class ZipFilter implements Filter {

    /**
     * Default constructor. 
     */
    public ZipFilter() {
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
		//request.setCharacterEncoding("UTF-8"); 
        //response.setCharacterEncoding("UTF-8"); 
		/*HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if ( acceptsGZipEncoding(httpRequest) ) {
           
        	httpResponse.addHeader("Content-Encoding", "gzip");
            GZipServletResponseWrapper gzipResponse =  new GZipServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        } else {
            chain.doFilter(request, response);
        }*/
        
        chain.doFilter(request, response);
       // System.out.println("hello");
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	 private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
	        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
	        return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1;
	    }

}
