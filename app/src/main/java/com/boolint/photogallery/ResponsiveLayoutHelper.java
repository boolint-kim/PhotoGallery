package com.boolint.photogallery;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 반응형 레이아웃을 위한 헬퍼 클래스
 * 폴더블 펼친 상태를 화면 비율로 동적 감지
 */
public class ResponsiveLayoutHelper {

    // 화면 타입 열거형
    public enum ScreenType {
        PHONE,              // 일반 폰 (< 600dp)
        TABLET,             // 태블릿 (600dp ~ 긴 비율)
        FOLDABLE_UNFOLDED,  // 폴더블 펼친 상태 (600dp~ + 정사각형 비율)
        PHONE_LANDSCAPE     // 폰 가로모드
    }

    private final Context context;
    private final int screenWidthDp;
    private final int screenHeightDp;
    private final int smallestWidthDp;
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
        this.smallestWidthDp = Math.min(screenWidthDp, screenHeightDp);

        // 방향 확인
        Configuration config = context.getResources().getConfiguration();
        this.isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 폴더블 펼친 상태 감지
     *
     * 판단 기준:
     * 1. Smallest width가 600dp 이상 (태블릿 범위)
     * 2. 화면 비율이 거의 정사각형에 가까움 (aspect ratio 0.75 ~ 1.3)
     * 3. 일반 태블릿은 더 긴 비율 (1.5 ~ 1.78)
     */
    private boolean isFoldableUnfolded() {
        if (smallestWidthDp < 600) {
            return false; // 태블릿 범위가 아님
        }

        // 화면 비율 계산 (항상 >= 1.0이 되도록)
        float aspectRatio = Math.max(screenWidthDp, screenHeightDp)
                / (float) Math.min(screenWidthDp, screenHeightDp);

        // 폴더블 특징:
        // - 갤럭시 Z Fold: 768 x 884 → ratio 1.15
        // - 거의 정사각형에 가까움 (1.0 ~ 1.35 범위)
        //
        // 일반 태블릿:
        // - 10인치 태블릿: 800 x 1280 → ratio 1.6
        // - 긴 비율 (1.4 이상)

        boolean isSquarish = aspectRatio >= 1.0 && aspectRatio <= 1.35;

        // 추가 검증: width와 height 차이가 200dp 이하면 폴더블
        int sizeDifference = Math.abs(screenWidthDp - screenHeightDp);
        boolean hasSmallSizeDiff = sizeDifference <= 200;

        return isSquarish || hasSmallSizeDiff;
    }

    /**
     * 현재 화면 타입 반환
     */
    public ScreenType getScreenType() {
        // 폴더블 펼친 상태 우선 확인
        if (isFoldableUnfolded()) {
            return ScreenType.FOLDABLE_UNFOLDED;
        }

        if (smallestWidthDp >= 600) {
            return ScreenType.TABLET;
        } else if (isLandscape) {
            return ScreenType.PHONE_LANDSCAPE;
        } else {
            return ScreenType.PHONE;
        }
    }

    /**
     * RecyclerView 컬럼 수 반환 (사진 그리드용)
     * 폴더블 펼친 상태: 세로/가로 모두 3열
     */
    public int getGridColumns() {
        switch (getScreenType()) {
            case FOLDABLE_UNFOLDED:
                return 3; // 세로/가로 모두 3열
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
     * 그리드 아이템 간격 반환 (dp)
     * 폴더블: 휴대폰과 태블릿 사이
     */
    public int getGridSpacing() {
        switch (getScreenType()) {
            case FOLDABLE_UNFOLDED:
                return 80; // 중간 크기 (휴대폰 20dp, 태블릿 60dp의 중간)
            case TABLET:
                return 60; // 태블릿: 큰 간격
            case PHONE_LANDSCAPE:
            case PHONE:
            default:
                return 20; // 휴대폰: 작은 간격
        }
    }

    /**
     * 아이템 패딩 반환 (dp)
     */
    public int getItemPadding() {
        switch (getScreenType()) {
            case FOLDABLE_UNFOLDED:
                return 6; // 중간 (휴대폰 4dp, 태블릿 8dp의 중간)
            case TABLET:
                return 8;
            case PHONE_LANDSCAPE:
            case PHONE:
            default:
                return 4;
        }
    }

    /**
     * 아이템 마진 반환 (dp)
     */
    public int getItemMargin() {
        switch (getScreenType()) {
            case FOLDABLE_UNFOLDED:
                return 3; // 중간 (휴대폰 2dp, 태블릿 4dp의 중간)
            case TABLET:
                return 4;
            case PHONE_LANDSCAPE:
            case PHONE:
            default:
                return 2;
        }
    }

    /**
     * 제목 텍스트 크기 반환 (sp)
     */
    public int getTitleTextSize() {
        switch (getScreenType()) {
            case FOLDABLE_UNFOLDED:
                return 13; // 중간 (휴대폰 12sp, 태블릿 16sp의 중간)
            case TABLET:
                return 16;
            case PHONE_LANDSCAPE:
                return 10;
            case PHONE:
            default:
                return 12;
        }
    }

    /**
     * 마스터-디테일 모드 여부
     */
    public boolean isMasterDetailMode() {
        return getScreenType() == ScreenType.FOLDABLE_UNFOLDED;
    }

    /**
     * 태블릿 모드 여부
     */
    public boolean isTabletMode() {
        return getScreenType() == ScreenType.TABLET;
    }

    /**
     * 폴더블 펼친 상태 여부
     */
    public boolean isFoldableUnfoldedMode() {
        return getScreenType() == ScreenType.FOLDABLE_UNFOLDED;
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
        ScreenType type = getScreenType();
        return type == ScreenType.FOLDABLE_UNFOLDED || type == ScreenType.TABLET;
    }

    /**
     * 인라인 검색바 표시 여부
     */
    public boolean shouldShowInlineSearchBar() {
        return getScreenType() == ScreenType.FOLDABLE_UNFOLDED;
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
        float aspectRatio = Math.max(screenWidthDp, screenHeightDp)
                / (float) Math.min(screenWidthDp, screenHeightDp);

        return String.format(
                "Screen Info:\n" +
                        "- Type: %s\n" +
                        "- Size: %dx%d dp\n" +
                        "- Smallest Width: %d dp\n" +
                        "- Aspect Ratio: %.2f\n" +
                        "- Orientation: %s\n" +
                        "- Is Foldable Unfolded: %b\n" +
                        "- Grid Columns: %d\n" +
                        "- Grid Spacing: %ddp\n" +
                        "- Item Padding: %ddp\n" +
                        "- Title Size: %dsp",
                getScreenType().name(),
                screenWidthDp, screenHeightDp,
                smallestWidthDp,
                aspectRatio,
                isLandscape ? "Landscape" : "Portrait",
                isFoldableUnfolded(),
                getGridColumns(),
                getGridSpacing(),
                getItemPadding(),
                getTitleTextSize()
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
            case FOLDABLE_UNFOLDED:
                return new ThumbnailSize(150, 85); // 중간 크기
            case TABLET:
                return new ThumbnailSize(320, 180);
            case PHONE_LANDSCAPE:
                return new ThumbnailSize(200, 112);
            case PHONE:
            default:
                return new ThumbnailSize(screenWidthDp - 32, 200);
        }
    }

    /**
     * 갤럭시 폴드 특화 감지 (레거시, 참고용)
     */
    public boolean isGalaxyFold() {
        float aspectRatio = (float) screenWidthDp / screenHeightDp;
        return (screenWidthDp >= 840 && aspectRatio >= 1.2 && aspectRatio <= 1.4) ||
                (screenWidthDp < 400 && aspectRatio < 0.5);
    }

    /**
     * 화면 회전 처리를 위한 설정 반환
     */
    public boolean shouldHandleConfigurationChange() {
        return getScreenType() != ScreenType.PHONE;
    }
}