package com.ghc.cn.utlis;

import javax.servlet.http.HttpSession;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ghc.cn.pojo.Employee;
import com.ghc.cn.service.EmployeeService;
import com.ghc.cn.service.WorkFlowShiroService;

//��ѯ��ǰԱ�����ϼ�����
public class AssigneeTaskListener implements TaskListener{

	
	@Override
	public void notify(DelegateTask delegateTask) {
		// ͨ��Ӳ�����ȡspring����
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		
		EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");
		
		//��ȡsession
		ServletRequestAttributes requestAttribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		 HttpSession session = requestAttribute.getRequest().getSession();
		 
//		 Employee employee = (Employee) session.getAttribute(Constans.GLOBAL_SESSION_ID);
		 Employee employee = (Employee) SecurityUtils.getSubject().getPrincipal();
		 Employee manager  = employeeService.findManagerById(employee.getManagerId());
		 
		 delegateTask.setAssignee(manager.getName());
	}

}
