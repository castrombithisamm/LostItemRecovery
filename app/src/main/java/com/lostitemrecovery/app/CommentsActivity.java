package com.lostitemrecovery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class CommentsActivity extends AppCompatActivity {

    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String Post_Key, current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");


        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("username").getValue().toString();
                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(PostsRef, Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int i, @NonNull Comments comments) {
                final String userName = getRef(i).getKey();
                final String usersIDS = getRef(i).getKey();
                final String lastName = getRef(i).getKey();

                commentsViewHolder.username.setText(comments.getUsername());
                commentsViewHolder.lastname.setText(comments.getLastname());

                commentsViewHolder.comment.setText(comments.getComment());
                commentsViewHolder.date.setText(comments.getDate());
                commentsViewHolder.time.setText(comments.getTime());


                //added new code here for messaging
                commentsViewHolder.mView.setOnClickListener(new View.OnClickListener()

                {

                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        userName ,
                                        "Send Message"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i == 0)
                                {
                                    Intent profileIntent = new Intent(CommentsActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("visit_user_id", usersIDS);
                                    startActivity(profileIntent);
                                }
                                if (i == 1)
                                {
                                    Intent ChatIntent = new Intent(CommentsActivity.this, ChatActivity.class);
                                    ChatIntent.putExtra("visit_user_id", usersIDS);
                                    ChatIntent.putExtra("userName", userName);
                                    ChatIntent.putExtra("lastName", lastName);
                                    startActivity(ChatIntent);
                                }
                            }
                        });

                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                CommentsViewHolder viewHolder = new CommentsViewHolder(view);
                return viewHolder;
            }
        };

        CommentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }




    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {TextView comment, date, time, lastname, username;

        View mView;

        public CommentsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
            username = itemView.findViewById(R.id.comment_username);
            lastname = itemView.findViewById(R.id.comment_lastname);
            comment = itemView.findViewById(R.id.comment_text);
            date = itemView.findViewById(R.id.comment_date);
            time = itemView.findViewById(R.id.comment_time);


        }

        public void setUsername(String username)
        {
            TextView myUserName =  (TextView) mView.findViewById(R.id.comment_username);
            myUserName.setText("@"+username+"  ");
        }

        public void setComment(String comment)
        {
            TextView myComment =  (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);

        }

        public void setDate(String date)
        {
            TextView myDate =  (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText("  Date: "+date);
        }

        public void setTime(String time)
        {
            TextView myTime =  (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText("  Time: "+time);
        }
    }

    private void ValidateComment(String userName)
    {
        String commentText =  CommentInputText.getText().toString();
        if (TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please enter text in comment...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMMM-yyyy");
            final  String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForDate.getTime());


            final String RandomKey =  current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            //commentsMap.put("lastname",  lastName);

                PostsRef.child(RandomKey).updateChildren(commentsMap)
                        .addOnCompleteListener(new OnCompleteListener()
                        {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(CommentsActivity.this, "Comment saved successfully...", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(CommentsActivity.this, "Error occurred, try again!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        }
    }
}
