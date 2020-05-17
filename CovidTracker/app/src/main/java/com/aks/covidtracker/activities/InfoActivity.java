package com.aks.covidtracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.aks.covidtracker.Constants;
import com.aks.covidtracker.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getString(R.string.developer_info));
    }


    public void onGithubClicked(View view){
        Uri githubUri = Uri.parse(Constants.GITHUB_PROFILE);
        Intent githubIntent = new Intent(Intent.ACTION_VIEW,githubUri);
        startActivity(githubIntent);
    }

    public void onLinkedInClicked(View view){
        Uri linkedinUri = Uri.parse(Constants.LINKEDIN_PROFILE);
        Intent linkedinIntent = new Intent(Intent.ACTION_VIEW,linkedinUri);
        startActivity(linkedinIntent);
    }
}

