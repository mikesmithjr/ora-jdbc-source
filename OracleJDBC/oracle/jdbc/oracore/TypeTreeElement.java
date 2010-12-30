package oracle.jdbc.oracore;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TypeTreeElement
{
  String schemaName = null;
  String typeName = null;
  String[] childSchemaNames = null;
  String[] childTypeNames = null;
  int size = 0;

  public TypeTreeElement(String paramString1, String paramString2)
  {
    this.schemaName = paramString1;
    this.typeName = paramString2;
  }

  public void putChild(String paramString1, String paramString2, int paramInt)
  {
    int i;
    if (this.childTypeNames == null)
    {
      i = 10;
      if (paramInt > i) i = paramInt * 2;
      this.childSchemaNames = new String[i];
      this.childTypeNames = new String[i];
    }
    if (paramInt >= this.childTypeNames.length)
    {
      i = (paramInt + 10) * 2;
      String[] arrayOfString = new String[i];
      System.arraycopy(this.childSchemaNames, 0, arrayOfString, 0, this.childSchemaNames.length);
      this.childSchemaNames = arrayOfString;
      arrayOfString = new String[i];
      System.arraycopy(this.childTypeNames, 0, arrayOfString, 0, this.childTypeNames.length);
      this.childTypeNames = arrayOfString;
    }
    this.childSchemaNames[paramInt] = paramString1;
    this.childTypeNames[paramInt] = paramString2;
    if (paramInt > this.size) this.size = paramInt;
  }

  public String getChildSchemaName(int paramInt)
  {
    return this.childSchemaNames[paramInt];
  }

  public String getChildTypeName(int paramInt)
  {
    return this.childTypeNames[paramInt];
  }

  public String toString()
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    localPrintWriter.println("schemaName: " + this.schemaName + " typeName: " + this.typeName);
    for (int i = 0; i < this.size; i++)
    {
      localPrintWriter.println("index: " + i + " schema name: " + this.childSchemaNames[i] + " type name: " + this.childTypeNames[i]);
    }

    return localStringWriter.toString();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.TypeTreeElement
 * JD-Core Version:    0.6.0
 */