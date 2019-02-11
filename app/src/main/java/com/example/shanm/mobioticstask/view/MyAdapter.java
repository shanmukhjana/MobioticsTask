package com.example.shanm.mobioticstask.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shanm.mobioticstask.R;
import com.example.shanm.mobioticstask.modal.Modal;
import com.example.shanm.mobioticstask.mydatabase.MyDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    Context context;
    ArrayList<Modal> arrayList;

    public MyAdapter(Context context, ArrayList<Modal> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_style,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Modal modal=arrayList.get(position);
        holder.title.setText(""+modal.getTitle());
        holder.description.setText(""+modal.getDescription());
        Glide.with(context).load(modal.getImage()).into(holder.imageView);
        final String videoUrl=modal.getVideoUrl();

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,VideoActivity.class);
                intent.putExtra("videoUrl",""+videoUrl);
                intent.putExtra("videoTitle",""+modal.getTitle());
                intent.putExtra("videoDesc",""+modal.getDescription());
                intent.putExtra("videoId",""+modal.getId());
                intent.putExtra("videoThumnail",""+modal.getImage());

                MyDatabase myDatabase=new MyDatabase(context);
                SQLiteDatabase sqLiteDatabase = myDatabase.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(MyDatabase.COL1, ""+modal.getId());
                cv.put(MyDatabase.SEEkTime, "00");
                sqLiteDatabase.insert(MyDatabase.TABLENAME, null, cv);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,description;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            title=itemView.findViewById(R.id.titleTv);
            description=itemView.findViewById(R.id.descriptionTv);
        }
    }

}
