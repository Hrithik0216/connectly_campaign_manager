package com.connectly_cm.Connectly_CM.repositories.sequence;

import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;

public interface EmailSequenceLatestRepositoryCustom {
    void updateSequenceData(EmailSequenceRequestLatest emailSequenceRequestLatest, UsersConfig usersConfig);
}
