package com.connectly_cm.Connectly_CM.repositories.pipedrive;

import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;

public interface CustomCrmSettingsRepository {
    CrmSettings findByUserId(String userId);
    boolean checkByUserId(String userId);
}
