package com.qfix.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qfix.data.model.Complaint;
import com.qfix.data.model.Feedback;
import com.qfix.data.model.Update;
import com.qfix.data.model.User;
import com.qfix.data.repository.ComplaintRepository;

import java.util.List;

public class ComplaintViewModel extends AndroidViewModel {
    private ComplaintRepository complaintRepository;
    private MutableLiveData<List<Complaint>> complaintsLiveData;
    private MutableLiveData<Complaint> complaintLiveData;
    private MutableLiveData<List<Feedback>> feedbackLiveData;
    private MutableLiveData<List<Update>> updatesLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public ComplaintViewModel(Application application) {
        super(application);
        complaintRepository = new ComplaintRepository(application.getApplicationContext());
        complaintsLiveData = new MutableLiveData<>();
        complaintLiveData = new MutableLiveData<>();
        feedbackLiveData = new MutableLiveData<>();
        updatesLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Complaint>> getComplaintsLiveData() {
        return complaintsLiveData;
    }

    public LiveData<Complaint> getComplaintLiveData() {
        return complaintLiveData;
    }

    public LiveData<List<Feedback>> getFeedbackLiveData() {
        return feedbackLiveData;
    }

    public LiveData<List<Update>> getUpdatesLiveData() {
        return updatesLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public boolean createComplaint(Complaint complaint) {
        isLoadingLiveData.setValue(true);
        boolean result = complaintRepository.createComplaint(complaint);
        isLoadingLiveData.setValue(false);
        return result;
    }

    public boolean updateComplaint(Complaint complaint) {
        isLoadingLiveData.setValue(true);
        boolean result = complaintRepository.updateComplaint(complaint);
        isLoadingLiveData.setValue(false);
        return result;
    }

    public void getComplaint(String complaintId) {
        isLoadingLiveData.setValue(true);
        Complaint complaint = complaintRepository.getComplaint(complaintId);
        isLoadingLiveData.setValue(false);
        
        if (complaint != null) {
            complaintLiveData.setValue(complaint);
        } else {
            errorMessageLiveData.setValue("Complaint not found");
        }
    }

    public void getComplaintsByCitizen(String citizenId) {
        isLoadingLiveData.setValue(true);
        List<Complaint> complaints = complaintRepository.getComplaintsByCitizen(citizenId);
        isLoadingLiveData.setValue(false);
        complaintsLiveData.setValue(complaints);
    }

    public void getComplaintsByWard(String ward) {
        isLoadingLiveData.setValue(true);
        List<Complaint> complaints = complaintRepository.getComplaintsByWard(ward);
        isLoadingLiveData.setValue(false);
        complaintsLiveData.setValue(complaints);
    }

    public void getComplaintsForAuthority(User authority) {
        isLoadingLiveData.setValue(true);
        List<Complaint> complaints = complaintRepository.getComplaintsForAuthority(authority);
        isLoadingLiveData.setValue(false);
        complaintsLiveData.setValue(complaints);
    }

    public void getComplaintsByStatus(String status) {
        isLoadingLiveData.setValue(true);
        List<Complaint> complaints = complaintRepository.getComplaintsByStatus(status);
        isLoadingLiveData.setValue(false);
        complaintsLiveData.setValue(complaints);
    }

    public void getPublicComplaints() {
        isLoadingLiveData.setValue(true);
        List<Complaint> complaints = complaintRepository.getPublicComplaints();
        isLoadingLiveData.setValue(false);
        complaintsLiveData.setValue(complaints);
    }

    public boolean addFeedback(Feedback feedback) {
        isLoadingLiveData.setValue(true);
        boolean result = complaintRepository.addFeedback(feedback);
        isLoadingLiveData.setValue(false);
        return result;
    }

    public void getFeedbackForComplaint(String complaintId) {
        isLoadingLiveData.setValue(true);
        List<Feedback> feedback = complaintRepository.getFeedbackForComplaint(complaintId);
        isLoadingLiveData.setValue(false);
        feedbackLiveData.setValue(feedback);
    }

    public boolean addUpdate(Update update) {
        isLoadingLiveData.setValue(true);
        boolean result = complaintRepository.addUpdate(update);
        isLoadingLiveData.setValue(false);
        return result;
    }

    public void getUpdatesForComplaint(String complaintId) {
        isLoadingLiveData.setValue(true);
        List<Update> updates = complaintRepository.getUpdatesForComplaint(complaintId);
        isLoadingLiveData.setValue(false);
        updatesLiveData.setValue(updates);
    }

    public LiveData<List<Complaint>> getComplaints() {
        return complaintsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void refreshComplaints() {
        getPublicComplaints();
    }
}
