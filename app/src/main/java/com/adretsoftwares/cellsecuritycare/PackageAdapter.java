package com.adretsoftwares.cellsecuritycare;

import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private List<PackageInfo> packageList;
    private OnPackageSelectedListener onPackageSelectedListener;

    public PackageAdapter(List<PackageInfo> packageList, OnPackageSelectedListener listener) {
        this.packageList = packageList;
        this.onPackageSelectedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.package_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= 0 && position < packageList.size()) {
            PackageInfo item = packageList.get(position);
            holder.tvPackageName.setText(item.packageName);
        }
    }

    public interface OnPackageSelectedListener {
        void onPackageSelected(PackageInfo packageInfo);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName;

        ViewHolder(View v) {
            super(v);
            tvPackageName = v.findViewById(R.id.tvPackageName);

            v.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position >= 0 && position < packageList.size()) {
                    onPackageSelectedListener.onPackageSelected(packageList.get(position));
                }
            });
        }
    }
}
