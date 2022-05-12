package me.mastercapexd.auth.link.message.vk;

import com.vk.api.sdk.objects.messages.KeyboardButton;
import com.vk.api.sdk.objects.messages.KeyboardButtonAction;
import com.vk.api.sdk.objects.messages.KeyboardButtonColor;
import com.vk.api.sdk.objects.messages.TemplateActionTypeNames;

import me.mastercapexd.auth.link.message.keyboard.button.DefaultButton;

public class VKButton extends DefaultButton {
	private static final VKButtonAction DEFAULT_ACTION = new VKButtonAction(TemplateActionTypeNames.TEXT);
	private static final VKButtonColor DEFAULT_COLOR = new VKButtonColor(KeyboardButtonColor.DEFAULT);

	public VKButton(String label) {
		super(label);
	}

	public VKButton(KeyboardButton keyboardButton) {
		super(keyboardButton.getAction().getLabel());
		this.action = new VKButtonAction(keyboardButton.getAction().getType());
		this.color = new VKButtonColor(keyboardButton.getColor());
		switch (keyboardButton.getAction().getType()) {
		case CALLBACK:
			this.actionData = keyboardButton.getAction().getPayload();
			break;
		case OPEN_LINK:
			this.actionData = keyboardButton.getAction().getLink();
			break;
		default:
			break;

		}
	}

	public KeyboardButton create() {
		KeyboardButton keyboardButton = new KeyboardButton();
		KeyboardButtonAction buttonAction = new KeyboardButtonAction();
		TemplateActionTypeNames buttonType = action.safeAs(VKButtonAction.class, DEFAULT_ACTION).getButtonActionType();
		buttonAction.setType(buttonType);
		if (buttonType == TemplateActionTypeNames.CALLBACK)
			buttonAction.setPayload(actionData);
		if (buttonType == TemplateActionTypeNames.OPEN_LINK)
			buttonAction.setLink(actionData);
		buttonAction.setLabel(label);
		keyboardButton.setAction(buttonAction);
		keyboardButton.setColor(color.safeAs(VKButtonColor.class, DEFAULT_COLOR).getButtonColor());
		return keyboardButton;
	}

	public class VKButtonBuilder extends DefaultButtonBuilder {
		public VKButtonBuilder() {
			super(VKButton.this);
		}
	}
}
