/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  Boolean SignUpModeActive = true;
  EditText Username ;
  EditText password ;
  Button button ;
  TextView textView ;

  public void redirectUsers()
  {
    if (ParseUser.getCurrentUser()!=null)
    {
      Intent intent = new Intent(getApplicationContext(),UserActivity.class);
      startActivity(intent);
    }
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setTitle("Twitter");
    textView = findViewById(R.id.textView_Pressed);
    Username = findViewById(R.id.username);
    password = findViewById(R.id.password);
    button = findViewById(R.id.button_Pressed);
  redirectUsers();
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
  public void SignupLogin(View view)
  {
    if(view.getId() == R.id.textView_Pressed)
    {
      Log.i("Switch","was tapped");
      if(SignUpModeActive)
      {
        SignUpModeActive=false;
        button.setText("Login");
        textView.setText("or,Signup");
      }else {
        SignUpModeActive=true;
        button.setText("Signup");
        textView.setText("or,Login");
      }
    }

  }
  public void Login(View view)
  {
    if(Username.getText().toString().matches("")|| password.getText().toString().matches(""))
    {
      Toast.makeText(this, "A username and password is required ", Toast.LENGTH_SHORT).show();
    }else {
      //SignUP
      if(SignUpModeActive)
      {
        ParseUser user = new ParseUser();
        user.setUsername(Username.getText().toString());
        user.setPassword(password.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if(e== null)
            {
              Log.i("Signup","Success");
              redirectUsers();
            }else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });

      }
      else
        //Login
      {
        ParseUser.logInInBackground(Username.getText().toString(),password.getText().toString(),(user, e) -> {

          if (user!=null)
          {
            Log.i("Info","Login ok !");
            redirectUsers();
          }
          else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });

      }
    }
  }

}