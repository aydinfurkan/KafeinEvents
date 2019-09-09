package com.example.kafeinevents.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kafeinevents.R;
import com.example.kafeinevents.api.ApiUtils;
import com.example.kafeinevents.api.ResObj;
import com.example.kafeinevents.api.UserService;
import com.example.kafeinevents.data.model.UserModel;
import com.example.kafeinevents.database.DatabaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button button;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.loginButton);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        userService = ApiUtils.getUserService();


        SharedPreferences SP = getSharedPreferences("com.example.kafeinevents.login", MODE_PRIVATE);
        String Email = SP.getString("email", "");
        String Password = SP.getString("password", "");

        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || !Email.contains("@")){
            Toast.makeText(LoginActivity.this, "Lütfen giriş yapınız.", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingStart();
            doLogin(Email, Password);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                    Toast.makeText(LoginActivity.this, "Email ya da şifre boş olamaz.", Toast.LENGTH_SHORT).show();
                } else if (!Email.contains("@")) {
                    Toast.makeText(LoginActivity.this, "Email geçerli değil.", Toast.LENGTH_SHORT).show();
                } else {
                    loadingStart();
                    doLogin(Email, Password);
                }

            }
        });

    }

    private void doLogin(final String email, final String password) {

        Call<ResObj> call = userService.login(email, password, "password");
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful() && response.body().getAccess_token() != null) {

                    rememberMe(email, password);

                    final DatabaseUser database = new DatabaseUser();

                    database.isUserExists(email, new DatabaseUser.OncheckUserExists(){

                        @Override
                        public void exist(String userId) {
                            Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void notExist() {
                            String name = email.substring(0, email.indexOf("@"));
                            UserModel userModel = new UserModel("", name, email);
                            String userId = database.createNewUser(userModel);
                            Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }
                    });

                } else {
                    loadingEnd();
                    Toast.makeText(LoginActivity.this, "Email ya da şifre hatalı.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                loadingEnd();
                Toast.makeText(LoginActivity.this, "Sunucu yanıt vermiyor.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void rememberMe(String email, String password) {

        SharedPreferences SP = getSharedPreferences("com.example.kafeinevents.login", MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();

    }

    private void loadingStart() {

        ProgressBar loading = findViewById(R.id.loginLoading);

        button.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

    }

    private void loadingEnd() {

        ProgressBar loading = findViewById(R.id.loginLoading);

        button.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

    }

}
