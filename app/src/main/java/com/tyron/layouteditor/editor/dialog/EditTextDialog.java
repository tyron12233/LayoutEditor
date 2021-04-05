package com.tyron.layouteditor.editor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.IdGenerator;
import com.tyron.layouteditor.editor.SimpleIdGenerator;
import com.tyron.layouteditor.adapters.StringAutoCompleteAdapter;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.custom.TextInputAutoCompleteTextView;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.util.NotificationCenter;
import com.tyron.layouteditor.values.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditTextDialog extends DialogFragment {

    long delay = 200;
    long last_text_edit = 0;
    long last_value_edit = 0;

    Handler handler = new Handler();

    TextWatcher keyChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(edittextRunnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(edittextRunnable, delay);
            }
        }
    };

    TextWatcher valueChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(edittextRunnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                last_value_edit = System.currentTimeMillis();
                handler.postDelayed(valueRunnable, delay);
            }
        }
    };

    private ArrayList<String> availableAttributes;
    private ArrayList<Attribute> attributes;
    private Attribute currentAttribute;

    private IdGenerator idGenerator;

    private TextInputLayout textInput_value;
    private TextInputLayout textInput_id;

    private TextInputAutoCompleteTextView editText;
    private TextInputAutoCompleteTextView editText_id;

    private final Runnable edittextRunnable = () -> {
        if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
            //if key is valid then we update the value depending on the key
            if (validateKey(editText_id.getText().toString())) {
				try{
                editText.setText(attributes.get(attributes.indexOf(new Attribute(editText_id.getText().toString(), null))).value.toString());

                }catch(Exception ignore){}
            }
        }
    };

    private final Runnable valueRunnable = () -> {
        if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
            //if key is valid then we update the value depending on the key
            if (validateValue(editText.getText().toString())) {

                // editText.setText(attributes.get(attributes.indexOf(new Attribute(editText_id.getText().toString(), null))).value.getAsString());

            }
        }
    };

    private String targetId;

    public EditTextDialog() { }

    public static EditTextDialog newInstance(List<String> availableAttributes, List<Attribute> attributeSet, Attribute attribute, String targetId,
                                             IdGenerator idGenerator){
        EditTextDialog dialog = new EditTextDialog();

        Bundle args = new Bundle();

        String attrString = Value.getGson().toJson(attribute);
        String attrSetString = Value.getGson().toJson(attributeSet);

        args.putStringArrayList("availableAttributes", (ArrayList<String>) availableAttributes);
        args.putString("attributeSet", attrSetString);
        args.putString("attribute", attrString);
        args.putString("id", targetId);
        args.putParcelable("idGenerator", idGenerator);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            Bundle args = getArguments();
            availableAttributes = args.getStringArrayList("availableAttributes");
            attributes = Value.getGson().fromJson(args.getString("attributeSet"),
                    new TypeToken<ArrayList<Attribute>>(){}.getType());
            currentAttribute = Value.getGson().fromJson(args.getString("attribute"), Attribute.class);
            targetId = args.getString("id");
            idGenerator = args.getParcelable("idGenerator");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Attribute attribute = currentAttribute;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Apply", null);


        View view = getActivity().getLayoutInflater().inflate(R.layout.edittext_dialog, null);

        editText = view.findViewById(R.id.edittext_value);
        editText.setText(attribute.value.toString());
        editText_id = view.findViewById(R.id.edittext_id);
        editText_id.setText(attribute.key);
        textInput_id = view.findViewById(R.id.textinput_id);
        textInput_value = view.findViewById(R.id.textinput_value);

        editText_id.setAdapter(new StringAutoCompleteAdapter(getActivity(), R.layout.edittext_dialog, R.id.lbl_name, availableAttributes));
        editText_id.addTextChangedListener(keyChangedListener);
        editText.addTextChangedListener(valueChangedListener);
		
		validateKey(attribute.key);
		
        builder.setView(view);
        builder.setTitle("Set attribute");

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final Attribute attribute = currentAttribute;

        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {


            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
                Value value = null;
                String textValue = editText.getText().toString();



                switch (Attributes.getType(editText_id.getText().toString())) {
                    case Attributes.TYPE_BOOLEAN:
                        attribute.value = new Primitive(Boolean.parseBoolean(textValue));
                        break;
                    case Attributes.TYPE_NUMBER:
                        attribute.value = new Primitive(Integer.parseInt(textValue));
                        break;
                    case Attributes.TYPE_DIMENSION:
                        attribute.value = Dimension.valueOf(textValue);
                        break;
                    default:
                    case Attributes.TYPE_STRING:
                        attribute.value = new Primitive(textValue);
                        break;
					case Attributes.TYPE_DRAWABLE_STRING:
					    attribute.value = DrawableValue.valueOf(textValue, EditTextDialog.this.getActivity());
                }
                attribute.key = editText_id.getText().toString();
                setAttribute(attribute);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdateWidget, targetId, Collections.singletonList(attribute));
                dismiss();
            });

        }
    }

    /**
     * Convenience method to replace an attribute in an ArrayList
     *
     * @param attribute attribute to set
     */
    private void setAttribute(Attribute attribute) {

        if (attributes.contains(attribute)) {
            attributes.set(attributes.indexOf(attribute), attribute);
        } else {
            attributes.add(attribute);
        }
    }

    private boolean validateKey(String key) {
        Attribute attribute = new Attribute(key, Null.INSTANCE);

        if(!availableAttributes.contains(key)){
            textInput_id.setError("Invalid attribute");
            return false;
        }
        textInput_id.setErrorEnabled(false);

        switch (Attributes.getType(key)) {
            case Attributes.TYPE_LAYOUT_STRING:
                editText.setAdapter(new StringAutoCompleteAdapter(getActivity(), R.layout.edittext_dialog, R.id.lbl_name, ((SimpleIdGenerator)idGenerator).getKeys()));
                break;
            case Attributes.TYPE_NUMBER:
                break;
            case Attributes.TYPE_BOOLEAN:
                editText.setAdapter(new StringAutoCompleteAdapter(getActivity(), R.layout.edittext_dialog, R.id.lbl_name, new ArrayList<String>(Arrays.asList("true", "false"))));
                break;
        }
        return true;
    }

    private boolean validateValue(String value) {

        String key = editText_id.getText().toString();
        boolean result = false;
        String error = "";


        if (key == null) {
            return false;
        }

        switch (Attributes.getType(key)) {
            case Attributes.TYPE_BOOLEAN:
                result = value.matches("(?:tru|fals)e");
                error = "Must be a boolean";
                break;
            case Attributes.TYPE_LAYOUT_STRING:
                result = idGenerator.keyExists(value);
                error = "Must be a valid view id";
                break;
            case Attributes.TYPE_DIMENSION:
                result = value.equals("match_parent") | value.equals("wrap_content") | value.equals("fill_parent") |
                        value.matches("[0-9]*(dp|px)");
                error = "Invalid value";
                break;
			case Attributes.TYPE_STRING:
			    result = true;
				break;
            case Attributes.TYPE_NUMBER:
                result = true;
                break;
			case Attributes.TYPE_DRAWABLE_STRING:
			    result = value.matches("#[0-9a-fA-F]{8}$|#[0-9a-fA-F]{6}$|#[0-9a-fA-F]{4}$|#[0-9a-fA-F]{3}");
				error = "Must be a valid color hex";
				break;
        }

        if (!result) {
            textInput_value.setError(error);
        } else {
            textInput_value.setErrorEnabled(false);
        }

        return result;
    }
}