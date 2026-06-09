package com.hsc.hsmartpicbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsc.hsmartpicbackend.model.dto.picture.PictureQueryRequest;
import com.hsc.hsmartpicbackend.model.dto.picture.PictureUploadRequest;
import com.hsc.hsmartpicbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hsc.hsmartpicbackend.model.entity.User;
import com.hsc.hsmartpicbackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author hsc
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-06-08 18:21:12
*/
public interface PictureService extends IService<Picture> {
    /**
     * 校验图片
     * @param picture 图片
     */
    void validPicture(Picture picture);
    /**
     * 上传图片
     * @param multipartFile 图片文件
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser 登录用户
     * @return 图片VO
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

       /**
     * 获取图片VO
     * @param picture 图片
     * @param request 请求
     * @return 图片VO
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片VO分页
     * @param picturePage 图片分页
     * @param request 请求
     * @return 图片VO分页
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest 图片查询请求
     * @return 查询条件包装器
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

}
