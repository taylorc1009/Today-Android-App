package com.app.today;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
/*import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;*/

class AppUtilities {
    //I was going to make a collection of date utilities for the calendar and alarms, but this was the only method I needed across both
    static Calendar buildTime(int hour, int minute, int second, int millis) {
        //Create an instance of the exact current time in milliseconds
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(System.currentTimeMillis());

        //Alter said time using the parameters to build a time instance the system can use for calculations
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, second);
        day.set(Calendar.MILLISECOND, millis);

        return day;
    }

    static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(NullPointerException e) {
            Log.e("? could not close keyboard", e.toString());
        }
    }

    static CardView createTableCard(Context context, int radius, int lMarg, int tMarg, int rMarg, int bMarg) {
        CardView card = new CardView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(lMarg, tMarg, rMarg, bMarg);
        card.setLayoutParams(params);
        card.setRadius(radius);
        return card;
    }

    static ConstraintLayout createConstraintLayout(Context context, int id, int lMarg, int tMarg, int rMarg, int bMarg) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(id);
        CardView.LayoutParams cParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cParams.setMargins(lMarg, tMarg, rMarg, bMarg);
        layout.setLayoutParams(cParams);
        return layout;
    }

    static TextView createText(Context context, int id, String string, int size, int color, int typeface) {
        TextView view = new TextView(context);
        view.setId(id);
        view.setText(string);
        view.setTextSize(size);
        view.setTextColor(ContextCompat.getColor(context, color));
        view.setTypeface(null, typeface);
        return view;
    }

    static ImageView createDrawableImage(Context context, int id, int image, int width, int height) {
        ImageView view = new ImageView(context);
        view.setId(id);
        view.setImageDrawable(ContextCompat.getDrawable(context, image));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        view.setLayoutParams(params);
        return view;
    }

    /*static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.

        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //super.applyTransformation(interpolatedTime, t);
                v.getLayoutParams().height = interpolatedTime == 1 ? ConstraintLayout.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 10);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1)
                    v.setVisibility(View.GONE);
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 10);
        v.startAnimation(a);
    }*/

    //Used to expand views with a slide animation
    /*public static void setVisibleWithAnim(final View view) {
        view.measure(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(150);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
                view.setLayoutParams(layoutParams);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }*/
}