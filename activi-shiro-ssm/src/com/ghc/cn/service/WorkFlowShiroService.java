package com.ghc.cn.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;


import com.ghc.cn.pojo.Baoxiaobill;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.Leavebill;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.TreeMenu;

public interface WorkFlowShiroService {
	
	public void  deployProcess(InputStream input,String filename);
	
	public List<Deployment> findAllDeployments();
	
	public List<ProcessDefinition> findAllProcessDefinition();

	public void saveLeaveAndStartProcess(Baoxiaobill bill, Employee employee);
	
	public List<Task> findMyTaskListByUserId(String name);

	public Baoxiaobill findBillByTaskId(String taskId);

	public List<Comment> findCommentListByTaskId(String taskId);

	public void submitTask(String taskId, String comment, int id, String name,String submitTask);

	public InputStream findImageInputStream(String deploymentId, String imageName);

	public ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	public Map<String, Object> findCoordingByTask(String taskId);

	public void deleteDeployment(String deploymentId, boolean b);

	public List<String> findOutComeListByTaskId(String taskId);

	public List<TreeMenu> findMenuList();
	
	public List<SysPermission> getSubMenu();

	
	

}
