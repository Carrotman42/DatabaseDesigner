package test.files.dont.export.these;
import com.ksoft.dd.Affinity;
import com.ksoft.dd.Column;

public interface TestInherit extends TestInheritLevel, TestMultipleInterface {
    @Column(type = Affinity.TEXT, notNull = true)
    public static final String SHARED_COLUMN = "shared_col";
}
