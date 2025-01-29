package com.app.easy_patient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easy_patient.R;
import com.app.easy_patient.model.DefaultImageModel;
import com.app.easy_patient.util.AdapterCallback;

import java.util.ArrayList;

public class MedicineImageAdapter extends RecyclerView.Adapter<MedicineImageAdapter.ViewHolder> {
    Context context;
    ArrayList<DefaultImageModel> imageList;
    private AdapterCallback mAdapterCallback;

    public MedicineImageAdapter(Context context, ArrayList<DefaultImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMed;
        LinearLayout llCircle;

        public ViewHolder(View itemView) {
            super(itemView);
            imgMed = itemView.findViewById(R.id.med_image);
            llCircle = itemView.findViewById(R.id.ll_circle_bg);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_image_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgMed.setImageResource(imageList.get(position).getImage());
        if (imageList.get(position).isSelected())
            holder.llCircle.setBackgroundResource(R.drawable.circuler_bg);
        else
            holder.llCircle.setBackgroundResource(R.drawable.circuler_bg_unselected);

        holder.llCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.onItemClickCallback(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

}
