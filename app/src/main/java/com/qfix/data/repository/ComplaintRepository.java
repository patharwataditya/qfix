package com.qfix.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.qfix.data.local.DatabaseClient;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.Feedback;
import com.qfix.data.model.Update;
import com.qfix.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ComplaintRepository {
    private DatabaseClient databaseClient;

    public ComplaintRepository(Context context) {
        databaseClient = DatabaseClient.getInstance(context);
    }

    public boolean createComplaint(Complaint complaint) {
        try {
            // Generate ID if not set
            if (complaint.getId() == null || complaint.getId().isEmpty()) {
                complaint.setId(UUID.randomUUID().toString());
            }
            String assignedDepartment = resolveDepartmentForCategory(complaint.getCategory());
            complaint.setAssignedDepartment(assignedDepartment);
            complaint.setAssignedAuthorityId(findAssignedAuthorityId(assignedDepartment, complaint.getWard()));
            databaseClient.getAppDatabase().complaintDao().insert(complaint);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateComplaint(Complaint complaint) {
        try {
            databaseClient.getAppDatabase().complaintDao().update(complaint);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Complaint getComplaint(String complaintId) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintByIdSync(complaintId);
    }

    public LiveData<List<Complaint>> getLocalComplaints() {
        return databaseClient.getAppDatabase().complaintDao().getAllComplaints();
    }

    public LiveData<List<Complaint>> getLocalComplaintsByCitizen(String citizenId) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintsByCitizen(citizenId);
    }

    public LiveData<Complaint> getLocalComplaintById(String complaintId) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintById(complaintId);
    }

    public void saveComplaintLocally(Complaint complaint) {
        databaseClient.getAppDatabase().complaintDao().insert(complaint);
    }

    public void saveComplaintsLocally(List<Complaint> complaints) {
        databaseClient.getAppDatabase().complaintDao().insertAll(complaints);
    }

    public List<Complaint> getComplaintsByCitizen(String citizenId) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintsByCitizenSync(citizenId);
    }

    public List<Complaint> getComplaintsByWard(String ward) {
        List<Complaint> allComplaints = databaseClient.getAppDatabase().complaintDao().getAllComplaintsSync();
        if (ward == null || ward.trim().isEmpty()) {
            return allComplaints;
        }

        ArrayList<Complaint> filteredComplaints = new ArrayList<>();
        for (Complaint complaint : allComplaints) {
            if (complaint.getWard() != null && complaint.getWard().equalsIgnoreCase(ward.trim())) {
                filteredComplaints.add(complaint);
            }
        }
        return filteredComplaints;
    }

    public List<Complaint> getComplaintsForAuthority(User authority) {
        List<Complaint> allComplaints = databaseClient.getAppDatabase().complaintDao().getAllComplaintsSync();
        if (authority == null) {
            return allComplaints;
        }

        ArrayList<Complaint> filteredComplaints = new ArrayList<>();
        String authorityId = normalize(authority.getUid());
        String authorityDepartment = normalize(authority.getDepartment());
        String authorityArea = normalize(preferredAuthorityArea(authority));

        for (Complaint complaint : allComplaints) {
            if (complaint == null) {
                continue;
            }

            String assignedAuthorityId = normalize(complaint.getAssignedAuthorityId());
            if (!assignedAuthorityId.isEmpty()) {
                if (assignedAuthorityId.equals(authorityId)) {
                    filteredComplaints.add(complaint);
                }
                continue;
            }

            String complaintDepartment = normalize(complaint.getAssignedDepartment());
            String complaintWard = normalize(complaint.getWard());
            boolean departmentMatches = authorityDepartment.isEmpty()
                    || complaintDepartment.isEmpty()
                    || authorityDepartment.equals(complaintDepartment);
            boolean areaMatches = authorityArea.isEmpty()
                    || complaintWard.isEmpty()
                    || authorityArea.equals(complaintWard);

            if (departmentMatches && areaMatches) {
                filteredComplaints.add(complaint);
            }
        }

        return filteredComplaints;
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintsByStatusSync(status);
    }

    public List<Complaint> getComplaintsByPriority(String priority) {
        return databaseClient.getAppDatabase().complaintDao().getComplaintsByPrioritySync(priority);
    }

    public List<Complaint> getPublicComplaints() {
        // For local implementation, we'll return all complaints
        return databaseClient.getAppDatabase().complaintDao().getAllComplaintsSync();
    }

    public List<Complaint> searchComplaints(String searchText) {
        // Simple text search implementation
        List<Complaint> allComplaints = databaseClient.getAppDatabase().complaintDao().getAllComplaintsSync();
        // In a real implementation, we would filter based on the search text
        return allComplaints;
    }

    public boolean addFeedback(Feedback feedback) {
        try {
            // Generate ID if not set
            if (feedback.getId() == null || feedback.getId().isEmpty()) {
                feedback.setId(UUID.randomUUID().toString());
            }
            databaseClient.getAppDatabase().feedbackDao().insert(feedback);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Feedback> getFeedbackForComplaint(String complaintId) {
        return databaseClient.getAppDatabase().feedbackDao().getFeedbackForComplaint(complaintId);
    }

    public boolean addUpdate(Update update) {
        try {
            // Generate ID if not set
            if (update.getId() == null || update.getId().isEmpty()) {
                update.setId(UUID.randomUUID().toString());
            }
            databaseClient.getAppDatabase().updateDao().insert(update);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Update> getUpdatesForComplaint(String complaintId) {
        return databaseClient.getAppDatabase().updateDao().getUpdatesForComplaint(complaintId);
    }

    private String resolveDepartmentForCategory(String category) {
        String normalizedCategory = normalize(category);
        if (normalizedCategory.contains("fire")) {
            return "Fire Department";
        }
        if (normalizedCategory.contains("water")) {
            return "Water Supply Department";
        }
        if (normalizedCategory.contains("electric")) {
            return "Electricity Department";
        }
        if (normalizedCategory.contains("garbage") || normalizedCategory.contains("drainage")) {
            return "Sanitation Department";
        }
        if (normalizedCategory.contains("park")) {
            return "Parks Department";
        }
        if (normalizedCategory.contains("traffic")) {
            return "Traffic Department";
        }
        return "Roads Department";
    }

    private String findAssignedAuthorityId(String department, String ward) {
        List<User> users = databaseClient.getAppDatabase().userDao().getAllUsers();
        String normalizedDepartment = normalize(department);
        String normalizedWard = normalize(ward);

        for (User user : users) {
            if (user == null || !"authority".equalsIgnoreCase(user.getRole())) {
                continue;
            }

            String userDepartment = normalize(user.getDepartment());
            if (!normalizedDepartment.isEmpty() && !normalizedDepartment.equals(userDepartment)) {
                continue;
            }

            String userArea = normalize(preferredAuthorityArea(user));
            if (!normalizedWard.isEmpty() && !userArea.isEmpty() && !normalizedWard.equals(userArea)) {
                continue;
            }

            return user.getUid();
        }

        return null;
    }

    private String preferredAuthorityArea(User user) {
        if (user == null) {
            return "";
        }
        if (user.getWorkArea() != null && !user.getWorkArea().trim().isEmpty()) {
            return user.getWorkArea();
        }
        return user.getWard();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
