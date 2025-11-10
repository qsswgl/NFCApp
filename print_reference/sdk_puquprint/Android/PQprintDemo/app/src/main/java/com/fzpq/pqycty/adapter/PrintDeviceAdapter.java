package com.fzpq.pqycty.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fzpq.pqycty.R;

import java.util.ArrayList;
import java.util.List;

public class PrintDeviceAdapter extends RecyclerView.Adapter<PrintDeviceAdapter.ViewHolder> {

    private Context context;
    private List<BluetoothDevice> datas;
    private LayoutInflater inflater;
    private onClickItemListener onClickItemListener;
    private onLongclickItemListener onLongclickItemListener;

    public void setOnClickItemListener(PrintDeviceAdapter.onClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public void setOnLongclickItemListener(onLongclickItemListener onLongclickItemListener) {
        this.onLongclickItemListener = onLongclickItemListener;
    }

    public PrintDeviceAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        datas = new ArrayList<>();
    }

    public void setDatas(List<BluetoothDevice> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bluetooth, parent,
                false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BluetoothDevice printConnect = datas.get(position);
        holder.tvName.setText(printConnect.getName());
        holder.tvMac.setText(printConnect.getAddress());
        if((datas.size()-1) == position){
            holder.vLine.setVisibility(View.GONE);
        }else {
            holder.vLine.setVisibility(View.VISIBLE);
        }
        holder.llNowDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickItemListener != null) {
                    onClickItemListener.onClick(position);
                }
            }
        });
        holder.llNowDevice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongclickItemListener != null) {
                    onLongclickItemListener.onClick(position);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface onClickItemListener {
        public void onClick(int position);
    }

    public interface onLongclickItemListener {
        public void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llNowDevice;
        private TextView tvName;
        private TextView tvMac;
        private View vLine;

        public ViewHolder(View view) {
            super(view);
            llNowDevice = (LinearLayout) view.findViewById(R.id.ll_nowdevice);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvMac = (TextView) view.findViewById(R.id.tv_mac);
            vLine = (View) view.findViewById(R.id.v_line);
        }
    }

}
