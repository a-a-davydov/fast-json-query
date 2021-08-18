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

package com.github.aadavydov.fastjsonquery.filter.value;

public class NullValue implements Value {

    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {
    }

    @Override
    public boolean eq(Value right) {
        return right.eqInverse(this);
    }

    @Override
    public boolean neq(Value right) {
        return right.neqInverse(this);
    }

    @Override
    public boolean gt(Value right) {
        return right.gtInverse(this);
    }

    @Override
    public boolean gte(Value right) {
        return right.gteInverse(this);
    }

    @Override
    public boolean lt(Value right) {
        return right.ltInverse(this);
    }

    @Override
    public boolean lte(Value right) {
        return right.lteInverse(this);
    }

    @Override
    public boolean eqInverse(NullValue left) {
        return true;
    }

    @Override
    public boolean neqInverse(NullValue left) {
        return false;
    }

    @Override
    public boolean gtInverse(NullValue left) {
        return false;
    }

    @Override
    public boolean gteInverse(NullValue left) {
        return true;
    }

    @Override
    public boolean ltInverse(NullValue left) {
        return false;
    }

    @Override
    public boolean lteInverse(NullValue left) {
        return true;
    }

    @Override
    public String toString() {
        return "NullValue{" + '}';
    }

}