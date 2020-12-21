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
		//1�����÷�ҳ����pageNum��ҳ�룩��pageSize(ÿҳ��¼��)�����������ع�sql���
		PageHelper.startPage(pageNum,PAGE_SIZE);
		List<Baoxiaobill> list =  baoXiaoService.queryBaoXiaoBills();
		//2��������ҳbean
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
		//1����������id(�������ݿ������)��ѯ��Ӧ����˵���ҵ�������Ϣ
		
	
		Baoxiaobill bill = baoXiaoService.findBillByTaskId(baoXiaoId);
	
		//��ѯ������ע
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
		
		//1����ȡ����ID����ȡ�������ʹ����������ȡ���̶���ID����ѯ���̶������
		ProcessDefinition pd = baoXiaoService.findProcessDefinitionByTaskId(task.getId());
		
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		//2���鿴��ǰ�����ȡ���ڻ��Ӧ�����꣬���ĸ�ֵ��ŵ�Map<String,Object>��
		Map<String, Object> map = baoXiaoService.findCoordingByTask(task.getId());
		
		model.addAttribute("acs", map);
		return "viewimage";
		
		
	}
	
}





