package com.boolint.photogallery;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 반응형 레이아웃을 위한 헬퍼 클래스
 * 다양한 화면 크기와 기기 유형을 감지하고 적절한 레이아웃 설정 제공
 */
public class ResponsiveLayoutHelper {

    // 화면 타입 열거형
    public enum ScreenType {
        PHONE,          // 일반 폰 (< 600dp)
        TABLET,         // 태블릿 (600dp ~ 839dp)
        FOLDABLE,       // 폴더블/대형 화면 (≥ 840dp)
        PHONE_LANDSCAPE // 폰 가로모드
    }

    private final Context context;
    private final int screenWidthDp;
    private final int screenHeightDp;
    private final boolean isLandscape;

    public ResponsiveLayoutHelper(Context context) {
        this.context = context;

        // 화면 크기 계산
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        float density = displayMetrics.density;
        this.screenWidthDp = (int) (displayMetrics.widthPixels / density);
        this.screenHeightDp = (int) (displayMetrics.heightPixels / density);

        // 방향 확인
        Configuration config = context.getResources().getConfiguration();
        this.isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 현재 화면 타입 반환
     */
    public ScreenType getScreenType() {
        if (screenWidthDp >= 840) {
            return ScreenType.FOLDABLE;
        } else if (screenWidthDp >= 600) {
            return ScreenType.TABLET;
        } else if (isLandscape) {
            return ScreenType.PHONE_LANDSCAPE;
        } else {
            return ScreenType.PHONE;
        }
    }

    /**
     * RecyclerView 컬럼 수 반환 (사진 그리드용)
     */
    public int getGridColumns() {
        switch (getScreenType()) {
            case FOLDABLE:
                return isLandscape ? 6 : 3;
            case TABLET:
                return isLandscape ? 6 : 3; // 태블릿: 가로 6열, 세로 3열
            case PHONE_LANDSCAPE:
                return 6; // 폰 가로: 6열
            case PHONE:
            default:
                return 3; // 폰 세로: 3열
        }
    }

    /**
     * 마스터-디테일 모드 여부
     */
    public boolean isMasterDetailMode() {
        return getScreenType() == ScreenType.FOLDABLE;
    }

    /**
     * 태블릿 모드 여부
     */
    public boolean isTabletMode() {
        return getScreenType() == ScreenType.TABLET;
    }

    /**
     * 폰 모드 여부
     */
    public boolean isPhoneMode() {
        ScreenType type = getScreenType();
        return type == ScreenType.PHONE || type == ScreenType.PHONE_LANDSCAPE;
    }

    /**
     * 확장된 툴바 사용 여부
     */
    public boolean shouldUseExtendedToolbar() {
        return getScreenType() == ScreenType.FOLDABLE || getScreenType() == ScreenType.TABLET;
    }

    /**
     * 인라인 검색바 표시 여부
     */
    public boolean shouldShowInlineSearchBar() {
        return getScreenType() == ScreenType.FOLDABLE;
    }

    /**
     * 비디오 아이템에 설명 표시 여부
     */
    public boolean shouldShowVideoDescription() {
        return getScreenType() == ScreenType.TABLET;
    }

    /**
     * 액션 버튼들 표시 여부
     */
    public boolean shouldShowActionButtons() {
        return getScreenType() != ScreenType.PHONE;
    }

    /**
     * 화면 정보 디버그 출력
     */
    public String getDebugInfo() {
        return String.format(
                "Screen Info:\n" +
                        "- Type: %s\n" +
                        "- Size: %dx%d dp\n" +
                        "- Orientation: %s\n" +
                        "- Grid Columns: %d\n" +
                        "- Master-Detail: %b\n" +
                        "- Extended Toolbar: %b",
                getScreenType().name(),
                screenWidthDp, screenHeightDp,
                isLandscape ? "Landscape" : "Portrait",
                getGridColumns(),
                isMasterDetailMode(),
                shouldUseExtendedToolbar()
        );
    }

    /**
     * 특정 화면 크기에서의 최적 썸네일 크기 반환
     */
    public static class ThumbnailSize {
        public final int width;
        public final int height;

        public ThumbnailSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public ThumbnailSize getOptimalThumbnailSize() {
        switch (getScreenType()) {
            case FOLDABLE:
                return new ThumbnailSize(120, 68);
            case TABLET:
                return new ThumbnailSize(320, 180);
            case PHONE_LANDSCAPE:
                return new ThumbnailSize(200, 112);
            case PHONE:
            default:
                return new ThumbnailSize(screenWidthDp - 32, 200); // 패딩 고려
        }
    }

    /**
     * 갤럭시 폴드 특화 감지
     */
    public boolean isGalaxyFold() {
        // 갤럭시 폴드 시리즈의 특징적인 화면 비율 감지
        float aspectRatio = (float) screenWidthDp / screenHeightDp;

        // 폴드 펼친 상태: 대략 4:3 ~ 5:4 비율
        // 폴드 접은 상태: 대략 21:9 비율
        return (screenWidthDp >= 840 && aspectRatio >= 1.2 && aspectRatio <= 1.4) ||
                (screenWidthDp < 400 && aspectRatio < 0.5);
    }

    /**
     * 화면 회전 처리를 위한 설정 반환
     */
    public boolean shouldHandleConfigurationChange() {
        // 태블릿과 폴더블에서는 설정 변경을 직접 처리
        return getScreenType() != ScreenType.PHONE;
    }
}