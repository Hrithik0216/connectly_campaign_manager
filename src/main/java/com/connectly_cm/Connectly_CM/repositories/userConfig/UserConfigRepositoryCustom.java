package com.connectly_cm.Connectly_CM.repositories.userConfig;

import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;


public interface UserConfigRepositoryCustom{
    void updateConfigByFindingFirst(String userId, UserConfiguration userConfiguration);
}
