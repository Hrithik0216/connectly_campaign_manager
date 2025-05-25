package com.connectly_cm.Connectly_CM.repositories.userConfig;

import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UserConfigRepositoryCustomImpl implements UserConfigRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void updateConfigByFindingFirst(String userId, UserConfiguration userConfiguration) {
        Query query = new Query().addCriteria(Criteria.where("userId").is(userId));
        Update update = new Update();
        if (userConfiguration.getDelayInSeconds() != null) {
            update.set("delayInSeconds", userConfiguration.getDelayInSeconds());
        }
        if (userConfiguration.getTimeWindow() != null) {
            update.set("timeWindow", userConfiguration.getTimeWindow());
        }
        mongoTemplate.updateFirst(query, update, UsersConfig.class);
    }
}
