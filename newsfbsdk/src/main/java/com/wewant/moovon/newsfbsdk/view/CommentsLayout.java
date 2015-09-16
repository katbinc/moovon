package com.wewant.moovon.newsfbsdk.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class CommentsLayout extends RelativeLayout {

    private static final String TAG = CommentsLayout.class.getSimpleName();

    private boolean preventScrollUp = false;
    private boolean preventScrollDown = false;

    private int animDuration = 200;
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    private Runnable onCloseListener;

    public CommentsLayout(Context context) {
        super(context);
        init();
    }

    public CommentsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommentsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "INTERCEPT ACTION DOWN");
                // save default base layout height
                defaultViewHeight = this.getHeight();
                // Init finger and view position
                previousFingerPosition = Y;
                baseLayoutPosition = (int) this.getY();
                return false;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "INTERCEPT ACTION UP");
                if (isScrollingUp || isScrollingDown) {
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "INTERCEPT ACTION MOVE");
                if (!isClosing) {
                    boolean canPrevent = false;
                    // if scroll UP
                    if (previousFingerPosition > Y) {
                        Log.d(TAG, "INTERCEPT move up");
                        if (preventScrollUp) {
                            canPrevent = true;
                        }
                    }
                    // id scroll down
                    else if (previousFingerPosition < Y) {
                        Log.d(TAG, "INTERCEPT move down");
                        if (preventScrollDown) {
                            canPrevent = true;
                        }
                    }
                    // Update position
                    previousFingerPosition = Y;
                    return canPrevent;
                }
                return true;
        }
        return false;
    }

    private void init() {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                View inflatedView = CommentsLayout.this;
                // finger position
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION DOWN");
                        // save default base layout height
                        defaultViewHeight = inflatedView.getHeight();
                        // Init finger and view position
                        previousFingerPosition = Y;
                        baseLayoutPosition = (int) inflatedView.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION UP");
                        if (isScrollingUp) {
                            // was scroll up
                            // Reset base layout position
                            inflatedView.setY(0);
                            // not scrolling now
                            isScrollingUp = false;
                        }
                        if (isScrollingDown) {
                            // was scroll down
                            // Reset base layout position
                            inflatedView.setY(0);
                            // Reset base layout size
                            inflatedView.getLayoutParams().height = defaultViewHeight;
                            inflatedView.requestLayout();
                            // not scrolling now
                            isScrollingDown = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "ACTION MOVE");
                        if (!isClosing) {
                            int currentYPosition = (int) inflatedView.getY();
                            // if scroll UP
                            if (previousFingerPosition > Y) {
                                if (!isScrollingUp) {
                                    isScrollingUp = true;
                                }
                                // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
                                if (inflatedView.getHeight() < defaultViewHeight) {
                                    inflatedView.getLayoutParams().height = inflatedView.getHeight() - (Y - previousFingerPosition);
                                    inflatedView.requestLayout();
                                } else {
                                    // can close
                                    if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 3) {
                                        closeUpAndDismissDialog(currentYPosition);
                                        return true;
                                    }
                                }
                                inflatedView.setY(inflatedView.getY() + (Y - previousFingerPosition));
                            }
                            // id scroll down
                            else {
                                if (!isScrollingDown) {
                                    isScrollingDown = true;
                                }
                                // can auto close
                                if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 3) {
                                    closeDownAndDismissDialog(currentYPosition);
                                    return true;
                                }
                                // Change base layout size and position (must change position because view anchor is top left corner)
                                inflatedView.setY(inflatedView.getY() + (Y - previousFingerPosition));
                                inflatedView.getLayoutParams().height = inflatedView.getHeight() - (Y - previousFingerPosition);
                                inflatedView.requestLayout();
                            }
                            // Update position
                            previousFingerPosition = Y;
                        }
                        break;
                }
                return true;
            }
        });

    }

    public void closeUpAndDismissDialog(int currentPosition){
        if (!isClosing) {
            isClosing = true;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(this, "y", currentPosition, -this.getHeight());
            positionAnimator.setDuration(animDuration);
            positionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (onCloseListener != null) {
                        onCloseListener.run();
                    }
                    isClosing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            positionAnimator.start();
        }
    }

    public void closeDownAndDismissDialog(int currentPosition){
        if (!isClosing) {
            isClosing = true;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(this, "y", currentPosition,
                currentPosition + getHeight());
            positionAnimator.setDuration(animDuration);
            positionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (onCloseListener != null) {
                        onCloseListener.run();
                    }
                    isClosing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            positionAnimator.start();
        }
    }

    public void setOnCloseListener(Runnable onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public void setPreventScrollUp(boolean preventScrollUp) {
        Log.d(TAG, "set scroll up" + preventScrollUp);
        this.preventScrollUp = preventScrollUp;
    }

    public void setPreventScrollDown(boolean preventScrollDown) {
        Log.d(TAG, "set scroll down" + preventScrollDown);
        this.preventScrollDown = preventScrollDown;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

}
