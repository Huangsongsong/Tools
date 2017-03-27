package com.huangss.Filter;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用动态代理实行全站编码过滤
 * @author 黄松松
 *
 */
public class MyProxyEncodingFilter implements Filter {


	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		//类型转换
		final HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		//如果请求方式是post请求
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		
		//创建一个代理类对象
		HttpServletRequest reqProxy = (HttpServletRequest)Proxy.newProxyInstance(req.getClass().getClassLoader(),
				req.getClass().getInterfaces(), new InvocationHandler() {
					
				public Object invoke(Object proxy, Method method, Object[] args)
							throws Throwable {
						//method表示request中的所有方法，这里只需要处理getparameter方法
						String methodName = method.getName();
						if("getParameter".equals(methodName)){
							String value = req.getParameter((String)args[0]);
							
							return new String(value.getBytes("iso8859-1"),
									"utf-8");
						}
						
						return method.invoke(req, args);
					}
				});
		
		chain.doFilter(reqProxy, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
