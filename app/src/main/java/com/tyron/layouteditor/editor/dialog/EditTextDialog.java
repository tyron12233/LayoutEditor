package com.tyron.layouteditor.editor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.tyron.layouteditor.R;
import com.tyron.layouteditor.SimpleIdGenerator;
import com.tyron.layouteditor.adapters.AttributeAutoCompleteAdapter;
import com.tyron.layouteditor.adapters.InterfaceAdapter;
import com.tyron.layouteditor.adapters.LayoutIdAutoCompleteAdapter;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Null;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;
import com.tyron.layouteditor.util.NotificationCenter;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class EditTextDialog extends DialogFragment {

    private ArrayList<Attribute> attributes;
	private int position;
	

    private TextInputLayout textInput_value;
    private TextInputLayout textInput_id;
    private AppCompatAutoCompleteTextView editText;
    private AppCompatAutoCompleteTextView editText_id;
	
	private String targetId;

    public static EditTextDialog newInstance(ArrayList<Attribute> attributes, int position, String targetId){
		
		EditTextDialog dialog = new EditTextDialog();
		
		Bundle args = new Bundle();
		String attrString = Value.getGson().toJson(attributes);
		args.putString("attributes", attrString);
		args.putInt("position", position);
		args.putString("id", targetId);
		dialog.setArguments(args);
		
		return dialog;
	}
	
	
    public EditTextDialog(){
       
    }
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		targetId = getArguments().getString("id");
		position = getArguments().getInt("position");
		attributes = new GsonBuilder()
		.registerTypeAdapter(Value.class, new InterfaceAdapter<Value>())
		.create().fromJson(getArguments().getString("attributes"), new TypeToken<ArrayList<Attribute>>(){}.getType());
	}
	
	
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       
	   final Attribute attribute = attributes.get(position);
	   
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Apply", (dialog, id) -> {

            Value value = null;
            String textValue = editText.getText().toString();

            switch(Attributes.getType(editText_id.getText().toString())){
                case Attributes.TYPE_BOOLEAN:
                    attribute.value = new Primitive(Boolean.parseBoolean(textValue));
                    break;
                case Attributes.TYPE_NUMBER:
                    attribute.value = new Primitive(Integer.parseInt(textValue));
                    break;
                default :
                case Attributes.TYPE_STRING :
                    attribute.value = new Primitive(textValue);
                    break;
            }
            attribute.key = editText_id.getText().toString();
            setAttribute(attribute);
           NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdateWidget, targetId, attributes);
        });
		
		
        View view = getActivity().getLayoutInflater().inflate(R.layout.edittext_dialog, null);

        editText = view.findViewById(R.id.edittext_value);
        editText.setText(attribute.value.getAsString());
        editText_id = view.findViewById(R.id.edittext_id);
        editText_id.setText(attribute.key);
        textInput_id = view.findViewById(R.id.textinput_id);
        textInput_value = view.findViewById(R.id.textinput_value);
        
		editText_id.setAdapter(new AttributeAutoCompleteAdapter(getActivity(), R.layout.edittext_dialog, R.id.lbl_name, attributes));
		editText_id.addTextChangedListener(keyChangedListener);
		builder.setView(view);
        builder.setTitle("Set attribute");
      
	    return builder.create();
    }

    /**
     * Convenience method to replace an attribute in an ArrayList
     * @param attribute attribute to set
     */
    private void setAttribute(Attribute attribute){

        if(attributes.contains(attribute)){
            attributes.set(attributes.indexOf(attribute), attribute);
        }else{
            attributes.add(attribute);
        }
    }

    long delay = 800;
    long last_text_edit = 0;
    Handler handler = new Handler();

    private final Runnable edittextRunnable = () -> {
        if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
            //if key is valid then we update the value depending on the key
            if(validateKey(editText_id.getText().toString())){

                //display suggestions
                if(Attributes.getType(editText_id.getText().toString()) == Attributes.TYPE_LAYOUT_STRING){
                    editText.setAdapter(new LayoutIdAutoCompleteAdapter(getActivity(), R.layout.edittext_dialog, R.id.lbl_name, SimpleIdGenerator.getInstance().getKeys()));
                }
                editText.setText(attributes.get(attributes.indexOf(new Attribute(editText_id.getText().toString(), null))).value.getAsString());

            }
        }
    };

    private boolean validateKey(String key){
        Attribute attribute = new Attribute(key, Null.INSTANCE);
        if(!attributes.contains(attribute)){
            textInput_id.setError("Invalid attribute");
            return false;
        }
        textInput_id.setErrorEnabled(false);
        return true;
    }

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
}