package com.connectly_cm.Connectly_CM.Services.sequences;

import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.models.sequences.EmailSequenceLatest;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.repositories.sequence.EmailSequenceLatestRepository;
import com.connectly_cm.Connectly_CM.repositories.userConfig.UserConfigRepository;
import com.connectly_cm.Connectly_CM.responses.ResultResponse;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class SequenceServiceLatest {
    @Autowired
    EmailSequenceLatestRepository emailSequenceLatestRepository;

    @Autowired
    UserConfigRepository userConfigRepository;

    private static final Logger LOGGER = Logger.getLogger(SequenceServiceLatest.class);

    public ResponseEntity<?> createSequence(String seqName, String userId) {
        EmailSequenceLatest newSeq = new EmailSequenceLatest();
        newSeq.setUserId(userId);
        newSeq.setSequenceName(seqName);
        newSeq.setActive(false);
        newSeq.setCreatedAt(DateTimeUtils.convertDateToString
                (new Date(), TimeZone.getTimeZone("UTC"), null));
        emailSequenceLatestRepository.save(newSeq);
        return new ResponseEntity<>(HttpStatusCode.valueOf(
                HttpStatus.CREATED.value()));
    }

    public ResultResponse addDataToSequence(EmailSequenceRequestLatest emailSequenceRequest, String userId) {
        UsersConfig userConfig = userConfigRepository.findByUserId(userId);
        ResultResponse result = new ResultResponse();
        if (userConfig != null) {
            emailSequenceLatestRepository.updateSequenceData(emailSequenceRequest, userConfig);
            result.setStatusCode(HttpStatus.OK.value());
            result.setData(emailSequenceRequest);
            result.setMessage("New sequence created with provided data.");
            return result;
        } else {
            result.setMessage("UserConfig not found. Please configure your requirements");
            result.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return result;
        }
    }

    public ResponseEntity<?> activateSequence(String seqId) {
        emailSequenceLatestRepository.updateSequenceState(seqId,true);
        return new ResponseEntity<>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
