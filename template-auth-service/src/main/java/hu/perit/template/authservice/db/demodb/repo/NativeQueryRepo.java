package hu.perit.template.authservice.db.demodb.repo;

import hu.perit.spvitamin.spring.data.config.DatasourceCollectionProperties;
import hu.perit.spvitamin.spring.data.config.DatasourceProperties;
import hu.perit.spvitamin.spring.data.nativequery.NativeQueryRepoImpl;
import hu.perit.template.authservice.db.demodb.DemoDbConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Do not unit test!
 *
 * @author nagy_peter
 */
@Repository
public class NativeQueryRepo
{
    private final NativeQueryRepoImpl impl;

    public NativeQueryRepo(@Qualifier(value = DemoDbConfig.ENTITY_MANAGER_FACTORY) EntityManager em, DatasourceCollectionProperties dbProperties)
    {
        DatasourceProperties datasourceProperties = dbProperties.getDatasource().get(DemoDbConfig.PERSISTENCE_UNIT);
        this.impl = new NativeQueryRepoImpl(em, datasourceProperties.getSocketTimeout());
    }

    public List<?> getResultList(String sql)
    {
        return this.getResultList(sql, true);
    }

    public List<?> getResultList(String sql, boolean logSql)
    {
        return this.impl.getResultList(sql, logSql);
    }

    public List<?> getResultList(String sql, List<Object> params, boolean logSql)
    {
        return this.impl.getResultList(sql, params, logSql);
    }

    public List<?> getResultList(String sql, List<Object> params, boolean logSql, Integer limit)
    {
        return this.impl.getResultList(sql, params, logSql);
    }

    public Object getSingleResult(String sql)
    {
        return this.impl.getSingleResult(sql);
    }

    public Object getSingleResult(String sql, boolean logSql)
    {
        return this.impl.getSingleResult(sql, logSql);
    }

    @Modifying
    @Transactional
    public void executeModifyingQuery(String sql)
    {
        this.impl.executeModifyingQuery(sql);
    }
}
