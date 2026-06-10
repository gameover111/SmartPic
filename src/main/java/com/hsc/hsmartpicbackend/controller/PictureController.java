package com.hsc.hsmartpicbackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsc.hsmartpicbackend.annotation.AuthCheck;
import com.hsc.hsmartpicbackend.common.BaseResponse;
import com.hsc.hsmartpicbackend.common.DeleteRequest;
import com.hsc.hsmartpicbackend.common.ResultUtils;
import com.hsc.hsmartpicbackend.constant.UserConstant;
import com.hsc.hsmartpicbackend.exception.BusinessException;
import com.hsc.hsmartpicbackend.exception.ErrorCode;
import com.hsc.hsmartpicbackend.exception.ThrowUtils;
import com.hsc.hsmartpicbackend.manager.CosManager;
import com.hsc.hsmartpicbackend.model.dto.picture.*;
import com.hsc.hsmartpicbackend.model.entity.Picture;
import com.hsc.hsmartpicbackend.model.entity.User;
import com.hsc.hsmartpicbackend.model.enums.PictureReviewStatusEnum;
import com.hsc.hsmartpicbackend.model.vo.PictureTagCategory;
import com.hsc.hsmartpicbackend.model.vo.PictureVO;
import com.hsc.hsmartpicbackend.service.PictureService;
import com.hsc.hsmartpicbackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;

    /**
     * 上传图片
     * @param multipartFile 图片文件
     * @param pictureUploadRequest 图片上传请求
     * @param request HttpServletRequest
     * @return 图片VO
     */
    @PostMapping("/upload")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 上传图片（url上传）
     *
     * @param pictureUploadRequest 图片上传请求
     * @param request              HttpServletRequest
     * @return 图片VO
     */
    @PostMapping("/upload/url")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }
    // ==================== 删除图片 ====================
    /**
     * 删除图片（仅图片所有者或管理员可操作）
     * @param deleteRequest 包含图片id的请求体
     * @param request       Http请求对象，用于获取当前登录用户
     * @return 删除成功返回true，失败抛出异常
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1. 参数校验：删除请求对象不能为空，且图片id必须大于0
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        // 3. 查询待删除的图片是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 4. 权限校验：只有图片的创建者或管理员才能删除
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 5. 执行删除操作
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // ==================== 更新图片（仅管理员） ====================
    /**
     * 更新图片信息（仅管理员可用）
     * @param pictureUpdateRequest 包含图片更新字段的请求体
     * @param request              Http请求对象，用于获取当前登录用户
     * @return 更新成功返回true
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)   // 强制要求管理员权限
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        // 1. 参数校验：请求体及图片id合法性
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 将请求对象属性拷贝到实体对象
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);

        // 3. 特殊处理：tags字段（前端传来的是List<String>，转为JSON字符串存储）
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));

        // 4. 业务校验（图片名称、分类、标签等合法性）
        pictureService.validPicture(picture);

        // 5. 检查待更新的图片是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        //填充审核参数
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(oldPicture, loginUser);

        // 6. 执行更新操作
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // ==================== 获取图片详情（仅管理员） ====================
    /**
     * 根据id获取原始图片实体（仅管理员可见）
     * @param id      图片id（通过请求参数传递）
     * @param request Http请求（未直接使用，但保留扩展）
     * @return 图片实体对象
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        // 参数校验：id必须大于0
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询图片
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(picture);
    }

    // ==================== 获取图片VO视图对象（所有用户） ====================
    /**
     * 根据id获取图片的VO视图对象（脱敏、关联用户信息等）
     * @param id      图片id
     * @param request Http请求，用于获取当前用户上下文（如点赞状态）
     * @return 图片VO对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询原始图片
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        // 转换为VO对象（可能包含用户信息、是否被当前用户点赞等）
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    // ==================== 分页获取图片列表（仅管理员，返回实体） ====================
    /**
     * 分页查询图片列表（仅管理员，返回原始实体，包含所有字段）
     * @param pictureQueryRequest 查询条件（分页参数、过滤条件）
     * @return 分页的图片实体列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        // 获取分页参数
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 构造分页对象并执行查询（查询条件通过getQueryWrapper动态生成）
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    // ==================== 分页获取图片VO列表（所有用户） ====================
    /**
     * 分页查询图片VO列表（普通用户可见，数据经过脱敏/增强）
     * @param pictureQueryRequest 查询条件
     * @param request             Http请求，用于VO转换时获取用户上下文
     * @return 分页的图片VO列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 限制每页最大数量，防止恶意请求
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 普通用户默认只能看到审核通过的数据
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 分页查询原始实体
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));

        // 将实体分页对象转换为VO分页对象
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    // ==================== 编辑图片（普通用户可编辑自己的图片） ====================
    /**
     * 编辑图片（普通用户可编辑自己的图片，管理员可编辑任何图片）
     * @param pictureEditRequest 编辑请求（包含图片id及需要修改的字段）
     * @param request            Http请求，用于获取当前登录用户
     * @return 编辑成功返回true
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 拷贝属性到实体对象
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);

        // 3. 特殊处理：标签字段（List -> JSON字符串）
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));

        // 4. 自动设置编辑时间为当前时间
        picture.setEditTime(new Date());

        // 5. 业务校验（图片名称、分类等）
        pictureService.validPicture(picture);

        // 6. 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //填充审核参数
        pictureService.fillReviewParams(picture, loginUser);
        // 7. 查询原始图片是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 8. 权限校验：只有图片所有者或管理员才能编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 9. 执行更新操作
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 获取图片标签分类（仅管理员可见）
     * @return 包含标签和分类的VO对象
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 审核图片（仅管理员可见）
     *
     * @param pictureReviewRequest 审核请求（包含图片id、审核状态、审核消息）
     * @param request              Http请求，用于获取当前登录用户
     * @return 审核成功返回true
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 批量抓取并创建图片
     *
     * @param pictureUploadByBatchRequest 批量上传请求（包含图片id、审核状态）
     * @param request                     Http请求，用于获取当前登录用户
     * @return 审核成功返回true
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                                      HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Integer count = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(count);
    }
}
