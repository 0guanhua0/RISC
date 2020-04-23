package edu.duke.ece651.riskclient.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onClickListener;

public class TerritoryAdapter extends RecyclerView.Adapter<TerritoryAdapter.RoomViewHolder> {

    private List<Territory> territories;
    private Map<Integer, String> idToName;
    private onClickListener listener;

    public TerritoryAdapter(){
        this.territories = new ArrayList<>();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_territory, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Territory territory = territories.get(position);
        StringBuilder builder = new StringBuilder();
        builder.append("Own by: ").append(idToName.get(territory.getOwner())).append("; ");
        if (territory.isRadiated()){
            builder.append("Produce 0 food and 0 tech");
        }else {
            builder.append("Produce ").append(territory.getFoodYield()).append(" food and ").append(territory.getTechYield()).append(" tech");
        }

        holder.tvTerritoryName.setText(territory.getName());
        holder.tvTerritoryInfo.setText(builder.toString());

        if (territory.isRadiated()){
            holder.tvTerritoryName.setTextColor(Color.RED);
            holder.tvTerritoryInfo.setTextColor(Color.RED);
        }else {
            holder.tvTerritoryName.setTextColor(Color.BLACK);
            holder.tvTerritoryInfo.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return territories.size();
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    public void setIdToName(Map<Integer, String> idToName) {
        this.idToName = idToName;
    }

    public void setTerritories(List<Territory> territories){
        // sort by owner id
        territories.sort((Territory t1, Territory t2) -> Integer.compare(t1.getOwner(), t2.getOwner()));
        this.territories.clear();
        this.territories.addAll(territories);
        notifyDataSetChanged();
    }

    public Territory getTerritory(int index){
        return territories.get(index);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView tvTerritoryName;
        TextView tvTerritoryInfo;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvTerritoryName = itemView.findViewById(R.id.territory_name);
            this.tvTerritoryInfo = itemView.findViewById(R.id.territory_info);
        }
    }
}
