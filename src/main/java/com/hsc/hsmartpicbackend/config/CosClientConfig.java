package com.hsc.hsmartpicbackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cos.client")  
@Data  
public class CosClientConfig {  
  
      
    private String host;  
  
      
    private String secretId;  
  
      
    private String secretKey;  
  
      
    private String region;  
  
      
    private String bucket;  
  
    @Bean  
    public COSClient cosClient() {  
        // 初始化COSClient-认证信息
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);  
        // 初始化COSClient-区域信息
        ClientConfig clientConfig = new ClientConfig(new Region(region));  
        // 初始化COSClient-返回COSClient实例
        return new COSClient(cred, clientConfig);  
    }  
}