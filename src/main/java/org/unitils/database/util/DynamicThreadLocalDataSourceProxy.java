/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.database.util;

import org.unitils.dbmaintainer.util.BaseDataSourceProxy;

import javax.sql.DataSource;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DynamicThreadLocalDataSourceProxy extends BaseDataSourceProxy {


    private ThreadLocal<BaseDataSourceProxy> threadLocalDataSourceProxy = new ThreadLocal<BaseDataSourceProxy>();


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
