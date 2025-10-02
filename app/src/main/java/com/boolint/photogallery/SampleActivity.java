package com.boolint.photogallery;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

/**
 * 메뉴 아이템 상세 화면
 * DataManager에서 선택된 메뉴 정보를 가져와 표시
 */
public class SampleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvId;
    private TextView tvKind;
    private TextView tvApiOption;
    private TextView tvData0;
    private TextView tvArea0;

    private DataManager dataManager;
    private MenuItem menuItem;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // DataManager에서 선택된 메뉴 가져오기
        dataManager = DataManager.getInstance();
        menuItem = dataManager.getSelectedMenuItem();
        position = dataManager.getSelectedMenuPosition();

        initViews();
        setupToolbar();
        displayMenuInfo();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.imageView);
        tvTitle = findViewById(R.id.tvTitle);
        tvId = findViewById(R.id.tvId);
        tvKind = findViewById(R.id.tvKind);
        tvApiOption = findViewById(R.id.tvApiOption);
        tvData0 = findViewById(R.id.tvData0);
        tvArea0 = findViewById(R.id.tvArea0);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("상세 정보");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayMenuInfo() {
        if (menuItem == null) {
            tvTitle.setText("메뉴 정보 없음");
            return;
        }

        // 메뉴 정보 표시
        tvTitle.setText("제목: " + (menuItem.title != null ? menuItem.title : "N/A"));
        tvId.setText("ID: " + (menuItem.id != null ? menuItem.id : "N/A"));
        tvKind.setText("종류: " + (menuItem.kind != null ? menuItem.kind : "N/A"));
        tvApiOption.setText("API 옵션: " + (menuItem.apiOption != null ? menuItem.apiOption : "N/A"));
        tvData0.setText("Data0: " + (menuItem.data0 != null ? menuItem.data0 : "N/A"));
        tvArea0.setText("Area0: " + (menuItem.area0 != null ? menuItem.area0 : "N/A"));

        // 이미지 로드
        long cacheKey = System.currentTimeMillis() / 300_000;
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder_photo)
                .error(R.drawable.placeholder_photo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .signature(new ObjectKey(cacheKey));

        // iconUrl이 있으면 우선 사용, 없으면 로컬 icon 사용
        if (menuItem.iconUrl != null && !menuItem.iconUrl.isEmpty()) {
            Glide.with(this)
                    .load(menuItem.iconUrl)
                    .apply(options)
                    .into(imageView);
        } else if (menuItem.icon != 0) {
            Glide.with(this)
                    .load(menuItem.icon)
                    .apply(options)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_photo);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}