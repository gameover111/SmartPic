package com.hsc.hsmartpicbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片 id
     */
    private Long id;
    /**
     * 图片url
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;

    private String category;        // 拓展：统一分类
    private List<String> tags;      // 拓展：统一标签列表

    private static final long serialVersionUID = 3077116915266249108L;

}
