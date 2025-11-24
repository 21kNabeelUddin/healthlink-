package HealthLink.HelathLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ZoomService {

    @Value("${zoom.api.key:}")
    private String zoomApiKey;

    @Value("${zoom.api.secret:}")
    private String zoomApiSecret;

    @Value("${zoom.base.url:https://zoom.us/j}")
    private String zoomBaseUrl;

    /**
     * Generate Zoom meeting details for an appointment
     * In production, this would call Zoom API to create actual meetings
     * For now, we generate meeting IDs and URLs
     */
    public ZoomMeetingDetails createMeeting(String patientName, String doctorName, LocalDateTime appointmentDateTime) {
        // Generate a unique meeting ID (in production, this comes from Zoom API)
        String meetingId = generateMeetingId();
        String password = generatePassword();
        
        // Generate meeting URLs
        String meetingUrl = zoomBaseUrl + "/" + meetingId;
        String joinUrl = meetingUrl + "?pwd=" + password;
        
        ZoomMeetingDetails details = new ZoomMeetingDetails();
        details.setMeetingId(meetingId);
        details.setMeetingUrl(meetingUrl);
        details.setPassword(password);
        details.setJoinUrl(joinUrl);
        
        return details;
    }

    /**
     * Generate a Zoom meeting ID (9-11 digits)
     */
    private String generateMeetingId() {
        Random random = new Random();
        // Generate 9-digit meeting ID (Zoom format)
        long meetingId = 100000000L + random.nextInt(900000000);
        return String.valueOf(meetingId);
    }

    /**
     * Generate a meeting password (6 characters)
     */
    private String generatePassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * Inner class to hold Zoom meeting details
     */
    public static class ZoomMeetingDetails {
        private String meetingId;
        private String meetingUrl;
        private String password;
        private String joinUrl;

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getMeetingUrl() {
            return meetingUrl;
        }

        public void setMeetingUrl(String meetingUrl) {
            this.meetingUrl = meetingUrl;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getJoinUrl() {
            return joinUrl;
        }

        public void setJoinUrl(String joinUrl) {
            this.joinUrl = joinUrl;
        }
    }
}

