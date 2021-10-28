package com.moksh.securemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moksh.securemessenger.Models.ChatHistory;
import com.moksh.securemessenger.Models.UserModels;
import com.moksh.securemessenger.R;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    Context context;
    List<UserModels> userModels;
    List<LinearLayout> clickedUserList = new ArrayList<>();
    DrawerLayout drawerLayout;
    View toolbar;
    EditText message;
    CardView send;
    RecyclerView chatRecycler;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference,adapterReference;
    String databaseNameString;
    ChatHistory chatHistory1;
    String localUser = "";
    int atPosition = -1;

    public UsersAdapter(Context context, List<UserModels> userModels, DrawerLayout drawerLayout, View toolbar, CardView send, EditText message, RecyclerView chatRecycler, FirebaseAuth auth) {
        this.context = context;
        this.userModels = userModels;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
        this.message = message;
        this.send = send;
        this.chatRecycler = chatRecycler;
        this.auth = auth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.users_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!clickedUserList.contains(holder.clicked)) clickedUserList.add(holder.clicked);
        holder.userName.setText("@"+userModels.get(position).getUsername());
        holder.name.setText(userModels.get(position).getFirstName()+" "+userModels.get(position).getLastName());

            holder.userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message.setHint("Message @" + userModels.get(holder.getAdapterPosition()).getUsername());
                    message.setText("");
                    for (LinearLayout notClicked : clickedUserList) {
                        notClicked.setVisibility(View.INVISIBLE);
                    }
                    TextView toolbarName = toolbar.findViewById(R.id.toolbar_name);
                    toolbarName.setText(userModels.get(holder.getAdapterPosition()).getUsername());
                    holder.clicked.setVisibility(View.VISIBLE);
                    CloseDrawer();
                    if (atPosition != holder.getAdapterPosition()) {
                        onClickView(userModels.get(holder.getAdapterPosition()).getUserUid());
                        atPosition = holder.getAdapterPosition();
                    }
                }
            });
        if (position == 0) {
            onClickView(userModels.get(holder.getAdapterPosition()).getUserUid());
            message.setHint("Message @"+userModels.get(holder.getAdapterPosition()).getUsername());
            message.setText("");
            holder.clicked.setVisibility(View.VISIBLE);
            TextView toolbarName = toolbar.findViewById(R.id.toolbar_name);
            toolbarName.setText(userModels.get(holder.getAdapterPosition()).getUsername());
        }
    }

    @Override
    public int getItemCount()
    {
        return userModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userView, clicked;
        TextView name, userName;

        //        ImageView userImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userView = itemView.findViewById(R.id.user_view);
            clicked = itemView.findViewById(R.id.clicked);
            name = itemView.findViewById(R.id.name);
            userName = itemView.findViewById(R.id.user_name);
//            userImage = itemView.findViewById(R.id.user_image);
        }
    }

    public void CloseDrawer(){
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
    public void onClickView(String Receiver)
    {
        String Sender = auth.getUid();
        database = FirebaseDatabase.getInstance();
        if (localUser.equals("")) {
            localUser();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(linearLayoutManager);
        databaseNameFinder(Sender,Receiver);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals(""))
                {
                    reference = database.getReference().child("chats").child(databaseNameString).push();
                    ChatHistory chatHistory = new ChatHistory(message.getText().toString(),Sender, localUser);
                    reference.setValue(chatHistory).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
                message.setText("");

            }

        });

    }
    public void databaseNameFinder(String sender, String receiver) {

        reference = database.getReference().child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String databaseName = snapshot1.getKey();
                    int Index = databaseName.indexOf("&");
                    String frontname = databaseName.substring(0, Index);
                    String lastname = databaseName.substring(Index + 1);
                    if (frontname.equals(sender) && lastname.equals(receiver)) {
                        databaseNameString = databaseName;
                        count++;
                        break;
                    } else if (frontname.equals(receiver) && lastname.equals(sender)) {
                        databaseNameString = databaseName;
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    databaseNameString = sender+"&"+receiver;
                }
                List<ChatHistory> chatHistoryList = new ArrayList<>();
                MessageAdapter messageAdapter  = new MessageAdapter(context,chatHistoryList);
                adapterReference = database.getReference().child("chats").child(databaseNameString);
                adapterReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            chatHistory1 = snapshot1.getValue(ChatHistory.class);
                            if(!chatHistoryList.contains(chatHistory1))
                                chatHistoryList.add(chatHistory1);
                        }
                        messageAdapter.notifyDataSetChanged();
                        chatRecycler.setAdapter(messageAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void localUser(){
        String uid = auth.getUid();
        reference = database.getReference().child("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if (dataSnapshot.getKey().equals("username"))
                    {
                        localUser = dataSnapshot.getValue().toString();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
