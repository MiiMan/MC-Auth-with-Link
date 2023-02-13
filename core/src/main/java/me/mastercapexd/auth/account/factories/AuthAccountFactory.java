package me.mastercapexd.auth.account.factories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import me.mastercapexd.auth.IdentifierType;
import me.mastercapexd.auth.account.Account;
import me.mastercapexd.auth.account.AuthAccountAdapter;
import me.mastercapexd.auth.storage.DatabaseHelper;
import me.mastercapexd.auth.storage.model.AuthAccount;

public class AuthAccountFactory extends AccountFactoryTemplate {
    private final DatabaseHelper databaseHelper;

    public AuthAccountFactory(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    protected Account newAccount(String id, IdentifierType identifierType, UUID uniqueId, String name) {
        AuthAccount authAccount = new AuthAccount(id, identifierType, name, uniqueId);
        try {
            databaseHelper.getAuthAccountDao().assignEmptyForeignCollection(authAccount, "links");
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return new AuthAccountAdapter(authAccount, new ArrayList<>());
    }
}
