package com.hsc.hsmartpicbackend.api.imagesearch;

import com.hsc.hsmartpicbackend.api.imagesearch.model.ImageSearchResult;
import com.hsc.hsmartpicbackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.hsc.hsmartpicbackend.api.imagesearch.sub.GetImageListApi;
import com.hsc.hsmartpicbackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        List<ImageSearchResult> imageList = searchImage("https://haowallpaper.com/link//common/file/previewFileImg/19095722698298240");
        System.out.println("结果列表" + imageList);
    }
}