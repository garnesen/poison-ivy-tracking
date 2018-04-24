package com.hci_capstone.poison_ivy_tracker;

import im.delight.android.location.SimpleLocation;

/**
 * Interface for sending a SimpleLocation object from the MainActivity to the fragment.
 */
public interface GetLocationListener {
    SimpleLocation getLocation();
}
