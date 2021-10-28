package com.moksh.securemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moksh.securemessenger.Models.ChatHistory;
import com.moksh.securemessenger.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{
    Context context;
    List<ChatHistory> chatHistoryList;
    int senderMessage = 1,receiverMessage = 2;
    FirebaseAuth auth;
    public MessageAdapter(Context context, List<ChatHistory> chatHistoryList) {
        this.context = context;
        this.chatHistoryList = chatHistoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == senderMessage)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.senders_layout,parent,false);
            return  new SenderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        auth = FirebaseAuth.getInstance();
        String uid = auth.getUid();
        if (uid.equals(chatHistoryList.get(position).getUid()))
        {
            return senderMessage;
        }
        else
        {
            return receiverMessage;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatHistory chatHistory = chatHistoryList.get(position);
        if (holder.getClass() == SenderViewHolder.class)
        {
            ((SenderViewHolder)holder).message.setText(chatHistory.getMessage());
        }
        else
        {
            ((ReceiverViewHolder)holder).message.setText(chatHistory.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatHistoryList.size();
    }

   public class SenderViewHolder extends RecyclerView.ViewHolder
   {
        ImageView userImage;
        TextView message;
       public SenderViewHolder(@NonNull View itemView) {
           super(itemView);
           userImage = itemView.findViewById(R.id.userImage);
           message = itemView.findViewById(R.id.message);
       }
   }

  public class ReceiverViewHolder extends RecyclerView.ViewHolder
  {
      ImageView userImage;
      TextView message;
      public ReceiverViewHolder(@NonNull View itemView) {
          super(itemView);
          userImage = itemView.findViewById(R.id.userImage);
          message = itemView.findViewById(R.id.message);
      }
  }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.heading_layout,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        if (lastUser.equals(chatHistoryList.get(position).getUsername()) && times<5)
//        {
//            holder.profilePic.setVisibility(View.GONE);
//            lastUser = chatHistoryList.get(position).getUsername();
//            holder.userName.setVisibility(View.GONE);
//            holder.message.setText(chatHistoryList.get(position).getMessage());
//            ++times;
//        }
//        else
//        {
//            holder.userName.setText(chatHistoryList.get(position).getUsername());
//            holder.message.setText(chatHistoryList.get(position).getMessage());
//            times = 0;
//            lastUser = chatHistoryList.get(position).getUsername();
//        }
//        holder.userName.setText(chatHistoryList.get(position).getUsername());
//        holder.message.setText(chatHistoryList.get(position).getMessage());
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatHistoryList.size();
//    }
//
//
//    class ViewHolder extends RecyclerView.ViewHolder{
//
//        TextView userName,message;
//        ImageView profilePic;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            userName = itemView.findViewById(R.id.user_name);
//            message = itemView.findViewById(R.id.message);
//            profilePic = itemView.findViewById(R.id.userImage);
//        }
//    }
}
