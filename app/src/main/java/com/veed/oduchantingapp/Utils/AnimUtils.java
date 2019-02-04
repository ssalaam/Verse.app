package com.veed.oduchantingapp.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;


/**
 * Created by Saboor Salaam on 9/19/2015.
 */
public class AnimUtils {
    public static void hide(final View view){
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0).setDuration(300);
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimation.start();

    }

    public static void show(final View view){
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0,1).setDuration(300);
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimation.start();
    }

    public static void hide(final View view, int duration){
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0).setDuration(duration);
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimation.start();

    }

    public static void show(final View view, int duration){
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, View.ALPHA, 0,1).setDuration(duration);
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimation.start();
    }


}
