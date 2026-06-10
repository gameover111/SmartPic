package com.hsc.hsmartpicbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 抓取词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 图片名称前缀
     */
    private String namePrefix;

    private String category;        // 拓展：统一分类
    private List<String> tags;      // 拓展：统一标签列表

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 4001075363060132914L;

}
