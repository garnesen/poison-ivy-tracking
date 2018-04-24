package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.image_chooser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.hci_capstone.poison_ivy_tracker.R;
import com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.ReportFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for selecting images from a given set.
 */
public class ImageChooserActivity extends AppCompatActivity {

    public static final String RETURN_EXTRA = "deleted_image_list";

    private GridView gridView;
    private ImageAdapter gridViewAdapter;
    private ImageExpander expander;
    private Menu menu;
    private ArrayList<String> deletedImageLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chooser);

        expander = new ImageExpander();
        deletedImageLocations = new ArrayList<>();

        // Get the list of image locations to load in the view.
        Intent intent = getIntent();
        List<String> imageLocations = intent.getStringArrayListExtra(ReportFragment.IMAGE_LIST_EXTRA);

        gridViewAdapter = new ImageAdapter(this, imageLocations);
        gridView = findViewById(R.id.image_chooser_gridview);
        gridView.setAdapter(gridViewAdapter);
    }

    private void onTrashIconClicked() {
        showTrashIcon(false);
        deletedImageLocations.addAll(gridViewAdapter.deleteSelectedImages());

        Intent data = new Intent();
        data.putStringArrayListExtra(RETURN_EXTRA, deletedImageLocations);
        setResult(RESULT_OK, data);
    }

    /**
     * Set the visibility of the trash menu icon.
     * @param show if true show, otherwise hide
     */
    private void showTrashIcon(boolean show) {
        menu.findItem(R.id.image_chooser_menu_trash).setVisible(show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_chooser_activity_menu, menu);
        this.menu = menu;
        showTrashIcon(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Make the "up" button behave like a back button.
                onBackPressed();
                return true;
            case R.id.image_chooser_menu_trash:
                onTrashIconClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Make the back button zoom back to thumbnail if expanded.
        if (expander.isExpanded()) {
            expander.zoomBackToThumb();
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * An adapter for the GridView of images.
     */
    private class ImageAdapter extends BaseAdapter {
        private List<String> imageLocations;
        private List<GridImage> gridImages;
        private int numChecked;
        private GridImage expandedGridImage;

        public ImageAdapter(Context context, List<String> imageLocations) {
            this.imageLocations = imageLocations;
            numChecked = 0;
            gridImages = new ArrayList<>(imageLocations.size());

            // Create all the GridImages.
            for (String imageLocation : imageLocations) {
                final GridImage gridImage = new GridImage(context);
                final Bitmap bitmap = BitmapFactory.decodeFile(imageLocation);
                gridImage.setImageBitmap(bitmap);
                gridImage.setTag(imageLocation);

                gridImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gridImage.getState() == GridImage.State.NO_CHECK) {
                            expander.zoomImageFromThumb(gridImage.getImageView(), bitmap);
                            expandedGridImage = gridImage;
                            showTrashIcon(true);
                        }
                        else if (gridImage.getState() == GridImage.State.CHECKED){
                            gridImage.setState(GridImage.State.UNCHECKED);
                            numChecked--;
                            if (numChecked == 0) {
                                // Set all to NoCheck.
                                unselectAll();
                                showTrashIcon(false);
                            }
                        }
                        else if (gridImage.getState() == GridImage.State.UNCHECKED) {
                            numChecked++;
                            gridImage.setState(GridImage.State.CHECKED);
                        }
                    }
                });

                gridImage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (gridImage.getState() == GridImage.State.NO_CHECK) {
                            showTrashIcon(true);
                            numChecked++;
                            // Set all to unchecked.
                            for (GridImage g : gridImages) {
                                if (g != gridImage && g != null) {
                                    g.setState(GridImage.State.UNCHECKED);
                                }
                            }
                        }
                        else if (gridImage.getState() == GridImage.State.UNCHECKED) {
                            numChecked++;
                        }
                        gridImage.setState(GridImage.State.CHECKED);
                        return true;
                    }
                });
                gridImages.add(gridImage);
            }
        }

        @Override
        public int getCount() {
            return gridImages.size();
        }

        @Override
        public Object getItem(int position) {
            return gridImages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return gridImages.get(position);
        }

        /**
         * Select all the GridImages.
         */
        public void selectAll() {
            for (GridImage g : gridImages) {
                g.setState(GridImage.State.CHECKED);
            }
            numChecked = gridImages.size();
        }

        /**
         * Unselect all the GridImages.
         */
        public void unselectAll() {
            for (GridImage g : gridImages) {
                g.setState(GridImage.State.NO_CHECK);
            }
            numChecked = 0;
        }

        /**
         * Remove all the selected images.
         * @return the selected image locations
         */
        public List<String> deleteSelectedImages() {
            List<String> selectedLocations = new ArrayList<>();
            if (numChecked == 0 && expander.isExpanded()) {
                expander.zoomBackNoAnimation();
                gridImages.remove(expandedGridImage);
                selectedLocations.add((String)expandedGridImage.getTag());
                this.notifyDataSetChanged();
                return selectedLocations;
            }
            for (int i = 0; i < gridImages.size(); i++) {
                GridImage g = gridImages.get(i);
                if (g.getState() == GridImage.State.CHECKED) {
                    String gridImageTag = (String) g.getTag();
                    selectedLocations.add(gridImageTag);
                    gridImages.remove(i);
                    imageLocations.remove(gridImageTag);
                    i--;
                }
            }
            unselectAll();
            this.notifyDataSetChanged();
            return selectedLocations;
        }
    }

    /**
     * A class to handle zooming of an image when selected.
     *
     * Most of the code was taken from https://developer.android.com/training/animation/zoom.html
     */
    private class ImageExpander {

        private boolean isExpanded;

        private View thumbView;
        private Bitmap fullImage;
        private Animator mCurrentAnimator;
        private ImageView expandedImageView;
        private Rect startBounds;
        private float startScale;

        private void clearAll() {
            thumbView = null;
            fullImage = null;
            expandedImageView = null;
            startBounds = null;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void zoomImageFromThumb(final View thumb, Bitmap full) {
            this.thumbView = thumb;
            this.fullImage = full;

            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.
            expandedImageView = (ImageView) findViewById(
                    R.id.expanded_image);
            expandedImageView.setImageBitmap(fullImage);

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
            startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            findViewById(R.id.image_chooser_container).getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
            gridView.setAlpha(0f);
            thumbView.setAlpha(0f);
            expandedImageView.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expandedImageView.setPivotX(0f);
            expandedImageView.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                            startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                            startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                            startScale, 1f))
                    .with(ObjectAnimator.ofFloat(expandedImageView,
                            View.SCALE_Y, startScale, 1f));
            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;
            isExpanded = true;

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            expandedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomBackToThumb();
                }
            });
        }

        public void zoomBackNoAnimation() {
            isExpanded = false;
            showTrashIcon(false);
            gridView.setAlpha(1f);
            thumbView.setAlpha(1f);
            expandedImageView.setVisibility(View.GONE);
            mCurrentAnimator = null;
        }

        public void zoomBackToThumb() {
            isExpanded = false;
            final float startScaleFinal = startScale;
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set = new AnimatorSet();
            set.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y,startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScaleFinal));
            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                    clearAll();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                    clearAll();
                }
            });
            gridView.setAlpha(1f);
            set.start();
            mCurrentAnimator = set;
        }
    }
}
