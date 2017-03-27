package com.huangss.Filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现全站编码过滤（修饰者模式）
 * @author 黄松松
 *
 */
public class MyEncodingFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		//类型转换
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		//如果请求方式是post请求
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		
		//如果是Get请求，将req进行功能增强
		MyHttpServletRequest myReq = new MyHttpServletRequest(req);
		
		chain.doFilter(myReq, resp);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}

/**
 * HttpServletRequestWrapper对HttpServletRequest接口进行了实现，重写内部所有方法
 * @author 黄松松
 *
 */
class MyHttpServletRequest extends HttpServletRequestWrapper{

	public MyHttpServletRequest(HttpServletRequest request) {
		super(request);
	} 
	
	//重写getParameter()方法，对get请求进行处理
	@Override
	public String getParameter(String name){
		String value = super.getParameter(name);
		if(value == null) return null;
		
		try {
			return new String(value.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = super.getParameterMap();
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
