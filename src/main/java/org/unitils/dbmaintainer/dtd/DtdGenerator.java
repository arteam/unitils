package org.unitils.dbmaintainer.dtd;

import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 * todo javadoc
 *
 * @author Filip Neven
 */
public interface DtdGenerator {

    void init(Configuration configuration, DataSource dataSource);

    void generateDtd();

}
