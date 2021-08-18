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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.aadavydov.fastjsonquery.filter.predicate;

import com.github.aadavydov.fastjsonquery.filter.value.Value;
import com.github.aadavydov.fastjsonquery.filter.variable.Path;

public class Operand implements PathValueReciever {

    private Path path = null;
    private boolean valueSet = false;
    private Value value = null;
    private ValuePredicate predicate = null;

    public Operand(String idPath) {
        this.path = new Path(idPath);
    }

    public Operand(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public boolean isValueSet() {
        return valueSet;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        this.valueSet = true;
        this.value = value;
        this.predicate.onValue();
    }

    public void reset() {
        this.valueSet = false;
        this.value = null;
    }

    public void setPredicate(ValuePredicate predicate) {
        this.predicate = predicate;
    }

}
