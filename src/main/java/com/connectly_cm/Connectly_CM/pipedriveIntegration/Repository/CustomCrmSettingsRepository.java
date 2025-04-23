package com.connectly_cm.Connectly_CM.pipedriveIntegration.Repository;

import com.connectly_cm.Connectly_CM.pipedriveIntegration.Model.CrmSettings;

public interface CustomCrmSettingsRepository {
    CrmSettings findByUserId(String userId);
    boolean checkByUserId(String userId);
}
