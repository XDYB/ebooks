/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 HackerEarth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:*
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.*
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.chunsheng.ebooks.hackerearth.api.client;


import me.chunsheng.ebooks.hackerearth.api.options.CompileOptions;
import me.chunsheng.ebooks.hackerearth.api.options.RunOptions;
import me.chunsheng.ebooks.hackerearth.api.requests.CompileRequest;
import me.chunsheng.ebooks.hackerearth.api.requests.RunRequest;
import me.chunsheng.ebooks.hackerearth.api.responses.CompileResponse;
import me.chunsheng.ebooks.hackerearth.api.responses.RunResponse;

public class HackerEarthAPI {

    private final String mClientSecret;

    public HackerEarthAPI(String clientSecret){
        this.mClientSecret = clientSecret;
    }

    public CompileResponse Compile(CompileOptions options){

        CompileRequest request = new CompileRequest(mClientSecret, options);
        CompileResponse response = (CompileResponse)request.Execute();
        return response;
    }

    public RunResponse Run(RunOptions options){
        RunRequest request = new RunRequest(mClientSecret, options);
        RunResponse response = (RunResponse)request.Execute();
        return response;
    }

}
