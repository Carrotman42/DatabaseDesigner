package com.ksoft.dd_processor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import com.ksoft.dd.Column;

public class TableData {
    String name;
    
    private static class NameColumnPair {
        /**
         * Represents the actual name of a column. This is needed because when getColumnIndex is
         * called it doesn't do case insensitive matching, so the actual column name needs to
         * exactly match the CREATE TABLE script.
         */
        public final String actualName;
        
        public final Column column;
        
        NameColumnPair(String actualname, Column colData) {
            actualName = actualname;
            column = colData;
        }
    }
    
    private final HashMap<String, NameColumnPair> cols = new HashMap<>();
    
    /**
     * 
     * @param name
     * @param info
     * @return true if the provided column name was already added (case insensitive).
     */
    public boolean putNewColumn(String name, Column info) {
        return null != cols.put(name.toUpperCase(), new NameColumnPair(name, info));
    }
    
    public void writeCreateSQL(PrintWriter wr) {
        // Assume the table is valid
        wr.append("CREATE TABLE ").append(name).append('(');
        
        Iterator<NameColumnPair> it = cols.values().iterator();
        
        NameColumnPair ent = it.next();
        writeColumn(wr, ent.actualName, ent.column);
        
        while (it.hasNext()) {
            ent = it.next();
            writeColumn(wr.append(','), ent.actualName, ent.column);
        }
        
        wr.append(')');
    }
    
    public void writeColumnArray(PrintWriter wr) {
        // Assume the table is valid
        
        Iterator<NameColumnPair> cs = cols.values().iterator();
        
        wr.append("public static final String[] ").append(name).append("_COLNAMES = {")
        
        // Since this processor runs every time a column name changes it is perfectly safe to
        // hard-code the column names here. This also allows tables to have non-public column names
                
                // valid table means it has at least one column
                .append('"').append(cs.next().actualName).append('"');
        
        while (cs.hasNext()) {
            wr.append(',').append('"').append(cs.next().actualName).append('"');
        }
        wr.append("};\n");
    }
    
    // See http://www.sqlite.org/lang_createtable.html
    private void writeColumn(PrintWriter b, String name, Column c) {
        b.append(name).append(' ').append(c.type().name());
        
        if (c.pkey()) {
            b.append(" PRIMARY KEY AUTOINCREMENT");
        }
        if (c.notNull()) {
            b.append(" NOT NULL");
        }
        if (c.defaultValue().length() > 0) {
            b.append(" DEFAULT \\\"").append(c.defaultValue()).append("\\\"");
        }
        if (c.foreignKeyTable().length() > 0) {
            b.append(" REFERENCES ").append(c.foreignKeyTable());
            
            //Add the foreign key column only if it is non empty.
            if(!c.foreignKeyColumn().isEmpty()){
            	b.append(String.format("(%s)", c.foreignKeyColumn()));
            }
            
            b.append(" ON DELETE ").append(c.onDelete().getSqlRepresentation());
            b.append(" ON UPDATE ").append(c.onUpdate().getSqlRepresentation());
        }
        
    }
    
    public boolean hasColumns() {
        return cols.size() > 0;
    }
}
