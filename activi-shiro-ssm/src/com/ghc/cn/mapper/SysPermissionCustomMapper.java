package com.ghc.cn.mapper;

import java.util.List;

import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.EmployeeCustom;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.SysRoleCustom;
import com.ghc.cn.pojo.TreeMenu;

public interface SysPermissionCustomMapper {

	public List<TreeMenu> findMenuList();
	
	public List<SysPermission> getSubMenu();
	
	public List<TreeMenu>  getAllMenuAndPermision();
	
	public List<SysPermission> findPermissionListByUserId(String name);

	//查询用户列表
	public List<EmployeeCustom> findUserListAndRole();
	
	//系统管理-用户列表-查看权限  根据用户名称查询出用户的角色喝对应的权限列表信息 
	public SysRole findRoleAndPermissionListByUserId(String username);

	
	
	

	
	
}
