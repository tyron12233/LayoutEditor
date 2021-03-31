package com.tyron.layouteditor.editor.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.parser.ParseHelper;
import com.tyron.layouteditor.util.AndroidUtilities;
import com.tyron.layouteditor.util.LayoutHelper;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.DrawableValue;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

import java.util.Arrays;
import java.util.Collections;

public class ColorPickerDialog extends DialogFragment {

    public static ColorPickerDialog newInstance(Attribute attribute, String targetId){
        ColorPickerDialog dialog = new ColorPickerDialog();

        Bundle args = new Bundle();
        args.putString("targetId", targetId);
        args.putString("attribute", Value.getGson().toJson(attribute));
        dialog.setArguments(args);
        return dialog;
    }

    private ColorPicker picker;
    private String targetId;
    private Attribute attribute;

    public ColorPickerDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            targetId = getArguments().getString("targetId");
            attribute = Value.getGson().fromJson(getArguments().getString("attribute"), Attribute.class);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Set color");

        picker = new ColorPicker(getContext());
        picker.setColor(Color.parseColor(attribute.value.toString()));
        picker.setPaddingRelative(0, AndroidUtilities.dp(8), 0, 0);
        builder.setView(picker);

        builder.setPositiveButton("APPLY", null);
        builder.setNegativeButton("CANCEL", null);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null){
            AlertDialog dialog = (AlertDialog) getDialog();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
                String stringHex = String.format("#%06X", (0xFFFFFF & picker.getColor()));
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdateWidget, targetId, Collections.singletonList(new Attribute(attribute.key, new Primitive(stringHex))));
                Toast.makeText(getContext(), "Selected color: " + picker.getColor(), Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }
    }

    private boolean ignoreTextChange = false;

    private class ColorPicker extends FrameLayout{

        private LinearLayout linearLayout;

        private final int paramValueSliderWidth = AndroidUtilities.dp(20);

        private Paint colorWheelPaint;
        private Paint valueSliderPaint;
        private Paint circlePaint;
        private Drawable circleDrawable;

        private Bitmap colorWheelBitmap;

        private TextInputLayout[] colorTextInputLayout = new TextInputLayout[4];
        private TextInputEditText[] colorEditText = new TextInputEditText[4];

        private int colorWheelRadius;

        private float[] colorHSV = new float[] { 0.0f, 0.0f, 1.0f };
        private float alpha = 1.0f;

        private float[] hsvTemp = new float[3];
        private LinearGradient colorGradient;
        private LinearGradient alphaGradient;

        private boolean circlePressed;
        private boolean colorPressed;
        private boolean alphaPressed;

        private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

        @SuppressLint("UseCompatLoadingForDrawables")
        public ColorPicker(@NonNull Context context) {
            super(context);
            setWillNotDraw(false);

            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();


            colorWheelPaint = new Paint();
            colorWheelPaint.setAntiAlias(true);
            colorWheelPaint.setDither(true);

            valueSliderPaint = new Paint();
            valueSliderPaint.setAntiAlias(true);
            valueSliderPaint.setDither(true);

            linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            addView(linearLayout, params);

            for(int a = 0; a < 4; a++){
                colorTextInputLayout[a] = new TextInputLayout(context);

                colorEditText[a] = new TextInputEditText(context);
                //colorEditText[a].setBackground(null);
                colorEditText[a].setInputType(InputType.TYPE_CLASS_NUMBER);
                colorEditText[a].setTextColor(0xff212121);
                colorEditText[a].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                colorEditText[a].setMaxLines(1);
                colorEditText[a].setTag(a);
                colorEditText[a].setGravity(Gravity.CENTER);

                if (a == 0) {
                    colorTextInputLayout[a].setHint("red");
                } else if (a == 1) {
                    colorTextInputLayout[a].setHint("green");
                } else if (a == 2) {
                    colorTextInputLayout[a].setHint("blue");
                } else if (a == 3) {
                    colorTextInputLayout[a].setHint("alpha");
                }

                colorTextInputLayout[a].setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

                colorEditText[a].setImeOptions((a == 3 ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT) | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                InputFilter[] inputFilters = new InputFilter[1];
                inputFilters[0] = new InputFilter.LengthFilter(3);
                colorEditText[a].setFilters(inputFilters);

                colorTextInputLayout[a].addView(colorEditText[a], LayoutHelper.createLinear(-1, -1));
                linearLayout.addView(colorTextInputLayout[a], LayoutHelper.createLinear(55, -2, 0, 0, a != 3 ? 16 : 0, 0));
                final int num = a;

                colorEditText[a].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (ignoreTextChange) {
                            return;
                        }
                        ignoreTextChange = true;
                        int color = ParseHelper.parseInt(editable.toString());
                        if (color < 0) {
                            color = 0;
                            colorEditText[num].setText("" + color);
                            colorEditText[num].setSelection(colorEditText[num].length());
                        } else if (color > 255) {
                            color = 255;
                            colorEditText[num].setText("" + color);
                            colorEditText[num].setSelection(colorEditText[num].length());
                        }
                        int currentColor = getColor();
                        if (num == 2) {
                            currentColor = (currentColor & 0xffffff00) | (color & 0xff);
                        } else if (num == 1) {
                            currentColor = (currentColor & 0xffff00ff) | ((color & 0xff) << 8);
                        } else if (num == 0) {
                            currentColor = (currentColor & 0xff00ffff) | ((color & 0xff) << 16);
                        } else if (num == 3) {
                            currentColor = (currentColor & 0x00ffffff) | ((color & 0xff) << 24);
                        }
                        setColor(currentColor);

                        ignoreTextChange = false;
                    }
                });
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(widthSize, heightSize);
            measureChild(linearLayout, widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(size, size);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:

                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int centerX = getWidth() / 2 - paramValueSliderWidth * 2;
                    int centerY = getHeight() / 2 - AndroidUtilities.dp(8);
                    int cx = x - centerX;
                    int cy = y - centerY;
                    double d = Math.sqrt(cx * cx + cy * cy);

                    if (circlePressed || !alphaPressed && !colorPressed && d <= colorWheelRadius) {
                        if (d > colorWheelRadius) {
                            d = colorWheelRadius;
                        }
                        circlePressed = true;
                        colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180.0f);
                        colorHSV[1] = Math.max(0.0f, Math.min(1.0f, (float) (d / colorWheelRadius)));
                        colorGradient = null;
                        alphaGradient = null;
                    }
                    if (colorPressed || !circlePressed && !alphaPressed && x >= centerX + colorWheelRadius + paramValueSliderWidth && x <= centerX + colorWheelRadius + paramValueSliderWidth * 2 && y >= centerY - colorWheelRadius && y <= centerY + colorWheelRadius) {
                        float value = (y - (centerY - colorWheelRadius)) / (colorWheelRadius * 2.0f);
                        if (value < 0.0f) {
                            value = 0.0f;
                        } else if (value > 1.0f) {
                            value = 1.0f;
                        }
                        colorHSV[2] = value;
                        colorPressed = true;
                    }
                    if (alphaPressed || !circlePressed && !colorPressed && x >= centerX + colorWheelRadius + paramValueSliderWidth * 3 && x <= centerX + colorWheelRadius + paramValueSliderWidth * 4 && y >= centerY - colorWheelRadius && y <= centerY + colorWheelRadius) {
                        alpha = 1.0f - (y - (centerY - colorWheelRadius)) / (colorWheelRadius * 2.0f);
                        if (alpha < 0.0f) {
                            alpha = 0.0f;
                        } else if (alpha > 1.0f) {
                            alpha = 1.0f;
                        }
                        alphaPressed = true;
                    }
                    if (alphaPressed || colorPressed || circlePressed) {
                        //startColorChange(true);
                        int color = getColor();

                        int red = Color.red(color);
                        int green = Color.green(color);
                        int blue = Color.blue(color);
                        int a = Color.alpha(color);
                        if (!ignoreTextChange) {
                            ignoreTextChange = true;
                            colorEditText[0].setText("" + red);
                            colorEditText[1].setText("" + green);
                            colorEditText[2].setText("" + blue);
                            colorEditText[3].setText("" + a);
                            for (int b = 0; b < 4; b++) {
                                colorEditText[b].setSelection(colorEditText[b].length());
                            }
                            ignoreTextChange = false;
                        }
                        invalidate();
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    alphaPressed = false;
                    colorPressed = false;
                    circlePressed = false;
                    //startColorChange(false);
                    break;
            }
            return super.onTouchEvent(event);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int centerX = getWidth() / 2 - paramValueSliderWidth * 2;
            int centerY = getHeight() / 2 + AndroidUtilities.dp(16);

            canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);

            float hueAngle = (float) Math.toRadians(colorHSV[0]);
            int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
            int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

            float pointerRadius = 0.075f * colorWheelRadius;

            hsvTemp[0] = colorHSV[0];
            hsvTemp[1] = colorHSV[1];
            hsvTemp[2] = 1.0f;

            drawPointerArrow(canvas, colorPointX, colorPointY, Color.HSVToColor(hsvTemp));

            int x = centerX + colorWheelRadius + paramValueSliderWidth;
            int y = centerY - colorWheelRadius;
            int width = AndroidUtilities.dp(9);
            int height = colorWheelRadius * 2;
            if (colorGradient == null) {
                colorGradient = new LinearGradient(x, y, x + width, y + height, new int[]{Color.BLACK, Color.HSVToColor(hsvTemp)}, null, Shader.TileMode.CLAMP);
            }
            valueSliderPaint.setShader(colorGradient);
            canvas.drawRect(x, y, x + width, y + height, valueSliderPaint);
            drawPointerArrow(canvas, x + width / 2, (int) (y + colorHSV[2] * height), Color.HSVToColor(colorHSV));

            x += paramValueSliderWidth * 2;
            if (alphaGradient == null) {
                int color = Color.HSVToColor(hsvTemp);
                alphaGradient = new LinearGradient(x, y, x + width, y + height, new int[]{color, color & 0x00ffffff}, null, Shader.TileMode.CLAMP);
            }
            valueSliderPaint.setShader(alphaGradient);
            canvas.drawRect(x, y, x + width, y + height, valueSliderPaint);
            drawPointerArrow(canvas, x + width / 2, (int) (y + (1.0f - alpha) * height), (Color.HSVToColor(colorHSV) & 0x00ffffff) | ((int) (255 * alpha) << 24));
        }


        private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
            int side = AndroidUtilities.dp(13);
            circleDrawable.setBounds(x - side, y - side, x + side, y + side);
            circleDrawable.draw(canvas);

            circlePaint.setColor(0xffffffff);
            canvas.drawCircle(x, y, AndroidUtilities.dp(11), circlePaint);
            circlePaint.setColor(color);
            canvas.drawCircle(x, y, AndroidUtilities.dp(9), circlePaint);
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldw, int oldh) {
            colorWheelRadius = Math.max(1, width / 2 - paramValueSliderWidth * 2 - AndroidUtilities.dp(20));
            colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);
            //linearLayout.setTranslationY(colorWheelRadius * 2 + AndroidUtilities.dp(20));
            colorGradient = null;
            alphaGradient = null;
        }

        private Bitmap createColorWheelBitmap(int width, int height) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            int colorCount = 12;
            int colorAngleStep = 360 / 12;
            int[] colors = new int[colorCount + 1];
            float[] hsv = new float[]{0.0f, 1.0f, 1.0f};
            for (int i = 0; i < colors.length; i++) {
                hsv[0] = (i * colorAngleStep + 180) % 360;
                colors[i] = Color.HSVToColor(hsv);
            }
            colors[colorCount] = colors[0];

            SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
            RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP);
            ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

            colorWheelPaint.setShader(composeShader);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

            return bitmap;
        }

        @SuppressLint("SetTextI18n")
        public void setColor(int color) {
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            int a = Color.alpha(color);
            if (!ignoreTextChange) {
                ignoreTextChange = true;
                colorEditText[0].setText("" + red);
                colorEditText[1].setText("" + green);
                colorEditText[2].setText("" + blue);
                colorEditText[3].setText("" + a);
                for (int b = 0; b < 4; b++) {
                    colorEditText[b].setSelection(colorEditText[b].length());
                }
                ignoreTextChange = false;
            }
            alphaGradient = null;
            colorGradient = null;
            alpha = a / 255.0f;
            Color.colorToHSV(color, colorHSV);
            invalidate();
        }
        public int getColor() {
            return (Color.HSVToColor(colorHSV) & 0x00ffffff) | ((int) (alpha * 255) << 24);
        }
    }
}
