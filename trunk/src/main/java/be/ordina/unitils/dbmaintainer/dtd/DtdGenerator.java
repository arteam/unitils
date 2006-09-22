package be.ordina.unitils.dbmaintainer.dtd;

import javax.sql.DataSource;

/**
 * todo javadoc
 *
 * @author Filip Neven
 */
public interface DtdGenerator {

    void init(DataSource dataSource);

    void generateDtd();

}
