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
        // Step 1: Load all tutors from account.csv (role = "Tutor")
        Map<String, Map<String, String>> accountMap = accountFile.readAll().stream()
                .filter(row -> "Tutor".equals(row.get("Role")))
                .filter(row -> row.get("AccountID") != null)
                .collect(Collectors.toMap(
                        row -> row.get("AccountID"), // Key: TutorID
                        row -> row,
                        (existing, newRow) -> existing // Keep first if duplicates in account.csv
                ));

        // Step 2: Load ALL assignments from tutor.csv (1 row per assignment)
        List<Map<String, String>> allAssignments = tutorFile.readAll().stream()
                .filter(row -> row.get("TutorID") != null) // Skip invalid rows
                .collect(Collectors.toList());

        // Step 3: Create 1 Tutor object per assignment (separate row per assignment)
        List<Tutor> assignmentRows = allAssignments.stream()
                .map(assignment -> {
                    String tutorID = assignment.get("TutorID");
                    Map<String, String> accountData = accountMap.get(tutorID);

                    // Skip if tutor doesn't exist in account.csv (invalid data)
                    if (accountData == null) return null;

                    // Create Tutor for THIS SINGLE ASSIGNMENT
                    return new Tutor(
                            tutorID,
                            accountData.getOrDefault("Name", "Unknown"),
                            accountData.getOrDefault("Password", ""),
                            accountData.getOrDefault("Role", "Tutor"),
                            assignment.getOrDefault("AssignedLevels", "No level"),
                            assignment.getOrDefault("AssignedSubjects", "No subject")
                    );
                })
                .filter(Objects::nonNull) // Remove invalid entries
                .collect(Collectors.toList());

        // Step 4: Add tutors with NO assignments (1 row per tutor)
        Set<String> tutorsWithAssignments = assignmentRows.stream()
                .map(Tutor::getAccountId)
                .collect(Collectors.toSet()); // Track tutors already in assignment rows

        List<Tutor> noAssignmentRows = accountMap.keySet().stream()
                .filter(tutorID -> !tutorsWithAssignments.contains(tutorID)) // Skip tutors with assignments
                .map(tutorID -> {
                    Map<String, String> accountData = accountMap.get(tutorID);
                    return new Tutor(
                            tutorID,
                            accountData.getOrDefault("Name", "Unknown"),
                            accountData.getOrDefault("Password", ""),
                            accountData.getOrDefault("Role", "Tutor"),
                            "No level", // No assignments
                            "No subject" // No assignments
                    );
                })
                .collect(Collectors.toList());

        // Combine assignment rows + no-assignment rows (all as separate rows)
        List<Tutor> allRows = new ArrayList<>();
        allRows.addAll(assignmentRows);
        allRows.addAll(noAssignmentRows);

        return allRows;
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

    public List<Tutor> getTutorName(String name) {
        Map<String, Map<String, String>> acctById = accountFile.readAll().stream()
                .collect(Collectors.toMap(
                        row -> row.get("AccountID"),
                        row -> row
                ));

        return tutorFile.readAll().stream()
                .filter(row -> name.equalsIgnoreCase(row.get("Name")))
                .map(detail -> {
                    String id = detail.get("TutorID");
                    Map<String, String> acct = acctById.get(id);

                    if (acct == null) return null;

                    return new Tutor(
                            id,
                            acct.get("Name"),
                            acct.get("Password"),
                            acct.get("Role"),
                            detail.get("AssignedLevels"),
                            detail.get("AssignedSubjects")
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Tutor> getTutorID(String tutorID) {
        Map<String, Map<String, String>> acctById = accountFile.readAll().stream()
                .collect(Collectors.toMap(
                        row -> row.get("AccountID"),
                        row -> row
                ));

        return tutorFile.readAll().stream()
                .filter(row -> tutorID.equalsIgnoreCase(row.get("TutorID")))
                .map(detail -> {
                    String id = detail.get("TutorID");
                    Map<String, String> acct = acctById.get(id);

                    if (acct == null) return null;

                    return new Tutor(
                            id,
                            acct.get("Name"),
                            acct.get("Password"),
                            acct.get("Role"),
                            detail.get("AssignedLevels"),
                            detail.get("AssignedSubjects")
                    );
                })
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

    public void deleteTutor(String accountID) {
        FileHandler tutorHandler = new FileHandler("tutor.csv", List.of("TutorID", "AssignedSubjects", "AssignedLevels"));
        List<Map<String, String>> tutorRows = tutorHandler.readAll();
        tutorRows.removeIf(row -> accountID.equals(row.get("TutorID")));
        tutorHandler.overwriteAll(tutorRows);
    }

    public void deleteTutorAssignment(String tutorID, String subject, String level) {
        List<Map<String, String>> tutorRows = tutorFile.readAll();

        boolean removed = tutorRows.removeIf(row ->
                tutorID.equals(row.get("TutorID")) &&
                        subject.equals(row.get("AssignedSubjects")) &&
                        level.equals(row.get("AssignedLevels"))
        );

        if (!removed) {
            System.out.println("Warning: Assignment not found for deletion (TutorID: " + tutorID +
                    ", Subject: " + subject + ", Level: " + level + ")");
        }

        tutorFile.overwriteAll(tutorRows);
    }
}