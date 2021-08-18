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

import com.google.gson.stream.JsonToken;

import java.io.IOException;

public interface CustomJsonReader {

    public boolean isLenient();

    public void setLenient(boolean lenient);

    public void beginArray() throws IOException;

    public void endArray() throws IOException;

    public void beginObject() throws IOException;

    public void endObject() throws IOException;

    public boolean hasNext() throws IOException;

    public JsonToken peek() throws IOException;

    public String nextName() throws IOException;

    public String nextString() throws IOException;

    public boolean nextBoolean() throws IOException;

    public void nextNull() throws IOException;

    public double nextDouble() throws IOException;

    public long nextLong() throws IOException;

    public int nextInt() throws IOException;

    public void close() throws IOException;

    public void skipValue() throws IOException;

    public String getPath();

}
