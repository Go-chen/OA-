package com.ghc.cn.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ghc.cn.pojo.Baoxiaobill;
import com.ghc.cn.pojo.Employee;
import com.ghc.cn.service.WorkFlowShiroService;
import com.ghc.cn.utlis.Constans;
import com.mysql.jdbc.StringUtils;

@Controller
public class WorkFlowShiroController {
	
	@Autowired
	private WorkFlowShiroService workFlowShiroService ;
	

	
	@RequestMapping("/deployProcess")
	public String deployProcess(String processName,MultipartFile processFile){
	
		try {
			workFlowShiroService.deployProcess(processFile.getInputStream(), processName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "redirect:/processDefinitionList";
		
	}
	
	@RequestMapping("/processDefinitionList")
	public ModelAndView processDefinitionList(){
		List<Deployment> depList = workFlowShiroService.findAllDeployments();
		List<ProcessDefinition> processList = workFlowShiroService.findAllProcessDefinition();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("depList",depList);
		mv.addObject("pdList",processList);
		mv.setViewName("workflow_list");
		return mv;
		
	}

	@RequestMapping("/saveStartBao")
	public String saveStartBao(Baoxiaobill bill,HttpSession session){
		//Employee employee = (Employee) session.getAttribute(Constans.GLOBAL_SESSION_ID);
		Employee employee = (Employee) SecurityUtils.getSubject().getPrincipal();
		workFlowShiroService.saveLeaveAndStartProcess(bill,employee);
		
		return "redirect:/myTaskList";	//请假单保存并提交后跳转到我的待办事务
		
	}
	
	@RequestMapping("/myTaskList")
	public ModelAndView myTaskList(HttpSession session){
		//Employee user = (Employee) session.getAttribute(Constans.GLOBAL_SESSION_ID);
		Employee user = (Employee) SecurityUtils.getSubject().getPrincipal();
		List<Task> list = workFlowShiroService.findMyTaskListByUserId(user.getName());
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("taskList",list);
		
		mv.setViewName("workflow_task");
		return mv;
		
	}
	
	@RequestMapping("/viewTaskForm")
	public ModelAndView viewTaskForm(String taskId){
		
		//1、根据任务的ID（流程数据库的数据）查询对应的请假单（业务表的数据）的信息
		Baoxiaobill bill = workFlowShiroService.findBillByTaskId(taskId);
		
		//查询任务批注
		List<Comment> commentList = workFlowShiroService.findCommentListByTaskId(taskId);
		
		//---->连线，生成四个按钮
		List<String> lianXianList = workFlowShiroService.findOutComeListByTaskId(taskId);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("bill", bill);
		mv.addObject("commentList", commentList);
		mv.addObject("taskId", taskId);
		mv.addObject("lianXianList", lianXianList);
		mv.setViewName("approve_leave");
		
		return mv;
		
	}
	
	@RequestMapping("/submitTask")
	public String  submitTask(int id,String taskId,String comment,HttpSession session,String submitMessage){
		//Employee employee = (Employee) session.getAttribute(Constans.GLOBAL_SESSION_ID);
		Employee employee = (Employee) SecurityUtils.getSubject().getPrincipal();
		workFlowShiroService.submitTask(taskId,comment,id,employee.getName(), submitMessage);
		return "redirect:/myTaskList";
		
	}
	
	//生成流程定义图片
	@RequestMapping("/viewImage")
	public String  viewImage(String deploymentId,String imageName,HttpServletResponse response) throws IOException {
		InputStream in = workFlowShiroService.findImageInputStream(deploymentId, imageName);
		
		OutputStream out = response.getOutputStream();
		
		//4：将输入流中的数据读取出来，写到输出流中
		for(int b=-1;(b=in.read())!=-1;){
			out.write(b);
		}
		out.close();
		in.close();
		
		return null;
	}
	
	//查看当前活动节点的流程图
	@RequestMapping("/viewCurrentImage")
	public ModelAndView  viewCurrentImage(String taskId) {
		/**一：查看流程图*/
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowShiroService.findProcessDefinitionByTaskId(taskId);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("deploymentId", pd.getDeploymentId());
		mv.addObject("imageName", pd.getDiagramResourceName());
		/**
		 * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		Map<String, Object> map = workFlowShiroService.findCoordingByTask(taskId);

		mv.addObject("acs", map);
		mv.setViewName("viewimage");
		return mv;
	}
	
	@RequestMapping("/delDeployment")
	public String  delDeployment(String deploymentId){
		  workFlowShiroService.deleteDeployment(deploymentId, true);
		  return "redirect:/processDefinitionList" ;
	}
	
	
	
}
