package me.mastercapexd.auth.proxy.commands;

import me.mastercapexd.auth.account.Account;
import me.mastercapexd.auth.authentication.step.AuthenticationStep;
import me.mastercapexd.auth.authentication.step.steps.LoginAuthenticationStep;
import me.mastercapexd.auth.config.PluginConfig;
import me.mastercapexd.auth.event.AccountTryLoginEvent;
import me.mastercapexd.auth.proxy.ProxyPlugin;
import me.mastercapexd.auth.proxy.commands.annotations.AuthenticationStepCommand;
import me.mastercapexd.auth.proxy.player.ProxyPlayer;
import me.mastercapexd.auth.storage.AccountStorage;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Dependency;

@Command({"login", "l"})
public class LoginCommand {
    @Dependency
    private ProxyPlugin plugin;
    @Dependency
    private PluginConfig config;
    @Dependency
    private AccountStorage accountStorage;

    @Default
    @AuthenticationStepCommand(stepName = LoginAuthenticationStep.STEP_NAME)
    public void login(ProxyPlayer player, Account account, String password) {
        String id = account.getPlayerId();
        AuthenticationStep currentAuthenticationStep = account.getCurrentAuthenticationStep();

        boolean isWrongPassword = !account.getHashType().checkHash(password, account.getPasswordHash());
        plugin.getEventBus().publish(AccountTryLoginEvent.class, account, isWrongPassword, !isWrongPassword).thenAccept(tryLoginEventPostResult -> {
            if (tryLoginEventPostResult.getEvent().isCancelled())
                return;

            if (account.getHashType() != config.getActiveHashType()) {
                account.setHashType(config.getActiveHashType());
                account.setPasswordHash(config.getActiveHashType().hash(password));
            }

            currentAuthenticationStep.getAuthenticationStepContext().setCanPassToNextStep(true);
            account.nextAuthenticationStep(plugin.getAuthenticationContextFactoryDealership().createContext(account));
            player.sendMessage(config.getProxyMessages().getMessage("login-success"));
        });
    }
}