package com.tyron.layouteditor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.models.Widget;

import java.util.ArrayList;

public class WidgetsAdapter extends RecyclerView.Adapter<WidgetsAdapter.ViewHolder> {

    private final ArrayList<Widget> data;
    private final BottomSheetBehavior<View> behavior;

    public WidgetsAdapter(ArrayList<Widget> data, BottomSheetBehavior<View> behavior) {
        this.data = data;
        this.behavior = behavior;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Widget widget = data.get(position);
        holder.name.setText(widget.getName());

        holder.itemView.setOnLongClickListener((view) -> {
            ViewCompat.startDragAndDrop(view, null, new View.DragShadowBuilder(view), widget, 0);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.layout_name);
        }
    }
}
