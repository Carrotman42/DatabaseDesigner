package test.files.dont.export.these;
import com.ksoft.dd.Affinity;
import com.ksoft.dd.Column;
import com.ksoft.dd.Table;
import com.ksoft.dd.TableName;

@Table
public class TestAnnotation implements TestInherit {
    @TableName
    public static final String TABLE_NAME = "mytable";
    
    @Column(type = Affinity.TEXT, notNull = true)
    public static final String COLUMN_1 = "mycol";
    
    @Column(type = Affinity.REAL, defaultValue = "0.0", notNull = true)
    public static final String COLUMN_2 = "myacol";
    
    @Column(type = Affinity.REAL, foreignKeyTable = TABLE_NAME, foreignKeyColumn = COLUMN_1)
    public static final String COLUMN_3 = "mybcol";
}
