package com.londonappbrewery.chatbyDimon;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // member variables
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;
    private android.support.v7.widget.Toolbar mBar;
    private MenuItem mChatMenu;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // Set up the display name and get the Firebase reference
        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = findViewById(R.id.messageInput);
        mSendButton = findViewById(R.id.sendButton);
        mChatListView = findViewById(R.id.chat_list_view);
        mBar = findViewById(R.id.toolbar);

        //Toolbar & menu init
        setupToolbar();

        // Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        // onClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }
    // set up the toolbar params and menu logic
    private void setupToolbar(){
        mBar.setTitle("Chat");
        mBar.inflateMenu(R.menu.chatmenu);
        mBar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logOut();
                        return true;
                    case R.id.about:
                        showAbout();
                        return true;
                }
                return false;
            }
        });
    }

    // Retrieve the display name from Firebase
    private void setupDisplayName(){
    // outdated method to save Display name in Shared Preferences
//        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
//        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
//        if(mDisplayName== null) mDisplayName = "Anonymous";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName = user.getDisplayName();
    }


    //Grab the text the user typed in and push the message to Firebase
    private void sendMessage() {
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            Log.d("chatlog","sendMessage executed");
            InstantMessage msg = new InstantMessage(input,mDisplayName);
            Log.d("chatlog","msgbody: "+ msg.getMessage() + "msgauthor: " + msg.getAuthor());
            mDatabaseReference.child("messages").push().setValue(msg);
            mInputText.setText("");

        }

    }
    private void showAbout(){
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("This app was build by Dima Brandorf 13/10/18")
                .setPositiveButton(R.string.close,null)
                .setIcon(android.R.drawable.btn_star_big_on)
                .show();
    }


    public void logOut()
    {
     FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MainChatActivity.this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    // Override the onStart() lifecycle method. Setup the adapter here.
    public void onStart() {
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }



    @Override
    public void onStop(){
        super.onStop();
        //Remove the Firebase event listener on the adapter.
        mAdapter.cleanUp();

    }

}
