package com.larryhowell.xunta.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberPlanFragment extends Fragment {
    @Bind(R.id.ripple)
    MaterialRippleLayout mRippleLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_plan, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        mRippleLayout.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), MakePlanActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(),
                        getActivity().findViewById(R.id.appbar), "appBar").toBundle()));
    }
}
