package com.ghc.cn.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


public class CustomExceptionResolver implements HandlerExceptionResolver  {

	//前端控制器DispatcherServlet在进行HandlerMapping、调用HandlerAdapter执行Handler过程中，如果遇到异常就会执行此方�?
	//handler�?终要执行的Handler，它的真实身份是HandlerMethod
	//Exception ex就是接收到异常信�?
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		//输出异常
		ex.printStackTrace();
		
		//统一异常处理代码
		//针对系统自定义的CustomException异常，就可以直接从异常类中获取异常信息，将异常处理在错误页面展示
		//异常信息
		String message = null;
		CustomException customException = null;
		//如果ex是系�? 自定义的异常，直接取出异常信�?
		if(ex instanceof CustomException){
			customException = (CustomException)ex;
		}else{
			//针对非CustomException异常，对这类重新构�?�成�?个CustomException，异常信息为“未知错误�??
			customException = new CustomException("未知错误");
		}
		
		//错误 信息
		message = customException.getMessage();
		
		request.setAttribute("message", message);

		
		try {
			//转向到错�? 页面
			request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView();
	}

}
