package com.boolint.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

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

    // AdMob
    private FrameLayout adContainerView;
    private AdView adView;

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

        // AdMob 초기화
        initializeAdMob();

        initViews();
        setupStatusBar();
        setupEdgeToEdgeInsets();
        setupToolbar();
        setupRecyclerView();
        setupScrollEffect();
        restoreScrollPosition();

        // 배너 광고 로드
        loadBannerAd();

        Log.d(TAG, "Setup completed for " + layoutHelper.getScreenType());
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "AdMob initialized");
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        adContainerView = findViewById(R.id.adContainerView);
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
                v.setPadding(0, 0, 0, 0);
                appBarLayout.setPadding(leftInset, systemBars.top, rightInset, 0);
                recyclerView.setPadding(leftInset, 0, rightInset, 0);
                adContainerView.setPadding(leftInset, 0, rightInset, systemBars.bottom);
            } else {
                v.setPadding(0, 0, 0, 0);
                appBarLayout.setPadding(0, systemBars.top, 0, 0);
                recyclerView.setPadding(0, 0, 0, 0);
                adContainerView.setPadding(0, 0, 0, systemBars.bottom);
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
            dataManager.setSelectedMenu(menuItem, position);
            Intent intent = new Intent(MainActivity.this, SampleActivity.class);
            startActivity(intent);
        });

        setupGridLayout();
        recyclerView.setAdapter(adapter);

        // API에서 아이콘 URL 로드
        if (!dataManager.isIconUrlsLoaded()) {
            Log.d(TAG, "Loading icon URLs from API...");
            loadIconUrlsFromApiAsync(menuList, ICON_API, new IconLoadCallback() {
                @Override
                public void onLoaded() {
                    runOnUiThread(() -> {
                        dataManager.setIconUrlsLoaded(true);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "Icon URLs loaded successfully");
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Icon load failed", e);
                }
            });
        } else {
            Log.d(TAG, "Icon URLs already loaded, skipping API call");
        }

        Log.d(TAG, "RecyclerView setup with " + menuList.size() +
                " items, columns: " + layoutHelper.getGridColumns());
    }

    private void setupGridLayout() {
        // ResponsiveLayoutHelper에서 동적으로 값 가져오기
        int columns = layoutHelper.getGridColumns();
        int spacing = layoutHelper.getGridSpacing();

        GridLayoutManager gridLayout = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(gridLayout);

        // 기존 decoration 제거
        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }

        // dp를 픽셀로 변환
        float density = getResources().getDisplayMetrics().density;
        int spacingPx = (int) (spacing * density);
        boolean includeEdge = true;

        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(columns, spacingPx, includeEdge)
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

    private void loadBannerAd() {
        if (adView != null) {
            adContainerView.removeAllViews();
            adView.destroy();
        }

        AdSize adSize = getAdSize();
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adView.setAdSize(adSize);

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Log.d(TAG, "Banner ad loaded with size: " + adSize.toString());
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;
        float adWidthPixels = adContainerView.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
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
        if (adView != null) {
            adView.resume();
        }
        Log.d(TAG, "onResume called");

        setupStatusBar();

        if (adapter != null) {
            adapter.updateMenuData(dataManager.getMenuList());
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "Configuration changed: " +
                (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?
                        "Landscape" : "Portrait"));

        setupStatusBar();

        // ResponsiveLayoutHelper 재생성
        layoutHelper = new ResponsiveLayoutHelper(this);
        Log.d(TAG, layoutHelper.getDebugInfo());

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

        // 광고 크기 재조정
        recyclerView.post(() -> loadBannerAd());
    }

    public static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
        private List<MenuItem> menuItems;
        private OnMenuClickListener clickListener;

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

            long cacheKey = System.currentTimeMillis() / 300_000;

            if (menuItem.getIconUrl() != null && !menuItem.getIconUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(menuItem.getIconUrl())
                        .apply(GLIDE_OPTIONS)
                        .signature(new ObjectKey(cacheKey))
                        .into(holder.photoImage);
            } else if (menuItem.getIcon() != 0) {
                Glide.with(holder.itemView.getContext())
                        .load(menuItem.getIcon())
                        .apply(GLIDE_OPTIONS)
                        .signature(new ObjectKey(cacheKey))
                        .into(holder.photoImage);
            } else {
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
                    JSONObject root = new JSONObject(json);
                    JSONArray files = root.optJSONArray("files");
                    if (files != null) {
                        HashMap<String, String> map = new HashMap<>();
                        for (int i = 0; i < files.length(); i++) {
                            JSONObject o = files.getJSONObject(i);
                            String arg = o.optString("argument", "");
                            String url = o.optString("url", "");
                            if (!arg.isEmpty() && !url.isEmpty()) {
                                map.put(arg, url);
                            }
                        }
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