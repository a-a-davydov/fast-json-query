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

public abstract class ComparableValueBase<T extends Comparable<T>, V extends ComparableValueBase<T, V>> implements Value {

    final T value;

    protected ComparableValueBase(T value) {
        this.value = value;
    }

    public final boolean eqInverse(V left) {
        return left.value.equals(this.value);
    }

    public final boolean neqInverse(V left) {
        return !left.value.equals(this.value);
    }

    public final boolean gtInverse(V left) {
        return left.value.compareTo(this.value) > 0;
    }

    public final boolean gteInverse(V left) {
        return left.value.compareTo(this.value) >= 0;
    }

    public final boolean ltInverse(V left) {
        return left.value.compareTo(this.value) < 0;
    }

    public final boolean lteInverse(V left) {
        return left.value.compareTo(this.value) <= 0;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ComparableValueBase{" + "value=" + value + '}';
    }

}
