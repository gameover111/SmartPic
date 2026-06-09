package com.hsc.hsmartpicbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片修改请求，一般情况下给普通用户使用，可修改的字段范围小于更新请求
 */
@Data
public class PictureEditRequest implements Serializable {  
  
      
    private Long id;  
  
      
    private String name;  
  
      
    private String introduction;  
  
      
    private String category;  
  
      
    private List<String> tags;  
  
    private static final long serialVersionUID = 1L;  
}