package com.boolint.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ICON_API = "http://wko.boolint.com:8080/WeatherService/WeatherInfo/ThumbKr.jsp";

    // 아이콘 로드 콜백 인터페이스
    private interface IconLoadCallback {
        void onLoaded();
        void onError(Exception e);
    }

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DataManager dataManager;
    private ResponsiveLayoutHelper layoutHelper;
    private WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        layoutHelper = new ResponsiveLayoutHelper(this);
        Log.d(TAG, layoutHelper.getDebugInfo());

        // DataManager 초기화
        dataManager = DataManager.getInstance();
        dataManager.initialize(this);

        initViews();
        setupStatusBar();
        setupEdgeToEdgeInsets();
        setupToolbar();
        setupRecyclerView();
        setupScrollEffect();
        restoreScrollPosition();

        Log.d(TAG, "Setup completed for " + layoutHelper.getScreenType());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupStatusBar() {
        windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());

        if (windowInsetsController != null) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
            windowInsetsController.setAppearanceLightStatusBars(!isDarkMode);
            Log.d(TAG, "Status bar setup - Dark mode: " + isDarkMode);
        }
    }

    private void setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());

            boolean isLandscape = getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE;

            int leftInset = Math.max(systemBars.left, displayCutout.left);
            int rightInset = Math.max(systemBars.right, displayCutout.right);

            if (isLandscape) {
                v.setPadding(0, 0, 0, systemBars.bottom);
                appBarLayout.setPadding(leftInset, systemBars.top, rightInset, 0);
                recyclerView.setPadding(leftInset, 0, rightInset, 0);
            } else {
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
        List<MenuItem> menuList = dataManager.getMenuList();
        adapter = new MenuAdapter(menuList);

        // 클릭 리스너 설정
        adapter.setOnMenuClickListener((menuItem, position) -> {
            // DataManager에 선택된 메뉴 저장
            dataManager.setSelectedMenu(menuItem, position);

            // SampleActivity로 이동
            Intent intent = new Intent(MainActivity.this, SampleActivity.class);
            startActivity(intent);
        });

        setupGridLayout();
        recyclerView.setAdapter(adapter);

        // API에서 아이콘 URL 로드 (최초 1회만)
        if (!dataManager.isIconUrlsLoaded()) {
            Log.d(TAG, "Loading icon URLs from API...");
            loadIconUrlsFromApiAsync(menuList, ICON_API, new IconLoadCallback() {
                @Override
                public void onLoaded() {
                    runOnUiThread(() -> {
                        dataManager.setIconUrlsLoaded(true); // 로드 완료 플래그 설정
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "Icon URLs loaded successfully");
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Icon load failed", e);
                    // 폴백(drawable)로 그대로 표시됨
                }
            });
        } else {
            Log.d(TAG, "Icon URLs already loaded, skipping API call");
        }

        Log.d(TAG, "RecyclerView setup with " + menuList.size() +
                " items, columns: " + layoutHelper.getGridColumns());
    }

    private void setupGridLayout() {
        int columns = layoutHelper.getGridColumns();

        GridLayoutManager gridLayout = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(gridLayout);

        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        boolean includeEdge = true;

        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(columns, spacing, includeEdge)
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Log.d(TAG, String.format(
                "Grid layout: %s, columns: %d, spacing: %ddp",
                layoutHelper.getScreenType(),
                columns,
                spacing
        ));
    }

    private void setupScrollEffect() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float ratio = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                toolbar.setAlpha(1 - ratio);
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

        setupStatusBar();

        if (adapter != null) {
            adapter.updateMenuData(dataManager.getMenuList());
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "Configuration changed: " +
                (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?
                        "Landscape" : "Portrait"));

        setupStatusBar();

        layoutHelper = new ResponsiveLayoutHelper(this);

        GridLayoutManager layoutManager =
                (GridLayoutManager) recyclerView.getLayoutManager();
        int scrollPosition;
        if (layoutManager != null) {
            scrollPosition = layoutManager.findFirstVisibleItemPosition();
        } else {
            scrollPosition = 0;
        }

        setupGridLayout();

        if (adapter != null) {
            adapter.notifyDataSetChanged();

            recyclerView.post(() -> {
                GridLayoutManager glm =
                        (GridLayoutManager) recyclerView.getLayoutManager();
                if (glm != null) {
                    glm.scrollToPositionWithOffset(scrollPosition, 0);
                }
            });
        }
    }

    /**
     * MenuAdapter - 메뉴 그리드 어댑터
     */
    public static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
        private List<MenuItem> menuItems;
        private OnMenuClickListener clickListener;

        // Glide 요청 옵션 (썸네일용)
        private static final RequestOptions GLIDE_OPTIONS = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder_photo)
                .error(R.drawable.placeholder_photo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .timeout(10000);

        public interface OnMenuClickListener {
            void onMenuClick(MenuItem menuItem, int position);
        }

        public MenuAdapter(List<MenuItem> menuItems) {
            this.menuItems = menuItems;
        }

        public void setOnMenuClickListener(OnMenuClickListener listener) {
            this.clickListener = listener;
        }

        public void updateMenuData(List<MenuItem> newMenuItems) {
            this.menuItems = newMenuItems;
            notifyDataSetChanged();
        }

        public void addMenuItem(MenuItem menuItem) {
            menuItems.add(menuItem);
            notifyItemInserted(menuItems.size() - 1);
        }

        public void addMenuItems(List<MenuItem> newMenuItems) {
            int startPosition = menuItems.size();
            menuItems.addAll(newMenuItems);
            notifyItemRangeInserted(startPosition, newMenuItems.size());
        }

        public void removeMenuItem(int position) {
            if (position >= 0 && position < menuItems.size()) {
                menuItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_item, parent, false);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            MenuItem menuItem = menuItems.get(position);

            if (holder.photoTitle != null) {
                holder.photoTitle.setText(menuItem.getTitle());
            }

            // 썸네일 이미지 로드 (캐시 시그니처 포함)
            long cacheKey = System.currentTimeMillis() / 300_000;

            // iconUrl이 있으면 우선적으로 사용, 없으면 로컬 icon 리소스 사용
            if (menuItem.getIconUrl() != null && !menuItem.getIconUrl().isEmpty()) {
                // API에서 가져온 원격 URL 이미지
                Glide.with(holder.itemView.getContext())
                        .load(menuItem.getIconUrl())
                        .apply(GLIDE_OPTIONS)
                        .signature(new ObjectKey(cacheKey))
                        .into(holder.photoImage);
            } else if (menuItem.getIcon() != 0) {
                // 로컬 리소스 아이콘
                Glide.with(holder.itemView.getContext())
                        .load(menuItem.getIcon())
                        .apply(GLIDE_OPTIONS)
                        .signature(new ObjectKey(cacheKey))
                        .into(holder.photoImage);
            } else {
                // 플레이스홀더 표시
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.placeholder_photo)
                        .into(holder.photoImage);
            }

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMenuClick(menuItem, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }

        @Override
        public void onViewRecycled(@NonNull MenuViewHolder holder) {
            super.onViewRecycled(holder);
            Glide.with(holder.itemView.getContext()).clear(holder.photoImage);
        }

        public static class MenuViewHolder extends RecyclerView.ViewHolder {
            TextView photoTitle;
            ImageView photoImage;

            public MenuViewHolder(@NonNull View itemView) {
                super(itemView);
                photoTitle = itemView.findViewById(R.id.photoTitle);
                photoImage = itemView.findViewById(R.id.photoImage);
            }
        }
    }

    /**
     * API에서 아이콘 URL을 비동기로 로드
     * @param list 메뉴 아이템 리스트
     * @param endpoint API 엔드포인트
     * @param cb 콜백
     */
    private void loadIconUrlsFromApiAsync(List<MenuItem> list, String endpoint, IconLoadCallback cb) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Request req = new Request.Builder()
                .url(endpoint)
                .header("User-Agent", "WeatherApp(Android)")
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cb != null) cb.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    if (cb != null) cb.onError(new IOException("HTTP " + response.code()));
                    return;
                }
                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        if (cb != null) cb.onError(new IOException("Empty body"));
                        return;
                    }
                    String json = body.string();
                    // {"files":[{"argument":"radar_nordic_image_reflectivity","url":"http://...jpg"}, ...]}
                    JSONObject root = new JSONObject(json);
                    JSONArray files = root.optJSONArray("files");
                    if (files != null) {
                        // argument → url 맵 구성
                        HashMap<String, String> map = new HashMap<>();
                        for (int i = 0; i < files.length(); i++) {
                            JSONObject o = files.getJSONObject(i);
                            String arg = o.optString("argument", "");
                            String url = o.optString("url", "");
                            if (!arg.isEmpty() && !url.isEmpty()) {
                                map.put(arg, url);
                            }
                        }
                        // vo.apiOption과 argument 매칭하여 iconUrl 세팅
                        for (MenuItem vo : list) {
                            String url = map.get(vo.apiOption);
                            if (url != null) vo.iconUrl = url;
                        }
                    }
                    if (cb != null) cb.onLoaded();
                } catch (Exception e) {
                    if (cb != null) cb.onError(e);
                }
            }
        });
    }
}