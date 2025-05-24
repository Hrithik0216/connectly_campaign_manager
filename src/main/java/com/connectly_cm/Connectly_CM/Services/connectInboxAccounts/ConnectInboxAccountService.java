package com.connectly_cm.Connectly_CM.Services.connectInboxAccounts;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConnectInboxAccountService {
    private static final Logger LOGGER = Logger.getLogger(ConnectInboxAccountService.class);

    @Autowired
    ConnectedAccountRepository connectedAccountRepository;
}
