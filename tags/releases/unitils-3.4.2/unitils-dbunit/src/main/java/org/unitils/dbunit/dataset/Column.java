package org.unitils.dbunit.dataset;

import static org.dbunit.dataset.ITable.NO_VALUE;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;


/**
 * A column in a data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Column {

    /* The name of the data set column */
    private String name;

    /* The type of the data set column, e.g. varchar */
    private DataType type;

    /* The actual value with a type corresponding the column type, e.g. String for varchar */
    private Object value;


    /**
     * Creates a value
     *
     * @param name  The name of the data set column, not null
     * @param type  The type of the data set column, e.g. varchar, not null
     * @param value The actual value with a type corresponding the column type, e.g. String for varchar, can be null
     */
    public Column(String name, DataType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }


    /**
     * @return The name of the data set column, not null
     */
    public String getName() {
        return name;
    }


    /**
     * @return The type of the data set column, e.g. varchar, not null
     */
    public DataType getType() {
        return type;
    }


    /**
     * @return The actual value with a type corresponding the column type, e.g. String for varchar, can be null
     */
    public Object getValue() {
        return value;
    }


    /**
     * Gets the value casted to the given type.
     *
     * @param castType The type to cast the value to, not null
     * @return The casted value
     * @throws UnitilsException When the value cannot be cast
     */
    public Object getCastedValue(DataType castType) {
        try {
            return castType.typeCast(value);
        } catch (TypeCastException e) {
            throw new UnitilsException("Unable to convert \"" + value + "\" to " + castType.toString() + ". Column name: " + name + ", current type: " + type.toString(), e);
        }
    }


    /**
     * Compares the column with the given actual column. If the actual column has a different type, the value
     * will be casted to this type.
     *
     * @param actualColumn The column to compare with, not null
     * @return The difference, null if none found
     */
    public ColumnDifference compare(Column actualColumn) {
        if (value == null || value.equals(NO_VALUE)) {
            return null;
        }
        if (value == actualColumn.getValue()) {
            return null;
        }
        if (value == null && actualColumn.getValue() != null) {
            return new ColumnDifference(this, actualColumn);
        }
        Object castedValue = getCastedValue(actualColumn.getType());
        if (castedValue == null && actualColumn.getValue() == null) {
            return null;
        } else if (castedValue == null && actualColumn.getValue() != null) {
            return new ColumnDifference(this, actualColumn);
        }
        if (!castedValue.equals(actualColumn.getValue())) {
            return new ColumnDifference(this, actualColumn);
        }
        return null;
    }

}
