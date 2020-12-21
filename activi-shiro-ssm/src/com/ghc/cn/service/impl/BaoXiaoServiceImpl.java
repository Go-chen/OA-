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
		
		//��ǰ��Ӧ������ʵ��
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
		//ʹ������ID����ѯ�������
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// ��ȡ���̶���ID
		String processDefinitionId = task.getProcessDefinitionId();
		// ��ѯ���̶���Ķ���
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery() // �������̶����ѯ���󣬶�Ӧ��act_re_procdef
				.processDefinitionId(processDefinitionId) // ʹ�����̶���ID��ѯ
				.singleResult();
		return pd;
	}


	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//�������
		Map<String, Object> map = new HashMap<String,Object>();
		//ʹ������ID����ѯ�������
		Task task = taskService.createTaskQuery()//
					.taskId(taskId)//ʹ������ID��ѯ
					.singleResult();
		//��ȡ���̶����ID
		String processDefinitionId = task.getProcessDefinitionId();
		//��ȡ���̶����ʵ����󣨶�Ӧ.bpmn�ļ��е����ݣ�
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
		//����ʵ��ID
		String processInstanceId = task.getProcessInstanceId();
		//ʹ������ʵ��ID����ѯ����ִ�е�ִ�ж������ȡ��ǰ���Ӧ������ʵ������
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//��������ʵ����ѯ
											.processInstanceId(processInstanceId)//ʹ������ʵ��ID��ѯ
											.singleResult();
		//��ȡ��ǰ���ID
		String activityId = pi.getActivityId();
		//��ȡ��ǰ�����
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//�ID
		//��ȡ����
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}

}
