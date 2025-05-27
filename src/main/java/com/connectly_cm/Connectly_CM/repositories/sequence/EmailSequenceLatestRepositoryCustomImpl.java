package com.connectly_cm.Connectly_CM.repositories.sequence;

import com.connectly_cm.Connectly_CM.dtos.sequences.ActivateDeactivateSeq;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.enums.SequenceStatus;
import com.connectly_cm.Connectly_CM.enums.SequenceStepStaus;
import com.connectly_cm.Connectly_CM.models.sequences.EmailSequenceLatest;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EmailSequenceLatestRepositoryCustomImpl implements EmailSequenceLatestRepositoryCustom {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void updateSequenceData(EmailSequenceRequestLatest emailSequenceRequestLatest,
                                   UsersConfig userConfig) {
        Query query = new Query(Criteria.where("_id").is(emailSequenceRequestLatest.getSequenceId()));
        Update update = new Update();
        List<EmailSequenceStepLatest> newEmailSeqSteps = emailSequenceRequestLatest.getEmailSteps().stream()
                .map(emailStep -> {
                    EmailSequenceStepLatest step = new EmailSequenceStepLatest();
                    step.setToEmailAddress(emailStep.getToEmailAddress());
                    step.setSubject(emailStep.getSubject());
                    step.setBodyText(emailStep.getBodyText());
                    step.setCreatedAt(DateTimeUtils.convertDateToString(new Date(),
                            TimeZone.getTimeZone("UTC"), null));
                    step.setStepStatus(SequenceStepStaus.TO_BE_PROCESSED);
                    return step;
                }).toList();
        update.set("emailSteps", newEmailSeqSteps);
        update.set("delayInSeconds", userConfig.getDelayInSeconds());
        update.set("fromAddress", userConfig.getFromAddress());
        update.set("timeWindow", userConfig.getTimeWindow());
        mongoTemplate.updateFirst(query, update, EmailSequenceLatest.class);
    }


    @Override
    public void updateSequenceState(ActivateDeactivateSeq seqState) {
        Query query = new Query(Criteria.where("_id").is(seqState.getSeqId()));
        Update update = new Update();
        update.set("seqStatus", seqState.getSeqStatus());
        mongoTemplate.updateFirst(query, update, EmailSequenceLatest.class);
    }
}
