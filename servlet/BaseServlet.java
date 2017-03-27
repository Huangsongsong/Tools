package com.huangss.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现一个servlet可以自定义不同的方法处理请求，
 * 简化 转发和重定向，
 * 解决了编码问题
 * @author 黄松松
 *
 */
public class BaseServlet extends HttpServlet{

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 解决乱码问题，使用修饰者模式,对request进行功能增强
		 */
		if(request.getMethod().equalsIgnoreCase("get")){
			//自定义一个request类，对其中的getParameter()和getParameterMap()方法进行增强
			//解决GET请求的编码问题
			request = new MyHttpServletRequest(request);
		}else if(request.getMethod().equalsIgnoreCase("post")){
			//解决POST请求的编码问题
			request.setCharacterEncoding("utf-8");
		}
		response.setContentType("text/html;charset=utf-8");//解决相应编码问题
		
		//得到请求路径的参数值
		String methodName = request.getParameter("method");
		
		Method method =null;
		try {
			//通过反射获取方法对用的Method对象
			//this表示当前类对象
			method = this.getClass().getMethod(methodName, HttpServletRequest.class,
					HttpServletResponse.class);
		} catch (Exception e) {
			throw new RuntimeException("您调用的方法不存在", e);
		}
		
		try {
			//执行对应的方法,得到该方法的返回值，
			String value = (String)method.invoke(this, request, response);
			//value="f:/index.jsp"或者"r:/TestFilter/index.jsp"
			//分解字符串，进行转发或重定向
			String[] path = value.split(":");
			if(path[0].equals("f")){
				request.getRequestDispatcher(path[1]).forward(request, response);
			}else if(path[0].equals("r")){
				response.sendRedirect(request.getContextPath() + path[1]);
			}
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
}

//增强的Request
//HttpServletRequestWrapper重写了HttpServletRequest的所有方法
//除了getParameter()和getParameterMap()，其他方法均使用原来的request
class MyHttpServletRequest extends HttpServletRequestWrapper{
	private HttpServletRequest request = null;
	
	public MyHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
	}
	
	@Override
	public String getParameter(String name) {
		String value = request.getParameter(name);
		if(value == null ) return null;
		
		try {
			return new String(value.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = request.getParameterMap();
		if(map == null) return null;
		
		for(String key : map.keySet()){
			String[] values = map.get(key);
			for(int i =0; i <values.length; i++){
				try {
					values[i] = new String(values[i].getBytes("iso-8859-1"), "utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
			map.put(key, values);
		}
		return map;
	}
}
