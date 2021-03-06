package com.example.kseniyaslobodyan.shagift.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.kseniyaslobodyan.shagift.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.example.kseniyaslobodyan.shagift.R;
import com.example.kseniyaslobodyan.shagift.model.PostEvent;


public class FaivoritesActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MyTag";
    private static final int REQUEST_IMAGE_CAPTURE = 333;
    private static final int REQUEST_WRITE_STORAGE = 444;
    private static final int GET_FROM_GALLERY = 555;
    PostAdapter adapter;
    RecyclerView lstMessages;
    ImageView imgCapture;
    Bitmap imageBitmap;
    static String userName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faivorites);

        adapter = new PostAdapter(ModelPost.instance.getAllFavoritesPosts());

        lstMessages = (RecyclerView) findViewById(R.id.lstMessages);
        lstMessages.setLayoutManager(new LinearLayoutManager(this));
        lstMessages.setAdapter(adapter);
    }


    class PostHolder extends RecyclerView.ViewHolder{

        public TextView tvAuthor;
        public TextView tvName;
        public TextView tvDesc;
        public ImageView imgPost;
        public VideoView vidPlayer;

        public PostHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
            vidPlayer = (VideoView) itemView.findViewById(R.id.vidPlayer);
        }
    }


    class PostAdapter extends FirebaseRecyclerAdapter<Post,PostHolder> {

        private FirebaseAuth auth;

        public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options) {
            super(options);
        }

        protected void onBindViewHolder(PostHolder holder, int position, @NonNull Post post) {
            holder.tvAuthor.setText(post.getAuthorName());
            holder.tvName.setText(post.getNamePost());
            holder.tvDesc.setText(post.getPostDescription());

            if (!post.getImage().isEmpty()) {
                Glide.with(FaivoritesActivity.this)
                        .load(post.getImage())
                        .into(holder.imgPost);
            }
        }

        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favoriteposts_layout, parent, false);
            return new PostHolder(v);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PostEvent event) {
        Toast.makeText(this, event.post, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        EventBus.getDefault().register(this);

        /***ADD To ALL**/
        LinearLayout bottom_menu = (LinearLayout)findViewById(R.id.bottom_menu);

        ImageButton btnmenuHome = (ImageButton) findViewById(R.id.bottom_menu_home);
        btnmenuHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaivoritesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //add post gift button in the bottom menu
        ImageButton btnaddpostgift = (ImageButton) bottom_menu.findViewById(R.id.bottom_menu_addgift);
        btnaddpostgift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the new post fragment
                CustomDialog customDialog = new CustomDialog();
                customDialog.show(getSupportFragmentManager(), "");
            }
        });

        //userprofile button in the bottom menu
        ImageButton btnuserprofile = (ImageButton) bottom_menu.findViewById(R.id.bottom_menu_userprofile);
        btnuserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaivoritesActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();


            }
        });

        //My post button in the bottom menu
        ImageButton btnmyposts = (ImageButton) bottom_menu.findViewById(R.id.bottom_menu_myposts);
        btnmyposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaivoritesActivity.this, MyPostActivity.class);
                startActivity(intent);
                finish();


            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK)
            adapter.notifyDataSetChanged();

    }


}
