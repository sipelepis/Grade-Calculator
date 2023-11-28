package grade.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email;
    private TextInputEditText password;
    private AppCompatButton login;
    private AppCompatButton register;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Fetch views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.Login);
        register = findViewById(R.id.Signup);

        mAuth = FirebaseAuth.getInstance();

        // Set click listeners
        login.setOnClickListener(view -> loginUser());
        register.setOnClickListener(view -> openRegisterActivity());
    }
    private void openRegisterActivity() {
        ActivityOptions options = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.slide_top, 0);

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(0, R.anim.slide_top);
    }
    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Validate email and password
        if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email address");
            return;
        }

        if (userPassword.isEmpty() || userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return;
        }
        // Sign in with Firebase
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, R.anim.slide_bottom);
                    finishAffinity();
                    // Proceed with your logic after successful login
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }
}