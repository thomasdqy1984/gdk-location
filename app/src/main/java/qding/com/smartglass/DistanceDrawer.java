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
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.glass.timeline.DirectRenderingCallback;

/**
 * {@link com.google.android.glass.timeline.DirectRenderingCallback} used to draw the chronometer on the timeline {link LiveCard}.
 * Rendering requires that:
 * <ol>
 * <li>a {@link android.view.SurfaceHolder} has been created through monitoring the
 * {link android.view.SurfaceHolder.Callback#onSurfaceCreated(android.view.SurfaceHolder)} and
 * {link android.view.SurfaceHolder.Callback#onSurfaceDestroyed(android.view.SurfaceHolder)} callbacks.
 * <li>rendering has not been paused (defaults to rendering) through monitoring the
 * {link DirecRenderingCallback#renderingPaused(android.view.SurfaceHolder, boolean)} callback.
 * </ol>
 * As this class uses an inflated {@link android.view.View} to draw on the {@link android.view.SurfaceHolder}'s
 * {@link android.graphics.Canvas}, monitoring the
 * {link android.view.SurfaceHolder.Callback#onSurfaceChanged(android.view.SurfaceHolder, int, int, int)} callback is also
 * required to properly measure and layout the {@link android.view.View}'s dimension.
 */
public class DistanceDrawer implements DirectRenderingCallback {

    private static final String TAG = "DistanceDrawer";
    private static final int COUNT_DOWN_VALUE = 3;

    private final DistanceView mDistanceView;

    private SurfaceHolder mHolder;
    private boolean mRenderingPaused;

    private final DistanceView.Listener mDistanceListener = new DistanceView.Listener() {

        @Override
        public void onChange() {
            if (mHolder != null) {
                draw(mDistanceView);
            }
        }
    };

    public DistanceDrawer(Context context) {
        this(new DistanceView(context));
    }

    public DistanceDrawer(DistanceView distanceView) {
        mDistanceView = distanceView;
        mDistanceView.setListener(mDistanceListener);
    }

    /**
     * Uses the provided {@code width} and {@code height} to measure and layout the inflated
     * {@link DistanceView}.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Measure and layout the view with the canvas dimensions.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mDistanceView.measure(measuredWidth, measuredHeight);
        mDistanceView.layout(
                0, 0, mDistanceView.getMeasuredWidth(), mDistanceView.getMeasuredHeight());
    }

    /**
     * Keeps the created {@link android.view.SurfaceHolder} and updates this class' rendering state.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The creation of a new Surface implicitly resumes the rendering.
        mRenderingPaused = false;
        mHolder = holder;
        updateRenderingState();
    }

    /**
     * Removes the {@link android.view.SurfaceHolder} used for drawing and stops rendering.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        updateRenderingState();
    }

    /**
     * Updates this class' rendering state according to the provided {@code paused} flag.
     */
    @Override
    public void renderingPaused(SurfaceHolder holder, boolean paused) {
        mRenderingPaused = paused;
        updateRenderingState();
    }

    /**
     * Starts or stops rendering according to the {link LiveCard}'s state.
     */
    private void updateRenderingState() {
        if (mHolder != null && !mRenderingPaused) {
            mDistanceView.start();
        } else {
            mDistanceView.stop();
        }
    }

    /**
     * Draws the view in the SurfaceHolder's canvas.
     */
    private void draw(View view) {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            Log.e(TAG, "Unable to lock canvas: " + e);
            return;
        }
        if (canvas != null) {
            view.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
