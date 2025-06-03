package br.com.recargapay.events.listener;

import br.com.recargapay.events.DepositFundsEvent;
import br.com.recargapay.events.TransferFundsEvent;
import br.com.recargapay.events.WalletCreation;
import br.com.recargapay.events.WithdrawFundsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogEvents {

    @EventListener
    public void handleWalletCreationEvent(WalletCreation event) {
        log.info("Wallet created: {}", event);
    }

    @EventListener
    public void handleTransferFundsEvent(TransferFundsEvent event) {
        log.info("Funds transferred: {}", event);
    }

    @EventListener
    public void handleWithdrawFundsEvent(WithdrawFundsEvent event) {
        log.info("Funds withdrawn: {}", event);
    }

    @EventListener
    public void handleDepositFundsEvent(DepositFundsEvent event) {
        log.info("Funds deposited: {}", event);
    }

}
