package com.boolint.photogallery;

import java.util.ArrayList;
import java.util.List;

/**
 * 싱글톤 패턴으로 앱 전체 데이터 관리
 * 화면 회전, Activity 재생성과 무관하게 데이터 유지
 */
public class DataManager {

    // Thread-Safe 싱글톤 구현 (Lazy Initialization)
    private static volatile DataManager instance;

    // 앱 전체에서 공유할 데이터들
    private List<MainActivity.PhotoItem> photoList;
    private String currentSearchQuery = "";
    private int currentScrollPosition = 0;
    private boolean isLoading = false;
    private boolean isDataInitialized = false;

    // 앱 설정 관련
    private boolean isDarkMode = false;
    private String lastUpdateTime = "";

    // private 생성자 - 외부에서 인스턴스 생성 차단
    private DataManager() {
        photoList = new ArrayList<>();
        initializeDefaultData();
    }

    // Thread-Safe 인스턴스 반환
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

    // 기본 데이터 초기화
    private void initializeDefaultData() {
        if (!isDataInitialized) {
            photoList = generateDefaultPhotoList();
            isDataInitialized = true;
        }
    }

    // 기본 사진 목록 생성
    private List<MainActivity.PhotoItem> generateDefaultPhotoList() {
        List<MainActivity.PhotoItem> items = new ArrayList<>();

        String[] titles = {
                "Sunset at Beach", "Mountain View", "City Lights", "Forest Path", "Ocean Waves",
                "Cherry Blossoms", "Desert Landscape", "Northern Lights", "Waterfall", "Autumn Leaves",
                "Snow Mountains", "Garden Flowers", "Lake Reflection", "Sky Clouds", "River Valley",
                "Urban Street", "Lighthouse", "Prairie Field", "Rock Formation", "Misty Morning",
                "Tropical Beach", "Forest Lake", "Canyon View", "Meadow Flowers", "Starry Night",
                "Historic Bridge", "Country Road", "Coastal Cliffs", "Wildlife Scene", "Peaceful Harbor",
                "Mountain Stream", "Vintage Architecture", "Modern Skyline", "Rural Landscape", "Island Paradise",
                "Winter Wonderland", "Spring Garden", "Summer Festival", "Harvest Season", "Rainy Day"
        };

        String[] descriptions = {
                "Beautiful sunset over the ocean", "Stunning mountain landscape", "City at night", "Peaceful forest walk", "Waves crashing on shore",
                "Spring blossoms in full bloom", "Vast desert under blue sky", "Amazing aurora borealis", "Powerful waterfall cascade", "Colorful autumn foliage",
                "Snow-capped mountain peaks", "Blooming garden paradise", "Perfect lake reflection", "Dramatic sky formation", "Serene river valley",
                "Busy urban street scene", "Historic lighthouse landmark", "Golden prairie grass", "Unique rock formations", "Foggy morning atmosphere",
                "Tropical paradise beach", "Hidden forest lake", "Grand canyon vista", "Wildflower meadow", "Night sky full of stars",
                "Old stone bridge", "Quiet country lane", "Dramatic coastal view", "Wildlife in nature", "Calm harbor scene",
                "Crystal clear stream", "Beautiful old building", "Modern city skyline", "Rolling hills landscape", "Remote island getaway",
                "Snow-covered landscape", "Fresh spring blooms", "Vibrant summer scene", "Golden harvest time", "Cozy rainy weather"
        };

        String[] imageUrls = {
                "", "https://example.com/mountain1.jpg", "https://example.com/city1.jpg",
                "https://example.com/forest1.jpg", "https://example.com/ocean1.jpg", "https://example.com/cherry1.jpg",
                "https://example.com/desert1.jpg", "https://example.com/aurora1.jpg", "https://example.com/waterfall1.jpg",
                "https://example.com/autumn1.jpg", "https://example.com/snow1.jpg", "https://example.com/garden1.jpg",
                "https://example.com/lake1.jpg", "https://example.com/sky1.jpg", "https://example.com/river1.jpg",
                "https://example.com/urban1.jpg", "https://example.com/lighthouse1.jpg", "https://example.com/prairie1.jpg",
                "https://example.com/rock1.jpg", "https://example.com/misty1.jpg", "https://example.com/tropical1.jpg",
                "https://example.com/forestlake1.jpg", "https://example.com/canyon1.jpg", "https://example.com/meadow1.jpg",
                "https://example.com/starry1.jpg", "https://example.com/bridge1.jpg", "https://example.com/country1.jpg",
                "https://example.com/coastal1.jpg", "https://example.com/wildlife1.jpg", "https://example.com/harbor1.jpg",
                "https://example.com/stream1.jpg", "https://example.com/vintage1.jpg", "https://example.com/modern1.jpg",
                "https://example.com/rural1.jpg", "https://example.com/island1.jpg", "https://example.com/winter1.jpg",
                "https://example.com/spring1.jpg", "https://example.com/summer1.jpg", "https://example.com/harvest1.jpg",
                "https://example.com/rainy1.jpg"
        };

        for (int i = 0; i < titles.length; i++) {
            items.add(new MainActivity.PhotoItem(titles[i], descriptions[i], imageUrls[i]));
        }

        return items;
    }

    // === 사진 리스트 관리 ===
    public List<MainActivity.PhotoItem> getPhotoList() {
        return new ArrayList<>(photoList); // 방어적 복사
    }

    public void setPhotoList(List<MainActivity.PhotoItem> newPhotoList) {
        this.photoList.clear();
        this.photoList.addAll(newPhotoList);
    }

    public void addPhotoItem(MainActivity.PhotoItem item) {
        photoList.add(item);
    }

    public void addPhotoItems(List<MainActivity.PhotoItem> items) {
        photoList.addAll(items);
    }

    public void removePhotoItem(int position) {
        if (position >= 0 && position < photoList.size()) {
            photoList.remove(position);
        }
    }

    public int getPhotoCount() {
        return photoList.size();
    }

    public MainActivity.PhotoItem getPhotoItem(int position) {
        if (position >= 0 && position < photoList.size()) {
            return photoList.get(position);
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
        return isDataInitialized && !photoList.isEmpty();
    }

    public boolean isDataInitialized() {
        return isDataInitialized;
    }

    // === 데이터 초기화 ===
    public void clearAllData() {
        photoList.clear();
        currentSearchQuery = "";
        currentScrollPosition = 0;
        isLoading = false;
        isDarkMode = false;
        lastUpdateTime = "";
        isDataInitialized = false;
        initializeDefaultData(); // 기본 데이터 재생성
    }

    public void refreshData() {
        // 데이터 새로고침 시 스크롤 위치만 초기화
        currentScrollPosition = 0;
        isLoading = false;
        // 사진 리스트는 유지하고 업데이트된 정보만 반영
    }

    // === 디버그용 메서드 ===
    public String getDebugInfo() {
        return String.format(
                "DataManager Status:\n" +
                        "- Photo Count: %d\n" +
                        "- Search Query: '%s'\n" +
                        "- Scroll Position: %d\n" +
                        "- Is Loading: %b\n" +
                        "- Is Initialized: %b\n" +
                        "- Dark Mode: %b",
                photoList.size(),
                currentSearchQuery,
                currentScrollPosition,
                isLoading,
                isDataInitialized,
                isDarkMode
        );
    }
}