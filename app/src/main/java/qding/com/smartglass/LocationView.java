/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qding.com.smartglass;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Animated countdown going from {@code mTimeSeconds} to 0.
 *
 * The current animation for each second is as follow:
 *   1. From 0 to 500ms, move the TextView from {@code MAX_TRANSLATION_Y} to 0 and its alpha from
 *      {@code 0} to {@code ALPHA_DELIMITER}.
 *   2. From 500ms to 1000ms, update the TextView's alpha from {@code ALPHA_DELIMITER} to {@code 1}.
 * At each second change, update the TextView text.
 */
public class LocationView extends FrameLayout {

    /**
     * Interface to listen for changes in the countdown.
     */
    public interface Listener {
        /** Notified of a change in the view. */
        public void onChange();
    }

    /** About 24 FPS, visible for testing. */
    static final long DELAY_MILLIS = 41;
    private final TextView mDistanceView;


    private final Handler mHandler = new Handler();
    private final Runnable mUpdateViewRunnable = new Runnable() {

        @Override
        public void run() {
            if (mRunning) {
                updateView();
                postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
            }
        }
    };

    private Listener mListener;
    private boolean mRunning;

    public LocationView(Context context) {
        this(context, null, 0);
    }

    public LocationView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.location_layout, this);
        mDistanceView = (TextView) findViewById(R.id.distance);
    }

    /**
     * Sets a {@link Listener}.
     */
    public void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * Returns the set {@link Listener}.
     */
    public Listener getListener() {
        return mListener;
    }

    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        return mHandler.postDelayed(action, delayMillis);
    }

    @Override
    public boolean removeCallbacks(Runnable action){
        mHandler.removeCallbacks(action);
        return true;
    }

    /**
     * Starts distance measuring.
     */
    public void start() {
        if (!mRunning) {
            postDelayed(mUpdateViewRunnable, DELAY_MILLIS);
        }
        mRunning = true;
    }

    /**
     * Stops distance measuring.
     */
    public void stop(){
        if (mRunning){
            removeCallbacks(mUpdateViewRunnable);
        }
        mRunning =false;
    }

    /**
     * Updates the view to reflect the current state of animation, visible for testing.
     *
     * @return whether or not the count down is finished.
     */
    void updateView() {
       mDistanceView.setText("MyDistance");

        if (mListener != null){
            mListener.onChange();
        }
    }
}
