package edu.duke.ece651.riskclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    public interface onClickListener{
        void onClick(String mapName);
    }

    private List<WorldMap> mapList;
    private onClickListener listener;

    public MapAdapter(){
        mapList = new ArrayList<>();
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
        holder.tvMapName.setText(map.getName());
        holder.tvMapName.setOnClickListener(v -> {
            listener.onClick(map.getName());
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
        mapList.addAll(maps);
        notifyDataSetChanged();
    }

    static class MapViewHolder extends RecyclerView.ViewHolder{

        TextView tvMapName;

        MapViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMapName = itemView.findViewById(R.id.map_name);
        }
    }
}
