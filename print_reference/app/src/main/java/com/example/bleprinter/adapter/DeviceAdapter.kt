package com.example.bleprinter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bleprinter.databinding.ItemDeviceBinding
import com.example.bleprinter.model.BleDevice

class DeviceAdapter(
    private val onDeviceClick: (BleDevice) -> Unit
) : ListAdapter<BleDevice, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding, onDeviceClick)
    }
    
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class DeviceViewHolder(
        private val binding: ItemDeviceBinding,
        private val onDeviceClick: (BleDevice) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(device: BleDevice) {
            binding.textViewDeviceName.text = device.name
            binding.textViewDeviceAddress.text = device.address
            binding.textViewDeviceRssi.text = "信号: ${device.rssi} dBm"
            
            binding.root.setOnClickListener {
                onDeviceClick(device)
            }
        }
    }
    
    class DeviceDiffCallback : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem.address == newItem.address
        }
        
        override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem == newItem
        }
    }
}
