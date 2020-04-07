package edu.duke.ece651.riskclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onClickListener;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    private List<WorldMap> mapList;
    private onClickListener listener;
    private int selectedPosition;

    public MapAdapter(){
        mapList = new ArrayList<>();
        selectedPosition = 0;
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_map, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, int position) {
        WorldMap map = mapList.get(position);

        holder.tvMapName.setText(String.format(Locale.US,"%s (support %d players)", map.getName(), map.getColorList().size()));

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
        return mapList.size();
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    public void setMaps(List<WorldMap> maps){
        mapList.clear();
        Collections.sort(maps, (WorldMap map1, WorldMap map2)-> Integer.compare(map1.getColorList().size(), map2.getColorList().size()));
        mapList.addAll(maps);
        notifyDataSetChanged();
    }

    public WorldMap<?> getMap(int index){
        return mapList.get(index);
    }

    static class MapViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout background;
        TextView tvMapName;

        MapViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.layout_background);
            tvMapName = itemView.findViewById(R.id.map_name);
        }
    }
}
