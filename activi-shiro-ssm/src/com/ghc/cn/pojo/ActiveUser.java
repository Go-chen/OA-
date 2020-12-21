package com.ghc.cn.pojo;

import java.util.List;

public class ActiveUser extends Employee implements java.io.Serializable {

	private List<TreeMenu> menusList;

	public List<TreeMenu> getMenusList() {
		return menusList;
	}

	public void setMenusList(List<TreeMenu> menusList) {
		this.menusList = menusList;
	}
	
	
}
