package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hci_capstone.poison_ivy_tracker.R;

public class IdentifyFragment extends Fragment {


    LinearLayout creepingDescription, climbingDescription, shrubDescription;
    ImageView  creepingTitleImg, climbingTitleImg, shrubTitleImg;
    ImageView  creepingDescriptionImg,climbingDescriptionImg, shrubDescriptionImg;
    CardView  cvCreeping, cvClimbing, cvShrub;
    TextView climbingText, shrubText, creepingText;

    ValueAnimator mAnimator;


    public IdentifyFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);
        //Set the text for the descriptions
        climbingText = view.findViewById(R.id.climbingText);
        shrubText = view.findViewById(R.id.shrubText);
        creepingText = view.findViewById(R.id.creepingText);

        climbingText.setText(Html.fromHtml(getString(R.string.climbingDescription)));
        shrubText.setText(Html.fromHtml(getString(R.string.shrubDescription)));
        creepingText.setText(Html.fromHtml(getString(R.string.creepingDescription)));


        // Get the elements for each card and set the arrow


        //Creeping
        creepingTitleImg = view.findViewById(R.id.creeping_title_img);
        creepingDescription = view.findViewById(R.id.creeping_description);
        creepingDescriptionImg = view.findViewById(R.id.creeping_description_img);
        cvCreeping = view.findViewById(R.id.cvcreeping);
        creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);


        //Climbing
        climbingTitleImg = view.findViewById(R.id.climbing_title_img);
        climbingDescription = view.findViewById(R.id.climbing_description);
        climbingDescriptionImg = view.findViewById(R.id.climbing_description_img);
        cvClimbing = view.findViewById(R.id.cvclimbing);
        climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);


        //Shrub
        shrubTitleImg = view.findViewById(R.id.shrub_title_img);
        shrubDescription = view.findViewById(R.id.shrub_description);
        shrubDescriptionImg = view.findViewById(R.id.shrub_description_img);
        cvShrub = view.findViewById(R.id.cvshrub);
        shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);


        //Listen for clicks on any part of the Ivy card
//        cvIvy.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (ivyDescription.getVisibility() == View.GONE) {
//                    // it's collapsed - expand it
//                    expand(ivyDescription);
//                    collapseAllOthers(ivyDescription);
//                    ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
//                }
//                else {
//                    // it's expanded - collapse it
//                    collapse(ivyDescription);
//                    ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
//                }

//            }
//        });

        cvCreeping.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (creepingDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(creepingDescription);
                    collapseAllOthers(creepingDescription);
                    creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                }
                else
                {
                    collapseAll();
                }

            }
        });

        cvClimbing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (climbingDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(climbingDescription);
                    collapseAllOthers(climbingDescription);
                    climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                }
                else
                {
                    collapseAll();
                }

            }
        });

        cvShrub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (shrubDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(shrubDescription);
                    collapseAllOthers(shrubDescription);
                    shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                }
                else
                {
                    collapseAll();
                }


            }
        });




        return view;
    }

    /**
     * Expand a view from 0 height to full.
     * Code from: https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
     * @param l the linear layout to expand
     */
    private void expand(final LinearLayout l){
        l.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = l.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        l.getLayoutParams().height = 1;
        l.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                l.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                l.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / l.getContext().getResources().getDisplayMetrics().density));
        l.startAnimation(a);
    }

    private void collapseAll()
    {
        collapse(climbingDescription);
        collapse(creepingDescription);
        collapse(shrubDescription);
        climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
    }

    private void collapseAllOthers(LinearLayout l)
    {
        if(l == climbingDescription)
        {
            collapse(creepingDescription);
            collapse(shrubDescription);
            creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        }
        if(l == creepingDescription)
        {
            collapse(climbingDescription);
            collapse(shrubDescription);
            climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        }
        if(l == shrubDescription)
        {
            collapse(climbingDescription);
            collapse(creepingDescription);
            climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        }

    }

    private void collapse(final LinearLayout l) {
//        l.setVisibility(View.GONE);
        int finalHeight = l.getHeight();

        ValueAnimator mAnimator = slideAnimator(l,finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                l.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }


    private ValueAnimator slideAnimator(final LinearLayout l, int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = l.getLayoutParams();
                layoutParams.height = value;
                l.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


}



