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

package com.github.aadavydov.fastjsonquery.filter;

import com.github.aadavydov.fastjsonquery.filter.computer.op.ValueOperator;
import com.github.aadavydov.fastjsonquery.filter.computer.struct.PredicateOperator;
import com.github.aadavydov.fastjsonquery.filter.predicate.*;
import com.github.aadavydov.fastjsonquery.filter.value.NumberValue;
import com.github.aadavydov.fastjsonquery.filter.value.StringValue;
import com.github.aadavydov.fastjsonquery.jsonutils.reader.SingleUseReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class JsonPredicateComputerTest {

    private static final String INT_ARRAY = "[1, 2, 2, 4, 5]";
    private static final String STRING_ARRAY = "[1, \"aaaaa\", 3, \"bbbbb\", 5]";
    private static final String OBJECT_ARRAY = "[1, {\"a\":1,\"b\":\"b\",\"c\":[1,2,3]}, 3, \"bbbbb\", 5]";
    private static final String LONG_OBJECT_ARRAY1 = "[\"5860887874820\", \"Check.\", \"Some long test string to test how it works with long strings because work with long strings may be differ from work with short strings\", 4, 4.0]";
    private static final String LONG_OBJECT_ARRAY2 = "[\"5860887874820\", \"Check.\", \"Some long test string to test how it works with long strings because work with long strings may be differ from work with short strings\", 4, 7.0]";

    static {
        JsonParser p = new JsonParser();
        try {
            p.parse(INT_ARRAY);
            p.parse(STRING_ARRAY);
            p.parse(OBJECT_ARRAY);
        } catch (JsonSyntaxException ex) {
            System.out.println("MALFORMED ETALONS");
            throw new IllegalStateException(ex);
        }
    }

    @Test
    public void testPerformance() throws Exception {

        Predicate p1 = new LeftPredicate(new Operand("[1]"), new StringValue("Check."), ValueOperator.EQ);
        Predicate p2 = new LeftPredicate(new Operand("[3]"), new NumberValue("6"), ValueOperator.GT);
        Predicate p3 = new LeftPredicate(new Operand("[4]"), new NumberValue("6"), ValueOperator.GT);

        JsonPredicateComputer pc = new JsonPredicateComputer(p1);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));

        pc = new JsonPredicateComputer(p2);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));

        pc = new JsonPredicateComputer(p3);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));


        ComplexPredicate and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(p1);

        ComplexPredicate or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(p2);
        or.addChild(p3);

        and.addChild(or);

        pc = new JsonPredicateComputer(and);

        long t1 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            pc.reset();
            Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
            pc.reset();
            Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));
        }

        long t2 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            pc.reset();
            Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
            pc.reset();
            Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));
        }

        long t3 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            pc.reset();
            Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
            pc.reset();
            Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));
        }

        long t4 = System.currentTimeMillis();

        System.out.println((t2 - t1) / 1000.0);
        System.out.println((t3 - t2) / 1000.0);
        System.out.println((t4 - t3) / 1000.0);

    }

    @Test
    public void testPerformance2() throws Exception {

        Predicate p1 = new LeftPredicate(new Operand("[1]"), new StringValue("Check."), ValueOperator.EQ);
        Predicate p2 = new LeftPredicate(new Operand("[3]"), new NumberValue("6"), ValueOperator.GT);
        Predicate p3 = new LeftPredicate(new Operand("[4]"), new NumberValue("6"), ValueOperator.GT);

        JsonPredicateComputer pc = new JsonPredicateComputer(p1);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));

        pc = new JsonPredicateComputer(p2);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));

        pc = new JsonPredicateComputer(p3);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY1))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(LONG_OBJECT_ARRAY2))));


        ComplexPredicate and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(p1);

        ComplexPredicate or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(p2);
        or.addChild(p3);

        and.addChild(or);

        pc = new JsonPredicateComputer(and);

        JsonFilter jf = new JsonFilter(pc);

        byte[] bytes1 = LONG_OBJECT_ARRAY1.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = LONG_OBJECT_ARRAY2.getBytes(StandardCharsets.UTF_8);

        long t1 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            Assert.assertEquals(false, jf.apply(bytes1, true));
            Assert.assertEquals(true, jf.apply(bytes2, true));
        }

        long t2 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            Assert.assertEquals(false, jf.apply(bytes1, true));
            Assert.assertEquals(true, jf.apply(bytes2, true));
        }

        long t3 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            Assert.assertEquals(false, jf.apply(bytes1, true));
            Assert.assertEquals(true, jf.apply(bytes2, true));
        }

        long t4 = System.currentTimeMillis();

        System.out.println((t2 - t1) / 1000.0);
        System.out.println((t3 - t2) / 1000.0);
        System.out.println((t4 - t3) / 1000.0);

    }

    @Test
    public void testComplexPredicateAND() throws Exception {

        ComplexPredicate or = new ComplexPredicate(PredicateOperator.OR);
        or.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GT));

        ComplexPredicate and = new ComplexPredicate(PredicateOperator.AND);

        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));

        or.addChild(and);

        JsonPredicateComputer pc = new JsonPredicateComputer(or);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        or = new ComplexPredicate(PredicateOperator.OR);
        or.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GT));

        and = new ComplexPredicate(PredicateOperator.AND);

        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));

        or.addChild(and);

        pc = new JsonPredicateComputer(or);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        or = new ComplexPredicate(PredicateOperator.OR);
        or.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GT));

        and = new ComplexPredicate(PredicateOperator.AND);

        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));

        or.addChild(and);

        pc = new JsonPredicateComputer(or);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(or);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        or = new ComplexPredicate(PredicateOperator.OR);
        or.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GT));

        and = new ComplexPredicate(PredicateOperator.AND);

        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        and.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));

        or.addChild(and);

        pc = new JsonPredicateComputer(or);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
    }

    @Test
    public void testComplexPredicateOR() throws Exception {

        ComplexPredicate and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GT));

        ComplexPredicate or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));

        and.addChild(or);

        JsonPredicateComputer pc = new JsonPredicateComputer(and);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GT));

        or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));

        and.addChild(or);

        pc = new JsonPredicateComputer(and);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GT));

        or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("1"), ValueOperator.GT));

        and.addChild(or);

        pc = new JsonPredicateComputer(and);
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        and = new ComplexPredicate(PredicateOperator.AND);
        and.addChild(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GT));

        or = new ComplexPredicate(PredicateOperator.OR);

        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));
        or.addChild(new LeftPredicate(new Operand("[2]"), new NumberValue("3"), ValueOperator.GT));

        and.addChild(or);

        pc = new JsonPredicateComputer(and);
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

    }

    @Test
    public void testBiPredicate() throws Exception {
        JsonPredicateComputer pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[1]"), new Operand("[0]"), ValueOperator.GT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[1]"), new Operand("[1]"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[1]"), new Operand("[2]"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[1]"), new Operand("[2]"), ValueOperator.EQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[1]"), new Operand("[3]"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new BinaryPredicate(new Operand("[3]"), new Operand("[1]"), ValueOperator.GT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(STRING_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(STRING_ARRAY))));

    }

    @Test
    public void testExistsPredicate() throws Exception {
        JsonPredicateComputer pc = new JsonPredicateComputer(new ExistsPredicate("[0]"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new ExistsPredicate("[1]"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new ExistsPredicate("[4]"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new ExistsPredicate("[5]"));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new ExistsPredicate("[1].a"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc = new JsonPredicateComputer(new ExistsPredicate("[1].d"));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));

        pc = new JsonPredicateComputer(new ExistsPredicate("[1].c"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));

        pc = new JsonPredicateComputer(new ExistsPredicate("[1].c.[1]"));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));

        pc = new JsonPredicateComputer(new ExistsPredicate("[1].c.[3]"));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(OBJECT_ARRAY))));
    }

    @Test
    public void testLeftPredicate() throws Exception {

        JsonPredicateComputer pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.GTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.GTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.GTE));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.LT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.LT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.LT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.LTE));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.LTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.LTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.EQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.EQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.EQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("1"), ValueOperator.NEQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("2"), ValueOperator.NEQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new LeftPredicate(new Operand("[1]"), new NumberValue("3"), ValueOperator.NEQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
    }

    @Test
    public void testRightPredicate() throws Exception {

        JsonPredicateComputer pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.GT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.GT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.GTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.GTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.GTE));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.LT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.LT));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.LT));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.LTE));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.LTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.LTE));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.EQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.EQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.EQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));

        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("3"), new Operand("[1]"), ValueOperator.NEQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("2"), new Operand("[1]"), ValueOperator.NEQ));
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(false, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc = new JsonPredicateComputer(new RightPredicate(new NumberValue("1"), new Operand("[1]"), ValueOperator.NEQ));
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
        pc.reset();
        Assert.assertEquals(true, pc.processJson(new SingleUseReader(new StringReader(INT_ARRAY))));
    }

}
