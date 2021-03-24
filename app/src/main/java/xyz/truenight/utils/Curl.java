/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Curl {

    public static String convert(URL url, String requestMethod, HashMap<String, List<String>> requestHeaders, String requestBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("curl --compressed '").append(url).append("' -i -X ").append(requestMethod);
        for (String key : requestHeaders.keySet()) {
            List<String> headerList = requestHeaders.get(key);
            String headerVal;
            if (headerList != null && headerList.size() == 1) {
                headerVal = headerList.get(0);
            } else {
                headerVal = "" + headerList;
            }
            sb.append(" -H '").append(key).append(": ").append(headerVal).append("'");
        }
        sb.append(" -d '").append(requestBody).append("'");
        return sb.toString();
    }
}
