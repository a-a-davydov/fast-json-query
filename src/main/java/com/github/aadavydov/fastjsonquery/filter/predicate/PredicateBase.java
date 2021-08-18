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

public abstract class PredicateBase implements Predicate {

    private ComplexPredicate parent = null;
    private boolean needToBeCalculated = true;
    private boolean predicateValue = false;

    @Override
    public final ComplexPredicate getParent() {
        return parent;
    }

    @Override
    public final void setParent(ComplexPredicate parent) {
        if (this.parent == null) {
            this.parent = parent;
        } else {
            throw new IllegalStateException("Ambigeous hierarchy.");
        }
    }

    @Override
    public final boolean getNeedToBeCalculated() {
        return needToBeCalculated;
    }

    @Override
    public final void setNeedToBeCalculated(boolean needToBeCalculated) {
        this.needToBeCalculated = needToBeCalculated;
    }

    @Override
    public final boolean getPredicateValue() {
        return predicateValue;
    }

    @Override
    public final void setPredicateValue(boolean predicateValue) {
        this.predicateValue = predicateValue;
    }

    @Override
    public void resetNode() {
        needToBeCalculated = true;
        predicateValue = false;
    }

    void throwResult() {
        if (parent != null) {
            // Если не корень
            parent.compute();
        }
    }

    @Override
    public void toDefaultIfNotCalculated() {
        if (getNeedToBeCalculated()) {
            setPredicateValue(false);
            setNeedToBeCalculated(false);
            throwResult();
        }
    }

}
