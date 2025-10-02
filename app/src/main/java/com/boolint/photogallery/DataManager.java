package com.boolint.photogallery;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * 싱글톤 패턴으로 앱 전체 데이터 관리
 * MenuItem 기반 메뉴 데이터 관리
 */
public class DataManager {

    private static volatile DataManager instance;

    private List<MenuItem> menuList;
    private String currentSearchQuery = "";
    private int currentScrollPosition = 0;
    private boolean isLoading = false;
    private boolean isDataInitialized = false;
    private boolean isIconUrlsLoaded = false; // 아이콘 URL 로드 완료 플래그

    // 현재 선택된 메뉴 아이템
    private MenuItem selectedMenuItem = null;
    private int selectedMenuPosition = -1;

    private boolean isDarkMode = false;
    private String lastUpdateTime = "";

    private Context appContext;

    private DataManager() {
        menuList = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public void initialize(Context context) {
        if (appContext == null && context != null) {
            appContext = context.getApplicationContext();
            initializeDefaultData();
        }
    }

    private void initializeDefaultData() {
        if (!isDataInitialized && appContext != null) {
            menuList = generateDefaultMenuList(appContext);
            isDataInitialized = true;
        }
    }

    /**
     * MenuHelper.getMenu()를 참고한 기본 메뉴 목록 생성
     */
    private List<MenuItem> generateDefaultMenuList(Context context) {
        List<MenuItem> list = new ArrayList<>();
        MenuItem vo;

        // k1 - 위성영상 한반도 RGB
        vo = new MenuItem("SUB");
        vo.id = "k1";
        vo.title = "위성영상 한반도 RGB";
        vo.kind = "satellite";
        vo.data0 = "true+ir";
        vo.area0 = "ko020lc";
        vo.data1 = "rgbt";
        vo.area1 = "ko";
        vo.data2 = "vis_ko";
        vo.area2 = "";
        vo.apiOption = "vis_ko";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k2 - 위성영상 동아시아 RGB
        vo = new MenuItem("SUB");
        vo.id = "k2";
        vo.title = "위성영상 동아시아 RGB";
        vo.kind = "satellite";
        vo.data0 = "true+ir";
        vo.area0 = "ea020lc";
        vo.data1 = "rgbt";
        vo.area1 = "ea";
        vo.data2 = "vis_ea";
        vo.area2 = "";
        vo.apiOption = "vis_ea";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k3 - 위성영상 전구 RGB
        vo = new MenuItem("SUB");
        vo.id = "k3";
        vo.title = "위성영상 전구 RGB";
        vo.kind = "satellite";
        vo.data0 = "true+ir";
        vo.area0 = "fd020ge";
        vo.data1 = "rgbt";
        vo.area1 = "fd";
        vo.data2 = "vis_fd";
        vo.area2 = "";
        vo.apiOption = "vis_fd";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k4 - 적외영상 한반도
        vo = new MenuItem("SUB");
        vo.id = "k4";
        vo.title = "적외영상 한반도";
        vo.kind = "satellite";
        vo.data0 = "ir105";
        vo.area0 = "ko020lc";
        vo.data1 = "ir105";
        vo.area1 = "ko";
        vo.data2 = "inf_ko";
        vo.area2 = "";
        vo.apiOption = "inf_ko";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k5 - 적외영상 동아시아
        vo = new MenuItem("SUB");
        vo.id = "k5";
        vo.title = "적외영상 동아시아";
        vo.kind = "satellite";
        vo.data0 = "ir105";
        vo.area0 = "ea020lc";
        vo.data1 = "ir105";
        vo.area1 = "ea";
        vo.data2 = "inf_ea";
        vo.area2 = "";
        vo.apiOption = "inf_ea";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k6 - 적외영상 전구
        vo = new MenuItem("SUB");
        vo.id = "k6";
        vo.title = "적외영상 전구";
        vo.kind = "satellite";
        vo.data0 = "ir105";
        vo.area0 = "fd020ge";
        vo.data1 = "ir105";
        vo.area1 = "fd";
        vo.data2 = "inf_fd";
        vo.area2 = "";
        vo.apiOption = "inf_fd";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k7 - 수증기영상 한반도
        vo = new MenuItem("SUB");
        vo.id = "k7";
        vo.title = "수증기영상 한반도";
        vo.kind = "satellite";
        vo.data0 = "wv063";
        vo.area0 = "ko020lc";
        vo.data1 = "wv069";
        vo.area1 = "ko";
        vo.data2 = "wv_ko";
        vo.area2 = "";
        vo.apiOption = "wv_ko";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k8 - 수증기영상 동아시아
        vo = new MenuItem("SUB");
        vo.id = "k8";
        vo.title = "수증기영상 동아시아";
        vo.kind = "satellite";
        vo.data0 = "wv063";
        vo.area0 = "ea020lc";
        vo.data1 = "wv069";
        vo.area1 = "ea";
        vo.data2 = "wv_ea";
        vo.area2 = "";
        vo.apiOption = "wv_ea";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k9 - 수증기영상 전구
        vo = new MenuItem("SUB");
        vo.id = "k9";
        vo.title = "수증기영상 전구";
        vo.kind = "satellite";
        vo.data0 = "wv063";
        vo.area0 = "fd020ge";
        vo.data1 = "wv069";
        vo.area1 = "fd";
        vo.data2 = "wv_fd";
        vo.area2 = "";
        vo.apiOption = "wv_fd";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k10 - 레이더영상 전국합성
        vo = new MenuItem("SUB");
        vo.id = "k10";
        vo.title = "레이더영상 전국합성";
        vo.kind = "radar";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "composite_korea";
        vo.area2 = "";
        vo.apiOption = "composite_korea";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k11 - 레이더+적외 합성
        vo = new MenuItem("SUB");
        vo.id = "k11";
        vo.title = "레이더+적외 합성";
        vo.kind = "rad+inf";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "composite_infrared";
        vo.area2 = "";
        vo.apiOption = "composite_infrared";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k12 - 지역 레이더
        vo = new MenuItem("SUB");
        vo.id = "k12";
        vo.title = "지역 레이더";
        vo.kind = "local_menu";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "composite_JNI";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k13 - 레이더+카메라
        vo = new MenuItem("SUB");
        vo.id = "k13";
        vo.title = "레이더+카메라";
        vo.kind = "rad+camera";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "composite_map";
        vo.area2 = "";
        vo.apiOption = "composite_map";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k14 - 강수형태
        vo = new MenuItem("SUB");
        vo.id = "k14";
        vo.title = "강수형태";
        vo.kind = "snowrain";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "snowrain_a";
        vo.area2 = "";
        vo.apiOption = "snowrain_a";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k15 - 기온분포도
        vo = new MenuItem("SUB");
        vo.id = "k15";
        vo.title = "기온분포도";
        vo.kind = "temperature";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "temperature_a";
        vo.area2 = "";
        vo.apiOption = "temperature_a";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k16 - 태풍정보
        vo = new MenuItem("SUB");
        vo.id = "k16";
        vo.title = "태풍정보";
        vo.kind = "typhoon";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "typhoon_a";
        vo.area2 = "";
        vo.apiOption = "typhoon_a";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k17 - 시정지도
        vo = new MenuItem("SUB");
        vo.id = "k17";
        vo.title = "시정지도";
        vo.kind = "visualmap";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k18 - 예보일기도
        vo = new MenuItem("SUB");
        vo.id = "k18";
        vo.title = "예보일기도";
        vo.kind = "forecast";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k19 - 황사
        vo = new MenuItem("SUB");
        vo.id = "k19";
        vo.title = "황사";
        vo.kind = "asiandust";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "asiandust_a";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k20 - 일기도
        vo = new MenuItem("SUB");
        vo.id = "k20";
        vo.title = "일기도";
        vo.kind = "weather_chart";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "weatherchart_a";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        // k21 - 일기예보
        vo = new MenuItem("SUB");
        vo.id = "k21";
        vo.title = "일기예보";
        vo.kind = "weather_cast";
        vo.data0 = "";
        vo.area0 = "";
        vo.data1 = "";
        vo.area1 = "";
        vo.data2 = "";
        vo.area2 = "";
        vo.apiOption = "";
        vo.icon = R.drawable.placeholder_photo;
        list.add(vo);

        return list;
    }

    // === 메뉴 리스트 관리 ===
    public List<MenuItem> getMenuList() {
        if (!isDataInitialized) {
            return new ArrayList<>();
        }
        return new ArrayList<>(menuList);
    }

    public void setMenuList(List<MenuItem> newMenuList) {
        this.menuList.clear();
        this.menuList.addAll(newMenuList);
    }

    public void addMenuItem(MenuItem item) {
        menuList.add(item);
    }

    public void addMenuItems(List<MenuItem> items) {
        menuList.addAll(items);
    }

    public void removeMenuItem(int position) {
        if (position >= 0 && position < menuList.size()) {
            menuList.remove(position);
        }
    }

    public int getMenuCount() {
        return menuList.size();
    }

    public MenuItem getMenuItem(int position) {
        if (position >= 0 && position < menuList.size()) {
            return menuList.get(position);
        }
        return null;
    }

    // === 스크롤 위치 관리 ===
    public int getCurrentScrollPosition() {
        return currentScrollPosition;
    }

    public void setCurrentScrollPosition(int position) {
        this.currentScrollPosition = Math.max(0, position);
    }

    // === 검색 관리 ===
    public String getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    public void setCurrentSearchQuery(String query) {
        this.currentSearchQuery = query != null ? query : "";
    }

    // === 로딩 상태 관리 ===
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    // === 앱 설정 관리 ===
    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String updateTime) {
        this.lastUpdateTime = updateTime != null ? updateTime : "";
    }

    // === 데이터 상태 확인 ===
    public boolean hasData() {
        return isDataInitialized && !menuList.isEmpty();
    }

    public boolean isDataInitialized() {
        return isDataInitialized;
    }

    // === 아이콘 URL 로드 상태 관리 ===
    public boolean isIconUrlsLoaded() {
        return isIconUrlsLoaded;
    }

    public void setIconUrlsLoaded(boolean loaded) {
        this.isIconUrlsLoaded = loaded;
    }

    // === 선택된 메뉴 아이템 관리 ===
    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem menuItem) {
        this.selectedMenuItem = menuItem;
    }

    public int getSelectedMenuPosition() {
        return selectedMenuPosition;
    }

    public void setSelectedMenuPosition(int position) {
        this.selectedMenuPosition = position;
    }

    // 선택된 메뉴 아이템과 위치를 함께 설정
    public void setSelectedMenu(MenuItem menuItem, int position) {
        this.selectedMenuItem = menuItem;
        this.selectedMenuPosition = position;
    }

    // === 데이터 초기화 ===
    public void clearAllData() {
        menuList.clear();
        currentSearchQuery = "";
        currentScrollPosition = 0;
        isLoading = false;
        isDarkMode = false;
        lastUpdateTime = "";
        isDataInitialized = false;
        isIconUrlsLoaded = false; // 아이콘 URL 로드 플래그도 초기화
        if (appContext != null) {
            initializeDefaultData();
        }
    }

    public void refreshData() {
        currentScrollPosition = 0;
        isLoading = false;
    }

    // === 디버그용 메서드 ===
    public String getDebugInfo() {
        return String.format(
                "DataManager Status:\n" +
                        "- Menu Count: %d\n" +
                        "- Search Query: '%s'\n" +
                        "- Scroll Position: %d\n" +
                        "- Is Loading: %b\n" +
                        "- Is Initialized: %b\n" +
                        "- Dark Mode: %b",
                menuList.size(),
                currentSearchQuery,
                currentScrollPosition,
                isLoading,
                isDataInitialized,
                isDarkMode
        );
    }
}