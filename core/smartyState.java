package com.app.core;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/* this class will hold the state of every object the user create(visit)
	the state includes :
	1- the filters , yet to be implemented
	hashmap(classBeanName,hashmap
								(String , HashMap
												("searchbycol" , col Name 
												 "searchval",			ValueUsedinSearching
												)
								)
		   )
	Ex :	("StudentBean",hashmap(
									"filter"
											("searchbycol" , "StudenName"
											 "searchval" , "NAFI"
											)
									)
									
			)
	2- the page the user left the table at.
	page example
	hashamp(classBeanName ,hashmap
								 ("page"-->this is keyword , pageNo)
			)
	Ex:		("StudentBean",
							("page",2)
			)
	3- the sorting, the user click column to sort ascending or descending
	sorting example 
	hashmap (classBeanName,hashamap
								 ("sortby" ,Col Name 
								   "sortmode" , "asc or dsc"
								 )
			) 
	Ex:    ("StudentBean" ,
							("sortby","StudenID"
							 "sortmode" ,"asc"
							)
			)
 
 */
public class smartyState <T> {
	//public HashMap <String ,HashMap <String , T>> smartyStateMap;
	public HashMap <T , T> smartyStateMap;
	public int dummy;
	public <T> smartyState(){
		smartyStateMap = new HashMap();
	}
	
}
