package com.boolint.photogallery;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView의 GridLayoutManager를 위한 개선된 아이템 간격 데코레이션
 * 균등한 간격 배치를 보장
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;
    private final boolean includeEdge;

    /**
     * @param spanCount   그리드 컬럼 수
     * @param spacing     아이템 간격 (픽셀)
     * @param includeEdge 가장자리에도 간격 적용 여부
     */
    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        int column = position % spanCount;

        if (includeEdge) {
            // 가장자리 포함: 모든 아이템이 동일한 전체 너비를 갖도록 계산
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            // 첫 번째 행
            if (position < spanCount) {
                outRect.top = spacing;
            }
            // 모든 아이템의 하단
            outRect.bottom = spacing;
        } else {
            // 가장자리 제외: 아이템 사이만 간격
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;

            // 첫 번째 행이 아닌 경우
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }

    /**
     * 동적으로 spanCount를 변경할 수 있는 버전
     */
    public static class Dynamic extends RecyclerView.ItemDecoration {
        private int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public Dynamic(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        public void setSpanCount(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;

                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}