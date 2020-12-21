package com.ghc.cn.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghc.cn.mapper.EmployeeMapper;
import com.ghc.cn.mapper.SysPermissionCustomMapper;
import com.ghc.cn.mapper.SysRoleMapper;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.EmployeeCustom;
import com.ghc.cn.pojo.EmployeeExample;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.TreeMenu;
import com.ghc.cn.service.EmployeeService;



@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private SysPermissionCustomMapper  sysPermissionCustomMapper;
	
	@Autowired
	private SysRoleMapper sysRoleMapper ;
	
	@Override
	public Employee findEmployeeByName(String username) {
		EmployeeExample example = new EmployeeExample();
		EmployeeExample.Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(username);
		
		List<Employee> list = employeeMapper.selectByExample(example);
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	} 

	@Override
	public Employee findManagerById(Long id) {
		
		return employeeMapper.selectByPrimaryKey(id);
	}

	@Override
	public Employee findSysUserByUserCode(String name) throws Exception {
		EmployeeExample employeeExample = new EmployeeExample();
		EmployeeExample.Criteria criteria = employeeExample.createCriteria();
		criteria.andNameEqualTo(name);
		List<Employee> list = employeeMapper.selectByExample(employeeExample);
		if (list!=null&&list.size()==1) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public List<TreeMenu> findMenuList(String name) throws Exception {
		
		return sysPermissionCustomMapper.findMenuList();
	}

	@Override
	public List<SysPermission> getSubMenu() throws Exception {

		return sysPermissionCustomMapper.getSubMenu();
	}

	@Override
	public List<SysPermission> findPermissionListByUserId(String name) {
		
		return sysPermissionCustomMapper.findPermissionListByUserId(name);
	}

	@Override
	public List<EmployeeCustom> findUserListAndRole() {
		
		return sysPermissionCustomMapper.findUserListAndRole();
	}

	@Override
	public void updateEmployeeRole(String roleid, String userid) {
		// TODO Auto-generated method stub
		sysRoleMapper.updateEmployeeRole(roleid,userid);
	}

	@Override
	public void addRoleAndPermission(SysRole sysRole, int[] permissionIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveUser(Employee user) {
		// TODO Auto-generated method stub
		//employeeMapper.saveUser(user);
		employeeMapper.insert(user);
	}

	

	

}
