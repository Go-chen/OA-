package com.ghc.cn.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghc.cn.mapper.BaoxiaobillMapper;
import com.ghc.cn.pojo.Baoxiaobill;
import com.ghc.cn.service.BaoXiaoService;
import com.ghc.cn.utlis.Constans;

@Service
public class BaoXiaoServiceImpl implements BaoXiaoService{

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
	private BaoxiaobillMapper baoxiaobillMapper;
	
	@Override
	public void deleteBaoXiao(int baoXiaoId) {
		baoxiaobillMapper.deleteByPrimaryKey(baoXiaoId);
		
	}
	
	
	@Override
	public List<Baoxiaobill> queryBaoXiaoBills() {
		List<Baoxiaobill> list = baoxiaobillMapper.selectByExample(null);
		return list;
		
	}


	@Override
	public Baoxiaobill findBillByTaskId(String baoXiaoId) {
	//	String business_key = Constans.BAOXIAOBILL_KEY + "." +baoXiaoId;
	//	Task task = taskService.createTaskQuery().processInstanceBusinessKey(business_key).singleResult();
		
		//当前对应的流程实例
		//ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//		String bussiness_key = pi.getBusinessKey();  //baoxiao.12
//		System.out.println(bussiness_key);
//		
//		String billId = bussiness_key.split("\\.")[1];
		Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(baoXiaoId));
		
		return baoxiaobill;
	}


	@Override
	public List<Comment> findCommentListByTaskId(String taskId) {
		String bussiness_key = Constans.BAOXIAOBILL_KEY +"."+taskId;
		HistoricProcessInstance pi = this.historyService.createHistoricProcessInstanceQuery()
													.processInstanceBusinessKey(bussiness_key).singleResult();	
		List<Comment> commentList = this.taskService.getProcessInstanceComments(pi.getId());
		
		return commentList;
	}


	@Override
	public Task findTaskByBussinessKey(String BUSSINESS_KEY) {
		
		return taskService.createTaskQuery().processInstanceBusinessKey(BUSSINESS_KEY).singleResult();
	}


	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery() // 创建流程定义查询对象，对应表act_re_procdef
				.processDefinitionId(processDefinitionId) // 使用流程定义ID查询
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

}
