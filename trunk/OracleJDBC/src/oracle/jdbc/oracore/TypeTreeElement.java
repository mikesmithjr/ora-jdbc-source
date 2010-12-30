package oracle.jdbc.oracore;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TypeTreeElement {
    String schemaName = null;
    String typeName = null;
    String[] childSchemaNames = null;
    String[] childTypeNames = null;
    int size = 0;

    public TypeTreeElement(String s, String t) {
        this.schemaName = s;
        this.typeName = t;
    }

    public void putChild(String schemaName, String typeName, int index) {
        if (this.childTypeNames == null) {
            int newSize = 10;
            if (index > newSize)
                newSize = index * 2;
            this.childSchemaNames = new String[newSize];
            this.childTypeNames = new String[newSize];
        }
        if (index >= this.childTypeNames.length) {
            int newSize = (index + 10) * 2;
            String[] temp = new String[newSize];
            System.arraycopy(this.childSchemaNames, 0, temp, 0, this.childSchemaNames.length);
            this.childSchemaNames = temp;
            temp = new String[newSize];
            System.arraycopy(this.childTypeNames, 0, temp, 0, this.childTypeNames.length);
            this.childTypeNames = temp;
        }
        this.childSchemaNames[index] = schemaName;
        this.childTypeNames[index] = typeName;
        if (index > this.size)
            this.size = index;
    }

    public String getChildSchemaName(int index) {
        return this.childSchemaNames[index];
    }

    public String getChildTypeName(int index) {
        return this.childTypeNames[index];
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("schemaName: " + this.schemaName + " typeName: " + this.typeName);
        for (int i = 0; i < this.size; i++) {
            pw.println("index: " + i + " schema name: " + this.childSchemaNames[i] + " type name: "
                    + this.childTypeNames[i]);
        }

        return sw.toString();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.TypeTreeElement JD-Core Version: 0.6.0
 */