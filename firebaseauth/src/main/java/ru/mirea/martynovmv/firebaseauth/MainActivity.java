package ru.mirea.martynovmv.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.martynovmv.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;
    private String email;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.textEmailField.getText().toString();
                password= binding.textPasswordField.getText().toString();
                Log.d(MainActivity.class.getSimpleName(), "Password " + password);
                signIn(email, password);
                updateUI(mAuth.getCurrentUser());
            }
        });
        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.textEmailField.getText().toString();
                password= binding.textPasswordField.getText().toString();
                createAccount(email, password);
//                updateUI(mAuth.getCurrentUser());
            }
        });
        binding.btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
                updateUI(mAuth.getCurrentUser());
            }
        });
        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                updateUI(mAuth.getCurrentUser());
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void sendEmailVerification() {
        binding.btnVerifyEmail.setEnabled(false);
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override

                    public void onComplete(@NonNull Task<Void> task) {

                        binding.btnVerifyEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null)
        {
            binding.statusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            binding.textviewUID.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            binding.btnSignIn.setVisibility(View.GONE);
            binding.textEmailField.setVisibility(View.GONE);
            binding.textPasswordField.setVisibility(View.GONE);
            binding.btnCreateAccount.setVisibility(View.GONE);
            binding.btnVerifyEmail.setEnabled(!user.isEmailVerified());
            binding.btnSignOut.setVisibility(View.VISIBLE);
            binding.btnVerifyEmail.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.statusTextView.setText(R.string.signed_out);
            binding.textviewUID.setText(null);
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.btnCreateAccount.setVisibility(View.VISIBLE);
            binding.textEmailField.setVisibility(View.VISIBLE);
            binding.textPasswordField.setVisibility(View.VISIBLE);
            binding.btnSignOut.setVisibility(View.GONE);
            binding.btnVerifyEmail.setVisibility(View.GONE);
        }
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email + " " + password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful())
                        {
                            binding.statusTextView.setText(R.string.auth_failed);
                        }
                    }
                });

    }
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }
}