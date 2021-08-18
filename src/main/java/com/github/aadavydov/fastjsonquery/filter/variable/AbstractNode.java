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

import com.github.aadavydov.fastjsonquery.filter.predicate.PathValueReciever;
import com.github.aadavydov.fastjsonquery.filter.predicate.PathVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractNode<C> {

    private Map<C, VariableNode<C, ?>> children;
    private List<PathValueReciever> recievers = null;
    private List<PathVisitor> visitors = null;

    protected void addChildNode(VariableNode<C, ?> node) {
        if (children == null) {
            children = new HashMap<>();
        }
        children.put(node.getName(), node);
    }

    public VariableNode getChildNode(C name) {
        if (children == null) return null;
        return children.get(name);
    }

    public void addReciever(PathValueReciever reciever) {
        if (recievers == null) recievers = new ArrayList<>();
        recievers.add(reciever);
    }

    public void addVisitor(PathVisitor visitor) {
        if (visitors == null) visitors = new ArrayList<>();
        visitors.add(visitor);
    }

    public List<PathValueReciever> getRecievers() {
        return recievers;
    }

    public List<PathVisitor> getVisitors() {
        return visitors;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public abstract AbstractNode getParent();
}
