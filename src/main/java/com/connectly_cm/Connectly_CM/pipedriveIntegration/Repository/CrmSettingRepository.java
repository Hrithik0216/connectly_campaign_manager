package com.connectly_cm.Connectly_CM.pipedriveIntegration.Repository;

import com.connectly_cm.Connectly_CM.pipedriveIntegration.Model.CrmSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrmSettingRepository extends MongoRepository<CrmSettings, String>, CustomCrmSettingsRepository {
}
