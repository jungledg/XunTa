package com.larryhowell.xunta.ui;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.adapter.BindListAdapter;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.BindPresenterImpl;
import com.larryhowell.xunta.presenter.GetBindListPresenterImpl;
import com.larryhowell.xunta.presenter.IBindPresenter;
import com.larryhowell.xunta.presenter.IGetBindListPresenter;
import com.larryhowell.xunta.widget.DividerItemDecoration;
import com.larryhowell.xunta.zxing.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class BindListActivityBackup extends AppCompatActivity
        implements IGetBindListPresenter.IGetBindListView, IBindPresenter.IBindView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.floating_actions)
    FloatingActionsMenu mFloatingActionsMenu;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private BindListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private MaterialDialog mDialog;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bind_list);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        mProgressDialog = new ProgressDialog(this, android.R.style.Theme_Material);
        mProgressDialog.setMessage("绑定中...");

        // 延迟执行才能使旋转进度条显示出来
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            refresh();
        }, 200);
    }

    private void refresh() {
        if (!Config.isConnected) {
            UtilBox.showSnackbar(this, R.string.cant_access_network);

            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        new GetBindListPresenterImpl(this).getBindList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFloatingActionButton.setVisibility(View.GONE);
        MobclickAgent.onResume(this);
    }

    @Override
    public void OnGetBindListResult(Boolean result, String info) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (result) {
            if (mAdapter == null) {
                mAdapter = new BindListAdapter(this);

                mAdapter.setOnItemClickListener((view, person) -> {
                    //mFloatingActionButton.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(BindListActivityBackup.this, MemberMainActivity.class);
                    intent.putExtra("person", person);
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    BindListActivityBackup.this,
                                    //Pair.create(mFloatingActionButton, "fab"),
                                    Pair.create(mAppBarLayout, "appBar")).toBundle());
                });

                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }

    @OnClick({R.id.fab_qr, R.id.fab_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_qr:
                startActivity(new Intent(this, CaptureActivity.class));
                mFloatingActionsMenu.collapse();
                break;

            case R.id.fab_input:
                showAddMemberDialog();
                mFloatingActionsMenu.collapse();
                break;
        }
    }

    private void showAddMemberDialog() {
        final View contentView = getLayoutInflater().inflate(R.layout.dialog_input, null);

        TextInputLayout til = (TextInputLayout) contentView.findViewById(R.id.til_input);
        til.setHint("手机号");

        mTextView = (TextView) contentView.findViewById(R.id.tv_input);

        final EditText editText = ((EditText) contentView
                .findViewById(R.id.edt_input));
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.requestFocus();

        mDialog = new MaterialDialog(this);
        mDialog.setPositiveButton("绑定", v -> {
            if (TextUtils.isEmpty(editText.getText())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入对方手机号");
            } else if (!Config.isConnected) {
                Toast.makeText(this, R.string.cant_access_network,
                        Toast.LENGTH_SHORT).show();
            } else if (!UtilBox.isTelephoneNumber(editText.getText().toString())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入11位手机号");
            } else {
                mProgressDialog.show();

                new BindPresenterImpl(BindListActivityBackup.this).bind(editText.getText().toString());
            }
        }).setNegativeButton("取消", v -> {
            mDialog.dismiss();
        }).setContentView(contentView)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    @Override
    public void onBindResult(Boolean result, String info) {
        mProgressDialog.dismiss();

        if (result) {
            mDialog.dismiss();
            UtilBox.showSnackbar(this, "已向对方发出申请");
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(info);
        }
    }
}
