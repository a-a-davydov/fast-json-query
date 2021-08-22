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

import com.github.aadavydov.fastjsonquery.filter.computer.op.ValueComputer;
import com.github.aadavydov.fastjsonquery.filter.computer.op.ValueOperator;

import java.util.List;

public class BinaryPredicate extends PredicateBase implements PathPredicate, ValuePredicate {

    private final ValueComputer computer;
    private final Operand left;
    private final Operand right;

    public BinaryPredicate(Operand left, Operand right, ValueOperator operator) {
        computer = operator.getComputer();
        this.left = left;
        this.right = right;
        this.left.setPredicate(this);
        this.right.setPredicate(this);
    }

    @Override
    public void onValue() {
        if (left.isValueSet() && right.isValueSet()) {
            setPredicateValue(computer.compare(left.getValue(), right.getValue()));
            setNeedToBeCalculated(false);
            throwResult();
        }
    }

    @Override
    public void resetNode() {
        left.reset();
        right.reset();
        super.resetNode();
    }

    @Override
    public void exportValueRecievers(List<PathValueReciever> list) {
        list.add(left);
        list.add(right);
    }

    @Override
    public void exportVisitors(List<PathVisitor> list) {
    }

}
