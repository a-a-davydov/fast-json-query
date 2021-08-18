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

package com.github.aadavydov.fastjsonquery.jsonutils;

public class CharsCollector {

    private char[] data;
    private int length;

    public void append(char[] inData, int inPos, int inLen) {
        if (inLen > 0) {
            ensureCapacity(inLen + length);
        }

        System.arraycopy(inData, inPos, data, length, inLen);
        length += inLen;
    }

    private void ensureCapacity(int newLength) {
        if (data == null || newLength > data.length) {
            char[] newdata = new char[newLength];
            if (length > 0) {
                System.arraycopy(data, 0, newdata, 0, length);
            }
            data = newdata;
        }
    }

    public void reset() {
        length = 0;
    }

    public char[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

    public void appendTo(StringBuilder sb) {
        sb.append(data, 0, length);
    }

    public boolean startsWith(char[] prefix) {
        if (prefix.length > length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (prefix[i] != data[i]) {
                return false;
            }

        }

        return true;
    }

    @Override
    public String toString() {
        return new String(data);
    }

}
