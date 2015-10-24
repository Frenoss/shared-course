package db61b;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/** Tests basic functionality including:
 *  1. The Row class
 *  2. The Table class
 *  3. The TableIterator class
 *  4. The Condition class
 *  @author Lucy Chen
 */
public class BasicTests {

    Literal l1 = new Literal("a");
    Literal l2 = new Literal("b");
    Literal l3 = new Literal("c");
    Literal l4 = new Literal("1");
    Literal l5 = new Literal("2");
    Literal l6 = new Literal("3");

    String name1 = "Some Table";
    String[] columnTitles = {"Title 1", "Title 2", "Title 3"};
    Table t1 = new Table(name1, columnTitles);

    @Test
    public void testRow() {

        /** Tests the Row constructor that takes in a list of columns. */
        List<Column> columns = new ArrayList<Column>();
        columns.add(l1);
        columns.add(l2);
        columns.add(l3);
        Row r = new Row(columns);

        assertEquals(3, r.size());
        assertEquals("a", r.get(0));
        assertEquals("b", r.get(1));
        assertEquals("c", r.get(2));
    }

    @Test
    public void testTable() {

        /** Tests the Table constructor. */
        assertEquals("Some Table", t1.name());
        assertEquals("Title 1", t1.title(0));
        assertEquals("Title 2", t1.title(1));
        assertEquals("Title 3", t1.title(2));

        String name2 = "Some Other Table";
        String[] badColumnTitles = {"Title 1", "Title 2", "Title 2", "Title 3"};
        boolean thrown = false;
        try {
            Table t2 = new Table(name2, badColumnTitles);
        } catch (DBException e) {
            thrown = true;
        }

        assertTrue(thrown);

        /** Tests the columnIndex method. */
        assertEquals(0, t1.columnIndex("Title 1"));
        assertEquals(2, t1.columnIndex("Title 3"));
        assertEquals(-1, t1.columnIndex("DNE"));

        /** Tests the size method. */
        assertEquals(0, t1.size());

        /** Tests the add method. */        
        List<Column> columns = new ArrayList<Column>();
        List<Column> moreColumns = new ArrayList<Column>();
        columns.add(l1);
        columns.add(l2);
        columns.add(l3);
        moreColumns.add(l4);
        moreColumns.add(l5);
        moreColumns.add(l6);
        Row r = new Row(columns);
        boolean added = t1.add(r);

        assertTrue(added);
        assertEquals(1, t1.size());

        boolean added2 = t1.add(r);
        
        assertFalse(added2);
        assertEquals(1, t1.size());

        Row s = new Row(moreColumns);
        t1.add(s);

        assertEquals(2, t1.size());

        /** Tests the iterator method. */
        Iterator<Row> iter = t1.iterator();
        HashSet<Row> origRows = t1.getRows();
        HashSet<Row> iterRows = new HashSet<Row>(t1.size());

        while (iter.hasNext()) {
            iterRows.add(iter.next());
        }

        assertTrue(origRows.containsAll(iterRows));

        /** Tests the writeTable method - output must be manually compared. */
        t1.writeTable(t1.name());

        /** Tests the readTable method - uses file produced by the
         *  writeTable test. */
        Table test = Table.readTable("Some Table");

        assertEquals(t1.name(), test.name());
        assertEquals(t1.size(), test.size());
        assertTrue(test.getRows().containsAll(t1.getRows()));
        assertEquals(t1.title(0), test.title(0));
        assertEquals(t1.title(1), test.title(1));
        assertEquals(t1.title(2), test.title(2));

        /** Tests the print method - output must be manually compared. */
        t1.print();
    }

    @Test
    public void testTableIterator() {

        Literal l7 = new Literal("I");
        Literal l8 = new Literal("like");
        Literal l9 = new Literal("pie.");
        List<Column> columns = new ArrayList<Column>();
        List<Column> moreColumns = new ArrayList<Column>();
        List<Column> someMoreColumns = new ArrayList<Column>();
        columns.add(l1);
        columns.add(l2);
        columns.add(l3);
        moreColumns.add(l4);
        moreColumns.add(l5);
        moreColumns.add(l6);
        someMoreColumns.add(l7);
        someMoreColumns.add(l8);
        someMoreColumns.add(l9);
        Row row1 = new Row(columns);
        Row row2 = new Row(moreColumns);
        Row row3 = new Row(someMoreColumns);
        t1.add(row1);
        t1.add(row2);
        t1.add(row3);
        TableIterator tIter = t1.tableIterator();

        /** Tests the next method. */
        assertTrue(t1.getRows().contains(tIter.getCurrRow()));

        tIter.next();
        assertTrue(t1.getRows().contains(tIter.getCurrRow()));

        tIter.next();
        assertTrue(t1.getRows().contains(tIter.getCurrRow()));

        tIter.next();
        assertFalse(t1.getRows().contains(tIter.getCurrRow()));

        /** Tests the reset method. */
        tIter.reset();
        assertNotNull(tIter.getCurrRow());
    }

    @Test
    public void testCondition() {

        Literal l41 = new Literal("1");
        Literal l51 = new Literal("2");
        Literal l61 = new Literal("3");
        Condition cond1 = new Condition(l41, ">", l51);
        Condition cond2 = new Condition(l61, "<=", l51);
        Condition cond3 = new Condition(l41, "=", l4);
        Condition cond4 = new Condition(l41, "<", l61);
        Condition cond5 = new Condition(l61, ">=", l6);
        Condition cond6 = new Condition(l51, "!=", l41);
        List<Condition> condList = new ArrayList<Condition>();
        condList.add(cond1);
        condList.add(cond2);
        condList.add(cond3);
        condList.add(cond4);
        condList.add(cond5);
        condList.add(cond6);

        /** Tests the Condition constructor with an invalid relation. */
        boolean thrown = false;
        try {
            Condition badCond = new Condition(l41, "greater than", l51);
        } catch (DBException e) {
            thrown = true;
        }

        assertTrue(thrown);

        /** Tests both test methods. */
        assertFalse(cond1.test());
        assertFalse(cond2.test());
        assertTrue(cond3.test());
        assertTrue(cond4.test());
        assertTrue(cond5.test());
        assertTrue(cond6.test());

        assertFalse(Condition.test(condList));
    }
    
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BasicTests.class));
    }
}
