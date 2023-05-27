package me.mastercapexd.auth.crypto;

import com.bivashy.auth.api.crypto.CryptoProvider1;
import com.bivashy.auth.api.crypto.HashInput;
import com.bivashy.auth.api.crypto.HashedPassword;

public abstract class BaseCryptoProvider implements CryptoProvider1 {
    private final String identifier;

    public BaseCryptoProvider(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public HashedPassword hash(HashInput input) {
        return hash(input, generateSalt());
    }

    protected HashedPassword hash(HashInput input, String salt){
        String password = input.getRawInput();
        String hash = password + salt;
        for (int i = 0; i < input.getHashIteration(); i++) {
            hash = hashInput(HashInput.of(hash + password + salt), salt).getHash();
        }
        return HashedPassword.of(hash, salt, this);
    }

    protected abstract HashedPassword hashInput(HashInput input, String salt);

    @Override
    public boolean matches(HashInput input, HashedPassword password) {
        return hash(input, password.getSalt()).getHash().equals(password.getHash());
    }

    protected String generateSalt() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
}
