package com.drm.cas.client.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName CasWebSecurityConfigBuilder
 * @Description WebSecurity配置类
 * @author James
 * @date 2018-04-04 13:21
 *
 */
@Component
public class CasWebSecurityConfigBuilder {

	private static Logger logger = LoggerFactory.getLogger(CasWebSecurityConfigBuilder.class);
	@Autowired
	private CasProp prop;
	@Autowired
	private AuthenticationEntryPoint casAuthenticationEntryPoint;
	@Value("${authox.web.main:/main.html}")
	private String mainPage;

	public void appendCasWebSecurityConfig(HttpSecurity http, AuthenticationManager authenticationManager)
			throws Exception {
		// 定义认证异常处理器
		http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint).and()
				.addFilter(casAuthenticationFilter(authenticationManager))
				.addFilterBefore(casLogoutFilter(), LogoutFilter.class)
				.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
	}

	public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager)
			throws Exception {
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager);
		casAuthenticationFilter.setFilterProcessesUrl(prop.getAppLoginUrl());// 设置需要拦截的url
		casAuthenticationFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler());// 登录成功后的处理器
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Set CasAuthenticationFilter with AuthenticationManager: {}; FilterProcessesUrl: {}; AuthenticationSuccessHandler: SimpleUrlAuthenticationSuccessHandler with {}.",
					authenticationManager, prop.getAppLoginUrl(), mainPage);
		}
		return casAuthenticationFilter;
	}

	/**
	 * 单点注销，接受cas服务端发出的注销session请求
	 * 
	 * @see SingleLogout(SLO) Front or Back Channel
	 * 
	 * @return
	 */
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		// 设置cas服务端路径前缀，应用于front channel的注销请求
		singleSignOutFilter.setCasServerUrlPrefix(prop.getCasServerUrl());
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		if (logger.isDebugEnabled()) {
			logger.debug("Set SingleSignOutFilter with CasServerUrlPrefix: {}.", prop.getCasServerUrl());
		}
		return singleSignOutFilter;
	}

	/**
	 * 单点请求cas客户端退出Filter类
	 * 
	 * 请求/logout，转发至cas服务端进行注销
	 */
	public LogoutFilter casLogoutFilter() {
		// 设置回调地址，以免注销后页面不再跳转
		LogoutFilter logoutFilter = new LogoutFilter(prop.getCasServerLogoutUrl(), new SecurityContextLogoutHandler());

		logoutFilter.setFilterProcessesUrl(prop.getAppLogoutUrl());
		if (logger.isDebugEnabled()) {
			logger.debug("Set LogoutFilter with LogoutSuccessUrl: {} ; LogoutHandler: {} ; FilterProcessesUrl: {}.",
					prop.getCasServerLogoutUrl(), SecurityContextLogoutHandler.class, prop.getAppLogoutUrl());
		}
		return logoutFilter;
	}

}
