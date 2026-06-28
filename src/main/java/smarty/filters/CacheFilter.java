package smarty.filters;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CachingFilter
 */
public class CacheFilter implements Filter {
	    private Map headersMap;
	    public void init(FilterConfig filterConfig) throws ServletException {	    
	        String headerParam = filterConfig.getInitParameter("header");
	        if (headerParam == null) {
	            System.out.println("No headers were found in the web.xml (init-param) for the HeaderFilter !");
	            return;
	        }
	        // Init the header list :
	        headersMap = new LinkedHashMap();
	        if (headerParam.contains("|")) {
	            String[] headers = headerParam.split("\\|");
	            for (String header : headers) {
	                parseHeader(header);
	            }
	        } else {
	            parseHeader(headerParam);
	        }
	       //System.out.println("The following headers were registered in the HeaderFilter :");
	      
	        for (Object item : headersMap.keySet()) {
	            System.out.println(item.toString() + ':' + headersMap.get(item).toString());
	        }
	    }
	    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	    	HttpServletResponse response = (HttpServletResponse) res;
	    	if (headersMap != null) {
	            // Add the header to the response	
	            for ( Object header : headersMap.keySet()) {
	                response.setHeader(header.toString() , headersMap.get(header).toString());
	                
	            }
	        }
	    	response.setHeader("Cache-Control", "cache, max-age=10000"); // HTTP 1.1.
	        response.setHeader("Pragma", "cache"); // HTTP 1.0.
	        // Continue
	        chain.doFilter(request, response);
	    }

	    public void destroy() {
	        this.headersMap = null;
	    }
	    private void parseHeader(String header) {
	        String headerName = header.substring(0, header.indexOf(":"));
	        if (!headersMap.containsKey(headerName)) {
	            headersMap.put(headerName, header.substring(header.indexOf(":") + 1));
	        }
	    }
}
