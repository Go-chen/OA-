package com.ghc.cn.controller;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ghc.cn.pojo.Baoxiaobill;
import com.ghc.cn.service.BaoXiaoService;
import com.ghc.cn.utlis.Constans;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
public class BaoXiaoController {

	@Autowired
	private BaoXiaoService baoXiaoService;
	
	
	
	public static final int PAGE_SIZE = 5;
	
	
	
	
	@RequestMapping("/queryBaoXiaoBill")
	public ModelAndView queryBaoXiaoBill(
			@RequestParam(value="pageNum",required=false,defaultValue="1")int pageNum){
		//1、设置分页参数pageNum（页码），pageSize(每页记录数)，拦截请求重构sql语句
		PageHelper.startPage(pageNum,PAGE_SIZE);
		List<Baoxiaobill> list =  baoXiaoService.queryBaoXiaoBills();
		//2、创建分页bean
		PageInfo page = new PageInfo<>(list);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("BaoXiaoBill", list);
		mv.addObject("page", page);
		mv.setViewName("billList");
		
		return mv;
		
	}
	
	@RequestMapping("/deleteBaoXiao")
	public String  deleteBaoXiao(int baoXiaoId){
		baoXiaoService.deleteBaoXiao(baoXiaoId);
		return "redirect:/queryBaoXiaoBill";
	
	}
	
	@RequestMapping("/findBaoXiaoTaskForm")
	public ModelAndView findBaoXiaoTaskForm(String baoXiaoId){
		//1、根据任务id(流程数据库的数据)查询对应的审核单（业务表）的信息
		
	
		Baoxiaobill bill = baoXiaoService.findBillByTaskId(baoXiaoId);
	
		//查询任务批注
		List<Comment> commentList = baoXiaoService.findCommentListByTaskId(baoXiaoId);
		ModelAndView mv = new ModelAndView();
		mv.addObject("baoXiaoBill",bill );
		mv.addObject("commentList", commentList);
		mv.addObject("baoXiaoId", baoXiaoId);
		mv.setViewName("approve_baoxiao");
		
		return mv;
		}
	
	@RequestMapping("/viewCurrentImageByBill")
	public String viewCurrentImageByBill(Integer baoXiaoId,ModelMap model){
		String BUSSINESS_KEY = Constans.BAOXIAOBILL_KEY + "." +baoXiaoId;
		Task task = this.baoXiaoService.findTaskByBussinessKey(BUSSINESS_KEY);
		
		//1、获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = baoXiaoService.findProcessDefinitionByTaskId(task.getId());
		
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		//2、查看当前活动，获取当期活动对应的坐标，将四个值存放到Map<String,Object>中
		Map<String, Object> map = baoXiaoService.findCoordingByTask(task.getId());
		
		model.addAttribute("acs", map);
		return "viewimage";
		
		
	}
	
}





