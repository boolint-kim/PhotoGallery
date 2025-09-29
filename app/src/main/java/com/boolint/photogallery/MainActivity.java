package com.boolint.photogallery;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import androidx.core.view.WindowInsetsControllerCompat;



import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // UI 컴포넌트들
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    // 싱글톤 데이터 매니저
    private DataManager dataManager;

    // 반응형 레이아웃 헬퍼
    private ResponsiveLayoutHelper layoutHelper;

    // WindowInsetsController for status bar control
    private WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 반응형 레이아웃 헬퍼 초기화
        layoutHelper = new ResponsiveLayoutHelper(this);
        Log.d(TAG, layoutHelper.getDebugInfo());

        // 싱글톤 인스턴스 획득
        dataManager = DataManager.getInstance();

        // 뷰 초기화
        initViews();

        // 상태바 아이콘 색상 설정
        setupStatusBar();

        // Edge-to-Edge 윈도우 인셋 처리
        setupEdgeToEdgeInsets();

        // 툴바 설정
        setupToolbar();

        // 리사이클러뷰 설정
        setupRecyclerView();

        // 스크롤 효과 설정
        setupScrollEffect();

        // 스크롤 위치 복원
        restoreScrollPosition();

        Log.d(TAG, "Setup completed for " + layoutHelper.getScreenType());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupStatusBar() {
        // WindowInsetsController 초기화
        windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());

        if (windowInsetsController != null) {
            // 다크 모드 확인
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

            // 상태바 아이콘 색상 설정
            // 다크모드: 밝은 아이콘 (false), 라이트모드: 어두운 아이콘 (true)
            windowInsetsController.setAppearanceLightStatusBars(!isDarkMode);

            Log.d(TAG, "Status bar setup - Dark mode: " + isDarkMode + ", Light icons: " + isDarkMode);
        }
    }

    private void setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());

            // 현재 방향 확인
            boolean isLandscape = getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE;

            // 좌우 인셋 계산
            int leftInset = Math.max(systemBars.left, displayCutout.left);
            int rightInset = Math.max(systemBars.right, displayCutout.right);

            if (isLandscape) {
                // 가로모드: 좌우 인셋 중요
                v.setPadding(0, 0, 0, systemBars.bottom);
                appBarLayout.setPadding(leftInset, systemBars.top, rightInset, 0);
                recyclerView.setPadding(leftInset, 0, rightInset, 0);
            } else {
                // 세로모드: 상하 인셋 중요
                v.setPadding(0, 0, 0, systemBars.bottom);
                appBarLayout.setPadding(0, systemBars.top, 0, 0);
                recyclerView.setPadding(0, 0, 0, 0);
            }

            recyclerView.setClipToPadding(false);
            return insets;
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView() {
        // 싱글톤에서 사진 데이터 가져오기
        List<PhotoItem> photoList = dataManager.getPhotoList();
        adapter = new PhotoAdapter(photoList);

        // 화면 타입에 따른 그리드 레이아웃 설정
        setupGridLayout();

        recyclerView.setAdapter(adapter);

        Log.d(TAG, "RecyclerView setup with " + photoList.size() + " photo items, columns: " + layoutHelper.getGridColumns());
    }

    private void setupGridLayout() {
        int columns = layoutHelper.getGridColumns();

        // 모든 화면에서 그리드 레이아웃 사용
        GridLayoutManager gridLayout = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(gridLayout);

        // 그리드 아이템 간격 설정
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        if (spacing > 0) {
            // 기존 데코레이션 제거
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(columns, spacing, true));
        }

        Log.d(TAG, "Grid layout setup: " + layoutHelper.getScreenType() + ", columns: " + columns + ", spacing: " + spacing + "dp");
    }

    private void setupScrollEffect() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // 투명도 조절
                float ratio = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                toolbar.setAlpha(1 - ratio);

                // 상태바 아이콘 색상은 항상 유지 (스크롤과 무관)
                // 이미 setupStatusBar()에서 설정했으므로 여기서는 변경하지 않음
            }
        });
    }

    private void restoreScrollPosition() {
        int scrollPosition = dataManager.getCurrentScrollPosition();
        if (scrollPosition > 0) {
            recyclerView.post(() -> {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (gridLayoutManager != null) {
                    gridLayoutManager.scrollToPosition(scrollPosition);
                    Log.d(TAG, "Scroll position restored to: " + scrollPosition);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 현재 스크롤 위치를 싱글톤에 저장
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (gridLayoutManager != null) {
            int position = gridLayoutManager.findFirstVisibleItemPosition();
            dataManager.setCurrentScrollPosition(position);
            Log.d(TAG, "Scroll position saved: " + position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // 상태바 아이콘 색상 재설정 (다른 액티비티에서 돌아왔을 때)
        setupStatusBar();

        // 사진 데이터가 변경되었을 수 있으므로 어댑터 새로고침
        if (adapter != null) {
            adapter.updatePhotoData(dataManager.getPhotoList());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "Configuration changed: " +
                (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" : "Portrait"));

        // 상태바 아이콘 색상 재설정 (다크모드 전환 시)
        setupStatusBar();

        // 화면 회전 시 레이아웃 다시 설정
        layoutHelper = new ResponsiveLayoutHelper(this);
        setupGridLayout();

        // 어댑터 새로고침
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // === PhotoItem 데이터 클래스 ===
    public static class PhotoItem implements android.os.Parcelable {
        private String title;
        private String description;
        private String imageUrl;

        public PhotoItem(String title, String description, String imageUrl) {
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        // Parcelable 구현
        protected PhotoItem(android.os.Parcel in) {
            title = in.readString();
            description = in.readString();
            imageUrl = in.readString();
        }

        public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
            @Override
            public PhotoItem createFromParcel(android.os.Parcel in) {
                return new PhotoItem(in);
            }

            @Override
            public PhotoItem[] newArray(int size) {
                return new PhotoItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(imageUrl);
        }

        // Getter 메서드들
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImageUrl() { return imageUrl; }

        // Setter 메서드들
        public void setTitle(String title) { this.title = title; }
        public void setDescription(String description) { this.description = description; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    // === PhotoAdapter - 사진 그리드 전용 어댑터 ===
    public static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private List<PhotoItem> photoItems;
        private OnPhotoClickListener clickListener;

        public interface OnPhotoClickListener {
            void onPhotoClick(PhotoItem photoItem, int position);
        }

        public PhotoAdapter(List<PhotoItem> photoItems) {
            this.photoItems = photoItems;
        }

        public void setOnPhotoClickListener(OnPhotoClickListener listener) {
            this.clickListener = listener;
        }

        // 사진 데이터 업데이트
        public void updatePhotoData(List<PhotoItem> newPhotoItems) {
            this.photoItems = newPhotoItems;
            notifyDataSetChanged();
        }

        // 개별 사진 추가
        public void addPhotoItem(PhotoItem photoItem) {
            photoItems.add(photoItem);
            notifyItemInserted(photoItems.size() - 1);
        }

        // 여러 사진 추가
        public void addPhotoItems(List<PhotoItem> newPhotoItems) {
            int startPosition = photoItems.size();
            photoItems.addAll(newPhotoItems);
            notifyItemRangeInserted(startPosition, newPhotoItems.size());
        }

        // 사진 제거
        public void removePhotoItem(int position) {
            if (position >= 0 && position < photoItems.size()) {
                photoItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_item, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            PhotoItem photoItem = photoItems.get(position);

            // 사진 제목 설정
            if (holder.photoTitle != null) {
                holder.photoTitle.setText(photoItem.getTitle());
            }

            // 사진 설명 설정 (태블릿에서만 표시)
            if (holder.photoDescription != null) {
                holder.photoDescription.setText(photoItem.getDescription());
            }
            holder.photoImage.setImageResource(R.drawable.icon);

            // 클릭 리스너 설정
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onPhotoClick(photoItem, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photoItems.size();
        }

        // === PhotoViewHolder - 사진 아이템 뷰홀더 ===
        public static class PhotoViewHolder extends RecyclerView.ViewHolder {
            TextView photoTitle, photoDescription;
            ImageView photoImage;

            public PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                photoTitle = itemView.findViewById(R.id.photoTitle);
                photoDescription = itemView.findViewById(R.id.photoDescription);
                photoImage = itemView.findViewById(R.id.photoImage);
            }
        }
    }
}