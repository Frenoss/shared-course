package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Lucy Chen
 */
class Table implements Iterable<Row> {
    /** A new Table named NAME whose columns are give by COLUMNTITLES,
     *  which must be distinct (else exception thrown). */
    Table(String name, String[] columnTitles) {
        _name = name;
        List<String> columns = Arrays.asList(columnTitles);
        for (String column : columns) {
            if (columns.indexOf(column) != columns.lastIndexOf(column)) {
                throw error("All columns in %s must be unique.", _name);
            }
        }
        _titles = columnTitles;
        allRows = new HashSet<Row>();
    }

    /** A new Table named NAME whose column names are give by COLUMNTITLES. */
    Table(String name, List<String> columnTitles) {
        this(name, columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    int numColumns() {
        return _titles.length;
    }

    /** Returns my name. */
    String name() {
        return _name;
    }

    /** Used only for testing - returns my rows. */
    HashSet<Row> getRows() {
        return allRows;
    }

    /** Returns a TableIterator over my rows in an unspecified order. */
    TableIterator tableIterator() {
        return new TableIterator(this);
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return allRows.iterator();
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    String title(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    int columnIndex(String title) {
        List<String> titles = Arrays.asList(_titles);
        return titles.indexOf(title);
    }

    /** Return the number of Rows in this table. */
    int size() {
        return allRows.size();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    boolean add(Row row) {
        return allRows.add(row);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(name, columnNames);
            String strRow = input.readLine();
            while (strRow != null) {
                String[] rowEntries = strRow.split(",");
                Row row = new Row(rowEntries);
                table.add(row);
                strRow = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int k = 0; k < numColumns(); k++) {
                output.append(title(k).trim());
                if (k != (numColumns() -1)) {
                    output.append(",");
                }
            }
            output.flush();
            output.println();
            int a = 0;
            for (Row row : allRows) {
                for (int i = 0; i < row.size(); i++) {
                    output.append(row.get(i).trim());
                    if (i != (row.size() - 1)) {
                        output.append(",");
                    }
                }
                output.flush();
                a++;
                if (a < size()) {
                    output.println();
                }
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (Row row : allRows) {
            System.out.print(" ");
            for (int i = 0; i < row.size(); i++) {
                System.out.print(" " + row.get(i));
            }
            System.out.println();
        }
    }

    /** My name. */
    private final String _name;
    /** My column titles. */
    private String[] _titles;
    /** All rows that I currently contain. */
    private HashSet<Row> allRows;
}
