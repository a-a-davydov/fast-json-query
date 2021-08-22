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

package com.github.aadavydov.fastjsonquery.filter.predicate;

import com.github.aadavydov.fastjsonquery.filter.computer.ComplexOperator;
import com.github.aadavydov.fastjsonquery.filter.computer.ComplexOperatorComputer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ComplexPredicate extends PredicateBase {

    private final ComplexOperatorComputer computer;

    private final List<Predicate> children = new ArrayList<>();

    public ComplexPredicate(ComplexOperator operator) {
        this.computer = operator.getComputer();
    }

    public void compute() {
        if (getNeedToBeCalculated()) {
            computer.compute(this);
            if (!getNeedToBeCalculated()) {

                // Отметить всех потомков как needToBeCalculated = false;
                traverceChilds((n) -> {
                    if (n.getNeedToBeCalculated()) {
                        n.setNeedToBeCalculated(false);
                        return true;
                    }
                    return false;
                });

                throwResult();
            }
        }
    }

    public List<Predicate> getChildren() {
        return children;
    }

    public void addChild(Predicate predicate) {
        this.children.add(predicate);
        predicate.setParent(this);
    }

    @Override
    public void toDefaultIfNotCalculated() {
        if (getNeedToBeCalculated()) {
            traverceChilds((n) -> {
                if (getNeedToBeCalculated() && n.getNeedToBeCalculated()) {
                    if (!(n instanceof ComplexPredicate)) {
                        n.toDefaultIfNotCalculated();
                        return false;
                    }
                    return true;
                }
                return false;
            });
        }
    }

    public void traverceChilds(Travercer t) {
        LinkedList<ComplexPredicate> queue = new LinkedList<>();
        ComplexPredicate node = this;
        while (node != null) {
            for (Predicate child : node.getChildren()) {
                if (t.visit(child)) {
                    if (child instanceof ComplexPredicate) {
                        queue.add((ComplexPredicate) child);
                    }
                }
            }
            node = queue.poll();
        }
    }

    public ComplexOperatorComputer getComputer() {
        return computer;
    }

    public static interface Travercer {
        boolean visit(Predicate node);
    }

}
