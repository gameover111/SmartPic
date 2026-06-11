package com.hsc.hsmartpicbackend.manager;

import cn.hutool.core.io.FileUtil;
import com.hsc.hsmartpicbackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
public class CosManager {
    @Resource
    private COSClient cosClient;
    @Resource
    private CosClientConfig cosClientConfig;
    /**
     * 上传文件到COS
     *
     * @param key    文件路径
     * @param file   文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }
    /**
     * 从COS获取文件
     *
     * @param key 文件路径
     * @return 文件
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }
    /**
     * 上传对象(附带图片信息)
     *
     * @param key    图片路径
     * @param file   图片
     * @return 上传结果
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        //对图片进行处理
        PicOperations picOperations = new PicOperations();
        // 表示返回的图片信息包含图片信息
        picOperations.setIsPicInfo(1);

        List<PicOperations.Rule> rules = new ArrayList<>();
        //1.图片压缩,转换为webp格式
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);

        //2.图片缩略图处理规则,仅对 ＞ 20KB 图片进行处理,缩略图大小为 256x256
        if (file.length() > 20 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String keySuffix = FileUtil.getSuffix(key);
            if (keySuffix == null) { keySuffix = "jpg";}
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + keySuffix;
            thumbnailRule.setFileId(thumbnailKey);

            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
            rules.add(thumbnailRule);

        }

        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 对象路径
     */
    public void deleteObject(String key) throws CosClientException {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }


}
