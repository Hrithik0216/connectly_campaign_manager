package com.connectly_cm.Connectly_CM.Services.sequences;

import com.connectly_cm.Connectly_CM.dtos.sequences.ActivateDeactivateSeq;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.models.sequences.EmailSequenceLatest;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.repositories.sequence.EmailSequenceLatestRepository;
import com.connectly_cm.Connectly_CM.repositories.userConfig.UserConfigRepository;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ResultResponse;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class SequenceServiceLatest {

    private static final Logger LOGGER = Logger.getLogger(SequenceServiceLatest.class);
    @Autowired
    EmailSequenceLatestRepository emailSequenceLatestRepository;

    @Autowired
    UserConfigRepository userConfigRepository;


    public ResponseEntity<?> createSequence(String seqName, String userId) {
        EmailSequenceLatest newSeq = new EmailSequenceLatest();
        newSeq.setUserId(userId);
        newSeq.setSequenceName(seqName);
        newSeq.setCreatedAt(DateTimeUtils.convertDateToString
                (new Date(), TimeZone.getTimeZone("UTC"), null));
        emailSequenceLatestRepository.save(newSeq);
        LOGGER.info("Created the sequence "+seqName);
        return new ResponseEntity<>(HttpStatusCode.valueOf(
                HttpStatus.CREATED.value()));
    }

    public ResultResponse addDataToSequence(EmailSequenceRequestLatest emailSequenceRequest, String userId) {
        UsersConfig userConfig = userConfigRepository.findByUserId(userId);
        Optional<EmailSequenceLatest> existingSeq = emailSequenceLatestRepository.findById(emailSequenceRequest.getSequenceId());

        ResultResponse result = new ResultResponse();
        if (existingSeq.isPresent()){

            if (userConfig != null) {
                LOGGER.info("The seq and userConfig exists. Adding data");
                emailSequenceLatestRepository.updateSequenceData(emailSequenceRequest, userConfig);
                result.setStatusCode(HttpStatus.OK.value());
                result.setData(emailSequenceRequest);
                result.setMessage("New sequence created with provided data.");
                return result;
            } else {
                LOGGER.info("The userConfig does not exist.");
                result.setMessage("UserConfig not found. Please configure your requirements");
                result.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return result;
            }
        }else{
            LOGGER.info("Sequence with the given seqId is not found. Please Create a seq and then add data");
            result.setMessage("Sequence with the given seqId is not found. Please Create a seq and then add data");
            result.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return result;
        }

    }

    public ResponseEntity<?> activateDeactivateSequence(ActivateDeactivateSeq seqData) {
        Optional<EmailSequenceLatest> existingSeq = emailSequenceLatestRepository.findById(seqData.getSeqId());

        if (existingSeq.isPresent()){
            LOGGER.info("Sequence with the given seqId exists");
            emailSequenceLatestRepository.updateSequenceState(seqData);
            return  ResponseEntity.status(HttpStatus.OK).body(Map.of("message",
                    "updated the data"));
        }else{
            LOGGER.info("Sequence with the given seqId is not found. Please Create a seq and then add data");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "sequence not found"));
        }

    }
}

/*Methods are ordered in a proper sequence*/
