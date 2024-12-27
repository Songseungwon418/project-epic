package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.ArticleService;
import com.ssw.epicgames.vos.ArticlePageVo;
import com.ssw.epicgames.vos.ArticleVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/board")
public class BoardController {
    private final ArticleService articleService;

    @Autowired
    public BoardController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@SessionAttribute(value = "user", required = false) UserEntity user,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "filter", required = false) String filter,
                                @RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView modelAndView = new ModelAndView();

        if (filter == null || keyword == null) {
            Pair<ArticlePageVo, ArticleVo[]> articles = this.articleService.getAllArticles(page);
            modelAndView.addObject("articlePageVo", articles.getLeft());
            modelAndView.addObject("articles", articles.getRight());
        } else {
            Pair<ArticlePageVo, ArticleVo[]> articles = this.articleService.searchArticles(page, filter, keyword);
            modelAndView.addObject("articlePageVo", articles.getLeft());
            modelAndView.addObject("articles", articles.getRight());
            modelAndView.addObject("filter", filter);
            modelAndView.addObject("keyword", keyword);
        }
        modelAndView.addObject("user", user);
        modelAndView.setViewName("board/list");
        return modelAndView;
    }
}
