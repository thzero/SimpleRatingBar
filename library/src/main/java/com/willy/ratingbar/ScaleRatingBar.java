package com.willy.ratingbar;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by willy on 2017/5/5.
 */

public class ScaleRatingBar extends AnimationRatingBar {

    // Control animation speed
    private static final long ANIMATION_DELAY = 15;

    public ScaleRatingBar(Context context) {
        super(context);
    }

    public ScaleRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void emptyRatingBar() {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        if (mRunnable != null) {
            mHandler.removeCallbacksAndMessages(mRunnableToken);
        }

        long delay = 0;
        for (final PartialView view : mPartialViews) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEmpty();
                }
            }, delay += 5);
        }
    }

    @Override
    protected void fillRatingBar(final float rating) {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        if (mRunnable != null) {
            mHandler.removeCallbacksAndMessages(mRunnableToken);
        }

        for (final PartialView partialView : mPartialViews) {
            final int ratingViewId = (int) partialView.getTag();
            final double maxIntOfRating = Math.ceil(rating);

            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty();
                continue;
            }

            mRunnable = getAnimationRunnable(rating, partialView, ratingViewId, maxIntOfRating);

            long timeMillis = SystemClock.uptimeMillis() + ANIMATION_DELAY;
            mHandler.postAtTime(mRunnable, mRunnableToken, timeMillis);
        }
    }

    @NonNull
    private Runnable getAnimationRunnable(final float rating, final PartialView partialView, final int ratingViewId, final double maxIntOfRating) {
        return new Runnable() {
            @Override
            public void run() {
                if (ratingViewId == maxIntOfRating) {
                    partialView.setPartialFilled(rating);
                } else {
                    partialView.setFilled();
                }

                if (ratingViewId == rating) {
                    Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                    Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
                    partialView.startAnimation(scaleUp);
                    partialView.startAnimation(scaleDown);
                }
            }
        };
    }
}

