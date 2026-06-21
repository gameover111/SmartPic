package com.hsc.hsmartpicbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户编辑个人信息请求（用户自身使用）
 */
@Data
public class UserEditRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
