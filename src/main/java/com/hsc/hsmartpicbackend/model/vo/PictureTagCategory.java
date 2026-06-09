package com.hsc.hsmartpicbackend.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PictureTagCategory {
    /**
     * 图片标签列表
     */
    private List<String> tagList;
    /**
     * 图片分类列表
     */
    private List<String> categoryList;
}
