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

package alexnerd.render.posts.control;

public class RenderTemplate {
    public static final String ARTICLE_TEASER = """
            <div class="blog-post">
              <h2 class="post-header">
                {{title}}
              </h2>
              <section class="meta_data">
                <span>Рубрика: {{rubric}}</span>
                <span>{{createDate}}</span>
              </section>
              <section class="content">
                <p>
                  {{{content}}}
                </p>
              </section>
              <section class="post-control">
                <div class="control-readmore">
                  <a href="{{link}}" class="read-more-link">Читать далее</a>
                </div>
              </section>
            </div>
            """;

    public static final String POST = """
            <div class="blog-post">
              <h2 class="post-header">
                {{title}}
              </h2>
              <section class="meta_data">
                <span>Рубрика: {{rubric}}</span>
                <span>{{createDate}}</span>
              </section>
              <section class="content">
                  {{{content}}}
              </section>
            </div>
            """;

    public static final String LAST_ARTICLES = """
            <li>
              <h5>
                 <a href="{{link}}">{{title}}</a>
              </h5>
              <section class="meta_data">
                 <span>{{createDate}}</span>
              </section>
            </li>
            """;
}
