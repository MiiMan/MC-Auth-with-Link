package me.mastercapexd.auth.messenger.commands;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.event.AccountUnlinkEvent;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.auth.api.link.user.LinkUser;

import io.github.revxrsal.eventbus.EventBus;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.messenger.commands.annotation.CommandKey;
import me.mastercapexd.auth.messenger.commands.annotation.ConfigurationArgumentError;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.orphan.OrphanCommand;

@CommandKey(UnlinkCommand.CONFIGURATION_KEY)
public class UnlinkCommand implements OrphanCommand {
    public static final String CONFIGURATION_KEY = "unlink";
    @Dependency
    private AccountDatabase accountDatabase;
    @Dependency
    private EventBus eventBus;

    @Dependency
    private PluginConfig config;

    @Dependency
    private AuthPlugin plugin;

    @ConfigurationArgumentError("unlink-not-enough-arguments")
    @DefaultFor("~")
    public void onUnlink(LinkCommandActorWrapper actorWrapper, LinkType linkType, Account account) {

        LinkUser linkUser = account.findFirstLinkUserOrNew(user -> user.getLinkType().equals(linkType), linkType);
        eventBus.publish(AccountUnlinkEvent.class, account, false, linkType, linkUser, linkUser.getLinkUserInfo().getIdentificator(), actorWrapper)
                .thenAccept(result -> {
                    if (result.getEvent().isCancelled())
                        return;

                    if (account.isSessionActive(config.getSessionDurability())) {
                        linkUser.getLinkUserInfo().setIdentificator(linkType.getDefaultIdentificator());
                        accountDatabase.updateAccountLinks(account);
                        actorWrapper.reply(linkType.getLinkMessages().getMessage("unlinked", linkType.newMessageContext(account)));
                    } else {
                        actorWrapper.reply(linkType.getLinkMessages().fromText("Для отвязки аккаунта необходимо находиться в игре"));
                    }
                });

        if (account.isSessionActive(config.getSessionDurability())) {
//            plugin.getAuthenticatingAccountBucket().removeAuthenticatingAccount(account);
            account.setLastSessionStartTimestamp(0);
            account.logout(config.getSessionDurability());
            accountDatabase.saveOrUpdateAccount(account);
            account.kick("Аккаунт отвязан от телеграмма!");
        }
    }
}
