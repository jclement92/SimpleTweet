package com.codepath.apps.restclienttemplate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SaveTweetFragment extends DialogFragment {
    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_DRAFT = "DRAFT";

    private String argTitle;
    private String argDraft;

    public SaveTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method used to create a new instance of
     * SaveTweetFragment using the provided parameters.
     *
     * @param title The title of the AlertDialog box.
     * @return A new instance of fragment SaveTweetFragment.
     */
    public static SaveTweetFragment newInstance(String title, String draft) {
        SaveTweetFragment fragment = new SaveTweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DRAFT, draft);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argTitle = getArguments().getString(ARG_TITLE);
            argDraft = getArguments().getString(ARG_DRAFT);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(argTitle)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                        // Use Activity method to create a file in the writeable directory
                        writeFile(argDraft);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        writeFile("");
                        dialog.dismiss();
                        requireActivity().finish();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void writeFile(String tweetContent) {
        FileOutputStream fos;
        try {
            fos = requireActivity().openFileOutput("filename.txt", Context.MODE_PRIVATE);
            // Create buffered writer
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(tweetContent);
            writer.close();
            requireActivity().finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
