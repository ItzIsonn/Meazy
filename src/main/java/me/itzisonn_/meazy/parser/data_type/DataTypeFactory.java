package me.itzisonn_.meazy.parser.data_type;

/**
 * Represents factory for creating {@link DataType}s
 */
public interface DataTypeFactory {
    /**
     * Creates data type
     *
     * @param id Class id
     * @param isNullable Whether this data type accepts null values
     * @return New data type
     */
    DataType create(String id, boolean isNullable);

    /**
     * Creates nullable data type
     *
     * @param id Class id
     * @return New data type
     */
    DataType create(String id);

    /**
     * Creates nullable data type that accepts any value
     * @return New data type
     */
    DataType create();
}
