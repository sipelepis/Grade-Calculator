package grade.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText finalGradeGoal;
    private AppCompatButton calculateButton, clearButton, addRowButton;
    private TextView totalPercentage, averageGradeTV;
    private LinearLayout dynamicContainer;
    private List<String[]> valuesList = new ArrayList<>();
    private Integer weightRemTasksInt = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextInputEditText
        finalGradeGoal = findViewById(R.id.FGGEditText);

        // Initialize AppCompatButton
        calculateButton = findViewById(R.id.Calculate);
        clearButton = findViewById(R.id.Clear);
        addRowButton = findViewById(R.id.AddRowButton);

        // Initialize TextView
        totalPercentage = findViewById(R.id.TotalViewTextView);
        averageGradeTV = findViewById(R.id.AverageGradeTextView);

        // Initialize LinearLayout
        dynamicContainer = findViewById(R.id.DynamicContainer);

        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
            }
        });
        // Listener for the calculate button
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEditTextValues()) {
                    try {
                        calculateAverageGrade();
                    } catch (Exception e) {
                        e.printStackTrace(); // Print the error for debugging
                        Toast.makeText(MainActivity.this, "An error occurred during calculation", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid inputs, please fill rows with values", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Listener for the clear button
        clearButton.setOnClickListener(view -> clearAllChildViews());
    }
    //Add View
    private void addView(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the input_layout.xml
        View view = getLayoutInflater().inflate(R.layout.input_layout, null);

        // Assign unique IDs to the views within the inflated layout
        LinearLayout LinearLayout = view.findViewById(R.id.LinearLayout);
        TextInputLayout assignmentExamTIL = view.findViewById(R.id.AssignmentExamTIL);
        TextInputEditText assignmentExamEditTextTemp = view.findViewById(R.id.AssignmentExamEditText);
        TextInputLayout gradeTIL = view.findViewById(R.id.GradeTIL);
        TextInputEditText gradeEditTextTemp  = view.findViewById(R.id.GradeEditText);
        TextInputLayout weightTIL = view.findViewById(R.id.WeightTIL);
        TextInputEditText weightEditTextTemp  = view.findViewById(R.id.WeightEditText);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    int inputVal = Integer.parseInt(dest.toString() + source.toString());
                    if (inputVal <= weightRemTasksInt) {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return ""; // Reject input
            }
        };

        weightEditTextTemp.setFilters(filterArray);

        gradeEditTextTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start have just replaced old text that had length before.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
                String input = s.toString();
                if (!input.matches("^([0-9]{1,2}(\\.[0-9]{1,2})?%?$|[0-9]{3}%?$|[A-F][+-]?$|\\+|-)$")) {
                    // If the input doesn't match the regex, clear it
                    s.clear();
                } else if (input.matches("\\d{3}") && !input.equals("100")) {
                    // If the input is a three-digit number that's not 100, clear it
                    s.clear();
                }
            }
        });
        // Generate unique IDs
        int uniqueId1 = View.generateViewId();
        LinearLayout.setPadding(0, 10, 0, 10);
        LinearLayout.setId(uniqueId1);

        dynamicContainer.addView(view);
    }
    // Update and View the weight remaining tasks

    // Validate the weight remaining tasks and EditText values
    private boolean isValidGrade(){
        try {

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean validateEditTextValues() {
        try {
            LinearLayout dynamicContainerInner = findViewById(R.id.DynamicContainer);
            int childCount = dynamicContainerInner.getChildCount();
            // If there are no child views, return immediately
            if (childCount == 0) {
                return false;
            }
            for (int i = 0; i < childCount; i++) {
                View childView = dynamicContainerInner.getChildAt(i);
                TextInputEditText assignmentExamEditTextChild = childView.findViewById(R.id.AssignmentExamEditText);
                TextInputEditText gradeEditTextChild = childView.findViewById(R.id.GradeEditText);
                TextInputEditText weightEditTextChild = childView.findViewById(R.id.WeightEditText);
                if (assignmentExamEditTextChild != null || gradeEditTextChild != null || weightEditTextChild != null) {
                    if ( assignmentExamEditTextChild.getText().toString().isEmpty() ||
                            gradeEditTextChild.getText().toString().isEmpty() ||
                            weightEditTextChild.getText().toString().isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean isTotalWeight100() {
        try {
            double totalWeight = 0;
            for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
                View rowView = dynamicContainer.getChildAt(i);
                TextInputEditText weightEditText = rowView.findViewById(R.id.WeightEditText);
                double weight = Double.parseDouble(weightEditText.getText().toString());
                totalWeight += weight;
            }
            return totalWeight == 100;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Calculate the total percentage and average grade
    private String getGradeAndGPAFromPercentage(double percentage) {
        String grade;
        double gpa;
        if (percentage >= 97) {
            grade = "A+";
        } else if (percentage >= 93) {
            grade = "A";
        } else if (percentage >= 90) {
            grade = "A-";
        } else if (percentage >= 87) {
            grade = "B+";
        } else if (percentage >= 83) {
            grade = "B";
        } else if (percentage >= 80) {
            grade = "B-";
        } else if (percentage >= 77) {
            grade = "C+";
        } else if (percentage >= 73) {
            grade = "C";
        } else if (percentage >= 70) {
            grade = "C-";
        } else if (percentage >= 67) {
            grade = "D+";
        } else if (percentage >= 63) {
            grade = "D";
        } else if (percentage >= 60) {
            grade = "D-";
        } else {
            grade = "F";
        }

        gpa = (percentage / 25) - 1;
        return String.format("%s (%.2f)", grade, Math.max(0, gpa)); // GPA can't be less than 0
    }
    private double getMinPercentage(String grade) {
        String percentage;

        switch (grade) {
            case "A+":
                percentage = "97-100%";
                break;
            case "A":
                percentage = "93-96%";
                break;
            case "A-":
                percentage = "90-92%";
                break;
            case "B+":
                percentage = "87-89%";
                break;
            case "B":
                percentage = "83-86%";
                break;
            case "B-":
                percentage = "80-82%";
                break;
            case "C+":
                percentage = "77-79%";
                break;
            case "C":
                percentage = "73-76%";
                break;
            case "C-":
                percentage = "70-72%";
                break;
            case "D+":
                percentage = "67-69%";
                break;
            case "D":
                percentage = "63-66%";
                break;
            case "D-":
                percentage = "60-62%";
                break;
            case "F":
                percentage = "0-59%";
                break;
            default:
                percentage = "0-0%";
                break;
        }

        String[] percentages = percentage.split("-");
        return Double.parseDouble(percentages[0]);
    }
    public void calculateAverageGrade() {
        deleteEmptyChildViews();
        if(isTotalWeight100()){
            try{
                double total = 0;
                double totalWeight = 100;

                int childCount = dynamicContainer.getChildCount();
                // If there are no child views, return immediately
                if (childCount == 0) {
                    return;
                }
                for (int i = 0; i < childCount; i++) {
                    View rowView = dynamicContainer.getChildAt(i);

                    TextInputEditText gradeEditText = rowView.findViewById(R.id.GradeEditText);
                    TextInputEditText weightEditText = rowView.findViewById(R.id.WeightEditText);
                    double grade;
                    try{
                        grade = Double.parseDouble(gradeEditText.getText().toString());
                    }catch (Exception e){
                        grade = getMinPercentage(gradeEditText.getText().toString());
                        e.printStackTrace();
                    }
                    double weight = Double.parseDouble(weightEditText.getText().toString());
                    double weightInit = weight / 100;
                    total += grade * weightInit;
                }
                totalPercentage.setText(getGradeAndGPAFromPercentage(total));
                averageGradeTV.setText(String.valueOf(total));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(MainActivity.this, "Weight must be equal to 100 to for calculation to work.", Toast.LENGTH_SHORT).show();
        }
    }
    // Delete empty child views
    private void clearAllChildViews() {
        try {
            int childCount = dynamicContainer.getChildCount();
            // If there are no child views, return immediately
            if (childCount == 0) {
                return;
            }
            // Iterate in reverse order
            for (int i = 0; i < childCount; i++) {
                View childView = dynamicContainer.getChildAt(i);
                dynamicContainer.removeView(childView);
                i--;
                childCount--;
            }
            averageGradeTV.setText("");
            totalPercentage.setText("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void deleteEmptyChildViews() {
        try {
            int childCount = dynamicContainer.getChildCount();
            // If there are no child views, return immediately
            if (childCount == 0) {
                return;
            }
            // Iterate in reverse order
            for (int i = 0; i < childCount; i++) {

                View childView = dynamicContainer.getChildAt(i);
                TextInputEditText assignmentExamEditTextChild = childView.findViewById(R.id.AssignmentExamEditText);
                TextInputEditText gradeEditTextChild = childView.findViewById(R.id.GradeEditText);
                TextInputEditText weightEditTextChild = childView.findViewById(R.id.WeightEditText);
                if (assignmentExamEditTextChild != null || gradeEditTextChild != null || weightEditTextChild != null) {
                    String assignmentExamValue = assignmentExamEditTextChild.getText().toString();
                    String gradeValue = gradeEditTextChild.getText().toString();
                    String weightValue = weightEditTextChild.getText().toString();
                    if (assignmentExamValue.isEmpty() || gradeValue.isEmpty() || weightValue.isEmpty()) {
                        dynamicContainer.removeView(childView);
                        i--;
                        childCount--;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}