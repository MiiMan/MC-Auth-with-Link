package com.bivashy.auth.api.link.user.info;

import com.bivashy.auth.api.util.Castable;

public interface LinkUserInfo extends Castable<LinkUserInfo> {
    LinkUserInfo NULL_USER_INFO = new LinkUserInfo() {
        @Override
        public LinkUserIdentificator getIdentificator() {
            return null;
        }

        @Override
        public LinkUserInfo setIdentificator(LinkUserIdentificator userIdentificator) {
            return this;
        }

        @Override
        public boolean isConfirmationEnabled() {
            return false;
        }

        @Override
        public LinkUserInfo setConfirmationEnabled(boolean confirmationEnabled) {
            return this;
        }
    };

    /**
     * Returns used identificator as {@link LinkUserIdentificator}. It uses
     * interface because identificator can be anything (string, integer, UUID,
     * link).
     *
     * @return Identificator of user.
     */
    LinkUserIdentificator getIdentificator();

    /**
     * Set user identificator, may throw {@link UnsupportedOperationException} if
     * {@link LinkUserInfo} cannot process provided {@link LinkUserIdentificator}
     * identificator.
     *
     * @param userIdentificator that should be applied
     * @return this {@link LinkUserInfo}
     */
    LinkUserInfo setIdentificator(LinkUserIdentificator userIdentificator);

    boolean isConfirmationEnabled();

    LinkUserInfo setConfirmationEnabled(boolean confirmationEnabled);
}
