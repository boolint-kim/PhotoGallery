package com.boolint.photogallery;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 반응형 레이아웃을 위한 헬퍼 클래스
 * 수정된 컬럼 규칙:
 * - 모든 세로모드: 3열
 * - 모든 가로모드: 6열
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
     * RecyclerView 컬럼 수 반환
     * 수정된 규칙:
     * - 폴더블 접었을 때: 세로 3열, 가로 6열
     * - 폴더블 펼쳤을 때: 세로 3열, 가로 3열
     * - 일반 휴대폰/태블릿: 세로 3열, 가로 6열
     */
    public int getGridColumns() {
        if (isFoldableUnfolded()) {
            return 3; // 폴더블 펼친 상태는 항상 3열
        } else if (isLandscape) {
            return 6; // 폴더블 접었을 때 가로, 일반 휴대폰/태블릿 가로: 6열
        } else {
            return 3; // 모든 세로모드: 3열
        }
    }

    /**
     * 그리드 아이템 간격 반환 (dp)
     */
    public int getGridSpacing() {
        if (isFoldableUnfolded()) {
            return 40; // 폴더블 펼친 상태: 중간 간격
        }

        switch (getScreenType()) {
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
        if (isFoldableUnfolded()) {
            return 6; // 폴더블 펼친 상태: 중간 패딩
        }

        switch (getScreenType()) {
            case TABLET:
                return 6; // 태블릿: 중간 패딩
            case PHONE_LANDSCAPE:
            case PHONE:
            default:
                return 3; // 휴대폰: 작은 패딩
        }
    }

    /**
     * 아이템 마진 반환 (dp)
     */
    public int getItemMargin() {
        if (isFoldableUnfolded()) {
            return 3; // 폴더블 펼친 상태: 중간 마진
        }

        switch (getScreenType()) {
            case TABLET:
                return 3; // 태블릿: 중간 마진
            case PHONE_LANDSCAPE:
            case PHONE:
            default:
                return 2; // 휴대폰: 작은 마진
        }
    }

    /**
     * 제목 텍스트 크기 반환 (sp)
     */
    public int getTitleTextSize() {
        if (isFoldableUnfolded()) {
            return 12; // 폴더블 펼친 상태: 세로/가로 모두 3열이므로 12sp
        } else if (isLandscape) {
            return 10; // 폴더블 접었을 때 가로, 일반 휴대폰/태블릿 가로(6열): 작은 텍스트
        } else {
            switch (getScreenType()) {
                case TABLET:
                    return 14; // 태블릿 세로: 큰 텍스트
                case PHONE:
                default:
                    return 12; // 휴대폰 세로: 기본 텍스트
            }
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
        return getScreenType() == ScreenType.TABLET && !isLandscape;
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
                return new ThumbnailSize(110, 110); // 폴더블 펼친 상태: 항상 110dp (세로/가로 모두 3열)
            case TABLET:
                return isLandscape
                        ? new ThumbnailSize(90, 90)   // 태블릿 가로: 90dp (6열)
                        : new ThumbnailSize(110, 110); // 태블릿 세로: 110dp (3열)
            case PHONE_LANDSCAPE:
                return new ThumbnailSize(70, 70);  // 휴대폰 가로: 70dp (6열)
            case PHONE:
            default:
                return new ThumbnailSize(110, 110); // 휴대폰 세로: 110dp (3열)
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