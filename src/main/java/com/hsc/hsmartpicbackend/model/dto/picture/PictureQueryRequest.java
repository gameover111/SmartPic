package com.hsc.hsmartpicbackend.model.dto.picture;

import com.hsc.hsmartpicbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 图片查询请求，需要继承公共包中的 PageRequest 来支持分页查询
 */
@EqualsAndHashCode(callSuper = true)
@Data  
public class PictureQueryRequest extends PageRequest implements Serializable {  
  
      
    private Long id;  
  
      
    private String name;  
  
      
    private String introduction;  
  
      
    private String category;  
  
      
    private List<String> tags;  
  
      
    private Long picSize;  
  
      
    private Integer picWidth;  
  
      
    private Integer picHeight;  
  
      
    private Double picScale;  
  
      
    private String picFormat;  
  
      
    private String searchText;  
  
      
    private Long userId;
    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /*
     * 开始编辑时间
     */
    private Date startEditTime;

    /*
     * 结束编辑时间
     */
    private Date endEditTime;
    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 是否只查询 spaceId 为 null 的数据
     */
    private boolean nullSpaceId;
  
    private static final long serialVersionUID = 1L;  
}