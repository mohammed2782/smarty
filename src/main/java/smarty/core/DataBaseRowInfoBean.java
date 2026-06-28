package smarty.core;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseRowInfoBean {
	private int rowNum;
	private String pkColName;
	private String pkColVal;
	private HashMap<String, DataBaseColumnInfoBean> colList;
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public String getPkColName() {
		return pkColName;
	}
	public void setPkColName(String pkColName) {
		this.pkColName = pkColName;
	}
	public String getPkColVal() {
		return pkColVal;
	}
	public void setPkColVal(String pkColVal) {
		this.pkColVal = pkColVal;
	}
	public HashMap<String, DataBaseColumnInfoBean> getColList() {
		return colList;
	}
	public void setColList(HashMap<String, DataBaseColumnInfoBean> colList) {
		this.colList = colList;
	}

}
