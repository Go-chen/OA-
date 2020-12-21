package com.ghc.cn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ghc.cn.exception.CustomException;
import com.ghc.cn.mapper.SysPermissionCustomMapper;
import com.ghc.cn.mapper.SysRoleMapper;
import com.ghc.cn.mapper.SysUserRoleMapper;
import com.ghc.cn.pojo.ActiveUser;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.EmployeeCustom;
import com.ghc.cn.pojo.SysRole;
import com.ghc.cn.pojo.TreeMenu;
import com.ghc.cn.service.EmployeeService;
import com.ghc.cn.service.WorkFlowShiroService;


@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SysPermissionCustomMapper sysPermissionCustomMapper	;
	
	@Autowired
	private WorkFlowShiroService workFlowShiroService ;
	
	@Autowired
	private  SysRoleMapper sysRoleMapper;
	
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
/*	@RequestMapping("/login")
	public String login(String username,String password,Model model,HttpSession session){
		
		Employee employee = employeeService.findEmployeeByName(username);
		
		
		if (employee!=null) {
			if (employee.getPassword().equals(password)) {	//登陆成功
				
				session.setAttribute(Constans.GLOBAL_SESSION_ID, employee);
				
				List<TreeMenu> treeMenus = workFlowShiroService.findMenuList();
				List<SysPermission> children = workFlowShiroService.getSubMenu();
				
				model.addAttribute("treeMenus", treeMenus);
				model.addAttribute("children", children);
				
				
				return "index" ;
			}else {
				model.addAttribute("errorMsg","账号或者密码错误");
				return "login";
			}
		} else {
			model.addAttribute("errorMsg","账号或者密码错误");
			return "login";
		}
		
	}*/
	
	@RequestMapping("/logout")
	public String logout(HttpSession session){
		session.invalidate();
		return "login";
	}
	
	@RequestMapping("/login")
	public String login(HttpServletRequest request,Model model)throws Exception{
		//要提取错误信息
		String exceptionName = (String) request.getAttribute("shiroLoginFailure");
		if (exceptionName!=null) {
			//1、账号错误
			if (UnknownAccountException.class.getName().equals(exceptionName)) {
				throw new CustomException("账号错误");
			}
			//2、密码错误
			if (IncorrectCredentialsException.class.equals(exceptionName)) {
				throw new CustomException("密码错误");
			}
			
		}
	
		return "login"; 
	}
	//主方法
	@RequestMapping("/main")
	public String main(Model model){
		//提取用户信息
		Subject subject = SecurityUtils.getSubject();
		ActiveUser activeUser =(ActiveUser) subject.getPrincipal();
		
		model.addAttribute("activeUser", activeUser);
		return "index" ;
	}
	
	//查找用户列表
	@RequestMapping("/findUserList")
	public ModelAndView findUserList(){
		List<EmployeeCustom> employees = sysPermissionCustomMapper.findUserListAndRole();
		List<SysRole> sysRoles = sysRoleMapper.selectByExample(null);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("userList", employees);
		mv.addObject("allRoles", sysRoles);
		mv.setViewName("userlist");
		return mv;
		
	}
	
	//用户管理的权限查看
	@RequestMapping("/sendajax")
	@ResponseBody	//java转换成json
	public SysRole sendajax(String username){
		SysRole sysRole = sysPermissionCustomMapper.findRoleAndPermissionListByUserId(username);
		return sysRole;
	}
	
	//重新设置权限
	@RequestMapping("/assignRole")
	@ResponseBody
	public Map<String, String> assignRole(String roleId,String userId){
		Map<String, String> map = new HashMap<String, String>();
		try {
			employeeService.updateEmployeeRole(roleId,userId);
			map.put("msg", "分配权限成功");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "分配权限失败");
			
		}
		return map;
		
	}
	
	
	//添加角色
	@RequestMapping("/toAddRole")
	public ModelAndView toAddRole(){
		List<TreeMenu> allPermissions = sysPermissionCustomMapper.getAllMenuAndPermision();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("allPermissions", allPermissions);
		
		mv.setViewName("rolelist");
		return mv;
		
	}
	
	
	//保存角色和权限
	@RequestMapping("/saveRoleAndPermissions")
	public String saveRoleAndPermissions(SysRole sysRole,int[] permissionIds){
		 employeeService.addRoleAndPermission(sysRole,permissionIds);
		return null;
		
	}
	
	
	
	//角色列表
	@RequestMapping("/findRoles")
	public ModelAndView findRoles(){
		List<SysRole> allRoles = sysRoleMapper.selectByExample(null);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("allRoles", allRoles);
		
		mv.setViewName("permissionlist");
		return mv;
	}
	
	//新建用戶
	@RequestMapping("/saveUser")
	public String saveUser(Employee user) {
		user.setSalt("eteokues");
		employeeService.saveUser(user);
		return "userlist";
	}
}
