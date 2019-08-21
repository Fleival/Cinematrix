package com.denspark.strelets.cinematrix.adapters.playlist_adapter;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.view.activities.PlayerActivity;
import com.denspark.strelets.cinematrix.not_for_production.playlist.File;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

public class FileEpisodeNodeViewBinder extends BaseNodeViewBinder {
    String referer;
    TextView qualityTextView;
    TextView fileTextView;
    Button playFileButton;

    public FileEpisodeNodeViewBinder(View itemView) {
        super(itemView);
        qualityTextView = itemView.findViewById(R.id.item_quality);
        fileTextView = itemView.findViewById(R.id.item_file);
        playFileButton = itemView.findViewById(R.id.play_file_btn);

    }

    @Override
    public int getLayoutId() {
        return R.layout.file_quality_playlist_item;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        File videoSource = (File)treeNode.getValue();
        qualityTextView.setText(videoSource.getQuality());
        fileTextView.setText(videoSource.getFileUrl());
        playFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerActivity.class);
//                intent.putExtra(PlayerActivity.EXTRA_REFERER, referer);
                intent.putExtra(PlayerActivity.EXTRA_VIDEO_URL, videoSource.getFileUrl());
                v.getContext().startActivity(intent);
            }
        });
    }
}

