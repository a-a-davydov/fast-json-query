/*
 * Copyright (c) 2021-2021. Andrey Davydov (andrey.davydov@gmail.com; https://github.com/a-a-davydov)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 *
 */

package com.github.aadavydov.fastjsonquery.parser;

import com.github.aadavydov.fastjsonquery.filter.JsonPredicateComputer;
import com.github.aadavydov.fastjsonquery.jsonutils.reader.SingleUseReader;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleExpressionParserTest {

    private static final String TEST_ARRAY = "[1, \"aaaaa\", true, \"bbbbb\", 9999, {aaa:5,bbb:\"bbb\",ccc:true,eee:[1,2,\"zzzz\",false]}]";
    private static final String TEST_OBJECT = "{aaa:5,\"bbb\":\"bbb\",ccc:true,eee:[1, \"aaaaa\", true, \"bbbbb\", 9999, {aaa:5,bbb:\"bbb\",ccc:true,eee:[1,2,\"zzzz\",false]}]}";

    public SimpleExpressionParserTest() {
    }

    private void testPredicate(String predicateString, String source, boolean expected) throws Exception {
        SimpleExpressionParser sep = new SimpleExpressionParser();

        JsonPredicateComputer pc = sep.parse(predicateString);

        pc.processJson(new SingleUseReader(new StringReader(source), true));

        assertTrue(predicateString, !pc.getNeedToBeCalculated());
        assertEquals(predicateString, expected, pc.getResult());

    }

    @Test
    public void testComparePredicate() throws Exception {

        testPredicate("@[0] == 1", TEST_ARRAY, true);
        testPredicate("@[0] == 2", TEST_ARRAY, false);

        testPredicate("@[1] == \"aaaaa\"", TEST_ARRAY, true);
        testPredicate("@[1] == \"bbbbb\"", TEST_ARRAY, false);

        testPredicate("@[2] == true", TEST_ARRAY, true);
        testPredicate("@[2] == false", TEST_ARRAY, false);

        testPredicate("@[5].aaa == 5", TEST_ARRAY, true);
        testPredicate("@[5].aaa == 6", TEST_ARRAY, false);

        testPredicate("@[5].aaa <= 6", TEST_ARRAY, true);
        testPredicate("@[5].aaa <= 5", TEST_ARRAY, true);
        testPredicate("@[5].aaa <= 4", TEST_ARRAY, false);

        testPredicate("@[5].aaa < 6", TEST_ARRAY, true);
        testPredicate("@[5].aaa < 5", TEST_ARRAY, false);
        testPredicate("@[5].aaa < 4", TEST_ARRAY, false);

        testPredicate("@[5].aaa >= 6", TEST_ARRAY, false);
        testPredicate("@[5].aaa >= 5", TEST_ARRAY, true);
        testPredicate("@[5].aaa >= 4", TEST_ARRAY, true);

        testPredicate("@[5].aaa > 6", TEST_ARRAY, false);
        testPredicate("@[5].aaa > 5", TEST_ARRAY, false);
        testPredicate("@[5].aaa > 4", TEST_ARRAY, true);

        testPredicate("@[5].aaa != 5", TEST_ARRAY, false);
        testPredicate("@[5].aaa != 6", TEST_ARRAY, true);

        testPredicate("@.eee[0] == 1", TEST_OBJECT, true);
        testPredicate("@.eee[0] == 2", TEST_OBJECT, false);

        testPredicate("@.aaa == 5", TEST_OBJECT, true);

        testPredicate("(@.aaa == 5)", TEST_OBJECT, true);

    }

    @Test
    public void testExistsPredicate() throws Exception {
        testPredicate("(@.aaa)", TEST_OBJECT, true);
        testPredicate("(@.\"aaa\")", TEST_OBJECT, true);
        testPredicate("(@.ddd)", TEST_OBJECT, false);
        testPredicate("@.eee[5]", TEST_OBJECT, true);
        testPredicate("@.eee.[5]", TEST_OBJECT, true);
        testPredicate("@.eee[6]", TEST_OBJECT, false);
        testPredicate("@.eee[5].bbb", TEST_OBJECT, true);
        testPredicate("@.\"eee\".[5].bbb", TEST_OBJECT, true);
        testPredicate("@.eee[6].bbb", TEST_OBJECT, false);
        testPredicate("@.eee[1234]", TEST_OBJECT, false);
        testPredicate("@.eee[0].bbb", TEST_OBJECT, false);
    }

    @Test
    public void testComplexPredicate() throws Exception {

        //AND

        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc == true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("(@.aaa == 5) && @.bbb == \"bbb\" && @.ccc == true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("(@.aaa == 5 && @.bbb == \"bbb\") && @.ccc == true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && (@.ccc == true && @.eee[1]) ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && (@.bbb == \"bbb\" && @.ccc == true) && @.eee[1] ", TEST_OBJECT, true);

        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" && @.ccc == true && @.eee[1] ", TEST_OBJECT, false);
        testPredicate("@.aaa == 5 && @.bbb != \"bbb\" && @.ccc == true && @.eee[1] ", TEST_OBJECT, false);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc != true && @.eee[1] ", TEST_OBJECT, false);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc == true && @.eee[1] == 123 ", TEST_OBJECT, false);

        //OR

        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" || @.ccc != true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" || @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" || @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 || @.bbb != \"bbb\" || @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" || @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, false);

        testPredicate("(@.aaa != 5) || @.bbb != \"bbb\" || @.ccc != true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("(@.aaa != 5 || @.bbb != \"bbb\") || @.ccc != true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" || (@.ccc != true || @.eee[1] )", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || (@.bbb != \"bbb\" || @.ccc != true )|| @.eee[1] ", TEST_OBJECT, true);

        //Complex
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" || @.ccc == true && @.eee[1] ", TEST_OBJECT, true);

        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" || @.ccc == true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb != \"bbb\" || @.ccc == true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" || @.ccc != true && @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" || @.ccc == true && @.eee[1] == 123 ", TEST_OBJECT, true);

        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" || @.ccc == true && @.eee[1] == 123", TEST_OBJECT, false);
        testPredicate("@.aaa == 5 && @.bbb != \"bbb\" || @.ccc != true && @.eee[1] ", TEST_OBJECT, false);
        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" || @.ccc != true && @.eee[1] ", TEST_OBJECT, false);
        testPredicate("@.aaa == 5 && @.bbb != \"bbb\" || @.ccc == true && @.eee[1] == 123 ", TEST_OBJECT, false);

        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc == true || @.eee[1] ", TEST_OBJECT, true);

        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" && @.ccc == true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb != \"bbb\" && @.ccc == true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc != true || @.eee[1] ", TEST_OBJECT, true);
        testPredicate("@.aaa == 5 && @.bbb == \"bbb\" && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 && @.bbb == \"bbb\" && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, false);

        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" && @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, false);

        testPredicate("@.aaa == 5 || @.bbb != \"bbb\" && @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" && @.ccc != true || @.eee[1] == 123 ", TEST_OBJECT, false);
        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, false);
        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb != \"bbb\" && @.ccc != true || @.eee[1]", TEST_OBJECT, true);

        //Parens
        testPredicate("(@.aaa != 5 || @.bbb != \"bbb\") && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, false);

        testPredicate("(@.aaa == 5 || @.bbb != \"bbb\") && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, true);
        testPredicate("(@.aaa != 5 || @.bbb == \"bbb\") && @.ccc == true || @.eee[1] == 123 ", TEST_OBJECT, true);

        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" && (@.ccc != true || @.eee[1] == 123) ", TEST_OBJECT, false);

        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" && (@.ccc == true || @.eee[1] == 123) ", TEST_OBJECT, true);
        testPredicate("@.aaa != 5 || @.bbb == \"bbb\" && (@.ccc != true || @.eee[1]) ", TEST_OBJECT, true);

        testPredicate("(@.aaa != 5 || @.bbb != \"bbb\") && (@.ccc != true || @.eee[1] == 123) ", TEST_OBJECT, false);

        testPredicate("(@.aaa == 5 || @.bbb != \"bbb\") && (@.ccc == true || @.eee[1] == 123) ", TEST_OBJECT, true);
        testPredicate("(@.aaa != 5 || @.bbb == \"bbb\") && (@.ccc != true || @.eee[1]) ", TEST_OBJECT, true);
        testPredicate("(@.aaa == 5 || @.bbb != \"bbb\") && (@.ccc != true || @.eee[1]) ", TEST_OBJECT, true);
        testPredicate("(@.aaa != 5 || @.bbb == \"bbb\") && (@.ccc == true || @.eee[1] == 123) ", TEST_OBJECT, true);

    }

}
