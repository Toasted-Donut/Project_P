package com.example.project_p;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.InputStream;
import java.util.Properties;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChecksListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChecksListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout checksLayout;

    public ChecksListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChecksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChecksListFragment newInstance(String param1, String param2) {
        ChecksListFragment fragment = new ChecksListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checks_list, container, false);
        checksLayout = view.findViewById(R.id.line_layout);
        if(((MainActivity)getActivity()).MAILBOX != null){
            add_checks(((MainActivity)getActivity()).MAILBOX.checks.size());
        }
        mRefreshLayout = view.findViewById(R.id.pullToRefresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainActivity mainActivity = (MainActivity)getActivity();
                            SharedPreferences settings = mainActivity.getSharedPreferences("settings", Context.MODE_PRIVATE);
                            InputStream InputStream = null;
                            Log.i("prop","start");
                            InputStream = mainActivity.getAssets().open("mail.properties");
                            Properties properties = new Properties();
                            properties.load(InputStream);
                            properties.setProperty("mail.imap.user",settings.getString("login","example@yandex.ru"));
                            properties.setProperty("mail.imap.password",settings.getString("password","123"));
                            mainActivity.MAILBOX = new Mail(properties);
                            Log.i("prop","mid");
                            mainActivity.MAILBOX.getMessages();
                            ChecksListFragment.this.add_checks(mainActivity.MAILBOX.checks.size());
                            Log.i("prop", mainActivity.MAILBOX.toJson());
                            mainActivity.MAILBOX.save(mainActivity);
                            mRefreshLayout.setRefreshing(false);
                        }
                        catch (Exception e) {
                            Log.i("prop",e.toString());
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        return view;
    }


    public void add_checks(int amount){
        for (int i = 0; i < amount; i++) {
            if(getFragmentManager().findFragmentByTag(String.valueOf(((MainActivity) getActivity()).MAILBOX.checks.get(i).ID))==null){
                Log.i("prop",String.valueOf(i));

                getFragmentManager().beginTransaction().add(R.id.line_layout,
                        CheckTemplateFragment.newInstance(
                                ((MainActivity) getActivity()).MAILBOX.checks.get(i).date,
                                ((MainActivity) getActivity()).MAILBOX.checks.get(i).filePath),
                        String.valueOf(((MainActivity) getActivity()).MAILBOX.checks.get(i).ID)).commit();
            }
        }
    }


}