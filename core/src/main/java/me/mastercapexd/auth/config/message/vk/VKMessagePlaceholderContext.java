package me.mastercapexd.auth.config.message.vk;

import java.util.List;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.responses.GetResponse;

import me.mastercapexd.auth.config.message.context.placeholder.PlaceholderProvider;
import me.mastercapexd.auth.config.message.link.context.LinkPlaceholderContext;
import me.mastercapexd.auth.hooks.VkPluginHook;
import me.mastercapexd.auth.link.vk.VKLinkType;

public class VKMessagePlaceholderContext extends LinkPlaceholderContext {
    private static final VkPluginHook VK_HOOK = AuthPlugin.instance().getHook(VkPluginHook.class);
    private static final VkApiClient CLIENT = VK_HOOK.getClient();
    private static final GroupActor ACTOR = VK_HOOK.getActor();

    public VKMessagePlaceholderContext(Account account) {
        super(account, VKLinkType.getInstance(), "vk");
        try {
            if (linkUser.isIdentifierDefaultOrNull() || !linkUser.getLinkUserInfo().getIdentificator().isNumber())
                return;
            List<GetResponse> userInformationResponses =
                    CLIENT.users().get(ACTOR).userIds(String.valueOf(linkUser.getLinkUserInfo().getIdentificator().asNumber())).execute();
            if (userInformationResponses.isEmpty())
                return;
            GetResponse userInformationResponse = userInformationResponses.get(0);
            registerPlaceholderProvider(PlaceholderProvider.of(userInformationResponse.getScreenName(), "%vk_screen_name%"));
            registerPlaceholderProvider(PlaceholderProvider.of(userInformationResponse.getFirstName(), "%vk_first_name%"));
            registerPlaceholderProvider(PlaceholderProvider.of(userInformationResponse.getLastName(), "%vk_last_name%"));
        } catch(ApiException | ClientException e) {
            e.printStackTrace();
        }
    }
}
