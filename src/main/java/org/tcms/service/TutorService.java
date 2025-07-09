package org.tcms.service;

import org.tcms.model.Tutor;
import org.tcms.model.User;
import org.tcms.utils.FileHandler;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TutorService extends UserService {
    private final FileHandler tutorFile;

    public TutorService() throws IOException {
        super(); // Inherit account.csv handling from UserService
        tutorFile = new FileHandler("tutor.csv", Arrays.asList("TutorID", "AssignedSubjects", "AssignedLevels"));
    }

    
    public List<Tutor> getAllTutor() {
        try {
            // Step 1: Load all valid tutors from account.csv (role = "Tutor")
            Map<String, Map<String, String>> accountMap = accountFile.readAll().stream()
                    .filter(row -> "Tutor".equals(row.get("Role")))
                    .filter(row -> row.get("AccountID") != null)
                    .collect(Collectors.toMap(
                            row -> row.get("AccountID"), // Key: TutorID
                            row -> row,
                            (existing, newRow) -> existing // Keep first duplicate
                    ));

            // Step 2: Load ALL entries from tutor.csv (including "NO TUTOR")
            Map<String, List<Map<String, String>>> tutorAssignments = tutorFile.readAll().stream()
                    .filter(row -> row.get("TutorID") != null) // Keep entries with TutorID (even "NO TUTOR")
                    .collect(Collectors.groupingBy(row -> row.get("TutorID")));

            // Step 3: Merge data, including "NO TUTOR" entries
            return tutorAssignments.entrySet().stream()
                    .flatMap(entry -> {
                        String tutorID = entry.getKey();
                        List<Map<String, String>> assignments = entry.getValue();

                        // Get account data (or use "NO TUTOR" if missing)
                        Map<String, String> accountData = accountMap.get(tutorID);
                        String username = (accountData != null) ?
                                accountData.getOrDefault("Name", "Unknown") :
                                "NO TUTOR"; // For "NO TUTOR" or invalid IDs

                        // Create a Tutor object for each assignment
                        return assignments.stream()
                                .map(assignment -> new Tutor(
                                        tutorID,
                                        username, // "NO TUTOR" if no account
                                        "", // Password irrelevant for display
                                        (accountData != null) ? accountData.get("Role") : "N/A",
                                        assignment.getOrDefault("AssignedLevels", "No level"),
                                        assignment.getOrDefault("AssignedSubjects", "No subject")
                                ));
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load tutor data", e);
        }
    }

    // Only gets Tutor data from account.csv
    public List<Tutor> getAllTutorsFromAccount() {
        List<User> allUsers = getAllUsers();

        return allUsers.stream()
                .filter(user -> "Tutor".equals(user.getRole()))
                .map(user -> new Tutor(
                        user.getAccountId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole(),
                        "",
                        ""
                ))
                .collect(Collectors.toList());
    }


    // Appends data in both account.csv and tutor.csv
    public void addTutor(Tutor tutor) {
        addUser(tutor);
        tutorFile.append(Map.of(
                "TutorID", tutor.getAccountId(),
                "AssignedSubjects", tutor.getAssignedSubject(),
                "AssignedLevels", tutor.getAssignedLevel()
        ));
    }

    // Only appends new line of data in tutor.csv
    public void addTutorToSubject(Tutor tutor) {
        tutorFile.append(Map.of(
                "TutorID", tutor.getAccountId(),
                "AssignedSubjects", tutor.getAssignedSubject(),
                "AssignedLevels", tutor.getAssignedLevel()
        ));
    }


    // In TutorService.java
    public void reassignTutor(String oldTutorID, String newTutorID, String subject, String level) {
        try {
            // Step 1: Update tutor.csv (remove subject from old tutor, add to new tutor)
            FileHandler tutorHandler = new FileHandler("tutor.csv", List.of("TutorID", "AssignedSubjects", "AssignedLevels"));
            List<Map<String, String>> tutorRows = tutorHandler.readAll();

            // Remove the subject from the old tutor
            tutorRows.removeIf(row ->
                    oldTutorID.equals(row.get("TutorID")) &&
                            subject.equals(row.get("AssignedSubjects")) &&
                            level.equals(row.get("AssignedLevels"))
            );

            // Add the subject to the new tutor (if not already assigned)
            boolean newTutorAlreadyHasSubject = tutorRows.stream()
                    .anyMatch(row ->
                            newTutorID.equals(row.get("TutorID")) &&
                                    subject.equals(row.get("AssignedSubjects")) &&
                                    level.equals(row.get("AssignedLevels"))
                    );

            if (!newTutorAlreadyHasSubject) {
                tutorRows.add(Map.of(
                        "TutorID", newTutorID,
                        "AssignedSubjects", subject,
                        "AssignedLevels", level
                ));
            }

            tutorHandler.overwriteAll(tutorRows);

            // Step 2: Update tuitionclass.csv (replace old tutor with new tutor)
            FileHandler classHandler = new FileHandler("tuitionclass.csv", List.of(
                    "ClassID", "TutorID", "SubjectName", "Information", "Charges", "Schedule", "Level"
            ));
            List<Map<String, String>> classRows = classHandler.readAll();

            classRows.forEach(row -> {
                if (oldTutorID.equals(row.get("TutorID")) &&
                        subject.equals(row.get("SubjectName")) &&
                        level.equals(row.get("Level"))) {
                    row.put("TutorID", newTutorID);
                }
            });

            classHandler.overwriteAll(classRows);

        } catch (Exception e) {
            throw new RuntimeException("Failed to reassign tutor", e);
        }
    }

    // Replace tutor ID with "NO TUTOR" in tutor.csv (keep assignment rows)
    public void markTutorAsDeletedInTutorCSV(String oldTutorID) {
        FileHandler tutorHandler = new FileHandler("tutor.csv",
                List.of("TutorID", "AssignedSubjects", "AssignedLevels"));

        List<Map<String, String>> tutorRows = tutorHandler.readAll();

        // Update rows with the deleted tutor's ID to "NO TUTOR"
        tutorRows.forEach(row -> {
            if (oldTutorID.equals(row.get("TutorID"))) {
                row.put("TutorID", "NO TUTOR"); // Replace ID
            }
        });

        tutorHandler.overwriteAll(tutorRows);
    }

    // Replace tutor ID with "NO TUTOR" in tuitionclass.csv
    public void markTutorAsDeletedInClassesCSV(String oldTutorID) {
        FileHandler classHandler = new FileHandler("tuitionclass.csv",
                List.of("ClassID", "TutorID", "SubjectName", "Information", "Charges", "Schedule", "Level"));

        List<Map<String, String>> classRows = classHandler.readAll();

        // Update classes taught by the deleted tutor
        classRows.forEach(row -> {
            if (oldTutorID.equals(row.get("TutorID"))) {
                row.put("TutorID", "NO TUTOR"); // Replace ID
            }
        });

        classHandler.overwriteAll(classRows);
    }

    public void deleteTutorAssignment(String tutorID, String subject, String level) {
        // Deletes tutor assignment from tutor.csv
        FileHandler tutorHandler = new FileHandler("tutor.csv",
                List.of("TutorID", "AssignedSubjects", "AssignedLevels"));

        List<Map<String, String>> tutorRows = tutorHandler.readAll();
        tutorRows.removeIf(row ->
                tutorID.equals(row.get("TutorID")) &&
                        subject.equals(row.get("AssignedSubjects")) &&
                        level.equals(row.get("AssignedLevels"))
        );
        tutorHandler.overwriteAll(tutorRows);

        // Deletes the class sessions in tuitionclass.csv
        FileHandler classHandler = new FileHandler("tuitionclass.csv",
                List.of("ClassID", "TutorID", "SubjectName", "Information", "Charges", "Schedule", "Level"));

        List<Map<String, String>> classRows = classHandler.readAll();
        classRows.removeIf(row ->
                tutorID.equals(row.get("TutorID")) &&
                        subject.equals(row.get("SubjectName")) &&
                        level.equals(row.get("Level"))
        );
        classHandler.overwriteAll(classRows);
    }
}