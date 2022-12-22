package com.example.project_p;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckTemplateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckTemplateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_DATE = "check_date";
    private static final String ARG_PARAM_FILE = "check_file";

    // TODO: Rename and change types of parameters
    private String mDateParam;
    private String mFileParam;
    private boolean isExpanded = false;

    public CheckTemplateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckTemplateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckTemplateFragment newInstance(String param1, String param2) {
        CheckTemplateFragment fragment = new CheckTemplateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_DATE, param1);
        args.putString(ARG_PARAM_FILE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDateParam = getArguments().getString(ARG_PARAM_DATE);
            mFileParam = getArguments().getString(ARG_PARAM_FILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_template, container, false);
        TextView btn = view.findViewById(R.id.btn_expand);
        TextView date = view.findViewById(R.id.check_date);
        WebView extra = view.findViewById(R.id.check_extra);
        date.setText(mDateParam);
        extra.setInitialScale(1);
        extra.getSettings().setAllowFileAccess(true);
        extra.getSettings().setLoadWithOverviewMode(true);
        extra.getSettings().setUseWideViewPort(true);
        extra.loadUrl(MyApplication.getAppContext().getFilesDir()+"/"+mFileParam);    //TBD нахуй загрузку  в веб вью лучше текст из чека
        final Animation anim_rot_cw = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_cw);
        final Animation anim_rot_ccw = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_ccw);

        extra.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isExpanded = !isExpanded;
                if(isExpanded){
                    extra.setVisibility(View.VISIBLE);
                    btn.startAnimation(anim_rot_cw);
                }
                else {
                    extra.setVisibility(View.GONE);
                    btn.startAnimation(anim_rot_ccw);
                }
            }
        });

        return view;
    }
}