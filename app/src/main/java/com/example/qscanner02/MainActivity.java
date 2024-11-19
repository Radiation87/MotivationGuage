package com.example.qscanner02;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Admin credentials
    private final String adminUsername = "admin";
    private final String adminPassword = "admin123";

    // Collections for storing user data
    private final List<Student> studentList = new ArrayList<>();
    private final List<Announcement> announcements = new ArrayList<>();
    private final Map<String, List<MotivationEntry>> motivationMap = new LinkedHashMap<>(); // Stores each student's motivation entries by username

    private String currentStudentUsername; // Track the currently logged-in student

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_scene1);
    }

    // Inner class to represent a Student
    private static class Student {
        String username;
        String email;
        String password;

        Student(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    // Inner class to represent a Motivation Entry with a value and a date
    private static class MotivationEntry {
        String value;
        String date;

        MotivationEntry(String value) {
            this.value = value;
            this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }

    // Login logic for both students and admin
    public void onClickLogInLogicButton(View view) {
        EditText usernameInput = findViewById(R.id.usernameEditTextS1);
        EditText passwordInput = findViewById(R.id.passwordEditTextS1);

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            // Admin login success
            setContentView(R.layout.admin_navi_scene4);
        } else {
            // Student login validation
            boolean found = false;
            for (Student student : studentList) {
                if (student.username.equals(username) && student.password.equals(password)) {
                    found = true;
                    currentStudentUsername = username; // Store the current student's username
                    setContentView(R.layout.moti_submit_scene2);
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save student information in the student add/remove scene
    public void onClickSaveStudentButton(View view) {
        EditText usernameInput = findViewById(R.id.studentUsernameInput);
        EditText emailInput = findViewById(R.id.studentEmailInput);
        EditText passwordInput = findViewById(R.id.studentPasswordInput);

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            studentList.add(new Student(username, email, password));
            motivationMap.put(username, new ArrayList<>()); // Initialize motivation list for the student
            usernameInput.setText("");
            emailInput.setText("");
            passwordInput.setText("");
            Toast.makeText(this, "Student saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Called when the Remove Student button is pressed
    public void onClickRemoveStudentButton(View view) {
        setContentView(R.layout.student_list_scene7); // Navigate to student list view for removal
        LinearLayout studentListLayout = findViewById(R.id.studentListLayout); // Layout to hold student buttons
        studentListLayout.removeAllViews(); // Clear any existing views

        // Iterate through each student and create a button for removal
        for (Student student : studentList) {
            Button studentButton = new Button(this);
            studentButton.setText(student.username);

            // Set each button to delete the student on click
            studentButton.setOnClickListener(v -> {
                // Remove student from list and associated data
                studentList.removeIf(s -> s.username.equals(student.username));
                motivationMap.remove(student.username);

                // Notify user and refresh list after deletion
                Toast.makeText(this, "Removed student: " + student.username, Toast.LENGTH_SHORT).show();
                onClickRemoveStudentButton(null); // Refresh the student list by calling this function again
            });

            studentListLayout.addView(studentButton); // Add button to layout
        }
    }


    // Motivation submission for the logged-in student, stored with date
    public void onClickSubmitMotivationButton(View view) {
        EditText userMotivationInputField = findViewById(R.id.userMotivationInputField);
        String motivationText = userMotivationInputField.getText().toString().trim();

        if (isValidInput(motivationText) && currentStudentUsername != null) {
            // Add motivation entry with date for the current student
            List<MotivationEntry> motivations = motivationMap.getOrDefault(currentStudentUsername, new ArrayList<>());
            motivations.add(new MotivationEntry(motivationText));
            motivationMap.put(currentStudentUsername, motivations);

            userMotivationInputField.setText("");
            Toast.makeText(this, "Motivation submitted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a valid motivation or log in", Toast.LENGTH_SHORT).show();
        }
    }

    // Announcement creation by admin
    private static class Announcement {
        String content;
        String date;

        Announcement(String content) {
            this.content = content;
            this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }

    public void onClickSubmitAnnouncementButton(View view) {
        EditText announcementInput = findViewById(R.id.announcementSubmissionEditText);
        String announcementText = announcementInput.getText().toString().trim();

        if (!announcementText.isEmpty()) {
            announcements.add(new Announcement(announcementText));
            announcementInput.setText("");
            Toast.makeText(this, "Announcement added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter an announcement", Toast.LENGTH_SHORT).show();
        }
    }

    // View announcements in the announcement list scene
    public void onClickViewAnnouncementsSceneButton(View view) {
        setContentView(R.layout.announcement_list_scene5);

        Button backButton = findViewById(R.id.backButton5to2);
        if (backButton != null) {
            backButton.setVisibility(View.GONE);
            backButton.setEnabled(false);
        }

        TextView announcementListTextView = findViewById(R.id.announcementListTextView);
        StringBuilder announcementText = new StringBuilder("Announcements:\n");
        for (Announcement announcement : announcements) {
            announcementText.append(announcement.date).append(": ").append(announcement.content).append("\n");
        }
        announcementListTextView.setText(announcementText.toString());
    }

    // Display student list dynamically in student_list_scene7
    public void onClickViewStudentsButton(View view) {
        setContentView(R.layout.student_list_scene7);
        LinearLayout studentListLayout = findViewById(R.id.studentListLayout);
        studentListLayout.removeAllViews();

        for (Student student : studentList) {
            Button studentButton = new Button(this);
            studentButton.setText(student.username);
            studentButton.setOnClickListener(v -> showStudentDetails(student.username));
            studentListLayout.addView(studentButton);
        }
    }

    // Display selected student's motivation history in student_details_scene8
    private void showStudentDetails(String studentUsername) {
        setContentView(R.layout.student_details_scene8);
        TextView motivationTextView = findViewById(R.id.motivationListTextView);

        List<MotivationEntry> motivations = motivationMap.get(studentUsername);
        StringBuilder motivationText = new StringBuilder("Motivation History:\n");

        if (motivations != null) {
            for (MotivationEntry entry : motivations) {
                motivationText.append(entry.date).append(": ").append(entry.value).append("\n");
            }
        } else {
            motivationText.append("No records available.");
        }
        motivationTextView.setText(motivationText.toString());
    }

    // Helper methods
    private boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 1 && value <= 5;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Back button navigation from student_details_scene8 to student_list_scene7
    public void onClickBackButton8to7(View view) {
        onClickViewStudentsButton(view); // Navigate back to student list
    }

    public void onClickBackButton5to4(View view) {
        setContentView(R.layout.admin_navi_scene4);
    }

    public void onClickCancelAnnouncementButton(View view) {
        setContentView(R.layout.admin_navi_scene4);
    }

    public void onClickMakeAnnouncementSceneButton(View view) {
        setContentView(R.layout.announcement_maker_scene6);
    }

    public void onClickAddOrRemoveStudentButton(View view) {
        setContentView(R.layout.student_manager_scene9);
    }

    public void onClickBackButton9to4(View view) {
        setContentView(R.layout.admin_navi_scene4);
    }

    public void onClickBackButton4to1(View view) {
        setContentView(R.layout.login_scene1);
    }

    public void onClickBackButton2to1(View view) {
        setContentView(R.layout.login_scene1);
    }

    public void onClickBackButton7to9(View view) {
        setContentView(R.layout.admin_navi_scene4);
    }

    public void onClickAnnouncementsSceneButton(View view) {
        setContentView(R.layout.announcement_list_scene5);

        Button backButton = findViewById(R.id.backButton5to4);
        if (backButton != null) {
            backButton.setVisibility(View.GONE);
            backButton.setEnabled(false);
        }

        TextView announcementListTextView = findViewById(R.id.announcementListTextView);
        StringBuilder announcementText = new StringBuilder("Announcements:\n");
        for (Announcement announcement : announcements) {
            announcementText.append(announcement.date).append(": ").append(announcement.content).append("\n");
        }
        announcementListTextView.setText(announcementText.toString());
    }

    public void onClickBackButton5to2(View view) {
        setContentView(R.layout.moti_submit_scene2);
    }
}
