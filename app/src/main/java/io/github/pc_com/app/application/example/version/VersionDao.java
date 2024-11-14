package io.github.pc_com.app.application.example.version;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface VersionDao {
    @Select
    String getVersion();
}
