package alexnerd.render.posts.control;

public class ContentTemplate {
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
