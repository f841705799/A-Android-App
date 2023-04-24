package a0547110.tees.ac.uk.eatwell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference Logout = findPreference("Logout");
            EditTextPreference name = findPreference("name");
            EditTextPreference phone = findPreference("phone");
            EditTextPreference email = findPreference("email");
            phone.setEnabled(false);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                name.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
                phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
            else {
                name.setText("Not logged in");
                name.setEnabled(false);
                phone.setText("Not logged in");

                email.setText("Not logged in");
                email.setEnabled(false);
            }
            name.setOnPreferenceChangeListener((namepre, newValue) -> {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newValue.toString())
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                }
                return true;
            });
            email.setOnPreferenceChangeListener((emailpre, newValue) -> {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(newValue.toString());
                }
                return true;
            });

            Logout.setOnPreferenceClickListener(preference -> {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    this.requireActivity().finish();
                }
                else {
                    Toast.makeText(this.getContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }
    }
}