package me.mastercapexd.auth.hooks;

import com.pengrad.telegrambot.TelegramBot;

public class BaseTelegramPluginHook implements TelegramPluginHook {
    private TelegramBot telegramBot = new TelegramBot.Builder(PLUGIN.getConfig().getTelegramSettings().getBotToken()).updateListenerSleep(5000).build();

    @Override
    public TelegramBot getTelegramBot() {
        return telegramBot;
    }
}
