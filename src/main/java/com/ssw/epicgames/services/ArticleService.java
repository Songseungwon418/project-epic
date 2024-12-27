package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.ArticleEntity;
import com.ssw.epicgames.entities.ImageEntity;
import com.ssw.epicgames.mappers.ArticleMapper;
import com.ssw.epicgames.mappers.CommentMapper;
import com.ssw.epicgames.mappers.ImageMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.vos.ArticlePageVo;
import com.ssw.epicgames.vos.ArticleVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleService {
    private final ArticleMapper articleMapper;
    private final ImageMapper imageMapper;
    private final CommentMapper commentMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ImageMapper imageMapper, CommentMapper commentMapper) {
        this.articleMapper = articleMapper;
        this.imageMapper = imageMapper;
        this.commentMapper = commentMapper;
    }

    public ImageEntity getImage(int index) {
        if (index < 1) {
            return null;
        }
        return this.imageMapper.selectImageByIndex(index);
    }

    public boolean uploadImage(ImageEntity image) {
        if (image == null ||
                image.getData() == null || image.getData().length == 0 ||
                image.getContentType() == null || image.getContentType().isEmpty() ||
                image.getName() == null || image.getName().isEmpty()) {
            return false;
        }
        image.setCreatedAt(LocalDateTime.now());
        return this.imageMapper.insertImage(image) > 0;
    }

    public Pair<ArticlePageVo, ArticleVo[]> searchArticles(int page, String filter, String keyword) {
        page = Math.max(page, 1);
        if (filter == null || (!filter.equals("all") && !filter.equals("title") && !filter.equals("nickname"))) {
            filter = "all";
        }
        if (keyword == null) {
            keyword = "";
        }
        int totalCount = this.articleMapper.selectArticleCountBySearch(filter, keyword);
        ArticlePageVo articlePageVo = new ArticlePageVo(page, totalCount);
        ArticleVo[] articleVos = this.articleMapper.selectArticleBySearch(filter, keyword, articlePageVo.countPerPage, articlePageVo.offsetCount);
        return Pair.of(articlePageVo, articleVos);
    }

    public CommonResult modifyArticle(ArticleEntity article) {
        if (article == null) {
            return CommonResult.FAILURE;
        }
        if (article.getIndex() < 1) {
            return CommonResult.FAILURE;
        }
        if (article.getUserEmail() == null || article.getUserEmail().isEmpty() || article.getUserEmail().length() > 50) {
            return CommonResult.FAILURE;
        }
        if (article.getTitle() == null || article.getTitle().isEmpty() || article.getTitle().length() > 100) {
            return CommonResult.FAILURE;
        }
        if (article.getContent() == null || article.getContent().isEmpty() || article.getContent().length() > 16_777_215) {
            return CommonResult.FAILURE;
        }
        ArticleEntity dbArticle = this.articleMapper.selectArticleByIndex(article.getIndex());
        if (dbArticle == null) {
            return CommonResult.FAILURE;
        }
        if (dbArticle.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }
        dbArticle.setTitle(article.getTitle());
        dbArticle.setContent(article.getContent());
        dbArticle.setUpdatedAt(LocalDateTime.now());
        return this.articleMapper.updateArticle(dbArticle) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult deleteArticle(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        ArticleEntity article = this.articleMapper.selectArticleByIndex(index);
        if (article == null) {
            return CommonResult.FAILURE;
        }
        if (article.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }
        article.setDeletedAt(LocalDateTime.now());
        return this.articleMapper.updateArticle(article) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public boolean increaseArticleView(ArticleEntity article) {
        if (article == null) {
            return false;
        }
        article.setView(article.getView() + 1);
        return this.articleMapper.updateArticle(article) > 0;
    }

    public Pair<ArticlePageVo, ArticleVo[]> getAllArticles(int page) {
        page = Math.max(page, 1);
        int totalCount = this.articleMapper.selectArticleCount();
        ArticlePageVo pageVo = new ArticlePageVo(page, totalCount);
        ArticleVo[] articleVos = this.articleMapper.selectAllArticles(pageVo.countPerPage, pageVo.offsetCount);
        return Pair.of(pageVo, articleVos);
    }

    public int getCommentCount(int index) {
        if (index < 1) {
            return 0;
        }
        return this.commentMapper.selectCommentCount(index);
    }

    public ArticleVo getArticle(int index) {
        if (index < 1) {
            return null;
        }
        return this.articleMapper.selectArticleByIndex(index);
    }

    public CommonResult write(ArticleEntity article) {
        if (article == null) {
            return CommonResult.FAILURE;
        }
        if (article.getUserEmail() == null || article.getUserEmail().isEmpty() || article.getUserEmail().length() > 50) {
            return CommonResult.FAILURE;
        }
        if (article.getTitle() == null || article.getTitle().isEmpty() || article.getTitle().length() > 100) {
            return CommonResult.FAILURE;
        }
        if (article.getContent() == null || article.getContent().isEmpty() || article.getContent().length() > 16_777_215) {
            return CommonResult.FAILURE;
        }
        article.setCreatedAt(LocalDateTime.now());
        article.setView(0);
        article.setUpdatedAt(null);
        article.setDeletedAt(null);

        return this.articleMapper.insertArticle(article) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

}
