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

	//��ѯ�û��б�
	public List<EmployeeCustom> findUserListAndRole();
	
	//ϵͳ����-�û��б�-�鿴Ȩ��  �����û����Ʋ�ѯ���û��Ľ�ɫ�ȶ�Ӧ��Ȩ���б���Ϣ 
	public SysRole findRoleAndPermissionListByUserId(String username);

	
	
	

	
	
}
