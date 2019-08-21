package com.denspark.strelets.cinematrix.adapters.playlist_adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.denspark.strelets.cinematrix.not_for_production.playlist.Episode;
import com.denspark.strelets.cinematrix.not_for_production.playlist.File;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Playlist;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Season;

import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

public class PlaylistTreeViewBuilder {
    Context mContext;
    private TreeNode root;
    private TreeView treeView;
    private RecyclerView rootRecyclerView;

    public PlaylistTreeViewBuilder(Context context) {
        mContext = context;
    }


    public View buildTreeView(boolean isSerial) {
        root = TreeNode.root();
//        buildTree();

        treeView = new TreeView(root, mContext, new PlaylistNodeViewFactory(isSerial));
        rootRecyclerView = treeView.getRootRecyclerView();

        View view = rootRecyclerView;
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return view;
    }

    public void updateTreeViewData(Playlist moviePlaylist, boolean isSerial) {
        if (isSerial)
            buildTreeForSerialPlaylist(moviePlaylist);
        if (!isSerial)
            buildTreeForMoviePlaylist(moviePlaylist);
        treeView.refreshTreeView();
    }

    private void buildTreeForMoviePlaylist(Playlist moviePlaylist) {
//        int translationLevelCount = 0;
        for (String translation : moviePlaylist.getMessage().getTranslations().getVideoFileList().keySet()) {
            TreeNode translationNode = new TreeNode(translation);
            translationNode.setLevel(0);

            for (File videoSource : moviePlaylist.getMessage().getTranslations().getVideoFileList().get(translation)) {
                TreeNode seasonNode = new TreeNode(videoSource);
                seasonNode.setLevel(1);
                translationNode.addChild(seasonNode);
            }

            if (root.hasChild()) {
                for (TreeNode node : root.getChildren()) {
                    if (node.getValue().equals(translation)) {
                        root.removeChild(node);
                        translationNode = node;
                        System.out.println(translationNode);
                    }
                }
            }
            root.addChild(translationNode);
        }
    }

    private void buildTreeForSerialPlaylist(Playlist moviePlaylist) {
        for (String translation : moviePlaylist.getMessage().getTranslations().getSeasons().keySet()) {
            TreeNode translationNode = new TreeNode(translation);
            translationNode.setLevel(0);

            for (Season season : moviePlaylist.getMessage().getTranslations().getSeasons().get(translation)) {
                TreeNode seasonNode = new TreeNode(season.getTitle());
                seasonNode.setLevel(1);
                for (Episode episode : season.getFolder()) {
                    TreeNode episodeNode = new TreeNode(episode.getTitle());
                    episodeNode.setLevel(2);
                    for (File videoSource :episode.getFiles()) {
                        TreeNode videoSourceNode = new TreeNode(videoSource);
                        videoSourceNode.setLevel(3);
                        episodeNode.addChild(videoSourceNode);
                    }
                    seasonNode.addChild(episodeNode);
                }
                translationNode.addChild(seasonNode);
            }

            if (root.hasChild()) {
                for (TreeNode node : root.getChildren()) {
                    if (node.getValue().equals(translation)) {
                        root.removeChild(node);
                        translationNode = node;
                        System.out.println(translationNode);
                    }
                }
            }
            root.addChild(translationNode);
        }
    }
}
