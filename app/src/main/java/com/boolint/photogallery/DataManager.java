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

    // DataManager.java의 generateDefaultPhotoList() 메서드 교체

    // 기본 사진 목록 생성 (실제 이미지 URL 사용)
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

        // Unsplash 무료 이미지 URL (실제로 사용 가능)
        // 각 카테고리별 실제 이미지 URL
        String[] imageUrls = {
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400",  // Sunset
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",  // Mountain
                "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=400",  // City Lights
                "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400",  // Forest
                "https://images.unsplash.com/photo-1505142468610-359e7d316be0?w=400",  // Ocean
                "https://images.unsplash.com/photo-1522383225653-ed111181a951?w=400",  // Cherry Blossoms
                "https://images.unsplash.com/photo-1509316785289-025f5b846b35?w=400",  // Desert
                "https://images.unsplash.com/photo-1579033461380-adb47c3eb938?w=400",  // Northern Lights
                "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=400",  // Waterfall
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",  // Autumn
                "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=400",  // Snow Mountains
                "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400",  // Garden
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=400",  // Lake
                "https://images.unsplash.com/photo-1534088568595-a066f410bcda?w=400",  // Sky
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",  // River Valley
                "https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=400",  // Urban
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",  // Lighthouse
                "https://images.unsplash.com/photo-1500964757637-c85e8a162699?w=400",  // Prairie
                "https://images.unsplash.com/photo-1542223189-67a03fa0f0bd?w=400",  // Rock
                "https://images.unsplash.com/photo-1511884642898-4c92249e20b6?w=400",  // Misty
                "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400",  // Tropical
                "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=400",  // Forest Lake
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",  // Canyon
                "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400",  // Meadow
                "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=400",  // Starry Night
                "https://images.unsplash.com/photo-1515705576963-95cad62945b6?w=400",  // Bridge
                "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=400",  // Country Road
                "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=400",  // Coastal
                "https://images.unsplash.com/photo-1564760055775-d63b17a55c44?w=400",  // Wildlife
                "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400",  // Harbor
                "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400",  // Stream
                "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=400",  // Vintage
                "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=400",  // Modern Skyline
                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=400",  // Rural
                "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400",  // Island
                "https://images.unsplash.com/photo-1491002052546-bf38f186af56?w=400",  // Winter
                "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400",  // Spring
                "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=400",  // Summer
                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=400",  // Harvest
                "https://images.unsplash.com/photo-1428908728789-d2de25dbd4e2?w=400"   // Rainy
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