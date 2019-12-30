package com.cyl.musiclake.ui.music.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.cyl.musiclake.R;
import com.cyl.musiclake.bean.Music;


public class ShowDetailDialog extends DialogFragment {

    private static boolean result = false;

    public static ShowDetailDialog newInstance(Music song) {
        ShowDetailDialog dialog = new ShowDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", song);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Music music = getArguments().getParcelable("music");
        StringBuilder sb = new StringBuilder();
        sb.append("Song title：")
                .append(music.getTitle())
                .append("\n\n")
                .append("Singer：")
                .append(music.getArtist())
                .append("\n\n")
                .append("Album：")
                .append(music.getAlbum());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.artist_detail)
                .setMessage(sb.toString())
                .create();
    }


}