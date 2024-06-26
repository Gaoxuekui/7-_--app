package com.example.manojd.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manojd.myapplication.R;
import com.example.manojd.myapplication.activity.DetailsActivity;
import com.example.manojd.myapplication.common.ContactClickListner;
import com.example.manojd.myapplication.model.Contact;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapterRecyclerView extends RecyclerView.Adapter<ContactAdapterRecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Contact> contacts;
    private final ContactClickListner listner;




    public ContactAdapterRecyclerView(Context context, ArrayList<Contact> contacts, ContactClickListner listner) {
        this.context = context;
        this.contacts = contacts;
        this.listner = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_items,parent,false);
        ViewHolder holder = new ViewHolder(view,context,contacts);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);

        holder.fName.setText(contact.getName());
        holder.number.setText(contact.getMobile());
        if(contact.getImage() == null){
            holder.imageView.setImageResource(R.drawable.contacts_icon);
        }
        else{
            byte [] arr = Base64.decode(contact.getImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        //首字母相同合并
        if(position==0){
            holder.zm.setText(" "+contact.getBig());
        }
        else{
            Contact temp = contacts.get(position-1);
            if(!temp.getBig().equals(contact.getBig())) {
                holder.zm.setText(" " + contact.getBig());
            }
            else{
                holder.zm.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            }
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView fName,number,zm;
        ArrayList<Contact>contacts = new ArrayList<Contact>();
        Context context;
        public ViewHolder(View itemView,Context context,ArrayList<Contact> contacts) {
            super(itemView);
            this.contacts=contacts;
            this.context=context;
            itemView.setOnClickListener(this);



            zm=itemView.findViewById(R.id.id_a_z);
            imageView = itemView.findViewById(R.id.imageView);
            fName = itemView.findViewById(R.id.textName);
            number = itemView.findViewById(R.id.textNumber);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Contact contact = this.contacts.get(position);
            Intent intent= new Intent(this.context, DetailsActivity.class);
//            intent.putExtra("contact",contact.getId());
            intent.putExtra("contact",contact.getId());
            this.context.startActivity(intent);
        }
    }

}
