package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {


    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter arrayAdapter ;
    ListView listView ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweet_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.tweet)
        {
            Log.i("Tweet ","now you can send tweets");
            AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle("Send a tweet");
            final  EditText tweetEditText = new EditText(this);
            builder.setView(tweetEditText);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("hit",tweetEditText.getText().toString());
                    ParseObject tweet = new ParseObject("tweet");
                    tweet.put("tweet",tweetEditText.getText().toString());
                    tweet.put("username",ParseUser.getCurrentUser().getUsername());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                            {
                                Toast.makeText(UserActivity.this, " Tweet sent! ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(UserActivity.this, "Tweet failed !", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("hit","cancle button");
                    dialog.cancel();
                }
            });

            builder.show();
        }else if (item.getItemId() == R.id.logout)
        {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.feed)
        {
            Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("The user List");
        listView = findViewById(R.id.list);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);//for the check on the list

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked())
                {
                    ParseUser.getCurrentUser().add("isFollowing",users.get(position));//when the list is checked
                    Log.i("checked","is checked");
                }else {
                    Log.i("checked","not checked");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",tempUsers);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground((objects, e) -> {
            if(e== null && objects.size()>0)
            {
                for(ParseUser parseUser : objects)
                {
                    users.add(parseUser.getUsername());
                }
                arrayAdapter.notifyDataSetChanged();


            }
            for (String username : users)
            {
                try
                {
                    if(ParseUser.getCurrentUser().getList("isFollowing").contains(username))
                    {
                        listView.setItemChecked(users.indexOf(username),true);
                    }
                }catch (Exception exception )
                {
                    exception.getMessage();
                }
            }

// as when we reload the app the checked item will no longer be there that is why we loop through the elements to keep the checked item checked

        });


    }
}