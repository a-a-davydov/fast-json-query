/*
 * Copyright (c) 2021. Andrey Davydov (andrey.davydov@gmail.com; https://github.com/a-a-davydov)
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

package com.github.aadavydov.fastjsonquery.filter;

import com.github.aadavydov.fastjsonquery.jsonutils.reader.CharBufferJsonReader;

import java.nio.charset.StandardCharsets;

public class JsonFilter {

    private final JsonPredicateComputer jsonPredicateComputer;
    private final CharBufferJsonReader byteBufferJsonReader = new CharBufferJsonReader(StandardCharsets.UTF_8);

    public JsonFilter(JsonPredicateComputer jsonPredicateComputer) {
        this.jsonPredicateComputer = jsonPredicateComputer;
    }

    public boolean apply(byte[] buf, boolean lenient) throws Exception {
        jsonPredicateComputer.reset();
        byteBufferJsonReader.setup(buf);
        byteBufferJsonReader.setLenient(lenient);
        return jsonPredicateComputer.processJson(byteBufferJsonReader);
    }

    public boolean apply(String s, boolean lenient) throws Exception {
        jsonPredicateComputer.reset();
        byteBufferJsonReader.setup(s);
        byteBufferJsonReader.setLenient(lenient);
        return jsonPredicateComputer.processJson(byteBufferJsonReader);
    }

}
