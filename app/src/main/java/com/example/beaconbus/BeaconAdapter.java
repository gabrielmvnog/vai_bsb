package com.example.beaconbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.altbeacon.beacon.Beacon;
import org.w3c.dom.Text;

import java.util.List;


public class BeaconAdapter extends  RecyclerView.Adapter<BeaconAdapter.MyViewHolder> {
    private Context context;
    private List<Beacon> Beacon_list;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView beaconId;
        TextView distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            beaconId = (TextView) itemView.findViewById(R.id.beacon_id);
            distance = (TextView) itemView.findViewById(R.id.beacon_distance);
        }
    }

    public BeaconAdapter(Context context, List<Beacon> Beacon_list) {
        this.context = context;
        this.Beacon_list = Beacon_list;

    }

    public void setBeacons(List<Beacon> Beacon_list) {
        this.Beacon_list = Beacon_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_card, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.distance.setText(String.format( "%.2f",Beacon_list.get(position).getDistance()));
        holder.beaconId.setText("" + Beacon_list.get(position).getId1());

    }

    @Override
    public int getItemCount() {
        return Beacon_list.size();
    }
}
