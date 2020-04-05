package edu.duke.ece651.riskclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onClickListener;

import static edu.duke.ece651.riskclient.RiskApplication.getContext;

public class TerritoryGroupAdapter extends RecyclerView.Adapter<TerritoryGroupAdapter.RoomViewHolder> {

    private List<Set<String>> groups;
    private onClickListener listener;
    private int selectedPosition;

    public TerritoryGroupAdapter(){
        groups = new ArrayList<>();
        selectedPosition = 0;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_territory_group, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Set<String> group = groups.get(position);

        holder.tvIndex.setText(String.valueOf(position + 1));

        holder.layoutT.removeAllViews();
        for (String t : group){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.listitem_territory, null);
            TextView tvTName = view.findViewById(R.id.territory_name);
            TextView tvInfo = view.findViewById(R.id.territory_info);
            tvTName.setText(t);
            tvInfo.setVisibility(View.GONE);
            holder.layoutT.addView(view);
        }

        if (position == selectedPosition){
            holder.background.setSelected(true);
        }else {
            holder.background.setSelected(false);
        }

        holder.background.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(position);
            }
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    public void setTerritories(List<Set<String>> territories){
        this.groups.clear();
        this.groups.addAll(territories);
        notifyDataSetChanged();
    }

    public Set<String> getGroup(int index) {
        return groups.get(index);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder{

        View background;
        TextView tvIndex;
        LinearLayout layoutT;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.background = itemView.findViewById(R.id.layout_background);
            this.tvIndex = itemView.findViewById(R.id.group_index);
            this.layoutT = itemView.findViewById(R.id.layout_territory);
        }
    }
}
