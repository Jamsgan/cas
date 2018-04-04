package com.drm.cas.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.drm.cas.client.user.MapUserDetails;

/**
 * @ClassName CasTokenUserDetailsService
 * @Description 解析CasAssertionAuthenticationToken成MapUserDetails（可以自定义覆盖）
 *              {@link com.drm.cas.client.user.MapUserDetails} 
 * @author James
 * @date 2018-03-27 13:40
 *
 */
public class CasTokenUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
	private static final List<GrantedAuthority> EMPTY_GRANTEDAUTHORITY = new ArrayList<GrantedAuthority>();
	private static final Logger logger = LoggerFactory.getLogger(CasTokenUserDetailsService.class);

	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		Map<String, Object> attributes = null;
		String name = token.getName();
		logger.debug(String.format(" The CasAssertionAuthenticationToken['name'] : {}", name));
		try {
			attributes = token.getAssertion().getPrincipal().getAttributes();
			logger.debug(String.format(" The Authentication['Principal']['Attribute'] : {}", attributes));
		} catch (Exception e) {
			logger.error(" LoadUserDetails fail :{}", e.getCause());
		}
		return new MapUserDetails(new User(name, "", EMPTY_GRANTEDAUTHORITY), attributes);
	}

}
