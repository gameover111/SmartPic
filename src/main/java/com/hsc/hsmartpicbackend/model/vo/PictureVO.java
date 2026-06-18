package com.hsc.hsmartpicbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.hsc.hsmartpicbackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PictureVO implements Serializable {

    private String thumbnailUrl;

    private Long id;


    private String url;


    private String name;


    private String introduction;


    private List<String> tags;


    private String category;


    private Long picSize;


    private Integer picWidth;


    private Integer picHeight;


    private Double picScale;


    private String picFormat;


    private Long userId;


    private Date createTime;


    private Date editTime;


    private Date updateTime;

    /**
     * 空间 id
     */
    private Long spaceId;
    /**
     * 图片主色调
     */
    private String picColor;

    private UserVO user;

    /**
     * 权限列表
     */
    private List<String> permissionList = new ArrayList<>();

    private static final long serialVersionUID = 698131601132966880L;
  
      
    public static Picture voToObj(PictureVO pictureVO) {  
        if (pictureVO == null) {  
            return null;  
        }  
        Picture picture = new Picture();  
        BeanUtils.copyProperties(pictureVO, picture);  
        
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));  
        return picture;  
    }  
  
      
    public static PictureVO objToVo(Picture picture) {  
        if (picture == null) {  
            return null;  
        }  
        PictureVO pictureVO = new PictureVO();  
        BeanUtils.copyProperties(picture, pictureVO);  
        
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));  
        return pictureVO;  
    }  
}
