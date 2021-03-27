package com.app.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * you should use this class as a session bean 
 */
public class smartyglobals<T> {
	public HashMap <T ,T> smartyGlobalsAssArr;
	public int dummy;
	public <T> smartyglobals(){
		smartyGlobalsAssArr = new HashMap();
	}
	
	
	
}
