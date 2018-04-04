package com.drm.cas.client.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @ClassName MapUserDetails
 * @Description 添加 Attributes信息的UserDetails
 * @author James
 * @date 2018-04-04 10:55
 */
public class MapUserDetails implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	/** 额外信息 */
	private final Map<String, Object> principalAttributes;
	/** 委托用户 */
	private final UserDetails delegate;

	public MapUserDetails(UserDetails user, Map<String, Object> attributes) {
		this.delegate = user;
		this.principalAttributes = attributes != null ? attributes : new HashMap<String, Object>();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return delegate.getAuthorities();
	}

	public String getUsername() {
		return delegate.getUsername();
	}

	public boolean isAccountNonExpired() {
		return delegate.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return delegate.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return delegate.isCredentialsNonExpired();
	}

	public boolean isEnabled() {
		return delegate.isEnabled();
	}

	public String getPassword() {
		return delegate.getPassword();
	}

	public Map<String, Object> getPrincipalAttributes() {
		return Collections.unmodifiableMap(principalAttributes);
	}

}
