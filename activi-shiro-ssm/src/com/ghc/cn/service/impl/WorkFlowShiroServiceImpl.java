package com.ghc.cn.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.ghc.cn.mapper.BaoxiaobillMapper;
import com.ghc.cn.mapper.SysPermissionCustomMapper;
import com.ghc.cn.pojo.Baoxiaobill;
import com.ghc.cn.pojo.BaoxiaobillExample;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.TreeMenu;
import com.ghc.cn.service.WorkFlowShiroService;
import com.ghc.cn.utlis.Constans;



@Service
public class WorkFlowShiroServiceImpl implements WorkFlowShiroService{

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService ;
	@Autowired
	private FormService formService ;
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private SysPermissionCustomMapper sysPermissionCustomMapper;

	
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper ;
	
	@Override
	public void deployProcess(InputStream input, String fileName) {
		ZipInputStream zip = new ZipInputStream(input);	
		repositoryService.createDeployment()
								 .name(fileName)
								 .addZipInputStream(zip)
								 .deploy();
		
	}


	@Override
	public List<Deployment> findAllDeployments() {
		
		return repositoryService.createDeploymentQuery().list();
	}


	@Override
	public List<ProcessDefinition> findAllProcessDefinition() {
		return repositoryService.createProcessDefinitionQuery().list();
	}


	@Override
	public void saveLeaveAndStartProcess(Baoxiaobill bill, Employee employee) {
		//1、保存报销单
		bill.setCreatdate(new Date());
		bill.setState(1); //表示审批中
		bill.setUserId(employee.getId());
		baoxiaobillMapper.insert(bill);	
		
		//2、启动流程实例
		Map<String, Object> map = new HashMap<String, Object>();	//${inputUser}
		map.put("inputUser", employee.getName());
		//把业务表的数据
		String bussiness_key =Constans.BAOXIAOBILL_KEY + "." +bill.getId();
		runtimeService.startProcessInstanceByKey(Constans.BAOXIAOBILL_KEY, bussiness_key, map);
		
	}



	@Override
	public List<Task> findMyTaskListByUserId(String name) {
		return taskService.createTaskQuery().taskAssignee(name).list();
	}



	@Override
	public Baoxiaobill findBillByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//当前对应的流程实例
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		String bussiness_key = pi.getBusinessKey();	//baoxiao.22
		System.out.println(bussiness_key);
		
		String billId = bussiness_key.split("\\.")[1];
		Baoxiaobill bill = baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(billId));
		return bill;
	}


	@Override
	public List<Comment> findCommentListByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		List<Comment> list = taskService.getProcessInstanceComments(task.getProcessInstanceId());
		return list;
	}


	@Override
	public void submitTask(String taskId, String comment, int id, String name,String submitMessage) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processionstanceId = task.getProcessInstanceId();
		//加批注要指定代办人
		Authentication.setAuthenticatedUserId(name);
		//1、加批注
		taskService.addComment(taskId, processionstanceId, comment);
		if (submitMessage.equals("提交")) {
			//2、完成任务
			taskService.complete(taskId);
			
		}else{
			Map<String, Object> map = new HashMap<String, Object>();	//${inputUser}
			map.put("message",submitMessage );
			taskService.complete(taskId,map);
			
		}
		
		//3、判断流程实例是否已经结束，如果结束，请假单的状态改为2
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processionstanceId).singleResult();
		if (pi == null) {//流程结束
			Baoxiaobill bill = baoxiaobillMapper.selectByPrimaryKey(id);
			bill.setState(2);
			baoxiaobillMapper.updateByPrimaryKey(bill);
		} 
		
	}


	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}


	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		//使用任务ID，查询任务对象
		Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd  = repositoryService.createProcessDefinitionQuery() //创建流程定义查询对象，对应表act_re_procdef 
								.processDefinitionId(processDefinitionId) //使用流程定义ID查询
								.singleResult();
		return pd;
	}


	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//存放坐标
				Map<String, Object> map = new HashMap<String,Object>();
				//使用任务ID，查询任务对象
				Task task = taskService.createTaskQuery()//
							.taskId(taskId)//使用任务ID查询
							.singleResult();
				//获取流程定义的ID
				String processDefinitionId = task.getProcessDefinitionId();
				//获取流程定义的实体对象（对应.bpmn文件中的数据）
				ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
				//流程实例ID
				String processInstanceId = task.getProcessInstanceId();
				//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
				ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
													.processInstanceId(processInstanceId)//使用流程实例ID查询
													.singleResult();
				//获取当前活动的ID
				String activityId = pi.getActivityId();
				//获取当前活动对象
				ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
				//获取坐标
				map.put("x", activityImpl.getX());
				map.put("y", activityImpl.getY());
				map.put("width", activityImpl.getWidth());
				map.put("height", activityImpl.getHeight());
				return map;
			}


	@Override
	public void deleteDeployment(String deploymentId, boolean b) {
		    repositoryService.deleteDeployment(deploymentId, true);
		  
	}


	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		//1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		//使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的id
		String activityId = pi.getActivityId();
		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				} else{
					list.add("默认提交");
				}
			}
		}
		return list;
	
		
	}

	public List<TreeMenu> findMenuList(){
		return sysPermissionCustomMapper.findMenuList();
		
	};
	
	public List<SysPermission> getSubMenu(){
		return sysPermissionCustomMapper.getSubMenu();
		
	};
	

	
	
}
