package com.lostitemrecovery.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity
{

    private Toolbar ChattoolBar;
    private ImageButton SendMessageButton, SendImageFileButton;
    private EditText userMessageInput;

    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;


    private String messageReceiverID, messageReceiverName, messageReceiverLastName, messageSenderID, saveCurrentDate, saveCurrentTime;

    private TextView receiverName, receiverLastName,userLastSeen;
    private CircleImageView receiverProfileImage;
    private FirebaseAuth mAuth;



    private DatabaseReference RootRef, allUsersDatabaseRef, NotificationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();


        RootRef = FirebaseDatabase.getInstance().getReference();
        allUsersDatabaseRef  = FirebaseDatabase.getInstance().getReference().child("Users");
        NotificationRef  = FirebaseDatabase.getInstance().getReference().child("Notifications");

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("userName").toString();
        messageReceiverLastName = getIntent().getExtras().get("lastName").toString();

        //receiverProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);

        InitializeFields();
        DisplayReceiverInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });

        FetchMessages();
    }

    private void FetchMessages()
    {
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        if (dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }

    private void SendMessage()
    {
       // updateUserStatus("online");


        String messageText = userMessageInput.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Please write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String message_sender_ref = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference user_message_key = RootRef.child("Messages").child(messageSenderID)
                    .child(messageReceiverID).push();

            String message_push_id = user_message_key.getKey();

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMMM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calForTime.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);


            Map messageTextBodyDetails = new HashMap();
            messageTextBodyDetails.put(message_sender_ref + "/" + message_push_id  , messageTextBody);
            messageTextBodyDetails.put(message_receiver_ref + "/" + message_push_id  , messageTextBody);



            RootRef.updateChildren(messageTextBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {//new code added here 7/11/2019
                        HashMap<String, String> chatNotificationMap = new HashMap<>();
                        chatNotificationMap.put("From", messageSenderID);
                        chatNotificationMap.put("type", "text");
                        NotificationRef.child(messageReceiverID).push()
                                .setValue(chatNotificationMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(ChatActivity.this, "Message Send Successfully", Toast.LENGTH_SHORT).show();
                                            userMessageInput.setText("");
                                        }
                                        /*else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            userMessageInput.setText("");

                                        }*/
                                    }
                                });
//code was initially here before moving up
                       // Toast.makeText(ChatActivity.this, "Message Send Successfully", Toast.LENGTH_SHORT).show();
                        //userMessageInput.setText("");


                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");

                    }
                }
            });

        }
    }


    //online status & firebase user last seen
/*
    public void updateUserStatus(String state)
    {
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());


        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        allUsersDatabaseRef.child(messageSenderID).child("userState")
        .updateChildren(currentStateMap);

    }
*/


    private void DisplayReceiverInfo()
    {
        receiverName.setText(messageReceiverName);
        receiverLastName.setText(messageReceiverLastName);


        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    //retrieve from db
                    final String userName = dataSnapshot.child("fullname").getValue().toString();
                    final  String lastName = dataSnapshot.child("lastname").getValue().toString();
                    //online status & firebase user last seen
                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                    //final String type = dataSnapshot.child("userState").child("type").getValue().toString();
                    //final String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    //final String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();

                   /*if (type.equals("online"))
                    {
                        userLastSeen.setText("online");
                    }
                    else
                    {
                        userLastSeen.setText("last seen: " + lastTime + "  " + lastDate);
                    }*/


                    //set values
                    receiverName.setText(userName);
                    receiverLastName.setText(lastName);
                    //Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(receiverProfileImage);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void InitializeFields()
    {
        ChattoolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChattoolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);


        receiverName = (TextView) findViewById(R.id.custom_profile_name);
        receiverLastName = (TextView) findViewById(R.id.custom_profile_last_name);
       //Online Offline State
        //userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        receiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);


        SendMessageButton = (ImageButton) findViewById(R.id.send_send_message_button);
        SendImageFileButton = (ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);


        messagesAdapter = new MessagesAdapter(messagesList);
        userMessageList = (RecyclerView) findViewById(R.id.messages_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messagesAdapter);


    }

}


