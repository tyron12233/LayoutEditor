package com.tyron.layouteditor.editor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tyron.layouteditor.R;
import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.values.Primitive;
import com.tyron.layouteditor.values.Value;

public class EditTextDialog extends DialogFragment {

    private final Attribute attribute;
    private EditText editText;
    private EditText editText_id;

    private OnUpdateListener listener;

    public EditTextDialog(Attribute attribute){
        this.attribute = attribute;
    }

    public interface OnUpdateListener{
        void onUpdate(Attribute attr);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Apply", (dialog, id) -> {

            Value value = null;
            String textValue = editText.getText().toString();
            switch(Attributes.getType(attribute.key)){
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
           listener.onUpdate(attribute);
        });

        View view = getActivity().getLayoutInflater().inflate(R.layout.edittext_dialog, null);

        editText = view.findViewById(R.id.edittext_value);
        editText.setText(attribute.value.getAsString());
        editText_id = view.findViewById(R.id.edittext_id);
        editText_id.setText(attribute.id);
        builder.setView(view);
        builder.setTitle(attribute.key);
        return builder.create();
    }


    public void setOnUpdateListener(OnUpdateListener listener){
        this.listener = listener;
    }
}