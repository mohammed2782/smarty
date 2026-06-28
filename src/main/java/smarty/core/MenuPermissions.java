package smarty.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MenuPermissions implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5481705845423929859L;
	private int menuId;
	private String menuCode;
	private String menuName;
	private int seq;
	private String icon;
	private boolean showInMenu = true;
	private String subMenuIds;
	LinkedHashMap<String, MenuPermissions> subMenuList;
	
	public int getMenuId() {
		return menuId;
	}
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
	public LinkedHashMap<String, MenuPermissions> getSubMenuList() {
		return subMenuList;
	}
	public void setSubMenuList(LinkedHashMap<String, MenuPermissions> subMenuList) {
		this.subMenuList = subMenuList;
	}
	public boolean isShowInMenu() {
		return showInMenu;
	}
	public void setShowInMenu(boolean showInMenu) {
		this.showInMenu = showInMenu;
	}
	public String getSubMenuIds() {
		return subMenuIds;
	}
	public void setSubMenuIds(String subMenuIds) {
		this.subMenuIds = subMenuIds;
	}
	
}
