package smarty.security;

import java.util.HashMap;

public class SecurityMatrix {
	private LoginUser user;
	public LoginUser getUser() {
		return user;
	}
	public void setUser(LoginUser user) {
		this.user = user;
	}
	/*
	 * getApproval button for ranks who can approve
	 */
	public String getApprovalButton(String userid,String divCode , String depCode ,String table, String category, String keyCol){
		String html="";
		String onClick="";//
		boolean showBTN = false;
		
		if ( (!user.isLoggedIn()) || (!user.isStaff()) )
			return "";
		
		//System.out.println("is loggedin");
		
		if (user.getSuperRank().equalsIgnoreCase("Y")){
			showBTN = true;
		}else{
			if (!user.isCanApprove())
				return "";
			
			showBTN = checkEligibility(userid,divCode , depCode);
		}
		if (showBTN){	
			html  = "<form action='./ApproveContents' method='post'>";
			html += " <input type='hidden' name='table' value='"+table+"' />";
			html += " <input type='hidden' name='category' value='"+category+"' />";
			html += " <input type='hidden' name='keycol' value='"+keyCol+"' />";
			html += "<input type='submit' value='Approve'>";
			html += "</form>";
		}	
		//String html="<a onclick=\"return confirm('"+dltCofrmMsg+"');\" href=?myClassBean="+myClassBean+"&"+idname+"="+ID+"&op=del>" +
			//	"<img src='img/delete.png' height =14 width=13 border=0></img></a>";
		return html;
	}
	/*
	 * we first check the user eligibility if he can edit
	 * Gen Update Button
	 */
	public  String getUpdateButton(String userid,String tableName,String divCode , String depCode ,HashMap<String,String>categories, String popURL){
		String html="";
		String onClick="";
		boolean showBTN = false;
		
		if ( (!user.isLoggedIn()) || (!user.isStaff()) )
			return "";
		
		//System.out.println("is loggedin");
		
		if (user.getSuperRank().equalsIgnoreCase("Y")){
			showBTN = true;
		}else{
			if (!user.isCanEdit())
				return "";
			
			showBTN = checkEligibility(userid,divCode , depCode);
		}
		if (showBTN){	
			if (popURL!=null){
				if (!popURL.trim().equals("")){
					String ColsAndCategories ="";
					for(String catCol : categories.keySet()){
						ColsAndCategories +=catCol+"="+categories.get(catCol)+"&"; 
					}
					popURL = popURL+"?mode=upd";
					onClick = "onclick='popUp(\""+popURL+"\"); return false;'";
				}
			}
			html="<a href='"+popURL+"'><img src='img/Edit.png' height =20 width=20 border=0 style='margin:5px;'></img></a>";
		}
		return html;
	}
	/*
	 * Gen Delete Button
	 */
	public  String getDeleteButton(String userid,String divCode , String depCode ,String forcontent ,String keyCol , String popURL){
		String html="";
		String onClick="";//
		boolean showBTN = false;
		
		if ( (!user.isLoggedIn()) || (!user.isStaff()) )
			return "";
		
		//System.out.println("is loggedin");
		
		if (user.getSuperRank().equalsIgnoreCase("Y")){
			showBTN = true;
		}else{
			if (!user.isCanEdit())
				return "";
			
			showBTN = checkEligibility(userid,divCode , depCode);
		}
		if (showBTN){	
			
			html="<a href='"+popURL+"?keyCol="+keyCol+"&forcontent="+forcontent+"' style='margin:5px;'>"
					+ "<img src='img/delete.png' height =20 width=20 border=0></img></a>";
		}	
		//String html="<a onclick=\"return confirm('"+dltCofrmMsg+"');\" href=?myClassBean="+myClassBean+"&"+idname+"="+ID+"&op=del>" +
			//	"<img src='img/delete.png' height =14 width=13 border=0></img></a>";
		return html;
	}
	/*
	 * Gen New Form Button.
	 */
	public  String getNewButton_Contents(String userid,
										 String tableName,
										 String divCode , 
										 String depCode ,
										 HashMap<String,String>categories , 
										 String popURL){
		String html="";
		String onClick="";
		boolean showBTN = false;
		
		if ( (!user.isLoggedIn()) || (!user.isStaff()) )
			return "";
		
		
		
		if (user.getSuperRank().equalsIgnoreCase("Y")){
			showBTN = true;
		}else{
			if (!user.isCanEdit())
				return "";
			
			showBTN = checkEligibility(userid,divCode , depCode);
		}
		if (showBTN){	
			if (popURL!=null){
				if (!popURL.trim().equals("")){
					String ColsAndCategories ="";
					for(String catCol : categories.keySet()){
						ColsAndCategories +=catCol+"="+categories.get(catCol)+"&"; 
					}
					popURL = popURL+"?"+ColsAndCategories+"divcode="+divCode+"&depCode="+depCode+"&op=new";
					onClick = "onclick='popUp(\""+popURL+"\"); return false;'";
				}
			}
			html="<a href='.' "+onClick+"><img src='img/add-icon.png' height =20 width=20 border=0 style='margin:5px;'></img></a>";
		}
		return html;
		
		
		//String html="<a href=?op=new><img src='/img/add-icon.gif' " +
			//		" height =21 width=21 border=0></img></a> ";
		//return html;
	}
	/*
	 * check if the user can edit , create or delete
	 * @param :
	 * 1- userid : is the userid.
	 * 2- action : del , upd , new.
	 */
	public boolean checkEligibility(String userid,String divCode , String depCode){
		System.out.println("check eligibility");
		if (user.getSuperRank().equalsIgnoreCase("Y")){
			return true;
		}else{
			if (user.getDiv_code().equalsIgnoreCase(divCode)){ // if this user belongs to this division
				if ( (user.getDep_code()==null) || (user.getDep_code().trim().equals("")) ){ // if the department is empty
					// means he can have control over the whole division.
					return true;
				}else{ // if he belongs to specific department
					if ( (depCode ==null) || (depCode.trim().equals(""))){
						return false;
					}else{
							if (user.getDep_code().trim().equalsIgnoreCase(depCode.trim())){
								return true;
							}else{
								return false;
							}
					}
					
				}
			}	
			return false;
		}
	}
}
