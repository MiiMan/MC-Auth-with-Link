package me.mastercapexd.auth.bungee.events;

import me.mastercapexd.auth.Account;
import net.md_5.bungee.api.plugin.Event;

public class VKLinkEvent extends Event implements Cancellable {
	private final Integer userId;
	private final Account linkedAccount;
	private boolean isCancelled = false;

	public VKLinkEvent(Integer userId, Account linkedAccount) {
		this.userId = userId;
		this.linkedAccount = linkedAccount;
	}

	public Integer getUserId() {
		return userId;
	}

	public Account getLinkedAccount() {
		return linkedAccount;
	}

	@Override
	public void setCancelled(boolean cancelValue) {
		this.isCancelled = cancelValue;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
}
