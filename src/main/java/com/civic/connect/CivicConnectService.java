package com.civic.connect;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * The Service Layer handles all core business logic, data manipulation,
 * and simulated feature implementation (AI, Priority, Rewards, Routing).
 */
@Service
public class CivicConnectService {

    // Simulates an in-memory database table for Complaint records.
    private final List<Complaint> complaints = new ArrayList<>();

    // Mock constants for AI/Priority logic
    private static final int AI_MIN_PRIORITY = 40;
    private static final int AI_MAX_PRIORITY = 90;

    // Mock store for organization authentication (ID and Password)
    public final List<OrgLoginDto> mockAuthStore = List.of(
            new OrgLoginDto("university", "student"),
            new OrgLoginDto("pubworks", "password")
    );


    public CivicConnectService() {
        // Initialize with mock data for testing filtering and dashboard view
        complaints.add(new Complaint(
                "MOCK-POTH-01", "CITIZEN-001", "Major Pothole", "123 Main St", "road.jpg", "City-wide Issues",
                "Urgent Infrastructure", "Public Works", 85, 30, "#4f46e5", "In Progress", LocalDateTime.now().minusHours(2)
        ));
        complaints.add(new Complaint(
                "MOCK-UNI-02", "STUDENT-003", "Broken light in Block 3", "University Block 3", "light.jpg", "University Issues",
                "Campus Safety", "University Maintenance", 65, 15, "#9333ea", "Submitted", LocalDateTime.now()
        ));
    }

    /**
     * Handles the core logic for processing a new complaint submission.
     * @param dto The data from the frontend.
     * @return The final, processed Complaint entity.
     */
    public Complaint processAndSaveComplaint(ComplaintSubmissionDto dto) {

        // --- 1. Simulated AI Classification & Smart Routing (Features 1 & 3) ---
        String department;
        String issueType;
        // Simple logic based on the user-selected context
        if (dto.organizationContext().equalsIgnoreCase("University Issues")) {
            department = "University Maintenance";
            issueType = dto.title().contains("light") ? "Campus Lighting" : "General Campus Issue";
        } else {
            department = "Public Works";
            issueType = dto.title().contains("Pothole") ? "Road Hazard" : "City Infrastructure";
        }

        // --- 2. Simulated Priority Scoring (Feature 2: Priority Score System) ---
        int priority = (int) (Math.random() * (AI_MAX_PRIORITY - AI_MIN_PRIORITY) + AI_MIN_PRIORITY);

        // --- 3. Reward Calculation (Feature 4: Reward and Recognition System) ---
        int reward = priority >= 80 ? 30 : priority >= 60 ? 15 : 5;
        String deptColor = department.contains("University") ? "#9333ea" : "#4f46e5"; // Color for UI

        // --- 4. Create and Save the final entity ---
        Complaint newComplaint = new Complaint(
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(), // Unique ID
                dto.userId(), dto.title(), dto.location(), dto.photoName(), dto.organizationContext(),
                issueType, department, priority, reward, deptColor, "Submitted", LocalDateTime.now()
        );

        // Add the new complaint to the in-memory list
        this.complaints.add(0, newComplaint);
        return newComplaint;
    }

    /**
     * Retrieves all stored complaints (used by the Citizen Dashboard /api/v1/complaints/all).
     * <--- THIS IS THE MISSING METHOD THAT FIXES THE ERROR --->
     */
    public List<Complaint> getAllComplaints() {
        return this.complaints;
    }

    /**
     * Retrieves and filters complaints relevant to a specific organization.
     */
    public List<Complaint> getComplaintsByOrganizationId(String orgId) {
        String filterContext = orgId.equalsIgnoreCase("university") ? "University Issues" : "City-wide Issues";

        return complaints.stream()
                .filter(c -> c.organizationContext().equalsIgnoreCase(filterContext))
                .sorted((a, b) -> Integer.compare(b.priorityScore(), a.priorityScore())) // Sort by priority (highest first)
                .collect(Collectors.toList());
    }

    /**
     * Handles status updates for the Complaint Tracking via Chatbot/Org Dashboard (Feature 5)
     */
    public Optional<Complaint> updateComplaintStatus(String id, String newStatus) {
        for (int i = 0; i < complaints.size(); i++) {
            Complaint current = complaints.get(i);
            if (current.id().equals(id)) {
                // Create a new updated record
                Complaint updated = new Complaint(
                        current.id(), current.userId(), current.title(), current.location(),
                        current.photoName(), current.organizationContext(), current.type(),
                        current.department(), current.priorityScore(), current.rewardValue(),
                        current.deptColor(), newStatus, current.submittedAt()
                );
                // Replace the old record with the new, updated one
                complaints.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }
}

