package com.civic.connect;

// 3. OrgLoginDto (Data Transfer Object for Mock Authentication)
// This is used by the frontend organization login screen to send credentials.
public record OrgLoginDto(
        String orgId,
        String password
) {}
