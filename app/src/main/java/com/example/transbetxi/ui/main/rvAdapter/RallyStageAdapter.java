package com.example.transbetxi.ui.main.rvAdapter;

import android.content.ClipData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transbetxi.R;
import com.example.transbetxi.data.RallyStage;
import com.example.transbetxi.ui.main.view.MainFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class RallyStageAdapter extends RecyclerView.Adapter<RallyStageAdapter.ViewHolder> {
    private static List<RallyStage> rallyStages;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stageName;
        public TextView stageDistance;
        public TextView stageStartTime;
        public TextView stageId;

        public ViewHolder(View itemView) {
            super(itemView);
            stageName = itemView.findViewById(R.id.tvStageName);
            stageId = itemView.findViewById(R.id.tvStageId);
            stageStartTime = itemView.findViewById(R.id.tvStartTime);
            stageDistance = itemView.findViewById(R.id.tvStageDistance);
        }


    }

    public RallyStageAdapter(List<RallyStage> rallyStages) {
        this.rallyStages = rallyStages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stage_item, parent, false);
        v.setVisibility(View.VISIBLE);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RallyStage rallyStage = rallyStages.get(position);
        holder.stageName.setText(rallyStage.getName());
        holder.stageDistance.setText(rallyStage.getLength());
        holder.stageStartTime.setText(rallyStage.getStartTime());
        holder.stageId.setText(rallyStage.getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAbsoluteAdapterPosition();
                listener.onItemClick(currentPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return rallyStages.size();
    }
}

