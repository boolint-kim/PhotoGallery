package com.boolint.photogallery;



import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Glide 전역 설정
 * - 메모리 캐시 크기
 * - 디스크 캐시 크기
 * - 이미지 품질 설정
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // 메모리 캐시 크기 설정 (50MB)
        int memoryCacheSizeBytes = 1024 * 1024 * 50; // 50MB
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));

        // 디스크 캐시 크기 설정 (250MB)
        int diskCacheSizeBytes = 1024 * 1024 * 250; // 250MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));

        // 기본 요청 옵션 설정
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565) // 메모리 절약 (ARGB_8888 대신)
                        .disallowHardwareConfig() // 일부 기기에서 하드웨어 비트맵 문제 방지
        );
    }

    @Override
    public boolean isManifestParsingEnabled() {
        // Glide v4부터는 false 권장
        return false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        // 커스텀 컴포넌트 등록 (필요시)
        super.registerComponents(context, glide, registry);
    }
}
