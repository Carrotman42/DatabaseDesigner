package com.ksoft.dd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface Column {
    /**
     * Represents the type of the database column. Must be defined.
     */
    Affinity type();
    
    /**
     * Represents the default value of a column. Optional.<br>
     * <br>
     * This is untested with double quotes (").
     */
    String defaultValue() default "";
    
    /**
     * Represents whether the column may be null. If not specified, the column value will be allowed
     * to be null.
     */
    boolean notNull() default false;
    
    /**
     * Represents the table for the foreign key constraint of this column. Optional. If specified,
     * {@link #foreignKeyColumn()} must be as well.
     */
    String foreignKeyTable() default "";
    
    /**
     * Represents the referenced column for the foreign key constraint of this column. Optional. If
     * specified, {@link #foreignKeyTable()} must be as well.
     */
    String foreignKeyColumn() default "";
    
    /**
     * The action to take when rows are deleted from the parent table. If specified,
     * {@link #foreignKeyColumn()} and {@link #foreignKeyTable()} must be specified.
     */
    ForeignKeyAction onDelete() default ForeignKeyAction.NO_ACTION;
    
    /**
     * The action to take when parent keys are updated. If specified, {@link #foreignKeyColumn()}
     * and {@link #foreignKeyTable()} must be specified.
     */
    ForeignKeyAction onUpdate() default ForeignKeyAction.NO_ACTION;
    
    /**
     * Represents whether the column represents the primary key of the table. If true, an
     * auto-incrementing primary key will be added. Note that you will still need to set
     * type=Integer and notNot=true.<br>
     * <br>
     * If omitted, it defaults to false.
     */
    boolean pkey() default false;
}
