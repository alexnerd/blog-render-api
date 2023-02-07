/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package alexnerd.render.posts.control.content;

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

import java.util.List;

public record Content(ContentType type, String content) {

    public static Content fromJsonValue(JsonValue value) {
        String type = value.asJsonObject().getString("type");
        String content = value.toString();
        return new Content(ContentType.valueOf(type), content);
    }

    public static List<Content> fromJsonArray(JsonArray array) {
        return array.stream()
                .parallel()
                .map(Content::fromJsonValue)
                .toList();
    }
}
