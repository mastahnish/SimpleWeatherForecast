package com.myos.simpleweatherforecast;


import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;


public class PreferenceFragment extends android.preference.PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String PREFS_KEY_LICENCES = "pref_licenses";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference licenses = findPreference(PREFS_KEY_LICENCES);
        licenses.setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();

        switch (key) {
            case PREFS_KEY_LICENCES:
                Log.d("cobytu", "KLIKNALEM LICENCJE");
                LicensesDialogFragment dialog = LicensesDialogFragment.newInstance();
                dialog.show(getFragmentManager(), "LicensesDialog");
                break;
        }
        return false;
    }
}
