package com.example.peelingv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button _btnLogin;
    EditText _edtUsername,_edtPassword;
    String edtUsername,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding();



        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(_edtUsername.getText().toString().equals("admin") && _edtPassword.getText().toString().equals("123456")) {
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Login.this, "Login สำเร็จ " + edtUsername, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Login.this, "Login ไม่สำเร็จ " + edtUsername, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void binding() {

        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _edtUsername = (EditText) findViewById(R.id.edtUsersname);
        _edtPassword = (EditText) findViewById(R.id.edtPassword);

    }


}