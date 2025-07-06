package org.tcms.service;

import org.tcms.model.Tutor;
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
        Map<String, Map<String,String>> acctById = accountFile.readAll().stream()
                .collect(Collectors.toMap(
                        row -> row.get("AccountID"),
                        row -> row
                ));

        return tutorFile.readAll().stream()
                .map(detail -> {
                    String id = detail.get("TutorID");
                    Map<String,String> acct = acctById.get(id);

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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void addTutor(Tutor tutor) {
        // Add to account.csv using UserService
        addUser(tutor);

        // Add to tutor.csv
        tutorFile.append(Map.of(
                "TutorID", tutor.getAccountId(),
                "AssignedSubjects", String.join(",", tutor.getAssignedSubject()),
                "AssignedLevels", String.join(",", tutor.getAssignedLevel())
        ));
    }

    public void updateTutor(Tutor tutor) {
        List<Map<String, String>> rows = tutorFile.readAll();

        for (Map<String, String> row : rows) {
            if (tutor.getAccountId().equals(row.get("tutorID"))) {
                row.put("assignedSubjects", String.join(",", tutor.getAssignedSubject()));
                row.put("assignedLevels", String.join(",", tutor.getAssignedLevel()));
                break;
            }
        }
        tutorFile.overwriteAll(rows);
    }

    public void deleteTutor(String tutorId) {
        List<Map<String, String>> rows = tutorFile.readAll();
        rows.removeIf(row -> tutorId.equals(row.get("tutorID")));
        tutorFile.overwriteAll(rows);
        // Also deletes from account.csv via UserService.deleteUser()
        deleteUser(tutorId);
    }
}