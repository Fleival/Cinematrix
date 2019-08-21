package com.denspark.strelets.cinematrix.adapters.playlist_adapter;

import android.view.View;
import android.widget.TextView;

import com.denspark.strelets.cinematrix.R;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

public class TranslationNodeViewBinder extends BaseNodeViewBinder {
    TextView textView;

    public TranslationNodeViewBinder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.item_translation);

    }

    @Override
    public int getLayoutId() {
        return R.layout.translation_playlist_item;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        textView.setText(treeNode.getValue().toString());
    }
}
