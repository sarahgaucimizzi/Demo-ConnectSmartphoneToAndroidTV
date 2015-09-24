package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by smizzi on 24/09/2015.
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>{

    private Context mContext;
    private List<String> mDeviceList;

    public DevicesAdapter(Context mContext, List<String> mDeviceList) {
        this.mContext = mContext;
        this.mDeviceList = mDeviceList;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        // Get device name for position
        String name = mDeviceList.get(position);
        holder.nameTextView.setText(name != null ? name : "");
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public void refresh(List<String> updatedList){
        mDeviceList = updatedList;
        notifyDataSetChanged();
    }

    public void add(String deviceName){
        mDeviceList.add(deviceName);
        notifyItemInserted(mDeviceList.size() - 1);
    }

    public void remove(int position){
        mDeviceList.remove(position);
        notifyItemRemoved(position);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.device_item_name_text_view);
        }
    }
}
