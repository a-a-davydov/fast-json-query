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

package com.github.aadavydov.fastjsonquery.filter.variable;

import com.github.aadavydov.fastjsonquery.filter.predicate.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class VariableCommander {

    private final RootNode tree = new RootNode();
    private final int[] indexStack;
    private AbstractNode currentPosition = null;
    private int maxIndexDepth = 0;
    private int indexPointer = 0;
    private String nextName = null;

    public VariableCommander(Predicate predicateTree) {
        buildTree(predicateTree);
        indexStack = new int[maxIndexDepth];
        Arrays.setAll(indexStack, (i) -> -1);
    }

    public void reset() {
        indexPointer = 0;
        currentPosition = null;
        Arrays.setAll(indexStack, (i) -> -1);
    }

    private void buildTree(Predicate predicateTree) {

        List<PathValueReciever> valueRecievers = new ArrayList<>();
        List<PathVisitor> visitors = new ArrayList<>();

        ComplexPredicate.Travercer t = (n) -> {
            if (n instanceof PathPredicate) {
                PathPredicate pp = (PathPredicate) n;
                pp.exportValueRecievers(valueRecievers);
                pp.exportVisitors(visitors);
                return false;
            }
            return true;
        };

        if (t.visit(predicateTree)) {
            ((ComplexPredicate) predicateTree).traverceChilds(t);
        }

        for (PathValueReciever vr : valueRecievers) {
            addPath(vr);
        }
        for (PathVisitor pv : visitors) {
            addPath(pv);
        }

    }

    private AbstractNode nodeForPath(Path path) {

        if (path != null && path.getPath() != null) {
            String[] names = path.getPath();

            int namesLen = names.length;
            int indexLen = 0;

            int openBraketIndex, closeBraketIndex;
            AbstractNode node = tree;

            for (int i = 0; i < namesLen; i++) {
                if (names[i].length() == 0) {
                    continue;
                }

                String currName = names[i];

                if ((openBraketIndex = names[i].indexOf('[')) == -1) {
                    node = nextNode(currName, node);
                } else {
                    String bracketString = currName.substring(0, openBraketIndex);
                    if (bracketString.length() > 0) {
                        node = nextNode(bracketString, node);
                    }

                    while (openBraketIndex != -1) {
                        closeBraketIndex = currName.indexOf(']', openBraketIndex);
                        Integer index = Integer.parseInt(currName.substring(openBraketIndex + 1, closeBraketIndex));
                        indexLen++;
                        node = nextNode(index, node);
                        openBraketIndex = currName.indexOf('[', closeBraketIndex);
                    }
                }
            }

            int totalIdxLen = indexLen + 1;

            if (totalIdxLen > maxIndexDepth) {
                maxIndexDepth = totalIdxLen;
            }

            return node;
        } else {
            return tree;
        }
    }

    protected void addPath(PathValueReciever valueReciever) {
        AbstractNode nodeForPath = nodeForPath(valueReciever.getPath());
        if (nodeForPath != null) {
            nodeForPath.addReciever(valueReciever);
        }
    }

    protected void addPath(PathVisitor visitor) {
        AbstractNode nodeForPath = nodeForPath(visitor.getPath());
        if (nodeForPath != null) {
            nodeForPath.addVisitor(visitor);
        }
    }

    private VariableNode nextNode(String nodeName, AbstractNode<String> parent) {
        VariableNode<String, ?> res = parent.getChildNode(nodeName);
        if (res == null) {
            res = new VariableNode<>(nodeName, parent);
        }
        return res;
    }

    private VariableNode nextNode(Integer index, AbstractNode<Integer> parent) {
        VariableNode<Integer, ?> res = parent.getChildNode(index);
        if (res == null) {
            res = new VariableNode<>(index, parent);
        }
        return res;
    }

    public boolean nextName(String name) {
        AbstractNode childNode = currentPosition.getChildNode(name);
        if (childNode == null) {
            return false;
        }
        nextName = name;
        return true;
    }

    public AbstractNode valueBegin() {
        if (currentPosition == null) {
            currentPosition = tree;
            return currentPosition;
        } else if (nextName != null) {
            currentPosition = currentPosition.getChildNode(nextName);
            nextName = null;
            return currentPosition;
        } else if (indexStack[indexPointer] >= 0) {
            AbstractNode childNode = currentPosition.getChildNode(indexStack[indexPointer]);
            if (childNode != null) {
                currentPosition = childNode;
            } else {
                indexStack[indexPointer]++;
            }
            return childNode;

        } else {
            throw new IllegalStateException();
        }
    }

    public void valueEnd() {
        currentPosition = currentPosition.getParent();
        if (indexStack[indexPointer] >= 0) {
            indexStack[indexPointer]++;
        }
    }

    public void beginArray() {
        indexPointer++;
        indexStack[indexPointer] = 0;
    }

    public void endArray() {
        indexStack[indexPointer] = -1;
        indexPointer--;
    }

    public boolean hasMoreVariables() {
        return currentPosition != null;
    }

}
