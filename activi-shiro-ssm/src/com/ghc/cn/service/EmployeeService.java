package com.ghc.cn.service;

import java.util.List;

import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.EmployeeCustom;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.TreeMenu;

public interface EmployeeService {

	//假设：用户名有唯一性约束
	public Employee findEmployeeByName(String username);
	
	//根据manager_id查询上级主管信息
	public Employee findManagerById(Long id);
	
	//根据用户账号查询用户信息
	public Employee findSysUserByUserCode(String name) throws Exception;
	
	//根据用户id查询权限范围的菜单
	public List<TreeMenu> findMenuList(String name) throws Exception;
	
	//根据用户id查询权限范围的子菜单（URL）
	public List<SysPermission> getSubMenu() throws Exception;

	public List<SysPermission> findPermissionListByUserId(String name);
	//查询用户列表
	public List<EmployeeCustom> findUserListAndRole();

	public void updateEmployeeRole(String roleid, String userid);

	public void addRoleAndPermission(SysRole sysRole, int[] permissionIds);

	public void saveUser(Employee user);
	
}
