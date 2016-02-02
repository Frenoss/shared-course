package db61b;

import java.util.List;
import static java.util.Arrays.asList;

import static db61b.Utils.*;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Lucy Chen */
class Condition {

    /** Internally, we represent our relation as a 3-bit value whose
     *  bits denote whether the relation allows the left value to be
     *  greater than the right (GT), equal to it (EQ),
     *  or less than it (LT). */
    private static final int GT = 1, EQ = 2, LT = 4;

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _col2 = col2;
        if (allRelations.contains(relation)) {
            _relation = intRelations.get(allRelations.indexOf(relation));
        } else {
            throw error("%s is not a valid relation.", relation);
        }
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, new Literal(val2));
    }

    /** Assuming that ROWS are rows from the respective tables from which
     *  my columns are selected, returns the result of performing the test I
     *  denote. */
    boolean test() {
        int temp = _col1.value().compareTo(_col2.value());
        if ((temp > 0) && ((_relation == GT) || (_relation == (GT | EQ)))) {
            return true;
        }
        if ((temp == 0) && ((_relation == EQ) || (_relation == (GT | EQ))
            || (_relation == (LT | EQ)))) {
            return true;
        }
        if ((temp < 0) && ((_relation == LT) || (_relation == (LT | EQ)))) {
            return true;
        }
        if ((temp != 0) && (_relation == ~EQ)) {
            return true;
        }
        return false;
    }

    /** Return true iff all CONDITIONS are satified. */
    static boolean test(List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (!condition.test()) {
                return false;
            }
        }
        return true;
    }

    /** The left value in a relation. */
    private Column _col1;
    /** The right value in a relation. */
    private Column _col2;
    /** The relation symbol in a relation. */
    private int _relation;
    /** All possible relations. */
    private List<String> allRelations = asList("<", ">", "=", "<=", ">=", "!=");
    /** Bit representations corresponding to the relations in allRelations. */
    private List<Integer> intRelations = asList(LT, GT, EQ, LT | EQ, GT | EQ, ~EQ);
}
