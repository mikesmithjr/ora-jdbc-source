package oracle.jdbc.rowset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.Joinable;

public class OracleJoinRowSet extends OracleWebRowSet implements JoinRowSet {
    private static final String MATCH_COLUMN_SUFFIX = "#MATCH_COLUMN";
    private static boolean[] supportedJoins = { false, true, false, false, false };
    private int joinType;
    private Vector addedRowSets;
    private Vector addedRowSetNames;
    private Object lockForJoinActions;

    public OracleJoinRowSet() throws SQLException {
        this.joinType = 1;
        this.addedRowSets = new Vector();
        this.addedRowSetNames = new Vector();
    }

    public synchronized void addRowSet(Joinable rowSet) throws SQLException {
        if (rowSet == null) {
            throw new SQLException("Invalid empty RowSet parameter");
        }
        if (!(rowSet instanceof RowSet)) {
            throw new SQLException("The parameter is not a RowSet instance");
        }
        OracleCachedRowSet addedRowSet = checkAndWrapRowSet((RowSet) rowSet);
        String tblName = getMatchColumnTableName((RowSet) rowSet);

        switch (this.joinType) {
        case 1:
            doInnerJoin(addedRowSet);

            this.addedRowSets.add(rowSet);
            this.addedRowSetNames.add(tblName);
            break;
        case 0:
        case 2:
        case 3:
        case 4:
        default:
            throw new SQLException("Join type is not supported");
        }
    }

    public synchronized void addRowSet(RowSet rowSet, int matchColumnIndex) throws SQLException {
        ((OracleRowSet) rowSet).setMatchColumn(matchColumnIndex);

        addRowSet((Joinable) rowSet);
    }

    public synchronized void addRowSet(RowSet rowSet, String matchColumnName) throws SQLException {
        ((OracleRowSet) rowSet).setMatchColumn(matchColumnName);

        addRowSet((Joinable) rowSet);
    }

    public synchronized void addRowSet(RowSet[] rowSets, int[] matchColumnIndexes)
            throws SQLException {
        if (rowSets.length != matchColumnIndexes.length) {
            throw new SQLException("Number of elements in rowsets is not equal to match columns");
        }
        for (int i = 0; i < rowSets.length; i++) {
            ((OracleRowSet) rowSets[i]).setMatchColumn(matchColumnIndexes[i]);

            addRowSet((Joinable) rowSets[i]);
        }
    }

    public synchronized void addRowSet(RowSet[] rowSets, String[] matchColumnNames)
            throws SQLException {
        if (rowSets.length != matchColumnNames.length) {
            throw new SQLException("Number of elements in rowsets is not equal to match columns");
        }
        for (int i = 0; i < rowSets.length; i++) {
            ((OracleRowSet) rowSets[i]).setMatchColumn(matchColumnNames[i]);

            addRowSet((Joinable) rowSets[i]);
        }
    }

    public Collection getRowSets() throws SQLException {
        return this.addedRowSets;
    }

    public String[] getRowSetNames() throws SQLException {
        Object[] objs = this.addedRowSetNames.toArray();
        String[] names = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            names[i] = ((String) objs[i]);
        }
        return names;
    }

    public CachedRowSet toCachedRowSet() throws SQLException {
        OracleCachedRowSet ocrs = (OracleCachedRowSet) createCopy();

        ocrs.setCommand("");

        return ocrs;
    }

    public int getJoinType() {
        return this.joinType;
    }

    public boolean supportsCrossJoin() {
        return supportedJoins[0];
    }

    public boolean supportsInnerJoin() {
        return supportedJoins[1];
    }

    public boolean supportsLeftOuterJoin() {
        return supportedJoins[2];
    }

    public boolean supportsRightOuterJoin() {
        return supportedJoins[3];
    }

    public boolean supportsFullJoin() {
        return supportedJoins[4];
    }

    public void setJoinType(int jtype) throws SQLException {
        if (jtype != 1) {
            throw new SQLException("Join type is not supported");
        }
        this.joinType = jtype;
    }

    public synchronized String getWhereClause() throws SQLException {
        if (this.addedRowSets.size() < 2) {
            return "WHERE";
        }
        StringBuffer whereClause = new StringBuffer();
        whereClause.append("WHERE\n");

        OracleRowSet leftRowSet = (OracleRowSet) this.addedRowSets.get(0);
        int[] matchIndexesLeft = leftRowSet.getMatchColumnIndexes();
        ResultSetMetaData leftMd = leftRowSet.getMetaData();
        String leftTable = leftRowSet.getTableName();

        for (int i = 1; i < this.addedRowSets.size(); i++) {
            if (i > 1) {
                whereClause.append("\nAND\n");
            }

            OracleRowSet rightRowSet = (OracleRowSet) this.addedRowSets.get(i);
            int[] matchIndexesRight = rightRowSet.getMatchColumnIndexes();
            ResultSetMetaData rightMd = rightRowSet.getMetaData();
            String rightTable = rightRowSet.getTableName();

            for (int j = 0; j < matchIndexesLeft.length; j++) {
                if (j > 0) {
                    whereClause.append("\nAND\n");
                }

                whereClause.append("(" + leftTable + "."
                        + leftMd.getColumnName(matchIndexesLeft[j]) + " = " + rightTable + "."
                        + rightMd.getColumnName(matchIndexesRight[j]) + ")");
            }

            leftRowSet = rightRowSet;
            matchIndexesLeft = matchIndexesRight;
            leftMd = rightMd;
            leftTable = rightTable;
        }

        whereClause.append(";");

        return whereClause.toString();
    }

    private void doInnerJoin(OracleCachedRowSet addedRowSet) throws SQLException {
        if (this.addedRowSets.isEmpty()) {
            setMetaData((RowSetMetaData) addedRowSet.getMetaData());
            populate(addedRowSet);

            setMatchColumn(addedRowSet.getMatchColumnIndexes());
        } else {
            Vector newRows = new Vector(100);
            RowSetMetaData newMd = new OracleRowSetMetaData(10);

            int[] matchIndexesLeft = getMatchColumnIndexes();
            int[] matchIndexesRight = addedRowSet.getMatchColumnIndexes();

            int newColCount = getMetaData().getColumnCount()
                    + addedRowSet.getMetaData().getColumnCount() - matchIndexesRight.length;

            newMd.setColumnCount(newColCount);

            String tableNameForMatchColumn = getTableName() + "#" + addedRowSet.getTableName();

            for (int indexLeft = 1; indexLeft <= this.colCount; indexLeft++) {
                boolean isMatchColumn = false;
                for (int j = 0; j < matchIndexesLeft.length; j++) {
                    if (indexLeft != matchIndexesLeft[j])
                        continue;
                    isMatchColumn = true;
                    break;
                }

                setNewColumnMetaData(indexLeft, newMd, indexLeft,
                                     (RowSetMetaData) this.rowsetMetaData, isMatchColumn,
                                     tableNameForMatchColumn);
            }

            RowSetMetaData addedRowSetMd = (RowSetMetaData) addedRowSet.getMetaData();

            int addedRowSetColCount = addedRowSetMd.getColumnCount();

            int indexRightNew = this.colCount + 1;
            int[] indexesRightNew = new int[addedRowSetColCount];

            for (int indexRight = 1; indexRight <= addedRowSetColCount; indexRight++) {
                boolean isMatchColumn = false;
                for (int j = 0; j < matchIndexesRight.length; j++) {
                    if (indexRight != matchIndexesLeft[j])
                        continue;
                    isMatchColumn = true;
                    break;
                }

                if (!isMatchColumn) {
                    setNewColumnMetaData(indexRightNew, newMd, indexRight, addedRowSetMd,
                                         isMatchColumn, tableNameForMatchColumn);

                    indexesRightNew[(indexRight - 1)] = indexRightNew;
                    indexRightNew++;
                } else {
                    indexesRightNew[(indexRight - 1)] = -1;
                }
            }

            beforeFirst();

            int addedRowSetRowCount = addedRowSet.size();
            boolean matchFound = false;

            for (int leftRow = 1; leftRow <= this.rowCount; leftRow++) {
                next();
                addedRowSet.beforeFirst();

                for (int rightRow = 1; rightRow <= addedRowSetRowCount; rightRow++) {
                    addedRowSet.next();

                    matchFound = true;
                    for (int i = 0; i < matchIndexesLeft.length; i++) {
                        Object leftObj = getObject(matchIndexesLeft[i]);
                        Object rightObj = addedRowSet.getObject(matchIndexesRight[i]);
                        if (leftObj.equals(rightObj))
                            continue;
                        matchFound = false;
                        break;
                    }

                    if (!matchFound)
                        continue;
                    OracleRow newRow = new OracleRow(newColCount, true);

                    for (int colLeft = 1; colLeft <= this.colCount; colLeft++) {
                        newRow.updateObject(colLeft, getObject(colLeft));
                    }

                    for (int colRight = 1; colRight <= addedRowSetColCount; colRight++) {
                        if (indexesRightNew[(colRight - 1)] == -1)
                            continue;
                        newRow.updateObject(indexesRightNew[(colRight - 1)], addedRowSet
                                .getObject(colRight));
                    }

                    newRows.add(newRow);
                }

            }

            this.rows = newRows;
            this.presentRow = 0;
            this.rowCount = this.rows.size();
            setMetaData(newMd);
        }
    }

    private void setNewColumnMetaData(int newColIndex, RowSetMetaData newMd, int origColIndex,
            RowSetMetaData origMd, boolean isMatchColumn, String tableNameForMatchColumn)
            throws SQLException {
        newMd.setAutoIncrement(newColIndex, origMd.isAutoIncrement(origColIndex));
        newMd.setCaseSensitive(newColIndex, origMd.isCaseSensitive(origColIndex));
        newMd.setCatalogName(newColIndex, origMd.getCatalogName(origColIndex));
        newMd.setColumnDisplaySize(newColIndex, origMd.getColumnDisplaySize(origColIndex));

        if (isMatchColumn) {
            newMd.setColumnName(newColIndex, origMd.getColumnName(newColIndex) + "#MATCH_COLUMN");
        } else {
            newMd.setColumnName(newColIndex, origMd.getColumnName(origColIndex));
        }

        newMd.setColumnLabel(newColIndex, newMd.getColumnName(origColIndex));

        newMd.setColumnType(newColIndex, origMd.getColumnType(origColIndex));
        newMd.setColumnTypeName(newColIndex, origMd.getColumnTypeName(origColIndex));
        newMd.setCurrency(newColIndex, origMd.isCurrency(origColIndex));
        newMd.setNullable(newColIndex, origMd.isNullable(origColIndex));
        newMd.setPrecision(newColIndex, origMd.getPrecision(origColIndex));
        newMd.setScale(newColIndex, origMd.getScale(origColIndex));
        newMd.setSchemaName(newColIndex, origMd.getSchemaName(origColIndex));
        newMd.setSearchable(newColIndex, origMd.isSearchable(origColIndex));
        newMd.setSigned(newColIndex, origMd.isSigned(origColIndex));

        if (isMatchColumn) {
            newMd.setTableName(newColIndex, tableNameForMatchColumn);
        } else {
            newMd.setTableName(newColIndex, origMd.getTableName(origColIndex));
        }
    }

    private OracleCachedRowSet checkAndWrapRowSet(RowSet rowSet) throws SQLException {
        OracleCachedRowSet ocrs;
        if ((rowSet instanceof OracleCachedRowSet)) {
            ocrs = (OracleCachedRowSet) rowSet;
        } else if ((rowSet instanceof OracleJDBCRowSet)) {
            ocrs = new OracleCachedRowSet();
            ocrs.populate(rowSet);

            int[] matchIndexes = ((OracleJDBCRowSet) rowSet).getMatchColumnIndexes();
            ocrs.setMatchColumn(matchIndexes);
        } else {
            throw new SQLException("Third-party RowSet Join not yet supported");
        }
        return ocrs;
    }

    private String getMatchColumnTableName(RowSet rowset) throws SQLException {
        String tblName = null;
        if ((rowset instanceof OracleRowSet)) {
            tblName = ((OracleRowSet) rowset).getTableName();
        }

        return tblName;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleJoinRowSet JD-Core Version: 0.6.0
 */