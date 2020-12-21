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
		//1�����汨����
		bill.setCreatdate(new Date());
		bill.setState(1); //��ʾ������
		bill.setUserId(employee.getId());
		baoxiaobillMapper.insert(bill);	
		
		//2����������ʵ��
		Map<String, Object> map = new HashMap<String, Object>();	//${inputUser}
		map.put("inputUser", employee.getName());
		//��ҵ��������
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
		//��ǰ��Ӧ������ʵ��
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
		//����עҪָ��������
		Authentication.setAuthenticatedUserId(name);
		//1������ע
		taskService.addComment(taskId, processionstanceId, comment);
		if (submitMessage.equals("�ύ")) {
			//2���������
			taskService.complete(taskId);
			
		}else{
			Map<String, Object> map = new HashMap<String, Object>();	//${inputUser}
			map.put("message",submitMessage );
			taskService.complete(taskId,map);
			
		}
		
		//3���ж�����ʵ���Ƿ��Ѿ������������������ٵ���״̬��Ϊ2
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processionstanceId).singleResult();
		if (pi == null) {//���̽���
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
		//ʹ������ID����ѯ�������
		Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		//��ȡ���̶���ID
		String processDefinitionId = task.getProcessDefinitionId();
		//��ѯ���̶���Ķ���
		ProcessDefinition pd  = repositoryService.createProcessDefinitionQuery() //�������̶����ѯ���󣬶�Ӧ��act_re_procdef 
								.processDefinitionId(processDefinitionId) //ʹ�����̶���ID��ѯ
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


	@Override
	public void deleteDeployment(String deploymentId, boolean b) {
		    repositoryService.deleteDeployment(deploymentId, true);
		  
	}


	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		//���ش�����ߵ����Ƽ���
		List<String> list = new ArrayList<String>();
		//1:ʹ������ID����ѯ�������
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//2����ȡ���̶���ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3����ѯProcessDefinitionEntiy����
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		//ʹ���������Task��ȡ����ʵ��ID
		String processInstanceId = task.getProcessInstanceId();
		//ʹ������ʵ��ID����ѯ����ִ�е�ִ�ж������������ʵ������
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
					.processInstanceId(processInstanceId)//ʹ������ʵ��ID��ѯ
					.singleResult();
		//��ȡ��ǰ���id
		String activityId = pi.getActivityId();
		//4����ȡ��ǰ�Ļ
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5����ȡ��ǰ����֮�����ߵ�����
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				} else{
					list.add("Ĭ���ύ");
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
