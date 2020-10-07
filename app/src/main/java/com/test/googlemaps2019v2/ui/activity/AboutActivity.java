package com.test.googlemaps2019v2.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.test.googlemaps2019v2.R;

public class AboutActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        Menu navMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = navMenu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_sign_out:{
                                signOut();
                                return true;
                            }
                            case R.id.action_chats:{
                                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            case R.id.action_profile:{
                                Intent intent = new Intent(AboutActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            case R.id.action_donate:{

                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
