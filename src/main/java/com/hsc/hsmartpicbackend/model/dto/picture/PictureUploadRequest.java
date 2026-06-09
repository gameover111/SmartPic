package com.hsc.hsmartpicbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片 id
     */
    private Long id;

    private static final long serialVersionUID = 3077116915266249108L;

}
