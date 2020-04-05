package edu.duke.ece651.riskclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onClickListener;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<RoomInfo> rooms;
    private onClickListener listener;

    public RoomAdapter(){
        rooms = new ArrayList<>();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomInfo room = rooms.get(position);

        holder.tvRoomName.setText(room.getRoomName());
        holder.tvRoomInfo.setText("nothing for now");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public RoomInfo getRoom(int index){
        return rooms.get(index);
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    public void setRooms(List<RoomInfo> rooms){
        this.rooms.clear();
        this.rooms.addAll(rooms);
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView tvRoomName;
        TextView tvRoomInfo;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvRoomName = itemView.findViewById(R.id.room_name);
            this.tvRoomInfo = itemView.findViewById(R.id.room_info);
        }
    }
}
