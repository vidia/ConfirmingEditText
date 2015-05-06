package com.davidtschida.android.lib.confirmingedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.davidtschida.android.lib.mymodule.app2.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO: document your custom view class.
 */
public class ConfirmingEditText extends EditText {

    boolean oldState;

    private Drawable validDrawable;
    private Drawable invalidDrawable;

    private OnValidityChangedCallback listener;
    private CEditTextMatcher matcher;



    private String callbackMethodName;

    public ConfirmingEditText(Context context) {
        super(context);
        init(null, 0);
    }

    public ConfirmingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ConfirmingEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ConfirmingEditText, defStyle, 0);

        validDrawable = a.getDrawable(R.styleable.ConfirmingEditText_validDrawable);
        invalidDrawable = a.getDrawable(R.styleable.ConfirmingEditText_invalidDrawable);

        callbackMethodName = a.getString(R.styleable.ConfirmingEditText_onChange);

        a.recycle();

        addTextChangedListener(new TextWatcher() {
            Method mHandler;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean validity = matcher.isValid(editable);

                if(oldState != validity) {
                    if(listener != null) {
                        listener.onValidityChanged(validity);
                    }
                    else {
                        if (callbackMethodName != null) {
                            if (mHandler == null) {
                                try {
                                    mHandler = getContext().getClass().getMethod(callbackMethodName,
                                            View.class, Boolean.class);
                                } catch (NoSuchMethodException e) {
                                    int id = getId();
                                    String idText = id == NO_ID ? "" : " with id '"
                                            + getContext().getResources().getResourceEntryName(
                                            id) + "'";
                                    throw new IllegalStateException("Could not find a method " +
                                            callbackMethodName + "(View, boolean) in the activity "
                                            + getContext().getClass() + " for onClick handler"
                                            + " on view " + ConfirmingEditText.this.getClass() + idText, e);
                                }
                            }

                            try {
                                mHandler.invoke(getContext(), (View) ConfirmingEditText.this, validity);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException("Could not execute non "
                                        + "public method of the activity", e);
                            } catch (InvocationTargetException e) {
                                throw new IllegalStateException("Could not execute "
                                        + "method of the activity", e);
                            }
                        }
                    }
                }
                setCompoundDrawablesWithIntrinsicBounds(null, null, (validity) ? validDrawable : invalidDrawable , null);
            }
        });
    }

    public void setMatcher(CEditTextMatcher matcher) {
        this.matcher = matcher;
    }

    public void setOnValidityChangedCallback(OnValidityChangedCallback callback) {
        listener = callback;
    }

    private void initialize(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, com.android.internal.R.styleable.View,
                defStyleAttr, 0);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ConfirmingEditText_validDrawable) {
                validDrawable = a.getDrawable(attr);
            } else if (attr == R.styleable.ConfirmingEditText_onChange) {
                if (getContext().isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot "
                            + "be used within a restricted context");
                }

                final String handlerName = a.getString(attr);
                if (handlerName != null) {
                    addTextChangedListener(new TextWatcher() {


                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            boolean validity = matcher.isValid(editable);

                            if (oldState != validity) {
                                if (listener != null) {
                                    listener.onValidityChanged(validity);
                                } else {



                                }
                            }
                            setCompoundDrawablesWithIntrinsicBounds(null, null, (validity) ? validDrawable : invalidDrawable, null);
                        }
                    });
                }
            }

        }
    }

}
