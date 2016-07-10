package com.larryhowell.xunta.ui;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.ILoginPresenter;
import com.larryhowell.xunta.presenter.LoginPresenterImpl;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ILoginPresenter.ILoginView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.edt_telephone)
    EditText mEditText;

    @Bind(R.id.rl_login)
    MaterialRippleLayout mRippleLayout;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.btn_login)
    Button mButton;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("登录中...");

        mRippleLayout.setOnClickListener(v -> {
            if (!Config.isConnected) {
                UtilBox.showSnackbar(LoginActivity.this, R.string.cant_access_network);
                return;
            }

            if (!UtilBox.isTelephoneNumber(mEditText.getText().toString())) {
                UtilBox.showSnackbar(LoginActivity.this, "请输入11位手机号");
                return;
            }

            mProgressDialog.show();

            UtilBox.toggleSoftInput(mEditText, false);

            new LoginPresenterImpl(LoginActivity.this).login(mEditText.getText().toString());
        });
    }

    @Override
    public void onLoginResult(Boolean result, String info) {
        mProgressDialog.dismiss();
        if (result) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }
}
