package org.tcms.service;

import org.tcms.model.Payment;
import org.tcms.model.Request;
import org.tcms.utils.FileHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestService {
    private final FileHandler requestFile;

    public RequestService() throws IOException {
        requestFile = new FileHandler("request.csv", Arrays.asList("RequestID","StudentID","OldClassID","NewClassID","Reason"));
    }

    public List<Request> getAllRequest() {
        return requestFile.readAll().stream()
                .map(row -> new Request(
                        row.get("RequestID"),
                        row.get("StudentID"),
                        row.get("OldClassID"),
                        row.get("NewClassID"),
                        row.get("Reason")
                ))
                .collect(Collectors.toList());
    }

    public void addRequest(Request request) {
        requestFile.append(Map.of(
                "RequestID", request.getRequestID(),
                "StudentID", request.getStudentID(),
                "OldClassID", request.getOldClassID(),
                "NewClassID", request.getNewClassID(),
                "Reason", request.getReason()
        ));
    }

    public void deleteRequest(String requestID) {
        List<Map<String, String>> rows = requestFile.readAll();
        rows.removeIf(row -> requestID.equals(row.get("RequestID")));
        requestFile.overwriteAll(rows);
    }
}
