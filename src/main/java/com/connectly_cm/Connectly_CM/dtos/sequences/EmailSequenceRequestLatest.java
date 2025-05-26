package com.connectly_cm.Connectly_CM.dtos.sequences;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailSequenceStepRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;

import java.util.List;

public class EmailSequenceRequestLatest {
   private String sequenceId;
    private List<EmailSequenceStepRequest> emailSteps;

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public List<EmailSequenceStepRequest> getEmailSteps() {
        return emailSteps;
    }

    public void setEmailSteps(List<EmailSequenceStepRequest> emailSteps) {
        this.emailSteps = emailSteps;
    }

    @Override
    public String toString() {
        return "EmailSequenceRequest{" +
                ", emailSteps=" + (emailSteps != null ? emailSteps.toString() : "[]") +
                '}';
    }
}
