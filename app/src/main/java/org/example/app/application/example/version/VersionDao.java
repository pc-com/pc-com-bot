package org.example.app.application.example.version;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface VersionDao {
    @Select
    String getVersion();
}
