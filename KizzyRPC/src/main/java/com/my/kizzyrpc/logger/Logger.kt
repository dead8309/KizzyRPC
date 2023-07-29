/*
 *   Copyright (c) 2023 Kizzy. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.my.kizzyrpc.logger

interface Logger {
    fun clear()
    fun i(tag: String, event: String)
    fun e(tag: String, event: String)
    fun d(tag: String, event: String)
    fun w(tag: String, event: String)
}

object NoOpLogger: Logger {
    override fun clear() {}
    override fun i(tag: String, event: String) {}
    override fun e(tag: String, event: String) {}
    override fun d(tag: String, event: String) {}
    override fun w(tag: String, event: String) {}
}