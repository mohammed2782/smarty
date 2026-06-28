package smarty.security;

import java.sql.Connection;
import java.sql.PreparedStatement;

import smarty.db.mysql;


public class PasswordMgm {
	private String errorReason;
	
	public boolean changeStudentPassword(String stdid , String newPass , String confirmPass){
		boolean error = false;
		Connection conn = null;
		PreparedStatement pst = null;
		boolean valid = checkPasswordValidity(newPass , confirmPass);
		if (!valid)
			error = true;
		
		if (!error){
			try{
				conn = mysql.getConn();
				pst = conn.prepareStatement("update kbstudents set s_passcode = MD5(?) , s_p_b4_enc=? , s_firsttimelogin=? where s_stdid=?");
				pst.setString(1, newPass);
				pst.setString(2, newPass);
				pst.setString(3, "N");
				pst.setString(4, stdid);
				pst.executeUpdate();
				conn.commit();
				
			}catch(Exception e){
				e.printStackTrace();
				try{
					conn.rollback();
				}catch(Exception eRoll){
					/**/
				}
			}finally{
				try{
					conn.close();
				}catch(Exception e){
					/*ignore*/
				}
			}
		}
		return error;
	}
	private boolean checkPasswordValidity(String pass , String confirmPass){
		boolean valid = false;
		
		if (pass == null){
			valid = false;
			errorReason = "Password is empty";
			return valid;
		}
		if (pass.trim().equals("") ){
			valid = false;
			errorReason = "Password is empty";
			return valid;
		}
		if (pass.trim().length() < 5 ){
			valid = false;
			errorReason = "Password should be at least 5 characters";
			return valid;
		}
		if (!confirmPass.equals(pass)){
			valid = false;
			errorReason = "Password and confirm password are not the same";
			return valid;
		}
		valid = true;
		return valid;
	}
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
}
