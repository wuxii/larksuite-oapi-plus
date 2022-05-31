package com.harmony.lark.spring.autoconfig;

import com.larksuite.oapi.core.AppType;
import com.larksuite.oapi.core.Domain;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxin
 */
@ConfigurationProperties("lark")
public class LarkApiProperties {

    private String appId;
    private String appSecret;
    private AppType appType = AppType.Internal;
    private Domain domain = Domain.FeiShu;
    private String encryptKey;
    private String verificationToken = "";
    private int pageSize = 20;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
