package com.connectly_cm.Connectly_CM.pipedriveIntegration.Repository;

import com.connectly_cm.Connectly_CM.pipedriveIntegration.Model.CrmSettings;

import java.util.List;
import java.util.Optional;

public interface CustomCrmSettingsRepository {
    Optional<List<CrmSettings>> findByUserId(String userId);
    boolean checkByUserId(String userId);
}
