package com.connectly_cm.Connectly_CM.dtos.sequences;

import com.connectly_cm.Connectly_CM.enums.SequenceStatus;

public class ActivateDeactivateSeq {
    private String seqId;
    private SequenceStatus seqStatus;

    public SequenceStatus getSeqStatus() {
        return seqStatus;
    }

    public void setSeqStatus(SequenceStatus seqStatus) {
        this.seqStatus = seqStatus;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

}
