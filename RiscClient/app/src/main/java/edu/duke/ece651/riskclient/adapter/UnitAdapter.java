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
import java.util.Locale;

import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onClickListener;
import edu.duke.ece651.riskclient.objects.UnitGroup;

import static edu.duke.ece651.risk.shared.Constant.UNIT_NAME;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.RoomViewHolder> {

    private List<UnitGroup> units;
    private onClickListener listener;

    public UnitAdapter(){
        units = new ArrayList<>();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_unit, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        UnitGroup unit = units.get(position);

        holder.tvUnitInfo.setText(String.format(Locale.US, "%d %s(level %d)", unit.getNumber(), UNIT_NAME.get(unit.getLevel()), unit.getLevel()));
        // differentiate the unit of territory owner and allay
        if (unit.isAllay()){
            holder.tvUnitInfo.setTextColor(Color.RED);
        }else {
            holder.tvUnitInfo.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return units.size();
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    public void setUnits(List<UnitGroup> units){
        this.units.clear();
        this.units.addAll(units);
        notifyDataSetChanged();
    }

    public UnitGroup getUnitGroup(int index){
        return units.get(index);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView tvUnitInfo;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvUnitInfo = itemView.findViewById(R.id.unit_info);
        }
    }
}
