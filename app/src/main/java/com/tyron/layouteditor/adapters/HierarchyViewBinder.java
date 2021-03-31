package com.tyron.layouteditor.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.models.HierarchyView;
import com.tyron.layouteditor.toolbox.tree.TreeNode;
import com.tyron.layouteditor.toolbox.tree.TreeViewBinder;

public class HierarchyViewBinder extends TreeViewBinder<HierarchyViewBinder.ViewHolder> {

    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener{
        void onItemLongClick(ViewHolder holder, int position, TreeNode<?> node);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode<?> n) {
        TreeNode<HierarchyView> node = (TreeNode<HierarchyView>) n;

        View view = node.getContent().widget.getAsView();

        String id = node.getContent().widget.getViewManager().getContext()
                .getInflater()
                .getIdGenerator()
                .getString(view.getId());

        holder.view_id.setText(id);

        holder.itemView.setOnLongClickListener((v) -> {
            if(listener != null){
                listener.onItemLongClick(holder, position, n);
            }
            return true;
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.hierarchy_view;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder{

        public TextView view_id;
        public ImageView view_icon;

        public ViewHolder(View rootView) {
            super(rootView);

            view_id = findViewById(R.id.view_id);
            view_icon = findViewById(R.id.view_icon);
        }
    }
}
