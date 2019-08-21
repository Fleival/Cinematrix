package com.denspark.strelets.cinematrix.adapters.playlist_adapter;

import android.view.View;

import me.texy.treeview.base.BaseNodeViewBinder;
import me.texy.treeview.base.BaseNodeViewFactory;

public class PlaylistNodeViewFactory extends BaseNodeViewFactory {
    private boolean isSerialMode;

    public PlaylistNodeViewFactory(boolean isSerialMode) {
        this.isSerialMode = isSerialMode;
    }

    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
        switch (level) {
            case 0:
                return new TranslationNodeViewBinder(view);
            case 1:
                if (isSerialMode) {
                    return new SeasonNodeViewBinder(view);
                }else {
                    return new FileEpisodeNodeViewBinder(view);
                }
            case 2:
                return new EpisodeNodeViewBinder(view);
            case 3:
                return new FileEpisodeNodeViewBinder(view);
            default:
                return null;
        }
    }
}