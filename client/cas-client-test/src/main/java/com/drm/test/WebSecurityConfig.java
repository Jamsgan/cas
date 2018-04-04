package com.drm.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.drm.cas.client.config.CasWebSecurityConfigBuilder;

/***
 * 
 * @ClassName WebSecurityConfig
 * @Description 测试： 加入cas的配置部分
 * @author James
 * @date 2018-04-04 14:14
 *
 */
@Configuration("casWebSecurityConfig")
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/**便携式配置*/
	@Autowired
	private CasWebSecurityConfigBuilder casWebSecurityConfigBuilder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();

		http.csrf().disable();

		http.authorizeRequests().anyRequest().authenticated().anyRequest().fullyAuthenticated();

		http.logout().permitAll();
		
		//传入http对象，以及该WebSecurityConfig的AuthenticationManager对象即可
		casWebSecurityConfigBuilder.appendCasWebSecurityConfig(http, authenticationManager());
	}

}
