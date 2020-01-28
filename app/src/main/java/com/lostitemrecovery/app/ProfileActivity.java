package com.lostitemrecovery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity
{
    private TextView userName, userProfName, userProfLastName, userStatus, userCountry, userGender, userLostItemStatus, userDOB;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, PostsRef;
    private FirebaseAuth mAuth;

    private Button MyPosts, MyMessages;

    private String currentUserID;
    //private int countPosts = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth =  FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        //PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        userName =(TextView) findViewById(R.id.my_username);
        userProfName = (TextView) findViewById(R.id.my_profile_full_name);
        userProfLastName = (TextView) findViewById(R.id.my_profile_last_name);

        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userCountry = (TextView) findViewById(R.id.my_country);
        userGender = (TextView) findViewById(R.id.my_gender);
        userLostItemStatus = (TextView) findViewById(R.id.my_lost_item_status);
        userDOB = (TextView) findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);

        MyMessages = (Button) findViewById(R.id.my_messages_button);
        //MyPosts = (Button) findViewById(R.id.my_post_button);


        MyMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToFindPeopleWhoLostOrFoundActivity();
            }
        });

      /*  MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToMyPostsActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserID).endAt(currentUserID + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                    if (dataSnapshot.exists())
                    {
                        countPosts = (int) dataSnapshot.getChildrenCount();
                        MyPosts.setText(Integer.toString(countPosts) + "Posts");
                    }

                    else
                    {
                        MyPosts.setText("0Post(s)");
                    }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

                */

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileLastName = dataSnapshot.child("lastname").getValue().toString();

                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myLostItemStatus = dataSnapshot.child("lostitemstatus").getValue().toString();


                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userProfLastName.setText(myProfileLastName);

                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: " + myDOB);
                    userCountry.setText("Country: "+ myCountry);
                    userGender.setText("Gender: " + myGender);
                    userLostItemStatus.setText("Lost Item Status: " + myLostItemStatus);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


    }

    private void SendUserToFindPeopleWhoLostOrFoundActivity()
    {

        Intent LoginIntent = new Intent(ProfileActivity.this, FindPeopleWhoLostOrFoundActivity.class);
        startActivity(LoginIntent);
    }


    //private void SendUserToMyPostsActivity()
    {

       // Intent LoginIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
       // startActivity(LoginIntent);
    }
}
