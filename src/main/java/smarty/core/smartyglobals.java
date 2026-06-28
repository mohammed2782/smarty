package smarty.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * you should use this class as a session bean 
 */
public class smartyglobals<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8522807031958355232L;
	public HashMap <T ,T> smartyGlobalsAssArr;
	public int dummy;
	public <T> smartyglobals(){
		smartyGlobalsAssArr = new HashMap();
	}
	
	
	
}
