package com.hci_capstone.poison_ivy_tracker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Fragment of the bottom navigation bar that allows the user to submit a report.
 *
 * TODO: If the user takes a picture and closes the app without being submitted, that image is still saved.
 * TODO: Scale captured image sizes down.
 * TODO: Allow multiple pictures to be taken?
 */
public class ReportFragment extends Fragment {

    ToggleButton yes_button, no_button, creeping_button, climbing_button, shrub_button, camera_button;
    Button submit_button;
    ToggleButtonGroup question1Group, question2Group;

    AnimatorSet question2Fade, question3Fade;
    Interpolator forwardInterpolator, reverseInterpolator;

    File currentImageFile;

    OnReportSubmittedListener reportCallback;

    final String AUTHORITY = "com.hci_capstone.poison_ivy_tracker.fileprovider";

    public ReportFragment() {

    }

    public interface OnReportSubmittedListener {
        void onReportSubmitted(boolean ivyPresent, String ivyTpe, List<String> imageLocation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Check that the parent container implements the callback.
        try {
            reportCallback = (OnReportSubmittedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement OnReportSubmittedListener");
        }

        yes_button = (ToggleButton) view.findViewById(R.id.report_yes_button);
        no_button = (ToggleButton) view.findViewById(R.id.report_no_button);
        creeping_button = (ToggleButton) view.findViewById(R.id.report_creeping_button);
        climbing_button = (ToggleButton) view.findViewById(R.id.report_climbing_button);
        shrub_button = (ToggleButton) view.findViewById(R.id.report_shrub_button);
        camera_button = (ToggleButton) view.findViewById(R.id.report_camera_button);
        submit_button = (Button) view.findViewById(R.id.report_submit_button);

        createFadeAnimations(view);

        question1Group = new ToggleButtonGroup();
        question1Group.addToggleButton(yes_button);
        question1Group.addToggleButton(no_button);

        question2Group = new ToggleButtonGroup();
        question2Group.addToggleButton(creeping_button);
        question2Group.addToggleButton(climbing_button);
        question2Group.addToggleButton(shrub_button);
        question2Group.setEnabled(false);

        question1Group.setOnCheckedChangeListener(new ToggleButtonGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleButton button, boolean isChecked) {
                if (button == yes_button) {
                    question2Group.setEnabled(true);
                    submit_button.setEnabled(false);
                    performFadeInFor(question2Fade);
                }
                else if (button == no_button) {
                    if (question2Group.isEnabled()) {
                        performFadeOutFor(question2Fade);
                    }
                    if (camera_button.isEnabled()) {
                        performFadeOutFor(question3Fade);
                    }

                    question2Group.setEnabled(false);
                    question2Group.clearCheck();

                    camera_button.setEnabled(false);
                    camera_button.setChecked(false);

                    submit_button.setEnabled(true);
                }
            }
        });

        question2Group.setOnCheckedChangeListener(new ToggleButtonGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleButton button, boolean isChecked) {

                // Only perform the animation after the first selection in question 2.
                if (!camera_button.isEnabled()) {
                    camera_button.setEnabled(true);
                    performFadeInFor(question3Fade);
                    submit_button.setEnabled(true);
                }
            }
        });

        camera_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // Check if the current image is null to prevent the camera from opening when
                // returning to the report fragment.
                if (isChecked && currentImageFile == null) {
                    dispatchTakePictureIntent();
                }
                else if (!isChecked && currentImageFile != null) {
                    deleteCurrentImage();
                    Toast.makeText(getContext(), "Image deleted.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ivyPresent = question1Group.getCurSelectedTag().equals("yes");
                String ivyType = question2Group.getCurSelectedTag();
                String imageLocation = currentImageFile != null ? currentImageFile.getAbsolutePath() : null;

                // Temporary code to convert one picture to list
                List<String> imageLocations = new ArrayList<String>();
                imageLocations.add(imageLocation);
                // End Temp Code

                reportCallback.onReportSubmitted(ivyPresent, ivyType, imageLocations);

                Log.v("IVY_REPORT", "Info submitted: " + ivyPresent + " " + ivyType + " " + imageLocation);
                Toast.makeText(getContext(), "Report submitted!", Toast.LENGTH_SHORT).show();
                reset();
            }
        });

        return view;
    }

    /**
     * Reset the view.
     */
    public void reset() {
        currentImageFile = null;
        submit_button.setEnabled(false);

        question1Group.clearCheck();
        question1Group.setEnabled(true);

        question2Group.clearCheck();
        if (question2Group.isEnabled()) {
            question2Group.setEnabled(false);
            performFadeOutFor(question2Fade);
        }

        camera_button.setChecked(false);
        if (camera_button.isEnabled()) {
            camera_button.setEnabled(false);
            performFadeOutFor(question3Fade);
        }
    }

    /**
     * Perform a fade in animation for an AnimatorSet.
     * @param as
     */
    private void performFadeInFor(AnimatorSet as) {
        as.setInterpolator(forwardInterpolator);
        as.start();
    }

    /**
     * Perform a fade out animation for an AnimatorSet.
     * @param as
     */
    private void performFadeOutFor(AnimatorSet as) {
        as.setInterpolator(reverseInterpolator);
        as.start();
    }

    /**
     * Initialize the AnimatorSets for fading in the questions.
     * @param view
     */
    private void createFadeAnimations(View view) {
        question2Fade = new AnimatorSet();
        question3Fade = new AnimatorSet();
        forwardInterpolator = new ForwardInterpolator();
        reverseInterpolator = new ReverseInterpolator();

        // Get the animation time from the dimens.
        TypedValue out = new TypedValue();
        getResources().getValue(R.dimen.report_fade_animation_duration, out, true);
        int animationDuration = (int) out.getFloat();

        // Create the fade animation for question 2.
        TextView question2 = (TextView) view.findViewById(R.id.report_question_2);
        TextView creeping_text = (TextView) view.findViewById(R.id.report_creeping_text);
        TextView climbing_text = (TextView) view.findViewById(R.id.report_climbing_text);
        TextView shrub_text = (TextView) view.findViewById(R.id.report_shrub_text);
        View divider2 = (View) view.findViewById(R.id.divider2);

        question2Fade.playTogether(
                createSingleFadeAnimation(question2, animationDuration),
                createSingleFadeAnimation(creeping_text, animationDuration),
                createSingleFadeAnimation(climbing_text, animationDuration),
                createSingleFadeAnimation(shrub_text, animationDuration),
                createSingleFadeAnimation(divider2, animationDuration),
                createSingleFadeAnimation(creeping_button, animationDuration),
                createSingleFadeAnimation(climbing_button, animationDuration),
                createSingleFadeAnimation(shrub_button, animationDuration)
        );

        // Create the fade animation for question 3.
        TextView question3 = (TextView) view.findViewById(R.id.report_question_3);
        TextView camera_text = (TextView) view.findViewById(R.id.report_camera_text);
        View divider3 = (View) view.findViewById(R.id.divider3);

        question3Fade.playTogether(
                createSingleFadeAnimation(question3, animationDuration),
                createSingleFadeAnimation(camera_text, animationDuration),
                createSingleFadeAnimation(divider3, animationDuration),
                createSingleFadeAnimation(camera_button, animationDuration)
        );

    }

    /**
     * Create an alpha transform Animator for a given view and duration.
     * @param view
     * @param duration
     * @return the animation
     */
    private Animator createSingleFadeAnimation(View view, int duration) {
        Animator a = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 1.0f);
        a.setDuration(duration);
        return a;
    }

    /**
     * An interpolator that reverses the effects of an animation.
     */
    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    /**
     * An interpolator for setting an animation back to forward.
     */
    public class ForwardInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return paramFloat;
        }
    }

    /************************
     *    CAMERA CONTROL    *
     ************************/

    static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Starts the default camera app and saves the image in internal storage.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                if (currentImageFile != null) {
                    deleteCurrentImage();
                }
                photoFile = createImageFile();
            }
            catch (IOException e) {
                Log.v("IVY_IMAGE_CAPTURE", "Failed to create photo file.");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.v("IVY_IMAGE_CAPTURE", "Successfully took picture.");
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED){
            Log.v("IVY_IMAGE_CAPTURE", "Picture was cancelled.");

            // Delete the empty temp file.
            deleteCurrentImage();

            camera_button.setChecked(false);
        }
    }

    /**
     * Creates an image file in internal storage.
     * @return the created image file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getFilesDir();
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentImageFile = image;
        return image;
    }

    /**
     * Delete the current file if it exists.
     */
    private void deleteCurrentImage() {
        if (currentImageFile != null) {
            currentImageFile.delete();
            currentImageFile = null;
        }
    }
}
