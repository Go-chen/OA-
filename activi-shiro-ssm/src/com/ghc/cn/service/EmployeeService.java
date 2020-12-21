package com.ghc.cn.service;

import java.util.List;

import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.EmployeeCustom;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.TreeMenu;

public interface EmployeeService {

	//���裺�û�����Ψһ��Լ��
	public Employee findEmployeeByName(String username);
	
	//����manager_id��ѯ�ϼ�������Ϣ
	public Employee findManagerById(Long id);
	
	//�����û��˺Ų�ѯ�û���Ϣ
	public Employee findSysUserByUserCode(String name) throws Exception;
	
	//�����û�id��ѯȨ�޷�Χ�Ĳ˵�
	public List<TreeMenu> findMenuList(String name) throws Exception;
	
	//�����û�id��ѯȨ�޷�Χ���Ӳ˵���URL��
	public List<SysPermission> getSubMenu() throws Exception;

	public List<SysPermission> findPermissionListByUserId(String name);
	//��ѯ�û��б�
	public List<EmployeeCustom> findUserListAndRole();

	public void updateEmployeeRole(String roleid, String userid);

	public void addRoleAndPermission(SysRole sysRole, int[] permissionIds);

	public void saveUser(Employee user);
	
}
