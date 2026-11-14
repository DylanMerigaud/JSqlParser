/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2026 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.select;

import static net.sf.jsqlparser.test.TestUtils.assertSqlCanBeParsedAndDeparsed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.jupiter.api.Test;

public class OrderByUsingTest {

    @Test
    public void testOrderByUsingIssue2925() throws JSQLParserException {
        String sql = "SELECT * FROM t ORDER BY a ASC NULLS LAST, b USING <";
        PlainSelect select = (PlainSelect) assertSqlCanBeParsedAndDeparsed(sql);
        List<OrderByElement> elements = select.getOrderByElements();
        assertNull(elements.get(0).getUsingOperator());
        assertEquals("<", elements.get(1).getUsingOperator());
        assertFalse(elements.get(1).isAscDescPresent());
    }

    @Test
    public void testOrderByUsingGreaterThanWithNulls() throws JSQLParserException {
        String sql = "SELECT * FROM t ORDER BY b USING > NULLS FIRST";
        PlainSelect select = (PlainSelect) assertSqlCanBeParsedAndDeparsed(sql);
        OrderByElement element = select.getOrderByElements().get(0);
        assertEquals(">", element.getUsingOperator());
        assertEquals(OrderByElement.NullOrdering.NULLS_FIRST, element.getNullOrdering());
    }

    @Test
    public void testOrderByUsingMultiCharacterOperator() throws JSQLParserException {
        String sql = "SELECT * FROM t ORDER BY name USING ~<~";
        PlainSelect select = (PlainSelect) assertSqlCanBeParsedAndDeparsed(sql);
        assertEquals("~<~", select.getOrderByElements().get(0).getUsingOperator());
    }

    @Test
    public void testOrderByUsingInWindowAndAggregate() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "SELECT row_number() OVER (ORDER BY b USING >) FROM t");
        assertSqlCanBeParsedAndDeparsed(
                "SELECT array_agg(b ORDER BY b USING <) FROM t");
    }

    @Test
    public void testOrderByUsingDoesNotTakeAscOrDesc() {
        assertThrows(JSQLParserException.class,
                () -> CCJSqlParserUtil.parse("SELECT * FROM t ORDER BY b ASC USING <"));
        assertThrows(JSQLParserException.class,
                () -> CCJSqlParserUtil.parse("SELECT * FROM t ORDER BY b USING < DESC"));
    }
}
