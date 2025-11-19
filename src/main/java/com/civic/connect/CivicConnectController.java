package com.civic.connect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CivicConnectController:
 * This layer handles incoming HTTP requests and maps them to the appropriate
 * methods in the CivicConnectService.
 * All endpoints are prefixed with /api/v1
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:8080") // Allows the frontend HTML to access the API
public class CivicConnectController {

    private final CivicConnectService service;

    // Spring automatically injects the service instance
    @Autowired
    public CivicConnectController(CivicConnectService service) {
        this.service = service;
    }

    /**
     * POST /api/v1/complaints/submit
     * Endpoint for citizens to submit a new complaint.
     * @param dto The complaint data from the frontend.
     * @return The fully processed Complaint object including AI results.
     */
    @PostMapping("/complaints/submit")
    public ResponseEntity<Complaint> submitComplaint(@RequestBody ComplaintSubmissionDto dto) {
        if (dto.title() == null || dto.title().isEmpty() || dto.location() == null || dto.location().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Complaint newComplaint = service.processAndSaveComplaint(dto);
        // Returns the created complaint with ID, Priority, and Reward
        return new ResponseEntity<>(newComplaint, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/complaints/dashboard/{userId}
     * Retrieves all complaints for a specific user to display on their dashboard.
     * (Note: This is mock implementation, retrieving ALL complaints for the simplicity of the single-user app)
     * @return A list of all complaints.
     */
    @GetMapping("/complaints/all")
    public List<Complaint> getAllComplaints() {
        // In this mock setup, we return all data for the client-side filtering logic
        return service.getAllComplaints();
    }

    /**
     * POST /api/v1/org/login
     * Mock login for Organization users (e.g., Public Works).
     * @param user The OrgLoginDto containing orgId and password.
     * @return 200 OK with success message or 401 Unauthorized.
     */
    @PostMapping("/org/login")
    public ResponseEntity<Map<String, String>> loginOrg(@RequestBody OrgLoginDto user) {

        // Find if the submitted orgId matches a mocked credential
        Optional<OrgLoginDto> foundUser = service.mockAuthStore.stream()
                .filter(u -> u.orgId().equalsIgnoreCase(user.orgId()))
                .findFirst();

        if (foundUser.isPresent() && foundUser.get().password().equals(user.password())) { // FIXED: Use password()
            // Success: Return the organization context ID
            return ResponseEntity.ok(Map.of("message", "Login successful", "orgId", user.orgId()));
        } else {
            // Failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }

    /**
     * GET /api/v1/org/complaints/{orgId}
     * Retrieves complaints relevant to the logged-in organization.
     * @param orgId The ID of the organization (e.g., 'university' or 'pubworks').
     * @return A list of filtered, prioritized complaints.
     */
    @GetMapping("/org/complaints/{orgId}")
    public List<Complaint> getOrgComplaints(@PathVariable String orgId) {
        return service.getComplaintsByOrganizationId(orgId);
    }

    /**
     * PUT /api/v1/org/status/update
     * Endpoint for organizations to update the status of a complaint.
     * @param updateMap A map containing "id" and "status" fields.
     * @return The updated complaint or 404 Not Found.
     */
    @PutMapping("/org/status/update")
    public ResponseEntity<Complaint> updateComplaintStatus(@RequestBody Map<String, String> updateMap) {
        String id = updateMap.get("id");
        String status = updateMap.get("status");

        if (id == null || status == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Complaint> updated = service.updateComplaintStatus(id, status);

        return updated.map(complaint -> new ResponseEntity<>(complaint, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
