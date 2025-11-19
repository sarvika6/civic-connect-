package com.civic.connect;

import java.time.LocalDateTime;

/**
 * --- Complaint Data Structures ---
 * * This file contains the records (Java 17+) used for data transfer and internal
 * representation of a complaint.
 */

// 1. Complaint Record (The internal, final representation of a complaint)
// This is the model stored and manipulated by the CivicConnectService.
public record Complaint(
        String id, // Unique complaint ID (e.g., MOCK-POTH-01)
        String userId,
        String title,
        String location,
        String photoName, // Mock file name from user input
        String organizationContext, // e.g., "City-wide Issues" or "University Issues"
        String type, // AI-detected issue type (e.g., "Road Hazard")
        String department, // Smart-routed department (e.g., "Public Works")
        int priorityScore, // AI-assigned priority (40-90 range)
        int rewardValue, // Points earned by user
        String deptColor, // Color code for UI display
        String status, // Current status (Submitted, In Progress, Resolved)
        LocalDateTime submittedAt
) {}

