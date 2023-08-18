package me.mastercapexd.auth.bucket;

import java.util.List;

import com.bivashy.auth.api.bucket.AuthenticationTaskBucket;
import com.bivashy.auth.api.model.AuthenticationTask;

public class BaseAuthenticationTaskBucket extends BaseListBucket<AuthenticationTask> implements AuthenticationTaskBucket {

    @Override
    public List<AuthenticationTask> getTasks() {
        return (List<AuthenticationTask>) getUnmodifiableRaw();
    }

}
