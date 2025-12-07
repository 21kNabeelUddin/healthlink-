package com.healthlink.infrastructure.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Zoom API Service for creating and managing Zoom meetings
 * Uses Zoom REST API v2 with OAuth 2.0 Server-to-Server authentication
 */
@Service
@Slf4j
public class ZoomApiService {

    private final RestTemplate restTemplate;

    @Value("${healthlink.zoom.account-id:}")
    private String accountId;

    @Value("${healthlink.zoom.client-id:}")
    private String clientId;

    @Value("${healthlink.zoom.client-secret:}")
    private String clientSecret;

    @Value("${healthlink.zoom.enabled:false}")
    private boolean zoomEnabled;

    private static final String ZOOM_TOKEN_URL = "https://zoom.us/oauth/token";
    private static final String ZOOM_API_BASE_URL = "https://api.zoom.us/v2";
    private static final String ZOOM_MEETING_TYPE_SCHEDULED = "2";

    public ZoomApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Create a Zoom meeting for an appointment
     */
    public ZoomMeetingResponse createMeeting(CreateZoomMeetingRequest request) {
        if (!zoomEnabled) {
            log.warn("Zoom integration is disabled. Skipping meeting creation.");
            return null;
        }

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("Failed to obtain Zoom access token");
                return null;
            }

            // Convert LocalDateTime to Zoom's expected format (UTC)
            ZonedDateTime startTime = request.getStartTime()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneId.of("UTC"));

            int durationMinutes = request.getDurationMinutes() != null 
                    ? request.getDurationMinutes() 
                    : 30;

            // Build meeting request
            Map<String, Object> meetingRequest = new HashMap<>();
            meetingRequest.put("topic", request.getTopic());
            meetingRequest.put("type", ZOOM_MEETING_TYPE_SCHEDULED);
            meetingRequest.put("start_time", startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            meetingRequest.put("duration", durationMinutes);
            meetingRequest.put("timezone", "UTC");
            meetingRequest.put("password", generateMeetingPassword());
            meetingRequest.put("settings", Map.of(
                    "host_video", true,
                    "participant_video", true,
                    "join_before_host", false,
                    "mute_upon_entry", false,
                    "waiting_room", false,
                    "auto_recording", "none",
                    "approval_type", 0 // Automatically approve
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(meetingRequest, headers);

            String url = ZOOM_API_BASE_URL + "/users/me/meetings";
            ResponseEntity<ZoomMeetingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ZoomMeetingResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ZoomMeetingResponse meeting = response.getBody();
                log.info("Zoom meeting created successfully: {}", meeting.getId());
                return meeting;
            } else {
                log.error("Failed to create Zoom meeting. Status: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("Error creating Zoom meeting", e);
            return null;
        }
    }

    /**
     * Get OAuth 2.0 access token using Server-to-Server OAuth
     */
    private String getAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // Basic Auth: Base64(clientId:clientSecret)
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            headers.set("Authorization", "Basic " + encodedCredentials);

            String requestBody = "grant_type=account_credentials&account_id=" + accountId;

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ZoomTokenResponse> response = restTemplate.exchange(
                    ZOOM_TOKEN_URL,
                    HttpMethod.POST,
                    entity,
                    ZoomTokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getAccessToken();
            } else {
                log.error("Failed to get Zoom access token. Status: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("Error obtaining Zoom access token", e);
            return null;
        }
    }

    /**
     * Generate a random meeting password (6-10 alphanumeric characters)
     */
    private String generateMeetingPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Exclude ambiguous characters
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }

    /**
     * Delete a Zoom meeting
     */
    public boolean deleteMeeting(String meetingId) {
        if (!zoomEnabled || meetingId == null) {
            return false;
        }

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return false;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId;
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            log.error("Error deleting Zoom meeting: {}", meetingId, e);
            return false;
        }
    }

    // DTOs for Zoom API

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateZoomMeetingRequest {
        private String topic;
        private LocalDateTime startTime;
        private Integer durationMinutes;
        private String timezone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZoomTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("scope")
        private String scope;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZoomMeetingResponse {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("host_id")
        private String hostId;

        @JsonProperty("host_email")
        private String hostEmail;

        @JsonProperty("topic")
        private String topic;

        @JsonProperty("type")
        private Integer type;

        @JsonProperty("status")
        private String status;

        @JsonProperty("start_time")
        private String startTime;

        @JsonProperty("duration")
        private Integer duration;

        @JsonProperty("timezone")
        private String timezone;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("join_url")
        private String joinUrl;

        @JsonProperty("start_url")
        private String startUrl;

        @JsonProperty("password")
        private String password;

        @JsonProperty("h323_password")
        private String h323Password;

        @JsonProperty("pstn_password")
        private String pstnPassword;

        @JsonProperty("encrypted_password")
        private String encryptedPassword;
    }
}

