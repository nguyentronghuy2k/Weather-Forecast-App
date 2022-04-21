package com.example.weatherapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInForm_Activity extends AppCompatActivity {

    private Button btnSignIn;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_form_);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(view -> {
            switch (view.getId()) {
                case R.id.btnSignIn:
                    signIn();
                    break;

            }


        });


        //Thiết lập đăng nhập yêu cầu user id , địa chỉ email
        //profile id và các thông tin cơ bản khác được lưu trữ trong DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kết quả trả về từ việc chạy Intent từ GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();


            // Đăng nhập thành công và chuyển qua giao diện dự báo thời tiết
            Intent intent = new Intent(SignInForm_Activity.this, HomeWeather.class);
            startActivity(intent);


        } catch (ApiException e) {
            Toast.makeText(this, "Có lỗi xảy ra" + e.getStatusCode(), Toast.LENGTH_SHORT).show();


        }
    }


}