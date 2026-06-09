package com.hsc.hsmartpicbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片更新请求，给管理员使用
 */
@Data
public class PictureUpdateRequest implements Serializable {  
  
      
    private Long id;  
  
      
    private String name;  
  
      
    private String introduction;  
  
      
    private String category;  
  
      
    private List<String> tags;  
  
    private static final long serialVersionUID = 1L;  
}
