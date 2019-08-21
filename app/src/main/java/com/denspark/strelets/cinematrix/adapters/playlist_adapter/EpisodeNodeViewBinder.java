package com.denspark.strelets.cinematrix.adapters.playlist_adapter;

import android.view.View;
import android.widget.TextView;

import com.denspark.strelets.cinematrix.R;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

public class EpisodeNodeViewBinder extends BaseNodeViewBinder {
    TextView textView;

    public EpisodeNodeViewBinder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.item_episode);

    }

    @Override
    public int getLayoutId() {
        return R.layout.episode_playlist_item;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        textView.setText(treeNode.getValue().toString());
    }
}