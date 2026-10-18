
package com.tejplays.dotloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.MotionEvent;
import android.content.Context;
import android.os.SystemClock;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(0);

        setContentView(new LoadingView(this));
    }

    static class LoadingView extends View {

        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final long start = SystemClock.uptimeMillis();
        private final int blue = Color.rgb(56, 103, 255);
        private boolean running = true;

        LoadingView(Context c) {
            super(c);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLACK);

            float w = getWidth();
            float h = getHeight();
            float density = getResources().getDisplayMetrics().density;

            float spacing = 39f * density;
            float left = w * 0.075f;
            float right = w * 0.925f;
            float top = h * 0.29f;
            float bottom = h * 0.76f;
            float smallR = 1.8f * density;

            float elapsed =
                    (SystemClock.uptimeMillis() - start) / 1000f;

            // Background dot grid
            p.setStyle(Paint.Style.FILL);

            for (float y = top; y <= bottom; y += spacing) {
                for (float x = left; x <= right; x += spacing) {
                    p.setColor(Color.rgb(27, 27, 31));
                    canvas.drawCircle(x, y, smallR, p);
                }
            }

            // Animated vertical blue dots
            float centerX = w * 0.545f;
            float lineTop = h * 0.31f;
            float lineBottom = h * 0.69f;
            float lineSpacing = 41f * density;

            int count = Math.max(
                    8,
                    (int) ((lineBottom - lineTop) / lineSpacing) + 1
            );

            float phase = (elapsed * 1.9f) % count;

            for (int i = 0; i < count; i++) {
                float y = lineTop + i * lineSpacing;

                if (y > lineBottom) break;

                float distance = Math.abs(i - phase);
                distance = Math.min(distance, count - distance);

                float strength = Math.max(
                        0f,
                        1f - distance / 3.6f
                );

                int alpha = (int) (65 + 190 * strength);

                p.setColor(Color.argb(alpha, 56, 103, 255));

                float radius =
                        (7.1f + 1.2f * strength) * density;

                canvas.drawCircle(centerX, y, radius, p);
            }

            // Drifting blue dot
            float driftX = w * (
                    0.37f + 0.055f *
                    (float) Math.sin(elapsed * 0.9)
            );

            float driftY = h * 0.79f;

            float pulse = 10.5f +
                    1.8f * (float) Math.sin(elapsed * 3.0);

            p.setColor(blue);
            canvas.drawCircle(
                    driftX, driftY, pulse * density, p
            );

            // Three-dot marker
            float markerY = lineTop +
                    Math.min(count - 1, Math.round(phase))
                    * lineSpacing;

            p.setColor(blue);
            canvas.drawCircle(
                    centerX, markerY, 16f * density, p
            );

            p.setColor(Color.rgb(0, 25, 100));

            canvas.drawCircle(
                    centerX - 4f * density,
                    markerY + 2f * density,
                    1.8f * density,
                    p
            );

            canvas.drawCircle(
                    centerX + 4f * density,
                    markerY + 2f * density,
                    1.8f * density,
                    p
            );

            if (running) {
                postInvalidateDelayed(33);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                running = !running;

                if (running) {
                    invalidate();
                }

                return true;
            }

            return true;
        }

        @Override
        protected void onDetachedFromWindow() {
            running = false;
            super.onDetachedFromWindow();
        }
    }
}
