package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import com.hci_capstone.poison_ivy_tracker.R;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.image_chooser.ImageChooserActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
 * TODO: Add max pictures limit.
 */
public class ReportFragment extends Fragment {

    public static final String IMAGE_LIST_EXTRA = "image_list";

    private ToggleButton yes_button, no_button, creeping_button, climbing_button, shrub_button;
    private Button camera_button, submit_button, edit_button;
    private ToggleButtonGroup question1Group, question2Group;

    private AnimatorSet question2Fade, question3Fade, imageEditFade;
    private Interpolator forwardInterpolator, reverseInterpolator;

    private List<File> currentImageFiles;

    private OnReportSubmittedListener reportCallback;

    final String AUTHORITY = "com.hci_capstone.poison_ivy_tracker.fileprovider";

    public ReportFragment() {
        currentImageFiles = new ArrayList<>();
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
        camera_button = (Button) view.findViewById(R.id.report_camera_button);
        edit_button = (Button) view.findViewById(R.id.report_edit_button);
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
                    if (edit_button.isEnabled()) {
                        performFadeOutFor(imageEditFade);
                        deleteAllImages();
                    }

                    question2Group.setEnabled(false);
                    question2Group.clearCheck();

                    camera_button.setEnabled(false);
                    edit_button.setEnabled(false);

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

                    // Check if we need to enable the edit buttons.
                    if (currentImageFiles.size() > 0) {
                        edit_button.setEnabled(true);
                        performFadeInFor(imageEditFade);
                    }
                }
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ImageChooserActivity.class);
                ArrayList<String> imageList = new ArrayList<>();
                for (File f : currentImageFiles) {
                    imageList.add(f.getAbsolutePath());
                }
                intent.putStringArrayListExtra(IMAGE_LIST_EXTRA, imageList);
                startActivityForResult(intent, REQUEST_IMAGE_CHOOSER);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ivyPresent = question1Group.getCurSelectedTag().equals("yes");
                String ivyType = question2Group.getCurSelectedTag();

                List<String> imageLocations = null;
                if (currentImageFiles.size() > 0) {
                    imageLocations = new ArrayList<>();
                    for (File f : currentImageFiles) {
                        imageLocations.add(f.getAbsolutePath());
                    }
                }

                reportCallback.onReportSubmitted(ivyPresent, ivyType, imageLocations);

                Log.v("IVY_REPORT", "Info submitted: " + ivyPresent + " " + ivyType + " " + imageLocations);
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
        currentImageFiles.clear();
        submit_button.setEnabled(false);

        question1Group.clearCheck();
        question1Group.setEnabled(true);

        question2Group.clearCheck();
        if (question2Group.isEnabled()) {
            question2Group.setEnabled(false);
            performFadeOutFor(question2Fade);
        }

        if (camera_button.isEnabled()) {
            camera_button.setEnabled(false);
            performFadeOutFor(question3Fade);
        }
        if (edit_button.isEnabled()) {
            edit_button.setEnabled(false);
            performFadeOutFor(imageEditFade);
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
        imageEditFade = new AnimatorSet();
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

        // Create the fade animation for the edit buttons.
        TextView edit_text = (TextView) view.findViewById(R.id.report_edit_text);

        imageEditFade.playTogether(
                createSingleFadeAnimation(edit_button, animationDuration),
                createSingleFadeAnimation(edit_text, animationDuration)
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CHOOSER = 2;

    /**
     * Starts the default camera app and saves the image in internal storage.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                currentImageFiles.add(photoFile);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Log.v("IVY_IMAGE_CAPTURE", "Successfully took picture. Stored at: " + getNewestImage().getAbsolutePath());

                // Enable picture editing.
                edit_button.setEnabled(true);
                performFadeInFor(imageEditFade);

                // Reduce the image size.
                reduceImageSize(getNewestImage());
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.v("IVY_IMAGE_CAPTURE", "Picture was cancelled.");

                // Delete the empty temp file.
                deleteNewestImage();
            }
        }
        else if (requestCode == REQUEST_IMAGE_CHOOSER) {
            if (resultCode == RESULT_OK) {
                Log.v("IVY_IMAGE_CHOOSER", "Image editing complete.");
                List<String> deletedImages = data.getStringArrayListExtra(ImageChooserActivity.RETURN_EXTRA);

                // Delete and remove the files returned from the ImageChooserActivity.
                for (int i = 0; i < currentImageFiles.size(); i++) {
                    File f = currentImageFiles.get(i);
                    if (deletedImages.contains(f.getAbsolutePath())) {
                        f.delete();
                        currentImageFiles.remove(f);
                        i--;
                    }
                }

                // Set edit button to be disabled if no pictures are left.
                if (currentImageFiles.size() == 0) {
                    edit_button.setEnabled(false);
                    performFadeOutFor(imageEditFade);
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.v("IVY_IMAGE_CHOOSER", "Image editing was cancelled.");
            }
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

        return image;
    }

    /**
     * Delete the current file if it exists.
     */
    private void deleteNewestImage() {
        if (currentImageFiles.size() >= 1) {
            File current = currentImageFiles.remove(currentImageFiles.size() - 1);
            current.delete();
        }
    }

    /**
     * Get the last image to be added to the list.
     * @return the last image
     */
    private File getNewestImage() {
        if (currentImageFiles.size() >= 1) {
            return currentImageFiles.get(currentImageFiles.size() - 1);
        }
        return null;
    }

    /**
     * Deletes all the current images.
     */
    private void deleteAllImages() {
        while (currentImageFiles.size() > 0) {
            File image = currentImageFiles.remove(0);
            image.delete();
        }
    }

    /**
     * TODO: Create a smarter image size reduction?
     * Overwrites an image file with a size reduced version of it.
     * @param file
     * @return the file
     */
    public File reduceImageSize(File file) {
        try {
            Log.v("IVY_IMAGE_CAPTURE", "Attempting to reduce image size, starting size: " + file.length());

            // BitmapFactory options to downsize the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 8;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            inputStream = new FileInputStream(file);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            Log.v("IVY_IMAGE_CAPTURE", "Finished image size reduction, new size: " + file.length());
            return file;
        } catch (Exception e) {
            Log.v("IVY_IMAGE_CAPTURE", "Failed to reduce image size.");
            return null;
        }
    }

}
