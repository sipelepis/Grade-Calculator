package grade.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private TextInputEditText monthEditText;
    private TextInputEditText dayEditText;
    private TextInputEditText yearEditText;

    private AppCompatButton login;
    private AppCompatButton register;
    private FirebaseAuth mAuth;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private boolean isValid = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Change color of FAB
        fab = findViewById(R.id.fab);
        Drawable myDrawable = fab.getDrawable(); // Replace with your drawable
        myDrawable.setTint(getResources().getColor(R.color.green)); // Replace with your color
        fab.setImageDrawable(myDrawable);

        // EditTexts for Name, Email, Password
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        // TextWatcher for Name, Email, Password
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isNameValid(s.toString())) {
                    isValid = false;
                    nameEditText.setError("Invalid name");
                } else {
                    isValid = true;
                    nameEditText.setError(null);
                }
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEmailValid(s.toString())) {
                    isValid = false;
                    emailEditText.setError("Invalid email address");
                } else {
                    isValid = true;
                    emailEditText.setError(null);
                }
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isPasswordValid(s.toString())) {
                    isValid = false;
                    passwordEditText.setError("Password must be at least 6 characters");
                } else {
                    isValid = true;
                    passwordEditText.setError(null);
                }
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // EditTexts for date of birth
        monthEditText = findViewById(R.id.monthDOB);
        dayEditText = findViewById(R.id.dayDOB);
        yearEditText = findViewById(R.id.yearDOB);

        // Set max length of EditTexts
        monthEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        dayEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        yearEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        // Month Validation

        monthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int month = Integer.parseInt(s.toString());
                    if (month < 1 || month > 12) {
                        isValid = false;
                        monthEditText.setError("Month must be between 1 and 12");
                    } else {
                        isValid = true;
                        monthEditText.setError(null);
                    }
                }
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Day validation
        dayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int day = Integer.parseInt(s.toString());
                    int maxDays = getMaxDaysForMonth(monthEditText.getText().toString());
                    if (day < 1 || day > maxDays) {
                        isValid = false;
                        dayEditText.setError("Day must be between 1 and " + maxDays);
                    } else {
                        isValid = true;
                        dayEditText.setError(null);
                    }
                }
                updateButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Year validation
        yearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int year = Integer.parseInt(s.toString());
                    if (year < 1940 || year > 2005) {
                        isValid = false;
                        yearEditText.setError("Year must be between 1940 and 2005");
                    } else {
                        isValid = true;
                        yearEditText.setError(null);
                    }
                    updateButtonState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Buttons
        login = findViewById(R.id.LoginHereButton);
        register = findViewById(R.id.Signup);

        // Set click listeners
        login.setOnClickListener(view -> openLoginActivity());
        fab.setOnClickListener(view -> goBack());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid && areInputsValid()) {
                    registerUser(emailEditText.getText().toString(), passwordEditText.getText().toString(), parsetoDate());
                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid inputs, please check again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Parse data to date
    private Date parsetoDate(){
        int month = Integer.parseInt(monthEditText.getText().toString());
        int day = Integer.parseInt(dayEditText.getText().toString());
        int year = Integer.parseInt(yearEditText.getText().toString());
        String dateString = String.format("%02d/%02d/%04d", month, day, year);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // Validators

    private boolean areInputsValid() {
        return isNameValid(nameEditText.getText().toString()) &&
                isEmailValid(emailEditText.getText().toString()) &&
                isPasswordValid(passwordEditText.getText().toString()) &&
                !monthEditText.getText().toString().isEmpty() &&
                !dayEditText.getText().toString().isEmpty() &&
                !yearEditText.getText().toString().isEmpty();
    }
    private int getMaxDaysForMonth(String monthString) {
        int month = Integer.parseInt(monthString);
        switch (month) {
            case 2:
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }
    private boolean isNameValid(String name) {
        String nameRegex = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";
        return name.matches(nameRegex);
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
    // Firebase methods
    private void addUserDataToFirestore(String name, String email, Date date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("dateOfBirth", date);
            db.collection("users").document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "User data added to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error adding user data to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Methods for buttons
    private void updateButtonState() {
        register.setEnabled(isValid);
    }
    private void openLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, R.anim.slide_bottom);
        finishAffinity();
    }
    private void goBack(){
        overridePendingTransition(0, R.anim.slide_bottom);
        finish();
    }
    private void registerUser(String email, String password, Date date){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        addUserDataToFirestore(nameEditText.getText().toString(), email, date);
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.slide_bottom);
                        finishAffinity();
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Account creation failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}