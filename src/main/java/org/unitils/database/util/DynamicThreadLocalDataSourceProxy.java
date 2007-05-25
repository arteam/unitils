package org.unitils.database.util;

import javax.sql.DataSource;

import org.unitils.dbmaintainer.util.BaseDataSourceProxy;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DynamicThreadLocalDataSourceProxy extends BaseDataSourceProxy {

    private ThreadLocal<BaseDataSourceProxy> threadLocalDataSourceProxy =
        new ThreadLocal<BaseDataSourceProxy>();

    
    public DynamicThreadLocalDataSourceProxy(DataSource targetDataSource) {
        super(targetDataSource);
    }


    public void registerDataSourceProxy(BaseDataSourceProxy dataSourceProxy) {
        dataSourceProxy.setTargetDataSource(getActualDataSource());
        threadLocalDataSourceProxy.set(dataSourceProxy);
    }

    public void deRegisterDataSourceProxy() {
        threadLocalDataSourceProxy.remove();
    }


    @Override
    public DataSource getTargetDataSource() {
        BaseDataSourceProxy proxy = threadLocalDataSourceProxy.get();
        if (proxy != null) {
            return proxy;
        }
        return super.getTargetDataSource();
    }


    private DataSource getActualDataSource() {
        return super.getTargetDataSource();
    }
}
