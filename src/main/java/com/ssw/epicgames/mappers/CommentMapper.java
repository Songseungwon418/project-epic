package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.CommentEntity;
import com.ssw.epicgames.vos.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {

    int insertComment(CommentEntity comment);

    CommentVo selectCommentByIndex(@Param("index") int index);

    CommentVo[] selectCommentsByArticleIndex(@Param("articleIndex") int articleIndex);

    int updateComment(CommentEntity comment);

    int selectCommentCount(@Param("index") int index);

}
