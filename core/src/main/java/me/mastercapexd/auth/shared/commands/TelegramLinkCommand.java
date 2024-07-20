package me.mastercapexd.auth.shared.commands;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.config.message.Messages;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.link.user.info.LinkUserIdentificator;
import com.bivashy.auth.api.model.PlayerIdSupplier;
import com.bivashy.auth.api.shared.commands.MessageableCommandActor;
import com.bivashy.auth.api.type.LinkConfirmationType;

import com.bivashy.messenger.common.identificator.Identificator;
import me.mastercapexd.auth.link.telegram.TelegramLinkType;
import me.mastercapexd.auth.link.user.confirmation.BaseLinkConfirmationUser;
import me.mastercapexd.auth.messenger.commands.annotation.CommandKey;
import me.mastercapexd.auth.server.commands.annotations.AuthenticationStepCommand;
import me.mastercapexd.auth.server.commands.annotations.TelegramUse;
import me.mastercapexd.auth.step.impl.LoginAuthenticationStep;
import me.mastercapexd.auth.step.impl.link.TelegramLinkAuthenticationStep;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.orphan.OrphanCommand;

@CommandKey(MessengerLinkCommandTemplate.CONFIGURATION_KEY)
public class TelegramLinkCommand extends MessengerLinkCommandTemplate implements OrphanCommand {
    public static final String LINK_NAME = "TELEGRAM";
    @Dependency
    private AuthPlugin plugin;
    @Dependency
    private PluginConfig config;
    @Dependency
    private AccountDatabase accountDatabase;

    public TelegramLinkCommand(Messages<?> messages) {
        super(messages, TelegramLinkType.getInstance());
    }

//    @AuthenticationStepCommand(stepName = TelegramLinkAuthenticationStep.STEP_NAME)
    @TelegramUse
    @DefaultFor("~")
    public void telegramLink(MessageableCommandActor commandActor, PlayerIdSupplier idSupplier, @Optional LinkUserIdentificator linkUserIdentificator) {
        String accountId = idSupplier.getPlayerId();

        accountDatabase.getAccountFromName(accountId).thenAccept(account -> {
            if (isInvalidAccount(account, commandActor, TelegramLinkType.LINK_USER_FILTER))
                return;

            if (!plugin.getAuthenticatingAccountBucket().getAuthenticatingAccount(idSupplier).get().getCurrentAuthenticationStep().getStepName().equals("TELEGRAM_LINK")) {
                account.getPlayer().get().sendMessage("Сначала введите пароль!");
                return;
            }

            String code = generateCode(() -> config.getTelegramSettings().getConfirmationSettings().generateCode());

            LinkConfirmationType linkConfirmationType = getLinkConfirmationType(commandActor);
            long timeoutTimestamp =
                    System.currentTimeMillis() + TelegramLinkType.getInstance().getSettings().getConfirmationSettings().getRemoveDelay().getMillis();
            sendLinkConfirmation(commandActor, linkConfirmationType.bindLinkConfirmationUser(
                    new BaseLinkConfirmationUser(linkConfirmationType, timeoutTimestamp, TelegramLinkType.getInstance(), account, code),
                    linkUserIdentificator));

            //account.getPlayer().get().sendMessage("Регистрация завершена! Перезайдите на сервер после подтвеждения в боте (введите /code " + code + " )");

            plugin.getAuthenticatingAccountBucket().removeAuthenticatingAccount(account);

            account.logout(config.getSessionDurability());
            account.kick("Регистрация завершена! Перезайдите на сервер после подтвеждения в боте (введите /code " + code + " )");
        });
    }
}
