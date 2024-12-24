package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.ArticleEntity;
import com.ssw.epicgames.vos.ArticleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {

    int insertArticle(ArticleEntity article);

    ArticleVo selectArticleByIndex(@Param("index") int index);

    ArticleVo[] selectAllArticles(@Param("limitCount") int limitCount,
                                  @Param("offsetCount") int offsetCount);

    int selectArticleCount();

    int updateArticle(ArticleEntity article);

    ArticleVo[] selectArticleBySearch(@Param("filter") String filter,
                                      @Param("keyword") String keyword,
                                      @Param("limitCount") int limitCount,
                                      @Param("offsetCount") int offsetCount);

    int selectArticleCountBySearch(@Param("filter") String filter,
                                   @Param("keyword") String keyword);




}
