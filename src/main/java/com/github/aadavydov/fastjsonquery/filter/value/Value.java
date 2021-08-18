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

public interface Value {

    public boolean eq(Value right);

    public boolean neq(Value right);

    public boolean gt(Value right);

    public boolean gte(Value right);

    public boolean lt(Value right);

    public boolean lte(Value right);

    public default boolean eqInverse(StringValue left) {
        return false;
    }

    public default boolean neqInverse(StringValue left) {
        return true;
    }

    public default boolean gtInverse(StringValue left) {
        return false;
    }

    public default boolean gteInverse(StringValue left) {
        return false;
    }

    public default boolean ltInverse(StringValue left) {
        return false;
    }

    public default boolean lteInverse(StringValue left) {
        return false;
    }

    public default boolean eqInverse(NumberValue left) {
        return false;
    }

    public default boolean neqInverse(NumberValue left) {
        return true;
    }

    public default boolean gtInverse(NumberValue left) {
        return false;
    }

    public default boolean gteInverse(NumberValue left) {
        return false;
    }

    public default boolean ltInverse(NumberValue left) {
        return false;
    }

    public default boolean lteInverse(NumberValue left) {
        return false;
    }

    public default boolean eqInverse(BooleanValue left) {
        return false;
    }

    public default boolean neqInverse(BooleanValue left) {
        return true;
    }

    public default boolean gtInverse(BooleanValue left) {
        return false;
    }

    public default boolean gteInverse(BooleanValue left) {
        return false;
    }

    public default boolean ltInverse(BooleanValue left) {
        return false;
    }

    public default boolean lteInverse(BooleanValue left) {
        return false;
    }

    public default boolean eqInverse(NullValue left) {
        return false;
    }

    public default boolean neqInverse(NullValue left) {
        return true;
    }

    public default boolean gtInverse(NullValue left) {
        return false;
    }

    public default boolean gteInverse(NullValue left) {
        return false;
    }

    public default boolean ltInverse(NullValue left) {
        return false;
    }

    public default boolean lteInverse(NullValue left) {
        return false;
    }

}
