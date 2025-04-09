package com.connectly_cm.Connectly_CM.pipedriveIntegration.Model;

import com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO.CrmConfig;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("crm_settings")
public class CrmSettings {
    @Id
    private String id;
    private String type;
    private String memberId;
    private String accessToken;
    private String refreshToken;
    private String instanceUrl;
    private String apiDomain;
    private String accessTokenExpiryDate;
    private Date createTs;
    private Date updateTs;
    private String name;
    private String userId;
    private String connectedEmail;
    private boolean syncSentEnabled;
    private boolean syncOpenEnabled;
    private boolean syncClickEnabled;
    private boolean syncReplyEnabled;
    private boolean syncPhoneCallEnabled;
    private boolean syncLiAcceptRequest;
    private boolean syncLiSendRequest;
    private boolean syncLiMessageSent;
    private boolean syncLiReplyReceived;
    private boolean syncLiInteractWithPost;
    private boolean syncCustomTask;
    private CrmConfig config;
//    private List<CrmOwner> owner;
    private String teamId;
    private long apiLimitPerDay = 15000;
//    private CrmSyncLog syncLog;
}
