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
import com.github.aadavydov.fastjsonquery.filter.computer.op.ValueOperator;
import com.github.aadavydov.fastjsonquery.filter.computer.struct.PredicateOperator;
import com.github.aadavydov.fastjsonquery.filter.predicate.*;
import com.github.aadavydov.fastjsonquery.filter.value.*;
import com.github.aadavydov.fastjsonquery.filter.variable.Path;
import com.github.aadavydov.fastjsonquery.grammars.QueryLexer;
import com.github.aadavydov.fastjsonquery.grammars.QueryParser;
import com.github.aadavydov.fastjsonquery.jsonutils.reader.CharBufferJsonReader;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SimpleExpressionParser {

    private final CharBufferJsonReader cjr = new CharBufferJsonReader(StandardCharsets.UTF_8);

    {
        cjr.setLenient(true);
    }

    public static JsonPredicateComputer parseExpression(String inputText) throws IOException {
        return (new SimpleExpressionParser()).parse(inputText);
    }

    public JsonPredicateComputer parse(String inputText) throws IOException {
        return new JsonPredicateComputer(buildPredicate(parseToContext(inputText)));
    }

    private Predicate buildPredicate(QueryParser.FilterContext filter) throws IOException {
        if (filter == null || filter.getChildCount() != 2) {
            return null;
        }

        ParseTree child = filter.getChild(0);

        if (child instanceof QueryParser.Logical_exprContext) {
            return buildPredicate((QueryParser.Logical_exprContext) child, null);
        }

        return null;
    }

    private Predicate buildPredicate(QueryParser.Logical_exprContext logicExpr, ComplexPredicate parent) throws IOException {

        QueryParser.Comparison_exprContext comparison_expr = logicExpr.comparison_expr();

        if (comparison_expr != null) { //paren expr

            Predicate compPredicate = buildPredicate(comparison_expr);
            if (parent != null) {
                parent.addChild(compPredicate);
            }
            return compPredicate;

        }

        TerminalNode LPAREN = logicExpr.LPAREN();

        if (LPAREN != null) { // paren expr
            Predicate compPredicate = buildPredicate(logicExpr.logical_expr(0), null);

            if (parent != null) {
                parent.addChild(compPredicate);
            }

            return compPredicate;
        }

        QueryParser.PathContext path = logicExpr.path();

        if (path != null) {
            ExistsPredicate ep = new ExistsPredicate(clearPath(path));

            if (parent != null) {
                parent.addChild(ep);
            }

            return ep;
        }

        QueryParser.AndContext and = logicExpr.and();
        QueryParser.OrContext or = logicExpr.or();

        PredicateOperator forKey = PredicateOperator.forKey(and != null ? and.getText() : or.getText());

        if (parent != null && parent.getComputer() == forKey.getComputer()) {
            buildPredicate(logicExpr.logical_expr(0), parent);
            buildPredicate(logicExpr.logical_expr(1), parent);
            return parent;
        } else {
            ComplexPredicate complexPredicate = new ComplexPredicate(forKey);
            buildPredicate(logicExpr.logical_expr(0), complexPredicate);
            buildPredicate(logicExpr.logical_expr(1), complexPredicate);
            if (parent != null) {
                parent.addChild(complexPredicate);
            }
            return complexPredicate;
        }

    }

    private Operand createOperand(QueryParser.PathContext poc) throws IOException {
        return new Operand(clearPath(poc));
    }

    private Value createValue(ParseTree node) throws IOException {

        if (node instanceof QueryParser.Num_operandContext) {
            return new NumberValue(node.getText());
        }

        if (node instanceof QueryParser.Null_operandContext) {
            return NullValue.INSTANCE;
        }

        if (node instanceof QueryParser.Str_operandContext) {
            return new StringValue(clearJsonString(node.getText()));
        }

        if (node instanceof QueryParser.Bool_operandContext) {
            return BooleanValue.forValue(Boolean.parseBoolean(node.getText()));
        }

        throw new IllegalArgumentException();
    }

    private String clearJsonString(String jsonString) throws IOException {
        cjr.setup(jsonString);
        cjr.peek();
        return cjr.nextString();
    }

    private Path clearPath(QueryParser.PathContext poc) throws IOException {

        List<ParseTree> children = poc.children;
        String[] elements = new String[children.size() - 1];

        int i = -1;
        for (ParseTree c : children) {
            if (i < 0) {
                i++;
                continue;
            }

            String text = c.getText();

            if (text.charAt(0) == '.') {
                text = text.substring(1);
            }

            if (text.charAt(0) == '\"') {
                text = clearJsonString(text);
            }

            elements[i] = text;
            i++;
        }

        return new Path(elements);
    }

    private Predicate buildPredicate(QueryParser.Comparison_exprContext compExpr) throws IOException {

        ParseTree left = compExpr.children.get(0).getChild(0);
        ParseTree operand = compExpr.children.get(1);
        ParseTree right = compExpr.children.get(2).getChild(0);

        ValueOperator valueOperator = ValueOperator.forKey(operand.getText());

        Predicate res = null;

        if (left instanceof QueryParser.PathContext) {
            if (right instanceof QueryParser.PathContext) {
                res = new BinaryPredicate(createOperand((QueryParser.PathContext) left), createOperand((QueryParser.PathContext) right), valueOperator);
            } else {
                res = new LeftPredicate(createOperand((QueryParser.PathContext) left), createValue(right), valueOperator);
            }
        } else {
            if (right instanceof QueryParser.PathContext) {
                res = new RightPredicate(createValue(left), createOperand((QueryParser.PathContext) right), valueOperator);
            } else {
                throw new IllegalStateException("Constant expressions not supported yet.");
            }
        }

        return res;
    }

    private QueryParser.FilterContext parseToContext(String inputText) {

        CodePointCharStream fromString = CharStreams.fromString(inputText);

        QueryLexer lexer = new QueryLexer(fromString);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ErrorListener.INSTANCE);

        QueryParser parser = new QueryParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(ErrorListener.INSTANCE);

        QueryParser.FilterContext result = parser.filter();

        return result;
    }

}
