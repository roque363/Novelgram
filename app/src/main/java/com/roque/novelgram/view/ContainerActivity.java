package com.roque.novelgram.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.novelgram.R;
import com.roque.novelgram.login.view.LoginActivity;
import com.roque.novelgram.post.view.HomeFragment;
import com.roque.novelgram.view.fragment.ProfileFragment;
import com.roque.novelgram.view.fragment.SearchFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ContainerActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String TAG = "ContainerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        firebaseInitialize();

        final HomeFragment homeFragment = new HomeFragment();
        final ProfileFragment profileFragment = new ProfileFragment();
        final SearchFragment searchFragment = new SearchFragment();

        // Set HomeFragment as Default on first load (Login)
        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, homeFragment)
                    .commit();
        }

        BottomNavigationView bottombar = findViewById(R.id.bottonbar);
        bottombar.setSelectedItemId(R.id.home);

        bottombar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.home:
                                addFragment(homeFragment);
                                break;
                            case R.id.profile:
                                addFragment(profileFragment);
                                break;
                            case R.id.search:
                                addFragment(searchFragment);
                                break;
                        }
                        return true;
                    }

                    // Set fragment
                    private void addFragment(Fragment fragment){
                        if (null != fragment) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .disallowAddToBackStack()
                                    .commit();
                        }
                    }
                }
        );
    }

    private void firebaseInitialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user is signed in
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.w(TAG, "Usuario Logeado" + currentUser.getEmail());
                } else {
                    Log.w(TAG, "Usuario NO Logeado");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSignOut:
                firebaseAuth.signOut();
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
                Toast.makeText(this,"Se cerró la sesión",Toast.LENGTH_SHORT).show();
                goLogin();
                break;
            case R.id.mAbout:
                Toast.makeText(this,"Novelgram by Roque",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goLogin () {
        Intent intent = new Intent(ContainerActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
