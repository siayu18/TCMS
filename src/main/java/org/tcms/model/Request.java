package org.tcms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Request {
    private String requestID;
    private String studentID;
    private String oldClassID;
    private String newClassID;
    private String reason;
}
