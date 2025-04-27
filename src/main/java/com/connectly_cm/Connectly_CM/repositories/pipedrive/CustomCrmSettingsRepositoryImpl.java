package com.connectly_cm.Connectly_CM.repositories.pipedrive;

import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public class CustomCrmSettingsRepositoryImpl implements CustomCrmSettingsRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public CrmSettings findByUserId(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        return (mongoTemplate.findOne(query, CrmSettings.class));
    }

    @Override
    public boolean checkByUserId(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        Optional<List<CrmSettings>> result = Optional.of(mongoTemplate.find(query, CrmSettings.class));
        return result.get().size() > 0 ? true : false;
    }
}
