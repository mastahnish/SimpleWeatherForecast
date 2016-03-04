package com.myos.simpleweatherforecast;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.webkit.WebView;

public class LicensesDialogFragment extends DialogFragment {


    public static LicensesDialogFragment newInstance() {
        return new LicensesDialogFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licences, null);
        view.loadUrl("file:///android_asset/licenses.html");
        return new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.opensourceLicenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}
