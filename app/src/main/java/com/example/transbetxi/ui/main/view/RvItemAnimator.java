package com.example.transbetxi.ui.main.view;

import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

class RvItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        Log.i("entra", "si");
        if (oldHolder == null || newHolder == null) {
            // cannot animate
            return false;
        }
        final ViewPropertyAnimatorCompat oldViewAnim = ViewCompat.animate(oldHolder.itemView);
        oldViewAnim.setDuration(200)
                .translationX(-oldHolder.itemView.getWidth())
                .alpha(0)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        dispatchChangeStarting(oldHolder, true);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        oldViewAnim.setListener(null);
                        ViewCompat.setTranslationX(view, -view.getWidth());
                        ViewCompat.setAlpha(view, 0);
                        dispatchChangeFinished(oldHolder, true);
                        animateChange(newHolder);
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        oldViewAnim.setListener(null);
                        ViewCompat.setTranslationX(view, -view.getWidth());
                        ViewCompat.setAlpha(view, 0);
                        dispatchChangeFinished(oldHolder, true);
                    }
                }).start();
        return true;
    }

    private void animateChange(RecyclerView.ViewHolder holder) {
        final ViewPropertyAnimatorCompat newViewAnimation = ViewCompat.animate(holder.itemView);
        Log.i("entra", "si");
        newViewAnimation.translationX(0).alpha(1).setDuration(200)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        dispatchChangeStarting(holder, false);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        newViewAnimation.setListener(null);
                        ViewCompat.setTranslationX(view, 0);
                        ViewCompat.setAlpha(view, 1);
                        dispatchChangeFinished(holder, false);
                        // implement any additional code here
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        newViewAnimation.setListener(null);
                        ViewCompat.setTranslationX(view, 0);
                        ViewCompat.setAlpha(view, 1);
                        dispatchChangeFinished(holder, false);
                    }
                }).start();
    }
}

