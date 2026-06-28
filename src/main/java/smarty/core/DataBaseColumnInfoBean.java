package smarty.core;

public class DataBaseColumnInfoBean {
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}

	public String getColValue() {
		return colValue;
	}
	public void setColValue(String colValue) {
		this.colValue = colValue;
	}
	public int getColSize() {
		return colSize;
	}
	public void setColSize(int colSize) {
		this.colSize = colSize;
	}
	private String colName;
	private String colType;
	private int colSize;
	private String colValue;
}
