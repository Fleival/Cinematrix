package com.denspark.strelets.cinematrix.view;

import android.view.View;
import com.airbnb.lottie.LottieAnimationView;

public class LottieAnimationHelper {
    private boolean isInProgress;
    private LottieAnimationView mAnimationView;
    private View mViewHide;

    public LottieAnimationHelper(LottieAnimationView animationView, View viewHide) {
        this.mAnimationView = animationView;
        this.mViewHide = viewHide;
    }

    public void playInfiniteAnimation() {
        View view = this.mViewHide;
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
            this.isInProgress = true;
        }
        this.mAnimationView.setVisibility(View.VISIBLE);
        this.mAnimationView.playAnimation();
    }

    public void stopInfiniteAnimation() {
        this.mAnimationView.cancelAnimation();
        this.mAnimationView.setVisibility(View.GONE);
        View view = this.mViewHide;
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            this.isInProgress = false;
        }
    }

    public boolean isInProgress() {
        return this.isInProgress;
    }
}
