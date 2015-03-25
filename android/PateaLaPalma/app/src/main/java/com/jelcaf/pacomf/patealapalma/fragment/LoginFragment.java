package com.jelcaf.pacomf.patealapalma.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;
import com.jelcaf.pacomf.patealapalma.R;
import com.jelcaf.pacomf.patealapalma.activity.SenderosSwipeActivity;
import com.jelcaf.pacomf.patealapalma.login.LoginMethods;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    private View rootView;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        initializeViews(rootView);

        return rootView;
    }

    private void initializeViews(View rootView) {
        ButtonRectangle loginBtn = (ButtonRectangle)rootView.findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginMethods.openFacebookSession(getActivity(), SenderosSwipeActivity.class);
            }
        });
    }

}
