package com.ghc.cn.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.ghc.cn.pojo.Baoxiaobill;

public interface BaoXiaoService {

	public void deleteBaoXiao(int baoXiaoId);

	public List<Baoxiaobill> queryBaoXiaoBills();

	public Baoxiaobill findBillByTaskId(String taskId);

	public List<Comment> findCommentListByTaskId(String taskId);

	public Task findTaskByBussinessKey(String bUSSINESS_KEY);

	public ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	public Map<String, Object> findCoordingByTask(String taskId);
	
	
}
