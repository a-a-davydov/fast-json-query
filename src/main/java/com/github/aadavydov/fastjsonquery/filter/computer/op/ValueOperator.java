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

package com.github.aadavydov.fastjsonquery.filter.computer.op;

import java.util.HashMap;
import java.util.Map;

public enum ValueOperator {

    GT(new ValueComputerGT(), ">"),
    GTE(new ValueComputerGTE(), ">="),
    EQ(new ValueComputerEQ(), "=="),
    NEQ(new ValueComputerNEQ(), "!="),
    LTE(new ValueComputerLTE(), "<="),
    LT(new ValueComputerLT(), "<");

    private static final Map<String, ValueOperator> MAPPING = new HashMap<>();

    static {
        for (ValueOperator vo : ValueOperator.values()) {
            if (MAPPING.put(vo.key, vo) != null) {
                throw new IllegalStateException();
            }
        }
    }

    private final ValueComputer computer;
    private final String key;

    private ValueOperator(ValueComputer computer, String key) {
        this.computer = computer;
        this.key = key;
    }

    public static ValueOperator forKey(String key) {
        return MAPPING.get(key);
    }

    public ValueComputer getComputer() {
        return computer;
    }


}
