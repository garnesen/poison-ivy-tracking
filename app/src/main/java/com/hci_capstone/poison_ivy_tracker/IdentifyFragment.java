package com.hci_capstone.poison_ivy_tracker;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Console;

/**
 * Created by douglasbotello on 2/23/18.
 */

public class IdentifyFragment extends Fragment {


    LinearLayout ivyDescription, creepingDescription, climbingDescription, shrubDescription;
    ImageView ivyTitleImg, creepingTitleImg, climbingTitleImg, shrubTitleImg;
    ImageView ivyDescriptionImg, creepingDescriptionImg,climbingDescriptionImg, shrubDescriptionImg;
    CardView cvIvy, cvCreeping, cvClimbing, cvShrub;

    ValueAnimator mAnimator;


    public IdentifyFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);

        // Get the elements for each card and set the arrow

        //Ivy
        ivyTitleImg = view.findViewById(R.id.ivy_title_img);
        ivyDescription = view.findViewById(R.id.ivy_description);
        ivyDescriptionImg = view.findViewById(R.id.ivy_description_img);
        cvIvy = view.findViewById(R.id.cvIvy);
        //Set ivy as open on load
        ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
        expand(ivyDescription);

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
        cvIvy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ivyDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(ivyDescription);
                    collapseAllOthers(ivyDescription);
                    ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                } else {
                    // it's expanded - collapse it
                    collapse(ivyDescription);
                    ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
                }

            }
        });

        cvCreeping.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (creepingDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(creepingDescription);
                    collapseAllOthers(creepingDescription);
                    creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                } else {
                    // it's expanded - collapse it
                    collapse(creepingDescription);
                    creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
                }

            }
        });

        cvClimbing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (climbingDescription.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    expand(climbingDescription);
                    climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24px);
                } else {
                    // it's expanded - collapse it
                    collapse(climbingDescription);
                    collapseAllOthers(climbingDescription);
                    climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
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
                } else {
                    // it's expanded - collapse it
                    collapse(shrubDescription);
                    shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
                }

            }
        });




        return view;
    }

    private void expand(LinearLayout l){
        //set Visible


        l.setVisibility(View.VISIBLE);
		final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        l.measure(widthSpec, heightSpec);
		mAnimator = slideAnimator(l, 0, l.getMeasuredHeight());


        mAnimator.start();
    }


    private void collapseAllOthers(LinearLayout l)
    {
        if(l == ivyDescription)
        {
            collapse(climbingDescription);
            collapse(creepingDescription);
            collapse(shrubDescription);
            climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);


        }
        if(l == climbingDescription)
        {
            collapse(ivyDescription);
            collapse(creepingDescription);
            collapse(shrubDescription);
            ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        }
        if(l == creepingDescription)
        {
            collapse(climbingDescription);
            collapse(ivyDescription);
            collapse(shrubDescription);
            climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            shrubTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
        }
        if(l == shrubDescription)
        {
            collapse(climbingDescription);
            collapse(creepingDescription);
            collapse(ivyDescription);
            climbingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            creepingTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
            ivyTitleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24px);
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



