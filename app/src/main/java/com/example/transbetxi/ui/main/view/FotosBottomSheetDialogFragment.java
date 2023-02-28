package com.example.transbetxi.ui.main.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.transbetxi.R;
import com.example.transbetxi.data.Photo;
import com.example.transbetxi.ui.main.rvAdapter.PhotoAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FotosBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private ArrayList<Photo> photos;

    public FotosBottomSheetDialogFragment(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_photo, container, false);
                RecyclerView recyclerView = view.findViewById(R.id.photo_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new PhotoAdapter(photos));
        return view;
    }

    private class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

        private ArrayList<Photo> photos;

        public PhotosAdapter(ArrayList<Photo> photos) {
            this.photos = photos;
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Photo photo = photos.get(position);
            Glide.with(holder.itemView.getContext())
                    .load(photo.getPath())
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;

            public PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.photo_thumbnail);
            }
        }
    }
}