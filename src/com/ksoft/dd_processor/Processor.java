package com.ksoft.dd_processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.ksoft.dd.Table;

@SupportedAnnotationTypes("com.ksoft.dd.Table")
public class Processor extends AbstractProcessor {
    public static final String OUTPUT_CLASS_NAME = "DDOutput";
    public static final String OUTPUT_PACKAGE = "com.ksoft.dd";
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            try {
                PrintWriter wr = new PrintWriter(processingEnv.getFiler()
                        .createSourceFile(OUTPUT_PACKAGE + '.' + OUTPUT_CLASS_NAME)
                        .openOutputStream());
                wr.append("package "
                          + OUTPUT_PACKAGE + ";\npublic class " + OUTPUT_CLASS_NAME + " {\n");
                
                Messager msg = processingEnv.getMessager();
                
                StringBuilder tableList = new StringBuilder();
                
                for (Element elt : roundEnv.getRootElements()) {
                    if (elt.getAnnotation(Table.class) == null) {
                        continue;
                    }
                    
                    TableData table = new TableData();
                    TableVisitor visit = new TableVisitor(msg, processingEnv.getTypeUtils());
                    
                    elt.accept(visit, table);
                    
                    if (table.name == null) {
                        msg.printMessage(
                                Kind.ERROR,
                                "You must have a constant field annotated with @TableName that represents the name of the table",
                                elt);
                    } else if (!table.hasColumns()) {
                        msg.printMessage(Kind.ERROR,
                                "You must specify at least one column with @Column", elt);
                    } else {
                        // Valid Table!
                        
                        tableList.append(", CREATE_").append(table.name);
                        wr.append("\nprivate static final String CREATE_").append(table.name)
                                .append(" = \"");
                        table.writeCreateSQL(wr);
                        wr.append("\";\n");
                        table.writeColumnArray(wr);
                    }
                }
                
                // We have to do the substring starting at char 1 to ignore the initial comma
                wr.append("public static final String[] TABLES = {").append(tableList.substring(1))
                        .append("};\n}");
                wr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
}
