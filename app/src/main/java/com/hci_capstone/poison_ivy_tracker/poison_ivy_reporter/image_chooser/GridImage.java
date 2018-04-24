package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter.image_chooser;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hci_capstone.poison_ivy_tracker.R;

/**
 * A widget that shows an image as a square and can be marked as unchecked, checked, or no check.
 */
public class GridImage extends FrameLayout {

    private ImageView image;
    private ImageView check;
    private State state;

    public enum State {
        NO_CHECK, UNCHECKED, CHECKED
    }

    public GridImage(Context context) {
        super(context);
        inflate(context, R.layout.grid_image, this);
        image = findViewById(R.id.grid_image_imageview);
        check = findViewById(R.id.grid_image_check);
        state = State.NO_CHECK;
    }

    public void setState(State state) {
        this.state = state;
        check.setVisibility(state == State.UNCHECKED || state == State.CHECKED ? VISIBLE : INVISIBLE);

        if (state == State.UNCHECKED) {
            check.setImageResource(R.drawable.ic_ring_24dp);
        }
        else if (state == State.CHECKED) {
            check.setImageResource(R.drawable.ic_check_circle_24dp);
        }
    }

    public State getState() {
        return state;
    }

    public void setImageBitmap(Bitmap bm) {
        image.setImageBitmap(bm);
    }

    public ImageView getImageView() {
        return image;
    }
}
