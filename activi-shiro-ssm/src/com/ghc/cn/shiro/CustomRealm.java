package com.ghc.cn.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.ghc.cn.pojo.ActiveUser;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.SysUserRole;
import com.ghc.cn.pojo.TreeMenu;
import com.ghc.cn.service.EmployeeService;

import oracle.net.aso.a;
import oracle.net.aso.l;
import oracle.net.aso.s;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private EmployeeService employeeService;
	//用户认证
		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			//伪代码
			//1、获取账号
			String username = (String) token.getPrincipal();//主题信息
			
			Employee employee = null;
			List<TreeMenu> menus = null;
			try {
				employee = employeeService.findSysUserByUserCode(username);
				if (employee == null) {
					return null;
				}
				menus = employeeService.findMenuList(username);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
			String password_db = employee.getPassword(); //密文
			String salt = employee.getSalt();
			
			ActiveUser activeUser = new ActiveUser();
			activeUser.setName(employee.getName());
			activeUser.setId(employee.getId());
			activeUser.setManagerId(employee.getManagerId());
			activeUser.setMenusList(menus);
			
			//密码的对比有框架完成 
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activeUser, password_db, ByteSource.Util.bytes(salt),"CustomRealm");
			return info;
		}
		
		//用户授权
		@Override
		protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
			List<String> permissions = null;
			//获取当前 登录人 的账号
			try {
				ActiveUser user  =  (ActiveUser) principal.getPrimaryPrincipal();
				List<SysPermission> permissionList = employeeService.findPermissionListByUserId(user.getName());
				
				//查询数据库，查出角色喝权限的列表信息
				permissions = new ArrayList<>();
				for (SysPermission sysPermission : permissionList) {
					permissions.add(sysPermission.getPercode());
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			
			
			info.addStringPermissions(permissions);
			return info;
		}

		

	

}
