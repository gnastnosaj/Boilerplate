package com.github.gnastnosaj.boilerplate.mvchelper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gnastnosaj.boilerplate.R;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.view.vary.VaryViewHelper;

/**
 * Created by jason on 8/18/2016.
 */
public class LoadViewFactory implements ILoadViewFactory {

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    @Override
    public ILoadView madeLoadView() {
        return new LoadViewHelper();
    }

    private class LoadMoreHelper implements ILoadMoreView {

        protected TextView footView;

        protected View.OnClickListener onClickRefreshListener;

        @Override
        public void init(FootViewAdder footViewHolder, View.OnClickListener onClickRefreshListener) {
            View contentView = footViewHolder.getContentView();

            Context context = contentView.getContext();
            TextView textView = new TextView(context);
            textView.setTextColor(Color.GRAY);
            textView.setPadding(0, dip2px(context, 16), 0, dip2px(context, 16));
            textView.setGravity(Gravity.CENTER);
            footViewHolder.addFootView(textView);

            footView = textView;
            this.onClickRefreshListener = onClickRefreshListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            footView.setText(R.string.mvchelper_loadMore_normal);
            footView.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showLoading() {
            footView.setText(R.string.mvchelper_loadMore_loading);
            footView.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception exception) {
            footView.setText(R.string.mvchelper_loadMore_fail);
            footView.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showNomore() {
            //footView.setText(R.string.mvchelper_loadMore_nomore);
            footView.setText("");
            footView.setOnClickListener(null);
        }

    }

    private class LoadViewHelper implements ILoadView {
        private VaryViewHelper helper;
        private View.OnClickListener onClickRefreshListener;
        private Context context;

        @Override
        public void init(View switchView, View.OnClickListener onClickRefreshListener) {
            this.context = switchView.getContext().getApplicationContext();
            this.onClickRefreshListener = onClickRefreshListener;
            helper = new VaryViewHelper(switchView);
        }

        @Override
        public void restore() {
            helper.restoreView();
        }

        @Override
        public void showLoading() {
            Context context = helper.getContext();

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);

            ProgressBar progressBar = new ProgressBar(context);
            layout.addView(progressBar);

            TextView textView = new TextView(context);
            textView.setText(R.string.mvchelper_load_loading);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int top = dip2px(context, 12);
            params.setMargins(0, top, 0, 0);
            layout.addView(textView, params);

            helper.showLayout(layout);
        }

        @Override
        public void tipFail(Exception exception) {
            Toast.makeText(context, R.string.mvchelper_load_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showFail(Exception exception) {
            Context context = helper.getContext();

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);

            TextView textView = new TextView(context);
            textView.setText(R.string.mvchelper_load_fail);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            Button button = new Button(context);
            button.setText(R.string.mvchelper_load_tryagain);
            button.setOnClickListener(onClickRefreshListener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int top = dip2px(context, 12);
            params.setMargins(0, top, 0, 0);
            layout.addView(button, params);

            helper.showLayout(layout);
        }

        @Override
        public void showEmpty() {
            Context context = helper.getContext();

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);

            TextView textView = new TextView(context);
            textView.setText(R.string.mvchelper_load_empty);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            Button button = new Button(context);
            button.setText(R.string.mvchelper_load_tryagain);
            button.setOnClickListener(onClickRefreshListener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int top = dip2px(context, 12);
            params.setMargins(0, top, 0, 0);
            layout.addView(button, params);

            helper.showLayout(layout);
        }

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
