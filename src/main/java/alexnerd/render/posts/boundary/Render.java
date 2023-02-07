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

package alexnerd.render.posts.boundary;

import alexnerd.render.posts.control.CompilerJS;
import alexnerd.render.posts.control.content.Content;
import alexnerd.render.posts.control.content.ContentResource;
import alexnerd.render.posts.control.content.ContentType;
import alexnerd.render.posts.control.Lang;
import alexnerd.render.posts.control.exception.RenderException;
import alexnerd.render.posts.control.RenderTemplate;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

public class Render {

    @Inject
    private ContentResource contentResource;

    @Inject
    private CompilerJS compilerJS;

    public String renderContent(Lang lang, ContentType type, String date, String title) {
        Content content = contentResource.getContent(lang, type, date, title);
        return this.render(content);
    }

    public String renderLast(Lang lang, ContentType type, int limit) {
        List<Content> last = contentResource.getLast(lang, type, limit);
        return last.stream()
                .parallel()
                .map(this::render)
                .collect(Collectors.joining());
    }

    private String render(Content content) {
        String template = switch (content.type()) {
            case POST, ARTICLE -> RenderTemplate.POST;
            case ARTICLE_TEASER -> RenderTemplate.ARTICLE_TEASER;
            case LAST_ARTICLES -> RenderTemplate.LAST_ARTICLES;
            default -> throw new RenderException(422, "Unsupported content type");
        };
        return this.compilerJS.compile(template, content.content());
    }
}
