package com.adretsoftwares.cellsecuritycare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adretsoftwares.cellsecuritycare.databinding.AppsItemBinding
import com.adretsoftwares.cellsecuritycare.model.AppDetails

class AppsDetailsAdapter :
    ListAdapter<AppDetails, AppsDetailsAdapter.AppsDetailsViewHolder>(AppsDiffUtil()) {

    class AppsDetailsViewHolder(private val binding: AppsItemBinding) : ViewHolder(binding.root) {

        fun bindViews(currentApp: AppDetails) {
            binding.apply {
                imgAppIcon.setImageDrawable(currentApp.icon)
                txtAppName.text = currentApp.label
                txtPackageName.text = currentApp.packageName
                chckHideOrUnhide.isChecked = currentApp.isHidden
            }
        }

    }

    class AppsDiffUtil : DiffUtil.ItemCallback<AppDetails>() {
        override fun areItemsTheSame(oldItem: AppDetails, newItem: AppDetails) =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: AppDetails, newItem: AppDetails) =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsDetailsViewHolder {
        val binding = AppsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppsDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppsDetailsViewHolder, position: Int) {
        holder.bindViews(getItem(position))
    }
}