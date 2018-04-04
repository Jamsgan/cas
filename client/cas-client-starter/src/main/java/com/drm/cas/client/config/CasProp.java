package com.drm.cas.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:Cas配置JavaBean
 * @author James
 * @date 2018/03/19
 */
@ConfigurationProperties
public class CasProp {

  /** 服务端地址 */
  @Value("${cas.server.host.url}")
  private String casServerUrl;
  /** 服务端登录请求地址 */
  @Value("${cas.server.host.url}${cas.server.host.login_url:/clogin}")
  private String casServerLoginUrl;
  /** 服务端登出请求地址 */
  @Value("${cas.server.host.url}${cas.server.host.logout_url:/clogout}?service=${app.server.host.url}")
  private String casServerLogoutUrl;
  /** 本应用地址 */
  @Value("${app.server.host.url}")
  private String appServerUrl;
  /** 本应用登出地址 */
  @Value("${app.login.url:/login}")
  private String appLoginUrl;
  /** 本应用登出地址 */
  @Value("${app.logout.url:/logout}")
  private String appLogoutUrl;

  public String getCasServerUrl() {
    return casServerUrl;
  }

  public void setCasServerUrl(String casServerUrl) {
    this.casServerUrl = casServerUrl;
  }

  public String getCasServerLoginUrl() {
    return casServerLoginUrl;
  }

  public void setCasServerLoginUrl(String casServerLoginUrl) {
    this.casServerLoginUrl = casServerLoginUrl;
  }

  public String getCasServerLogoutUrl() {
    return casServerLogoutUrl;
  }

  public void setCasServerLogoutUrl(String casServerLogoutUrl) {
    this.casServerLogoutUrl = casServerLogoutUrl;
  }

  public String getAppServerUrl() {
    return appServerUrl;
  }

  public void setAppServerUrl(String appServerUrl) {
    this.appServerUrl = appServerUrl;
  }

  public String getAppLoginUrl() {
    return appLoginUrl;
  }

  public void setAppLoginUrl(String appLoginUrl) {
    this.appLoginUrl = appLoginUrl;
  }

  public String getAppLogoutUrl() {
    return appLogoutUrl;
  }

  public void setAppLogoutUrl(String appLogoutUrl) {
    this.appLogoutUrl = appLogoutUrl;
  }

}
