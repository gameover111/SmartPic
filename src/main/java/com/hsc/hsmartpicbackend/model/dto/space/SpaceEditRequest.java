package com.hsc.hsmartpicbackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑空间请求
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
    /**
     * 空间最大容量
     */
    private Long maxSize;

    /**
     * 空间最大图片数量
     */
    private Long maxCount;

    private static final long serialVersionUID = 1L;
}