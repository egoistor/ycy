package com.example.ycy.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.avos.avoscloud.AVUser;
import com.example.ycy.R;
import com.example.ycy.activity.LoginActivity;
import com.example.ycy.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {
    View view;
    Button mLogoutButton;

    SharedPreferences.Editor editor;
    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        mLogoutButton = view.findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();// 清除缓存用户对象

                editor = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
