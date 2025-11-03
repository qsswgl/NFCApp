package com.nfc.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nfc.app.database.NFCRecord
import com.nfc.app.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter : ListAdapter<NFCRecord, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: NFCRecord) {
            binding.apply {
                tvNfcid.text = "NFCID: ${record.nfcId}"
                tvCardNumber.text = "卡号: ${record.cardNumber}"
                tvCarNumber.text = "车号: ${record.carNumber}"
                tvTime.text = "时间: ${formatTime(record.readTime)}"
                tvContent.text = "内容: ${record.content}"
            }
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    private class RecordDiffCallback : DiffUtil.ItemCallback<NFCRecord>() {
        override fun areItemsTheSame(oldItem: NFCRecord, newItem: NFCRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NFCRecord, newItem: NFCRecord): Boolean {
            return oldItem == newItem
        }
    }
}
