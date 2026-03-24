package com.example.shadowfox_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    private AppCompatButton btnLogin, btnLoginWithGoogle;
    private EditText edtUsername, edtPassword;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFirebase();
        initBiometric();
        setupLoginButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHome();
        }
    }

    // ─── Initialization ───────────────────────────────────────────────────────

    private void initViews() {
        btnLogin           = findViewById(R.id.btn_login);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        edtUsername        = findViewById(R.id.edt_username);
        edtPassword        = findViewById(R.id.edt_password);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
    }

    private void initBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        showToast("Authentication error: " + errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        navigateToHome(); // Navigate only after biometric succeeds
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        showToast("Authentication failed");
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Confirm your identity to continue")
                .setNegativeButtonText("Use account password")
                .build();
    }

    // ─── Login Logic ──────────────────────────────────────────────────────────

    private void setupLoginButtons() {
        btnLogin.setOnClickListener(view -> attemptEmailLogin());
        btnLoginWithGoogle.setOnClickListener(view -> attemptGoogleLogin());
    }

    private void attemptEmailLogin() {
        String username = edtUsername.getText().toString().toLowerCase().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Enter the username");
            return;
        }

        if (!validatePassword()) return;

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail: success");
                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        showToast("Authentication failed.");
                    }
                });
    }

    private void attemptGoogleLogin() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // false = show all accounts, not just previously signed-in
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)          // false = always show account picker
                .build();

        androidx.credentials.GetCredentialRequest request =
                new androidx.credentials.GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleCredential(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.w(TAG, "Google Sign-In failed", e);
                        showToast("Google Sign-In failed: " + e.getMessage());
                    }
                });
    }

    private void handleGoogleCredential(Credential credential) {
        if (credential instanceof CustomCredential
                && credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            GoogleIdTokenCredential googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.getData());

            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Log.w(TAG, "Unexpected credential type: " + credential.getType());
            showToast("Unexpected credential type.");
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential: success");
                        navigateToHome();
                    } else {
                        Log.w(TAG, "signInWithCredential: failure", task.getException());
                        showToast("Google authentication failed.");
                    }
                });
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    private boolean validatePassword() {
        String password = edtPassword.getText().toString().trim();

        if (password.isEmpty()) {
            edtPassword.setError("Field can't be empty");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            edtPassword.setError("Password too weak (requires 8+ chars, upper, lower, digit, special)");
            return false;
        }

        edtPassword.setError(null);
        return true;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}