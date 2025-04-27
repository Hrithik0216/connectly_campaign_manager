package com.connectly_cm.Connectly_CM.repositories.pipedrive;

import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrmSettingRepository extends MongoRepository<CrmSettings, String>, CustomCrmSettingsRepository {
}
