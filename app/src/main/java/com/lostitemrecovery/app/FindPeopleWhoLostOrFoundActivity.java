package com.lostitemrecovery.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindPeopleWhoLostOrFoundActivity extends AppCompatActivity
{   private Toolbar mToolbar;

    private Button SearchButton;
    //private EditText SearchInputText;

    private RecyclerView SearchResultList;
    private DatabaseReference allUsersDatabaseRef;

    private FirebaseAuth mAuth;

    private String online_user_id;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people_who_lost_or_found);


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.find_people_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All in One");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));


        SearchButton = (Button) findViewById(R.id.search_people_button);
       // SearchInputText = (EditText) findViewById(R.id.search_box_input);



        SearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //String searchBoxInput = SearchInputText.getText().toString();
                SearchPeopleWhoFoundOrLost();

            }
        });

    }

//Online Offline State
   /* public void updateUserStatus(String state)
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

        allUsersDatabaseRef.child(online_user_id).child("userState")
                .updateChildren(currentStateMap);

    }*/
    //Online Offline State
/*

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }


    @Override
    protected void onStop() {
        super.onStop();
        updateUserStatus("offline");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");

    }
*/
    //Online Offline State

    private void SearchPeopleWhoFoundOrLost()
    {  FirebaseRecyclerOptions<FindPeople> options=new FirebaseRecyclerOptions.Builder<FindPeople>().
            setQuery(allUsersDatabaseRef, FindPeople.class).build();
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG).show();
        //Query searchPeopleQuery = allUsersDatabaseRef.orderByChild("fullname")
                //.startAt(searchBoxInput.endsWith(searchBoxInput +"\uf8ff"));
        FirebaseRecyclerAdapter<FindPeople, FindFriendViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindPeople, FindFriendViewHolder> (options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder findPeopleViewHolder, final int i,
                                            @NonNull final FindPeople findPeople)
            {
                final String PostKey = getRef(i).getKey();
                //added new code

                final String userName = getRef(i).getKey();
                final String lastName = getRef(i).getKey();
                final String usersIDS = getRef(i).getKey();

                //new code 7/8/2019
                //new code 7/8/2019

               /* allUsersDatabaseRef.child(usersIDS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String type;

                            if (dataSnapshot.hasChild("userState"))
                            {
                                type = dataSnapshot.child("userState").child("type").getValue().toString();

                                if (type.equals("online"))
                                {
                                    findPeopleViewHolder.onlineStatusView.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    findPeopleViewHolder.onlineStatusView.setVisibility(View.INVISIBLE);

                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
*/
                findPeopleViewHolder.username.setText(findPeople.getFullname());
                findPeopleViewHolder.lastname.setText(findPeople.getLastname());
                findPeopleViewHolder.status.setText(findPeople.getStatus());
                Picasso.get().load(findPeople.getProfileimage()).into(findPeopleViewHolder.profileimage);


                findPeopleViewHolder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String visit_user_id = getRef(i).getKey();
                        Intent profileIntent = new Intent(FindPeopleWhoLostOrFoundActivity.this, PersonalProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);


                    }
                });

                findPeopleViewHolder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent findOthersIntent = new Intent(FindPeopleWhoLostOrFoundActivity.this, FindPeopleWhoLostOrFoundActivity.class);
                        findOthersIntent.putExtra("PostKey", PostKey);
                        startActivity(findOthersIntent);

                    }
                });


                //added new code here for messaging
                findPeopleViewHolder.mView.setOnClickListener(new View.OnClickListener()

                {

                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                      userName,
                                        "Send Message"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPeopleWhoLostOrFoundActivity.this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i == 0)
                                {
                                Intent profileIntent = new Intent(FindPeopleWhoLostOrFoundActivity.this, PersonalProfileActivity.class);
                                    profileIntent.putExtra("visit_user_id", usersIDS);
                                    startActivity(profileIntent);
                                }
                                if (i == 1)
                                {
                                    Intent ChatIntent = new Intent(FindPeopleWhoLostOrFoundActivity.this, ChatActivity.class);
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
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_users_display_layout,viewGroup,false);
                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        SearchResultList.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.startListening();
    }

    public class FindFriendViewHolder extends RecyclerView.ViewHolder
    {   View mView;
        //ImageView onlineStatusView;
        TextView username, lastname, status;
        CircleImageView profileimage;

        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
            username = itemView.findViewById(R.id.all_users_profile_name);
          lastname = itemView.findViewById(R.id.all_users_profile_last_name);
            status = itemView.findViewById(R.id.all_users_profile_status);
            profileimage = itemView.findViewById(R.id.all_users_profile_image);
           // onlineStatusView = itemView.findViewById(R.id.all_user_online_icon);

        }
    }}