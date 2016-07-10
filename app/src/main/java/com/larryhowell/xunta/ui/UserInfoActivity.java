package com.larryhowell.xunta.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_nickname)
    TextView mNicknameTextView;

    @Bind(R.id.iv_portrait)
    ImageView mPortraitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNicknameTextView.setText(Config.nickname);

        ImageLoader.getInstance().displayImage(Config.portrait, mPortraitImageView);
    }

    @OnClick({R.id.rv_portrait, R.id.rv_nickname, R.id.iv_portrait, R.id.rv_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rv_portrait:
                break;

            case R.id.rv_nickname:
                break;

            case R.id.iv_portrait:
                break;

            case R.id.rv_logout:
                break;
        }
    }
}
