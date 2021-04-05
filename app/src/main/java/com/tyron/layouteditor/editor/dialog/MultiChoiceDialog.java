package com.tyron.layouteditor.editor.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tyron.layouteditor.models.Attribute;

public class MultiChoiceDialog extends DialogFragment {

    public static MultiChoiceDialog newInstance(Attribute attribute){
        MultiChoiceDialog dialog = new MultiChoiceDialog();

        Bundle args = new Bundle();

        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        return builder.create();
    }
}
