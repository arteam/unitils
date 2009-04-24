package org.unitils.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.unitils.core.UnitilsException;


/**
 * A data set value
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Value {

    /* The name of the data set column */
    private String columnName;

    /* The type of the data set column, e.g. varchar */
    private DataType columnType;

    /* The actual value with a type corresponding the column type, e.g. String for varchar */
    private Object value;


    /**
     * Creates a value
     *
     * @param columnName The name of the data set column, not null
     * @param columnType The type of the data set column, e.g. varchar, not null
     * @param value      The actual value with a type corresponding the column type, e.g. String for varchar, can be null
     */
    public Value(String columnName, DataType columnType, Object value) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.value = value;
    }


    /**
     * @return The name of the data set column, not null
     */
    public String getColumnName() {
        return columnName;
    }


    /**
     * @return The type of the data set column, e.g. varchar, not null
     */
    public DataType getColumnType() {
        return columnType;
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
            throw new UnitilsException("Unable to convert \"" + value + "\" to " + castType.toString() + ". Column name: " + columnName + ", current type: " + columnType.toString(), e);
        }
    }


    /**
     * Checks that the value is equal to the given actual value. If the actual value has a different type, the value
     * will be casted to this type. If the actual value is for a different column, the values are not equal.
     *
     * @param actualValue The actual value to compare with, can be null
     * @return True if the given actual value is equal
     */
    public boolean equalValue(Value actualValue) {
        if (actualValue == null || !columnName.equalsIgnoreCase(actualValue.getColumnName())) {
            return false;
        }

        Object castedValue = getCastedValue(actualValue.getColumnType());
        return castedValue.equals(actualValue.getValue());
    }

}
