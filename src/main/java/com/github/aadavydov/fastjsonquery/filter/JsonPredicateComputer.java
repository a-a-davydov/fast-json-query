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

import com.github.aadavydov.fastjsonquery.filter.predicate.ComplexPredicate;
import com.github.aadavydov.fastjsonquery.filter.predicate.PathValueReciever;
import com.github.aadavydov.fastjsonquery.filter.predicate.PathVisitor;
import com.github.aadavydov.fastjsonquery.filter.predicate.Predicate;
import com.github.aadavydov.fastjsonquery.filter.value.*;
import com.github.aadavydov.fastjsonquery.filter.variable.AbstractNode;
import com.github.aadavydov.fastjsonquery.filter.variable.VariableCommander;
import com.github.aadavydov.fastjsonquery.jsonutils.CustomJsonReader;
import com.google.gson.stream.JsonToken;

import java.util.List;

public class JsonPredicateComputer {

    private final Predicate tree;
    private final VariableCommander commander;

    public JsonPredicateComputer(Predicate tree) {
        this.tree = tree;
        commander = new VariableCommander(tree);
    }

    public boolean getResult() {
        return tree.getPredicateValue();
    }

    public boolean getNeedToBeCalculated() {
        return tree.getNeedToBeCalculated();
    }

    private void processVariable(AbstractNode currentPosition, Value unknownValue) throws Exception {
        List<PathValueReciever> recievers = currentPosition.getRecievers();
        if (recievers != null) {
            for (PathValueReciever predicate : recievers) {
                predicate.setValue(unknownValue);
            }
        }
    }

    private void processPath(AbstractNode currentPosition) throws Exception {
        List<PathVisitor> visitors = currentPosition.getVisitors();
        if (visitors != null) {
            for (PathVisitor visitor : visitors) {
                visitor.visitPath();
            }
        }
    }

    public void reset() {
        commander.reset();
        tree.resetNode();
        if (tree instanceof ComplexPredicate) {
            ((ComplexPredicate) tree).traverceChilds((n) -> {
                n.resetNode();
                return true;
            });
        }
    }

    private void processPrimitive(JsonToken peek, CustomJsonReader in) throws Exception {
        AbstractNode currentPosition = commander.valueBegin();

        if (currentPosition != null) {
            processPath(currentPosition);
            Value v;
            switch (peek) {
                case STRING:
                    v = new StringValue(in.nextString());
                    break;
                case NUMBER:
                    v = new NumberValue(in.nextString());
                    break;
                case BOOLEAN:
                    v = BooleanValue.forValue(in.nextBoolean());
                    break;
                case NULL:
                    in.nextNull();
                    v = NullValue.INSTANCE;
                    break;
                default:
                    throw new IllegalStateException();
            }
            processVariable(currentPosition, v);
            commander.valueEnd();
        } else {
            in.skipValue();
        }

    }

    public boolean processJson(CustomJsonReader in) throws Exception {

        if (!tree.getNeedToBeCalculated()) {
            return tree.getPredicateValue();
        }

        String lastName;
        AbstractNode currentPosition;
        do {
            JsonToken peek = in.peek();
            switch (peek) {
                case STRING:
                case NUMBER:
                case BOOLEAN:
                case NULL:
                    processPrimitive(peek, in);
                    break;
                case NAME:
                    lastName = in.nextName();
                    if (!commander.nextName(lastName)) {
                        in.skipValue();
                    }
                    break;
                case BEGIN_OBJECT:
                    currentPosition = commander.valueBegin();

                    if (currentPosition == null) {
                        in.skipValue();
                    } else {
                        processPath(currentPosition);
                        if (currentPosition.hasChildren()) {
                            in.beginObject();
                        } else {
                            in.skipValue();
                            commander.valueEnd();
                        }
                    }

                    break;
                case END_OBJECT:
                    in.endObject();
                    commander.valueEnd();
                    break;
                case BEGIN_ARRAY:
                    currentPosition = commander.valueBegin();

                    if (currentPosition == null) {
                        in.skipValue();
                    } else {
                        processPath(currentPosition);
                        if (currentPosition.hasChildren()) {
                            in.beginArray();
                            commander.beginArray();
                        } else {
                            in.skipValue();
                            commander.valueEnd();
                        }
                    }
                    break;
                case END_ARRAY:
                    in.endArray();
                    commander.endArray();
                    commander.valueEnd();
                    break;
                case END_DOCUMENT:
                    break;
                default:
                    throw new IllegalStateException();
            }
        } while (commander.hasMoreVariables() && tree.getNeedToBeCalculated());

        if (tree.getNeedToBeCalculated()) {
            tree.toDefaultIfNotCalculated();
        }

        return tree.getPredicateValue();

    }

}
