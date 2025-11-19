package com.civic.connect;

// 2. ComplaintSubmissionDto (Data Transfer Object for Submission)
// This is used to capture data coming IN from the frontend submission form.
// The fields here match what the JavaScript 'fetch' call sends in its body.
public record ComplaintSubmissionDto(
        String userId,
        String title,
        String location,
        String photoName,
        String organizationContext
) {}
