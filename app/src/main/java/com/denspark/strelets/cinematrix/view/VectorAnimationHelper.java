package com.denspark.strelets.cinematrix.view;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.ImageView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class VectorAnimationHelper {
    ImageView animationContainer;
    private boolean isInProgress;
    AnimationCallback mAnimationCallback = new AnimationCallback() {
        public void onAnimationStart(Drawable drawable) {
            super.onAnimationStart(drawable);
        }

        public void onAnimationEnd(Drawable drawable) {
            ((Animatable) drawable).start();
        }
    };
    Drawable mDrawable = this.animationContainer.getDrawable();
    View mViewHide;

    public VectorAnimationHelper(View viewHide, ImageView animationContainer2) {
        this.mViewHide = viewHide;
        this.animationContainer = animationContainer2;
    }

    public void showWorkingInProgressInstead() {
        this.mViewHide.setVisibility(View.INVISIBLE);
        this.animationContainer.setVisibility(View.VISIBLE);
        Drawable drawable = this.mDrawable;
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
            if (VERSION.SDK_INT >= 23) {
                AnimatedVectorDrawableCompat.registerAnimationCallback(this.mDrawable, this.mAnimationCallback);
            }
            this.isInProgress = true;
        }
    }

    public void showWorkingInProgressOn() {
        this.animationContainer.setVisibility(View.VISIBLE);
        Drawable drawable = this.mDrawable;
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
            if (VERSION.SDK_INT >= 23) {
                AnimatedVectorDrawableCompat.registerAnimationCallback(this.mDrawable, this.mAnimationCallback);
            }
        }
    }

    public void hideWorkingInProgressInstead() {
        stopVectorAnimation();
        this.animationContainer.setVisibility(View.GONE);
        this.mViewHide.setVisibility(View.VISIBLE);
        this.isInProgress = false;
    }

    public void hideWorkingInProgressOn() {
        stopVectorAnimation();
        this.animationContainer.setVisibility(View.GONE);
    }

    private void stopVectorAnimation() {
        AnimatedVectorDrawableCompat.unregisterAnimationCallback(this.mDrawable, this.mAnimationCallback);
        Drawable drawable = this.mDrawable;
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }
    }

    public boolean isInProgress() {
        return this.isInProgress;
    }
}
