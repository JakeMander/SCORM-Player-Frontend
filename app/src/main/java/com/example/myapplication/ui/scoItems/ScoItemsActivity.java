package com.example.myapplication.ui.scoItems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.login.LoginViewModel;
import com.example.myapplication.ui.login.LoginViewModelFactory;

public class ScoItemsActivity extends AppCompatActivity {

    private Context mAppContext;
    private ScoItemsActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = getApplicationContext();
        mViewModel = new ViewModelProvider(this, new ScoItemsActivityVMFactory(mAppContext))
                .get(ScoItemsActivityViewModel.class);

        setContentView(R.layout.activity_sco_items);
        TextView greetingView = findViewById(R.id.scoItemsGreeting);

        mViewModel.getLoggedInUser().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String loggedInUser) {
                greetingView.setText(getString(R.string.welcome) + loggedInUser + "!");
            }
        });

        mViewModel.retrieveLoggedInUser();
    }
}