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

package com.github.aadavydov.fastjsonquery.jsonutils.reader;

import com.github.aadavydov.fastjsonquery.jsonutils.CharsCollector;
import sun.nio.cs.ArrayDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class CharBufferJsonReader extends ReusableJsonReader {

    private final CharsetDecoder decoder;
    private char[] charData = null;
    private int clen;

    public CharBufferJsonReader(Charset charset) {
        this.decoder = charset.newDecoder();
    }

    public void setup(CharsCollector stringData) {
        super.setup(null, stringData.getData(), 0, stringData.getLength());
    }

    public void setup(StringBuilder stringData) {

        clen = stringData.length();

        if (charData == null || charData.length < clen) {
            charData = new char[clen];
        }

        stringData.getChars(0, clen, charData, 0);
        super.setup(null, charData, 0, clen);
    }

    public void setup(String stringData) {

        clen = stringData.length();

        if (charData == null || charData.length < clen) {
            charData = new char[clen];
        }

        stringData.getChars(0, clen, charData, 0);
        super.setup(null, charData, 0, clen);
    }

    public void setup(byte[] binData) throws IOException {
        setBinData(binData);
        super.setup(null, charData, 0, clen);
    }

    private void setBinData(byte[] binData) throws IOException {

        int estSize = (int) (decoder.maxCharsPerByte() * binData.length) + 1;

        if (charData == null || charData.length < estSize) {
            charData = new char[estSize];
        }

        decoder.reset();

        if (decoder instanceof ArrayDecoder) {
            ArrayDecoder ad = (ArrayDecoder) decoder;
            clen = ad.decode(binData, 0, binData.length, charData);
        } else {
            CharBuffer wrap = CharBuffer.wrap(charData, 0, charData.length);
            CoderResult cr = decoder.decode(ByteBuffer.wrap(binData, 0, binData.length), wrap, true);

            if (!cr.isUnderflow()) {
                cr.throwException();
            }

            cr = decoder.flush(wrap);

            if (!cr.isUnderflow()) {
                cr.throwException();
            }

            clen = wrap.position();
        }

    }

}
