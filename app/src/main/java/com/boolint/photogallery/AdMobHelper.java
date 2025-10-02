package com.boolint.photogallery;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

/**
 * AdMob 배너 광고를 관리하는 헬퍼 클래스
 * - 광고 초기화 및 로드
 * - 광고 크기 자동 계산
 * - RecyclerView 패딩 자동 조정
 * - 라이프사이클 관리
 */
public class AdMobHelper {
    private static final String TAG = "AdMobHelper";

    // 테스트 광고 단위 ID (실제 배포시 변경 필요)
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    private final Activity activity;
    private final FrameLayout adContainerView;
    private final RecyclerView recyclerView;
    private final ResponsiveLayoutHelper layoutHelper;

    private AdView adView;

    /**
     * AdMobHelper 생성자
     *
     * @param activity Activity 인스턴스
     * @param adContainerView 광고를 표시할 컨테이너
     * @param recyclerView 패딩을 조정할 RecyclerView
     * @param layoutHelper 레이아웃 정보를 제공하는 헬퍼
     */
    public AdMobHelper(Activity activity,
                       FrameLayout adContainerView,
                       RecyclerView recyclerView,
                       ResponsiveLayoutHelper layoutHelper) {
        this.activity = activity;
        this.adContainerView = adContainerView;
        this.recyclerView = recyclerView;
        this.layoutHelper = layoutHelper;
    }

    /**
     * AdMob SDK 초기화
     */
    public void initialize() {
        MobileAds.initialize(activity, initializationStatus -> {
            Log.d(TAG, "AdMob initialized");
        });
    }

    /**
     * 배너 광고 로드
     */
    public void loadBannerAd() {
        // 기존 광고 제거
        if (adView != null) {
            adContainerView.removeAllViews();
            adView.destroy();
        }

        // 광고 크기 계산 및 AdView 생성
        AdSize adSize = calculateAdSize();
        adView = new AdView(activity);
        adView.setAdUnitId(AD_UNIT_ID);
        adView.setAdSize(adSize);

        // 광고 리스너 설정
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "Ad loaded successfully");
                // 광고 로드 완료 후 RecyclerView 패딩 조정
                adContainerView.postDelayed(() -> updateRecyclerViewPadding(), 100);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                // 광고 로드 실패 시 기본 패딩 설정
                setDefaultRecyclerViewPadding();
            }
        });

        // 광고를 컨테이너에 추가
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        // 광고 컨테이너 마진 설정
        adContainerView.post(this::ensureAdContainerMargins);

        // 광고 컨테이너 레이아웃 변경 감지
        adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (adContainerView.getHeight() > 0) {
                            updateRecyclerViewPadding();
                        }
                    }
                });

        // 광고 요청
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Log.d(TAG, "Banner ad loading with size: " + adSize.toString());
    }

    /**
     * 화면 크기에 맞는 광고 크기 계산
     */
    private AdSize calculateAdSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;
        float adWidthPixels = outMetrics.widthPixels;

        // 가로모드에서는 좌우 시스템바 고려
        boolean isLandscape = activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        if (isLandscape) {
            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) adContainerView.getLayoutParams();

            // 좌우 마진을 제외한 실제 사용 가능한 너비
            adWidthPixels -= (params.leftMargin + params.rightMargin);

            Log.d(TAG, String.format("Ad width calculation - total: %.0f, leftMargin: %d, rightMargin: %d, available: %.0f",
                    (float)outMetrics.widthPixels, params.leftMargin, params.rightMargin, adWidthPixels));
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    /**
     * 광고 컨테이너의 마진을 시스템바에 맞게 설정
     */
    private void ensureAdContainerMargins() {
        Insets systemBars = ViewCompat.getRootWindowInsets(adContainerView)
                .getInsets(WindowInsetsCompat.Type.systemBars());
        Insets displayCutout = ViewCompat.getRootWindowInsets(adContainerView)
                .getInsets(WindowInsetsCompat.Type.displayCutout());

        int leftInset = Math.max(systemBars.left, displayCutout.left);
        int rightInset = Math.max(systemBars.right, displayCutout.right);

        boolean isLandscape = activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) adContainerView.getLayoutParams();

        if (isLandscape) {
            params.leftMargin = leftInset;
            params.rightMargin = rightInset;
            params.bottomMargin = systemBars.bottom;
        } else {
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = systemBars.bottom;
        }

        adContainerView.setLayoutParams(params);

        Log.d(TAG, String.format("Ad container margins ensured - left: %d, right: %d, bottom: %d",
                params.leftMargin, params.rightMargin, params.bottomMargin));
    }

    /**
     * 광고 높이에 맞춰 RecyclerView 패딩 동적 조정
     */
    private void updateRecyclerViewPadding() {
        adContainerView.post(() -> {
            int adHeight = adContainerView.getHeight();
            if (adHeight > 0) {
                float density = activity.getResources().getDisplayMetrics().density;

                // 광고 높이 + 추가 여유 공간
                int basicExtraPadding = (int) (24 * density);

                // 아이템의 대략적인 높이 계산
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int columns = layoutHelper.getGridColumns();
                int itemWidth = screenWidth / columns;
                int itemHeight = (int) (itemWidth * 1.3);

                // 전체 패딩 = 광고 높이 + 기본 여유 + 아이템 높이의 1/4
                int totalPadding = adHeight + basicExtraPadding + (itemHeight / 4);

                boolean isLandscape = activity.getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE;

                Insets systemBars = ViewCompat.getRootWindowInsets(recyclerView)
                        .getInsets(WindowInsetsCompat.Type.systemBars());
                Insets displayCutout = ViewCompat.getRootWindowInsets(recyclerView)
                        .getInsets(WindowInsetsCompat.Type.displayCutout());

                int leftInset = Math.max(systemBars.left, displayCutout.left);
                int rightInset = Math.max(systemBars.right, displayCutout.right);

                if (isLandscape) {
                    recyclerView.setPadding(leftInset, 0, rightInset, totalPadding);
                } else {
                    recyclerView.setPadding(0, 0, 0, totalPadding);
                }

                Log.d(TAG, String.format("RecyclerView padding updated: adHeight=%d, itemHeight=%d, totalPadding=%d",
                        adHeight, itemHeight, totalPadding));
            } else {
                setDefaultRecyclerViewPadding();
            }
        });
    }

    /**
     * 기본 RecyclerView 패딩 설정 (광고가 없거나 로드 실패 시)
     */
    private void setDefaultRecyclerViewPadding() {
        float density = activity.getResources().getDisplayMetrics().density;

        // 아이템의 대략적인 높이 계산
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int columns = layoutHelper.getGridColumns();
        int itemWidth = screenWidth / columns;
        int itemHeight = (int) (itemWidth * 1.3);

        // 기본 광고 예상 높이(80dp) + 기본 여유(24dp) + 아이템 높이의 1/4
        int defaultAdHeight = (int) (80 * density);
        int extraPadding = (int) (24 * density);
        int defaultPadding = defaultAdHeight + extraPadding + (itemHeight / 4);

        boolean isLandscape = activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        Insets systemBars = ViewCompat.getRootWindowInsets(recyclerView)
                .getInsets(WindowInsetsCompat.Type.systemBars());
        Insets displayCutout = ViewCompat.getRootWindowInsets(recyclerView)
                .getInsets(WindowInsetsCompat.Type.displayCutout());

        int leftInset = Math.max(systemBars.left, displayCutout.left);
        int rightInset = Math.max(systemBars.right, displayCutout.right);

        if (isLandscape) {
            recyclerView.setPadding(leftInset, 0, rightInset, defaultPadding);
        } else {
            recyclerView.setPadding(0, 0, 0, defaultPadding);
        }

        Log.d(TAG, "RecyclerView default padding set: " + defaultPadding);
    }

    /**
     * Activity의 onPause()에서 호출
     */
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
    }

    /**
     * Activity의 onResume()에서 호출
     */
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * Activity의 onDestroy()에서 호출
     */
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    /**
     * Configuration 변경 시 광고 재로드
     */
    public void onConfigurationChanged() {
        recyclerView.post(this::loadBannerAd);
    }

    /**
     * 광고 단위 ID 변경 (실제 배포용)
     *
     */
    public static String getAdUnitId() {
        return AD_UNIT_ID;
    }
}