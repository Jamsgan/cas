package com.drm.cas.client.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;

/**
 * 
 * @ClassName CasClientAutoConfiguration
 * @Description 自动配置类
 * @author James
 * @date 2018-04-04 11:12
 *
 */
@Configuration
@ComponentScan("com.drm.cas.client.config")
@EnableConfigurationProperties(CasProp.class)
@ConditionalOnProperty(name = "cas.client.enable", havingValue = "true", matchIfMissing = false)
public class CasClientAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(CasClientAutoConfiguration.class);

	private final CasProp prop;

	public CasClientAutoConfiguration(CasProp properties) {
		this.prop = properties;
	}

	/**
	 * 认证的入口，即跳转至服务端的cas地址
	 * <p>
	 * <b>Note:</b>浏览器访问不可直接填客户端的login请求,若如此则会返回Error页面，无法被此入口拦截
	 * </p>
	 */
	@Bean
	@Primary
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
		casAuthenticationEntryPoint.setLoginUrl(prop.getCasServerLoginUrl());
		if (logger.isDebugEnabled()) {
			logger.debug("Set casAuthenticationEntryPoint loginUrl: {}.", prop.getCasServerLoginUrl());
		}
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties);
		return casAuthenticationEntryPoint;
	}

	// 对所有的未拥有ticket的访问均需要验证
	@Value("${cas.client.authenticateAllArtifacts:true}")
	private boolean authenticateAllArtifacts;

	/**
	 * 设置客户端service的属性
	 * <p>
	 * 主要设置请求cas服务端后的回调路径,一般为主页地址，不可为登录地址
	 * 
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		// 设置回调的service路径，此为主页路径
		String service = prop.getAppServerUrl() + prop.getAppLoginUrl();
		serviceProperties.setService(service);
		serviceProperties.setAuthenticateAllArtifacts(authenticateAllArtifacts);
		if (logger.isDebugEnabled()) {
			logger.debug("Set serviceProperties Service: {} and AuthenticateAllArtifacts: {}.", service,
					authenticateAllArtifacts);
		}
		return serviceProperties;
	}

	@Value("${cas.client.casAuthenticationProvider.key:casAuthenticationProviderKey}")
	private String key;

	/**
	 * 创建cas校验类
	 * 
	 * <p>
	 * <b>Notes:</b> TicketValidator、AuthenticationUserDetailService属性必须设置;
	 * serviceProperties属性主要应用于ticketValidator用于去cas服务端检验ticket
	 * </p>
	 * 
	 * @return
	 */
	@Bean
	public CasAuthenticationProvider casAuthenticationProvider(
			@Qualifier("casTokenUserDetailsService") AuthenticationUserDetailsService<CasAssertionAuthenticationToken> aService,
			ServiceProperties properties, TicketValidator ticketValidator) {
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setAuthenticationUserDetailsService(aService);
		casAuthenticationProvider.setServiceProperties(properties);
		casAuthenticationProvider.setTicketValidator(ticketValidator);
		casAuthenticationProvider.setKey(key);
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Set CasAuthenticationProvider AuthenticationUserDetailsService: {}; ServiceProperties: {}; TicketValidator: {}, Key: {}.",
					aService, properties, ticketValidator, key);
		}
		return casAuthenticationProvider;
	}

	/**
	 * 配置ticket校验器
	 * 
	 * @return
	 */
	@Bean
	public TicketValidator cas20ServiceTicketValidator() {
		Cas20ServiceTicketValidator cas20ServiceTicketValidator = new Cas20ServiceTicketValidator(
				prop.getCasServerUrl());
		cas20ServiceTicketValidator.setEncoding("UTF8");
		if (logger.isDebugEnabled()) {
			logger.debug("Set TicketValidator:{} with Url{}.", cas20ServiceTicketValidator, prop.getCasServerUrl());
		}
		return cas20ServiceTicketValidator;
	}

	@Bean
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
	 * 监听SingleSignOutFilter 的session类型进行不同的session处理。
	 * 
	 * @return
	 */
	@Bean
	public SingleSignOutHttpSessionListener singleSignOutHttpSessionListener() {
		return new SingleSignOutHttpSessionListener();
	}

	@Bean(name = "casTokenUserDetailsService")
	@ConditionalOnMissingBean(AuthenticationUserDetailsService.class)
	public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> casTokenUserDetailsService() {
		return new CasTokenUserDetailsService();
	}

}
